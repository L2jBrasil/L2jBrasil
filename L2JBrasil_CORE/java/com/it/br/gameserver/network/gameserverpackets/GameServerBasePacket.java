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
package com.it.br.gameserver.network.gameserverpackets;

import com.it.br.gameserver.TaskPriority;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * @author -Wooden-
 *
 */
public abstract class GameServerBasePacket
{
	private ByteArrayOutputStream _bao;

	protected GameServerBasePacket()
	{
		_bao = new ByteArrayOutputStream();
	}

	protected void writeD(int value)
	{
		_bao.write(value &0xff);
		_bao.write(value >> 8 &0xff);
		_bao.write(value >> 16 &0xff);
		_bao.write(value >> 24 &0xff);
	}

	protected void writeH(int value)
	{
		_bao.write(value &0xff);
		_bao.write(value >> 8 &0xff);
	}

	protected void writeC(int value)
	{
		_bao.write(value &0xff);
	}

	protected void writeF(double org)
	{
		long value = Double.doubleToRawLongBits(org);
		_bao.write((int)(value &0xff));
		_bao.write((int)(value >> 8 &0xff));
		_bao.write((int)(value >> 16 &0xff));
		_bao.write((int)(value >> 24 &0xff));
		_bao.write((int)(value >> 32 &0xff));
		_bao.write((int)(value >> 40 &0xff));
		_bao.write((int)(value >> 48 &0xff));
		_bao.write((int)(value >> 56 &0xff));
	}

	protected void writeS(String text)
	{
		try
		{
			if (text != null)
			{
				_bao.write(text.getBytes("UTF-16LE"));
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		_bao.write(0);
		_bao.write(0);
	}

	protected void writeB(byte[] array)
	{
		try
		{
			_bao.write(array);
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public int getLength()
	{
		return _bao.size()+2;
	}

	public byte[] getBytes()
	{
		writeD(0x00);	// reserve for checksum

		int padding = _bao.size() % 8;
		if (padding != 0)
		{
			for (int i = padding; i<8;i++)
			{
				writeC(0x00);
			}
		}

		return _bao.toByteArray();
	}

	public TaskPriority getPriority() 
        { 
                return TaskPriority.PR_HIGH; 
        }
	public abstract byte[] getContent() throws IOException;
}
