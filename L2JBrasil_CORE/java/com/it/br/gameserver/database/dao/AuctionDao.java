package com.it.br.gameserver.database.dao;

import com.it.br.gameserver.database.L2DatabaseFactory;
import com.it.br.gameserver.idfactory.IdFactory;
import com.it.br.gameserver.model.actor.instance.L2PcInstance;
import com.it.br.gameserver.model.entity.Auction;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * @author Tayran
 * @version 3.0.4
 */
public class AuctionDao
{
    private static final Logger _log = Logger.getLogger(AuctionDao.class.getName());

    private static final String SELECT_ALL_AUCTIONS      = "SELECT * FROM auction WHERE id = ?";
    private static final String SELECT_AUCTIONS_BID      = "SELECT bidderId, bidderName, maxBid, clan_name, time_bid FROM auction_bid WHERE auctionId = ? ORDER BY maxBid DESC";
    private static final String SELECT_AUCTIONS_END_DATE = "UPDATE auction SET endDate = ? WHERE id = ?";
    private static final String UPDATE_BIDDER            = "UPDATE auction_bid SET bidderId=?, bidderName=?, maxBid=?, time_bid=? WHERE auctionId=? AND bidderId=?";
    private static final String INSERT_BIDDER            = "INSERT INTO auction_bid (id, auctionId, bidderId, bidderName, maxBid, clan_name, time_bid) VALUES (?, ?, ?, ?, ?, ?, ?)";
    private static final String DELETE_BID               = "DELETE FROM auction_bid WHERE auctionId=?";
    private static final String DELETE_AUCTION           = "DELETE FROM auction WHERE itemId=?";
    private static final String DELETE_AUCTION_BID       = "DELETE FROM auction_bid WHERE auctionId=? AND bidderId=?";
    private static final String INSERT_AUCTION           = "INSERT INTO auction (id, sellerId, sellerName, sellerClanName, itemType, itemId, itemObjectId, itemName, itemQuantity, startingBid, currentBid, endDate) VALUES (?,?,?,?,?,?,?,?,?,?,?,?)";

    public static List<Object> loadAuctions(Auction auction)
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
            _log.warning( AuctionDao.class.getName() + ": Exception: loadAuctions(): " + e.getMessage());
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
            _log.warning(AuctionDao.class.getName() + ".: Exception: loadBid(): " + e.getMessage());
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
            _log.warning( AuctionDao.class.getName() + ": Exception: saveAuctionDate(): " + e.getMessage());
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
            _log.warning( AuctionDao.class.getName() + ": Exception: updateBidder(): " + e.getMessage());
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
            _log.warning( AuctionDao.class.getName() + ": Exception: addBidder(): " + e.getMessage());
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
            _log.warning( AuctionDao.class.getName() + ": Exception: removeBid(): " + e.getMessage());
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
            _log.warning( AuctionDao.class.getName() + ": Exception: deleteAuction(): " + e.getMessage());
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
            _log.warning( AuctionDao.class.getName() + ": Exception: deleteBid(): " + e.getMessage());
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
            _log.warning( AuctionDao.class.getName() + ": Exception: insertAuction(): " + e.getMessage());
            e.printStackTrace();
        }
    }
}