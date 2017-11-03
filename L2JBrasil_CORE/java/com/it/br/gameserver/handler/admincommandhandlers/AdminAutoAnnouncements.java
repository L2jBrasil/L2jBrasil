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

import java.util.StringTokenizer;

import com.it.br.Config;
import com.it.br.gameserver.handler.AutoAnnouncementHandler;
import com.it.br.gameserver.handler.IAdminCommandHandler;
import com.it.br.gameserver.model.actor.instance.L2PcInstance;

/**
 * This class handles following admin commands: - announce text = announces text to all players - list_announcements = show menu - reload_announcements = reloads announcements from txt file - announce_announcements = announce all stored announcements to all players - add_announcement text = adds text to startup announcements - del_announcement id = deletes announcement with respective id
 *
 * @version $Revision: 1.4.4.5 $ $Date: 2005/04/11 10:06:06 $
 */
public class AdminAutoAnnouncements implements IAdminCommandHandler
{
	private static String[] ADMIN_COMMANDS = { "admin_list_autoannouncements", "admin_add_autoannouncement", "admin_del_autoannouncement", "admin_autoannounce" };
	private static final int REQUIRED_LEVEL = Config.GM_ANNOUNCE;


	public boolean useAdminCommand(String command, L2PcInstance admin)
	{
		if (!Config.ALT_PRIVILEGES_ADMIN)
                {
			if (!(checkLevel(admin.getAccessLevel()) && admin.isGM()))
                        {
				return false;
			}
		}
		if (command.equals("admin_list_autoannouncements"))
		{
			AutoAnnouncementHandler.getInstance().listAutoAnnouncements(admin);
		}
		else if (command.startsWith("admin_add_autoannouncement"))
		{
			// FIXME the player can send only 16 chars (if you try to send more it sends null), remove this function or not?
			if (!command.equals("admin_add_autoannouncement"))
			{
				try
				{
					StringTokenizer st = new StringTokenizer(command.substring(27));
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
						AutoAnnouncementHandler.getInstance().listAutoAnnouncements(admin);
					}
				}
				catch (StringIndexOutOfBoundsException e)
				{
				}// ignore errors
			}
		}
		else if (command.startsWith("admin_del_autoannouncement"))
		{
			try
			{
				int val = new Integer(command.substring(27)).intValue();
				AutoAnnouncementHandler.getInstance().removeAnnouncement(val);
				AutoAnnouncementHandler.getInstance().listAutoAnnouncements(admin);
			}
			catch (StringIndexOutOfBoundsException e)
			{
			}
		}
		// Command is admin autoannounce
		else if (command.startsWith("admin_autoannounce"))
		{
			// Call method from another class
			AutoAnnouncementHandler.getInstance().listAutoAnnouncements(admin);
		}
		return true;
	}


	public String[] getAdminCommandList()
	{
		return ADMIN_COMMANDS;
	}

	private boolean checkLevel(int level)
	{
		return level >= REQUIRED_LEVEL;
	}
}
