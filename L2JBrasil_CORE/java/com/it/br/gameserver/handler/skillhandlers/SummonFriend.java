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
import com.it.br.gameserver.SevenSigns;
import com.it.br.gameserver.datatables.sql.ItemTable;
import com.it.br.gameserver.handler.ISkillHandler;
import com.it.br.gameserver.instancemanager.GrandBossManager;
import com.it.br.gameserver.model.L2Character;
import com.it.br.gameserver.model.L2Object;
import com.it.br.gameserver.model.L2Skill;
import com.it.br.gameserver.model.L2Skill.SkillType;
import com.it.br.gameserver.model.actor.instance.L2PcInstance;
import com.it.br.gameserver.model.entity.event.TvTEvent;
import com.it.br.gameserver.model.quest.QuestState;
import com.it.br.gameserver.network.SystemMessageId;
import com.it.br.gameserver.network.serverpackets.SystemMessage;
import com.it.br.gameserver.util.Util;

public class SummonFriend implements ISkillHandler
{
	//private static Logger _log = LoggerFactory.getLogger(SummonFriend.class);
	private static final SkillType[] SKILL_IDS = {SkillType.SUMMON_FRIEND};

	public void useSkill(L2Character activeChar, L2Skill skill, L2Object[] targets)
	{
 		if (!(activeChar instanceof L2PcInstance)) 
 			return; // currently not implemented for others

 		L2PcInstance activePlayer = (L2PcInstance)activeChar;

		if(activePlayer.isInOlympiadMode())
		{
			activePlayer.sendPacket(new SystemMessage(SystemMessageId.THIS_ITEM_IS_NOT_AVAILABLE_FOR_THE_OLYMPIAD_EVENT));
			return;
		}
 		// Checks summoner not in siege zone
 		if(activeChar.isInsideZone(L2Character.ZONE_SIEGE))
 		{
 			((L2PcInstance) activeChar).sendMessage("You cannot summon in siege zone.");
 			return;
 		}

 		// Checks summoner not in arenas, siege zones, jail
 		if(activePlayer.isInsideZone(L2Character.ZONE_PVP))
 		{
 			activePlayer.sendPacket(new SystemMessage(SystemMessageId.YOU_CANNOT_SUMMON_IN_COMBAT));
 			return;
 		}

 		if(GrandBossManager.getInstance().getZone(activePlayer) != null && !activePlayer.isGM())
 		{
 			activePlayer.sendPacket(new SystemMessage(SystemMessageId.YOU_MAY_NOT_SUMMON_FROM_YOUR_CURRENT_LOCATION));
 			return;
 		}

 		if (!checkSummonerStatus(activePlayer))
			return;

		try
        {
			for (L2Character target: (L2Character[]) targets)
			{
				if (activeChar == target)
					continue;

				if (target instanceof L2PcInstance)
				{
					L2PcInstance targetPlayer = (L2PcInstance) target;
					if (!checkSummonTargetStatus(targetPlayer, activePlayer))
						continue;

					// Need Support for Bring up box for target
					if (!Util.checkIfInRange(0, activeChar, target, false))
					{
						teleToTarget(targetPlayer, activePlayer, skill);	
					}
                }
			}
        } catch (Throwable e) 
        {
 	 	 	if (Config.DEBUG) e.printStackTrace();
 	 	}
 	}

	public static void teleToTarget(L2PcInstance targetChar, L2PcInstance summonerChar, L2Skill summonSkill)
	{
		if (targetChar == null || summonerChar == null || summonSkill == null)
			return;

		if (!checkSummonerStatus(summonerChar))
			return;

		if (!checkSummonTargetStatus(targetChar, summonerChar))
			return;

		int itemConsumeId = 8615;
		int itemConsumeCount = 1;
		if (itemConsumeId != 0 && itemConsumeCount != 0)
		{
			//Delete by rocknow
			String ItemName = ItemTable.getInstance().getTemplate(itemConsumeId).getName();
			if (targetChar.getInventory().getInventoryItemCount(itemConsumeId, 0) < itemConsumeCount)
			{
				SystemMessage sm = new SystemMessage(SystemMessageId.S1_REQUIRED_FOR_SUMMONING);
				sm.addString(ItemName);
				targetChar.sendPacket(sm);
				return;
			}
			targetChar.getInventory().destroyItemByItemId("Consume", itemConsumeId, itemConsumeCount, summonerChar, targetChar);
			SystemMessage sm = new SystemMessage(SystemMessageId.S1_DISAPPEARED);
			sm.addString(ItemName);
			targetChar.sendPacket(sm);
		}
		targetChar.teleToLocation(summonerChar.getX(), summonerChar.getY(), summonerChar.getZ(), true);
	}

	public static boolean checkSummonerStatus(L2PcInstance summonerChar)
	{
		if (summonerChar == null)
			return false;

		if (summonerChar.isInOlympiadMode())
		{
			summonerChar.sendPacket(new SystemMessage(SystemMessageId.THIS_ITEM_IS_NOT_AVAILABLE_FOR_THE_OLYMPIAD_EVENT));
			return false;
		}

		if (summonerChar.inObserverMode())
			return false;

		if (!TvTEvent.onEscapeUse(summonerChar.getObjectId()))
		{
			summonerChar.sendPacket(new SystemMessage(SystemMessageId.YOUR_TARGET_IS_IN_AN_AREA_WHICH_BLOCKS_SUMMONING));
			return false;
		}

		if (summonerChar.isInsideZone(L2Character.ZONE_NOSUMMONFRIEND))
		{
			summonerChar.sendPacket(new SystemMessage(SystemMessageId.YOUR_TARGET_IS_IN_AN_AREA_WHICH_BLOCKS_SUMMONING));
			return false;
		}

		QuestState qs = summonerChar.getQuestState("1630_PaganTeleporters");
		if (qs != null && qs.isStarted())
		{
			summonerChar.sendPacket(new SystemMessage(SystemMessageId.YOUR_TARGET_IS_IN_AN_AREA_WHICH_BLOCKS_SUMMONING));
			return false;
		}
		return true;
	}

	public static boolean checkSummonTargetStatus(L2Object target, L2PcInstance summonerChar)
	{
		if (target == null || !(target instanceof L2PcInstance))
			return false;
	
		L2PcInstance targetChar = (L2PcInstance) target;
	
		if (targetChar.isAlikeDead())
		{
			SystemMessage sm = new SystemMessage(SystemMessageId.S1_IS_DEAD_AT_THE_MOMENT_AND_CANNOT_BE_SUMMONED);
			sm.addString(targetChar.getName());
			summonerChar.sendPacket(sm);
			return false;
		}
		
		if(targetChar.isAio())
		{
			targetChar.sendPacket(SystemMessage.sendString("You cannot summon Aio Buffers."));
			return false;
		}
	
		if (targetChar.isInStoreMode())
		{
			SystemMessage sm = new SystemMessage(SystemMessageId.S1_CURRENTLY_TRADING_OR_OPERATING_PRIVATE_STORE_AND_CANNOT_BE_SUMMONED);
			sm.addString(targetChar.getName());
			summonerChar.sendPacket(sm);
			return false;
		}
	
		if (targetChar.isRooted() || targetChar.isInCombat())
		{
			SystemMessage sm = new SystemMessage(SystemMessageId.S1_IS_ENGAGED_IN_COMBAT_AND_CANNOT_BE_SUMMONED);
			sm.addString(targetChar.getName());
			summonerChar.sendPacket(sm);
			return false;
		}
	
		if (targetChar.isInOlympiadMode())
		{
			summonerChar.sendPacket(new SystemMessage(SystemMessageId.YOU_CANNOT_SUMMON_PLAYERS_WHO_ARE_IN_OLYMPIAD));
			return false;
		}
	
		if (targetChar.isFestivalParticipant())
		{
			summonerChar.sendPacket(new SystemMessage(SystemMessageId.YOUR_TARGET_IS_IN_AN_AREA_WHICH_BLOCKS_SUMMONING));
			return false;
		}
	
		if (targetChar.inObserverMode())
		{
			summonerChar.sendPacket(new SystemMessage(SystemMessageId.YOUR_TARGET_IS_IN_AN_AREA_WHICH_BLOCKS_SUMMONING));
			return false;
		}
	
		if (!TvTEvent.onEscapeUse(targetChar.getObjectId()))
		{
			summonerChar.sendPacket(new SystemMessage(SystemMessageId.YOUR_TARGET_IS_IN_AN_AREA_WHICH_BLOCKS_SUMMONING));
			return false;
		}
	
		if (targetChar.isInsideZone(L2Character.ZONE_NOSUMMONFRIEND))
		{
			SystemMessage sm = new SystemMessage(SystemMessageId.S1_IN_SUMMON_BLOCKING_AREA);
			sm.addString(targetChar.getName());
			summonerChar.sendPacket(sm);
			return false;
		}
	
		// Requires a Summoning Crystal
		if (targetChar.getInventory().getItemByItemId(8615) == null)
			return false; //Do Nothing 
    	
		// on retail character can enter 7s dungeon with summon friend,
		// but will be teleported away by mobs
		// because currently this is not working in L2J we do not allowing summoning
		if (summonerChar.isIn7sDungeon())
		{
			int targetCabal = SevenSigns.getInstance().getPlayerCabal(targetChar);
			if (SevenSigns.getInstance().isSealValidationPeriod())
			{
				if (targetCabal != SevenSigns.getInstance().getCabalHighestScore())
				{
					summonerChar.sendPacket(new SystemMessage(SystemMessageId.YOUR_TARGET_IS_IN_AN_AREA_WHICH_BLOCKS_SUMMONING));
					return false;
				}
			}
			else
			{
				if (targetCabal == SevenSigns.CABAL_NULL)
				{
					summonerChar.sendPacket(new SystemMessage(SystemMessageId.YOUR_TARGET_IS_IN_AN_AREA_WHICH_BLOCKS_SUMMONING));
					return false;
				}
			}
		}
		return true;
	}


	public SkillType[] getSkillIds()
	{
		return SKILL_IDS;
	}
}
