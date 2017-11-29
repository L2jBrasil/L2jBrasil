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
package com.it.br.gameserver.network.clientpackets;

import static com.it.br.configuration.Configurator.getSettings;

import java.util.Arrays;
import java.util.logging.Logger;

import com.it.br.Config;
import com.it.br.configuration.settings.L2JBrasilSettings;
import com.it.br.configuration.settings.L2JModsSettings;
import com.it.br.gameserver.handler.IItemHandler;
import com.it.br.gameserver.handler.ItemHandler;
import com.it.br.gameserver.instancemanager.CastleManager;
import com.it.br.gameserver.instancemanager.ClanHallManager;
import com.it.br.gameserver.model.Inventory;
import com.it.br.gameserver.model.L2Clan;
import com.it.br.gameserver.model.L2Effect;
import com.it.br.gameserver.model.L2ItemInstance;
import com.it.br.gameserver.model.L2Skill;
import com.it.br.gameserver.model.actor.instance.L2PcInstance;
import com.it.br.gameserver.network.SystemMessageId;
import com.it.br.gameserver.network.serverpackets.ActionFailed;
import com.it.br.gameserver.network.serverpackets.InventoryUpdate;
import com.it.br.gameserver.network.serverpackets.ItemList;
import com.it.br.gameserver.network.serverpackets.ShowCalculator;
import com.it.br.gameserver.network.serverpackets.SystemMessage;
import com.it.br.gameserver.templates.L2ArmorType;
import com.it.br.gameserver.templates.L2Item;
import com.it.br.gameserver.templates.L2Weapon;
import com.it.br.gameserver.templates.L2WeaponType;
import com.it.br.gameserver.util.Util;

public final class UseItem extends L2GameClientPacket
{
	private static Logger _log = Logger.getLogger(UseItem.class.getName());
	private static final String _C__14_USEITEM = "[C] 14 UseItem";
	private int _objectId;

	@Override
	protected void readImpl()
	{
		_objectId = readD();
	}

	@Override
	protected void runImpl()
	{
		L2PcInstance activeChar = getClient().getActiveChar();
		if (activeChar == null)
                return;

		if (!getClient().getFloodProtectors().getUseItem().tryPerformAction("UseItem"))
		{
			activeChar.sendMessage("You can not Use Items so fast!");
			return;
		}

		L2ItemInstance item = activeChar.getInventory().getItemByObjectId(_objectId);
		if (activeChar.getPrivateStoreType() != 0 && item.getItemId() != getSettings(L2JModsSettings.class).getLogoutItemId())
		{
			activeChar.sendPacket(new SystemMessage(SystemMessageId.CANNOT_TRADE_DISCARD_DROP_ITEM_WHILE_IN_SHOPMODE));
			activeChar.sendPacket(new ActionFailed());
			return;
		}

		if (!activeChar.isHero() && !activeChar.isGM() && !activeChar.isFakeHero() && (item.getItemId() == 6842 || (item.getItemId() >= 6611 && item.getItemId() <= 6621)))
		{
			activeChar.sendMessage("His weapon was destroyed  successfully you do not have requirements to use it");
			activeChar.destroyItem("Destroy", item.getObjectId(), 1, null, false);
			activeChar.sendPacket(new ActionFailed());
			return;
		}

		if (activeChar.getActiveTradeList() != null) 
			activeChar.cancelActiveTrade();

		// NOTE: disabled due to deadlocks
		// synchronized (activeChar.getInventory())

		if (item == null)
			return;

		if (item.isWear())
		{
			// No unequipping wear-items
			return;
		}

		int itemId = item.getItemId();
		/*
		 * Alt game - Karma punishment // SOE
		 * 736  	Scroll of Escape
		 * 1538  	Blessed Scroll of Escape
		 * 1829  	Scroll of Escape: Clan Hall
		 * 1830  	Scroll of Escape: Castle
		 * 3958  	L2Day - Blessed Scroll of Escape
		 * 5858  	Blessed Scroll of Escape: Clan Hall
		 * 5859  	Blessed Scroll of Escape: Castle
		 * 6663  	Scroll of Escape: Orc Village
		 * 6664  	Scroll of Escape: Silenos Village
		 * 7117  	Scroll of Escape to Talking Island
		 * 7118  	Scroll of Escape to Elven Village
		 * 7119  	Scroll of Escape to Dark Elf Village
		 * 7120  	Scroll of Escape to Orc Village
		 * 7121  	Scroll of Escape to Dwarven Village
		 * 7122  	Scroll of Escape to Gludin Village
		 * 7123  	Scroll of Escape to the Town of Gludio
		 * 7124  	Scroll of Escape to the Town of Dion
		 * 7125  	Scroll of Escape to Floran
		 * 7126  	Scroll of Escape to Giran Castle Town
		 * 7127  	Scroll of Escape to Hardin's Private Academy
		 * 7128  	Scroll of Escape to Heine
		 * 7129  	Scroll of Escape to the Town of Oren
		 * 7130  	Scroll of Escape to Ivory Tower
		 * 7131  	Scroll of Escape to Hunters Village
		 * 7132  	Scroll of Escape to Aden Castle Town
		 * 7133  	Scroll of Escape to the Town of Goddard
		 * 7134  	Scroll of Escape to the Rune Township
		 * 7135  	Scroll of Escape to the Town of Schuttgart.
		 * 7554  	Scroll of Escape to Talking Island
		 * 7555  	Scroll of Escape to Elven Village
		 * 7556  	Scroll of Escape to Dark Elf Village
		 * 7557  	Scroll of Escape to Orc Village
		 * 7558  	Scroll of Escape to Dwarven Village
		 * 7559  	Scroll of Escape to Giran Castle Town
		 * 7618  	Scroll of Escape - Ketra Orc Village
		 * 7619  	Scroll of Escape - Varka Silenos Village
		 */

		if (!Config.ALT_GAME_KARMA_PLAYER_CAN_TELEPORT && activeChar.getKarma() > 0 && (itemId == 736 || itemId == 1538 || itemId == 1829 || itemId == 1830
				|| itemId == 3958 || itemId == 5858 || itemId == 5859 || itemId == 6663
				|| itemId == 6664 || (itemId >= 7117 && itemId <= 7135)	|| (itemId >= 7554 && itemId <= 7559) || itemId == 7618 || itemId == 7619))
			return;

		if((itemId == 5858) && (ClanHallManager.getInstance().getClanHallByOwner(activeChar.getClan()) == null))
		{
			activeChar.sendMessage("Blessed Scroll of Escape: Clan Hall cannot be used due to unsuitable terms.");
			return;
		}

		if((itemId == 5859) && (ClanHallManager.getInstance().getClanHallByOwner(activeChar.getClan()) == null))
		{
			activeChar.sendMessage("Blessed Scroll of Escape: Castle cannot be used due to unsuitable terms.");
			return;
		}

		L2Clan cl = activeChar.getClan();
		if ((cl == null || cl.getHasCastle() == 0) && itemId == 7015 && Config.CASTLE_SHIELD)
		{
			SystemMessage sm = new SystemMessage(SystemMessageId.CANNOT_EQUIP_ITEM_DUE_TO_BAD_CONDITION);
			activeChar.sendPacket(sm);
			sm = null;
			return;
		}

		if ((cl == null || cl.getHasHideout() == 0) && itemId == 6902 && Config.CLANHALL_SHIELD)
		{
			SystemMessage sm = new SystemMessage(SystemMessageId.CANNOT_EQUIP_ITEM_DUE_TO_BAD_CONDITION);
			activeChar.sendPacket(sm);
			sm = null;
			return;
		}

		if (itemId >= 7860 && itemId <= 7879 && Config.APELLA_ARMORS && (cl == null || activeChar.getPledgeClass() < 5))
		{
			SystemMessage sm = new SystemMessage(SystemMessageId.CANNOT_EQUIP_ITEM_DUE_TO_BAD_CONDITION);
			activeChar.sendPacket(sm);
			sm = null;
			return;
		}

		if (itemId >= 7850 && itemId <= 7859 && Config.OATH_ARMORS && cl == null)
		{
			SystemMessage sm = new SystemMessage(SystemMessageId.CANNOT_EQUIP_ITEM_DUE_TO_BAD_CONDITION);
			activeChar.sendPacket(sm);
			sm = null;
			return;
		}

		if (itemId == 6841 && Config.CASTLE_CROWN && (cl == null || cl.getHasCastle() == 0 || !activeChar.isClanLeader()))
		{
			SystemMessage sm = new SystemMessage(SystemMessageId.CANNOT_EQUIP_ITEM_DUE_TO_BAD_CONDITION);
			activeChar.sendPacket(sm);
			sm = null;
			return;
		}

		if (Config.CASTLE_CIRCLETS && (itemId >= 6834 && itemId <= 6840 || itemId == 8182 || itemId == 8183))
		{
			if (cl == null)
			{
				SystemMessage sm = new SystemMessage(SystemMessageId.CANNOT_EQUIP_ITEM_DUE_TO_BAD_CONDITION);
				activeChar.sendPacket(sm);
				sm = null;
				return;
			}
			else
			{
				int circletId = CastleManager.getInstance().getCircletByCastleId(cl.getHasCastle());
				if (activeChar.getPledgeType() == -1 || circletId != itemId)
				{
					SystemMessage sm = new SystemMessage(SystemMessageId.CANNOT_EQUIP_ITEM_DUE_TO_BAD_CONDITION);
					activeChar.sendPacket(sm);
					sm = null;
					return;
				}
			}
		}

		// Items that cannot be used
		if (itemId == 57)
			return;

		L2Weapon curwep = activeChar.getActiveWeaponItem();
		if (curwep != null)
		{
			if ((curwep.getItemType() == L2WeaponType.DUAL) && (item.getItemType() == L2WeaponType.NONE))
			{ 
				activeChar.sendMessage("[Server Protection]: Wear the shield first.");
				return; 
			}
			else if ((curwep.getItemType() == L2WeaponType.BOW) && (item.getItemType() == L2WeaponType.NONE))
			{ 
				activeChar.sendMessage("[Server Protection]: Wear the shield first.");
				return; 
			}
			else if ((curwep.getItemType() == L2WeaponType.BIGBLUNT) && (item.getItemType() == L2WeaponType.NONE))
			{ 
				activeChar.sendMessage("[Server Protection]: Wear the shield first.");
				return; 
			}
			else if ((curwep.getItemType() == L2WeaponType.BIGSWORD) && (item.getItemType() == L2WeaponType.NONE))
			{ 
				activeChar.sendMessage("[Server Protection]: Wear the shield first.");
				return; 
			}
			else if ((curwep.getItemType() == L2WeaponType.POLE) && (item.getItemType() == L2WeaponType.NONE))
			{ 
				activeChar.sendMessage("[Server Protection]: Wear the shield first.");
				return; 
			}
			else if ((curwep.getItemType() == L2WeaponType.DUALFIST) && (item.getItemType() == L2WeaponType.NONE))
			{ 
				activeChar.sendMessage("[Server Protection]: Wear the shield first.");
				return; 
			}
			else if ((curwep.getItemType() == L2WeaponType.ROD) && (item.getItemType() == L2WeaponType.NONE))
			{
				activeChar.sendMessage("[Server Protection]: Wear the shield first.");
				return;
			}
			else if ((curwep.getItemType() == L2WeaponType.DAGGER) && (item.getItemType() == L2WeaponType.NONE))
			{
				activeChar.sendMessage("[Server Protection]: Wear the shield first.");
				return;
			}
			else if ((curwep.getItemType() == L2WeaponType.SWORD) && (item.getItemType() == L2WeaponType.NONE))
			{
				activeChar.sendMessage("[Server Protection]: Wear the shield first.");
				return;
			}
		}

		if (activeChar.isFishing() && (itemId < 6535 || itemId > 6540))
		{
			// You cannot do anything else while fishing
			SystemMessage sm = new SystemMessage(SystemMessageId.CANNOT_DO_WHILE_FISHING_3);
			getClient().getActiveChar().sendPacket(sm);
			sm = null;
			return;
		}

		// Char cannot use item when dead
		if (activeChar.isDead())
		{
			SystemMessage sm = new SystemMessage(SystemMessageId.S1_CANNOT_BE_USED);
			sm.addItemName(itemId);
			getClient().getActiveChar().sendPacket(sm);
			sm = null;
			return;
		}

		// Char cannot use pet items
		if (item.getItem().isForWolf() || item.getItem().isForHatchling() || item.getItem().isForStrider() || item.getItem().isForBabyPet())
		{
			SystemMessage sm = new SystemMessage(SystemMessageId.CANNOT_EQUIP_PET_ITEM); // You cannot equip a pet item.
			sm.addItemName(itemId);
			getClient().getActiveChar().sendPacket(sm);
			sm = null;
			return;
		}

		if (Config.DEBUG)
			_log.finest(activeChar.getObjectId() + ": use item " + _objectId);

		if (item.isEquipable())
		{
			if (Config.OVER_ENCHANT_PROTECTION_ENABLED)
			{
				if (item.getItem() instanceof L2Weapon)
				{
					if (!activeChar.isGM() && item.getEnchantLevel() > Config.ENCHANT_MAX_ALLOWED_WEAPON || item.getEnchantLevel() > Config.ENCHANT_MAX_ALLOWED_ARMOR || item.getEnchantLevel() > Config.ENCHANT_MAX_ALLOWED_JEWELRY)
					{
						Util.handleIllegalPlayerAction(activeChar, "Player " + activeChar.getName() + " has an item Overenchanted ", Config.DEFAULT_PUNISH);
						activeChar.sendMessage("You have been kicked for using an item overenchanted!");
						activeChar.closeNetConnection();
						return;
					}
				}
			}

			// No unequipping/equipping while the player is in special conditions
			if (activeChar.isStunned() || activeChar.isSleeping() || activeChar.isParalyzed() || activeChar.isAlikeDead())
			{
				activeChar.sendMessage("Your status does not allow you to do that.");
				return;
			}

			// If Player isn't Gm and have Enchant item > 10 he will be jailed!
			if (!activeChar.isGM() && item.getEnchantLevel() > Config.MAX_ITEM_ENCHANT_KICK)
			{
				activeChar.sendMessage("You have been kicked for using an item over +11!");
				Util.handleIllegalPlayerAction(activeChar, "Player " + activeChar.getName() + " have item Overenchanted ", Config.DEFAULT_PUNISH);
				activeChar.closeNetConnection();
				return;
			}

			int bodyPart = item.getItem().getBodyPart();
			// Prevent player to remove the weapon on special conditions
			if ((activeChar.isCastingNow() || activeChar.isMounted() || activeChar.isAfraid()) && (bodyPart == L2Item.SLOT_LR_HAND || bodyPart == L2Item.SLOT_L_HAND || bodyPart == L2Item.SLOT_R_HAND))
				return;
			
	        L2JBrasilSettings l2jBrasilSettings = getSettings(L2JBrasilSettings.class);

			if (l2jBrasilSettings.isArgumentsRetailLike())
			{
				if (bodyPart == L2Item.SLOT_L_HAND)
				{
					if (activeChar.getInventory().getPaperdollItemByL2ItemId(0x4000) != null && activeChar.getInventory().getPaperdollItemByL2ItemId(0x4000).getAugmentation() !=null)
					{
						activeChar.getInventory().getPaperdollItemByL2ItemId(0x4000).getAugmentation().removeBonus(activeChar);
					}
				}

				L2Effect[] effects = activeChar.getAllEffects();
				for (L2Effect e : effects)
				{
					if ((e.getSkill().getSkillType() == L2Skill.SkillType.BUFF ||
							e.getSkill().getSkillType() == L2Skill.SkillType.HEAL_PERCENT ||
							e.getSkill().getSkillType() == L2Skill.SkillType.REFLECT)
							&& (e.getSkill().getId() >= 3124 && e.getSkill().getId() <= 3259)
							&& (bodyPart == L2Item.SLOT_LR_HAND
							|| bodyPart == L2Item.SLOT_L_HAND
							|| bodyPart == L2Item.SLOT_R_HAND))
					{
						activeChar.stopSkillEffects(e.getSkill().getId());
						break;
					}
				}
			}

			switch (bodyPart)
			{
				case L2Item.SLOT_LR_HAND:
				case L2Item.SLOT_L_HAND:
				case L2Item.SLOT_R_HAND:
				{
					if(item.getEnchantLevel() > Config.ENCHANT_MAX_ALLOWED_WEAPON && !activeChar.isGM())
					{
						activeChar.setAccountAccesslevel(-100); //ban
						activeChar.sendMessage("You have been banned for using an item wich is over enchanted!"); //message
						activeChar.closeNetConnection(); //kick
						return;
					}
					break;
				}
		
				case L2Item.SLOT_CHEST:
				case L2Item.SLOT_BACK:
				case L2Item.SLOT_GLOVES:
				case L2Item.SLOT_FEET:
				case L2Item.SLOT_HEAD:
				case L2Item.SLOT_FULL_ARMOR:
				case L2Item.SLOT_LEGS:
				{
					if(item.getEnchantLevel() > Config.ENCHANT_MAX_ALLOWED_ARMOR && !activeChar.isGM())
					{
						activeChar.setAccountAccesslevel(-100); //ban
						activeChar.sendMessage("You have been banned for using an item wich is over enchanted!"); //message
						activeChar.closeNetConnection(); //kick
						return;
					}
					break;
				}
				case L2Item.SLOT_R_EAR:
				case L2Item.SLOT_L_EAR:
				case L2Item.SLOT_NECK:
				case L2Item.SLOT_R_FINGER:
				case L2Item.SLOT_L_FINGER:
				{
					if(item.getEnchantLevel() > Config.ENCHANT_MAX_ALLOWED_JEWELRY && !activeChar.isGM())
					{
						activeChar.setAccountAccesslevel(-100); //ban
						activeChar.sendMessage("You have been banned for using an item wich is over enchanted!"); //message
						activeChar.closeNetConnection(); //kick
						return;
					}
					break;
				}
			}

			// Since c5 you can equip weapon again
			// Don't allow weapon/shield equipment if wearing formal wear
			if (activeChar.isWearingFormalWear() && (bodyPart == L2Item.SLOT_LR_HAND || bodyPart == L2Item.SLOT_L_HAND || bodyPart == L2Item.SLOT_R_HAND))
			{
				SystemMessage sm = new SystemMessage(SystemMessageId.CANNOT_USE_ITEMS_SKILLS_WITH_FORMALWEAR);
				activeChar.sendPacket(sm);
				return;
			} 

			// Don't allow weapon/shield equipment if a cursed weapon is equiped
			if (activeChar.isCursedWeaponEquipped() && ((bodyPart == L2Item.SLOT_LR_HAND || bodyPart == L2Item.SLOT_L_HAND || bodyPart == L2Item.SLOT_R_HAND) || itemId == 6408)) // Don't allow to put formal wear 
				return;	

			if (!l2jBrasilSettings.isDaggersUseHeavyEnabled())
			{
				if (activeChar.getClassId().getId() == 93 || activeChar.getClassId().getId() == 108 || activeChar.getClassId().getId() == 101 || activeChar.getClassId().getId() == 8 || activeChar.getClassId().getId() == 23 || activeChar.getClassId().getId() == 36) 
				{ 
					if (item.getItemType() == L2ArmorType.HEAVY) 
					{ 
						activeChar.sendPacket(new SystemMessage(SystemMessageId.CANNOT_EQUIP_ITEM_DUE_TO_BAD_CONDITION)); 
						return; 
					}    
				} 
			}

			if (!l2jBrasilSettings.isAnyClassUseLightEnabled())
			{
				if (l2jBrasilSettings.getNotAllowedUseLight().contains( activeChar.getClassId().getId()))
				{
					if (item.getItemType() == L2ArmorType.LIGHT)
					{
						activeChar.sendMessage("This class cannot use equip like Light.");
						return;
					}
				}
			}

			if (!l2jBrasilSettings.isAnyClassUseHeavyEnabled())
			{
				if (l2jBrasilSettings.getNotAllowedUseHeavy().contains( activeChar.getClassId().getId()))
				{
					if (item.getItemType() == L2ArmorType.HEAVY)
					{
						activeChar.sendMessage("This class cannot use equip like Heavy.");
						return;
					}
				}
			}

			if (activeChar.isInOlympiadMode() && (item.isHeroitem() || item.isOlyRestrictedItem() || item.isOlyRestrictedGradS()))
				return;

			// Don't allow weapon/shield hero equipment during Olympiads
			if (activeChar.isInOlympiadMode() && (bodyPart == L2Item.SLOT_LR_HAND || bodyPart == L2Item.SLOT_L_HAND || bodyPart == L2Item.SLOT_R_HAND) && item.isHeroitem())
				return;

			// Equip or unEquip
			L2ItemInstance[] items = null;
			boolean isEquiped = item.isEquipped();
			SystemMessage sm = null;
			L2ItemInstance old = activeChar.getInventory().getPaperdollItem(Inventory.PAPERDOLL_LRHAND);
			activeChar.checkSSMatch(item, old);
			if (old == null)
				old = activeChar.getInventory().getPaperdollItem(Inventory.PAPERDOLL_RHAND);

			if (isEquiped)
			{
				if (item.getEnchantLevel() > 0)
				{
					sm = new SystemMessage(SystemMessageId.EQUIPMENT_S1_S2_REMOVED);
					sm.addNumber(item.getEnchantLevel());
					sm.addItemName(itemId);
				}
				else
				{
					sm = new SystemMessage(SystemMessageId.S1_DISARMED);
					sm.addItemName(itemId);
				}
				activeChar.sendPacket(sm);

				// Remove augementation bonus on unequip
				if (item.isAugmented())
					item.getAugmentation().removeBonus(activeChar);

				int slot = activeChar.getInventory().getSlotFromItem(item);
				items = activeChar.getInventory().unEquipItemInBodySlotAndRecord(slot);
			}
			else
			{
				if (Config.ALT_DISABLE_BOW_CLASSES)
				{
					if(item.getItem() instanceof L2Weapon && ((L2Weapon)item.getItem()).getItemType() == L2WeaponType.BOW)
					{
						if(Config.DISABLE_BOW_CLASSES.contains(activeChar.getClassId().getId()))
						{
							activeChar.sendMessage("This item can not be equipped by your class");
							activeChar.sendPacket(ActionFailed.STATIC_PACKET);
							return;
						}
					}
				}
				int tempBodyPart = item.getItem().getBodyPart();
				L2ItemInstance tempItem = activeChar.getInventory().getPaperdollItemByL2ItemId(tempBodyPart);

				// remove augmentation stats for replaced items currently weapons only..
				if (tempItem != null && tempItem.isAugmented())
					tempItem.getAugmentation().removeBonus(activeChar);
				else if (tempBodyPart == 0x4000)
				{
					L2ItemInstance tempItem2 = activeChar.getInventory().getPaperdollItem(7);
					if (tempItem2 != null && tempItem2.isAugmented())
						tempItem2.getAugmentation().removeBonus(activeChar);

					tempItem2 = activeChar.getInventory().getPaperdollItem(8);
					if (tempItem2 != null && tempItem2.isAugmented())
						tempItem2.getAugmentation().removeBonus(activeChar);
				}

				//check if the item replaces a wear-item, dont allow an item to replace a wear-item
				if (tempItem != null && tempItem.isWear())
					return;

				else if (tempBodyPart == 0x4000) // left+right hand equipment
				{
					// this may not remove left OR right hand equipment
					tempItem = activeChar.getInventory().getPaperdollItem(7);
					if (tempItem != null && tempItem.isWear()) return;

					tempItem = activeChar.getInventory().getPaperdollItem(8);
					if (tempItem != null && tempItem.isWear()) return;
				}
				else if (tempBodyPart == 0x8000) // fullbody armor
				{
					// this may not remove chest or leggins
					tempItem = activeChar.getInventory().getPaperdollItem(10);
					if (tempItem != null && tempItem.isWear()) return;

					tempItem = activeChar.getInventory().getPaperdollItem(11);
					if (tempItem != null && tempItem.isWear()) return;
				}

				if (item.getEnchantLevel() > 0)
				{
					sm = new SystemMessage(SystemMessageId.S1_S2_EQUIPPED);
					sm.addNumber(item.getEnchantLevel());
					sm.addItemName(itemId);
				}
				else
				{
					sm = new SystemMessage(SystemMessageId.S1_EQUIPPED);
					sm.addItemName(itemId);
				}
				activeChar.sendPacket(sm);

				// Apply augementation bonus on equip
				if (item.isAugmented())
					item.getAugmentation().applyBonus(activeChar);

				items = activeChar.getInventory().equipItemAndRecord(item);

				// Consume mana - will start a task if required; returns if item is not a shadow item
				item.decreaseMana(false);
			}
			sm = null;

			activeChar.refreshExpertisePenalty();

			if (item.getItem().getType2() == L2Item.TYPE2_WEAPON)
				activeChar.checkIfWeaponIsAllowed();

			InventoryUpdate iu = new InventoryUpdate();
			iu.addItems(Arrays.asList(items));
			activeChar.sendPacket(iu);
			activeChar.abortAttack();
			activeChar.broadcastUserInfo();
		}
		else
		{
			L2Weapon weaponItem = activeChar.getActiveWeaponItem();
			int itemid = item.getItemId();
                //_log.finest("item not equipable id:"+ item.getItemId());
			if (itemid == 4393)
				activeChar.sendPacket(new ShowCalculator(4393));
                
			else if ((weaponItem != null && weaponItem.getItemType() == L2WeaponType.ROD)
					&& ((itemid >= 6519 && itemid <= 6527) || (itemid >= 7610 && itemid <= 7613) || (itemid >= 7807 && itemid <= 7809) || (itemid >= 8484 && itemid <= 8486) || (itemid >= 8505 && itemid <= 8513)))
			{
				activeChar.getInventory().setPaperdollItem(Inventory.PAPERDOLL_LHAND, item);
				activeChar.broadcastUserInfo();
				// Send a Server->Client packet ItemList to this L2PcInstance to update left hand equipement
				ItemList il = new ItemList(activeChar, false);
				sendPacket(il);
				return;
			}
			else
			{
				IItemHandler handler = ItemHandler.getInstance().getItemHandler(item.getItemId());
				if (handler == null)
					_log.warning("No item handler registered for item ID " + item.getItemId() + ".");
				else
					handler.useItem(activeChar, item);
			}
		}
	}

	@Override
	public String getType()
	{
		return _C__14_USEITEM;
	}
}
