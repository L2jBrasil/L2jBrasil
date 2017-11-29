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
package com.it.br.gameserver.network.serverpackets;

import static com.it.br.configuration.Configurator.getSettings;

import com.it.br.configuration.settings.L2JBrasilSettings;
import com.it.br.gameserver.model.TradeList;
import com.it.br.gameserver.model.actor.instance.L2PcInstance;

/**
 * This class ...
 *
 * @version $Revision: 1.7.2.2.2.3 $ $Date: 2005/03/27 15:29:39 $
 */
public class PrivateStoreListBuy extends L2GameServerPacket
{
	private static final String _S__D1_PRIVATESTORELISTBUY = "[S] b8 PrivateStoreListBuy";
	private L2PcInstance _storePlayer;
	private L2PcInstance activeChar;
	private int playerAdena;
	private TradeList.TradeItem[] items;

	public PrivateStoreListBuy(L2PcInstance player, L2PcInstance storePlayer) {
		_storePlayer = storePlayer;
		activeChar = player;
		
		L2JBrasilSettings l2jBrasilSettings = getSettings(L2JBrasilSettings.class);
		if(l2jBrasilSettings.isSellByItemEnabled()) {
			CreatureSay cs11 = new CreatureSay(0, 15, "", "ATTENTION: Store System is not based on Adena, be careful!"); // 8D
			activeChar.sendPacket(cs11);
			playerAdena = activeChar.getItemCount(l2jBrasilSettings.getSellItem(), -1);
		}
		else
			playerAdena = activeChar.getAdena();
		
		_storePlayer.getSellList().updateItems(); // Update SellList for case inventory content has changed
		//this items must be the items available into the _activeChar (seller) inventory
		items = _storePlayer.getBuyList().getAvailableItems(activeChar.getInventory());
		
		
	}


	@Override
	protected final void writeImpl()
	{
		writeC(0xb8);
		writeD(_storePlayer.getObjectId());
		writeD(playerAdena);

		writeD(items.length);

		for(TradeList.TradeItem item : items)
		{
			writeD(item.getObjectId());
			writeD(item.getItem().getItemId());
			writeH(item.getEnchant());
			writeD(item.getCount()); //give max possible sell amount

			writeD(item.getItem().getReferencePrice());
			writeH(0);

			writeD(item.getItem().getBodyPart());
			writeH(item.getItem().getType2());
			writeD(item.getPrice());//buyers price

			writeD(item.getCount()); // maximum possible tradecount
		}
	}


	@Override
	public String getType()
	{
		return _S__D1_PRIVATESTORELISTBUY;
	}
}
