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
package com.it.br.gameserver.network.clientpackets;

import com.it.br.Config;
import com.it.br.gameserver.datatables.xml.L2PetDataTable;
import com.it.br.gameserver.handler.IItemHandler;
import com.it.br.gameserver.handler.ItemHandler;
import com.it.br.gameserver.model.L2ItemInstance;
import com.it.br.gameserver.model.actor.instance.L2PcInstance;
import com.it.br.gameserver.model.actor.instance.L2PetInstance;
import com.it.br.gameserver.network.SystemMessageId;
import com.it.br.gameserver.network.serverpackets.PetInfo;
import com.it.br.gameserver.network.serverpackets.PetItemList;
import com.it.br.gameserver.network.serverpackets.SystemMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class RequestPetUseItem extends L2GameClientPacket
{
	private static Logger _log = LoggerFactory.getLogger(RequestPetUseItem.class);
	private static final String _C__8A_REQUESTPETUSEITEM = "[C] 8a RequestPetUseItem";

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

		L2PetInstance pet = (L2PetInstance)activeChar.getPet();

		if (pet == null)
			return;

		L2ItemInstance item = pet.getInventory().getItemByObjectId(_objectId);

                if (item == null)
                        return;

                if (item.isWear())
                        return;

		int itemId = item.getItemId();

		if (activeChar.isAlikeDead() || (activeChar.isDead() || pet.isDead()))
                {
			SystemMessage sm = new SystemMessage(SystemMessageId.S1_CANNOT_BE_USED);
			sm.addItemName(item.getItemId());
			activeChar.sendPacket(sm);
			sm = null;
			return;
		}

		if (Config.DEBUG)
            _log.trace(activeChar.getObjectId()+": pet use item " + _objectId);

		//check if the item matches the pet
		if (item.isEquipable())
		{
			if (L2PetDataTable.isWolf(pet.getNpcId()) && // wolf
                    item.getItem().isForWolf())
			{
				useItem(pet, item, activeChar);
				return;
			}
			else if (L2PetDataTable.isHatchling(pet.getNpcId()) && // hatchlings
                        item.getItem().isForHatchling())
			{
				useItem(pet, item, activeChar);
				return;
			}
            else if (L2PetDataTable.isStrider(pet.getNpcId()) && // striders
                    item.getItem().isForStrider())
            {
                useItem(pet, item, activeChar);
                return;
            }
            else if (L2PetDataTable.isBaby(pet.getNpcId()) && // baby pets (buffalo, cougar, kookaboora)
                    item.getItem().isForBabyPet())
            {
                useItem(pet, item, activeChar);
                return;
            }
			else
			{
				activeChar.sendPacket(new SystemMessage(SystemMessageId.ITEM_NOT_FOR_PETS));
                                return;
			}
		}
		else if (L2PetDataTable.isPetFood(itemId))
		{
			if (L2PetDataTable.isWolf(pet.getNpcId()) && L2PetDataTable.isWolfFood(itemId))
			{
				feed(activeChar, pet, item);
				return;
			}
			if (L2PetDataTable.isSinEater(pet.getNpcId()) && L2PetDataTable.isSinEaterFood(itemId))
			{
				feed(activeChar, pet, item);
				return;
			}
			else if (L2PetDataTable.isHatchling(pet.getNpcId()) && L2PetDataTable.isHatchlingFood(itemId))
			{
				feed(activeChar, pet, item);
				return;
			}
			else if (L2PetDataTable.isStrider(pet.getNpcId()) && L2PetDataTable.isStriderFood(itemId))
			{
				feed(activeChar, pet, item);
				return;
			}
			else if (L2PetDataTable.isWyvern(pet.getNpcId()) && L2PetDataTable.isWyvernFood(itemId))
			{
				feed(activeChar, pet, item);
				return;
			}
			else if (L2PetDataTable.isBaby(pet.getNpcId()) && L2PetDataTable.isBabyFood(itemId))
			{
				feed(activeChar, pet, item);
			}
		}

	    IItemHandler handler = ItemHandler.getInstance().getItemHandler(item.getItemId());

	    if (handler != null)
		{
			useItem(pet, item, activeChar);
		}
		else
		{
			SystemMessage sm = new SystemMessage(SystemMessageId.ITEM_NOT_FOR_PETS);
			activeChar.sendPacket(sm);
		}

		return;
	}

	private synchronized void useItem(L2PetInstance pet, L2ItemInstance item, L2PcInstance activeChar)
	{
		if (item.isEquipable())
		{
			if (item.isEquipped())
				pet.getInventory().unEquipItemInSlot(item.getEquipSlot());
			else
				pet.getInventory().equipItem(item);

			PetItemList pil = new PetItemList(pet);
			activeChar.sendPacket(pil);

			PetInfo pi = new PetInfo(pet);
			activeChar.sendPacket(pi);
			// The PetInfo packet wipes the PartySpelled (list of active spells' icons).  Re-add them
			pet.updateEffectIcons(true);
		}
		else
		{
			//_log.trace("item not equipable id:"+ item.getItemId());
		    IItemHandler handler = ItemHandler.getInstance().getItemHandler(item.getItemId());

		    if (handler == null)
		        _log.warn("no itemhandler registered for itemId:" + item.getItemId());
		    else
		        handler.useItem(pet, item);
		}
	}

	/**
	 * When fed by owner double click on food from pet inventory. <BR><BR>
	 *
	 * <FONT COLOR=#FF0000><B> <U>Caution</U> : 1 food = 100 points of currentFed</B></FONT><BR><BR>
	 */
	private void feed(L2PcInstance player, L2PetInstance pet, L2ItemInstance item)
	{
		// if pet has food in inventory
		if (pet.destroyItem("Feed", item.getObjectId(), 1, pet, false))
                pet.setCurrentFed(pet.getCurrentFed() + 100);
		pet.broadcastStatusUpdate();
	}

	/* (non-Javadoc)
	 * @see com.it.br.gameserver.network.clientpackets.ClientBasePacket#getType()
	 */

	@Override
	public String getType()
	{
		return _C__8A_REQUESTPETUSEITEM;
	}
}
