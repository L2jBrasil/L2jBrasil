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

import com.it.br.gameserver.model.L2Clan;

public class PledgeShowInfoUpdate extends L2GameServerPacket
{
	private static final String _S__A1_PLEDGESHOWINFOUPDATE = "[S] 88 PledgeShowInfoUpdate";
	private L2Clan _clan;

	public PledgeShowInfoUpdate(L2Clan clan)
	{
		_clan = clan;
	}

	@Override
	protected final void writeImpl()
	{
		writeC(0x88);
		writeD(_clan.getClanId());
		writeD(_clan.getCrestId());
		writeD(_clan.getLevel()); //clan level
		writeD(_clan.getHasCastle());
		writeD(_clan.getHasHideout());
        writeD(_clan.getRank());
		writeD(_clan.getReputationScore()); // clan reputation score
		writeD(0);
		writeD(0);
		writeD(_clan.getAllyId()); //c5
		writeS(_clan.getAllyName()); //c5
		writeD(_clan.getAllyCrestId()); //c5
		writeD(_clan.isAtWar()); //c5
	}

	@Override
	public String getType()
	{
		return _S__A1_PLEDGESHOWINFOUPDATE;
	}
}