/*
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA
 * 02111-1307, USA.
 *
 * http://www.gnu.org/copyleft/gpl.html
 */
package com.it.br.gameserver.model.entity;

import com.it.br.gameserver.GameServer;
import com.it.br.gameserver.ThreadPoolManager;
import com.it.br.gameserver.database.dao.AuctionDao;
import com.it.br.gameserver.datatables.sql.ClanTable;
import com.it.br.gameserver.instancemanager.AuctionManager;
import com.it.br.gameserver.instancemanager.ClanHallManager;
import com.it.br.gameserver.model.L2Clan;
import com.it.br.gameserver.model.L2World;
import com.it.br.gameserver.model.actor.instance.L2PcInstance;

import java.sql.ResultSet;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Auction
{
    protected static final Logger _log = Logger.getLogger(Auction.class.getName());
	private int _id								= 0;
	private int _adenaId						= 57;
	private long _endDate;
	private int _highestBidderId				= 0;
	private String _highestBidderName			= "";
	private int _highestBidderMaxBid			= 0;
	private int _itemId							= 0;
	private String _itemName					= "";
	private int _itemObjectId					= 0;
	private int _itemQuantity					= 0;
	private String _itemType					= "";
	private int _sellerId						= 0;
	private String _sellerClanName              = "";
	private String _sellerName					= "";
	private int _currentBid						= 0;
	private int _startingBid					= 0;

	private Map<Integer, Bidder> _bidders        = new HashMap<>();
	private static final String[] ItemTypeName =
	{
	             "ClanHall"
	};
	public static enum ItemTypeEnum
	{
	    ClanHall
	}
	 public class Bidder
	 {
	     private String _name;
	     private String _clanName;
	     private int _bid;
	     private Calendar _timeBid;
	     public Bidder(String name, String clanName, int bid, long timeBid)
	     {
	         _name = name;
	         _clanName = clanName;
	         _bid = bid;
	         _timeBid = Calendar.getInstance();
	         _timeBid.setTimeInMillis(timeBid);
	     }
	     public String getName()
	     {
	         return _name;
	     }
	     public String getClanName()
	     {
	         return _clanName;
	     }
	     public int getBid()
	     {
	         return _bid;
	     }
	     public Calendar getTimeBid()
	     {
	         return _timeBid;
	     }
	     public void setTimeBid(long timeBid)
	     {
	         _timeBid.setTimeInMillis(timeBid);
	     }
	     public void setBid(int bid)
	     {
	         _bid = bid;
	     }
	 }
	/** Task Sheduler for endAuction */
    public class AutoEndTask implements Runnable
    {
        public AutoEndTask(){}

		public void run()
        {
            try
            {
                 endAuction();
            } catch (Throwable t) { }
        }
    }
    /** Constructor */

	public Auction(int auctionId)
	{
		_id = auctionId;
		load();
        startAutoTask();
	}
    public Auction(int itemId, L2Clan Clan, long delay, int bid, String name)
    {
        _id = itemId;
        _endDate = System.currentTimeMillis() + delay;
        _itemId = itemId;
        _itemName = name;
        _itemType = "ClanHall";
        _sellerId = Clan.getLeaderId();
        _sellerName = Clan.getLeaderName();
        _sellerClanName = Clan.getName();
        _startingBid = bid;
    }
    /** Load auctions */
	private void load()
	{
        try
        {
            ResultSet rs;
            rs = AuctionDao.loadAuctions(this);

            while (rs.next())
            {
        	    _currentBid = rs.getInt("currentBid");
        	    _endDate= rs.getLong("endDate");
        	    _itemId = rs.getInt("itemId");
        	    _itemName = rs.getString("itemName");
        	    _itemObjectId = rs.getInt("itemObjectId");
        	    _itemType = rs.getString("itemType");
        	    _sellerId = rs.getInt("sellerId");
                _sellerClanName = rs.getString("sellerClanName");
        	    _sellerName = rs.getString("sellerName");
        	    _startingBid = rs.getInt("startingBid");
            }
            loadBid();
            rs.close();
        }
        catch (Exception e)
        {
            _log.warning(Auction.class.getName() + ": Exception: Auction.load(): " + e.getMessage());
            e.printStackTrace();
        }
	}
	/** Load bidders **/
	public void loadBid()
	{
	    _highestBidderId = 0;
		_highestBidderName = "";
		_highestBidderMaxBid = 0;		

        try
        {
            ResultSet rs = AuctionDao.loadBid(this);

            while (rs.next())
            {
                if (rs.isFirst())
                {
                    _highestBidderId = rs.getInt("bidderId");
                    _highestBidderName = rs.getString("bidderName");
                    _highestBidderMaxBid = rs.getInt("maxBid");
                }
                _bidders.put(rs.getInt("bidderId"), new Bidder(rs.getString("bidderName"), rs.getString("clan_name"), rs.getInt("maxBid"), rs.getLong("time_bid")));
            }
            rs.close();
        }
        catch (Exception e)
        {
            _log.warning(Auction.class.getName() + ": Exception: Auction.loadBid(): " + e.getMessage());
            e.printStackTrace();
        }
	}
    /** Task Manage */
    private void startAutoTask()
    {
    	long currentTime = System.currentTimeMillis();
    	long taskDelay = 0;
        if (_endDate <= currentTime){
        	_endDate = currentTime + 7*24*60*60*1000;
        	saveAuctionDate();
        }else
        	taskDelay = _endDate - currentTime;
        ThreadPoolManager.getInstance().scheduleGeneral(new AutoEndTask(), taskDelay);
    }
	public static String getItemTypeName(ItemTypeEnum value)
	{
	    return ItemTypeName[value.ordinal()];
	}
	/** Save Auction Data End */
    private void saveAuctionDate()
    {
        AuctionDao.saveAuctionDate(this);
    }
    /** Set a bid */
	public synchronized void setBid(L2PcInstance bidder, int bid)
	{
	    int requiredAdena = bid;
	    if (getHighestBidderName().equals(bidder.getClan().getLeaderName()))
	    		requiredAdena = bid - getHighestBidderMaxBid();
		if ((getHighestBidderId() >0 && bid > getHighestBidderMaxBid())
				|| (getHighestBidderId() == 0 && bid >= getStartingBid()))
	    {
			if(takeItem(bidder, 57, requiredAdena))
			{
				updateInDB(bidder, bid);
            	bidder.getClan().setAuctionBiddedAt(_id, true);
            	return;
			}
	    }
		bidder.sendMessage("Invalid bid!");
	}
	/** Return Item in WHC */
	private void returnItem(String Clan, int itemId, int quantity, boolean penalty)
	{
        if (penalty)
            quantity *= 0; //take 10% tax fee if needed
        ClanTable.getInstance().getClanByName(Clan).getWarehouse().addItem("Outbidded", _adenaId, quantity, null, null);
	}
	/** Take Item in WHC */
	private boolean takeItem(L2PcInstance bidder, int itemId, int quantity)
	{
    	if (bidder.getClan() != null && bidder.getClan().getWarehouse().getAdena() >= quantity)
    	{
    		bidder.getClan().getWarehouse().destroyItemByItemId("Buy", _adenaId, quantity, bidder, bidder);
        	return true;
    	}
		bidder.sendMessage("You do not have enough adena");
        return false;
	}
	/** Update auction in DB */
	private void updateInDB(L2PcInstance bidder, int bid)
	{
        try
        {
            if (getBidders().get(bidder.getClanId()) != null)
            {
                AuctionDao.updateBidder(bidder, this, bid);
            }
            else
            {
                AuctionDao.addBidder(bidder, this, bid);
                if (L2World.getInstance().getPlayer(_highestBidderName) != null)
                    L2World.getInstance().getPlayer(_highestBidderName).sendMessage("You have been out bidded");
            }
            _highestBidderId = bidder.getClanId();
            _highestBidderMaxBid = bid;
            _highestBidderName = bidder.getClan().getLeaderName();
            if (_bidders.get(_highestBidderId) == null)
                _bidders.put(_highestBidderId, new Bidder(_highestBidderName, bidder.getClan().getName(), bid, Calendar.getInstance().getTimeInMillis()));
            else
            {
                _bidders.get(_highestBidderId).setBid(bid);
                _bidders.get(_highestBidderId).setTimeBid(Calendar.getInstance().getTimeInMillis());
            }
            bidder.sendMessage("You have bidded successfully");
        }
        catch (Exception e)
        {
        	 _log.log(Level.SEVERE, "Exception: Auction.updateInDB(L2PcInstance bidder, int bid): " + e.getMessage());
            e.printStackTrace();
        }
	}
    /** Remove bids */
    private void removeBids()
    {
        AuctionDao.removeBid(this);

        for (Bidder b : _bidders.values())
        {
          if (ClanTable.getInstance().getClanByName(b.getClanName()).getHasHideout() == 0)
        	  returnItem(b.getClanName(), 57, 9*b.getBid()/10, false); // 10 % tax
          else
          {
        	  if (L2World.getInstance().getPlayer(b.getName()) != null)
        		  L2World.getInstance().getPlayer(b.getName()).sendMessage("Congratulation you have won ClanHall!");
          }
          ClanTable.getInstance().getClanByName(b.getClanName()).setAuctionBiddedAt(0, true);
        }
        _bidders.clear();
    }
    /** Remove auctions */
    public void deleteAuctionFromDB()
    {
        AuctionManager.getInstance().getAuctions().remove(this);
        AuctionDao.deleteAuction(this);
    }
    /** End of auction */
    public void endAuction()
    {
    	if(GameServer.gameServer.getCHManager() != null && GameServer.gameServer.getCHManager().loaded()){
	        if (_highestBidderId == 0 && _sellerId == 0)
	        {
	            startAutoTask();
	            return;
	        }
	        if (_highestBidderId == 0 && _sellerId > 0)
	        {
	            /** If seller haven't sell ClanHall, auction removed,
	             *  THIS MUST BE CONFIRMED */
	        	int aucId = AuctionManager.getInstance().getAuctionIndex(_id);
	        	AuctionManager.getInstance().getAuctions().remove(aucId);
	            return;
	        }
	        if (_sellerId > 0)
	        {
	            returnItem(_sellerClanName, 57, _highestBidderMaxBid, true);
	            returnItem(_sellerClanName, 57, ClanHallManager.getInstance().getClanHallById(_itemId).getLease(), false);
	        }
		    deleteAuctionFromDB();
		    L2Clan Clan = ClanTable.getInstance().getClanByName(_bidders.get(_highestBidderId).getClanName());
		    _bidders.remove(_highestBidderId);
		    Clan.setAuctionBiddedAt(0, true);
		    removeBids();
		    ClanHallManager.getInstance().setOwner(_itemId, Clan);
    	}else{
    		/** Task waiting ClanHallManager is loaded every 3s */
            ThreadPoolManager.getInstance().scheduleGeneral(new AutoEndTask(), 3000);
    	}
    }
    /** Cancel bid */
    public synchronized void cancelBid(int bidder)
    {
        AuctionDao.deleteBid(this, bidder);
         returnItem(_bidders.get(bidder).getClanName(), 57, _bidders.get(bidder).getBid(), true);
        ClanTable.getInstance().getClanByName(_bidders.get(bidder).getClanName()).setAuctionBiddedAt(0, true);
        _bidders.clear();
        loadBid();
    }
    /** Cancel auction */
    public void cancelAuction()
    {
        deleteAuctionFromDB();
        removeBids();
    }
    /** Confirm an auction */
    public void confirmAuction()
    {
        AuctionManager.getInstance().getAuctions().add(this);
        AuctionDao.insertAuction(this);
    }
    /** Get var auction */
	public final int getId() { return _id; }
	public final int getCurrentBid() { return _currentBid; }
	public final long getEndDate() { return _endDate; }
	public final int getHighestBidderId() { return _highestBidderId; }
	public final String getHighestBidderName() { return _highestBidderName; }
	public final int getHighestBidderMaxBid() { return _highestBidderMaxBid; }
	public final int getItemId() { return _itemId; }
	public final String getItemName() { return _itemName; }
	public final int getItemObjectId() { return _itemObjectId; }
	public final int getItemQuantity() { return _itemQuantity; }
	public final String getItemType() { return _itemType; }
	public final int getSellerId() { return _sellerId; }
	public final String getSellerName() { return _sellerName; }
    public final String getSellerClanName() { return _sellerClanName; }
	public final int getStartingBid() { return _startingBid; }
    public final Map<Integer, Bidder> getBidders(){ return _bidders; };
}