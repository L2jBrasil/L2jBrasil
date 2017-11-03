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

import com.it.br.gameserver.handler.IItemHandler;
import com.it.br.gameserver.model.L2ItemInstance;
import com.it.br.gameserver.model.actor.instance.L2PcInstance;
import com.it.br.gameserver.model.actor.instance.L2PlayableInstance;
import com.it.br.gameserver.network.SystemMessageId;
import com.it.br.gameserver.network.serverpackets.ItemList;
import com.it.br.gameserver.network.serverpackets.SystemMessage;

/**
 * This class ...
 *
 * @version $Revision: 1.1.2.1.2.3 $ $Date: 2005/03/27 15:30:07 $
 */

public class CompShotPacks implements IItemHandler
{
	private static final int[] ITEM_IDS ={
	                                 5134, 5135, 5136, 5137, 5138, 5139, /**/ 5250, 5251, 5252, 5253, 5254, 5255 // SS
	                                 //5140, 5141, 5142, 5143, 5144, 5145, /**/ 5256, 5257, 5258, 5259, 5260, 5261, // SpS
	                                 //5146, 5147, 5148, 5149, 5150, 5151, /**/ 5262, 5263, 5264, 5265, 5266, 5267  // BSpS
	                               };


	public void useItem(L2PlayableInstance playable, L2ItemInstance item)
	{
		if (!(playable instanceof L2PcInstance))
			return;
		L2PcInstance activeChar = (L2PcInstance)playable;

	    int itemId = item.getItemId();
	    int itemToCreateId = 0;
	    int amount = 0; // default regular pack

	    if (itemId >= 5134 && itemId <= 5139) // SS
	    {
	    	if (itemId == 5134) // No Grade
	    		itemToCreateId = 1835;
	    	else
	    		itemToCreateId = itemId - 3672;

	    	amount = 300;
	    }
	    else if(itemId >= 5250 && itemId <= 5255) // Greater SS
	    {
	    	if (itemId == 5250) // No Grade
	    		itemToCreateId = 1835;
	    	else
	       		itemToCreateId = itemId - 3788;

	    	amount = 1000;
	    }
	    else if(itemId >= 5140 && itemId <= 5145) // SpS
	    {
	    }
	    else if(itemId >= 5256 && itemId <= 5261) // Greater SpS
	    {
	    }

		activeChar.getInventory().destroyItem("Extract", item, activeChar, null);
		activeChar.getInventory().addItem("Extract", itemToCreateId, amount, activeChar, item);

	    SystemMessage sm = new SystemMessage(SystemMessageId.EARNED_S2_S1_S);
	    sm.addItemName(itemToCreateId);
	    sm.addNumber(amount);
	    activeChar.sendPacket(sm);

        ItemList playerUI = new ItemList(activeChar, false);
        activeChar.sendPacket(playerUI);
	}


	public int[] getItemIds()
	{
		return ITEM_IDS;
	}
}
