package com.it.br.gameserver.database.dao;

import com.it.br.gameserver.database.L2DatabaseFactory;
import com.it.br.gameserver.model.L2Spawn;
import com.it.br.gameserver.model.actor.instance.L2BoxInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BoxesDao {
    private static final Logger _log = LoggerFactory.getLogger(BoxesDao.class);

    private static String GET_BOX_ACcESS = "SELECT spawn, charname FROM boxaccess WHERE charname=? AND spawn=?";
    private static String INSERT_GRANT = "INSERT INTO boxaccess (charname,spawn) VALUES(?,?)";
    private static String DELETE_GRANT = "DELETE FROM boxaccess WHERE charname=? AND spawn=?";
    private static String LIST_GRANT = "SELECT charname FROM boxaccess WHERE spawn=?";
    private static String GET_ITEMS = "SELECT id, spawn, npcid, drawer, itemid, name, count, enchant FROM boxes where spawn=? and npcid=? and drawer=?";

    public static boolean hasAccess(String player, L2Spawn spawn) {
        try (Connection con = L2DatabaseFactory.getInstance().getConnection();
             PreparedStatement st = con.prepareStatement(GET_BOX_ACcESS))
        {
            st.setString(1, player);
            st.setInt(2, spawn.getId());
            ResultSet rs = st.executeQuery();

            if (rs.next())
                return true;
        }
        catch (SQLException e)
        {
            _log.warn( BoxesDao.class.getName() + ": Exception: hasAccess(String player, L2Spawn spawn): " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    public static List<String> getAccess(L2Spawn spawn)
    {
        List<String> list = new ArrayList<>();
        try (Connection con = L2DatabaseFactory.getInstance().getConnection();
             PreparedStatement st = con.prepareStatement(LIST_GRANT))
        {
            st.setInt(1, spawn.getId());
            ResultSet rs = st.executeQuery();
            while (rs.next())
            {
                list.add(rs.getString("charname"));
            }
        }
        catch (SQLException e)
        {
            _log.warn( BoxesDao.class.getName() + ": Exception: getAccess(L2Spawn spawn): " + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }

    public static Set<L2BoxInstance.L2BoxItem> getItems(L2BoxInstance box, L2Spawn spawn, int npcId, String drawer)
    {
        Set<L2BoxInstance.L2BoxItem> list = new HashSet<>();
        try (Connection con = L2DatabaseFactory.getInstance().getConnection();
             PreparedStatement st = con.prepareStatement(GET_ITEMS))
        {
            st.setInt(1, spawn.getId());
            st.setInt(2, npcId);
            st.setString(3, drawer);
            ResultSet rs = st.executeQuery();
            while (rs.next())
            {
                _log.debug("found: itemid="+rs.getInt("itemid")+", count="+rs.getInt("count"));
                list.add(box.new L2BoxItem(rs.getInt("itemid"),rs.getInt("count"),rs.getString("name"),rs.getInt("id"),rs.getInt("enchant")));
            }
        }
        catch (SQLException e)
        {
            _log.warn( BoxesDao.class.getName() + ": Exception: getAccess(L2Spawn spawn): " + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }
}
