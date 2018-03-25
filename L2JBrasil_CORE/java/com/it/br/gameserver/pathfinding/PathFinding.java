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
package com.it.br.gameserver.pathfinding;

import com.it.br.gameserver.model.L2World;
import com.it.br.gameserver.pathfinding.geonodes.GeoPathFinding;
import com.it.br.gameserver.pathfinding.utils.BinaryNodeHeap;
import com.it.br.gameserver.pathfinding.utils.FastNodeList;

import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author -Nemesiss-
 */
public abstract class PathFinding
{
	private static PathFinding _instance;

	public static PathFinding getInstance()
	{
		if (_instance == null)
		{
			{
				//Smaler Memory Usage, Higher Cpu Usage (CalculatedOnTheFly)
				return GeoPathFinding.getInstance();
			}
		}
		return _instance;
	}

	public abstract boolean pathNodesExist(short regionoffset);
	public abstract List<AbstractNodeLoc> findPath(int gx, int gy, short z, int gtx, int gtz, short tz);
	public abstract Node[] readNeighbors(short node_x,short node_y, int idx);

	public List<AbstractNodeLoc> search(Node start, Node end)
	{
		// The simplest grid-based pathfinding.
		// Drawback is not having higher cost for diagonal movement (means funny routes)
		// Could be optimized e.g. not to calculate backwards as far as forwards.

		// List of Visited Nodes
		LinkedList<Node> visited = new LinkedList<Node>();

		// List of Nodes to Visit
		LinkedList<Node> to_visit = new LinkedList<Node>();
		to_visit.add(start);

		int i = 0;
		while (i < 800)//TODO! Add limit to cfg. Worst case max distance is 1810..
		{
			Node node;
			try
			{
				 node = to_visit.removeFirst();
			}
			catch (Exception e)
			{
				// No Path found
				return null;
			}
			if (node.equals(end)) //path found!
				return constructPath(node);
			else
			{
				i++;
				visited.add(node);
				node.attacheNeighbors();
				Node[] neighbors = node.getNeighbors();
				if (neighbors == null) continue;
				for (Node n : neighbors)
				{
					if (!visited.contains(n) && !to_visit.contains(n))
					{
						n.setParent(node);
						to_visit.add(n);
					}
				}
			}
		}
		//No Path found
		return null;
	}

	public List<AbstractNodeLoc> searchByClosest(Node start, Node end)
	{
		// Always continues checking from the closest to target non-blocked
		// node from to_visit list. There's extra length in path if needed
		// to go backwards/sideways but when moving generally forwards, this is extra fast
		// and accurate. And can reach insane distances (try it with 800 nodes..).
		// Minimum required node count would be around 300-400.
		// Generally returns a bit (only a bit) more intelligent looking routes than
		// the basic version. Not a true distance image (which would increase CPU
		// load) level of intelligence though.

		// List of Visited Nodes
		FastNodeList visited = new FastNodeList(550);

		// List of Nodes to Visit
		LinkedList<Node> to_visit = new LinkedList<Node>();
		to_visit.add(start);
		short targetx = end.getLoc().getNodeX();
		short targety = end.getLoc().getNodeY();
		int dx, dy;
		boolean added;
		int i = 0;
		while (i < 550)
		{
			Node node;
			try
			{
				 node = to_visit.removeFirst();
			}
			catch (Exception e)
			{
				// No Path found
				return null;
			}
			if (node.equals(end)) //path found!
				return constructPath(node);
			else
			{
				i++;
				visited.add(node);
				node.attacheNeighbors();
				Node[] neighbors = node.getNeighbors();
				if (neighbors == null) continue;
				for (Node n : neighbors)
				{
					if (!visited.containsRev(n) && !to_visit.contains(n))
					{
						added = false;
						n.setParent(node);
						dx = targetx - n.getLoc().getNodeX();
						dy = targety - n.getLoc().getNodeY();
						n.setCost(dx*dx+dy*dy);
						for (int index = 0; index < to_visit.size(); index++)
						{
							// supposed to find it quite early..
							if (to_visit.get(index).getCost() > n.getCost())
							{
								to_visit.add(index, n);
								added = true;
								break;
							}
						}
						if (!added) to_visit.addLast(n);
					}
				}
			}
		}
		//No Path found
		return null;
	}


	public List<AbstractNodeLoc> searchAStar(Node start, Node end)
	{
		// Not operational yet?
		int start_x = start.getLoc().getX();
		int start_y = start.getLoc().getY();
		int end_x = end.getLoc().getX();
		int end_y = end.getLoc().getY();
		//List of Visited Nodes
		FastNodeList visited = new FastNodeList(800);//TODO! Add limit to cfg

		// List of Nodes to Visit
		BinaryNodeHeap to_visit = new BinaryNodeHeap(800);
		to_visit.add(start);

		int i = 0;
		while (i < 800)//TODO! Add limit to cfg
		{
			Node node;
			try
			{
				 node = to_visit.removeFirst();
			}
			catch (Exception e)
			{
				// No Path found
				return null;
			}
			if (node.equals(end)) //path found!
				return constructPath(node);
			else
			{
				visited.add(node);
				node.attacheNeighbors();
				for (Node n : node.getNeighbors())
				{
					if (!visited.contains(n) && !to_visit.contains(n))
					{
						i++;
						n.setParent(node);
						n.setCost(Math.abs(start_x - n.getLoc().getNodeX())+Math.abs(start_y - n.getLoc().getNodeY())
								+Math.abs(end_x - n.getLoc().getNodeX())+Math.abs(end_y - n.getLoc().getNodeY()));
						to_visit.add(n);
					}
				}
			}
		}
		//No Path found
		return null;
	}

	public List<AbstractNodeLoc> constructPath(Node node)
	{
		LinkedList<AbstractNodeLoc> path = new LinkedList<AbstractNodeLoc>();
		int previousdirectionx = -1000;
		int previousdirectiony = -1000;
		int directionx;
		int directiony;
		while (node.getParent() != null)
		{
			// only add a new route point if moving direction changes
			directionx = node.getLoc().getNodeX() - node.getParent().getLoc().getNodeX();
			directiony = node.getLoc().getNodeY() - node.getParent().getLoc().getNodeY();
			if(directionx != previousdirectionx || directiony != previousdirectiony)
			{
				previousdirectionx = directionx;
				previousdirectiony = directiony;
				path.addFirst(node.getLoc());
			}
			node = node.getParent();
		}
		return path;
	}

	/**
	 * Convert geodata position to pathnode position
	 * @param geo_pos
	 * @return pathnode position
	 */
	public short getNodePos(int geo_pos)
	{
		return (short)(geo_pos >> 3); //OK?
	}

	/**
	 * Convert node position to pathnode block position
	 * @param geo_pos
	 * @return pathnode block position (0...255)
	 */
	public short getNodeBlock(int node_pos)
	{
		return (short)(node_pos % 256);
	}

	public byte getRegionX(int node_pos)
	{
		return (byte)((node_pos >> 8) + 16);
	}

	public byte getRegionY(int node_pos)
	{
		return (byte)((node_pos >> 8) + 10);
	}

	public short getRegionOffset(byte rx, byte ry)
	{
		return (short)((rx << 5) + ry);
	}

	/**
	 * Convert pathnode x to World x position
	 * @param node_x, rx
	 * @return
	 */
	public int calculateWorldX(short node_x)
	{
		return   L2World.MAP_MIN_X  + node_x * 128 + 48 ;
	}

	/**
	 * Convert pathnode y to World y position
	 * @param node_y
	 * @return
	 */
	public int calculateWorldY(short node_y)
	{
		return  L2World.MAP_MIN_Y + node_y * 128 + 48 ;
	}
}
