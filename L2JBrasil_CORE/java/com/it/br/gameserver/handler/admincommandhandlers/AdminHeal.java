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
 * [URL]http://www.gnu.org/copyleft/gpl.html[/URL]
 */
package com.it.br.gameserver.handler.admincommandhandlers;

import com.it.br.Config;
import com.it.br.gameserver.handler.IAdminCommandHandler;
import com.it.br.gameserver.model.GMAudit;
import com.it.br.gameserver.model.L2Character;
import com.it.br.gameserver.model.L2Object;
import com.it.br.gameserver.model.L2World;
import com.it.br.gameserver.model.actor.instance.L2PcInstance;
import com.it.br.gameserver.network.SystemMessageId;
import com.it.br.gameserver.network.serverpackets.SystemMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

/**
 * This class handles following admin commands:
 * - heal = restores HP/MP/CP on target, name or radius
 *
 * @version $Revision: 3.0.3 $ $Date: 2017/11/09 $
 */
public class AdminHeal implements IAdminCommandHandler
{
	private static Logger _log = LoggerFactory.getLogger(AdminRes.class);
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

    public AdminHeal()
    {
        admin.put("admin_heal", Config.admin_heal);
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

		GMAudit.auditGMAction(activeChar.getName(), command, (activeChar.getTarget() != null?activeChar.getTarget().getName():"no-target"), "");

		if (command.equals("admin_heal")) handleHeal(activeChar);
		else if (command.startsWith("admin_heal"))
		{
			try
			{
				String healTarget = command.substring(11);
				handleHeal(activeChar, healTarget);
			}
			catch (StringIndexOutOfBoundsException e)
			{
				if (Config.DEVELOPER)
					System.out.println("Heal error: "+e);
				SystemMessage sm = new SystemMessage(SystemMessageId.S1_S2);
				sm.addString("Incorrect target/radius specified.");
				activeChar.sendPacket(sm);
			}
		}
		return true;
	}

	private void handleHeal(L2PcInstance activeChar)
	{
		handleHeal(activeChar, null);
	}

	private void handleHeal(L2PcInstance activeChar, String player) {

		L2Object obj = activeChar.getTarget();
		if (player != null)
		{
			L2PcInstance plyr = L2World.getInstance().getPlayer(player);

			if (plyr != null)
				obj = plyr;
			else
			{
				try
				{
					int radius  = Integer.parseInt(player);
					for (L2Object object : activeChar.getKnownList().getKnownObjects().values())
					{
						if (object instanceof L2Character)
						{
							L2Character character = (L2Character) object;
							character.setCurrentHpMp(character.getMaxHp(), character.getMaxMp());
							if ( object instanceof L2PcInstance ) character.setCurrentCp(character.getMaxCp());
						}
					}
					activeChar.sendMessage("Healed within " + radius + " unit radius.");
					return;
				}
				catch (NumberFormatException nbe) {}
			}
		}
		if (obj == null)
			obj = activeChar;
		if ((obj != null) && (obj instanceof L2Character))
		{
			L2Character target = (L2Character)obj;
			target.setCurrentHpMp(target.getMaxHp(), target.getMaxMp());
			if ( target instanceof L2PcInstance )
				target.setCurrentCp(target.getMaxCp());
			if (Config.DEBUG)
				_log.debug("GM: "+activeChar.getName()+"("+activeChar.getObjectId()+") healed character "+target.getName());
		}
		else
			activeChar.sendPacket(new SystemMessage(SystemMessageId.INCORRECT_TARGET));
	}
}
