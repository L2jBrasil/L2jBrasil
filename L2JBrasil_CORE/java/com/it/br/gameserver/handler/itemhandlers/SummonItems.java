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

/**
 *
 * @author FBIagent
 *
 */

package com.it.br.gameserver.handler.itemhandlers;

import com.it.br.gameserver.ThreadPoolManager;
import com.it.br.gameserver.datatables.xml.NpcTable;
import com.it.br.gameserver.datatables.xml.SummonItemsData;
import com.it.br.gameserver.handler.IItemHandler;
import com.it.br.gameserver.idfactory.IdFactory;
import com.it.br.gameserver.model.L2ItemInstance;
import com.it.br.gameserver.model.L2Spawn;
import com.it.br.gameserver.model.L2SummonItem;
import com.it.br.gameserver.model.L2World;
import com.it.br.gameserver.model.actor.instance.L2PcInstance;
import com.it.br.gameserver.model.actor.instance.L2PetInstance;
import com.it.br.gameserver.model.actor.instance.L2PlayableInstance;
import com.it.br.gameserver.model.entity.event.TvTEvent;
import com.it.br.gameserver.network.SystemMessageId;
import com.it.br.gameserver.network.serverpackets.MagicSkillLaunched;
import com.it.br.gameserver.network.serverpackets.MagicSkillUser;
import com.it.br.gameserver.network.serverpackets.PetInfo;
import com.it.br.gameserver.network.serverpackets.Ride;
import com.it.br.gameserver.network.serverpackets.SystemMessage;
import com.it.br.gameserver.templates.L2NpcTemplate;

public class SummonItems implements IItemHandler
{

	@SuppressWarnings("unused")
	public void useItem(L2PlayableInstance playable, L2ItemInstance item)
	{
		if (!(playable instanceof L2PcInstance))
			return;

		if (!TvTEvent.onItemSummon(playable.getObjectId()))
			return;

		L2PcInstance activeChar = (L2PcInstance)playable;

		if (!activeChar.getFloodProtectors().getItemPetSummon().tryPerformAction("ItemPetSummon"))
		{
			activeChar.sendMessage("You can not use Summon Item Pet so fast!");
			return;
		}

		if(activeChar.isSitting())
		{
			activeChar.sendPacket(new SystemMessage(SystemMessageId.CANT_MOVE_SITTING));
			return;
		}

		if (activeChar.inObserverMode())
			return;

		if (activeChar.isInOlympiadMode())
        {
            activeChar.sendPacket(new SystemMessage(SystemMessageId.THIS_ITEM_IS_NOT_AVAILABLE_FOR_THE_OLYMPIAD_EVENT));
            return;
        }

		if(activeChar.isParalyzed())
		{
			activeChar.sendMessage("You Cannot Use This While You Are Paralyzed");
			return;
		}

		L2SummonItem sitem = SummonItemsData.getInstance().getSummonItem(item.getItemId());

		if ((activeChar.getPet() != null || activeChar.isMounted()) && sitem.isPetSummon())
		{
            activeChar.sendPacket(new SystemMessage(SystemMessageId.YOU_ALREADY_HAVE_A_PET));
			return;
		}

		if (activeChar.isAttackingNow())
		{
            activeChar.sendPacket(new SystemMessage(SystemMessageId.YOU_CANNOT_SUMMON_IN_COMBAT));
			return;
		}

        if (activeChar.isCursedWeaponEquipped() && sitem.isPetSummon())
        {
        	activeChar.sendPacket(new SystemMessage(SystemMessageId.STRIDER_CANT_BE_RIDDEN_WHILE_IN_BATTLE));
        	return;
        }

        int npcID = sitem.getNpcId();

        if (npcID == 0)
        	return;

		L2NpcTemplate npcTemplate = NpcTable.getInstance().getTemplate(npcID);

        if (npcTemplate == null)
            return;

        switch (sitem.getType())
        {
        case 0: // static summons (like christmas tree)
            try
            {
                L2Spawn spawn = new L2Spawn(npcTemplate);

                if (spawn == null)
                	return;

                spawn.setId(IdFactory.getInstance().getNextId());
                spawn.setLocx(activeChar.getX());
                spawn.setLocy(activeChar.getY());
                spawn.setLocz(activeChar.getZ());
                L2World.getInstance().storeObject(spawn.spawnOne());
                activeChar.destroyItem("Summon", item.getObjectId(), 1, null, false);
                activeChar.sendMessage("Created " + npcTemplate.name + " at x: " + spawn.getLocx() + " y: " + spawn.getLocy() + " z: " + spawn.getLocz());
            }
            catch (Exception e)
            {
                activeChar.sendMessage("Target is not ingame.");
            }

        	break;
        case 1: // pet summons
        	L2PetInstance petSummon = L2PetInstance.spawnPet(npcTemplate, activeChar, item);

    		if (petSummon == null)
    			break;

    		petSummon.setTitle(activeChar.getName());

    		if (!petSummon.isRespawned())
    		{
    			petSummon.setCurrentHp(petSummon.getMaxHp());
    			petSummon.setCurrentMp(petSummon.getMaxMp());
    			petSummon.getStat().setExp(petSummon.getExpForThisLevel());
    			petSummon.setCurrentFed(petSummon.getMaxFed());
    		}

    		petSummon.setRunning();

    		if (!petSummon.isRespawned())
    			petSummon.store();

            activeChar.setPet(petSummon);

    		activeChar.sendPacket(new MagicSkillUser(activeChar, 2046, 1, 1000, 600000));
    		activeChar.sendPacket(new SystemMessage(SystemMessageId.SUMMON_A_PET));
            L2World.getInstance().storeObject(petSummon);
    		petSummon.spawnMe(activeChar.getX()+50, activeChar.getY()+100, activeChar.getZ());
            activeChar.sendPacket(new PetInfo(petSummon));
    		petSummon.startFeed(false);
    		item.setEnchantLevel(petSummon.getLevel());

    		ThreadPoolManager.getInstance().scheduleGeneral(new PetSummonFinalizer(activeChar, petSummon), 900);

    		if (petSummon.getCurrentFed() <= 0)
    			ThreadPoolManager.getInstance().scheduleGeneral(new PetSummonFeedWait(activeChar, petSummon), 60000);
    		else
    			petSummon.startFeed(false);

        	break;
        case 2: // wyvern
        	if(!activeChar.disarmWeapons()) return;
        	Ride mount = new Ride(activeChar.getObjectId(), Ride.ACTION_MOUNT, sitem.getNpcId());
            activeChar.sendPacket(mount);
            activeChar.broadcastPacket(mount);
            activeChar.setMountType(mount.getMountType());
            activeChar.setMountObjectID(item.getObjectId());
        }
	}

	static class PetSummonFeedWait implements Runnable
	{
		private L2PcInstance _activeChar;
		private L2PetInstance _petSummon;

		PetSummonFeedWait(L2PcInstance activeChar, L2PetInstance petSummon)
		{
			_activeChar = activeChar;
			_petSummon = petSummon;
		}

	
		public void run()
		{
			try
			{
				if (_petSummon.getCurrentFed() <= 0 )
					_petSummon.unSummon(_activeChar);
				else
					_petSummon.startFeed(false);
			}
			catch (Throwable e)
			{
			}
		}
	}

	static class PetSummonFinalizer implements Runnable
	{
		private L2PcInstance _activeChar;
		private L2PetInstance _petSummon;

		PetSummonFinalizer(L2PcInstance activeChar, L2PetInstance petSummon)
		{
			_activeChar = activeChar;
			_petSummon = petSummon;
		}

	
		public void run()
		{
			try
			{
				_activeChar.sendPacket(new MagicSkillLaunched(_activeChar, 2046, 1));
				_petSummon.setFollowStatus(true);
		        _petSummon.setShowSummonAnimation(false);
			}
			catch (Throwable e)
			{
			}
		}
	}


	public int[] getItemIds()
    {
    	return SummonItemsData.getInstance().itemIDs();
    }
}
