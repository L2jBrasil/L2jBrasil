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

import com.it.br.gameserver.model.L2ManufactureItem;
import com.it.br.gameserver.model.L2ManufactureList;
import com.it.br.gameserver.model.actor.instance.L2PcInstance;

/**
 * This class ...
 * dddd d(ddd)
 * @version $Revision: 1.1.2.1.2.3 $ $Date: 2005/03/27 15:29:39 $
 */
public class RecipeShopSellList extends L2GameServerPacket
{

    private static final String _S__D9_RecipeShopSellList = "[S] d9 RecipeShopSellList";
    private L2PcInstance _buyer,_manufacturer;

    public RecipeShopSellList(L2PcInstance buyer,L2PcInstance manufacturer)
    {
        _buyer = buyer;
        _manufacturer = manufacturer;
    }


	@Override
	protected final void writeImpl()
    {
        L2ManufactureList createList = _manufacturer.getCreateList();

        if (createList != null)
        {
            //dddd d(ddd)
            writeC(0xd9);
            writeD(_manufacturer.getObjectId());
            writeD((int) _manufacturer.getCurrentMp());//Creator's MP
            writeD(_manufacturer.getMaxMp());//Creator's MP
            writeD(_buyer.getAdena());//Buyer Adena

            int count = createList.size();
            writeD(count);
            L2ManufactureItem temp;

            for (int i = 0; i < count; i++)
            {
                temp = createList.getList().get(i);
                writeD(temp.getRecipeId());
                writeD(0x00); //unknown
                writeD(temp.getCost());
            }
        }
    }

    /* (non-Javadoc)
     * @see com.it.br.gameserver.network.serverpackets.ServerBasePacket#getType()
     */

	@Override
	public String getType()
    {
        return _S__D9_RecipeShopSellList;
    }

}
