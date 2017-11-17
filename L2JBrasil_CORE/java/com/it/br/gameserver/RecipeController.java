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
package com.it.br.gameserver;

import static com.it.br.configuration.Configurator.getSettings;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.LineNumberReader;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.it.br.Config;
import com.it.br.configuration.settings.ServerSettings;
import com.it.br.gameserver.model.Inventory;
import com.it.br.gameserver.model.L2ItemInstance;
import com.it.br.gameserver.model.L2ManufactureItem;
import com.it.br.gameserver.model.L2RecipeInstance;
import com.it.br.gameserver.model.L2RecipeList;
import com.it.br.gameserver.model.L2Skill;
import com.it.br.gameserver.model.actor.instance.L2PcInstance;
import com.it.br.gameserver.network.SystemMessageId;
import com.it.br.gameserver.network.serverpackets.ActionFailed;
import com.it.br.gameserver.network.serverpackets.ItemList;
import com.it.br.gameserver.network.serverpackets.MagicSkillUser;
import com.it.br.gameserver.network.serverpackets.RecipeBookItemList;
import com.it.br.gameserver.network.serverpackets.RecipeItemMakeInfo;
import com.it.br.gameserver.network.serverpackets.RecipeShopItemInfo;
import com.it.br.gameserver.network.serverpackets.SetupGauge;
import com.it.br.gameserver.network.serverpackets.StatusUpdate;
import com.it.br.gameserver.network.serverpackets.SystemMessage;
import com.it.br.gameserver.skills.Stats;
import com.it.br.gameserver.util.Util;
import com.it.br.util.Rnd;

public class RecipeController
{
	protected static final Logger _log = Logger.getLogger(RecipeController.class.getName());

	private static RecipeController _instance;
	private Map<Integer, L2RecipeList> _lists;
	protected static final Map<L2PcInstance, RecipeItemMaker> _activeMakers = Collections.synchronizedMap(new WeakHashMap<L2PcInstance, RecipeItemMaker>());

	public static RecipeController getInstance()
	{
		return _instance == null ? _instance = new RecipeController() : _instance;
	}

	public RecipeController()
	{
		_lists = new HashMap<>();
		String line = null;
		LineNumberReader lnr = null;

		try
		{
			ServerSettings serverSettings = getSettings(ServerSettings.class);
			File recipesData = new File(serverSettings.getDatapackDirectory(), "data/csv/recipes.csv");
			lnr = new LineNumberReader(new BufferedReader(new FileReader(recipesData)));

			while ((line = lnr.readLine()) != null)
			{
				if (line.trim().length() == 0 || line.startsWith("#"))
					continue;

				parseList(line);

			}
			_log.config("RecipeController: Loaded " + _lists.size() + " Recipes.");
		}
		catch (Exception e)
		{
			if (lnr != null)

				_log.log(Level.WARNING, "error while creating recipe controller in linenr: " + lnr.getLineNumber(), e);
			else
				_log.warning("No recipes were found in data folder");
		}
		finally
		{
			try
			{
				lnr.close();
			}
			catch (Exception e)
			{
			}
		}
	}

	public int getRecipesCount()
	{
		return _lists.size();
	}

	public L2RecipeList getRecipeList(int listId)
	{
		return _lists.get(listId);
	}

	public L2RecipeList getRecipeByItemId(int itemId)
	{
		for (int i = 0; i < _lists.size(); i++)
		{
			L2RecipeList find = _lists.get(new Integer(i));
			if (find.getRecipeId() == itemId)
			{
				return find;
			}
		}
		return null;
	}

    public L2RecipeList getRecipeById(int recId)
    {
        for (int i = 0; i < _lists.size(); i++)
        {
            L2RecipeList find = _lists.get(new Integer(i));
            if (find.getId() == recId)
            {
                return find;
            }
        }
        return null;
    }

	public synchronized void requestBookOpen(L2PcInstance player, boolean isDwarvenCraft)
	{
		RecipeItemMaker maker = null;
		if (Config.ALT_GAME_CREATION) maker = _activeMakers.get(player);

		if (maker == null)
		{
			RecipeBookItemList response = new RecipeBookItemList(isDwarvenCraft, player.getMaxMp());
			response.addRecipes(isDwarvenCraft	? player.getDwarvenRecipeBook() : player.getCommonRecipeBook());
			player.sendPacket(response);
			return;
		}

		SystemMessage sm = new SystemMessage(SystemMessageId.CANT_ALTER_RECIPEBOOK_WHILE_CRAFTING);
		player.sendPacket(sm);
		return;
	}

	public synchronized void requestMakeItemAbort(L2PcInstance player)
	{
		_activeMakers.remove(player);  // TODO:  anything else here?
	}

	public synchronized void requestManufactureItem(L2PcInstance manufacturer, int recipeListId,
	                                                L2PcInstance player)
	{
		L2RecipeList recipeList = getValidRecipeList(player, recipeListId);

		if (recipeList == null) return;

		List<L2RecipeList> dwarfRecipes = Arrays.asList(manufacturer.getDwarvenRecipeBook());
		List<L2RecipeList> commonRecipes = Arrays.asList(manufacturer.getCommonRecipeBook());

		if (!dwarfRecipes.contains(recipeList) && !commonRecipes.contains(recipeList))
		{
			Util.handleIllegalPlayerAction(player,"Warning!! Character "+player.getName()+" of account "+player.getAccountName()+" sent a false recipe id.",Config.DEFAULT_PUNISH);
    		return;
		}

		RecipeItemMaker maker;

		if (Config.ALT_GAME_CREATION && (maker = _activeMakers.get(manufacturer)) != null) // check if busy
		{
			player.sendMessage("Manufacturer is busy, please try later.");
			return;
		}

		maker = new RecipeItemMaker(manufacturer, recipeList, player);
		if (maker._isValid)
		{
			if (Config.ALT_GAME_CREATION)
			{
				_activeMakers.put(manufacturer, maker);
				ThreadPoolManager.getInstance().scheduleGeneral(maker, 100);
			}
			else maker.run();
		}
	}

	public synchronized void requestMakeItem(L2PcInstance player, int recipeListId)
	{
		if (player.isInDuel())
		{
			player.sendPacket(new SystemMessage(SystemMessageId.CANT_CRAFT_DURING_COMBAT));
			return;
		}

		L2RecipeList recipeList = getValidRecipeList(player, recipeListId);

		if (recipeList == null)	return;

		List<L2RecipeList> dwarfRecipes = Arrays.asList(player.getDwarvenRecipeBook());
		List<L2RecipeList> commonRecipes = Arrays.asList(player.getCommonRecipeBook());

		if (!dwarfRecipes.contains(recipeList) && !commonRecipes.contains(recipeList))
		{
			Util.handleIllegalPlayerAction(player,"Warning!! Character "+player.getName()+" of account "+player.getAccountName()+" sent a false recipe id.",Config.DEFAULT_PUNISH);
    		return;
		}

		RecipeItemMaker maker;

		// check if already busy (possible in alt mode only)
		if (Config.ALT_GAME_CREATION && ((maker = _activeMakers.get(player)) != null))
		{
			SystemMessage sm = new SystemMessage(SystemMessageId.S1_S2);
			sm.addString("You are busy creating");
			sm.addItemName(recipeList.getItemId());
			player.sendPacket(sm);
			return;
		}

		maker = new RecipeItemMaker(player, recipeList, player);
		if (maker._isValid)
		{
			if (Config.ALT_GAME_CREATION)
			{
				_activeMakers.put(player, maker);
				ThreadPoolManager.getInstance().scheduleGeneral(maker, 100);
			}
			else maker.run();
		}
	}

	//TODO XMLize the recipe list
	private void parseList(String line)
	{
		try
		{
			StringTokenizer st = new StringTokenizer(line, ";");
			List<L2RecipeInstance> recipePartList = new ArrayList<>();

			//we use common/dwarf for easy reading of the recipes.csv file
			String recipeTypeString = st.nextToken();

			// now parse the string into a boolean
			boolean isDwarvenRecipe;

			if (recipeTypeString.equalsIgnoreCase("dwarven")) isDwarvenRecipe = true;
			else if (recipeTypeString.equalsIgnoreCase("common")) isDwarvenRecipe = false;
			else
			{ //prints a helpfull message
				_log.warning("Error parsing recipes.csv, unknown recipe type " + recipeTypeString);
				return;
			}

			String recipeName = st.nextToken();
			int id = Integer.parseInt(st.nextToken());
			int recipeId = Integer.parseInt(st.nextToken());
			int level = Integer.parseInt(st.nextToken());

			//material
			StringTokenizer st2 = new StringTokenizer(st.nextToken(), "[],");
			while (st2.hasMoreTokens())
			{
				StringTokenizer st3 = new StringTokenizer(st2.nextToken(), "()");
				int rpItemId = Integer.parseInt(st3.nextToken());
				int quantity = Integer.parseInt(st3.nextToken());
				L2RecipeInstance rp = new L2RecipeInstance(rpItemId, quantity);
				recipePartList.add(rp);
			}

			int itemId = Integer.parseInt(st.nextToken());
			int count = Integer.parseInt(st.nextToken());

			//npc fee
			/*String notdoneyet = */st.nextToken();

			int mpCost = Integer.parseInt(st.nextToken());
			int successRate = Integer.parseInt(st.nextToken());

			L2RecipeList recipeList = new L2RecipeList(id, level, recipeId, recipeName, successRate,
			                                           mpCost, itemId, count, isDwarvenRecipe);
			for (L2RecipeInstance recipePart : recipePartList)
			{
				recipeList.addRecipe(recipePart);
			}
			_lists.put(new Integer(_lists.size()), recipeList);
		}
		catch (Exception e)
		{
			_log.severe("Exception in RecipeController.parseList() - " + e);
		}
	}

	private class RecipeItemMaker implements Runnable
	{
		protected boolean _isValid;
		protected List<TempItem> _items = null;
		protected final L2RecipeList _recipeList;
		protected final L2PcInstance _player; // "crafter"
		protected final L2PcInstance _target; // "customer"
		protected final L2Skill _skill;
		protected final int _skillId;
		protected final int _skillLevel;
		protected double _creationPasses;
		protected double _manaRequired;
		protected int _price;
		protected int _totalItems;
		protected int _materialsRefPrice;
		protected int _delay;

		public RecipeItemMaker(L2PcInstance pPlayer, L2RecipeList pRecipeList, L2PcInstance pTarget)
		{
			_player = pPlayer;
			_target = pTarget;
			_recipeList = pRecipeList;

			_isValid = false;
			_skillId = _recipeList.isDwarvenRecipe()	? L2Skill.SKILL_CREATE_DWARVEN
			                                      	: L2Skill.SKILL_CREATE_COMMON;
			_skillLevel = _player.getSkillLevel(_skillId);
			_skill = _player.getKnownSkill(_skillId);

			_player.isInCraftMode(true);

			if (_player.isAlikeDead())
			{
				_player.sendMessage("Dead people don't craft.");
				_player.sendPacket(new ActionFailed());
				abort();
				return;
			}

			if (_target.isAlikeDead())
			{
				_target.sendMessage("Dead customers can't use manufacture.");
				_target.sendPacket(new ActionFailed());
				abort();
				return;
			}

			if(_target.isProcessingTransaction())
			{
				_target.sendMessage("You are busy.");
				_target.sendPacket(new ActionFailed());
				abort();
				return;
			}

			if(_player.isProcessingTransaction())
			{
				if(_player!=_target)
				{
					_target.sendMessage("Manufacturer "+_player.getName() + " is busy.");
				}
				_player.sendPacket(new ActionFailed());
				abort();
				return;
			}

			// validate recipe list
			if ((_recipeList == null) || (_recipeList.getRecipes().length == 0))
			{
				_player.sendMessage("No such recipe");
				_player.sendPacket(new ActionFailed());
				abort();
				return;
			}

			_manaRequired = _recipeList.getMpCost();

			// validate skill level
			if (_recipeList.getLevel() > _skillLevel)
			{
				_player.sendMessage("Need skill level " + _recipeList.getLevel());
				_player.sendPacket(new ActionFailed());
				abort();
				return;
			}

			// check that customer can afford to pay for creation services
			if (_player != _target)
			{
				for (L2ManufactureItem temp : _player.getCreateList().getList())
					if (temp.getRecipeId() == _recipeList.getId()) // find recipe for item we want manufactured
					{
						_price = temp.getCost();
						if (_target.getAdena() < _price) // check price
						{
							_target.sendPacket(new SystemMessage(SystemMessageId.YOU_NOT_ENOUGH_ADENA));
							abort();
							return;
						}
						break;
					}
			}

			// make temporary items
			if ((_items = listItems(false)) == null)
				{
					abort();
					return;
				}

			// calculate reference price
			for (TempItem i : _items)
			{
				_materialsRefPrice += i.getReferencePrice() * i.getQuantity();
				_totalItems += i.getQuantity();
			}
			// initial mana check requires MP as written on recipe
			if (_player.getCurrentMp() < _manaRequired)
			{
				_target.sendPacket(new SystemMessage(SystemMessageId.NOT_ENOUGH_MP));
				abort();
				return;
			}

			// determine number of creation passes needed
			// can "equip"  skillLevel items each pass
			_creationPasses = (_totalItems / _skillLevel) + ((_totalItems % _skillLevel)!=0 ? 1 : 0);

			if (Config.ALT_GAME_CREATION && _creationPasses != 0) // update mana required to "per pass"
				_manaRequired /= _creationPasses; // checks to validateMp() will only need portion of mp for one pass

			updateMakeInfo(true);
			updateCurMp();
			updateCurLoad();

			_player.isInCraftMode(false);
			_isValid = true;
		}
	
		public void run()
		{
			if (!Config.IS_CRAFTING_ENABLED)
			{
				_target.sendMessage("Item creation is currently disabled.");
				abort();
				return;
			}

			if (_player == null || _target == null)
			{
				_log.warning("player or target == null (disconnected?), aborting"+_target+_player);
				abort();
				return;
			}

			if (_player.isOnline()==0 || _target.isOnline()==0)
			{
				_log.warning("player or target is not online, aborting "+_target+_player);
				abort();
				return;
			}

			if (Config.ALT_GAME_CREATION && _activeMakers.get(_player) == null)
			{
				if (_target!=_player)
				{
					_target.sendMessage("Manufacture aborted");
					_player.sendMessage("Manufacture aborted");
				}
				else
				{
					_player.sendMessage("Item creation aborted");
				}

				abort();
				return;
			}

			if (Config.ALT_GAME_CREATION && !_items.isEmpty())
			{

				if (!validateMp()) return;				// check mana
				_player.reduceCurrentMp(_manaRequired); 	// use some mp
				updateCurMp();							// update craft window mp bar

				grabSomeItems(); // grab (equip) some more items with a nice msg to player

				// if still not empty, schedule another pass
				if(!_items.isEmpty())
				{
					// divided by RATE_CONSUMABLES_COST to remove craft time increase on higher consumables rates
					_delay = (int) (Config.ALT_GAME_CREATION_SPEED * _player.getMReuseRate(_skill)
							* GameTimeController.TICKS_PER_SECOND / Config.RATE_CONSUMABLE_COST)
							* GameTimeController.MILLIS_IN_TICK;

					// FIXME: please fix this packet to show crafting animation (somebody)
					MagicSkillUser msk = new MagicSkillUser(_player, _skillId, _skillLevel, _delay, 0);
					_player.broadcastPacket(msk);

					_player.sendPacket(new SetupGauge(0, _delay));
					ThreadPoolManager.getInstance().scheduleGeneral(this, 100 + _delay);
				}
				else
				{
					// for alt mode, sleep delay msec before finishing
					_player.sendPacket(new SetupGauge(0, _delay));

					try
					{
						Thread.sleep(_delay);
					}
					catch (InterruptedException e){}
					finally
					{
						finishCrafting();
					}
				}
			}    // for old craft mode just finish
			else finishCrafting();
		}

		private void finishCrafting()
		{
			if(!Config.ALT_GAME_CREATION) _player.reduceCurrentMp(_manaRequired);

			// first take adena for manufacture
			if ((_target != _player) && _price > 0) // customer must pay for services
			{
				// attempt to pay for item
				L2ItemInstance adenatransfer = _target.transferItem("PayManufacture",
										_target.getInventory().getAdenaInstance().getObjectId(),
										_price, _player.getInventory(), _player);

				if(adenatransfer==null)
				{
					_target.sendPacket(new SystemMessage(SystemMessageId.YOU_NOT_ENOUGH_ADENA));
					abort();
					return;
				}
			}

			if ((_items = listItems(true)) == null) // this line actually takes materials from inventory
			{ // handle possible cheaters here
			  // (they click craft then try to get rid of items in order to get free craft)
			}
			else if (Rnd.get(100) < _recipeList.getSuccessRate())
			{
				rewardPlayer(); // and immediately puts created item in its place
				updateMakeInfo(true);
			}
			else
			{
				_player.sendMessage("Item(s) failed to create");
				if (_target != _player)
					_target.sendMessage("Item(s) failed to create");

				updateMakeInfo(false);
			}
			// update load and mana bar of craft window
			updateCurMp();
			updateCurLoad();
			_activeMakers.remove(_player);
			_player.isInCraftMode(false);
			_target.sendPacket(new ItemList(_target, false));
		}
		private void updateMakeInfo(boolean success)
		{
			if (_target == _player) _target.sendPacket(new RecipeItemMakeInfo(_recipeList.getId(), _target,
			                                                               success));
			else _target.sendPacket(new RecipeShopItemInfo(_player.getObjectId(), _recipeList.getId()));
		}

		private void updateCurLoad()
		{
			StatusUpdate su = new StatusUpdate(_target.getObjectId());
			su.addAttribute(StatusUpdate.CUR_LOAD, _target.getCurrentLoad());
			_target.sendPacket(su);
		}

		private void updateCurMp()
		{
			StatusUpdate su = new StatusUpdate(_target.getObjectId());
			su.addAttribute(StatusUpdate.CUR_MP, (int) _target.getCurrentMp());
			_target.sendPacket(su);
		}

		private void grabSomeItems()
		{
			int numItems = _skillLevel;

			while (numItems > 0 && !_items.isEmpty())
			{
				TempItem item = _items.get(0);

				int count = item.getQuantity();
				if (count >= numItems) count = numItems;

				item.setQuantity(item.getQuantity() - count);
				if (item.getQuantity() <= 0) _items.remove(0);
				else _items.set(0, item);

				numItems -= count;

				if (_target == _player)
				{
					SystemMessage sm = new SystemMessage(SystemMessageId.S1_S2_EQUIPPED); // you equipped ...
					sm.addNumber(count);
					sm.addItemName(item.getItemId());
					_player.sendPacket(sm);
				}
				else _target.sendMessage("Manufacturer " + _player.getName() + " used " + count + " "
				                        + item.getItemName());
			}
		}

		private boolean validateMp()
		{
			if (_player.getCurrentMp() < _manaRequired)
			{
				// rest (wait for MP)
				if (Config.ALT_GAME_CREATION)
				{
					_player.sendPacket(new SetupGauge(0, _delay));
					ThreadPoolManager.getInstance().scheduleGeneral(this, 100 + _delay);
				}
				else
					// no rest - report no mana
				{
					_target.sendPacket(new SystemMessage(SystemMessageId.NOT_ENOUGH_MP));
					abort();
				}
				return false;
			}
			return true;
		}

		private List<TempItem> listItems(boolean remove)
		{
			L2RecipeInstance[] recipes = _recipeList.getRecipes();
			Inventory inv = _target.getInventory();
			List<TempItem> materials = new ArrayList<>();

			for (L2RecipeInstance recipe : recipes)
			{
				int quantity = _recipeList.isConsumable() ? (int) (recipe.getQuantity() * Config.RATE_CONSUMABLE_COST)
				                                         : (int) recipe.getQuantity();

				if (quantity > 0)
				{
					L2ItemInstance item = inv.getItemByItemId(recipe.getItemId());

					// check materials
					if (item==null || item.getCount() < quantity)
					{
						_target.sendMessage("You dont have the right elements for making this item"
							+ ((_recipeList.isConsumable() && Config.RATE_CONSUMABLE_COST != 1)	? ".\nDue to server rates you need "
							+ Config.RATE_CONSUMABLE_COST + "x more material than listed in recipe" : ""));
						abort();
						return null;
					}

					// make new temporary object, just for counting puroses

					TempItem temp = new TempItem(item, quantity);
					materials.add(temp);
				}
			}

			if (remove)
			{
				for(TempItem tmp : materials)
				{
					inv.destroyItemByItemId("Manufacture", tmp.getItemId(), tmp.getQuantity(), _target, _player);
				}
			}
			return materials;
		}

		private void abort()
		{
			updateMakeInfo(false);
			_player.isInCraftMode(false);
			_activeMakers.remove(_player);
		}

		/**
		 * FIXME: This class should be in some other file, but I don't know where
		 *
		 * Class explanation:
		 * For item counting or checking purposes. When you don't want to modify inventory
		 * class contains itemId, quantity, ownerId, referencePrice, but not objectId
		 */
		private class TempItem
		{ // no object id stored, this will be only "list" of items with it's owner
			private int _itemId;
			private int _quantity;
			private int _ownerId;
			private int _referencePrice;
			private String _itemName;

			/**
			 * @param item
			 * @param quantity of that item
			 */
			public TempItem(L2ItemInstance item, int quantity)
			{
				super();
				_itemId = item.getItemId();
				_quantity = quantity;
				_ownerId = item.getOwnerId();
				_itemName = item.getItem().getName();
				_referencePrice = item.getReferencePrice();
			}

			/**
			 * @return Returns the quantity.
			 */
			public int getQuantity()
			{
				return _quantity;
			}

			/**
			 * @param quantity The quantity to set.
			 */
			public void setQuantity(int quantity)
			{
				_quantity = quantity;
			}

			public int getReferencePrice()
			{
				return _referencePrice;
			}

			/**
			 * @return Returns the itemId.
			 */
			public int getItemId()
			{
				return _itemId;
			}

			/**
			 * @return Returns the ownerId.
			 */
			@SuppressWarnings("unused")
			public int getOwnerId()
			{
				return _ownerId;
			}

			/**
			 * @return Returns the itemName.
			 */
			public String getItemName()
			{
				return _itemName;
			}
		}

		private void rewardPlayer()
		{
			int itemId = _recipeList.getItemId();
			int itemCount = _recipeList.getCount();

			L2ItemInstance createdItem = _target.getInventory().addItem("Manufacture", itemId, itemCount,
			                                                           _target, _player);

			// inform customer of earned item
            SystemMessage sm = null;
            if (itemCount > 1)
            {
    			sm = new SystemMessage(SystemMessageId.EARNED_S2_S1_S);
    			sm.addItemName(itemId);
                sm.addNumber(itemCount);
    			_target.sendPacket(sm);
            } else
            {
                sm = new SystemMessage(SystemMessageId.EARNED_ITEM);
                sm.addItemName(itemId);
                _target.sendPacket(sm);
            }

			if (_target != _player)
			{
				// inform manufacturer of earned profit
				sm = new SystemMessage(SystemMessageId.EARNED_ADENA);
				sm.addNumber(_price);
				_player.sendPacket(sm);
			}

			if (Config.ALT_GAME_CREATION)
			{
				int recipeLevel = _recipeList.getLevel();
				int exp = createdItem.getReferencePrice() * itemCount;
				// one variation

				// exp -= materialsRefPrice;   // mat. ref. price is not accurate so other method is better

				if (exp < 0) exp = 0;

				// another variation
				exp /= recipeLevel;
				for (int i = _skillLevel; i > recipeLevel; i--)
					exp /= 4;

				int sp = exp / 10;

				// Added multiplication of Creation speed with XP/SP gain
				// slower crafting -> more XP,  faster crafting -> less XP
				// you can use ALT_GAME_CREATION_XP_RATE/SP to
				// modify XP/SP gained (default = 1)

				_player.addExpAndSp((int) _player.calcStat(Stats.EXPSP_RATE, exp * Config.ALT_GAME_CREATION_XP_RATE * Config.ALT_GAME_CREATION_SPEED, null, null)
				,(int) _player.calcStat(Stats.EXPSP_RATE, sp * Config.ALT_GAME_CREATION_SP_RATE * Config.ALT_GAME_CREATION_SPEED, null, null));
			}
			updateMakeInfo(true); // success
		}
	}

	private L2RecipeList getValidRecipeList(L2PcInstance player, int id)
	{
		L2RecipeList recipeList = getRecipeList(id - 1);

		if ((recipeList == null) || (recipeList.getRecipes().length == 0))
		{
			player.sendMessage("No recipe for: " + id);
			player.isInCraftMode(false);
			return null;
		}
		return recipeList;
	}
}