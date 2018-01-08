package com.it.br.gameserver.database.dao;

import com.it.br.gameserver.database.L2DatabaseFactory;
import com.it.br.gameserver.model.L2Clan;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * @author Tayran
 * @version 3.0.4
 */
public class CastleDao {
    private static final Logger _log = Logger.getLogger(CastleDao.class.getName());

    private static String SELECT_ID = "SELECT id FROM castle ORDER BY id";
    private static String UPDATE_TAX_PERCENT = "UPDATE castle SET taxPercent = 0 WHERE id = ?";

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

    public static void updateTaxPercent(L2Clan clan) {
        try (Connection con = L2DatabaseFactory.getInstance().getConnection();
             PreparedStatement statement = con.prepareStatement(UPDATE_TAX_PERCENT))
        {
            statement.setInt(2, clan.getClanId());
            statement.execute();
        }
        catch (SQLException e)
        {
            _log.warning( CastleDao.class.getName() + ": Exception: updateTaxPercent(L2Clan): " + e.getMessage());
            e.printStackTrace();
        }
    }
}
