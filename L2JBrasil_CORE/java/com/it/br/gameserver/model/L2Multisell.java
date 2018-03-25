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

import com.it.br.configuration.Configurator;
import com.it.br.configuration.settings.ServerSettings;
import com.it.br.gameserver.datatables.sql.ItemTable;
import com.it.br.gameserver.model.actor.instance.L2PcInstance;
import com.it.br.gameserver.network.serverpackets.MultiSellList;
import com.it.br.gameserver.templates.L2Armor;
import com.it.br.gameserver.templates.L2Item;
import com.it.br.gameserver.templates.L2Weapon;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Multisell list manager
 *
 */
public class L2Multisell
{
    private static Logger _log = LoggerFactory.getLogger(L2Multisell.class);
    private List<MultiSellListContainer> _entries = new ArrayList<>();
    private static L2Multisell _instance = new L2Multisell();

    public MultiSellListContainer getList(int id)
    {
        synchronized (_entries)
        {
            for (MultiSellListContainer list : _entries)
            {
                if (list.getListId() == id) return list;
            }
        }

        _log.warn("[L2Multisell] can't find list with id: " + id);
        return null;
    }

    public L2Multisell()
    {
        parseData();
    }

    public void reload()
    {
        parseData();
    }

    public static L2Multisell getInstance()
    {
        return _instance;
    }

    private void parseData()
    {
        _entries.clear();
        parse();
    }

    /**
     * This will generate the multisell list for the items.  There exist various
     * parameters in multisells that affect the way they will appear:
     * 1) inventory only:
     * 		* if true, only show items of the multisell for which the
     * 		  "primary" ingredients are already in the player's inventory.  By "primary"
     * 		  ingredients we mean weapon and armor.
     * 		* if false, show the entire list.
     * 2) maintain enchantment: presumably, only lists with "inventory only" set to true
     * 		should sometimes have this as true.  This makes no sense otherwise...
     * 		* If true, then the product will match the enchantment level of the ingredient.
     * 		  if the player has multiple items that match the ingredient list but the enchantment
     * 		  levels differ, then the entries need to be duplicated to show the products and
     * 		  ingredients for each enchantment level.
     * 		  For example: If the player has a crystal staff +1 and a crystal staff +3 and goes
     * 		  to exchange it at the mammon, the list should have all exchange possibilities for
     * 		  the +1 staff, followed by all possibilities for the +3 staff.
     * 		* If false, then any level ingredient will be considered equal and product will always
     * 		  be at +0
     * 3) apply taxes: Uses the "taxIngredient" entry in order to add a certain amount of adena to the ingredients
     *
     */
    private MultiSellListContainer generateMultiSell(int listId, boolean inventoryOnly, L2PcInstance player, double taxRate)
    {
        MultiSellListContainer listTemplate = L2Multisell.getInstance().getList(listId);
        MultiSellListContainer list = new MultiSellListContainer();
        if (listTemplate == null) return list;
        list = L2Multisell.getInstance().new MultiSellListContainer();
        list.setListId(listId);

        if (inventoryOnly)
        {
        	if (player == null)
        		return list;

        	L2ItemInstance[] items;
        	if (listTemplate.getMaintainEnchantment())
        		items = player.getInventory().getUniqueItemsByEnchantLevel(false,false,false);
        	else
        		items = player.getInventory().getUniqueItems(false,false,false);

        	int enchantLevel;
            for (L2ItemInstance item : items)
            {
            	// only do the matchup on equipable items that are not currently equipped
            	// so for each appropriate item, produce a set of entries for the multisell list.
            	if (!item.isEquipped() && ((item.getItem() instanceof L2Armor) || (item.getItem() instanceof L2Weapon)))
            	{
            		enchantLevel = (listTemplate.getMaintainEnchantment()? item.getEnchantLevel() : 0);
            		// loop through the entries to see which ones we wish to include
	                for (MultiSellEntry ent : listTemplate.getEntries())
	                {
	                	boolean doInclude = false;

	                	// check ingredients of this entry to see if it's an entry we'd like to include.
		                for (MultiSellIngredient ing : ent.getIngredients())
		                {
		                    if (item.getItemId() == ing.getItemId())
		                    {
		                    	doInclude = true;
		                        break;
		                    }
		                }

		                // manipulate the ingredients of the template entry for this particular instance shown
		                // i.e: Assign enchant levels and/or apply taxes as needed.
		                if (doInclude)
		                	list.addEntry(prepareEntry(ent, listTemplate.getApplyTaxes(), listTemplate.getMaintainEnchantment(), enchantLevel, taxRate));
	                }
            	}
            } // end for each inventory item.
        } // end if "inventory-only"
        else  // this is a list-all type
        {
        	// if no taxes are applied, no modifications are needed
    		for (MultiSellEntry ent : listTemplate.getEntries())
    			list.addEntry(prepareEntry(ent, listTemplate.getApplyTaxes(), false, 0, taxRate));
        }

        return list;
    }

	// Regarding taxation, the following is the case:
	// a) The taxes come out purely from the adena TaxIngredient
	// b) If the entry has no adena ingredients other than the taxIngredient, the resulting
    //    amount of adena is appended to the entry
	// c) If the entry already has adena as an entry, the taxIngredient is used in order to increase
    //	  the count for the existing adena ingredient
    private MultiSellEntry prepareEntry(MultiSellEntry templateEntry, boolean applyTaxes, boolean maintainEnchantment, int enchantLevel, double taxRate)
    {
    	MultiSellEntry newEntry = L2Multisell.getInstance().new MultiSellEntry();
    	newEntry.setEntryId(templateEntry.getEntryId()*100000+enchantLevel);
    	int adenaAmount = 0;

        for (MultiSellIngredient ing : templateEntry.getIngredients())
        {
        	// load the ingredient from the template
        	MultiSellIngredient newIngredient = L2Multisell.getInstance().new MultiSellIngredient(ing);

        	// if taxes are to be applied, modify/add the adena count based on the template adena/ancient adena count
        	if ( ing.getItemId() == 57 && ing.isTaxIngredient() )
        	{
        		if (applyTaxes)
        			adenaAmount += (int)Math.round(ing.getItemCount()*taxRate);
        		continue;	// do not adena yet, as non-taxIngredient adena entries might occur next (order not guaranteed)
        	}
        	else if ( ing.getItemId() == 57 )  // && !ing.isTaxIngredient()
        	{
        		adenaAmount += ing.getItemCount();
        		continue;	// do not adena yet, as taxIngredient adena entries might occur next (order not guaranteed)
        	}
        	// if it is an armor/weapon, modify the enchantment level appropriately, if necessary
            // not used for clan reputation and fame 
            else if (maintainEnchantment && newIngredient.getItemId() > 0) 
        	{
            	L2Item tempItem = ItemTable.getInstance().createDummyItem(ing.getItemId()).getItem();
            	if ((tempItem instanceof L2Armor) || (tempItem instanceof L2Weapon))
            		newIngredient.setEnchantmentLevel(enchantLevel);
        	}

        	// finally, add this ingredient to the entry
        	newEntry.addIngredient(newIngredient);
        }
        // now add the adena, if any.
        if (adenaAmount > 0 )
        {
        	newEntry.addIngredient(L2Multisell.getInstance().new MultiSellIngredient(57,adenaAmount,0,false,false));
        }
        // Now modify the enchantment level of products, if necessary
        for (MultiSellIngredient ing : templateEntry.getProducts())
        {
        	// load the ingredient from the template
        	MultiSellIngredient newIngredient = L2Multisell.getInstance().new MultiSellIngredient(ing);

        	if (maintainEnchantment)
            {
            	// if it is an armor/weapon, modify the enchantment level appropriately
            	// (note, if maintain enchantment is "false" this modification will result to a +0)
            	L2Item tempItem = ItemTable.getInstance().createDummyItem(ing.getItemId()).getItem();
            	if ((tempItem instanceof L2Armor) || (tempItem instanceof L2Weapon))
            		newIngredient.setEnchantmentLevel(enchantLevel);
            }
        	newEntry.addProduct(newIngredient);
        }
        return newEntry;
    }

    public void SeparateAndSend(int listId, L2PcInstance player, boolean inventoryOnly, double taxRate)
    {
		MultiSellListContainer list = generateMultiSell(listId, inventoryOnly, player, taxRate);
		MultiSellListContainer temp = new MultiSellListContainer();
		int page = 1;

		temp.setListId(list.getListId());

		for (MultiSellEntry e : list.getEntries())
		{
			if (temp.getEntries().size() == 40)
			{
				player.sendPacket(new MultiSellList(temp, page, 0));
				page++;
				temp = new MultiSellListContainer();
				temp.setListId(list.getListId());
			}
			temp.addEntry(e);
		}
		player.sendPacket(new MultiSellList(temp, page, 1));
    }

    public class MultiSellEntry
    {
        private int _entryId;

        private List<MultiSellIngredient> _products = new ArrayList<>();
        private List<MultiSellIngredient> _ingredients = new ArrayList<>();

        /**
         * @param entryId The entryId to set.
         */
        public void setEntryId(int entryId)
        {
            _entryId = entryId;
        }

        /**
         * @return Returns the entryId.
         */
        public int getEntryId()
        {
            return _entryId;
        }

        /**
         * @param product The product to add.
         */
        public void addProduct(MultiSellIngredient product)
        {
            _products.add(product);
        }

        /**
         * @return Returns the products.
         */
        public List<MultiSellIngredient> getProducts()
        {
            return _products;
        }

        /**
         * @param ingredient The ingredients to set.
         */
        public void addIngredient(MultiSellIngredient ingredient)
        {
            _ingredients.add(ingredient);
        }

        /**
         * @return Returns the ingredients.
         */
        public List<MultiSellIngredient> getIngredients()
        {
            return _ingredients;
        }
    }

    public class MultiSellIngredient
    {
        private int _itemId, _itemCount, _enchantmentLevel;
        private boolean _isTaxIngredient, _mantainIngredient;

        public MultiSellIngredient(int itemId, int itemCount, boolean isTaxIngredient, boolean mantainIngredient)
        {
        	this(itemId, itemCount, 0, isTaxIngredient, mantainIngredient);
        }

        public MultiSellIngredient(int itemId, int itemCount, int enchantmentLevel, boolean isTaxIngredient, boolean mantainIngredient)
        {
            setItemId(itemId);
            setItemCount(itemCount);
            setEnchantmentLevel(enchantmentLevel);
            setIsTaxIngredient(isTaxIngredient);
            setMantainIngredient(mantainIngredient);
        }

        public MultiSellIngredient(MultiSellIngredient e)
        {
        	_itemId = e.getItemId();
        	_itemCount = e.getItemCount();
        	_enchantmentLevel = e.getEnchantmentLevel();
        	_isTaxIngredient = e.isTaxIngredient();
        	_mantainIngredient = e.getMantainIngredient();
        }
        /**
         * @param itemId The itemId to set.
         */
        public void setItemId(int itemId)
        {
            _itemId = itemId;
        }

        /**
         * @return Returns the itemId.
         */
        public int getItemId()
        {
            return _itemId;
        }

        /**
         * @param itemCount The itemCount to set.
         */
        public void setItemCount(int itemCount)
        {
            _itemCount = itemCount;
        }

        /**
         * @return Returns the itemCount.
         */
        public int getItemCount()
        {
            return _itemCount;
        }

        /**
         * @param enchantmentLevel The itemCount to set.
         */
        public void setEnchantmentLevel(int enchantmentLevel)
        {
        	_enchantmentLevel = enchantmentLevel;
        }

        /**
         * @return Returns the itemCount.
         */
        public int getEnchantmentLevel()
        {
            return _enchantmentLevel;
        }

        public void setIsTaxIngredient(boolean isTaxIngredient)
        {
        	_isTaxIngredient = isTaxIngredient;
        }

        public boolean isTaxIngredient()
        {
        	return _isTaxIngredient;
        }

        public void setMantainIngredient(boolean mantainIngredient)
        {
        	_mantainIngredient = mantainIngredient;
        }

        public boolean getMantainIngredient()
        {
        	return _mantainIngredient;
        }
    }

    public class MultiSellListContainer
    {
        private int _listId;
        private boolean _applyTaxes = false;
        private boolean _maintainEnchantment = false;

        List<MultiSellEntry> _entriesC;

        public MultiSellListContainer()
        {
            _entriesC = new ArrayList<>();
        }

        /**
         * @param listId The listId to set.
         */
        public void setListId(int listId)
        {
            _listId = listId;
        }

        public void setApplyTaxes(boolean applyTaxes)
        {
        	_applyTaxes = applyTaxes;
        }

        public void setMaintainEnchantment(boolean maintainEnchantment)
        {
        	_maintainEnchantment = maintainEnchantment;
        }

        /**
         * @return Returns the listId.
         */
        public int getListId()
        {
            return _listId;
        }

        public boolean getApplyTaxes()
        {
            return _applyTaxes;
        }

        public boolean getMaintainEnchantment()
        {
            return _maintainEnchantment;
        }

        public void addEntry(MultiSellEntry e)
        {
            _entriesC.add(e);
        }

        public List<MultiSellEntry> getEntries()
        {
            return _entriesC;
        }
    }

    private void hashFiles(String dirname, List<File> hash) {
    	ServerSettings serverSettings = Configurator.getSettings(ServerSettings.class);
        File dir = new File(serverSettings.getDatapackDirectory(), "data/" + dirname);
        if (!dir.exists())
        {
            _log.info("Dir " + dir.getAbsolutePath() + " not exists");
            return;
        }
        File[] files = dir.listFiles();
        for (File f : files)
        {
            if (f.getName().endsWith(".xml")) hash.add(f);
        }
    }

    private void parse()
    {
        Document doc = null;
        int id = 0;
        List<File> files = new ArrayList<>();
        hashFiles("multisell", files);

        for (File f : files)
        {
            id = Integer.parseInt(f.getName().replaceAll(".xml", ""));
            try
            {

                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                factory.setValidating(false);
                factory.setIgnoringComments(true);
                doc = factory.newDocumentBuilder().parse(f);
            }
            catch (Exception e)
            {
                _log.error( "Error loading file " + f, e);
            }
            try
            {
                MultiSellListContainer list = parseDocument(doc);
                list.setListId(id);
                _entries.add(list);
            }
            catch (Exception e)
            {
                _log.error( "Error in file " + f, e);
            }
        }
    }

    protected MultiSellListContainer parseDocument(Document doc)
    {
        MultiSellListContainer list = new MultiSellListContainer();

        for (Node n = doc.getFirstChild(); n != null; n = n.getNextSibling())
        {
            if ("list".equalsIgnoreCase(n.getNodeName()))
            {
            	Node attribute;
            	attribute = n.getAttributes().getNamedItem("applyTaxes");
            	if(attribute == null)
            		list.setApplyTaxes(false);
            	else
            		list.setApplyTaxes(Boolean.parseBoolean(attribute.getNodeValue()));
            	attribute = n.getAttributes().getNamedItem("maintainEnchantment");
            	if(attribute == null)
            		list.setMaintainEnchantment(false);
            	else
            		list.setMaintainEnchantment(Boolean.parseBoolean(attribute.getNodeValue()));

                for (Node d = n.getFirstChild(); d != null; d = d.getNextSibling())
                {
                    if ("item".equalsIgnoreCase(d.getNodeName()))
                    {
                        MultiSellEntry e = parseEntry(d);
                        list.addEntry(e);
                    }
                }
            }
            else if ("item".equalsIgnoreCase(n.getNodeName()))
            {
                MultiSellEntry e = parseEntry(n);
                list.addEntry(e);
            }
        }

        return list;
    }

    protected MultiSellEntry parseEntry(Node n)
    {
        int entryId = Integer.parseInt(n.getAttributes().getNamedItem("id").getNodeValue());

        Node first = n.getFirstChild();
        MultiSellEntry entry = new MultiSellEntry();

        for (n = first; n != null; n = n.getNextSibling())
        {
            if ("ingredient".equalsIgnoreCase(n.getNodeName()))
            {
            	Node attribute;

                int id = Integer.parseInt(n.getAttributes().getNamedItem("id").getNodeValue());
                int count = Integer.parseInt(n.getAttributes().getNamedItem("count").getNodeValue());
                boolean isTaxIngredient = false, mantainIngredient = false;

                attribute = n.getAttributes().getNamedItem("isTaxIngredient");

                if (attribute != null)
                	isTaxIngredient = Boolean.parseBoolean(attribute.getNodeValue());

                attribute = n.getAttributes().getNamedItem("mantainIngredient");

                if (attribute != null)
                	mantainIngredient = Boolean.parseBoolean(attribute.getNodeValue());

                MultiSellIngredient e = new MultiSellIngredient(id, count, isTaxIngredient, mantainIngredient);
                entry.addIngredient(e);
            }
            else if ("production".equalsIgnoreCase(n.getNodeName()))
            {
                int id = Integer.parseInt(n.getAttributes().getNamedItem("id").getNodeValue());
                int count = Integer.parseInt(n.getAttributes().getNamedItem("count").getNodeValue());

                MultiSellIngredient e = new MultiSellIngredient(id, count, false, false);
                entry.addProduct(e);
            }
        }

        entry.setEntryId(entryId);

        return entry;
    }
}
