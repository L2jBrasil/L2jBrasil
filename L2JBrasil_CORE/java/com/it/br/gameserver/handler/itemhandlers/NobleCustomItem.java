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

import com.it.br.configuration.settings.L2JBrasilSettings;
import com.it.br.gameserver.handler.IItemHandler;
import com.it.br.gameserver.model.L2ItemInstance;
import com.it.br.gameserver.model.actor.instance.L2PcInstance;
import com.it.br.gameserver.model.actor.instance.L2PlayableInstance;
import com.it.br.gameserver.model.entity.event.TvTEvent;
import com.it.br.gameserver.network.serverpackets.ActionFailed;
import com.it.br.gameserver.network.serverpackets.MagicSkillUser;

import static com.it.br.configuration.Configurator.getSettings;

/**
 * @author Stefoulis15
 **/

public class NobleCustomItem implements IItemHandler
{

	public void useItem(L2PlayableInstance playable, L2ItemInstance item)
    {
		L2JBrasilSettings l2jBrasilSettings = getSettings(L2JBrasilSettings.class);
        if(l2jBrasilSettings.isNobleCustomItemEnabled())
        {
            if(!(playable instanceof L2PcInstance))
                return;
            L2PcInstance activeChar = (L2PcInstance)playable;
            if (activeChar.isNoble())
            {
                activeChar.sendMessage("You Have already noblesse status!");
                activeChar.sendPacket(new ActionFailed());
                return;
            } 
            if(activeChar.atEvent && TvTEvent.isStarted()) 
            { 
         	 	 activeChar.sendMessage("You cannot use this feature during TvT."); 
         	     return;
            } 
            if (activeChar.isSubClassActive() && l2jBrasilSettings.isActiveSubNeededToUseNobleItem())
            {
            	activeChar.sendPacket(new ActionFailed());
            	activeChar.sendMessage("You Must Be With Your SubClass");
            	return;
            }
            
        	if (activeChar.isInOlympiadMode())
        	{
        		activeChar.sendMessage("This Item Can't Be Used while you are playing on the Olympiad Games.");
        		activeChar.sendPacket(new ActionFailed());
                return;
        	}
        	if (activeChar.isInCombat() || activeChar.isFlying() || activeChar.isEnchanting() || activeChar.isCastingNow() || activeChar.isInvisible() 
               ||  activeChar.isInDuel() ||  activeChar.isDead() ||  activeChar.isSleeping() ||  activeChar.isParalyzed() ||  activeChar.isAfraid() 
               ||  activeChar.getPvpFlag() > 0)
            {
             		   activeChar.sendPacket(new ActionFailed());
             		   return;
            }
        	if (activeChar.getLevel() <= getSettings(L2JBrasilSettings.class).getLevelNeededToUseNobleCustomItem())
        	{
        		activeChar.sendMessage("You Don't Meet The Creteria. Your Level is Low.");
        		activeChar.sendPacket(new ActionFailed());
                return;
        	}
            	
        	MagicSkillUser MSU = new MagicSkillUser(playable, activeChar, 2025, 1, 1, 0);
        	activeChar.sendPacket(MSU);
        	activeChar.broadcastPacket(MSU);
        	activeChar.setNoble(true);
        	activeChar.sendMessage("You Are Now a Noble,You Are Granted With Noblesse Status , And Noblesse Skills.");
        	activeChar.broadcastUserInfo();
        	playable.destroyItem("Consume", item.getObjectId(), 1, null, false);
        	activeChar.getInventory().addItem("Tiara", 7694, 1, activeChar, null); 
        }
    }



	public int[] getItemIds()
    {
        return new int[] { getSettings(L2JBrasilSettings.class).getNobleItemId() };
    }
}