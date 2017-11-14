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

import java.util.StringTokenizer;

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
 * @version $Revision: 1.3.2.1.2.4 $ $Date: 2007/07/28 10:06:06 $
 */
public class AdminAdmin implements IAdminCommandHandler {

	private static final String[] ADMIN_COMMANDS = {
	        "admin_admin",
            "admin_admin1",
            "admin_admin2",
            "admin_admin3",
            "admin_admin4",
            "admin_admin5",
		    "admin_gmliston",
            "admin_gmlistoff",
            "admin_silence",
            "admin_diet",
            "admin_tradeoff",
            "admin_reload",
            "admin_set",
            "admin_set_menu",
            "admin_set_mod",
		    "admin_saveolymp",
            "admin_manualhero"
	};

	private static final int REQUIRED_LEVEL = Config.GM_MENU;

	public boolean useAdminCommand(String command, L2PcInstance activeChar)
    {

		if (!Config.ALT_PRIVILEGES_ADMIN)
			if (!(checkLevel(activeChar.getAccessLevel()) && activeChar.isGM()))
				return false;

		GMAudit.auditGMAction(activeChar.getName(), command, (activeChar.getTarget() != null?activeChar.getTarget().getName():"no-target"), "");

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
				StringTokenizer st = new StringTokenizer(command);
				st.nextToken();
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
		else if(command.startsWith("admin_reload"))
		{
			StringTokenizer st = new StringTokenizer(command);
			st.nextToken();
			try
			{
				String type = st.nextToken();
				if(type.equals("multisell"))
				{
					L2Multisell.getInstance().reload();
					activeChar.sendMessage("multisell reloaded");
				}
				else if(type.startsWith("teleport"))
				{
					TeleportLocationTable.getInstance().reloadAll();
					activeChar.sendMessage("teleport locations reloaded");
				}
				else if(type.startsWith("skill"))
				{
					SkillTable.getInstance();
					SkillTable.reload();
					activeChar.sendMessage("skills reloaded");
				}
				else if(type.equals("npc"))
				{
					NpcTable.getInstance().reloadAllNpc();
					activeChar.sendMessage("npcs reloaded");
				}
				else if(type.startsWith("htm"))
				{
					HtmCache.getInstance().reload();
					activeChar.sendMessage("Cache[HTML]: " + HtmCache.getInstance().getMemoryUsage()  + " megabytes on " + HtmCache.getInstance().getLoadedFiles() + " html reloaded");
				}
				else if(type.startsWith("item"))
				{
					ItemTable.getInstance().reload();
					activeChar.sendMessage("Item templates reloaded");
				}
				else if (type.startsWith("config")) 
                { 
					AdminHelpPage.showHelpPage(activeChar, "reload_menu1.htm");
                }
				else if (type.startsWith("admin")) 
                { 
					Config.loadGMAcessConfig();
                    activeChar.sendMessage("Admin config settings reloaded"); 
                }
				else if (type.startsWith("custom")) 
                { 
					Config.loadCommandConfig();
					Config.loadBrasilConfig();
					Config.loadL2JModConfig();
                    activeChar.sendMessage("Custom config settings reloaded"); 
                }
				else if (type.startsWith("event")) 
                { 
					Config.loadCHConfig();
					Config.loadSepulchersConfig();
					Config.loadOlympConfig();
					Config.loadSevenSignsConfig();
					Config.loadTvTConfig();
                    activeChar.sendMessage("Event config settings reloaded"); 
                }
				else if (type.startsWith("main")) 
                { 
					Config.loadAltSettingsConfig();
					Config.loadBossConfig();
					Config.loadClanConfig();
					Config.loadClassConfig();
					Config.loadEnchantConfig();
					Config.loadExtensionsConfig();
					Config.loadOptionConfig();
					Config.loadOtherConfig();
					Config.loadPvPConfig();
					Config.loadRatesConfig();
                    activeChar.sendMessage("Main config settings reloaded"); 
                }
				else if (type.startsWith("network")) 
                { 
					Configurator.getInstance().reloadSettings(NetworkSettings.class);
                    activeChar.sendMessage("Network config settings reloaded"); 
                }
				else if (type.startsWith("security")) 
                { 
					Config.loadFloodConfig();
					Config.loadIdFactoryConfig();
					Config.loadScriptingConfig(); 
                    activeChar.sendMessage("Security config settings reloaded"); 
                }
				else if(type.startsWith("instancemanager"))
				{
					Manager.reloadAll();
					activeChar.sendMessage("All instance manager reloaded");
				}
				else if(type.startsWith("npcwalkers"))
				{
					NpcWalkerRoutesTable.getInstance().load();
					activeChar.sendMessage("All NPC walker routes reloaded");
				}
			}
			catch(Exception e)
			{
				activeChar.sendMessage("Usage:  //reload <multisell|skill|npc|htm|config|item|instancemanager>");
			}
		}

		else if(command.startsWith("admin_set"))
		{
			StringTokenizer st = new StringTokenizer(command);
			String[] cmd=st.nextToken().split("_");
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


	public String[] getAdminCommandList()
	{
		return ADMIN_COMMANDS;
	}

	private boolean checkLevel(int level)
	{
		return (level >= REQUIRED_LEVEL);
	}

	private void showMainPage(L2PcInstance activeChar, String command)
	{
		int mode = 0;
		String filename;
		try
		{
			mode = Integer.parseInt(command.substring(11));
		}
		catch (Exception e) {  }// Nao precisa printar.

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
