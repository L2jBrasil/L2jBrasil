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
import com.it.br.configuration.settings.EventSettings;
import com.it.br.gameserver.handler.IAdminCommandHandler;
import com.it.br.gameserver.model.GMAudit;
import com.it.br.gameserver.model.L2Object;
import com.it.br.gameserver.model.actor.instance.L2PcInstance;
import com.it.br.gameserver.model.entity.event.TvTEvent;
import com.it.br.gameserver.model.entity.event.TvTEventTeleporter;
import com.it.br.gameserver.model.entity.event.TvTManager;

import static com.it.br.configuration.Configurator.getSettings;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

/**
 * @version $Revision: 3.0.3 $ $Date: 2017/11/09 $
 */
public class AdminTvTEvent implements IAdminCommandHandler
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

    public AdminTvTEvent()
    {
        admin.put("admin_tvt_add", Config.admin_tvt_add);
        admin.put("admin_tvt_remove", Config.admin_tvt_remove);
        admin.put("admin_tvt_advance", Config.admin_tvt_advance);
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

		GMAudit.auditGMAction(activeChar.getName(), command, (activeChar.getTarget() != null ? activeChar.getTarget().getName() : "no-target"), "");
		if (command.equals("admin_tvt_add"))
		{
			L2Object target = activeChar.getTarget();
			if (target == null || !(target instanceof L2PcInstance))
			{
				activeChar.sendMessage("You should select a player!");
				return true;
			}
			add(activeChar, (L2PcInstance)target);
		}
		else if (command.equals("admin_tvt_remove"))
		{
			L2Object target = activeChar.getTarget();
			if (target == null || !(target instanceof L2PcInstance))
			{
				activeChar.sendMessage("You should select a player!");
				return true;
			}
			remove(activeChar, (L2PcInstance)target);
		}
		else if ( command.equals( "admin_tvt_advance" ))
		{
			TvTManager.getInstance().skipDelay();
		}
		return true;
	}

	private void add(L2PcInstance adminInstance, L2PcInstance playerInstance)
	{
		if (TvTEvent.isPlayerParticipant(playerInstance.getObjectId()))
		{
			adminInstance.sendMessage("Player already participated in the event!");
			return;
		}

		if (!TvTEvent.addParticipant(playerInstance))
		{
			adminInstance.sendMessage("Player instance could not be added, it seems to be null!");
			return;
		}

		if (TvTEvent.isStarted())
			// we don't need to check return value of TvTEvent.getParticipantTeamCoordinates() for null, TvTEvent.addParticipant() returned true so target is in event
			new TvTEventTeleporter(playerInstance, TvTEvent.getParticipantTeamCoordinates(playerInstance.getObjectId()), true, false);
	}

	private void remove(L2PcInstance adminInstance, L2PcInstance playerInstance)
	{
		if (!TvTEvent.removeParticipant(playerInstance.getObjectId(), playerInstance))
		{
			adminInstance.sendMessage("Player is not part of the event!");
			return;
		}

		new TvTEventTeleporter(playerInstance, getSettings(EventSettings.class).getTvTEventParticipationNpcCoordinates(), true, true);
	}
}
