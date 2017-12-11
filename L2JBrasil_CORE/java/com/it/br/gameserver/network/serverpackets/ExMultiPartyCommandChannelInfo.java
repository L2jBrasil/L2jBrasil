package com.it.br.gameserver.network.serverpackets;

import com.it.br.gameserver.model.L2CommandChannel;
import com.it.br.gameserver.model.L2Party;

public class ExMultiPartyCommandChannelInfo extends L2GameServerPacket
{
    private final L2CommandChannel _channel;

    public ExMultiPartyCommandChannelInfo(final L2CommandChannel channel)
    {
        _channel = channel;
    }

    @Override
    protected void writeImpl()
    {
        if (_channel == null)
            return;

        writeC(0xfe);
        writeH(0x30);

        writeS(_channel.getChannelLeader().getName());
        writeD(0); // Channel loot
        writeD(_channel.getMemberCount());

        writeD(_channel.getPartys().size());
        for (final L2Party p : _channel.getPartys())
        {
            writeS(p.getLeader().getName());
            writeD(p.getPartyLeaderOID());
            writeD(p.getMemberCount());
        }
    }

    @Override
    public String getType()
    {
        return "[S] FE:30 ExMultiPartyCommandChannelInfo";
    }
}