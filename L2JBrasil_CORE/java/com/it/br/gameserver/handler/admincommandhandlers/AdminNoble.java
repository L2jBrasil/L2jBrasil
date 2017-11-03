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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.it.br.Config;
import com.it.br.L2DatabaseFactory;
import com.it.br.gameserver.GmListTable;
import com.it.br.gameserver.handler.IAdminCommandHandler;
import com.it.br.gameserver.model.L2Object;
import com.it.br.gameserver.model.actor.instance.L2PcInstance;
import com.it.br.gameserver.network.SystemMessageId;
import com.it.br.gameserver.network.serverpackets.SystemMessage;

public class AdminNoble implements IAdminCommandHandler
{
	private static String[] _adminCommands = { "admin_setnoble" };
	private final static Log _log = LogFactory.getLog(AdminNoble.class.getName());
	private static final int REQUIRED_LEVEL = Config.GM_MENU;


	public boolean useAdminCommand(String command, L2PcInstance activeChar)
	{
		if (!Config.ALT_PRIVILEGES_ADMIN)
		{
			if (!(checkLevel(activeChar.getAccessLevel()) && activeChar.isGM()))
			{
				return false;
			}
		}
		if (command.startsWith("admin_setnoble"))
		{
			L2Object target = activeChar.getTarget();
			L2PcInstance player = null;
			SystemMessage sm = new SystemMessage(SystemMessageId.S1_S2);
			if (target instanceof L2PcInstance)
			{
				player = (L2PcInstance)target;
			} else
			{
				player = activeChar;
			}

			if (player.isNoble())
			{
				player.setNoble(false);
				sm.addString("You are no longer a server noble.");
				GmListTable.broadcastMessageToGMs("GM "+activeChar.getName()+" removed noble stat of player"+ target.getName());
				Connection connection = null;
				try
				{
					connection = L2DatabaseFactory.getInstance().getConnection();

					PreparedStatement statement = connection.prepareStatement("SELECT obj_id FROM characters where char_name=?");
					statement.setString(1,target.getName());
					ResultSet rset = statement.executeQuery();
					int objId = 0;
					if (rset.next())
					{
						objId = rset.getInt(1);
					}
					rset.close();
					statement.close();

					if (objId == 0) {connection.close(); return false;}

					statement = connection.prepareStatement("UPDATE characters SET nobless=0 WHERE obj_id=?");
					statement.setInt(1, objId);
					statement.execute();
					statement.close();
					connection.close();
				}
				catch (Exception e)
				{
					_log.warn("could not set noble stats of char:", e);
				}
				finally
				{
					try { connection.close(); } catch (Exception e) {}
				}
			}
			else
			{
				player.setNoble(true);
				sm.addString("You are now a server noble, congratulations!");
				GmListTable.broadcastMessageToGMs("GM "+activeChar.getName()+" has given noble stat for player "+target.getName()+".");
				Connection connection = null;
				try
				{
					connection = L2DatabaseFactory.getInstance().getConnection();

					PreparedStatement statement = connection.prepareStatement("SELECT obj_id FROM characters where char_name=?");
					statement.setString(1,target.getName());
					ResultSet rset = statement.executeQuery();
					int objId = 0;
					if (rset.next())
					{
						objId = rset.getInt(1);
					}
					rset.close();
					statement.close();

					if (objId == 0) {connection.close(); return false;}

					statement = connection.prepareStatement("UPDATE characters SET nobless=1 WHERE obj_id=?");
					statement.setInt(1, objId);
					statement.execute();
					statement.close();
					connection.close();
				}
				catch (Exception e)
				{
					_log.warn("could not set noble stats of char:", e);
				}
				finally
				{
					try { connection.close(); } catch (Exception e) {}
				}

			}
			player.sendPacket(sm);
			player.broadcastUserInfo();
			if(player.isNoble() == true)
			{
			}
		}
		return false;
	}

public String[] getAdminCommandList() {
		return _adminCommands;
	}
	private boolean checkLevel(int level)
	{
		return (level >= REQUIRED_LEVEL);
	}
}