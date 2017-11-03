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

import java.util.logging.Logger;

import com.it.br.Config;
import com.it.br.gameserver.handler.IAdminCommandHandler;
import com.it.br.gameserver.model.actor.instance.L2PcInstance;
import com.it.br.gameserver.network.SystemMessageId;
import com.it.br.gameserver.network.serverpackets.SystemMessage;

/**
 * This class handles following admin commands:
 * <ul>
 * 	<li>admin_unblockip</li>
 * </ul>
 *
 * @version $Revision: 1.3.2.6.2.4 $ $Date: 2005/04/11 10:06:06 $
 */
public class AdminUnblockIp implements IAdminCommandHandler
{

    private static final Logger _log = Logger.getLogger(AdminTeleport.class.getName());

    private static final int REQUIRED_LEVEL = Config.GM_UNBLOCK;
    private static final String[] ADMIN_COMMANDS = {
        "admin_unblockip"
    };

    /* (non-Javadoc)
     * @see com.it.br.gameserver.handler.IAdminCommandHandler#useAdminCommand(java.lang.String, com.it.br.gameserver.model.L2PcInstance)
     */

	public boolean useAdminCommand(String command, L2PcInstance activeChar)
    {
        if (!Config.ALT_PRIVILEGES_ADMIN)
            if (!(checkLevel(activeChar.getAccessLevel()) && activeChar.isGM())) return false;

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


	public String[] getAdminCommandList()
    {
        return ADMIN_COMMANDS;
    }

    private boolean checkLevel(int level)
    {
        return (level >= REQUIRED_LEVEL);
    }

    private boolean unblockIp(String ipAddress, L2PcInstance activeChar)
    {
    	//LoginServerThread.getInstance().unBlockip(ipAddress);
        _log.warning("IP removed by STAFF " + activeChar.getName());
        return true;
    }

}
