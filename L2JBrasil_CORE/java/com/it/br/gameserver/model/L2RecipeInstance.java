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
package com.it.br.gameserver.model;

/**
 * This class describes a RecipeList componant (1 line of the recipe : Item-Quantity needed).<BR><BR>
 */
public class L2RecipeInstance
{
	/** The Identifier of the item needed in the L2RecipeInstance */
    private int _itemId;

	/** The item quantity needed in the L2RecipeInstance */
    private int _quantity;


	/**
	 * Constructor of L2RecipeInstance (create a new line in a RecipeList).<BR><BR>
	 */
    public L2RecipeInstance(int itemId, int quantity)
    {
        _itemId = itemId;
        _quantity = quantity;
    }

	/**
	 * Return the Identifier of the L2RecipeInstance Item needed.<BR><BR>
	 */
    public int getItemId()
    {
        return _itemId;
    }

	/**
	 * Return the Item quantity needed of the L2RecipeInstance.<BR><BR>
	 */
    public int getQuantity()
    {
        return _quantity;
    }

}
