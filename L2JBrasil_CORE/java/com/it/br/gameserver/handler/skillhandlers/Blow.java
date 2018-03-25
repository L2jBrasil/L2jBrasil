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
package com.it.br.gameserver.handler.skillhandlers;

import com.it.br.Config;
import com.it.br.gameserver.handler.ISkillHandler;
import com.it.br.gameserver.model.*;
import com.it.br.gameserver.model.L2Skill.SkillType;
import com.it.br.gameserver.model.actor.instance.L2PcInstance;
import com.it.br.gameserver.network.SystemMessageId;
import com.it.br.gameserver.network.serverpackets.SystemMessage;
import com.it.br.gameserver.skills.Env;
import com.it.br.gameserver.skills.Formulas;
import com.it.br.gameserver.skills.funcs.Func;
import com.it.br.gameserver.templates.L2WeaponType;

/**
 *
 * @author  Steuf
 */
public class Blow implements ISkillHandler
{
	private static final SkillType[] SKILL_IDS = {SkillType.BLOW};

	private int _successChance;
	public static int FRONT = Config.FRONT_BLOW_CHANCE;
	public static int SIDE = Config.SIDE_BLOW_CHANCE;
	public static int BEHIND = Config.BEHIND_BLOW_CHANCE;

	public void useSkill(L2Character activeChar, L2Skill skill, L2Object[] targets)
	{
		if(activeChar.isAlikeDead())
			return;
        for(int index = 0;index < targets.length;index++)
        {
			L2Character target = (L2Character)targets[index];
			if(Config.BACKSTABRESTRICTION && skill.getId() == 30)
			{
				FRONT = 0;
				SIDE = 0;
				BEHIND = 70;
			}

			if(target.isAlikeDead())
				continue;
			
			Formulas.getInstance();
			// Check firstly if target dodges skill
			final boolean skillIsEvaded = Formulas.calcPhysicalSkillEvasion(target, skill);
			
			_successChance = SIDE;
			
			if(activeChar.isBehindTarget())
				_successChance = BEHIND;
			else if(activeChar.isFrontTarget())
				_successChance = FRONT;
			
			//If skill requires Crit or skill requires behind,
			//calculate chance based on DEX, Position and on self BUFF
			boolean success = true;
			if ((skill.getCondition() & L2Skill.COND_BEHIND) != 0)
				success = (_successChance == BEHIND);
			
			if ((skill.getCondition() & L2Skill.COND_CRIT) != 0)
				success = Formulas.getInstance().calcBlow(activeChar, target, _successChance);

			if (!skillIsEvaded && success)
			{
				if (skill.hasEffects())
				{
					if (target.reflectSkill(skill))
					{
						activeChar.stopSkillEffects(skill.getId());
						skill.getEffects(null, activeChar);
						SystemMessage sm = new SystemMessage(SystemMessageId.YOU_FEEL_S1_EFFECT);
						sm.addSkillName(skill.getId());
						activeChar.sendPacket(sm);
					}
				}
				
	            L2ItemInstance weapon = activeChar.getActiveWeaponInstance();
	            boolean soul = (weapon != null && weapon.getChargedSoulshot() == L2ItemInstance.CHARGED_SOULSHOT && weapon.getItemType() == L2WeaponType.DAGGER);
	            boolean shld = Formulas.calcShldUse(activeChar, target);

	            // Crit rate base crit rate for skill, modified with STR bonus
	            boolean crit = false;
				if(Formulas.calcCrit(skill.getBaseCritRate()*10*Formulas.getInstance().getSTRBonus(activeChar)))
					crit = true;
				double damage = (int)Formulas.getInstance().calcBlowDamage(activeChar, target, skill, shld, soul);
				if (crit)
				{
					damage *= 2;
					// Vicious Stance is special after C5, and only for BLOW skills
					// Adds directly to damage
					L2Effect vicious = activeChar.getFirstEffect(312);
					if(vicious != null && damage > 1)
					{
						for(Func func: vicious.getStatFuncs())
						{
							Env env = new Env();
							env.player = activeChar;
							env.target = target;
							env.skill = skill;
							env.value = damage;
							func.calc(env);
							damage = (int)env.value;
						}
					}
				}

				if (soul && weapon != null)
	            	weapon.setChargedSoulshot(L2ItemInstance.CHARGED_NONE);
				if(skill.getDmgDirectlyToHP() && target instanceof L2PcInstance)
	        	{
					L2PcInstance player = (L2PcInstance)target;
	        		if (!player.isInvul())
					{
	        	       if (damage >= player.getCurrentHp())
	        	       {
	        	    	   if(player.isInDuel()) player.setCurrentHp(1);
	        	    	   else
	        	    	   {
	        	    		   player.setCurrentHp(0);
	        	    		   if (player.isInOlympiadMode())
	        	    		   {
	        	    			   player.abortAttack();
	        	    			   player.abortCast();
	        	    			   player.getStatus().stopHpMpRegeneration();
	        	    		   }
	        	    		   else
	        	    			   player.doDie(activeChar);
	        	    	   }
	        	       }
	        	       else
	        		      player.setCurrentHp(player.getCurrentHp() - damage);
					}
	        		SystemMessage smsg = new SystemMessage(SystemMessageId.S1_HIT_YOU_S2_DMG);
	        		smsg.addString(activeChar.getName());
	        		smsg.addNumber((int)damage);
	        		player.sendPacket(smsg);
	        	}
	        	else
	        		target.reduceCurrentHp(damage, activeChar);
				if(activeChar instanceof L2PcInstance)
					activeChar.sendPacket(new SystemMessage(SystemMessageId.CRITICAL_HIT));
				SystemMessage sm = new SystemMessage(SystemMessageId.YOU_DID_S1_DMG);
	            sm.addNumber((int)damage);
	            activeChar.sendPacket(sm);
			}
			//Possibility of a lethal strike
			Formulas.getInstance().calcLethalHit(activeChar, target, skill);
            L2Effect effect = activeChar.getFirstEffect(skill.getId());
            //Self Effect
            if (effect != null && effect.isSelfEffect())
            	effect.exit();
            skill.getEffectsSelf(activeChar);
        }
	}

	public SkillType[] getSkillIds()
    {
        return SKILL_IDS;
    }
}
