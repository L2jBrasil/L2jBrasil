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

/**
 *
 * @author  -Nemesiss-
 */
public class AdminGeodata implements IAdminCommandHandler
{
	//private static Logger _log = Logger.getLogger(AdminKill.class.getName());
	private static final String[] ADMIN_COMMANDS =
		{
		"admin_geo_z",
		"admin_geo_type",
		"admin_geo_nswe",
		"admin_geo_los",
		"admin_geo_position",
		"admin_geo_bug",
		"admin_geo_load",
		"admin_geo_unload"
		};
	private static final int REQUIRED_LEVEL = Config.GM_MIN;


	@SuppressWarnings("deprecation")
	public boolean useAdminCommand(String command, L2PcInstance activeChar)
	{
        if (!Config.ALT_PRIVILEGES_ADMIN)
            if (!(checkLevel(activeChar.getAccessLevel()) && activeChar.isGM()))
            	return false;

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


	public String[] getAdminCommandList()
	{
		return ADMIN_COMMANDS;
	}

	private boolean checkLevel(int level)
	{
		return (level >= REQUIRED_LEVEL);
	}

}
