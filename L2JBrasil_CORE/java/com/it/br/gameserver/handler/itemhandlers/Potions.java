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
package com.it.br.gameserver.handler.itemhandlers;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.it.br.Config;
import com.it.br.gameserver.ThreadPoolManager;
import com.it.br.gameserver.datatables.sql.SkillTable;
import com.it.br.gameserver.handler.IItemHandler;
import com.it.br.gameserver.handler.ISkillHandler;
import com.it.br.gameserver.handler.SkillHandler;
import com.it.br.gameserver.model.L2Effect;
import com.it.br.gameserver.model.L2Effect.EffectType;
import com.it.br.gameserver.model.L2ItemInstance;
import com.it.br.gameserver.model.L2Object;
import com.it.br.gameserver.model.L2Skill;
import com.it.br.gameserver.model.actor.instance.L2PcInstance;
import com.it.br.gameserver.model.actor.instance.L2PetInstance;
import com.it.br.gameserver.model.actor.instance.L2PlayableInstance;
import com.it.br.gameserver.model.entity.event.TvTEvent;
import com.it.br.gameserver.network.SystemMessageId;
import com.it.br.gameserver.network.serverpackets.ActionFailed;
import com.it.br.gameserver.network.serverpackets.MagicSkillUser;
import com.it.br.gameserver.network.serverpackets.StatusUpdate;
import com.it.br.gameserver.network.serverpackets.SystemMessage;

/**
 * This class ...
 * 
 * @version $Revision: 1.2.4.4 $ $Date: 2005/03/27 15:30:07 $
 */

public class Potions implements IItemHandler
{
	protected static final Logger _log = Logger.getLogger(Potions.class.getName());
	private int _herbstask = 0;
	private SystemMessage sm = null;

	/** Task for Herbs */
	private class HerbTask implements Runnable
	{
		private L2PcInstance _activeChar;
		private int _magicId;
		private int _level;

		HerbTask(L2PcInstance activeChar, int magicId, int level)
		{
			_activeChar = activeChar;
			_magicId = magicId;
			_level = level;
		}

		public void run()
		{
			try
			{
				usePotion(_activeChar, _magicId, _level);
			}
			catch(Throwable t)
			{
				_log.log(Level.WARNING, "", t);
			}
		}
	}

	private static final int[] ITEM_IDS =
	{
		65,725,726,727,728,734,735,1060,1061,1062,1073,1374,1375,
		1539,1540,5591,5592,6035,6036,6652,6653,6654,6655,8193,8194,
		8195,8196,8197,8198,8199,8200,8201,8202,8600,8601,8602,8603,
		8604,8605,8606,8607,8608,8609,8610,8611,8612,8613,8614,	//elixir of life
		8622,8623,8624,8625,8626,8627, //elixir of Strength
		8628,8629,8630,8631,8632,8633, //elixir of cp
		8634,8635,8636,8637,8638,8639, //Crystals Element
		7912,7913,7914,7915,7916,7917, //Blessings Element
		7906,7907,7908,7909,7910,7911
	};

	public synchronized void useItem(L2PlayableInstance playable, L2ItemInstance item)
	{
		L2PcInstance activeChar;
		boolean res = false;

		if(playable instanceof L2PcInstance)
		{
			activeChar = (L2PcInstance) playable;
		}
		else if(playable instanceof L2PetInstance)
		{
			activeChar = ((L2PetInstance) playable).getOwner();
		}
		else
			return;

		if(!TvTEvent.onPotionUse(playable.getObjectId()))
		{
			playable.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}

		if(activeChar.isInOlympiadMode())
		{
			activeChar.sendPacket(new SystemMessage(SystemMessageId.THIS_ITEM_IS_NOT_AVAILABLE_FOR_THE_OLYMPIAD_EVENT));
			return;
		}
		
		if (!activeChar.canUsePotion())
		{
			activeChar.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}

		int itemId = item.getItemId();
		switch(itemId)
		{
			// MANA POTIONS
			case 726: // mana drug, xml: 2003
				if(!isEffectReplaceable(activeChar, L2Effect.EffectType.MANA_HEAL_OVER_TIME, itemId))
					return;
				res = usePotion(activeChar, 2003, 1); // configurable through xml
				break;
			case 728: // mana_potion, xml: 2005
			{
				activeChar.setCurrentMp(Config.MANA_POTION_RES + activeChar.getCurrentMp());
				StatusUpdate su = new StatusUpdate(activeChar.getObjectId());
				su.addAttribute(StatusUpdate.CUR_MP, (int) activeChar.getCurrentMp());
				activeChar.sendPacket(su);
				MagicSkillUser MSU = new MagicSkillUser(activeChar, activeChar, 2005, 1, 0, 0);
				activeChar.broadcastPacket(MSU);
				sm = new SystemMessage(SystemMessageId.USE_S1);
				sm.addItemName(itemId);
				activeChar.sendPacket(sm);
				playable.destroyItem("Consume", item.getObjectId(), 1, null, false);
				activeChar.sendMessage("restored " + Config.MANA_POTION_RES + " MP." );
			}
			return;
			// CRYSTALS AND BLESSINGS ELEMENT - By StormMaker
			case 7912: // Fire Crystal, xml: 2254
				res = usePotion(activeChar, 2254, 1);
				break;
			case 7913: // Water Crystal, xml: 2255
				res = usePotion(activeChar, 2255, 1);
				break;
			case 7914: // Wind Crystal, xml: 2256
				res = usePotion(activeChar, 2256, 1);
				break;
			case 7915: // Earth Crystal, xml: 2257
				res = usePotion(activeChar, 2257, 1);
				break;
			case 7916: // Dark Crystal, xml: 2258
				res = usePotion(activeChar, 2258, 1);
				break;
			case 7917: // Sanctity Crystal, xml: 2259
				res = usePotion(activeChar, 2259, 1);
				break;
			case 7906: // Fire Blessing, xml: 2248
				res = usePotion(activeChar, 2248, 1);
				break;
			case 7907: // Water Blessing, xml: 2255
				res = usePotion(activeChar, 2249, 1);
				break;
			case 7908: // Wind Blessing, xml: 2256
				res = usePotion(activeChar, 2250, 1);
				break;
			case 7909: // Earth Blessing, xml: 2257
				res = usePotion(activeChar, 2251, 1);
				break;
			case 7910: // Dark Blessing, xml: 2258
				res = usePotion(activeChar, 2252, 1);
				break;
			case 7911: // Sanctity Blessing, xml: 2259
				res = usePotion(activeChar, 2253, 1);
				break;
			// CRYSTALS AND BLESSINGS ELEMENT - By StormMaker
				
			// HEALING AND SPEED POTIONS
			case 65: // red_potion, xml: 2001
				res = usePotion(activeChar, 2001, 1);
				break;
			case 725: // healing_drug, xml: 2002
				if(!isEffectReplaceable(activeChar, L2Effect.EffectType.HEAL_OVER_TIME, itemId))
					return;
				res = usePotion(activeChar, 2002, 1);
				break;
			case 727: // _healing_potion, xml: 2032
				if(!isEffectReplaceable(activeChar, L2Effect.EffectType.HEAL_OVER_TIME, itemId))
					return;
				res = usePotion(activeChar, 2032, 1);
				break;
			case 734: // quick_step_potion, xml: 2011
				res = usePotion(activeChar, 2011, 1);
				break;
			case 735: // swift_attack_potion, xml: 2012
				res = usePotion(activeChar, 2012, 1);
				break;
			case 1060: // lesser_healing_potion,
			case 1073: // beginner's potion, xml: 2031
				if(!isEffectReplaceable(activeChar, L2Effect.EffectType.HEAL_OVER_TIME, itemId))
					return;
				res = usePotion(activeChar, 2031, 1);
				break;
			case 1061: // healing_potion, xml: 2032
				if(!isEffectReplaceable(activeChar, L2Effect.EffectType.HEAL_OVER_TIME, itemId))
					return;
				res = usePotion(activeChar, 2032, 1);
				break;
			case 1062: // haste_potion, xml: 2011
				res = usePotion(activeChar, 2011, 1);
				break;
			case 1374: // adv_quick_step_potion, xml: 2034
				res = usePotion(activeChar, 2034, 1);
				break;
			case 1375: // adv_swift_attack_potion, xml: 2035
				res = usePotion(activeChar, 2035, 1);
				break;
			case 1539: // greater_healing_potion, xml: 2037
				if (!isEffectReplaceable(activeChar, L2Effect.EffectType.HEAL_OVER_TIME, itemId))
					return;
				res = usePotion(activeChar, 2037, 1);
				break;
			case 1540: // quick_healing_potion, xml: 2038
				res = usePotion(activeChar, 2038, 1);
				break;
			case 5591: // CP
				res = usePotion(activeChar, 2166, 1);
				break;
			case 5592: // Greater CP
				res = usePotion(activeChar, 2166, 2);
				break;
			case 6035: // Magic Haste Potion, xml: 2169
				res = usePotion(activeChar, 2169, 1);
				break;
			case 6036: // Greater Magic Haste Potion, xml: 2169
				res = usePotion(activeChar, 2169, 2);
				break;

			// ELIXIR
			case 8622:
			case 8623:
			case 8624:
			case 8625:
			case 8626:
			case 8627:
				// elixir of Life
				if(itemId == 8622 && activeChar.getExpertiseIndex() == 0 || 
					itemId == 8623 && activeChar.getExpertiseIndex() == 1 || 
					itemId == 8624 && activeChar.getExpertiseIndex() == 2 || 
					itemId == 8625 && activeChar.getExpertiseIndex() == 3 || 
					itemId == 8626 && activeChar.getExpertiseIndex() == 4 || 
					itemId == 8627 && activeChar.getExpertiseIndex() == 5)
				{
					res = usePotion(activeChar, 2287, (activeChar.getExpertiseIndex() + 1));
				}
				else
				{
					sm = new SystemMessage(SystemMessageId.INCOMPATIBLE_ITEM_GRADE); // INCOMPATIBLE_ITEM_GRADE
					sm.addItemName(itemId);
					activeChar.sendPacket(sm);
					sm = null;

					return;
				}
				break;
			case 8628:
			case 8629:
			case 8630:
			case 8631:
			case 8632:
			case 8633:
				// elixir of Strength
				if(itemId == 8628 && activeChar.getExpertiseIndex() == 0 || 
					itemId == 8629 && activeChar.getExpertiseIndex() == 1 || 
					itemId == 8630 && activeChar.getExpertiseIndex() == 2 || 
					itemId == 8631 && activeChar.getExpertiseIndex() == 3 || 
					itemId == 8632 && activeChar.getExpertiseIndex() == 4 || 
					itemId == 8633 && activeChar.getExpertiseIndex() == 5)
				{
					res = usePotion(activeChar, 2288, (activeChar.getExpertiseIndex() + 1));
				}
				else
				{
					sm = new SystemMessage(SystemMessageId.INCOMPATIBLE_ITEM_GRADE); // INCOMPATIBLE_ITEM_GRADE
					sm.addItemName(itemId);
					activeChar.sendPacket(sm);
					sm = null;

					return;
				}
				break;
			case 8634:
			case 8635:
			case 8636:
			case 8637:
			case 8638:
			case 8639:
				// elixir of cp
				if(itemId == 8634 && activeChar.getExpertiseIndex() == 0 || 
					itemId == 8635 && activeChar.getExpertiseIndex() == 1 || 
					itemId == 8636 && activeChar.getExpertiseIndex() == 2 || 
					itemId == 8637 && activeChar.getExpertiseIndex() == 3 || 
					itemId == 8638 && activeChar.getExpertiseIndex() == 4 || 
					itemId == 8639 && activeChar.getExpertiseIndex() == 5)
				{
					res = usePotion(activeChar, 2289, (activeChar.getExpertiseIndex() + 1));
				}
				else
				{
					sm = new SystemMessage(SystemMessageId.INCOMPATIBLE_ITEM_GRADE); // INCOMPATIBLE_ITEM_GRADE
					sm.addItemName(itemId);
					activeChar.sendPacket(sm);
					sm = null;

					return;
				}
				break;

			// VALAKAS AMULETS
			case 6652: // Amulet Protection of Valakas
				res = usePotion(activeChar, 2231, 1);
				break;
			case 6653: // Amulet Flames of Valakas
				res = usePotion(activeChar, 2233, 1);
				break;
			case 6654: // Amulet Flames of Valakas
				res = usePotion(activeChar, 2233, 1);
				break;
			case 6655: // Amulet Slay Valakas
				res = usePotion(activeChar, 2232, 1);
				break;

			// HERBS
			case 8600: // Herb of Life
				res = usePotion(activeChar, 2278, 1);
				break;
			case 8601: // Greater Herb of Life
				res = usePotion(activeChar, 2278, 2);
				break;
			case 8602: // Superior Herb of Life
				res = usePotion(activeChar, 2278, 3);
				break;
			case 8603: // Herb of Mana
				res = usePotion(activeChar, 2279, 1);
				break;
			case 8604: // Greater Herb of Mane
				res = usePotion(activeChar, 2279, 2);
				break;
			case 8605: // Superior Herb of Mane
				res = usePotion(activeChar, 2279, 3);
				break;
			case 8606: // Herb of Strength
				res = usePotion(activeChar, 2280, 1);
				break;
			case 8607: // Herb of Magic
				res = usePotion(activeChar, 2281, 1);
				break;
			case 8608: // Herb of Atk. Spd.
				res = usePotion(activeChar, 2282, 1);
				break;
			case 8609: // Herb of Casting Spd.
				res = usePotion(activeChar, 2283, 1);
				break;
			case 8610: // Herb of Critical Attack
				res = usePotion(activeChar, 2284, 1);
				break;
			case 8611: // Herb of Speed
				res = usePotion(activeChar, 2285, 1);
				break;
			case 8612: // Herb of Warrior
				res = usePotion(activeChar, 2280, 1);// Herb of Strength
				res = usePotion(activeChar, 2282, 1);// Herb of Atk. Spd
				res = usePotion(activeChar, 2284, 1);// Herb of Critical Attack
				break;
			case 8613: // Herb of Mystic
				res = usePotion(activeChar, 2281, 1);// Herb of Magic
				res = usePotion(activeChar, 2283, 1);// Herb of Casting Spd.
				break;
			case 8614: // Herb of Warrior
				res = usePotion(activeChar, 2278, 3);// Superior Herb of Life
				res = usePotion(activeChar, 2279, 3);// Superior Herb of Mana
				break;

			// FISHERMAN POTIONS
			case 8193: // Fisherman's Potion - Green
				if(activeChar.getSkillLevel(1315) <= 3)
				{
					playable.destroyItem("Consume", item.getObjectId(), 1, null, false);
					playable.sendPacket(new SystemMessage(SystemMessageId.NOTHING_HAPPENED));
					return;
				}
				res = usePotion(activeChar, 2274, 1);
				break;
			case 8194: // Fisherman's Potion - Jade
				if(activeChar.getSkillLevel(1315) <= 6)
				{
					playable.destroyItem("Consume", item.getObjectId(), 1, null, false);
					playable.sendPacket(new SystemMessage(SystemMessageId.NOTHING_HAPPENED));
					return;
				}
				res = usePotion(activeChar, 2274, 2);
				break;
			case 8195: // Fisherman's Potion - Blue
				if(activeChar.getSkillLevel(1315) <= 9)
				{
					playable.destroyItem("Consume", item.getObjectId(), 1, null, false);
					playable.sendPacket(new SystemMessage(SystemMessageId.NOTHING_HAPPENED));
					return;
				}
				res = usePotion(activeChar, 2274, 3);
				break;
			case 8196: // Fisherman's Potion - Yellow
				if(activeChar.getSkillLevel(1315) <= 12)
				{
					playable.destroyItem("Consume", item.getObjectId(), 1, null, false);
					playable.sendPacket(new SystemMessage(SystemMessageId.NOTHING_HAPPENED));
					return;
				}
				res = usePotion(activeChar, 2274, 4);
				break;
			case 8197: // Fisherman's Potion - Orange
				if(activeChar.getSkillLevel(1315) <= 15)
				{
					playable.destroyItem("Consume", item.getObjectId(), 1, null, false);
					playable.sendPacket(new SystemMessage(SystemMessageId.NOTHING_HAPPENED));
					return;
				}
				res = usePotion(activeChar, 2274, 5);
				break;
			case 8198: // Fisherman's Potion - Purple
				if(activeChar.getSkillLevel(1315) <= 18)
				{
					playable.destroyItem("Consume", item.getObjectId(), 1, null, false);
					playable.sendPacket(new SystemMessage(SystemMessageId.NOTHING_HAPPENED));
					return;
				}
				res = usePotion(activeChar, 2274, 6);
				break;
			case 8199: // Fisherman's Potion - Red
				if(activeChar.getSkillLevel(1315) <= 21)
				{
					playable.destroyItem("Consume", item.getObjectId(), 1, null, false);
					playable.sendPacket(new SystemMessage(SystemMessageId.NOTHING_HAPPENED));
					return;
				}
				res = usePotion(activeChar, 2274, 7);
				break;
			case 8200: // Fisherman's Potion - White
				if(activeChar.getSkillLevel(1315) <= 24)
				{
					playable.destroyItem("Consume", item.getObjectId(), 1, null, false);
					playable.sendPacket(new SystemMessage(SystemMessageId.NOTHING_HAPPENED));
					return;
				}
				res = usePotion(activeChar, 2274, 8);
				break;
			case 8201: // Fisherman's Potion - Black
				res = usePotion(activeChar, 2274, 9);
				break;
			case 8202: // Fishing Potion
				res = usePotion(activeChar, 2275, 1);
				break;
			default:
		}

		activeChar = null;

		if(res)
		{
			playable.destroyItem("Consume", item.getObjectId(), 1, null, false);
		}
	}

	private boolean isEffectReplaceable(L2PcInstance activeChar, Enum<EffectType> effectType, int itemId)
	{
		L2Effect[] effects = activeChar.getAllEffects();

		if (effects == null)
			return true;

		for(L2Effect e : effects)
		{
			if(e.getEffectType() == effectType)
			{
				if (e.getSkill().isPotion())
				{
					// One can reuse pots after 2/3 of their duration is over.
					// It would be faster to check if its > 10 but that would screw custom pot durations...
					if(e.getTaskTime() > e.getSkill().getBuffDuration() * 67 / 100000)
						return true;
					sm = new SystemMessage(SystemMessageId.S1_NOT_AVAILABLE);
					sm.addItemName(itemId);
					activeChar.sendPacket(sm);
					sm = null;

					return false;
				}
			}
		}
		return true;
	}

	public boolean usePotion(L2PcInstance activeChar, int magicId, int level)
	{
		if (activeChar.isCastingNow() && magicId > 2277 && magicId < 2285)
		{
			_herbstask += 100;
			ThreadPoolManager.getInstance().scheduleAi(new HerbTask(activeChar, magicId, level), _herbstask);
		}
		else
		{
			if (magicId > 2277 && magicId < 2285 && _herbstask >= 100)
			{
				_herbstask -= 100;
			}
			L2Skill skill = SkillTable.getInstance().getInfo(magicId, level);
			if (skill != null)
			{
				if (activeChar.isSkillDisabled(skill.getId()))
				{
					sm = new SystemMessage(SystemMessageId.S1_NOT_AVAILABLE);
					sm.addSkillName(skill.getId(),skill.getLevel());
					activeChar.sendPacket(sm);
					return false;
				}
				L2Object[] targets = skill.getTargetList(activeChar);
				try
				{
					@SuppressWarnings("cast")
					int reuseDelay = (int)(skill.getReuseDelay());
							
					ISkillHandler handler = SkillHandler.getInstance().getSkillHandler(skill.getSkillType());
					sm = new SystemMessage(SystemMessageId.USE_S1);
					sm.addSkillName(magicId,skill.getLevel());
					activeChar.sendPacket(sm);			
					activeChar.broadcastPacket(new MagicSkillUser(activeChar, activeChar, magicId, level, 0, reuseDelay));
					
					if (handler != null)
						handler.useSkill(activeChar, skill, targets);
					else
						skill.useSkill(activeChar, targets);
					if(reuseDelay > 200)
						activeChar.disableSkill(skill.getId(), reuseDelay);
					
				}
				catch(Exception e){}
				
				if (!((activeChar.isParalyzed() || activeChar.isFakeDeath()) && !skill.isPotion()))
					return true;
			}
		}
		return false;
	}


	public int[] getItemIds()
	{
		return ITEM_IDS;
	}
}
