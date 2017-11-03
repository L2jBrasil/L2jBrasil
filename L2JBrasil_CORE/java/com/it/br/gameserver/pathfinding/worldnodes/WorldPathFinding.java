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

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.it.br.gameserver.pathfinding.AbstractNodeLoc;
import com.it.br.gameserver.pathfinding.Node;
import com.it.br.gameserver.pathfinding.PathFinding;

/**
 *
 * @author -Nemesiss-
 */
public class WorldPathFinding extends PathFinding
{
	//private static Logger _log = Logger.getLogger(WorldPathFinding.class.getName());
	private static WorldPathFinding _instance;
    @SuppressWarnings("unused")
	private static Map<Short, ByteBuffer> _pathNodes = new HashMap<>();
	private static Map<Short, IntBuffer> _pathNodesIndex = new HashMap<>();

	public static WorldPathFinding getInstance()
	{
		if (_instance == null)
			_instance = new WorldPathFinding();
		return _instance;
	}


	@Override
	public boolean pathNodesExist(short regionoffset)
	{
		return _pathNodesIndex.containsKey(regionoffset);
	}

 	@Override
	public List<AbstractNodeLoc> findPath(int gx, int gy, short z, int gtx,	int gtz, short tz)
	{
		return null;
	}

	@Override
	public Node[] readNeighbors(short node_x,short node_y, int idx)
	{
		// TODO Auto-generated method stub
		return null;
	}

	//Private

	private WorldPathFinding()
	{
		//TODO! {Nemesiss] Load PathNodes.
	}
}
