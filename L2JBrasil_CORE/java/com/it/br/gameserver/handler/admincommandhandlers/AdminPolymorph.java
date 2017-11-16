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
import com.it.br.gameserver.model.L2Character;
import com.it.br.gameserver.model.L2Object;
import com.it.br.gameserver.model.actor.instance.L2PcInstance;
import com.it.br.gameserver.network.SystemMessageId;
import com.it.br.gameserver.network.serverpackets.MagicSkillUser;
import com.it.br.gameserver.network.serverpackets.SetupGauge;
import com.it.br.gameserver.network.serverpackets.SystemMessage;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

/**
 * This class handles following admin commands: polymorph
 *
 * @version $Revision: 3.0.3 $ $Date: 2017/11/09 $
 */
public class AdminPolymorph implements IAdminCommandHandler
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

    public AdminPolymorph()
    {
        admin.put("admin_polymorph", Config.admin_polymorph);
        admin.put("admin_unpolymorph", Config.admin_unpolymorph);
        admin.put("admin_polymorph_menu", Config.admin_polymorph_menu);
        admin.put("admin_unpolymorph_menu", Config.admin_unpolymorph_menu);
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

		if (command.startsWith("admin_polymorph"))
		{
			L2Object target = activeChar.getTarget();
			try
			{
				String p1 = st.nextToken();
				if (st.hasMoreTokens())
				{
					String p2 = st.nextToken();
					doPolymorph(activeChar, target, p2, p1);
				}
				else
					doPolymorph(activeChar, target, p1, "npc");
			}
			catch(Exception e)
			{
				activeChar.sendMessage("Usage: //polymorph [type] <id>");
			}
		}
		else if (command.equals("admin_unpolymorph"))
		{
			doUnpoly(activeChar,activeChar.getTarget());
		}
		if (command.contains("menu"))
			showMainPage(activeChar);
		return true;
	}

	/**
	 * @param activeChar
	 * @param obj
	 * @param id
	 * @param type
	 */
	private void doPolymorph(L2PcInstance activeChar, L2Object obj, String id, String type)
	{
		if (obj != null)
		{
			obj.getPoly().setPolyInfo(type, id);
			//animation
			if(obj instanceof L2Character)
			{
				L2Character Char = (L2Character) obj;
				MagicSkillUser msk = new MagicSkillUser(Char, 1008, 1, 4000, 0);
				Char.broadcastPacket(msk);
				SetupGauge sg = new SetupGauge(0, 4000);
				Char.sendPacket(sg);
			}
			//end of animation
			obj.decayMe();
			obj.spawnMe(obj.getX(),obj.getY(),obj.getZ());
			activeChar.sendMessage("Polymorph succeed");
		}
		else
			activeChar.sendPacket(new SystemMessage(SystemMessageId.INCORRECT_TARGET));
	}

	/**
	 * @param activeChar player
	 * @param target player
	 */
	private void doUnpoly(L2PcInstance activeChar, L2Object target)
	{
		if (target !=null)
		{
			target.getPoly().setPolyInfo(null, "1");
			target.decayMe();
			target.spawnMe(target.getX(),target.getY(),target.getZ());
			activeChar.sendMessage("Unpolymorph succeed");
		}
		else
			activeChar.sendPacket(new SystemMessage(SystemMessageId.INCORRECT_TARGET));
	}

	private void showMainPage(L2PcInstance activeChar)
	{
		AdminHelpPage.showHelpPage(activeChar, "effects_menu.htm");
	}
}
