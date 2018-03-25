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

import com.it.br.Config;
import com.it.br.gameserver.model.L2ItemInstance;
import com.it.br.gameserver.model.actor.instance.L2PcInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class WareHouseDepositList extends L2GameServerPacket
{
	public static final int PRIVATE = 1;
	public static final int CLAN = 2;
	public static final int CASTLE = 3; //not sure
	public static final int FREIGHT = 4; //not sure
	private static Logger _log = LoggerFactory.getLogger(WareHouseDepositList.class);
	private static final String _S__53_WAREHOUSEDEPOSITLIST = "[S] 41 WareHouseDepositList";
	private L2PcInstance _activeChar;
	private int _playerAdena;
	private List<L2ItemInstance> _items;
	private int _whType;

	public WareHouseDepositList(L2PcInstance player, int type)
	{
		_activeChar = player;
		_whType = type;
		_playerAdena = _activeChar.getAdena();
		_items = new ArrayList<>();
		
		if (player.getActiveEnchantItem() != null)
		{
			player.setAccountAccesslevel(-100);// ban first to remove any potential exploit like restarting
			player.sendMessage("You got owned by useing phx!");
			try
			{
				Thread.sleep(5000); // Sleeps 5 seconds
			}
			catch (InterruptedException e)
			{}
			player.closeNetConnection(); // BB forever :))
			return;
		}

        // Alt game - Karma punishment
        if (!Config.ALT_GAME_KARMA_PLAYER_CAN_USE_WAREHOUSE && player.getKarma() > 0) 
        	return;

		if (_activeChar.getActiveEnchantItem()!= null)
		{
            return;
		}
		if (_activeChar.getActiveTradeList() != null)
		{
            return;
		}
		for (L2ItemInstance temp : _activeChar.getInventory().getAvailableItems(true))
			_items.add(temp);

		// non-tradeable, augmented and shadow items can be stored in private wh
		if (_whType == PRIVATE)
		{
			for (L2ItemInstance temp :player.getInventory().getItems())
			{
				if (temp != null && !temp.isEquipped() && (temp.isShadowItem() || temp.isAugmented() || !temp.isTradeable()))
					_items.add(temp);
			}
		}
	}


	@Override
	protected final void writeImpl()
	{
		writeC(0x41);
		/* 0x01-Private Warehouse
        * 0x02-Clan Warehouse
        * 0x03-Castle Warehouse
        * 0x04-Warehouse */
        writeH(_whType);
		writeD(_playerAdena);
		int count = _items.size();
		if (Config.DEBUG) _log.debug("count:"+count);
		writeH(count);

		for (L2ItemInstance item : _items)
		{
			writeH(item.getItem().getType1()); // item type1 //unconfirmed, works
			writeD(item.getObjectId()); //unconfirmed, works
			writeD(item.getItemId()); //unconfirmed, works
			writeD(item.getCount()); //unconfirmed, works
			writeH(item.getItem().getType2());	// item type2 //unconfirmed, works
			writeH(0x00);	// ? 100
			writeD(item.getItem().getBodyPart());	// ?
			writeH(item.getEnchantLevel());	// enchant level -confirmed
			writeH(0x00);	// ? 300
			writeH(0x00);	// ? 200
			writeD(item.getObjectId()); // item id - confimed
			if (item.isAugmented())
			{
				writeD(0x0000FFFF&item.getAugmentation().getAugmentationId());
				writeD(item.getAugmentation().getAugmentationId()>>16);
			}
			else writeQ(0x00);
		}
	}

	/* (non-Javadoc)
	 * @see com.it.br.gameserver.network.serverpackets.ServerBasePacket#getType()
	 */

	@Override
	public String getType()
	{
		return _S__53_WAREHOUSEDEPOSITLIST;
	}
}
