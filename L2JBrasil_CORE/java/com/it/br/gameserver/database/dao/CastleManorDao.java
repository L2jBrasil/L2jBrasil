package com.it.br.gameserver.database.dao;

import com.it.br.gameserver.database.L2DatabaseFactory;
import com.it.br.gameserver.instancemanager.CastleManager;
import com.it.br.gameserver.instancemanager.CastleManorManager;
import com.it.br.gameserver.model.entity.Castle;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class CastleManorDao {
    private static final Logger _log = Logger.getLogger(CastleManorDao.class.getName());

    private static final String DELETE_PRODUCTION = "DELETE FROM castle_manor_production WHERE castle_id=?;";
    private static final String DELETE_PRODUCTION_PERIOD = "DELETE FROM castle_manor_production WHERE castle_id=? AND period=?;";
    private static final String DELETE_PROCURE = "DELETE FROM castle_manor_procure WHERE castle_id=?;";
    private static final String DELETE_PROCURE_PERIOD = "DELETE FROM castle_manor_procure WHERE castle_id=? AND period=?;";
    private static final String UPDATE_CROP = "UPDATE castle_manor_procure SET can_buy=? WHERE crop_id=? AND castle_id=? AND period=?";
    private static final String UPDATE_SEED = "UPDATE castle_manor_production SET can_produce=? WHERE seed_id=? AND castle_id=? AND period=?";
    private static final String LOAD_PROCURE = "SELECT * FROM castle_manor_procure WHERE castle_id=?";
    private static final String LOAD_PRODUCTION ="SELECT * FROM castle_manor_production WHERE castle_id=?";

    public static void saveSeed(Castle castle) {
        try (Connection con = L2DatabaseFactory.getInstance().getConnection();
             PreparedStatement statement = con.prepareStatement(DELETE_PRODUCTION))
        {
            statement.setInt(1, castle.getCastleId());
            statement.execute();

            if (castle.getSeedProduction(0) != null)
            {
                int count = 0;
                String query = "INSERT INTO castle_manor_production VALUES ";
                String values[] = new String[castle.getSeedProduction(0).size()];
                for(CastleManorManager.SeedProduction s : castle.getSeedProduction(0))
                {
                    values[count] = "("+ castle.getCastleId() +","+s.getId()+","+s.getCanProduce()+","+s.getStartProduce()+","+s.getPrice()+","+CastleManorManager.PERIOD_CURRENT+")";
                    count++;
                }
                if (values.length > 0)
                {
                    query += values[0];
                    for (int i=1; i<values.length; i++)
                    {
                        query += "," + values[i];
                    }
                    try(PreparedStatement st2 = con.prepareStatement(query)) {
                        st2.execute();
                    }
                }
            }

            if (castle.getSeedProduction(1)  != null)
            {
                int count = 0;
                String query = "INSERT INTO castle_manor_production VALUES ";
                String values[] = new String[castle.getSeedProduction(1).size()];
                for(CastleManorManager.SeedProduction s : castle.getSeedProduction(1))
                {
                    values[count] = "("+castle.getCastleId()+","+s.getId()+","+s.getCanProduce()+","+s.getStartProduce()+","+s.getPrice()+","+CastleManorManager.PERIOD_NEXT+")";
                    count++;
                }
                if (values.length > 0)
                {
                    query += values[0];
                    for (int i=1;i<values.length;i++)
                    {
                        query += "," + values[i];
                    }
                    try(PreparedStatement st3 = con.prepareStatement(query)) {
                        st3.execute();
                    }
                }
            }
        }
        catch (SQLException e)
        {
            _log.warning( ClanDao.class.getName() + ": Exception: saveSeed(Castle): " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void saveSeed(Castle castle, int period) {
        try (Connection con = L2DatabaseFactory.getInstance().getConnection();
             PreparedStatement statement = con.prepareStatement(DELETE_PRODUCTION_PERIOD))
        {
            statement.setInt(1, castle.getCastleId());
            statement.setInt(2, period);
            statement.execute();

            List<CastleManorManager.SeedProduction> prod = castle.getSeedProduction(period);

            if (prod != null)
            {
                int count = 0;
                String query = "INSERT INTO castle_manor_production VALUES ";
                String values[] = new String[prod.size()];
                for(CastleManorManager.SeedProduction s : prod)
                {
                    values[count] = "("+castle.getCastleId()+","+s.getId()+","+s.getCanProduce()+","+s.getStartProduce()+","+s.getPrice()+","+period+")";
                    count++;
                }
                if (values.length > 0)
                {
                    query += values[0];
                    for (int i=1;i<values.length;i++)
                    {
                        query += "," + values[i];
                    }
                    try(PreparedStatement st2 = con.prepareStatement(query)) {
                        st2.execute();
                    }
                }
            }
        }
        catch (SQLException e)
        {
            _log.warning( ClanDao.class.getName() + ": Exception: saveSeed(Castle, period): " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void saveCrop(Castle castle) {
        try (Connection con = L2DatabaseFactory.getInstance().getConnection();
             PreparedStatement statement = con.prepareStatement(DELETE_PROCURE))
        {
            statement.setInt(1, castle.getCastleId());
            statement.execute();
            if (castle.getCropProcure(0) != null)
            {
                int count = 0;
                String query = "INSERT INTO castle_manor_procure VALUES ";
                String values[] = new String[castle.getCropProcure(0).size()];
                for (CastleManorManager.CropProcure cp : castle.getCropProcure(0))
                {
                    values[count] = "("+castle.getCastleId()+","+cp.getId()+","+cp.getAmount()+","+cp.getStartAmount()+","+cp.getPrice()+","+cp.getReward()+","+CastleManorManager.PERIOD_CURRENT+")";
                    count++;
                }
                if (values.length > 0)
                {
                    query += values[0];
                    for (int i=1;i<values.length;i++)
                    {
                        query += "," + values[i];
                    }
                    try(PreparedStatement st2 = con.prepareStatement(query)) {
                        st2.execute();
                    }
                }
            }
            if (castle.getCropProcure(1) != null)
            {
                int count = 0;
                String query = "INSERT INTO castle_manor_procure VALUES ";
                String values[] = new String[castle.getCropProcure(1).size()];
                for (CastleManorManager.CropProcure cp : castle.getCropProcure(1))
                {
                    values[count] = "("+castle.getCastleId()+","+cp.getId()+","+cp.getAmount()+","+cp.getStartAmount()+","+cp.getPrice()+","+cp.getReward()+","+CastleManorManager.PERIOD_NEXT+")";
                    count++;
                }
                if (values.length > 0)
                {
                    query += values[0];
                    for (int i=1;i<values.length;i++)
                    {
                        query += "," + values[i];
                    }
                    try(PreparedStatement st2 = con.prepareStatement(query)) {
                        st2.execute();
                    }
                }
            }
        }
        catch (SQLException e)
        {
            _log.warning( ClanDao.class.getName() + ": Exception: saveCrop(Castle): " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void saveCrop(Castle castle, int period) {
        try (Connection con = L2DatabaseFactory.getInstance().getConnection();
             PreparedStatement statement = con.prepareStatement(DELETE_PROCURE_PERIOD))
        {
            statement.setInt(1, castle.getCastleId());
            statement.setInt(2, period);
            statement.execute();

            List<CastleManorManager.CropProcure> proc = castle.getCropProcure(period);

            if (proc != null)
            {
                int count = 0;
                String query = "INSERT INTO castle_manor_procure VALUES ";
                String values[] = new String[proc.size()];

                for (CastleManorManager.CropProcure cp : proc)
                {
                    values[count] = "("+castle.getCastleId()+","+cp.getId()+","+cp.getAmount()+","+cp.getStartAmount()+","+cp.getPrice()+","+cp.getReward()+","+period+")";
                    count++;
                }
                if (values.length > 0)
                {
                    query += values[0];
                    for (int i=1;i<values.length;i++)
                    {
                        query += "," + values[i];
                    }
                    try(PreparedStatement st2 = con.prepareStatement(query)) {
                        st2.execute();
                    }
                }
            }
        }
        catch (SQLException e)
        {
            _log.warning( ClanDao.class.getName() + ": Exception: saveCrop(Castle, period): " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void updateCrop(Castle castle, int cropId, int amount, int period) {
        try (Connection con = L2DatabaseFactory.getInstance().getConnection();
             PreparedStatement statement = con.prepareStatement(UPDATE_CROP))
        {
            statement.setInt(1, amount);
            statement.setInt(2, cropId);
            statement.setInt(3, castle.getCastleId());
            statement.setInt(4, period);
            statement.execute();
        }
        catch (SQLException e)
        {
            _log.warning( ClanDao.class.getName() + ": Exception: updateCrop(Castle, cropId, amount, period): " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void updateSeed(Castle castle, int seedId, int amount, int period) {
        try (Connection con = L2DatabaseFactory.getInstance().getConnection();
             PreparedStatement statement = con.prepareStatement(UPDATE_SEED))
        {
            statement.setInt(1, amount);
            statement.setInt(2, seedId);
            statement.setInt(3, castle.getCastleId());
            statement.setInt(4, period);
            statement.execute();
        }
        catch (SQLException e)
        {
            _log.warning( ClanDao.class.getName() + ": Exception: updateSeed(Castle, seedId, amount, period): " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static List<List<CastleManorManager.SeedProduction>>  loadProduction(Castle castle, CastleManorManager manorManager) {
        List<List<CastleManorManager.SeedProduction>> list = new ArrayList<>();
        List<CastleManorManager.SeedProduction> production = new ArrayList<>();
        List<CastleManorManager.SeedProduction> productionNext = new ArrayList<>();
        try (Connection con = L2DatabaseFactory.getInstance().getConnection();
             PreparedStatement statement = con.prepareStatement(LOAD_PRODUCTION))
        {
            statement.setInt(1, castle.getCastleId());
            ResultSet rs = statement.executeQuery();
            while(rs.next()) {
                int seedId = rs.getInt("seed_id");
                int canProduce = rs.getInt("can_produce");
                int startProduce = rs.getInt("start_produce");
                int price = rs.getInt("seed_price");
                int period = rs.getInt("period");
                if (period == CastleManorManager.PERIOD_CURRENT)
                    production.add(manorManager.new SeedProduction(seedId,canProduce,price,startProduce));
                else
                    productionNext.add(manorManager.new SeedProduction(seedId,canProduce,price,startProduce));
            }

        }
        catch (SQLException e)
        {
            _log.warning( ClanDao.class.getName() + ": Exception: loadProduction(Castle, CastleManorManager): " + e.getMessage());
            e.printStackTrace();
        }
        list.add(production);
        list.add(productionNext);
        return list;
    }

    public static List<List<CastleManorManager.CropProcure>> loadProcure(Castle castle, CastleManorManager manorManager) {
        List<List<CastleManorManager.CropProcure>> list = new ArrayList<>();
        List<CastleManorManager.CropProcure> procure = new ArrayList<>();
        List<CastleManorManager.CropProcure> procureNext = new ArrayList<>();
        try (Connection con = L2DatabaseFactory.getInstance().getConnection();
             PreparedStatement statement = con.prepareStatement(LOAD_PROCURE))
        {
            statement.setInt(1, castle.getCastleId());
            ResultSet rs = statement.executeQuery();
            while(rs.next()) {
                int cropId = rs.getInt("crop_id");
                int canBuy = rs.getInt("can_buy");
                int startBuy = rs.getInt("start_buy");
                int rewardType = rs.getInt("reward_type");
                int price = rs.getInt("price");
                int period = rs.getInt("period");
                if (period == CastleManorManager.PERIOD_CURRENT)
                    procure.add(manorManager.new CropProcure(cropId, canBuy, rewardType, startBuy, price));
                else
                    procureNext.add(manorManager.new CropProcure(cropId, canBuy, rewardType, startBuy, price));
            }
        }
        catch (SQLException e)
        {
            _log.warning( ClanDao.class.getName() + ": Exception: loadProcure(Castle, CastleManorManager): " + e.getMessage());
            e.printStackTrace();
        }
        list.add(procure);
        list.add(procureNext);
        return list;
    }
}
