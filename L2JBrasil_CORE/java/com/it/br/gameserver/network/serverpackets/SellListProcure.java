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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import com.it.br.gameserver.instancemanager.CastleManager;
import com.it.br.gameserver.instancemanager.CastleManorManager.CropProcure;
import com.it.br.gameserver.model.L2ItemInstance;
import com.it.br.gameserver.model.actor.instance.L2PcInstance;

public class SellListProcure extends L2GameServerPacket
{
    private static final String _S__E9_SELLLISTPROCURE = "[S] E9 SellListProcure";
    //private static Logger _log = Logger.getLogger(SellListProcure.class.getName());

    private final L2PcInstance _activeChar;
    private int _money;
    private Map<L2ItemInstance,Integer> _sellList = new HashMap<>();
    private List<CropProcure> _procureList = new ArrayList<>();
    private int _castle;

    public SellListProcure(L2PcInstance player, int castleId)
    {
        _money = player.getAdena();
        _activeChar = player;
        _castle = castleId;
        _procureList =  CastleManager.getInstance().getCastleById(_castle).getCropProcure(0);
        for(CropProcure c : _procureList)
        {
            L2ItemInstance item = _activeChar.getInventory().getItemByItemId(c.getId());
            if(item != null && c.getAmount() > 0)
            {
                _sellList.put(item,c.getAmount());
            }
        }
    }


	@Override
	protected final void writeImpl()
    {
        writeC(0xE9);
        writeD(_money);         // money
        writeD(0x00);           // lease ?
        writeH(_sellList.size());         // list size

        for(L2ItemInstance item : _sellList.keySet())
        {
            writeH(item.getItem().getType1());
            writeD(item.getObjectId());
            writeD(item.getItemId());
            writeD(_sellList.get(item));  // count
            writeH(item.getItem().getType2());
            writeH(0);  // unknown
            writeD(0);  // price, u shouldnt get any adena for crops, only raw materials
        }
    }


	@Override
	public String getType()
    {
        return _S__E9_SELLLISTPROCURE;
    }
}
