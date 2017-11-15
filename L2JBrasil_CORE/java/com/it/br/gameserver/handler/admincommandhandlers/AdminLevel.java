/*
 * $Header: AdminTest.java, 25/07/2005 17:15:21 luisantonioa Exp $
 *
 * $Author: luisantonioa $
 * $Date: 25/07/2005 17:15:21 $
 * $Revision: 1 $
 * $Log: AdminTest.java,v $
 * Revision 1  25/07/2005 17:15:21  luisantonioa
 * Added copyright notice
 *
 *
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
import com.it.br.gameserver.model.actor.instance.L2PlayableInstance;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

/**
 * @version $Revision: 3.0.3 $ $Date: 2017/11/09 $
 */
public class AdminLevel implements IAdminCommandHandler
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

    public AdminLevel()
    {
        admin.put("admin_remlevel", Config.admin_remlevel);
        admin.put("admin_addlevel", Config.admin_addlevel);
        admin.put("admin_setlevel", Config.admin_setlevel);
    }

    private static final String[][] COMMANDS_HELP = {
    	{"admin_remlevel",                                 // remove level amount from your target
    		
    		"Remove amount of levels from your target (player or pet).",
    		"Usage: addlevel <num>",
    		"Options:",
    		"num - amount of levels to add/remove",
     	},
    	{"admin_addlevel",                                 // add a level amount to your target
    		
    		"Add a level amount to your target (player or pet).",
    		"Usage: addlevel <num>",
    		"Options:",
    		"num - amount of levels to add/remove",
     	},
    	{"admin_setlevel",                                 // set level of your target
    		
    		"Set level of your target (player or pet).",
    		"Usage: setlevel <num>",
    		"Options:",
    		"num - level to set",
     	} 	
    };

    public Set<String> getAdminCommandList()
    {
        return admin.keySet();
    }

    public boolean useAdminCommand(String command, L2PcInstance activeChar)
    {
        StringTokenizer st = new StringTokenizer(command);
        String commandName = st.nextToken();  // get command

        if(checkPermission(commandName, activeChar)) return false;

        if (activeChar == null) return false;

		if (commandName.equals("admin_addlevel") || commandName.equals("admin_setlevel") || commandName.equals("admin_remlevel"))
        {
			int reslevel = 0;
			int curlevel = 0;
			long xpcur = 0;
			long xpres = 0;
			int lvl = 0;
			
			try
			{
				lvl = Integer.parseInt(st.nextToken());
				
			} catch (Exception e)
			{
			}
			
			L2PlayableInstance target;
			
			if (activeChar.getTarget() instanceof L2PlayableInstance && lvl > 0)
			{
				target = (L2PlayableInstance)activeChar.getTarget();
			
				curlevel = target.getLevel();
			
				reslevel = commandName.equals("admin_addlevel")?(curlevel + lvl):commandName.equals("admin_remlevel")?(curlevel - lvl):lvl;

				try
				{
					xpcur = target.getStat().getExp();
					xpres = target.getStat().getExpForLevel(reslevel);
					
					if (xpcur > xpres)
						target.getStat().removeExp(xpcur - xpres);
					else
						target.getStat().addExp(xpres - xpcur);
					
				}
				catch (Exception e)
				{
					activeChar.sendMessage("Incorrect level amount or number.");
				}
			}
			else
			{
				showAdminCommandHelp(activeChar,commandName);
			}
        }
        return true;
    }

	/**
	 * Show tips about command usage and syntax. 
	 * @param command admin command name
	 */    
	private void showAdminCommandHelp(L2PcInstance activeChar, String command)
	{
		for (String[] element : COMMANDS_HELP) {
			if (command.equals(element[0]))
			{
				for (int k=1; k < element.length; k++)
					activeChar.sendMessage(element[k]);
			}
		}
	}
}
