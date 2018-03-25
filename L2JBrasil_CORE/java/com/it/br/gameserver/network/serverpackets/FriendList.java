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
package com.it.br.gameserver.network.serverpackets;

import com.it.br.gameserver.database.L2DatabaseFactory;
import com.it.br.gameserver.model.L2World;
import com.it.br.gameserver.model.actor.instance.L2PcInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * Support for "Chat with Friends" dialog.
 *
 * Format: ch (hdSdh)
 * h: Total Friend Count
 *
 * h: Unknown
 * d: Player Object ID
 * S: Friend Name
 * d: Online/Offline
 * h: Unknown
 *
 * @author Tempy
 *
 */
public class FriendList extends L2GameServerPacket
{
	private static Logger _log = LoggerFactory.getLogger(FriendList.class);
	private static final String _S__FA_FRIENDLIST = "[S] FA FriendList";

    private L2PcInstance _activeChar;

    public FriendList(L2PcInstance character)
    {
    	_activeChar = character;
    }


	@Override
	protected final void writeImpl()
	{
		if (_activeChar == null)
			return;

        Connection con = null;

		try
		{
			String sqlQuery = "SELECT friend_id, friend_name FROM character_friends WHERE " +
                    "char_id=" + _activeChar.getObjectId() + " ORDER BY friend_name ASC";

			con = L2DatabaseFactory.getInstance().getConnection();
            PreparedStatement statement = con.prepareStatement(sqlQuery);
			ResultSet rset = statement.executeQuery(sqlQuery);

			// Obtain the total number of friend entries for this player.
			rset.last();

            if (rset.getRow() > 0)
            {

            	writeC(0xfa);
    			writeH(rset.getRow());

    			rset.beforeFirst();

    			while (rset.next())
    			{
                    int friendId = rset.getInt("friend_id");
    				String friendName = rset.getString("friend_name");

    				if (friendId == _activeChar.getObjectId())
                        continue;

    				L2PcInstance friend = L2World.getInstance().getPlayer(friendName);

    				writeH(0); // ??
    				writeD(friendId);
    				writeS(friendName);

    				if (friend == null)
    					writeD(0); // offline
    				else
    					writeD(1); // online

    				writeH(0); // ??
    			}
            }

			rset.close();
			statement.close();
		}
		catch (Exception e)	{
			_log.warn("Error found in " + _activeChar.getName() + "'s FriendList: " + e);
		}
		finally	{
			try {con.close();} catch (Exception e) {}
		}
	}

	/* (non-Javadoc)
	 * @see com.it.br.gameserver.network.serverpackets.ServerBasePacket#getType()
	 */

	@Override
	public String getType()
	{
		return _S__FA_FRIENDLIST;
	}
}
