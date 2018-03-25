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
package com.it.br.gameserver.network.clientpackets;

import com.it.br.gameserver.datatables.sql.ClanTable;
import com.it.br.gameserver.model.L2Clan;
import com.it.br.gameserver.model.L2World;
import com.it.br.gameserver.model.actor.instance.L2PcInstance;
import com.it.br.gameserver.network.SystemMessageId;
import com.it.br.gameserver.network.serverpackets.ActionFailed;
import com.it.br.gameserver.network.serverpackets.SystemMessage;

public final class RequestStopPledgeWar extends L2GameClientPacket
{
	private static final String _C__4F_REQUESTSTOPPLEDGEWAR = "[C] 4F RequestStopPledgeWar";
	//private static Logger _log = LoggerFactory.getLogger(RequestStopPledgeWar.class);

	private String _pledgeName;


	@Override
	protected void readImpl()
	{
		_pledgeName = readS();
	}


	@Override
	protected void runImpl()
	{
		L2PcInstance player = getClient().getActiveChar();
		if (player == null) return;
		L2Clan playerClan = player.getClan();
		if (playerClan == null) return;

		L2Clan clan = ClanTable.getInstance().getClanByName(_pledgeName);

		if (clan == null)
		{
			player.sendMessage("No such clan.");
			player.sendPacket(new ActionFailed());
			return;
		}

		if (!playerClan.isAtWarWith(clan.getClanId()))
		{
			player.sendMessage("You aren't at war with this clan.");
			player.sendPacket(new ActionFailed());
			return;
		}

		// Check if player who does the request has the correct rights to do it
		if ((player.getClanPrivileges() & L2Clan.CP_CL_PLEDGE_WAR) != L2Clan.CP_CL_PLEDGE_WAR )
		{
			player.sendPacket(new SystemMessage(SystemMessageId.YOU_ARE_NOT_AUTHORIZED_TO_DO_THAT));
			return;
		}

		//_log.info("RequestStopPledgeWar: By leader or authorized player: " + playerClan.getLeaderName() + " of clan: "
		//	+ playerClan.getName() + " to clan: " + _pledgeName);

		//        L2PcInstance leader = L2World.getInstance().getPlayer(clan.getLeaderName());
		//        if(leader != null && leader.isOnline() == 0)
		//        {
		//            player.sendMessage("Clan leader isn't online.");
		//            player.sendPacket(new ActionFailed());
		//            return;
		//        }

		//        if (leader.isProcessingRequest())
		//        {
		//            SystemMessage sm = new SystemMessage(SystemMessage.S1_IS_BUSY_TRY_LATER);
		//            sm.addString(leader.getName());
		//            player.sendPacket(sm);
		//            return;
		//        }

		ClanTable.getInstance().deleteclanswars(playerClan.getClanId(), clan.getClanId());
        for (L2PcInstance cha : L2World.getInstance().getAllPlayers()) {
        	if (cha.getClan() == player.getClan() || cha.getClan() == clan)
        		cha.broadcastUserInfo();
        }
	}


	@Override
	public String getType()
	{
		return _C__4F_REQUESTSTOPPLEDGEWAR;
	}
}