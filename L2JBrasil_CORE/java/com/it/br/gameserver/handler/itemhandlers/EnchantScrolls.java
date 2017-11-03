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

import com.it.br.gameserver.Shutdown;
import com.it.br.gameserver.handler.IItemHandler;
import com.it.br.gameserver.model.L2ItemInstance;
import com.it.br.gameserver.model.actor.instance.L2PcInstance;
import com.it.br.gameserver.model.actor.instance.L2PlayableInstance;
import com.it.br.gameserver.network.SystemMessageId;
import com.it.br.gameserver.network.serverpackets.ChooseInventoryItem;
import com.it.br.gameserver.network.serverpackets.SystemMessage;

public class EnchantScrolls implements IItemHandler
{

	private static final int[] ITEM_IDS = {
        729, 730, 731, 732, 6569, 6570, // a grade
        947, 948, 949, 950, 6571, 6572, // b grade
        951, 952, 953, 954, 6573, 6574, // c grade
        955, 956, 957, 958, 6575, 6576, // d grade
        959, 960, 961, 962, 6577, 6578  // s grade
	};



	public void useItem(L2PlayableInstance playable, L2ItemInstance item)
	{
		if (!(playable instanceof L2PcInstance)) return;
		L2PcInstance activeChar = (L2PcInstance)playable;
        if(activeChar.isCastingNow()) return;
		/** Not enchant's when restarting / shutdown of the server is protect against exploit by kj2a. **/
		if (Shutdown.getCounterInstance() != null)
		{
			activeChar.sendMessage("You can not ecnhant when restarting / shutdown of the server");
			return;
		}
		/** Not enchant's when is dead...is protect against exploit by AbSoLuTePoWeR. **/
		if (activeChar.isDead())
		{
			activeChar.sendMessage("You can't ecnhant right now!");
			return;
		}
		if (activeChar.isEnchanting())
		{
			activeChar.sendMessage("Enchantment already in progress!");
			return;
		}
		activeChar.setActiveEnchantItem(item);
		activeChar.sendPacket(new SystemMessage(SystemMessageId.SELECT_ITEM_TO_ENCHANT));
		activeChar.sendPacket(new ChooseInventoryItem(item.getItemId()));
		return;
	}
	

	public int[] getItemIds()
	{
		return ITEM_IDS;
	}
}
