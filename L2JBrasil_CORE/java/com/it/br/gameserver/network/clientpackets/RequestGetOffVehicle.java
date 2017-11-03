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

import com.it.br.gameserver.instancemanager.BoatManager;
import com.it.br.gameserver.model.actor.instance.L2BoatInstance;
import com.it.br.gameserver.model.actor.instance.L2PcInstance;
import com.it.br.gameserver.network.serverpackets.GetOffVehicle;

/**
 * @author Maktakien
 *
 */
public final class RequestGetOffVehicle extends L2GameClientPacket
{
	 private int _id, _x, _y, _z;


	@Override
	protected void readImpl()
	{
		_id  = readD();
		_x  = readD();
		_y  = readD();
		_z  = readD();
	}


	@Override
	protected void runImpl()
	{
		L2PcInstance activeChar = getClient().getActiveChar();
		if(activeChar == null)
			return;
        L2BoatInstance boat = BoatManager.getInstance().GetBoat(_id);
        GetOffVehicle Gon = new GetOffVehicle(activeChar,boat,_x,_y,_z);
        activeChar.broadcastPacket(Gon);
	}

	/* (non-Javadoc)
	 * @see com.it.br.gameserver.BasePacket#getType()
	 */

	@Override
	public String getType()
	{
		// TODO Auto-generated method stub
		return "[S] 5d GetOffVehicle";
	}
}
