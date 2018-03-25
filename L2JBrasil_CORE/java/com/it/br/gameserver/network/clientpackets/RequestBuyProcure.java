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
import com.it.br.gameserver.datatables.sql.ItemTable;
import com.it.br.gameserver.instancemanager.CastleManorManager;
import com.it.br.gameserver.instancemanager.CastleManorManager.CropProcure;
import com.it.br.gameserver.model.L2ItemInstance;
import com.it.br.gameserver.model.L2Manor;
import com.it.br.gameserver.model.L2Object;
import com.it.br.gameserver.model.actor.instance.L2ManorManagerInstance;
import com.it.br.gameserver.model.actor.instance.L2PcInstance;
import com.it.br.gameserver.network.SystemMessageId;
import com.it.br.gameserver.network.serverpackets.ActionFailed;
import com.it.br.gameserver.network.serverpackets.InventoryUpdate;
import com.it.br.gameserver.network.serverpackets.StatusUpdate;
import com.it.br.gameserver.network.serverpackets.SystemMessage;
import com.it.br.gameserver.templates.L2Item;
import com.it.br.gameserver.util.Util;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
public class RequestBuyProcure extends L2GameClientPacket {
	private static final String _C__C3_REQUESTBUYPROCURE = "[C] C3 RequestBuyProcure";
	private int _listId;
	private int _count;
	private int[] _items;
	private List<CropProcure> _procureList = new ArrayList<>();


	@Override
	protected void readImpl()
	{
		_listId = readD();
		_count = readD();
		if(_count > 500) // protect server
		{
			_count = 0;
			return;
		}

		_items = new int[_count * 2];
		for (int i = 0; i < _count; i++)
		{
			long servise = readD();
			int itemId   = readD(); _items[i * 2 + 0] = itemId;
			long cnt      = readD();
			if (cnt > Integer.MAX_VALUE || cnt < 1)
			{
				_count=0; _items = null;
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


		// Alt game - Karma punishment
        if (!Config.ALT_GAME_KARMA_PLAYER_CAN_SHOP && player.getKarma() > 0) return;

		L2Object target = player.getTarget();

        if(_count < 1)
        {
            sendPacket(new ActionFailed());
            return;
        }

        long subTotal = 0;
        int tax = 0;

    	// Check for buylist validity and calculates summary values
        int slots = 0;
        int weight = 0;
        L2ManorManagerInstance manor = (target != null && target instanceof L2ManorManagerInstance) ? (L2ManorManagerInstance)target : null;


		for (int i = 0; i < _count; i++)
		{
			int itemId = _items[i * 2 + 0];
			int count  = _items[i * 2 + 1];
			int price = 0;
			if (count > Integer.MAX_VALUE)
			{
				Util.handleIllegalPlayerAction(player,"Warning!! Character "+player.getName()+" of account "+player.getAccountName()+" tried to purchase over "+Integer.MAX_VALUE+" items at the same time.",  Config.DEFAULT_PUNISH);
				SystemMessage sm = new SystemMessage(SystemMessageId.YOU_HAVE_EXCEEDED_QUANTITY_THAT_CAN_BE_INPUTTED);
				sendPacket(sm);
				return;
			}

			L2Item template = ItemTable.getInstance().getTemplate(L2Manor.getInstance().getRewardItem(
					itemId,manor.getCastle().getCrop(itemId,CastleManorManager.PERIOD_CURRENT).getReward()));
	        weight += count * template.getWeight();

			if (!template.isStackable()) slots += count;
			else if (player.getInventory().getItemByItemId(itemId) == null) slots++;
		}

		if (!player.getInventory().validateWeight(weight))
		{
			sendPacket(new SystemMessage(SystemMessageId.WEIGHT_LIMIT_EXCEEDED));
			return;
		}


		if (!player.getInventory().validateCapacity(slots))
		{
			sendPacket(new SystemMessage(SystemMessageId.SLOTS_FULL));
			return;
		}

		// Proceed the purchase
		InventoryUpdate playerIU = new InventoryUpdate();
		_procureList =  manor.getCastle().getCropProcure(CastleManorManager.PERIOD_CURRENT);

		for (int i=0; i < _count; i++)
		{
			int itemId = _items[i * 2 + 0];
			int count  = _items[i * 2 + 1];
			if (count < 0) count = 0;

			int rewardItemId=L2Manor.getInstance().getRewardItem(
					itemId,manor.getCastle().getCrop(itemId, CastleManorManager.PERIOD_CURRENT).getReward());

			int rewardItemCount = 1; //L2Manor.getInstance().getRewardAmount(itemId, manor.getCastle().getCropReward(itemId));

			rewardItemCount = count / rewardItemCount;

			// Add item to Inventory and adjust update packet
			L2ItemInstance item = player.getInventory().addItem("Manor",rewardItemId,rewardItemCount,player,manor);
			L2ItemInstance iteme = player.getInventory().destroyItemByItemId("Manor",itemId,count,player,manor);

			if (item == null || iteme == null)
				continue;

			playerIU.addRemovedItem(iteme);
			if (item.getCount() > rewardItemCount) playerIU.addModifiedItem(item);
			else playerIU.addNewItem(item);

			// Send Char Buy Messages
			SystemMessage sm = new SystemMessage(SystemMessageId.EARNED_S2_S1_S);
			sm.addItemName(rewardItemId);
			sm.addNumber(rewardItemCount);
			player.sendPacket(sm);
			sm = null;

			//manor.getCastle().setCropAmount(itemId, manor.getCastle().getCrop(itemId, CastleManorManager.PERIOD_CURRENT).getAmount() - count);
		}

		// Send update packets
		player.sendPacket(playerIU);

		StatusUpdate su = new StatusUpdate(player.getObjectId());
		su.addAttribute(StatusUpdate.CUR_LOAD, player.getCurrentLoad());
		player.sendPacket(su);
	}


	@Override
	public String getType()
	{
		return _C__C3_REQUESTBUYPROCURE;
	}
}
