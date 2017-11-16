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
import com.it.br.gameserver.GeoData;
import com.it.br.gameserver.handler.IAdminCommandHandler;
import com.it.br.gameserver.model.GMAudit;
import com.it.br.gameserver.model.actor.instance.L2PcInstance;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

/**
 *
 * @author  -Nemesiss-
 * @version $Revision: 3.0.3 $ $Date: 2017/11/09 $
 */
public class AdminGeodata implements IAdminCommandHandler
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

    public AdminGeodata()
    {
        admin.put("admin_geo_z", Config.admin_geo_z);
        admin.put("admin_geo_type", Config.admin_geo_type);
        admin.put("admin_geo_nswe", Config.admin_geo_nswe);
        admin.put("admin_geo_los", Config.admin_geo_los);
        admin.put("admin_geo_position", Config.admin_geo_position);
        admin.put("admin_geo_bug", Config.admin_geo_bug);
        admin.put("admin_geo_load", Config.admin_geo_load);
        admin.put("admin_geo_unload", Config.admin_geo_unload);
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

        if (Config.GEODATA < 1)
        {
        	activeChar.sendMessage("Geo Engine is Turned Off!");
        	return true;
        }

        if (command.equals("admin_geo_z"))
        	activeChar.sendMessage("GeoEngine: Geo_Z = "+GeoData.getInstance().getHeight(activeChar.getX(),activeChar.getY(),activeChar.getZ())+ " Loc_Z = "+activeChar.getZ());
        else if (command.equals("admin_geo_type"))
        {
            short type = GeoData.getInstance().getType(activeChar.getX(),activeChar.getY());
            activeChar.sendMessage("GeoEngine: Geo_Type = "+type);
            short height = GeoData.getInstance().getHeight(activeChar.getX(),activeChar.getY(),activeChar.getZ());
            activeChar.sendMessage("GeoEngine: height = "+height);
        }
        else if (command.equals("admin_geo_nswe"))
        {
            String result = "";
            short nswe = GeoData.getInstance().getNSWE(activeChar.getX(),activeChar.getY(),activeChar.getZ());
            if ((nswe & 8) == 0) result += " N";
            if ((nswe & 4) == 0) result += " S";
            if ((nswe & 2) == 0) result += " W";
            if ((nswe & 1) == 0) result += " E";
            activeChar.sendMessage("GeoEngine: Geo_NSWE -> "+nswe+ "->"+result);
        }
        else if (command.equals("admin_geo_los"))
        {
            if (activeChar.getTarget() != null)
            {
                if(GeoData.getInstance().canSeeTargetDebug(activeChar,activeChar.getTarget()))
                    activeChar.sendMessage("GeoEngine: Can See Target");
                else
                	activeChar.sendMessage("GeoEngine: Can't See Target");

            }
            else
                activeChar.sendMessage("None Target!");
        }
        else if(command.equals("admin_geo_position"))
        {
        	activeChar.sendMessage("GeoEngine: Your current position: ");
        	activeChar.sendMessage(".... world coords: x: "+activeChar.getX()+" y: "+activeChar.getY()+" z: "+activeChar.getZ());
        	activeChar.sendMessage(".... geo position: "+GeoData.getInstance().geoPosition(activeChar.getX(), activeChar.getY()));
        }
        else if(command.startsWith("admin_geo_load"))
        {
        	String[] v = command.substring(15).split(" ");
        	if(v.length != 2)
        		activeChar.sendMessage("Usage: //admin_geo_load <regionX> <regionY>");
        	else
        	{
        		try
        		{
        			byte rx = Byte.parseByte(v[0]);
        			byte ry = Byte.parseByte(v[1]);
        			
        			boolean result = GeoData.loadGeodataFile(rx, ry);

        			if(result)
        				activeChar.sendMessage("GeoEngine: File for region ["+rx+","+ry+"] loaded succesfuly");
        			else
        				activeChar.sendMessage("GeoEngine: File for region ["+rx+","+ry+"] couldn't be loaded");
        		}
        		catch(Exception e){activeChar.sendMessage("You have to write numbers of regions <regionX> <regionY>");}
        	}
        }
        else if(command.startsWith("admin_geo_unload"))
        {
        	String[] v = command.substring(17).split(" ");
        	if(v.length != 2)
        		activeChar.sendMessage("Usage: //admin_geo_unload <regionX> <regionY>");
        	else
        	{
        		try
        		{
        			byte rx = Byte.parseByte(v[0]);
        			byte ry = Byte.parseByte(v[1]);

        			GeoData.unloadGeodata(rx, ry);
        			activeChar.sendMessage("GeoEngine: File for region ["+rx+","+ry+"] unloaded.");
        		}
        		catch(Exception e){activeChar.sendMessage("You have to write numbers of regions <regionX> <regionY>");}
        	}
        }
        else if(command.startsWith("admin_geo_bug"))
        {
        	try
			{
				String comment = command.substring(14);
				GeoData.getInstance().addGeoDataBug(activeChar, comment);
			}
			catch (StringIndexOutOfBoundsException e)
			{
				activeChar.sendMessage("Usage: //admin_geo_bug you coments here");
			}
        }
		return true;
	}
}
