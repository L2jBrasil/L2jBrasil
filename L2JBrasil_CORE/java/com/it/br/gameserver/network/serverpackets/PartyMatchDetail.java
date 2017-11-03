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
package com.it.br.gameserver.network.serverpackets;

import com.it.br.gameserver.model.actor.instance.L2PcInstance;

/**
 *
 *
 * sample
 * b0
 * d8 a8 10 48  objectId
 * 00 00 00 00
 * 00 00 00 00
 * 00 00
 *
 * format   ddddS
 *
 * @version $Revision: 1.1.2.1.2.3 $ $Date: 2005/03/27 15:29:39 $
 */
public class PartyMatchDetail extends L2GameServerPacket
{
	private static final String _S__B0_PARTYMATCHDETAIL = "[S] 97 PartyMatchDetail";
	private L2PcInstance _activeChar;

	/**
	 * @param allPlayers
	 */
	public PartyMatchDetail(L2PcInstance player)
	{
		_activeChar = player;
	}


	@Override
	protected final void writeImpl()
	{
		writeC(0x97);

		writeD(_activeChar.getObjectId());
		if (_activeChar.isPartyMatchingShowLevel())
		{
			writeD(1); // show level
		}
		else
		{
			writeD(0); // hide level
		}

		if (_activeChar.isPartyMatchingShowClass())
		{
			writeD(1); // show class
		}
		else
		{
			writeD(0); // hide class
		}

		writeD(0); //c2

		writeS("  " + _activeChar.getPartyMatchingMemo()); // seems to be bugged.. first 2 chars get stripped away
	}

	/* (non-Javadoc)
	 * @see com.it.br.gameserver.network.serverpackets.ServerBasePacket#getType()
	 */

	@Override
	public String getType()
	{
		return _S__B0_PARTYMATCHDETAIL;
	}
}
