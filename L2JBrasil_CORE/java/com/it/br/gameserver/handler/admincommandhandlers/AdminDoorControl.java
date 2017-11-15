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
import com.it.br.gameserver.datatables.xml.DoorTable;
import com.it.br.gameserver.handler.IAdminCommandHandler;
import com.it.br.gameserver.instancemanager.CastleManager;
import com.it.br.gameserver.model.GMAudit;
import com.it.br.gameserver.model.L2Object;
import com.it.br.gameserver.model.actor.instance.L2DoorInstance;
import com.it.br.gameserver.model.actor.instance.L2PcInstance;
import com.it.br.gameserver.model.entity.Castle;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

/**
 * This class handles following admin commands:
 * - open1 = open coloseum door 24190001
 * - open2 = open coloseum door 24190002
 * - open3 = open coloseum door 24190003
 * - open4 = open coloseum door 24190004
 * - openall = open all coloseum door
 * - close1 = close coloseum door 24190001
 * - close2 = close coloseum door 24190002
 * - close3 = close coloseum door 24190003
 * - close4 = close coloseum door 24190004
 * - closeall = close all coloseum door
 *
 * - open = open selected door
 * - close = close selected door
 * @version $Revision: 3.0.3 $ $Date: 2017/11/09 $
 */
public class AdminDoorControl implements IAdminCommandHandler
{
    private static DoorTable   _doorTable;
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

    public AdminDoorControl()
    {
        admin.put("admin_open", Config.admin_open);
        admin.put("admin_close", Config.admin_close);
        admin.put("admin_openall", Config.admin_openall);
        admin.put("admin_closeall", Config.admin_closeall);
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

        _doorTable = DoorTable.getInstance();

        try
        {
            if (command.startsWith("admin_open "))
            {
                int doorId = Integer.parseInt(command.substring(11));
                if (_doorTable.getDoor(doorId) != null)
                    _doorTable.getDoor(doorId).openMe();
                else
                {
                    for (Castle castle : CastleManager.getInstance().getCastles())
                        if (castle.getDoor(doorId) != null)
                        {
                            castle.getDoor(doorId).openMe();
                        }
                }
            }
            else if (command.startsWith("admin_close "))
            {
                int doorId = Integer.parseInt(command.substring(12));
                if (_doorTable.getDoor(doorId) != null)
                    _doorTable.getDoor(doorId).closeMe();
                else
                {
                    for (Castle castle : CastleManager.getInstance().getCastles())
                        if (castle.getDoor(doorId) != null)
                        {
                            castle.getDoor(doorId).closeMe();
                        }
                }
            }
            if (command.equals("admin_closeall"))
            {
                for (L2DoorInstance door : _doorTable.getDoors())
                    door.closeMe();
                for (Castle castle : CastleManager.getInstance().getCastles())
                    for (L2DoorInstance door : castle.getDoors())
                        door.closeMe();
            }
            if (command.equals("admin_openall"))
            {
                for (L2DoorInstance door : _doorTable.getDoors())
                    door.openMe();
                for (Castle castle : CastleManager.getInstance().getCastles())
                    for (L2DoorInstance door : castle.getDoors())
                        door.openMe();
            }
            if (command.equals("admin_open"))
            {
                L2Object target = activeChar.getTarget();
                if (target instanceof L2DoorInstance)
                {
                    ((L2DoorInstance) target).openMe();
                }
                else
                {
                    activeChar.sendMessage("Incorrect target.");
                }
            }
            if (command.equals("admin_close"))
            {
                L2Object target = activeChar.getTarget();
                if (target instanceof L2DoorInstance)
                {
                    ((L2DoorInstance)target).closeMe();
                }
                else
                {
                    activeChar.sendMessage("Incorrect target.");
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
		String target = (activeChar.getTarget() != null?activeChar.getTarget().getName():"no-target");
        GMAudit.auditGMAction(activeChar.getName(), command, target, "");
        return true;
	}
}

