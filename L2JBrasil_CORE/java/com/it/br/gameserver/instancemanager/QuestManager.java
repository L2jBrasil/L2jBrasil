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

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import com.it.br.Config;
import com.it.br.gameserver.model.quest.Quest;
import com.it.br.gameserver.scripting.L2ScriptEngineManager;
import com.it.br.gameserver.scripting.ScriptManager;

public class QuestManager extends ScriptManager<Quest>
{
    protected static final Logger _log = Logger.getLogger(QuestManager.class.getName());
    private static QuestManager _instance;
    // Data Field
    private Map<String, Quest> _quests = new HashMap<>();

    // Constructor
    public QuestManager()
    {
    }
    
	public static QuestManager getInstance()
	{
		if(_instance == null)
		{
			_instance = new QuestManager();
		}
		return _instance;
	}

    // Method - Public
    public final boolean reload(String questFolder)
    {
    	Quest q = getQuest(questFolder);
    	if (q == null)
			return false;
		return q.reload();
    }
    
    /**
     * Reloads a the quest given by questId.<BR>
     * <B>NOTICE: Will only work if the quest name is equal the quest folder name</B>
     * @param questId The id of the quest to be reloaded
     * @return true if reload was succesful, false otherwise
     */
    public final boolean reload(int questId)
    {
    	Quest q = this.getQuest(questId);
		if (q == null)
			return false;
		return q.reload();
    }
    
    public final void reloadAllQuests()
	{
		_log.info("Reloading Server Scripts");
		try
		{
			// unload all scripts
			for (Quest quest : _quests.values())
				if (quest != null)
					quest.unload();
			// now load all scripts
			File scripts = new File(Config.DATAPACK_ROOT + "/data/scripts.cfg");
			L2ScriptEngineManager.getInstance().executeScriptList(scripts);
			QuestManager.getInstance().report();
		}
		catch (IOException ioe)
		{
			_log.severe("Failed loading scripts.cfg, no script going to be loaded");
		}
	}
    
    public final void report()
	{
		_log.info("Loaded: " + _quests.size() + " quests");
	}
    
    public final void save()
    {
    	for(Quest q: _quests.values())
    		q.saveGlobalData();
    }

    // Property - Public
    public final Quest getQuest(String name)
    {
		return _quests.get(name);
    }

    public final Quest getQuest(int questId)
    {
    	for (Quest q: _quests.values())
    		if (q.getQuestIntId() == questId)
    			return q;
    	return null;
    }
    
	public final boolean removeQuest(Quest q)
	{
		return _quests.remove(q.getName()) != null;
	}

	public final void addQuest(Quest newQuest)
	{
		if (newQuest == null)
			throw new IllegalArgumentException("Quest argument cannot be null");
		Quest old = _quests.get(newQuest.getName());
		// FIXME: unloading the old quest at this point is a tad too late.
		// the new quest has already initialized itself and read the data, starting
		// an unpredictable number of tasks with that data. The old quest will now
		// save data which will never be read.
		// However, requesting the newQuest to re-read the data is not necessarily a
		// good option, since the newQuest may have already started timers, spawned NPCs
		// or taken any other action which it might re-take by re-reading the data.
		// the current solution properly closes the running tasks of the old quest but
		// ignores the data; perhaps the least of all evils...
		if (old != null && old.isRealQuest())
		{
			old.unload();
			_log.info("Replaced: (" + old.getName() + ") with a new version (" + newQuest.getName() + ")");
		}
		_quests.put(newQuest.getName(), newQuest);
	}

	@Override
	public Iterable<Quest> getAllManagedScripts()
	{
		return _quests.values();
	}

	@Override
	public boolean unload(Quest ms)
	{
		ms.saveGlobalData();
		return this.removeQuest(ms);
	}

	@Override
	public String getScriptManagerName()
	{
		return "QuestManager";
	}
	public static void reload()
	{
		_instance = new QuestManager();
	}
}
