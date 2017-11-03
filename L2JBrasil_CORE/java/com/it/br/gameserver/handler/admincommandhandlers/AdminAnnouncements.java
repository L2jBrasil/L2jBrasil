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

/**
 * This class handles following admin commands:
 * - announce text = announces text to all players
 * - list_announcements = show menu
 * - reload_announcements = reloads announcements from txt file
 * - announce_announcements = announce all stored announcements to all players
 * - add_announcement text = adds text to startup announcements
 * - del_announcement id = deletes announcement with respective id
 *
 * @version $Revision: 1.4.4.5 $ $Date: 2005/04/11 10:06:06 $
 */
public class AdminAnnouncements implements IAdminCommandHandler {

	private static final String[] ADMIN_COMMANDS = {
			"admin_list_announcements",
			"admin_reload_announcements",
			"admin_announce_announcements",
			"admin_add_announcement",
			"admin_del_announcement",
			"admin_announce",
			"admin_announce_menu"
			};
	private static final int REQUIRED_LEVEL = Config.GM_ANNOUNCE;


	public boolean useAdminCommand(String command, L2PcInstance activeChar) {
        if (!Config.ALT_PRIVILEGES_ADMIN)
            if (!(checkLevel(activeChar.getAccessLevel()) && activeChar.isGM())) return false;

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
			 Announcements sys = new Announcements(); 
             sys.handleAnnounce(command, 20); 
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
			//FIXME the player can send only 16 chars (if you try to send more it sends null), remove this function or not?
			if (!command.equals("admin_add_announcement"))
			{
			 try{
				String val = command.substring(23);
				Announcements.getInstance().addAnnouncement(val);
				Announcements.getInstance().listAnnouncements(activeChar);
			 } catch(StringIndexOutOfBoundsException e){}//ignore errors
			}
		}
		else if (command.startsWith("admin_del_announcement"))
		{
            try
            {
    			int val = new Integer(command.substring(23)).intValue();
    			Announcements.getInstance().delAnnouncement(val);
    			Announcements.getInstance().listAnnouncements(activeChar);
            }
            catch (StringIndexOutOfBoundsException e)
            { }
		}

		// Command is admin announce
		else if (command.startsWith("admin_announce"))
		{
			// Call method from another class 
            Announcements sys = new Announcements(); 
            sys.handleAnnounce(command, 15); 
		}

		return true;
	}


	public String[] getAdminCommandList() {
		return ADMIN_COMMANDS;
	}

	private boolean checkLevel(int level) {
		return (level >= REQUIRED_LEVEL);
	}

}
