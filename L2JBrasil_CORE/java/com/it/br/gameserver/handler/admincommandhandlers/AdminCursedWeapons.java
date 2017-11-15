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

import com.it.br.Config;
import com.it.br.gameserver.handler.IAdminCommandHandler;
import com.it.br.gameserver.instancemanager.CursedWeaponsManager;
import com.it.br.gameserver.model.CursedWeapon;
import com.it.br.gameserver.model.L2Object;
import com.it.br.gameserver.model.actor.instance.L2PcInstance;
import com.it.br.gameserver.network.SystemMessageId;
import com.it.br.gameserver.network.serverpackets.NpcHtmlMessage;
import com.it.br.gameserver.network.serverpackets.SystemMessage;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

/**
 * This class handles following admin commands:
 * - cw_info = displays cursed weapon status
 * - cw_remove = removes a cursed weapon from the world, item id or name must be provided
 * - cw_add = adds a cursed weapon into the world, item id or name must be provided. Target will be the weilder
 * - cw_goto = teleports GM to the specified cursed weapon
 * - cw_reload = reloads instance manager
 * @version $Revision: 3.0.3 $ $Date: 2017/11/09 $
 */
public class AdminCursedWeapons implements IAdminCommandHandler
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

    public AdminCursedWeapons()
    {
        admin.put("admin_cw_info", Config.admin_cw_info);
        admin.put("admin_cw_remove", Config.admin_cw_remove);
        admin.put("admin_cw_goto", Config.admin_cw_goto);
        admin.put("admin_cw_reload", Config.admin_cw_reload);
        admin.put("admin_cw_add", Config.admin_cw_add);
        admin.put("admin_cw_info_menu", Config.admin_cw_info_menu);
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

		CursedWeaponsManager cwm = CursedWeaponsManager.getInstance();
		int id=0;

		if (command.startsWith("admin_cw_info"))
		{
			if (!command.contains("menu"))
			{
				activeChar.sendMessage("====== Cursed Weapons: ======");
				for (CursedWeapon cw : cwm.getCursedWeapons())
				{
					activeChar.sendMessage("> "+cw.getName()+" ("+cw.getItemId()+")");
					if (cw.isActivated())
					{
						L2PcInstance pl = cw.getPlayer();
						activeChar.sendMessage("  Player holding: "+ pl==null ? "null" : pl.getName());
						activeChar.sendMessage("    Player karma: "+cw.getPlayerKarma());
						activeChar.sendMessage("    Time Remaining: "+(cw.getTimeLeft()/60000)+" min.");
						activeChar.sendMessage("    Kills : "+cw.getNbKills());
					}
					else if (cw.isDropped())
					{
						activeChar.sendMessage("  Lying on the ground.");
						activeChar.sendMessage("    Time Remaining: "+(cw.getTimeLeft()/60000)+" min.");
						activeChar.sendMessage("    Kills : "+cw.getNbKills());
					}
					else
					{
						activeChar.sendMessage("  Don't exist in the world.");
					}
					activeChar.sendPacket(new SystemMessage(SystemMessageId.FRIEND_LIST_FOOT));
				}
			}
			else
			{
				StringBuilder replyMSG = new StringBuilder();
				NpcHtmlMessage adminReply = new NpcHtmlMessage(5);
				adminReply.setFile("data/html/admin/cwinfo.htm");
				for (CursedWeapon cw : cwm.getCursedWeapons())
				{
					int itemId = cw.getItemId();
					replyMSG.append("<table width=270><tr><td>Name:</td><td>"+cw.getName()+"</td></tr>");
					if (cw.isActivated())
					{
						L2PcInstance pl = cw.getPlayer();
						replyMSG.append("<tr><td>Weilder:</td><td>"+ (pl==null?"null" : pl.getName())+"</td></tr>");
						replyMSG.append("<tr><td>Karma:</td><td>"+String.valueOf(cw.getPlayerKarma())+"</td></tr>");
						replyMSG.append("<tr><td>Kills:</td><td>"+String.valueOf(cw.getPlayerPkKills())+"/"+String.valueOf(cw.getNbKills())+"</td></tr>");
						replyMSG.append("<tr><td>Time remaining:</td><td>"+String.valueOf(cw.getTimeLeft()/60000)+" min.</td></tr>");
						replyMSG.append("<tr><td><button value=\"Remove\" action=\"bypass -h admin_cw_remove "+String.valueOf(itemId)+"\" width=73 height=15 back=\"sek.cbui94\" fore=\"sek.cbui92\"></td>");
						replyMSG.append("<td><button value=\"Go\" action=\"bypass -h admin_cw_goto "+String.valueOf(itemId)+"\" width=73 height=15 back=\"sek.cbui94\" fore=\"sek.cbui92\"></td></tr>");
					}
					else if (cw.isDropped())
					{
						replyMSG.append("<tr><td>Position:</td><td>Lying on the ground</td></tr>");
						replyMSG.append("<tr><td>Time remaining:</td><td>"+String.valueOf(cw.getTimeLeft()/60000)+" min.</td></tr>");
						replyMSG.append("<tr><td>Kills:</td><td>"+String.valueOf(cw.getNbKills())+"</td></tr>");
						replyMSG.append("<tr><td><button value=\"Remove\" action=\"bypass -h admin_cw_remove "+String.valueOf(itemId)+"\" width=73 height=15 back=\"sek.cbui94\" fore=\"sek.cbui92\"></td>");
						replyMSG.append("<td><button value=\"Go\" action=\"bypass -h admin_cw_goto "+String.valueOf(itemId)+"\" width=73 height=15 back=\"sek.cbui94\" fore=\"sek.cbui92\"></td></tr>");
					}
					else
					{
						replyMSG.append("<tr><td>Position:</td><td>Doesn't exist.</td></tr>");
						replyMSG.append("<tr><td><button value=\"Give to Target\" action=\"bypass -h admin_cw_add "+String.valueOf(itemId)+"\" width=99 height=15 back=\"sek.cbui94\" fore=\"sek.cbui92\"></td><td></td></tr>");
					}
					replyMSG.append("</table>");
					replyMSG.append("<br>");
				}
				adminReply.replace("%cwinfo%", replyMSG.toString());
				activeChar.sendPacket(adminReply);
			}
		}
		else if (command.startsWith("admin_cw_reload"))
		{
			cwm.reload();
		}
		else
		{
			CursedWeapon cw=null;
			try
			{
				String parameter = st.nextToken();
				if (parameter.matches("[0-9]*"))
					id = Integer.parseInt(parameter);
				else
				{
					parameter = parameter.replace('_', ' ');
					for (CursedWeapon cwp : cwm.getCursedWeapons())
					{
						if (cwp.getName().toLowerCase().contains(parameter.toLowerCase()))
						{
							id=cwp.getItemId();
							break;
						}
					}
				}
				cw = cwm.getCursedWeapon(id);
				if (cw==null)
				{
					activeChar.sendMessage("Unknown cursed weapon ID.");
					return false;
				}
			}
			catch (Exception e)
			{
				activeChar.sendMessage("Usage: //cw_remove|//cw_goto|//cw_add <itemid|name>");
			}

			if (command.startsWith("admin_cw_remove "))
			{
				cw.endOfLife();
			}
			else if (command.startsWith("admin_cw_goto "))
			{
				cw.goTo(activeChar);
			}
			else if (command.startsWith("admin_cw_add"))
			{
				if (cw==null)
				{
					activeChar.sendMessage("Usage: //cw_add <itemid|name>");
					return false;
				}
				else if (cw.isActive())
					activeChar.sendMessage("This cursed weapon is already active.");
				else
				{
					L2Object target = activeChar.getTarget();
					if (target != null && target instanceof L2PcInstance)
						((L2PcInstance)target).addItem("AdminCursedWeaponAdd", id, 1, target, true);
					else
						activeChar.addItem("AdminCursedWeaponAdd", id, 1, activeChar, true);
				}
			}
			else
			{
				activeChar.sendMessage("Unknown command.");
			}
		}
		return true;
	}
}
