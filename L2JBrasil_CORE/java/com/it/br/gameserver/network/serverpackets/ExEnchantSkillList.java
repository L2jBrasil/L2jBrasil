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

public class ExEnchantSkillList extends L2GameServerPacket
{
    private static final String _S__FE_17_EXENCHANTSKILLLIST = "[S] FE:17 ExEnchantSkillList";
    private List<Skill> _skills;

    class Skill
    {
        public int id;
        public int nextLevel;
        public int sp;
        public int exp;

        Skill(int pId, int pNextLevel, int pSp, int pExp)
        {
            id = pId;
            nextLevel = pNextLevel;
            sp = pSp;
            exp = pExp;
        }
    }

    public void addSkill(int id, int level, int sp, int exp)
    {
        _skills.add(new Skill(id, level, sp, exp));
    }

    public ExEnchantSkillList()
    {
        _skills = new ArrayList<>();
}

    /* (non-Javadoc)
     * @see com.it.br.gameserver.network.serverpackets.ServerBasePacket#writeImpl()
     */

	@Override
	protected void writeImpl()
    {
        writeC(0xfe);
        writeH(0x17);

        writeD(_skills.size());
        for (Skill sk : _skills)
        {
            writeD(sk.id);
            writeD(sk.nextLevel);
            writeD(sk.sp);
            writeQ(sk.exp);
        }

    }

    /* (non-Javadoc)
     * @see com.it.br.gameserver.BasePacket#getType()
     */

    @Override
	public String getType()
    {
        return _S__FE_17_EXENCHANTSKILLLIST;
    }

}