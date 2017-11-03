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

import java.util.Vector;

/**
 *
 *
 * sample
 * 0000: 6d 0c 00 00 00 00 00 00 00 03 00 00 00 f3 03 00    m...............
 * 0010: 00 00 00 00 00 01 00 00 00 f4 03 00 00 00 00 00    ................
 * 0020: 00 01 00 00 00 10 04 00 00 00 00 00 00 01 00 00    ................
 * 0030: 00 2c 04 00 00 00 00 00 00 03 00 00 00 99 04 00    .,..............
 * 0040: 00 00 00 00 00 02 00 00 00 a0 04 00 00 00 00 00    ................
 * 0050: 00 01 00 00 00 c0 04 00 00 01 00 00 00 01 00 00    ................
 * 0060: 00 76 00 00 00 01 00 00 00 01 00 00 00 a3 00 00    .v..............
 * 0070: 00 01 00 00 00 01 00 00 00 c2 00 00 00 01 00 00    ................
 * 0080: 00 01 00 00 00 d6 00 00 00 01 00 00 00 01 00 00    ................
 * 0090: 00 f4 00 00 00
 *
 * format   d (ddd)
 *
 * @version $Revision: 1.3.2.1.2.5 $ $Date: 2005/03/27 15:29:39 $
 */
public class SkillList extends L2GameServerPacket
{
    private static final String _S__6D_SKILLLIST = "[S] 58 SkillList";
    private Vector<Skill> _skills;

    class Skill
    {
        public int id;
        public int level;
        public boolean passive;

        Skill(int pId, int pLevel, boolean pPassive)
        {
            id = pId;
            level = pLevel;
            passive = pPassive;
        }
    }

    public SkillList()
    {
        _skills = new Vector<Skill>();
    }

    public void addSkill(int id, int level, boolean passive)
    {
        _skills.add(new Skill(id, level, passive));
    }


	@Override
	protected final void writeImpl()
    {
        writeC(0x58);
        writeD(_skills.size());

        for (int i = 0; i < _skills.size(); i++)
        {
            Skill temp = _skills.get(i);
            writeD(temp.passive ? 1 : 0);
            writeD(temp.level);
            writeD(temp.id);
            writeC(0x00); //c5
        }
    }

    /* (non-Javadoc)
     * @see com.it.br.gameserver.network.serverpackets.ServerBasePacket#getType()
     */

	@Override
	public String getType()
    {
        return _S__6D_SKILLLIST;
    }
}
