/*
 * This program is free software; you can redistribute it and/or modify
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
package com.it.br.gameserver.geoeditorcon;

import com.it.br.gameserver.model.actor.instance.L2PcInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Luno, Dezmond
 */
public class GeoEditorThread extends Thread
{
	private static Logger _log = LoggerFactory.getLogger(GeoEditorThread.class
			.getName());

	private boolean _working = false;

	private int _mode = 0; // 0 - don't send coords, 1 - send each

	// validateposition from client, 2 - send in
	// intervals of _sendDelay ms.
	private int _sendDelay = 1000; // default - once in second

	private Socket _geSocket;

	private OutputStream _out;

	private List<L2PcInstance> _gms;

	public GeoEditorThread(Socket ge)
	{
		_geSocket = ge;
		_working = true;
		_gms = new ArrayList<>();
	}


	@Override
	public void interrupt()
	{
		try
		{
			_geSocket.close();
		} catch (Exception e)
		{
		}
		super.interrupt();
	}


	@Override
	public void run()
	{
		try
		{
			_out = _geSocket.getOutputStream();
			int timer = 0;

			while (_working)
			{
				if (!isConnected())
					_working = false;

				if (_mode == 2 && timer > _sendDelay)
				{
					for (L2PcInstance gm : _gms)
						if (!gm.getClient().getConnection().isClosed())
							sendGmPosition(gm);
						else
							_gms.remove(gm);
					timer = 0;
				}

				try
				{
					sleep(100);
					if (_mode == 2)
						timer += 100;
				} catch (Exception e)
				{
				}
			}
		} catch (SocketException e)
		{
			_log.warn("GeoEditor disconnected. " + e.getMessage());
		} catch (Exception e)
		{
			e.printStackTrace();
		} finally
		{
			try
			{
				_geSocket.close();
			} catch (Exception e)
			{
			}
			_working = false;
		}
	}

	public void sendGmPosition(int gx, int gy, short z)
	{
		if (!isConnected())
			return;
		try
		{
			synchronized (_out)
			{
				writeC(0x0b); // length 11 bytes!
				writeC(0x01); // Cmd = save cell;
				writeD(gx); // Global coord X;
				writeD(gy); // Global coord Y;
				writeH(z); // Coord Z;
				_out.flush();
			}
		} catch (SocketException e)
		{
			_log.warn("GeoEditor disconnected. " + e.getMessage());
			_working = false;
		} catch (Exception e)
		{
			e.printStackTrace();
			try
			{
				_geSocket.close();
			} catch (Exception ex)
			{
			}
			_working = false;
		}
	}

	public void sendGmPosition(L2PcInstance _gm)
	{
		sendGmPosition(_gm.getX(), _gm.getY(), (short) _gm.getZ());
	}

	public void sendPing()
	{
		if (!isConnected())
			return;
		try
		{
			synchronized (_out)
			{
				writeC(0x01); // length 1 byte!
				writeC(0x02); // Cmd = ping (dummy packet for connection
								// test);
				_out.flush();
			}
		} catch (SocketException e)
		{
			_log.warn("GeoEditor disconnected. " + e.getMessage());
			_working = false;
		} catch (Exception e)
		{
			e.printStackTrace();
			try
			{
				_geSocket.close();
			} catch (Exception ex)
			{
			}
			_working = false;
		}
	}

	private void writeD(int value) throws IOException
	{
		_out.write(value & 0xff);
		_out.write(value >> 8 & 0xff);
		_out.write(value >> 16 & 0xff);
		_out.write(value >> 24 & 0xff);
	}

	private void writeH(int value) throws IOException
	{
		_out.write(value & 0xff);
		_out.write(value >> 8 & 0xff);
	}

	private void writeC(int value) throws IOException
	{
		_out.write(value & 0xff);
	}

	public void setMode(int value)
	{
		_mode = value;
	}

	public void setTimer(int value)
	{
		if (value < 500)
			_sendDelay = 500; // maximum - 2 times per second!
		else if (value > 60000)
			_sendDelay = 60000; // Minimum - 1 time per minute.
		else
			_sendDelay = value;
	}

	public void addGM(L2PcInstance gm)
	{
		if (!_gms.contains(gm))
			_gms.add(gm);
	}

	public void removeGM(L2PcInstance gm)
	{
		if (_gms.contains(gm))
			_gms.remove(gm);
	}

	public boolean isSend(L2PcInstance gm)
	{
		if (_mode == 1 && _gms.contains(gm))
			return true;
		return false;
	}

	private boolean isConnected()
	{
		return _geSocket.isConnected() && !_geSocket.isClosed();
	}

	public boolean isWorking()
	{
		sendPing();
		return _working;
	}

	public int getMode()
	{
		return _mode;
	}
}
