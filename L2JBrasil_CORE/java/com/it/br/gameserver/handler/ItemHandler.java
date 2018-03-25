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

import com.it.br.gameserver.handler.itemhandlers.*;

import java.util.Map;
import java.util.TreeMap;

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