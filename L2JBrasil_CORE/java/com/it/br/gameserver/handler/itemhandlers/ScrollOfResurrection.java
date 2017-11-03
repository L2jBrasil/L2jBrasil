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

import com.it.br.gameserver.datatables.sql.SkillTable;
import com.it.br.gameserver.handler.IItemHandler;
import com.it.br.gameserver.instancemanager.CastleManager;
import com.it.br.gameserver.model.L2Character;
import com.it.br.gameserver.model.L2ItemInstance;
import com.it.br.gameserver.model.L2Skill;
import com.it.br.gameserver.model.actor.instance.L2PcInstance;
import com.it.br.gameserver.model.actor.instance.L2PetInstance;
import com.it.br.gameserver.model.actor.instance.L2PlayableInstance;
import com.it.br.gameserver.model.entity.Castle;
import com.it.br.gameserver.model.entity.event.TvTEvent;
import com.it.br.gameserver.network.SystemMessageId;
import com.it.br.gameserver.network.serverpackets.ActionFailed;
import com.it.br.gameserver.network.serverpackets.SystemMessage;

public class ScrollOfResurrection implements IItemHandler
{
    // all the items ids that this handler knows
    private static final int[] ITEM_IDS = { 737, 3936, 3959, 6387 };

	public void useItem(L2PlayableInstance playable, L2ItemInstance item)
	{
        if (!(playable instanceof L2PcInstance)) return;

        L2PcInstance activeChar = (L2PcInstance)playable;
        if (activeChar.isSitting())
        {
        	activeChar.sendPacket(new SystemMessage(SystemMessageId.CANT_MOVE_SITTING));
        	return;
        }
        if (activeChar.isInOlympiadMode() || activeChar.inObserverMode())
        { 
        	activeChar.sendMessage("You Cannot Use This Item In Olympiad Mode!");  
        	activeChar.sendPacket(new ActionFailed());  
        	return;  
        }
        if(activeChar.atEvent && (TvTEvent.isStarted() || TvTEvent.isPlayerParticipant(activeChar.getObjectId())))
		{
			activeChar.sendMessage("You can't use Scroll of Ressurection in TvT.");
			return;
		}

        if (activeChar.isMovementDisabled()) return;

        int itemId = item.getItemId();
        //boolean blessedScroll = (itemId != 737);
        boolean humanScroll = (itemId == 3936 || itemId == 3959 || itemId == 737);
        boolean petScroll = (itemId == 6387 || itemId == 737);

        // SoR Animation section
        L2Character target = (L2Character)activeChar.getTarget();

        if (target != null && target.isDead())
        {
            L2PcInstance targetPlayer = null;

            if (target instanceof L2PcInstance)
            	targetPlayer = (L2PcInstance)target;

            L2PetInstance targetPet = null;

            if (target instanceof L2PetInstance)
            	targetPet = (L2PetInstance)target;

            if (targetPlayer != null || targetPet != null)
            {
                boolean condGood = true;

                //check target is not in a active siege zone
                Castle castle = null;

                if (targetPlayer != null)
                	castle = CastleManager.getInstance().getCastle(targetPlayer.getX(), targetPlayer.getY(), targetPlayer.getZ());
                else
                	castle = CastleManager.getInstance().getCastle(targetPet.getX(), targetPet.getY(), targetPet.getZ());

            	if (castle != null
            			&& castle.getSiege().getIsInProgress())
            	{
                    condGood = false;
                    activeChar.sendPacket(new SystemMessage(SystemMessageId.CANNOT_BE_RESURRECTED_DURING_SIEGE));
            	}

                if (targetPet != null)
                {
                	if (targetPet.getOwner() != activeChar)
                    {
                		if (targetPet.getOwner().isReviveRequested())
                		{
                			if (targetPet.getOwner().isRevivingPet())
                				activeChar.sendPacket(new SystemMessage(SystemMessageId.RES_HAS_ALREADY_BEEN_PROPOSED)); // Resurrection is already been proposed.
                			else
                				activeChar.sendPacket(new SystemMessage(SystemMessageId.PET_CANNOT_RES)); // A pet cannot be resurrected while it's owner is in the process of resurrecting.
                            condGood = false;
                		}
                    }
                    else if (!petScroll)
                    {
                        condGood = false;
                        activeChar.sendMessage("You do not have the correct scroll");
                    }
                }
                else
                {
                    if (targetPlayer.isFestivalParticipant()) // Check to see if the current player target is in a festival.
                    {
                        condGood = false;
                        activeChar.sendPacket(SystemMessage.sendString("You may not resurrect participants in a festival."));
                    }
                	if (targetPlayer.isReviveRequested())
                	{
                		if (targetPlayer.isRevivingPet())
                			activeChar.sendPacket(new SystemMessage(SystemMessageId.MASTER_CANNOT_RES)); // While a pet is attempting to resurrect, it cannot help in resurrecting its master.
                		else
                			activeChar.sendPacket(new SystemMessage(SystemMessageId.RES_HAS_ALREADY_BEEN_PROPOSED)); // Resurrection is already been proposed.
                        condGood = false;
                	}
                    else if (!humanScroll)
                    {
                        condGood = false;
                        activeChar.sendMessage("You do not have the correct scroll");
                    }
                }

                if (condGood)
                {
                    if (!activeChar.destroyItem("Consume", item.getObjectId(), 1, null, false))
                        return;

                    int skillId = 0;
                    int skillLevel = 1;

                    switch (itemId) {
                    	case  737: skillId = 2014; break; // Scroll of Resurrection
                    	case 3936: skillId = 2049; break; // Blessed Scroll of Resurrection
                    	case 3959: skillId = 2062; break; // L2Day - Blessed Scroll of Resurrection
                    	case 6387: skillId = 2179; break; // Blessed Scroll of Resurrection: For Pets
                    }

                    if (skillId != 0)
                    {
                    	L2Skill skill = SkillTable.getInstance().getInfo(skillId, skillLevel);
                    	activeChar.useMagic(skill, true, true);
                    	/* Micht : Unusefull, already done in useMagic()
                    	MagicSkillUser msu = new MagicSkillUser(activeChar, skillId, skillLevel, skill.getHitTime(),0);
                    	activeChar.broadcastPacket(msu);
                    	SetupGauge sg = new SetupGauge(0, skill.getHitTime());
                    	activeChar.sendPacket(sg);
                    	*/

                    	SystemMessage sm = new SystemMessage(SystemMessageId.S1_DISAPPEARED);
                    	sm.addItemName(itemId);
                    	activeChar.sendPacket(sm);
                    }
                }
            }
        }
        else
        {
        	activeChar.sendPacket(new SystemMessage(SystemMessageId.TARGET_IS_INCORRECT));
        }
    }

	public int[] getItemIds()
    {
        return ITEM_IDS;
    }
}