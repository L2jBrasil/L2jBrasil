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
 * Format: (ch)ddddd
 *
 */
public class ExConfirmVariationGemstone extends L2GameServerPacket
{
	private static final String _S__FE_54_EXCONFIRMVARIATIONGEMSTONE = "[S] FE:54 ExConfirmVariationGemstone";

	private int _gemstoneObjId;
	private int _unk1;
	private int _gemstoneCount;
	private int _unk2;
	private int _unk3;

	public ExConfirmVariationGemstone(int gemstoneObjId, int count)
	{
		_gemstoneObjId = gemstoneObjId;
		_unk1 = 1;
		_gemstoneCount = count;
		_unk2 = 1;
		_unk3 = 1;
	}

	/**
	 * @see com.it.br.gameserver.network.serverpackets.ServerBasePacket#writeImpl()
	 */

	@Override
	protected void writeImpl()
	{
		writeC(0xfe);
		writeH(0x54);
        writeD(_gemstoneObjId);
        writeD(_unk1);
        writeD(_gemstoneCount);
        writeD(_unk2);
        writeD(_unk3);
	}

	/**
	 * @see com.it.br.gameserver.BasePacket#getType()
	 */

	@Override
	public String getType()
	{
		return _S__FE_54_EXCONFIRMVARIATIONGEMSTONE;
	}

}
