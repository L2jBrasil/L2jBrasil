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
package com.it.br.gameserver.handler.admincommandhandlers;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import com.it.br.Config;
import com.it.br.gameserver.datatables.sql.ClanTable;
import com.it.br.gameserver.handler.IAdminCommandHandler;
import com.it.br.gameserver.instancemanager.AuctionManager;
import com.it.br.gameserver.instancemanager.CastleManager;
import com.it.br.gameserver.instancemanager.ClanHallManager;
import com.it.br.gameserver.model.L2Clan;
import com.it.br.gameserver.model.L2Object;
import com.it.br.gameserver.model.actor.instance.L2PcInstance;
import com.it.br.gameserver.model.entity.Castle;
import com.it.br.gameserver.model.entity.ClanHall;
import com.it.br.gameserver.model.zone.type.L2ClanHallZone;
import com.it.br.gameserver.network.SystemMessageId;
import com.it.br.gameserver.network.serverpackets.NpcHtmlMessage;
import com.it.br.gameserver.network.serverpackets.SystemMessage;

/**
 * This class handles all siege commands:
 * @version $Revision: 3.0.3 $ $Date: 2017/11/09 $
 *
 */
public class AdminSiege implements IAdminCommandHandler
{
    private static Map<String, Integer> admin = new HashMap<>();

    private boolean checkPermission(String command, L2PcInstance activeChar)
    {
        if (!Config.ALT_PRIVILEGES_ADMIN)
            if (!(checkLevel(command, activeChar.getAccessLevel()) && activeChar.isGM()))
            {
                activeChar.sendMessage("E necessario ter Access Level " + admin.get(command) + " para usar o comando : " + command);
                return true;
            }
        return false;
    }

    private boolean checkLevel(String command, int level)
    {
        Integer requiredAcess = admin.get(command);
        return (level >= requiredAcess);
    }

    public AdminSiege()
    {
        admin.put("admin_siege", Config.admin_siege);
        admin.put("admin_add_attacker", Config.admin_add_attacker);
        admin.put("admin_add_defender", Config.admin_add_defender);
        admin.put("admin_add_guard", Config.admin_add_guard);
        admin.put("admin_list_siege_clans", Config.admin_list_siege_clans);
        admin.put("admin_clear_siege_list", Config.admin_clear_siege_list);
        admin.put("admin_move_defenders", Config.admin_move_defenders);
        admin.put("admin_spawn_doors", Config.admin_spawn_doors);
        admin.put("admin_endsiege", Config.admin_endsiege);
        admin.put("admin_startsiege", Config.admin_startsiege);
        admin.put("admin_setcastle", Config.admin_setcastle);
        admin.put("admin_removecastle", Config.admin_removecastle);
        admin.put("admin_clanhall", Config.admin_clanhall);
        admin.put("admin_clanhallset", Config.admin_clanhallset);
        admin.put("admin_clanhalldel", Config.admin_clanhalldel);
        admin.put("admin_clanhallopendoors", Config.admin_clanhallopendoors);
        admin.put("admin_clanhallclosedoors", Config.admin_clanhallclosedoors);
        admin.put("admin_clanhallteleportself", Config.admin_clanhallteleportself);
    }

    public Set<String> getAdminCommandList()
    {
        return admin.keySet();
    }

    public boolean useAdminCommand(String command, L2PcInstance activeChar)
    {
        StringTokenizer st = new StringTokenizer(command);
        String commandName = st.nextToken();

        if(checkPermission(commandName, activeChar)) return false;

		// Get castle
		Castle castle = null;
		ClanHall clanhall = null;
		if (command.startsWith("admin_clanhall"))
			clanhall = ClanHallManager.getInstance().getClanHallById(Integer.parseInt(st.nextToken()));
		else if (st.hasMoreTokens())
			castle = CastleManager.getInstance().getCastle(st.nextToken());
		// Get castle
		String val = "";
		if (st.hasMoreTokens())
			val = st.nextToken();
		if ((castle == null  || castle.getCastleId() < 0) && clanhall == null)
			// No castle specified
			showCastleSelectPage(activeChar);
		else
		{
			L2Object target = activeChar.getTarget();
			L2PcInstance player = null;
			if (target instanceof L2PcInstance)
				player = (L2PcInstance)target;

			if (command.equalsIgnoreCase("admin_add_attacker"))
			{
				if (player == null)
					activeChar.sendPacket(new SystemMessage(SystemMessageId.TARGET_IS_INCORRECT));
				else
					castle.getSiege().registerAttacker(player,true);
			}
			else if (command.equalsIgnoreCase("admin_add_defender"))
			{
				if (player == null)
					activeChar.sendPacket(new SystemMessage(SystemMessageId.TARGET_IS_INCORRECT));
				else
					castle.getSiege().registerDefender(player,true);
			}
			else if (command.equalsIgnoreCase("admin_add_guard"))
			{
				try
				{
					int npcId = Integer.parseInt(val);
					castle.getSiege().getSiegeGuardManager().addSiegeGuard(activeChar, npcId);
				}
				catch (Exception e)
				{
					activeChar.sendMessage("Usage: //add_guard npcId");
				}
			}
			else if (command.equalsIgnoreCase("admin_clear_siege_list"))
			{
				castle.getSiege().clearSiegeClan();
			}
			else if (command.equalsIgnoreCase("admin_endsiege"))
			{
				castle.getSiege().endSiege();
			}
			else if (command.equalsIgnoreCase("admin_list_siege_clans"))
			{
				castle.getSiege().listRegisterClan(activeChar);
				return true;
			}
			else if (command.equalsIgnoreCase("admin_move_defenders"))
			{
				activeChar.sendPacket(SystemMessage.sendString("Not implemented yet."));
			}
			else if (command.equalsIgnoreCase("admin_setcastle"))
			{
				if (player == null || player.getClan() == null)
					activeChar.sendPacket(new SystemMessage(SystemMessageId.TARGET_IS_INCORRECT));
				else
					castle.setOwner(player.getClan());
			}
			else if (command.equalsIgnoreCase("admin_removecastle"))
			{
				L2Clan clan = ClanTable.getInstance().getClan(castle.getOwnerId());
				if (clan != null)
					castle.removeOwner(clan);
				else
					activeChar.sendMessage("Unable to remove castle");
			}
			else if (command.equalsIgnoreCase("admin_clanhallset"))
			{
				if (player == null || player.getClan() == null)
					activeChar.sendPacket(new SystemMessage(SystemMessageId.TARGET_IS_INCORRECT));
				else if(!ClanHallManager.getInstance().isFree(clanhall.getId()))
					activeChar.sendMessage("This ClanHall isn't free!");
				else if(player.getClan().getHasHideout() == 0)
				{
					ClanHallManager.getInstance().setOwner(clanhall.getId(), player.getClan());
					if(AuctionManager.getInstance().getAuction(clanhall.getId()) != null)
						AuctionManager.getInstance().getAuction(clanhall.getId()).deleteAuctionFromDB();
				}
				else
					activeChar.sendMessage("You have already a ClanHall!");
			}
			else if (command.equalsIgnoreCase("admin_clanhalldel"))
			{
				if(!ClanHallManager.getInstance().isFree(clanhall.getId())){
					ClanHallManager.getInstance().setFree(clanhall.getId());
					AuctionManager.getInstance().initNPC(clanhall.getId());
				}else
					activeChar.sendMessage("This ClanHall is already Free!");
			}
			else if (command.equalsIgnoreCase("admin_clanhallopendoors"))
			{
				clanhall.openCloseDoors(true);
			}
			else if (command.equalsIgnoreCase("admin_clanhallclosedoors"))
			{
				clanhall.openCloseDoors(false);
			}
			else if (command.equalsIgnoreCase("admin_clanhallteleportself"))
			{
				L2ClanHallZone zone = clanhall.getZone();
				if (zone != null)
				{
					activeChar.teleToLocation(zone.getSpawn(), true);
				}
			}
			else if (command.equalsIgnoreCase("admin_spawn_doors"))
			{
				castle.spawnDoor();
			}
			else if (command.equalsIgnoreCase("admin_startsiege"))
			{
				castle.getSiege().startSiege();
			}
			if (clanhall != null)
				showClanHallPage(activeChar, clanhall);
			else
				showSiegePage(activeChar, castle.getName());
		}
		return true;
	}

	private void showCastleSelectPage(L2PcInstance activeChar)
	{
		int i=0;
		NpcHtmlMessage adminReply = new NpcHtmlMessage(5);
		adminReply.setFile("data/html/admin/castles.htm");
		StringBuilder cList = new StringBuilder();
		for (Castle castle: CastleManager.getInstance().getCastles())
		{
			if (castle != null)
			{
				String name=castle.getName();
				cList.append("<td fixwidth=90><a action=\"bypass -h admin_siege "+name+"\">"+name+"</a></td>");
				i++;
			}
			if (i>2)
			{
				cList.append("</tr><tr>");
				i=0;
			}
		}
		adminReply.replace("%castles%", cList.toString());
		cList.setLength(0);
		i=0;
		for (ClanHall clanhall: ClanHallManager.getInstance().getClanHalls().values())
		{
			if (clanhall != null)
			{
				cList.append("<td fixwidth=134><a action=\"bypass -h admin_clanhall "+clanhall.getId()+"\">");
				cList.append(clanhall.getName()+"</a></td>");
				i++;
			}
			if (i>1)
			{
				cList.append("</tr><tr>");
				i=0;
			}
		}
		adminReply.replace("%clanhalls%", cList.toString());
		cList.setLength(0);
		i=0;
		for (ClanHall clanhall: ClanHallManager.getInstance().getFreeClanHalls().values())
		{
			if (clanhall != null)
			{
				cList.append("<td fixwidth=134><a action=\"bypass -h admin_clanhall "+clanhall.getId()+"\">");
				cList.append(clanhall.getName()+"</a></td>");
				i++;
			}
			if (i>1)
			{
				cList.append("</tr><tr>");
				i=0;
			}
		}
		adminReply.replace("%freeclanhalls%", cList.toString());
		activeChar.sendPacket(adminReply);
	}

	private void showSiegePage(L2PcInstance activeChar, String castleName)
	{
		NpcHtmlMessage adminReply = new NpcHtmlMessage(5);
		adminReply.setFile("data/html/admin/castle.htm");
		adminReply.replace("%castleName%", castleName);
		activeChar.sendPacket(adminReply);
	}

	private void showClanHallPage(L2PcInstance activeChar, ClanHall clanhall)
	{
		NpcHtmlMessage adminReply = new NpcHtmlMessage(5);
		adminReply.setFile("data/html/admin/clanhall.htm");
		adminReply.replace("%clanhallName%", clanhall.getName());
		adminReply.replace("%clanhallId%", String.valueOf(clanhall.getId()));
		L2Clan owner = ClanTable.getInstance().getClan(clanhall.getOwnerId());
		if (owner == null)
			adminReply.replace("%clanhallOwner%","None");
		else
			adminReply.replace("%clanhallOwner%",owner.getName());
		activeChar.sendPacket(adminReply);
	}
}
