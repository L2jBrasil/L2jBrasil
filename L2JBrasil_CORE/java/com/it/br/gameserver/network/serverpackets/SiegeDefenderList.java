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

//import java.util.Calendar; //signed time related
//import org.slf4j.Logger; import org.slf4j.LoggerFactory;

import com.it.br.gameserver.datatables.sql.ClanTable;
import com.it.br.gameserver.model.L2Clan;
import com.it.br.gameserver.model.L2SiegeClan;
import com.it.br.gameserver.model.entity.Castle;
/**
 * Populates the Siege Defender List in the SiegeInfo Window<BR>
 * <BR>
 * packet type id 0xcb<BR>
 * format: cddddddd + dSSdddSSd<BR>
 * <BR>
 * c = 0xcb<BR>
 * d = CastleID<BR>
 * d = unknow (0x00)<BR>
 * d = unknow (0x01)<BR>
 * d = unknow (0x00)<BR>
 * d = Number of Defending Clans?<BR>
 * d = Number of Defending Clans<BR>
 * { //repeats<BR>
 * d = ClanID<BR>
 * S = ClanName<BR>
 * S = ClanLeaderName<BR>
 * d = ClanCrestID<BR>
 * d = signed time (seconds)<BR>
 * d = Type -> Owner = 0x01 || Waiting = 0x02 || Accepted = 0x03<BR>
 * d = AllyID<BR>
 * S = AllyName<BR>
 * S = AllyLeaderName<BR>
 * d = AllyCrestID<BR>
 *
 * @author KenM
 */
public class SiegeDefenderList extends L2GameServerPacket
{
	private static final String _S__CA_SiegeDefenderList = "[S] cb SiegeDefenderList";
	//private static Logger _log = LoggerFactory.getLogger(SiegeDefenderList.class);
	private Castle _castle;

	public SiegeDefenderList(Castle castle)
	{
		_castle = castle;
	}


	@Override
	protected final void writeImpl()
	{
		writeC(0xcb);
		writeD(_castle.getCastleId());
		writeD(0x00);  //0
		writeD(0x01);  //1
		writeD(0x00);  //0
		int size = _castle.getSiege().getDefenderClans().size() + _castle.getSiege().getDefenderWaitingClans().size();
		if (size > 0)
		{
			L2Clan clan;

			writeD(size);
			writeD(size);
			// Listing the Lord and the approved clans
			for(L2SiegeClan siegeclan : _castle.getSiege().getDefenderClans())
			{
				clan = ClanTable.getInstance().getClan(siegeclan.getClanId());
				if (clan == null) continue;

				writeD(clan.getClanId());
				writeS(clan.getName());
				writeS(clan.getLeaderName());
				writeD(clan.getCrestId());
				writeD(0x00); //signed time (seconds) (not storated by L2J)
				switch(siegeclan.getType())
				{
					case OWNER:
						writeD(0x01); //owner
						break;
					case DEFENDER_PENDING:
						writeD(0x02); //approved
						break;
					case DEFENDER:
						writeD(0x03); // waiting approved
						break;
					default:
						writeD(0x00);
					break;
				}
				writeD(clan.getAllyId());
				writeS(clan.getAllyName());
				writeS(""); //AllyLeaderName
				writeD(clan.getAllyCrestId());
			}
			for(L2SiegeClan siegeclan : _castle.getSiege().getDefenderWaitingClans())
			{
				clan = ClanTable.getInstance().getClan(siegeclan.getClanId());
				writeD(clan.getClanId());
				writeS(clan.getName());
				writeS(clan.getLeaderName());
				writeD(clan.getCrestId());
				writeD(0x00); //signed time (seconds) (not storated by L2J)
				writeD(0x02); //waiting approval
				writeD(clan.getAllyId());
				writeS(clan.getAllyName());
				writeS(""); //AllyLeaderName
				writeD(clan.getAllyCrestId());
			}
		}
		else
		{
			writeD(0x00);
			writeD(0x00);
		}
	}

	/* (non-Javadoc)
	 * @see com.it.br.gameserver.network.serverpackets.ServerBasePacket#getType()
	 */

	@Override
	public String getType()
	{
		return _S__CA_SiegeDefenderList;
	}

}
