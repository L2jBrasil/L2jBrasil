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
import com.it.br.gameserver.model.ClanWarehouse;
import com.it.br.gameserver.model.ItemContainer;
import com.it.br.gameserver.model.L2Clan;
import com.it.br.gameserver.model.L2ItemInstance;
import com.it.br.gameserver.model.actor.instance.L2FolkInstance;
import com.it.br.gameserver.model.actor.instance.L2NpcInstance;
import com.it.br.gameserver.model.actor.instance.L2PcInstance;
import com.it.br.gameserver.network.SystemMessageId;
import com.it.br.gameserver.network.serverpackets.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class ...
 *
 * 32  SendWareHouseWithDrawList  cd (dd)
 * WootenGil rox :P
 * @version $Revision: 1.2.2.1.2.4 $ $Date: 2005/03/29 23:15:16 $
 */
public final class SendWareHouseWithDrawList extends L2GameClientPacket
{
	private static final String _C__32_SENDWAREHOUSEWITHDRAWLIST = "[C] 32 SendWareHouseWithDrawList";
	private static Logger _log = LoggerFactory.getLogger(SendWareHouseWithDrawList.class);

	private int _count;
	private int[] _items;


	@Override
	protected void readImpl()
	{
		_count = readD();
		if (_count < 0  || _count * 8 > _buf.remaining() || _count > Config.MAX_ITEM_IN_PACKET)
		{
		    _count = 0;
		    _items = null;
		    return;
		}
		_items = new int[_count * 2];
		for (int i=0; i < _count; i++)
		{
			int objectId = readD();
			_items[i * 2 + 0] = objectId;
			long cnt    = readD();
			if (cnt > Integer.MAX_VALUE || cnt < 0)
			{
			    _count = 0; _items = null;
			    return;
			}
			_items[i * 2 + 1] = (int)cnt;
		}
	}


	@Override
	protected void runImpl()
	{
		L2PcInstance player = getClient().getActiveChar();
        if (player == null) return;
        ItemContainer warehouse = player.getActiveWarehouse();
        if (warehouse == null) return;
		L2FolkInstance manager = player.getLastFolkNPC();
		if ((manager == null || !player.isInsideRadius(manager, L2NpcInstance.INTERACTION_DISTANCE, false, false)) && !player.isGM()) return;
		if (!player.getFloodProtectors().getWithdrawItem().tryPerformAction("WithdrawItem"))
		{
			player.sendMessage("You can not Withdraw Items so fast!");
			player.sendPacket(new ActionFailed());
			return; 
		}

        if (warehouse instanceof ClanWarehouse && Config.GM_DISABLE_TRANSACTION && player.getAccessLevel() >= Config.GM_TRANSACTION_MIN && player.getAccessLevel() <= Config.GM_TRANSACTION_MAX)
        {
            player.sendMessage("Transactions are disabled for your Access Level");
            return;
        } 

        if (player.getActiveEnchantItem() != null) 
	{ 
	    player.setAccessLevel(-100); 
	    player.setAccountAccesslevel(-100); 
	    player.sendMessage("[Cheat Guard]: You are Banned for Trying to use Enchant Exploit!"); 
	    player.closeNetConnection(); 
	    return; 
	} 
	         
        // Alt game - Karma punishment
        if (!Config.ALT_GAME_KARMA_PLAYER_CAN_USE_WAREHOUSE && player.getKarma() > 0) return;

        if (Config.MEMBERS_CAN_WITHDRAW_FROM_CLANWH)
        {
        	if (warehouse instanceof ClanWarehouse &&
        			((player.getClanPrivileges() & L2Clan.CP_CL_VIEW_WAREHOUSE)
        			!= L2Clan.CP_CL_VIEW_WAREHOUSE))
        	{
        		return;
        	}
        }
        else
        {
        	if (warehouse instanceof ClanWarehouse && !player.isClanLeader())
        	{
        		// this msg is for depositing but maybe good to send some msg?
        		player.sendPacket(new SystemMessage(SystemMessageId.ONLY_CLAN_LEADER_CAN_RETRIEVE_ITEMS_FROM_CLAN_WAREHOUSE));
        		return;
        	}
        }

        int weight = 0;
        int slots = 0;

		for (int i = 0; i < _count; i++)
		{
			int objectId = _items[i * 2 + 0];
			int count = _items[i * 2 + 1];

            // Calculate needed slots
            L2ItemInstance item = warehouse.getItemByObjectId(objectId);
            if (item == null) continue;
            weight += weight * item.getItem().getWeight();
			if (!item.isStackable()) slots += count;
            else if (player.getInventory().getItemByItemId(item.getItemId()) == null) slots++;
		}

        // Item Max Limit Check
        if (!player.getInventory().validateCapacity(slots))
        {
            sendPacket(new SystemMessage(SystemMessageId.SLOTS_FULL));
            return;
        }

        // Weight limit Check
        if (!player.getInventory().validateWeight(weight))
        {
            sendPacket(new SystemMessage(SystemMessageId.WEIGHT_LIMIT_EXCEEDED));
            return;
        }

        // Proceed to the transfer
		InventoryUpdate playerIU = Config.FORCE_INVENTORY_UPDATE ? null : new InventoryUpdate();
		for (int i = 0; i < _count; i++)
		{
			int objectId = _items[i * 2 + 0];
			int count = _items[i * 2 + 1];

			L2ItemInstance oldItem = warehouse.getItemByObjectId(objectId);
			if (oldItem == null || oldItem.getCount() < count)
				player.sendMessage("Can't withdraw requested item"+(count>1?"s":""));
			L2ItemInstance newItem = warehouse.transferItem("Warehouse", objectId, count, player.getInventory(), player, player.getLastFolkNPC());
            if (newItem == null)
            {
            	_log.warn("Error withdrawing a warehouse object for char " + player.getName());
            	continue;
            }

            if (playerIU != null)
            {
	    		if (newItem.getCount() > count) playerIU.addModifiedItem(newItem);
	        	else playerIU.addNewItem(newItem);
            }
		}

        // Send updated item list to the player
		if (playerIU != null) player.sendPacket(playerIU);
		else player.sendPacket(new ItemList(player, false));

		// Update current load status on player
		StatusUpdate su = new StatusUpdate(player.getObjectId());
		su.addAttribute(StatusUpdate.CUR_LOAD, player.getCurrentLoad());
		player.sendPacket(su);
	}

	/* (non-Javadoc)
	 * @see com.it.br.gameserver.network.clientpackets.ClientBasePacket#getType()
	 */

	@Override
	public String getType()
	{
		return _C__32_SENDWAREHOUSEWITHDRAWLIST;
	}
}
