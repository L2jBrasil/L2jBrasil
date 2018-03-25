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
package com.it.br.gameserver.templates;

import com.it.br.gameserver.model.base.ClassId;
import com.it.br.gameserver.model.base.Race;

import java.util.ArrayList;
import java.util.List;

public class L2PcTemplate extends L2CharTemplate
{
	private final List<PcTemplateItem> _items = new ArrayList<>();
	
	public final Race race;
	public final ClassId classId;

	public final int _currentCollisionRadius;
	public final int _currentCollisionHeight;
	public final String className;

	public final int spawnX;
	public final int spawnY;
	public final int spawnZ;

	public final int classBaseLevel;
	public final float lvlHpAdd;
	public final float lvlHpMod;
	public final float lvlCpAdd;
	public final float lvlCpMod;
	public final float lvlMpAdd;
	public final float lvlMpMod;

	public L2PcTemplate(StatsSet set)
	{
		super(set);
		classId = ClassId.values()[set.getInteger("classId")];
		race = Race.values()[set.getInteger("raceId")];
		className = set.getString("className");
		_currentCollisionRadius = set.getInteger("collision_radius");
		_currentCollisionHeight = set.getInteger("collision_height");

		spawnX = set.getInteger("spawnX");
		spawnY = set.getInteger("spawnY");
		spawnZ = set.getInteger("spawnZ");

		classBaseLevel = set.getInteger("classBaseLevel");
		lvlHpAdd = set.getFloat("lvlHpAdd");
		lvlHpMod = set.getFloat("lvlHpMod");
		lvlCpAdd = set.getFloat("lvlCpAdd");
		lvlCpMod = set.getFloat("lvlCpMod");
		lvlMpAdd = set.getFloat("lvlMpAdd");
		lvlMpMod = set.getFloat("lvlMpMod");
	}

	public void addItem(int itemId, int amount, boolean equipped)
	{
		_items.add(new PcTemplateItem(itemId, amount, equipped));
	}
	
	public List<PcTemplateItem> getItems()
	{
		return _items;
	}

	
	public final int getFallHeight()  
    {  
         return 333; // TODO: unhardcode it
    }
	
	public static final class PcTemplateItem
	{
		private final int _itemId;
		private final int _amount;
		private final boolean _equipped;

		public PcTemplateItem(int itemId, int amount, boolean equipped)
		{
			_itemId = itemId;
			_amount = amount;
			_equipped = equipped;
		}

		public int getItemId()
		{
			return _itemId;
		}

		public int getAmount()
		{
			return _amount;
		}

		public boolean isEquipped()
		{
			return _equipped;
		}
	}

}