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

import com.it.br.gameserver.database.L2DatabaseFactory;
import com.it.br.gameserver.datatables.sql.ClanTable;
import com.it.br.gameserver.model.L2Clan;
import com.it.br.gameserver.model.entity.ClanHall;
import com.it.br.gameserver.model.zone.type.L2ClanHallZone;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

public class ClanHallManager
{
	private static ClanHallManager _instance;

	private Map<Integer, ClanHall> _clanHall;
	private Map<Integer, ClanHall> _freeClanHall;
	private Map<Integer, ClanHall> _allClanHalls;
	private boolean _loaded = false;

	public static ClanHallManager getInstance()
	{
		if (_instance == null)
		{
			_instance = new ClanHallManager();
		}
		return _instance;
	}

	public boolean loaded()
	{
		return _loaded;
	}

	private ClanHallManager()
	{
		_clanHall = new HashMap<>();
		_freeClanHall = new HashMap<>();
		_allClanHalls = new HashMap<>();
		load();
	}

	/** Load All Clan Hall */
	private final void load()
	{
       Connection con = null;
        try
        {
        	int id,ownerId,lease,grade = 0;
        	String Name,Desc,Location;
        	long paidUntil = 0;
        	boolean paid = false;
            PreparedStatement statement;
            ResultSet rs;
            con = L2DatabaseFactory.getInstance().getConnection();
            statement = con.prepareStatement("SELECT * FROM clanhall ORDER BY id");
            rs = statement.executeQuery();
            while (rs.next())
            {
            	id = rs.getInt("id");
            	Name = rs.getString("name");
            	ownerId = rs.getInt("ownerId");
            	lease = rs.getInt("lease");
            	Desc = rs.getString("desc");
            	Location = rs.getString("location");
            	paidUntil = rs.getLong("paidUntil");
            	grade = rs.getInt("Grade");
            	paid = rs.getBoolean("paid");
            	
            	ClanHall ch = new ClanHall(id, Name, ownerId, lease, Desc, Location, paidUntil, grade, paid);
            	if(ownerId == 0)
            	{
            	      _freeClanHall.put(id, ch);
            	}
            	else
            	{
            	if(ClanTable.getInstance().getClan(rs.getInt("ownerId")) != null)
            	{
            		_clanHall.put(id,new ClanHall(id,rs.getString("name"),rs.getInt("ownerId"),rs.getInt("lease"),rs.getString("desc"),rs.getString("location"),rs.getLong("paidUntil"),rs.getInt("Grade"),rs.getBoolean("paid")));
            		ClanTable.getInstance().getClan(rs.getInt("ownerId")).setHasHideout(id);
            	}
            	else
            	{
            		_freeClanHall.put(id, ch);
            		_freeClanHall.get(id).free();
            		AuctionManager.getInstance().initNPC(id);
            	    }

            	}
            	_allClanHalls.put(id, ch);
            }
            statement.close();
            System.out.println("Loaded: "+getClanHalls().size() +" clan halls");
            System.out.println("Loaded: "+getFreeClanHalls().size() +" free clan halls");
            _loaded = true;
        }
        catch (Exception e)
        {
            System.out.println("Exception: ClanHallManager.load(): " + e.getMessage());
            e.printStackTrace();
        }
        finally
        {
        	try
        	{ 
        		con.close();
        		} catch (Exception e)
        		{
        	}
        }
	}

	/** Get Map with all FreeClanHalls */
	public final Map<Integer, ClanHall> getFreeClanHalls()
	{
		return _freeClanHall;
	}

	/** Get Map with all ClanHalls */
	public final Map<Integer, ClanHall> getClanHalls()
	{
		return _clanHall;
	}

    /** Get Map with all ClanHalls*/ 
    public final Map<Integer, ClanHall> getAllClanHalls() 
    { 
        return _allClanHalls; 
    }

	/** Check is free ClanHall */
	public final boolean isFree(int chId)
	{
		if(_freeClanHall.containsKey(chId))
			return true;
		return false;
	}

	/** Free a ClanHall */
	public final synchronized void setFree(int chId)
	{
		_freeClanHall.put(chId,_clanHall.get(chId));
		ClanTable.getInstance().getClan(_freeClanHall.get(chId).getOwnerId()).setHasHideout(0);
		_freeClanHall.get(chId).free();
		_clanHall.remove(chId);
	}

	/** Set ClanHallOwner */
	public final synchronized void setOwner(int chId, L2Clan clan)
	{
		if(!_clanHall.containsKey(chId))
		{
			_clanHall.put(chId,_freeClanHall.get(chId));
			_freeClanHall.remove(chId);
		}else
			_clanHall.get(chId).free();
		ClanTable.getInstance().getClan(clan.getClanId()).setHasHideout(chId);
		_clanHall.get(chId).setOwner(clan);
	}

    /** Get Clan Hall by Id */
    public final ClanHall getClanHallById(int clanHallId)
    {
    	if(_clanHall.containsKey(clanHallId))
    		return _clanHall.get(clanHallId);
    	if(_freeClanHall.containsKey(clanHallId))
    		return _freeClanHall.get(clanHallId);
        return null;
    }

    public final ClanHall getNearbyClanHall(int x, int y, int maxDist)
    {
    	L2ClanHallZone zone = null;
    	for (Map.Entry<Integer, ClanHall> ch : _clanHall.entrySet())
    	{
    		zone = ch.getValue().getZone();
   		if (zone != null && zone.getDistanceToZone(x, y) < maxDist)
    			return ch.getValue();
    	}
    	for (Map.Entry<Integer, ClanHall> ch : _freeClanHall.entrySet())
    	{
    		zone = ch.getValue().getZone();
    		if (zone != null && zone.getDistanceToZone(x, y) < maxDist)
    			return ch.getValue();
    	}
        return null;
    }

    /** Get Clan Hall by Owner */
    public final ClanHall getClanHallByOwner(L2Clan clan)
    {
    	for (Map.Entry<Integer, ClanHall> ch : _clanHall.entrySet())
    	{
    		if (clan.getClanId() == ch.getValue().getOwnerId())
    			return ch.getValue();
    	}
    	return null;
    }
}