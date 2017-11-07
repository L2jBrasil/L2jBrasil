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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.it.br.Config;
import com.it.br.L2DatabaseFactory;
import com.it.br.gameserver.datatables.sql.SkillTable;
import com.it.br.gameserver.model.L2DropCategory;
import com.it.br.gameserver.model.L2DropData;
import com.it.br.gameserver.model.L2MinionData;
import com.it.br.gameserver.model.L2Skill;
import com.it.br.gameserver.model.base.ClassId;
import com.it.br.gameserver.skills.BaseStats;
import com.it.br.gameserver.skills.Stats;
import com.it.br.gameserver.templates.L2NpcTemplate;
import com.it.br.gameserver.templates.StatsSet;

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
			try(Connection con = L2DatabaseFactory.getInstance().getConnection();)
			{
				PreparedStatement statement;
				statement = con.prepareStatement("SELECT " + L2DatabaseFactory.getInstance().safetyString(new String[] {"id", "idTemplate", "name", "serverSideName", "title", "serverSideTitle", "class", "collision_radius", "collision_height", "level", "sex", "type", "attackrange", "hp", "mp", "hpreg", "mpreg", "str", "con", "dex", "int", "wit", "men", "exp", "sp", "patk", "pdef", "matk", "mdef", "atkspd", "aggro", "matkspd", "rhand", "lhand", "armor", "walkspd", "runspd", "faction_id", "faction_range", "isUndead", "absorb_level", "absorb_type"}) + " FROM npc");
				ResultSet npcdata = statement.executeQuery();

				fillNpcTable(npcdata);
				npcdata.close();
				statement.close();
			}
			catch (Exception e)
			{
				_log.warn("NPCTable: Error creating NPC table: " + e);
			}

			try(Connection con = L2DatabaseFactory.getInstance().getConnection();)
			{
				PreparedStatement statement = con.prepareStatement("SELECT npcid, skillid, level FROM npcskills");
				ResultSet npcskills = statement.executeQuery();
				L2NpcTemplate npcDat = null;
				L2Skill npcSkill = null;

				while (npcskills.next())
				{
					int mobId = npcskills.getInt("npcid");
					npcDat = _npcs.get(mobId);

					if (npcDat == null)
						continue;

					int skillId = npcskills.getInt("skillid");
					int level = npcskills.getInt("level");

					if (skillId == 4416)
					{
						npcDat.setRace(level);
						continue;
					}

					npcSkill = SkillTable.getInstance().getInfo(skillId, level);

					if (npcSkill == null)
						continue;

					npcDat.addSkill(npcSkill);
				}

				npcskills.close();
				statement.close();
			}
			catch (Exception e)
			{
				_log.warn("NPCTable: Error reading NPC skills table: " + e);
			}

			try(Connection con = L2DatabaseFactory.getInstance().getConnection();)
			{
				PreparedStatement statement;
				statement = con.prepareStatement("SELECT " + L2DatabaseFactory.getInstance().safetyString(new String[] { "id", "idTemplate", "name", "serverSideName", "title", "serverSideTitle", "class", "collision_radius", "collision_height", "level", "sex", "type", "attackrange", "hp", "mp", "hpreg", "mpreg", "str", "con", "dex", "int", "wit", "men", "exp", "sp", "patk", "pdef", "matk", "mdef", "atkspd", "aggro", "matkspd", "rhand", "lhand", "armor", "walkspd", "runspd", "faction_id", "faction_range", "isUndead", "absorb_level", "absorb_type"}) + " FROM custom_npc");
				ResultSet npcdata = statement.executeQuery();

				fillNpcTable(npcdata);
				npcdata.close();
				statement.close();
			}
			catch (Exception e)
			{
				_log.warn("NPCTable: Error creating custom NPC table: " + e);
			}

			try(Connection con = L2DatabaseFactory.getInstance().getConnection();)
			{
				PreparedStatement statement2 = con.prepareStatement("SELECT " + L2DatabaseFactory.getInstance().safetyString(new String[] { "mobId", "itemId", "min", "max", "category", "chance" }) + " FROM custom_droplist ORDER BY mobId, chance DESC");
				ResultSet dropData = statement2.executeQuery();
				L2DropData dropDat = null;
				L2NpcTemplate npcDat = null;
				int cCount = 0;
				while (dropData.next())
				{
					int mobId = dropData.getInt("mobId");
					npcDat = _npcs.get(mobId);
					if (npcDat == null)
					{
						_log.warn("NPCTable: CUSTOM DROPLIST No npc correlating with id : " + mobId);
						continue;
					}
					dropDat = new L2DropData();
					dropDat.setItemId(dropData.getInt("itemId"));
					dropDat.setMinDrop(dropData.getInt("min"));
					dropDat.setMaxDrop(dropData.getInt("max"));
					dropDat.setChance(dropData.getInt("chance"));
					int category = dropData.getInt("category");
					npcDat.addDropData(dropDat, category);
					cCount++;
				}
				dropData.close();
				statement2.close();
				_log.info("CustomDropList : Added " + cCount + " custom droplist");
			}
			catch (Exception e)
			{
				_log.warn("NPCTable: Error reading NPC CUSTOM drop data: " + e);
			}

			try(Connection con = L2DatabaseFactory.getInstance().getConnection();)
			{
				PreparedStatement statement2 = con.prepareStatement("SELECT " + L2DatabaseFactory.getInstance().safetyString(new String[] {"mobId", "itemId", "min", "max", "category", "chance"}) + " FROM droplist ORDER BY mobId, chance DESC");
				ResultSet dropData = statement2.executeQuery();
				L2DropData dropDat = null;
				L2NpcTemplate npcDat = null;

				while (dropData.next())
				{
					int mobId = dropData.getInt("mobId");
					npcDat = _npcs.get(mobId);
					if (npcDat == null)
					{
						_log.warn("NPCTable: No npc correlating with id : " + mobId);
						continue;
					}
					dropDat = new L2DropData();

					dropDat.setItemId(dropData.getInt("itemId"));
					dropDat.setMinDrop(dropData.getInt("min"));
					dropDat.setMaxDrop(dropData.getInt("max"));
					dropDat.setChance(dropData.getInt("chance"));

					int category = dropData.getInt("category");

					npcDat.addDropData(dropDat, category);
				}

				dropData.close();
				statement2.close();
			}
			catch (Exception e)
			{
				_log.warn("NPCTable: Error reading NPC drop data: " + e);
			}

			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setValidating(false);
			factory.setIgnoringComments(true);
			int th = 0;
			File f = new File(Config.DATAPACK_ROOT + "/data/xml/skill_learn.xml");
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
								L2NpcTemplate npc = _npcs.get(npcId);

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
			catch(SAXException e)
			{
				_log.error("NPCTable: Error reading NPC trainer data", e);
			}
			catch(IOException e)
			{
				_log.error("NPCTable: Error reading NPC trainer data", e);
			}
			catch(ParserConfigurationException e)
			{
				_log.error("NPCTable: Error reading NPC trainer data", e);
			}
			_log.info("NpcTable: Loaded " + th + " teachers.");

			DocumentBuilderFactory factory1 = DocumentBuilderFactory.newInstance();
			factory1.setValidating(false);
			factory1.setIgnoringComments(true);
			int cnt = 0;
			File f1 = new File(Config.DATAPACK_ROOT + "/data/xml/minion.xml");
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
				L2MinionData minionDat = null;
				L2NpcTemplate npcDat = null;
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

								npcDat = _npcs.get(raidId);
								minionDat = new L2MinionData();

								minionDat.setMinionId(mid);
								minionDat.setAmountMin(mmin);
								minionDat.setAmountMax(mmax);
								npcDat.addRaidData(minionDat);
								cnt++;
								minionDat = null;
							}
						}
					}
				}
			}
			catch(SAXException e)
			{
				_log.warn("Error loading minion data" + e);
			}
			catch(IOException e)
			{
				_log.warn("Error loading minion data" + e);
			}
			catch(ParserConfigurationException e)
			{
				_log.warn("Error loading minion data" + e);
			}
			_log.info("NpcTable: Loaded " + cnt + " minions.");
		}
		catch (Exception e)
		{
			_log.warn("NPCTable: Error reading NPC data: " + e);
		}

		_initialized = true;
	}

	private void fillNpcTable(ResultSet NpcData)
	throws Exception
	{
		while (NpcData.next())
		{
			StatsSet npcDat = new StatsSet(); 
			int id = NpcData.getInt("id");

			if (Config.ASSERT) 
				assert id < 1000000; 

			npcDat.set("npcId", id);
			npcDat.set("idTemplate",NpcData.getInt("idTemplate"));
			int level = NpcData.getInt("level");
			npcDat.set("level", level);
			npcDat.set("jClass", NpcData.getString("class"));

			npcDat.set("baseShldDef", 0);
			npcDat.set("baseShldRate", 0);
			npcDat.set("baseCritRate",  38);

			npcDat.set("name", NpcData.getString("name"));
			npcDat.set("serverSideName", NpcData.getBoolean("serverSideName"));
			//npcDat.set("name", "");
			npcDat.set("title",NpcData.getString("title"));
			npcDat.set("serverSideTitle",NpcData.getBoolean("serverSideTitle"));
			npcDat.set("collision_radius", NpcData.getDouble("collision_radius"));
			npcDat.set("collision_height", NpcData.getDouble("collision_height"));
			npcDat.set("sex", NpcData.getString("sex"));
			if (!Config.ALLOW_NPC_WALKERS && NpcData.getString("type").equalsIgnoreCase("L2NpcWalker"))
 	 	            npcDat.set("type", "L2Npc");
			else
				npcDat.set("type", NpcData.getString("type"));
			npcDat.set("baseAtkRange", NpcData.getInt("attackrange"));
			npcDat.set("rewardExp", NpcData.getInt("exp"));
			npcDat.set("rewardSp", NpcData.getInt("sp"));
			npcDat.set("basePAtkSpd", NpcData.getInt("atkspd"));
			npcDat.set("baseMAtkSpd", NpcData.getInt("matkspd"));
			npcDat.set("aggroRange", NpcData.getInt("aggro"));
			npcDat.set("rhand", NpcData.getInt("rhand"));
			npcDat.set("lhand", NpcData.getInt("lhand"));
			npcDat.set("armor", NpcData.getInt("armor"));
			npcDat.set("baseWalkSpd", NpcData.getInt("walkspd"));
			npcDat.set("baseRunSpd", NpcData.getInt("runspd"));

			// constants, until we have stats in DB
			npcDat.safeSet("baseSTR", NpcData.getInt("str"), 0, BaseStats.MAX_STAT_VALUE, "Loading npc template id: "+NpcData.getInt("idTemplate"));
			npcDat.safeSet("baseCON", NpcData.getInt("con"), 0, BaseStats.MAX_STAT_VALUE, "Loading npc template id: "+NpcData.getInt("idTemplate"));
			npcDat.safeSet("baseDEX", NpcData.getInt("dex"), 0, BaseStats.MAX_STAT_VALUE, "Loading npc template id: "+NpcData.getInt("idTemplate"));
			npcDat.safeSet("baseINT", NpcData.getInt("int"), 0, BaseStats.MAX_STAT_VALUE, "Loading npc template id: "+NpcData.getInt("idTemplate"));
			npcDat.safeSet("baseWIT", NpcData.getInt("wit"), 0, BaseStats.MAX_STAT_VALUE, "Loading npc template id: "+NpcData.getInt("idTemplate"));
			npcDat.safeSet("baseMEN", NpcData.getInt("men"), 0, BaseStats.MAX_STAT_VALUE, "Loading npc template id: "+NpcData.getInt("idTemplate"));

			/*
			npcDat.set("baseSTR", NpcData.getInt("str"));
			npcDat.set("baseCON", NpcData.getInt("con"));
			npcDat.set("baseDEX", NpcData.getInt("dex"));
			npcDat.set("baseINT", NpcData.getInt("int"));
			npcDat.set("baseWIT", NpcData.getInt("wit"));
			npcDat.set("baseMEN", NpcData.getInt("men"));
			*/

			npcDat.set("baseHpMax", NpcData.getInt("hp"));
			npcDat.set("baseCpMax", 0);
			npcDat.set("baseMpMax", NpcData.getInt("mp"));
			npcDat.set("baseHpReg", NpcData.getFloat("hpreg")>0?NpcData.getFloat("hpreg"):1.5 + ((level-1)/10.0));
			npcDat.set("baseMpReg", NpcData.getFloat("mpreg")>0?NpcData.getFloat("mpreg"):0.9 + 0.3*((level-1)/10.0));
			npcDat.set("basePAtk", NpcData.getInt("patk"));
			npcDat.set("basePDef", NpcData.getInt("pdef"));
			npcDat.set("baseMAtk", NpcData.getInt("matk"));
			npcDat.set("baseMDef", NpcData.getInt("mdef"));

			npcDat.set("factionId", NpcData.getString("faction_id"));
			npcDat.set("factionRange", NpcData.getInt("faction_range"));

			npcDat.set("isUndead", NpcData.getString("isUndead"));

			npcDat.set("absorb_level", NpcData.getString("absorb_level"));
			npcDat.set("absorb_type", NpcData.getString("absorb_type"));

			L2NpcTemplate template = new L2NpcTemplate(npcDat);
			template.addVulnerability(Stats.BOW_WPN_VULN,1);
			template.addVulnerability(Stats.BLUNT_WPN_VULN,1);
			template.addVulnerability(Stats.DAGGER_WPN_VULN,1);

			_npcs.put(id, template);
		}

		_log.info("NpcTable: Loaded " + _npcs.size() + " Npc Templates.");
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
			try(Connection con = L2DatabaseFactory.getInstance().getConnection();)
			{
				PreparedStatement st = con.prepareStatement("SELECT " + L2DatabaseFactory.getInstance().safetyString(new String[] {"id", "idTemplate", "name", "serverSideName", "title", "serverSideTitle", "class", "collision_radius", "collision_height", "level", "sex", "type", "attackrange", "hp", "mp", "hpreg", "mpreg", "str", "con", "dex", "int", "wit", "men", "exp", "sp", "patk", "pdef", "matk", "mdef", "atkspd", "aggro", "matkspd", "rhand", "lhand", "armor", "walkspd", "runspd", "faction_id", "faction_range", "isUndead", "absorb_level", "absorb_type"}) + " FROM npc WHERE id=?");
				st.setInt(1, id);
				ResultSet rs = st.executeQuery();
				fillNpcTable(rs);
				rs.close();
				st.close();
			}
			catch (Exception e)
			{
				_log.warn("NPCTable: Could not reload data for NPC " + id + ": " + e);
			}

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

	// just wrapper
	public void reloadAllNpc()
	{
		restoreNpcData();
	}

	public void saveNpc(StatsSet npc)
	{
		String query = "";

		try(Connection con = L2DatabaseFactory.getInstance().getConnection();)
		{
			Map<String, Object> set = npc.getSet();

			String name = "";
			String values = "";

			for (Object obj : set.keySet())
			{
				name = (String)obj;

				if (!name.equalsIgnoreCase("npcId"))
				{
					if (values != "")
						values += ", ";

					values += name + " = '" + set.get(name) + "'";
				}
			}

			query = "UPDATE npc SET " + values + " WHERE id = ?";
			PreparedStatement statement = con.prepareStatement(query);
			statement.setInt(1, npc.getInteger("npcId"));
			statement.execute();
			statement.close();
		}
		catch (Exception e)
		{
			_log.warn("NPCTable: Could not store new NPC data in database: " + e);
		}
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