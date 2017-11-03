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
package com.it.br.gameserver.network.serverpackets;

public class Ride extends L2GameServerPacket
{
    private static final String _S__86_Ride = "[S] 86 Ride";
    public static final int ACTION_MOUNT = 1;
    public static final int ACTION_DISMOUNT = 0;
    private int _id;
    private int _bRide;
    private int _rideType;
    private int _rideClassID;

    public Ride(int id, int action, int rideClassId)
    {
        _id = id; // charobjectID
        _bRide = action; // 1 for mount ; 2 for dismount
        _rideClassID = rideClassId + 1000000; // npcID

        if (rideClassId == 12526 || //wind strider
            rideClassId == 12527 || //star strider
            rideClassId == 12528) //twilight strider
        {
            _rideType = 1; // 1 for Strider ; 2 for wyvern
        }
        else if (rideClassId == 12621) // wyvern
        {
            _rideType = 2; // 1 for Strider ; 2 for wyvern
        }
    }


    @Override
	public void runImpl()
    {

    }

    public int getMountType()
    {
        return _rideType;
    }


	@Override
	protected final void writeImpl()
    {

        writeC(0x86);
        writeD(_id);
        writeD(_bRide);
        writeD(_rideType);
        writeD(_rideClassID);
    }

    /* (non-Javadoc)
     * @see com.it.br.gameserver.network.serverpackets.ServerBasePacket#getType()
     */

	@Override
	public String getType()
    {
        return _S__86_Ride;
    }
}
