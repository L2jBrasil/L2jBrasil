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

import java.util.logging.Logger;

import com.it.br.Config;
import com.it.br.gameserver.datatables.sql.SkillTable;
import com.it.br.gameserver.handler.ISkillHandler;
import com.it.br.gameserver.lib.Log;
import com.it.br.gameserver.model.L2Character;
import com.it.br.gameserver.model.L2Effect;
import com.it.br.gameserver.model.L2ItemInstance;
import com.it.br.gameserver.model.L2Object;
import com.it.br.gameserver.model.L2Skill;
import com.it.br.gameserver.model.L2Skill.SkillType;
import com.it.br.gameserver.model.actor.instance.L2NpcInstance;
import com.it.br.gameserver.model.actor.instance.L2PcInstance;
import com.it.br.gameserver.model.actor.instance.L2RaidBossInstance;
import com.it.br.gameserver.network.SystemMessageId;
import com.it.br.gameserver.network.serverpackets.EtcStatusUpdate;
import com.it.br.gameserver.network.serverpackets.SystemMessage;
import com.it.br.gameserver.skills.Formulas;
import com.it.br.gameserver.skills.effects.EffectCharge;
import com.it.br.gameserver.templates.L2WeaponType;

public class Pdam implements ISkillHandler
{
    // all the items ids that this handler knowns
    private static Logger _log = Logger.getLogger(Pdam.class.getName());

    private static final SkillType[] SKILL_IDS = {SkillType.PDAM,/* SkillType.CHARGEDAM */};
    public void useSkill(L2Character activeChar, L2Skill skill, L2Object[] targets)
    {
        if (activeChar.isAlikeDead()) return;

        int damage = 0;

        if (Config.DEBUG)
            if (Config.DEBUG) _log.fine("Begin Skill processing in Pdam.java " + skill.getSkillType());

        for (int index = 0; index < targets.length; index++)
        {
            L2Character target = (L2Character) targets[index];
            L2ItemInstance weapon = activeChar.getActiveWeaponInstance();
            if (activeChar instanceof L2PcInstance && target instanceof L2PcInstance
                && target.isAlikeDead() && target.isFakeDeath())
            {
                target.stopFakeDeath(null);
            }
            else if (target.isAlikeDead()) continue;

            boolean dual = activeChar.isUsingDualWeapon();
            boolean shld = Formulas.calcShldUse(activeChar, target);
            // PDAM critical chance not affected by buffs, only by STR. Only some skills are meant to crit.
            boolean crit = false;
            if (skill.getBaseCritRate() > 0)
            	crit = Formulas.calcCrit(skill.getBaseCritRate() * 10 * Formulas.getInstance().getSTRBonus(activeChar));

            boolean soul = (weapon != null && weapon.getChargedSoulshot() == L2ItemInstance.CHARGED_SOULSHOT && weapon.getItemType() != L2WeaponType.DAGGER);

            if (!crit && (skill.getCondition() & L2Skill.COND_CRIT) != 0) damage = 0;
            else damage = (int) Formulas.calcPhysDam(activeChar, target, skill, shld, false, dual, soul);
            if (crit) damage *= 2; // PDAM Critical damage always 2x and not affected by buffs

            if (damage > 5000 && activeChar instanceof L2PcInstance)
            {
                String name = "";
                if (target instanceof L2RaidBossInstance) name = "RaidBoss ";
                if (target instanceof L2NpcInstance) name += target.getName() + "(" + ((L2NpcInstance) target).getTemplate().npcId + ")";
                if (target instanceof L2PcInstance) name = target.getName() + "(" + target.getObjectId() + ") "; name += target.getLevel() + " lvl";
                Log.add(activeChar.getName() + "(" + activeChar.getObjectId() + ") "
                    + activeChar.getLevel() + " lvl did damage " + damage + " with skill "
                    + skill.getName() + "(" + skill.getId() + ") to " + name, "damage_pdam");
            }

            if (soul && weapon != null) weapon.setChargedSoulshot(L2ItemInstance.CHARGED_NONE);

            if (damage > 0)
            {
            	activeChar.sendDamageMessage(target, damage, false, crit, false);

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
            		else
                	{
            			// activate attacked effects, if any
            			target.stopSkillEffects(skill.getId());
                        if (Formulas.getInstance().calcSkillSuccess(activeChar, target, skill, false, false, false))
                        {
                            skill.getEffects(activeChar, target);

                            SystemMessage sm = new SystemMessage(SystemMessageId.YOU_FEEL_S1_EFFECT);
                            sm.addSkillName(skill.getId());
                            target.sendPacket(sm);
                        }
                        else
                        {
                            SystemMessage sm = new SystemMessage(SystemMessageId.S1_WAS_UNAFFECTED_BY_S2);
                            sm.addString(target.getName());
                            sm.addSkillName(skill.getDisplayId());
                            activeChar.sendPacket(sm);
                        }
                	}
            	}
            	
            	// Possibility of a lethal strike 
            	boolean lethal = Formulas.getInstance().calcLethalHit(activeChar, target, skill); 
                
                // Make damage directly to HP 
                if(!lethal && skill.getDmgDirectlyToHP()) 
                { 
                	if(target instanceof L2PcInstance) 
                	{ 
                		L2PcInstance player = (L2PcInstance)target; 
                		if (!player.isInvul()) 
                		{ 
                			if (damage >= player.getCurrentHp()) 
                			{ 
                				if (player.isInDuel()) 
                					player.setCurrentHp(1); 
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
                			{
                				player.setCurrentHp(player.getCurrentHp() - damage); 
                			}
                		} 
                		
                		SystemMessage smsg = new SystemMessage(SystemMessageId.S1_HIT_YOU_S2_DMG); 
                		smsg.addString(activeChar.getName()); 
                		smsg.addNumber(damage); 
                		player.sendPacket(smsg); 
                	} 
                	else 
                	{
                		target.reduceCurrentHp(damage, activeChar);
                	}
                }
                else
                {
                	target.reduceCurrentHp(damage, activeChar); 
                }
            }	
            else // No - damage
            {
            	activeChar.sendPacket(new SystemMessage(SystemMessageId.ATTACK_FAILED));
            }

            if (skill.getId() == 345 || skill.getId() == 346) // Sonic Rage or Raging Force
            {
            	EffectCharge effect = (EffectCharge)activeChar.getFirstEffect(L2Effect.EffectType.CHARGE);
            	if (effect != null)
            	{
                    int effectcharge = effect.getLevel();
                    if (effectcharge < 7)
                    {
                        effectcharge++;
                        effect.addNumCharges(1);
                        if (activeChar instanceof L2PcInstance)
                        {
                        	activeChar.sendPacket(new EtcStatusUpdate((L2PcInstance)activeChar));
                        	SystemMessage sm = new SystemMessage(SystemMessageId.FORCE_INCREASED_TO_S1);
                        	sm.addNumber(effectcharge);
                        	activeChar.sendPacket(sm);
                        }
                    }
                    else
                    {
                        SystemMessage sm = new SystemMessage(SystemMessageId.FORCE_MAXLEVEL_REACHED);
                        activeChar.sendPacket(sm);
                    }
                }
                else
                {
                    if (skill.getId() == 345) // Sonic Rage
                    {
                        L2Skill dummy = SkillTable.getInstance().getInfo(8, 7); // Lv7 Sonic Focus
                        dummy.getEffects(activeChar, activeChar);
                    }
                    else if (skill.getId() == 346) // Raging Force
                    {
                        L2Skill dummy = SkillTable.getInstance().getInfo(50, 7); // Lv7 Focused Force
                        dummy.getEffects(activeChar, activeChar);
                    }
                }
            }
            //self Effect :]
            L2Effect effect = activeChar.getFirstEffect(skill.getId());
            if (effect != null && effect.isSelfEffect())
            {
            	//Replace old effect with new one.
            	effect.exit();
            }
            skill.getEffectsSelf(activeChar);
        }

        if (skill.isSuicideAttack())
        {
        	activeChar.doDie(null);
        	activeChar.setCurrentHp(0);
        }
    }

    public SkillType[] getSkillIds()
    {
        return SKILL_IDS;
    }
}
