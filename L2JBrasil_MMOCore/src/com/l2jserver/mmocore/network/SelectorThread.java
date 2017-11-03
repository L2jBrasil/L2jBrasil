/* This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA
 * 02111-1307, USA.
 *
 * http://www.gnu.org/copyleft/gpl.html
 */
package com.l2jserver.mmocore.network;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author KenM
 */
public final class SelectorThread<T extends MMOClient<?>> extends Thread
{
    // default BYTE_ORDER
    private static final ByteOrder BYTE_ORDER = ByteOrder.LITTLE_ENDIAN;
    // default HEADER_SIZE
    private static final int HEADER_SIZE = 2;
    // Selector
    private final Selector _selector;
    // Implementations
    private final IPacketHandler<T> _packetHandler;
    private final IMMOExecutor<T> _executor;
    private final IClientFactory<T> _clientFactory;
    private final IAcceptFilter _acceptFilter;
    // Configurations
    private final int HELPER_BUFFER_SIZE;
    private final int HELPER_BUFFER_COUNT;
    private final int MAX_SEND_PER_PASS;
    private final int MAX_READ_PER_PASS;
    private final long SLEEP_TIME;
    // Main Buffers
    private final ByteBuffer DIRECT_WRITE_BUFFER;
    private final ByteBuffer WRITE_BUFFER;
    private final ByteBuffer READ_BUFFER;
    // String Buffer
    private final NioNetStringBuffer STRING_BUFFER;
    // ByteBuffers General Purpose Pool
    private final List<ByteBuffer> _bufferPool;
    // Pending Close
    private final NioNetStackList<MMOConnection<T>> _pendingClose;
    
    private boolean _shutdown;
    
    public SelectorThread(final SelectorConfig sc, final IMMOExecutor<T> executor, final IPacketHandler<T> packetHandler, final IClientFactory<T> clientFactory, final IAcceptFilter acceptFilter) throws IOException
    {
        super.setName("SelectorThread-" + super.getId());

        HELPER_BUFFER_SIZE = sc.HELPER_BUFFER_SIZE;
        HELPER_BUFFER_COUNT = sc.HELPER_BUFFER_COUNT;
        MAX_SEND_PER_PASS = sc.MAX_SEND_PER_PASS;
        MAX_READ_PER_PASS = sc.MAX_READ_PER_PASS;
        SLEEP_TIME = sc.SLEEP_TIME;
        DIRECT_WRITE_BUFFER = ByteBuffer.allocateDirect(sc.WRITE_BUFFER_SIZE) .order(BYTE_ORDER);
        WRITE_BUFFER = ByteBuffer.wrap(new byte[sc.WRITE_BUFFER_SIZE]).order(BYTE_ORDER);
        READ_BUFFER = ByteBuffer.wrap(new byte[sc.READ_BUFFER_SIZE]).order(BYTE_ORDER);
        STRING_BUFFER = new NioNetStringBuffer(64 * 1024);
        _pendingClose = new NioNetStackList<MMOConnection<T>>();
        _bufferPool = new ArrayList<>();
        
        for (int i = 0; i < HELPER_BUFFER_COUNT; i++)
            _bufferPool.add(_bufferPool.size(), ByteBuffer.wrap(new byte[HELPER_BUFFER_SIZE]).order(BYTE_ORDER));

        _acceptFilter = acceptFilter;
        _packetHandler = packetHandler;
        _clientFactory = clientFactory;
        _executor = executor;
        _selector = Selector.open();
    }
    
    public final void openServerSocket(InetAddress address, int tcpPort) throws IOException
    {
        ServerSocketChannel selectable = ServerSocketChannel.open();
        selectable.configureBlocking(false);

        ServerSocket ss = selectable.socket();
        
        if (address == null)
            ss.bind(new InetSocketAddress(tcpPort));
        else
            ss.bind(new InetSocketAddress(address, tcpPort));
        
        selectable.register(_selector, SelectionKey.OP_ACCEPT);
    }
    
    final ByteBuffer getPooledBuffer()
    {
        if (_bufferPool.isEmpty())
            return ByteBuffer.wrap(new byte[HELPER_BUFFER_SIZE]).order(BYTE_ORDER);
        
        return _bufferPool.remove(0);
    }
    
    final void recycleBuffer(final ByteBuffer buf)
    {
        if (_bufferPool.size() < HELPER_BUFFER_COUNT)
        {
            buf.clear();
            _bufferPool.add(_bufferPool.size(), buf);
        }
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public final void run()
    {
        int selectedKeysCount = 0;

        SelectionKey key;
        MMOConnection<T> con;

        Iterator<SelectionKey> selectedKeys;

        while (!_shutdown)
        {
            try
            {
                selectedKeysCount = _selector.selectNow();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }

            if (selectedKeysCount > 0)
            {
                selectedKeys = _selector.selectedKeys().iterator();

                while (selectedKeys.hasNext())
                {
                    key = selectedKeys.next();
                    selectedKeys.remove();

                    con = (MMOConnection<T>) key.attachment();

                    switch (key.readyOps())
                    {
                        case SelectionKey.OP_CONNECT:
                            finishConnection(key, con);
                            break;
                        case SelectionKey.OP_ACCEPT:
                            acceptConnection(key, con);
                            break;
                        case SelectionKey.OP_READ:
                            readPacket(key, con);
                            break;
                        case SelectionKey.OP_WRITE:
                            writePacket(key, con);
                            break;
                        case SelectionKey.OP_READ | SelectionKey.OP_WRITE:
                            writePacket(key, con);
                            if (key.isValid())
                                readPacket(key, con);
                            break;
                    }
                }
            }

            synchronized (_pendingClose)
            {
                while (!_pendingClose.isEmpty())
                {
                    con = _pendingClose.removeFirst();
                    writeClosePacket(con);
                    closeConnectionImpl(con.getSelectionKey(), con);
                }
            }

            try
            {
                Thread.sleep(SLEEP_TIME);
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        }
        closeSelectorThread();
    }
    
    private final void finishConnection(final SelectionKey key, final MMOConnection<T> con)
    {
        try
        {
            ((SocketChannel) key.channel()).finishConnect();
        }
        catch (IOException e)
        {
            con.getClient().onForcedDisconnection();
            closeConnectionImpl(key, con);
        }

        // key might have been invalidated on finishConnect()
        if (key.isValid())
        {
            key.interestOps(key.interestOps() | SelectionKey.OP_READ);
            key.interestOps(key.interestOps() & ~SelectionKey.OP_CONNECT);
        }
    }
    
    private final void acceptConnection(final SelectionKey key, MMOConnection<T> con)
    {
        ServerSocketChannel ssc = (ServerSocketChannel) key.channel();
        SocketChannel sc;

        try
        {
            while ((sc = ssc.accept()) != null)
            {
                if (_acceptFilter == null || _acceptFilter.accept(sc))
                {
                    sc.configureBlocking(false);
                    SelectionKey clientKey = sc.register(_selector, SelectionKey.OP_READ);
                    con = new MMOConnection<T>(this, sc.socket(), clientKey);
                    con.setClient(_clientFactory.create(con));
                    clientKey.attach(con);
                }
                else
                    sc.socket().close();
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
    
    private final void readPacket(final SelectionKey key, final MMOConnection<T> con)
    {
        if (!con.isClosed())
        {
            ByteBuffer buf;
            if ((buf = con.getReadBuffer()) == null)
                buf = READ_BUFFER;

            // if we try to to do a read with no space in the buffer it will
            // read 0 bytes
            // going into infinite loop
            if (buf.position() == buf.limit())
                System.exit(0);

            int result = -2;

            try
            {
                result = con.read(buf);
            }
            catch (IOException e)
            {
                // error handling goes bellow
            }

            if (result > 0)
            {
                buf.flip();

                final T client = con.getClient();

                for (int i = 0; i < MAX_READ_PER_PASS; i++)
                    if (!tryReadPacket(key, client, buf, con))
                        return;

                // only reachable if MAX_READ_PER_PASS has been reached
                // check if there are some more bytes in buffer
                // and allocate/compact to prevent content lose.
                if (buf.remaining() > 0)
                {
                    // did we use the READ_BUFFER ?
                    if (buf == READ_BUFFER)
                        // move the pending byte to the connections READ_BUFFER
                        allocateReadBuffer(con);
                    else
                        // move the first byte to the beginning :)
                        buf.compact();
                }
            }
            else
            {
                switch (result)
                {
                    case 0:
                    case -1:
                        closeConnectionImpl(key, con);
                        break;
                    case -2:
                        con.getClient().onForcedDisconnection();
                        closeConnectionImpl(key, con);
                        break;
                }
            }
        }
    }
    
    private final boolean tryReadPacket(final SelectionKey key, final T client, final ByteBuffer buf, final MMOConnection<T> con)
    {
        switch (buf.remaining())
        {
            case 0:
                // buffer is full
                // nothing to read
                return false;
            case 1:
                // we don`t have enough data for header so we need to read
                key.interestOps(key.interestOps() | SelectionKey.OP_READ);

                // did we use the READ_BUFFER ?
                if (buf == READ_BUFFER)
                    // move the pending byte to the connections READ_BUFFER
                    allocateReadBuffer(con);
                else
                    // move the first byte to the beginning :)
                    buf.compact();
                return false;
            default:
                // data size excluding header size :>
                final int dataPending = (buf.getShort() & 0xFFFF) - HEADER_SIZE;

                // do we got enough bytes for the packet?
                if (dataPending <= buf.remaining())
                {
                    // avoid parsing dummy packets (packets without body)
                    if (dataPending > 0)
                    {
                        final int pos = buf.position();
                        parseClientPacket(pos, buf, dataPending, client);
                        buf.position(pos + dataPending);
                    }

                    // if we are done with this buffer
                    if (!buf.hasRemaining())
                    {
                        if (buf != READ_BUFFER)
                        {
                            con.setReadBuffer(null);
                            recycleBuffer(buf);
                        }
                        else
                            READ_BUFFER.clear();
                        return false;
                    }
                    return true;
                }
                else
                {
                    // we don`t have enough bytes for the dataPacket so we need
                    // to read
                    key.interestOps(key.interestOps() | SelectionKey.OP_READ);
                    
                    // did we use the READ_BUFFER ?
                    if (buf == READ_BUFFER)
                    {
                        // move it`s position
                        buf.position(buf.position() - HEADER_SIZE);
                        // move the pending byte to the connections READ_BUFFER
                        allocateReadBuffer(con);
                    }
                    else
                    {
                        buf.position(buf.position() - HEADER_SIZE);
                        buf.compact();
                    }
                    return false;
                }
        }
    }
    
    private final void allocateReadBuffer(final MMOConnection<T> con)
    {
        con.setReadBuffer(getPooledBuffer().put(READ_BUFFER));
        READ_BUFFER.clear();
    }
    
    private final void parseClientPacket(final int pos, final ByteBuffer buf, final int dataSize, final T client)
    {
        final boolean ret = client.decrypt(buf, dataSize);

        if (ret && buf.hasRemaining())
        {
            // apply limit
            final int limit = buf.limit();
            buf.limit(pos + dataSize);
            final ReceivablePacket<T> cp = _packetHandler.handlePacket(buf, client);

            if (cp != null)
            {
                cp._buf = buf;
                cp._sbuf = STRING_BUFFER;
                cp._client = client;

                if (cp.read())
                    _executor.execute(cp);

                cp._buf = null;
                cp._sbuf = null;
            }
            buf.limit(limit);
        }
    }
    
    private final void writeClosePacket(final MMOConnection<T> con)
    {
    	SendablePacket<T> sp;
    	synchronized (con.getSendQueue())
		{
			if (con.getSendQueue().isEmpty())
				return;
			
			while ((sp = con.getSendQueue().removeFirst()) != null)
			{
				WRITE_BUFFER.clear();
				
				putPacketIntoWriteBuffer(con.getClient(), sp);
				
				WRITE_BUFFER.flip();
				
				try
				{
					con.write(WRITE_BUFFER);
				}
				catch (IOException e)
				{
					// error handling goes on the if bellow
				}
			}
		}
    }
    
    protected final void writePacket(final SelectionKey key, final MMOConnection<T> con)
    {
        if (!prepareWriteBuffer(con))
        {
            key.interestOps(key.interestOps() & ~SelectionKey.OP_WRITE);
            return;
        }

        DIRECT_WRITE_BUFFER.flip();
        final int size = DIRECT_WRITE_BUFFER.remaining();
        int result = -1;

        try
        {
            result = con.write(DIRECT_WRITE_BUFFER);
        }
        catch (IOException e)
        {
            // error handling goes on the if bellow
        }

        // check if no error happened
        if (result >= 0)
        {
            // check if we written everything
            if (result == size)
            {
                // complete write
                synchronized (con.getSendQueue())
                {
                    if (con.getSendQueue().isEmpty() && !con.hasPendingWriteBuffer())
                        key.interestOps(key.interestOps() & ~SelectionKey.OP_WRITE);
                }
            }
            else
                // incomplete write
                con.createWriteBuffer(DIRECT_WRITE_BUFFER);
        }
        else
        {
            con.getClient().onForcedDisconnection();
            closeConnectionImpl(key, con);
        }
    }

    private final boolean prepareWriteBuffer(final MMOConnection<T> con)
    {
        boolean hasPending = false;
        DIRECT_WRITE_BUFFER.clear();

        // if there is pending content add it
        if (con.hasPendingWriteBuffer())
        {
            con.movePendingWriteBufferTo(DIRECT_WRITE_BUFFER);
            hasPending = true;
        }

        if (DIRECT_WRITE_BUFFER.remaining() > 1 && !con.hasPendingWriteBuffer())
        {
            final NioNetStackList<SendablePacket<T>> sendQueue = con.getSendQueue();
            final T client = con.getClient();
            SendablePacket<T> sp;

            for (int i = 0; i < MAX_SEND_PER_PASS; i++)
            {
                synchronized (con.getSendQueue())
                {
                    if (sendQueue.isEmpty())
                        sp = null;
                    else
                        sp = sendQueue.removeFirst();
                }

                if (sp == null)
                    break;

                hasPending = true;

                // put into WriteBuffer
                putPacketIntoWriteBuffer(client, sp);

                WRITE_BUFFER.flip();

                if (DIRECT_WRITE_BUFFER.remaining() >= WRITE_BUFFER.limit())
                    DIRECT_WRITE_BUFFER.put(WRITE_BUFFER);
                else
                {
                    con.createWriteBuffer(WRITE_BUFFER);
                    break;
                }
            }
        }
        return hasPending;
    }
    
    private final void putPacketIntoWriteBuffer(final T client,final SendablePacket<T> sp)
    {
        WRITE_BUFFER.clear();

        // reserve space for the size
        final int headerPos = WRITE_BUFFER.position();
        final int dataPos = headerPos + HEADER_SIZE;
        WRITE_BUFFER.position(dataPos);

        // set the write buffer
        sp._buf = WRITE_BUFFER;
        // write content to buffer
        sp.write();
        // delete the write buffer
        sp._buf = null;

        // size (inclusive header)
        int dataSize = WRITE_BUFFER.position() - dataPos;
        WRITE_BUFFER.position(dataPos);
        client.encrypt(WRITE_BUFFER, dataSize);

        // recalculate size after encryption
        dataSize = WRITE_BUFFER.position() - dataPos;

        WRITE_BUFFER.position(headerPos);
        // write header
        WRITE_BUFFER.putShort((short) (dataSize + HEADER_SIZE));
        WRITE_BUFFER.position(dataPos + dataSize);
    }
    
    final void closeConnection(final MMOConnection<T> con)
    {
        synchronized (_pendingClose)
        {
            _pendingClose.addLast(con);
        }
    }
    
    private final void closeConnectionImpl(final SelectionKey key, final MMOConnection<T> con)
    {
        try
        {
            // notify connection
            con.getClient().onDisconnection();
        }
        finally
        {
            try
            {
                // close socket and the SocketChannel
                con.close();
            }
            catch (IOException e)
            {
                // ignore, we are closing anyway
            }
            finally
            {
                con.releaseBuffers();
                // clear attachment
                key.attach(null);
                // cancel key
                key.cancel();
            }
        }
    }
    
    public final void shutdown()
    {
        _shutdown = true;
    }
    
    protected void closeSelectorThread()
    {
        for (final SelectionKey key : _selector.keys())
        {
            try
            {
                key.channel().close();
            }
            catch (IOException e)
            {
                // ignore
            }
        }

        try
        {
            _selector.close();
        }
        catch (IOException e)
        {
            // Ignore
        }
    }
}