package com.it.br.gameserver.database.dao;

import com.it.br.gameserver.Announcements;
import com.it.br.gameserver.CastleUpdater;
import com.it.br.gameserver.ThreadPoolManager;
import com.it.br.gameserver.database.L2DatabaseFactory;
import com.it.br.gameserver.datatables.sql.ClanTable;
import com.it.br.gameserver.datatables.xml.DoorTable;
import com.it.br.gameserver.model.L2Clan;
import com.it.br.gameserver.model.actor.instance.L2DoorInstance;
import com.it.br.gameserver.model.entity.Castle;
import com.it.br.gameserver.network.serverpackets.PledgeShowInfoUpdate;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * @author Tayran
 * @version 3.0.4
 */
public class CastleDao {
    private static final Logger _log = Logger.getLogger(CastleDao.class.getName());

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
            _log.warning( CastleDao.class.getName() + ": Exception: load(): " + e.getMessage());
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
            _log.warning( CastleDao.class.getName() + ": Exception: load(): " + e.getMessage());
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
            _log.warning( CastleDao.class.getName() + ": Exception: loadDoor(Castle): " + e.getMessage());
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
            _log.warning( CastleDao.class.getName() + ": Exception: loadDoorUpgrade(Castle): " + e.getMessage());
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
            _log.warning( CastleDao.class.getName() + ": Exception: loadDoorUpgrade(Castle): " + e.getMessage());
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
            _log.warning( CastleDao.class.getName() + ": Exception: insertDoorUpgrade(int doorId, int hp, int pDef, int mDef): " + e.getMessage());
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
                statement.setInt(1, castle.getCastleId());
                statement.setInt(2, castle.getOwnerId());
                statement.execute();
            }
        }
        catch (SQLException e)
        {
            _log.warning( CastleDao.class.getName() + ": Exception: updateClanOwner(Castle): " + e.getMessage());
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
            _log.warning( CastleDao.class.getName() + ": Exception: updateTaxPercent(castleId, value): " + e.getMessage());
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
            _log.warning( CastleDao.class.getName() + ": Exception: updateTaxPercent(L2Clan): " + e.getMessage());
            e.printStackTrace();
        }
    }
}
