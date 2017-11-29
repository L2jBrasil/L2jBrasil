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

import static com.it.br.configuration.Configurator.getSettings;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.logging.Logger;

import com.it.br.Config;
import com.it.br.configuration.settings.L2JModsSettings;
import com.it.br.gameserver.handler.IAdminCommandHandler;
import com.it.br.gameserver.model.GMAudit;
import com.it.br.gameserver.model.L2Character;
import com.it.br.gameserver.model.L2Object;
import com.it.br.gameserver.model.L2World;
import com.it.br.gameserver.model.actor.instance.L2ControllableMobInstance;
import com.it.br.gameserver.model.actor.instance.L2PcInstance;
import com.it.br.gameserver.network.SystemMessageId;
import com.it.br.gameserver.network.serverpackets.SystemMessage;

/**
 * This class handles following admin commands:
 * - kill = kills target L2Character
 * - kill_monster = kills target non-player
 *
 * - kill <radius> = If radius is specified, then ALL players only in that radius will be killed.
 * - kill_monster <radius> = If radius is specified, then ALL non-players only in that radius will be killed.
 *
 * @version $Revision: 3.0.3 $ $Date: 2017/11/09 $
 */
public class AdminKill implements IAdminCommandHandler
{
    private static Logger _log = Logger.getLogger(AdminKill.class.getName());
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


    public AdminKill()
    {
        admin.put("admin_kill", Config.admin_kill);
        admin.put("admin_kill_monster", Config.admin_kill_monster);
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

		if (command.startsWith("admin_kill"))
		{
			if (st.hasMoreTokens())
			{
				String firstParam = st.nextToken();
				L2PcInstance plyr = L2World.getInstance().getPlayer(firstParam);
				if (plyr != null)
				{
					if (st.hasMoreTokens())
					{
						try
						{
							int radius  = Integer.parseInt(st.nextToken());
							for (L2Character knownChar : plyr.getKnownList().getKnownCharactersInRadius(radius))
							{
								if (knownChar == null || knownChar instanceof L2ControllableMobInstance || knownChar.equals(activeChar)) continue;

								kill(activeChar, knownChar);
							}

							activeChar.sendMessage("Killed all characters within a " + radius + " unit radius.");
							return true;
						}
						catch (NumberFormatException e) {
							activeChar.sendMessage("Invalid radius.");
							return false;
						}
					} else
					{
						kill(activeChar, plyr);
					}
				}
				else
				{
					try
					{
						int radius  = Integer.parseInt(firstParam);

						for (L2Character knownChar : activeChar.getKnownList().getKnownCharactersInRadius(radius))
						{
							if (knownChar == null || knownChar instanceof L2ControllableMobInstance || knownChar.equals(activeChar))
								continue;
							kill(activeChar, knownChar);
						}

						activeChar.sendMessage("Killed all characters within a " + radius + " unit radius.");
						return true;
					}
					catch (NumberFormatException e) {
						activeChar.sendMessage("Usage: //kill <player_name | radius>");
						return false;
					}
				}
			}
			else
			{
				L2Object obj = activeChar.getTarget();
				if (obj == null || obj instanceof L2ControllableMobInstance || !(obj instanceof L2Character))
					activeChar.sendPacket(new SystemMessage(SystemMessageId.INCORRECT_TARGET));
				else
					kill(activeChar, (L2Character)obj);
			}
		}
		return true;
	}

	private void kill(L2PcInstance activeChar, L2Character target) {
		
		L2JModsSettings l2jModsSettings = getSettings(L2JModsSettings.class);
		
		if (target instanceof L2PcInstance)
		{
			if(!((L2PcInstance)target).isGM())
				target.stopAllEffects(); // e.g. invincibility effect
			target.reduceCurrentHp(target.getMaxHp() + target.getMaxCp() + 1, activeChar);
		}
		else if (l2jModsSettings.isChampionEnabled() && target.isChampion())
			target.reduceCurrentHp(target.getMaxHp() * l2jModsSettings.getChampionHp() + 1, activeChar);
		else
        { 
        if(target.isInvul()) target.setIsInvul(false); 
			target.reduceCurrentHp(target.getMaxHp() + 1, activeChar);
        }
		if (Config.DEBUG)
			_log.fine("GM: "+activeChar.getName()+"("+activeChar.getObjectId()+")"+
					" killed character "+target.getObjectId());
	}
}
