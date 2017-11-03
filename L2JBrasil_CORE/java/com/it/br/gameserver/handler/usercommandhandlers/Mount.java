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

package com.it.br.gameserver.handler.usercommandhandlers;

import com.it.br.gameserver.GeoData;
import com.it.br.gameserver.handler.IUserCommandHandler;
import com.it.br.gameserver.model.L2Character;
import com.it.br.gameserver.model.L2Summon;
import com.it.br.gameserver.model.actor.instance.L2PcInstance;
import com.it.br.gameserver.network.SystemMessageId;
import com.it.br.gameserver.network.serverpackets.Ride;
import com.it.br.gameserver.network.serverpackets.SystemMessage;
import com.it.br.gameserver.util.Broadcast;

public class Mount implements IUserCommandHandler
{
    private static final int[] COMMAND_IDS = { 61 };

    /* (non-Javadoc)
     * @see com.it.br.gameserver.handler.IUserCommandHandler#useUserCommand(int, com.it.br.gameserver.model.L2PcInstance)
     */

	public synchronized boolean useUserCommand(int id, L2PcInstance activeChar)
    {
        if (id != COMMAND_IDS[0]) return false;

        L2Summon pet = activeChar.getPet();

        if (pet != null && pet.isMountable() && !activeChar.isMounted())
        {
            if (activeChar.isDead())
            {
                // A strider cannot be ridden when player is dead.
                SystemMessage msg = new SystemMessage(SystemMessageId.STRIDER_CANT_BE_RIDDEN_WHILE_DEAD);
                activeChar.sendPacket(msg);
            }
            else if (pet.isDead())
            {
                // A dead strider cannot be ridden.
                SystemMessage msg = new SystemMessage(SystemMessageId.DEAD_STRIDER_CANT_BE_RIDDEN);
                activeChar.sendPacket(msg);
            }
            else if (pet.isInCombat())
            {
                // A strider in battle cannot be ridden.
                SystemMessage msg = new SystemMessage(SystemMessageId.STRIDER_IN_BATLLE_CANT_BE_RIDDEN);
                activeChar.sendPacket(msg);
            }
            else if (activeChar.isInCombat())
            {
                // A pet cannot be ridden while player is in battle.
                SystemMessage msg = new SystemMessage(SystemMessageId.STRIDER_CANT_BE_RIDDEN_WHILE_IN_BATTLE);
                activeChar.sendPacket(msg);
            }
            else if (!activeChar.isInsideRadius(pet, 60, true, false))
	    {
		// check radius.
            	activeChar.sendMessage("Too far away from strider to mount.");
		return false;
	    }
	    else if (!GeoData.getInstance().canSeeTarget(activeChar, pet))
	    {
		SystemMessage msg = new SystemMessage(SystemMessageId.CANT_SEE_TARGET);
		activeChar.sendPacket(msg);
		return false;
	    }
            else if (activeChar.isSitting() || activeChar.isMoving()  || activeChar.isInsideZone(L2Character.ZONE_WATER))
            {
                // A strider can be ridden only when player is standing.
                SystemMessage msg = new SystemMessage(SystemMessageId.STRIDER_CAN_BE_RIDDEN_ONLY_WHILE_STANDING);
                activeChar.sendPacket(msg);
            }
            else if (!pet.isDead() && !activeChar.isMounted())
            {
            	if(!activeChar.disarmWeapons()) return false;
                Ride mount = new Ride(activeChar.getObjectId(), Ride.ACTION_MOUNT, pet.getTemplate().npcId);
                Broadcast.toSelfAndKnownPlayersInRadius(activeChar, mount, 810000/*900*/);
                activeChar.setMountType(mount.getMountType());
                activeChar.setMountObjectID(pet.getControlItemId());
                pet.unSummon(activeChar);
            }
        }
        else if (activeChar.isRentedPet())
        {
        	activeChar.stopRentPet();
        }
        else if (activeChar.isMounted())
        {
        	activeChar.dismount();
        }
        return true;
    }
    /* (non-Javadoc)
     * @see com.it.br.gameserver.handler.IUserCommandHandler#getUserCommandList()
     */

	public int[] getUserCommandList()
    {
        return COMMAND_IDS;
    }
}
