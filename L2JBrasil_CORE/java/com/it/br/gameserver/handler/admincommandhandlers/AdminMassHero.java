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

import java.util.logging.Logger;

import com.it.br.gameserver.handler.IAdminCommandHandler;
import com.it.br.gameserver.model.L2World;
import com.it.br.gameserver.model.actor.instance.L2PcInstance;
import com.it.br.gameserver.network.serverpackets.SocialAction;

public class AdminMassHero implements IAdminCommandHandler
{
	protected static final Logger _log = Logger.getLogger(AdminMassHero.class.getName());
	
	@Override
	public String[] getAdminCommandList()
	{
		return ADMIN_COMMANDS;
	}

	@Override
	public boolean useAdminCommand(String command, L2PcInstance activeChar)
	{
		if(activeChar == null)
			return false;

		if(command.startsWith("admin_masshero") || command.startsWith("admin_allhero"))
		{
			for(L2PcInstance player : L2World.getInstance().getAllPlayers())
			{
				if(player != null)
				{
					/* Check to see if the player already is Hero */
					if(!player.isHero()|| !player.isInOlympiadMode())
					{
						player.setFakeHero(true);
						player.sendMessage("Admin is rewarding all online players with Hero Status.");
						player.broadcastPacket(new SocialAction(player.getObjectId(), 16));
						player.broadcastUserInfo();
					}
					player = null;
				}
			}
		}
		return true;
	}

	private static String[] ADMIN_COMMANDS = { "admin_masshero" };
}