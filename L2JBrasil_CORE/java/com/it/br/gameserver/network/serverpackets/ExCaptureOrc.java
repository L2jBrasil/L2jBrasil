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
package com.it.br.gameserver.network.serverpackets;

/**
 *
 * @author  KenM
 */
public class ExCaptureOrc extends L2GameServerPacket
{
	private static final String _S__FE_44_EXCAPTUREORC = "[S] FE:44 ExCaptureOrc";

	private final static byte[] _test;
	static
	{
		_test = new byte[]
		{
				(byte) 0xE4 ,(byte) 0xAB ,(byte) 0x8E ,(byte) 0xC5 ,(byte) 0xE9 ,(byte) 0xF9 ,(byte) 0x86 ,(byte) 0x7B,
				(byte) 0x9E ,(byte) 0x5D ,(byte) 0x83 ,(byte) 0x14 ,(byte) 0x05 ,(byte) 0xD4 ,(byte) 0x48 ,(byte) 0x01,
				(byte) 0xCD ,(byte) 0xA2 ,(byte) 0x8D ,(byte) 0x90 ,(byte) 0x62 ,(byte) 0x8C ,(byte) 0xDA ,(byte) 0x32,
				(byte) 0x7B ,(byte) 0x1B ,(byte) 0x87 ,(byte) 0x6D ,(byte) 0x08 ,(byte) 0xC4 ,(byte) 0xE1 ,(byte) 0x56,
				(byte) 0x9B ,(byte) 0x3B ,(byte) 0xC3 ,(byte) 0x40 ,(byte) 0xDF ,(byte) 0xE8 ,(byte) 0xD7 ,(byte) 0xE1,
				(byte) 0x98 ,(byte) 0x38 ,(byte) 0x1C ,(byte) 0xA5 ,(byte) 0x8E ,(byte) 0x45 ,(byte) 0x3F ,(byte) 0xF2,
				(byte) 0x5E ,(byte) 0x1C ,(byte) 0x59 ,(byte) 0x8E ,(byte) 0x74 ,(byte) 0x01 ,(byte) 0x9E ,(byte) 0xC2,
				(byte) 0x00 ,(byte) 0x95 ,(byte) 0xB0 ,(byte) 0x1D ,(byte) 0x87 ,(byte) 0xED ,(byte) 0x9C ,(byte) 0x8A
		};
	}

	/**
	 * @see com.it.br.gameserver.network.serverpackets.L2GameServerPacket#writeImpl()
	 */

	@Override
	protected void writeImpl()
	{
		writeC(0xFE);
		writeH(0x44);
		writeB(_test);
	}

	/**
	 * @see com.it.br.gameserver.network.serverpackets.L2GameServerPacket#getType()
	 */

	@Override
	public String getType()
	{
		return _S__FE_44_EXCAPTUREORC;
	}
}
