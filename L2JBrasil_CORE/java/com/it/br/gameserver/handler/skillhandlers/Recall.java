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

import com.it.br.Config;
import com.it.br.gameserver.datatables.xml.MapRegionTable;
import com.it.br.gameserver.handler.ISkillHandler;
import com.it.br.gameserver.instancemanager.GrandBossManager;
import com.it.br.gameserver.model.L2Character;
import com.it.br.gameserver.model.L2Object;
import com.it.br.gameserver.model.L2Skill;
import com.it.br.gameserver.model.L2Skill.SkillType;
import com.it.br.gameserver.model.actor.instance.L2PcInstance;
import com.it.br.gameserver.model.entity.event.TvTEvent;
import com.it.br.gameserver.network.SystemMessageId;
import com.it.br.gameserver.network.serverpackets.ActionFailed;
import com.it.br.gameserver.network.serverpackets.SystemMessage;

public class Recall implements ISkillHandler
{
	//private static Logger _log = LoggerFactory.getLogger(Recall.class);
	private static final SkillType[] SKILL_IDS = { SkillType.RECALL };

	public void useSkill(L2Character activeChar, L2Skill skill, L2Object[] targets)
	{
		try
		{
			if(activeChar instanceof L2PcInstance)
			{
				final L2PcInstance instance = (L2PcInstance)activeChar;
				if(instance.isInOlympiadMode())
				{
					activeChar.sendPacket(new SystemMessage(SystemMessageId.THIS_ITEM_IS_NOT_AVAILABLE_FOR_THE_OLYMPIAD_EVENT));
					return;
				}
				if (!TvTEvent.onEscapeUse(((L2PcInstance)activeChar).getObjectId()))
	        	{
	        		((L2PcInstance)activeChar).sendPacket(new ActionFailed());
	        		return;
	        	}
				// Checks summoner not in siege zone
				if(activeChar.isInsideZone(L2Character.ZONE_SIEGE))
				{
					((L2PcInstance) activeChar).sendMessage("You cannot summon in siege zone.");
					return;
				}
				if(activeChar.isInsideZone(L2Character.ZONE_PVP))
				{
					activeChar.sendPacket(new SystemMessage(SystemMessageId.YOU_CANNOT_SUMMON_IN_COMBAT));
					return;
				}
				if(GrandBossManager.getInstance().getZone(instance) != null && !instance.isGM())
				{
					instance.sendPacket(new SystemMessage(SystemMessageId.YOU_MAY_NOT_SUMMON_FROM_YOUR_CURRENT_LOCATION));
					return;
				}
			}

			for(int index = 0; index < targets.length; index++)
			{
				if(!(targets[index] instanceof L2Character))
					continue;

				L2Character target = (L2Character) targets[index];

				if(target instanceof L2PcInstance)
				{
					L2PcInstance targetChar = (L2PcInstance) target;

					if(targetChar.isFestivalParticipant())
					{
						targetChar.sendPacket(SystemMessage.sendString("You can't use escape skill in a festival."));
						continue;
					}
					if(targetChar.atEvent && TvTEvent.isStarted())
					{
						targetChar.sendMessage("You can't use escape skill in Event.");
						continue;
					}
					if(targetChar.isInJail())
					{
						targetChar.sendPacket(SystemMessage.sendString("You can't escape from jail."));
						continue;
					}
					if(targetChar.isInDuel())
					{
						targetChar.sendPacket(SystemMessage.sendString("You can't use escape skills during a duel."));
						continue;
					}
					if(targetChar.isAlikeDead())
					{
						SystemMessage sm = new SystemMessage(SystemMessageId.S1_IS_DEAD_AT_THE_MOMENT_AND_CANNOT_BE_SUMMONED);
						sm.addString(targetChar.getName());
						activeChar.sendPacket(sm);
						sm = null;
						continue;
					}
					if(targetChar.isInStoreMode())
					{
						SystemMessage sm = new SystemMessage(SystemMessageId.S1_CURRENTLY_TRADING_OR_OPERATING_PRIVATE_STORE_AND_CANNOT_BE_SUMMONED);
						sm.addString(targetChar.getName());
						activeChar.sendPacket(sm);
						sm = null;
						continue;
					}
					if(targetChar.isRooted() || targetChar.isInCombat())
					{
						SystemMessage sm = new SystemMessage(SystemMessageId.S1_IS_ENGAGED_IN_COMBAT_AND_CANNOT_BE_SUMMONED);
						sm.addString(targetChar.getName());
						activeChar.sendPacket(sm);
						sm = null;
						continue;
					}
					if(GrandBossManager.getInstance().getZone(targetChar) != null && !targetChar.isGM())
					{
						activeChar.sendPacket(new SystemMessage(SystemMessageId.YOUR_TARGET_IS_IN_AN_AREA_WHICH_BLOCKS_SUMMONING));
						continue;
					}
					if(targetChar.isInOlympiadMode())
					{
						activeChar.sendPacket(new SystemMessage(SystemMessageId.YOU_CANNOT_SUMMON_PLAYERS_WHO_ARE_IN_OLYMPIAD));
						continue;
					}
					if(targetChar.isAio())
					{
						activeChar.sendPacket(SystemMessage.sendString("You cannot summon Aio Buffers."));
						continue;
					}
					if(targetChar.isInsideZone(L2Character.ZONE_PVP))
					{
						activeChar.sendPacket(new SystemMessage(SystemMessageId.YOUR_TARGET_IS_IN_AN_AREA_WHICH_BLOCKS_SUMMONING));
						continue;
					}
				}
				target.teleToLocation(MapRegionTable.TeleportWhereType.Town);
				target = null;
			}
		}
		catch(Throwable e)
		{
			if (Config.DEBUG)
				e.printStackTrace();
		}
	}


	public SkillType[] getSkillIds()
	{
		return SKILL_IDS;
	}
}