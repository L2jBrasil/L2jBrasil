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
import com.it.br.gameserver.network.serverpackets.GetOnVehicle;
import com.it.br.util.Point3D;

/**
 * This class ...
 *
 * @version $Revision: 1.1.4.3 $ $Date: 2005/03/27 15:29:30 $
 */
public final class RequestGetOnVehicle extends L2GameClientPacket
{
    private static final String _C__5C_GETONVEHICLE = "[C] 5C GetOnVehicle";

    private int _id, _x, _y, _z;


	@Override
	protected void readImpl()
    {
        _id = readD();
        _x = readD();
        _y = readD();
        _z = readD();
    }


	@Override
	protected void runImpl()
    {
        L2PcInstance activeChar = getClient().getActiveChar();
        if (activeChar == null) return;

        L2BoatInstance boat = BoatManager.getInstance().GetBoat(_id);
        if (boat == null) return;

        GetOnVehicle Gon = new GetOnVehicle(activeChar,boat,_x,_y,_z);
        activeChar.setInBoatPosition(new Point3D(_x,_y,_z));
        activeChar.getPosition().setXYZ(boat.getPosition().getX(),boat.getPosition().getY(),boat.getPosition().getZ());
        activeChar.broadcastPacket(Gon);
        activeChar.revalidateZone(true);
    }

    /* (non-Javadoc)
     * @see com.it.br.gameserver.network.clientpackets.ClientBasePacket#getType()
     */

	@Override
	public String getType()
    {
        return _C__5C_GETONVEHICLE;
    }
}
