package com.it.br.gameserver.database.dao;

import com.it.br.Config;
import com.it.br.L2DatabaseFactory;
import com.it.br.gameserver.model.L2ItemInstance;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Logger;

public class ItemsOnGroundDao {

    private static final Logger _log = Logger.getLogger(ItemsOnGroundDao.class.getName());
    private static final String UPDATE_ITEMS_MISC_ONLY = "UPDATE itemsonground SET drop_time=? WHERE drop_time=-1 AND equipable=0";
    private static final String UPDATE_ITEMS_ALL = "UPDATE itemsonground SET drop_time=? WHERE drop_time=-1";
    private static final String SELECT_ITEMS_GROUND = "SELECT object_id, item_id, count, enchant_level, x, y, z, drop_time, equipable FROM itemsonground";
    private static final String DELETE_ALL_ITEMS_GROUND = "DELETE FROM itemsonground";
    private static final String INSERT = "INSERT INTO itemsonground(object_id,item_id,count,enchant_level,x,y,z,drop_time,equipable) VALUES(?,?,?,?,?,?,?,?,?)";

    public static void updateItemsGroundOnLoad()
    {
        String str = !Config.DESTROY_EQUIPABLE_PLAYER_ITEM ? UPDATE_ITEMS_MISC_ONLY : UPDATE_ITEMS_ALL;
        try(Connection con = L2DatabaseFactory.getInstance().getConnection();
            PreparedStatement statement = con.prepareStatement(str))
        {
            statement.setLong(1, System.currentTimeMillis());
            statement.execute();
            statement.close();
        }
        catch (SQLException e)
        {
            _log.warning(ItemsOnGroundDao.class.getName() + " : error while updating table ItemsOnGround " + e);
            e.printStackTrace();
        }
    }

    public static ResultSet selectItemsOnGround()
    {
        ResultSet rset = null;
        try(Connection con = L2DatabaseFactory.getInstance().getConnection();
            PreparedStatement statement = con.prepareStatement(SELECT_ITEMS_GROUND)) {
            rset = statement.executeQuery();
        }
        catch (SQLException e)
        {
            _log.warning(ItemsOnGroundDao.class.getName() + " : error while select table ItemsOnGround " + e);
            e.printStackTrace();
        }
        return rset;
    }

    public static void deleteAllItems()
    {
        try(Connection con = L2DatabaseFactory.getInstance().getConnection();
            PreparedStatement statement = con.prepareStatement(DELETE_ALL_ITEMS_GROUND)) {
            statement.execute();
        }
        catch (SQLException e)
        {
            _log.warning(ItemsOnGroundDao.class.getName() + " : error while cleaning table ItemsOnGround using DELETE " + e);
            e.printStackTrace();
        }
    }

    public static void insertIntoItemsOnGround(L2ItemInstance item)
    {
        try (Connection con = L2DatabaseFactory.getInstance().getConnection();
             PreparedStatement statement = con.prepareStatement(INSERT)) {
            statement.setInt(1, item.getObjectId());
            statement.setInt(2, item.getItemId());
            statement.setInt(3, item.getCount());
            statement.setInt(4, item.getEnchantLevel());
            statement.setInt(5, item.getX());
            statement.setInt(6, item.getY());
            statement.setInt(7, item.getZ());

            if (item.isProtected())
                statement.setLong(8, -1); //item will be protected
            else
                statement.setLong(8, item.getDropTime()); //item will be added to ItemsAutoDestroy
            if (item.isEquipable())
                statement.setLong(9, 1); //set equipable
            else
                statement.setLong(9, 0);
            statement.execute();
        }
        catch (SQLException e)
        {
            _log.warning(ItemsOnGroundDao.class.getName() + " : error while inserting into table ItemsOnGround " + e);
            e.printStackTrace();
        }
    }
}
