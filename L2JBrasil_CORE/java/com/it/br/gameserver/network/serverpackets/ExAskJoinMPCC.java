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
 * @author  chris_00
 *
 * Asks the player to join a CC
 *
 */
public class ExAskJoinMPCC extends L2GameServerPacket
{
	private String _requestorName;

	public ExAskJoinMPCC(String requestorName)
	{
		_requestorName = requestorName;
	}

	@Override
	protected void writeImpl()
	{
		writeC(0xFE);
		writeH(0x27);
		writeS(_requestorName);  // name of CCLeader
	}

	@Override
	public String getType()
	{
		return "[S] FE:27 ExAskJoinMPCC";
	}
}
