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

import com.it.br.util.Point3D;

import java.util.List;

/**
 * Format: (ch) d[ddddd]
 *
 * @author  -Wooden-
 */
public class ExCursedWeaponLocation extends L2GameServerPacket
{
	private static final String _S__FE_46_EXCURSEDWEAPONLOCATION = "[S] FE:46 ExCursedWeaponLocation";
	private List<CursedWeaponInfo> _cursedWeaponInfo;

	public ExCursedWeaponLocation(List<CursedWeaponInfo> cursedWeaponInfo)
	{
		_cursedWeaponInfo = cursedWeaponInfo;
	}

	/**
	 * @see com.it.br.gameserver.network.serverpackets.ServerBasePacket#writeImpl()
	 */

	@Override
	protected void writeImpl()
	{
		writeC(0xfe);
		writeH(0x46);

		if(!_cursedWeaponInfo.isEmpty())
		{
			writeD(_cursedWeaponInfo.size());
			for(CursedWeaponInfo w : _cursedWeaponInfo)
			{
				writeD(w.id);
				writeD(w.activated);

				writeD(w.pos.getX());
				writeD(w.pos.getY());
				writeD(w.pos.getZ());
			}
		}
		else
		{
			writeD(0);
			writeD(0);
		}
	}

	/**
	 * @see com.it.br.gameserver.BasePacket#getType()
	 */

	@Override
	public String getType()
	{
		return _S__FE_46_EXCURSEDWEAPONLOCATION;
	}

	public static class CursedWeaponInfo
	{
		public Point3D pos;
		public int id;
		public int activated; //0 - not activated ? 1 - activated

		public CursedWeaponInfo(Point3D p, int ID, int status)
		{
			pos = p;
			id = ID;
			activated = status;
		}

	}
}