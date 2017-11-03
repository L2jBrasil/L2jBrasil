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
import java.util.List;

public class ExEnchantSkillInfo extends L2GameServerPacket
{
    private static final String _S__FE_18_EXENCHANTSKILLINFO = "[S] FE:18 ExEnchantSkillInfo";
    private List<Req> _reqs;
    private int _id;
    private int _level;
    private int _spCost;
    private int _xpCost;
    private int _rate;

    class Req
    {
        public int id;
        public int count;
        public int type;
        public int unk;

        Req(int pType, int pId, int pCount, int pUnk)
        {
            id = pId;
            type = pType;
            count = pCount;
            unk = pUnk;
        }
    }

    public ExEnchantSkillInfo(int id, int level, int spCost, int xpCost, int rate)
    {
        _reqs = new ArrayList<>();
        _id = id;
        _level = level;
        _spCost = spCost;
        _xpCost = xpCost;
        _rate = rate;
    }

    public void addRequirement(int type, int id, int count, int unk)
    {
        _reqs.add(new Req(type, id, count, unk));
    }

    /* (non-Javadoc)
     * @see com.it.br.gameserver.network.serverpackets.ServerBasePacket#writeImpl()
     */

	@Override
	protected void writeImpl()
    {
        writeC(0xfe);
        writeH(0x18);

        writeD(_id);
        writeD(_level);
        writeD(_spCost);
        writeQ(_xpCost);
        writeD(_rate);

        writeD(_reqs.size());

        for (Req temp : _reqs)
        {
            writeD(temp.type);
            writeD(temp.id);
            writeD(temp.count);
            writeD(temp.unk);
        }

    }

    /* (non-Javadoc)
     * @see com.it.br.gameserver.BasePacket#getType()
     */

    @Override
	public String getType()
    {
        return _S__FE_18_EXENCHANTSKILLINFO;
    }

}