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

import com.it.br.gameserver.TaskPriority;
import com.it.br.gameserver.ai.CtrlIntention;
import com.it.br.gameserver.instancemanager.BoatManager;
import com.it.br.gameserver.model.L2CharPosition;
import com.it.br.gameserver.model.actor.instance.L2BoatInstance;
import com.it.br.gameserver.model.actor.instance.L2PcInstance;
import com.it.br.gameserver.network.serverpackets.ActionFailed;
import com.it.br.gameserver.templates.L2WeaponType;
import com.it.br.util.Point3D;

public final class RequestMoveToLocationInVehicle extends L2GameClientPacket
{
	private final Point3D _pos = new Point3D(0,0,0);
	private final Point3D _origin_pos = new Point3D(0,0,0);
	private int _boatId;

	public TaskPriority getPriority() { return TaskPriority.PR_HIGH; }

	/**
	 * @param buf
	 * @param client
	 */

	@Override
	protected void readImpl()
	{
		int _x, _y, _z;
		_boatId  = readD();   //objectId of boat
		_x = readD();
		_y = readD();
		_z = readD();
		_pos.setXYZ(_x, _y, _z);
		_x = readD();
		_y = readD();
		_z = readD();
		_origin_pos.setXYZ(_x, _y, _z);
	}

	/* (non-Javadoc)
	 * @see com.it.br.gameserver.network.clientpackets.ClientBasePacket#runImpl()
	 */

	@Override
	protected
	void runImpl()
	{
		L2PcInstance activeChar = getClient().getActiveChar();
		if (activeChar == null)
			return;
		else if (activeChar.isAttackingNow() && activeChar.getActiveWeaponItem() != null && (activeChar.getActiveWeaponItem().getItemType() == L2WeaponType.BOW))
		{
			activeChar.sendPacket(new ActionFailed());
		}
		else
		{
			if(!activeChar.isInBoat())
			{
				activeChar.setInBoat(true);
			}
			L2BoatInstance boat = BoatManager.getInstance().GetBoat(_boatId);
			activeChar.setBoat(boat);
			activeChar.setInBoatPosition(_pos);
			activeChar.getAI().setIntention(CtrlIntention.AI_INTENTION_MOVE_TO_IN_A_BOAT, new L2CharPosition(_pos.getX(),_pos.getY(), _pos.getZ(), 0), new L2CharPosition(_origin_pos.getX(),_origin_pos.getY(),_origin_pos.getZ(), 0));
		}
	}

	/**
	 * @return
	 */

	/* (non-Javadoc)
	 * @see com.it.br.gameserver.BasePacket#getType()
	 */

	@Override
	public String getType()
	{
		// TODO Auto-generated method stub
		return null;
	}
}