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

import com.it.br.Config;
import com.it.br.gameserver.database.L2DatabaseFactory;
import com.it.br.gameserver.datatables.sql.SkillTable;
import com.it.br.gameserver.model.L2Character;
import com.it.br.gameserver.model.L2Clan;
import com.it.br.gameserver.model.L2Object;
import com.it.br.gameserver.model.Location;
import com.it.br.gameserver.model.actor.instance.L2PcInstance;
import com.it.br.gameserver.model.entity.Castle;
import com.it.br.gameserver.model.entity.Siege;
import com.it.br.gameserver.network.SystemMessageId;
import com.it.br.gameserver.network.serverpackets.SystemMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;

public class SiegeManager
{
    private static final Logger _log = LoggerFactory.getLogger(SiegeManager.class);
    private SiegeManager(){}

    private static SiegeManager _instance;
    public static final SiegeManager getInstance()
    {
        if (_instance == null)
        {
        	_instance = new SiegeManager();
        	_instance.loadSiegeConfig();
        }
        return _instance;
    }

    // Method - Public
    public final void addSiegeSkills(L2PcInstance character)
    {
        character.addSkill(SkillTable.getInstance().getInfo(246, 1), false);
        character.addSkill(SkillTable.getInstance().getInfo(247, 1), false);
    }

    /**
     * Return true if character summon<BR><BR>
     * @param activeChar The L2Character of the character can summon
     */
    public final boolean checkIfOkToSummon(L2Character activeChar, boolean isCheckOnly)
    {
        if (activeChar == null || !(activeChar instanceof L2PcInstance)) 
        	return false;

        SystemMessage sm = new SystemMessage(SystemMessageId.S1_S2);
        L2PcInstance player = (L2PcInstance)activeChar;
        if(player.getPet() != null)
        	return false;

        Castle castle = CastleManager.getInstance().getCastle(player);

        if(activeChar.isInsideZone(L2Character.ZONE_CASTLE))
        	sm.addString("You may not summon from your current location.");
        else if (castle == null || castle.getCastleId() <= 0)
            sm.addString("You must be on castle ground to summon this");
        else if (!castle.getSiege().getIsInProgress())
            sm.addString("You can only summon this during a siege.");
        else if (player.getClanId() != 0 && castle.getSiege().getAttackerClan(player.getClanId()) == null)
            sm.addString("You can only summon this as a registered attacker.");
        else
            return true;

        if (!isCheckOnly) 
        {
        	player.sendPacket(sm);
        }
        return false;
    }

    /**
     * Return true if the clan is registered or owner of a castle<BR><BR>
     * @param clan The L2Clan of the player
     */
    public final boolean checkIsRegistered(L2Clan clan, int castleid)
    {
        if (clan == null)
        	return false;

        if (clan.getHasCastle() > 0)
        	return true;

        Connection con = null;
        boolean register = false;
        try
        {
            con = L2DatabaseFactory.getInstance().getConnection();
            PreparedStatement statement = con.prepareStatement("SELECT clan_id FROM siege_clans where clan_id=? and castle_id=?");
            statement.setInt(1, clan.getClanId());
            statement.setInt(2, castleid);
            ResultSet rs = statement.executeQuery();

            while (rs.next())
            {
                register = true;
                break;
            }

            rs.close();
            statement.close();
        }
        catch (Exception e)
        {
            System.out.println("Exception: checkIsRegistered(): " + e.getMessage());
            e.printStackTrace();
        }
        finally
        {
            try { con.close(); } catch (Exception e) {}
        }
        return register;
    }

    public final void removeSiegeSkills(L2PcInstance character)
    {
        character.removeSkill(SkillTable.getInstance().getInfo(246, 1));
        character.removeSkill(SkillTable.getInstance().getInfo(247, 1));
    }

    // --------------------------------------------- //
    // -            SIEGE PROPIETERS               - //
    // --------------------------------------------- //
    // =========================================================
    private int _attackerMaxClans;
    private int _attackerRespawnDelay;
    private int _defenderMaxClans;
    private int _defenderRespawnDelay;
    private Map<Integer, List<SiegeSpawn>> _artefactSpawnList;
    private Map<Integer, List<SiegeSpawn>>  _controlTowerSpawnList;
    private int _controlTowerLosePenalty;
    private int _flagMaxCount;
    private int _siegeClanMinLevel;
    private int _siegeLength;
	private boolean _teleport_to_siege = false;
	private boolean _teleport_to_siege_town = false;
    private List<Siege> _sieges;
    public static int DAY_TO_SIEGE;
    // =========================================================
    private final void loadSiegeConfig()
    {
        try(InputStream is = new FileInputStream(new File(Config.SIEGE_FILE)))
        {
            Properties siegeSettings = new Properties();
            siegeSettings.load(is);

            _siegeLength = Integer.decode(siegeSettings.getProperty("SiegeLength", "120"));
            _flagMaxCount = Integer.decode(siegeSettings.getProperty("MaxFlags", "1"));
            _teleport_to_siege = Boolean.parseBoolean(siegeSettings.getProperty("AllowTeleportToSiege", "false"));
            _teleport_to_siege_town = Boolean.parseBoolean(siegeSettings.getProperty("AllowTeleportToSiegeTown", "false"));
            _siegeClanMinLevel = Integer.decode(siegeSettings.getProperty("SiegeClanMinLevel", "4"));
            DAY_TO_SIEGE = Integer.parseInt(siegeSettings.getProperty("DayToSiege", "14"));
            _attackerMaxClans = Integer.decode(siegeSettings.getProperty("AttackerMaxClans", "500"));
            _defenderMaxClans = Integer.decode(siegeSettings.getProperty("DefenderMaxClans", "500"));
            _defenderRespawnDelay = Integer.decode(siegeSettings.getProperty("DefenderRespawn", "20000"));
            _attackerRespawnDelay = Integer.decode(siegeSettings.getProperty("AttackerRespawn", "30000"));
            _controlTowerLosePenalty = Integer.decode(siegeSettings.getProperty("CTLossPenalty", "20000"));
            _controlTowerSpawnList = new HashMap<>();
            _artefactSpawnList = new HashMap<>();

            for (Castle castle: CastleManager.getInstance().getCastles())
            {
            	List<SiegeSpawn> _controlTowersSpawns = new ArrayList<>();

            	for (int i=1; i<0xFF; i++)
            	{
            		String _spawnParams = siegeSettings.getProperty(castle.getName() + "ControlTower" + Integer.toString(i), "");
            		if (_spawnParams.length() == 0) break;
            		StringTokenizer st = new StringTokenizer(_spawnParams.trim(), ",");

            		try
            		{
            			int x = Integer.parseInt(st.nextToken());
            			int y = Integer.parseInt(st.nextToken());
            			int z = Integer.parseInt(st.nextToken());
            			int npc_id = Integer.parseInt(st.nextToken());
            			int hp = Integer.parseInt(st.nextToken());

            			_controlTowersSpawns.add(new SiegeSpawn(castle.getCastleId(),x,y,z,0,npc_id,hp));
            		}
            		catch (Exception e)
            		{
            			_log.warn("Error while loading control tower(s) for "+castle.getName()+" castle.");
            		}
            	}

            	List<SiegeSpawn> _artefactSpawns = new ArrayList<>();
            	for (int i=1; i<0xFF; i++)
            	{
            		String _spawnParams = siegeSettings.getProperty(castle.getName() + "Artefact" + Integer.toString(i), "");
            		if (_spawnParams.length() == 0) break;
            		StringTokenizer st = new StringTokenizer(_spawnParams.trim(), ",");

            		try
            		{
            			int x = Integer.parseInt(st.nextToken());
            			int y = Integer.parseInt(st.nextToken());
            			int z = Integer.parseInt(st.nextToken());
            			int heading = Integer.parseInt(st.nextToken());
            			int npc_id = Integer.parseInt(st.nextToken());

            			_artefactSpawns.add(new SiegeSpawn(castle.getCastleId(),x,y,z,heading,npc_id));
            		}
            		catch (Exception e)
            		{
            			_log.warn("Error while loading artefact(s) for "+castle.getName()+" castle.");
            		}
            	}
            	_controlTowerSpawnList.put(castle.getCastleId(), _controlTowersSpawns);
            	_artefactSpawnList.put(castle.getCastleId(), _artefactSpawns);
            }

        } catch (Exception e)
        {
            //_initialized = false;
            System.err.println("Error while loading siege data.");
            e.printStackTrace();
        }
    }

    // Property - Public
    public final List<SiegeSpawn> getArtefactSpawnList(int _castleId)
    {
    	if (_artefactSpawnList.containsKey(_castleId))
    		return _artefactSpawnList.get(_castleId);
    	else
    		return null;
    }

    public final List<SiegeSpawn> getControlTowerSpawnList(int _castleId)
    {
    	if (_controlTowerSpawnList.containsKey(_castleId))
    		return _controlTowerSpawnList.get(_castleId);
    	else
    		return null;
    }

    public final int getAttackerMaxClans() { return _attackerMaxClans; }
    public final int getAttackerRespawnDelay() { return _attackerRespawnDelay; }
    public final int getControlTowerLosePenalty() { return _controlTowerLosePenalty; }
    public final int getDefenderMaxClans() { return _defenderMaxClans; }
    public final int getDefenderRespawnDelay() { return (_defenderRespawnDelay); }
    public final int getFlagMaxCount() { return _flagMaxCount; }
    public final Siege getSiege(L2Object activeObject) { return getSiege(activeObject.getX(), activeObject.getY(), activeObject.getZ()); }
    public final Siege getSiege(int x, int y, int z)
    {
        for (Castle castle: CastleManager.getInstance().getCastles())
            if (castle.getSiege().checkIfInZone(x, y, z)) return castle.getSiege();
        return null;
    }

    public final int getSiegeClanMinLevel() { return _siegeClanMinLevel; }
    public final int getSiegeLength() { return _siegeLength; }
    public final List<Siege> getSieges()
    {
        if (_sieges == null) _sieges = new ArrayList<>();
        return _sieges;
    }

	/**
  	* @return the _teleport_to_siege
    */
    public boolean is_teleport_to_siege_allowed()
   	{
    	return _teleport_to_siege;
   	}

   	/**
    * @return the _teleport_to_siege_town
    */
    public boolean is_teleport_to_siege_town_allowed()
	{
    	return _teleport_to_siege_town;
    }

    public class  SiegeSpawn
    {
    	Location _location;
    	private int _npcId;
    	private int _heading;
    	private int _castleId;
    	private int _hp;

    	public SiegeSpawn(int castle_id, int x, int y, int z, int heading, int npc_id)
    	{
    		_castleId = castle_id;
    		_location = new Location(x,y,z,heading);
    		_heading = heading;
    		_npcId = npc_id;
    	}

    	public SiegeSpawn(int castle_id, int x, int y, int z, int heading, int npc_id, int hp)
    	{
    		_castleId = castle_id;
    		_location = new Location(x,y,z,heading);
    		_heading = heading;
    		_npcId = npc_id;
    		_hp = hp;
    	}

    	public int getCastleId()
    	{
    		return _castleId;
    	}

    	public int getNpcId()
    	{
    		return _npcId;
    	}

    	public int getHeading()
    	{
    		return _heading;
    	}

    	public int getHp()
    	{
    		return _hp;
    	}

    	public Location getLocation()
    	{
    		return _location;
    	}
    }
}