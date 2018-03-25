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

import com.it.br.configuration.settings.L2JBrasilSettings;
import com.it.br.gameserver.model.TradeList;
import com.it.br.gameserver.model.actor.instance.L2MerchantInstance;
import com.it.br.gameserver.model.actor.instance.L2PcInstance;

import static com.it.br.configuration.Configurator.getSettings;


/**
 * This class ...
 *
 * @version $Revision: 1.2.2.3.2.6 $ $Date: 2005/03/27 15:29:57 $
 */
public class PrivateStoreListSell extends L2GameServerPacket
{
	//	private static final String _S__B4_PRIVATEBUYLISTSELL = "[S] 9b PrivateBuyListSell";
	private static final String _S__B4_PRIVATESTORELISTSELL = "[S] 9b PrivateStoreListSell";
	private L2PcInstance _storePlayer;
	private L2PcInstance activeChar;
	private int playerAdena;
	private boolean packageSale;
	private TradeList.TradeItem[] items;

	// player's private shop
	public PrivateStoreListSell(L2PcInstance player, L2PcInstance storePlayer)
	{
		activeChar = player;
		_storePlayer = storePlayer;
		
		L2JBrasilSettings l2jBrasilSettings = getSettings(L2JBrasilSettings.class);
		if(l2jBrasilSettings.isSellByItemEnabled()) {
			CreatureSay cs11 = new CreatureSay(0, 15, "", "ATTENTION: Store System is not based on Adena, be careful!"); // 8D
			activeChar.sendPacket(cs11);
			playerAdena = activeChar.getItemCount(l2jBrasilSettings.getSellItem(), -1);
		}
		else
			playerAdena = activeChar.getAdena();
		
		items = _storePlayer.getSellList().getItems();
		packageSale = _storePlayer.getSellList().isPackaged();
	}

	// lease shop
	@Deprecated
	public PrivateStoreListSell(L2PcInstance player, L2MerchantInstance storeMerchant)
	{
		activeChar = player;
		
		L2JBrasilSettings l2jBrasilSettings = getSettings(L2JBrasilSettings.class);
		if(l2jBrasilSettings.isSellByItemEnabled())	{
			CreatureSay cs11 = new CreatureSay(0, 15, "", "ATTENTION: Store System is not based on Adena, be careful!"); // 8D
			activeChar.sendPacket(cs11);
			playerAdena = activeChar.getItemCount(l2jBrasilSettings.getSellItem(), -1);
		}
		else
			playerAdena = activeChar.getAdena();
		
		items = _storePlayer.getSellList().getItems();
		packageSale = _storePlayer.getSellList().isPackaged();
	}


	@Override
	protected final void writeImpl()
	{
		writeC(0x9b);
		writeD(_storePlayer.getObjectId());
		writeD(packageSale ? 1 : 0);
		writeD(playerAdena);

		writeD(items.length);
		for(TradeList.TradeItem item : items)
		{
			writeD(item.getItem().getType2());
			writeD(item.getObjectId());
			writeD(item.getItem().getItemId());
			writeD(item.getCount());
			writeH(0x00);
			writeH(item.getEnchant());
			writeH(0x00);
			writeD(item.getItem().getBodyPart());
			writeD(item.getPrice()); //your price
			writeD(item.getItem().getReferencePrice()); //store price
		}
	}

	/* (non-Javadoc)
	 * @see com.l2jfrozen.gameserver.serverpackets.ServerBasePacket#getType()
	 */

	@Override
	public String getType()
	{
		return _S__B4_PRIVATESTORELISTSELL;
	}
}
