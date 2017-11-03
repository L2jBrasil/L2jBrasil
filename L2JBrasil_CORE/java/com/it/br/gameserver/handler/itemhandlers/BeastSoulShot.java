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

import com.it.br.Config;
import com.it.br.gameserver.handler.IItemHandler;
import com.it.br.gameserver.model.L2ItemInstance;
import com.it.br.gameserver.model.L2Summon;
import com.it.br.gameserver.model.actor.instance.L2BabyPetInstance;
import com.it.br.gameserver.model.actor.instance.L2PcInstance;
import com.it.br.gameserver.model.actor.instance.L2PetInstance;
import com.it.br.gameserver.model.actor.instance.L2PlayableInstance;
import com.it.br.gameserver.network.SystemMessageId;
import com.it.br.gameserver.network.serverpackets.ExAutoSoulShot;
import com.it.br.gameserver.network.serverpackets.MagicSkillUser;
import com.it.br.gameserver.network.serverpackets.SystemMessage;
import com.it.br.gameserver.templates.L2Weapon;
import com.it.br.gameserver.util.Broadcast;

/**
 * Beast SoulShot Handler
 * 
 * @author programmos, scoria dev
 */
public class BeastSoulShot implements IItemHandler
{
	// All the item IDs that this handler knows.
	private static final int[] ITEM_IDS =
	{
		6645
	};


	public void useItem(L2PlayableInstance playable, L2ItemInstance item)
	{
		if(playable == null)
			return;

		L2PcInstance activeOwner = null;

		if(playable instanceof L2Summon)
		{
			activeOwner = ((L2Summon) playable).getOwner();
			activeOwner.sendPacket(new SystemMessage(SystemMessageId.PET_CANNOT_USE_ITEM));

			return;
		}
		else if(playable instanceof L2PcInstance)
		{
			activeOwner = (L2PcInstance) playable;
		}

		if(activeOwner == null)
			return;

		L2Summon activePet = activeOwner.getPet();

		if(activePet == null)
		{
			activeOwner.sendPacket(new SystemMessage(SystemMessageId.PETS_ARE_NOT_AVAILABLE_AT_THIS_TIME));
			return;
		}

		if(activePet.isDead())
		{
			activeOwner.sendPacket(new SystemMessage(SystemMessageId.SOULSHOTS_AND_SPIRITSHOTS_ARE_NOT_AVAILABLE_FOR_A_DEAD_PET));
			return;
		}

		int itemId = 6645;
		int shotConsumption = 1;

		L2ItemInstance weaponInst = null;
		L2Weapon weaponItem = null;

		if(activePet instanceof L2PetInstance && !(activePet instanceof L2BabyPetInstance))
		{
			weaponInst = ((L2PetInstance) activePet).getActiveWeaponInstance();
			weaponItem = ((L2PetInstance) activePet).getActiveWeaponItem();

			if(weaponInst == null)
			{
				activeOwner.sendPacket(new SystemMessage(SystemMessageId.CANNOT_USE_SOULSHOTS));
				return;
			}

			if(weaponInst.getChargedSoulshot() != L2ItemInstance.CHARGED_NONE)
				// SoulShots are already active.
				return;

			int shotCount = item.getCount();
			shotConsumption = weaponItem.getSoulShotCount();
			weaponItem = null;

			if(shotConsumption == 0)
			{
				activeOwner.sendPacket(new SystemMessage(SystemMessageId.CANNOT_USE_SOULSHOTS));
				return;
			}

			if(!(shotCount > shotConsumption))
			{
				// Not enough Soulshots to use.
				activeOwner.sendPacket(new SystemMessage(SystemMessageId.NOT_ENOUGH_SOULSHOTS_FOR_PET));
				return;
			}

			shotCount = 0;
			weaponInst.setChargedSoulshot(L2ItemInstance.CHARGED_SOULSHOT);
		}
		else
		{
			if(activePet.getChargedSoulShot() != L2ItemInstance.CHARGED_NONE)
				return;

			activePet.setChargedSoulShot(L2ItemInstance.CHARGED_SOULSHOT);
		}

		// If the player doesn't have enough beast soulshot remaining, remove any auto soulshot task.
		// TODO: test ss
		if(!Config.DONT_DESTROY_SS)
		{
			if(!activeOwner.destroyItemWithoutTrace("Consume", item.getObjectId(), shotConsumption, null, false))
			{
				if(activeOwner.getAutoSoulShot().containsKey(itemId))
				{
					activeOwner.removeAutoSoulShot(itemId);
					activeOwner.sendPacket(new ExAutoSoulShot(itemId, 0));
					SystemMessage sm = new SystemMessage(SystemMessageId.AUTO_USE_OF_S1_CANCELLED);
					sm.addString(item.getItem().getName());
					activeOwner.sendPacket(sm);
					sm = null;

					return;
				}
				activeOwner.sendPacket(new SystemMessage(SystemMessageId.NOT_ENOUGH_SOULSHOTS_FOR_PET));
				return;
			}
		}

		// Pet uses the power of spirit.
		activeOwner.sendPacket(new SystemMessage(SystemMessageId.PET_USE_THE_POWER_OF_SPIRIT));
		Broadcast.toSelfAndKnownPlayersInRadius(activeOwner, new MagicSkillUser(activePet, activePet, 2033, 1, 0, 0), 360000/*600*/);

		activeOwner = null;
		activePet = null;
		weaponInst = null;
	}


	public int[] getItemIds()
	{
		return ITEM_IDS;
	}
}
