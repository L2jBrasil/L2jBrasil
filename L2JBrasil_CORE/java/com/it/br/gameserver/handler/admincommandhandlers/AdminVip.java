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
import com.it.br.gameserver.database.L2DatabaseFactory;
import com.it.br.gameserver.handler.IAdminCommandHandler;
import com.it.br.gameserver.model.L2Object;
import com.it.br.gameserver.model.L2World;
import com.it.br.gameserver.model.actor.instance.L2PcInstance;
import com.it.br.gameserver.network.SystemMessageId;
import com.it.br.gameserver.network.serverpackets.EtcStatusUpdate;
import com.it.br.gameserver.network.serverpackets.SystemMessage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Give / Take Status Vip to Player
 * Changes name color and title color if enabled
 * 
 * Uses:
 * setvip [<player_name>] [<time_duration in days>]
 * removevip [<player_name>]
 *
 * If <player_name> is not specified, the current target player is used.
 *
 * @author KhayrusS
 * @version $Revision: 3.0.3 $ $Date: 2017/11/09 $
 *
 */
public class AdminVip implements IAdminCommandHandler
{
    private final static Logger _log = Logger.getLogger(AdminVip.class.getName());
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

    public AdminVip()
    {
        admin.put("admin_setvip", Config.admin_setvip);
        admin.put("admin_removevip", Config.admin_removevip);
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

		if (command.startsWith("admin_setvip"))
		{
			StringTokenizer str = new StringTokenizer(command);
			L2Object target = activeChar.getTarget();

			L2PcInstance player = null;
			SystemMessage sm = new SystemMessage(SystemMessageId.S1_S2);

			if (target != null && target instanceof L2PcInstance)
				player = (L2PcInstance)target;
			else
				player = activeChar;

			try
			{
				str.nextToken();
				String time = str.nextToken();
				if (str.hasMoreTokens())
				{
					String playername = time;
					time = str.nextToken();
					player = L2World.getInstance().getPlayer(playername);
					doVip(activeChar, player, playername, time);
				}
				else
				{
					String playername = player.getName();
					doVip(activeChar, player, playername, time);
				}
				if(!time.equals("0"))
				{
					sm.addString("You are now a Vip , congratulations!");
					player.sendPacket(sm);
				}
			}
			catch(Exception e)
			{
				activeChar.sendMessage("Usage: //setvip <char_name> [time](in days)");
			}

			player.broadcastUserInfo();
			if(player.isVip())
				return true;
		}
		else if(command.startsWith("admin_removevip"))
		{
			StringTokenizer str = new StringTokenizer(command);
			L2Object target = activeChar.getTarget();

			L2PcInstance player = null;

			if (target != null && target instanceof L2PcInstance)
				player = (L2PcInstance)target;
			else
				player = activeChar;

			try
			{
				str.nextToken();
				if (str.hasMoreTokens())
				{
					String playername = str.nextToken();
					player = L2World.getInstance().getPlayer(playername);
					removeVip(activeChar, player, playername);
				}
				else
				{
					String playername = player.getName();
					removeVip(activeChar, player, playername);
				}
			}
			catch(Exception e)
			{
				activeChar.sendMessage("Usage: //removevip <char_name>");
			}
			player.broadcastUserInfo();
			if(!player.isVip())
				return true;
		}
		return false;
	}

	public void doVip(L2PcInstance activeChar, L2PcInstance _player, String _playername, String _time)
	{
		int days = Integer.parseInt(_time);
		if (_player == null)
		{
			activeChar.sendMessage("not found char" + _playername);
			return;
		}

		if(days > 0)
		{
			_player.setVip(true);
			_player.setEndTime("vip", days);

			Connection connection = null;
			try
			{
				connection = L2DatabaseFactory.getInstance().getConnection();

				PreparedStatement statement = connection.prepareStatement("UPDATE characters SET vip=1, vip_end=? WHERE obj_id=?");
				statement.setLong(1, _player.getVipEndTime());
				statement.setInt(2, _player.getObjectId());
				statement.execute();
				statement.close();
				connection.close();

				if(Config.ALLOW_VIP_NCOLOR && activeChar.isVip())
					_player.getAppearance().setNameColor(Config.VIP_NCOLOR);

				if(Config.ALLOW_VIP_TCOLOR && activeChar.isVip())
					_player.getAppearance().setTitleColor(Config.VIP_TCOLOR);

				_player.broadcastUserInfo();
				_player.sendPacket(new EtcStatusUpdate(_player));
				_player.sendSkillList();
				_player.rewardVipSkills();
				GmListTable.broadcastMessageToGMs("GM "+ activeChar.getName()+ " set vip stat for player "+ _playername + " for " + _time + " day(s)");
			}
			catch (Exception e)
			{
				_log.log(Level.WARNING,"could not set vip stats of char:", e);
			}
			finally
			{
				L2DatabaseFactory.close(connection);
			}
		}
		else
		{
			removeVip(activeChar, _player, _playername);
		}
	}

	public void removeVip(L2PcInstance activeChar, L2PcInstance _player, String _playername)
	{
		_player.setVip(false);
		_player.setVipEndTime(0);

		Connection connection = null;
		try
		{
			connection = L2DatabaseFactory.getInstance().getConnection();

			PreparedStatement statement = connection.prepareStatement("UPDATE characters SET vip=0, vip_end=0 WHERE obj_id=?");
			statement.setInt(1, _player.getObjectId());
			statement.execute();
			statement.close();
			connection.close();

			_player.lostVipSkills();
			_player.getAppearance().setNameColor(0xFFFFFF);
			_player.getAppearance().setTitleColor(0xFFFFFF);
			_player.broadcastUserInfo();
			_player.sendPacket(new EtcStatusUpdate(_player));
			_player.sendSkillList();
			GmListTable.broadcastMessageToGMs("GM "+activeChar.getName()+" remove vip stat of player "+ _playername);
		}
		catch (Exception e)
		{
			_log.log(Level.WARNING,"could not remove vip stats of char:", e);
		}
		finally
		{
			L2DatabaseFactory.close(connection);
		}
	}
}