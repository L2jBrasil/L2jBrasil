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
package com.it.br.gameserver.handler;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Logger;

import com.it.br.Config;
import com.it.br.gameserver.handler.admincommandhandlers.*;
import com.it.br.gameserver.model.actor.instance.L2PcInstance;

public class AdminCommandHandler
{
	private static Logger _log = Logger.getLogger(AdminCommandHandler.class.getName());
	private static AdminCommandHandler _instance;
	private Map<String, IAdminCommandHandler> _datatable;

    @SuppressWarnings("unused")
	private static Logger _priviLog = Logger.getLogger("AltPrivilegesAdmin");
    private static Map<String,Integer> _privileges;

	public static AdminCommandHandler getInstance()
	{
		if (_instance == null)
		{
			_instance = new AdminCommandHandler();
		}
		return _instance;
	}

	private AdminCommandHandler()
	{
		_datatable = new HashMap<>();
		registerAdminCommandHandler(new AdminAdmin());
        registerAdminCommandHandler(new AdminAio());
		registerAdminCommandHandler(new AdminAnnouncements());
        registerAdminCommandHandler(new AdminAutoAnnouncements());
        registerAdminCommandHandler(new AdminBan());
		registerAdminCommandHandler(new AdminBanChat());
        registerAdminCommandHandler(new AdminBBS());
        registerAdminCommandHandler(new AdminCache());
 		registerAdminCommandHandler(new AdminInvul());
		registerAdminCommandHandler(new AdminDelete());
		registerAdminCommandHandler(new AdminKill());
		registerAdminCommandHandler(new AdminTarget());
		registerAdminCommandHandler(new AdminShop());
		registerAdminCommandHandler(new AdminDisconnect());
		registerAdminCommandHandler(new AdminCreateItem());
        registerAdminCommandHandler(new AdminHeal());
		registerAdminCommandHandler(new AdminHelpPage());
		registerAdminCommandHandler(new AdminShutdown());
		registerAdminCommandHandler(new AdminSpawn());
		registerAdminCommandHandler(new AdminSkill());
		registerAdminCommandHandler(new AdminExpSp());
        registerAdminCommandHandler(new AdminEventEngine());
		registerAdminCommandHandler(new AdminGmChat());
		registerAdminCommandHandler(new AdminEditChar());
		registerAdminCommandHandler(new AdminGm());
		registerAdminCommandHandler(new AdminTeleport());
		registerAdminCommandHandler(new AdminRepairChar());
        registerAdminCommandHandler(new AdminChangeAccessLevel());
        registerAdminCommandHandler(new AdminPolymorph());
        registerAdminCommandHandler(new AdminKick());
        registerAdminCommandHandler(new AdminMonsterRace());
        registerAdminCommandHandler(new AdminEditNpc());
        registerAdminCommandHandler(new AdminFightCalculator());
        registerAdminCommandHandler(new AdminMenu());
        registerAdminCommandHandler(new AdminSiege());
        registerAdminCommandHandler(new AdminClanFull());
        registerAdminCommandHandler(new AdminPathNode());
        registerAdminCommandHandler(new AdminPetition());
        registerAdminCommandHandler(new AdminPForge());
        registerAdminCommandHandler(new AdminEffects());
        registerAdminCommandHandler(new AdminDoorControl());
        registerAdminCommandHandler(new AdminTest());
        registerAdminCommandHandler(new AdminTvTEvent());
        registerAdminCommandHandler(new AdminEnchant());
        registerAdminCommandHandler(new AdminMobGroup());
        registerAdminCommandHandler(new AdminRes());
        registerAdminCommandHandler(new AdminMammon());
        registerAdminCommandHandler(new AdminMassHero());
        registerAdminCommandHandler(new AdminMassRecall());
        registerAdminCommandHandler(new AdminUnblockIp());
        registerAdminCommandHandler(new AdminPledge());
        registerAdminCommandHandler(new AdminRideWyvern());
        registerAdminCommandHandler(new AdminReload());
        registerAdminCommandHandler(new AdminLogin());
        registerAdminCommandHandler(new AdminLevel());
        registerAdminCommandHandler(new AdminQuest());
        registerAdminCommandHandler(new AdminZone());
        registerAdminCommandHandler(new AdminCursedWeapons());
        registerAdminCommandHandler(new AdminGeodata());
        registerAdminCommandHandler(new AdminGeoEditor());
        registerAdminCommandHandler(new AdminManor());
        registerAdminCommandHandler(new AdminHero());
        registerAdminCommandHandler(new AdminVip());
        registerAdminCommandHandler(new AdminNoble());
        registerAdminCommandHandler(new AdminRecallAll());
        registerAdminCommandHandler(new AdminDebug());
		_log.config("AdminCommandHandler: Loading " + _datatable.size() + " handlers.");
	}

	public void registerAdminCommandHandler(IAdminCommandHandler handler)
	{
		String[] ids = handler.getAdminCommandList();
		for (int i = 0; i < ids.length; i++)
		{
			if (Config.DEBUG) _log.fine("adicionando handler para o comando "+ids[i]);
			_datatable.put(ids[i], handler);
		}
	}

	public IAdminCommandHandler getAdminCommandHandler(String adminCommand)
	{
		String command = adminCommand;
		if (adminCommand.indexOf(" ") != -1)
                {
			command = adminCommand.substring(0, adminCommand.indexOf(" "));
		}
		if (Config.DEBUG)
			_log.fine("Handler obtained: "+command+ " -> "+(_datatable.get(command) != null));
		return _datatable.get(command);
	}

    public int size()
    {
        return _datatable.size();
    }

    public final boolean checkPrivileges(L2PcInstance player, String adminCommand)
    {
        //Only a GM can execute a admin command
        if (!player.isGM())
            return false;

        //Skip special privileges handler?
        if (!Config.ALT_PRIVILEGES_ADMIN || Config.EVERYBODY_HAS_ADMIN_RIGHTS)
            return true;

        if (_privileges == null)
            _privileges = new HashMap<>();

        String command = adminCommand;
        if (adminCommand.indexOf(" ") != -1)
        {
            command = adminCommand.substring(0, adminCommand.indexOf(" "));
        }

        //The command not exists
        if (!_datatable.containsKey(command))
            return false;

        int requireLevel = 0;

        if (!_privileges.containsKey(command))
        {
            //Try to loaded the command config
            boolean isLoaded = false;

            try(InputStream is = new FileInputStream(Config.ADMINCOMMAND_FILE))
            {
                Properties Settings = new Properties();
                Settings.load(is);

                String stringLevel = Settings.getProperty(command);

                if (stringLevel != null)
                {
                    isLoaded = true;
                    requireLevel = Integer.parseInt(stringLevel);
                }
            }
            catch (Exception e) { }

            //Secure level?
            if (!isLoaded)
            {
                if (Config.ALT_PRIVILEGES_SECURE_CHECK)
                {
                    _log.fine("Command executed!!");
                    return false;
                }
                requireLevel = Config.ALT_PRIVILEGES_DEFAULT_LEVEL;
            }
            _privileges.put(command,requireLevel);
        }
        else
        {
            requireLevel = _privileges.get(command);
        }

        if (player.getAccessLevel() < requireLevel)
        {
            _log.warning("<GM>" + player.getName() + ": needs: '" + command +"'");
            return false;
        }
        return true;
    }
}