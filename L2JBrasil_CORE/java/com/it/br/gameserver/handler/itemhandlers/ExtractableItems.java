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

package com.it.br.gameserver.handler.itemhandlers;

import java.util.logging.Logger;

import com.it.br.gameserver.datatables.csv.ExtractableItemsData;
import com.it.br.gameserver.datatables.sql.ItemTable;
import com.it.br.gameserver.handler.IItemHandler;
import com.it.br.gameserver.model.L2ExtractableItem;
import com.it.br.gameserver.model.L2ExtractableProductItem;
import com.it.br.gameserver.model.L2ItemInstance;
import com.it.br.gameserver.model.actor.instance.L2PcInstance;
import com.it.br.gameserver.model.actor.instance.L2PlayableInstance;
import com.it.br.gameserver.network.SystemMessageId;
import com.it.br.gameserver.network.serverpackets.SystemMessage;
import com.it.br.util.Rnd;

/**
 *
 * @author FBIagent 11/12/2006
 *
 */

public class ExtractableItems implements IItemHandler
{
	private static Logger _log = Logger.getLogger(ItemTable.class.getName());

	public void useItem(L2PlayableInstance playable, L2ItemInstance item)
	{
		if (!(playable instanceof L2PcInstance))
			return;

		L2PcInstance activeChar = (L2PcInstance) playable;

		int itemID = item.getItemId();
		L2ExtractableItem exitem = ExtractableItemsData.getInstance()
				.getExtractableItem(itemID);

		if (exitem == null)
			return;

		int createItemID = 0, createAmount = 0, rndNum = Rnd.get(100), chanceFrom = 0;

		// calculate extraction
		for (L2ExtractableProductItem expi : exitem.getProductItemsArray())
		{
			int chance = expi.getChance();

			if (rndNum >= chanceFrom && rndNum <= chance + chanceFrom)
			{
				createItemID = expi.getId();
				createAmount = expi.getAmmount();
				break;
			}

			chanceFrom += chance;
		}

		if (createItemID == 0)
		{
			activeChar.sendMessage("Nothing happened.");
			return;
		}

		if (createItemID > 0)
		{
			if (ItemTable.getInstance().createDummyItem(createItemID) == null)
			{
				_log.warning("createItemID "+createItemID+" doesn't have template!");
				activeChar.sendMessage("Nothing happened.");
				return;
			}
			if (ItemTable.getInstance().createDummyItem(createItemID)
					.isStackable())
				activeChar.addItem("Extract", createItemID, createAmount, item, false);
			else
			{
				for (int i = 0; i < createAmount; i++)
					activeChar.addItem("Extract", createItemID, 1, item, false);
			}
			SystemMessage sm;

			if (createAmount > 1)
			{
				sm = new SystemMessage(SystemMessageId.EARNED_S2_S1_S);
				sm.addItemName(createItemID);
				sm.addNumber(createAmount);
			} else
			{
				sm = new SystemMessage(SystemMessageId.EARNED_ITEM);
				sm.addItemName(createItemID);
			}
			activeChar.sendPacket(sm);
		} else
		{
			activeChar.sendMessage("Item failed to open"); // TODO: Put a more proper message here.
		}

		activeChar.destroyItemByItemId("Extract", itemID, 1, activeChar
				.getTarget(), true);
	}


	public int[] getItemIds()
    {
    	return ExtractableItemsData.getInstance().itemIDs();
    }
}
