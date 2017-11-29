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

import static com.it.br.configuration.Configurator.getSettings;

import com.it.br.Config;
import com.it.br.configuration.settings.L2JModsSettings;
import com.it.br.gameserver.handler.IItemHandler;
import com.it.br.gameserver.model.L2Character;
import com.it.br.gameserver.model.L2ItemInstance;
import com.it.br.gameserver.model.actor.instance.L2PcInstance;
import com.it.br.gameserver.model.actor.instance.L2PlayableInstance;
import com.it.br.gameserver.model.entity.event.TvTEvent;
import com.it.br.gameserver.network.serverpackets.ActionFailed;

/**
 * @author *Slayer
 **/

public class OfflineCustomItem implements IItemHandler
{

	public void useItem(L2PlayableInstance playable, L2ItemInstance item)
    {
		L2PcInstance activeChar = (L2PcInstance)playable;
		L2JModsSettings l2jModsSettings = getSettings(L2JModsSettings.class);
		if ((activeChar.isInStoreMode() && l2jModsSettings.isOfflineTradeEnabled()) || (activeChar.isInCraftMode() && l2jModsSettings.isOfflineCraftEnabled()))
        {
            if(!(playable instanceof L2PcInstance))
                return;

            if(activeChar.atEvent)
            {
            	activeChar.sendMessage("You cannot go into offline mode while in the event.");
            	return;
            }
            else if (activeChar.isFestivalParticipant())
            {
            	activeChar.sendMessage("You can't use this item while participating in the Festival!");
            	return;
            }
            else if (activeChar.isInOlympiadMode())
            {
            	activeChar.sendMessage("You can't teleport while you are in olympiad");
            	return;
            }
            else if (activeChar.inObserverMode())
            {
            	activeChar.sendMessage("You can't teleport while you are in observer mode");
            	return;
            }
            else if (Config.TVT_EVENT_ENABLED && TvTEvent.isStarted() && TvTEvent.isPlayerParticipant(activeChar.getObjectId())) 
            {
                activeChar.sendMessage("You can't teleport while you are in the TvTEvent.");
                return;
            }
        	if (activeChar.isInCombat() || activeChar.isFlying() || activeChar.isEnchanting() || activeChar.isCastingNow() || activeChar.isInvisible() 
               ||  activeChar.isInDuel() ||  activeChar.isDead() ||  activeChar.isSleeping() ||  activeChar.isParalyzed() ||  activeChar.isAfraid() 
               ||  activeChar.getPvpFlag() > 0)
            {
             		   activeChar.sendPacket(new ActionFailed());
             		   return;
            }

            activeChar.sendMessage("You are in offline mode bye bye :p!");
            
            activeChar.destroyItem("Consume", item.getObjectId(), l2jModsSettings.getLogoutItemCount(), null, false);
            if (l2jModsSettings.isOfflineSleepEffectEnabled())
            	activeChar.startAbnormalEffect(L2Character.ABNORMAL_EFFECT_SLEEP);
            activeChar.store();
            activeChar.closeNetConnection();
			
			if (activeChar.getOfflineStartTime() == 0)
				activeChar.setOfflineStartTime(System.currentTimeMillis());
        }
    }

	public int[] getItemIds() {
		return new int[] { getSettings(L2JModsSettings.class).getLogoutItemId() };
    }
}