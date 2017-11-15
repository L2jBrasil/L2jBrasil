/*
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.it.br.gameserver.handler.admincommandhandlers;

import com.it.br.Config;
import com.it.br.gameserver.handler.IAdminCommandHandler;
import com.it.br.gameserver.model.L2Character;
import com.it.br.gameserver.model.L2Object;
import com.it.br.gameserver.model.L2World;
import com.it.br.gameserver.model.actor.instance.L2PcInstance;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

/**
 * @version $Revision: 3.0.3 $ $Date: 2017/11/09 $
 */
public class AdminDebug implements IAdminCommandHandler
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

    public AdminDebug()
    {
        admin.put("admin_debug", Config.admin_debug);
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

		if ("admin_debug".equalsIgnoreCase(commandName))
		{
			L2Object target;
			if (st.countTokens() > 1)
			{
				target = L2World.getInstance().getPlayer(st.nextToken());
				if (target == null)
				{
					activeChar.sendMessage("Player not found.");
					return true;
				}
			}
			else
				target = activeChar.getTarget();

			if (target instanceof L2Character)
				setDebug(activeChar, (L2Character)target);
			else
				setDebug(activeChar, activeChar);
		}
		return true;
	}

	private final void setDebug(L2PcInstance activeChar, L2Character target)
	{
		if (target.isDebug())
		{
			target.setDebug(null);
			activeChar.sendMessage("Stop debugging "+target.getName());
		}
		else
		{
			target.setDebug(activeChar);
			activeChar.sendMessage("Start debugging "+target.getName());
		}
	}
}