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
import com.it.br.gameserver.GmListTable;
import com.it.br.gameserver.handler.IAdminCommandHandler;
import com.it.br.gameserver.model.actor.instance.L2PcInstance;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.logging.Logger;

/**
 * This class handles following admin commands:
 * - gm = turns gm mode on/off
 *
 * @version $Revision: 3.0.3 $ $Date: 2017/11/09 $
 */
public class AdminGm implements IAdminCommandHandler
{
    private static Logger _log = Logger.getLogger(AdminGm.class.getName());
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


    public AdminGm()
    {
        admin.put("admin_gm", Config.admin_gm);
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

		if (command.equals("admin_gm"))
            handleGm(activeChar);

		return true;
	}

	private void handleGm(L2PcInstance activeChar)
    {
		if (activeChar.isGM())
		{
            GmListTable.getInstance().deleteGm(activeChar);
			activeChar.setIsGM(false);

            activeChar.sendMessage("You no longer have GM status.");

			if (Config.DEBUG) _log.fine("GM: "+activeChar.getName()+"("+activeChar.getObjectId()+") turned his GM status off");
		}
        else {
            GmListTable.getInstance().addGm(activeChar, false);
			activeChar.setIsGM(true);

			activeChar.sendMessage("You now have GM status.");

			if (Config.DEBUG) _log.fine("GM: "+activeChar.getName()+"("+activeChar.getObjectId()+") turned his GM status on");
		}
	}
}
