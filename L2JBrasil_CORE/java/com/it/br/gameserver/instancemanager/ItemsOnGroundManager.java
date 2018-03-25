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
package com.it.br.gameserver.instancemanager;

import com.it.br.Config;
import com.it.br.gameserver.ThreadPoolManager;
import com.it.br.gameserver.database.dao.ItemsOnGroundDao;
import com.it.br.gameserver.model.L2ItemInstance;
import com.it.br.gameserver.model.L2Object;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * This class manage all items on ground
 *
 * @refactor by Tayran
 * @version 3.0.4
 * @author  DiezelMax - original ideea
 * @author  Enforcer  - actual build
 */
public class ItemsOnGroundManager
{
    static final Logger _log = LoggerFactory.getLogger(ItemsOnGroundManager.class);
    private static ItemsOnGroundManager _instance;

    public static List<L2ItemInstance> getItems() {
        return _items;
    }

    protected static List<L2ItemInstance> _items = null;

    public ItemsOnGroundManager()
    {
    	if(!Config.SAVE_DROPPED_ITEM) return;
    		_items = new ArrayList<>();
    	if (Config.SAVE_DROPPED_ITEM_INTERVAL >0)
    			ThreadPoolManager.getInstance().scheduleGeneralAtFixedRate(new storeInDb(), Config.SAVE_DROPPED_ITEM_INTERVAL, Config.SAVE_DROPPED_ITEM_INTERVAL);
    }

    public static final ItemsOnGroundManager getInstance()
    {
        if (_instance == null)
        {
            _instance = new ItemsOnGroundManager();
            _instance.load();
        }
        return _instance;
    }

    private void load()
    {
    	// If SaveDroppedItem is false, may want to delete all items previously stored to avoid add old items on reactivate
        if(!Config.SAVE_DROPPED_ITEM && Config.CLEAR_DROPPED_ITEM_TABLE)
        	emptyTable();


        if(!Config.SAVE_DROPPED_ITEM)
        	return;

        // if DestroyPlayerDroppedItem was previously  false, items curently protected will be added to ItemsAutoDestroy
        if (Config.DESTROY_DROPPED_PLAYER_ITEM)
        {
            ItemsOnGroundDao.updateItemsGroundOnLoad();
        }
        int count = 0;

        ItemsOnGroundDao.selectItemsOnGround();

        if (Config.EMPTY_DROPPED_ITEM_TABLE_AFTER_LOAD)
            emptyTable();
    }

    public void save(L2ItemInstance item)
    {
        if(!Config.SAVE_DROPPED_ITEM) return;
        _items.add(item);
    }

    public void removeObject(L2Object item)
    {
        if(!Config.SAVE_DROPPED_ITEM) return;
        _items.remove(item);
    }

    public void saveInDb()
    {
    	new storeInDb().run();
    }

    public void cleanUp()
    {
    	_items.clear();
    }

    public void emptyTable()
    {
        ItemsOnGroundDao.deleteAllItems();
    }

    protected class storeInDb extends Thread {

        @Override
        public void run() {
            if (!Config.SAVE_DROPPED_ITEM) return;

            emptyTable();

            if (_items.isEmpty()) {
                if (Config.DEBUG)
                    _log.warn("ItemsOnGroundManager: nothing to save...");
                return;
            }

            for (L2ItemInstance item : _items) {

                if (CursedWeaponsManager.getInstance().isCursed(item.getItemId()))
                    continue; // Cursed Items not saved to ground, prevent double save

                ItemsOnGroundDao.insert(item);
            }
            if (Config.DEBUG)
                _log.warn("ItemsOnGroundManager: " + _items.size() + " items on ground saved");
        }
    }
}
