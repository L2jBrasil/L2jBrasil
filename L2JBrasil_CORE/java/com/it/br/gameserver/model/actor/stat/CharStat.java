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
package com.it.br.gameserver.model.actor.stat;

import static com.it.br.configuration.Configurator.getSettings;

import com.it.br.Config;
import com.it.br.configuration.settings.L2JBrasilSettings;
import com.it.br.gameserver.model.L2Character;
import com.it.br.gameserver.model.L2Skill;
import com.it.br.gameserver.model.actor.instance.L2PcInstance;
import com.it.br.gameserver.skills.Calculator;
import com.it.br.gameserver.skills.Env;
import com.it.br.gameserver.skills.Stats;

public class CharStat
{
	private L2Character _activeChar;
	private long _exp = 0;
	private int _sp = 0;
	private byte _level = 1;

	public CharStat(L2Character activeChar)
	{
		_activeChar = activeChar;
	}

	public final double calcStat(Stats stat, double init, L2Character target, L2Skill skill)
	{
		if(_activeChar == null)
		{
			return init;
		}

		int id = stat.ordinal();

		Calculator c = _activeChar.getCalculators()[id];

		if(c == null || c.size() == 0)
		{
			return init;
		}

		Env env = new Env();
		env.player = _activeChar;
		env.target = target;
		env.skill = skill;
		env.value = init;
		env.baseValue = init;

		c.calc(env);
		if(env.value <= 0 && (stat == Stats.MAX_HP || stat == Stats.MAX_MP || stat == Stats.MAX_CP || stat == Stats.MAGIC_DEFENCE || stat == Stats.POWER_DEFENCE || stat == Stats.POWER_ATTACK || stat == Stats.MAGIC_ATTACK || stat == Stats.POWER_ATTACK_SPEED || stat == Stats.MAGIC_ATTACK_SPEED || stat == Stats.SHIELD_DEFENCE || stat == Stats.STAT_CON || stat == Stats.STAT_DEX || stat == Stats.STAT_INT || stat == Stats.STAT_MEN || stat == Stats.STAT_STR || stat == Stats.STAT_WIT))
		{
			env.value = 1;
		}

		c = null;

		return env.value;
	}

	public int getAccuracy()
	{
		if(_activeChar == null)
		{
			return 0;
		}

		return (int) (calcStat(Stats.ACCURACY_COMBAT, 0, null, null) / _activeChar.getWeaponExpertisePenalty());
	}

	public L2Character getActiveChar()
	{
		return _activeChar;
	}

	public final float getAttackSpeedMultiplier()
	{
		if(_activeChar == null)
		{
			return 1;
		}

		return (float) (1.1 * getPAtkSpd() / _activeChar.getTemplate().basePAtkSpd);
	}

	public final int getCON()
	{
		if(_activeChar == null)
		{
			return 1;
		}

		return (int) calcStat(Stats.STAT_CON, _activeChar.getTemplate().baseCON, null, null);
	}

	public final double getCriticalDmg(L2Character target, double init)
	{
		return calcStat(Stats.CRITICAL_DAMAGE, init, target, null);
	}

	public int getCriticalHit(L2Character target, L2Skill skill)
	{
		if(_activeChar == null)
		{
			return 1;
		}

		int criticalHit = (int) (calcStat(Stats.CRITICAL_RATE, _activeChar.getTemplate().baseCritRate, target, skill) * 10.0 + 0.5);

		criticalHit /= 10;
		
		int maxPCritRate = getSettings(L2JBrasilSettings.class).getMaxPCritRate();

		if(criticalHit > maxPCritRate) {
			criticalHit = maxPCritRate;
		}

		return criticalHit;
	}

	public final int getDEX()
	{
		if(_activeChar == null)
		{
			return 1;
		}

		return (int) calcStat(Stats.STAT_DEX, _activeChar.getTemplate().baseDEX, null, null);
	}

	public int getEvasionRate(L2Character target)
	{
		if(_activeChar == null)
		{
			return 1;
		}
		int maxEvasion = getSettings(L2JBrasilSettings.class).getMaxEvasion();
		int val = (int) (calcStat(Stats.EVASION_RATE, 0, target, null) / _activeChar.getArmourExpertisePenalty());
		if (val > maxEvasion  && !(_activeChar instanceof L2PcInstance && ((L2PcInstance)_activeChar).isGM())) 
            val = maxEvasion;
		return val;
	}

	public long getExp()
	{
		return _exp;
	}

	public void setExp(long value)
	{
		_exp = value;
	}

	public int getINT()
	{
		if(_activeChar == null)
		{
			return 1;
		}

		return (int) calcStat(Stats.STAT_INT, _activeChar.getTemplate().baseINT, null, null);
	}

	public byte getLevel()
	{
		return _level;
	}

	public void setLevel(byte value)
	{
		_level = value;
	}

	public final int getMagicalAttackRange(L2Skill skill)
	{
		if(skill != null)
		{
			return (int) calcStat(Stats.MAGIC_ATTACK_RANGE, skill.getCastRange(), null, skill);
		}

		if(_activeChar == null)
		{
			return 1;
		}

		return _activeChar.getTemplate().baseAtkRange;
	}

	public int getMaxCp()
	{
		if(_activeChar == null)
		{
			return 1;
		}

		return (int) calcStat(Stats.MAX_CP, _activeChar.getTemplate().baseCpMax, null, null);
	}

	public int getMaxHp()
	{
		if(_activeChar == null)
		{
			return 1;
		}

		return (int) calcStat(Stats.MAX_HP, _activeChar.getTemplate().baseHpMax, null, null);
	}

	public int getMaxMp()
	{
		if(_activeChar == null)
		{
			return 1;
		}

		return (int) calcStat(Stats.MAX_MP, _activeChar.getTemplate().baseMpMax, null, null);
	}

	public int getMAtk(L2Character target, L2Skill skill)
	{
		if(_activeChar == null)
		{
			return 1;
		}

		float bonusAtk = 1;

		if(Config.L2JMOD_CHAMPION_ENABLE && _activeChar.isChampion())
		{
			bonusAtk = Config.L2JMOD_CHAMPION_ATK;
		}

		double attack = _activeChar.getTemplate().baseMAtk * bonusAtk;

		Stats stat = skill == null ? null : skill.getStat();

		if(stat != null)
		{
			switch(stat)
			{
				case AGGRESSION:
					attack += _activeChar.getTemplate().baseAggression;
					break;
				case BLEED:
					attack += _activeChar.getTemplate().baseBleed;
					break;
				case POISON:
					attack += _activeChar.getTemplate().basePoison;
					break;
				case STUN:
					attack += _activeChar.getTemplate().baseStun;
					break;
				case ROOT:
					attack += _activeChar.getTemplate().baseRoot;
					break;
				case MOVEMENT:
					attack += _activeChar.getTemplate().baseMovement;
					break;
				case CONFUSION:
					attack += _activeChar.getTemplate().baseConfusion;
					break;
				case SLEEP:
					attack += _activeChar.getTemplate().baseSleep;
					break;
				case FIRE:
					attack += _activeChar.getTemplate().baseFire;
					break;
				case WIND:
					attack += _activeChar.getTemplate().baseWind;
					break;
				case WATER:
					attack += _activeChar.getTemplate().baseWater;
					break;
				case EARTH:
					attack += _activeChar.getTemplate().baseEarth;
					break;
				case HOLY:
					attack += _activeChar.getTemplate().baseHoly;
					break;
				case DARK:
					attack += _activeChar.getTemplate().baseDark;
					break;
			default:
				break;
			}
		}

		if(skill != null)
		{
			attack += skill.getPower();
		}

		stat = null;

		return (int) calcStat(Stats.MAGIC_ATTACK, attack, target, skill);
	}

	public int getMAtkSpd()
	{
		if(_activeChar == null)
		{
			return 1;
		}

		float bonusSpdAtk = 1;

		if(Config.L2JMOD_CHAMPION_ENABLE && _activeChar.isChampion())
		{
			bonusSpdAtk = Config.L2JMOD_CHAMPION_SPD_ATK;
		}

		double val = calcStat(Stats.MAGIC_ATTACK_SPEED, _activeChar.getTemplate().baseMAtkSpd * bonusSpdAtk, null, null);

		val /= _activeChar.getArmourExpertisePenalty();
		int maxMAtkSpeed = getSettings(L2JBrasilSettings.class).getMaxMAtkSpeed();
		if(val > maxMAtkSpeed && _activeChar instanceof L2PcInstance)
		{
			val = maxMAtkSpeed;
		}

		return (int) val;
	}

	public final int getMCriticalHit(L2Character target, L2Skill skill)
	{
		if(_activeChar == null)
		{
			return 1;
		}

		L2JBrasilSettings l2jBrasilSettings = getSettings(L2JBrasilSettings.class);
		
		double mrate = calcStat(Stats.MCRITICAL_RATE, l2jBrasilSettings.getMultipleMCrit(), target, skill);

		int maxMCritRate = getSettings(L2JBrasilSettings.class).getMaxMCritRate();
		if(mrate > maxMCritRate)
		{
			mrate = maxMCritRate;
		}

		return (int) mrate;
	}

	public int getMDef(L2Character target, L2Skill skill)
	{
		if(_activeChar == null)
		{
			return 1;
		}

		double defence = _activeChar.getTemplate().baseMDef;

		if(_activeChar.isRaid())
		{
			defence *= Config.RAID_DEFENCE_MULTIPLIER;
		}

		return (int) calcStat(Stats.MAGIC_DEFENCE, defence, target, skill);
	}

	public final int getMEN()
	{
		if(_activeChar == null)
		{
			return 1;
		}

		return (int) calcStat(Stats.STAT_MEN, _activeChar.getTemplate().baseMEN, null, null);
	}

	public final float getMovementSpeedMultiplier()
	{
		if(_activeChar == null)
		{
			return 1;
		}

		return getRunSpeed() / (float) _activeChar.getTemplate().baseRunSpd;
	}

	public final float getMoveSpeed()
	{
		if(_activeChar == null)
		{
			return 1;
		}

		if(_activeChar.isRunning())
		{
			return getRunSpeed();
		}

		return getWalkSpeed();
	}

	public final double getMReuseRate(L2Skill skill)
	{
		if(_activeChar == null)
		{
			return 1;
		}

		return calcStat(Stats.MAGIC_REUSE_RATE, _activeChar.getTemplate().baseMReuseRate, null, skill);
	}

	public final double getPReuseRate(L2Skill skill)
	{
		if(_activeChar == null)
		{
			return 1;
		}

		return calcStat(Stats.P_REUSE, _activeChar.getTemplate().baseMReuseRate, null, skill);
	}

	public int getPAtk(L2Character target)
	{
		if(_activeChar == null)
		{
			return 1;
		}

		float bonusAtk = 1;

		if(Config.L2JMOD_CHAMPION_ENABLE && _activeChar.isChampion())
		{
			bonusAtk = Config.L2JMOD_CHAMPION_ATK;
		}

		return (int) calcStat(Stats.POWER_ATTACK, _activeChar.getTemplate().basePAtk * bonusAtk, target, null);
	}

	public final double getPAtkAnimals(L2Character target)
	{
		return calcStat(Stats.PATK_ANIMALS, 1, target, null);
	}

	public final double getPAtkDragons(L2Character target)
	{
		return calcStat(Stats.PATK_DRAGONS, 1, target, null);
	}

	public final double getPAtkInsects(L2Character target)
	{
		return calcStat(Stats.PATK_INSECTS, 1, target, null);
	}

	public final double getPAtkMonsters(L2Character target)
	{
		return calcStat(Stats.PATK_MONSTERS, 1, target, null);
	}

	public final double getPAtkPlants(L2Character target)
	{
		return calcStat(Stats.PATK_PLANTS, 1, target, null);
	}
	
    public final double getPAtkGiants(L2Character target)  
    {  
        return calcStat(Stats.PATK_GIANTS, 1, target, null);  
    }

	public int getPAtkSpd()
	{
		if(_activeChar == null)
		{
			return 1;
		}

		float bonusAtk = 1;

		if(Config.L2JMOD_CHAMPION_ENABLE && _activeChar.isChampion())
		{
			bonusAtk = Config.L2JMOD_CHAMPION_SPD_ATK;
		}

		double val = calcStat(Stats.POWER_ATTACK_SPEED, _activeChar.getTemplate().basePAtkSpd * bonusAtk, null, null);

		val /= _activeChar.getArmourExpertisePenalty();

		int maxPAtkSpeed = getSettings(L2JBrasilSettings.class).getMaxPAtkSpeed();
		if(val > maxPAtkSpeed && _activeChar instanceof L2PcInstance) {
			val = maxPAtkSpeed;
		}

		return (int) val;
	}

	public final double getPAtkUndead(L2Character target)
	{
		return calcStat(Stats.PATK_UNDEAD, 1, target, null);
	}

	public final double getPDefUndead(L2Character target)
	{
		return calcStat(Stats.PDEF_UNDEAD, 1, target, null);
	}

	public final double getPDefPlants(L2Character target)
	{
		return calcStat(Stats.PDEF_PLANTS, 1, target, null);
	}

	public final double getPDefInsects(L2Character target)
	{
		return calcStat(Stats.PDEF_INSECTS, 1, target, null);
	}

	public final double getPDefAnimals(L2Character target)
	{
		return calcStat(Stats.PDEF_ANIMALS, 1, target, null);
	}

	public final double getPDefMonsters(L2Character target)
	{
		return calcStat(Stats.PDEF_MONSTERS, 1, target, null);
	}

	public final double getPDefDragons(L2Character target)
	{
		return calcStat(Stats.PDEF_DRAGONS, 1, target, null);
	}

	public int getPDef(L2Character target)
	{
		if(_activeChar == null)
		{
			return 1;
		}

		double defence = _activeChar.getTemplate().basePDef;

		if(_activeChar.isRaid())
		{
			defence *= Config.RAID_DEFENCE_MULTIPLIER;
		}

		return (int) calcStat(Stats.POWER_DEFENCE, defence, target, null);
	}

	public final int getPhysicalAttackRange()
	{
		if(_activeChar == null)
		{
			return 1;
		}

		return (int) calcStat(Stats.POWER_ATTACK_RANGE, _activeChar.getTemplate().baseAtkRange, null, null);
	}

	public final double getReuseModifier(L2Character target)
	{
		return calcStat(Stats.ATK_REUSE, 1, target, null);
	}

	public int getRunSpeed()
	{
		if(_activeChar == null)
		{
			return 1;
		}
		
		L2JBrasilSettings l2jBrasilSettings = getSettings(L2JBrasilSettings.class);

		int val = (int) Math.round(calcStat(Stats.RUN_SPEED, _activeChar.getTemplate().baseRunSpd, null, null)) + l2jBrasilSettings.getRunSpeedBoost();

		if(_activeChar.isInsideZone(L2Character.ZONE_WATER))
		{
			val /= 2;
		}

		if(_activeChar.isFlying())
		{
			val += Config.WYVERN_SPEED;
			return val;
		}

		if(_activeChar.isRiding())
		{
			val += Config.STRIDER_SPEED;
			return val;
		}

		val /= _activeChar.getArmourExpertisePenalty();
		
		int maxRunSpeed = l2jBrasilSettings.getMaxRunSpeed();
		if(val > maxRunSpeed && !_activeChar.charIsGM()) {
			val = maxRunSpeed;
		}

		return val;
	}

	public final int getShldDef()
	{
		return (int) calcStat(Stats.SHIELD_DEFENCE, 0, null, null);
	}

	public int getSp()
	{
		return _sp;
	}

	public void setSp(int value)
	{
		_sp = value;
	}

	public final int getSTR()
	{
		if(_activeChar == null)
		{
			return 1;
		}

		return (int) calcStat(Stats.STAT_STR, _activeChar.getTemplate().baseSTR, null, null);
	}

	public final int getWalkSpeed()
	{
		if(_activeChar == null)
		{
			return 1;
		}

		if(_activeChar instanceof L2PcInstance)
		{
			return getRunSpeed() * 70 / 100;
		}
		else
		{
			return (int) calcStat(Stats.WALK_SPEED, _activeChar.getTemplate().baseWalkSpd, null, null);
		}
	}

	public final int getWIT()
	{
		if(_activeChar == null)
		{
			return 1;
		}

		return (int) calcStat(Stats.STAT_WIT, _activeChar.getTemplate().baseWIT, null, null);
	}

	public final int getMpConsume(L2Skill skill)
	{
    	if (skill == null)
    		return 1;
    	
		double mpConsume = skill.getMpConsume();
		if (skill.isDance())
		{
			if (_activeChar != null && _activeChar.getDanceCount() > 0)
				mpConsume += _activeChar.getDanceCount() * skill.getNextDanceMpCost();
		}
		
		mpConsume = calcStat(Stats.MP_CONSUME, mpConsume, null, skill);
		
		if (skill.isDance())
			return (int) (calcStat(Stats.DANCE_MP_CONSUME_RATE, mpConsume, null, null));
		else if (skill.isMagic())
			return (int) (calcStat(Stats.MAGICAL_MP_CONSUME_RATE, mpConsume, null, null));
		else
			return (int) (calcStat(Stats.PHYSICAL_MP_CONSUME_RATE, mpConsume, null, null));
	}

	public final int getMpInitialConsume(L2Skill skill)
	{
    	if (skill == null)
    		return 1;

		double mpConsume = calcStat(Stats.MP_CONSUME, skill.getMpInitialConsume(), null, skill);
		
		if (skill.isDance())
			return (int) (calcStat(Stats.DANCE_MP_CONSUME_RATE, mpConsume, null, null));
		else if (skill.isMagic())
			return (int) (calcStat(Stats.MAGICAL_MP_CONSUME_RATE, mpConsume, null, null));
		else
			return (int) (calcStat(Stats.PHYSICAL_MP_CONSUME_RATE, mpConsume, null, null));
	}
	
	public int getDefenseElementValue(int defenseAttribute)
	{
		switch (defenseAttribute)
		{
			case 1: // wind
				return (int) (calcStat(Stats.WIND_VULN, 0, null, null));
			case 2: // fire
				return (int) (calcStat(Stats.FIRE_VULN, 0, null, null));
			case 3: // water
				return (int) (calcStat(Stats.WATER_VULN, 0, null, null));
			case 4: // earth
				return (int) (calcStat(Stats.EARTH_VULN, 0, null, null));
			case 5: // holy
				return (int) (calcStat(Stats.HOLY_VULN, 0, null, null));
			case 6: // dark
				return (int) (calcStat(Stats.DARK_VULN, 0, null, null));
			default:
				return 0;
		}
	}
	
}