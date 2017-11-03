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
 * This class describes a Recipe used by Dwarf to craft Item.
 * All L2RecipeList are made of L2RecipeInstance (1 line of the recipe : Item-Quantity needed).<BR><BR>
 *
 */
public class L2RecipeList
{
	/** The table containing all L2RecipeInstance (1 line of the recipe : Item-Quantity needed) of the L2RecipeList */
	private L2RecipeInstance[] _recipes;

	/** The Identifier of the Instance */
	private int _id;

	/** The crafting level needed to use this L2RecipeList */
	private int _level;

	/** The Identifier of the L2RecipeList */
	private int _recipeId;

	/** The name of the L2RecipeList */
	private String _recipeName;

	/** The crafting succes rate when using the L2RecipeList */
	private int _successRate;

	/** The crafting MP cost of this L2RecipeList */
	private int _mpCost;

	/** The Identifier of the Item crafted with this L2RecipeList */
	private int _itemId;

	/** The quantity of Item crafted when using this L2RecipeList */
	private int _count;

	/** If this a common or a dwarven recipe */
	private boolean _isDwarvenRecipe;

	/**
	 * Constructor of L2RecipeList (create a new Recipe).<BR><BR>
	 */
	public L2RecipeList(int id, int level, int recipeId, String recipeName, int successRate, int mpCost, int itemId, int count, boolean isDwarvenRecipe)
	{
		_id = id;
		_recipes = new L2RecipeInstance[0];
		_level = level;
		_recipeId = recipeId;
		_recipeName = recipeName;
		_successRate = successRate;
		_mpCost = mpCost;
		_itemId = itemId;
		_count = count;
		_isDwarvenRecipe = isDwarvenRecipe;
	}

	/**
	 * Add a L2RecipeInstance to the L2RecipeList (add a line Item-Quantity needed to the Recipe).<BR><BR>
	 */
	public void addRecipe(L2RecipeInstance recipe)
	{
		int len = _recipes.length;
		L2RecipeInstance[] tmp = new L2RecipeInstance[len+1];
		System.arraycopy(_recipes, 0, tmp, 0, len);
		tmp[len] = recipe;
		_recipes = tmp;
	}


	/**
	 * Return the Identifier of the Instance.<BR><BR>
	 */
	public int getId()
	{
		return _id;
	}

	/**
	 * Return the crafting level needed to use this L2RecipeList.<BR><BR>
	 */
	public int getLevel()
	{
		return _level;
	}

	/**
	 * Return the Identifier of the L2RecipeList.<BR><BR>
	 */
	public int getRecipeId()
	{
		return _recipeId;
	}

	/**
	 * Return the name of the L2RecipeList.<BR><BR>
	 */
	public String getRecipeName()
	{
		return _recipeName;
	}

	/**
	 * Return the crafting succes rate when using the L2RecipeList.<BR><BR>
	 */
	public int getSuccessRate()
	{
		return _successRate;
	}

	/**
	 * Return the crafting MP cost of this L2RecipeList.<BR><BR>
	 */
	public int getMpCost()
	{
		return _mpCost;
	}

	/**
	 * Return rue if the Item crafted with this L2RecipeList is consubable (shot, arrow,...).<BR><BR>
	 */
	public boolean isConsumable()
	{
		return ((_itemId >= 1463 && _itemId <= 1467) // Soulshots
				|| (_itemId >= 2509 && _itemId <= 2514) // Spiritshots
				|| (_itemId >= 3947 && _itemId <= 3952) // Blessed Spiritshots
				|| (_itemId >= 1341 && _itemId <= 1345) // Arrows
		);
	}

	/**
	 * Return the Identifier of the Item crafted with this L2RecipeList.<BR><BR>
	 */
	public int getItemId()
	{
		return _itemId;
	}

	/**
	 * Return the quantity of Item crafted when using this L2RecipeList.<BR><BR>
	 */
	public int getCount()
	{
		return _count;
	}

	/**
	 * Return <B>true</B> if this a Dwarven recipe or <B>false</B> if its a Common recipe
	 */
	public boolean isDwarvenRecipe()
	{
		return _isDwarvenRecipe;
	}

	/**
	 * Return the table containing all L2RecipeInstance (1 line of the recipe : Item-Quantity needed) of the L2RecipeList.<BR><BR>
	 */
	public L2RecipeInstance[] getRecipes()
	{
		return _recipes;
	}
}

