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

/**
 * sample
 *
 * a3
 * 05000000
 * 03000000 03000000 06000000 3c000000 00000000 	power strike
 * 10000000 02000000 06000000 3c000000 00000000 	mortal blow
 * 38000000 04000000 06000000 36010000 00000000 	power shot
 * 4d000000 01000000 01000000 98030000 01000000 	ATTACK aura  920sp
 * 8e000000 03000000 03000000 cc010000 00000000     Armor Mastery
 *
 * format   d (ddddd)
 * skillid, level, maxlevel?,
 *
 * C4 format changes:
 * 0000: [8a] [00 00 00 00] [35 00 00 00] 92 00 00 00 01 00 00    .....5..........
 *            ^^^^^^^^^^^^^
 * 0010: 00 2d 00 00 00 04 01 00 00 00 00 00 00 a4 00 00    .-..............
 * 0020: 00 01 00 00 00 03 00 00 00 e4 0c 00 00 00 00 00    ................
 * 0030: 00 d4 00 00 00 01 00 00 00 06 00 00 00 08 52 00    ..............R.
 * @version $Revision: 1.3.2.1.2.5 $ $Date: 2005/03/27 15:29:57 $
 */
public class AquireSkillList extends L2GameServerPacket
{
	//private static Logger _log = LoggerFactory.getLogger(AquireSkillList.class);
    public enum skillType
    {
    	Usual,
    	Fishing,
    	Clan
    }

	private static final String _S__A3_AQUIRESKILLLIST = "[S] 8a AquireSkillList";

	private List<Skill> _skills;
	private skillType _fishingSkills;

	private class Skill
	{
		public int id;
		public int nextLevel;
		public int maxLevel;
		public int spCost;
		public int requirements;

		public Skill(int pId, int pNextLevel, int pMaxLevel, int pSpCost, int pRequirements)
		{
			id = pId;
			nextLevel = pNextLevel;
			maxLevel = pMaxLevel;
			spCost = pSpCost;
			requirements = pRequirements;
		}
	}

	public AquireSkillList(skillType type)
	{
		_skills = new ArrayList<>();
		_fishingSkills = type;
	}

	public void addSkill(int id, int nextLevel, int maxLevel, int spCost, int requirements)
	{
		_skills.add(new Skill(id, nextLevel, maxLevel, spCost, requirements));
	}


	@Override
	protected final void writeImpl()
	{
		writeC(0x8a);
        writeD(_fishingSkills.ordinal());   //c4 : C5 : 0: usuall  1: fishing 2: clans
		writeD(_skills.size());

		for (Skill temp : _skills)
		{
			writeD(temp.id);
			writeD(temp.nextLevel);
			writeD(temp.maxLevel);
			writeD(temp.spCost);
			writeD(temp.requirements);
		}
	}

	/* (non-Javadoc)
	 * @see com.it.br.gameserver.network.serverpackets.ServerBasePacket#getType()
	 */

	@Override
	public String getType()
	{
		return _S__A3_AQUIRESKILLLIST;
	}
}
