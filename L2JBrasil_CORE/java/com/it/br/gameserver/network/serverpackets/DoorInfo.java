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

import com.it.br.gameserver.model.actor.instance.L2DoorInstance;

public class DoorInfo extends L2GameServerPacket
{
	private static final String _S__60_DOORINFO = "[S] 4c DoorInfo";
	private L2DoorInstance _door;
	private final int _type;
	private final boolean _isTargetable;
	private final boolean _isClosed;
	private final int _maxHp;
	private final int _currentHp;
	private final boolean _showHp; 
	private final int _damageGrade;
  
	public DoorInfo(L2DoorInstance door, boolean showHp)
	{
		_type = 1;
		_door = door;
		_isTargetable = true;
		_isClosed = true;
		_maxHp = door.getMaxHp();
		_currentHp = (int) door.getCurrentHp();
		_showHp = showHp;
		_damageGrade = door.getDamage(); 
	}

	@Override
	protected final void writeImpl()
	{
		writeC(0x4c);
		writeD(_door.getObjectId());
		writeD(_door.getDoorId());
		writeD(_type);
		writeD(_isTargetable ? 1 : 0);
		writeD(_isClosed ? 1 : 0);
		writeD(_door.isEnemyOf(getClient().getActiveChar()) ? 1 : 0);
		writeD(_currentHp);
		writeD(_maxHp);
		writeD(_showHp ? 1 : 0);  
		writeD(_damageGrade);
	}

	@Override
	public String getType()
	{
		return _S__60_DOORINFO;
	}
}