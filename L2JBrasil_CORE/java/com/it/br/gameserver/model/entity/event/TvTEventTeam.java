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
package com.it.br.gameserver.model.entity.event;

import com.it.br.gameserver.model.actor.instance.L2PcInstance;

import java.util.HashMap;
import java.util.Map;

public class TvTEventTeam
{
	/** The name of the team<br> */
	private String _name;
	/** The team spot coordinated<br> */
	private int[] _coordinates = new int[3];
	/** The points of the team<br> */
	private short _points;
	/** Name and instance of all participated players in FastMap<br> */
	private Map<Integer, L2PcInstance> _participatedPlayers = new HashMap<>();

	/**
	 * C'tor initialize the team<br><br>
	 * @param name as String<br>
	 * @param coordinates as int[]<br>
	 */
	public TvTEventTeam(String name, int[] coordinates)
	{
		_name = name;
		_coordinates = coordinates;
		_points = 0;
	}

	/**
	 * Adds a player to the team<br><br>
	 *
	 * @param playerInstance as L2PcInstance<br>
	 * @return boolean: true if success, otherwise false<br>
	 */
	public boolean addPlayer(L2PcInstance playerInstance)
	{
		if (playerInstance == null)
			return false;

		synchronized (_participatedPlayers)
		{
			_participatedPlayers.put(playerInstance.getObjectId(), playerInstance);
		}
		return true;
	}

	/**
	 * Removes a player from the team<br><br>
	 */
	public void removePlayer(int playerObjectId)
	{
		synchronized (_participatedPlayers)
		{
			_participatedPlayers.remove(playerObjectId);
		}
	}

	/**
	 * Increases the points of the team<br>
	 */
	public void increasePoints()
	{
		++_points;
	}

	/**
	 * Cleanup the team and make it ready for adding players again<br>
	 */
	public void cleanMe()
	{
		_participatedPlayers.clear();
		_participatedPlayers = new HashMap<>();
		_points = 0;
	}

	/**
	 * Is given player in this team?<br><br>
	 * @return boolean: true if player is in this team, otherwise false<br>
	 */
	public boolean containsPlayer(int playerObjectId)
	{
		boolean containsPlayer;
		synchronized (_participatedPlayers)
		{
			containsPlayer = _participatedPlayers.containsKey(playerObjectId);
		}
		return containsPlayer;
	}

	/**
	 * Returns the name of the team<br><br>
	 * @return String: name of the team<br>
	 */
	public String getName()
	{
		return _name;
	}

	/**
	 * Returns the coordinates of the team spot<br><br>
	 * @return int[]: team coordinates<br>
	 */
	public int[] getCoordinates()
	{
		return _coordinates;
	}

	/**
	 * Returns the points of the team<br><br>
	 * @return short: team points<br>
	 */
	public short getPoints()
	{
		return _points;
	}

	/**
	 * Returns name and instance of all participated players in FastMap<br><br>
	 * @return Map<String, L2PcInstance>: map of players in this team<br>
	 */
	public Map<Integer, L2PcInstance> getParticipatedPlayers()
	{
		Map<Integer, L2PcInstance> participatedPlayers = null;

		synchronized (_participatedPlayers)
		{
			participatedPlayers = _participatedPlayers;
		}

		return participatedPlayers;
	}

	/**
	 * Returns player count of this team<br><br>
	 * @return int: number of players in team<br>
	 */
	public int getParticipatedPlayerCount()
	{
		int participatedPlayerCount;

		synchronized (_participatedPlayers)
		{
			participatedPlayerCount = _participatedPlayers.size();
		}
		return participatedPlayerCount;
	}
}