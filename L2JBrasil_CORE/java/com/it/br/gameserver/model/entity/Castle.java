package com.it.br.gameserver.model.entity;

import com.it.br.Config;
import com.it.br.gameserver.Announcements;
import com.it.br.gameserver.CastleUpdater;
import com.it.br.gameserver.SevenSigns;
import com.it.br.gameserver.ThreadPoolManager;
import com.it.br.gameserver.database.L2DatabaseFactory;
import com.it.br.gameserver.datatables.sql.ClanTable;
import com.it.br.gameserver.datatables.xml.DoorTable;
import com.it.br.gameserver.instancemanager.CastleManager;
import com.it.br.gameserver.instancemanager.CastleManorManager;
import com.it.br.gameserver.instancemanager.CastleManorManager.CropProcure;
import com.it.br.gameserver.instancemanager.CastleManorManager.SeedProduction;
import com.it.br.gameserver.model.L2Clan;
import com.it.br.gameserver.model.L2Manor;
import com.it.br.gameserver.model.L2Object;
import com.it.br.gameserver.model.actor.instance.L2DoorInstance;
import com.it.br.gameserver.model.actor.instance.L2PcInstance;
import com.it.br.gameserver.model.zone.type.L2CastleTeleportZone;
import com.it.br.gameserver.model.zone.type.L2CastleZone;
import com.it.br.gameserver.model.zone.type.L2SiegeZone;
import com.it.br.gameserver.network.serverpackets.PledgeShowInfoUpdate;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;
import java.util.logging.Logger;

public class Castle
{
    protected static Logger _log = Logger.getLogger(Castle.class.getName());

    // =========================================================
    // Data Field
    private List<CropProcure> _procure = new ArrayList<>();
    private List<SeedProduction> _production = new ArrayList<>();
    private List<CropProcure> _procureNext = new ArrayList<>();
    private List<SeedProduction> _productionNext = new ArrayList<>();
    private boolean _isNextPeriodApproved = false;

    private static final String CASTLE_MANOR_DELETE_PRODUCTION = "DELETE FROM castle_manor_production WHERE castle_id=?;";
    private static final String CASTLE_MANOR_DELETE_PRODUCTION_PERIOD = "DELETE FROM castle_manor_production WHERE castle_id=? AND period=?;";
    private static final String CASTLE_MANOR_DELETE_PROCURE = "DELETE FROM castle_manor_procure WHERE castle_id=?;";
    private static final String CASTLE_MANOR_DELETE_PROCURE_PERIOD = "DELETE FROM castle_manor_procure WHERE castle_id=? AND period=?;";
    private static final String CASTLE_UPDATE_CROP = "UPDATE castle_manor_procure SET can_buy=? WHERE crop_id=? AND castle_id=? AND period=?";
    private static final String CASTLE_UPDATE_SEED = "UPDATE castle_manor_production SET can_produce=? WHERE seed_id=? AND castle_id=? AND period=?";

	private int _castleId = 0;
	private List<L2DoorInstance> _doors = new ArrayList<>();
	private List<String> _doorDefault = new ArrayList<>();
	private String _name = "";
	private int _ownerId = 0;
	private Siege _siege = null;
	private Calendar _siegeDate;
	private int _siegeDayOfWeek = 7; // Default to saturday
	private int _siegeHourOfDay = 20; // Default to 8 pm server time
	private int _taxPercent = 0;
	private double _taxRate = 0;
	private int _treasury = 0;
	private L2SiegeZone _zone;
    private L2CastleZone _castleZone;
	private L2CastleTeleportZone _teleZone;
    private L2Clan _formerOwner = null;
    private int _nbArtifact = 1;
    private Map<Integer, Integer> _engrave= new HashMap<>();
    private boolean _showNpcCrest = false;
	private final int[] _gate =
	{
		Integer.MIN_VALUE, 0, 0
	};

	public Castle(int castleId)
	{
		_castleId = castleId;
		if(_castleId == 7 || castleId == 9) // Goddard and Schuttgart
			_nbArtifact = 2;
        load();
		loadDoor();
	}

	public void Engrave(L2Clan clan, int objId)
	{
		getSiege().announceToPlayer("Clan " + clan.getName() + " has start to engrave one of the rulers.", true);
		_engrave.put(objId, clan.getClanId());
		if (_engrave.size() == _nbArtifact)
		{
			boolean rst = true;
			for (int id : _engrave.values())
			{
				if (id != clan.getClanId())
					rst = false;
			}
			if(rst)
			{
				_engrave.clear();
				setOwner(clan);
			}
			else
				getSiege().announceToPlayer("Clan " + clan.getName() + " has finished to engrave one of the rulers.", true);
		}
		else
			getSiege().announceToPlayer("Clan " + clan.getName() + " has finished to engrave one of the rulers.", true);
	}

	public void addToTreasury(int amount)
    {
        if (getOwnerId() <= 0) return;

        if (_name.equalsIgnoreCase("Schuttgart") || _name.equalsIgnoreCase("Goddard"))
        {
        	Castle rune = CastleManager.getInstance().getCastle("rune");
        	if (rune != null )
        	{
        		int runeTax = (int)(amount * rune.getTaxRate());
        		if (rune.getOwnerId() > 0 ) rune.addToTreasury(runeTax);
        		amount -= runeTax;
        	}
        }
        if (!_name.equalsIgnoreCase("aden") && !_name.equalsIgnoreCase("Rune") && !_name.equalsIgnoreCase("Schuttgart") && !_name.equalsIgnoreCase("Goddard"))    // If current castle instance is not Aden, Rune, Goddard or Schuttgart.
        {
            Castle aden = CastleManager.getInstance().getCastle("aden");
            if (aden != null)
            {
                int adenTax = (int)(amount * aden.getTaxRate());        // Find out what Aden gets from the current castle instance's income
                if (aden.getOwnerId() > 0) aden.addToTreasury(adenTax); // Only bother to really add the tax to the treasury if not npc owned

                amount -= adenTax; // Subtract Aden's income from current castle instance's income
            }
        }

        addToTreasuryNoTax(amount);
    }

    /** Add amount to castle instance's treasury (warehouse), no tax paying. */
    public boolean addToTreasuryNoTax(int amount)
    {
        if (getOwnerId() <= 0) return false;

        if (amount < 0) {
        	amount *= -1;
        	if (_treasury < amount) return false;
        	_treasury -= amount;
        } else {
        	if ((long)_treasury + amount > Integer.MAX_VALUE) _treasury = Integer.MAX_VALUE;
        	else _treasury += amount;
        }

        Connection con = null;
        try
        {
            con = L2DatabaseFactory.getInstance().getConnection();
            PreparedStatement statement = con.prepareStatement("Update castle set treasury = ? where id = ?");
            statement.setInt(1, getTreasury());
            statement.setInt(2, getCastleId());
            statement.execute();
            statement.close();
        }
        catch (Exception e) {}
        finally {try { con.close(); } catch (Exception e) {}}
        return true;
    }

	/**
	 * Move non clan members off castle area and to nearest town.<BR><BR>
	 */
	public void banishForeigners()
    {
		getCastleZone().banishForeigners(getOwnerId());
    }

    /**
     * Return true if object is inside the zone
     */
    public boolean checkIfInZone(int x, int y, int z)
    {
    	return _zone.isInsideZone(x, y, z);
    }

	/**
	 * Sets this castles zone
	 * 
	 * @param zone
	 */
	public void setCastleZone(L2CastleZone zone)
	{
		_castleZone = zone;
	}
	
	public void setZone(L2SiegeZone zone)
	{
		_zone = zone;
	}

	public L2SiegeZone getZone()
	{
		return _zone;
	}
	
	public L2CastleZone getCastleZone()
	{
		return _castleZone;
	}
	

	public void setTeleZone(L2CastleTeleportZone zone)
	{
		_teleZone = zone;
	}

	public L2CastleTeleportZone getTeleZone()
	{
		return _teleZone;
	}

    /**
     * Get the objects distance to this castle
     * @param obj
     * @return
     */
    public double getDistance(L2Object obj)
    {
    	return _zone.getDistanceToZone(obj);
    }

	public void closeDoor(L2PcInstance activeChar, int doorId)
	{
	    openCloseDoor(activeChar, doorId, false);
	}

	public void openDoor(L2PcInstance activeChar, int doorId)
	{
	    openCloseDoor(activeChar, doorId, true);
	}

	public void openCloseDoor(L2PcInstance activeChar, int doorId, boolean open)
	{
	    if (activeChar.getClanId() != getOwnerId())
	        return;

	    L2DoorInstance door = getDoor(doorId);
        if (door != null)
        {
            if (open)
                door.openMe();
            else
                door.closeMe();
        }
	}

	// This method updates the castle tax rate
	public void setOwner(L2Clan clan)
	{
		// Remove old owner
	    if (getOwnerId() > 0 && (clan == null || clan.getClanId() != getOwnerId()))
	    {
	        L2Clan oldOwner = ClanTable.getInstance().getClan(getOwnerId());// Try to find clan instance
			if (oldOwner != null)
			{
				if (_formerOwner == null)
				{
					_formerOwner = oldOwner;
					if (Config.REMOVE_CASTLE_CIRCLETS)
					{
						CastleManager.getInstance().removeCirclet(_formerOwner,getCastleId());
					}
				}
				oldOwner.setHasCastle(0); // Unset has castle flag for old owner
        		new Announcements().announceToAll(oldOwner.getName() + " has lost " + getName() + " castle!");
			}
	    }

	    updateOwnerInDB(clan); // Update in database
	    setShowNpcCrest(false);

	    if (getSiege().getIsInProgress())// If siege in progress
        	getSiege().midVictory();// Mid victory phase of siege

	    updateClansReputation();
	}

	public void removeOwner(L2Clan clan)
	{
		if (clan != null)
		{
			_formerOwner = clan;
			if (Config.REMOVE_CASTLE_CIRCLETS)
			{
				CastleManager.getInstance().removeCirclet(_formerOwner,getCastleId());
			}
			clan.setHasCastle(0);
			new Announcements().announceToAll(clan.getName() + " has lost " +getName() + " castle");
			clan.broadcastToOnlineMembers(new PledgeShowInfoUpdate(clan));
		}

		updateOwnerInDB(null);
		if (getSiege().getIsInProgress())
			getSiege().midVictory();

		updateClansReputation();
	}

    // This method updates the castle tax rate
    public void setTaxPercent(L2PcInstance activeChar, int taxPercent)
    {
        int maxTax;
        switch(SevenSigns.getInstance().getSealOwner(SevenSigns.SEAL_STRIFE))
        {
            case SevenSigns.CABAL_DAWN:
                maxTax = 25;
                break;
            case SevenSigns.CABAL_DUSK:
                maxTax = 5;
                break;
            default: // no owner
            	maxTax = 15;
        }

        if (taxPercent < 0 || taxPercent > maxTax)
        {
            activeChar.sendMessage("Tax value must be between 0 and "+maxTax+".");
            return;
        }

        setTaxPercent(taxPercent);
        activeChar.sendMessage(getName() + " castle tax changed to " + taxPercent + "%.");
    }

    public void setTaxPercent(int taxPercent)
    {
        _taxPercent = taxPercent;
        _taxRate = _taxPercent / 100.0;

        Connection con = null;
        try
        {
            con = L2DatabaseFactory.getInstance().getConnection();
            PreparedStatement statement = con.prepareStatement("Update castle set taxPercent = ? where id = ?");
            statement.setInt(1, taxPercent);
            statement.setInt(2, getCastleId());
            statement.execute();
            statement.close();
        }
        catch (Exception e) {}
        finally {try { con.close(); } catch (Exception e) {}}
    }

	/**
	 * Respawn all doors on castle grounds<BR><BR>
	 */
	public void spawnDoor()
    {
	    spawnDoor(false);
    }

	/**
	 * Respawn all doors on castle grounds<BR><BR>
	 */
	public void spawnDoor(boolean isDoorWeak)
	{
		for(int i = 0; i < getDoors().size(); i++)
		{
			L2DoorInstance door = getDoors().get(i);
			if(door.getCurrentHp() <= 0)
			{
				door.decayMe(); // Kill current if not killed already
				door = DoorTable.parseList(door.getName() 					+ ";" + 
										   door.getDoorId() 				+ ";" + 
										   door.getX() 						+ ";" + 
										   door.getY() 						+ ";" + 
										   door.getZ() 						+ ";" + 
										   door.getXMin() 					+ ";" + 
										   door.getYMin() 					+ ";" + 
										   door.getZMin() 					+ ";" + 
										   door.getXMax() 					+ ";" + 
										   door.getYMax() 					+ ";" + 
										   door.getZMax() 					+ ";" + 
										   door.getMaxHp() 					+ ";" + 
										   door.getTemplate().basePDef 		+ ";" + 
										   door.getTemplate().baseMDef);

				if(isDoorWeak)
				{
					door.setCurrentHp(door.getMaxHp() / 2);
				}

				door.spawnMe(door.getX(), door.getY(), door.getZ());
				getDoors().set(i, door);
			}
			else if(door.getOpen())
			{
				door.closeMe();
			}

			door = null;
		}
		loadDoorUpgrade(); // Check for any upgrade the doors may have
	}

	// This method upgrade door
	public void upgradeDoor(int doorId, int hp, int pDef, int mDef)
	{
        L2DoorInstance door = getDoor(doorId);
	    if (door == null)
	        return;

        if (door != null && door.getDoorId() == doorId)
        {
        	door.setCurrentHp(door.getMaxHp() + hp);

        	saveDoorUpgrade(doorId, hp, pDef, mDef);
            return;
        }
	}

	// =========================================================
	// Method - Private
	// This method loads castle
	private void load()
	{
        Connection con = null;
        try
        {
            PreparedStatement statement;
            ResultSet rs;

            con = L2DatabaseFactory.getInstance().getConnection();

            statement = con.prepareStatement("Select * from castle where id = ?");
            statement.setInt(1, getCastleId());
            rs = statement.executeQuery();

            while (rs.next())
            {
        	    _name = rs.getString("name");
        	    //_OwnerId = rs.getInt("ownerId");

        	    _siegeDate = Calendar.getInstance();
        	    _siegeDate.setTimeInMillis(rs.getLong("siegeDate"));

        	    _siegeDayOfWeek = rs.getInt("siegeDayOfWeek");
        	    if (_siegeDayOfWeek < 1 || _siegeDayOfWeek > 7)
        	        _siegeDayOfWeek = 7;

        	    _siegeHourOfDay = rs.getInt("siegeHourOfDay");
        	    if (_siegeHourOfDay < 0 || _siegeHourOfDay > 23)
        	        _siegeHourOfDay = 20;

        	    _taxPercent = rs.getInt("taxPercent");
        	    _treasury = rs.getInt("treasury");
        	    _showNpcCrest = rs.getBoolean("showNpcCrest");
            }

            statement.close();

            _taxRate = _taxPercent / 100.0;

            statement = con.prepareStatement("Select clan_id from clan_data where hasCastle = ?");
            statement.setInt(1, getCastleId());
            rs = statement.executeQuery();

            while (rs.next())
            {
        	    _ownerId = rs.getInt("clan_id");
            }

            if (getOwnerId() > 0)
            {
                L2Clan clan = ClanTable.getInstance().getClan(getOwnerId());                        // Try to find clan instance
                ThreadPoolManager.getInstance().scheduleGeneral(new CastleUpdater(clan, 1), 3600000);     // Schedule owner tasks to start running
            }

            statement.close();
        }
        catch (Exception e)
        {
            System.out.println("Exception: loadCastleData(): " + e.getMessage());
            e.printStackTrace();
        }
        finally {try { con.close(); } catch (Exception e) {}}
	}

	// This method loads castle door data from database
	private void loadDoor()
	{
        Connection con = null;
        try
        {
            con = L2DatabaseFactory.getInstance().getConnection();
            PreparedStatement statement = con.prepareStatement("Select * from castle_door where castleId = ?");
            statement.setInt(1, getCastleId());
            ResultSet rs = statement.executeQuery();

            while (rs.next())
            {
                // Create list of the door default for use when respawning dead doors
                _doorDefault.add(rs.getString("name")
                        + ";" + rs.getInt("id")
                        + ";" + rs.getInt("x")
                        + ";" + rs.getInt("y")
                        + ";" + rs.getInt("z")
                        + ";" + rs.getInt("range_xmin")
                        + ";" + rs.getInt("range_ymin")
                        + ";" + rs.getInt("range_zmin")
                        + ";" + rs.getInt("range_xmax")
                        + ";" + rs.getInt("range_ymax")
                        + ";" + rs.getInt("range_zmax")
                        + ";" + rs.getInt("hp")
                        + ";" + rs.getInt("pDef")
                        + ";" + rs.getInt("mDef"));

                L2DoorInstance door = DoorTable.parseList(_doorDefault.get(_doorDefault.size() - 1));
				door.spawnMe(door.getX(), door.getY(),door.getZ());
                _doors.add(door);
                DoorTable.getInstance().putDoor(door);
            }

            statement.close();
        }
        catch (Exception e)
        {
            System.out.println("Exception: loadCastleDoor(): " + e.getMessage());
            e.printStackTrace();
        }
        finally {try { con.close(); } catch (Exception e) {}}
	}

	// This method loads castle door upgrade data from database
	private void loadDoorUpgrade()
	{
        Connection con = null;
        try
        {
            con = L2DatabaseFactory.getInstance().getConnection();
            PreparedStatement statement = con.prepareStatement("Select * from castle_doorupgrade where doorId in (Select Id from castle_door where castleId = ?)");
            statement.setInt(1, getCastleId());
            ResultSet rs = statement.executeQuery();

            while (rs.next())
            {
                upgradeDoor(rs.getInt("id"), rs.getInt("hp"), rs.getInt("pDef"), rs.getInt("mDef"));
            }

            statement.close();
        }
        catch (Exception e)
        {
            System.out.println("Exception: loadCastleDoorUpgrade(): " + e.getMessage());
            e.printStackTrace();
        }
        finally {try { con.close(); } catch (Exception e) {}}
	}

	public void removeDoorUpgrade()
	{
        Connection con = null;
        try
        {
            con = L2DatabaseFactory.getInstance().getConnection();
            PreparedStatement statement = con.prepareStatement("delete from castle_doorupgrade where doorId in (select id from castle_door where castleId=?)");
            statement.setInt(1, getCastleId());
            statement.execute();
            statement.close();
        }
        catch (Exception e)
        {
            System.out.println("Exception: removeDoorUpgrade(): " + e.getMessage());
            e.printStackTrace();
        }
        finally {try { con.close(); } catch (Exception e) {}}
	}

	private void saveDoorUpgrade(int doorId, int hp, int pDef, int mDef)
	{
        Connection con = null;
        try
        {
            con = L2DatabaseFactory.getInstance().getConnection();
            PreparedStatement statement = con.prepareStatement("INSERT INTO castle_doorupgrade (doorId, hp, pDef, mDef) values (?,?,?,?)");
            statement.setInt(1, doorId);
            statement.setInt(2, hp);
            statement.setInt(3, pDef);
            statement.setInt(4, mDef);
            statement.execute();
            statement.close();
        }
        catch (Exception e)
        {
            System.out.println("Exception: saveDoorUpgrade(int doorId, int hp, int pDef, int mDef): " + e.getMessage());
            e.printStackTrace();
        }
        finally
        {
            try { con.close(); } catch (Exception e) {}
        }
	}

	private void updateOwnerInDB(L2Clan clan)
	{
		if (clan != null)
		    _ownerId = clan.getClanId(); // Update owner id property
		else
                {
			_ownerId = 0; // Remove owner
                }

	    Connection con = null;
        try
        {
            con = L2DatabaseFactory.getInstance().getConnection();
            PreparedStatement statement;

            // ============================================================================
            // NEED TO REMOVE HAS CASTLE FLAG FROM CLAN_DATA
            // SHOULD BE CHECKED FROM CASTLE TABLE
            statement = con.prepareStatement("UPDATE clan_data SET hasCastle=0 WHERE hasCastle=?");
            statement.setInt(1, getCastleId());
            statement.execute();
            statement.close();

            statement = con.prepareStatement("UPDATE clan_data SET hasCastle=? WHERE clan_id=?");
            statement.setInt(1, getCastleId());
            statement.setInt(2, getOwnerId());
            statement.execute();
            statement.close();
            // ============================================================================

            // Announce to clan memebers
            if (clan != null)
            {
    		    clan.setHasCastle(getCastleId()); // Set has castle flag for new owner
    		    new Announcements().announceToAll(clan.getName() + " has taken " + getName() + " castle!");
    		    clan.broadcastToOnlineMembers(new PledgeShowInfoUpdate(clan));

    		    ThreadPoolManager.getInstance().scheduleGeneral(new CastleUpdater(clan, 1), 3600000);	// Schedule owner tasks to start running
            }
        }
        catch (Exception e)
        {
            System.out.println("Exception: updateOwnerInDB(L2Clan clan): " + e.getMessage());
            e.printStackTrace();
        }
        finally
        {
            try { con.close(); } catch (Exception e) {}
        }
	}

	// =========================================================
	// Property
	public final int getCastleId()
	{
		return _castleId;
	}

	public final L2DoorInstance getDoor(int doorId)
	{
	    if (doorId <= 0)
	        return null;

        for (int i = 0; i < getDoors().size(); i++)
        {
            L2DoorInstance door = getDoors().get(i);
            if (door.getDoorId() == doorId)
                return door;
        }
		return null;
	}

	public final List<L2DoorInstance> getDoors()
	{
		return _doors;
	}

	public final String getName()
	{
	    return _name;
	}

	public final int getOwnerId()
	{
		return _ownerId;
	}

	public final Siege getSiege()
	{
            if (_siege == null) _siege = new Siege(new Castle[] {this});
		return _siege;
	}

	public final Calendar getSiegeDate() 
        { 
                return _siegeDate; 
        }

	public final int getSiegeDayOfWeek() 
        { 
                return _siegeDayOfWeek; 
        }

	public final int getSiegeHourOfDay() 
        { 
                return _siegeHourOfDay; 
        }

	public final int getTaxPercent()
	{
		return _taxPercent;
	}

	public final double getTaxRate()
	{
		return _taxRate;
	}

	public final int getTreasury()
	{
		return _treasury;
	}

	public final boolean getShowNpcCrest()
	{
	return _showNpcCrest;
	}

	public final void setShowNpcCrest(boolean showNpcCrest)
	{
		if(_showNpcCrest != showNpcCrest)
		{
			_showNpcCrest = showNpcCrest;
			updateShowNpcCrest();
		}
	}

	public List<SeedProduction> getSeedProduction(int period)
	{
		return (period == CastleManorManager.PERIOD_CURRENT ? _production : _productionNext);
	}

	public List<CropProcure> getCropProcure(int period)
	{
		return (period == CastleManorManager.PERIOD_CURRENT ? _procure : _procureNext);
	}

	public void setSeedProduction(List<SeedProduction> seed, int period)
	{
		if (period == CastleManorManager.PERIOD_CURRENT)
			_production = seed;
		else
			_productionNext = seed;
	}

	public void setCropProcure(List<CropProcure> crop, int period)
	{
		if (period == CastleManorManager.PERIOD_CURRENT)
			_procure = crop;
		else
			_procureNext = crop;
	}

	public synchronized SeedProduction getSeed(int seedId, int period)
	{
		for (SeedProduction seed : getSeedProduction(period))
		{
			if (seed.getId() == seedId)
			{
				return seed;
			}
		}
		return null;
	}

	public synchronized CropProcure getCrop(int cropId, int period)
	{
		for (CropProcure crop : getCropProcure(period) )
		{
			if (crop.getId() == cropId)
			{
				return crop;
			}
		}
		return null;
	}

	public int getManorCost (int period)
	{
		List<CropProcure> procure;
		List<SeedProduction> production;

		if (period == CastleManorManager.PERIOD_CURRENT)
		{
			procure = _procure;
			production = _production;
		} else 
                {
			procure = _procureNext;
			production = _productionNext;
		}

		int total = 0;
		if (production != null)
		{
			for (SeedProduction seed : production)
			{
				total += L2Manor.getInstance().getSeedBuyPrice(seed.getId()) * seed.getStartProduce();
			}
		}
		if (procure != null)
		{
			for (CropProcure crop: procure)
			{
				total += crop.getPrice() * crop.getStartAmount();
			}
		}
		return total;
	}

	//save manor production data
	public void saveSeedData()
	{
        Connection con = null;
        PreparedStatement statement;

        try
        {
            con = L2DatabaseFactory.getInstance().getConnection();

            statement = con.prepareStatement(CASTLE_MANOR_DELETE_PRODUCTION);
            statement.setInt(1, getCastleId());

            statement.execute();
            statement.close();

            if (_production != null)
            {
            	int count = 0;
            	String query = "INSERT INTO castle_manor_production VALUES ";
            	String values[] = new String[_production.size()];
            	for(SeedProduction s : _production)
            	{
            		values[count] = "("+getCastleId()+","+s.getId()+","+s.getCanProduce()+","+s.getStartProduce()+","+s.getPrice()+","+CastleManorManager.PERIOD_CURRENT+")";
            		count++;
            	}
            	if (values.length > 0)
            	{
            		query += values[0];
            		for (int i=1; i<values.length; i++)
            		{
            			query += "," + values[i];
            		}
            		statement = con.prepareStatement(query);
            		statement.execute();
            		statement.close();
            	}
            }

            if (_productionNext != null)
            {
            	int count = 0;
            	String query = "INSERT INTO castle_manor_production VALUES ";
            	String values[] = new String[_productionNext.size()];
            	for(SeedProduction s : _productionNext)
            	{
            		values[count] = "("+getCastleId()+","+s.getId()+","+s.getCanProduce()+","+s.getStartProduce()+","+s.getPrice()+","+CastleManorManager.PERIOD_NEXT+")";
            		count++;
            	}
            	if (values.length > 0)
            	{
            		query += values[0];
            		for (int i=1;i<values.length;i++)
            		{
            			query += "," + values[i];
            		}
            		statement = con.prepareStatement(query);
            		statement.execute();
            		statement.close();
            	}
        	}
     	} catch (Exception e)
     	{
     		_log.info("Error adding seed production data for castle " + getName() +": " + e.getMessage());
     	} finally {
     		try { con.close(); } catch (Exception e) {}
     	}
	}

	//save manor production data for specified period
	public void saveSeedData(int period)
	{
		Connection con = null;
		PreparedStatement statement;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();

            statement = con.prepareStatement(CASTLE_MANOR_DELETE_PRODUCTION_PERIOD);
            statement.setInt(1, getCastleId());
            statement.setInt(2, period);
            statement.execute();
            statement.close();

            List<SeedProduction> prod = null;
            prod = getSeedProduction(period);

            if (prod != null)
            {
            	int count = 0;
            	String query = "INSERT INTO castle_manor_production VALUES ";
            	String values[] = new String[prod.size()];
            	for(SeedProduction s : prod)
            	{
            		values[count] = "("+getCastleId()+","+s.getId()+","+s.getCanProduce()+","+s.getStartProduce()+","+s.getPrice()+","+period+")";
            		count++;
            	}
            	if (values.length > 0)
            	{
            		query += values[0];
            		for (int i=1;i<values.length;i++)
            		{
            			query += "," + values[i];
            		}
            		statement = con.prepareStatement(query);
            		statement.execute();
            		statement.close();
            	}
            }
		} catch (Exception e)
		{
			_log.info("Error adding seed production data for castle " + getName() +": " + e.getMessage());
		} finally {
            try { con.close(); } catch (Exception e) {}
        }
    }

	//save crop procure data
	public void saveCropData()
	{
		Connection con = null;
		PreparedStatement statement;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();

			statement = con.prepareStatement(CASTLE_MANOR_DELETE_PROCURE);
			statement.setInt(1, getCastleId());
			statement.execute();
			statement.close();
			if (_procure != null)
			{
				int count = 0;
				String query = "INSERT INTO castle_manor_procure VALUES ";
				String values[] = new String[_procure.size()];
				for (CropProcure cp : _procure)
				{
					values[count] = "("+getCastleId()+","+cp.getId()+","+cp.getAmount()+","+cp.getStartAmount()+","+cp.getPrice()+","+cp.getReward()+","+CastleManorManager.PERIOD_CURRENT+")";
					count++;
				}
				if (values.length > 0)
				{
					query += values[0];
					for (int i=1;i<values.length;i++)
					{
						query += "," + values[i];
					}
					statement = con.prepareStatement(query);
					statement.execute();
					statement.close();
				}
			}
			if (_procureNext != null)
			{
				int count = 0;
				String query = "INSERT INTO castle_manor_procure VALUES ";
				String values[] = new String[_procureNext.size()];
				for (CropProcure cp : _procureNext)
				{
					values[count] = "("+getCastleId()+","+cp.getId()+","+cp.getAmount()+","+cp.getStartAmount()+","+cp.getPrice()+","+cp.getReward()+","+CastleManorManager.PERIOD_NEXT+")";
					count++;
				}
				if (values.length > 0)
				{
					query += values[0];
					for (int i=1;i<values.length;i++)
					{
						query += "," + values[i];
					}
					statement = con.prepareStatement(query);
					statement.execute();
					statement.close();
				}
			}
		} catch (Exception e) {
			_log.info("Error adding crop data for castle " + getName() +": " + e.getMessage());
		} finally {
			try {
				con.close();
			} catch (Exception e) { }
		}
    }

	//	save crop procure data for specified period
	public void saveCropData(int period) {
		Connection con = null;
		PreparedStatement statement;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();

			statement = con.prepareStatement(CASTLE_MANOR_DELETE_PROCURE_PERIOD);
			statement.setInt(1, getCastleId());
			statement.setInt(2, period);
			statement.execute();
			statement.close();

			List<CropProcure> proc = null;
			proc = getCropProcure(period);

			if (proc != null)
			{
				int count = 0;
				String query = "INSERT INTO castle_manor_procure VALUES ";
				String values[] = new String[proc.size()];

				for (CropProcure cp : proc)
				{
					values[count] = "("+getCastleId()+","+cp.getId()+","+cp.getAmount()+","+cp.getStartAmount()+","+cp.getPrice()+","+cp.getReward()+","+period+")";
					count++;
				}
				if (values.length > 0)
				{
					query += values[0];
					for (int i=1;i<values.length;i++)
					{
						query += "," + values[i];
					}
					statement = con.prepareStatement(query);
					statement.execute();
					statement.close();
				}
			}
		} catch (Exception e) {
			_log.info("Error adding crop data for castle " + getName() +": " + e.getMessage());
		} finally {
			try {
				con.close();
			} catch (Exception e) { }
		}
    }

	public void updateCrop (int cropId, int amount, int period)
	{
		Connection con = null;
		PreparedStatement statement;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();

			statement = con.prepareStatement(CASTLE_UPDATE_CROP);
			statement.setInt(1, amount);
			statement.setInt(2, cropId);
			statement.setInt(3, getCastleId());
			statement.setInt(4, period);
			statement.execute();
			statement.close();
		} catch (Exception e) {
			_log.info("Error adding crop data for castle " + getName() +": " + e.getMessage());
		} finally {
			try {
				con.close();
			} catch (Exception e) { }
		}
    }

	public void updateSeed (int seedId, int amount, int period)
	{
		Connection con = null;
		PreparedStatement statement;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();

			statement = con.prepareStatement(CASTLE_UPDATE_SEED);
			statement.setInt(1, amount);
			statement.setInt(2, seedId);
			statement.setInt(3, getCastleId());
			statement.setInt(4, period);
			statement.execute();
			statement.close();
		} catch (Exception e) {
			_log.info("Error adding seed production data for castle " + getName() +": " + e.getMessage());
		} finally {
			try {
				con.close();
			} catch (Exception e) { }
		}
    }

	public boolean isNextPeriodApproved()
	{
		return _isNextPeriodApproved;
    }

	public void setNextPeriodApproved(boolean val)
	{
		_isNextPeriodApproved = val;
	}

	public void updateShowNpcCrest()
	{
		Connection con = null;
		PreparedStatement statement;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();

			statement = con.prepareStatement("UPDATE castle SET showNpcCrest = ? WHERE id = ?");
			statement.setString(1, String.valueOf(getShowNpcCrest()));
			statement.setInt(2, getCastleId());
			statement.execute();
			statement.close();
		}
		catch (Exception e)
		{
			_log.info("Error saving showNpcCrest for castle " + getName() + ": " + e.getMessage());
		}
		finally
		{
			try
			{
				con.close();
			}
			catch (Exception e)
			{
			}
		}
	}
	
	public void updateClansReputation()
    {
        if (_formerOwner != null )
        {
            if (_formerOwner != ClanTable.getInstance().getClan(getOwnerId()))
            {
                int maxreward = Math.max(0,_formerOwner.getReputationScore());
            	_formerOwner.setReputationScore(_formerOwner.getReputationScore()-Config.LOOSE_CASTLE_POINTS, true);
                L2Clan owner = ClanTable.getInstance().getClan(getOwnerId());
                if (owner != null)
                {
                	owner.setReputationScore(owner.getReputationScore()+Math.min(Config.TAKE_CASTLE_POINTS,maxreward), true);
                	owner.broadcastToOnlineMembers(new PledgeShowInfoUpdate(owner));
                }
            }
            else
            	_formerOwner.setReputationScore(_formerOwner.getReputationScore()+Config.CASTLE_DEFENDED_POINTS, true);

            _formerOwner.broadcastToOnlineMembers(new PledgeShowInfoUpdate(_formerOwner));
        }
        else
        {
            L2Clan owner = ClanTable.getInstance().getClan(getOwnerId());
            if (owner != null)
            {
            	owner.setReputationScore(owner.getReputationScore()+Config.TAKE_CASTLE_POINTS, true);
            	owner.broadcastToOnlineMembers(new PledgeShowInfoUpdate(owner));
            }
        }
    }
	
	public void createClanGate(int x, int y, int z)
	{
		_gate[0] = x;
		_gate[1] = y;
		_gate[2] = z;
	}

	/** Optimized as much as possible. */
	public void destroyClanGate()
	{
		_gate[0] = Integer.MIN_VALUE;
	}

	/**
	 * This method must always be called before using gate coordinate retrieval methods! Optimized as much as possible.
	 * 
	 * @return is a Clan Gate available
	 */

	public boolean isGateOpen()
	{
		return _gate[0] != Integer.MIN_VALUE;
	}

	public int getGateX()
	{
		return _gate[0];
	}

	public int getGateY()
	{
		return _gate[1];
	}

	public int getGateZ()
	{
		return _gate[2];
	}
}