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
package com.it.br.gameserver.model;

/**
 *
 * @author -Nemesiss-
 */
public class L2SummonItem
{
	private final int _itemId;
	private final int  _npcId;
	private final byte  _type;

	public L2SummonItem(int itemId, int npcId, byte type)
	{
		_itemId = itemId;
		_npcId = npcId;
		_type = type;
	}

	public int getItemId()
	{
		return _itemId;
	}

	public int getNpcId()
	{
		return _npcId;
	}

	public byte getType()
	{
		return _type;
	}

	public boolean isPetSummon()
	{
		return _type == 1 || _type == 2;
	}
}
