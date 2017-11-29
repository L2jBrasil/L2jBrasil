package com.it.br.gameserver.database.dao;

import com.it.br.L2DatabaseFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Logger;

/**
 * @author Tayran
 * @version 3.0.4
 */
public class VanHalterDao {
    private static Logger _log = Logger.getLogger(VanHalterDao.class.getName());
    private static String LOAD_FROM_VAN_HALTER_SPAWNLIST_BETWEEN_ID = "SELECT id, count, npc_templateid, locx, locy, locz, heading, respawn_delay FROM vanhalter_spawnlist WHERE npc_templateid BETWEEN ? AND ? ORDER BY id";
    private static String LOAD_FROM_VAN_HALTER_SPAWNLIST_BY_ID = "SELECT id, count, npc_templateid, locx, locy, locz, heading, respawn_delay FROM vanhalter_spawnlist WHERE npc_templateid = ? ORDER BY id";

    /**
     * Load from vanhalter_spawnlist by ID
     * @param id1 first id range
     * @param id2 Second id range
     * @return ResultSet
     */
    public static ResultSet loadFromSpawnListBetweenIds(int id1, int id2)
    {
        ResultSet rset = null;
        try(Connection con = L2DatabaseFactory.getInstance().getConnection();
            PreparedStatement statement = con.prepareStatement(LOAD_FROM_VAN_HALTER_SPAWNLIST_BETWEEN_ID))
        {
            statement.setInt(1, 22175);
            statement.setInt(2, 22176);
            rset = statement.executeQuery();
        }
        catch(SQLException e)
        {
            _log.warning(VanHalterDao.class.getName() + ".loadRoyals(): Spawn Royals could not be initialized: " + e);
            e.printStackTrace();
        }
        return rset;
    }

    /**
     * Load from vanhalter_spawnlist by ID
     * @return ResultSet
     */
    public static ResultSet loadFromSpawnListById(int id)
    {
        ResultSet rset = null;
        try(Connection con = L2DatabaseFactory.getInstance().getConnection();
            PreparedStatement statement = con.prepareStatement(LOAD_FROM_VAN_HALTER_SPAWNLIST_BY_ID))
        {
            statement.setInt(1, id);
            rset = statement.executeQuery();
        }
        catch(SQLException e)
        {
            _log.warning(VanHalterDao.class.getName() + ".loadRoyalCaptain(): Spawn could not be initialized: " + e);
            e.printStackTrace();
        }
        return rset;
    }

}
