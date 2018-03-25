package com.it.br.gameserver.database.dao;

import com.it.br.Config;
import com.it.br.gameserver.database.L2DatabaseFactory;
import com.it.br.gameserver.datatables.sql.SkillTable;
import com.it.br.gameserver.datatables.xml.NpcTable;
import com.it.br.gameserver.model.L2DropData;
import com.it.br.gameserver.model.L2Skill;
import com.it.br.gameserver.skills.BaseStats;
import com.it.br.gameserver.skills.Stats;
import com.it.br.gameserver.templates.L2NpcTemplate;
import com.it.br.gameserver.templates.StatsSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

public class NpcDao {
    private static final Logger _log = LoggerFactory.getLogger(NpcDao.class);

    private static final String LOAD = "SELECT " + L2DatabaseFactory.safetyString(new String[] {"id", "idTemplate", "name", "serverSideName", "title", "serverSideTitle", "class", "collision_radius", "collision_height", "level", "sex", "type", "attackrange", "hp", "mp", "hpreg", "mpreg", "str", "con", "dex", "int", "wit", "men", "exp", "sp", "patk", "pdef", "matk", "mdef", "atkspd", "aggro", "matkspd", "rhand", "lhand", "armor", "walkspd", "runspd", "faction_id", "faction_range", "isUndead", "absorb_level", "absorb_type"}) + " FROM npc";
    private static final String LOAD_SKILLS = "SELECT " + L2DatabaseFactory.safetyString(new String[] {  "npcid", "skillid", "level" }) + " FROM npcskills";
    private static final String LOAD_CUSTOM_NPC = "SELECT " + L2DatabaseFactory.safetyString(new String[] { "id", "idTemplate", "name", "serverSideName", "title", "serverSideTitle", "class", "collision_radius", "collision_height", "level", "sex", "type", "attackrange", "hp", "mp", "hpreg", "mpreg", "str", "con", "dex", "int", "wit", "men", "exp", "sp", "patk", "pdef", "matk", "mdef", "atkspd", "aggro", "matkspd", "rhand", "lhand", "armor", "walkspd", "runspd", "faction_id", "faction_range", "isUndead", "absorb_level", "absorb_type"}) + " FROM custom_npc";
    private static final String LOAD_CUSTOM_DROPLIST = "SELECT " + L2DatabaseFactory.safetyString(new String[] { "mobId", "itemId", "min", "max", "category", "chance" }) + " FROM custom_droplist ORDER BY mobId, chance DESC";
    private static final String LOAD_DROPLIST = "SELECT " + L2DatabaseFactory.safetyString(new String[] {"mobId", "itemId", "min", "max", "category", "chance"}) + " FROM droplist ORDER BY mobId, chance DESC";
    private static final String LOAD_ID = "SELECT " + L2DatabaseFactory.safetyString(new String[] {"id", "idTemplate", "name", "serverSideName", "title", "serverSideTitle", "class", "collision_radius", "collision_height", "level", "sex", "type", "attackrange", "hp", "mp", "hpreg", "mpreg", "str", "con", "dex", "int", "wit", "men", "exp", "sp", "patk", "pdef", "matk", "mdef", "atkspd", "aggro", "matkspd", "rhand", "lhand", "armor", "walkspd", "runspd", "faction_id", "faction_range", "isUndead", "absorb_level", "absorb_type"}) + " FROM npc WHERE id=?";


    public static void load() {
        try (Connection con = L2DatabaseFactory.getInstance().getConnection();
             PreparedStatement statement = con.prepareStatement(LOAD)) {
            ResultSet npcData = statement.executeQuery();
            fillNpcTable(npcData);
            npcData.close();
        } catch (SQLException e) {
            _log.warn(NpcDao.class.getName() + ": Exception: load(): " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void loadById(int id) {
        try (Connection con = L2DatabaseFactory.getInstance().getConnection();
             PreparedStatement statement = con.prepareStatement(LOAD_ID)) {
            statement.setInt(1, id);
            ResultSet npcData = statement.executeQuery();
            fillNpcTable(npcData);
            npcData.close();
        } catch (SQLException e) {
            _log.warn(NpcDao.class.getName() + ": Exception: loadById(): " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void loadSKills() {
        try (Connection con = L2DatabaseFactory.getInstance().getConnection();
             PreparedStatement statement = con.prepareStatement(LOAD_SKILLS)) {
            ResultSet npcskills = statement.executeQuery();
            L2NpcTemplate npcDat;
            L2Skill npcSkill;

            while (npcskills.next())
            {
                int mobId = npcskills.getInt("npcid");
                npcDat = NpcTable.getInstance().getNpcsMap().get(mobId);

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
        } catch (SQLException e) {
            _log.warn(NpcDao.class.getName() + ": Exception: loadSKills(): " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void loadCustomNpcs() {
        try (Connection con = L2DatabaseFactory.getInstance().getConnection();
             PreparedStatement statement = con.prepareStatement(LOAD_CUSTOM_NPC)) {
            ResultSet npcData = statement.executeQuery();
            fillNpcTable(npcData);
            npcData.close();

        } catch (SQLException e) {
            _log.warn(NpcDao.class.getName() + ": Exception: loadCustomNpcs(): " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void loadCustomDropList() {
        try (Connection con = L2DatabaseFactory.getInstance().getConnection();
             PreparedStatement statement = con.prepareStatement(LOAD_CUSTOM_DROPLIST)) {
            ResultSet dropData = statement.executeQuery();
            int count = fillDropList(dropData);
            _log.info("CustomDropList : Added " + count + " custom droplist");
            dropData.close();
        } catch (SQLException e) {
            _log.warn(NpcDao.class.getName() + ": Exception: loadCustomDropList(): " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void loadDropList() {
        try (Connection con = L2DatabaseFactory.getInstance().getConnection();
             PreparedStatement statement = con.prepareStatement(LOAD_DROPLIST)) {
            ResultSet dropData = statement.executeQuery();
            int count = fillDropList(dropData);
            _log.info("DropList : Added " + count + " droplist");
            dropData.close();
        } catch (SQLException e) {
            _log.warn(NpcDao.class.getName() + ": Exception: loadDropList(): " + e.getMessage());
            e.printStackTrace();
        }
    }


    public static void updateNpc(StatsSet npc) {
        String query = "";
        try(Connection con = L2DatabaseFactory.getInstance().getConnection()) {
            Map<String, Object> set = npc.getSet();

            String name;
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
        } catch (SQLException e) {
            _log.warn(NpcDao.class.getName() + ": Exception: saveNpc(): " + e.getMessage());
            e.printStackTrace();
        }
    }



    private static void fillNpcTable(ResultSet NpcData) throws SQLException
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

            NpcTable.getInstance().getNpcsMap().put(id, template);
        }
    }

    private static int fillDropList(ResultSet dropData) throws SQLException
    {
        L2DropData dropDat;
        L2NpcTemplate npcDat;
        int count = 0;
        while (dropData.next())
        {
            int mobId = dropData.getInt("mobId");
            npcDat = NpcTable.getInstance().getNpcsMap().get(mobId);
            if (npcDat == null)
            {
                _log.warn("NPCTable: DROPLIST No npc correlating with id : " + mobId);
                continue;
            }
            dropDat = new L2DropData();
            dropDat.setItemId(dropData.getInt("itemId"));
            dropDat.setMinDrop(dropData.getInt("min"));
            dropDat.setMaxDrop(dropData.getInt("max"));
            dropDat.setChance(dropData.getInt("chance"));
            int category = dropData.getInt("category");
            npcDat.addDropData(dropDat, category);
            count++;
        }
        return count;
    }


}
