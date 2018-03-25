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

import com.it.br.gameserver.ai.CtrlIntention;
import com.it.br.gameserver.instancemanager.MercTicketManager;
import com.it.br.gameserver.model.L2ItemInstance;
import com.it.br.gameserver.model.L2World;
import com.it.br.gameserver.model.actor.instance.L2PetInstance;
import com.it.br.gameserver.model.actor.instance.L2SummonInstance;
import com.it.br.gameserver.network.serverpackets.ActionFailed;

/**
 * This class ...
 *
 * @version $Revision: 1.2.4.4 $ $Date: 2005/03/29 23:15:33 $
 */
public final class RequestPetGetItem extends L2GameClientPacket
{
	//private static Logger _log = LoggerFactory.getLogger(RequestPetGetItem.class);
	private static final String _C__8f_REQUESTPETGETITEM= "[C] 8F RequestPetGetItem";

	private int _objectId;


	@Override
	protected void readImpl()
	{
		_objectId = readD();
	}


	@Override
	protected void runImpl()
	{
		L2World world = L2World.getInstance();
		L2ItemInstance item = (L2ItemInstance)world.findObject(_objectId);
		if (item == null || getClient().getActiveChar() == null)
		    return;
        int castleId = MercTicketManager.getInstance().getTicketCastleId(item.getItemId());              
          if (castleId > 0) 
          { 
               sendPacket(new ActionFailed()); 
               return; 
          }                        
	                 
		if(getClient().getActiveChar().getPet() instanceof L2SummonInstance)
		{
			sendPacket(new ActionFailed());
			return;
		}
		L2PetInstance pet = (L2PetInstance)getClient().getActiveChar().getPet();
		if (pet == null || pet.isDead() || pet.isOutOfControl())
		{
			sendPacket(new ActionFailed());
			return;
		}
		pet.getAI().setIntention(CtrlIntention.AI_INTENTION_PICK_UP, item);
	}


	@Override
	public String getType()
	{
		return _C__8f_REQUESTPETGETITEM;
	}

}
