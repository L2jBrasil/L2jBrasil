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
import com.it.br.gameserver.Announcements;
import com.it.br.gameserver.handler.IAdminCommandHandler;
import com.it.br.gameserver.model.L2World;
import com.it.br.gameserver.model.actor.instance.L2PcInstance;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

/**
 * This class handles following admin commands:
 * - announce text = announces text to all players
 * - list_announcements = show menu
 * - reload_announcements = reloads announcements from txt file
 * - announce_announcements = announce all stored announcements to all players
 * - add_announcement text = adds text to startup announcements
 * - del_announcement id = deletes announcement with respective id
 *
 * @version $Revision: 3.0.3 $ $Date: 2017/11/09 $
 */
public class AdminAnnouncements implements IAdminCommandHandler
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

    public AdminAnnouncements()
    {
        admin.put("admin_list_announcements", Config.admin_list_announcements);
        admin.put("admin_reload_announcements", Config.admin_reload_announcements);
        admin.put("admin_announce_announcements", Config.admin_announce_announcements);
        admin.put("admin_add_announcement", Config.admin_add_announcement);
        admin.put("admin_del_announcement", Config.admin_del_announcement);
        admin.put("admin_announce", Config.admin_announce);
        admin.put("admin_announce_menu", Config.admin_announce_menu);
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

        if (command.equals("admin_list_announcements"))
		{
			Announcements.getInstance().listAnnouncements(activeChar);
		}
		else if (command.equals("admin_reload_announcements"))
		{
			Announcements.getInstance().loadAnnouncements();
			Announcements.getInstance().listAnnouncements(activeChar);
		}
		else if (command.startsWith("admin_announce_menu"))
		{
            Announcements.getInstance().handleAnnounce(command, 20);
            Announcements.getInstance().listAnnouncements(activeChar);
		}
		else if (command.equals("admin_announce_announcements"))
		{
			for (L2PcInstance player : L2World.getInstance().getAllPlayers())
			{
				Announcements.getInstance().showAnnouncements(player);
			}
			Announcements.getInstance().listAnnouncements(activeChar);
		}
		else if (command.startsWith("admin_add_announcement"))
		{
			if (!command.equals("admin_add_announcement"))
			{
                 try
                 {
                     String val = command.substring(23);
                     if (!val.equals(""))
                     {
                         Announcements.getInstance().addAnnouncement(val);
                         Announcements.getInstance().listAnnouncements(activeChar);
                     }
                 } catch(StringIndexOutOfBoundsException e){}//ignore errors
			}
		}
		else if (command.startsWith("admin_del_announcement"))
		{
            try
            {
                int val = Integer.parseInt(command.substring(23));
    			Announcements.getInstance().delAnnouncement(val);
    			Announcements.getInstance().listAnnouncements(activeChar);
            }
            catch (StringIndexOutOfBoundsException e){}//ignore errors
		}
		else if (command.startsWith("admin_announce"))
		{
            Announcements.getInstance().handleAnnounce(command, 15);
		}

		return true;
	}
}
