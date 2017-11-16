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
import com.it.br.gameserver.GmListTable;
import com.it.br.gameserver.handler.IAdminCommandHandler;
import com.it.br.gameserver.model.L2World;
import com.it.br.gameserver.model.actor.instance.L2PcInstance;
import com.it.br.gameserver.network.SystemMessageId;
import com.it.br.gameserver.network.serverpackets.CreatureSay;
import com.it.br.gameserver.network.serverpackets.SystemMessage;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

public class AdminGmChat implements IAdminCommandHandler
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

    public AdminGmChat()
    {
        admin.put("admin_gmchat", Config.admin_gmchat);
        admin.put("admin_snoop", Config.admin_snoop);
        admin.put("admin_gmchat_menu", Config.admin_gmchat_menu);
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

		if (command.startsWith("admin_gmchat"))
			handleGmChat(command, activeChar);
		else if(command.startsWith("admin_snoop"))
			snoop(command, activeChar);
		if (command.startsWith("admin_gmchat_menu"))
			AdminHelpPage.showHelpPage(activeChar, "main_menu.htm");
		return true;
	}

	private void snoop(String command, L2PcInstance activeChar)
	{
		StringTokenizer st = new StringTokenizer(command);
		st.nextToken();

		if(!st.hasMoreTokens())
		{
			activeChar.sendMessage("Usage: //snoop <player_name>");
			return;
		}

		L2PcInstance target = L2World.getInstance().getPlayer(st.nextToken());

		if(command.length() > 12)
		{
			target = L2World.getInstance().getPlayer(command.substring(12));
		}

		if(target == null)
		{
			activeChar.sendPacket(new SystemMessage(SystemMessageId.TARGET_CANT_FOUND));
			return;
		}
		target.addSnooper(activeChar);
		activeChar.addSnooped(target);
	}

	private void handleGmChat(String command, L2PcInstance activeChar)
	{
		try
		{
			int offset=0;
			String text;
			if (command.contains("menu"))
				offset=17;
			else
				offset=13;
			text = command.substring(offset);
			CreatureSay cs = new CreatureSay(0, 9, activeChar.getName(), text);
			GmListTable.broadcastToGMs(cs);
		}
		catch (StringIndexOutOfBoundsException e){}
	}
}