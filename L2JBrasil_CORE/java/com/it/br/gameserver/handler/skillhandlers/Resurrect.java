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

import java.util.ArrayList;
import java.util.List;

import com.it.br.gameserver.handler.ISkillHandler;
import com.it.br.gameserver.model.L2Character;
import com.it.br.gameserver.model.L2Object;
import com.it.br.gameserver.model.L2Skill;
import com.it.br.gameserver.model.L2Skill.SkillTargetType;
import com.it.br.gameserver.model.L2Skill.SkillType;
import com.it.br.gameserver.model.actor.instance.L2PcInstance;
import com.it.br.gameserver.model.actor.instance.L2PetInstance;
import com.it.br.gameserver.network.serverpackets.SystemMessage;
import com.it.br.gameserver.skills.Formulas;
import com.it.br.gameserver.taskmanager.DecayTaskManager;
/**
 * This class ...
 *
 * @version $Revision: 1.1.2.5.2.4 $ $Date: 2005/04/03 15:55:03 $
 */

public class Resurrect implements ISkillHandler
{
	//private static Logger _log = Logger.getLogger(Resurrect.class.getName());

	private static final SkillType[] SKILL_IDS = {SkillType.RESURRECT};


	public void useSkill(L2Character activeChar, L2Skill skill, L2Object[] targets)
	{
        L2PcInstance player = null;
        if (activeChar instanceof L2PcInstance) player = (L2PcInstance)activeChar;

        L2Character target = null;
        L2PcInstance targetPlayer;
        List<L2Character> targetToRes = new ArrayList<>();

        for (int index = 0; index < targets.length; index++)
        {
            target = (L2Character) targets[index];

            if (target instanceof L2PcInstance)
            {
                targetPlayer = (L2PcInstance)target;

                // Check for same party or for same clan, if target is for clan.
                if (skill.getTargetType() == SkillTargetType.TARGET_CORPSE_CLAN)
                {
                    if (player.getClanId() != targetPlayer.getClanId()) continue;
                }
            }
            if(target.isVisible()) targetToRes.add(target);
        }

        if (targetToRes.size() == 0)
        {
            activeChar.abortCast();
            activeChar.sendPacket(SystemMessage.sendString("No valid target to resurrect"));
        }

        for (L2Character cha: targetToRes)
            if (activeChar instanceof L2PcInstance)
            {
            	if (cha instanceof L2PcInstance)
            		((L2PcInstance)cha).reviveRequest((L2PcInstance)activeChar,skill,false);
            	else if (cha instanceof L2PetInstance)
            	{
            		if (((L2PetInstance)cha).getOwner() == activeChar)
            			cha.doRevive(Formulas.getInstance().calculateSkillResurrectRestorePercent(skill.getPower(), activeChar));
            		else
                		((L2PetInstance)cha).getOwner().reviveRequest((L2PcInstance)activeChar,skill,true);
            	}
            	else cha.doRevive(Formulas.getInstance().calculateSkillResurrectRestorePercent(skill.getPower(), activeChar));
            }
            else
            {
            	DecayTaskManager.getInstance().cancelDecayTask(cha);
            	cha.doRevive(Formulas.getInstance().calculateSkillResurrectRestorePercent(skill.getPower(), activeChar));
            }
	}

	public SkillType[] getSkillIds()
	{
		return SKILL_IDS;
	}
}
