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

import java.nio.ByteBuffer;

/**
 * @author KenM
 * 
 */
public abstract class ReceivablePacket<T extends MMOClient<?>> extends AbstractPacket<T> implements Runnable
{
    NioNetStringBuffer _sbuf;

    protected ReceivablePacket(){}

    protected abstract boolean read();

    public abstract void run();

    protected final void readB(final byte[] dst)
    {
        _buf.get(dst);
    }

    protected final void readB(final byte[] dst, final int offset, final int len)
    {
        _buf.get(dst, offset, len);
    }

    protected final int readC()
    {
        return _buf.get() & 0xFF;
    }

    protected final int readH()
    {
        return _buf.getShort() & 0xFFFF;
    }

    protected final int readD()
    {
        return _buf.getInt();
    }

    protected final long readQ()
    {
        return _buf.getLong();
    }

    protected final double readF()
    {
        return _buf.getDouble();
    }

    protected final String readS()
    {
        _sbuf.clear();

        char ch;
        while ((ch = _buf.getChar()) != 0)
        {
            _sbuf.append(ch);
        }

        return _sbuf.toString();
    }
    
	/**
	 * packet forge purpose
	 * @param data
	 * @param client
	 * @param sBuffer
	 */
	public void setBuffers(ByteBuffer data, T client, NioNetStringBuffer sBuffer)
	{
		_buf = data;
		_client = client;
		_sbuf = sBuffer;
	}
}