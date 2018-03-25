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
import com.it.br.gameserver.model.actor.instance.L2PetInstance;
import com.it.br.gameserver.util.Util;

public final class RequestGetItemFromPet extends L2GameClientPacket
{
	private static final String REQUESTGETITEMFROMPET__C__8C = "[C] 8C RequestGetItemFromPet";
	//private static Logger _log = LoggerFactory.getLogger(RequestGetItemFromPet.class);
	private int _objectId;
	private int _amount;
	@SuppressWarnings("unused")
    private int _unknown;


	@Override
	protected void readImpl()
	{
		_objectId = readD();
		_amount   = readD();
		_unknown  = readD();// = 0 for most trades
	}


	@Override
	protected void runImpl()
	{
		L2PcInstance player = getClient().getActiveChar();
        if (player == null || player.getPet() == null || !(player.getPet() instanceof L2PetInstance)) return;
        L2PetInstance pet = (L2PetInstance)player.getPet();

        if(_amount < 0)
        {
        	Util.handleIllegalPlayerAction(player,"[RequestGetItemFromPet] count < 0! ban! oid: "+_objectId+" owner: "+player.getName(),Config.DEFAULT_PUNISH);
        	return;
        }
        else if(_amount == 0)
        	return;

		if (pet.transferItem("Transfer", _objectId, _amount, player.getInventory(), player, pet) == null)
		{
			_log.warn("Invalid item transfer request: " + pet.getName() + "(pet) --> " + player.getName());
		}
	}


	@Override
	public String getType()
	{
		return REQUESTGETITEMFROMPET__C__8C;
	}
}
