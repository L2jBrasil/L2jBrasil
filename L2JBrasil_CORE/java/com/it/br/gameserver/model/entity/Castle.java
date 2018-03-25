package com.it.br.gameserver.model.entity;

import com.it.br.Config;
import com.it.br.gameserver.Announcements;
import com.it.br.gameserver.CastleUpdater;
import com.it.br.gameserver.SevenSigns;
import com.it.br.gameserver.ThreadPoolManager;
import com.it.br.gameserver.database.dao.CastleDao;
import com.it.br.gameserver.database.dao.CastleManorDao;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class Castle
{
    protected static Logger _log = LoggerFactory.getLogger(Castle.class);

    // =========================================================
    // Data Field
    private List<CropProcure> _procure = new ArrayList<>();
    private List<SeedProduction> _production = new ArrayList<>();
    private List<CropProcure> _procureNext = new ArrayList<>();
    private List<SeedProduction> _productionNext = new ArrayList<>();
    private boolean _isNextPeriodApproved = false;

	private int _castleId = 0;
	private List<L2DoorInstance> _doors = new ArrayList<>();

	public List<String> getDoorDefault() {
		return _doorDefault;
	}

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
        CastleDao.loadById(this);
		CastleDao.loadDoor(this);
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

		CastleDao.updateTreasuryTax(getTreasury(), getCastleId());

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

        CastleDao.updateTaxPercent(getCastleId(), taxPercent);
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
		CastleDao.loadDoorUpgrade(this); // Check for any upgrade the doors may have
	}

	// This method upgrade door
	public void upgradeDoor(int doorId, int hp, int pDef, int mDef)
	{
        L2DoorInstance door = getDoor(doorId);
	    if (door == null)
	        return;

        if (door.getDoorId() == doorId)
        {
        	door.setCurrentHp(door.getMaxHp() + hp);
			CastleDao.insertDoorUpgrade(doorId, hp, pDef, mDef);
        }
	}

	public void setName(String _name) {
		this._name = _name;
	}

	public void setSiegeDate(Calendar _siegeDate) {
		this._siegeDate = _siegeDate;
	}

	public void setSiegeDayOfWeek(int _siegeDayOfWeek) {
		this._siegeDayOfWeek = _siegeDayOfWeek;
	}

	public void setSiegeHourOfDay(int _siegeHourOfDay) {
		this._siegeHourOfDay = _siegeHourOfDay;
	}

	public void setTreasury(int _treasury) {
		this._treasury = _treasury;
	}

	public void setOwnerId(int _ownerId) {
		this._ownerId = _ownerId;
	}

	public void setTaxRate(double _taxRate) {
		this._taxRate = _taxRate;
	}

	private void updateOwnerInDB(L2Clan clan)
	{
		if (clan != null) _ownerId = clan.getClanId(); // Update owner id property
		else _ownerId = 0; // Remove owner

		CastleDao.updateClanOwner(this);

		// Announce to clan memebers
		if (clan != null)
		{
			clan.setHasCastle(getCastleId()); // Set has castle flag for new owner
			new Announcements().announceToAll(clan.getName() + " has taken " + getName() + " castle!");
			clan.broadcastToOnlineMembers(new PledgeShowInfoUpdate(clan));

			ThreadPoolManager.getInstance().scheduleGeneral(new CastleUpdater(clan, 1), 3600000);	// Schedule owner tasks to start running
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
			CastleDao.updateShowNpcCrest(this);
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
		CastleManorDao.saveSeed(this);
	}

	//save crop procure data
	public void saveCropData()
	{
		CastleManorDao.saveCrop(this);
    }

	//	save crop procure data for specified period
	public void saveCropData(int period) {
		CastleManorDao.saveCrop(this, period);
	}

	public boolean isNextPeriodApproved()
	{
		return _isNextPeriodApproved;
    }

	public void setNextPeriodApproved(boolean val)
	{
		_isNextPeriodApproved = val;
	}
	
	private void updateClansReputation()
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