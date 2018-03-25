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
package com.it.br.gameserver.skills.l2skills;

import com.it.br.Config;
import com.it.br.gameserver.datatables.xml.NpcTable;
import com.it.br.gameserver.idfactory.IdFactory;
import com.it.br.gameserver.model.L2Character;
import com.it.br.gameserver.model.L2Object;
import com.it.br.gameserver.model.L2Skill;
import com.it.br.gameserver.model.L2World;
import com.it.br.gameserver.model.actor.instance.L2CubicInstance;
import com.it.br.gameserver.model.actor.instance.L2PcInstance;
import com.it.br.gameserver.model.actor.instance.L2SiegeSummonInstance;
import com.it.br.gameserver.model.actor.instance.L2SummonInstance;
import com.it.br.gameserver.model.base.Experience;
import com.it.br.gameserver.network.SystemMessageId;
import com.it.br.gameserver.network.serverpackets.PetInfo;
import com.it.br.gameserver.network.serverpackets.SystemMessage;
import com.it.br.gameserver.templates.L2NpcTemplate;
import com.it.br.gameserver.templates.StatsSet;

public class L2SkillSummon extends L2Skill 
{

	private int     _npcId;
	private float   _expPenalty;
	private boolean _isCubic;

	public L2SkillSummon(StatsSet set) 
	{
		super(set);

		_npcId      = set.getInteger("npcId", 0);
		_expPenalty = set.getFloat ("expPenalty", 0.f);
		_isCubic    = set.getBool  ("isCubic", false);
	}

	public boolean checkCondition(L2Character activeChar)
	{
		if (activeChar instanceof L2PcInstance)
		{
			L2PcInstance player = (L2PcInstance)activeChar;
            if (player.isInCombat()) 
            { 
            	player.sendPacket(new SystemMessage(SystemMessageId.YOU_CANNOT_SUMMON_IN_COMBAT)); 
            	return false; 
            } 
			if (_isCubic) 
			{
				if (player.isInCombat()) 
				{ 
					player.sendPacket(new SystemMessage(SystemMessageId.YOU_CANNOT_SUMMON_IN_COMBAT)); 
					return false; 
				} 
				if (getTargetType() != L2Skill.SkillTargetType.TARGET_SELF)
				{
					return true; //Player is always able to cast mass cubic skill
				}
				int mastery = player.getSkillLevel(L2Skill.SKILL_CUBIC_MASTERY);
				if (mastery < 0)
					mastery = 0;
				int count = player.getCubics().size();
				if (count > mastery) {
					SystemMessage sm = new SystemMessage(SystemMessageId.S1_S2);
					sm.addString("You already have "+count+" cubic(s).");
					activeChar.sendPacket(sm);
					return false;
				}
			} 
			else 
			{
				if (player.inObserverMode())
					return false;
				if (player.getPet() != null)
				{
					SystemMessage sm = new SystemMessage(SystemMessageId.S1_S2);
					sm.addString("You already have a pet.");
					activeChar.sendPacket(sm);
					return false;
				}
			}
		}
		return super.checkCondition(activeChar, null, false);
	}


	@Override
	public void useSkill(L2Character caster, L2Object[] targets) {
		if (caster.isAlikeDead() || !(caster instanceof L2PcInstance))
			return;

		L2PcInstance activeChar = (L2PcInstance) caster;

		if (_npcId == 0) 
		{
            SystemMessage sm = new SystemMessage(SystemMessageId.S1_S2);
            sm.addString("Summon skill "+getId()+" not described yet");
			activeChar.sendPacket(sm);
			return;
		}

		if (_isCubic) 
		{
			if (targets.length > 1) //Mass cubic skill
			{
				for (L2Object obj: targets)
				{
					if (!(obj instanceof L2PcInstance)) continue;
					L2PcInstance player = ((L2PcInstance)obj);
					int mastery = player.getSkillLevel(L2Skill.SKILL_CUBIC_MASTERY);
					if (mastery < 0)
						mastery = 0;
                    if (mastery == 0 && player.getCubics().size() > 0)
					{
						//Player can have only 1 cubic - we shuld replace old cubic with new one
                        for (L2CubicInstance c: player.getCubics().values())
                        {
                            c.stopAction();
                            c = null;
                        }
						player.getCubics().clear();
					}
					if (player.getCubics().size() > mastery) continue;
                    if (player.getCubics().containsKey(_npcId))
                    {
                        player.sendMessage("You already have such cubic");
                    }
                    else
                    {
						player.addCubic(_npcId, getLevel());
						player.broadcastUserInfo();
                    }
				}
				return;
			}
			else 
			{
				int mastery = activeChar.getSkillLevel(L2Skill.SKILL_CUBIC_MASTERY);
				if (mastery < 0)
					mastery = 0;
				if (activeChar.getCubics().size() > mastery) 
				{
					if (Config.DEBUG)
						_log.debug("player can't summon any more cubics. ignore summon skill");
					activeChar.sendPacket(new SystemMessage(SystemMessageId.CUBIC_SUMMONING_FAILED));
					return;
				}
                if (activeChar.getCubics().containsKey(_npcId))
                {
                    activeChar.sendMessage("You already have such cubic");
                    return;
                }
				activeChar.addCubic(_npcId, getLevel());
				activeChar.broadcastUserInfo();
				return;
			}
		}

		if (activeChar.getPet() != null || activeChar.isMounted()) 
		{
			if (Config.DEBUG)
				_log.debug("player has a pet already. ignore summon skill");
			return;
		}

		L2SummonInstance summon;
		L2NpcTemplate summonTemplate = NpcTable.getInstance().getTemplate(_npcId);
		if (summonTemplate.type.equalsIgnoreCase("L2SiegeSummon"))
			summon = new L2SiegeSummonInstance(IdFactory.getInstance().getNextId(), summonTemplate, activeChar, this);
		else
			summon = new L2SummonInstance(IdFactory.getInstance().getNextId(), summonTemplate, activeChar, this);

        summon.setName(summonTemplate.name);
        summon.setTitle(activeChar.getName());
        summon.setExpPenalty(_expPenalty);
        if (summon.getLevel() >= Experience.LEVEL.length)
        {
            summon.getStat().setExp(Experience.LEVEL[Experience.LEVEL.length - 1]);
            _log.warn("Summon ("+summon.getName()+") NpcID: "+summon.getNpcId()+" has a level above 75. Please rectify.");
        }
        else
        {
            summon.getStat().setExp(Experience.LEVEL[(summon.getLevel() % Experience.LEVEL.length)]);
        }
		summon.setCurrentHp(summon.getMaxHp());
		summon.setCurrentMp(summon.getMaxMp());
		summon.setHeading(activeChar.getHeading());
    	summon.setRunning();
		activeChar.setPet(summon);

    	L2World.getInstance().storeObject(summon);
        summon.spawnMe(activeChar.getX()+50, activeChar.getY()+100, activeChar.getZ());

    	summon.setFollowStatus(true);
        summon.setShowSummonAnimation(false);
                                              
        activeChar.sendPacket(new PetInfo(summon));
	}
}
