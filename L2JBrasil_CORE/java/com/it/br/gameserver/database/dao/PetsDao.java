package com.it.br.gameserver.database.dao;

import com.it.br.gameserver.database.L2DatabaseFactory;
import com.it.br.gameserver.model.L2ItemInstance;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Logger;

public class PetsDao {
    private static final Logger _log = Logger.getLogger(PetsDao.class.getName());

    private static String DELETE = "DELETE FROM pets WHERE item_obj_id=?";

    public static void delete(L2ItemInstance item) {
        try (Connection con = L2DatabaseFactory.getInstance().getConnection();
             PreparedStatement statement = con.prepareStatement(DELETE)) {
            statement.setInt(1, item.getObjectId());
            statement.execute();
            statement.close();
        } catch (SQLException e) {
            _log.warning(ItemsDao.class.getName() + ": Exception: delete(L2ItemInstance): " + e.getMessage());
            e.printStackTrace();
        }
    }

}
