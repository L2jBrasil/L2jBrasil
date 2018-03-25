package com.it.br.gameserver.database.dao;

import com.it.br.Config;
import com.it.br.gameserver.ItemsAutoDestroy;
import com.it.br.gameserver.database.L2DatabaseFactory;
import com.it.br.gameserver.instancemanager.ItemsOnGroundManager;
import com.it.br.gameserver.model.L2ItemInstance;
import com.it.br.gameserver.model.L2World;
import com.it.br.gameserver.templates.L2EtcItemType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author Tayran
 * @version 3.0.4
 */
public class ItemsOnGroundDao {

    private static final Logger _log = LoggerFactory.getLogger(ItemsOnGroundDao.class);
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
            _log.warn(ItemsOnGroundDao.class.getName() + " : error while updating table ItemsOnGround " + e);
            e.printStackTrace();
        }
    }

    public static void selectItemsOnGround()
    {
        int count = 0;
        try(Connection con = L2DatabaseFactory.getInstance().getConnection();
            PreparedStatement statement = con.prepareStatement(SELECT_ITEMS_GROUND)) {
            ResultSet result = statement.executeQuery();
            while (result.next()) {
                L2ItemInstance item = new L2ItemInstance(result.getInt(1), result.getInt(2));
                L2World.getInstance().storeObject(item);

                if (item.isStackable() && result.getInt(3) > 1) //this check and..
                    item.setCount(result.getInt(3));

                if (result.getInt(4) > 0)            // this, are really necessary?
                    item.setEnchantLevel(result.getInt(4));

                item.getPosition().setWorldPosition(result.getInt(5), result.getInt(6), result.getInt(7));
                item.getPosition().setWorldRegion(L2World.getInstance().getRegion(item.getPosition().getWorldPosition()));
                item.getPosition().getWorldRegion().addVisibleObject(item);
                item.setDropTime(result.getLong(8));

                if (result.getLong(8) == -1)
                    item.setProtected(true);
                else
                    item.setProtected(false);

                item.setIsVisible(true);
                L2World.getInstance().addVisibleObject(item, item.getPosition().getWorldRegion(), null);

                ItemsOnGroundManager.getItems().add(item);
                count++;
                // add to ItemsAutoDestroy only items not protected
                if (!Config.LIST_PROTECTED_ITEMS.contains(item.getItemId())) {
                    if (result.getLong(8) > -1) {
                        if ((Config.AUTODESTROY_ITEM_AFTER > 0 && item.getItemType() != L2EtcItemType.HERB)
                                || (Config.HERB_AUTO_DESTROY_TIME > 0 && item.getItemType() == L2EtcItemType.HERB))
                            ItemsAutoDestroy.getInstance().addItem(item);
                    }
                }
            }
            result.close();
            if (count > 0)
                System.out.println("ItemsOnGroundManager: restored " + count + " items.");
            else
                System.out.println("Initializing ItemsOnGroundManager.");

        }
        catch (SQLException e)
        {
            _log.warn(ItemsOnGroundDao.class.getName() + " : error while select table ItemsOnGround " + e);
            e.printStackTrace();
        }
    }

    public static void deleteAllItems()
    {
        try(Connection con = L2DatabaseFactory.getInstance().getConnection();
            PreparedStatement statement = con.prepareStatement(DELETE_ALL_ITEMS_GROUND)) {
            statement.execute();
        }
        catch (SQLException e)
        {
            _log.warn(ItemsOnGroundDao.class.getName() + " : error while cleaning table ItemsOnGround using DELETE " + e);
            e.printStackTrace();
        }
    }

    public static void insert(L2ItemInstance item)
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
            _log.warn(ItemsOnGroundDao.class.getName() + " : error while inserting into table ItemsOnGround " + e);
            e.printStackTrace();
        }
    }
}
