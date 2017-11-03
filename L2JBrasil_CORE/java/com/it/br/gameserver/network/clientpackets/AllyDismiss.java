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
package com.it.br.gameserver.network.clientpackets;

import com.it.br.Config;
import com.it.br.gameserver.datatables.sql.ClanTable;
import com.it.br.gameserver.model.L2Clan;
import com.it.br.gameserver.model.actor.instance.L2PcInstance;
import com.it.br.gameserver.network.SystemMessageId;
import com.it.br.gameserver.network.serverpackets.SystemMessage;

public final class AllyDismiss extends L2GameClientPacket
{
    private static final String _C__85_ALLYDISMISS = "[C] 85 AllyDismiss";

    private String _clanName;


    @Override
	protected void readImpl()
    {
    	_clanName = readS();
    }


    @Override
	protected void runImpl()
    {
        if (_clanName == null)
        {
            return;
        }
        L2PcInstance player = getClient().getActiveChar();
        if (player == null)
        {
            return;
        }
		if (player.getClan() == null)
        {
			player.sendPacket(new SystemMessage(SystemMessageId.YOU_ARE_NOT_A_CLAN_MEMBER));
            return;
        }
        L2Clan leaderClan = player.getClan();
		if (leaderClan.getAllyId() == 0)
		{
			player.sendPacket(new SystemMessage(SystemMessageId.NO_CURRENT_ALLIANCES));
			return;
		}
		if (!player.isClanLeader() || leaderClan.getClanId() != leaderClan.getAllyId())
		{
			player.sendPacket(new SystemMessage(SystemMessageId.FEATURE_ONLY_FOR_ALLIANCE_LEADER));
			return;
		}
		L2Clan clan = ClanTable.getInstance().getClanByName(_clanName);
        if (clan == null)
        {
		player.sendPacket(new SystemMessage(SystemMessageId.CLAN_DOESNT_EXISTS));
		return;
        }
        if (clan.getClanId() == leaderClan.getClanId())
        {
		player.sendPacket(new SystemMessage(SystemMessageId.ALLIANCE_LEADER_CANT_WITHDRAW));
		return;
        }
        if (clan.getAllyId() != leaderClan.getAllyId())
        {
		player.sendPacket(new SystemMessage(SystemMessageId.DIFFERENT_ALLIANCE));
		return;
        }

		long currentTime = System.currentTimeMillis();
                leaderClan.setAllyPenaltyExpiryTime(currentTime + Config.ACCEPT_CLAN_DAYS_WHEN_DISMISSED * 86400000L,L2Clan.PENALTY_TYPE_DISMISS_CLAN); //24*60*60*1000 = 86400000
		leaderClan.updateClanInDB();

                clan.setAllyId(0);
                clan.setAllyName(null);
                clan.setAllyPenaltyExpiryTime(currentTime + Config.ALLY_JOIN_DAYS_WHEN_DISMISSED * 86400000L,L2Clan.PENALTY_TYPE_CLAN_DISMISSED); //24*60*60*1000 = 86400000
                clan.updateClanInDB();

                player.sendPacket(new SystemMessage(SystemMessageId.YOU_HAVE_EXPELED_A_CLAN));
     // Added to delete the Alliance Crest when a clan leaves an ally. 
      try 
      { 
          player.getClan().setAllyCrestId(0); 
          for (L2PcInstance member : player.getClan().getOnlineMembers("")) 
              member.broadcastUserInfo(); 
      } 
      catch(Throwable t){} 
    }


    @Override
	public String getType()
    {
        return _C__85_ALLYDISMISS;
    }
}