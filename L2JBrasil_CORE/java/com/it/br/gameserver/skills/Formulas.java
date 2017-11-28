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
package com.it.br.gameserver.skills;

import static com.it.br.configuration.Configurator.getSettings;

import java.util.logging.Logger;

import com.it.br.Config;
import com.it.br.configuration.settings.L2JBrasilSettings;
import com.it.br.gameserver.SevenSigns;
import com.it.br.gameserver.SevenSignsFestival;
import com.it.br.gameserver.instancemanager.ClanHallManager;
import com.it.br.gameserver.instancemanager.SiegeManager;
import com.it.br.gameserver.model.Inventory;
import com.it.br.gameserver.model.L2Character;
import com.it.br.gameserver.model.L2SiegeClan;
import com.it.br.gameserver.model.L2Skill;
import com.it.br.gameserver.model.L2Skill.SkillType;
import com.it.br.gameserver.model.L2Summon;
import com.it.br.gameserver.model.actor.instance.L2DoorInstance;
import com.it.br.gameserver.model.actor.instance.L2NpcInstance;
import com.it.br.gameserver.model.actor.instance.L2PcInstance;
import com.it.br.gameserver.model.actor.instance.L2PetInstance;
import com.it.br.gameserver.model.actor.instance.L2PlayableInstance;
import com.it.br.gameserver.model.entity.ClanHall;
import com.it.br.gameserver.model.entity.Siege;
import com.it.br.gameserver.network.SystemMessageId;
import com.it.br.gameserver.network.serverpackets.SystemMessage;
import com.it.br.gameserver.skills.conditions.ConditionPlayerState;
import com.it.br.gameserver.skills.conditions.ConditionPlayerState.CheckPlayerState;
import com.it.br.gameserver.skills.conditions.ConditionUsingItemType;
import com.it.br.gameserver.skills.funcs.Func;
import com.it.br.gameserver.templates.L2Armor;
import com.it.br.gameserver.templates.L2NpcTemplate;
import com.it.br.gameserver.templates.L2PcTemplate;
import com.it.br.gameserver.templates.L2Weapon;
import com.it.br.gameserver.templates.L2WeaponType;
import com.it.br.gameserver.util.Util;
import com.it.br.util.Rnd;

/**
 * Global calculations, can be modified by server admins
 */
public final class Formulas
{

	/** Regen Task period */
	protected static final Logger _log = Logger.getLogger(L2Character.class.getName());
	private static final int HP_REGENERATE_PERIOD = 3000; // 3 secs

	static class FuncAddLevel3 extends Func
	{
		static final FuncAddLevel3[] _instancies = new FuncAddLevel3[Stats.NUM_STATS];

		static Func getInstance(Stats stat)
		{
			int pos = stat.ordinal();
			if (_instancies[pos] == null) _instancies[pos] = new FuncAddLevel3(stat);
			return _instancies[pos];
		}

		private FuncAddLevel3(Stats pStat)
		{
			super(pStat, 0x10, null);
		}

	
		@Override
		public void calc(Env env)
		{
			env.value += env.player.getLevel() / 3.0;
		}
	}

	static class FuncMultLevelMod extends Func
	{
		static final FuncMultLevelMod[] _instancies = new FuncMultLevelMod[Stats.NUM_STATS];

		static Func getInstance(Stats stat)
		{
			int pos = stat.ordinal();
			if (_instancies[pos] == null) _instancies[pos] = new FuncMultLevelMod(stat);
			return _instancies[pos];
		}

		private FuncMultLevelMod(Stats pStat)
		{
			super(pStat, 0x20, null);
		}

	
		@Override
		public void calc(Env env)
		{
			env.value *= env.player.getLevelMod();
		}
	}

	static class FuncMultRegenResting extends Func
	{
		static final FuncMultRegenResting[] _instancies = new FuncMultRegenResting[Stats.NUM_STATS];

		/**
		 * Return the Func object corresponding to the state concerned.<BR><BR>
		 */
		static Func getInstance(Stats stat)
		{
			int pos = stat.ordinal();

			if (_instancies[pos] == null) _instancies[pos] = new FuncMultRegenResting(stat);

			return _instancies[pos];
		}

		/**
		 * Constructor of the FuncMultRegenResting.<BR><BR>
		 */
		private FuncMultRegenResting(Stats pStat)
		{
			super(pStat, 0x20, null);
			setCondition(new ConditionPlayerState(CheckPlayerState.RESTING, true));
		}

		/**
		 * Calculate the modifier of the state concerned.<BR><BR>
		 */
	
		@Override
		public void calc(Env env)
		{
			if (!cond.test(env)) return;

			env.value *= 1.45;
		}
	}

	static class FuncPAtkMod extends Func
	{
		static final FuncPAtkMod _fpa_instance = new FuncPAtkMod();

		static Func getInstance()
		{
			return _fpa_instance;
		}

		private FuncPAtkMod()
		{
			super(Stats.POWER_ATTACK, 0x30, null);
		}

		@Override
		public void calc(Env env)
		{
			if (env.player instanceof L2PcInstance)
			{
				env.value *= BaseStats.STR.calcBonus(env.player) * env.player.getLevelMod();
			}
			else
			{
				float level = env.player.getLevel();
				env.value *= BaseStats.STR.calcBonus(env.player) * ((level + 89) / 100);
			}
		}
	}

	static class FuncMAtkMod extends Func
	{
		static final FuncMAtkMod _fma_instance = new FuncMAtkMod();

		static Func getInstance()
		{
			return _fma_instance;
		}

		private FuncMAtkMod()
		{
			super(Stats.MAGIC_ATTACK, 0x20, null);
		}

	
		@Override
		public void calc(Env env)
		{
			if (env.player instanceof L2PcInstance)
			{
				double intb = BaseStats.INT.calcBonus(env.player);
				double lvlb = env.player.getLevelMod();
				env.value *= (lvlb * lvlb) * (intb * intb);
			}
			else
			{
				float level = env.player.getLevel();
				double intb = BaseStats.INT.calcBonus(env.player);
				float lvlb = ((level + 89) / 100);
				env.value *= (lvlb * lvlb) * (intb * intb);
			}
		}
	}

	static class FuncMDefMod extends Func
	{
		static final FuncMDefMod _fmm_instance = new FuncMDefMod();

		static Func getInstance()
		{
			return _fmm_instance;
		}

		private FuncMDefMod()
		{
			super(Stats.MAGIC_DEFENCE, 0x20, null);
		}

		@Override
		public void calc(Env env)
		{
			if(env.player instanceof L2PcInstance)
			{
				L2PcInstance p = (L2PcInstance) env.player;
				if(p.getInventory().getPaperdollItem(Inventory.PAPERDOLL_LFINGER) != null)	env.value -= 5;
				if(p.getInventory().getPaperdollItem(Inventory.PAPERDOLL_RFINGER) != null)		env.value -= 5;
				if(p.getInventory().getPaperdollItem(Inventory.PAPERDOLL_LEAR) != null)		env.value -= 9;
				if(p.getInventory().getPaperdollItem(Inventory.PAPERDOLL_REAR) != null)		env.value -= 9;
				if(p.getInventory().getPaperdollItem(Inventory.PAPERDOLL_NECK) != null)		env.value -= 13;
			}
			env.value *= BaseStats.MEN.calcBonus(env.player) * env.player.getLevelMod();
		}
	}

	static class FuncPDefMod extends Func
	{
		static final FuncPDefMod _fmm_instance = new FuncPDefMod();

		static Func getInstance()
		{
			return _fmm_instance;
		}

		private FuncPDefMod()
		{
			super(Stats.POWER_DEFENCE, 0x20, null);
		}

	
		@Override
		public void calc(Env env)
		{
			if (env.player instanceof L2PcInstance)
            {
                L2PcInstance p = (L2PcInstance) env.player;
    			if (p.getInventory().getPaperdollItem(Inventory.PAPERDOLL_HEAD) != null) env.value -= 12;
    			if (p.getInventory().getPaperdollItem(Inventory.PAPERDOLL_CHEST) != null)
    				env.value -= ((p.getClassId().isMage()) ? 15 : 31);
    			if (p.getInventory().getPaperdollItem(Inventory.PAPERDOLL_LEGS) != null)
    				env.value -= ((p.getClassId().isMage()) ? 8 : 18);
    			if (p.getInventory().getPaperdollItem(Inventory.PAPERDOLL_GLOVES) != null) env.value -= 8;
    			if (p.getInventory().getPaperdollItem(Inventory.PAPERDOLL_FEET) != null) env.value -= 7;
            }
			env.value *= env.player.getLevelMod();
		}
	}

	static class FuncBowAtkRange extends Func
	{
		private static final FuncBowAtkRange _fbar_instance = new FuncBowAtkRange();

		static Func getInstance()
		{
			return _fbar_instance;
		}

		private FuncBowAtkRange()
		{
			super(Stats.POWER_ATTACK_RANGE, 0x10, null);
			setCondition(new ConditionUsingItemType(L2WeaponType.BOW.mask()));
		}

	
		@Override
		public void calc(Env env)
		{
			if (!cond.test(env)) return;
			env.value += 450;
		}
	}

	static class FuncAtkAccuracy extends Func
	{
		static final FuncAtkAccuracy _faa_instance = new FuncAtkAccuracy();

		static Func getInstance()
		{
			return _faa_instance;
		}

		private FuncAtkAccuracy()
		{
			super(Stats.ACCURACY_COMBAT, 0x10, null);
		}

	
		@Override
		public void calc(Env env)
		{
			L2Character p = env.player;
			//[Square(DEX)]*6 + lvl + weapon hitbonus;
			env.value += Math.sqrt(p.getDEX()) * 6;
			env.value += p.getLevel();
            if (p.getLevel() > 77) env.value += (p.getLevel() - 77); 
            if (p.getLevel() > 69) env.value += (p.getLevel() - 69); 
			if( p instanceof L2Summon) env.value += (p.getLevel() < 60) ? 4 : 5;
		}
	}

	static class FuncAtkEvasion extends Func
	{
		static final FuncAtkEvasion _fae_instance = new FuncAtkEvasion();

		static Func getInstance()
		{
			return _fae_instance;
		}

		private FuncAtkEvasion()
		{
			super(Stats.EVASION_RATE, 0x10, null);
		}

	
		@Override
		public void calc(Env env)
		{
			L2Character p = env.player;
			//[Square(DEX)]*6 + lvl;
			env.value += Math.sqrt(p.getDEX()) * 6;
			env.value += p.getLevel();
            if (p.getLevel() > 77) env.value += (p.getLevel() - 77); 
            if (p.getLevel() > 69) env.value += (p.getLevel() - 69); 
		}
	}

	static class FuncAtkCritical extends Func
	{
		static final FuncAtkCritical _fac_instance = new FuncAtkCritical();

		static Func getInstance()
		{
			return _fac_instance;
		}

		private FuncAtkCritical()
		{
			super(Stats.CRITICAL_RATE, 0x09, null);
		}

	
		@Override
		public void calc(Env env)
		{
			L2Character p = env.player;
			if( p instanceof L2Summon) env.value = 40;
			else if (p instanceof L2PcInstance && p.getActiveWeaponInstance() == null) env.value = 40;
			else
			{
				env.value *= BaseStats.DEX.calcBonus(env.player);
				env.value *= 10;
				int maxPCritRate = getSettings(L2JBrasilSettings.class).getMaxPCritRate();
				if(env.value > maxPCritRate)
					env.value = maxPCritRate;
			}
			env.baseValue = env.value;
		}
	}

	static class FuncMAtkCritical extends Func  
	{
		static final FuncMAtkCritical _fac_instance = new FuncMAtkCritical();  
  
		static Func getInstance()  
		{
			return _fac_instance;  
		}

		private FuncMAtkCritical()  
		{
			super(Stats.MCRITICAL_RATE, 0x30, null);  
		}

		@Override
		public void calc(Env env)  
		{  
			L2Character p = env.player;  
			if(p instanceof L2Summon)  
				env.value = 8;  
			else if (p instanceof L2PcInstance && p.getActiveWeaponInstance() == null)  
				env.value *= BaseStats.WIT.calcBonus(p);
		}
	}
	
	static class FuncMoveSpeed extends Func
	{
		static final FuncMoveSpeed _fms_instance = new FuncMoveSpeed();

		static Func getInstance()
		{
			return _fms_instance;
		}

		private FuncMoveSpeed()
		{
			super(Stats.RUN_SPEED, 0x30, null);
		}

		@Override
		public void calc(Env env)
		{
			env.value *= BaseStats.DEX.calcBonus(env.player);
		}
	}

	static class FuncPAtkSpeed extends Func
	{
		static final FuncPAtkSpeed _fas_instance = new FuncPAtkSpeed();

		static Func getInstance()
		{
			return _fas_instance;
		}

		private FuncPAtkSpeed()
		{
			super(Stats.POWER_ATTACK_SPEED, 0x20, null);
		}

		@Override
		public void calc(Env env)
		{
			env.value *= BaseStats.DEX.calcBonus(env.player);
		}
	}

	static class FuncMAtkSpeed extends Func
	{
		static final FuncMAtkSpeed _fas_instance = new FuncMAtkSpeed();

		static Func getInstance()
		{
			return _fas_instance;
		}

		private FuncMAtkSpeed()
		{
			super(Stats.MAGIC_ATTACK_SPEED, 0x20, null);
		}
		
		@Override
		public void calc(Env env)
		{
			env.value *= BaseStats.WIT.calcBonus(env.player);
		}
	}

	static class FuncHennaSTR extends Func
	{
		static final FuncHennaSTR _fh_instance = new FuncHennaSTR();

		static Func getInstance()
		{
			return _fh_instance;
		}

		private FuncHennaSTR()
		{
			super(Stats.STAT_STR, 0x10, null);
		}

		@Override
		public void calc(Env env)
		{
			//			L2PcTemplate t = (L2PcTemplate)env._player.getTemplate();
			L2PcInstance pc = (L2PcInstance) env.player;
			if (pc != null) env.value += pc.getHennaStatSTR();
		}
	}

	static class FuncHennaDEX extends Func
	{
		static final FuncHennaDEX _fh_instance = new FuncHennaDEX();

		static Func getInstance()
		{
			return _fh_instance;
		}

		private FuncHennaDEX()
		{
			super(Stats.STAT_DEX, 0x10, null);
		}

		@Override
		public void calc(Env env)
		{
			//			L2PcTemplate t = (L2PcTemplate)env._player.getTemplate();
			L2PcInstance pc = (L2PcInstance) env.player;
			if (pc != null) env.value += pc.getHennaStatDEX();
		}
	}

	static class FuncHennaINT extends Func
	{
		static final FuncHennaINT _fh_instance = new FuncHennaINT();

		static Func getInstance()
		{
			return _fh_instance;
		}

		private FuncHennaINT()
		{
			super(Stats.STAT_INT, 0x10, null);
		}

	
		@Override
		public void calc(Env env)
		{
			//			L2PcTemplate t = (L2PcTemplate)env._player.getTemplate();
			L2PcInstance pc = (L2PcInstance) env.player;
			if (pc != null) env.value += pc.getHennaStatINT();
		}
	}

	static class FuncHennaMEN extends Func
	{
		static final FuncHennaMEN _fh_instance = new FuncHennaMEN();

		static Func getInstance()
		{
			return _fh_instance;
		}

		private FuncHennaMEN()
		{
			super(Stats.STAT_MEN, 0x10, null);
		}

	
		@Override
		public void calc(Env env)
		{
			//			L2PcTemplate t = (L2PcTemplate)env._player.getTemplate();
			L2PcInstance pc = (L2PcInstance) env.player;
			if (pc != null) env.value += pc.getHennaStatMEN();
		}
	}

	static class FuncHennaCON extends Func
	{
		static final FuncHennaCON _fh_instance = new FuncHennaCON();

		static Func getInstance()
		{
			return _fh_instance;
		}

		private FuncHennaCON()
		{
			super(Stats.STAT_CON, 0x10, null);
		}

	
		@Override
		public void calc(Env env)
		{
			//			L2PcTemplate t = (L2PcTemplate)env._player.getTemplate();
			L2PcInstance pc = (L2PcInstance) env.player;
			if (pc != null) env.value += pc.getHennaStatCON();
		}
	}

	static class FuncHennaWIT extends Func
	{
		static final FuncHennaWIT _fh_instance = new FuncHennaWIT();

		static Func getInstance()
		{
			return _fh_instance;
		}

		private FuncHennaWIT()
		{
			super(Stats.STAT_WIT, 0x10, null);
		}

	
		@Override
		public void calc(Env env)
		{
			//			L2PcTemplate t = (L2PcTemplate)env._player.getTemplate();
			L2PcInstance pc = (L2PcInstance) env.player;
			if (pc != null) env.value += pc.getHennaStatWIT();
		}
	}

	static class FuncMaxHpAdd extends Func
	{
		static final FuncMaxHpAdd _fmha_instance = new FuncMaxHpAdd();

		static Func getInstance()
		{
			return _fmha_instance;
		}

		private FuncMaxHpAdd()
		{
			super(Stats.MAX_HP, 0x10, null);
		}

	
		@Override
		public void calc(Env env)
		{
			L2PcTemplate t = (L2PcTemplate) env.player.getTemplate();
			int lvl = env.player.getLevel() - t.classBaseLevel;
			double hpmod = t.lvlHpMod * lvl;
			double hpmax = (t.lvlHpAdd + hpmod) * lvl;
			double hpmin = (t.lvlHpAdd * lvl) + hpmod;
			env.value += (hpmax + hpmin) / 2;
		}
	}

	static class FuncMaxHpMul extends Func
	{
		static final FuncMaxHpMul _fmhm_instance = new FuncMaxHpMul();

		static Func getInstance()
		{
			return _fmhm_instance;
		}

		private FuncMaxHpMul()
		{
			super(Stats.MAX_HP, 0x20, null);
		}
		
		@Override
		public void calc(Env env)
		{
			env.value *= BaseStats.CON.calcBonus(env.player);
		}
	}

	static class FuncMaxCpAdd extends Func
	{
		static final FuncMaxCpAdd _fmca_instance = new FuncMaxCpAdd();

		static Func getInstance()
		{
			return _fmca_instance;
		}

		private FuncMaxCpAdd()
		{
			super(Stats.MAX_CP, 0x10, null);
		}
		
		@Override
		public void calc(Env env)
		{
			L2PcTemplate t = (L2PcTemplate) env.player.getTemplate();
			int lvl = env.player.getLevel() - t.classBaseLevel;
			double cpmod = t.lvlCpMod * lvl;
			double cpmax = (t.lvlCpAdd + cpmod) * lvl;
			double cpmin = (t.lvlCpAdd * lvl) + cpmod;
			env.value += (cpmax + cpmin) / 2;
		}
	}

	static class FuncMaxCpMul extends Func
	{
		static final FuncMaxCpMul _fmcm_instance = new FuncMaxCpMul();

		static Func getInstance()
		{
			return _fmcm_instance;
		}

		private FuncMaxCpMul()
		{
			super(Stats.MAX_CP, 0x20, null);
		}
		
		@Override
		public void calc(Env env)
		{
			env.value *= BaseStats.CON.calcBonus(env.player);
		}
	}

	static class FuncMaxMpAdd extends Func
	{
		static final FuncMaxMpAdd _fmma_instance = new FuncMaxMpAdd();

		static Func getInstance()
		{
			return _fmma_instance;
		}

		private FuncMaxMpAdd()
		{
			super(Stats.MAX_MP, 0x10, null);
		}

	
		@Override
		public void calc(Env env)
		{
			L2PcTemplate t = (L2PcTemplate) env.player.getTemplate();
			int lvl = env.player.getLevel() - t.classBaseLevel;
			double mpmod = t.lvlMpMod * lvl;
			double mpmax = (t.lvlMpAdd + mpmod) * lvl;
			double mpmin = (t.lvlMpAdd * lvl) + mpmod;
			env.value += (mpmax + mpmin) / 2;
		}
	}

	static class FuncMaxMpMul extends Func
	{
		static final FuncMaxMpMul _fmmm_instance = new FuncMaxMpMul();

		static Func getInstance()
		{
			return _fmmm_instance;
		}

		private FuncMaxMpMul()
		{
			super(Stats.MAX_MP, 0x20, null);
		}
		
		@Override
		public void calc(Env env)
		{
			env.value *= BaseStats.MEN.calcBonus(env.player);
		}
	}

	private static final Formulas _instance = new Formulas();
	private static final byte SKILL_REFLECT_SUCCEED = 0; 
	
	public static Formulas getInstance()
	{
		return _instance;
	}

	private Formulas()
	{
	}

	/**
	 * Return the period between 2 regenerations task (3s for L2Character, 5 min for L2DoorInstance).<BR><BR>
	 */
	public int getRegeneratePeriod(L2Character cha)
	{
		if (cha instanceof L2DoorInstance) return HP_REGENERATE_PERIOD * 100; // 5 mins

		return HP_REGENERATE_PERIOD; // 3s
	}

	/**
	 * Return the standard NPC Calculator set containing ACCURACY_COMBAT and EVASION_RATE.<BR><BR>
	 *
	 * <B><U> Concept</U> :</B><BR><BR>
	 * A calculator is created to manage and dynamically calculate the effect of a character property (ex : MAX_HP, REGENERATE_HP_RATE...).
	 * In fact, each calculator is a table of Func object in which each Func represents a mathematic function : <BR><BR>
	 *
	 * FuncAtkAccuracy -> Math.sqrt(_player.getDEX())*6+_player.getLevel()<BR><BR>
	 *
	 * To reduce cache memory use, L2NPCInstances who don't have skills share the same Calculator set called <B>NPC_STD_CALCULATOR</B>.<BR><BR>
	 *
	 */
	public Calculator[] getStdNPCCalculators()
	{
		Calculator[] std = new Calculator[Stats.NUM_STATS];
		
		std[Stats.MAX_HP.ordinal()] = new Calculator();
		std[Stats.MAX_HP.ordinal()].addFunc(FuncMaxHpMul.getInstance());
		
		std[Stats.MAX_MP.ordinal()] = new Calculator();
		std[Stats.MAX_MP.ordinal()].addFunc(FuncMaxMpMul.getInstance());
		
		std[Stats.POWER_ATTACK.ordinal()] = new Calculator();
		std[Stats.POWER_ATTACK.ordinal()].addFunc(FuncPAtkMod.getInstance());
		
		std[Stats.MAGIC_ATTACK.ordinal()] = new Calculator();
		std[Stats.MAGIC_ATTACK.ordinal()].addFunc(FuncMAtkMod.getInstance());
		
		std[Stats.POWER_DEFENCE.ordinal()] = new Calculator();
		std[Stats.POWER_DEFENCE.ordinal()].addFunc(FuncPDefMod.getInstance());
		
		std[Stats.MAGIC_DEFENCE.ordinal()] = new Calculator();
		std[Stats.MAGIC_DEFENCE.ordinal()].addFunc(FuncMDefMod.getInstance());
		
		std[Stats.CRITICAL_RATE.ordinal()] = new Calculator();
		std[Stats.CRITICAL_RATE.ordinal()].addFunc(FuncAtkCritical.getInstance());
		
		std[Stats.MCRITICAL_RATE.ordinal()] = new Calculator();
		std[Stats.MCRITICAL_RATE.ordinal()].addFunc(FuncMAtkCritical.getInstance());
		
		std[Stats.ACCURACY_COMBAT.ordinal()] = new Calculator();
		std[Stats.ACCURACY_COMBAT.ordinal()].addFunc(FuncAtkAccuracy.getInstance());
		
		std[Stats.EVASION_RATE.ordinal()] = new Calculator();
		std[Stats.EVASION_RATE.ordinal()].addFunc(FuncAtkEvasion.getInstance());
		
		std[Stats.POWER_ATTACK_SPEED.ordinal()] = new Calculator();
		std[Stats.POWER_ATTACK_SPEED.ordinal()].addFunc(FuncPAtkSpeed.getInstance());
		
		std[Stats.MAGIC_ATTACK_SPEED.ordinal()] = new Calculator();
		std[Stats.MAGIC_ATTACK_SPEED.ordinal()].addFunc(FuncMAtkSpeed.getInstance());
		
		std[Stats.RUN_SPEED.ordinal()] = new Calculator();
		std[Stats.RUN_SPEED.ordinal()].addFunc(FuncMoveSpeed.getInstance());
		
		return std;
	}

	/**
	 * Add basics Func objects to L2PcInstance and L2Summon.<BR><BR>
	 *
	 * <B><U> Concept</U> :</B><BR><BR>
	 * A calculator is created to manage and dynamically calculate the effect of a character property (ex : MAX_HP, REGENERATE_HP_RATE...).
	 * In fact, each calculator is a table of Func object in which each Func represents a mathematic function : <BR><BR>
	 *
	 * FuncAtkAccuracy -> Math.sqrt(_player.getDEX())*6+_player.getLevel()<BR><BR>
	 *
	 * @param cha L2PcInstance or L2Summon that must obtain basic Func objects
	 */
	public void addFuncsToNewCharacter(L2Character cha)
	{
		if (cha instanceof L2PcInstance)
		{
			cha.addStatFunc(FuncMaxHpAdd.getInstance());
			cha.addStatFunc(FuncMaxHpMul.getInstance());
			cha.addStatFunc(FuncMaxCpAdd.getInstance());
			cha.addStatFunc(FuncMaxCpMul.getInstance());
			cha.addStatFunc(FuncMaxMpAdd.getInstance());
			cha.addStatFunc(FuncMaxMpMul.getInstance());
			//cha.addStatFunc(FuncMultRegenResting.getInstance(Stats.REGENERATE_HP_RATE));
			//cha.addStatFunc(FuncMultRegenResting.getInstance(Stats.REGENERATE_CP_RATE));
			//cha.addStatFunc(FuncMultRegenResting.getInstance(Stats.REGENERATE_MP_RATE));
			cha.addStatFunc(FuncBowAtkRange.getInstance());
			//cha.addStatFunc(FuncMultLevelMod.getInstance(Stats.POWER_ATTACK));
			//cha.addStatFunc(FuncMultLevelMod.getInstance(Stats.POWER_DEFENCE));
			//cha.addStatFunc(FuncMultLevelMod.getInstance(Stats.MAGIC_DEFENCE));
			cha.addStatFunc(FuncPAtkMod.getInstance());
			cha.addStatFunc(FuncMAtkMod.getInstance());
			cha.addStatFunc(FuncPDefMod.getInstance());
			cha.addStatFunc(FuncMDefMod.getInstance());
			cha.addStatFunc(FuncAtkCritical.getInstance());
			cha.addStatFunc(FuncMAtkCritical.getInstance());
			cha.addStatFunc(FuncAtkAccuracy.getInstance());
			cha.addStatFunc(FuncAtkEvasion.getInstance());
			cha.addStatFunc(FuncPAtkSpeed.getInstance());
			cha.addStatFunc(FuncMAtkSpeed.getInstance());
			cha.addStatFunc(FuncMoveSpeed.getInstance());
			
			cha.addStatFunc(FuncHennaSTR.getInstance());
			cha.addStatFunc(FuncHennaDEX.getInstance());
			cha.addStatFunc(FuncHennaINT.getInstance());
			cha.addStatFunc(FuncHennaMEN.getInstance());
			cha.addStatFunc(FuncHennaCON.getInstance());
			cha.addStatFunc(FuncHennaWIT.getInstance());
		}
		else if (cha instanceof L2PetInstance)
		{
			cha.addStatFunc(FuncMaxHpMul.getInstance());
			cha.addStatFunc(FuncMaxMpMul.getInstance());
			cha.addStatFunc(FuncPAtkMod.getInstance());
			cha.addStatFunc(FuncMAtkMod.getInstance());
			cha.addStatFunc(FuncPDefMod.getInstance());
			cha.addStatFunc(FuncMDefMod.getInstance());
			cha.addStatFunc(FuncAtkCritical.getInstance());
			cha.addStatFunc(FuncMAtkCritical.getInstance());
			cha.addStatFunc(FuncAtkAccuracy.getInstance());
			cha.addStatFunc(FuncAtkEvasion.getInstance());
			cha.addStatFunc(FuncMoveSpeed.getInstance());
			cha.addStatFunc(FuncPAtkSpeed.getInstance());
			cha.addStatFunc(FuncMAtkSpeed.getInstance());
		}
		else if (cha instanceof L2Summon)
		{
			//cha.addStatFunc(FuncMultRegenResting.getInstance(Stats.REGENERATE_HP_RATE));
			//cha.addStatFunc(FuncMultRegenResting.getInstance(Stats.REGENERATE_MP_RATE));
			cha.addStatFunc(FuncMaxHpMul.getInstance());
			cha.addStatFunc(FuncMaxMpMul.getInstance());
			cha.addStatFunc(FuncPAtkMod.getInstance());
			cha.addStatFunc(FuncMAtkMod.getInstance());
			cha.addStatFunc(FuncPDefMod.getInstance());
			cha.addStatFunc(FuncMDefMod.getInstance());
			cha.addStatFunc(FuncAtkCritical.getInstance());
			cha.addStatFunc(FuncMAtkCritical.getInstance());
			cha.addStatFunc(FuncAtkAccuracy.getInstance());
			cha.addStatFunc(FuncAtkEvasion.getInstance());
			cha.addStatFunc(FuncMoveSpeed.getInstance());
			cha.addStatFunc(FuncPAtkSpeed.getInstance());
			cha.addStatFunc(FuncMAtkSpeed.getInstance());
		}
	}

	/**
	 * Calculate the HP regen rate (base + modifiers).<BR><BR>
	 */
	public final double calcHpRegen(L2Character cha)
	{
        double init = cha.getTemplate().baseHpReg;
		double hpRegenMultiplier = cha.isRaid() ? Config.RAID_HP_REGEN_MULTIPLIER : Config.HP_REGEN_MULTIPLIER;
		double hpRegenBonus = 0;

		if (Config.L2JMOD_CHAMPION_ENABLE && cha.isChampion())
			hpRegenMultiplier *= Config.L2JMOD_CHAMPION_HP_REGEN;

		if (cha instanceof L2PcInstance)
		{
            L2PcInstance player = (L2PcInstance) cha;

            // Calculate correct baseHpReg value for certain level of PC
            init += (player.getLevel() > 10) ? ((player.getLevel()-1)/10.0) : 0.5;

            // SevenSigns Festival modifier
			if (SevenSignsFestival.getInstance().isFestivalInProgress() && player.isFestivalParticipant())
                hpRegenMultiplier *= calcFestivalRegenModifier(player);
			else
			{
				double siegeModifier = calcSiegeRegenModifer(player);
				if (siegeModifier > 0) hpRegenMultiplier *= siegeModifier;
			}

            if (player.isInsideZone(L2Character.ZONE_CLANHALL) && player.getClan() != null)
            {
            	int clanHallIndex = player.getClan().getHasHideout();
            	if (clanHallIndex > 0)
            	{
            		ClanHall clansHall = ClanHallManager.getInstance().getClanHallById(clanHallIndex);
            		if(clansHall != null)
            			if (clansHall.getFunction(ClanHall.FUNC_RESTORE_HP) != null)
            				hpRegenMultiplier *= 1+ clansHall.getFunction(ClanHall.FUNC_RESTORE_HP).getLvl()/100;
            	}
            }

			// Mother Tree effect is calculated at last
			if (player.isInsideZone(L2Character.ZONE_MOTHERTREE)) hpRegenBonus += 2;
		
           // Custom: 1x Olympiad modifier
           if(player.isInOlympiadMode())
              hpRegenMultiplier = 1;
        

            // Calculate Movement bonus
            if (player.isSitting()) hpRegenMultiplier *= 1.5;      // Sitting
            else if (!player.isMoving()) hpRegenMultiplier *= 1.1; // Staying
            else if (player.isRunning()) hpRegenMultiplier *= 0.7; // Running

            // Add CON bonus
            init *= cha.getLevelMod() * BaseStats.CON.calcBonus(cha);
		}
        else if (cha instanceof L2PetInstance) 
               init = ((L2PetInstance) cha).getPetData().getPetRegenHP(); 
        if (init < 1) init = 1;

        return cha.calcStat(Stats.REGENERATE_HP_RATE, init, null, null) * hpRegenMultiplier + hpRegenBonus;
	}

	/**
	 * Calculate the MP regen rate (base + modifiers).<BR><BR>
	 */
	public final double calcMpRegen(L2Character cha)
	{
        double init = cha.getTemplate().baseMpReg;
        double mpRegenMultiplier = cha.isRaid() ? Config.RAID_MP_REGEN_MULTIPLIER : Config.MP_REGEN_MULTIPLIER;
		double mpRegenBonus = 0;

		if (cha instanceof L2PcInstance)
		{
			L2PcInstance player = (L2PcInstance) cha;

            // Calculate correct baseMpReg value for certain level of PC
            init += 0.3*((player.getLevel()-1)/10.0);

            // SevenSigns Festival modifier
            if (SevenSignsFestival.getInstance().isFestivalInProgress() && player.isFestivalParticipant())
				mpRegenMultiplier *= calcFestivalRegenModifier(player);

			// Mother Tree effect is calculated at last
			if (player.isInsideZone(L2Character.ZONE_MOTHERTREE)) 
				mpRegenBonus += 1;

            if (player.isInsideZone(L2Character.ZONE_CLANHALL) && player.getClan() != null)
            {
            	int clanHallIndex = player.getClan().getHasHideout();
            	if (clanHallIndex > 0)
            	{
            		ClanHall clansHall = ClanHallManager.getInstance().getClanHallById(clanHallIndex);
            		if(clansHall != null)
            			if (clansHall.getFunction(ClanHall.FUNC_RESTORE_MP) != null)
            				mpRegenMultiplier *= 1+ clansHall.getFunction(ClanHall.FUNC_RESTORE_MP).getLvl()/100;
            	}
            }

           // Custom: 1x Olympiad modifier
            if(player.isInOlympiadMode())
              mpRegenMultiplier = 1;
            
			// Calculate Movement bonus
            if (player.isSitting()) mpRegenMultiplier *= 1.5;      // Sitting
            else if (!player.isMoving()) mpRegenMultiplier *= 1.1; // Staying
            else if (player.isRunning()) mpRegenMultiplier *= 0.7; // Running

            // Add MEN bonus
            init *= cha.getLevelMod() * BaseStats.MEN.calcBonus(cha);
		}
        else if (cha instanceof L2PetInstance) 
                init = ((L2PetInstance) cha).getPetData().getPetRegenMP(); 
		if (init < 1) init = 1;

		return cha.calcStat(Stats.REGENERATE_MP_RATE, init, null, null) * mpRegenMultiplier + mpRegenBonus;
	}

	/**
	 * Calculate the CP regen rate (base + modifiers).<BR><BR>
	 */
	public final double calcCpRegen(L2Character cha)
	{
        double init = cha.getTemplate().baseHpReg;
        double cpRegenMultiplier = Config.CP_REGEN_MULTIPLIER;
        double cpRegenBonus = 0;

        if (cha instanceof L2PcInstance)
        {
	        L2PcInstance player = (L2PcInstance) cha;

	        // Calculate correct baseHpReg value for certain level of PC
	        init += (player.getLevel() > 10) ? ((player.getLevel()-1)/10.0) : 0.5;
          
           // Custom: 1x Olympiad modifier
           if(player.isInOlympiadMode())
          
              
	        // Calculate Movement bonus
	        if (player.isSitting()) cpRegenMultiplier *= 1.5;      // Sitting
	        else if (!player.isMoving()) cpRegenMultiplier *= 1.1; // Staying
	        else if (player.isRunning()) cpRegenMultiplier *= 0.7; // Running
        } else
        {
	        // Calculate Movement bonus
        	if (!cha.isMoving()) cpRegenMultiplier *= 1.1; // Staying
	        else if (cha.isRunning()) cpRegenMultiplier *= 0.7; // Running
        }

        // Apply CON bonus
        init *= cha.getLevelMod() * BaseStats.CON.calcBonus(cha);
        if (init < 1) init = 1;

        return cha.calcStat(Stats.REGENERATE_CP_RATE, init, null, null) * cpRegenMultiplier + cpRegenBonus;
	}

	@SuppressWarnings("deprecation")
	public final double calcFestivalRegenModifier(L2PcInstance activeChar)
	{
		final int[] festivalInfo = SevenSignsFestival.getInstance().getFestivalForPlayer(activeChar);
		final int oracle = festivalInfo[0];
		final int festivalId = festivalInfo[1];
		int[] festivalCenter;

		// If the player isn't found in the festival, leave the regen rate as it is.
		if (festivalId < 0) return 0;

		// Retrieve the X and Y coords for the center of the festival arena the player is in.
		if (oracle == SevenSigns.CABAL_DAWN) festivalCenter = SevenSignsFestival.FESTIVAL_DAWN_PLAYER_SPAWNS[festivalId];
		else festivalCenter = SevenSignsFestival.FESTIVAL_DUSK_PLAYER_SPAWNS[festivalId];

		// Check the distance between the player and the player spawn point, in the center of the arena.
		double distToCenter = activeChar.getDistance(festivalCenter[0], festivalCenter[1]);

		if (Config.DEBUG)
			_log.info("Distance: " + distToCenter + ", RegenMulti: " + (distToCenter * 2.5) / 50);

		return 1.0 - (distToCenter * 0.0005); // Maximum Decreased Regen of ~ -65%;
	}

	public final double calcSiegeRegenModifer(L2PcInstance activeChar)
	{
		if (activeChar == null || activeChar.getClan() == null) return 0;

		Siege siege = SiegeManager.getInstance().getSiege(activeChar.getPosition().getX(),
															activeChar.getPosition().getY(),
															activeChar.getPosition().getZ());
		if (siege == null || !siege.getIsInProgress()) return 0;

		L2SiegeClan siegeClan = siege.getAttackerClan(activeChar.getClan().getClanId());
		if (siegeClan == null || siegeClan.getFlag().size() == 0
			|| !Util.checkIfInRange(200, activeChar, siegeClan.getFlag().get(0), true)) return 0;

		return 1.5; // If all is true, then modifer will be 50% more
	}
	/** Calculate blow damage based on cAtk */
	public double calcBlowDamage(L2Character attacker, L2Character target, L2Skill skill, boolean shld, boolean ss)
	{
		double power = skill.getPower();
		double damage = attacker.getPAtk(target);
		double defence = target.getPDef(attacker);
		if(ss)
			damage *= 2.;
		if(shld)
			defence += target.getShldDef();
		if(ss && skill.getSSBoost()>0)
			power *= skill.getSSBoost();

		//Multiplier should be removed, it's false ??
		damage += 1.3*attacker.calcStat(Stats.CRITICAL_DAMAGE, damage+power, target, skill);
		//damage *= (double)attacker.getLevel()/target.getLevel();

		// get the natural vulnerability for the template
		if (target instanceof L2NpcInstance)
		{
			damage *= ((L2NpcInstance) target).getTemplate().getVulnerability(Stats.DAGGER_WPN_VULN);
		}
		// get the vulnerability for the instance due to skills (buffs, passives, toggles, etc)
		damage = target.calcStat(Stats.DAGGER_WPN_VULN, damage, target, null);
		damage *= 70. / defence;
		damage += Rnd.get() * attacker.getRandomDamage(target);
		// Sami: Must be removed, after armor resistances are checked.
		// These values are a quick fix to balance dagger gameplay and give
		// armor resistances vs dagger. daggerWpnRes could also be used if a skill
		// was given to all classes. The values here try to be a compromise.
		// They were originally added in a late C4 rev (2289).
		if (target instanceof L2PcInstance)
		{
			L2Armor armor = ((L2PcInstance)target).getActiveChestArmorItem();
			if (armor != null)
			{
				if(((L2PcInstance)target).isWearingHeavyArmor())
					damage /= 1.2; // 2
				//if(((L2PcInstance)target).isWearingLightArmor())
				//	damage /= 1.1; // 1.5
				//if(((L2PcInstance)target).isWearingMagicArmor())
				//	damage /= 1;   // 1.3
			}
		}
		return damage < 1 ? 1. : damage;
    } 
	        public static byte calcSkillReflect(L2Character target, L2Skill skill) 
	        { 
	                byte reflect = 0; 
	                        switch (skill.getSkillType()) 
	                { 
	                        case BUFF: 
	                        case REFLECT: 
	                        case HEAL_PERCENT: 
	                        case MANAHEAL_PERCENT: 
	                        case HOT: 
	                        case CPHOT: 
	                        case MPHOT: 
	                        case AGGDEBUFF: 
	                        case CONT: 
	                                return 0; 
	                                // these skill types can deal damage 
	                        case PDAM: 
	                        case BLOW: 
	                        case MDAM: 
	                        case DEATHLINK: 
	                        case CHARGEDAM: 
	                                final Stats stat = skill.isMagic() ? Stats.VENGEANCE_SKILL_MAGIC_DAMAGE : Stats.VENGEANCE_SKILL_PHYSICAL_DAMAGE; 
	                                final double venganceChance = target.getStat().calcStat(stat, 0, target, skill); 
	                                byte SKILL_REFLECT_VENGEANCE = 0; 
	                                if (venganceChance > Rnd.get(100)) 
	                                        reflect |= SKILL_REFLECT_VENGEANCE; 
	                                break; 
	                } 
	                 
	                final double reflectChance = target.calcStat(skill.isMagic() ? Stats.REFLECT_SKILL_MAGIC : Stats.REFLECT_SKILL_PHYSIC, 0, null, skill); 
	                 
	                if (Rnd.get(100) < reflectChance) 
	                        reflect |= SKILL_REFLECT_SUCCEED; 
	                 
	                return reflect; 
	}
	        
    /**  
     * Calculate damage caused by falling, character will not die  
     * @param cha
     * @param fallHeight
     * @return
     */
	public static double calcFallDam(L2Character cha, int fallHeight)  
    {  
        if (!Config.ENABLE_FALLING_DAMAGE || fallHeight < 0)  
        	return 0;  
        final double damage = cha.calcStat(Stats.FALL, fallHeight * cha.getMaxHp() / 1000, null, null);  
        return damage;  
    }  

	/** Calculated damage caused by ATTACK of attacker on target,
	 * called separatly for each weapon, if dual-weapon is used.
	 *
	 * @param attacker player or NPC that makes ATTACK
	 * @param target player or NPC, target of ATTACK
	 * @param miss one of ATTACK_XXX constants
	 * @param crit if the ATTACK have critical success
	 * @param dual if dual weapon is used
	 * @param ss if weapon item was charged by soulshot
	 * @return damage points
	 */
	public final static double calcPhysDam(L2Character attacker, L2Character target, L2Skill skill,
									boolean shld, boolean crit, boolean dual, boolean ss)
	{
		if (attacker instanceof L2PcInstance)
		{
			L2PcInstance pcInst = (L2PcInstance)attacker;
			if (pcInst.isGM() && pcInst.getAccessLevel() < Config.GM_CAN_GIVE_DAMAGE)
					return 0;
		}

		double damage = attacker.getPAtk(target);
		double defence = target.getPDef(attacker);
		if (ss) damage *= 2;
		if (skill != null)
		{
			double skillpower = skill.getPower();
			float ssboost = skill.getSSBoost();
			if (ssboost <= 0)
				damage += skillpower;
			else if (ssboost > 0)
			{
				if (ss)
				{
					skillpower *= ssboost;
					damage += skillpower;
				}
				else
					damage += skillpower;
			}
		}
		// In C5 summons make 10 % less dmg in PvP.
		if(attacker instanceof L2Summon && target instanceof L2PcInstance) damage *= 0.9;

		// defence modifier depending of the attacker weapon
		L2Weapon weapon = attacker.getActiveWeaponItem();
		Stats stat = null;
		if (weapon != null)
		{
			switch (weapon.getItemType())
			{
				case BOW:
					stat = Stats.BOW_WPN_VULN;
					break;
				case BLUNT:
				case BIGBLUNT:
					stat = Stats.BLUNT_WPN_VULN;
					break;
				case DAGGER:
					stat = Stats.DAGGER_WPN_VULN;
					break;
				case DUAL:
					stat = Stats.DUAL_WPN_VULN;
					break;
				case DUALFIST:
					stat = Stats.DUALFIST_WPN_VULN;
					break;
				case ETC:
					stat = Stats.ETC_WPN_VULN;
					break;
				case FIST:
					stat = Stats.FIST_WPN_VULN;
					break;
				case POLE:
					stat = Stats.POLE_WPN_VULN;
					break;
				case SWORD:
					stat = Stats.SWORD_WPN_VULN;
					break;
				case BIGSWORD:
					stat = Stats.SWORD_WPN_VULN;
					break;
			}
		}


		if (crit) 
            damage += (attacker.getCriticalDmg(target, damage) * target.calcStat(Stats.CRIT_VULN, target.getTemplate().baseCritVuln, target, skill != null ? skill : null)); 		if (shld && !Config.GAME_SHIELD_BLOCKS)
		{
			defence += target.getShldDef();
		}

		//if (!(attacker instanceof L2RaidBossInstance) &&
		/*
		if ((attacker instanceof L2NpcInstance || attacker instanceof L2SiegeGuardInstance))
		{
			if (attacker instanceof L2RaidBossInstance) damage *= 1; // was 10 changed for temp fix
			//			else
			//			damage *= 2;
			//			if (attacker instanceof L2NpcInstance || attacker instanceof L2SiegeGuardInstance){
			//damage = damage * attacker.getSTR() * attacker.getAccuracy() * 0.05 / defence;
			//			damage = damage * attacker.getSTR()*  (attacker.getSTR() + attacker.getLevel()) * 0.025 / defence;
			//			damage += _rnd.nextDouble() * damage / 10 ;
		}
		*/
		//		else {
		//if (skill == null)
		damage = 70 * damage / defence;

		if (stat != null)
		{
			// get the vulnerability due to skills (buffs, passives, toggles, etc)
			damage = target.calcStat(stat, damage, target, null);
			if (target instanceof L2NpcInstance)
			{
				// get the natural vulnerability for the template
				damage *= ((L2NpcInstance) target).getTemplate().getVulnerability(stat);
			}
		}

		damage += Rnd.nextDouble() * damage / 10;
		//		damage += _rnd.nextDouble()* attacker.getRandomDamage(target);
		//		}
		if (shld && Config.GAME_SHIELD_BLOCKS)
		{
			damage -= target.getShldDef();
			if (damage < 0) damage = 0;
		}

		if (attacker instanceof L2NpcInstance)
		{
			//Skill Race : Undead
			if (((L2NpcInstance) attacker).getTemplate().getRace() == L2NpcTemplate.Race.UNDEAD)
				damage /= attacker.getPDefUndead(target);
		}
		if (target instanceof L2NpcInstance)
		{
			switch (((L2NpcInstance) target).getTemplate().getRace())
			{
				case UNDEAD:
					damage *= attacker.getPAtkUndead(target);
					break;
				case BEAST:
					damage *= attacker.getPAtkMonsters(target);
					break;
				case ANIMAL:
					damage *= attacker.getPAtkAnimals(target);
					break;
				case PLANT:
					damage *= attacker.getPAtkPlants(target);
					break;
				case DRAGON:
					damage *= attacker.getPAtkDragons(target);
					break;
				case BUG:
					damage *= attacker.getPAtkInsects(target);
					break;
				case GIANT:  
                    damage *= attacker.getPAtkGiants(target);  
                    break;
				default:
					// nothing
					break;
			}
		}
        if (shld)
        {
            if (100 - Config.PERFECT_SHIELD_BLOCK < Rnd.get(100)) 
            {
                damage = 1;
                target.sendPacket(new SystemMessage(SystemMessageId.YOUR_EXCELLENT_SHIELD_DEFENSE_WAS_A_SUCCESS));
            }
        }
		if (damage > 0 && damage < 1)
		{
			damage = 1;
		}
		else if (damage < 0)
		{
			damage = 0;
		}
		
		// Dmg bonusses in PvP fight
		if((attacker instanceof L2PcInstance || attacker instanceof L2Summon)
				&& (target instanceof L2PcInstance || target instanceof L2Summon))
		{
			if(skill == null)
				damage *= attacker.calcStat(Stats.PVP_PHYSICAL_DMG, 1, null, null);
			else
				damage *= attacker.calcStat(Stats.PVP_PHYS_SKILL_DMG, 1, null, null);	
		}
                  		if (attacker instanceof L2PcInstance)
		{
			L2PcInstance pcInst = (L2PcInstance)attacker;
			if (pcInst.getClassId().getId() == Config.ALT_CLASSID)
			{
				if (Config.ALT_DAGGER)
				{
					L2Weapon wpn = pcInst.getActiveWeaponItem();
					if (wpn != null && wpn.getItemType() == L2WeaponType.DAGGER)
						damage = (int) (damage * Config.ALT_DAMAGE);
				}else if (Config.ALT_BOW)
				{
					L2Weapon wpn = pcInst.getActiveWeaponItem();
					if (wpn != null && wpn.getItemType() == L2WeaponType.BOW)
						damage = (int) (damage * Config.ALT_DAMAGE);
				}else if (Config.ALT_BLUNT)
				{
					L2Weapon wpn = pcInst.getActiveWeaponItem();
					if (wpn != null && wpn.getItemType() == L2WeaponType.BLUNT)
						damage = (int) (damage * Config.ALT_DAMAGE);
				}else if (Config.ALT_DUALFIST)
				{
					L2Weapon wpn = pcInst.getActiveWeaponItem();
					if (wpn != null && wpn.getItemType() == L2WeaponType.DUALFIST)
						damage = (int) (damage * Config.ALT_DAMAGE);
				}else if (Config.ALT_DUAL)
				{
					L2Weapon wpn = pcInst.getActiveWeaponItem();
					if (wpn != null && wpn.getItemType() == L2WeaponType.DUAL)
						damage = (int) (damage * Config.ALT_DAMAGE);
				}else if (Config.ALT_SWORD)
				{
					L2Weapon wpn = pcInst.getActiveWeaponItem();
					if (wpn != null && wpn.getItemType() == L2WeaponType.SWORD)
						damage = (int) (damage * Config.ALT_DAMAGE);
				}else if (Config.ALT_POLE)
				{
					L2Weapon wpn = pcInst.getActiveWeaponItem();
					if (wpn != null && wpn.getItemType() == L2WeaponType.POLE)
						damage = (int) (damage * Config.ALT_DAMAGE);
				}
			}
       }
		return damage;
	}

	public final static double calcMagicDam(L2Character attacker, L2Character target, L2Skill skill, boolean ss, boolean bss, boolean mcrit)
	{
		if (attacker instanceof L2PcInstance)
		{
			L2PcInstance pcInst = (L2PcInstance)attacker;
			if (pcInst.isGM() && pcInst.getAccessLevel() < Config.GM_CAN_GIVE_DAMAGE)
				return 0;
		}
		
		double mAtk = attacker.getMAtk(target, skill);
		double mDef = target.getMDef(attacker, skill);
		if (bss) mAtk *= 4;
		else if (ss) mAtk *= 2;
		double damage = 91 * Math.sqrt(mAtk) / mDef * skill.getPower(attacker) * calcSkillVulnerability(target, skill);
				
		if (skill != null && skill.getElement() != 0)
		{
			if (skill.getElement() == 1)
			{
				damage *= attacker.calcStat(Stats.WIND_POWER, 1, null, null);
			}
			else if (skill.getElement() == 2)
			{
				damage *= attacker.calcStat(Stats.FIRE_POWER, 1, null, null);
			}
			else if (skill.getElement() == 3)
			{
				damage *= attacker.calcStat(Stats.WATER_POWER, 1, null, null);
			}
			else if (skill.getElement() == 4)
			{
				damage *= attacker.calcStat(Stats.EARTH_POWER, 1, null, null);
			}
			else if (skill.getElement() == 5)
			{
				damage *= attacker.calcStat(Stats.HOLY_POWER, 1, null, null);
			}
			else if (skill.getElement() == 6)
			{
				damage *= attacker.calcStat(Stats.DARK_POWER, 1, null, null);
			}
		}

		if(attacker instanceof L2Summon && target instanceof L2PcInstance) damage *= 0.9;

		if (Config.GAME_MAGICFAILURES && !calcMagicSuccess(attacker, target, skill))
		{
			if (attacker instanceof L2PcInstance)
			{
				if (calcMagicSuccess(attacker, target, skill) && (target.getLevel() - attacker.getLevel()) <= 9)
				{
					if (skill.getSkillType() == SkillType.DRAIN)
						attacker.sendPacket(new SystemMessage(SystemMessageId.DRAIN_HALF_SUCCESFUL));
					else
						attacker.sendPacket(new SystemMessage(SystemMessageId.ATTACK_FAILED));

					damage /= 2;
				}
				else
				{
					SystemMessage sm = new SystemMessage(SystemMessageId.S1_WAS_UNAFFECTED_BY_S2);
					sm.addString(target.getName());
					sm.addSkillName(skill.getId());
					attacker.sendPacket(sm);

					damage = 1;
				}
			}

			if (target instanceof L2PcInstance)
			{
				if (skill.getSkillType() == SkillType.DRAIN)
				{
					SystemMessage sm = new SystemMessage(SystemMessageId.RESISTED_S1_DRAIN);
					sm.addString(attacker.getName());
					target.sendPacket(sm);
				}
				else
				{
					SystemMessage sm = new SystemMessage(SystemMessageId.RESISTED_S1_MAGIC);
					sm.addString(attacker.getName());
					target.sendPacket(sm);
				}
			}
		}
		else if (mcrit)
			damage *= 3;

		if (attacker instanceof L2PcInstance && target instanceof L2PcInstance) 
			damage *= 2.5; 
		else 
			damage *= 3; 

		if((attacker instanceof L2PcInstance || attacker instanceof L2Summon) && (target instanceof L2PcInstance || target instanceof L2Summon))
		{
			if(skill.isMagic())
				damage *= attacker.calcStat(Stats.PVP_MAGICAL_DMG, 1, null, null);
			else
				damage *= attacker.calcStat(Stats.PVP_PHYS_SKILL_DMG, 1, null, null);
		}

 		if (skill != null)
         {
 			if (target instanceof L2PlayableInstance)
 				damage *= skill.getPvpMulti();
 			
             if (skill.getSkillType() == SkillType.DEATHLINK)
                 damage = damage * (1.0 - attacker.getStatus().getCurrentHp()/attacker.getMaxHp()) * 2.0;
         }

		return damage;
	}

	/** Returns true in case of critical hit */
	public final static boolean calcCrit(double rate)
	{
		return rate > Rnd.get(1000);
	}
	/** Calcul value of blow success */
	public final boolean calcBlow(L2Character activeChar, L2Character target, int chance)
	{
		return activeChar.calcStat(Stats.BLOW_RATE, chance*(1.0+(activeChar.getDEX()-20)/100), target, null)>Rnd.get(100);
	}
	/** Calcul value of lethal chance */
	public final double calcLethal(L2Character activeChar, L2Character target, int baseLethal)
	{
		return activeChar.calcStat(Stats.LETHAL_RATE, (baseLethal*((double)activeChar.getLevel()/target.getLevel())), target, null);
	}
	
	public final boolean calcLethalHit(L2Character activeChar, L2Character target, L2Skill skill) 
	{ 
		if((target.isRaid() && Config.ALLOW_RAID_LETHAL) 
				|| (!target.isRaid() && !(target instanceof L2DoorInstance) 
				&& !(Config.ALLOW_LETHAL_PROTECTION_MOBS && target instanceof L2NpcInstance 
				&& (Config.LIST_LETHAL_PROTECTED_MOBS.contains(((L2NpcInstance) target).getNpcId())))))

		if ((!target.isRaid() || Config.ALLOW_RAID_LETHAL)
				&& !(target instanceof L2DoorInstance)
				&& !(target instanceof L2NpcInstance && ((L2NpcInstance) target).getNpcId() == 35062)
				&& !(Config.ALLOW_LETHAL_PROTECTION_MOBS && target instanceof L2NpcInstance && (Config.LIST_LETHAL_PROTECTED_MOBS.contains(((L2NpcInstance) target).getNpcId()))))
		{ 
			int chance = Rnd.get(100); 
			// 2nd lethal effect activate (cp,hp to 1 or if target is npc then hp to 1) 
			if (skill.getLethalChance2() > 0 && chance < calcLethal(activeChar, target, skill.getLethalChance2())) 
			{ 
				if (target instanceof L2NpcInstance) 
					target.reduceCurrentHp(target.getCurrentHp() - 1, activeChar); 
				else if (target instanceof L2PcInstance) // If is a active player set his HP and CP to 1 
				{ 
					L2PcInstance player = (L2PcInstance) target; 
					if (!player.isInvul()) 
					{ 
						player.setCurrentHp(1); 
						player.setCurrentCp(1); 
					} 
				} 
				activeChar.sendPacket(new SystemMessage(SystemMessageId.LETHAL_STRIKE)); 
			} 
			else if (skill.getLethalChance1() > 0 && chance < calcLethal(activeChar, target, skill.getLethalChance1())) 
			{ 
				if (target instanceof L2PcInstance) 
				{ 
					L2PcInstance player = (L2PcInstance) target; 
					if (!player.isInvul()) 
						player.setCurrentCp(1); // Set CP to 1 
				} 
				else if (target instanceof L2NpcInstance) // If is a monster remove first damage and after 50% of current hp 
					target.reduceCurrentHp(target.getCurrentHp() / 2, activeChar); 
				activeChar.sendPacket(new SystemMessage(SystemMessageId.LETHAL_STRIKE));
			}
			else
				return false;
		}
		else
			return false;

		return true;
	}
 		    
	public final boolean calcMCrit(double mRate)
	{
		return mRate > Rnd.get(1000);
	}

	/** Returns true in case when ATTACK is canceled due to hit */
	public final boolean calcAtkBreak(L2Character target, double dmg)
	{
		double init = 0;

		if (Config.GAME_CANCEL_CAST && target.isCastingNow()) init = 50;
		if (Config.GAME_CANCEL_BOW && target.isAttackingNow())
		{
			L2Weapon wpn = target.getActiveWeaponItem();
			if (wpn != null && wpn.getItemType() == L2WeaponType.BOW) init = 15;
		}

        if (init <= 0) return false; // No attack break

        // Chance of break is higher with higher dmg
        init += Math.sqrt(13*dmg);  

        // Chance is affected by target MEN
        init -= (BaseStats.MEN.calcBonus(target) * 100 - 100);

        // Calculate all modifiers for ATTACK_CANCEL
        double rate = target.calcStat(Stats.ATTACK_CANCEL, init, null, null); 

        // Adjust the rate to be between 1 and 99
        if (rate > 99) rate = 99;
        else if (rate < 1) rate = 1;

        return Rnd.get(100) < rate;
	}

	/** Calculate delay (in milliseconds) before next ATTACK */
	public final int calcPAtkSpd(L2Character attacker, L2Character target, double rate)
	{
		// measured Oct 2006 by Tank6585, formula by Sami
		// attack speed 312 equals 1500 ms delay... (or 300 + 40 ms delay?)
		if(rate < 2) return 2700;
	    else return (int)(470000/rate);
	}

	/** Calculate delay (in milliseconds) for skills cast */
	public final int calcMAtkSpd(L2Character attacker, L2Character target, L2Skill skill, double skillTime)
	{
		if (skill.isMagic()) return (int) (skillTime * 333 / attacker.getMAtkSpd());
		return (int) (skillTime * 333 / attacker.getPAtkSpd());
	}

	/** Calculate delay (in milliseconds) for skills cast */
	public final int calcMAtkSpd(L2Character attacker, L2Skill skill, double skillTime)
	{
		if (skill.isMagic()) return (int) (skillTime * 333 / attacker.getMAtkSpd());
		return (int) (skillTime * 333 / attacker.getPAtkSpd());
	}

	/** Returns true if hit missed (taget evaded) */
	public boolean calcHitMiss(L2Character attacker, L2Character target)
	{
		// accuracy+dexterity => probability to hit in percents
		int acc_attacker;
		int evas_target;
		acc_attacker = attacker.getAccuracy();
		evas_target = target.getEvasionRate(attacker);
     	int d = 85 + acc_attacker - evas_target;
		return d < Rnd.get(100);
	}

	/** Returns true if shield defence successfull */
	public static boolean calcShldUse(L2Character attacker, L2Character target)
	{
		L2Weapon at_weapon = attacker.getActiveWeaponItem();
		double shldRate = target.calcStat(Stats.SHIELD_RATE, 0, attacker, null)
				* BaseStats.DEX.calcBonus(target);
		if (shldRate == 0.0) return false;
        int degreeside = (int)target.calcStat(Stats.SHIELD_DEFENCE_ANGLE, 0, null, null) + 120;
        if (degreeside < 360 && (!target.isFacing(attacker, degreeside)))
            return false;
		// if attacker use bow and target wear shield, shield block rate is multiplied by 1.3 (30%)
		if (at_weapon != null && at_weapon.getItemType() == L2WeaponType.BOW)
			shldRate *= 1.3;
		return shldRate > Rnd.get(100);
	}

	public boolean calcMagicAffected(L2Character actor, L2Character target, L2Skill skill)
	{
		SkillType type = skill.getSkillType();
		if (target.isRaid()
				&& (type == SkillType.CONFUSION || type == SkillType.MUTE || type == SkillType.PARALYZE
					|| type == SkillType.ROOT || type == SkillType.FEAR || type == SkillType.SLEEP
					|| type == SkillType.STUN || type == SkillType.DEBUFF || type == SkillType.AGGDEBUFF)
                    || Config.FORBIDDEN_RAID_SKILLS_LIST.contains(skill.getId()))
			return false; // these skills should have only 1/1000 chance on raid, now it's 0.

		double defence = 0;
		if (skill.isActive() && skill.isOffensive()) defence = target.getMDef(actor, skill);
		double attack = 2 * actor.getMAtk(target, skill) * calcSkillVulnerability(target, skill);
		double d = attack - defence;
		d /= attack + defence;

		if (target.calcStat(Stats.DEBUFF_IMMUNITY, 0, null, skill) > 0 && skill.is_Debuff())
			return false;

		d += 0.5 * Rnd.nextGaussian();
		return d > 0;
	}

	public static double calcSkillVulnerability(L2Character target, L2Skill skill)
	{
		double multiplier = 1;	// initialize...

		// Get the skill type to calculate its effect in function of base stats
		// of the L2Character target
		if (skill != null)
		{
			// first, get the natural template vulnerability values for the target
			Stats stat = skill.getStat();
			if (stat != null)
			{
				switch (stat)
				{
				case AGGRESSION:
					multiplier *= target.getTemplate().baseAggressionVuln;
					break;
				case BLEED:
					multiplier *= target.getTemplate().baseBleedVuln;
					break;
				case POISON:
					multiplier *= target.getTemplate().basePoisonVuln;
					break;
				case STUN:
					multiplier *= target.getTemplate().baseStunVuln;
					break;
				case ROOT:
					multiplier *= target.getTemplate().baseRootVuln;
					break;
				case MOVEMENT:
					multiplier *= target.getTemplate().baseMovementVuln;
					break;
				case CONFUSION:
					multiplier *= target.getTemplate().baseConfusionVuln;
					break;
				case SLEEP:
					multiplier *= target.getTemplate().baseSleepVuln;
					break;
				case FIRE:
					multiplier *= target.getTemplate().baseFireVuln;
					break;
				case WIND:
					multiplier *= target.getTemplate().baseWindVuln;
					break;
				case WATER:
					multiplier *= target.getTemplate().baseWaterVuln;
					break;
				case EARTH:
					multiplier *= target.getTemplate().baseEarthVuln;
					break;
				case HOLY:
					multiplier *= target.getTemplate().baseHolyVuln;
					break;
				case DARK:
					multiplier *= target.getTemplate().baseDarkVuln;
					break;
				}
			}

			// Next, calculate the elemental vulnerabilities
			switch (skill.getElement())
			{
			case L2Skill.ELEMENT_EARTH:
				multiplier = target.calcStat(Stats.EARTH_VULN, multiplier, target, skill);
				break;
			case L2Skill.ELEMENT_FIRE:
				multiplier = target.calcStat(Stats.FIRE_VULN, multiplier, target, skill);
				break;
			case L2Skill.ELEMENT_WATER:
				multiplier = target.calcStat(Stats.WATER_VULN, multiplier, target, skill);
				break;
			case L2Skill.ELEMENT_WIND:
				multiplier = target.calcStat(Stats.WIND_VULN, multiplier, target, skill);
				break;
			case L2Skill.ELEMENT_HOLY:
				multiplier = target.calcStat(Stats.HOLY_VULN, multiplier, target, skill);
				break;
			case L2Skill.ELEMENT_DARK:
				multiplier = target.calcStat(Stats.DARK_VULN, multiplier, target, skill);
				break;
			}
			
			// Finally, calculate skilltype vulnerabilities
			SkillType type = skill.getSkillType();
			
			// For additional effects on PDAM and MDAM skills (like STUN, SHOCK, PARALYZE...)
			if (type != null && (type == SkillType.PDAM || type == SkillType.MDAM))
				type = skill.getEffectType();

			if (type != null)
			{
				switch (type)
				{
					case BLEED:
						multiplier = target.calcStat(Stats.BLEED_VULN, multiplier, target, null);
						break;
					case POISON:
						multiplier = target.calcStat(Stats.POISON_VULN, multiplier, target, null);
						break;
					case STUN:
						multiplier = target.calcStat(Stats.STUN_VULN, multiplier, target, null);
						break;
					case PARALYZE:
						multiplier = target.calcStat(Stats.PARALYZE_VULN, multiplier, target, null);
						break;
					case ROOT:
						multiplier = target.calcStat(Stats.ROOT_VULN, multiplier, target, null);
						break;
					case SLEEP:
						multiplier = target.calcStat(Stats.SLEEP_VULN, multiplier, target, null);
						break;
					case MUTE:
					case FEAR:
					case BETRAY:
					case AGGREDUCE_CHAR:
						multiplier = target.calcStat(Stats.DERANGEMENT_VULN, multiplier, target, null);
						break;
					case CONFUSION:
					case CONFUSE_MOB_ONLY:
						multiplier = target.calcStat(Stats.CONFUSION_VULN, multiplier, target, null);
						break;
					case DEBUFF:
					case WEAKNESS:
						multiplier = target.calcStat(Stats.DEBUFF_VULN, multiplier, target, null);
						break;
                    case BUFF: 
                        multiplier = target.calcStat(Stats.BUFF_VULN, multiplier, target, null); 
                        break; 
					default:
						;
				}
			}
			
		}
		return multiplier;
	}

	public double calcSkillStatModifier(SkillType type, L2Character target)
	{
		double multiplier = 1;
		if (type == null) return multiplier;
		switch (type)
		{
			case STUN:
			case BLEED:
				multiplier = 2 - Math.sqrt(BaseStats.CON.calcBonus(target));
				break;
			case POISON:
			case SLEEP:
			case DEBUFF:
			case WEAKNESS:
			case ERASE:
			case ROOT:
			case MUTE:
			case FEAR:
			case BETRAY:
			case CONFUSION:
			case CONFUSE_MOB_ONLY:
			case AGGREDUCE_CHAR:
			case PARALYZE:
				multiplier = 2 - Math.sqrt(BaseStats.MEN.calcBonus(target));
				break;
			default:
				return multiplier;
		}
		if (multiplier < 0)
			multiplier = 0;
		return multiplier;
	}

	public boolean calcSkillSuccess(L2Character attacker, L2Character target, L2Skill skill, boolean ss, boolean sps, boolean bss)
	{
        if (skill.ignoreResists()) 
        { 
        	return (Rnd.get(100) < skill.getPower()); 
        } 
	 
		SkillType type = skill.getSkillType();

		if (target.calcStat(Stats.DEBUFF_IMMUNITY, 0, null, skill) > 0 && skill.is_Debuff())
			return false;
		
		if (target.isRaid()
			&& (type == SkillType.CONFUSION || type == SkillType.MUTE || type == SkillType.PARALYZE
				|| type == SkillType.ROOT || type == SkillType.FEAR || type == SkillType.SLEEP
				|| type == SkillType.STUN || type == SkillType.DEBUFF || type == SkillType.AGGDEBUFF)
				||  Config.FORBIDDEN_RAID_SKILLS_LIST.contains(skill.getId()))
			return false; // these skills should not work on RaidBoss

		if(target.isInvul() 
				&& (type == SkillType.CONFUSION || type == SkillType.MUTE || type == SkillType.PARALYZE 
				|| type == SkillType.ROOT || type == SkillType.FEAR || type == SkillType.SLEEP 
				|| type == SkillType.STUN || type == SkillType.DEBUFF || type == SkillType.CANCEL 
				|| type == SkillType.NEGATE || type == SkillType.WARRIOR_BANE || type == SkillType.MAGE_BANE))
			return false; // these skills should not work on Invulable persons

		int value = (int) skill.getPower();
		int lvlDepend = skill.getLevelDepend();

		if (type == SkillType.PDAM || type == SkillType.MDAM) // For additional effects on PDAM skills (like STUN, SHOCK,...)
		{
			value = skill.getEffectPower();
			type = skill.getEffectType();
		}
		if (value == 0 || type == null)
		{
			if (skill.getSkillType() == SkillType.PDAM)
			{
				value = 50;
				type = SkillType.STUN;
			}
			if (skill.getSkillType() == SkillType.MDAM)
			{
				value = 30;
				type = SkillType.PARALYZE;
			}
		}

		if (value == 0) value = (type == SkillType.PARALYZE) ? 50 : (type == SkillType.FEAR)? 40 : 80;
		if (lvlDepend == 0) lvlDepend = (type == SkillType.PARALYZE || type == SkillType.FEAR) ? 1 : 2;

		// int lvlmodifier = (skill.getMagicLevel() - target.getLevel()) * lvlDepend;
		int lvlmodifier = ((skill.getMagicLevel() > 0 ? skill.getMagicLevel() : attacker.getLevel()) - target.getLevel())
			* lvlDepend;
		double statmodifier = calcSkillStatModifier(type, target);
		double resmodifier = calcSkillVulnerability(target, skill);

		int ssmodifier = 100;
		if (bss) ssmodifier = 150;
		else if (sps || ss) ssmodifier = 125;

		// Calculate BaseRate.
		int rate = (int) ((value * statmodifier + lvlmodifier));
		if (skill.isMagic())
			  rate = (int) (rate * Math.pow((double) attacker.getMAtk(target, skill) / target.getMDef(attacker, skill), 0.2)); 
 
         // Add Bonus for Sps/SS 
         if (ssmodifier != 100) 
         { 
                if (rate > 10000 / (100 + ssmodifier)) rate = 100 - (100 - rate) * 100 / ssmodifier; 
                else rate = rate * ssmodifier / 100; 
        } 
		if (rate > 99) rate = 99;
		else if (rate < 1) rate = 1;

		//Finaly apply resists. 
        rate *= resmodifier; 

		if (Config.DEVELOPER)
			System.out.println(skill.getName()
				+ ": "
				+ value
				+ ", "
				+ statmodifier
				+ ", "
				+ lvlmodifier
				+ ", "
				+ resmodifier
				+ ", "
				+ ((int) (Math.pow((double) attacker.getMAtk(target, skill)
					/ target.getMDef(attacker, skill), 0.2) * 100) - 100) + ", " + ssmodifier + " ==> "
				+ rate);
		return (Rnd.get(100) < rate);
	}

	public static boolean calcMagicSuccess(L2Character attacker, L2Character target, L2Skill skill)
	{
		double lvlDifference = (target.getLevel() - (skill.getMagicLevel() > 0 ? skill.getMagicLevel() : attacker.getLevel()));
		int rate = Math.round((float)(Math.pow(1.3, lvlDifference) * 100));

		return (Rnd.get(10000) > rate);
	}

	public static boolean calcPhysicalSkillEvasion(L2Character target, L2Skill skill)
	{
		if (skill.isMagic() && skill.getSkillType() != SkillType.BLOW)
			return false;
		
		return Rnd.get(100) < target.calcStat(Stats.P_SKILL_EVASION, 0, null, skill);
	}
	
	public boolean calculateUnlockChance(L2Skill skill)
	{
		int level = skill.getLevel();
		int chance = 0;
		switch (level)
		{
			case 1:
				chance = 30;
				break;

			case 2:
				chance = 50;
				break;

			case 3:
				chance = 75;
				break;

			case 4:
			case 5:
			case 6:
			case 7:
			case 8:
			case 9:
			case 10:
			case 11:
			case 12:
			case 13:
			case 14:
				chance = 100;
				break;
		}
		if (Rnd.get(120) > chance)		
			return false;
		return true;
	}
	
    public double calcManaDam(L2Character attacker, L2Character target, L2Skill skill,
			boolean ss, boolean bss)
    {
    	//Mana Burnt = (SQR(M.Atk)*Power*(Target Max MP/97))/M.Def
    	double mAtk = attacker.getMAtk(target, skill);
		double mDef = target.getMDef(attacker, skill);
		double mp = target.getMaxMp();
		if (bss) mAtk *= 4;
		else if (ss) mAtk *= 2;

		double damage = (Math.sqrt(mAtk) * skill.getPower(attacker) * (mp/97)) / mDef;
		damage *= calcSkillVulnerability(target, skill);		
    	return damage;
    }
    
    public double calculateSkillResurrectRestorePercent(double baseRestorePercent, L2Character caster)
	{
		double restorePercent = baseRestorePercent;
		double modifier = BaseStats.WIT.calcBonus(caster);

		if(restorePercent != 100 && restorePercent != 0)
		{
			restorePercent = baseRestorePercent * modifier;
			if(restorePercent - baseRestorePercent > 20.0) 
				restorePercent = baseRestorePercent + 20.0;
		}
		if(restorePercent > 50) 
			restorePercent = 50;
		if(restorePercent < baseRestorePercent) 
			restorePercent = baseRestorePercent;
		return restorePercent;
	}

    public double getSTRBonus(L2Character activeChar)
    {
    	return BaseStats.STR.calcBonus(activeChar);
    }

	public boolean calcSkillMastery(L2Character actor, L2Skill sk) 
	{ 
	if (sk.getSkillType() == SkillType.FISHING) 
		return false; 
	double val = actor.getStat().calcStat(Stats.SKILL_MASTERY, 0, null, null); 

	if (actor instanceof L2PcInstance && ((L2PcInstance)actor).isMageClass()) 
		val *= BaseStats.INT.calcBonus(actor); 
	else 
	val*= BaseStats.STR.calcBonus(actor); 

	return Rnd.get(100) < val; 
	}

	/**
	* @return
	*/
	public static Calculator[] getStdDoorCalculators()
	{
	return null;
	} 
}
