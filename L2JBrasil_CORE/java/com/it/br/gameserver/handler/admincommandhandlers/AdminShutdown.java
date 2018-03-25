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
import com.it.br.gameserver.GameTimeController;
import com.it.br.gameserver.Shutdown;
import com.it.br.gameserver.handler.IAdminCommandHandler;
import com.it.br.gameserver.model.L2World;
import com.it.br.gameserver.model.actor.instance.L2PcInstance;
import com.it.br.gameserver.network.serverpackets.NpcHtmlMessage;

import java.text.SimpleDateFormat;
import java.util.*;


/**
 * This class handles following admin commands:
 * - server_shutdown [sec] = shows menu or shuts down server in sec seconds
 *
 * @version $Revision: 3.0.3 $ $Date: 2017/11/09 $
 */
public class AdminShutdown implements IAdminCommandHandler
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

    public AdminShutdown()
    {
        admin.put("admin_server_shutdown", Config.admin_server_shutdown);
        admin.put("admin_server_restart", Config.admin_server_restart);
        admin.put("admin_server_abort", Config.admin_server_abort);
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

		if (command.startsWith("admin_server_shutdown"))
		{
			try
			{
				int val = Integer.parseInt(command.substring(22));
				serverShutdown(activeChar, val, false);
			}
			catch (StringIndexOutOfBoundsException e)
			{
				sendHtmlForm(activeChar);
			}
		} else if (command.startsWith("admin_server_restart"))
		{
			try
			{
				int val = Integer.parseInt(command.substring(21));
				serverShutdown(activeChar, val, true);
			}
			catch (StringIndexOutOfBoundsException e)
			{
				sendHtmlForm(activeChar);
			}
		} else if (command.startsWith("admin_server_abort"))
		{
			serverAbort(activeChar);
		}

		return true;
	}

	private void sendHtmlForm(L2PcInstance activeChar)
        {
		NpcHtmlMessage adminReply = new NpcHtmlMessage(5);
		int t = GameTimeController.getInstance().getGameTime();
		int h = t/60;
		int m = t%60;
		SimpleDateFormat format = new SimpleDateFormat("h:mm a");
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.HOUR_OF_DAY, h);
		cal.set(Calendar.MINUTE, m);
		adminReply.setFile("data/html/admin/shutdown.htm");
		adminReply.replace("%count%",String.valueOf(L2World.getInstance().getAllPlayersCount()));
		adminReply.replace("%used%",String.valueOf(Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()));
		adminReply.replace("%xp%",String.valueOf(Config.RATE_XP));
		adminReply.replace("%sp%",String.valueOf(Config.RATE_SP));
		adminReply.replace("%adena%",String.valueOf(Config.RATE_DROP_ADENA));
		adminReply.replace("%drop%",String.valueOf(Config.RATE_DROP_ITEMS));
		adminReply.replace("%time%",String.valueOf(format.format(cal.getTime())));
		activeChar.sendPacket(adminReply);
	}

	private void serverShutdown(L2PcInstance activeChar, int seconds, boolean restart)
	{
		Shutdown.getInstance().startShutdown(activeChar, seconds, restart);
	}

	private void serverAbort(L2PcInstance activeChar)
	{
		Shutdown.getInstance().abort(activeChar);
	}

}
