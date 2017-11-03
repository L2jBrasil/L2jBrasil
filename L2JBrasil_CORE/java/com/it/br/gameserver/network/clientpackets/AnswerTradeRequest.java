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

import com.it.br.Config;
import com.it.br.gameserver.model.L2World;
import com.it.br.gameserver.model.actor.instance.L2PcInstance;
import com.it.br.gameserver.network.SystemMessageId;
import com.it.br.gameserver.network.serverpackets.ActionFailed;
import com.it.br.gameserver.network.serverpackets.SendTradeDone;
import com.it.br.gameserver.network.serverpackets.SystemMessage;

/**
 * This class ...
 *
 * @version $Revision: 1.5.4.2 $ $Date: 2005/03/27 15:29:30 $
 */
public final class AnswerTradeRequest extends L2GameClientPacket
{
	private static final String _C__40_ANSWERTRADEREQUEST = "[C] 40 AnswerTradeRequest";
	//private static Logger _log = Logger.getLogger(AnswerTradeRequest.class.getName());

	private int _response;


	@Override
	protected void readImpl()
	{
		_response = readD();
	}


	@Override
	protected void runImpl()
	{
	L2PcInstance player = getClient().getActiveChar();
        if (player == null)
        {
            return;
        }

        if (Config.GM_DISABLE_TRANSACTION && player.getAccessLevel() >= Config.GM_TRANSACTION_MIN && player.getAccessLevel() <= Config.GM_TRANSACTION_MAX)
	{
		player.sendMessage("Transactions are disabled for your Access Level");
		sendPacket(new ActionFailed());
		return;
	}
       
        if (player.isInOlympiadMode() || (player.isCastingNow())) 
	{ 
	    player.cancelActiveTrade(); 
	    player.sendMessage("[Server]: Your trade has been cancelled due to Olympiad match or because the player is casting now!"); 
	    return; 
	} 

        L2PcInstance partner = player.getActiveRequester();
        if (partner == null || L2World.getInstance().findObject(partner.getObjectId()) == null)
        {
            // Trade partner not found, cancel trade
	    player.sendPacket(new SendTradeDone(0));
            SystemMessage msg = new SystemMessage(SystemMessageId.TARGET_IS_NOT_FOUND_IN_THE_GAME);
            player.sendPacket(msg);
	    player.setActiveRequester(null);
	    player.setAllowTrade(true);
	    partner.setAllowTrade(true);
	    player.sendPacket(new ActionFailed());
            return;
        }

	if (_response == 1 && !partner.isRequestExpired())
        {
            player.startTrade(partner);
            partner.setAllowTrade(true);
            player.setAllowTrade(true);
        }
	else
	{
            SystemMessage msg = new SystemMessage(SystemMessageId.S1_DENIED_TRADE_REQUEST);
	    msg.addString(player.getName());
	    partner.sendPacket(msg);
	    player.setAllowTrade(true);
	    player.sendPacket(new ActionFailed());
	}

	    // Clears requesting status
	    player.setActiveRequester(null);
	    partner.onTransactionResponse();
	}

	/* (non-Javadoc)
	 * @see com.it.br.gameserver.network.clientpackets.ClientBasePacket#getType()
	 */

	@Override
	public String getType()
	{
		return _C__40_ANSWERTRADEREQUEST;
	}
}


