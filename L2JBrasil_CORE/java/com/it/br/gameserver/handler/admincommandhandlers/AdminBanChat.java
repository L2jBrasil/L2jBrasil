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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.StringTokenizer;

import com.it.br.Config;
import com.it.br.L2DatabaseFactory;
import com.it.br.gameserver.Announcements;
import com.it.br.gameserver.handler.IAdminCommandHandler;
import com.it.br.gameserver.model.GMAudit;
import com.it.br.gameserver.model.L2World;
import com.it.br.gameserver.model.actor.instance.L2PcInstance;

public class AdminBanChat implements IAdminCommandHandler 
{
	private static final String[] ADMIN_COMMANDS ={ "admin_banchat", "admin_unbanchat"};

	private static final int REQUIRED_LEVEL = Config.GM_BAN_CHAT;

	public boolean useAdminCommand(String command, L2PcInstance activeChar)
	{
		if (!Config.ALT_PRIVILEGES_ADMIN)
		{
			if (!(checkLevel(activeChar.getAccessLevel())))
			{
				System.out.println("Not required level.");
				return false;
			}
		}

		StringTokenizer st = new StringTokenizer(command);
		st.nextToken();
		String player = "";
		int duration = -1;
		L2PcInstance targetPlayer = null;

		if (st.hasMoreTokens())
		{
			player = st.nextToken();
			targetPlayer = L2World.getInstance().getPlayer(player);
			
			if (st.hasMoreTokens())
			{
				try
				{
					duration = Integer.parseInt(st.nextToken());
				}
				catch (NumberFormatException nfe)
				{
					activeChar.sendMessage("Invalid number format used: " + nfe);
					return false;
				}
			}
		}
		else
		{
			if (activeChar.getTarget() != null && activeChar.getTarget() instanceof L2PcInstance)
			{
				targetPlayer = (L2PcInstance)activeChar.getTarget();
			}
		}
		
		if (targetPlayer != null && targetPlayer.equals(activeChar))
		{
			activeChar.sendMessage("CANNOT_USE_ON_YOURSELF");
			return false;
		}
		else if (command.startsWith("admin_banchat"))
		{
			if (targetPlayer == null && player.equals(""))
			{
				activeChar.sendMessage("Usage: //banchat <char_name> [penalty_minutes]");
				return false;
			}
			if (targetPlayer != null)
			{
				if (targetPlayer.getPunishLevel().value() > 0)
				{
					activeChar.sendMessage(targetPlayer.getName()+" is already jailed or banned.");
					return false;
				}
				String banLengthStr = "";
				
				targetPlayer.setPunishLevel(L2PcInstance.PunishLevel.CHAT, duration);
				if (duration > 0)
					banLengthStr = " for " + duration + " minutes";
				if (Config.ANNOUNCE_BAN_CHAT)
					Announcements.getInstance().announceToAll(targetPlayer.getName() + " is now chat banned" + banLengthStr + ".");
				else
					activeChar.sendMessage(targetPlayer.getName() + " is now chat banned" + banLengthStr + ".");
					auditAction(command, activeChar, targetPlayer.getName());
			}
			else
			{
				banChatOfflinePlayer(activeChar, player, duration, true);
				auditAction(command, activeChar, player);
			}
		}
		else if (command.startsWith("admin_unbanchat"))
		{
			if (targetPlayer == null && player.equals(""))
			{
				activeChar.sendMessage("Usage: //unban_chat <char_name>");
				return false;
			}
			if (targetPlayer != null)
			{
				if (targetPlayer.isChatBanned())
				{
					targetPlayer.setPunishLevel(L2PcInstance.PunishLevel.NONE, 0);
					if (Config.ANNOUNCE_UNBAN_CHAT)
						Announcements.getInstance().announceToAll(targetPlayer.getName() + "'s chat ban has been lifted.");
					else
						activeChar.sendMessage(targetPlayer.getName() + "'s chat ban has now been lifted.");
					auditAction(command, activeChar, targetPlayer.getName());
				}
				else
				{
					activeChar.sendMessage(targetPlayer.getName() + " is not currently chat banned.");
				}
			}
			else
			{
				banChatOfflinePlayer(activeChar, player, 0, false);
				auditAction(command, activeChar, player);

	         }
	     }
		return true;
	}
	
	private void auditAction(String fullCommand, L2PcInstance activeChar, String target)
	{
		if (!Config.GMAUDIT)
			return;
		
		String[] command = fullCommand.split(" ");
		
		GMAudit.auditGMAction(activeChar.getName()+" ["+activeChar.getObjectId()+"]", command[0], (target.equals("") ? "no-target" : target), (command.length > 2 ? command[2] : ""));
	}
	
	private boolean checkLevel(int level) 
    {
	return (level >= REQUIRED_LEVEL);
    }
	
	private void banChatOfflinePlayer(L2PcInstance activeChar, String name, int delay, boolean ban)
	{
		Connection con = null;
		int level = 0;
		long value = 0;
		if(ban)
		{
			level = L2PcInstance.PunishLevel.CHAT.value();
			value = (delay > 0 ? delay * 60000L : 60000);
		}
		else
		{
			level = L2PcInstance.PunishLevel.NONE.value();
			value = 0;
		}
		
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			
			PreparedStatement statement = con.prepareStatement("UPDATE characters SET punish_level=?, punish_timer=? WHERE char_name=?");
			statement.setInt(1, level);
			statement.setLong(2, value);
			statement.setString(3, name);
			
			statement.execute();
			int count = statement.getUpdateCount();
			statement.close();
			
			if (count == 0)
				activeChar.sendMessage("Character not found!");
			else
				if(ban)
					activeChar.sendMessage("Character " + name + " chat-banned for " + (delay > 0 ? delay + " minutes." : "ever!"));
				else
					activeChar.sendMessage("Character " + name + "'s chat-banned lifted");
		}
		catch (SQLException se)
		{
			activeChar.sendMessage("SQLException while chat-banning player");
			if (Config.DEBUG)
				se.printStackTrace();
		}
		finally
		{
			L2DatabaseFactory.close(con);
		}
	}

	public String[] getAdminCommandList() 
	{
		return ADMIN_COMMANDS;
	}
}