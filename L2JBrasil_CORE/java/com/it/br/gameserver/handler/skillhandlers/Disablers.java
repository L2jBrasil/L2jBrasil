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

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.it.br.Config;
import com.it.br.gameserver.ai.CtrlEvent;
import com.it.br.gameserver.ai.CtrlIntention;
import com.it.br.gameserver.ai.L2AttackableAI;
import com.it.br.gameserver.handler.ISkillHandler;
import com.it.br.gameserver.handler.SkillHandler;
import com.it.br.gameserver.model.L2Attackable;
import com.it.br.gameserver.model.L2Character;
import com.it.br.gameserver.model.L2Effect;
import com.it.br.gameserver.model.L2ItemInstance;
import com.it.br.gameserver.model.L2Object;
import com.it.br.gameserver.model.L2Skill;
import com.it.br.gameserver.model.L2Skill.SkillType;
import com.it.br.gameserver.model.L2Summon;
import com.it.br.gameserver.model.actor.instance.L2PcInstance;
import com.it.br.gameserver.model.actor.instance.L2SiegeSummonInstance;
import com.it.br.gameserver.model.base.Experience;
import com.it.br.gameserver.network.SystemMessageId;
import com.it.br.gameserver.network.serverpackets.SystemMessage;
import com.it.br.gameserver.skills.Formulas;
import com.it.br.gameserver.skills.Stats;
import com.it.br.util.Rnd;

/**
 * This Handles Disabler skills
 * @author _drunk_
 */
public class Disablers implements ISkillHandler
{
	private static final SkillType[] SKILL_IDS = 
	{
			L2Skill.SkillType.STUN, 
			L2Skill.SkillType.ROOT,
			L2Skill.SkillType.SLEEP, 
			L2Skill.SkillType.CONFUSION,
			L2Skill.SkillType.AGGDAMAGE, 
			L2Skill.SkillType.AGGREDUCE,
			L2Skill.SkillType.AGGREDUCE_CHAR,
			L2Skill.SkillType.AGGREMOVE,
			L2Skill.SkillType.UNBLEED, 
			L2Skill.SkillType.UNPOISON,
			L2Skill.SkillType.MUTE, 
			L2Skill.SkillType.FAKE_DEATH,
			L2Skill.SkillType.CONFUSE_MOB_ONLY,
			L2Skill.SkillType.NEGATE,
			L2Skill.SkillType.CANCEL, 
			L2Skill.SkillType.PARALYZE,
			L2Skill.SkillType.ERASE,
			L2Skill.SkillType.CANCEL_DEBUFF,
			L2Skill.SkillType.MAGE_BANE, 
			L2Skill.SkillType.WARRIOR_BANE, 
			L2Skill.SkillType.BETRAY
	};

    protected static final Logger _log = Logger.getLogger(L2Skill.class.getName());
    private  String[] _negateStats=null;
    private  float _negatePower=0.f;
    private int _negateId=0;


	public void useSkill(L2Character activeChar, L2Skill skill, L2Object[] targets)
    {
        SkillType type = skill.getSkillType();

        boolean ss = false;
        boolean sps = false;
        boolean bss = false;

        L2ItemInstance weaponInst = activeChar.getActiveWeaponInstance();

        if (activeChar instanceof L2PcInstance)
        {
            if (weaponInst == null && skill.isOffensive())
            {
                SystemMessage sm2 = new SystemMessage(SystemMessageId.S1_S2);
                sm2.addString("You must equip a weapon before casting a spell.");
                activeChar.sendPacket(sm2);
                return;
            }
        }

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
        // If there is no weapon equipped, check for an active summon.
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

        for (int index = 0; index < targets.length; index++)
        {
            // Get a target
            if (!(targets[index] instanceof L2Character)) continue;

            L2Character target = (L2Character) targets[index];

            if (target == null || target.isDead()) //bypass if target is null or dead
        		continue;

            switch (type)
            {
        		case BETRAY:
        	 	{
        	 		if (Formulas.getInstance().calcSkillSuccess(activeChar, target, skill, ss, sps, bss))
        	 			skill.getEffects(activeChar, target);
        	 		else
        	 		{
        	 			SystemMessage sm = new SystemMessage(SystemMessageId.S1_WAS_UNAFFECTED_BY_S2);
        	 			sm.addString(target.getName());
        	 			sm.addSkillName(skill.getId());
        	 			activeChar.sendPacket(sm);
        	 		}
        	 		break;
        	 	}
                case FAKE_DEATH:
                {
                    // stun/fakedeath is not mdef dependant, it depends on lvl difference, target CON and power of stun
                    skill.getEffects(activeChar, target);
                    break;
                }
                case ROOT:
                case STUN:
                {
                    if(target.reflectSkill(skill))
                    	target = activeChar;

                    if (Formulas.getInstance().calcSkillSuccess(activeChar, target, skill, ss, sps, bss))
                    	skill.getEffects(activeChar, target);
                    else
                    {
                        if (activeChar instanceof L2PcInstance)
                        {
                            SystemMessage sm = new SystemMessage(SystemMessageId.S1_WAS_UNAFFECTED_BY_S2);
                            sm.addString(target.getName());
                            sm.addSkillName(skill.getDisplayId());
                            activeChar.sendPacket(sm);
                        }
                    }
                    break;
                }
                case SLEEP:
                case PARALYZE: //use same as root for now
                {
                    if(target.reflectSkill(skill))
                    	target = activeChar;

                    if (Formulas.getInstance().calcSkillSuccess(activeChar, target, skill, ss, sps, bss))
                    	skill.getEffects(activeChar, target);
                    else
                    {
                        if (activeChar instanceof L2PcInstance)
                        {
                            SystemMessage sm = new SystemMessage(SystemMessageId.S1_WAS_UNAFFECTED_BY_S2);
                            sm.addString(target.getName());
                            sm.addSkillName(skill.getDisplayId());
                            activeChar.sendPacket(sm);
                        }
                    }
                    break;
                }
                case CONFUSION:
                case MUTE:
                {
                    if(target.reflectSkill(skill))
                    	target = activeChar;

                    if (Formulas.getInstance().calcSkillSuccess(activeChar, target, skill, ss, sps, bss))
                    {
                        // stop same type effect if avaiable
                        L2Effect[] effects = target.getAllEffects();
                        for (L2Effect e : effects)
                            if (e.getSkill().getSkillType() == type)
                            	e.exit();
                        // then restart
                        // Make above skills mdef dependant
                        if (Formulas.getInstance().calcSkillSuccess(activeChar, target, skill, ss, sps, bss))
                        //if(Formulas.getInstance().calcMagicAffected(activeChar, target, skill))
                            skill.getEffects(activeChar, target);
                        else
                        {
                            if (activeChar instanceof L2PcInstance)
                            {
                                SystemMessage sm = new SystemMessage(SystemMessageId.S1_WAS_UNAFFECTED_BY_S2);
                                sm.addString(target.getName());
                                sm.addSkillName(skill.getDisplayId());
                                activeChar.sendPacket(sm);
                            }
                        }
                    }
                    else
                    {
                        if (activeChar instanceof L2PcInstance)
                        {
                            SystemMessage sm = new SystemMessage(SystemMessageId.S1_WAS_UNAFFECTED_BY_S2);
                            sm.addString(target.getName());
                            sm.addSkillName(skill.getDisplayId());
                            activeChar.sendPacket(sm);
                        }
                    }
                    break;
                }
                case CONFUSE_MOB_ONLY:
                {
                    // do nothing if not on mob
                    if (target instanceof L2Attackable)
					{
						if (Formulas.getInstance().calcSkillSuccess(activeChar, target, skill, ss, sps, bss))
						{
							L2Effect[] effects = target.getAllEffects();
							for (L2Effect e : effects)
							{
								if (e.getSkill().getSkillType() == type)
									e.exit();
							}
                    	skill.getEffects(activeChar, target);
						}
						else
						{
							if (activeChar instanceof L2PcInstance)
							{
								SystemMessage sm = new SystemMessage(SystemMessageId.S1_WAS_UNAFFECTED_BY_S2);
	                            sm.addString(target.getName());
	                            sm.addSkillName(skill.getDisplayId());
								activeChar.sendPacket(sm);
							}
						}
					}
                    	else
                    	activeChar.sendPacket(new SystemMessage(SystemMessageId.TARGET_IS_INCORRECT));
                    break;
                }
                case AGGDAMAGE:
                {
                    if (target instanceof L2Attackable)
                        target.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, activeChar, (int) ((150*skill.getPower())/(target.getLevel()+7)));
                    //TODO [Nemesiss] should this have 100% chance?
                    skill.getEffects(activeChar, target);
                    break;
                }
                case AGGREDUCE:
                {
                	// these skills needs to be rechecked
                	if (target instanceof L2Attackable)
                    {
                    	skill.getEffects(activeChar, target);

                    	double aggdiff = ((L2Attackable)target).getHating(activeChar)
                    					   - target.calcStat(Stats.AGGRESSION, ((L2Attackable)target).getHating(activeChar), target, skill);

                    	if (skill.getPower() > 0)
                    		((L2Attackable)target).reduceHate(null, (int) skill.getPower());
                    	else if (aggdiff > 0)
                    		((L2Attackable)target).reduceHate(null, (int) aggdiff);
                    }
                    break;
                }
                case AGGREDUCE_CHAR:
                {
                	// these skills needs to be rechecked
                	if (Formulas.getInstance().calcSkillSuccess(activeChar, target, skill, ss, sps, bss))
                	{
                		if (target instanceof L2Attackable)
                		{
                    		L2Attackable targ = (L2Attackable)target;
                			targ.stopHating(activeChar);
                    		if (targ.getMostHated() == null)
                            {
                           		((L2AttackableAI)targ.getAI()).setGlobalAggro(-25);
                        		targ.clearAggroList();
                        		targ.getAI().setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
                        		targ.setWalking();
                            }
                        }
                    	skill.getEffects(activeChar, target);
                    }
                	else
                	{
                		if (activeChar instanceof L2PcInstance)
                		{
                			SystemMessage sm = new SystemMessage(SystemMessageId.S1_WAS_UNAFFECTED_BY_S2);
                			sm.addString(target.getName());
                			sm.addSkillName(skill.getId());
                			activeChar.sendPacket(sm);
                		}
                	}
                    break;
                }
                case AGGREMOVE:
                {
                	// these skills needs to be rechecked
                	if (target instanceof L2Attackable && !target.isRaid())
                	{
                		if (Formulas.getInstance().calcSkillSuccess(activeChar, target, skill, ss, sps, bss))
                		{
                			if (skill.getTargetType() == L2Skill.SkillTargetType.TARGET_UNDEAD)
                			{
                				if(target.isUndead())
                					((L2Attackable)target).reduceHate(null, ((L2Attackable)target).getHating(((L2Attackable)target).getMostHated()));
                			}
                			else
                				((L2Attackable)target).reduceHate(null, ((L2Attackable)target).getHating(((L2Attackable)target).getMostHated()));
                		}
                		else
                		{
                			if (activeChar instanceof L2PcInstance)
                			{
                				SystemMessage sm = new SystemMessage(SystemMessageId.S1_WAS_UNAFFECTED_BY_S2);
                				sm.addString(target.getName());
                				sm.addSkillName(skill.getId());
                				activeChar.sendPacket(sm);
                			}
                		}
                	}
                    break;
                }
                case UNBLEED:
                {
                    negateEffect(target,SkillType.BLEED,skill.getPower());
                    break;
                }
                case UNPOISON:
                {
                    negateEffect(target,SkillType.POISON,skill.getPower());
                    break;
                }
                case ERASE:
                {
                	if (Formulas.getInstance().calcSkillSuccess(activeChar, target, skill, ss, sps, bss)
                		// doesn't affect siege golem or wild hog cannon
                		&& !(target instanceof L2SiegeSummonInstance)
                		)
                	{
                		L2PcInstance summonOwner = null;
                		L2Summon summonPet = null;
                		summonOwner = ((L2Summon)target).getOwner();
                		summonPet = summonOwner.getPet();
                		summonPet.unSummon(summonOwner);
                        SystemMessage sm = new SystemMessage(SystemMessageId.LETHAL_STRIKE);
				        summonOwner.sendPacket(sm);
                	}
                	else
                    {
                        if (activeChar instanceof L2PcInstance)
                        {
                            SystemMessage sm = new SystemMessage(SystemMessageId.S1_WAS_UNAFFECTED_BY_S2);
                            sm.addString(target.getName());
                            sm.addSkillName(skill.getId());
                            activeChar.sendPacket(sm);
                        }
                    }
                	break;
                }
                case MAGE_BANE:
            	{
                    for(L2Object t: targets)
                    {
                    	L2Character target1 = (L2Character) t;

                        if(target1.reflectSkill(skill))
                        	target1 = activeChar;

                    	if (! Formulas.getInstance().calcSkillSuccess(activeChar, target1, skill, ss, sps, bss))
                    		continue;

                    	 L2Effect[] effects = target1.getAllEffects();
                    	 for(L2Effect e: effects)
                    	 {
                         // TODO: Unhardcode this SkillType, maybe on its own child class  
                         // only Acumen and Greater Empower  
                         if (e.getSkill().getId() == 1085 || e.getSkill().getId() == 1059 ||
							e.getSkill().getId() == 4356 || e.getSkill().getId() == 4355) 
                              e.exit(true);     
                         }
                    }
                    break;
            	}
                case WARRIOR_BANE:
            	{
                    for(L2Object t: targets)
                    {
                    	L2Character target1 = (L2Character) t;

                        if(target1.reflectSkill(skill))
                        	target1 = activeChar;

                    	if (! Formulas.getInstance().calcSkillSuccess(activeChar, target1, skill, ss, sps, bss))
                    		continue;

                    	 L2Effect[] effects = target1.getAllEffects();
                    	 for(L2Effect e: effects)
                    	 {
                             // TODO: Unhardcode this SkillType, maybe on its own child class  
                             // only Wind Walk and Haste  
 	                         if (e.getSkill().getId() == 1204 || e.getSkill().getId() == 1086 ||
								e.getSkill().getId() == 4342  || e.getSkill().getId() == 4357) 
                                 e.exit(true);  
                        }
                    }
                    break;
            	}
                case CANCEL_DEBUFF: 
	                { 
	                        L2Effect[] effects = target.getAllEffects(); 
	                         
	                        if (effects.length == 0 || effects == null) break; 
	                         
                        for (L2Effect e : effects) 
	                        { 
                        	if (e.getSkill().isDebuff()) 
	                                        e.exit(); 
 
                        } 
                         
	                        break; 
	                } 
                case CANCEL:
				{
					if(target.reflectSkill(skill))
						target = activeChar;

					if(skill.getId() == 1056)
					{
						if(target.isRaid())
						{
							if(activeChar instanceof L2PcInstance)
							{
								SystemMessage sm = new SystemMessage(SystemMessageId.S1_WAS_UNAFFECTED_BY_S2);
								sm.addString(target.getName());
								sm.addSkillName(skill.getDisplayId());
								activeChar.sendPacket(sm);
								sm = null;
							}
							break;
						}
						int lvlmodifier = 52 + skill.getLevel() * 2;
						if(skill.getLevel() == 12)
							lvlmodifier = (Experience.MAX_LEVEL - 1);

						int landrate = (int) skill.getPower();
						if((target.getLevel() - lvlmodifier) > 0)
							landrate = 90 - 4 * (target.getLevel() - lvlmodifier);

						landrate = (int) target.calcStat(Stats.CANCEL_VULN, landrate, target, null);

						if(Rnd.get(100) < landrate)
						{
							L2Effect[] effects = target.getAllEffects();
							int maxfive = 5;
							for(L2Effect e : effects)
							{
								switch(e.getEffectType())
								{
									case SIGNET_GROUND:
									case SIGNET_EFFECT:
										continue;
								}

								if(e.getSkill().getId() != 4082 && e.getSkill().getId() != 4215 && e.getSkill().getId() != 5182 && e.getSkill().getId() != 4515 && e.getSkill().getId() != 110 && e.getSkill().getId() != 111 && e.getSkill().getId() != 1323 && e.getSkill().getId() != 1325)
								// Cannot cancel skills 4082, 4215, 4515, 110, 111, 1323, 1325
								{
									if(e.getSkill().getSkillType() != SkillType.BUFF) //sleep, slow, surrenders etc
										e.exit(true);
									else
									{
										int rate = 100;
										int level = e.getLevel();
										if(level > 0)
											rate = Integer.valueOf(150 / (1 + level));

										if(rate > 95)
											rate = 95;
										else if(rate < 5)
											rate = 5;

										if(Rnd.get(100) < rate)
										{
											e.exit(true);
											maxfive--;
											if(maxfive == 0)
												break;
										}
									}
								}
							}
							effects = null;
						}
						else
						{
							if(activeChar instanceof L2PcInstance)
							{
								SystemMessage sm = new SystemMessage(SystemMessageId.S1_WAS_UNAFFECTED_BY_S2);
								sm.addString(target.getName());
								sm.addSkillName(skill.getDisplayId());
								activeChar.sendPacket(sm);
								sm = null;
							}
						}
						break;
					}
					else
					// all others cancel type skills
					{
						int landrate = (int) skill.getPower();
						landrate = (int) target.calcStat(Stats.CANCEL_VULN, landrate, target, null);
						if(Rnd.get(100) < landrate)
						{
							L2Effect[] effects = target.getAllEffects();
							int maxdisp = (int) skill.getNegatePower();
							if(maxdisp == 0)
								maxdisp = Config.BUFFS_MAX_AMOUNT + Config.DEBUFFS_MAX_AMOUNT + 6;
							for(L2Effect e : effects)
							{
								switch(e.getEffectType())
								{
									case SIGNET_GROUND:
									case SIGNET_EFFECT:
										continue;
								}

								if(e.getSkill().getId() != 4082 && e.getSkill().getId() != 4215 && e.getSkill().getId() != 5182 && e.getSkill().getId() != 4515 && e.getSkill().getId() != 110 && e.getSkill().getId() != 111 && e.getSkill().getId() != 1323 && e.getSkill().getId() != 1325)
								{
									if(e.getSkill().getSkillType() == SkillType.BUFF)
									{
										int rate = 100;
										int level = e.getLevel();
										if(level > 0)
											rate = Integer.valueOf(150 / (1 + level));

										if(rate > 95)
											rate = 95;
										else if(rate < 5)
											rate = 5;

										if(Rnd.get(100) < rate)
										{
											e.exit(true);
											maxdisp--;
											if(maxdisp == 0)
												break;
										}
									}
								}
							}
							effects = null;
						}
						else
						{
							if(activeChar instanceof L2PcInstance)
							{
								SystemMessage sm = new SystemMessage(SystemMessageId.S1_WAS_UNAFFECTED_BY_S2);
								sm.addString(target.getName());
								sm.addSkillName(skill.getDisplayId());
								activeChar.sendPacket(sm);
								sm = null;
							}
						}
						break;
					}
				}
               	case NEGATE:
                {
                    if (skill.getId() == 2275) // fishing potion
					{
						_negatePower = skill.getNegatePower();
						_negateId = skill.getNegateId();
                		negateEffect(target,SkillType.BUFF,_negatePower,_negateId);
                    }
                	// all others negate type skills
                    else
                    {
                    	_negateStats = skill.getNegateStats();
                    	_negatePower = skill.getNegatePower();

                    	for (String stat : _negateStats)
                    	{
                    		stat = stat.toLowerCase().intern();
                        	if (stat == "buff")
                        	{
                        		int lvlmodifier = 52 + skill.getMagicLevel() * 2;
								if(skill.getMagicLevel() == 12)
									lvlmodifier = (Experience.MAX_LEVEL - 1);

								int landrate = 90;
								if((target.getLevel() - lvlmodifier) > 0)
									landrate = 90 - 4 * (target.getLevel() - lvlmodifier);

								landrate = (int) target.calcStat(Stats.CANCEL_VULN, landrate, target, null);

								if(Rnd.get(100) < landrate)
									negateEffect(target, SkillType.BUFF, -1);
							}
	                    	if (stat == "debuff") negateEffect(target,SkillType.DEBUFF,-1);
	                    	if (stat == "weakness") negateEffect(target,SkillType.WEAKNESS,-1);
	                    	if (stat == "stun") negateEffect(target,SkillType.STUN,-1);
	                    	if (stat == "sleep") negateEffect(target,SkillType.SLEEP,-1);
	                    	if (stat == "confusion") negateEffect(target,SkillType.CONFUSION,-1);
	                    	if (stat == "mute") negateEffect(target,SkillType.MUTE,-1);
	                    	if (stat == "fear") negateEffect(target,SkillType.FEAR,-1);
	                    	if (stat == "poison") negateEffect(target,SkillType.POISON,_negatePower);
	                    	if (stat == "bleed") negateEffect(target,SkillType.BLEED,_negatePower);
	                    	if (stat == "paralyze") negateEffect(target,SkillType.PARALYZE,-1);
	                    	if (stat == "heal")
	                    	{
	                    		ISkillHandler Healhandler = SkillHandler.getInstance().getSkillHandler(SkillType.HEAL);
	                    		if (Healhandler == null)
	                    		{
		                    		_log.severe("Couldn't find skill handler for HEAL.");
		                    		continue;
	                    		}
	                    		L2Object tgts[] = new L2Object[]{target};
	                    		try 
								{
	                    			Healhandler.useSkill(activeChar, skill, tgts);
	                    		} 
								catch (IOException e) 
								{
	                    		 _log.log(Level.WARNING, "", e);
	                    		}
	                    		Healhandler = null;
                            }
                        }//end for
                    }//end else
                    if (Formulas.getInstance().calcSkillSuccess(activeChar, target, skill, ss, sps, bss)) 
                    { 
                    	skill.getEffects(activeChar, target); 
                    } 
                }// end case
            }//end switch
          //Possibility of a lethal strike 
            Formulas.getInstance().calcLethalHit(activeChar, target, skill);
        }//end for

        // self Effect :]
        L2Effect effect = activeChar.getFirstEffect(skill.getId());
        if (effect != null && effect.isSelfEffect())
        {
        	//Replace old effect with new one.
        	effect.exit(false);
        }
        skill.getEffectsSelf(activeChar);

    } //end void

    private void negateEffect(L2Character target, SkillType type, double power) 
	{
    	negateEffect(target, type, power, 0);
    }

    private void negateEffect(L2Character target, SkillType type, double power, int skillId) 
	{
        L2Effect[] effects = target.getAllEffects();
        for (L2Effect e : effects)
        {
        	if(e.getSkill()!=null && e.getSkill().getId() == 4215 || e.getSkill().getId() == 4515)
        	{
				continue; //skills cannot be removed
			}
        	else if (power == -1) // if power is -1 the effect is always removed without power/lvl check ^^
        	{
        		if (e.getSkill().getSkillType() == type || (e.getSkill().getEffectType() != null && e.getSkill().getEffectType() == type)) 
				{
        			if (skillId != 0)
        			{
        				if (skillId == e.getSkill().getId()) e.exit();
        				e.exit(true);
        			}
        			else
        				e.exit(true);
        		}
        	}
        	else if ((e.getSkill().getSkillType() == type && e.getSkill().getPower() <= power) || (e.getSkill().getEffectType() != null && e.getSkill().getEffectType() == type && e.getSkill().getEffectLvl() <= power))
        	{
    			if (skillId != 0)
    			{
    				if (skillId == e.getSkill().getId())
    					e.exit(true);
    			}
    			else e.exit(true);
        	}
		}
    }


	public SkillType[] getSkillIds()
    {
        return SKILL_IDS;
    }
}