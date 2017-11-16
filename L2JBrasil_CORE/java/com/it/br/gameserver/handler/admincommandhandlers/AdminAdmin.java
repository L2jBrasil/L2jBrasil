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
 * [URL]http://www.gnu.org/copyleft/gpl.html[/URL]
 */
package com.it.br.gameserver.handler.admincommandhandlers;

import com.it.br.Config;
import com.it.br.configuration.Configurator;
import com.it.br.configuration.settings.NetworkSettings;
import com.it.br.gameserver.GmListTable;
import com.it.br.gameserver.cache.HtmCache;
import com.it.br.gameserver.datatables.sql.ItemTable;
import com.it.br.gameserver.datatables.sql.SkillTable;
import com.it.br.gameserver.datatables.xml.NpcTable;
import com.it.br.gameserver.datatables.xml.NpcWalkerRoutesTable;
import com.it.br.gameserver.datatables.xml.TeleportLocationTable;
import com.it.br.gameserver.handler.IAdminCommandHandler;
import com.it.br.gameserver.instancemanager.Manager;
import com.it.br.gameserver.model.GMAudit;
import com.it.br.gameserver.model.L2Multisell;
import com.it.br.gameserver.model.Olympiad.Olympiad;
import com.it.br.gameserver.model.actor.instance.L2PcInstance;
import com.it.br.gameserver.network.SystemMessageId;
import com.it.br.gameserver.network.serverpackets.SystemMessage;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

/**
 * This class handles following admin commands:
 * - admin|admin1/admin2/admin3/admin4/admin5 = slots for the 5 starting admin menus
 * - gmliston/gmlistoff = includes/excludes active character from /gmlist results
 * - silence = toggles private messages acceptance mode
 * - diet = toggles weight penalty mode
 * - tradeoff = toggles trade acceptance mode
 * - reload = reloads specified component from multisell|skill|npc|htm|item|instancemanager
 * - set/set_menu/set_mod = alters specified server setting
 * - saveolymp = saves olympiad state manually
 * - manualhero = cycles olympiad and calculate new heroes.
 * @version $Revision: 3.0.3 $ $Date: 2017/11/09 $
 */
public class AdminAdmin implements IAdminCommandHandler
{
    private static Map<String, Integer> admin = new HashMap<>();

    public AdminAdmin()
    {
        admin.put("admin_admin", Config.admin_admin);
        admin.put("admin_admin1", Config.admin_admin1);
        admin.put("admin_admin2", Config.admin_admin2);
        admin.put("admin_admin3", Config.admin_admin3);
        admin.put("admin_admin4", Config.admin_admin4);
        admin.put("admin_admin5", Config.admin_admin5);
        admin.put("admin_gmliston", Config.admin_gmliston);
        admin.put("admin_gmlistoff", Config.admin_gmlistoff);
        admin.put("admin_silence", Config.admin_silence);
        admin.put("admin_diet", Config.admin_diet);
        admin.put("admin_tradeoff", Config.admin_tradeoff);
        admin.put("admin_set", Config.admin_set);
        admin.put("admin_set_menu", Config.admin_set_menu);
        admin.put("admin_set_mod", Config.admin_set_mod);
        admin.put("admin_saveolymp", Config.admin_saveolymp);
        admin.put("admin_manualhero", Config.admin_manualhero);
    }

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

    public Set<String> getAdminCommandList()
    {
        return admin.keySet();
    }

	public boolean useAdminCommand(String command, L2PcInstance activeChar)
    {
        StringTokenizer st = new StringTokenizer(command);
        String commandName = st.nextToken();

        if(checkPermission(commandName, activeChar)) return false;

		GMAudit.auditGMAction(activeChar.getName(), command, (activeChar.getTarget() != null ? activeChar.getTarget().getName() : "no-target"), "");

		if (command.startsWith("admin_admin"))
		{
			showMainPage(activeChar,command);
		}
		else if(command.startsWith("admin_gmliston"))
		{
			GmListTable.getInstance().showGm(activeChar);
			activeChar.sendMessage("Registered to gm list");
		}
		else if(command.startsWith("admin_gmlistoff"))
		{
			GmListTable.getInstance().hideGm(activeChar);
			activeChar.sendMessage("Removed from gm list");
		}
		else if(command.startsWith("admin_silence"))
		{
			if (activeChar.getMessageRefusal()) // already in message refusal mode
			{
				activeChar.setMessageRefusal(false);
				activeChar.sendPacket(new SystemMessage(SystemMessageId.MESSAGE_ACCEPTANCE_MODE));
			}
			else
			{
				activeChar.setMessageRefusal(true);
				activeChar.sendPacket(new SystemMessage(SystemMessageId.MESSAGE_REFUSAL_MODE));
			}
		}
		else if(command.startsWith("admin_saveolymp"))
		{
			try
			{
				Olympiad.getInstance().save();
			}
			catch(Exception e){e.printStackTrace();}
			activeChar.sendMessage("Olympiad saved!!");
		}
		else if(command.startsWith("admin_manualhero"))
		{
			try
			{
				Olympiad.getInstance().manualSelectHeroes();
			}
			catch(Exception e){e.printStackTrace();}
			activeChar.sendMessage("Heroes formatted");
		}
		else if(command.startsWith("admin_diet"))
		{
			try
			{
				if(st.nextToken().equalsIgnoreCase("on"))
				{
					activeChar.setDietMode(true);
					activeChar.sendMessage("Diet mode on");
				}
				else if(st.nextToken().equalsIgnoreCase("off"))
				{
					activeChar.setDietMode(false);
					activeChar.sendMessage("Diet mode off");
				}
			}
			catch(Exception ex)
			{
				if(activeChar.getDietMode())
				{
					activeChar.setDietMode(false);
					activeChar.sendMessage("Diet mode off");
				}
				else
				{
					activeChar.setDietMode(true);
					activeChar.sendMessage("Diet mode on");
				}
			}
			finally
			{
				activeChar.refreshOverloaded();
			}
		}
		else if(command.startsWith("admin_tradeoff"))
		{
			try
			{
				String mode = command.substring(15);
				if (mode.equalsIgnoreCase("on"))
				{
					activeChar.setTradeRefusal(true);
					activeChar.sendMessage("trade refusal on");
				}
				else if (mode.equalsIgnoreCase("off"))
				{
					activeChar.setTradeRefusal(false);
					activeChar.sendMessage("trade refusal off");
				}
			}
			catch(Exception ex)
			{
				if(activeChar.getTradeRefusal())
				{
					activeChar.setTradeRefusal(false);
					activeChar.sendMessage("trade refusal disabled");
				}
				else
				{
					activeChar.setTradeRefusal(true);
					activeChar.sendMessage("trade refusal enabled");
				}
			}
		}
		else if(command.startsWith("admin_set"))
		{
			String[] cmd=commandName.split("_");
			try
			{
				String[] parameter = st.nextToken().split("=");
				String pName = parameter[0].trim();
				String pValue = parameter[1].trim();
				if (Config.setParameterValue(pName, pValue))
					activeChar.sendMessage("parameter "+pName+" succesfully set to "+pValue);
				else
					activeChar.sendMessage("Parameter invalid!");
			}
			catch(Exception e)
			{
				if (cmd.length==2)
					activeChar.sendMessage("Uso: //set parameter=value");
			}
			finally
			{
				if (cmd.length==3)
				{
					if (cmd[2].equalsIgnoreCase("menu"))
						AdminHelpPage.showHelpPage(activeChar, "settings.htm");
					else if (cmd[2].equalsIgnoreCase("mod"))
						AdminHelpPage.showHelpPage(activeChar, "mods_menu.htm");
				}
			}
		}
		return true;
	}

    private void showMainPage(L2PcInstance activeChar, String command)
	{
		int mode = 0;
		String filename;
		try
		{
			mode = Integer.parseInt(command.substring(11));
		}
		catch (Exception e) {  }

		switch (mode)
		{
		case 1:
			filename="main";
			break;
		case 2:
			filename="game";
			break;
		case 3:
			filename="effects";
			break;
		case 4:
			filename="server";
			break;
		case 5:
			filename="mods";
			break;
		default:
			if (Config.GM_ADMIN_MENU_STYLE.equals("modern"))
				filename="main";
			else
				filename="classic";
		break;
		}
		AdminHelpPage.showHelpPage(activeChar, filename+"_menu.htm");
	}
}
