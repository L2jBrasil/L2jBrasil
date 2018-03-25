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
import com.it.br.configuration.settings.L2JBrasilSettings;
import com.it.br.configuration.settings.OlympiadSettings;
import com.it.br.gameserver.ThreadPoolManager;
import com.it.br.gameserver.cache.HtmCache;
import com.it.br.gameserver.datatables.sql.ItemTable;
import com.it.br.gameserver.datatables.sql.SkillTable;
import com.it.br.gameserver.datatables.sql.SpawnTable;
import com.it.br.gameserver.datatables.xml.DoorTable;
import com.it.br.gameserver.datatables.xml.NpcTable;
import com.it.br.gameserver.model.*;
import com.it.br.gameserver.model.Olympiad.Olympiad;
import com.it.br.gameserver.model.actor.instance.*;
import com.it.br.gameserver.network.SystemMessageId;
import com.it.br.gameserver.network.clientpackets.Say2;
import com.it.br.gameserver.network.serverpackets.*;
import com.it.br.gameserver.templates.L2NpcTemplate;
import com.it.br.gameserver.util.StringUtil;
import com.it.br.util.Rnd;
import com.it.br.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import static com.it.br.configuration.Configurator.getSettings;

/**
 * @author FBIagent
 */
public class TvTEvent
{
	private static final String ON_KILL_PMTITLE = "pmtitle";
	private static final String ON_KILL_TITLE = "title";

	enum EventState
	{
		INACTIVE,INACTIVATING,PARTICIPATING,STARTING,STARTED,REWARDING
	}

	protected static final Logger _log = LoggerFactory.getLogger(TvTEvent.class);
	/** html path **/
	private static final String htmlPath = "data/html/mods/";
	/**	The teams of the TvTEvent<br> */
	private static TvTEventTeam[] _teams = new TvTEventTeam[2];
	/** The state of the TvTEvent<br> */
	private static EventState _state = EventState.INACTIVE;
	/** The spawn of the participation npc<br> */
	private static L2Spawn _npcSpawn = null;
	/** the npc instance of the participation npc<br> */
	private static L2NpcInstance _lastNpcSpawn = null;
	/** Instance id<br> */
	private static int _TvTEventInstance = 0;
	private static int[] npcCoordinates;

	private TvTEvent(){}

	/**
	 * Teams initializing<br>
	 */
	public static void init()
	{
		
		EventSettings eventSettings = getSettings(EventSettings.class);
		
		_teams[0] = new TvTEventTeam(eventSettings.getTvTEventTeam1Name(), eventSettings.getTvTEventTeam1Coordinates());
		_teams[1] = new TvTEventTeam(eventSettings.getTvTEventTeam2Name(), eventSettings.getTvTEventTeam2Coordinates());

		if (eventSettings.isTvTRestorePlayerOldPosition())
			TvTEventTeleporter.initializeRestoreMap();
	}

	/**
	 * Starts the participation of the TvTEvent<br>
	 * 1. Get L2NpcTemplate by Config.TVT_EVENT_PARTICIPATION_NPC_ID<br>
	 * 2. Try to spawn a new npc of it<br><br>
	 * @return boolean: true if success, otherwise false<br>
	 */
	public static boolean startParticipation()
	{
		EventSettings eventSettings = getSettings(EventSettings.class);
		L2NpcTemplate tmpl = NpcTable.getInstance().getTemplate( eventSettings.getTvTEventParticipationNpcId());

		if (tmpl == null)
		{
			_log.warn( "TvTEventEngine[TvTEvent.startParticipation()]: L2NpcTemplate is a NullPointer -> Invalid npc id in configs?");
			return false;
		}

		if(!getSettings(OlympiadSettings.class).isEventsDuringOlympiadEnabled() && Olympiad.getInstance().inCompPeriod())
		{
			System.out.println("TvTEventEngine : TvT Event uninitialized, Olympiad running");
			return false;
		}

		try
		{
			_npcSpawn = new L2Spawn(tmpl);
			
			npcCoordinates = eventSettings.getTvTEventParticipationNpcCoordinates();
			
			_npcSpawn.setLocx(npcCoordinates[0]);
			_npcSpawn.setLocy(npcCoordinates[1]);
			_npcSpawn.setLocz(npcCoordinates[2]);
			_npcSpawn.setAmount(1);
			_npcSpawn.setHeading(9999999);
			_npcSpawn.setRespawnDelay(3);
			// later no need to delete spawn from db, we don't store it (false)
			SpawnTable.getInstance().addNewSpawn(_npcSpawn, false);
			_npcSpawn.init();
			_lastNpcSpawn = _npcSpawn.getLastSpawn();
			_lastNpcSpawn.setCurrentHp(_lastNpcSpawn.getMaxHp());
			_lastNpcSpawn.setTitle("TvT Event Participation");
			_lastNpcSpawn.isAggressive();
			_lastNpcSpawn.decayMe();
			_lastNpcSpawn.spawnMe(_npcSpawn.getLastSpawn().getX(), _npcSpawn.getLastSpawn().getY(), _npcSpawn.getLastSpawn().getZ());
			_lastNpcSpawn.broadcastPacket(new MagicSkillUser(_lastNpcSpawn, _lastNpcSpawn, 1034, 1, 1, 1));
		}
		catch (Exception e)
		{
			_log.warn( "TvTEventEngine[TvTEvent.startParticipation()]: exception: exception: " + e.getMessage(), e);
			return false;
		}
		setState(EventState.PARTICIPATING);
		return true;
	}

	private static int highestLevelPcInstanceOf(Map< Integer, L2PcInstance > players)
	{
		int maxLevel = Integer.MIN_VALUE, maxLevelId = -1;
		for (L2PcInstance player : players.values())
		{
			if (player.getLevel() >= maxLevel)
			{
				maxLevel = player.getLevel();
				maxLevelId = player.getObjectId();
			}
		}
		return maxLevelId;
	}

	/**
	 * Starts the TvTEvent fight<br>
	 * 1. Set state EventState.STARTING<br>
	 * 2. Close doors specified in configs<br>
	 * 3. Abort if not enought participants(return false)<br>
	 * 4. Set state EventState.STARTED<br>
	 * 5. Teleport all participants to team spot<br><br>
	 * @return boolean: true if success, otherwise false<br>
	 */
	public static boolean startFight()
	{
		// Set state to STARTING
		setState(EventState.STARTING);

		// Randomize and balance team distribution
		Map< Integer, L2PcInstance > allParticipants = new HashMap<>();
		allParticipants.putAll(_teams[0].getParticipatedPlayers());
		allParticipants.putAll(_teams[1].getParticipatedPlayers());
		_teams[0].cleanMe();
		_teams[1].cleanMe();
		int balance[] = { 0, 0 }, priority = 0, highestLevelPlayerId;
		L2PcInstance highestLevelPlayer;
		// XXX: allParticipants should be sorted by level instead of using highestLevelPcInstanceOf for every fetch
		while (!allParticipants.isEmpty())
		{
			// Priority team gets one player
			highestLevelPlayerId = highestLevelPcInstanceOf(allParticipants);
			highestLevelPlayer = allParticipants.get(highestLevelPlayerId);
			allParticipants.remove(highestLevelPlayerId);
			_teams[priority].addPlayer(highestLevelPlayer);
			balance[priority] += highestLevelPlayer.getLevel();
			// Exiting if no more players
			if (allParticipants.isEmpty()) break;
			// The other team gets one player
			// XXX: Code not dry
			priority = 1-priority;
			highestLevelPlayerId = highestLevelPcInstanceOf(allParticipants);
			highestLevelPlayer = allParticipants.get(highestLevelPlayerId);
			allParticipants.remove(highestLevelPlayerId);
			_teams[priority].addPlayer(highestLevelPlayer);
			balance[priority] += highestLevelPlayer.getLevel();
			// Recalculating priority
			priority = balance[0] > balance[1] ? 1 : 0;
		}

		EventSettings eventSettings = getSettings(EventSettings.class);
		// Check for enought participants
		if (_teams[0].getParticipatedPlayerCount() <  eventSettings.getTvTEventMinPlayersInTeams() || _teams[1].getParticipatedPlayerCount() < eventSettings.getTvTEventMinPlayersInTeams())
		{
			// Set state INACTIVE
			setState(EventState.INACTIVE);
			// Cleanup of teams
			_teams[0].cleanMe();
			_teams[1].cleanMe();
			// Unspawn the event NPC
			unSpawnNpc();
			return false;
		}

		// Opens all doors specified in configs for tvt
		openDoors(eventSettings.getTvTDoorsToOpen());
		// Closes all doors specified in configs for tvt
		closeDoors(eventSettings.getTvTDoorsToClose());
		// Set state STARTED
		setState(EventState.STARTED);

		// Iterate over all teams
		for (TvTEventTeam team : _teams)
		{
			// Iterate over all participated player instances in this team
			for (L2PcInstance playerInstance : team.getParticipatedPlayers().values())
			{
				boolean _canreward = true;
				if (playerInstance != null)
				{
					// Teleporter implements Runnable and starts itself
					new TvTEventTeleporter(playerInstance, team.getCoordinates(), false, false);
					String onKill = getSettings(EventSettings.class).getTvTEventOnKill();
					if (ON_KILL_TITLE.equalsIgnoreCase(onKill) || ON_KILL_PMTITLE.equalsIgnoreCase(onKill))
					{
						playerInstance._originalTitle = playerInstance.getTitle();
						playerInstance.setTitle("Kills: " + playerInstance.getPointScore());
						playerInstance.broadcastTitleInfo();
					}
				}
			}
		}
		return true;
	}

	/**
	 * Calculates the TvTEvent reward<br>
	 * 1. If both teams are at a tie(points equals), send it as system message to all participants, if one of the teams have 0 participants left online abort rewarding<br>
	 * 2. Wait till teams are not at a tie anymore<br>
	 * 3. Set state EvcentState.REWARDING<br>
	 * 4. Reward team with more points<br>
	 * 5. Show win html to wining team participants<br><br>
	 * @return String: winning team name<br>
	 */
	public static String calculateRewards()
	{
		if (_teams[0].getPoints() == _teams[1].getPoints())
		{
			// Check if one of the teams have no more players left
			if (_teams[0].getParticipatedPlayerCount() == 0 || _teams[1].getParticipatedPlayerCount() == 0)
			{
				// set state to rewarding
				setState(EventState.REWARDING);
				// return here, the fight can't be completed
				return "TvT Event: Event has ended. No team won due to inactivity!";
			}

			// Both teams have equals points
			sysMsgToAllParticipants("TvT Event: Event has ended, both teams have tied.");
			if (getSettings(EventSettings.class).isTvTRewardTeamTie())
			{
				rewardTeam(_teams[0]);
				rewardTeam(_teams[1]);
				return "TvT Event: Event has ended with both teams tying.";
			}
			else
				return "TvT Event: Event has ended with both teams tying.";
		}

		// Set state REWARDING so nobody can point anymore
		setState(EventState.REWARDING);

		// Get team which has more points
		TvTEventTeam team = _teams[_teams[0].getPoints() > _teams[1].getPoints() ? 0 : 1];

		rewardTeam(team);
		return "TvT Event: Event finish. Team " + team.getName() + " won with " + team.getPoints() + " kills.";
	}
	
	private static void rewardTeam(TvTEventTeam team)
	{	
		// Iterate over all participated player instances of the winning team
		for (L2PcInstance playerInstance : team.getParticipatedPlayers().values())
		{
			boolean _canreward = true;
			// Check for nullpointer
			if (playerInstance == null)
			{
				continue;
			}

			EventSettings eventSettings = getSettings(EventSettings.class);
			if (eventSettings.isTvTRewardOnlyKillers())
			{
				if (playerInstance.getRewardKills() <= eventSettings.getTvTEventRewardKill())
					_canreward = false;
			}

			SystemMessage systemMessage = null;

			if (_canreward)
			{
				// Iterate over all tvt event rewards
				for (Entry<Integer, Integer> reward : eventSettings.getTvTEventReward().entrySet())
				{
					PcInventory inv = playerInstance.getInventory();
					int itemId = reward.getKey();
					int itemCount = reward.getValue();
					
					// Check for stackable item, non stackabe items need to be added one by one
					if (ItemTable.getInstance().createDummyItem(itemId).isStackable())
					{
						inv.addItem("TvT Event", itemId, itemCount, playerInstance, playerInstance);
						if (itemCount > 1)
						{
							systemMessage = new SystemMessage(SystemMessageId.EARNED_S2_S1_S);
							systemMessage.addItemName(itemId);
							systemMessage.addNumber(itemCount);
						}
						else
						{
							systemMessage = new SystemMessage(SystemMessageId.EARNED_ITEM);
							systemMessage.addItemName(itemId);
						}
						playerInstance.sendPacket(systemMessage);
					}
					else
					{
						for (int i = 0; i < itemCount; ++i)
						{
							inv.addItem("TvT Event", itemId, 1, playerInstance, playerInstance);
							systemMessage = new SystemMessage(SystemMessageId.EARNED_ITEM);
							systemMessage.addItemName(itemCount);
							playerInstance.sendPacket(systemMessage);
						}
					}
				}

				StatusUpdate statusUpdate = new StatusUpdate(playerInstance.getObjectId());
				NpcHtmlMessage npcHtmlMessage = new NpcHtmlMessage(0);

				statusUpdate.addAttribute(StatusUpdate.CUR_LOAD, playerInstance.getCurrentLoad());
				npcHtmlMessage.setHtml(HtmCache.getInstance().getHtm(playerInstance.getHtmlPrefix(), htmlPath+"Reward.htm"));
				playerInstance.sendPacket(statusUpdate);
				playerInstance.sendPacket(npcHtmlMessage);
			}
			else
			{
				StatusUpdate statusUpdate = new StatusUpdate(playerInstance.getObjectId());
				NpcHtmlMessage npcHtmlMessage = new NpcHtmlMessage(0);

				statusUpdate.addAttribute(StatusUpdate.CUR_LOAD, playerInstance.getCurrentLoad());
				npcHtmlMessage.setHtml(HtmCache.getInstance().getHtm(playerInstance.getHtmlPrefix(), htmlPath+"Reward0.htm"));
				playerInstance.sendPacket(statusUpdate);
				playerInstance.sendPacket(npcHtmlMessage);
			}
		}
	}

	/**
	 * Stops the TvTEvent fight<br>
	 * 1. Set state EventState.INACTIVATING<br>
	 * 2. Remove tvt npc from world<br>
	 * 3. Open doors specified in configs<br>
	 * 4. Teleport all participants back to participation npc location<br>
	 * 5. Teams cleaning<br>
	 * 6. Set state EventState.INACTIVE<br>
	 */
	public static void stopFight()
	{
		// Set state INACTIVATING
		setState(EventState.INACTIVATING);
		//Unspawn event npc
		unSpawnNpc();
		// Closes all doors specified in Configs for tvt
		EventSettings eventSettings = getSettings(EventSettings.class);
		closeDoors(eventSettings.getTvTDoorsToOpen());

		// Iterate over all teams
		for (TvTEventTeam team : _teams)
		{
			for (final L2PcInstance playerInstance : team.getParticipatedPlayers().values())
			{
				boolean _canreward = true;

				// Check for nullpointer
				if (playerInstance != null)
				{
					new TvTEventTeleporter(playerInstance, eventSettings.getTvTEventParticipationNpcCoordinates(), false, false);
					String  onKill = eventSettings.getTvTEventOnKill();
					if (ON_KILL_TITLE.equalsIgnoreCase(onKill) || ON_KILL_PMTITLE.equalsIgnoreCase(onKill))
					{
						ThreadPoolManager.getInstance().scheduleGeneral(new Runnable()
						{
							@Override
							public void run()
							{
								playerInstance.setTitle(playerInstance._originalTitle);
								playerInstance.broadcastTitleInfo();
								playerInstance.clearPoints();
							}
						}, Util.secondsToMilliseconds(eventSettings.getTvTEventStartLeaveTeleportDelay()));
					}
				}
			}
		}

		if (eventSettings.isTvTRestorePlayerOldPosition())
			TvTEventTeleporter.clearRestoreMap();

		// Cleanup of teams
		_teams[0].cleanMe();
		_teams[1].cleanMe();
		// Set state INACTIVE
		setState(EventState.INACTIVE);
	}

	/**
	 * Adds a player to a TvTEvent team<br>
	 * 1. Calculate the id of the team in which the player should be added<br>
	 * 2. Add the player to the calculated team<br><br>
	 * @param playerInstance as L2PcInstance<br>
	 * @return boolean: true if success, otherwise false<br>
	 */
	public static synchronized boolean addParticipant(L2PcInstance playerInstance)
	{
		// Check for nullpoitner
		if (playerInstance == null || playerInstance.isOffline())
		{
			return false;
		}

		byte teamId = 0;

		// Check to which team the player should be added
		if (_teams[0].getParticipatedPlayerCount() == _teams[1].getParticipatedPlayerCount())
		{
			teamId = (byte) (Rnd.get(2));
		}
		else
		{
			teamId = (byte) (_teams[0].getParticipatedPlayerCount() > _teams[1].getParticipatedPlayerCount() ? 1 : 0);
		}

		if(!getSettings(L2JBrasilSettings.class).isDualBoxInEventEnabled() && playerInstance._active_boxes> 1)
		{
			List<String> players_in_boxes = playerInstance.active_boxes_characters;
			if(players_in_boxes != null && players_in_boxes.size()>1)
				for(String character_name: players_in_boxes)
				{
					L2PcInstance player = L2World.getInstance().getPlayer(character_name);

					if(player != null && isParticipating() || isStarting())
					{
						player.sendMessage("You already participated in event with another char!"); 
						return false;
					}
				}
			playerInstance.sendMessage("Dual Box not allowed in Events");
			return false;
		}
		return _teams[teamId].addPlayer(playerInstance);
	}

	/**
	 * Removes a TvTEvent player from it's team<br>
	 * 1. Get team id of the player<br>
	 * 2. Remove player from it's team<br><br>
	 * @param playerObjectId as ObjectId <br>
	 * @return boolean: true if success, otherwise false<br>
	 */
	public static boolean removeParticipant(int playerObjectId, L2PcInstance playerInstance)
	{
		// Check for nullpoitner
		if (playerInstance == null)
		{
			return false;
		}
		
		if (playerInstance._active_boxes!=-1)
			playerInstance.decreaseBoxes();

		// Get the teamId of the player
		byte teamId = getParticipantTeamId(playerObjectId);

		// Check if the player is participant
		if (teamId != -1)
		{
			// Remove the player from team
			_teams[teamId].removePlayer(playerObjectId);
			return true;
		}
		return false;
	}

	public static boolean payParticipationFee(L2PcInstance playerInstance)
	{
		int[] participationFee = getSettings(EventSettings.class).getTvTEventParticipationFee();
		int itemId = participationFee[0];
		int itemNum = participationFee[1];
		if (itemId == 0 || itemNum == 0)
			return true;

		if (playerInstance.getInventory().getInventoryItemCount(itemId, -1) < itemNum)
			return false;

		return playerInstance.destroyItemByItemId("TvT Participation Fee", itemId, itemNum, _lastNpcSpawn, true);
	}

	public static String getParticipationFee()
	{
		int[] participationFee = getSettings(EventSettings.class).getTvTEventParticipationFee();
		int itemId = participationFee[0];
		int itemNum = participationFee[1];

		if (itemId == 0 || itemNum == 0)
			return "-";

		return StringUtil.concat(String.valueOf(itemNum), " ", ItemTable.getInstance().getTemplate(itemId).getName());
	}

	/**
	 * Send a SystemMessage to all participated players<br>
	 * 1. Send the message to all players of team number one<br>
	 * 2. Send the message to all players of team number two<br><br>
	 *
	 * @param message as String<br>
	 */
	public static void sysMsgToAllParticipants(String message)
	{
		for (L2PcInstance playerInstance : _teams[0].getParticipatedPlayers().values())
		{
			if (playerInstance != null)
			{
				playerInstance.sendMessage(message);
			}
		}

		for (L2PcInstance playerInstance : _teams[1].getParticipatedPlayers().values())
		{
			if (playerInstance != null)
			{
				playerInstance.sendMessage(message);
			}
		}
	}

	/**
	 * Close doors specified in configs
	 */
	private static void closeDoors(List<Integer> doors)
	{
		for (int doorId : doors)
		{
			L2DoorInstance doorInstance = DoorTable.getInstance().getDoor(doorId);
			
			if (doorInstance != null)
			{
				doorInstance.closeMe();
			}
		}
	}

	/**
	 * Open doors specified in configs
	 */
	private static void openDoors(List<Integer> doors)
	{
		for (int doorId : doors)
		{
			L2DoorInstance doorInstance = DoorTable.getInstance().getDoor(doorId);
			
			if (doorInstance != null)
			{
				doorInstance.openMe();
			}
		}
	}

	/**
	 * UnSpawns the TvTEvent npc
	 */
	private static void unSpawnNpc()
	{
		// Delete the npc
		_lastNpcSpawn.deleteMe();
		SpawnTable.getInstance().deleteSpawn(_lastNpcSpawn.getSpawn(), false);
		// Stop respawning of the npc
		_npcSpawn.stopRespawn();
		_npcSpawn = null;
		_lastNpcSpawn = null;
	}

	/**
	 * Called when a player logs in<br><br>
	 * @param playerInstance as L2PcInstance<br>
	 */
	public static void onLogin(L2PcInstance playerInstance)
	{
		if (playerInstance == null || (!isStarting() && !isStarted()))
		{
			return;
		}

		byte teamId = getParticipantTeamId(playerInstance.getObjectId());
		
		if (teamId == -1)
		{
			return;
		}

		_teams[teamId].addPlayer(playerInstance);
		new TvTEventTeleporter(playerInstance, _teams[teamId].getCoordinates(), true, false);
	}

	/**
	 * Called when a player logs out<br><br>
	 * @param playerInstance as L2PcInstance<br>
	 */
	public static void onLogout(L2PcInstance playerInstance)
	{
		if (playerInstance != null && isStarted() && isPlayerParticipant(playerInstance.getObjectId()))
		{
			EventSettings eventSettings = getSettings(EventSettings.class);
			if (removeParticipant(playerInstance.getObjectId(), playerInstance))
			{
				npcCoordinates = eventSettings.getTvTEventParticipationNpcCoordinates();
				playerInstance.setXYZInvisible(npcCoordinates[0] + Rnd.get(101)-50, npcCoordinates[1] + Rnd.get(101)-50, npcCoordinates[2]);
			}
			String onKill = eventSettings.getTvTEventOnKill();
			if (ON_KILL_TITLE.equalsIgnoreCase(onKill) || ON_KILL_PMTITLE.equalsIgnoreCase(onKill))
			{
				playerInstance.setTitle(playerInstance._originalTitle);
				playerInstance.broadcastTitleInfo();
			}
		}
	}

	/**
	 * Called on every bypass by npc of type L2TvTEventNpc<br>
	 * Needs synchronization cause of the max player check<br><br>
	 * @param command as String<br>
	 * @param playerInstance as L2PcInstance<br>
	 */
	public static synchronized void onBypass(String command, L2PcInstance playerInstance)
	{
		if (playerInstance == null || !isParticipating())
			return;

		final String htmContent;

		if (command.equals("tvt_event_participation"))
		{
			EventSettings eventSettings = getSettings(EventSettings.class);
			NpcHtmlMessage npcHtmlMessage = new NpcHtmlMessage(0);
			int playerLevel = playerInstance.getLevel();

			if (playerInstance.isCursedWeaponEquipped())
			{
				htmContent = HtmCache.getInstance().getHtm(playerInstance.getHtmlPrefix(), htmlPath+"CursedWeaponEquipped.htm");
				if (htmContent != null)
					npcHtmlMessage.setHtml(htmContent);
			}
			else if (Olympiad.getInstance().isRegistered(playerInstance))
			{
				htmContent = HtmCache.getInstance().getHtm(playerInstance.getHtmlPrefix(), htmlPath+"Olympiad.htm");
				if (htmContent != null)
					npcHtmlMessage.setHtml(htmContent);
			}
			else if (playerInstance.getKarma() > 0)
			{
				htmContent = HtmCache.getInstance().getHtm(playerInstance.getHtmlPrefix(), htmlPath+"Karma.htm");
				if (htmContent != null)
					npcHtmlMessage.setHtml(htmContent);
			}
			else if (playerLevel < eventSettings.getTvTEventMinPlayerLevel() || playerLevel > eventSettings.getTvTEventMaxPlayerLevel())
			{
				htmContent = HtmCache.getInstance().getHtm(playerInstance.getHtmlPrefix(), htmlPath+"Level.htm");
				if (htmContent != null)
				{
					npcHtmlMessage.setHtml(htmContent);
					npcHtmlMessage.replace("%min%", String.valueOf(eventSettings.getTvTEventMinPlayerLevel()));
					npcHtmlMessage.replace("%max%", String.valueOf(eventSettings.getTvTEventMaxPlayerLevel()));
				}
			}
			else if (_teams[0].getParticipatedPlayerCount() == eventSettings.getTvTEventMaxPlayersInTeams() && _teams[1].getParticipatedPlayerCount() == eventSettings.getTvTEventMaxPlayersInTeams())
			{
				htmContent = HtmCache.getInstance().getHtm(playerInstance.getHtmlPrefix(), htmlPath+"TeamsFull.htm");
				if (htmContent != null)
				{
					npcHtmlMessage.setHtml(htmContent);
					npcHtmlMessage.replace("%max%", String.valueOf(eventSettings.getTvTEventMaxPlayersInTeams()));
				}
			}
			else if (!payParticipationFee(playerInstance))
			{
				htmContent = HtmCache.getInstance().getHtm(playerInstance.getHtmlPrefix(), htmlPath+"ParticipationFee.htm");
				if (htmContent != null)
				{
					npcHtmlMessage.setHtml(htmContent);
					npcHtmlMessage.replace("%fee%", getParticipationFee());
				}
			}
			else if (addParticipant(playerInstance))
				npcHtmlMessage.setHtml(HtmCache.getInstance().getHtm(playerInstance.getHtmlPrefix(), htmlPath+"Registered.htm"));
			else
				return;

			playerInstance.sendPacket(npcHtmlMessage);
		}
		else if (command.equals("tvt_event_remove_participation"))
		{
			removeParticipant(playerInstance.getObjectId(), playerInstance);

			NpcHtmlMessage npcHtmlMessage = new NpcHtmlMessage(0);
			npcHtmlMessage.setHtml(HtmCache.getInstance().getHtm(playerInstance.getHtmlPrefix(), htmlPath+"Unregistered.htm"));
			playerInstance.sendPacket(npcHtmlMessage);
		}
	}

	/**
	 * Called on every onAction in L2PcIstance<br><br>
	 * @return boolean: true if player is allowed to target, otherwise false<br>
	 */
	public static boolean onAction(L2PcInstance playerInstance, int targetedPlayerObjectId)
	{
		if (playerInstance == null || !isStarted())
		{
			return true;
		}

		if (playerInstance.isGM())
			return true;

		byte playerTeamId = getParticipantTeamId(playerInstance.getObjectId());
		byte targetedPlayerTeamId = getParticipantTeamId(targetedPlayerObjectId);

		if ((playerTeamId != -1 && targetedPlayerTeamId == -1) || (playerTeamId == -1 && targetedPlayerTeamId != -1))
		{
			return false;
		}

		if (playerTeamId != -1 && targetedPlayerTeamId != -1 && playerTeamId == targetedPlayerTeamId && playerInstance.getObjectId() != targetedPlayerObjectId && 
				!getSettings(EventSettings.class).isTvTEventTargetTeamMembersAllowed())
		{
			return false;
		}
		return true;
	}

	/**
	 * Called on every scroll use<br><br>
	 * @param playerName as String<br>
	 * @return boolean: true if player is allowed to use scroll, otherwise false<br>
	 */
	public static boolean onScrollUse(int playerObjectId)
	{
		if (!isStarted())
			return true;

		if (isPlayerParticipant(playerObjectId) && !getSettings(EventSettings.class).isTvTEventScrollsAllowed())
			return false;

		return true;
	}
	
	/**
	 * Called on every potion use<br><br>
	 *
	 * @param playerName as String<br>
	 * @return boolean: true if player is allowed to use potions, otherwise false<br>
	 */
	public static boolean onPotionUse(int playerObjectId)
	{
		if (!isStarted())
			return true;

		if (isPlayerParticipant(playerObjectId) && !getSettings(EventSettings.class).isTvTEventPotionsAllowed())
			return false;

		return true;
	}

	/**
	 * Called on every escape use(thanks to nbd)<br><br>
	 * @param playerName as String<br>
	 * @return boolean: true if player is not in tvt event, otherwise false<br>
	 */
	public static boolean onEscapeUse(int playerObjectId)
	{
		if (!isStarted())
		{
			return true;
		}

		if (isPlayerParticipant(playerObjectId))
		{
			return false;
		}

		return true;
	}

	/**
	 * Called on every summon item use<br><br>
	 * @param playerName as String<br>
	 * @return boolean: true if player is allowed to summon by item, otherwise false<br>
	 */
	public static boolean onItemSummon(int playerObjectId)
	{
		if (!isStarted())
		{
			return true;
		}

		if (isPlayerParticipant(playerObjectId) && ! getSettings(EventSettings.class).isTvTEventSummonByItemAllowed())
		{
			return false;
		}
		return true;
	}

	/**
	 * Is called when a player is killed<br><br>
	 * @param killerCharacter as L2Character<br>
	 * @param killedPlayerInstance as L2PcInstance<br>
	 */
	public static void onKill(L2Character killerCharacter, L2PcInstance killedPlayerInstance)
	{
		if (killedPlayerInstance == null || !isStarted())
		{
			return;
		}

		byte killedTeamId = getParticipantTeamId(killedPlayerInstance.getObjectId());
		if (killedTeamId == -1)
		{
			return;
		}

		new TvTEventTeleporter(killedPlayerInstance, _teams[killedTeamId].getCoordinates(), false, false);
		if (killerCharacter == null)
		{
			return;
		}

		L2PcInstance killerPlayerInstance = null;
		if (killerCharacter instanceof L2PetInstance || killerCharacter instanceof L2SummonInstance)
		{
			killerPlayerInstance = ((L2Summon) killerCharacter).getOwner();
			if (killerPlayerInstance == null)
			{
				return;
			}
		}
		else if (killerCharacter instanceof L2PcInstance)
		{
			killerPlayerInstance = (L2PcInstance) killerCharacter;
		}
		else
		{
			return;
		}

		byte killerTeamId = getParticipantTeamId(killerPlayerInstance.getObjectId());
		if (killerTeamId != -1 && killedTeamId != -1 && killerTeamId != killedTeamId)
		{
			TvTEventTeam killerTeam = _teams[killerTeamId];
			killerTeam.increasePoints();
			if (getSettings(EventSettings.class).isTvTRewardOnlyKillers())
				killerPlayerInstance.increaseRewardKills();

			String onKill = getSettings(EventSettings.class).getTvTEventOnKill();
			if ("pm".equalsIgnoreCase(onKill))
			{
				CreatureSay cs = new CreatureSay(killerPlayerInstance.getObjectId(), Say2.TELL, killerPlayerInstance.getName(), "I have killed " + killedPlayerInstance.getName() + "!");
				for (L2PcInstance playerInstance : _teams[killerTeamId].getParticipatedPlayers().values())
				{
					if (playerInstance != null)
						playerInstance.sendPacket(cs);
				}
			}
			else if (ON_KILL_TITLE.equalsIgnoreCase(onKill))
			{
				killerPlayerInstance.increasePointScore();
				killerPlayerInstance.setTitle("Kills: " + killerPlayerInstance.getPointScore());
				killerPlayerInstance.broadcastTitleInfo();
			}
			else if (ON_KILL_PMTITLE.equalsIgnoreCase(onKill))
			{
				CreatureSay cs = new CreatureSay(killerPlayerInstance.getObjectId(), Say2.TELL, killerPlayerInstance.getName(), "I have killed " + killedPlayerInstance.getName() + "!");

				for (L2PcInstance playerInstance : _teams[killerTeamId].getParticipatedPlayers().values())
				{
					if (playerInstance != null)
						playerInstance.sendPacket(cs);
				}
				killerPlayerInstance.increasePointScore();
				killerPlayerInstance.setTitle("Kills: " + killerPlayerInstance.getPointScore());
				killerPlayerInstance.broadcastTitleInfo();
			}
		}
	}

	/**
	 * Called on Appearing packet received (player finished teleporting)<br><br>
	 * @param L2PcInstance playerInstance
	 */
	public static void onTeleported(L2PcInstance playerInstance)
	{
		if (!isStarted() || playerInstance == null || !isPlayerParticipant(playerInstance.getObjectId()))
			return;

		if (playerInstance.isMageClass())
		{
			Map<Integer, Integer> mageBuffs = getSettings(EventSettings.class).getTvTEventMageBuffs();
			if (mageBuffs != null && !mageBuffs.isEmpty())
			{
				for (Entry<Integer, Integer> entry : mageBuffs.entrySet())
				{
					L2Skill skill = SkillTable.getInstance().getInfo(entry.getKey(), entry.getValue());
					if (skill != null)
						skill.getEffects(playerInstance, playerInstance);
				}
			}
		}
		else
		{
			Map<Integer, Integer> fitherBuffs = getSettings(EventSettings.class).getTvTEventFighterBuffs();
			if (fitherBuffs != null && !fitherBuffs.isEmpty())
			{
				for (Entry<Integer, Integer> entry : fitherBuffs.entrySet())
				{
					L2Skill skill = SkillTable.getInstance().getInfo(entry.getKey(), entry.getValue());
					if (skill != null)
						skill.getEffects(playerInstance, playerInstance);
				}
			}
		}

		if (getSettings(EventSettings.class).isTvTRestoreCPHPMP()) {
			playerInstance.setCurrentCp(playerInstance.getMaxCp());
			playerInstance.setCurrentHpMp(playerInstance.getMaxHp(), playerInstance.getMaxMp());
		}
	}

	/*
	 * Return true if player valid for skill
	 */
	public static final boolean checkForTvTSkill(L2PcInstance source, L2PcInstance target, L2Skill skill)
	{
		if (!isStarted())
			return true;
		// TvT is started
		final int sourcePlayerId = source.getObjectId();
		final int targetPlayerId = target.getObjectId();
		final boolean isSourceParticipant = isPlayerParticipant(sourcePlayerId);
		final boolean isTargetParticipant = isPlayerParticipant(targetPlayerId);

		// both players not participating
		if (!isSourceParticipant && !isTargetParticipant)
			return true;
		// one player not participating
		if (!(isSourceParticipant && isTargetParticipant))
			return false;
		// players in the different teams ?
		if (getParticipantTeamId(sourcePlayerId) != getParticipantTeamId(targetPlayerId))
		{
			if (!skill.isOffensive())
				return false;
		}
		return true;
	}

	/**
	 * Sets the TvTEvent state<br><br>
	 * @param state as EventState<br>
	 */
	private static void setState(EventState state)
	{
		synchronized (_state)
		{
			_state = state;
		}
	}

	/**
	 * Is TvTEvent inactive?<br><br>
	 * @return boolean: true if event is inactive(waiting for next event cycle), otherwise false<br>
	 */
	public static boolean isInactive()
	{
		boolean isInactive;
		
		synchronized (_state)
		{
			isInactive = _state == EventState.INACTIVE;
		}
		
		return isInactive;
	}

	/**
	 * Is TvTEvent in inactivating?<br><br>
	 * @return boolean: true if event is in inactivating progress, otherwise false<br>
	 */
	public static boolean isInactivating()
	{
		boolean isInactivating;

		synchronized (_state)
		{
			isInactivating = _state == EventState.INACTIVATING;
		}

		return isInactivating;
	}

	/**
	 * Is TvTEvent in participation?<br><br>
	 * @return boolean: true if event is in participation progress, otherwise false<br>
	 */
	public static boolean isParticipating()
	{
		boolean isParticipating;

		synchronized (_state)
		{
			isParticipating = _state == EventState.PARTICIPATING;
		}

		return isParticipating;
	}

	/**
	 * Is TvTEvent starting?<br><br>
	 * @return boolean: true if event is starting up(setting up fighting spot, teleport players etc.), otherwise false<br>
	 */
	public static boolean isStarting()
	{
		boolean isStarting;

		synchronized (_state)
		{
			isStarting = _state == EventState.STARTING;
		}

		return isStarting;
	}

	/**
	 * Is TvTEvent started?<br><br>
	 * @return boolean: true if event is started, otherwise false<br>
	 */
	public static boolean isStarted()
	{
		boolean isStarted;

		synchronized (_state)
		{
			isStarted = _state == EventState.STARTED;
		}

		return isStarted;
	}

	/**
	 * Is TvTEvent rewadrding?<br><br>
	 * @return boolean: true if event is currently rewarding, otherwise false<br>
	 */
	public static boolean isRewarding()
	{
		boolean isRewarding;

		synchronized (_state)
		{
			isRewarding = _state == EventState.REWARDING;
		}

		return isRewarding;
	}

	/**
	 * Returns the team id of a player, if player is not participant it returns -1<br><br>
	 * @param playerName as String<br>
	 * @return byte: team name of the given playerName, if not in event -1<br>
	 */
	public static byte getParticipantTeamId(int playerObjectId)
	{
		return (byte) (_teams[0].containsPlayer(playerObjectId) ? 0 : (_teams[1].containsPlayer(playerObjectId) ? 1 : -1));
	}

	/**
	 * Returns the team of a player, if player is not participant it returns null <br><br>
	 * @param player objectId as Integer<br>
	 * @return TvTEventTeam: team of the given playerObjectId, if not in event null <br>
	 */
	public static TvTEventTeam getParticipantTeam(int playerObjectId)
	{
		return (_teams[0].containsPlayer(playerObjectId) ? _teams[0] : (_teams[1].containsPlayer(playerObjectId) ? _teams[1] : null));
	}

	/**
	 * Returns the enemy team of a player, if player is not participant it returns null <br><br>
	 * @param player objectId as Integer<br>
	 * @return TvTEventTeam: enemy team of the given playerObjectId, if not in event null <br>
	 */
	public static TvTEventTeam getParticipantEnemyTeam(int playerObjectId)
	{
		return (_teams[0].containsPlayer(playerObjectId) ? _teams[1] : (_teams[1].containsPlayer(playerObjectId) ? _teams[0] : null));
	}

	/**
	 * Returns the team coordinates in which the player is in, if player is not in a team return null<br><br>
	 * @param playerName as String<br>
	 * @return int[]: coordinates of teams, 2 elements, index 0 for team 1 and index 1 for team 2<br>
	 */
	public static int[] getParticipantTeamCoordinates(int playerObjectId)
	{
		return _teams[0].containsPlayer(playerObjectId) ? _teams[0].getCoordinates() : (_teams[1].containsPlayer(playerObjectId) ? _teams[1].getCoordinates() : null);
	}

	/**
	 * Is given player participant of the event?<br><br>
	 * @param playerName as String<br>
	 * @return boolean: true if player is participant, ohterwise false<br>
	 */
	public static boolean isPlayerParticipant(int playerObjectId)
	{
		if (!isParticipating() && !isStarting() && !isStarted())
		{
			return false;
		}

		return _teams[0].containsPlayer(playerObjectId) || _teams[1].containsPlayer(playerObjectId);
	}

	/**
	 * Returns participated player count<br><br>
	 * @return int: amount of players registered in the event<br>
	 */
	public static int getParticipatedPlayersCount()
	{
		if (!isParticipating() && !isStarting() && !isStarted())
		{
			return 0;
		}

		return _teams[0].getParticipatedPlayerCount() + _teams[1].getParticipatedPlayerCount();
	}

	/**
	 * Returns teams names<br><br>
	 * @return String[]: names of teams, 2 elements, index 0 for team 1 and index 1 for team 2<br>
	 */
	public static String[] getTeamNames()
	{
		return new String[]
		{
			_teams[0].getName(), _teams[1].getName()
		};
	}

	/**
	 * Returns player count of both teams<br><br>
	 * @return int[]: player count of teams, 2 elements, index 0 for team 1 and index 1 for team 2<br>
	 */
	public static int[] getTeamsPlayerCounts()
	{
		return new int[]
		{
			_teams[0].getParticipatedPlayerCount(), _teams[1].getParticipatedPlayerCount()
		};
	}

	/**
	 * Returns points count of both teams
	 * @return int[]: points of teams, 2 elements, index 0 for team 1 and index 1 for team 2<br>
	 */
	public static int[] getTeamsPoints()
	{
		return new int[]
		{
			_teams[0].getPoints(), _teams[1].getPoints()
		};
	}

	public static int getTvTEventInstance()
	{
		return _TvTEventInstance;
	}
}