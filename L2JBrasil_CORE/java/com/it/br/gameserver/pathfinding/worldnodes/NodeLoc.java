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
package com.it.br.gameserver.pathfinding.worldnodes;

import com.it.br.gameserver.pathfinding.AbstractNodeLoc;

/**
 *
 * @author -Nemesiss-
 */
public class NodeLoc extends AbstractNodeLoc
{
	private final int _x;
	private final int _y;
	private final short _z;

	public NodeLoc(int x, int y, short z)
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
		return _x;
	}

	/**
	 * @see com.it.br.gameserver.pathfinding.AbstractNodeLoc#getY()
	 */

	@Override
	public int getY()
	{
		return _y;
	}

	/**
	 * @see com.it.br.gameserver.pathfinding.AbstractNodeLoc#getZ()
	 */

	@Override
	public short getZ()
	{
		return _z;
	}

	/**
	 * @see com.it.br.gameserver.pathfinding.AbstractNodeLoc#getNodeX()
	 */

	@Override
	public short getNodeX()
	{
		// TODO Auto-generated method stub
		return 0;
	}

	/**
	 * @see com.it.br.gameserver.pathfinding.AbstractNodeLoc#getNodeY()
	 */

	@Override
	public short getNodeY()
	{
		// TODO Auto-generated method stub
		return 0;
	}

}
