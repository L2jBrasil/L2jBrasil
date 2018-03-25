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
package com.it.br.gameserver.handler.skillhandlers;

import com.it.br.gameserver.ai.CtrlEvent;
import com.it.br.gameserver.ai.CtrlIntention;
import com.it.br.gameserver.handler.ISkillHandler;
import com.it.br.gameserver.instancemanager.DuelManager;
import com.it.br.gameserver.model.*;
import com.it.br.gameserver.model.L2Skill.SkillType;
import com.it.br.gameserver.model.actor.instance.L2DoorInstance;
import com.it.br.gameserver.model.actor.instance.L2PcInstance;
import com.it.br.gameserver.model.actor.instance.L2PlayableInstance;
import com.it.br.gameserver.network.SystemMessageId;
import com.it.br.gameserver.network.serverpackets.SystemMessage;
import com.it.br.gameserver.skills.Formulas;
import com.it.br.gameserver.skills.Stats;

/**
 * This class ...
 *
 * @version $Revision: 1.1.2.2.2.9 $ $Date: 2005/04/03 15:55:04 $
 */

public class Continuous implements ISkillHandler
{
	//private static Logger _log = LoggerFactory.getLogger(Continuous.class);

	private static final SkillType[] SKILL_IDS = 
	{
		L2Skill.SkillType.BUFF,
		L2Skill.SkillType.DEBUFF,
		L2Skill.SkillType.DOT,
		L2Skill.SkillType.MDOT,
		L2Skill.SkillType.POISON,
		L2Skill.SkillType.BLEED,
		L2Skill.SkillType.HOT,
		L2Skill.SkillType.CPHOT,
		L2Skill.SkillType.MPHOT,
		//L2Skill.SkillType.MANAHEAL,
		//L2Skill.SkillType.MANA_BY_LEVEL,
		L2Skill.SkillType.FEAR,
		L2Skill.SkillType.CONT,
		L2Skill.SkillType.WEAKNESS,
		L2Skill.SkillType.REFLECT,
		L2Skill.SkillType.UNDEAD_DEFENSE,
		L2Skill.SkillType.AGGDEBUFF,
		L2Skill.SkillType.FORCE_BUFF
		};

	/* (non-Javadoc)
	 * @see com.it.br.gameserver.handler.IItemHandler#useItem(com.it.br.gameserver.model.L2PcInstance, com.it.br.gameserver.model.L2ItemInstance)
	 */

	public void useSkill(L2Character activeChar, L2Skill skill, L2Object[] targets)
	{
		L2Character target = null;

		L2PcInstance player = null;
		if (activeChar instanceof L2PcInstance)
			player = (L2PcInstance)activeChar;

        for(int index = 0;index < targets.length;index++)
        {
            target = (L2Character)targets[index];

            if(skill.getSkillType() != L2Skill.SkillType.BUFF && skill.getSkillType() != L2Skill.SkillType.HOT
            		&& skill.getSkillType() != L2Skill.SkillType.CPHOT && skill.getSkillType() != L2Skill.SkillType.MPHOT
            		&& skill.getSkillType() != L2Skill.SkillType.UNDEAD_DEFENSE && skill.getSkillType() != L2Skill.SkillType.AGGDEBUFF
            		&& skill.getSkillType() != L2Skill.SkillType.CONT)
            {
                if(target.reflectSkill(skill))
                	target = activeChar;
            }

            // Walls and Door should not be buffed
            if(target instanceof L2DoorInstance && (skill.getSkillType() == L2Skill.SkillType.BUFF || skill.getSkillType() == L2Skill.SkillType.HOT))
            	continue;

			if (target.calcStat(Stats.BUFF_IMMUNITY, 0, null, skill) > 0
					&& (skill.getSkillType() == L2Skill.SkillType.BUFF
					|| skill.getSkillType() == L2Skill.SkillType.HEAL_PERCENT
					|| skill.getSkillType() == L2Skill.SkillType.FORCE_BUFF
					|| skill.getSkillType() == L2Skill.SkillType.MANAHEAL_PERCENT
					|| skill.getSkillType() == L2Skill.SkillType.COMBATPOINTHEAL
					|| skill.getSkillType() == L2Skill.SkillType.REFLECT))
				continue;

			// Anti-Buff Protection prevents you from getting buffs by other players
           if (activeChar instanceof L2PlayableInstance && target != activeChar && target.isBuffProtected() && !skill.isHeroSkill()
           && (skill.getSkillType() == L2Skill.SkillType.BUFF
           || skill.getSkillType() == L2Skill.SkillType.HEAL_PERCENT
           || skill.getSkillType() == L2Skill.SkillType.FORCE_BUFF
           || skill.getSkillType() == L2Skill.SkillType.MANAHEAL_PERCENT
           || skill.getSkillType() == L2Skill.SkillType.COMBATPOINTHEAL
           || skill.getSkillType() == L2Skill.SkillType.REFLECT))
               continue;

			// Player holding a cursed weapon can't be buffed and can't buff
            if (skill.getSkillType() == L2Skill.SkillType.BUFF)
            {
	            if (target != activeChar)
	            {
	            	if (target instanceof L2PcInstance && ((L2PcInstance)target).isCursedWeaponEquipped())
	            		continue;
	            	else if (player != null && player.isCursedWeaponEquipped())
	            		continue;
	            }
            }

			if (skill.isOffensive())
			{
				boolean ss = false;
		        boolean sps = false;
		        boolean bss = false;
		        if (player != null)
		        {
		        	L2ItemInstance weaponInst = activeChar.getActiveWeaponInstance();
		        	if (weaponInst != null)
		        	{
		        		if (skill.isMagic())
		        		{
			        		if (weaponInst.getChargedSpiritshot() == L2ItemInstance.CHARGED_BLESSED_SPIRITSHOT)
			                {
			                    bss = true;
			                    if (skill.getId() != 1020) // vitalize
			                    	weaponInst.setChargedSpiritshot(L2ItemInstance.CHARGED_NONE);
			                }
			                else if (weaponInst.getChargedSpiritshot() == L2ItemInstance.CHARGED_SPIRITSHOT)
			                {
			                    sps = true;
			                    if (skill.getId() != 1020) // vitalize
			                    	weaponInst.setChargedSpiritshot(L2ItemInstance.CHARGED_NONE);
			                }
		        		}
		                else
		                	if (weaponInst.getChargedSoulshot() == L2ItemInstance.CHARGED_SOULSHOT)
			                {
			                    ss = true;
			                    if (skill.getId() != 1020) // vitalize
			                    	weaponInst.setChargedSoulshot(L2ItemInstance.CHARGED_NONE);
			                }
		        	}
		        }
		        else if (activeChar instanceof L2Summon)
		        {
		            L2Summon activeSummon = (L2Summon) activeChar;
	        		if (skill.isMagic())
	        		{
			            if (activeSummon.getChargedSpiritShot() == L2ItemInstance.CHARGED_BLESSED_SPIRITSHOT)
			            {
			                bss = true;
			                activeSummon.setChargedSpiritShot(L2ItemInstance.CHARGED_NONE);
			            }
			            else if (activeSummon.getChargedSpiritShot() == L2ItemInstance.CHARGED_SPIRITSHOT)
			            {
			                sps = true;
			                activeSummon.setChargedSpiritShot(L2ItemInstance.CHARGED_NONE);
			            }
	        		}
		            else
		            	if (activeSummon.getChargedSoulShot() == L2ItemInstance.CHARGED_SOULSHOT)
			            {
			                ss = true;
			                activeSummon.setChargedSoulShot(L2ItemInstance.CHARGED_NONE);
			            }
		        }

				boolean acted = Formulas.getInstance().calcSkillSuccess(activeChar, target, skill, ss, sps, bss);
				if (!acted) {
					activeChar.sendPacket(new SystemMessage(SystemMessageId.ATTACK_FAILED));
					continue;
				}
			}
			boolean stopped = false;
			L2Effect[] effects = target.getAllEffects();
			if (effects != null)
			{
				for (L2Effect e : effects) 
				{
                    if (e != null && skill != null)
                        if (e.getSkill().getId() == skill.getId()) 
                        {
						e.exit();
						stopped = true;
					}
				}
			}
			if (skill.isToggle() && stopped)
				return;
			// if this is a debuff let the duel manager know about it
			// so the debuff can be removed after the duel
			// (player & target must be in the same duel)
			if (target instanceof L2PcInstance && ((L2PcInstance)target).isInDuel()
					&& (skill.getSkillType() == L2Skill.SkillType.DEBUFF ||
					skill.getSkillType() == L2Skill.SkillType.BUFF)
					&& player.getDuelId() == ((L2PcInstance)target).getDuelId())
			{
				DuelManager dm = DuelManager.getInstance();
				for (L2Effect buff : skill.getEffects(activeChar, target))
					if (buff != null) dm.onBuff(((L2PcInstance)target), buff);
			}
			else
				skill.getEffects(activeChar, target);

        	if (skill.getSkillType() == L2Skill.SkillType.AGGDEBUFF)
			{
        		if (target instanceof L2Attackable)
        			target.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, activeChar, (int)skill.getPower());
        		else if (target instanceof L2PlayableInstance)
    			{
        			if (target.getTarget() == activeChar)
        				target.getAI().setIntention(CtrlIntention.AI_INTENTION_ATTACK,activeChar);
        			else
        				target.setTarget(activeChar);
    			}
			}
        }
        // Possibility of a lethal strike                
        Formulas.getInstance().calcLethalHit(activeChar, target, skill);
        // self Effect :]
        L2Effect effect = activeChar.getFirstEffect(skill.getId());
        if (effect != null && effect.isSelfEffect())
        {
        	//Replace old effect with new one.
        	effect.exit();
        }
        skill.getEffectsSelf(activeChar);
	}


	public SkillType[] getSkillIds()
	{
		return SKILL_IDS;
	}
}
