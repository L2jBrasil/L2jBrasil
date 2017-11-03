/*
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.it.br.gameserver.model.zone.type;

import java.util.HashMap;
import java.util.Map;

import com.it.br.Config;
import com.it.br.gameserver.GameServer;
import com.it.br.gameserver.datatables.xml.MapRegionTable;
import com.it.br.gameserver.model.L2Attackable;
import com.it.br.gameserver.model.L2Character;
import com.it.br.gameserver.model.actor.instance.L2NpcInstance;
import com.it.br.gameserver.model.actor.instance.L2PcInstance;
import com.it.br.gameserver.model.actor.instance.L2PlayableInstance;
import com.it.br.gameserver.model.zone.L2ZoneType;
import com.it.br.gameserver.network.serverpackets.L2GameServerPacket;
import com.it.br.util.L2FastList;

public class L2BossZone extends L2ZoneType
{
    private String _zoneName;
    private boolean _enabled = true; // default value, unless overridden by xml...
    private int _timeInvade;
	private Map<Integer, Long> _playerAllowedReEntryTimes;
    private L2FastList<Integer> _playersAllowed;
	private int[] _oustLoc = { 0, 0, 0 };
	protected L2FastList<L2Character> _raidList = new L2FastList<L2Character>();

	public L2BossZone()
	{
		super();
		_playerAllowedReEntryTimes = new HashMap<>();
		_playersAllowed = new L2FastList<Integer>();
		_oustLoc = new int[3];
	}

    @Override
	public void setParameter(String name, String value)
    {
    	if (name.equals("name"))
			_zoneName = value;
		else if (name.equals("InvadeTime"))
			_timeInvade = Integer.parseInt(value);
		else if (name.equals("EnabledByDefault"))
			_enabled = Boolean.parseBoolean(value);
		else if (name.equals("oustX"))
			_oustLoc[0] = Integer.parseInt(value);
		else if (name.equals("oustY"))
			_oustLoc[1] = Integer.parseInt(value);
		else if (name.equals("oustZ"))
			_oustLoc[2] = Integer.parseInt(value);
		else
			super.setParameter(name, value);
    }

    @Override
	protected void onEnter(L2Character character)
    {
    	if (_enabled)
    	{
    		if (character instanceof L2PcInstance)
    		{
    			L2PcInstance player = (L2PcInstance) character;
    			if(player.isGM() || Config.ALLOW_DIRECT_TP_TO_BOSS_ROOM)
    			{
    				player.sendMessage("You entered " + _zoneName);
    				return;
    			}
    		}
    	}
    	else
    	{
    		if (character instanceof L2PcInstance)
    		{
    			L2PcInstance player = (L2PcInstance) character;
    			player.setInsideZone(L2Character.ZONE_NOSUMMONFRIEND, true);
    			if(player.isGM() || Config.ALLOW_DIRECT_TP_TO_BOSS_ROOM)
    			{
    				player.sendMessage("You entered " + _zoneName);
    				return;
    			}
    		// if player has been (previously) cleared by npc/ai for entry and the zone is
    		// set to receive players (aka not waiting for boss to respawn)
    		if (_playersAllowed.contains(player.getObjectId()))
    		{
    			// Get the information about this player's last logout-exit from this zone.
    			Long expirationTime = _playerAllowedReEntryTimes.get(player.getObjectId());
    			// with legal entries, do nothing.
    			if (expirationTime == null) // legal null expirationTime entries
    			{
    				long serverStartTime = GameServer.dateTimeServerStarted.getTimeInMillis();
    				if (serverStartTime > System.currentTimeMillis() - _timeInvade)
    					return;
    			}
    			else
    			{
    				// legal non-null logoutTime entries
    				_playerAllowedReEntryTimes.remove(player.getObjectId());
    				if (expirationTime.longValue() > System.currentTimeMillis())
    						return;
					}
    				_playersAllowed.remove(_playersAllowed.indexOf(player.getObjectId()));
    			}
    			// teleport out all players who attempt "illegal" (re-)entry
    			if (_oustLoc[0] != 0 && _oustLoc[1] != 0 && _oustLoc[2] != 0)
    				player.teleToLocation(_oustLoc[0], _oustLoc[1], _oustLoc[2]);
    			else
    				player.teleToLocation(MapRegionTable.TeleportWhereType.Town);
    		}	
    	}
    }

    @Override
	protected void onExit(L2Character character)
    {	
    	if (character instanceof L2PcInstance)
    	{
    		L2PcInstance player = (L2PcInstance) character;
    		player.setInsideZone(L2Character.ZONE_NOSUMMONFRIEND, false);
    		if(player.isGM() || Config.ALLOW_DIRECT_TP_TO_BOSS_ROOM)
    		{
    			player.sendMessage("You left " + _zoneName);
    			return;
    		}
    		// if the player just got disconnected/logged out, store the dc
    		// time so that
    		// decisions can be made later about allowing or not the player
    		// to log into the zone
    		if (player.isOnline() == 0 && _playersAllowed.contains(player.getObjectId()))
    			// mark the time that the player left the zone
    			_playerAllowedReEntryTimes.put(player.getObjectId(), System.currentTimeMillis() + _timeInvade);
    		else
    		{
    			if (_playersAllowed.contains(player.getObjectId()))
    				_playersAllowed.remove(_playersAllowed.indexOf(player.getObjectId()));
    			_playerAllowedReEntryTimes.remove(player.getObjectId());
    		}
    	}
    	if (character instanceof L2PlayableInstance)
    		if (getCharactersInside() != null && !getCharactersInside().isEmpty())
    		{
    			_raidList.clear();
    			int count = 0;
    			for (L2Character obj : getCharactersInside().values())
    			{
    				if (obj == null)
    					continue;
    				if (obj instanceof L2PlayableInstance)
    					count++;
    				else if (obj instanceof L2Attackable && obj.isRaid())
    					_raidList.add(obj);
    			}
    			// if inside zone isnt any player, force all boss instance return to its spawn points
    			if (count == 0 && !_raidList.isEmpty())
    				for (int i = 0; i < _raidList.size(); i++)
    				{
    					L2Attackable raid = (L2Attackable) _raidList.get(i);
    					if (raid == null || raid.isDead())
    						continue;
    					if (!raid.isInsideRadius(raid.getSpawn().getLocx(), raid.getSpawn().getLocy(), 150, false))
    						raid.returnHome();
    				}
    		
    		}
    	if (character instanceof L2Attackable && character.isRaid() && !character.isDead())
    		((L2Attackable) character).returnHome();
    }

    public void setZoneEnabled(boolean flag)
	{
		if (_enabled != flag)
			oustAllPlayers();
		_enabled = flag;
	}
    
    public String getZoneName()
    {
    	return _zoneName;
    }

    public int getTimeInvade()
    {
    	return _timeInvade;
    }

    public void setAllowedPlayers(L2FastList<Integer> players)
	{
		if (players != null)
			_playersAllowed = players;
	}

    public L2FastList<Integer> getAllowedPlayers()
	{
		return _playersAllowed;
	}

    public boolean isPlayerAllowed(L2PcInstance player)
	{
    	if (player.isGM() || !Config.ALLOW_DIRECT_TP_TO_BOSS_ROOM && player.isGM())
			return true;
		else if (_playersAllowed.contains(player.getObjectId()))
			return true;
		else
		{
			if (_oustLoc[0] != 0 && _oustLoc[1] != 0 && _oustLoc[2] != 0)
				player.teleToLocation(_oustLoc[0], _oustLoc[1], _oustLoc[2]);
			else
				player.teleToLocation(MapRegionTable.TeleportWhereType.Town);
			return false;
		}
	}

    /**
	 * Some GrandBosses send all players in zone to a specific part of the zone, rather than just removing them all. If this is the case, this command should be used. If this is no the case, then use oustAllPlayers().
	 *
	 * @param x
	 * @param y
	 * @param z
	 */
	public void movePlayersTo(int x, int y, int z)
	{
		if (_characterList.isEmpty())
			return;
		for (L2Character character : _characterList.values())
			if (character instanceof L2PcInstance)
			{
				L2PcInstance player = (L2PcInstance) character;
				if (player.isOnline() == 1)
					player.teleToLocation(x, y, z);
			}
	}

    /**
	 * Occasionally, all players need to be sent out of the zone (for example, if the players are just running around without fighting for too long, or if all players die, etc). This call sends all online players to town and marks offline players to be teleported (by clearing their relog expiration times) when they log back in (no real need for off-line teleport).
	 */
	public void oustAllPlayers()
	{
		if (_characterList.isEmpty())
			return;
		for (L2Character character : _characterList.values())
			if (character instanceof L2PcInstance)
			{
				L2PcInstance player = (L2PcInstance) character;
				if (player.isOnline() == 1)
					if (_oustLoc[0] != 0 && _oustLoc[1] != 0 && _oustLoc[2] != 0)
						player.teleToLocation(_oustLoc[0], _oustLoc[1], _oustLoc[2]);
					else
						player.teleToLocation(MapRegionTable.TeleportWhereType.Town);
			}
		_playerAllowedReEntryTimes.clear();
		_playersAllowed.clear();
	}

    /**
	 * This function is to be used by external sources, such as quests and AI in order to allow a player for entry into the zone for some time. Naturally if the player does not enter within the allowed time, he/she will be teleported out again...
	 *
	 * @param player: reference to the player we wish to allow
	 * @param durationInSec : amount of time in seconds during which entry is valid.
	 */
	public void allowPlayerEntry(L2PcInstance player, int durationInSec)
	{
		if (!player.isGM() || !Config.ALLOW_DIRECT_TP_TO_BOSS_ROOM)
		{
			if (!_playersAllowed.contains(player.getObjectId()))
				_playersAllowed.add(player.getObjectId());
			_playerAllowedReEntryTimes.put(player.getObjectId(), System.currentTimeMillis() + durationInSec * 1000);
		}
	}

	// these are methods that java calls to invoke scripts
	@Override
	public void broadcastPacket(L2GameServerPacket packet)
	{
		if (_characterList == null || _characterList.isEmpty())
			return;
		for (L2Character character : _characterList.values())
		{
			if (character == null)
				continue;
			if (character instanceof L2PcInstance)
			{
				L2PcInstance player = (L2PcInstance) character;
				if (player.isOnline() == 1)
					player.sendPacket(packet);
			}
		}
	}

	// these are methods that java calls to invoke scripts
	public void updateKnownList(L2NpcInstance npc)
	{
		if (_characterList == null || _characterList.isEmpty())
			return;
		Map<Integer, L2PcInstance> npcKnownPlayers = npc.getKnownList().getKnownPlayers();
		for (L2Character character : _characterList.values())
		{
			if (character == null)
				continue;
			if (character instanceof L2PcInstance)
			{
				L2PcInstance player = (L2PcInstance) character;
				if (player.isOnline() == 1 && !player.isOffline())
					npcKnownPlayers.put(player.getObjectId(), player);
			}
		}
		return;
	}

	@Override
	protected void onDieInside(L2Character character){ }

	@Override
	protected void onReviveInside(L2Character character){ }
}