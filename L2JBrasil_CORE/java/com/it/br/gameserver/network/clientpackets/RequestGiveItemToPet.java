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
import com.it.br.gameserver.model.L2ItemInstance;
import com.it.br.gameserver.model.actor.instance.L2PcInstance;
import com.it.br.gameserver.model.actor.instance.L2PetInstance;
import com.it.br.gameserver.network.SystemMessageId;
import com.it.br.gameserver.network.serverpackets.ActionFailed;
import com.it.br.gameserver.network.serverpackets.SystemMessage;
import com.it.br.gameserver.templates.L2WeaponType;
import com.it.br.gameserver.util.IllegalPlayerAction;
import com.it.br.gameserver.util.Util;

/**
 * This class ...
 *
 * @version $Revision: 1.3.2.1.2.5 $ $Date: 2005/03/29 23:15:33 $
 */
public final class RequestGiveItemToPet extends L2GameClientPacket
{
	private static final String REQUESTCIVEITEMTOPET__C__8B = "[C] 8B RequestGiveItemToPet";
	//private static Logger _log = LoggerFactory.getLogger(RequestGetItemFromPet.class);
	private int _objectId;
	private int _amount;


	@Override
	protected void readImpl()
	{
		_objectId = readD();
		_amount   = readD();
	}


	@Override
	protected void runImpl()
	{
		L2PcInstance player = getClient().getActiveChar();
        if (player == null || player.getPet() == null || !(player.getPet() instanceof L2PetInstance)) return;
        
        if (!getClient().getFloodProtectors().getTransaction().tryPerformAction("giveitemtopet"))
		{
			player.sendMessage("You give items to pet too fast.");
			return;
		}
        // Alt game - Karma punishment
        if (!Config.ALT_GAME_KARMA_PLAYER_CAN_TRADE && player.getKarma() > 0) return;

        if (player.getPrivateStoreType() != 0)
        {
            player.sendMessage("Cannot exchange items while trading");
            return;
        }  
		if(player.isCastingNow())
		{
			return;
		}
        if (player.getActiveEnchantItem() != null)  
	    {  
	        player.setAccountAccesslevel(-100);   
	        Util.handleIllegalPlayerAction(player,"Player "+player.getName()+" Tried To Use Enchant Exploit And Got Banned!", IllegalPlayerAction.PUNISH_KICKBAN);  
	        return; 
	    }  
		if(player.isOverloaded())
		{
			player.sendPacket(new ActionFailed());
			return;
		}
	    L2ItemInstance item = player.getInventory().getItemByObjectId(_objectId); 
	         
	    if (item == null) 
	           return; 
	    
	    if (item.isAugmented()) 
	    { 
	       player.sendMessage("You cannot give augmented items to pet"); 
	       return; 
	    }
        if (!item.isDropable() || !item.isDestroyable() || !item.isTradeable())
		{
			player.sendMessage("That is a item is not for the pets!");
			return;
		}	         
	    if ((item.getItem().getItemType() == L2WeaponType.ROD) 
	         || ((item.getItemId() >= 6611) && (item.getItemId() <= 6621)) 
	         || ((item.getItemId() >= 7816) && (item.getItemId() <= 7831)) 
	         || item.isShadowItem()) 
	    { 
	        player.sendMessage("You cannot give hero items to pet"); 
	        return; 
	    } 
        L2PetInstance pet = (L2PetInstance)player.getPet();
		if (pet.isDead())
		{
			sendPacket(new SystemMessage(SystemMessageId.CANNOT_GIVE_ITEMS_TO_DEAD_PET));
			return;
		}  
		if(_amount < 0)
		{
			return;
		}

		if (player.transferItem("Transfer", _objectId, _amount, pet.getInventory(), pet) == null)
		{
			_log.warn("Invalid item transfer request: " + pet.getName() + "(pet) --> " + player.getName());
		}
	}


	@Override
	public String getType()
	{
		return REQUESTCIVEITEMTOPET__C__8B;
	}
}
