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
import com.it.br.gameserver.model.L2ManufactureItem;
import com.it.br.gameserver.model.L2ManufactureList;
import com.it.br.gameserver.model.actor.instance.L2PcInstance;
import com.it.br.gameserver.network.SystemMessageId;
import com.it.br.gameserver.network.serverpackets.RecipeShopMsg;
import com.it.br.gameserver.network.serverpackets.SystemMessage;

/**
 * This class ...
 * cd(dd)
 * @version $Revision: 1.1.2.3.2.3 $ $Date: 2005/03/27 15:29:30 $
 */
public final class RequestRecipeShopListSet extends L2GameClientPacket
{
	private static final String _C__B2_RequestRecipeShopListSet = "[C] b2 RequestRecipeShopListSet";
	//private static Logger _log = LoggerFactory.getLogger(RequestRecipeShopListSet.class);

	private int _count;
	private int[] _items; // count*2



	@Override
	protected void readImpl()
	{
		_count = readD();
		if (_count < 0  || _count * 8 > _buf.remaining() || _count > Config.MAX_ITEM_IN_PACKET)
            _count = 0;
		_items = new int[_count * 2];
        for (int x = 0; x < _count ; x++)
        {
            int recipeID = readD(); _items[x*2 + 0] = recipeID;
            int cost     = readD(); _items[x*2 + 1] = cost;
        }
	}


	@Override
	protected void runImpl()
	{
		L2PcInstance player = getClient().getActiveChar();
		if (player == null)
		    return;

		if (player.isInDuel())
		{
			player.sendPacket(new SystemMessage(SystemMessageId.CANT_CRAFT_DURING_COMBAT));
			return;
		}

                if (player.isInOlympiadMode())
		{
			player.sendMessage("Can't craft during Oly mode.");
			return;
		}

		if (_count == 0)
		{
			player.setPrivateStoreType(L2PcInstance.STORE_PRIVATE_NONE);
			player.broadcastUserInfo();
			player.standUp();
        
		}
		else
		{
            L2ManufactureList createList = new L2ManufactureList();

            for (int x = 0; x < _count ; x++)
            {
                int recipeID = _items[x*2 + 0];
                int cost     = _items[x*2 + 1];
                createList.add(new L2ManufactureItem(recipeID, cost));
            }
            createList.setStoreName(player.getCreateList() != null ? player.getCreateList().getStoreName() : "");
            player.setCreateList(createList);

			player.setPrivateStoreType(L2PcInstance.STORE_PRIVATE_MANUFACTURE);
			player.sitDown();
			player.broadcastUserInfo();
			player.sendPacket(new RecipeShopMsg(player));
			player.broadcastPacket(new RecipeShopMsg(player));
		}
	}



	@Override
	public String getType()
	{
		return _C__B2_RequestRecipeShopListSet;
	}


}
