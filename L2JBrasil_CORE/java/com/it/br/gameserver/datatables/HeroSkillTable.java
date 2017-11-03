/* This program is free software; you can redistribute it and/or modify
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
package com.it.br.gameserver.datatables;

import com.it.br.gameserver.datatables.sql.SkillTable;
import com.it.br.gameserver.model.L2Skill;

/**
 *
 * @author BiTi
 */
public class HeroSkillTable
{
	private static HeroSkillTable _instance;
	private static L2Skill[] _heroSkills;

	private HeroSkillTable()
	{
		_heroSkills = new L2Skill[5];
		_heroSkills[0] = SkillTable.getInstance().getInfo(395, 1);
		_heroSkills[1] = SkillTable.getInstance().getInfo(396, 1);
		_heroSkills[2] = SkillTable.getInstance().getInfo(1374, 1);
		_heroSkills[3] = SkillTable.getInstance().getInfo(1375, 1);
		_heroSkills[4] = SkillTable.getInstance().getInfo(1376, 1);
	}

	public static HeroSkillTable getInstance()
	{
		if (_instance == null)
			_instance = new HeroSkillTable();
		return _instance;
	}

	public static L2Skill[] GetHeroSkills()
	{
		return _heroSkills;
	}

	public static boolean isHeroSkill(int skillid)
	{
		Integer[] _HeroSkillsId = new Integer[]{395, 396, 1374, 1375, 1376};

		for (int id: _HeroSkillsId)
		{
			if (id == skillid)
				return true;
		}

		return false;
	}
}
