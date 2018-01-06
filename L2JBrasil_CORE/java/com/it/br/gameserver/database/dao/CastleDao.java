package com.it.br.gameserver.database.dao;

import com.it.br.gameserver.database.L2DatabaseFactory;

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

    public static List<Integer> loadAllCastle() {
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
            _log.warning( CastleDao.class.getName() + ": Exception: loadAllCastle(): " + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }
}
