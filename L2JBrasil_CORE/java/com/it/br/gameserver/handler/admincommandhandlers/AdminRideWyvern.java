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
import com.it.br.gameserver.network.serverpackets.Ride;
import com.it.br.gameserver.network.serverpackets.SystemMessage;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

/**
 * @version $Revision: 3.0.3 $ $Date: 2017/11/09 $
 */
public class AdminRideWyvern implements IAdminCommandHandler
{
    private int _petRideId;
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

    public AdminRideWyvern()
    {
        admin.put("admin_ride_wyvern", Config.admin_ride_wyvern);
        admin.put("admin_ride_strider", Config.admin_ride_strider);
        admin.put("admin_unride_wyvern", Config.admin_unride_wyvern);
        admin.put("admin_unride_strider", Config.admin_unride_strider);
        admin.put("admin_unride", Config.admin_unride);
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

        if(command.startsWith("admin_ride"))
        {
            if(activeChar.isMounted() || activeChar.getPet() != null){
                SystemMessage sm = new SystemMessage(SystemMessageId.S1_S2);
                sm.addString("Already Have a Pet or Mounted.");
                activeChar.sendPacket(sm);
                return false;
            }
            if (command.startsWith("admin_ride_wyvern")) {
            	_petRideId = 12621;
            }
            else if (command.startsWith("admin_ride_strider")) {
            	_petRideId = 12526;
            }
            else
            {
                SystemMessage sm = new SystemMessage(SystemMessageId.S1_S2);
                sm.addString("Command '"+command+"' not recognized");
                activeChar.sendPacket(sm);
                return false;
            }
            if(!activeChar.disarmWeapons()) return false;
            Ride mount = new Ride(activeChar.getObjectId(), Ride.ACTION_MOUNT, _petRideId);
            activeChar.sendPacket(mount);
            activeChar.broadcastPacket(mount);
            activeChar.setMountType(mount.getMountType());
        }
        else if(command.startsWith("admin_unride"))
        {
        	if (activeChar.setMountType(0))
        	{
        		Ride dismount = new Ride(activeChar.getObjectId(), Ride.ACTION_DISMOUNT,0);
        		activeChar.broadcastPacket(dismount);
        	}
        }
        return true;
    }
}
