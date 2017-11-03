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

import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import com.it.br.Config;
import com.it.br.gameserver.model.L2World;
import com.it.br.gameserver.model.actor.instance.L2PcInstance;
import com.it.br.gameserver.network.SystemMessageId;
import com.it.br.gameserver.network.serverpackets.FriendRecvMsg;
import com.it.br.gameserver.network.serverpackets.SystemMessage;

/**
 * Recieve Private (Friend) Message - 0xCC
 *
 * Format: c SS
 *
 * S: Message
 * S: Receiving Player
 *
 * @author Tempy
 *
 */
public final class RequestSendFriendMsg extends L2GameClientPacket
{
    private static final String _C__CC_REQUESTSENDMSG = "[C] CC RequestSendMsg";
    private static Logger _logChat = Logger.getLogger("chat");

    private String _message;
    private String _reciever;


    @Override
	protected void readImpl()
    {
        _message = readS();
        _reciever = readS();
    }


    @Override
	protected void runImpl()
    {
    	L2PcInstance activeChar = getClient().getActiveChar();
    	if (activeChar == null) return;

        L2PcInstance targetPlayer = L2World.getInstance().getPlayer(_reciever);
        if (targetPlayer == null)
        {
        	activeChar.sendPacket(new SystemMessage(SystemMessageId.TARGET_IS_NOT_FOUND_IN_THE_GAME));
        	return;
        }

		if (Config.LOG_CHAT)
		{
			LogRecord record = new LogRecord(Level.INFO, _message);
			record.setLoggerName("chat");
			record.setParameters(new Object[]{"PRIV_MSG", "[" + activeChar.getName() + " to "+ _reciever +"]"});

			_logChat.log(record);
		}

        FriendRecvMsg frm = new FriendRecvMsg(activeChar.getName(), _reciever, _message);
        targetPlayer.sendPacket(frm);
    }


    @Override
	public String getType()
    {
        return _C__CC_REQUESTSENDMSG;
    }
}