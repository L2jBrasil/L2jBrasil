/* This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
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

import com.it.br.gameserver.handler.IAdminCommandHandler;
import com.it.br.gameserver.model.L2World;
import com.it.br.gameserver.model.actor.instance.L2PcInstance;

public class AdminRecallAll implements IAdminCommandHandler
{
	private static final String[] ADMIN_COMMANDS = { "admin_recallall" };

	private void teleportTo(L2PcInstance activeChar, int x, int y, int z)
	{
		activeChar.teleToLocation(x, y, z, false);
	}
	public boolean useAdminCommand(String command, L2PcInstance activeChar)
	{
		if (command.startsWith("admin_recallall"))
		{
			for(L2PcInstance players :L2World.getInstance().getAllPlayers())
			{
				teleportTo(players, activeChar.getX(), activeChar.getY(), activeChar.getZ());
			}
		}
		return false;
	}
	public String[] getAdminCommandList()
	{
	 return ADMIN_COMMANDS;
	}
}