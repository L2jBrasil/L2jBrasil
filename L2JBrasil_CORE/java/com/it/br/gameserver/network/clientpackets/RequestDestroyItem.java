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
import com.it.br.gameserver.database.L2DatabaseFactory;
import com.it.br.gameserver.datatables.xml.L2PetDataTable;
import com.it.br.gameserver.instancemanager.CursedWeaponsManager;
import com.it.br.gameserver.model.L2ItemInstance;
import com.it.br.gameserver.model.L2World;
import com.it.br.gameserver.model.actor.instance.L2PcInstance;
import com.it.br.gameserver.network.SystemMessageId;
import com.it.br.gameserver.network.serverpackets.*;
import com.it.br.gameserver.util.Util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.logging.Level;

public final class RequestDestroyItem extends L2GameClientPacket
{
	private static final String _C__59_REQUESTDESTROYITEM = "[C] 59 RequestDestroyItem";
	//private static Logger _log = Logger.getLogger(RequestDestroyItem.class.getName());

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
		    return;

		if (!activeChar.getFloodProtectors().getCrystallizeItem().tryPerformAction("DestroyItem"))
		{
			activeChar.sendMessage("You can not Destroy Items so fast!");
			activeChar.sendPacket(new ActionFailed());
		 	return;
		}
		if(_count <= 0)
		{
			if (_count < 0) Util.handleIllegalPlayerAction(activeChar,"[RequestDestroyItem] count < 0! ban! oid: "+_objectId+" owner: "+activeChar.getName(),Config.DEFAULT_PUNISH);
			activeChar.sendPacket(new ActionFailed());
			return;
		}

		int count = _count;

        if (activeChar.getPrivateStoreType() != 0)
        {
            activeChar.sendPacket(new SystemMessage(SystemMessageId.CANNOT_TRADE_DISCARD_DROP_ITEM_WHILE_IN_SHOPMODE));
            activeChar.sendPacket(new ActionFailed());
            return;
        }

		L2ItemInstance itemToRemove = activeChar.getInventory().getItemByObjectId(_objectId);
		// if we cant find requested item, its actualy a cheat!
		if (itemToRemove == null) 
			return;

		if (itemToRemove.isAugmented()) 
		{ 
			activeChar.sendPacket(new SystemMessage(SystemMessageId.AUGMENTED_ITEM_CANNOT_BE_DISCARDED));
			activeChar.sendPacket(new ActionFailed());
			return;  
		} 
		
		// Cannot discard item that the skill is consumming
		if (activeChar.isCastingNow())
		{
			if (activeChar.getCurrentSkill() != null && activeChar.getCurrentSkill().getSkill().getItemConsumeId() == itemToRemove.getItemId())
			{
	            activeChar.sendPacket(new SystemMessage(SystemMessageId.CANNOT_DISCARD_THIS_ITEM));
	            activeChar.sendPacket(new ActionFailed());
	            return;
			}
		}

		int itemId = itemToRemove.getItemId();
		if (itemToRemove == null || itemToRemove.isWear() || !itemToRemove.isDestroyable() || CursedWeaponsManager.getInstance().isCursed(itemId))
		{
			activeChar.sendPacket(new SystemMessage(SystemMessageId.CANNOT_DISCARD_THIS_ITEM));
			activeChar.sendPacket(new ActionFailed());
			return;
		}

        if(!itemToRemove.isStackable() && count > 1)
        {
            Util.handleIllegalPlayerAction(activeChar,"[RequestDestroyItem] count > 1 but item is not stackable! oid: "+_objectId+" owner: "+activeChar.getName(),Config.DEFAULT_PUNISH);
            activeChar.sendPacket(new ActionFailed());
            return;
        }

		if (_count > itemToRemove.getCount())
			count = itemToRemove.getCount();


		if (itemToRemove.isEquipped())
		{
			L2ItemInstance[] unequiped =
				activeChar.getInventory().unEquipItemInSlotAndRecord(itemToRemove.getEquipSlot());
			InventoryUpdate iu = new InventoryUpdate();
			for (int i = 0; i < unequiped.length; i++)
			{
				activeChar.checkSSMatch(null, unequiped[i]);

				iu.addModifiedItem(unequiped[i]);
			}
			
			if (itemToRemove != null && itemToRemove.isAugmented()) 
			{
				// in case of some exploit... remove augmentation bonus
				itemToRemove.getAugmentation().removeBonus(activeChar);
			}
			
			activeChar.sendPacket(iu);
			activeChar.broadcastUserInfo();
		}

		if (L2PetDataTable.isPetItem(itemId))
		{
			Connection con = null;
			try
			{
				if (activeChar.getPet() != null && activeChar.getPet().getControlItemId() == _objectId)
				{
					activeChar.getPet().unSummon(activeChar);
				}

				// if it's a pet control item, delete the pet
				con = L2DatabaseFactory.getInstance().getConnection();
				PreparedStatement statement = con.prepareStatement("DELETE FROM pets WHERE item_obj_id=?");
				statement.setInt(1, _objectId);
				statement.execute();
				statement.close();
			}
			catch (Exception e)
			{
				_log.log(Level.WARNING, "could not delete pet objectid: ", e);
			}
			finally
			{
				try { con.close(); } catch (Exception e) {}
			}
		}

		L2ItemInstance removedItem = activeChar.getInventory().destroyItem("Destroy", _objectId, count, activeChar, null);

		if(removedItem == null)
			return;

		if (!Config.FORCE_INVENTORY_UPDATE)
		{
			InventoryUpdate iu = new InventoryUpdate();
			if (removedItem.getCount() == 0) iu.addRemovedItem(removedItem);
			else iu.addModifiedItem(removedItem);

			//client.getConnection().sendPacket(iu);
			activeChar.sendPacket(iu);
		}
		else sendPacket(new ItemList(activeChar, true));

		StatusUpdate su = new StatusUpdate(activeChar.getObjectId());
		su.addAttribute(StatusUpdate.CUR_LOAD, activeChar.getCurrentLoad());
		activeChar.sendPacket(su);

		L2World world = L2World.getInstance();
		world.removeObject(removedItem);
	}

	/* (non-Javadoc)
	 * @see com.it.br.gameserver.network.clientpackets.ClientBasePacket#getType()
	 */

	@Override
	public String getType()
	{
		return _C__59_REQUESTDESTROYITEM;
	}
}
