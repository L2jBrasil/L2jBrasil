package com.it.br.gameserver.database.dao;

import com.it.br.gameserver.database.L2DatabaseFactory;
import com.it.br.gameserver.datatables.xml.NpcTable;
import com.it.br.gameserver.model.L2Spawn;
import com.it.br.gameserver.templates.L2NpcTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Tayran
 * @version 3.0.4
 */
public class VanHalterDao {
    private static Logger _log = LoggerFactory.getLogger(VanHalterDao.class);
    private static String LOAD_FROM_VAN_HALTER_SPAWNLIST_BETWEEN_ID = "SELECT id, count, npc_templateid, locx, locy, locz, heading, respawn_delay FROM vanhalter_spawnlist WHERE npc_templateid BETWEEN ? AND ? ORDER BY id";
    private static String LOAD_FROM_VAN_HALTER_SPAWNLIST_BY_ID = "SELECT id, count, npc_templateid, locx, locy, locz, heading, respawn_delay FROM vanhalter_spawnlist WHERE npc_templateid = ? ORDER BY id";

    /**
     * Load from vanhalter_spawnlist by ID
     * @param id1 first id range
     * @param id2 Second id range
     * @return List<L2Spawn>
     */
    public static List<L2Spawn> loadFromSpawnListBetweenIds(int id1, int id2)
    {
        List<L2Spawn> list = new ArrayList<>();

        try(Connection con = L2DatabaseFactory.getInstance().getConnection();
            PreparedStatement statement = con.prepareStatement(LOAD_FROM_VAN_HALTER_SPAWNLIST_BETWEEN_ID))
        {
            statement.setInt(1, id1);
            statement.setInt(2, id2);
            ResultSet rset = statement.executeQuery();

            while(rset.next())
            {
                L2Spawn spawnDat;
                L2NpcTemplate template = NpcTable.getInstance().getTemplate(rset.getInt("npc_templateid"));
                if(template != null)
                {
                    spawnDat = new L2Spawn(template);
                    spawnDat.setAmount(rset.getInt("count"));
                    spawnDat.setLocx(rset.getInt("locx"));
                    spawnDat.setLocy(rset.getInt("locy"));
                    spawnDat.setLocz(rset.getInt("locz"));
                    spawnDat.setHeading(rset.getInt("heading"));
                    spawnDat.setRespawnDelay(rset.getInt("respawn_delay"));
                    list.add(spawnDat);
                }
                else
                {
                    _log.warn(VanHalterDao.class.getName() + ".loadFromSpawnListBetweenIds: Data missing in NPC table for ID: " + rset.getInt("npc_templateid") + ".");
                }
            }

        }
        catch(Exception e)
        {
            _log.warn(VanHalterDao.class.getName() + ".loadFromSpawnListBetweenIds(): Spawn Royals could not be initialized: " + e);
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Load from vanhalter_spawnlist by ID
     * @return List<L2Spawn>
     */
    public static L2Spawn loadFromSpawnListById(int id)
    {
        L2Spawn spawnDat = null;

        try(Connection con = L2DatabaseFactory.getInstance().getConnection();
            PreparedStatement statement = con.prepareStatement(LOAD_FROM_VAN_HALTER_SPAWNLIST_BY_ID))
        {
            statement.setInt(1, id);
            ResultSet rset = statement.executeQuery();

            while (rset.next())
            {
                L2NpcTemplate template = NpcTable.getInstance().getTemplate(rset.getInt("npc_templateid"));
                if (template != null)
                {
                    spawnDat = new L2Spawn(template);
                    spawnDat.setAmount(rset.getInt("count"));
                    spawnDat.setLocx(rset.getInt("locx"));
                    spawnDat.setLocy(rset.getInt("locy"));
                    spawnDat.setLocz(rset.getInt("locz"));
                    spawnDat.setHeading(rset.getInt("heading"));
                    spawnDat.setRespawnDelay(rset.getInt("respawn_delay"));
                }
                else {
                    _log.warn(VanHalterDao.class.getName() + ".loadFromSpawnListById(int id): Data missing in NPC table for ID: " + rset.getInt("npc_templateid") + ".");
                }
            }
        } catch (Exception e) {
            _log.warn(VanHalterDao.class.getName() + ".loadFromSpawnListById(int id): Spawn could not be initialized: " + e);
            e.printStackTrace();
        }
        return spawnDat;
    }

}
