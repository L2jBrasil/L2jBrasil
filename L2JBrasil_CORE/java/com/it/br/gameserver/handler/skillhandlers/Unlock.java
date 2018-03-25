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

import com.it.br.gameserver.ai.CtrlIntention;
import com.it.br.gameserver.handler.ISkillHandler;
import com.it.br.gameserver.model.L2Character;
import com.it.br.gameserver.model.L2Object;
import com.it.br.gameserver.model.L2Skill;
import com.it.br.gameserver.model.L2Skill.SkillType;
import com.it.br.gameserver.model.actor.instance.L2ChestInstance;
import com.it.br.gameserver.model.actor.instance.L2DoorInstance;
import com.it.br.gameserver.network.SystemMessageId;
import com.it.br.gameserver.network.serverpackets.ActionFailed;
import com.it.br.gameserver.network.serverpackets.SocialAction;
import com.it.br.gameserver.network.serverpackets.SystemMessage;
import com.it.br.gameserver.skills.Formulas;
import com.it.br.util.Rnd;

public class Unlock implements ISkillHandler
{
	//private static Logger _log = LoggerFactory.getLogger(Unlock.class);
	private static final SkillType[] SKILL_IDS = {SkillType.UNLOCK};


	public void useSkill(L2Character activeChar, L2Skill skill, L2Object[] targets)
	{
		L2Object[] targetList = skill.getTargetList(activeChar);

		if (targetList == null) return;

		for (int index = 0; index < targetList.length; index++)
		{
			L2Object target = targetList[index];

			boolean success = Formulas.getInstance().calculateUnlockChance(skill);
			if (target instanceof L2DoorInstance)
			{
				L2DoorInstance door = (L2DoorInstance) target;
				if (!door.isUnlockable())
				{
					activeChar.sendPacket(new SystemMessage(SystemMessageId.UNABLE_TO_UNLOCK_DOOR));
					activeChar.sendPacket(new ActionFailed());
					return;
				}

				if(success && (!door.getOpen()))
				{
					door.openMe();
					door.onOpen();
					SystemMessage msg = new SystemMessage(SystemMessageId.S1_S2);
					msg.addString("Unlock the door!");
					activeChar.sendPacket(msg);
					msg = null;
				}
				else
				{
					activeChar.sendPacket(new SystemMessage(SystemMessageId.FAILED_TO_UNLOCK_DOOR));
				}
			}
			else if (target instanceof L2ChestInstance)
			{
				L2ChestInstance chest = (L2ChestInstance) targetList[index];
				if (chest.getCurrentHp() <= 0 || chest.isInteracted())
				{
					activeChar.sendPacket(new ActionFailed());
					return;
				}
				else
				{
					int chestChance = 0;
					int chestGroup = 0;
					int chestTrapLimit = 0;

					if (chest.getLevel() > 60) chestGroup = 4;
					else if (chest.getLevel() > 40) chestGroup = 3;
					else if (chest.getLevel() > 30) chestGroup = 2;
					else chestGroup = 1;

					switch (chestGroup)
					{
						case 1:
						{
							if (skill.getLevel() > 10) chestChance = 100;
							else if (skill.getLevel() >= 3) chestChance = 50;
							else if (skill.getLevel() == 2) chestChance = 45;
							else if (skill.getLevel() == 1) chestChance = 40;

							chestTrapLimit = 10;
						}
							break;
						case 2:
						{
							if (skill.getLevel() > 12) chestChance = 100;
							else if (skill.getLevel() >= 7) chestChance = 50;
							else if (skill.getLevel() == 6) chestChance = 45;
							else if (skill.getLevel() == 5) chestChance = 40;
							else if (skill.getLevel() == 4) chestChance = 35;
							else if (skill.getLevel() == 3) chestChance = 30;

							chestTrapLimit = 30;
						}
							break;
						case 3:
						{
							if (skill.getLevel() >= 14) chestChance = 50;
							else if (skill.getLevel() == 13) chestChance = 45;
							else if (skill.getLevel() == 12) chestChance = 40;
							else if (skill.getLevel() == 11) chestChance = 35;
							else if (skill.getLevel() == 10) chestChance = 30;
							else if (skill.getLevel() == 9) chestChance = 25;
							else if (skill.getLevel() == 8) chestChance = 20;
							else if (skill.getLevel() == 7) chestChance = 15;
							else if (skill.getLevel() == 6) chestChance = 10;

							chestTrapLimit = 50;
						}
							break;
						case 4:
						{
							if (skill.getLevel() >= 14) chestChance = 50;
							else if (skill.getLevel() == 13) chestChance = 45;
							else if (skill.getLevel() == 12) chestChance = 40;
							else if (skill.getLevel() == 11) chestChance = 35;

							chestTrapLimit = 80;
						}
							break;
					}
					if (Rnd.get(100) <= chestChance)
					{
						activeChar.broadcastPacket(new SocialAction(activeChar.getObjectId(),3));
	                    chest.setSpecialDrop();
	                    chest.setMustRewardExpSp(false);
	                    chest.setInteracted();
	                    chest.reduceCurrentHp(99999999, activeChar);
					}
					else
					{
	                    activeChar.broadcastPacket(new SocialAction(activeChar.getObjectId(),13));
	                    if (Rnd.get(100) < chestTrapLimit) chest.chestTrap(activeChar);
	                    chest.setInteracted();
	                    chest.addDamageHate(activeChar,0,1);
	                    chest.getAI().setIntention(CtrlIntention.AI_INTENTION_ATTACK, activeChar);
					}
				}
			}
		}
	}


	public SkillType[] getSkillIds()
	{
		return SKILL_IDS;
	}
}
