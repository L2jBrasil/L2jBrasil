package com.it.br.gameserver.database.dao;

import com.it.br.gameserver.database.L2DatabaseFactory;
import com.it.br.gameserver.idfactory.IdFactory;
import com.it.br.gameserver.model.actor.instance.L2PcInstance;
import com.it.br.gameserver.model.entity.Auction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Tayran
 * @version 3.0.4
 */
public class AuctionDao
{
    private static final Logger _log = LoggerFactory.getLogger(AuctionDao.class);

    private static final String SELECT_AUCTIONS_ORDER_BY_ID = "SELECT id FROM auction ORDER BY id";
    private static final String SELECT_ALL_AUCTIONS         = "SELECT * FROM auction WHERE id = ?";
    private static final String SELECT_AUCTIONS_BID         = "SELECT bidderId, bidderName, maxBid, clan_name, time_bid FROM auction_bid WHERE auctionId = ? ORDER BY maxBid DESC";
    private static final String SELECT_AUCTIONS_END_DATE    = "UPDATE auction SET endDate = ? WHERE id = ?";
    private static final String UPDATE_BIDDER               = "UPDATE auction_bid SET bidderId=?, bidderName=?, maxBid=?, time_bid=? WHERE auctionId=? AND bidderId=?";
    private static final String INSERT_BIDDER               = "INSERT INTO auction_bid (id, auctionId, bidderId, bidderName, maxBid, clan_name, time_bid) VALUES (?, ?, ?, ?, ?, ?, ?)";
    private static final String DELETE_BID                  = "DELETE FROM auction_bid WHERE auctionId=?";
    private static final String DELETE_AUCTION              = "DELETE FROM auction WHERE itemId=?";
    private static final String DELETE_AUCTION_BID          = "DELETE FROM auction_bid WHERE auctionId=? AND bidderId=?";
    private static final String INSERT_AUCTION              = "INSERT INTO auction (id, sellerId, sellerName, sellerClanName, itemType, itemId, itemObjectId, itemName, itemQuantity, startingBid, currentBid, endDate) VALUES (?,?,?,?,?,?,?,?,?,?,?,?)";
    private static final String INSERT_VALUES               = "INSERT INTO `auction` VALUES ?";
    private static final String[] ITEM_INIT_DATA =
            {
                    "(22, 0, 'NPC', 'NPC Clan', 'ClanHall', 22, 0, 'Moonstone Hall', 1, 20000000, 0, 1164841200000)",
                    "(23, 0, 'NPC', 'NPC Clan', 'ClanHall', 23, 0, 'Onyx Hall', 1, 20000000, 0, 1164841200000)",
                    "(24, 0, 'NPC', 'NPC Clan', 'ClanHall', 24, 0, 'Topaz Hall', 1, 20000000, 0, 1164841200000)",
                    "(25, 0, 'NPC', 'NPC Clan', 'ClanHall', 25, 0, 'Ruby Hall', 1, 20000000, 0, 1164841200000)",
                    "(26, 0, 'NPC', 'NPC Clan', 'ClanHall', 26, 0, 'Crystal Hall', 1, 20000000, 0, 1164841200000)",
                    "(27, 0, 'NPC', 'NPC Clan', 'ClanHall', 27, 0, 'Onyx Hall', 1, 20000000, 0, 1164841200000)",
                    "(28, 0, 'NPC', 'NPC Clan', 'ClanHall', 28, 0, 'Sapphire Hall', 1, 20000000, 0, 1164841200000)",
                    "(29, 0, 'NPC', 'NPC Clan', 'ClanHall', 29, 0, 'Moonstone Hall', 1, 20000000, 0, 1164841200000)",
                    "(30, 0, 'NPC', 'NPC Clan', 'ClanHall', 30, 0, 'Emerald Hall', 1, 20000000, 0, 1164841200000)",
                    "(31, 0, 'NPC', 'NPC Clan', 'ClanHall', 31, 0, 'The Atramental Barracks', 1, 8000000, 0, 1164841200000)",
                    "(32, 0, 'NPC', 'NPC Clan', 'ClanHall', 32, 0, 'The Scarlet Barracks', 1, 8000000, 0, 1164841200000)",
                    "(33, 0, 'NPC', 'NPC Clan', 'ClanHall', 33, 0, 'The Viridian Barracks', 1, 8000000, 0, 1164841200000)",
                    "(36, 0, 'NPC', 'NPC Clan', 'ClanHall', 36, 0, 'The Golden Chamber', 1, 50000000, 0, 1164841200000)",
                    "(37, 0, 'NPC', 'NPC Clan', 'ClanHall', 37, 0, 'The Silver Chamber', 1, 50000000, 0, 1164841200000)",
                    "(38, 0, 'NPC', 'NPC Clan', 'ClanHall', 38, 0, 'The Mithril Chamber', 1, 50000000, 0, 1164841200000)",
                    "(39, 0, 'NPC', 'NPC Clan', 'ClanHall', 39, 0, 'Silver Manor', 1, 50000000, 0, 1164841200000)",
                    "(40, 0, 'NPC', 'NPC Clan', 'ClanHall', 40, 0, 'Gold Manor', 1, 50000000, 0, 1164841200000)",
                    "(41, 0, 'NPC', 'NPC Clan', 'ClanHall', 41, 0, 'The Bronze Chamber', 1, 50000000, 0, 1164841200000)",
                    "(42, 0, 'NPC', 'NPC Clan', 'ClanHall', 42, 0, 'The Golden Chamber', 1, 50000000, 0, 1164841200000)",
                    "(43, 0, 'NPC', 'NPC Clan', 'ClanHall', 43, 0, 'The Silver Chamber', 1, 50000000, 0, 1164841200000)",
                    "(44, 0, 'NPC', 'NPC Clan', 'ClanHall', 44, 0, 'The Mithril Chamber', 1, 50000000, 0, 1164841200000)",
                    "(45, 0, 'NPC', 'NPC Clan', 'ClanHall', 45, 0, 'The Bronze Chamber', 1, 50000000, 0, 1164841200000)",
                    "(46, 0, 'NPC', 'NPC Clan', 'ClanHall', 46, 0, 'Silver Manor', 1, 50000000, 0, 1164841200000)",
                    "(47, 0, 'NPC', 'NPC Clan', 'ClanHall', 47, 0, 'Moonstone Hall', 1, 50000000, 0, 1164841200000)",
                    "(48, 0, 'NPC', 'NPC Clan', 'ClanHall', 48, 0, 'Onyx Hall', 1, 50000000, 0, 1164841200000)",
                    "(49, 0, 'NPC', 'NPC Clan', 'ClanHall', 49, 0, 'Emerald Hall', 1, 50000000, 0, 1164841200000)",
                    "(50, 0, 'NPC', 'NPC Clan', 'ClanHall', 50, 0, 'Sapphire Hall', 1, 50000000, 0, 1164841200000)",
                    "(51, 0, 'NPC', 'NPC Clan', 'ClanHall', 51, 0, 'Mont Chamber', 1, 50000000, 0, 1164841200000)",
                    "(52, 0, 'NPC', 'NPC Clan', 'ClanHall', 52, 0, 'Astaire Chamber', 1, 50000000, 0, 1164841200000)",
                    "(53, 0, 'NPC', 'NPC Clan', 'ClanHall', 53, 0, 'Aria Chamber', 1, 50000000, 0, 1164841200000)",
                    "(54, 0, 'NPC', 'NPC Clan', 'ClanHall', 54, 0, 'Yiana Chamber', 1, 50000000, 0, 1164841200000)",
                    "(55, 0, 'NPC', 'NPC Clan', 'ClanHall', 55, 0, 'Roien Chamber', 1, 50000000, 0, 1164841200000)",
                    "(56, 0, 'NPC', 'NPC Clan', 'ClanHall', 56, 0, 'Luna Chamber', 1, 50000000, 0, 1164841200000)",
                    "(57, 0, 'NPC', 'NPC Clan', 'ClanHall', 57, 0, 'Traban Chamber', 1, 50000000, 0, 1164841200000)",
                    "(58, 0, 'NPC', 'NPC Clan', 'ClanHall', 58, 0, 'Eisen Hall', 1, 50000000, 0, 1164841200000)",
                    "(59, 0, 'NPC', 'NPC Clan', 'ClanHall', 59, 0, 'Heavy Metal Hall', 1, 50000000, 0, 1164841200000)",
                    "(60, 0, 'NPC', 'NPC Clan', 'ClanHall', 60, 0, 'Molten Ore Hall', 1, 50000000, 0, 1164841200000)",
                    "(61, 0, 'NPC', 'NPC Clan', 'ClanHall', 61, 0, 'Titan Hall', 1, 50000000, 0, 1164841200000)"
            };

    public static List<Object> load(Auction auction)
    {
        List<Object> list = new ArrayList<>();
        try (Connection con = L2DatabaseFactory.getInstance().getConnection();
             PreparedStatement statement = con.prepareStatement(SELECT_ALL_AUCTIONS))
        {
            statement.setInt(1, auction.getId());
            ResultSet rs = statement.executeQuery();
            while (rs.next())
            {
                list.add(rs.getInt("currentBid"));
                list.add(rs.getLong("endDate"));
                list.add(rs.getInt("itemId"));
                list.add(rs.getString("itemName"));
                list.add(rs.getInt("itemObjectId"));
                list.add(rs.getString("itemType"));
                list.add(rs.getInt("sellerId"));
                list.add(rs.getString("sellerClanName"));
                list.add(rs.getString("sellerName"));
                list.add(rs.getInt("startingBid"));
            }

        }
        catch (SQLException e)
        {
            _log.warn( AuctionDao.class.getName() + ": Exception: load(Auction): " + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }

    public static List<Integer> loadOrderById()
    {
        List<Integer> list = new ArrayList<>();
        try (Connection con = L2DatabaseFactory.getInstance().getConnection();
             PreparedStatement statement = con.prepareStatement(SELECT_AUCTIONS_ORDER_BY_ID))
        {
            ResultSet rs = statement.executeQuery();
            while (rs.next())
                list.add(rs.getInt("id"));
        }
        catch (SQLException e)
        {
            _log.warn( AuctionDao.class.getName() + ": Exception: loadOrderById(): " + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }

    public static List<Object> loadBid(Auction auction)
    {
        List<Object> list = new ArrayList<>();
        try (Connection con = L2DatabaseFactory.getInstance().getConnection();
             PreparedStatement statement = con.prepareStatement(SELECT_AUCTIONS_BID))
        {
            statement.setInt(1, auction.getId());
            ResultSet rs = statement.executeQuery();

            while (rs.next())
            {
                list.add(rs.getInt("bidderId"));
                list.add(rs.getString("bidderName"));
                list.add(rs.getInt("maxBid"));
                list.add(rs.getString("clan_name"));
                list.add(rs.getLong("time_bid"));
            }
        }
        catch (Exception e)
        {
            _log.warn(AuctionDao.class.getName() + ".: Exception: loadBid(Auction): " + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }

    public static void saveAuctionDate(Auction auction)
    {
        try (Connection con = L2DatabaseFactory.getInstance().getConnection();
             PreparedStatement statement = con.prepareStatement(SELECT_AUCTIONS_END_DATE))
        {
            statement.setLong(1, auction.getEndDate());
            statement.setInt(2,auction. getId());
            statement.execute();
        }
        catch (SQLException e)
        {
            _log.warn( AuctionDao.class.getName() + ": Exception: saveAuctionDate(Auction): " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void updateBidder(L2PcInstance bidder, Auction auction, int bid)
    {
        try (Connection con = L2DatabaseFactory.getInstance().getConnection();
             PreparedStatement statement = con.prepareStatement(UPDATE_BIDDER))
        {
            statement.setInt(1, bidder.getClanId());
            statement.setString(2, bidder.getClan().getLeaderName());
            statement.setInt(3, bid);
            statement.setLong(4, System.currentTimeMillis());
            statement.setInt(5, auction.getId());
            statement.setInt(6, bidder.getClanId());
            statement.execute();
        }
        catch (SQLException e)
        {
            _log.warn( AuctionDao.class.getName() + ": Exception: updateBidder(Auction): " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void addBidder(L2PcInstance bidder, Auction auction, int bid)
    {
        try (Connection con = L2DatabaseFactory.getInstance().getConnection();
             PreparedStatement statement = con.prepareStatement(INSERT_BIDDER))
        {
            statement.setInt(1, IdFactory.getInstance().getNextId());
            statement.setInt(2, auction.getId());
            statement.setInt(3, bidder.getClanId());
            statement.setString(4, bidder.getName());
            statement.setInt(5, bid);
            statement.setString(6, bidder.getClan().getName());
            statement.setLong(7, System.currentTimeMillis());
            statement.execute();
        }
        catch (SQLException e)
        {
            _log.warn( AuctionDao.class.getName() + ": Exception: addBidder(Auction): " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void removeBid(Auction auction)
    {
        try (Connection con = L2DatabaseFactory.getInstance().getConnection();
             PreparedStatement statement = con.prepareStatement(DELETE_BID))
        {
            statement.setInt(1, auction.getId());
            statement.execute();
        }
        catch (SQLException e)
        {
            _log.warn( AuctionDao.class.getName() + ": Exception: removeBid(Auction): " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void deleteAuction(Auction auction)
    {
        try (Connection con = L2DatabaseFactory.getInstance().getConnection();
             PreparedStatement statement = con.prepareStatement(DELETE_AUCTION))
        {
            statement.setInt(1, auction.getItemId());
            statement.execute();
        }
        catch (SQLException e)
        {
            _log.warn( AuctionDao.class.getName() + ": Exception: deleteAuction(Auction): " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void deleteBid(Auction auction, int bidder)
    {
        try (Connection con = L2DatabaseFactory.getInstance().getConnection();
             PreparedStatement statement = con.prepareStatement(DELETE_AUCTION_BID))
        {
            statement.setInt(1, auction.getId());
            statement.setInt(2, bidder);
            statement.execute();
        }
        catch (SQLException e)
        {
            _log.warn( AuctionDao.class.getName() + ": Exception: deleteBid(Auction): " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void insertAuction(Auction auction)
    {
        try (Connection con = L2DatabaseFactory.getInstance().getConnection();
             PreparedStatement statement = con.prepareStatement(INSERT_AUCTION))
        {
            statement.setInt(1, auction.getId());
            statement.setInt(2, auction.getSellerId());
            statement.setString(3, auction.getSellerName());
            statement.setString(4, auction.getSellerClanName());
            statement.setString(5, auction.getItemType());
            statement.setInt(6, auction.getItemId());
            statement.setInt(7, auction.getItemObjectId());
            statement.setString(8, auction.getItemName());
            statement.setInt(9, auction.getItemQuantity());
            statement.setInt(10, auction.getStartingBid());
            statement.setInt(11, auction.getCurrentBid());
            statement.setLong(12, auction.getEndDate());
            statement.execute();
            statement.close();
        }
        catch (SQLException e)
        {
            _log.warn( AuctionDao.class.getName() + ": Exception: insertAuction(Auction): " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void insertByValues(int arrayPos)
    {
        try (Connection con = L2DatabaseFactory.getInstance().getConnection();
             PreparedStatement statement = con.prepareStatement(INSERT_VALUES))
        {
            statement.setString(1, ITEM_INIT_DATA[arrayPos]);
            statement.execute();
            statement.close();
        }
        catch (SQLException e)
        {
            _log.warn( AuctionDao.class.getName() + ": Exception: insertByValues(int arrayPos): " + e.getMessage());
            e.printStackTrace();
        }
    }
}