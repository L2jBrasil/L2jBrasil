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

/**
 *
 * @author -Nemesiss-
 */
public class Node
{
	private final AbstractNodeLoc _loc;
	private final int _neighborsIdx;
	private Node[] _neighbors;
	private Node _parent;
	private short _cost;


	public Node(AbstractNodeLoc Loc, int Neighbors_idx)
	{
		_loc = Loc;
		_neighborsIdx = Neighbors_idx;
	}

	public void setParent(Node p)
	{
		_parent = p;
	}

	public void setCost(int cost)
	{
		_cost = (short)cost;
	}

	public void attacheNeighbors()
	{
		if(_loc == null) _neighbors = null;
		else _neighbors = PathFinding.getInstance().readNeighbors(_loc.getNodeX(),_loc.getNodeY(), _neighborsIdx);
	}

	public Node[] getNeighbors()
	{
		return _neighbors;
	}

	public Node getParent()
	{
		return _parent;
	}

	public AbstractNodeLoc getLoc()
	{
		return _loc;
	}

	public short getCost()
	{
		return _cost;
	}

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */

	@Override
	public boolean equals(Object arg0)
	{
		if(!(arg0 instanceof Node))
			return false;
		Node n = (Node)arg0;
		//Check if x,y,z are the same
		return _loc.getX() == n.getLoc().getX() && _loc.getY() == n.getLoc().getY()
		&& _loc.getZ() == n.getLoc().getZ();
	}
}
