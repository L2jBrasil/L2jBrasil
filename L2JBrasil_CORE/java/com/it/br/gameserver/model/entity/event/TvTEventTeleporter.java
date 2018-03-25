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

import com.it.br.configuration.settings.EventSettings;
import com.it.br.gameserver.ThreadPoolManager;
import com.it.br.gameserver.model.L2Summon;
import com.it.br.gameserver.model.actor.instance.L2PcInstance;
import com.it.br.gameserver.model.entity.Duel;
import com.it.br.util.Rnd;
import com.it.br.util.Util;

import java.util.HashMap;
import java.util.Map;

import static com.it.br.configuration.Configurator.getSettings;

public class TvTEventTeleporter implements Runnable
{
	/** The instance of the player to teleport */
	private L2PcInstance _playerInstance = null;
	/** Coordinates of the spot to teleport to */
	private int[] _coordinates = new int[3];
	/** Admin removed this player from event */
	private boolean _adminRemove = false;
	/** Old Player Coordinates */
	private static Map<Integer, Integer[]> _oldPlayerPos;

	/**
	 * Initialize the teleporter and start the delayed task<br><br>
	 * @param playerInstance as L2PcInstance<br>
	 * @param coordinates as int[]<br>
	 * @param fastShedule as boolean<br>
	 * @param adminRemove as boolean<br>
	 */
	public TvTEventTeleporter(L2PcInstance playerInstance, int[] coordinates, boolean fastSchedule, boolean adminRemove)
	{
		_playerInstance = playerInstance;
		_coordinates = coordinates;
		_adminRemove = adminRemove;
		EventSettings eventSettings = getSettings(EventSettings.class);
		long delay = Util.secondsToMilliseconds((TvTEvent.isStarted() ?  eventSettings.getTvTEventRespawnTeleportDelay() : eventSettings.getTvTEventStartLeaveTeleportDelay()) );

		ThreadPoolManager.getInstance().scheduleGeneral(this, fastSchedule ? 0 : delay);
	}

	/**
	 * The task method to teleport the player<br>
	 * 1. Unsummon pet if there is one<br>
	 * 2. Remove all effects<br>
	 * 3. Revive and full heal the player<br>
	 * 4. Teleport the player<br>
	 * 5. Broadcast status and user info<br><br>
	 * @see java.lang.Runnable#run()<br>
	 */
	public void run()
	{
		if (_playerInstance == null)
			return;

		L2Summon summon = _playerInstance.getPet();

		if (summon != null)
			summon.unSummon(_playerInstance);
		EventSettings eventSettings = getSettings(EventSettings.class);

		int effectRemoval = eventSettings.getTvTEventEffectsRemoval();
		if (effectRemoval == 0
				|| (effectRemoval == 1 && (_playerInstance.getTeam() == 0 || (_playerInstance.isInDuel() && _playerInstance.getDuelState() != Duel.DUELSTATE_INTERRUPTED))))
			_playerInstance.stopAllEffects();

		if (_playerInstance.isInDuel())
			_playerInstance.setDuelState(Duel.DUELSTATE_INTERRUPTED);

		int TvTInstance = TvTEvent.getTvTEventInstance();
		if (TvTInstance != 0)
		{
			if (TvTEvent.isStarted() && !_adminRemove)
			{
				_playerInstance.setInstanceId(TvTInstance);
			}
			else
			{
				_playerInstance.setInstanceId(0);
			}
		}
		else
		{
			_playerInstance.setInstanceId(0);
		}

		_playerInstance.doRevive();

		int objId = _playerInstance.getObjectId();

		boolean restorePosition = eventSettings.isTvTRestorePlayerOldPosition();
		if (restorePosition && TvTEvent.isStarted() && !_adminRemove)
		{
			final Integer[] oldCoords =
			{
				_playerInstance.getX(),
				_playerInstance.getY(),
				_playerInstance.getZ()
			};
			_oldPlayerPos.put(objId, oldCoords);
		}

		if (restorePosition && !TvTEvent.isStarted())
		{
			Integer[] coor = _oldPlayerPos.get(objId);

			if (coor != null)
			{
				_playerInstance.teleToLocation(coor[0], coor[1], coor[2], false);
			}
			else
			{
				_playerInstance.teleToLocation( _coordinates[ 0 ] + Rnd.get(101)-50, _coordinates[ 1 ] + Rnd.get(101)-50, _coordinates[ 2 ], false );
			}
		}
		else
			_playerInstance.teleToLocation( _coordinates[ 0 ] + Rnd.get(101)-50, _coordinates[ 1 ] + Rnd.get(101)-50, _coordinates[ 2 ], false );

		if (TvTEvent.isStarted() && !_adminRemove)
			_playerInstance.setTeam(TvTEvent.getParticipantTeamId(_playerInstance.getObjectId()) + 1);
		else
			_playerInstance.setTeam(0);

		if (_oldPlayerPos.containsKey(objId))
			_oldPlayerPos.remove(objId);

		_playerInstance.setCurrentCp(_playerInstance.getMaxCp());
		_playerInstance.setCurrentHp(_playerInstance.getMaxHp());
		_playerInstance.setCurrentMp(_playerInstance.getMaxMp());
		_playerInstance.broadcastStatusUpdate();
		_playerInstance.broadcastUserInfo();
	}

	/**
	 * Initializes the map where the player position
	 * will be stored
	 */
	public static void initializeRestoreMap()
	{
		if (_oldPlayerPos == null)
			_oldPlayerPos = new HashMap<>();
	}

	/**
	 * Clear all containing data for a new event
	 */
	public static void clearRestoreMap()
	{
		_oldPlayerPos.clear();
	}
}