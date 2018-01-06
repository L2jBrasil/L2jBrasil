package com.it.br.gameserver.database.dao;

import com.it.br.gameserver.database.L2DatabaseFactory;
import com.it.br.gameserver.model.L2ClanMember;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Logger;

/**
 * @author Tayran
 * @version 3.0.4
 */
public class ItemsDao {
    private static final Logger _log = Logger.getLogger(ItemsDao.class.getName());

    private static String DELETE = "DELETE FROM items WHERE owner_id = ? and item_id = ?";

    public static void delete(L2ClanMember owner, int itemId) {
        try (Connection con = L2DatabaseFactory.getInstance().getConnection();
             PreparedStatement statement = con.prepareStatement(DELETE))
        {
            statement.setInt(1, owner.getObjectId());
            statement.setInt(2, itemId);
            statement.execute();
            statement.close();
        }
        catch (SQLException e)
        {
            _log.warning( ItemsDao.class.getName() + ": Exception: delete(L2PcInstance, int itemId): " + e.getMessage());
            e.printStackTrace();
        }
    }

}
