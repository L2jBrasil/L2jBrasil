package com.it.br.gameserver.database.dao;

import com.it.br.gameserver.CastleUpdater;
import com.it.br.gameserver.ThreadPoolManager;
import com.it.br.gameserver.database.L2DatabaseFactory;
import com.it.br.gameserver.datatables.sql.ClanTable;
import com.it.br.gameserver.datatables.xml.DoorTable;
import com.it.br.gameserver.datatables.xml.NpcTable;
import com.it.br.gameserver.model.L2Clan;
import com.it.br.gameserver.model.L2Spawn;
import com.it.br.gameserver.model.actor.instance.L2DoorInstance;
import com.it.br.gameserver.model.entity.Castle;
import com.it.br.gameserver.templates.L2NpcTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * @author Tayran
 * @version 3.0.4
 */
public class CastleDao {
    private static final Logger _log = LoggerFactory.getLogger(CastleDao.class);

    private static final String SELECT_ID = "SELECT id FROM castle ORDER BY id";
    private static final String SELECT_BY_ID = "SELECT * FROM castle WHERE id = ?";
    private static final String UPDATE_TAX_PERCENT = "UPDATE castle SET taxPercent = ? WHERE id = ?";
    private static final String UPDATE_TREASURY_NO_TAX = "UPDATE castle SET treasury = ? WHERE id = ?";
    private static final String SELECT_DOORS = "SELECT * FROM castle_door WHERE castleId = ?";
    private static final String SELECT_DOORUPGRADE = "SELECT * FROM castle_doorupgrade WHERE doorId IN (SELECT Id FROM castle_door WHERE castleId = ?)";
    private static final String DELETE_DOORUPGRADE = "DELETE FROM castle_doorupgrade WHERE doorId IN (SELECT id FROM castle_door WHERE castleId=?)";
    private static final String INSERT_DOORUPGRADE = "INSERT INTO castle_doorupgrade (doorId, hp, pDef, mDef) VALUES (?,?,?,?)";
    private static final String UPDATE_CLAN_DATA_SET_HAS_CASTLE_0_WHERE_HAS_CASTLE = "UPDATE clan_data SET hasCastle=0 WHERE hasCastle=?";
    private static final String UPDATE_CLAN_DATA_SET_HAS_CASTLE_WHERE_CLAN_ID = "UPDATE clan_data SET hasCastle=? WHERE clan_id=?";
    private static final String UPDATE_CASTLE_SET_SHOW_NPC_CREST_WHERE_ID = "UPDATE castle SET showNpcCrest = ? WHERE id = ?";
    private static final String DELETE_CASTLE_SIEGE_GUARDS_WHERE_NPC_ID_AND_X_AND_Y_AND_Z_AND_IS_HIRED_1 = "Delete From castle_siege_guards Where npcId = ? And x = ? AND y = ? AND z = ? AND isHired = 1";
    private static final String DELETE_CASTLE_SIEGE_GUARDS_WHERE_CASTLE_ID_AND_IS_HIRED_1 = "Delete From castle_siege_guards Where castleId = ? And isHired = 1";
    private static final String SELECT_CASTLE_SIEGE_GUARDS_WHERE_CASTLE_ID_AND_IS_HIRED = "SELECT * FROM castle_siege_guards Where castleId = ? And isHired = ?";
    private static final String INSERT_CASTLE_SIEGE_GUARDS = "Insert Into castle_siege_guards (castleId, npcId, x, y, z, heading, respawnDelay, isHired) Values (?, ?, ?, ?, ?, ?, ?, ?)";

    public static List<Integer> load() {
        List<Integer> list = new ArrayList<>();

        try (Connection con = L2DatabaseFactory.getInstance().getConnection();
             PreparedStatement statement = con.prepareStatement(SELECT_ID))
        {
            ResultSet rs = statement.executeQuery();

            while (rs.next())
            {
                list.add(rs.getInt("id"));
            }
        }
        catch (SQLException e)
        {
            _log.warn( CastleDao.class.getName() + ": Exception: load(): " + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }

    public static void loadById(Castle castle) {
        try (Connection con = L2DatabaseFactory.getInstance().getConnection();
             PreparedStatement statement = con.prepareStatement(SELECT_BY_ID))
        {
            statement.setInt(1, castle.getCastleId());
            ResultSet rs = statement.executeQuery();


            while (rs.next())
            {
                castle.setName(rs.getString("name"));

                castle.setSiegeDate(Calendar.getInstance());
                castle.getSiegeDate().setTimeInMillis(rs.getLong("siegeDate"));

                castle.setSiegeDayOfWeek(rs.getInt("siegeDayOfWeek"));
                if (castle.getSiegeDayOfWeek() < 1 || castle.getSiegeDayOfWeek() > 7)
                    castle.setSiegeDayOfWeek(7);

                castle.setSiegeHourOfDay(rs.getInt("siegeHourOfDay"));
                if (castle.getSiegeHourOfDay() < 0 || castle.getSiegeHourOfDay() > 23)
                    castle.setSiegeHourOfDay(20);

                castle.setTaxPercent(rs.getInt("taxPercent"));
                castle.setTreasury(rs.getInt("treasury"));
                castle.setShowNpcCrest(rs.getBoolean("showNpcCrest"));
            }
            castle.setTaxRate(castle.getTaxPercent() / 100.0);

            List<Integer> clanId = ClanDao.getClanIdByCastleId(castle.getCastleId());
            if(!clanId.isEmpty())
                castle.setOwnerId(clanId.get(0));

            if (castle.getOwnerId() > 0)
            {
                L2Clan clan = ClanTable.getInstance().getClan(castle.getOwnerId());                        // Try to find clan instance
                ThreadPoolManager.getInstance().scheduleGeneral(new CastleUpdater(clan, 1), 3600000);     // Schedule owner tasks to start running
            }
        }
        catch (SQLException e)
        {
            _log.warn( CastleDao.class.getName() + ": Exception: loadById(Castle castle): " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void loadDoor(Castle castle) {
        try (Connection con = L2DatabaseFactory.getInstance().getConnection();
             PreparedStatement statement = con.prepareStatement(SELECT_DOORS))
        {
            statement.setInt(1, castle.getCastleId());
            ResultSet rs = statement.executeQuery();

            while (rs.next())
            {
                // Create list of the door default for use when respawning dead doors
                castle.getDoorDefault().add(rs.getString("name")
                        + ";" + rs.getInt("id")
                        + ";" + rs.getInt("x")
                        + ";" + rs.getInt("y")
                        + ";" + rs.getInt("z")
                        + ";" + rs.getInt("range_xmin")
                        + ";" + rs.getInt("range_ymin")
                        + ";" + rs.getInt("range_zmin")
                        + ";" + rs.getInt("range_xmax")
                        + ";" + rs.getInt("range_ymax")
                        + ";" + rs.getInt("range_zmax")
                        + ";" + rs.getInt("hp")
                        + ";" + rs.getInt("pDef")
                        + ";" + rs.getInt("mDef"));

                L2DoorInstance door = DoorTable.parseList(castle.getDoorDefault().get(castle.getDoorDefault().size() - 1));
                door.spawnMe(door.getX(), door.getY(),door.getZ());
                castle.getDoors().add(door);
                DoorTable.getInstance().putDoor(door);
            }
        }
        catch (SQLException e)
        {
            _log.warn( CastleDao.class.getName() + ": Exception: loadDoor(Castle): " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void loadDoorUpgrade(Castle castle) {
        try (Connection con = L2DatabaseFactory.getInstance().getConnection();
             PreparedStatement statement = con.prepareStatement(SELECT_DOORUPGRADE))
        {
            statement.setInt(1, castle.getCastleId());
            ResultSet rs = statement.executeQuery();

            while (rs.next())
            {
                castle.upgradeDoor(rs.getInt("id"), rs.getInt("hp"), rs.getInt("pDef"), rs.getInt("mDef"));
            }
        }
        catch (SQLException e)
        {
            _log.warn( CastleDao.class.getName() + ": Exception: loadDoorUpgrade(Castle): " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void removeDoorUpgrade(Castle castle) {
        try (Connection con = L2DatabaseFactory.getInstance().getConnection();
             PreparedStatement statement = con.prepareStatement(DELETE_DOORUPGRADE))
        {
            statement.setInt(1, castle.getCastleId());
            statement.execute();
            statement.close();
        }
        catch (SQLException e)
        {
            _log.warn( CastleDao.class.getName() + ": Exception: loadDoorUpgrade(Castle): " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void insertDoorUpgrade(int doorId, int hp, int pDef, int mDef)
    {
        try (Connection con = L2DatabaseFactory.getInstance().getConnection();
             PreparedStatement statement = con.prepareStatement(INSERT_DOORUPGRADE))
        {
            statement.setInt(1, doorId);
            statement.setInt(2, hp);
            statement.setInt(3, pDef);
            statement.setInt(4, mDef);
            statement.execute();
        }
        catch (SQLException e)
        {
            _log.warn( CastleDao.class.getName() + ": Exception: insertDoorUpgrade(int doorId, int hp, int pDef, int mDef): " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void updateClanOwner(Castle castle) {
        try (Connection con = L2DatabaseFactory.getInstance().getConnection();
             PreparedStatement statement = con.prepareStatement(UPDATE_CLAN_DATA_SET_HAS_CASTLE_0_WHERE_HAS_CASTLE))
        {
            statement.setInt(1, castle.getCastleId());

            statement.execute();

            try(PreparedStatement st2 = con.prepareStatement(UPDATE_CLAN_DATA_SET_HAS_CASTLE_WHERE_CLAN_ID)) {
                st2.setInt(1, castle.getCastleId());
                st2.setInt(2, castle.getOwnerId());
                st2.execute();
            }
        }
        catch (SQLException e)
        {
            _log.warn( CastleDao.class.getName() + ": Exception: updateClanOwner(Castle): " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void updateTaxPercent(int castleId, int value) {
        try (Connection con = L2DatabaseFactory.getInstance().getConnection();
             PreparedStatement statement = con.prepareStatement(UPDATE_TAX_PERCENT))
        {
            statement.setInt(1, value);
            statement.setInt(2, castleId);
            statement.execute();
        }
        catch (SQLException e)
        {
            _log.warn( CastleDao.class.getName() + ": Exception: updateTaxPercent(castleId, value): " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void updateTreasuryTax(int trerasury, int castleId) {
        try (Connection con = L2DatabaseFactory.getInstance().getConnection();
             PreparedStatement statement = con.prepareStatement(UPDATE_TREASURY_NO_TAX))
        {
            statement.setInt(1, trerasury);
            statement.setInt(2, castleId);
            statement.execute();
        }
        catch (SQLException e)
        {
            _log.warn( CastleDao.class.getName() + ": Exception: updateTreasuryTax(int trerasury, int castleId): " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void updateShowNpcCrest(Castle castle) {
        try (Connection con = L2DatabaseFactory.getInstance().getConnection();
             PreparedStatement statement = con.prepareStatement(UPDATE_CASTLE_SET_SHOW_NPC_CREST_WHERE_ID))
        {
            statement.setString(1, String.valueOf(castle.getShowNpcCrest()));
            statement.setInt(2, castle.getCastleId());
            statement.execute();
        }
        catch (SQLException e)
        {
            _log.warn( CastleDao.class.getName() + ": Exception: updateShowNpcCrest(Castle): " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void removeMerc(int npcId, int x, int y, int z)
    {
        try (Connection con = L2DatabaseFactory.getInstance().getConnection();
             PreparedStatement statement = con.prepareStatement(DELETE_CASTLE_SIEGE_GUARDS_WHERE_NPC_ID_AND_X_AND_Y_AND_Z_AND_IS_HIRED_1))
        {
            statement.setInt(1, npcId);
            statement.setInt(2, x);
            statement.setInt(3, y);
            statement.setInt(4, z);
            statement.execute();
        }
        catch (SQLException e)
        {
            _log.warn( CastleDao.class.getName() + ": Exception: removeMerc(int npcId, int x, int y, int z): " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void removeMercs(Castle castle)
    {
        try (Connection con = L2DatabaseFactory.getInstance().getConnection();
             PreparedStatement statement = con.prepareStatement(DELETE_CASTLE_SIEGE_GUARDS_WHERE_CASTLE_ID_AND_IS_HIRED_1))
        {
            statement.setInt(1, castle.getCastleId());
            statement.execute();
        }
        catch (SQLException e)
        {
            _log.warn( CastleDao.class.getName() + ": Exception: removeMercs(Castle): " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static List<L2Spawn> loadSiegeGuard(Castle castle)
    {
        L2Spawn spawn;
        List<L2Spawn> list = new ArrayList<>();
        try (Connection con = L2DatabaseFactory.getInstance().getConnection();
             PreparedStatement statement = con.prepareStatement(SELECT_CASTLE_SIEGE_GUARDS_WHERE_CASTLE_ID_AND_IS_HIRED))
        {
            statement.setInt(1, castle.getCastleId());
            if (castle.getOwnerId() > 0)   // If castle is owned by a clan, then don't spawn default guards
            {
                statement.setInt(2, 1);
            }
            else {
                statement.setInt(2, 0);
            }
            ResultSet rs = statement.executeQuery();

            while (rs.next())
            {
                L2NpcTemplate template = NpcTable.getInstance().getTemplate(rs.getInt("npcId"));
                if (template != null)
                {
                    spawn = new L2Spawn(template);
                    spawn.setId(rs.getInt("id"));
                    spawn.setAmount(1);
                    spawn.setLocx(rs.getInt("x"));
                    spawn.setLocy(rs.getInt("y"));
                    spawn.setLocz(rs.getInt("z"));
                    spawn.setHeading(rs.getInt("heading"));
                    spawn.setRespawnDelay(rs.getInt("respawnDelay"));
                    spawn.setLocation(0);

                    list.add(spawn);
                }
                else
                {
                    _log.warn("Missing npc data in npc table for id: " + rs.getInt("npcId"));
                }
            }

        }
        catch (Exception e)
        {
            _log.warn( CastleDao.class.getName() + ": Exception: loadSiegeGuard(Castle): " + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }

    public static void saveSiegeGuard(Castle castle, int x, int y, int z, int heading, int npcId, int isHire)
    {
        try (Connection con = L2DatabaseFactory.getInstance().getConnection();
             PreparedStatement statement = con.prepareStatement(INSERT_CASTLE_SIEGE_GUARDS))
        {
            statement.setInt(1, castle.getCastleId());
            statement.setInt(2, npcId);
            statement.setInt(3, x);
            statement.setInt(4, y);
            statement.setInt(5, z);
            statement.setInt(6, heading);
            if (isHire == 1)
                statement.setInt(7, 0);
            else
                statement.setInt(7, 600);
            statement.setInt(8, isHire);
            statement.execute();
        }
        catch (SQLException e)
        {
            _log.warn( CastleDao.class.getName() + ": Exception: saveSiegeGuard(Castle castle, int x, int y, int z, int heading, int npcId, int isHire): " + e.getMessage());
            e.printStackTrace();
        }
    }
}
