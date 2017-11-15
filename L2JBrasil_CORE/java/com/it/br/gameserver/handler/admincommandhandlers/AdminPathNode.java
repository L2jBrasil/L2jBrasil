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
import com.it.br.gameserver.model.L2World;
import com.it.br.gameserver.model.actor.instance.L2PcInstance;
import com.it.br.gameserver.pathfinding.AbstractNodeLoc;
import com.it.br.gameserver.pathfinding.geonodes.GeoPathFinding;

import java.util.*;

/**
 * @version $Revision: 3.0.3 $ $Date: 2017/11/09 $
 */
public class AdminPathNode implements IAdminCommandHandler
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

    public AdminPathNode()
    {
        admin.put("admin_pn_info", Config.admin_pn_info);
        admin.put("admin_show_path", Config.admin_show_path);
        admin.put("admin_path_debug", Config.admin_path_debug);
        admin.put("admin_show_pn", Config.admin_show_pn);
        admin.put("admin_find_path", Config.admin_find_path);
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

        if(command.equals("admin_pn_info"))
        {
            // FIXME: Need implement or remove
        }
    	else if(command.equals("admin_show_path"))
        {

        }
    	else if(command.equals("admin_path_debug"))
        {

        }
    	else if(command.equals("admin_show_pn"))
        {

        }
    	else if(command.equals("admin_find_path"))
        {
    		if (Config.GEODATA < 2)
    		{
    			activeChar.sendMessage("PathFinding has not been enabled.");
    			return true;
    		}
    		if (activeChar.getTarget() != null)
            {
    			int gx = (activeChar.getX() - L2World.MAP_MIN_X) >> 4;
    			int gy = (activeChar.getY() - L2World.MAP_MIN_Y) >> 4;
    			int gtx = (activeChar.getTarget().getX() - L2World.MAP_MIN_X) >> 4;
    			int gty = (activeChar.getTarget().getY() - L2World.MAP_MIN_Y) >> 4;
    			List<AbstractNodeLoc> path = GeoPathFinding.getInstance().findPath(gx, gy, (short)activeChar.getZ(), gtx, gty, (short)activeChar.getTarget().getZ());
                if (path == null)
                {
                	activeChar.sendMessage("No Route!");
                	return true;
                }
    			for(AbstractNodeLoc a : path)
                {
                	activeChar.sendMessage("x:"+a.getX()+" y:"+a.getY()+" z:"+a.getZ());
                }
            }
            else
                activeChar.sendMessage("No Target!");
        }
        return true;
    }
}
