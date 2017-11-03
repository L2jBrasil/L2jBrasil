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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.it.br.Config;
import com.it.br.L2DatabaseFactory;
import com.it.br.gameserver.datatables.sql.ItemTable;
import com.it.br.gameserver.datatables.sql.SkillTable;
import com.it.br.gameserver.datatables.xml.ArmorSetsTable;
import com.it.br.gameserver.model.L2ItemInstance.ItemLocation;
import com.it.br.gameserver.model.actor.instance.L2PcInstance;
import com.it.br.gameserver.templates.L2Armor;
import com.it.br.gameserver.templates.L2EtcItem;
import com.it.br.gameserver.templates.L2EtcItemType;
import com.it.br.gameserver.templates.L2Item;
import com.it.br.gameserver.templates.L2Weapon;
import com.it.br.gameserver.templates.L2WeaponType;

/**
 * This class manages inventory
 *
 * @version $Revision: 1.13.2.9.2.12 $ $Date: 2005/03/29 23:15:15 $
 * rewritten 23.2.2006 by Advi
 */
public abstract class Inventory extends ItemContainer
{
	//protected static final Logger _log = Logger.getLogger(Inventory.class.getName());

    public interface PaperdollListener {
    	public void notifyEquiped(int slot, L2ItemInstance inst);
    	public void notifyUnequiped(int slot, L2ItemInstance inst);
    }

	public static final int PAPERDOLL_UNDER = 0;
	public static final int PAPERDOLL_LEAR = 1;
	public static final int PAPERDOLL_REAR = 2;
	public static final int PAPERDOLL_NECK = 3;
	public static final int PAPERDOLL_LFINGER = 4;
	public static final int PAPERDOLL_RFINGER = 5;
	public static final int PAPERDOLL_HEAD = 6;
	public static final int PAPERDOLL_RHAND = 7;
	public static final int PAPERDOLL_LHAND = 8;
	public static final int PAPERDOLL_GLOVES = 9;
	public static final int PAPERDOLL_CHEST = 10;
	public static final int PAPERDOLL_LEGS = 11;
	public static final int PAPERDOLL_FEET = 12;
	public static final int PAPERDOLL_BACK = 13;
	public static final int PAPERDOLL_LRHAND = 14;
	public static final int PAPERDOLL_FACE = 15;
	public static final int PAPERDOLL_HAIR = 16;
	public static final int PAPERDOLL_DHAIR = 17;

    //Speed percentage mods
    public static final double MAX_ARMOR_WEIGHT = 12000;

	private final L2ItemInstance[] _paperdoll;
	private final List<PaperdollListener> _paperdollListeners;

	// protected to be accessed from child classes only
	protected int _totalWeight;

	// used to quickly check for using of items of special type
	private int _wearedMask;

	final class FormalWearListener implements PaperdollListener
	{
	
		public void notifyUnequiped(int slot, L2ItemInstance item)
	    {
	        if (!(getOwner() != null
	                && getOwner() instanceof L2PcInstance))
	            return;

	        L2PcInstance owner = (L2PcInstance)getOwner();

	        if (item.getItemId() == 6408)
	            owner.setIsWearingFormalWear(false);
	    }
	
		public void notifyEquiped(int slot, L2ItemInstance item)
	    {
	        if (!(getOwner() != null
	                && getOwner() instanceof L2PcInstance))
	            return;

	        L2PcInstance owner = (L2PcInstance)getOwner();

	        // If player equip Formal Wear unequip weapons and abort cast/attack
	        if (item.getItemId() == 6408)
	        {
	            owner.setIsWearingFormalWear(true);
	        }
	        else
	        {
	            if (!owner.isWearingFormalWear())
	                return;
	        }
	    }
	}

	/**
	 * Recorder of alterations in inventory
	 */
    public static final class ChangeRecorder implements PaperdollListener
    {
    	private final Inventory _inventory;
    	private final List<L2ItemInstance> _changed;

    	/**
    	 * Constructor of the ChangeRecorder
    	 * @param inventory
    	 */
    	ChangeRecorder(Inventory inventory) {
    		_inventory = inventory;
			_changed = new ArrayList<>();
			_inventory.addPaperdollListener(this);
    	}

    	/**
    	 * Add alteration in inventory when item equiped
    	 */
    
		public void notifyEquiped(int slot, L2ItemInstance item) {
    		if (!_changed.contains(item))
    			_changed.add(item);
    	}

    	/**
    	 * Add alteration in inventory when item unequiped
    	 */
    
		public void notifyUnequiped(int slot, L2ItemInstance item) {
    		if (!_changed.contains(item))
    			_changed.add(item);
    	}

    	/**
    	 * Returns alterations in inventory
    	 * @return L2ItemInstance[] : array of alterated items
    	 */
    	public L2ItemInstance[] getChangedItems() {
    		return _changed.toArray(new L2ItemInstance[_changed.size()]);
    	}
    }

    final class BowListener implements PaperdollListener
    {
    
		public void notifyUnequiped(int slot, L2ItemInstance item)
    	{
    		if (slot != PAPERDOLL_LRHAND)
    			return;
    		if (Config.ASSERT) assert null == getPaperdollItem(PAPERDOLL_LRHAND);
    		if (item.getItemType() == L2WeaponType.BOW)
    		{
    			L2ItemInstance arrow = getPaperdollItem(PAPERDOLL_LHAND);
    			if (arrow != null)
    				setPaperdollItem(PAPERDOLL_LHAND, null);
    		}
    	}
    
		public void notifyEquiped(int slot, L2ItemInstance item)
    	{
    		if (slot != PAPERDOLL_LRHAND)
    			return;
    		if (Config.ASSERT) assert item == getPaperdollItem(PAPERDOLL_LRHAND);
    		if (item.getItemType() == L2WeaponType.BOW)
    		{
    			L2ItemInstance arrow = findArrowForBow(item.getItem());
    			if (arrow != null)
    				setPaperdollItem(PAPERDOLL_LHAND, arrow);
    		}
    	}
    }

    final class StatsListener implements PaperdollListener
    {
    
		public void notifyUnequiped(int slot, L2ItemInstance item)
    	{
    		if (slot == PAPERDOLL_LRHAND)
    			return;
    		getOwner().removeStatsOwner(item);
    	}
    
		public void notifyEquiped(int slot, L2ItemInstance item)
    	{
    		if (slot == PAPERDOLL_LRHAND)
    			return;
    		getOwner().addStatFuncs(item.getStatFuncs(getOwner()));
    	}
    }

    final class ItemSkillsListener implements PaperdollListener
    {
    
		public void notifyUnequiped(int slot, L2ItemInstance item)
    	{
    		L2PcInstance player;

	        if(getOwner() instanceof L2PcInstance)
	        {
	            player = (L2PcInstance)getOwner();
	        }
	        else
	        	return;

	        L2Skill passiveSkill = null;
	        L2Skill enchant4Skill = null;

			L2Item it = item.getItem();

			if(it instanceof L2Weapon)
			{
				// Remove augmentation bonuses on unequip
				if (item.isAugmented() && getOwner() instanceof L2PcInstance)
				    item.getAugmentation().removeBonus((L2PcInstance)getOwner());
			    
			    passiveSkill = ((L2Weapon)it).getSkill();
				enchant4Skill= ((L2Weapon)it).getEnchant4Skill();
	               if(it.getItemId()==9140 || it.getItemId()==9141) 
             { 
                 player.removeSkill(SkillTable.getInstance().getInfo(3260, 1), false); 
                 player.removeSkill(SkillTable.getInstance().getInfo(3261, 1), false); 
                 player.removeSkill(SkillTable.getInstance().getInfo(3262, 1), false); 
             } 
			}
			else if(it instanceof L2Armor)
				passiveSkill = ((L2Armor)it).getSkill();

			if(passiveSkill != null)
			{
				player.removeSkill(passiveSkill, false);
				player.sendSkillList();
			}
			if(enchant4Skill != null)
			{
				player.removeSkill(enchant4Skill, false);
				player.sendSkillList(); 
			}
    	}
    
		public void notifyEquiped(int slot, L2ItemInstance item)
    	{
			L2PcInstance player;
			
	        if(getOwner() instanceof L2PcInstance)
	        {
	            player = (L2PcInstance)getOwner();
	        }
	        else 
	        	return;
	        
			L2Skill passiveSkill = null;
			L2Skill enchant4Skill = null;
			
			L2Item it = item.getItem();
			
			if(it instanceof L2Weapon)
			{
				// Apply augmentation bonuses on equip
                if (item.isAugmented() && getOwner() instanceof L2PcInstance)
                    item.getAugmentation().applyBonus((L2PcInstance)getOwner());

			    passiveSkill = ((L2Weapon)it).getSkill();
				
				if(item.getEnchantLevel() >= 4)
					enchant4Skill= ((L2Weapon)it).getEnchant4Skill();
                
             if(it.getItemId()==9140 || it.getItemId()==9141) 
              { 
                  player.addSkill(SkillTable.getInstance().getInfo(3260, 1), false); 
                  player.addSkill(SkillTable.getInstance().getInfo(3261, 1), false); 
                  player.addSkill(SkillTable.getInstance().getInfo(3262, 1), false); 
              }
			}
			else if(it instanceof L2Armor)
				passiveSkill = ((L2Armor)it).getSkill();

			if(passiveSkill != null)
			{
	    		player.addSkill(passiveSkill, false);
	    		player.sendSkillList(); 
			}
			if(enchant4Skill != null)
			{
				player.addSkill(enchant4Skill, false);
				player.sendSkillList(); 
			}

    	}
    }
    final class ArmorSetListener implements PaperdollListener
    {
    
		public void notifyEquiped(int slot, L2ItemInstance item)
    	{
    		if(!(getOwner() instanceof L2PcInstance))
    			return;
    		
    		L2PcInstance player = (L2PcInstance)getOwner();
    		
    		 // checks if player worns chest item
    		L2ItemInstance chestItem = getPaperdollItem(PAPERDOLL_CHEST); 
    		if(chestItem == null)
    			return;
    		
    		 // checks if there is armorset for chest item that player worns
    		L2ArmorSet armorSet = ArmorSetsTable.getInstance().getSet(chestItem.getItemId());
    		if(armorSet == null)
    			return;
    		
    		 // checks if equiped item is part of set
    		if(armorSet.containItem(slot, item.getItemId()))
    		{
    			if(armorSet.containAll(player))
    			{
    	    		L2Skill skill = SkillTable.getInstance().getInfo(armorSet.getSkillId(),armorSet.getSkillLvl());
    	    		if(skill != null)
    	    		{
    	    			player.addSkill(skill, false);
    	    			player.sendSkillList(); 
    	    		}
    	    		else
    	    			_log.warning("Inventory.ArmorSetListener: Incorrect skill: "+armorSet.getSkillId()+".");
    	    		
    	    		if(armorSet.containShield(player)) // has shield from set
    	    		{
        	    		L2Skill skills = SkillTable.getInstance().getInfo(armorSet.getShieldSkillId(),1);
        	    		if(skills != null)
        	    		{
        	    			player.addSkill(skills, false);
        	    			player.sendSkillList(); 
        	    		}
        	    		else
        	    			_log.warning("Inventory.ArmorSetListener: Incorrect skill: "+armorSet.getShieldSkillId()+".");
    	    		}
    	    		if(armorSet.isEnchanted6(player)) // has all parts of set enchanted to 6 or more
    	    		{
    	    			int skillId = armorSet.getEnchant6skillId();
    	    			if(skillId > 0)
    	    			{
	        	    		L2Skill skille = SkillTable.getInstance().getInfo(skillId,1);
	        	    		if(skille != null)
	        	    		{
	        	    			player.addSkill(skille, false);
	        	    			player.sendSkillList(); 
	        	    		}
	        	    		else
	        	    			_log.warning("Inventory.ArmorSetListener: Incorrect skill: "+armorSet.getEnchant6skillId()+".");
    	    			}
    	    		}
    			}
    		}
    		else if (armorSet.containShield(item.getItemId()))
    		{
    			if(armorSet.containAll(player))
    			{
    				L2Skill skills = SkillTable.getInstance().getInfo(armorSet.getShieldSkillId(),1);
    	    		if(skills != null)
    	    		{
    	    			player.addSkill(skills, false);
    	    			player.sendSkillList(); 
    	    		}
    	    		else
    	    			_log.warning("Inventory.ArmorSetListener: Incorrect skill: "+armorSet.getShieldSkillId()+".");
    			}
    		}
    	}
    
		public void notifyUnequiped(int slot, L2ItemInstance item)
    	{
    		if(!(getOwner() instanceof L2PcInstance))
    			return;
    		
    		L2PcInstance player = (L2PcInstance)getOwner();
    		
    		boolean remove = false;
    		int removeSkillId1 = 0; // set skill
            int removeSkillLvl1 = 1; // set skillLvl 
    		int removeSkillId2 = 0; // shield skill
    		int removeSkillId3 = 0; // enchant +6 skill
    		
    		if(slot == PAPERDOLL_CHEST)
    		{
    			L2ArmorSet armorSet = ArmorSetsTable.getInstance().getSet(item.getItemId());
    			if(armorSet == null)
    				return;
    			
    			remove = true;
    			removeSkillId1 = armorSet.getSkillId();
    	        removeSkillLvl1 = armorSet.getSkillLvl(); 
    			removeSkillId2 = armorSet.getShieldSkillId();
    			removeSkillId3 = armorSet.getEnchant6skillId();
    		}
    		else
    		{
    			L2ItemInstance chestItem = getPaperdollItem(PAPERDOLL_CHEST); 
    			if(chestItem == null)
    				return;
    			
    			L2ArmorSet armorSet = ArmorSetsTable.getInstance().getSet(chestItem.getItemId());
    			if(armorSet == null)
    				return;
    			
    			if(armorSet.containItem(slot, item.getItemId())) // removed part of set
    			{
    				remove = true;
    				removeSkillId1 = armorSet.getSkillId();
    	        	removeSkillLvl1 = armorSet.getSkillLvl(); 
    				removeSkillId2 = armorSet.getShieldSkillId();
    				removeSkillId3 = armorSet.getEnchant6skillId();
    			}
    			else if(armorSet.containShield(item.getItemId())) // removed shield
    			{
    				remove = true;
    				removeSkillId2 = armorSet.getShieldSkillId();
    			}
    		}
    		
    		if(remove)
    		{
    			if(removeSkillId1 != 0)
    			{
        			L2Skill skill = SkillTable.getInstance().getInfo(removeSkillId1,removeSkillLvl1);
        			if(skill != null)
        				player.removeSkill(skill);
        			else
        				_log.warning("Inventory.ArmorSetListener: Incorrect skill: "+removeSkillId1+".");
    			}
    			if(removeSkillId2 != 0)
    			{
        			L2Skill skill = SkillTable.getInstance().getInfo(removeSkillId2,1);
        			if(skill != null)
        				player.removeSkill(skill);
        			else
        				_log.warning("Inventory.ArmorSetListener: Incorrect skill: "+removeSkillId2+".");
    			}
    			if(removeSkillId3 != 0)
    			{
        			L2Skill skill = SkillTable.getInstance().getInfo(removeSkillId3,1);
        			if(skill != null)
        				player.removeSkill(skill);
        			else
        				_log.warning("Inventory.ArmorSetListener: Incorrect skill: "+removeSkillId3+".");
    			}
    			player.sendSkillList();
    		}
    	}
    }
    
	/*
    final class FormalWearListener implements PaperdollListener
    {
    	public void notifyUnequiped(int slot, L2ItemInstance item)
    	{
			if (!(getOwner() != null
					&& getOwner() instanceof L2PcInstance))
				return;
			
			L2PcInstance owner = (L2PcInstance)getOwner(); 

			if (item.getItemId() == 6408)
				owner.setIsWearingFormalWear(false);
    	}
    	public void notifyEquiped(int slot, L2ItemInstance item)
    	{
			if (!(getOwner() != null
					&& getOwner() instanceof L2PcInstance))
				return;
			
			L2PcInstance owner = (L2PcInstance)getOwner(); 

			// If player equip Formal Wear unequip weapons and abort cast/attack 
			if (item.getItemId() == 6408) 
			{
				owner.setIsWearingFormalWear(true);
				if (owner.isCastingNow())
					owner.abortCast();
				if (owner.isAttackingNow())
					owner.abortAttack();
				setPaperdollItem(PAPERDOLL_LHAND, null);
				setPaperdollItem(PAPERDOLL_RHAND, null);
				setPaperdollItem(PAPERDOLL_LRHAND, null);
			} 
			else 
			{
				if (!owner.isWearingFormalWear())
	            	return;

				// Don't let weapons be equipped if player is wearing Formal Wear 
				if (slot == PAPERDOLL_LHAND 
					|| slot == PAPERDOLL_RHAND
					|| slot == PAPERDOLL_LRHAND)
				{
					setPaperdollItem(slot, null);
				}
			}
    	}
    }
	*/
    /**
     * Constructor of the inventory
     */
	protected Inventory()
	{
		_paperdoll = new L2ItemInstance[0x12];
		_paperdollListeners = new ArrayList<>();
		addPaperdollListener(new ArmorSetListener());
		addPaperdollListener(new BowListener());
		addPaperdollListener(new ItemSkillsListener());
		addPaperdollListener(new StatsListener());
		//addPaperdollListener(new FormalWearListener());
	}
	
	protected abstract ItemLocation getEquipLocation();

	/**
	 * Returns the instance of new ChangeRecorder 
	 * @return ChangeRecorder
	 */
	public ChangeRecorder newRecorder(){return new ChangeRecorder(this);}
	
	/**
	 * Drop item from inventory and updates database
	 * @param process : String Identifier of process triggering this action
     * @param item : L2ItemInstance to be dropped
	 * @param actor : L2PcInstance Player requesting the item drop
	 * @param reference : L2Object Object referencing current action like NPC selling item or previous item in transformation
     * @return L2ItemInstance corresponding to the destroyed item or the updated item in inventory
	 */
	public L2ItemInstance dropItem(String process, L2ItemInstance item, L2PcInstance actor, L2Object reference)
	{
		synchronized (item)
		{
			if (!_items.contains(item))
			{
				return null;
			}

			removeItem(item);
			item.setOwnerId(process, 0, actor, reference);
			item.setLocation(ItemLocation.VOID);
			item.setLastChange(L2ItemInstance.REMOVED);

			item.updateDatabase();
			refreshWeight();
		}
		return item;
	}

	/**
	 * Drop item from inventory by using its <B>objectID</B> and updates database
	 * @param process : String Identifier of process triggering this action
     * @param objectId : int Item Instance identifier of the item to be dropped
     * @param count : int Quantity of items to be dropped
	 * @param actor : L2PcInstance Player requesting the item drop
	 * @param reference : L2Object Object referencing current action like NPC selling item or previous item in transformation
     * @return L2ItemInstance corresponding to the destroyed item or the updated item in inventory
	 */
	public L2ItemInstance dropItem(String process, int objectId, int count, L2PcInstance actor, L2Object reference)
	{
		L2ItemInstance item = getItemByObjectId(objectId);
		if (item == null) return null;

		// Adjust item quantity and create new instance to drop
		if (item.getCount() > count)
		{
			item.changeCount(process, -count, actor, reference);
			item.setLastChange(L2ItemInstance.MODIFIED);
			item.updateDatabase();
			
			item = ItemTable.getInstance().createItem(process, item.getItemId(), count, actor, reference);

			item.updateDatabase();
			refreshWeight();
			return item;
		}
		// Directly drop entire item
		else return dropItem(process, item, actor, reference);
	}

    /**
     * Adds item to inventory for further adjustments and Equip it if necessary (itemlocation defined)<BR><BR>
     * 
     * @param item : L2ItemInstance to be added from inventory
     */

	@Override
	protected void addItem(L2ItemInstance item)
    {
    	super.addItem(item);
    	if (item.isEquipped())
            equipItem(item);
    }

    /**
     * Removes item from inventory for further adjustments.
     * @param item : L2ItemInstance to be removed from inventory
     */

	@Override
	protected void removeItem(L2ItemInstance item)
    {
    	// Unequip item if equiped
//    	if (item.isEquipped()) unEquipItemInSlotAndRecord(item.getEquipSlot());
        for (int i = 0; i < _paperdoll.length; i++)
            if (_paperdoll[i] == item) unEquipItemInSlot(i);

        super.removeItem(item);
    }

	/**
	 * Returns the item in the paperdoll slot
	 * @return L2ItemInstance
	 */
	public L2ItemInstance getPaperdollItem(int slot)
	{
		return _paperdoll[slot];
	}
	
	/**
	 * Returns the item in the paperdoll L2Item slot
	 * @param slot identifier
	 * @return L2ItemInstance
	 */
	public L2ItemInstance getPaperdollItemByL2ItemId(int slot)
	{
		switch (slot) {
			case 0x01:
				return _paperdoll[0];
			case 0x04:
				return _paperdoll[1];
			case 0x02:
				return _paperdoll[2];
			case 0x08:
				return _paperdoll[3];
			case 0x20:
				return _paperdoll[4];
			case 0x10:
				return _paperdoll[5];
			case 0x40:
				return _paperdoll[6];
			case 0x80:
				return _paperdoll[7];
			case 0x0100:
				return _paperdoll[8];
			case 0x0200:
				return _paperdoll[9];
			case 0x0400:
				return _paperdoll[10];
			case 0x0800:
				return _paperdoll[11];
			case 0x1000:
				return _paperdoll[12];
			case 0x2000:
				return _paperdoll[13];
			case 0x4000:
				return _paperdoll[14];
			case 0x040000:
				return _paperdoll[15];
			case 0x010000:
				return _paperdoll[16];
			case 0x080000:
				return _paperdoll[17];			
		}
		return null;
	}

	/**
	 * Returns the ID of the item in the paperdol slot
	 * @param slot : int designating the slot
	 * @return int designating the ID of the item
	 */
	public int getPaperdollItemId(int slot)
	{
		L2ItemInstance item = _paperdoll[slot]; 
		if (item != null)
			return item.getItemId();
		else if (slot == PAPERDOLL_HAIR)
		{
			item = _paperdoll[PAPERDOLL_DHAIR]; 
			if (item != null)
				return item.getItemId();
		}
		return 0;
	}
	
	public int getPaperdollAugmentationId(int slot)
	{
		L2ItemInstance item = _paperdoll[slot];
		if (item != null)
		{
			if (item.getAugmentation() != null)
			{
				return item.getAugmentation().getAugmentationId();
			}
			else
			{
				return 0;
			}
		}
		return 0;
	}

	/**
	 * Returns the objectID associated to the item in the paperdoll slot
	 * @param slot : int pointing out the slot
	 * @return int designating the objectID
	 */
	public int getPaperdollObjectId(int slot)
	{
		L2ItemInstance item = _paperdoll[slot]; 
		if (item != null)
			return item.getObjectId();
		else if (slot == PAPERDOLL_HAIR)
		{
			item = _paperdoll[PAPERDOLL_DHAIR]; 
			if (item != null)
				return item.getObjectId();
		}
		return 0;
	}

	/** 
	 * Adds new inventory's paperdoll listener
	 * @param listener pointing out the listener
	 */
	public synchronized void addPaperdollListener(PaperdollListener listener)
	{
		if (Config.ASSERT) assert !_paperdollListeners.contains(listener);
		_paperdollListeners.add(listener);
	}
	
	/** 
	 * Removes a paperdoll listener
	 * @param listener pointing out the listener to be deleted
	 */
	public synchronized void removePaperdollListener(PaperdollListener listener)
	{
		_paperdollListeners.remove(listener);
	}
	
	/**
	 * Equips an item in the given slot of the paperdoll.
	 * <U><I>Remark :</I></U> The item <B>HAS TO BE</B> already in the inventory
	 * @param slot : int pointing out the slot of the paperdoll
	 * @param item : L2ItemInstance pointing out the item to add in slot
	 * @return L2ItemInstance designating the item placed in the slot before
	 */
	public L2ItemInstance setPaperdollItem(int slot, L2ItemInstance item)
	{
		L2ItemInstance old = _paperdoll[slot]; 
		if (old != item)
		{
			if (old != null)
			{
				_paperdoll[slot] = null;
				// Put old item from paperdoll slot to base location
				old.setLocation(getBaseLocation());
				old.setLastChange(L2ItemInstance.MODIFIED);
				// Get the mask for paperdoll
				int mask = 0;
				for (int i=0; i < PAPERDOLL_LRHAND; i++)
				{
					L2ItemInstance pi = _paperdoll[i]; 
					if (pi != null)
						mask |= pi.getItem().getItemMask(); 
				}
				_wearedMask = mask;
				// Notify all paperdoll listener in order to unequip old item in slot
				for (PaperdollListener listener : _paperdollListeners)
                {
                    if (listener == null) continue;
                    listener.notifyUnequiped(slot, old);
                }
				old.updateDatabase();
			}
			// Add new item in slot of paperdoll
			if (item != null)
			{
				_paperdoll[slot] = item;
				item.setLocation(getEquipLocation(), slot);
				item.setLastChange(L2ItemInstance.MODIFIED);
				_wearedMask |= item.getItem().getItemMask();
				for (PaperdollListener listener : _paperdollListeners)
					listener.notifyEquiped(slot, item);
				item.updateDatabase();
			}
		}
		return old;
	}
	
	/**
	 * Return the mask of weared item
	 * @return int
	 */
	public int getWearedMask()
	{
		return _wearedMask;
	}
	
	public int getSlotFromItem(L2ItemInstance item)
	{
		int slot = -1;
		int location = item.getEquipSlot();
		
		switch(location)
		{
			case PAPERDOLL_UNDER:		slot = L2Item.SLOT_UNDERWEAR;	break;
			case PAPERDOLL_LEAR:		slot = L2Item.SLOT_L_EAR;		break;
			case PAPERDOLL_REAR:		slot = L2Item.SLOT_R_EAR;		break;
			case PAPERDOLL_NECK:		slot = L2Item.SLOT_NECK;		break;
			case PAPERDOLL_RFINGER:		slot = L2Item.SLOT_R_FINGER;	break;
			case PAPERDOLL_LFINGER:		slot = L2Item.SLOT_L_FINGER;	break;
			case PAPERDOLL_HAIR:		slot = L2Item.SLOT_HAIR;		break;
			case PAPERDOLL_FACE:		slot = L2Item.SLOT_FACE;		break;
			case PAPERDOLL_DHAIR:		slot = L2Item.SLOT_DHAIR;		break;
			case PAPERDOLL_HEAD:		slot = L2Item.SLOT_HEAD;		break;
			case PAPERDOLL_RHAND:		slot = L2Item.SLOT_R_HAND; 		break;
			case PAPERDOLL_LHAND:		slot = L2Item.SLOT_L_HAND; 		break;
			case PAPERDOLL_GLOVES:		slot = L2Item.SLOT_GLOVES; 		break;
			case PAPERDOLL_CHEST:		slot = item.getItem().getBodyPart(); break;// fall through
			case PAPERDOLL_LEGS:		slot = L2Item.SLOT_LEGS;		break;
			case PAPERDOLL_BACK:		slot = L2Item.SLOT_BACK;		break;
			case PAPERDOLL_FEET:		slot = L2Item.SLOT_FEET;		break;
			case PAPERDOLL_LRHAND:		slot = L2Item.SLOT_LR_HAND;		break;
		}
		
		return slot;
	}

	/**
	 * Unequips item in body slot and returns alterations.
	 * @param slot : int designating the slot of the paperdoll
	 * @return L2ItemInstance[] : list of changes
	 */
    public synchronized L2ItemInstance[] unEquipItemInBodySlotAndRecord(int slot)
    {
		Inventory.ChangeRecorder recorder = newRecorder(); 
		try
		{
			unEquipItemInBodySlot(slot); 
			if (getOwner() instanceof L2PcInstance)
				((L2PcInstance)getOwner()).refreshExpertisePenalty();
		} finally {
			removePaperdollListener(recorder);
		}
		return recorder.getChangedItems();
    }
    
    /**
     * Sets item in slot of the paperdoll to null value
     * @param pdollSlot : int designating the slot
     * @return L2ItemInstance designating the item in slot before change
     */
	public synchronized L2ItemInstance unEquipItemInSlot(int pdollSlot) {
		return setPaperdollItem(pdollSlot, null);
	}
	
	/**
	 * Unepquips item in slot and returns alterations
	 * @param slot : int designating the slot
	 * @return L2ItemInstance[] : list of items altered
	 */
    public synchronized L2ItemInstance[] unEquipItemInSlotAndRecord(int slot)
    {
		Inventory.ChangeRecorder recorder = newRecorder(); 
		try
		{
			unEquipItemInSlot(slot);
			if (getOwner() instanceof L2PcInstance)
				((L2PcInstance)getOwner()).refreshExpertisePenalty();
		} finally {
			removePaperdollListener(recorder);
		}
		return recorder.getChangedItems();
    }
    
	/**
	 * Unequips item in slot (i.e. equips with default value)
	 * @param slot : int designating the slot
	 */
	private void unEquipItemInBodySlot(int slot)
	{
		if (Config.DEBUG) _log.fine("--- unequip body slot:" + slot);
		int pdollSlot = -1;
		
		switch (slot)
		{
		case L2Item.SLOT_L_EAR:		pdollSlot = PAPERDOLL_LEAR;	break;
		case L2Item.SLOT_R_EAR:		pdollSlot = PAPERDOLL_REAR;	break;
		case L2Item.SLOT_NECK:		pdollSlot = PAPERDOLL_NECK;	break;
		case L2Item.SLOT_R_FINGER:	pdollSlot = PAPERDOLL_RFINGER;	break;
		case L2Item.SLOT_L_FINGER:	pdollSlot = PAPERDOLL_LFINGER;	break;
		case L2Item.SLOT_HAIR:		pdollSlot = PAPERDOLL_HAIR;	break;
		case L2Item.SLOT_FACE:		pdollSlot = PAPERDOLL_FACE;	break;
		case L2Item.SLOT_DHAIR:
			setPaperdollItem(PAPERDOLL_HAIR, null);
			setPaperdollItem(PAPERDOLL_FACE, null);// this should be the same as in DHAIR
			pdollSlot = PAPERDOLL_DHAIR;
			break;
		case L2Item.SLOT_HEAD:		pdollSlot = PAPERDOLL_HEAD;	break;
		case L2Item.SLOT_R_HAND:	pdollSlot = PAPERDOLL_RHAND; break;
		case L2Item.SLOT_L_HAND:	pdollSlot = PAPERDOLL_LHAND; break;
		case L2Item.SLOT_GLOVES:	pdollSlot = PAPERDOLL_GLOVES; break;
		case L2Item.SLOT_CHEST:		// fall through
		case L2Item.SLOT_FULL_ARMOR:pdollSlot = PAPERDOLL_CHEST; break;
		case L2Item.SLOT_LEGS:		pdollSlot = PAPERDOLL_LEGS;	break;
		case L2Item.SLOT_BACK:		pdollSlot = PAPERDOLL_BACK;	break;
		case L2Item.SLOT_FEET:		pdollSlot = PAPERDOLL_FEET;	break;
		case L2Item.SLOT_UNDERWEAR:	pdollSlot = PAPERDOLL_UNDER;break;
		case L2Item.SLOT_LR_HAND:
			setPaperdollItem(PAPERDOLL_LHAND, null);
			setPaperdollItem(PAPERDOLL_RHAND, null);// this should be the same as in LRHAND
			pdollSlot = PAPERDOLL_LRHAND;
			break;
		}
		if (pdollSlot >= 0)
			setPaperdollItem(pdollSlot, null);
	}

	/**
	 * Equips item and returns list of alterations
	 * @param item : L2ItemInstance corresponding to the item
	 * @return L2ItemInstance[] : list of alterations
	 */
    public L2ItemInstance[] equipItemAndRecord(L2ItemInstance item)
    {
		Inventory.ChangeRecorder recorder = newRecorder();
        
		try {
		    equipItem(item); 
		} 
		finally {
		    removePaperdollListener(recorder);
		}
        
		return recorder.getChangedItems();
    }
    
	/**
	 * Equips item in slot of paperdoll.
	 * @param item : L2ItemInstance designating the item and slot used.
	 */
	public synchronized void equipItem(L2ItemInstance item)
	{
        if((getOwner() instanceof L2PcInstance) && ((L2PcInstance)getOwner()).getPrivateStoreType() != 0)
            return;
        
        if(getOwner() instanceof L2PcInstance)
        {
            L2PcInstance player = (L2PcInstance)getOwner();
            
            if(!player.isGM())
                if (!player.isHero())
                {
                    int itemId = item.getItemId();
                    if ((itemId >= 6611 && itemId <= 6621) || itemId == 6842)
                        return;
                }
        }

		int targetSlot = item.getItem().getBodyPart();
        
		switch (targetSlot)
		{
			case L2Item.SLOT_LR_HAND:
			{
				if (setPaperdollItem(PAPERDOLL_LHAND, null) != null)
				{
					// exchange 2h for 2h
					setPaperdollItem(PAPERDOLL_RHAND, null);
					setPaperdollItem(PAPERDOLL_LHAND, null);
				}
				else
				{
					setPaperdollItem(PAPERDOLL_RHAND, null);
				}
				
				setPaperdollItem(PAPERDOLL_RHAND, item);
				setPaperdollItem(PAPERDOLL_LRHAND, item);
				break;
			}
			case L2Item.SLOT_L_HAND:
			{
				if (!(item.getItem() instanceof L2EtcItem) || item.getItem().getItemType() != L2EtcItemType.ARROW)
				{
					L2ItemInstance old1 = setPaperdollItem(PAPERDOLL_LRHAND, null);
                    
					if (old1 != null)
					{
						setPaperdollItem(PAPERDOLL_RHAND, null);
					}
				}
                
				setPaperdollItem(PAPERDOLL_LHAND, null);
				setPaperdollItem(PAPERDOLL_LHAND, item);
				break;
			}
			case L2Item.SLOT_R_HAND:
			{
				if (_paperdoll[PAPERDOLL_LRHAND] != null)
				{
					setPaperdollItem(PAPERDOLL_LRHAND, null);
					setPaperdollItem(PAPERDOLL_LHAND, null);
					setPaperdollItem(PAPERDOLL_RHAND, null);
				}
				else
				{
					setPaperdollItem(PAPERDOLL_RHAND, null);
				}
				
				setPaperdollItem(PAPERDOLL_RHAND, item);
				break;
			}
			case L2Item.SLOT_L_EAR:
			case L2Item.SLOT_R_EAR:
			case L2Item.SLOT_L_EAR | L2Item.SLOT_R_EAR:
			{
				if (_paperdoll[PAPERDOLL_LEAR] == null)
				{
					setPaperdollItem(PAPERDOLL_LEAR, item);
				}
				else if (_paperdoll[PAPERDOLL_REAR] == null)
				{
					setPaperdollItem(PAPERDOLL_REAR, item);
				}
				else
				{
					setPaperdollItem(PAPERDOLL_LEAR, null);
					setPaperdollItem(PAPERDOLL_LEAR, item);
				}
					
				break;
			}
			case L2Item.SLOT_L_FINGER:
			case L2Item.SLOT_R_FINGER:
			case L2Item.SLOT_L_FINGER | L2Item.SLOT_R_FINGER:
			{
				if (_paperdoll[PAPERDOLL_LFINGER] == null)
				{
					setPaperdollItem(PAPERDOLL_LFINGER, item);
				}
				else if (_paperdoll[PAPERDOLL_RFINGER] == null)
				{
					setPaperdollItem(PAPERDOLL_RFINGER, item);
				}
				else
				{
					setPaperdollItem(PAPERDOLL_LFINGER, null);
					setPaperdollItem(PAPERDOLL_LFINGER, item);
				}
                
				break;
			}
			case L2Item.SLOT_NECK:
				setPaperdollItem(PAPERDOLL_NECK, item);
				break;
			case L2Item.SLOT_FULL_ARMOR:
				setPaperdollItem(PAPERDOLL_CHEST, null);
				setPaperdollItem(PAPERDOLL_LEGS, null);
				setPaperdollItem(PAPERDOLL_CHEST, item);
				break;
			case L2Item.SLOT_CHEST:
				setPaperdollItem(PAPERDOLL_CHEST, item);
				break;
			case L2Item.SLOT_LEGS:
			{
				// handle full armor
				L2ItemInstance chest = getPaperdollItem(PAPERDOLL_CHEST);
				if (chest != null && chest.getItem().getBodyPart() == L2Item.SLOT_FULL_ARMOR)
				{
					setPaperdollItem(PAPERDOLL_CHEST, null);
				}
				
				setPaperdollItem(PAPERDOLL_LEGS, null);
				setPaperdollItem(PAPERDOLL_LEGS, item);
				break;
			}
			case L2Item.SLOT_FEET:
				setPaperdollItem(PAPERDOLL_FEET, item);
				break;
			case L2Item.SLOT_GLOVES:
				setPaperdollItem(PAPERDOLL_GLOVES, item);
				break;
			case L2Item.SLOT_HEAD:
				setPaperdollItem(PAPERDOLL_HEAD, item);
				break;
			case L2Item.SLOT_HAIR:
				if (setPaperdollItem(PAPERDOLL_DHAIR, null) != null)
				{
					setPaperdollItem(PAPERDOLL_DHAIR, null);
					setPaperdollItem(PAPERDOLL_HAIR, null);
					setPaperdollItem(PAPERDOLL_FACE, null);
				}
				else
					setPaperdollItem(PAPERDOLL_HAIR, null);
				setPaperdollItem(PAPERDOLL_HAIR, item);
				break;
			case L2Item.SLOT_FACE:
				if (setPaperdollItem(PAPERDOLL_DHAIR, null) != null)
				{
					setPaperdollItem(PAPERDOLL_DHAIR, null);
					setPaperdollItem(PAPERDOLL_HAIR, null);
					setPaperdollItem(PAPERDOLL_FACE, null);
				}
				else
					setPaperdollItem(PAPERDOLL_FACE, null);
				setPaperdollItem(PAPERDOLL_FACE, item);
				break;
			case L2Item.SLOT_DHAIR:
				if (setPaperdollItem(PAPERDOLL_HAIR, null) != null)
				{
					setPaperdollItem(PAPERDOLL_HAIR, null);
					setPaperdollItem(PAPERDOLL_FACE, null);
				}
				else
				{
					setPaperdollItem(PAPERDOLL_FACE, null);
				}				
				setPaperdollItem(PAPERDOLL_DHAIR, item);
				break;
			case L2Item.SLOT_UNDERWEAR:
				setPaperdollItem(PAPERDOLL_UNDER, item);
				break;
			case L2Item.SLOT_BACK:
				setPaperdollItem(PAPERDOLL_BACK, item);
				break;
			default:
				_log.warning("unknown body slot:" + targetSlot);
		}
	}
    
	/**
	 * Refresh the weight of equipment loaded
	 */

	@Override
	protected void refreshWeight()
 	{
		long weight = 0;
 
 		for (L2ItemInstance item : _items)
 		{
 			if (item != null && item.getItem() != null)
 				weight += item.getItem().getWeight() * item.getCount();
 		}
		_totalWeight = (int)Math.min(weight, Integer.MAX_VALUE);
 	}
	
	/**
	 * Returns the totalWeight.
	 * @return int
	 */
	public int getTotalWeight()
	{
		return _totalWeight;
	}
	
	/**
	 * Return the L2ItemInstance of the arrows needed for this bow.<BR><BR>
	 * @param bow : L2Item designating the bow
	 * @return L2ItemInstance pointing out arrows for bow
	 */
	public L2ItemInstance findArrowForBow(L2Item bow)
	{
		int arrowsId = 0;
		
		switch (bow.getCrystalType())
		{
		default: // broken weapon.csv ??
		case L2Item.CRYSTAL_NONE:   arrowsId = 17;   break; // Wooden arrow
		case L2Item.CRYSTAL_D:      arrowsId = 1341; break; // Bone arrow
		case L2Item.CRYSTAL_C:      arrowsId = 1342; break; // Fine steel arrow
		case L2Item.CRYSTAL_B:      arrowsId = 1343; break; // Silver arrow
		case L2Item.CRYSTAL_A:      arrowsId = 1344; break; // Mithril arrow
		case L2Item.CRYSTAL_S:      arrowsId = 1345; break; // Shining arrow
		}

		// Get the L2ItemInstance corresponding to the item identifier and return it
		return getItemByItemId(arrowsId);
	}

	/**
	 * Get back items in inventory from database
	 */

	@Override
	public void restore()
	{
	    Connection con = null;
	    try
	    {
	        con = L2DatabaseFactory.getInstance().getConnection();
	        PreparedStatement statement = con.prepareStatement(
	                                                           "SELECT object_id FROM items WHERE owner_id=? AND (loc=? OR loc=?) " +
	        "ORDER BY object_id DESC");
	        statement.setInt(1, getOwner().getObjectId());
	        statement.setString(2, getBaseLocation().name());
	        statement.setString(3, getEquipLocation().name());
	        ResultSet inv = statement.executeQuery();
	        
	        L2ItemInstance item;
	        while (inv.next())
	        {
	            int objectId = inv.getInt(1);
	            item = L2ItemInstance.restoreFromDb(objectId);
	            if (item == null) continue;
	            
	            if(getOwner() instanceof L2PcInstance)
	            {
	                L2PcInstance player = (L2PcInstance)getOwner();
	                
	                if(!player.isGM())
	                    if (!player.isHero())
	                    {
	                        int itemId = item.getItemId();
	                        if ((itemId >= 6611 && itemId <= 6621) || itemId == 6842)
	                            item.setLocation(ItemLocation.INVENTORY);
	                    }
	            }
	            
	            L2World.getInstance().storeObject(item);
	            
	            // If stackable item is found in inventory just add to current quantity
	            if (item.isStackable() && getItemByItemId(item.getItemId()) != null)
	                addItem("Restore", item, null, getOwner());
	            else addItem(item);
	        }
	        
	        inv.close();
	        statement.close();
	        refreshWeight();
	    }
	    catch (Exception e)
	    {
	        _log.warning("Could not restore inventory : " + e);
	    } 
	    finally 
	    {
	        try { con.close(); } catch (Exception e) {}
	    }
	}
	
	/**
	 * Re-notify to paperdoll listeners every equipped item
	 */
	public void reloadEquippedItems() 
	{
		L2ItemInstance item;
		int slot;
		
		for (int i = 0; i < _paperdoll.length; i++)
		{
			item = _paperdoll[i];
			if (item == null) continue;
			slot = item.getEquipSlot();
			
			for (PaperdollListener listener : _paperdollListeners)
			{
				if (listener == null) continue;
				listener.notifyUnequiped(slot, item);
				listener.notifyEquiped(slot, item);
			}
		}
	}
}