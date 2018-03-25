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
package com.it.br.gameserver.script.faenor;

import com.it.br.Config;
import com.it.br.gameserver.Announcements;
import com.it.br.gameserver.datatables.EventDroplist;
import com.it.br.gameserver.model.L2DropCategory;
import com.it.br.gameserver.model.L2DropData;
import com.it.br.gameserver.model.L2PetData;
import com.it.br.gameserver.script.DateRange;
import com.it.br.gameserver.script.EngineInterface;
import com.it.br.gameserver.script.Expression;
import com.it.br.gameserver.templates.L2NpcTemplate;
import org.apache.bsf.BSFException;
import org.apache.bsf.BSFManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Luis Arias
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class FaenorInterface implements EngineInterface
{
    private static FaenorInterface _instance;

    public static FaenorInterface getInstance()
    {
        if (_instance == null)
        {
            _instance = new FaenorInterface();
        }
        return _instance;
    }

    public FaenorInterface()
    {
    }

    /* (non-Javadoc)
     * @see com.it.br.gameserver.script.EngineInterface#getAllPlayers()
     */
    @SuppressWarnings("rawtypes")
	public List getAllPlayers()
    {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     *
     * Adds a new Quest Drop to an NPC
     */
	public void addQuestDrop(int npcID, int itemID, int min, int max, int chance, String questID, String[] states)
    {
        L2NpcTemplate npc = npcTable.getTemplate(npcID);
        if (npc == null)
        {
            throw new NullPointerException();
        }
        L2DropData drop = new L2DropData();
        drop.setItemId(itemID);
        drop.setMinDrop(min);
        drop.setMaxDrop(max);
        drop.setChance(chance);
        drop.setQuestID(questID);
        drop.addStates(states);
        addDrop(npc, drop, false);
    }

    /**
     *
     * Adds a new Drop to an NPC
     */
    public void addDrop(int npcID, int itemID, int min, int max, boolean sweep, int chance) throws NullPointerException
    {
        L2NpcTemplate npc = npcTable.getTemplate(npcID);
        if (npc == null)
        {
            if (Config.DEBUG) System.out.print("Npc doesnt Exist");
            throw new NullPointerException();
        }
        L2DropData drop = new L2DropData();
        drop.setItemId(itemID);
        drop.setMinDrop(min);
        drop.setMaxDrop(max);
        drop.setChance(chance);

        addDrop(npc, drop, sweep);
    }

	/**
	 * Adds a new drop to an NPC.  If the drop is sweep, it adds it to the NPC's Sweep category
	 * If the drop is non-sweep, it creates a new category for this drop.
	 *
	 * @param npc
	 * @param drop
	 * @param sweep
	 */
    public void addDrop(L2NpcTemplate npc, L2DropData drop, boolean sweep)
    {
    	if(sweep)
    		addDrop(npc, drop,-1);
    	else
    	{
        	int maxCategory = -1;

	    	for(L2DropCategory cat:npc.getDropData())
	    	{
	    		if(maxCategory<cat.getCategoryType())
	    			maxCategory = cat.getCategoryType();
	    	}
	    	maxCategory++;
	    	npc.addDropData(drop, maxCategory);
    	}

    }

	/**
	 * Adds a new drop to an NPC, in the specified category.  If the category does not exist,
	 * it is created.
	 *
	 * @param npc
	 * @param drop
	 */
    public void addDrop(L2NpcTemplate npc, L2DropData drop, int category)
    {
    	npc.addDropData(drop, category);
    }


    /**
     * @return Returns the _questDrops.
     */
    @SuppressWarnings("rawtypes")
	public List getQuestDrops(int npcID)
    {
        L2NpcTemplate npc = npcTable.getTemplate(npcID);
        if (npc == null)
        {
            return null;
        }
        List<L2DropData> questDrops = new ArrayList<>();
        for (L2DropCategory cat:npc.getDropData())
        for (L2DropData drop : cat.getAllDrops() )
        {
            if (drop.getQuestID() != null)
            {
                questDrops.add(drop);
            }
        }
        return questDrops;
    }


	public void addEventDrop(int[] items, int[] count, double chance, DateRange range)
    {
        EventDroplist.getInstance().addGlobalDrop(items, count, (int)(chance * L2DropData.MAX_CHANCE), range);
    }


	public void onPlayerLogin(String[] message, DateRange validDateRange)
    {
        Announcements.getInstance().addEventAnnouncement(validDateRange, message);
    }

    public void addPetData(BSFManager context, int petID, int levelStart, int levelEnd, Map<String, String> stats)
		throws BSFException
    {
        L2PetData[] petData = new L2PetData[levelEnd - levelStart + 1];
        int value           = 0;
        for (int level = levelStart; level <= levelEnd; level++)
        {
            petData[level - 1]  = new L2PetData();
            petData[level - 1].setPetID(petID);
            petData[level - 1].setPetLevel(level);

	        context.declareBean("level", new Double(level), Double.TYPE);
            for (String stat : stats.keySet())
            {
				value = ((Number)Expression.eval(context, "beanshell", stats.get(stat))).intValue();
                petData[level - 1].setStat(stat, value);
            }
	        context.undeclareBean("level");
        }

    }

}
