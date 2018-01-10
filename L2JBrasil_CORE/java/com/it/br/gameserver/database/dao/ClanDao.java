package com.it.br.gameserver.database.dao;

import com.it.br.Config;
import com.it.br.gameserver.database.L2DatabaseFactory;
import com.it.br.gameserver.model.L2Clan;

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
public class ClanDao {
    private static final Logger _log = Logger.getLogger(ClanDao.class.getName());

    private static final String SELECT_ID = "SELECT clan_id FROM clan_data";
    private static final String DELETE_CLAN_DATA = "DELETE FROM clan_data WHERE clan_id=?";
    private static final String DELETE_CLAN_PRIVS = "DELETE FROM clan_privs WHERE clan_id=?";
    private static final String DELETE_CLAN_SKILLS = "DELETE FROM clan_skills WHERE clan_id=?";
    private static final String DELETE_CLAN_SUBPLEDGES = "DELETE FROM clan_subpledges WHERE clan_id=?";
    private static final String DELETE_CLAN_WARS = "DELETE FROM clan_wars WHERE clan1=? OR clan2=?";
    private static final String DELETE_CLAN_NOTICES = "DELETE FROM clan_notices WHERE clanID=?";
    private static final String STORE_WARS = "REPLACE INTO clan_wars (clan1, clan2, wantspeace1, wantspeace2) VALUES(?,?,?,?)";
    private static final String RESTORE_WARS = "SELECT clan1, clan2, wantspeace1, wantspeace2 FROM clan_wars";
    private static final String GET_CLAN_NAME = "SELECT clan_name FROM clan_data WHERE clan_id = (SELECT clanid FROM characters WHERE char_name = ?)";
    private static final String GET_CLAN_WITH_CASTLE = "SELECT clan_id FROM clan_data WHERE hasCastle = ?";
    private static final String DELETE_TWO_CLAN_WARS = "DELETE FROM clan_wars WHERE clan1=? AND clan2=?";

    public static List<Integer> load() {
        List<Integer> list = new ArrayList<>();

        try (Connection con = L2DatabaseFactory.getInstance().getConnection();
             PreparedStatement statement = con.prepareStatement(SELECT_ID))
        {
            ResultSet rs = statement.executeQuery();

            while (rs.next())
            {
                list.add(rs.getInt("clan_id"));
            }
        }
        catch (SQLException e)
        {
            _log.warning( ClanDao.class.getName() + ": Exception: load(): " + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }

    public static List<Integer> getClanIdByCastleId(int castleId) {
        List<Integer> list = new ArrayList<>();
        try (Connection con = L2DatabaseFactory.getInstance().getConnection();
             PreparedStatement statement = con.prepareStatement(GET_CLAN_WITH_CASTLE))
        {
            statement.setInt(1, castleId);
            ResultSet rs = statement.executeQuery();

            while (rs.next())
            {
                list.add(rs.getInt("clan_id"));
            }
        }
        catch (SQLException e)
        {
            _log.warning( ClanDao.class.getName() + ": Exception: getClanIdByCastleId(): " + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }

    public static void storeWars(int clanId1, int clanId2) {
        try (Connection con = L2DatabaseFactory.getInstance().getConnection();
             PreparedStatement statement = con.prepareStatement(STORE_WARS))
        {
            statement.setInt(1, clanId1);
            statement.setInt(2, clanId2);
            statement.setInt(3, 0);
            statement.setInt(4, 0);
            statement.execute();
            statement.close();
        }
        catch (SQLException e)
        {
            _log.warning( ClanDao.class.getName() + ": Exception: storeWars(int clanId1, int clanId2): " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void delete(L2Clan clan) {
        try(Connection con = L2DatabaseFactory.getInstance().getConnection();
            PreparedStatement st= con.prepareStatement(DELETE_CLAN_DATA);
            PreparedStatement st1 = con.prepareStatement(DELETE_CLAN_PRIVS);
            PreparedStatement st2 = con.prepareStatement(DELETE_CLAN_SKILLS);
            PreparedStatement st3 = con.prepareStatement(DELETE_CLAN_SUBPLEDGES);
            PreparedStatement st4 = con.prepareStatement(DELETE_CLAN_WARS);
            PreparedStatement st5 = con.prepareStatement(DELETE_CLAN_NOTICES))
        {

            st.setInt(1, clan.getClanId());
            st.execute();

            st1.setInt(1, clan.getClanId());
            st1.execute();

            st2.setInt(1, clan.getClanId());
            st2.execute();

            st3.setInt(1, clan.getClanId());
            st3.execute();

            st4.setInt(1, clan.getClanId());
            st4.execute();

            st5.setInt(1, clan.getClanId());
            st5.execute();

            int castleId = clan.getHasCastle();

            if(castleId != 0)
            {
                CastleDao.updateTaxPercent(castleId, 0);
            }

            if (Config.DEBUG) _log.fine("Clan " + clan.getName() + "removed in db, clan id: " + clan.getClanId());
        }
        catch (SQLException e)
        {
            _log.warning( ClanDao.class.getName() + ": Exception: delete(L2Clan): " + e.getMessage());
        }
    }

    public static void deleteWars(L2Clan clan1, L2Clan clan2) {
        try(Connection con = L2DatabaseFactory.getInstance().getConnection();
            PreparedStatement st= con.prepareStatement(DELETE_TWO_CLAN_WARS))
        {
            st.setInt(1, clan1.getClanId());
            st.setInt(1, clan2.getClanId());
            st.execute();

            if (Config.DEBUG) _log.fine("Clan " + clan1.getName() + " and " + clan2.getName() + " removed in table \"clan_wars\"");
        }
        catch (SQLException e)
        {
            _log.warning( ClanDao.class.getName() + ": Exception: deleteWars(L2Clan1, L2Clan2): " + e.getMessage());
        }
    }

    public static Map<Integer, List<Integer>> restoreWars() {
        Map<Integer, List<Integer>> listWars = new HashMap<>();
        try(Connection con = L2DatabaseFactory.getInstance().getConnection();
            PreparedStatement st= con.prepareStatement(RESTORE_WARS))
        {
            ResultSet rset = st.executeQuery();
            int count = 0;
            while(rset.next())
            {
                List<Integer> listClans = new ArrayList<>();
                listClans.add(rset.getInt("clan1"));
                listClans.add(rset.getInt("clan2"));
                listWars.put(count, listClans);
                count++;
            }
        }
        catch (SQLException e)
        {
            _log.warning( ClanDao.class.getName() + ": Exception: restoreWars(): " + e.getMessage());
        }
        return listWars;
    }

    public static String getClanName(String partyMemberName) {
        String clanName = "";
        try(Connection con = L2DatabaseFactory.getInstance().getConnection();
            PreparedStatement st= con.prepareStatement(GET_CLAN_NAME))
        {
            st.setString(1, partyMemberName);
            ResultSet rset = st.executeQuery();

            if (rset.next())
            {
                clanName = rset.getString("clan_name");
            }
        }
        catch (SQLException e)
        {
            _log.warning( ClanDao.class.getName() + ": Exception: getClanName(String partyMemberName): " + e.getMessage());
        }
        return clanName;
    }

}
