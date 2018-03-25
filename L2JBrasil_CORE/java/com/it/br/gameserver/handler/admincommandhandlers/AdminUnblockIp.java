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
import com.it.br.gameserver.model.actor.instance.L2PcInstance;
import com.it.br.gameserver.network.SystemMessageId;
import com.it.br.gameserver.network.serverpackets.SystemMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

/**
 * This class handles following admin commands:
 * <ul>
 * 	<li>admin_unblockip</li>
 * </ul>
 *
 * @version $Revision: 3.0.3 $ $Date: 2017/11/09 $
 */
public class AdminUnblockIp implements IAdminCommandHandler
{
    private static final Logger _log = LoggerFactory.getLogger(AdminTeleport.class);
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

    public AdminUnblockIp()
    {
        admin.put("admin_unblockip", Config.admin_unblockip);
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

        if (command.startsWith("admin_unblockip "))
        {
            try
            {
                String ipAddress = command.substring(16);
                if (unblockIp(ipAddress, activeChar))
                {
                    SystemMessage sm = new SystemMessage(SystemMessageId.S1_S2);
                    sm.addString("Removed IP " + ipAddress + " from blocklist!");
                    activeChar.sendPacket(sm);
                }
            }
            catch (StringIndexOutOfBoundsException e)
            {
                // Send syntax to the user
                SystemMessage sm = new SystemMessage(SystemMessageId.S1_S2);
                sm.addString("Usage mode: //unblockip <ip>");
                activeChar.sendPacket(sm);
            }
        }

        return true;
    }

    private boolean unblockIp(String ipAddress, L2PcInstance activeChar)
    {
    	//LoginServerThread.getInstance().unBlockip(ipAddress);
        // FIXME: Need implement.
        _log.warn("IP removed by STAFF " + activeChar.getName());
        return true;
    }

}
