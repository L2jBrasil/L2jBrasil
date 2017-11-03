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
public class L2ExtractableProductItem
{
	private final int _id;
	private final int _ammount;
	private final int _chance;

	public L2ExtractableProductItem(int id, int ammount, int chance)
	{
		_id = id;
		_ammount = ammount;
		_chance = chance;
	}

	public int getId()
	{
		return _id;
	}

	public int getAmmount()
	{
		return _ammount;
	}

	public int getChance()
	{
		return _chance;
	}
}
