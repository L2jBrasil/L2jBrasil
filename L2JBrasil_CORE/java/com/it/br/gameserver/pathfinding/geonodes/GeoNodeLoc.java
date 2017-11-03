/* This program is free software; you can redistribute it and/or modify
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
package com.it.br.gameserver.pathfinding.geonodes;

import com.it.br.gameserver.model.L2World;
import com.it.br.gameserver.pathfinding.AbstractNodeLoc;

/**
 *
 * @author -Nemesiss-
 */
public class GeoNodeLoc extends AbstractNodeLoc
{
	private final short _x;
	private final short _y;
	private final short _z;

	public GeoNodeLoc(short x, short y, short z)
	{
		_x = x;
		_y = y;
		_z = z;
	}

	/**
	 * @see com.it.br.gameserver.pathfinding.AbstractNodeLoc#getX()
	 */

	@Override
	public int getX()
	{
		return   L2World.MAP_MIN_X  + _x * 128 + 48 ;
	}

	/**
	 * @see com.it.br.gameserver.pathfinding.AbstractNodeLoc#getY()
	 */

	@Override
	public int getY()
	{
		return  L2World.MAP_MIN_Y + _y * 128 + 48 ;
	}

	/**
	 * @see com.it.br.gameserver.pathfinding.AbstractNodeLoc#getZ()
	 */

	@Override
	public short getZ()
	{
		return _z;
	}


	@Override
	public short getNodeX()
	{
		return _x;
	}


	@Override
	public short getNodeY()
	{
		return _y;
	}
}
