package com.it.br.gameserver.network.serverpackets;

import com.it.br.gameserver.model.L2ItemInstance;
import com.it.br.gameserver.model.TradeList;
import com.it.br.gameserver.model.TradeList.TradeItem;
import com.it.br.gameserver.model.actor.instance.L2PcInstance;

public class TradeUpdate extends L2GameServerPacket
{
    private static final String _S__74_TRADEUPDATE = "[S] 74 TradeUpdate";
    private final L2ItemInstance[] _items;
    private final TradeItem[] _trade_items;

    public TradeUpdate(final TradeList trade, final L2PcInstance activeChar)
    {
        _items = activeChar.getInventory().getItems();
        _trade_items = trade.getItems();
    }

    private int getItemCount(final int objectId)
    {
        for (final L2ItemInstance item : _items)
            if (item.getObjectId() == objectId)
                return item.getCount();
        return 0;
    }


    @Override
	public String getType()
    {
        return _S__74_TRADEUPDATE;
    }


    @Override
	protected final void writeImpl()
    {
        writeC(0x74);

        writeH(_trade_items.length);
        for (final TradeItem _item : _trade_items)
        {
            int _aveable_count = getItemCount(_item.getObjectId())
                    - _item.getCount();
            boolean _stackable = _item.getItem().isStackable();
            if (_aveable_count == 0)
            {
                _aveable_count = 1;
                _stackable = false;
            }
            writeH(_stackable ? 3 : 2);
            writeH(_item.getItem().getType1()); // item type1
            writeD(_item.getObjectId());
            writeD(_item.getItem().getItemId());
            writeD(_aveable_count);
            writeH(_item.getItem().getType2()); // item type2
            writeH(0x00); // ?
            writeD(_item.getItem().getBodyPart()); // rev 415  slot    0006-lr.ear  0008-neck  0030-lr.finger  0040-head  0080-??  0100-l.hand  0200-gloves  0400-chest  0800-pants  1000-feet  2000-??  4000-r.hand  8000-r.hand
            writeH(_item.getEnchant()); // enchant level
            writeH(0x00); // ?
            writeH(0x00);
        }
    }
}
