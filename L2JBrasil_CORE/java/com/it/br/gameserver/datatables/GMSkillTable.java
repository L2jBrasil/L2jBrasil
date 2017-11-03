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
import com.it.br.gameserver.model.actor.instance.L2PcInstance;

public class GMSkillTable
{
	private static GMSkillTable _instance;
	private static L2Skill[] _gmSkills;

	private GMSkillTable()
	{
		_gmSkills = new L2Skill[24];
		_gmSkills[0] = SkillTable.getInstance().getInfo(7041, 1);
		_gmSkills[1] = SkillTable.getInstance().getInfo(7042, 1);
		_gmSkills[2] = SkillTable.getInstance().getInfo(7043, 1);
		_gmSkills[3] = SkillTable.getInstance().getInfo(7044, 1);
		_gmSkills[4] = SkillTable.getInstance().getInfo(7045, 1);
		_gmSkills[5] = SkillTable.getInstance().getInfo(7046, 1);
		_gmSkills[6] = SkillTable.getInstance().getInfo(7047, 1);
		_gmSkills[7] = SkillTable.getInstance().getInfo(7048, 1);
		_gmSkills[8] = SkillTable.getInstance().getInfo(7049, 1);
		_gmSkills[9] = SkillTable.getInstance().getInfo(7050, 1);
		_gmSkills[10] = SkillTable.getInstance().getInfo(7051, 1);
		_gmSkills[11] = SkillTable.getInstance().getInfo(7052, 1);
		_gmSkills[12] = SkillTable.getInstance().getInfo(7053, 1);
		_gmSkills[13] = SkillTable.getInstance().getInfo(7054, 1);
		_gmSkills[14] = SkillTable.getInstance().getInfo(7055, 1);
		_gmSkills[15] = SkillTable.getInstance().getInfo(7056, 1);
		_gmSkills[16] = SkillTable.getInstance().getInfo(7057, 1);
		_gmSkills[17] = SkillTable.getInstance().getInfo(7058, 1);
		_gmSkills[18] = SkillTable.getInstance().getInfo(7059, 1);
		_gmSkills[19] = SkillTable.getInstance().getInfo(7060, 1);
		_gmSkills[20] = SkillTable.getInstance().getInfo(7061, 1);
		_gmSkills[21] = SkillTable.getInstance().getInfo(7062, 1);
		_gmSkills[22] = SkillTable.getInstance().getInfo(7063, 1);
		_gmSkills[23] = SkillTable.getInstance().getInfo(7064, 1);
	}

	public static GMSkillTable getInstance()
	{
		if (_instance == null)
			_instance = new GMSkillTable();
		return _instance;
	}

	public static L2Skill[] GetGMSkills()
	{
		return _gmSkills;
	}

	public static boolean isGMSkill(int skillid)
	{
		Integer[] _gmSkillsId = new Integer[]
		{
			7041, 7042, 7043, 7044, 7045, 7046, 7047, 7048, 7049, 7050, 
		    7051, 7052, 7053, 7054, 7055, 7056, 7057, 7058, 7059, 7060, 
		    7061, 7062, 7063, 7064
		};

		for (int id: _gmSkillsId)
		{
			if (id == skillid)
				return true;
		}
		return false;
	}

    public void addSkills(L2PcInstance gmchar)
    {
        L2Skill arr$[] = GetGMSkills();
        int len$ = arr$.length;
        for(int i$ = 0; i$ < len$; i$++)
        {
            L2Skill s = arr$[i$];
            gmchar.addSkill(s, false);
        }
    }
}