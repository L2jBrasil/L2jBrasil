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


public class ItemRequest
{
	int _objectId;
	int _itemId;
	int _count;
	int _price;

	public ItemRequest(int objectId, int count, int price)
	{
		_objectId = objectId;
		_count = count;
		_price = price;
	}

	public ItemRequest(int objectId, int itemId, int count, int price)
	{
		_objectId = objectId;
		_itemId = itemId;
		_count = count;
		_price = price;
	}

	public int getObjectId(){return _objectId;}
	public int getItemId(){return _itemId;}
	public void setCount(int count){_count = count;}
	public int getCount(){return _count;}
	public int getPrice(){return _price;}
}
