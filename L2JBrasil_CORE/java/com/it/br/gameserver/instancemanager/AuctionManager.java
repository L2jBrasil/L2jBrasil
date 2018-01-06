/* This program is free software; you can redistribute it and/or modify
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
package com.it.br.gameserver.instancemanager;


import com.it.br.gameserver.database.dao.AuctionDao;
import com.it.br.gameserver.model.entity.Auction;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class AuctionManager
{
    protected static final Logger _log = Logger.getLogger(AuctionManager.class.getName());
    private static AuctionManager _instance;
    private List<Auction> _auctions;
		 private static final int[] ItemInitDataId =
		 {
		     22,23,24,25,26,27,28,29,30,31,32,33,36,37,38,39,40,41,42,43,44,45,46,47,48
		     ,49,50,51,52,53,54,55,56,57,58,59,60,61
		 };
    public static final AuctionManager getInstance()
    {
        if (_instance == null)
        {
        	_instance = new AuctionManager();
        }
        return _instance;
    }
    public AuctionManager()
    {
    	_auctions = new ArrayList<>();
    	load();
    }
    public void reload()
    {
    	_auctions.clear();
    	load();
    }

    private final void load()
    {
        List<Integer> auctionIds = AuctionDao.loadOrderById();

        auctionIds.forEach(id -> _auctions.add(new Auction(id)));
        System.out.println("Loaded: " + getAuctions().size() + " auction(s)");
    }

    public final Auction getAuction(int auctionId)
    {
        int index = getAuctionIndex(auctionId);
        if (index >= 0)
        	return getAuctions().get(index);
        return null;
    }

    public final int getAuctionIndex(int auctionId)
    {
        Auction auction;
        for (int i = 0; i < getAuctions().size(); i++)
        {
        	auction = getAuctions().get(i);
            if (auction != null && auction.getId() == auctionId)
            	return i;
        }
        return -1;
    }

    public final List<Auction> getAuctions()
    {
        return _auctions;
    }
    /** Init Clan NPC aution */
	public void initNPC(int id){
        Connection con = null;
        int i = 0;
        for(i=0;i<ItemInitDataId.length;i++)
        	if(ItemInitDataId[i] == id)
        		break;
        if(i>=ItemInitDataId.length){
        	_log.warning("Clan Hall auction not found for Id :"+id);
        	return;
        }
        AuctionDao.insertByValues(i);
	}
}
