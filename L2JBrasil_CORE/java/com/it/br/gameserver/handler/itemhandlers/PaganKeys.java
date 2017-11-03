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
package com.it.br.gameserver.handler.itemhandlers;


import com.it.br.gameserver.handler.IItemHandler;
import com.it.br.gameserver.model.L2ItemInstance;
import com.it.br.gameserver.model.L2Object;
import com.it.br.gameserver.model.actor.instance.L2DoorInstance;
import com.it.br.gameserver.model.actor.instance.L2PcInstance;
import com.it.br.gameserver.model.actor.instance.L2PlayableInstance;
import com.it.br.gameserver.network.SystemMessageId;
import com.it.br.gameserver.network.serverpackets.ActionFailed;
import com.it.br.gameserver.network.serverpackets.PlaySound;
import com.it.br.gameserver.network.serverpackets.SocialAction;
import com.it.br.gameserver.network.serverpackets.SystemMessage;
import com.it.br.util.Rnd;

/**
 * @author  chris
 */
public class PaganKeys implements IItemHandler
{
	private static final int[] ITEM_IDS = {8273, 8274, 8275};
	public static final int INTERACTION_DISTANCE = 100;


	public void useItem(L2PlayableInstance playable, L2ItemInstance item)
	{

		int itemId = item.getItemId();
		if (!(playable instanceof L2PcInstance)) return;
		L2PcInstance activeChar = (L2PcInstance) playable;
		L2Object target = activeChar.getTarget();

		if (!(target instanceof L2DoorInstance))
		{
			activeChar.sendPacket(new SystemMessage(SystemMessageId.INCORRECT_TARGET));
			activeChar.sendPacket(new ActionFailed());
			return;
		}
		L2DoorInstance door = (L2DoorInstance)target;

		if (!(activeChar.isInsideRadius(door, INTERACTION_DISTANCE, false, false)))
		{
			activeChar.sendMessage("Too far.");
			activeChar.sendPacket(new ActionFailed());
			return;
		}
		if (activeChar.getAbnormalEffect() > 0 || activeChar.isInCombat())
		{
			activeChar.sendMessage("You cannot use the key now.");
			activeChar.sendPacket(new ActionFailed());
			return;
		}

		int openChance = 35;

		if (!playable.destroyItem("Consume", item.getObjectId(), 1, null, false)) return;

		switch (itemId){
		case 8273: //AnteroomKey
			  if (door.getDoorName().startsWith("Anteroom")){
                	if (openChance > 0 && Rnd.get(100) < openChance) {
                		activeChar.sendMessage("You opened Anterooms Door.");
                		door.openMe();
                		door.onOpen(); // Closes the door after 60sec
                		activeChar.broadcastPacket(new SocialAction(activeChar.getObjectId(), 3));
                	}
                	else {
                		//test with: activeChar.sendPacket(new SystemMessage(SystemMessage.FAILED_TO_UNLOCK_DOOR));
                		activeChar.sendMessage("You failed to open Anterooms Door.");
                		activeChar.broadcastPacket(new SocialAction(activeChar.getObjectId(), 13));
        				PlaySound playSound = new PlaySound("interfacesound.system_close_01");
        				activeChar.sendPacket(playSound);
                	}
			  }
			  else{
				  activeChar.sendMessage("Incorrect Door.");
			  }
			  break;
		case 8274: //Chapelkey, Capel Door has a Gatekeeper?? I use this key for Altar Entrance
			if (door.getDoorName().startsWith("Altar_Entrance")){
            	if (openChance > 0 && Rnd.get(100) < openChance) {
            		activeChar.sendMessage("You opened Altar Entrance.");
            		door.openMe();
            		door.onOpen();
            		activeChar.broadcastPacket(new SocialAction(activeChar.getObjectId(), 3));
            	}
            	else {
            		activeChar.sendMessage("You failed to open Altar Entrance.");
            		activeChar.broadcastPacket(new SocialAction(activeChar.getObjectId(), 13));
    				PlaySound playSound = new PlaySound("interfacesound.system_close_01");
    				activeChar.sendPacket(playSound);
            	}
            }
			else{
				activeChar.sendMessage("Incorrect Door.");
			}
			break;
		case 8275: //Key of Darkness
			if (door.getDoorName().startsWith("Door_of_Darkness")){
            	if (openChance > 0 && Rnd.get(100) < openChance) {
            		activeChar.sendMessage("You opened Door of Darkness.");
            		door.openMe();
            		door.onOpen();
            		activeChar.broadcastPacket(new SocialAction(activeChar.getObjectId(), 3));
            	}
            	else {
            		activeChar.sendMessage("You failed to open Door of Darkness.");
            		activeChar.broadcastPacket(new SocialAction(activeChar.getObjectId(), 13));
    				PlaySound playSound = new PlaySound("interfacesound.system_close_01");
    				activeChar.sendPacket(playSound);
            	}
            }
			else{
				activeChar.sendMessage("Incorrect Door.");
			}
			break;
		}
	}


	public int[] getItemIds()
	{
		return ITEM_IDS;
	}
}
