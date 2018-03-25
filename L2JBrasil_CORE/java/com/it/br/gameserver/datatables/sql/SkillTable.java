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
package com.it.br.gameserver.datatables.sql;

import com.it.br.gameserver.model.L2Skill;
import com.it.br.gameserver.skills.SkillsEngine;
import com.it.br.gameserver.templates.L2WeaponType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * This class ...
 *
 * @version $Revision: 1.8.2.6.2.18 $ $Date: 2005/04/06 16:13:25 $
 */
public class SkillTable
{
    protected static final Logger _log = LoggerFactory.getLogger(SkillTable.class);
	//private static Logger _log = LoggerFactory.getLogger(SkillTable.class);
	private static SkillTable _instance;

	private Map<Integer, L2Skill> _skills;
	private boolean _initialized = true;

	public static SkillTable getInstance()
	{
		if (_instance == null)
			_instance = new SkillTable();
		return _instance;
	}

	private SkillTable()
	{
		_skills = new HashMap<>();
		SkillsEngine.getInstance().loadAllSkills(_skills);
        _log.info("SkillsEngine: Loaded "+  _skills.size() +" Skill templates from XML files.");
	}

    public static void reload()
    {
        _instance = new SkillTable();
    }

	public boolean isInitialized()
	{
	    return _initialized;
	}

	/**
     * Provides the skill hash
     * @param skill The L2Skill to be hashed
     * @return SkillTable.getSkillHashCode(skill.getId(), skill.getLevel())
     */
    public static int getSkillHashCode(L2Skill skill)
    {
        return SkillTable.getSkillHashCode(skill.getId(), skill.getLevel());
    }

    /**
     * Centralized method for easier change of the hashing sys
     * @param skillId The Skill Id
     * @param skillLevel The Skill Level
     * @return The Skill hash number
     */
    public static int getSkillHashCode(int skillId, int skillLevel)
    {
        return skillId*256+skillLevel;
    }

	public L2Skill getInfo(int skillId, int level)
	{
		return _skills.get(SkillTable.getSkillHashCode(skillId, level));
	}

	public int getMaxLevel(int magicId, int level)
	{
	    L2Skill temp;

	    while (level < 100)
	    {
	        level++;
	        temp = _skills.get(SkillTable.getSkillHashCode(magicId, level));

		    if (temp == null)
		        return level-1;
	    }

	    return level;
	}

	private static final L2WeaponType[] weaponDbMasks = {
		L2WeaponType.ETC,
		L2WeaponType.BOW,
		L2WeaponType.POLE,
		L2WeaponType.DUALFIST,
		L2WeaponType.DUAL,
		L2WeaponType.BLUNT,
		L2WeaponType.SWORD,
		L2WeaponType.DAGGER,
        L2WeaponType.BIGSWORD,
        L2WeaponType.ROD,
        L2WeaponType.BIGBLUNT
		};

	public int calcWeaponsAllowed(int mask)
	{
		if (mask == 0)
			return 0;

		int weaponsAllowed = 0;

		for (int i=0; i < weaponDbMasks.length; i++)
			if ((mask & (1<<i)) != 0)
				weaponsAllowed |= weaponDbMasks[i].mask();

        return weaponsAllowed;
	}
	
	/**
	 * Enum to hold some important references to frequently used (hardcoded) skills in core
	 * 
	 * @author DrHouse
	 *
	 */
	public static enum FrequentSkill
	{
		SEAL_OF_RULER(246, 1),
		BUILD_HEADQUARTERS(247, 1),
		LUCKY(194, 1),
		DWARVEN_CRAFT(1321, 1),
		COMMON_CRAFT(1322, 1),
		WYVERN_BREATH(4289, 1),
		STRIDER_SIEGE_ASSAULT(325, 1),
		FAKE_PETRIFICATION(4616, 1),
		FIREWORK(5965, 1),
		LARGE_FIREWORK(2025, 1),
		BLESSING_OF_PROTECTION(5182, 1),
		ARENA_CP_RECOVERY(4380, 1),
		VOID_BURST(3630, 1),
		VOID_FLOW(3631, 1),
		THE_VICTOR_OF_WAR(5074, 1),
		THE_VANQUISHED_OF_WAR(5075, 1),
		SPECIAL_TREE_RECOVERY_BONUS(2139, 1);
		
		public final int _id;
		public final int _level;
		private L2Skill _skill = null;
		
		private FrequentSkill(int id, int level)
		{
			_id = id;
			_level = level;
		}
		
		public L2Skill getSkill()
		{
			return _skill;
		}
		
	}
}
