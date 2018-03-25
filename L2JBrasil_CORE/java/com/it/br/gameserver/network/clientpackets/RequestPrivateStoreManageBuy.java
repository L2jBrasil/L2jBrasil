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
import com.it.br.gameserver.model.actor.instance.L2PcInstance;
import com.it.br.gameserver.network.serverpackets.ActionFailed;
import com.it.br.gameserver.network.serverpackets.PrivateStoreManageListBuy;
import com.it.br.gameserver.util.Util;

public final class RequestPrivateStoreManageBuy extends L2GameClientPacket
{
	private static final String _C__90_REQUESTPRIVATESTOREMANAGEBUY = "[C] 90 RequestPrivateStoreManageBuy";
	//private static Logger _log = LoggerFactory.getLogger(RequestPrivateStoreBuyManage.class);

	@Override
	protected void readImpl(){}

	@Override
	protected void runImpl()
	{
		L2PcInstance player = getClient().getActiveChar();
		if (player == null)
			return;

		// Fix for privatestore exploit during login
		if(!player.isVisible() || player.isLocked())
		{
			Util.handleIllegalPlayerAction(player, "Player " + player.getName() + " try exploit at login with privatestore!", Config.DEFAULT_PUNISH);
			_log.warn("Player " + player.getName() + " try exploit at login with privatestore!");
			return;
		}

        // Player shouldn't be able to set stores if he/she is alike dead (dead or fake death)
        if (player.isAlikeDead())
        {
            sendPacket(new ActionFailed());
            return;
        }
        
        // If player is in store mode /offline_shop like L2OFF
        if (player.isStored())
        {
        	player.sendPacket(ActionFailed.STATIC_PACKET);
        	return;
        }

        if (player.isInOlympiadMode())
        {
            sendPacket(new ActionFailed());
            return;
        }
        // Like L2OFF - You can't open buy/sell when you are sitting
        if(player.isSitting() && player.getPrivateStoreType() == 0)
        {
        	sendPacket(ActionFailed.STATIC_PACKET);
        	return;
        }
        if(player.isSitting() && player.getPrivateStoreType() != 0)
        {
        	player.standUp();
        }
        if(player.getMountType() != 0)
        	return;
        if (player.getPrivateStoreType() == L2PcInstance.STORE_PRIVATE_BUY || player.getPrivateStoreType() == L2PcInstance.STORE_PRIVATE_BUY +1)
        	player.setPrivateStoreType(L2PcInstance.STORE_PRIVATE_NONE);

        if (player.getPrivateStoreType() == L2PcInstance.STORE_PRIVATE_NONE)
        {
			if (player.isSitting()) 
				player.standUp();

			player.setPrivateStoreType(L2PcInstance.STORE_PRIVATE_BUY +1);
			player.sendPacket(new PrivateStoreManageListBuy(player));
        }
	}

	@Override
	public String getType()
	{
		return _C__90_REQUESTPRIVATESTOREMANAGEBUY;
	}
}