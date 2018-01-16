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
package com.it.br.gameserver.datatables.xml;

import com.it.br.configuration.settings.ServerSettings;
import com.it.br.gameserver.database.dao.NpcDao;
import com.it.br.gameserver.model.L2DropCategory;
import com.it.br.gameserver.model.L2MinionData;
import com.it.br.gameserver.model.L2Skill;
import com.it.br.gameserver.model.base.ClassId;
import com.it.br.gameserver.templates.L2NpcTemplate;
import com.it.br.gameserver.templates.StatsSet;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;

import static com.it.br.configuration.Configurator.getSettings;

public class NpcTable
{
	private static final Log _log = LogFactory.getLog(NpcTable.class.getName());

	private static NpcTable _instance;

	private Map<Integer, L2NpcTemplate> _npcs;
	private boolean _initialized = false;

	public static NpcTable getInstance()
	{
		if (_instance == null)
			_instance = new NpcTable();
		return _instance;
	}

	private NpcTable()
	{
		_npcs = new HashMap<>();
		restoreNpcData();
	}

	private void restoreNpcData()
	{
		try
		{
			NpcDao.load();
			NpcDao.loadSKills();
			NpcDao.loadCustomNpcs();
			_log.info("NpcTable: Loaded " + NpcTable.getInstance().getNpcsMap().size() + " Npc Templates.");

			NpcDao.loadCustomDropList();
			NpcDao.loadDropList();

			loadSKillLearnFromXml();
			loadMinionsFromXml();
		}
		catch (Exception e)
		{
			_log.warn("NPCTable: Error reading NPC data: " + e);
		}

		_initialized = true;
	}

	public void reloadNpc(int id)
	{

		try
		{
			// save a copy of the old data
			L2NpcTemplate old = getTemplate(id);
			Map<Integer,L2Skill> skills = new HashMap<>();

			if (old.getSkills() != null)
				skills.putAll(old.getSkills());

			List<L2DropCategory> categories = new ArrayList<>();

			if (old.getDropData() != null)
				categories.addAll(old.getDropData());

			ClassId[] classIds = null;

			if (old.getTeachInfo() != null)
				classIds=old.getTeachInfo().clone();

			List<L2MinionData> minions = new ArrayList<>();

			if (old.getMinionData() != null)
				minions.addAll(old.getMinionData());

			// reload the NPC base data
			NpcDao.loadById(id);

			// restore additional data from saved copy
			L2NpcTemplate created = getTemplate(id);

			for (L2Skill skill : skills.values())
				created.addSkill(skill);

			if (classIds != null)
				for (ClassId classId : classIds)
					created.addTeachInfo(classId);

			for (L2MinionData minion : minions)
				created.addRaidData(minion);
		}
		catch (Exception e)
		{
			_log.warn("NPCTable: Could not reload data for NPC " + id + ": " + e);
		}

	}

	private static void loadSKillLearnFromXml(){
		ServerSettings serverSettings = getSettings(ServerSettings.class);
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setValidating(false);
		factory.setIgnoringComments(true);
		int th = 0;
		File f = new File(serverSettings.getDatapackDirectory() + "/data/xml/skill_learn.xml");
		if(!f.exists())
		{
			_log.error("skill_learn.xml could not be loaded: file not found");
			_log.warn("char_template.xml could not be loaded: file not found");
			return;
		}
		try
		{
			InputSource in = new InputSource(new InputStreamReader(new FileInputStream(f), "UTF-8"));
			in.setEncoding("UTF-8");
			Document doc = factory.newDocumentBuilder().parse(in);
			for(Node n = doc.getFirstChild(); n != null; n = n.getNextSibling())
			{
				if(n.getNodeName().equalsIgnoreCase("list"))
				{
					for(Node d = n.getFirstChild(); d != null; d = d.getNextSibling())
					{
						if(d.getNodeName().equalsIgnoreCase("learn"))
						{
							int npcId = Integer.valueOf(d.getAttributes().getNamedItem("npc_id").getNodeValue());
							int classId = Integer.valueOf(d.getAttributes().getNamedItem("class_id").getNodeValue());
							L2NpcTemplate npc = NpcTable.getInstance().getNpcsMap().get(npcId);

							if(npc == null)
							{
								_log.warn("NPCTable: Error getting NPC template ID " + npcId + " while trying to load skill trainer data.");
								continue;
							}

							npc.addTeachInfo(ClassId.values()[classId]);
							th++;
						}
					}
				}
			}
		}
		catch(Exception e)
		{
			_log.error("NPCTable: Error reading NPC trainer data", e);
		}
		_log.info("NpcTable: Loaded " + th + " teachers.");
	}

	private static void loadMinionsFromXml(){
		ServerSettings serverSettings = getSettings(ServerSettings.class);

		DocumentBuilderFactory factory1 = DocumentBuilderFactory.newInstance();
		factory1.setValidating(false);
		factory1.setIgnoringComments(true);
		int cnt = 0;
		File f1 = new File(serverSettings.getDatapackDirectory() + "/data/xml/minion.xml");
		if(!f1.exists())
		{
			_log.error("minion.xml could not be loaded: file not found");
			return;
		}
		try
		{
			InputSource in1 = new InputSource(new InputStreamReader(new FileInputStream(f1), "UTF-8"));
			in1.setEncoding("UTF-8");
			Document doc1 = factory1.newDocumentBuilder().parse(in1);
			L2MinionData minionDat;
			L2NpcTemplate npcDat;
			for(Node n1 = doc1.getFirstChild(); n1 != null; n1 = n1.getNextSibling())
			{
				if(n1.getNodeName().equalsIgnoreCase("list"))
				{
					for(Node d1 = n1.getFirstChild(); d1 != null; d1 = d1.getNextSibling())
					{
						if(d1.getNodeName().equalsIgnoreCase("minion"))
						{
							int raidId = Integer.valueOf(d1.getAttributes().getNamedItem("boss_id").getNodeValue());
							int mid = Integer.valueOf(d1.getAttributes().getNamedItem("minion_id").getNodeValue());
							int mmin = Integer.valueOf(d1.getAttributes().getNamedItem("amount_min").getNodeValue());
							int mmax = Integer.valueOf(d1.getAttributes().getNamedItem("amount_max").getNodeValue());

							npcDat = NpcTable.getInstance().getNpcsMap().get(raidId);
							minionDat = new L2MinionData();

							minionDat.setMinionId(mid);
							minionDat.setAmountMin(mmin);
							minionDat.setAmountMax(mmax);
							npcDat.addRaidData(minionDat);
							cnt++;
						}
					}
				}
			}
		}
		catch(Exception e)
		{
			_log.warn("Error loading minion data" + e);
		}
		_log.info("NpcTable: Loaded " + cnt + " minions.");
	}

	// just wrapper
	public void reloadAllNpc()
	{
		restoreNpcData();
	}

	public boolean isInitialized()
	{
		return _initialized;
	}

	public void replaceTemplate(L2NpcTemplate npc)
	{
		_npcs.put(npc.npcId, npc);
	}

	public L2NpcTemplate getTemplate(int id)
	{
		return _npcs.get(id);
	}

	public Map<Integer, L2NpcTemplate> getNpcsMap()
	{
		return _npcs;
	}

	public L2NpcTemplate getTemplateByName(String name)
	{
		for (L2NpcTemplate npcTemplate : _npcs.values())
			if (npcTemplate.name.equalsIgnoreCase(name))
				return npcTemplate;

		return null;
	}

	public L2NpcTemplate[] getAllOfLevel(int lvl)
	{
		List<L2NpcTemplate> list = new ArrayList<>();

		for (L2NpcTemplate t : _npcs.values())
			if (t.level == lvl)
				list.add(t);

		return list.toArray(new L2NpcTemplate[list.size()]);
	}

	public L2NpcTemplate[] getAllMonstersOfLevel(int lvl)
	{
		List<L2NpcTemplate> list = new ArrayList<>();

		for (L2NpcTemplate t : _npcs.values())
			if (t.level == lvl && "L2Monster".equals(t.type))
				list.add(t);

		return list.toArray(new L2NpcTemplate[list.size()]);
	}

	public L2NpcTemplate[] getAllNpcStartingWith(String letter)
	{
		List<L2NpcTemplate> list = new ArrayList<>();

		for (L2NpcTemplate t : _npcs.values())
			if (t.name.startsWith(letter) && "L2Npc".equals(t.type))
				list.add(t);

		return list.toArray(new L2NpcTemplate[list.size()]);
	}
	
	public L2NpcTemplate[] getAllNpcClassType(String classType)
    {
        List <L2NpcTemplate> list = new ArrayList<>();
        
        for (L2NpcTemplate t : _npcs.values())
            if(classType.equals(t.type))
                list.add(t);

        return list.toArray(new L2NpcTemplate[list.size()]);
    }

	public Set<Integer> getAllNpcOfClassType(String classType)
	{
		return null;
	}
	
	/**
	 * @param clazz
	 * @return
	 */
	public Set<Integer> getAllNpcOfL2jClass(Class<?> clazz)
	{
		return null;
	}

	/**
	 * @param aiType
	 * @return
	 */
	public Set<Integer> getAllNpcOfAiType(String aiType)
	{
		return null;
	}

}