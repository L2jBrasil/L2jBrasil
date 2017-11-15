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
import com.it.br.gameserver.communitybbs.Manager.RegionBBSManager;
import com.it.br.gameserver.handler.IAdminCommandHandler;
import com.it.br.gameserver.model.GMAudit;
import com.it.br.gameserver.model.L2World;
import com.it.br.gameserver.model.actor.instance.L2PcInstance;
import com.it.br.gameserver.network.serverpackets.LeaveWorld;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

/**
 * @version $Revision: 3.0.3 $ $Date: 2017/11/09 $
 */
public class AdminKick implements IAdminCommandHandler
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

    public AdminKick()
    {
        admin.put("admin_kick", Config.admin_kick);
        admin.put("admin_kick_non_gm", Config.admin_kick_non_gm);
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

		String target = (activeChar.getTarget() != null?activeChar.getTarget().getName():"no-target");
        GMAudit.auditGMAction(activeChar.getName(), command, target, "");

        if (command.startsWith("admin_kick"))
        {
            if (st.countTokens() > 1)
            {
                st.nextToken();
                String player = st.nextToken();
                L2PcInstance plyr = L2World.getInstance().getPlayer(player);
                if (plyr != null)
                {
                    plyr.logout();
    				activeChar.sendMessage("You kicked " + plyr.getName() + " from the game.");
    				RegionBBSManager.getInstance().changeCommunityBoard();
                }
            }
        }
        if (command.startsWith("admin_kick_non_gm"))
        {
        	int counter = 0;
        	for (L2PcInstance player : L2World.getInstance().getAllPlayers())
            {
        		if(!player.isGM())
        		{
        			counter++;
        			player.sendPacket(new LeaveWorld());
        			player.logout();
        			RegionBBSManager.getInstance().changeCommunityBoard();
        		}
            }
        	activeChar.sendMessage("Kicked "+counter+" players");
        }
        return true;
    }
}
