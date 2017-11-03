package com.it.br.gameserver.handler.itemhandlers;

import com.it.br.Config;
import com.it.br.gameserver.handler.IItemHandler;
import com.it.br.gameserver.model.L2ItemInstance;
import com.it.br.gameserver.model.actor.instance.L2PcInstance;
import com.it.br.gameserver.model.actor.instance.L2PlayableInstance;
import com.it.br.gameserver.model.entity.event.TvTEvent;
import com.it.br.gameserver.network.serverpackets.ActionFailed;
import com.it.br.gameserver.network.serverpackets.InventoryUpdate;
import com.it.br.gameserver.network.serverpackets.SocialAction;

/**
 * @author Stefoulis15
 **/

public class HeroCustomItem  implements IItemHandler
{
    private static final int ITEM_IDS[] = { Config.HERO_CUSTOM_ITEM_ID };

	public void useItem(L2PlayableInstance playable, L2ItemInstance item)
    {
        if(Config.ALLOW_HERO_CUSTOM_ITEM)
        {
            if(!(playable instanceof L2PcInstance))
                return;

            L2PcInstance activeChar = (L2PcInstance)playable;
            if(activeChar.isHero())
            {
            	activeChar.sendMessage("You are already a Hero!");
            	activeChar.sendPacket(new ActionFailed());
                return;
            }
            if(activeChar.isAio())
            {
            	activeChar.sendMessage("Are you an Aio, so can not turn Hero");
            	activeChar.sendPacket(new ActionFailed());
                return;
            }
            if(activeChar.atEvent && TvTEvent.isStarted())
            {
         	 	 activeChar.sendMessage("You cannot use this feature during Event."); 
         	 	 activeChar.sendPacket(new ActionFailed());
         	     return;
            }
        	if (activeChar.isInOlympiadMode())
        	{
        		activeChar.sendMessage("This item cannot be used on Olympiad Games.");
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

        	if (!activeChar.isNoble() && Config.NOBLE_STATUS_NEEDED_TO_USE_HERO_ITEM)
        	{
        		activeChar.sendMessage("You must be a Noblesse in order to use the Hero Item!");
        		activeChar.sendPacket(new ActionFailed());
                return;
        	}

        	activeChar.broadcastPacket(new SocialAction(activeChar.getObjectId(), 16));
        	activeChar.setHero(true);
        	activeChar.sendMessage("You are now a hero, You have been granted with the status of hero, Skills and Aura. This effect is valid until the Restart.");
        	activeChar.broadcastUserInfo();
        	playable.destroyItem("Consume", item.getObjectId(), 1, null, false);
        	new InventoryUpdate();
        }
    }

	public int[] getItemIds()
    {
        return ITEM_IDS;
    }
}