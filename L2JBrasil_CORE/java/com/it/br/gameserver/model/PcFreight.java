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

import com.it.br.gameserver.model.L2ItemInstance.ItemLocation;
import com.it.br.gameserver.model.actor.instance.L2PcInstance;

import java.util.ArrayList;
import java.util.List;

public class PcFreight extends ItemContainer
{
    //private static final Logger _log = LoggerFactory.getLogger(PcFreight.class);

    private L2PcInstance _owner;    // This is the L2PcInstance that owns this Freight;
    private int _activeLocationId;

    public PcFreight(L2PcInstance owner)
    {
        _owner = owner;
    }


	@Override
	public L2PcInstance getOwner() { return _owner; }

	@Override
	public ItemLocation getBaseLocation() { return ItemLocation.FREIGHT; }
    public void setActiveLocation(int locationId) { _activeLocationId = locationId; }
    public int getactiveLocation() { return _activeLocationId; }

	/**
	 * Returns the quantity of items in the inventory
	 * @return int
	 */

	@Override
	public int getSize()
	{
		int size = 0;
		for (L2ItemInstance item : _items)
		{
			if (item.getEquipSlot() == 0 || _activeLocationId == 0
        			|| item.getEquipSlot() == _activeLocationId) size++;
		}
		return size;
	}

	/**
	 * Returns the list of items in inventory
	 * @return L2ItemInstance : items in inventory
	 */

	@Override
	public L2ItemInstance[] getItems()
	{
		List<L2ItemInstance> list = new ArrayList<>();
		for (L2ItemInstance item : _items)
		{
			if (item.getEquipSlot() == 0 || item.getEquipSlot() == _activeLocationId) list.add(item);
		}

		return list.toArray(new L2ItemInstance[list.size()]);
	}

	/**
	 * Returns the item from inventory by using its <B>itemId</B>
	 * @param itemId : int designating the ID of the item
	 * @return L2ItemInstance designating the item or null if not found in inventory
	 */

	@Override
	public L2ItemInstance getItemByItemId(int itemId)
	{
	    for (L2ItemInstance item : _items)
	        if ((item.getItemId() == itemId)
	        	&& (item.getEquipSlot() == 0 || _activeLocationId == 0
	        			|| item.getEquipSlot() == _activeLocationId)
	        	) return item;

	    return null;
	}

    /**
     * Adds item to PcFreight for further adjustments.
     * @param item : L2ItemInstance to be added from inventory
     */

	@Override
	protected void addItem(L2ItemInstance item)
    {
        super.addItem(item);
    	if (_activeLocationId > 0) item.setLocation(item.getLocation(), _activeLocationId);
    }

    /**
	 * Get back items in PcFreight from database
	 */

	@Override
	public void restore()
    {
    	int locationId = _activeLocationId;
    	_activeLocationId = 0;
    	super.restore();
    	_activeLocationId = locationId;
    }


	@Override
	public boolean validateCapacity(int slots)
	{
		return (getSize() + slots <= _owner.GetFreightLimit());
	}
}