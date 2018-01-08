package com.it.br.gameserver.database.dao;

import com.it.br.gameserver.SevenSignsFestival;
import com.it.br.gameserver.database.L2DatabaseFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * @author Tayran
 * @version 3.0.4
 */
public class SevenSignsDao {
    private static final Logger _log = Logger.getLogger(SevenSignsDao.class.getName());

    private static String LOAD = "SELECT festivalId, cabal, cycle, date, score, members FROM seven_signs_festival";

    public static Map<Integer, List<Object>> load() {
        Map<Integer, List<Object>> map = new HashMap<>();
        try (Connection con = L2DatabaseFactory.getInstance().getConnection();
             PreparedStatement statement = con.prepareStatement(LOAD))
        {
            ResultSet rset = statement.executeQuery();

            int count = 0;
            while (rset.next())
            {
                List<Object> list = new ArrayList<>();
                list.add(rset.getInt("cycle"));
                list.add(rset.getInt("festivalId"));
                list.add(rset.getString("cabal"));
                list.add(rset.getString("date"));
                list.add(rset.getString("members"));
                map.put(count, list);
                count++;
            }
        }
        catch (SQLException e)
        {
            _log.warning( SevenSignsDao.class.getName() + ": Exception: load(): " + e.getMessage());
            e.printStackTrace();
        }
        return map;
    }

    public static Map<Integer, List<Integer>> selectCycle() {
        Map<Integer, List<Integer>> map = new HashMap<>();
        String query = "SELECT festival_cycle, ";

        for (int i = 0; i < SevenSignsFestival.FESTIVAL_COUNT-1; i++)
        {
            query += "accumulated_bonus" + String.valueOf(i) + ", ";
            query += "accumulated_bonus" + String.valueOf(SevenSignsFestival.FESTIVAL_COUNT -1) + " ";
            query += "FROM seven_signs_status WHERE id=0";
            try (Connection con = L2DatabaseFactory.getInstance().getConnection();
                 PreparedStatement statement = con.prepareStatement(query)) {
                ResultSet rset = statement.executeQuery();
                List<Integer> list = new ArrayList<>();

                while (rset.next())
                {
                    list.add(rset.getInt("festival_cycle"));
                    list.add(rset.getInt("accumulated_bonus"));
                    map.put(i, list);
                }
            }
            catch (SQLException e)
            {
                _log.warning( SevenSignsDao.class.getName() + ": Exception: selectCycle(): " + e.getMessage());
                e.printStackTrace();
            }
        }
        return map;
    }

}
