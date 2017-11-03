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
package com.it.br.gameserver.handler;

import java.util.Map;
import java.util.TreeMap;

import com.it.br.gameserver.handler.itemhandlers.BeastSoulShot;
import com.it.br.gameserver.handler.itemhandlers.BeastSpice;
import com.it.br.gameserver.handler.itemhandlers.BeastSpiritShot;
import com.it.br.gameserver.handler.itemhandlers.BlessedSpiritShot;
import com.it.br.gameserver.handler.itemhandlers.Book;
import com.it.br.gameserver.handler.itemhandlers.CharChangePotions;
import com.it.br.gameserver.handler.itemhandlers.ChestKey;
import com.it.br.gameserver.handler.itemhandlers.ChristmasTree;
import com.it.br.gameserver.handler.itemhandlers.CrystalCarol;
import com.it.br.gameserver.handler.itemhandlers.EnchantScrolls;
import com.it.br.gameserver.handler.itemhandlers.EnergyStone;
import com.it.br.gameserver.handler.itemhandlers.ExtractableItems;
import com.it.br.gameserver.handler.itemhandlers.Firework;
import com.it.br.gameserver.handler.itemhandlers.FishShots;
import com.it.br.gameserver.handler.itemhandlers.Harvester;
import com.it.br.gameserver.handler.itemhandlers.HeroCustomItem;
import com.it.br.gameserver.handler.itemhandlers.Maps;
import com.it.br.gameserver.handler.itemhandlers.MercTicket;
import com.it.br.gameserver.handler.itemhandlers.MysteryPotion;
import com.it.br.gameserver.handler.itemhandlers.NobleCustomItem;
import com.it.br.gameserver.handler.itemhandlers.OfflineCustomItem;
import com.it.br.gameserver.handler.itemhandlers.PaganKeys;
import com.it.br.gameserver.handler.itemhandlers.Potions;
import com.it.br.gameserver.handler.itemhandlers.Recipes;
import com.it.br.gameserver.handler.itemhandlers.Remedy;
import com.it.br.gameserver.handler.itemhandlers.RollingDice;
import com.it.br.gameserver.handler.itemhandlers.ScrollOfEscape;
import com.it.br.gameserver.handler.itemhandlers.ScrollOfResurrection;
import com.it.br.gameserver.handler.itemhandlers.Scrolls;
import com.it.br.gameserver.handler.itemhandlers.Seed;
import com.it.br.gameserver.handler.itemhandlers.SevenSignsRecord;
import com.it.br.gameserver.handler.itemhandlers.SoulCrystals;
import com.it.br.gameserver.handler.itemhandlers.SoulShots;
import com.it.br.gameserver.handler.itemhandlers.SpecialXMas;
import com.it.br.gameserver.handler.itemhandlers.SpiritShot;
import com.it.br.gameserver.handler.itemhandlers.SummonItems;

public class ItemHandler
{
	private static ItemHandler _instance;
	private Map<Integer, IItemHandler> _datatable;

	/**
	 * Create ItemHandler if doesn't exist and returns ItemHandler
	 * @return ItemHandler
	 */
	public static ItemHandler getInstance()
	{
		if (_instance == null)
		{
			_instance = new ItemHandler();
		}
		return _instance;
	}

	/**
	 * Returns the number of elements contained in datatable
	 * @return int : Size of the datatable
	 */
    public int size()
    {
        return _datatable.size();
    }

    /**
     * Constructor of ItemHandler
     */
	private ItemHandler()
	{
		_datatable = new TreeMap<Integer, IItemHandler>();
		registerItemHandler(new BlessedSpiritShot());
		registerItemHandler(new BeastSoulShot());
		registerItemHandler(new BeastSpiritShot());
		registerItemHandler(new BeastSpice());
		registerItemHandler(new Book());
		registerItemHandler(new CharChangePotions());
		registerItemHandler(new ChestKey());
		registerItemHandler(new ChristmasTree());
		registerItemHandler(new CrystalCarol());
		registerItemHandler(new EnchantScrolls());
		registerItemHandler(new EnergyStone());
		registerItemHandler(new ExtractableItems());
		registerItemHandler(new Firework());
		registerItemHandler(new FishShots());
		registerItemHandler(new Harvester());
		registerItemHandler(new HeroCustomItem());
		registerItemHandler(new Maps());
		registerItemHandler(new MysteryPotion());
		registerItemHandler(new MercTicket());
		registerItemHandler(new NobleCustomItem());
		registerItemHandler(new OfflineCustomItem());
		registerItemHandler(new PaganKeys());
		registerItemHandler(new Potions());
		registerItemHandler(new Recipes());
		registerItemHandler(new RollingDice());
		registerItemHandler(new Remedy());
		registerItemHandler(new Scrolls());
		registerItemHandler(new SoulCrystals());
		registerItemHandler(new SevenSignsRecord());
		registerItemHandler(new Seed());
		registerItemHandler(new ScrollOfEscape());
		registerItemHandler(new ScrollOfResurrection());
		registerItemHandler(new SoulShots());
		registerItemHandler(new SpiritShot());
		registerItemHandler(new SpecialXMas());
		registerItemHandler(new SummonItems());
	}

	/**
	 * Adds handler of item type in <I>datatable</I>.<BR><BR>
	 * <B><I>Concept :</I></U><BR>
	 * This handler is put in <I>datatable</I> Map &lt;Integer ; IItemHandler &gt; for each ID corresponding to an item type
	 * (existing in classes of package itemhandlers) sets as key of the Map.
	 * @param handler (IItemHandler)
	 */
	public void registerItemHandler(IItemHandler handler)
	{
		// Get all ID corresponding to the item type of the handler
		int[] ids = handler.getItemIds();
		// Add handler for each ID found
		for (int i = 0; i < ids.length; i++)
		{
			_datatable.put(new Integer(ids[i]), handler);
		}
	}

	/**
	 * Returns the handler of the item
	 * @param itemId : int designating the itemID
	 * @return IItemHandler
	 */
	public IItemHandler getItemHandler(int itemId)
	{
		return _datatable.get(new Integer(itemId));
	}
}