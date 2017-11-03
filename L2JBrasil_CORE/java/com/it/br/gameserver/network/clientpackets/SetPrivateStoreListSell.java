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
import com.it.br.gameserver.model.TradeList;
import com.it.br.gameserver.model.actor.instance.L2PcInstance;
import com.it.br.gameserver.network.SystemMessageId;
import com.it.br.gameserver.network.serverpackets.ActionFailed;
import com.it.br.gameserver.network.serverpackets.PrivateStoreManageListSell;
import com.it.br.gameserver.network.serverpackets.PrivateStoreMsgSell;
import com.it.br.gameserver.network.serverpackets.SystemMessage;
import com.it.br.gameserver.util.Util;

/**
 * This class ...
 *
 * @version $Revision: 1.2.2.1.2.5 $ $Date: 2005/03/27 15:29:30 $
 */
public class SetPrivateStoreListSell extends L2GameClientPacket
{
	private static final String _C__74_SETPRIVATESTORELISTSELL = "[C] 74 SetPrivateStoreListSell";
	//private static Logger _log = Logger.getLogger(SetPrivateStoreListSell.class.getName());

	private int _count;
	private boolean _packageSale;
	private int[] _items; // count * 3



	@Override
	protected void readImpl()
	{
	    _packageSale = (readD() == 1);
		_count = readD();
		if (_count <= 0  || _count * 12 > _buf.remaining() || _count > Config.MAX_ITEM_IN_PACKET)
		{
		    _count = 0;
		    _items = null;
		    return;
		}
		_items = new int[_count * 3];
		for (int x = 0; x < _count ; x++)
		{
			int objectId = readD();
			_items[x * 3 + 0] = objectId;
			long cnt      = readD();
			if (cnt > Integer.MAX_VALUE || cnt < 0)
			{
			    _count = 0;
			    _items = null;
			    return;
			}
			_items[x * 3 + 1] = (int)cnt;
			int price    = readD();
			_items[x * 3 + 2] = price;
		}
	}


	@Override
	protected void runImpl()
	{
		L2PcInstance player = getClient().getActiveChar();
		if (player == null) return;

        if (Config.GM_DISABLE_TRANSACTION && player.getAccessLevel() >= Config.GM_TRANSACTION_MIN && player.getAccessLevel() <= Config.GM_TRANSACTION_MAX)
        {
            player.sendMessage("Transactions are disabled for your Access Level");
            player.sendPacket(new ActionFailed());
            return;
        }

		if(player.getPvpKills() < Config.PVP_TO_USE_STORE)
		{
			player.sendMessage("You need " + Config.PVP_TO_USE_STORE + " Pvp(s) to use the store.");
			return;
		}
		
        if (player.getKarma() >= 1)
        {
            player.sendMessage("Move your karma away!");
            player.sendPacket(new ActionFailed());
            return;
        }

        TradeList tradeList = player.getSellList();
        tradeList.clear();
        tradeList.setPackaged(_packageSale);

        for (int i = 0; i < _count; i++)
        {
            int objectId = _items[i * 3 + 0];
            int count    = _items[i * 3 + 1];
            int price    = _items[i * 3 + 2];
            
	            if (price <= 0) 
	            { 
	                String msgErr = "[SetPrivateStoreListSell] player "+getClient().getActiveChar().getName()+" tried an overflow exploit (use PHX), ban this player!"; 
	                Util.handleIllegalPlayerAction(getClient().getActiveChar(),msgErr,Config.DEFAULT_PUNISH); 
	                player.closeNetConnection(); 
                _count = 0; 
	                _items = null; 
                return; 
	            } 
            tradeList.addItem(objectId, count, price);
        }

        if (_count <= 0)
        {
            player.setPrivateStoreType(L2PcInstance.STORE_PRIVATE_NONE);
            player.broadcastUserInfo();
            player.sendPacket(new ActionFailed());
            return;
        }
        
        // Check maximum number of allowed slots for pvt shops
        if (_count > player.GetPrivateSellStoreLimit())
        {
        	player.sendPacket(new PrivateStoreManageListSell(player));
            player.sendPacket(new SystemMessage(SystemMessageId.YOU_HAVE_EXCEEDED_QUANTITY_THAT_CAN_BE_INPUTTED));
            player.sendPacket(new ActionFailed());
            return;
        }

        player.sitDown();
		if (_packageSale)
			player.setPrivateStoreType(L2PcInstance.STORE_PRIVATE_PACKAGE_SELL);
		else
		player.setPrivateStoreType(L2PcInstance.STORE_PRIVATE_SELL);
		player.broadcastUserInfo();
		player.broadcastPacket(new PrivateStoreMsgSell(player));
	}


	@Override
	public String getType()
	{
		return _C__74_SETPRIVATESTORELISTSELL;
	}
}
