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
import com.it.br.gameserver.handler.AutoAnnouncementHandler;
import com.it.br.gameserver.handler.IAdminCommandHandler;
import com.it.br.gameserver.model.actor.instance.L2PcInstance;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

/**
 * This class handles following admin commands: - announce text = announces text to all players - list_announcements = show menu - reload_announcements = reloads announcements from txt file - announce_announcements = announce all stored announcements to all players - add_announcement text = adds text to startup announcements - del_announcement id = deletes announcement with respective id
 *
 * @version $Revision: 3.0.3 $ $Date: 2017/11/09 $
 */
public class AdminAutoAnnouncements extends AdminAnnouncements implements IAdminCommandHandler
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

    public AdminAutoAnnouncements()
    {
        admin.put("admin_list_autoannouncements", Config.admin_list_autoannouncements);
        admin.put("admin_add_autoannouncement", Config.admin_add_autoannouncement);
        admin.put("admin_del_autoannouncement", Config.admin_del_autoannouncement);
        admin.put("admin_autoannounce", Config.admin_autoannounce);
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

        if (command.equals("admin_list_autoannouncements"))
		{
			AutoAnnouncementHandler.getInstance().listAutoAnnouncements(activeChar);
		}
		else if (command.startsWith("admin_add_autoannouncement"))
		{
			if (!command.equals("admin_add_autoannouncement"))
			{
				try
				{
					int delay = Integer.parseInt(st.nextToken().trim());
					String autoAnnounce = st.nextToken();
					if (delay > 30)
					{
						while (st.hasMoreTokens())
						{
							autoAnnounce = autoAnnounce + " " + st.nextToken();
						}
						;
						AutoAnnouncementHandler.getInstance().registerAnnouncment(autoAnnounce, delay);
						AutoAnnouncementHandler.getInstance().listAutoAnnouncements(activeChar);
					}
				}
				catch (StringIndexOutOfBoundsException e) { }// ignore errors
			}
		}
		else if (command.startsWith("admin_del_autoannouncement"))
		{
			try
			{
				int val = Integer.parseInt(command.substring(27));
				AutoAnnouncementHandler.getInstance().removeAnnouncement(val);
				AutoAnnouncementHandler.getInstance().listAutoAnnouncements(activeChar);
			}
			catch (StringIndexOutOfBoundsException e) { }
		}
		else if (command.startsWith("admin_autoannounce"))
		{
			AutoAnnouncementHandler.getInstance().listAutoAnnouncements(activeChar);
		}
		return true;
	}
}
