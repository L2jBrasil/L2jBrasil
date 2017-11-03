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

import java.util.logging.Logger;

import com.it.br.Config;
import com.it.br.gameserver.Shutdown;
import com.it.br.gameserver.model.L2World;
import com.it.br.gameserver.model.TradeList;
import com.it.br.gameserver.model.actor.instance.L2PcInstance;
import com.it.br.gameserver.network.SystemMessageId;
import com.it.br.gameserver.network.serverpackets.SystemMessage;

/**
 * This class ...
 *
 * @version $Revision: 1.6.2.2.2.2 $ $Date: 2005/03/27 15:29:30 $
 */
public final class TradeDone extends L2GameClientPacket
{
	private static final String _C__17_TRADEDONE = "[C] 17 TradeDone";
	private static Logger _log = Logger.getLogger(TradeDone.class.getName());
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
		if (Shutdown.getCounterInstance() != null)
		{
			player.sendMessage("You can't trade when restarting / shutdown of the server");
			return;
		} 
		if (player == null) 
		{
			return;
		}
		if (!getClient().getFloodProtectors().getTransaction().tryPerformAction("Transaction"))
        {
			player.sendMessage("You can not use Trade so fast!");
			return;
        }
		TradeList trade = player.getActiveTradeList();
		if (trade == null)
		{
			if (Config.DEBUG)
			{
				_log.warning("player.getTradeList == null in " + getType() + " for player " + player.getName());
			}
			return;
		}
		if (trade.isLocked()) 
		{
			return;
		}
		if (_response == 1)
		{
			if (trade.getPartner() == null || L2World.getInstance().findObject(trade.getPartner().getObjectId()) == null)
			{
				// Trade partner not found, cancel trade
				player.cancelActiveTrade();
				SystemMessage msg = new SystemMessage(SystemMessageId.TARGET_IS_NOT_FOUND_IN_THE_GAME);
				player.sendPacket(msg);
				msg = null;
				return;
			}
			if (trade.getOwner().getActiveEnchantItem() != null || trade.getPartner().getActiveEnchantItem() != null) 
                        {
				return;
			}
                        if (Config.GM_DISABLE_TRANSACTION && player.getAccessLevel() >= Config.GM_TRANSACTION_MIN && player.getAccessLevel() <= Config.GM_TRANSACTION_MAX)
			{
				player.cancelActiveTrade();
				player.sendMessage("Transactions are disabled for your Access Level");
                                return;
                        }
			if (player.getInstanceId() != trade.getPartner().getInstanceId() && player.getInstanceId() != -1)
			{
				player.cancelActiveTrade();
				return;
			}
			trade.confirm();
		} else 
                {
			player.cancelActiveTrade();
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.it.br.gameserver.network.clientpackets.ClientBasePacket#getType()
	 */

	@Override
	public String getType()
	{
		return _C__17_TRADEDONE;
	}
}
