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
import com.it.br.gameserver.ThreadPoolManager;
import com.it.br.gameserver.Universe;
import com.it.br.gameserver.handler.IAdminCommandHandler;
import com.it.br.gameserver.model.L2Character;
import com.it.br.gameserver.model.L2Object;
import com.it.br.gameserver.model.actor.instance.L2PcInstance;
import com.it.br.gameserver.network.serverpackets.MagicSkillUser;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;


/**
 * @version $Revision: 3.0.3 $ $Date: 2017/11/09 $
 */

public class AdminTest implements IAdminCommandHandler
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

    public AdminTest()
    {
        admin.put("admin_test", Config.admin_test);
        admin.put("admin_stats", Config.admin_stats);
        admin.put("admin_skill_test", Config.admin_skill_test);
        admin.put("admin_st", Config.admin_st);
        admin.put("admin_mp", Config.admin_mp);
        admin.put("admin_known", Config.admin_known);
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

        if (command.equals("admin_stats"))
        {
            for (String line : ThreadPoolManager.getInstance().getStats())
            {
                activeChar.sendMessage(line);
            }
        }
        else if (command.startsWith("admin_skill_test") || command.startsWith("admin_st"))
        {
            try
            {
                int id = Integer.parseInt(st.nextToken());
                adminTestSkill(activeChar,id);
            }
            catch(NumberFormatException e)
            {
                activeChar.sendMessage("Command format is //skill_test <ID>");
            }
        }
        else if (command.startsWith("admin_test uni flush"))
        {
            Universe.getInstance().flush();
            activeChar.sendMessage("Universe Map Saved.");
        }
        else if (command.startsWith("admin_test uni"))
        {
            activeChar.sendMessage("Universe Map Size is: "+Universe.getInstance().size());
        }
        else if (command.equals("admin_mp on"))
        {
            //.startPacketMonitor();
            activeChar.sendMessage("command not working");
        }
        else if (command.equals("admin_mp off"))
        {
            //.stopPacketMonitor();
            activeChar.sendMessage("command not working");
        }
        else if (command.equals("admin_mp dump"))
        {
            //.dumpPacketHistory();
            activeChar.sendMessage("command not working");
        }
        else if (command.equals("admin_known on"))
        {
            Config.CHECK_KNOWN = true;
        }
        else if (command.equals("admin_known off"))
        {
            Config.CHECK_KNOWN = false;
        }
        return true;
    }

    /**
     * @param activeChar L2PcInstance
     * @param id Skill Id
     */
    private void adminTestSkill(L2PcInstance activeChar, int id)
    {
        L2Character player;
        L2Object target = activeChar.getTarget();
        if(target == null || !(target instanceof L2Character))
        {
            player = activeChar;
        }
        else
        {
            player = (L2Character)target;
        }
        player.broadcastPacket(new MagicSkillUser(activeChar, player, id, 1, 1, 1));

    }
}
