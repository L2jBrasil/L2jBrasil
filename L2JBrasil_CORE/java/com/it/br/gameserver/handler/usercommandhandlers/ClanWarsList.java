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

package com.it.br.gameserver.handler.usercommandhandlers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.it.br.L2DatabaseFactory;
import com.it.br.gameserver.handler.IUserCommandHandler;
import com.it.br.gameserver.model.L2Clan;
import com.it.br.gameserver.model.actor.instance.L2PcInstance;
import com.it.br.gameserver.network.SystemMessageId;
import com.it.br.gameserver.network.serverpackets.SystemMessage;

/**
 * Support for /clanwarlist command
 * @author Tempy
 */
public class ClanWarsList implements IUserCommandHandler
{
	private static final int[] COMMAND_IDS = { 88, 89, 90 };

	/* (non-Javadoc)
	 * @see com.it.br.gameserver.handler.IUserCommandHandler#useUserCommand(int, com.it.br.gameserver.model.L2PcInstance)
	 */

	public boolean useUserCommand(int id, L2PcInstance activeChar)
	{
		if (id != COMMAND_IDS[0] && id != COMMAND_IDS[1] && id != COMMAND_IDS[2])
            return false;

		L2Clan clan = activeChar.getClan();

		if (clan == null)
		{
			activeChar.sendMessage("You are not in a clan.");
			return false;
		}

		SystemMessage sm;
		try(Connection con = L2DatabaseFactory.getInstance().getConnection();)
		{
			PreparedStatement statement;

			if (id == 88)
			{
				// Attack List
				activeChar.sendPacket(new SystemMessage(SystemMessageId.CLANS_YOU_DECLARED_WAR_ON));
				statement = con.prepareStatement("select clan_name,clan_id,ally_id,ally_name from clan_data,clan_wars where clan1=? and clan_id=clan2 and clan2 not in (select clan1 from clan_wars where clan2=?)");
				statement.setInt(1, clan.getClanId());
				statement.setInt(2, clan.getClanId());
			}
			else if (id == 89)
			{
				// Under Attack List
				activeChar.sendPacket(new SystemMessage(SystemMessageId.CLANS_THAT_HAVE_DECLARED_WAR_ON_YOU));
				statement = con.prepareStatement("select clan_name,clan_id,ally_id,ally_name from clan_data,clan_wars where clan2=? and clan_id=clan1 and clan1 not in (select clan2 from clan_wars where clan1=?)");
				statement.setInt(1, clan.getClanId());
				statement.setInt(2, clan.getClanId());
			}
			else // ID = 90
			{
				// War List
				activeChar.sendPacket(new SystemMessage(SystemMessageId.WAR_LIST));
				statement = con.prepareStatement("select clan_name,clan_id,ally_id,ally_name from clan_data,clan_wars where clan1=? and clan_id=clan2 and clan2 in (select clan1 from clan_wars where clan2=?)");
				statement.setInt(1, clan.getClanId());
				statement.setInt(2, clan.getClanId());
			}

			ResultSet rset = statement.executeQuery();

			while (rset.next())
			{
				String clanName = rset.getString("clan_name");
				int ally_id = rset.getInt("ally_id");

				if (ally_id > 0)
				{
					// Target With Ally
					sm = new SystemMessage(SystemMessageId.S1_S2_ALLIANCE);
					sm.addString(clanName);
					sm.addString(rset.getString("ally_name"));
				}
				else
				{
					// Target Without Ally
					sm = new SystemMessage(SystemMessageId.S1_NO_ALLI_EXISTS);
					sm.addString(clanName);
				}

				activeChar.sendPacket(sm);
			}

			activeChar.sendPacket(new SystemMessage(SystemMessageId.FRIEND_LIST_FOOT));

			rset.close();
			statement.close();
		}
		catch (Exception e)	{ }
		return true;
	}

	/* (non-Javadoc)
	 * @see com.it.br.gameserver.handler.IUserCommandHandler#getUserCommandList()
	 */

	public int[] getUserCommandList()
	{
		return COMMAND_IDS;
	}
}
