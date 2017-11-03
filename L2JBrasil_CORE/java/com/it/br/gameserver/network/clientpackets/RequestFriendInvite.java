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
package com.it.br.gameserver.network.clientpackets;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.logging.Level;

import com.it.br.L2DatabaseFactory;
import com.it.br.gameserver.model.L2World;
import com.it.br.gameserver.model.actor.instance.L2PcInstance;
import com.it.br.gameserver.network.SystemMessageId;
import com.it.br.gameserver.network.serverpackets.AskJoinFriend;
import com.it.br.gameserver.network.serverpackets.SystemMessage;
import com.it.br.gameserver.util.Util;

public final class RequestFriendInvite extends L2GameClientPacket
{
	private static final String _C__5E_REQUESTFRIENDINVITE = "[C] 5E RequestFriendInvite";
	//private static Logger _log = Logger.getLogger(RequestFriendInvite.class.getName());
	private String _name;


	@Override
	protected void readImpl()
	{
		_name = readS();
	}


	@Override
	protected void runImpl()
	{
		SystemMessage sm;
		Connection con = null;
		L2PcInstance activeChar = getClient().getActiveChar();

        if (activeChar == null)
            return;

        L2PcInstance friend = L2World.getInstance().getPlayer(_name);
        _name = Util.capitalizeFirst(_name); //FIXME: is it right to capitalize a nickname?

    	if (friend == null)
        {
    	    //Target is not found in the game.
    	    sm = new SystemMessage(SystemMessageId.THE_USER_YOU_REQUESTED_IS_NOT_IN_GAME);
    	    activeChar.sendPacket(sm);
    	    sm = null;
    	    return;
    	}
        else if (friend == activeChar)
        {
    	    //You cannot add yourself to your own friend list.
    	    sm = new SystemMessage(SystemMessageId.YOU_CANNOT_ADD_YOURSELF_TO_OWN_FRIEND_LIST);
    	    activeChar.sendPacket(sm);
    	    sm = null;
    	    return;
    	}

		try
		{
		    con = L2DatabaseFactory.getInstance().getConnection();
		    PreparedStatement statement = con.prepareStatement("SELECT char_id FROM character_friends WHERE char_id=? AND friend_id=?");
		    statement.setInt(1, activeChar.getObjectId());
		    statement.setInt(2, friend.getObjectId());
		    ResultSet rset = statement.executeQuery();

            if (rset.next())
            {
    			//Player already is in your friendlist
    			sm = new SystemMessage(SystemMessageId.S1_ALREADY_IN_FRIENDS_LIST);
    			sm.addString(_name);
		    }
            else
            {
		        if (!friend.isProcessingRequest())
		        {
		    	    //requets to become friend
    			    activeChar.onTransactionRequest(friend);
    			    sm = new SystemMessage(SystemMessageId.S1_REQUESTED_TO_BECOME_FRIENDS);
    			    sm.addString(_name);
    			    AskJoinFriend ajf = new AskJoinFriend(activeChar.getName());
    			    friend.sendPacket(ajf);
    			}
                else
                {
    			    sm = new SystemMessage(SystemMessageId.S1_IS_BUSY_TRY_LATER);
    			}
		    }

			friend.sendPacket(sm);
			sm = null;
			rset.close();
			statement.close();
		}
		catch (Exception e)
		{
		    _log.log(Level.WARNING, "could not add friend objectid: ", e);
		}
		finally
		{
		    try { con.close(); } catch (Exception e) {}
		}
	}


	@Override
	public String getType()
	{
		return _C__5E_REQUESTFRIENDINVITE;
	}
}