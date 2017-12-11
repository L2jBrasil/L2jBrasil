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
package com.it.br.gameserver.model.quest;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import com.it.br.Config;
import com.it.br.gameserver.GameTimeController;
import com.it.br.gameserver.cache.HtmCache;
import com.it.br.gameserver.instancemanager.QuestManager;
import com.it.br.gameserver.model.L2Character;
import com.it.br.gameserver.model.L2DropData;
import com.it.br.gameserver.model.L2ItemInstance;
import com.it.br.gameserver.model.PcInventory;
import com.it.br.gameserver.model.actor.instance.L2MonsterInstance;
import com.it.br.gameserver.model.actor.instance.L2NpcInstance;
import com.it.br.gameserver.model.actor.instance.L2PcInstance;
import com.it.br.gameserver.network.SystemMessageId;
import com.it.br.gameserver.network.serverpackets.ExShowQuestMark;
import com.it.br.gameserver.network.serverpackets.ItemList;
import com.it.br.gameserver.network.serverpackets.PlaySound;
import com.it.br.gameserver.network.serverpackets.QuestList;
import com.it.br.gameserver.network.serverpackets.StatusUpdate;
import com.it.br.gameserver.network.serverpackets.SystemMessage;
import com.it.br.gameserver.network.serverpackets.TutorialCloseHtml;
import com.it.br.gameserver.network.serverpackets.TutorialEnableClientEvent;
import com.it.br.gameserver.network.serverpackets.TutorialShowHtml;
import com.it.br.gameserver.network.serverpackets.TutorialShowQuestionMark;
import com.it.br.gameserver.skills.Stats;
import com.it.br.util.Rnd;

/**
 * @author Luis Arias
 */
public final class QuestState
{
	protected static final Logger _log = Logger.getLogger(Quest.class.getName());

	/** Quest associated to the QuestState */
	private final String _questName;

	/** Player who engaged the quest */
	private final L2PcInstance _player;

	/** State of the quest */
	private State _state;

	/** Boolean representing the completion of the quest */
	private boolean _isCompleted;

	/** List of couples (variable for quest,value of the variable for quest) */
	private Map<String, String> _vars = new HashMap<>();

    /** Boolean flag letting QuestStateManager know to exit quest when cleaning up */
    private boolean _isExitQuestOnCleanUp = false;

    /**
	 * Constructor of the QuestState : save the quest in the list of quests of the player.<BR/><BR/>
	 *
	 * <U><I>Actions :</U></I><BR/>
	 * <LI>Save informations in the object QuestState created (Quest, Player, Completion, State)</LI>
	 * <LI>Add the QuestState in the player's list of quests by using setQuestState()</LI>
	 * <LI>Add drops gotten by the quest</LI>
	 * <BR/>
	 * @param quest : quest associated with the QuestState
	 * @param player : L2PcInstance pointing out the player
	 * @param state : state of the quest
	 */
    QuestState(Quest quest, L2PcInstance player, State state)
    {
    	_questName = quest.getName();
		_player = player;

		// Save the state of the quest for the player in the player's list of quest onwed
        getPlayer().setQuestState(this);

		// set the state of the quest
		_state = state;
    }

    public String getQuestName()
    {
    	return _questName;
    }
    /**
     * Return the quest
     * @return Quest
     */
	public Quest getQuest()
    {
		return QuestManager.getInstance().getQuest(_questName);
	}

	/**
	 * Return the L2PcInstance
	 * @return L2PcInstance
	 */
	public L2PcInstance getPlayer()
    {
		return _player;
	}

	/**
	 * Return the state of the quest
	 * @return State
	 */
	public State getState()
    {
		return _state;
	}

	/**
	 * Return true if quest completed, false otherwise
	 * @return boolean
	 */
	public boolean isCompleted()
    {
		return _isCompleted;
	}

	/**
	 * Return true if quest started, false otherwise
	 * @return boolean
	 */
	public boolean isStarted()
    {
		if (getStateId().equals("Start") || getStateId().equals("Completed"))
		    return false;

        return true;
	}

	/**
	 * Return state of the quest after its initialization.<BR><BR>
	 * <U><I>Actions :</I></U>
	 * <LI>Remove drops from previous state</LI>
	 * <LI>Set new state of the quest</LI>
	 * <LI>Add drop for new state</LI>
	 * <LI>Update information in database</LI>
	 * <LI>Send packet QuestList to client</LI>
	 * @param state
	 * @return object
	 */
	public Object setState(State state)
    {
        // set new state
		_state = state;

		if(state == null) 
			return null;

		if(getStateId().equals("Completed")) 
			_isCompleted = true;
		else _isCompleted = false;

		Quest.updateQuestInDb(this);
		QuestList ql = new QuestList();

        getPlayer().sendPacket(ql);
		return state;
	}

	/**
	 * Return ID of the state of the quest
	 * @return String
	 */
	public String getStateId()
	{
		if(getState()!=null)
			return getState().getName();
		else
			return "Created";
	}

	/**
	 * Add parameter used in quests.
	 * 	 * @param var : String pointing out the name of the variable for quest
	 * @param val : String pointing out the value of the variable for quest
	 * @return String (equal to parameter "val")
	 */
	String setInternal(String var, String val)
	{
		if (var == null || var.isEmpty() || val == null || val.isEmpty())
			return "";

		_vars.put(var, val);
		return val;
	}

	/**
	 * Return value of parameter "val" after adding the couple (var,val) in class variable "vars".<BR><BR>
	 * <U><I>Actions :</I></U><BR>
	 * <LI>Initialize class variable "vars" if is null</LI>
	 * <LI>Initialize parameter "val" if is null</LI>
	 * <LI>Add/Update couple (var,val) in class variable FastMap "vars"</LI>
	 * <LI>If the key represented by "var" exists in FastMap "vars", the couple (var,val) is updated in the database. The key is known as
	 * existing if the preceding value of the key (given as result of function put()) is not null.<BR>
	 * If the key doesn't exist, the couple is added/created in the database</LI>
	 * @param var : String indicating the name of the variable for quest
	 * @param val : String indicating the value of the variable for quest
	 * @return String (equal to parameter "val")
	 */
	public String set(String var, String val)
	{

		if (val == null)
			val = "";

		// FastMap.put() returns previous value associated with specified key, or null if there was no mapping for key.
		String old = _vars.put(var, val);

		if (old != null)
			Quest.updateQuestVarInDb(this, var, val);
		else
			Quest.createQuestVarInDb(this, var, val);

		if (var == "cond")
		{
	        try
	        {
	        	int previousVal = 0;
	        	try
	        	{
	        		previousVal = Integer.parseInt(old);
	        	}
	        	catch(Exception ex)
	        	{
	        		previousVal = 0;
	        	}
	        	setCond(Integer.parseInt(val), previousVal);
	        }
	        catch (Exception e)
	        {
	            _log.finer(getPlayer().getName()+", "+getQuestName()+" cond ["+val+"] is not an integer.  Value stored, but no packet was sent: " + e);
	        }
		}

		return val;
	}

	/**
	 * Internally handles the progression of the quest so that it is ready for sending
	 * appropriate packets to the client<BR><BR>
	 * <U><I>Actions :</I></U><BR>
	 * <LI>Check if the new progress number resets the quest to a previous (smaller) step</LI>
	 * <LI>If not, check if quest progress steps have been skipped</LI>
	 * <LI>If skipped, prepare the variable completedStateFlags appropriately to be ready for sending to clients</LI>
	 * <LI>If no steps were skipped, flags do not need to be prepared...</LI>
	 * <LI>If the passed step resets the quest to a previous step, reset such that steps after the parameter are not
	 * considered, while skipped steps before the parameter, if any, maintain their info</LI>
	 * @param cond : int indicating the step number for the current quest progress (as will be shown to the client)
	 * @param old : int indicating the previously noted step
	 *
	 * For more info on the variable communicating the progress steps to the client, please see
	 */
	private void setCond(int cond, int old)
	{
		int completedStateFlags = 0;	// initializing...

		// if there is no change since last setting, there is nothing to do here
		if (cond == old)
			return;

		// cond 0 and 1 do not need completedStateFlags.  Also, if cond > 1, the 1st step must
		// always exist (i.e. it can never be skipped).  So if cond is 2, we can still safely
		// assume no steps have been skipped.
		// Finally, more than 31 steps CANNOT be supported in any way with skipping.
		if (cond < 3 || cond > 31)
		{
			unset("__compltdStateFlags");
		}
		else
			completedStateFlags = getInt("__compltdStateFlags");

		// case 1: No steps have been skipped so far...
		if(completedStateFlags == 0)
		{
			// check if this step also doesn't skip anything.  If so, no further work is needed
			// also, in this case, no work is needed if the state is being reset to a smaller value
			// in those cases, skip forward to informing the client about the change...

			// ELSE, if we just now skipped for the first time...prepare the flags!!!
			if (cond > (old+1))
			{
				// set the most significant bit to 1 (indicates that there exist skipped states)
				// also, ensure that the least significant bit is an 1 (the first step is never skipped, no matter
				// what the cond says)
				completedStateFlags = 0x80000001;

				// since no flag had been skipped until now, the least significant bits must all
				// be set to 1, up until "old" number of bits.
				completedStateFlags |= ((1<<old)-1);

				// now, just set the bit corresponding to the passed cond to 1 (current step)
				completedStateFlags |= (1<<(cond-1));
				set("__compltdStateFlags", String.valueOf(completedStateFlags));
			}
		}
		// case 2: There were exist previously skipped steps
		else
		{
			// if this is a push back to a previous step, clear all completion flags ahead
			if (cond < old)
			{
				completedStateFlags &= ((1<<cond)-1);  // note, this also unsets the flag indicating that there exist skips

				//now, check if this resulted in no steps being skipped any more
				if(completedStateFlags == ((1<<cond)-1))
					unset("__compltdStateFlags");
				else
				{
					// set the most significant bit back to 1 again, to correctly indicate that this skips states.
					// also, ensure that the least significant bit is an 1 (the first step is never skipped, no matter
					// what the cond says)
					completedStateFlags |= 0x80000001;
					set("__compltdStateFlags", String.valueOf(completedStateFlags));
				}
			}
			// if this moves forward, it changes nothing on previously skipped steps...so just mark this
			// state and we are done
			else
			{
				completedStateFlags |= (1<<(cond-1));
				set("__compltdStateFlags", String.valueOf(completedStateFlags));
			}
		}

		// send a packet to the client to inform it of the quest progress (step change)
		QuestList ql = new QuestList();
		getPlayer().sendPacket(ql);
		
		int questId = getQuest().getQuestIntId();
		if (questId > 0 && questId < 999 && cond > 0)
			getPlayer().sendPacket(new ExShowQuestMark(questId));
	}

	/**
	 * Remove the variable of quest from the list of variables for the quest.<BR><BR>
	 * <U><I>Concept : </I></U>
	 * Remove the variable of quest represented by "var" from the class variable FastMap "vars" and from the database.
	 * @param var : String designating the variable for the quest to be deleted
	 * @return String pointing out the previous value associated with the variable "var"
	 */
	public String unset(String var)
	{
		if (_vars == null)
			return null;

		String old = _vars.remove(var);

		if (old != null)
			Quest.deleteQuestVarInDb(this, var);

		return old;
	}

	/**
	 * Return the value of the variable of quest represented by "var"
	 * @param var : name of the variable of quest
	 * @return Object
	 */
	public Object get(String var)
	{
		if (_vars == null)
			return null;

		return _vars.get(var);
	}

	/**
	 * Return the value of the variable of quest represented by "var"
	 * @param var : String designating the variable for the quest
	 * @return int
	 */
    public int getInt(String var)
    {
        if (_vars == null)
		return 0;

        int varint = 0;

        try
        {
            varint = Integer.parseInt(_vars.get(var));
        }
        catch (Exception e)
        {
            _log.finer(getPlayer().getName()+": variable "+var+" isn't an integer: " + varint + e);
//	    if (Config.AUTODELETE_INVALID_QUEST_DATA)
//		exitQuest(true);
        }

        return varint;
    }

    /**
     * Add player to get notification of characters death
     * @param character : L2Character of the character to get notification of death
     */
    public void addNotifyOfDeath(L2Character character)
    {
        if (character == null)
            return;

        character.addNotifyQuestOfDeath(this);
    }

	/**
	 * Return the quantity of one sort of item hold by the player
	 * @param itemId : ID of the item wanted to be count
	 * @return int
	 */
    public int getQuestItemsCount(int itemId)
    {
        int count = 0;

        for (L2ItemInstance item: getPlayer().getInventory().getItems())
            if (item != null && item.getItemId() == itemId)
                count += item.getCount();

        return count;
    }
	
	/**
	 * Check for an item in player's inventory.
	 * @param itemId the ID of the item to check for
	 * @return {@code true} if the item exists in player's inventory, {@code false} otherwise
	 */
	public boolean hasQuestItems(int itemId)
	{
		return _player.getInventory().getItemByItemId(itemId) != null;
	}
	
	/**
	 * Check for multiple items in player's inventory.
	 * @param itemIds a list of item IDs to check for
	 * @return {@code true} if all items exist in player's inventory, {@code false} otherwise
	 */
	public boolean hasQuestItems(int... itemIds)
	{
		final PcInventory inv = _player.getInventory();
		for (int itemId : itemIds)
		{
			if (inv.getItemByItemId(itemId) == null)
				return false;
		}
		return true;
	}
	
    /**
     * Return the level of enchantment on the weapon of the player(Done specifically for weapon SA's)
     * @param itemId : ID of the item to check enchantment
     * @return int
     */
    public int getEnchantLevel(int itemId)
    {
        L2ItemInstance enchanteditem = getPlayer().getInventory().getItemByItemId(itemId);

        if (enchanteditem == null)
            return 0;

        return enchanteditem.getEnchantLevel();
    }

    /**
	 * Give item/reward to the player
	 * @param itemId
	 * @param count
	 */
	public void giveItems(int itemId, int count)
	{
		giveItems(itemId, count, 0);
	}

	public void giveItems(int itemId, int count, int enchantlevel)
	{
		if (count <= 0)
			return;
		
		int questId = getQuest().getQuestIntId();
		// If item for reward is gold (ID=57), modify count with rate for quest reward
		if (itemId == 57 && !(questId>=217 && questId<=233) && !(questId>=401 && questId<=418) )
			count=(int)(count*Config.RATE_QUESTS_REWARD);
		// Set quantity of item

		// Add items to player's inventory
		L2ItemInstance item = getPlayer().getInventory().addItem("Quest", itemId, count, getPlayer(), getPlayer().getTarget());

		if (item == null)
			return;
		if (enchantlevel > 0)
			item.setEnchantLevel(enchantlevel);

		// If item for reward is gold, send message of gold reward to client
		if (itemId == 57)
		{
			SystemMessage smsg = new SystemMessage(SystemMessageId.EARNED_ADENA);
			smsg.addNumber(count);
			getPlayer().sendPacket(smsg);
		}
		// Otherwise, send message of object reward to client
		else
		{
            if (count > 1)
            {
            	SystemMessage smsg = new SystemMessage(SystemMessageId.EARNED_S2_S1_S);
            	smsg.addItemName(item.getItemId());
            	smsg.addNumber(count);
            	getPlayer().sendPacket(smsg);
            }
            else
            {
                SystemMessage smsg = new SystemMessage(SystemMessageId.EARNED_ITEM);
                smsg.addItemName(item.getItemId());
                getPlayer().sendPacket(smsg);
            }
		}
        getPlayer().sendPacket(new ItemList(getPlayer(), false));

        StatusUpdate su = new StatusUpdate(getPlayer().getObjectId());
        su.addAttribute(StatusUpdate.CUR_LOAD, getPlayer().getCurrentLoad());
        getPlayer().sendPacket(su);
	}

    /**
     * Drop Quest item using Config.RATE_DROP_QUEST
     * @param itemId : int Item Identifier of the item to be dropped
     * @param count (minCount, maxCount) : int Quantity of items to be dropped
     * @param neededCount : Quantity of items needed for quest
     * @param dropChance : int Base chance of drop, same as in droplist
     * @param sound : boolean indicating whether to play sound
     * @return boolean indicating whether player has requested number of items
     */
    public boolean dropQuestItems(int itemId, int count, int neededCount, int dropChance, boolean sound)
    {
        return dropQuestItems(itemId, count, count, neededCount, dropChance, sound);
    }

    public boolean dropQuestItems(int itemId, int minCount, int maxCount, int neededCount, int dropChance, boolean sound)
    {
        dropChance *= Config.RATE_DROP_QUEST / ((getPlayer().getParty() != null) ? getPlayer().getParty().getMemberCount() : 1);
        int currentCount = getQuestItemsCount(itemId);

        if (neededCount > 0 && currentCount >= neededCount)
            return true;

        if (currentCount >= neededCount)
            return true;

        int itemCount = 0;
        int random = Rnd.get(L2DropData.MAX_CHANCE);

        while (random < dropChance)
        {
            // Get the item quantity dropped
            if (minCount < maxCount)
                itemCount += Rnd.get(minCount, maxCount);
            else if (minCount == maxCount)
                itemCount += minCount;
            else
                itemCount++;

            // Prepare for next iteration if dropChance > L2DropData.MAX_CHANCE
            dropChance -= L2DropData.MAX_CHANCE;
        }

        if (itemCount > 0)
        {
            // if over neededCount, just fill the gap
            if (neededCount > 0 && currentCount + itemCount > neededCount)
                itemCount = neededCount - currentCount;

            // Inventory slot check
            if (!getPlayer().getInventory().validateCapacityByItemId(itemId))
                return false;

            //just wait 4-5 seconds before the drop  
            try 
            { 
            	Thread.sleep(Rnd.get(4, 5)*1000); 
            } 
            catch(InterruptedException e) 
            {}

            // Give the item to Player
            getPlayer().addItem("Quest", itemId, itemCount, getPlayer().getTarget(), true);

            if (sound)
            {
                playSound((currentCount + itemCount < neededCount) ? "Itemsound.quest_itemget" : "Itemsound.quest_middle");
            }
        }

        return (neededCount > 0 && currentCount + itemCount >= neededCount);
    }

    //TODO: More radar functions need to be added when the radar class is complete.
    // BEGIN STUFF THAT WILL PROBABLY BE CHANGED
	public void addRadar(int x, int y, int z)
	{
		getPlayer().getRadar().addMarker(x, y, z);
	}

	public void removeRadar(int x, int y, int z)
	{ 
		getPlayer().getRadar().removeMarker(x, y, z);
	}

	public void clearRadar()
	{
		getPlayer().getRadar().removeAllMarkers();
		}
	// END STUFF THAT WILL PROBABLY BE CHANGED

	/**
	 * Remove items from player's inventory when talking to NPC in order to have rewards.<BR><BR>
	 * <U><I>Actions :</I></U>
	 * <LI>Destroy quantity of items wanted</LI>
	 * <LI>Send new inventory list to player</LI>
	 * @param itemId : Identifier of the item
	 * @param count : Quantity of items to destroy
	 */
	public void takeItems(int itemId, int count)
	{
		// Get object item from player's inventory list
		L2ItemInstance item = getPlayer().getInventory().getItemByItemId(itemId);

		if (item == null)
			return;

		// Tests on count value in order not to have negative value
		if (count < 0 || count > item.getCount())
			count = item.getCount();

		// Destroy the quantity of items wanted
        if (itemId == 57)
            getPlayer().reduceAdena("Quest", count, getPlayer(), true);
        else
        { 
        	if (item.isEquipped()) 
        		getPlayer().getInventory().unEquipItemInBodySlotAndRecord(item.getItem().getBodyPart()); 
        	getPlayer().destroyItemByItemId("Quest", itemId, count, getPlayer(), true);
        }
    }

	/**
	 * Send a packet in order to play sound at client terminal
	 * @param sound
	 */
	public void playSound(String sound)
	{
		getPlayer().sendPacket(new PlaySound(sound));
	}

	/**
	 * Add XP and SP as quest reward
	 * @param exp
	 * @param sp
	 */
	public void addExpAndSp(int exp, int sp)
	{
	    getPlayer().addExpAndSp((int)getPlayer().calcStat(Stats.EXPSP_RATE, exp * Config.RATE_QUESTS_REWARD, null, null),
                           (int)getPlayer().calcStat(Stats.EXPSP_RATE, sp * Config.RATE_QUESTS_REWARD, null, null));
	}

	/**
	 * Return random value
	 * @param max : max value for randomisation
	 * @return int
	 */
	public int getRandom(int max)
	{
		return Rnd.get(max);
	}
	
	public int getRandom(int min, int max)
	{
		return Rnd.get(min, max);
	}

    /**
    * Return Item id 
     * @param loc 
    * @return int
    */
    public int getItemEquipped(int loc)
    { 
    	return getPlayer().getInventory().getPaperdollItemId(loc);
    }

    /**
    * Return the number of ticks from the GameTimeController
    * @return int
    */
    public int getGameTicks()
    {
    	return GameTimeController.getGameTicks();
    }

    /**
     * Return true if quest is to exited on clean up by QuestStateManager
     * @return boolean
     */
    public final boolean isExitQuestOnCleanUp()
    {
        return _isExitQuestOnCleanUp;
    }

    /**
     * Return the QuestTimer object with the specified name
     * @param isExitQuestOnCleanUp 
     */
    public void setIsExitQuestOnCleanUp(boolean isExitQuestOnCleanUp)
    {
        _isExitQuestOnCleanUp = isExitQuestOnCleanUp;
    }

    /**
     * Start a timer for quest.<BR><BR>
     * @param name The name of the timer. Will also be the value for event of onEvent
     * @param timeThe milisecond value the timer will elapse
     */
    public void startQuestTimer(String name, long time)
    {
    	getQuest().startQuestTimer(name, time, null, getPlayer());
    }

    public void startQuestTimer(String name, long time, L2NpcInstance npc)
    {
    	getQuest().startQuestTimer(name, time, npc, getPlayer());
    }

    /**
     * Return the QuestTimer object with the specified name
     * @param name 
     * @return QuestTimer<BR> Return null if name does not exist
     */
    public final QuestTimer getQuestTimer(String name)
    {
    	return getQuest().getQuestTimer(name, null, getPlayer());
    }

    /**
     * Add spawn for player instance
     * Return object id of newly spawned npc
     * @param npcId 
     * @return 
     */
    public L2NpcInstance addSpawn(int npcId)
    {
    	return addSpawn(npcId, getPlayer().getX(), getPlayer().getY(), getPlayer().getZ(), 0, false, 0);
    }

    public L2NpcInstance addSpawn(int npcId, int despawnDelay)
    {
    	return addSpawn(npcId, getPlayer().getX(), getPlayer().getY(), getPlayer().getZ(), 0, false, despawnDelay);
    }

    public L2NpcInstance addSpawn(int npcId, int x, int y, int z)
    {
    	return addSpawn(npcId, x, y, z, 0, false, 0);
    }

    /**
     * Add spawn for player instance
     * Will despawn after the spawn length expires
     * Uses player's coords and heading.
     * Adds a little randomization in the x y coords
     * Return object id of newly spawned npc
     * @param npcId 
     * @param cha 
     * @return 
     */
	public L2NpcInstance addSpawn(int npcId, L2Character cha)
	{
	    return addSpawn(npcId, cha, true,0);
	}

    public L2NpcInstance addSpawn(int npcId, L2Character cha, int despawnDelay)
    {
        return addSpawn(npcId, cha.getX(), cha.getY(), cha.getZ(), cha.getHeading(), true, despawnDelay);
    }

    /**
     * Add spawn for player instance
     * Will despawn after the spawn length expires
     * Return object id of newly spawned npc
     * @param npcId 
     * @param x 
     * @param y 
     * @param z 
     * @param despawnDelay 
     * @return 
     */
    public L2NpcInstance addSpawn(int npcId, int x, int y, int z, int despawnDelay)
    {
        return addSpawn(npcId, x, y, z, 0, false, despawnDelay);
    }

    /**
     * Add spawn for player instance
     * Inherits coords and heading from specified L2Character instance.
     * It could be either the player, or any killed/attacked mob
     * Return object id of newly spawned npc
     * @param npcId 
     * @param cha 
     * @param randomOffset 
     * @param despawnDelay 
     * @return 
     */
    public L2NpcInstance addSpawn(int npcId, L2Character cha, boolean randomOffset, int despawnDelay)
    {
        return addSpawn(npcId, cha.getX(), cha.getY(), cha.getZ(), cha.getHeading(), randomOffset, despawnDelay);
    }

    /**
     * Add spawn for player instance
     * Return object id of newly spawned npc
     * @param npcId 
     * @param x 
     * @param y 
     * @param z 
     * @param heading 
     * @param randomOffset 
     * @param despawnDelay 
     * @return 
     */
    public L2NpcInstance addSpawn(int npcId, int x, int y, int z,int heading, boolean randomOffset, int despawnDelay)
    {
    	return getQuest().addSpawn(npcId, x, y, z, heading, randomOffset, despawnDelay);
    }
    
    public L2NpcInstance addSpawn(int npcId, int x, int y, int z, int despawnDelay, boolean isSummonSpawn)
    {
    	return getQuest().addSpawn(npcId, x, y, z, 0, false, despawnDelay, isSummonSpawn);
    }

	public String showHtmlFile(String fileName)
    {
    	return getQuest().showHtmlFile(getPlayer(), fileName);
	}

	/**
	 * Destroy element used by quest when quest is exited
	 * @param repeatable
	 * @return QuestState
	 */
	public QuestState exitQuest(boolean repeatable)
	{
		// remove this quest from the notifyDeath list of this character if its on this list
		_player.removeNotifyQuestOfDeath(this);
		
		if (isCompleted())
			return this;

		// Say quest is completed
		_isCompleted = true;
		// Clean registered quest items

		// Remove quest variables.
		_vars.clear();

		int[] itemIdList = getQuest().getRegisteredItemIds();
		if (itemIdList != null)
		{
			for (int i = 0; i < itemIdList.length; i++)
				takeItems(itemIdList[i], -1);
		}

		// If quest is repeatable, delete quest from list of quest of the player and from database (quest CAN be created again => repeatable)
		if (repeatable)
		{
			getPlayer().delQuestState(getQuestName());
			Quest.deleteQuestInDb(this);
			
			_vars = null;
		}
		else

		Quest.updateQuestInDb(this);
		return this;
	}
	 
	public void showQuestionMark(int number) 
	{ 
		getPlayer().sendPacket(new TutorialShowQuestionMark(number)); 
	} 
 		 
	public void playTutorialVoice(String voice) 
	{ 
		getPlayer().sendPacket(new PlaySound(2, voice, 0, 0, getPlayer().getX(), getPlayer().getY(), getPlayer().getZ())); 
	} 
 		 
	public void showTutorialHTML(String html) 
	{ 
		String text = HtmCache.getInstance().getHtm("data/jscript/quests/255_Tutorial/"+ html); 
		if(text == null || text.equalsIgnoreCase("")) 
			text = "<html><body>File data/jscript/quests/255_Tutorial/" + html + " not found or file is empty.</body></html>"; 
		getPlayer().sendPacket(new TutorialShowHtml(text)); 
	} 
	
	public void closeTutorialHtml() 
	{ 
		getPlayer().sendPacket(new TutorialCloseHtml()); 
	} 
 		 
	public void onTutorialClientEvent(int number) 
	{ 
		getPlayer().sendPacket(new TutorialEnableClientEvent(number)); 
	} 
 		 
	public void dropItem(L2MonsterInstance npc, L2PcInstance player, int itemId, int count) 
	{ 
		npc.DropItem(player, itemId, count); 
	} 
}
