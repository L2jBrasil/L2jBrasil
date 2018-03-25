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
import com.it.br.gameserver.model.ItemContainer;
import com.it.br.gameserver.model.L2ItemInstance;
import com.it.br.gameserver.model.L2World;
import com.it.br.gameserver.model.TradeList;
import com.it.br.gameserver.model.actor.instance.L2PcInstance;
import com.it.br.gameserver.network.SystemMessageId;
import com.it.br.gameserver.network.serverpackets.*;

/**
 * This class ...
 *
 * @version $Revision: 1.5.2.2.2.5 $ $Date: 2005/03/27 15:29:29 $
 */
public final class AddTradeItem extends L2GameClientPacket
{
    private static final String _C__16_ADDTRADEITEM = "[C] 16 AddTradeItem";
    //private static final Logger _log = LoggerFactory.getLogger(AddTradeItem.class);
    private int _tradeId;
    private int _objectId;
    private int _count;

	public AddTradeItem()
    {
    }

	@Override
	protected void readImpl()
	{
    	_tradeId = readD();
        _objectId = readD();
        _count = readD();
	}


	@Override
	protected void runImpl()
    {
        final L2PcInstance player = getClient().getActiveChar();
        if (player == null)
            return;

        final TradeList trade = player.getActiveTradeList();
        if (trade == null)
        {
            _log.warn("Character: " + player.getName() + " requested item:"
                    + _objectId + " add without active tradelist:" + _tradeId);
            return;
        }

		if (trade.getPartner() == null || L2World.getInstance().findObject(trade.getPartner().getObjectId()) == null)
        {
            // Trade partner not found, cancel trade
            if (trade.getPartner() != null)
            	_log.warn("Character:" + player.getName() + " requested invalid trade object: " + _objectId);
            SystemMessage msg = new SystemMessage(SystemMessageId.TARGET_IS_NOT_FOUND_IN_THE_GAME);
            player.sendPacket(msg);
            player.cancelActiveTrade();
            return;
        }

        // Trade partner not found, cancel trade
        if (!player.isInsideRadius(trade.getPartner(), 150, true, false))
        {
            player.cancelActiveTrade();
            player.sendPacket(new ActionFailed());
            return;
        }

        if (!player.validateItemManipulation(_objectId, "trade"))
        {
            player.sendPacket(new SystemMessage(SystemMessageId.NOTHING_HAPPENED));
            return;
        }
        
        if (Config.GM_DISABLE_TRANSACTION && player.getAccessLevel() >= Config.GM_TRANSACTION_MIN && player.getAccessLevel() <= Config.GM_TRANSACTION_MAX)
        {
            player.sendMessage("Transactions are disabled for your Access Level");
            player.cancelActiveTrade();
            return;
        }

        if (trade.isConfirmed())
        {
            player.sendPacket(new SystemMessage(SystemMessageId.NOTHING_HAPPENED));
            return;
        }


        final L2ItemInstance _tmpitem = ItemContainer.getItemByObjectId(_objectId, player.getInventory());
        if (_tmpitem == null)
        {
            player.sendPacket(new SystemMessage(SystemMessageId.NOTHING_HAPPENED));
            return;
        }

        int _inventoryItemsCount = _tmpitem.getCount();

        if (_count > _inventoryItemsCount)
            _count = _inventoryItemsCount;

        TradeList.TradeItem item = null;

		//Java Emulator Security
		if (player.getInventory().getItemByObjectId(_objectId) == null || _count <= 0)
		{
			_log.info("JES: Player " + player.getName() + " tried to trade exploit.");
			return;
		}
        // First: possible adding item
        item = trade.addItem(_objectId, _count);
        if (item != null)
        {
            _inventoryItemsCount -= item.getCount();
            player.sendPacket(new TradeOwnAdd(item));
            player.sendPacket(new TradeUpdate(trade, player));
            trade.getPartner().sendPacket(new TradeOtherAdd(item));
            return;
        }
    }


	@Override
	public String getType()
    {
        return _C__16_ADDTRADEITEM;
    }
}
