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

import com.it.br.Config;
import com.it.br.gameserver.model.L2ItemInstance;
import com.it.br.gameserver.model.L2Skill;
import com.it.br.gameserver.model.L2World;
import com.it.br.gameserver.model.PcInventory;
import com.it.br.gameserver.model.actor.instance.L2PcInstance;
import com.it.br.gameserver.network.SystemMessageId;
import com.it.br.gameserver.network.serverpackets.ActionFailed;
import com.it.br.gameserver.network.serverpackets.InventoryUpdate;
import com.it.br.gameserver.network.serverpackets.ItemList;
import com.it.br.gameserver.network.serverpackets.StatusUpdate;
import com.it.br.gameserver.network.serverpackets.SystemMessage;
import com.it.br.gameserver.templates.L2Item;
import com.it.br.gameserver.util.IllegalPlayerAction;
import com.it.br.gameserver.util.Util;

public final class RequestCrystallizeItem extends L2GameClientPacket
{
	private static final String _C__72_REQUESTDCRYSTALLIZEITEM = "[C] 72 RequestCrystallizeItem";
	//private static Logger _log = Logger.getLogger(RequestCrystallizeItem.class.getName());

	private int _objectId;
	private int _count;


	@Override
	protected void readImpl()
	{
		_objectId = readD();
		_count = readD();
	}


	@Override
	protected void runImpl()
	{
		L2PcInstance activeChar = getClient().getActiveChar();

		if (activeChar == null)
		{
			_log.fine("RequestCrystalizeItem: activeChar was null");
			return;
		}

		if (_count <= 0)
		{
			Util.handleIllegalPlayerAction(activeChar,
					"[RequestCrystallizeItem] count <= 0! ban! oid: "
							+ _objectId + " owner: " + activeChar.getName(),
					IllegalPlayerAction.PUNISH_KICK);
			return;
		}

		if (activeChar.getPrivateStoreType() != 0
				|| activeChar.isInCrystallize())
		{
			activeChar
					.sendPacket(new SystemMessage(SystemMessageId.CANNOT_TRADE_DISCARD_DROP_ITEM_WHILE_IN_SHOPMODE));
			return;
		}

		int skillLevel = activeChar.getSkillLevel(L2Skill.SKILL_CRYSTALLIZE);
		if (skillLevel <= 0)
		{
			SystemMessage sm = new SystemMessage(SystemMessageId.CRYSTALLIZE_LEVEL_TOO_LOW);
			activeChar.sendPacket(sm);
			sm = null;
			ActionFailed af = new ActionFailed();
			activeChar.sendPacket(af);
			return;
		}

		PcInventory inventory = activeChar.getInventory();
		if (inventory != null)
		{
			L2ItemInstance item = inventory.getItemByObjectId(_objectId);
			if (item == null || item.isWear())
			{
				ActionFailed af = new ActionFailed();
				activeChar.sendPacket(af);
				return;
			}

			int itemId = item.getItemId();
			if ((itemId >= 6611 && itemId <= 6621) || itemId == 6842)
				return;

			if (_count > item.getCount())
			{
				_count = activeChar.getInventory().getItemByObjectId(_objectId)
						.getCount();
			}
		}

		L2ItemInstance itemToRemove = activeChar.getInventory()
				.getItemByObjectId(_objectId);
		if (itemToRemove == null || itemToRemove.isWear())
		{
			return;
		}
		if (!itemToRemove.getItem().isCrystallizable()
				|| (itemToRemove.getItem().getCrystalCount() <= 0)
				|| (itemToRemove.getItem().getCrystalType() == L2Item.CRYSTAL_NONE))
		{
			_log.warning("" + activeChar.getObjectId()
					+ " tried to crystallize "
					+ itemToRemove.getItem().getItemId());
			return;
		}

		// Check if the char can crystallize C items and return if false;
		if (itemToRemove.getItem().getCrystalType() == L2Item.CRYSTAL_C
				&& skillLevel <= 1)
		{
			SystemMessage sm = new SystemMessage(SystemMessageId.CRYSTALLIZE_LEVEL_TOO_LOW);
			activeChar.sendPacket(sm);
			sm = null;
			ActionFailed af = new ActionFailed();
			activeChar.sendPacket(af);
			return;
		}

		// Check if the user can crystallize B items and return if false;
		if (itemToRemove.getItem().getCrystalType() == L2Item.CRYSTAL_B
				&& skillLevel <= 2)
		{
			SystemMessage sm = new SystemMessage(SystemMessageId.CRYSTALLIZE_LEVEL_TOO_LOW);
			activeChar.sendPacket(sm);
			sm = null;
			ActionFailed af = new ActionFailed();
			activeChar.sendPacket(af);
			return;
		}

		// Check if the user can crystallize A items and return if false;
		if (itemToRemove.getItem().getCrystalType() == L2Item.CRYSTAL_A
				&& skillLevel <= 3)
		{
			SystemMessage sm = new SystemMessage(SystemMessageId.CRYSTALLIZE_LEVEL_TOO_LOW);
			activeChar.sendPacket(sm);
			sm = null;
			ActionFailed af = new ActionFailed();
			activeChar.sendPacket(af);
			return;
		}

		// Check if the user can crystallize S items and return if false;
		if (itemToRemove.getItem().getCrystalType() == L2Item.CRYSTAL_S
				&& skillLevel <= 4)
		{
			SystemMessage sm = new SystemMessage(SystemMessageId.CRYSTALLIZE_LEVEL_TOO_LOW);
			activeChar.sendPacket(sm);
			sm = null;
			ActionFailed af = new ActionFailed();
			activeChar.sendPacket(af);
			return;
		}

		activeChar.setInCrystallize(true);

		// unequip if needed
		if (itemToRemove.isEquipped())
		{
			L2ItemInstance[] unequiped = activeChar.getInventory()
					.unEquipItemInSlotAndRecord(itemToRemove.getEquipSlot());
			InventoryUpdate iu = new InventoryUpdate();
			for (int i = 0; i < unequiped.length; i++)
			{
				iu.addModifiedItem(unequiped[i]);
			}
			activeChar.sendPacket(iu);
			// activeChar.updatePDef();
			// activeChar.updatePAtk();
			// activeChar.updateMDef();
			// activeChar.updateMAtk();
			// activeChar.updateAccuracy();
			// activeChar.updateCriticalChance();
		}

		// remove from inventory
		L2ItemInstance removedItem = activeChar.getInventory().destroyItem("Crystalize", _objectId, _count, activeChar, null);

		// add crystals
		int crystalId = itemToRemove.getItem().getCrystalItemId();
		int crystalAmount = itemToRemove.getCrystalCount();
		L2ItemInstance createditem = activeChar.getInventory().addItem("Crystalize", crystalId, crystalAmount, activeChar,itemToRemove);

		SystemMessage sm = new SystemMessage(SystemMessageId.EARNED_S2_S1_S);
		sm.addItemName(crystalId);
		sm.addNumber(crystalAmount);
		activeChar.sendPacket(sm);
		sm = null;

		// send inventory update
		if (!Config.FORCE_INVENTORY_UPDATE)
		{
			InventoryUpdate iu = new InventoryUpdate();
			if (removedItem.getCount() == 0)
				iu.addRemovedItem(removedItem);
			else
				iu.addModifiedItem(removedItem);

			if (createditem.getCount() != crystalAmount)
				iu.addModifiedItem(createditem);
			else
				iu.addNewItem(createditem);

			activeChar.sendPacket(iu);
		} else
			activeChar.sendPacket(new ItemList(activeChar, false));

		// status & user info
		StatusUpdate su = new StatusUpdate(activeChar.getObjectId());
		su.addAttribute(StatusUpdate.CUR_LOAD, activeChar.getCurrentLoad());
		activeChar.sendPacket(su);

		activeChar.broadcastUserInfo();

		L2World world = L2World.getInstance();
		world.removeObject(removedItem);

		activeChar.setInCrystallize(false);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.it.br.gameserver.network.clientpackets.ClientBasePacket#getType()
	 */

	@Override
	public String getType()
	{
		return _C__72_REQUESTDCRYSTALLIZEITEM;
	}
}
