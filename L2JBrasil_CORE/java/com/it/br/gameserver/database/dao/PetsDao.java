package com.it.br.gameserver.database.dao;

import com.it.br.gameserver.database.L2DatabaseFactory;
import com.it.br.gameserver.datatables.xml.L2PetDataTable;
import com.it.br.gameserver.idfactory.IdFactory;
import com.it.br.gameserver.model.L2ItemInstance;
import com.it.br.gameserver.model.actor.instance.L2BabyPetInstance;
import com.it.br.gameserver.model.actor.instance.L2PcInstance;
import com.it.br.gameserver.model.actor.instance.L2PetInstance;
import com.it.br.gameserver.templates.L2NpcTemplate;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Logger;

/**
 * @author Tayran
 * @version 3.0.4
 */
public class PetsDao {
    private static final Logger _log = Logger.getLogger(PetsDao.class.getName());

    private static final String DELETE = "DELETE FROM pets WHERE item_obj_id=?";
    private static final String UPDATE_OBJ_ID = "UPDATE pets SET item_obj_id = ? WHERE item_obj_id = ?";
    private static final String SELECT_PET_NAME = "SELECT name FROM pets p, items i WHERE p.item_obj_id = i.object_id AND name=? AND i.item_id IN (?)";
    private static final String RESTORE = "SELECT item_obj_id, name, level, curHp, curMp, exp, sp, karma, pkkills, fed FROM pets WHERE item_obj_id=?";
    private static final String INSERT = "INSERT INTO pets (name,level,curHp,curMp,exp,sp,karma,pkkills,fed,item_obj_id) VALUES (?,?,?,?,?,?,?,?,?,?)";
    private static final String UPDATE = "UPDATE pets SET name=?,level=?,curHp=?,curMp=?,exp=?,sp=?,karma=?,pkkills=?,fed=? WHERE item_obj_id = ?";

    public static void delete(L2ItemInstance item) {
        try (Connection con = L2DatabaseFactory.getInstance().getConnection();
             PreparedStatement statement = con.prepareStatement(DELETE)) {
            statement.setInt(1, item.getObjectId());
            statement.execute();
            statement.close();
        } catch (SQLException e) {
            _log.warning(PetsDao.class.getName() + ": Exception: delete(L2ItemInstance): " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static L2PetInstance restore(L2ItemInstance control, L2NpcTemplate template, L2PcInstance owner)
    {
        try (Connection con = L2DatabaseFactory.getInstance().getConnection();
             PreparedStatement statement = con.prepareStatement(RESTORE)) {
            statement.setInt(1, control.getObjectId());
            ResultSet rset = statement.executeQuery();

            L2PetInstance pet;
            if (template.type.compareToIgnoreCase("L2BabyPet")==0) {
                pet = new L2BabyPetInstance(IdFactory.getInstance().getNextId(), template, owner, control);
            }
            else {
                pet = new L2PetInstance(IdFactory.getInstance().getNextId(), template, owner, control);
            }

            if (!rset.next())
            {
                statement.close();
                return pet;
            }

            pet.setRespawned(true);
            pet.setName(rset.getString("name"));

            pet.getStat().setLevel(rset.getByte("level"));
            pet.getStat().setExp(rset.getLong("exp"));
            pet.getStat().setSp(rset.getInt("sp"));

            pet.getStatus().setCurrentHp(rset.getDouble("curHp"));
            pet.getStatus().setCurrentMp(rset.getDouble("curMp"));
            pet.getStatus().setCurrentCp(pet.getMaxCp());

            pet.setKarma(rset.getInt("karma"));
            pet.setPkKills(rset.getInt("pkkills"));
            pet.setCurrentFed(rset.getInt("fed"));
            statement.executeUpdate();
            statement.close();
            return pet;
        } catch (SQLException e) {
            _log.warning(PetsDao.class.getName() + ": Exception: updateObjId(int newObjectId, int oldObjectId): " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    public static void insert(L2PetInstance pet) {

        String req;
        if (!pet.isRespawned())
            req = INSERT;
        else
            req = UPDATE;

        try (Connection con = L2DatabaseFactory.getInstance().getConnection();
             PreparedStatement statement = con.prepareStatement(req)) {
            statement.setString(1, pet.getName());
            statement.setInt(2, pet.getStat().getLevel());
            statement.setDouble(3, pet.getStatus().getCurrentHp());
            statement.setDouble(4, pet.getStatus().getCurrentMp());
            statement.setLong(5, pet.getStat().getExp());
            statement.setInt(6, pet.getStat().getSp());
            statement.setInt(7, pet.getKarma());
            statement.setInt(8, pet.getPkKills());
            statement.setInt(9, pet.getCurrentFed());
            statement.setInt(10, pet.getControlItemId());

            statement.executeUpdate();
            statement.close();
        } catch (SQLException e) {
            _log.warning(PetsDao.class.getName() + ": Exception: delete(L2ItemInstance): " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void updateObjId(int newObjectId, int oldObjectId) {
        try (Connection con = L2DatabaseFactory.getInstance().getConnection();
             PreparedStatement statement = con.prepareStatement(UPDATE_OBJ_ID)) {
            statement.setInt(1, newObjectId);
            statement.setInt(2, oldObjectId);
            statement.executeUpdate();
            statement.close();
        } catch (SQLException e) {
            _log.warning(PetsDao.class.getName() + ": Exception: updateObjId(int newObjectId, int oldObjectId): " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static boolean doesPetNameExist(String name, int petNpcId)
    {
        boolean result = true;
        try (Connection con = L2DatabaseFactory.getInstance().getConnection();
             PreparedStatement statement = con.prepareStatement(SELECT_PET_NAME)) {
            statement.setString(1, name);

            StringBuilder cond = new StringBuilder("");
            for (int it : L2PetDataTable.getPetItemsAsNpc(petNpcId))
            {
                if (cond.toString().equals(""))
                    cond.append(", ");

                cond.append(it);
            }
            statement.setString(2, cond.toString());
            ResultSet rset = statement.executeQuery();
            result = rset.next();
            statement.close();
        } catch (SQLException e) {
            _log.warning(PetsDao.class.getName() + ": Exception: doesPetNameExist(String name, int petNpcId): " + e.getMessage());
            e.printStackTrace();
        }
        return result;
    }


}
