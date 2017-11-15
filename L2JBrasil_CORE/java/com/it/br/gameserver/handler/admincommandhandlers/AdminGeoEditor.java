/* This program is free software; you can redistribute it and/or modify
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
import com.it.br.gameserver.geoeditorcon.GeoEditorListener;
import com.it.br.gameserver.handler.IAdminCommandHandler;
import com.it.br.gameserver.model.GMAudit;
import com.it.br.gameserver.model.actor.instance.L2PcInstance;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

/**
 * @author  Luno, Dezmond
 * @version $Revision: 3.0.3 $ $Date: 2017/11/09 $
 */
public class AdminGeoEditor implements IAdminCommandHandler
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

    public AdminGeoEditor()
    {
        admin.put("admin_ge_status", Config.admin_ge_status);
        admin.put("admin_ge_mode", Config.admin_ge_mode);
        admin.put("admin_ge_join", Config.admin_ge_join);
        admin.put("admin_ge_leave", Config.admin_ge_leave);
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

		String target = (activeChar.getTarget() != null) ? activeChar.getTarget().getName() : "no-target";
        GMAudit.auditGMAction(activeChar.getName(), command, target, "");

        if (!Config.ACCEPT_GEOEDITOR_CONN)
        {
        	activeChar.sendMessage("Server do not accepts geoeditor connections now.");
        	return true;
        }
        if(command.startsWith("admin_ge_status"))
        {
        	activeChar.sendMessage(GeoEditorListener.getInstance().getStatus());
        }
        else if(command.startsWith("admin_ge_mode"))
        {
        	if (GeoEditorListener.getInstance().getThread() == null)
        	{
        		activeChar.sendMessage("Geoeditor not connected.");
        		return true;
        	}
			try
			{
				String val = command.substring("admin_ge_mode".length());
				StringTokenizer st1 = new StringTokenizer(val);

				if (st1.countTokens() < 1)
	        	{
	        		activeChar.sendMessage("Usage: //ge_mode X");
	        		activeChar.sendMessage("Mode 0: Don't send coordinates to geoeditor.");
	        		activeChar.sendMessage("Mode 1: Send coordinates at ValidatePosition from clients.");
	        		activeChar.sendMessage("Mode 2: Send coordinates each second.");
	        		return true;
	        	}
				int m;
				m = Integer.parseInt(st1.nextToken());
				GeoEditorListener.getInstance().getThread().setMode(m);
				activeChar.sendMessage("Geoeditor connection mode set to "+m+".");
			} catch (Exception e)
			{
        		activeChar.sendMessage("Usage: //ge_mode X");
        		activeChar.sendMessage("Mode 0: Don't send coordinates to geoeditor.");
        		activeChar.sendMessage("Mode 1: Send coordinates at ValidatePosition from clients.");
        		activeChar.sendMessage("Mode 2: Send coordinates each second.");
				e.printStackTrace();
			}
    		return true;
        }
        else if(command.equals("admin_ge_join"))
        {
        	if (GeoEditorListener.getInstance().getThread() == null)
        	{
        		activeChar.sendMessage("Geoeditor not connected.");
        		return true;
        	}
       		GeoEditorListener.getInstance().getThread().addGM(activeChar);
    		activeChar.sendMessage("You added to list for geoeditor.");
        }
        else if(command.equals("admin_ge_leave"))
        {
        	if (GeoEditorListener.getInstance().getThread() == null)
        	{
        		activeChar.sendMessage("Geoeditor not connected.");
        		return true;
        	}
       		GeoEditorListener.getInstance().getThread().removeGM(activeChar);
    		activeChar.sendMessage("You removed from list for geoeditor.");
        }
        return true;
	}
}
