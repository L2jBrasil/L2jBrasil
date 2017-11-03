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
package com.it.br.gameserver.pathfinding.utils;

import com.it.br.gameserver.pathfinding.Node;

/**
 *
 * @author -Nemesiss-
 */
public class FastNodeList
{
	private Node[] _list;
	private int _size;

	public FastNodeList(int size)
	{
		_list = new Node[size];
	}
	public void add(Node n)
	{
		_list[_size] = n;
		_size++;
	}
	public boolean contains(Node n)
	{
		for (int i =0; i < _size; i++)
			if(_list[i].equals(n))
				return true;
		return false;
	}
	public boolean containsRev(Node n)
	{
		for (int i=_size-1; i >= 0; i--)
			if(_list[i].equals(n))
				return true;
		return false;
	}
}
