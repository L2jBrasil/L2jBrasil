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
package com.it.br.gameserver.instancemanager;

import static com.it.br.configuration.Configurator.getSettings;
import static com.it.br.util.Util.minutesToMiliseconds;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ScheduledFuture;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.it.br.Config;
import com.it.br.L2DatabaseFactory;
import com.it.br.configuration.settings.SepulchersSettings;
import com.it.br.gameserver.ThreadPoolManager;
import com.it.br.gameserver.datatables.sql.SpawnTable;
import com.it.br.gameserver.datatables.xml.DoorTable;
import com.it.br.gameserver.datatables.xml.NpcTable;
import com.it.br.gameserver.model.L2ItemInstance;
import com.it.br.gameserver.model.L2Spawn;
import com.it.br.gameserver.model.actor.instance.L2DoorInstance;
import com.it.br.gameserver.model.actor.instance.L2NpcInstance;
import com.it.br.gameserver.model.actor.instance.L2PcInstance;
import com.it.br.gameserver.model.actor.instance.L2SepulcherMonsterInstance;
import com.it.br.gameserver.model.actor.instance.L2SepulcherNpcInstance;
import com.it.br.gameserver.model.quest.QuestState;
import com.it.br.gameserver.network.SystemMessageId;
import com.it.br.gameserver.network.serverpackets.NpcHtmlMessage;
import com.it.br.gameserver.network.serverpackets.SystemMessage;
import com.it.br.gameserver.templates.L2NpcTemplate;
import com.it.br.gameserver.util.Util;
import com.it.br.util.Rnd;

/**
 * @author sandman
 */
public class FourSepulchersManager
{
	protected static final Logger _log = Logger.getLogger(FourSepulchersManager.class.getName());
	private static final String QUEST_ID = "620_FourGoblets";
	private static final int ENTRANCE_PASS = 7075;
	private static final int USED_PASS = 7261;
	private static final int CHAPEL_KEY = 7260;
	private static final int ANTIQUE_BROOCH = 7262;
	protected boolean _firstTimeRun;
	protected boolean _inEntryTime = false;
	protected boolean _inWarmUpTime = false;
	protected boolean _inAttackTime = false;
	protected boolean _inCoolDownTime = false;
	protected ScheduledFuture<?> _changeCoolDownTimeTask = null;
	protected ScheduledFuture<?> _changeEntryTimeTask = null;
	protected ScheduledFuture<?> _changeWarmUpTimeTask = null;
	protected ScheduledFuture<?> _changeAttackTimeTask = null;
	protected ScheduledFuture<?> _onPartyAnnihilatedTask = null;
	
	private int[][] _startHallSpawn = { 
			{ 181632, -85587, -7218 }, 
			{ 179963, -88978, -7218 }, 
			{ 173217, -86132, -7218 }, 
			{ 175608, -82296, -7218 } };
	
	private int[][][] _shadowSpawnLoc = { 
			{ 
				{ 25339, 191231, -85574, -7216, 33380 }, 
				{ 25349, 189534, -88969, -7216, 32768 }, 
				{ 25346, 173195, -76560, -7215, 49277 }, 
				{ 25342, 175591, -72744, -7215, 49317 }
			}, 
			{
				{ 25342, 191231, -85574, -7216, 33380 }, 
				{ 25339, 189534, -88969, -7216, 32768 }, 
				{ 25349, 173195, -76560, -7215, 49277 }, 
				{ 25346, 175591, -72744, -7215, 49317 } 
			},
			{ 
				{ 25346, 191231, -85574, -7216, 33380 }, 
				{ 25342, 189534, -88969, -7216, 32768 }, 
				{ 25339, 173195, -76560, -7215, 49277 }, 
				{ 25349, 175591, -72744, -7215, 49317 }
			}, 
			{ 
				{ 25349, 191231, -85574, -7216, 33380 }, 
				{ 25346, 189534, -88969, -7216, 32768 },
				{ 25342, 173195, -76560, -7215, 49277 }, 
				{ 25339, 175591, -72744, -7215, 49317 } 
			}, };
	
	protected Map<Integer, Boolean> _archonSpawned = new HashMap<>();
	protected Map<Integer, Boolean> _hallInUse = new HashMap<>();
	protected Map<Integer, int[]> _startHallSpawns = new HashMap<>();
	protected Map<Integer, Integer> _hallGateKeepers = new HashMap<>();
	protected Map<Integer, Integer> _keyBoxNpc = new HashMap<>();
	protected Map<Integer, Integer> _victim = new HashMap<>();
	protected Map<Integer, L2PcInstance> _challengers = new HashMap<>();
	protected Map<Integer, L2Spawn> _executionerSpawns = new HashMap<>();
	protected Map<Integer, L2Spawn> _keyBoxSpawns = new HashMap<>();
	protected Map<Integer, L2Spawn> _mysteriousBoxSpawns = new HashMap<>();
	protected Map<Integer, L2Spawn> _shadowSpawns = new HashMap<>();
	protected Map<Integer, List<L2Spawn>> _dukeFinalMobs = new HashMap<>();
	protected Map<Integer, List<L2SepulcherMonsterInstance>> _dukeMobs = new HashMap<>();
	protected Map<Integer, List<L2Spawn>> _emperorsGraveNpcs = new HashMap<>();
	protected Map<Integer, List<L2Spawn>> _magicalMonsters = new HashMap<>();
	protected Map<Integer, List<L2Spawn>> _physicalMonsters = new HashMap<>();
	protected Map<Integer, List<L2SepulcherMonsterInstance>> _viscountMobs = new HashMap<>();
	protected List<L2Spawn> _physicalSpawns;
	protected List<L2Spawn> _magicalSpawns;
	protected List<L2Spawn> _managers;
	protected List<L2Spawn> _dukeFinalSpawns;
	protected List<L2Spawn> _emperorsGraveSpawns;
	protected List<L2NpcInstance> _allMobs = new ArrayList<>();
	protected long _attackTimeEnd = 0;
	protected long _coolDownTimeEnd = 0;
	protected long _entryTimeEnd = 0;
	protected long _warmUpTimeEnd = 0;
	protected byte _newCycleMin = 55;

	private FourSepulchersManager()
	{
	}

	public static final FourSepulchersManager getInstance()
	{
		return SingletonHolder._instance;
	}

	public void init()
	{
		if(_changeCoolDownTimeTask != null)
		{
			_changeCoolDownTimeTask.cancel(true);
		}
		if(_changeEntryTimeTask != null)
		{
			_changeEntryTimeTask.cancel(true);
		}
		if(_changeWarmUpTimeTask != null)
		{
			_changeWarmUpTimeTask.cancel(true);
		}
		if(_changeAttackTimeTask != null)
		{
			_changeAttackTimeTask.cancel(true);
		}

		_changeCoolDownTimeTask = null;
		_changeEntryTimeTask = null;
		_changeWarmUpTimeTask = null;
		_changeAttackTimeTask = null;

		_inEntryTime = false;
		_inWarmUpTime = false;
		_inAttackTime = false;
		_inCoolDownTime = false;

		_firstTimeRun = true;
		initFixedInfo();
		loadMysteriousBox();
		initKeyBoxSpawns();
		loadPhysicalMonsters();
		loadMagicalMonsters();
		initLocationShadowSpawns();
		initExecutionerSpawns();
		loadDukeMonsters();
		loadEmperorsGraveMonsters();
		spawnManagers();
		timeSelector();
	}

	// phase select on server launch
	protected void timeSelector()
	{
		timeCalculator();
		long currentTime = Calendar.getInstance().getTimeInMillis();
		// if current time >= time of entry beginning and if current time < time
		// of entry beginning + time of entry end
		if (currentTime >= _coolDownTimeEnd && currentTime < _entryTimeEnd) // entry
		// time
		// check
		{
			clean();
			_changeEntryTimeTask = ThreadPoolManager.getInstance().scheduleGeneral(new ChangeEntryTime(), 0);
			_log.info("FourSepulchersManager: Beginning in Entry time");
		}
		else if (currentTime >= _entryTimeEnd && currentTime < _warmUpTimeEnd) // warmup
		// time
		// check
		{
			clean();
			_changeWarmUpTimeTask = ThreadPoolManager.getInstance().scheduleGeneral(new ChangeWarmUpTime(), 0);
			_log.info("FourSepulchersManager: Beginning in WarmUp time");
		}
		else if (currentTime >= _warmUpTimeEnd && currentTime < _attackTimeEnd) // attack
		// time
		// check
		{
			clean();
			_changeAttackTimeTask = ThreadPoolManager.getInstance().scheduleGeneral(new ChangeAttackTime(), 0);
			_log.info("FourSepulchersManager: Beginning in Attack time");
		}
		else
		// else cooldown time and without cleanup because it's already
		// implemented
		{
			_changeCoolDownTimeTask = ThreadPoolManager.getInstance().scheduleGeneral(new ChangeCoolDownTime(), 0);
			_log.info("FourSepulchersManager: Beginning in Cooldown time");
		}
	}

	// phase end times calculator
	protected void timeCalculator()
	{
		Calendar tmp = Calendar.getInstance();
		if (tmp.get(Calendar.MINUTE) < _newCycleMin)
			tmp.set(Calendar.HOUR, Calendar.getInstance().get(Calendar.HOUR) - 1);
		tmp.set(Calendar.MINUTE, _newCycleMin);
		_coolDownTimeEnd = tmp.getTimeInMillis();
		SepulchersSettings sepulcheresSettings = getSettings(SepulchersSettings.class);
		_entryTimeEnd = _coolDownTimeEnd + minutesToMiliseconds(sepulcheresSettings.getEntryTime());
		_warmUpTimeEnd = _entryTimeEnd + minutesToMiliseconds(sepulcheresSettings.getWarmUpTime());
		_attackTimeEnd = _warmUpTimeEnd + minutesToMiliseconds(sepulcheresSettings.getAttackTime());
	}

	public void clean()
	{
		for(int i = 31921; i < 31925; i++)
		{
			int[] Location = _startHallSpawns.get(i);
			GrandBossManager.getInstance().getZone(Location[0], Location[1], Location[2]).oustAllPlayers();
		}

		deleteAllMobs();
		closeAllDoors();

		_hallInUse.clear();
		_hallInUse.put(31921, false);
		_hallInUse.put(31922, false);
		_hallInUse.put(31923, false);
		_hallInUse.put(31924, false);

		if(_archonSpawned.size() != 0)
		{
			Set<Integer> npcIdSet = _archonSpawned.keySet();
			for(int npcId : npcIdSet)
			{
				_archonSpawned.put(npcId, false);
			}
		}
	}

	protected void spawnManagers()
	{
		_managers = new ArrayList<>();
		// L2Spawn spawnDat;
		int i = 31921;
		for (L2Spawn spawnDat; i <= 31924; i++)
		{
			if (i < 31921 || i > 31924)
				continue;
			L2NpcTemplate template1 = NpcTable.getInstance().getTemplate(i);
			if (template1 == null)
				continue;
			try
			{
				spawnDat = new L2Spawn(template1);
				spawnDat.setAmount(1);
				spawnDat.setRespawnDelay(60);
				switch (i)
				{
					case 31921: // conquerors
						spawnDat.setLocx(181061);
						spawnDat.setLocy(-85595);
						spawnDat.setLocz(-7200);
						spawnDat.setHeading(-32584);
						break;
					case 31922: // emperors
						spawnDat.setLocx(179292);
						spawnDat.setLocy(-88981);
						spawnDat.setLocz(-7200);
						spawnDat.setHeading(-33272);
						break;
					case 31923: // sages
						spawnDat.setLocx(173202);
						spawnDat.setLocy(-87004);
						spawnDat.setLocz(-7200);
						spawnDat.setHeading(-16248);
						break;
					case 31924: // judges
						spawnDat.setLocx(175606);
						spawnDat.setLocy(-82853);
						spawnDat.setLocz(-7200);
						spawnDat.setHeading(-16248);
						break;
				}
				_managers.add(spawnDat);
				SpawnTable.getInstance().addNewSpawn(spawnDat, false);
				spawnDat.doSpawn();
				spawnDat.startRespawn();
			}
			catch (SecurityException e)
			{
				e.printStackTrace();
			}
			catch (ClassNotFoundException e)
			{
				e.printStackTrace();
			}
			catch (NoSuchMethodException e)
			{
				e.printStackTrace();
			}
		}
	}

	protected void initFixedInfo()
	{
		_startHallSpawns.put(31921, _startHallSpawn[0]);
		_startHallSpawns.put(31922, _startHallSpawn[1]);
		_startHallSpawns.put(31923, _startHallSpawn[2]);
		_startHallSpawns.put(31924, _startHallSpawn[3]);

		_hallInUse.put(31921, false);
		_hallInUse.put(31922, false);
		_hallInUse.put(31923, false);
		_hallInUse.put(31924, false);

		_hallGateKeepers.put(31925, 25150012);
		_hallGateKeepers.put(31926, 25150013);
		_hallGateKeepers.put(31927, 25150014);
		_hallGateKeepers.put(31928, 25150015);
		_hallGateKeepers.put(31929, 25150016);
		_hallGateKeepers.put(31930, 25150002);
		_hallGateKeepers.put(31931, 25150003);
		_hallGateKeepers.put(31932, 25150004);
		_hallGateKeepers.put(31933, 25150005);
		_hallGateKeepers.put(31934, 25150006);
		_hallGateKeepers.put(31935, 25150032);
		_hallGateKeepers.put(31936, 25150033);
		_hallGateKeepers.put(31937, 25150034);
		_hallGateKeepers.put(31938, 25150035);
		_hallGateKeepers.put(31939, 25150036);
		_hallGateKeepers.put(31940, 25150022);
		_hallGateKeepers.put(31941, 25150023);
		_hallGateKeepers.put(31942, 25150024);
		_hallGateKeepers.put(31943, 25150025);
		_hallGateKeepers.put(31944, 25150026);

		_keyBoxNpc.put(18120, 31455);
		_keyBoxNpc.put(18121, 31455);
		_keyBoxNpc.put(18122, 31455);
		_keyBoxNpc.put(18123, 31455);
		_keyBoxNpc.put(18124, 31456);
		_keyBoxNpc.put(18125, 31456);
		_keyBoxNpc.put(18126, 31456);
		_keyBoxNpc.put(18127, 31456);
		_keyBoxNpc.put(18128, 31457);
		_keyBoxNpc.put(18129, 31457);
		_keyBoxNpc.put(18130, 31457);
		_keyBoxNpc.put(18131, 31457);
		_keyBoxNpc.put(18149, 31458);
		_keyBoxNpc.put(18150, 31459);
		_keyBoxNpc.put(18151, 31459);
		_keyBoxNpc.put(18152, 31459);
		_keyBoxNpc.put(18153, 31459);
		_keyBoxNpc.put(18154, 31460);
		_keyBoxNpc.put(18155, 31460);
		_keyBoxNpc.put(18156, 31460);
		_keyBoxNpc.put(18157, 31460);
		_keyBoxNpc.put(18158, 31461);
		_keyBoxNpc.put(18159, 31461);
		_keyBoxNpc.put(18160, 31461);
		_keyBoxNpc.put(18161, 31461);
		_keyBoxNpc.put(18162, 31462);
		_keyBoxNpc.put(18163, 31462);
		_keyBoxNpc.put(18164, 31462);
		_keyBoxNpc.put(18165, 31462);
		_keyBoxNpc.put(18183, 31463);
		_keyBoxNpc.put(18184, 31464);
		_keyBoxNpc.put(18212, 31465);
		_keyBoxNpc.put(18213, 31465);
		_keyBoxNpc.put(18214, 31465);
		_keyBoxNpc.put(18215, 31465);
		_keyBoxNpc.put(18216, 31466);
		_keyBoxNpc.put(18217, 31466);
		_keyBoxNpc.put(18218, 31466);
		_keyBoxNpc.put(18219, 31466);

		_victim.put(18150, 18158);
		_victim.put(18151, 18159);
		_victim.put(18152, 18160);
		_victim.put(18153, 18161);
		_victim.put(18154, 18162);
		_victim.put(18155, 18163);
		_victim.put(18156, 18164);
		_victim.put(18157, 18165);
	}


	private void loadMysteriousBox()
	{
		Connection con = null;
		_mysteriousBoxSpawns.clear();
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			PreparedStatement statement = con.prepareStatement("SELECT id, count, npc_templateid, locx, locy, locz, heading, respawn_delay, key_npc_id FROM four_sepulchers_spawnlist Where spawntype = ? ORDER BY id");
			statement.setInt(1, 0);
			ResultSet rset = statement.executeQuery();
			L2Spawn spawnDat;
			L2NpcTemplate template1;
			while (rset.next())
			{
				template1 = NpcTable.getInstance().getTemplate(rset.getInt("npc_templateid"));
				if (template1 != null)
				{
					spawnDat = new L2Spawn(template1);
					spawnDat.setAmount(rset.getInt("count"));
					spawnDat.setLocx(rset.getInt("locx"));
					spawnDat.setLocy(rset.getInt("locy"));
					spawnDat.setLocz(rset.getInt("locz"));
					spawnDat.setHeading(rset.getInt("heading"));
					spawnDat.setRespawnDelay(rset.getInt("respawn_delay"));
					SpawnTable.getInstance().addNewSpawn(spawnDat, false);
					int keyNpcId = rset.getInt("key_npc_id");
					_mysteriousBoxSpawns.put(keyNpcId, spawnDat);
				} else
					_log.warning("FourSepulchersManager.LoadMysteriousBox: Data missing in NPC table for ID: " + rset.getInt("npc_templateid") + ".");
			}
			rset.close();
			statement.close();
			_log.info("FourSepulchersManager: loaded " + _mysteriousBoxSpawns.size() + " Mysterious-Box spawns.");
		}
		catch (Exception e)
		{
			// problem with initializing spawn, go to next one
			_log.warning("FourSepulchersManager.LoadMysteriousBox: Spawn could not be initialized: " + e);
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

	private void initKeyBoxSpawns()
	{
		L2Spawn spawnDat;
		L2NpcTemplate template;
		for (int keyNpcId : _keyBoxNpc.keySet())
			try
			{
				template = NpcTable.getInstance().getTemplate(_keyBoxNpc.get(keyNpcId));
				if (template != null)
				{
					spawnDat = new L2Spawn(template);
					spawnDat.setAmount(1);
					spawnDat.setLocx(0);
					spawnDat.setLocy(0);
					spawnDat.setLocz(0);
					spawnDat.setHeading(0);
					spawnDat.setRespawnDelay(3600);
					SpawnTable.getInstance().addNewSpawn(spawnDat, false);
					_keyBoxSpawns.put(keyNpcId, spawnDat);
				} else
					_log.warning("FourSepulchersManager.InitKeyBoxSpawns: Data missing in NPC table for ID: " + _keyBoxNpc.get(keyNpcId) + ".");
			}
			catch (Exception e)
			{
				_log.warning("FourSepulchersManager.InitKeyBoxSpawns: Spawn could not be initialized: " + e);
			}
	}

	private void loadPhysicalMonsters()
	{
		_physicalMonsters.clear();
		int loaded = 0;
		Connection con = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			PreparedStatement statement1 = con.prepareStatement("SELECT Distinct key_npc_id FROM four_sepulchers_spawnlist Where spawntype = ? ORDER BY key_npc_id");
			statement1.setInt(1, 1);
			ResultSet rset1 = statement1.executeQuery();
			while (rset1.next())
			{
				int keyNpcId = rset1.getInt("key_npc_id");
				PreparedStatement statement2 = con.prepareStatement("SELECT id, count, npc_templateid, locx, locy, locz, heading, respawn_delay, key_npc_id FROM four_sepulchers_spawnlist Where key_npc_id = ? and spawntype = ? ORDER BY id");
				statement2.setInt(1, keyNpcId);
				statement2.setInt(2, 1);
				ResultSet rset2 = statement2.executeQuery();
				L2Spawn spawnDat;
				L2NpcTemplate template1;
				_physicalSpawns = new ArrayList<>();
				while (rset2.next())
				{
					template1 = NpcTable.getInstance().getTemplate(rset2.getInt("npc_templateid"));
					if (template1 != null)
					{
						spawnDat = new L2Spawn(template1);
						spawnDat.setAmount(rset2.getInt("count"));
						spawnDat.setLocx(rset2.getInt("locx"));
						spawnDat.setLocy(rset2.getInt("locy"));
						spawnDat.setLocz(rset2.getInt("locz"));
						spawnDat.setHeading(rset2.getInt("heading"));
						spawnDat.setRespawnDelay(rset2.getInt("respawn_delay"));
						SpawnTable.getInstance().addNewSpawn(spawnDat, false);
						_physicalSpawns.add(spawnDat);
						loaded++;
					} else
						_log.warning("FourSepulchersManager.LoadPhysicalMonsters: Data missing in NPC table for ID: " + rset2.getInt("npc_templateid") + ".");
				}
				rset2.close();
				statement2.close();
				_physicalMonsters.put(keyNpcId, _physicalSpawns);
			}
			rset1.close();
			statement1.close();
			_log.info("FourSepulchersManager: loaded " + loaded + " Physical type monsters spawns.");
		}
		catch (Exception e)
		{
			// problem with initializing spawn, go to next one
			_log.warning("FourSepulchersManager.LoadPhysicalMonsters: Spawn could not be initialized: " + e);
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

	private void loadMagicalMonsters()
	{
		_magicalMonsters.clear();
		int loaded = 0;
		Connection con = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			PreparedStatement statement1 = con.prepareStatement("SELECT Distinct key_npc_id FROM four_sepulchers_spawnlist Where spawntype = ? ORDER BY key_npc_id");
			statement1.setInt(1, 2);
			ResultSet rset1 = statement1.executeQuery();
			while (rset1.next())
			{
				int keyNpcId = rset1.getInt("key_npc_id");
				PreparedStatement statement2 = con.prepareStatement("SELECT id, count, npc_templateid, locx, locy, locz, heading, respawn_delay, key_npc_id FROM four_sepulchers_spawnlist Where key_npc_id = ? and spawntype = ? ORDER BY id");
				statement2.setInt(1, keyNpcId);
				statement2.setInt(2, 2);
				ResultSet rset2 = statement2.executeQuery();
				L2Spawn spawnDat;
				L2NpcTemplate template1;
				_magicalSpawns = new ArrayList<>();
				while (rset2.next())
				{
					template1 = NpcTable.getInstance().getTemplate(rset2.getInt("npc_templateid"));
					if (template1 != null)
					{
						spawnDat = new L2Spawn(template1);
						spawnDat.setAmount(rset2.getInt("count"));
						spawnDat.setLocx(rset2.getInt("locx"));
						spawnDat.setLocy(rset2.getInt("locy"));
						spawnDat.setLocz(rset2.getInt("locz"));
						spawnDat.setHeading(rset2.getInt("heading"));
						spawnDat.setRespawnDelay(rset2.getInt("respawn_delay"));
						SpawnTable.getInstance().addNewSpawn(spawnDat, false);
						_magicalSpawns.add(spawnDat);
						loaded++;
					} else
						_log.warning("FourSepulchersManager.LoadMagicalMonsters: Data missing in NPC table for ID: " + rset2.getInt("npc_templateid") + ".");
				}
				rset2.close();
				statement2.close();
				_magicalMonsters.put(keyNpcId, _magicalSpawns);
			}
			rset1.close();
			statement1.close();
			_log.info("FourSepulchersManager: loaded " + loaded + " Magical type monsters spawns.");
		}
		catch (Exception e)
		{
			// problem with initializing spawn, go to next one
			_log.warning("FourSepulchersManager.LoadMagicalMonsters: Spawn could not be initialized: " + e);
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

	private void loadDukeMonsters()
	{
		_dukeFinalMobs.clear();
		_archonSpawned.clear();
		int loaded = 0;
		Connection con = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			PreparedStatement statement1 = con.prepareStatement("SELECT Distinct key_npc_id FROM four_sepulchers_spawnlist Where spawntype = ? ORDER BY key_npc_id");
			statement1.setInt(1, 5);
			ResultSet rset1 = statement1.executeQuery();
			while (rset1.next())
			{
				int keyNpcId = rset1.getInt("key_npc_id");
				PreparedStatement statement2 = con.prepareStatement("SELECT id, count, npc_templateid, locx, locy, locz, heading, respawn_delay, key_npc_id FROM four_sepulchers_spawnlist Where key_npc_id = ? and spawntype = ? ORDER BY id");
				statement2.setInt(1, keyNpcId);
				statement2.setInt(2, 5);
				ResultSet rset2 = statement2.executeQuery();
				L2Spawn spawnDat;
				L2NpcTemplate template1;
				_dukeFinalSpawns = new ArrayList<>();
				while (rset2.next())
				{
					template1 = NpcTable.getInstance().getTemplate(rset2.getInt("npc_templateid"));
					if (template1 != null)
					{
						spawnDat = new L2Spawn(template1);
						spawnDat.setAmount(rset2.getInt("count"));
						spawnDat.setLocx(rset2.getInt("locx"));
						spawnDat.setLocy(rset2.getInt("locy"));
						spawnDat.setLocz(rset2.getInt("locz"));
						spawnDat.setHeading(rset2.getInt("heading"));
						spawnDat.setRespawnDelay(rset2.getInt("respawn_delay"));
						SpawnTable.getInstance().addNewSpawn(spawnDat, false);
						_dukeFinalSpawns.add(spawnDat);
						loaded++;
					} else
						_log.warning("FourSepulchersManager.LoadDukeMonsters: Data missing in NPC table for ID: " + rset2.getInt("npc_templateid") + ".");
				}
				rset2.close();
				statement2.close();
				_dukeFinalMobs.put(keyNpcId, _dukeFinalSpawns);
				_archonSpawned.put(keyNpcId, false);
			}
			rset1.close();
			statement1.close();
			_log.info("FourSepulchersManager: loaded " + loaded + " Church of duke monsters spawns.");
		}
		catch (Exception e)
		{
			// problem with initializing spawn, go to next one
			_log.warning("FourSepulchersManager.LoadDukeMonsters: Spawn could not be initialized: " + e);
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

	private void loadEmperorsGraveMonsters()
	{
		_emperorsGraveNpcs.clear();
		int loaded = 0;
		Connection con = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			PreparedStatement statement1 = con.prepareStatement("SELECT Distinct key_npc_id FROM four_sepulchers_spawnlist Where spawntype = ? ORDER BY key_npc_id");
			statement1.setInt(1, 6);
			ResultSet rset1 = statement1.executeQuery();
			while (rset1.next())
			{
				int keyNpcId = rset1.getInt("key_npc_id");
				PreparedStatement statement2 = con.prepareStatement("SELECT id, count, npc_templateid, locx, locy, locz, heading, respawn_delay, key_npc_id FROM four_sepulchers_spawnlist Where key_npc_id = ? and spawntype = ? ORDER BY id");
				statement2.setInt(1, keyNpcId);
				statement2.setInt(2, 6);
				ResultSet rset2 = statement2.executeQuery();
				L2Spawn spawnDat;
				L2NpcTemplate template1;
				_emperorsGraveSpawns = new ArrayList<>();
				while (rset2.next())
				{
					template1 = NpcTable.getInstance().getTemplate(rset2.getInt("npc_templateid"));
					if (template1 != null)
					{
						spawnDat = new L2Spawn(template1);
						spawnDat.setAmount(rset2.getInt("count"));
						spawnDat.setLocx(rset2.getInt("locx"));
						spawnDat.setLocy(rset2.getInt("locy"));
						spawnDat.setLocz(rset2.getInt("locz"));
						spawnDat.setHeading(rset2.getInt("heading"));
						spawnDat.setRespawnDelay(rset2.getInt("respawn_delay"));
						SpawnTable.getInstance().addNewSpawn(spawnDat, false);
						_emperorsGraveSpawns.add(spawnDat);
						loaded++;
					} else
						_log.warning("FourSepulchersManager.LoadEmperorsGraveMonsters: Data missing in NPC table for ID: " + rset2.getInt("npc_templateid") + ".");
				}
				rset2.close();
				statement2.close();
				_emperorsGraveNpcs.put(keyNpcId, _emperorsGraveSpawns);
			}
			rset1.close();
			statement1.close();
			_log.info("FourSepulchersManager: loaded " + loaded + " Emperor's grave NPC spawns.");
		}
		catch (Exception e)
		{
			// problem with initializing spawn, go to next one
			_log.warning("FourSepulchersManager.LoadEmperorsGraveMonsters: Spawn could not be initialized: " + e);
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

	protected void initLocationShadowSpawns()
	{
		int locNo = Rnd.get(4);
		final int[] gateKeeper = { 31929, 31934, 31939, 31944 };
		L2Spawn spawnDat;
		L2NpcTemplate template;
		_shadowSpawns.clear();
		for (int i = 0; i <= 3; i++)
		{
			template = NpcTable.getInstance().getTemplate(_shadowSpawnLoc[locNo][i][0]);
			if (template != null)
				try
				{
					spawnDat = new L2Spawn(template);
					spawnDat.setAmount(1);
					spawnDat.setLocx(_shadowSpawnLoc[locNo][i][1]);
					spawnDat.setLocy(_shadowSpawnLoc[locNo][i][2]);
					spawnDat.setLocz(_shadowSpawnLoc[locNo][i][3]);
					spawnDat.setHeading(_shadowSpawnLoc[locNo][i][4]);
					SpawnTable.getInstance().addNewSpawn(spawnDat, false);
					int keyNpcId = gateKeeper[i];
					_shadowSpawns.put(keyNpcId, spawnDat);
				}
				catch (Exception e)
				{
					_log.log(Level.SEVERE, "Error on InitLocationShadowSpawns", e);
				}
			else
				_log.warning("FourSepulchersManager.InitLocationShadowSpawns: Data missing in NPC table for ID: " + _shadowSpawnLoc[locNo][i][0] + ".");
		}
	}

	protected void initExecutionerSpawns()
	{
		L2Spawn spawnDat;
		L2NpcTemplate template;
		for (int keyNpcId : _victim.keySet())
			try
			{
				template = NpcTable.getInstance().getTemplate(_victim.get(keyNpcId));
				if (template != null)
				{
					spawnDat = new L2Spawn(template);
					spawnDat.setAmount(1);
					spawnDat.setLocx(0);
					spawnDat.setLocy(0);
					spawnDat.setLocz(0);
					spawnDat.setHeading(0);
					spawnDat.setRespawnDelay(3600);
					SpawnTable.getInstance().addNewSpawn(spawnDat, false);
					_executionerSpawns.put(keyNpcId, spawnDat);
				} else
					_log.warning("FourSepulchersManager.InitExecutionerSpawns: Data missing in NPC table for ID: " + _victim.get(keyNpcId) + ".");
			}
			catch (Exception e)
			{
				_log.warning("FourSepulchersManager.InitExecutionerSpawns: Spawn could not be initialized: " + e);
			}
	}

	public boolean isEntryTime()
	{
		return _inEntryTime;
	}

	public boolean isAttackTime()
	{
		return _inAttackTime;
	}

	public synchronized void tryEntry(L2NpcInstance npc, L2PcInstance player)
	{
		int npcId = npc.getNpcId();
		switch (npcId)
		{
			// ID ok
			case 31921:
			case 31922:
			case 31923:
			case 31924:
				break;
			// ID not ok
			default:
				if (!player.isGM())
				{
					_log.warning("Player " + player.getName() + "(" + player.getObjectId() + ") tried to cheat in four sepulchers.");
					Util.handleIllegalPlayerAction(player, "Warning!! Character " + player.getName() + " tried to enter four sepulchers with invalid npc id.", Config.DEFAULT_PUNISH);
					player.closeNetConnection(); // kick
					return;
				}
				return;
		}
		if (_hallInUse.get(npcId).booleanValue())
		{
			showHtmlFile(player, npcId + "-FULL.htm", npc, null);
			return;
		}
		
		
		SepulchersSettings settings = getSettings(SepulchersSettings.class);
		if (settings.getMinimunPartyMembers() > 1)
		{
			if (!player.isInParty() || player.getParty().getMemberCount() < settings.getMinimunPartyMembers())
			{
				showHtmlFile(player, npcId + "-SP.htm", npc, null);
				return;
			}
			if (!player.getParty().isLeader(player))
			{
				showHtmlFile(player, npcId + "-NL.htm", npc, null);
				return;
			}
			for (L2PcInstance mem : player.getParty().getPartyMembers())
			{
				QuestState qs = mem.getQuestState(QUEST_ID);
				if (qs == null || !qs.isStarted() && !qs.isCompleted())
				{
					showHtmlFile(player, npcId + "-NS.htm", npc, mem);
					return;
				}
				if (mem.getInventory().getItemByItemId(ENTRANCE_PASS) == null)
				{
					showHtmlFile(player, npcId + "-SE.htm", npc, mem);
					return;
				}
				if (player.getWeightPenalty() >= 3)
				{
					mem.sendPacket(new SystemMessage(SystemMessageId.INVENTORY_LESS_THAN_80_PERCENT));
					return;
				}
			}
		}
		else if (settings.getMinimunPartyMembers() <= 1 && player.isInParty())
		{
			if (!player.getParty().isLeader(player))
			{
				showHtmlFile(player, npcId + "-NL.htm", npc, null);
				return;
			}
			for (L2PcInstance mem : player.getParty().getPartyMembers())
			{
				QuestState qs = mem.getQuestState(QUEST_ID);
				if (qs == null || !qs.isStarted() && !qs.isCompleted())
				{
					showHtmlFile(player, npcId + "-NS.htm", npc, mem);
					return;
				}
				if (mem.getInventory().getItemByItemId(ENTRANCE_PASS) == null)
				{
					showHtmlFile(player, npcId + "-SE.htm", npc, mem);
					return;
				}
				if (player.getWeightPenalty() >= 3)
				{
					mem.sendPacket(new SystemMessage(SystemMessageId.INVENTORY_LESS_THAN_80_PERCENT));
					return;
				}
			}
		}
		else
		{
			QuestState qs = player.getQuestState(QUEST_ID);
			if (qs == null || !qs.isStarted() && !qs.isCompleted())
			{
				showHtmlFile(player, npcId + "-NS.htm", npc, player);
				return;
			}
			if (player.getInventory().getItemByItemId(ENTRANCE_PASS) == null)
			{
				showHtmlFile(player, npcId + "-SE.htm", npc, player);
				return;
			}
			if (player.getWeightPenalty() >= 3)
			{
				player.sendPacket(new SystemMessage(SystemMessageId.INVENTORY_LESS_THAN_80_PERCENT));
				return;
			}
		}
		if (!isEntryTime())
		{
			showHtmlFile(player, npcId + "-NE.htm", npc, null);
			return;
		}
		showHtmlFile(player, npcId + "-OK.htm", npc, null);
		entry(npcId, player);
	}

	private void entry(int npcId, L2PcInstance player)
	{
		int[] Location = _startHallSpawns.get(npcId);
		int driftx;
		int drifty;
		SepulchersSettings sepulcheresSettings = getSettings(SepulchersSettings.class);
		if (sepulcheresSettings.getMinimunPartyMembers() > 1)
		{
			List<L2PcInstance> members = new ArrayList<>();
			for (L2PcInstance mem : player.getParty().getPartyMembers())
				if (!mem.isDead() && Util.checkIfInRange(700, player, mem, true))
					members.add(mem);
			for (L2PcInstance mem : members)
			{
				GrandBossManager.getInstance().getZone(Location[0], Location[1], Location[2]).allowPlayerEntry(mem, 30);
				driftx = Rnd.get(-80, 80);
				drifty = Rnd.get(-80, 80);
				mem.teleToLocation(Location[0] + driftx, Location[1] + drifty, Location[2]);
				mem.destroyItemByItemId("Quest", ENTRANCE_PASS, 1, mem, true);
				if (mem.getInventory().getItemByItemId(ANTIQUE_BROOCH) == null)
					mem.addItem("Quest", USED_PASS, 1, mem, true);
				L2ItemInstance hallsKey = mem.getInventory().getItemByItemId(CHAPEL_KEY);
				if (hallsKey != null)
					mem.destroyItemByItemId("Quest", CHAPEL_KEY, hallsKey.getCount(), mem, true);
			}
			_challengers.remove(npcId);
			_challengers.put(npcId, player);
			_hallInUse.remove(npcId);
			_hallInUse.put(npcId, true);
		}
		if (sepulcheresSettings.getMinimunPartyMembers() <= 1 && player.isInParty())
		{
			List<L2PcInstance> members = new ArrayList<>();
			for (L2PcInstance mem : player.getParty().getPartyMembers())
				if (!mem.isDead() && Util.checkIfInRange(700, player, mem, true))
					members.add(mem);
			for (L2PcInstance mem : members)
			{
				GrandBossManager.getInstance().getZone(Location[0], Location[1], Location[2]).allowPlayerEntry(mem, 30);
				driftx = Rnd.get(-80, 80);
				drifty = Rnd.get(-80, 80);
				mem.teleToLocation(Location[0] + driftx, Location[1] + drifty, Location[2]);
				mem.destroyItemByItemId("Quest", ENTRANCE_PASS, 1, mem, true);
				if (mem.getInventory().getItemByItemId(ANTIQUE_BROOCH) == null)
					mem.addItem("Quest", USED_PASS, 1, mem, true);
				L2ItemInstance hallsKey = mem.getInventory().getItemByItemId(CHAPEL_KEY);
				if (hallsKey != null)
					mem.destroyItemByItemId("Quest", CHAPEL_KEY, hallsKey.getCount(), mem, true);
			}
			_challengers.remove(npcId);
			_challengers.put(npcId, player);
			_hallInUse.remove(npcId);
			_hallInUse.put(npcId, true);
		}
		else
		{
			GrandBossManager.getInstance().getZone(Location[0], Location[1], Location[2]).allowPlayerEntry(player, 30);
			driftx = Rnd.get(-80, 80);
			drifty = Rnd.get(-80, 80);
			player.teleToLocation(Location[0] + driftx, Location[1] + drifty, Location[2]);
			player.destroyItemByItemId("Quest", ENTRANCE_PASS, 1, player, true);
			if (player.getInventory().getItemByItemId(ANTIQUE_BROOCH) == null)
				player.addItem("Quest", USED_PASS, 1, player, true);
			L2ItemInstance hallsKey = player.getInventory().getItemByItemId(CHAPEL_KEY);
			if (hallsKey != null)
				player.destroyItemByItemId("Quest", CHAPEL_KEY, hallsKey.getCount(), player, true);
			_challengers.remove(npcId);
			_challengers.put(npcId, player);
			_hallInUse.remove(npcId);
			_hallInUse.put(npcId, true);
		}
	}

	public void spawnMysteriousBox(int npcId)
	{
		if (!isAttackTime())
			return;
		L2Spawn spawnDat = _mysteriousBoxSpawns.get(npcId);
		if (spawnDat != null)
		{
			_allMobs.add(spawnDat.doSpawn());
			spawnDat.stopRespawn();
		}
	}

	public void spawnMonster(int npcId)
	{
		if (!isAttackTime())
			return;
		List<L2Spawn> monsterList;
		List<L2SepulcherMonsterInstance> mobs = new ArrayList<>();
		L2Spawn keyBoxMobSpawn;
		if (Rnd.get(2) == 0)
			monsterList = _physicalMonsters.get(npcId);
		else
			monsterList = _magicalMonsters.get(npcId);
		if (monsterList != null)
		{
			boolean spawnKeyBoxMob = false;
			boolean spawnedKeyBoxMob = false;
			for (L2Spawn spawnDat : monsterList)
			{
				if (spawnedKeyBoxMob)
					spawnKeyBoxMob = false;
				else
					switch (npcId)
					{
						case 31469:
						case 31474:
						case 31479:
						case 31484:
							if (Rnd.get(48) == 0)
								spawnKeyBoxMob = true;
								// _log.info("FourSepulchersManager.SpawnMonster:
								// Set to spawn Church of Viscount Key Mob.");
							break;
						default:
							spawnKeyBoxMob = false;
					}
				L2SepulcherMonsterInstance mob = null;
				if (spawnKeyBoxMob)
				{
					try
					{
						L2NpcTemplate template = NpcTable.getInstance().getTemplate(18149);
						if (template != null)
						{
							keyBoxMobSpawn = new L2Spawn(template);
							keyBoxMobSpawn.setAmount(1);
							keyBoxMobSpawn.setLocx(spawnDat.getLocx());
							keyBoxMobSpawn.setLocy(spawnDat.getLocy());
							keyBoxMobSpawn.setLocz(spawnDat.getLocz());
							keyBoxMobSpawn.setHeading(spawnDat.getHeading());
							keyBoxMobSpawn.setRespawnDelay(3600);
							SpawnTable.getInstance().addNewSpawn(keyBoxMobSpawn, false);
							mob = (L2SepulcherMonsterInstance) keyBoxMobSpawn.doSpawn();
							keyBoxMobSpawn.stopRespawn();
						} else
							_log.warning("FourSepulchersManager.SpawnMonster: Data missing in NPC table for ID: 18149");
					}
					catch (Exception e)
					{
						_log.warning("FourSepulchersManager.SpawnMonster: Spawn could not be initialized: " + e);
					}
					spawnedKeyBoxMob = true;
				}
				else
				{
					mob = (L2SepulcherMonsterInstance) spawnDat.doSpawn();
					spawnDat.stopRespawn();
				}
				if (mob != null)
				{
					mob.mysteriousBoxId = npcId;
					switch (npcId)
					{
						case 31469:
						case 31474:
						case 31479:
						case 31484:
						case 31472:
						case 31477:
						case 31482:
						case 31487:
							mobs.add(mob);
					}
					_allMobs.add(mob);
				}
			}
			switch (npcId)
			{
				case 31469:
				case 31474:
				case 31479:
				case 31484:
					_viscountMobs.put(npcId, mobs);
					break;
				case 31472:
				case 31477:
				case 31482:
				case 31487:
					_dukeMobs.put(npcId, mobs);
					break;
			}
		}
	}

	public synchronized boolean isViscountMobsAnnihilated(int npcId)
	{
		List<L2SepulcherMonsterInstance> mobs = _viscountMobs.get(npcId);
		if (mobs == null)
			return true;
		for (L2SepulcherMonsterInstance mob : mobs)
			if (!mob.isDead())
				return false;
		return true;
	}

	public synchronized boolean isDukeMobsAnnihilated(int npcId)
	{
		List<L2SepulcherMonsterInstance> mobs = _dukeMobs.get(npcId);
		if (mobs == null)
			return true;
		for (L2SepulcherMonsterInstance mob : mobs)
			if (!mob.isDead())
				return false;
		return true;
	}

	public void spawnKeyBox(L2NpcInstance activeChar)
	{
		if (!isAttackTime())
			return;
		L2Spawn spawnDat = _keyBoxSpawns.get(activeChar.getNpcId());
		if (spawnDat != null)
		{
			spawnDat.setAmount(1);
			spawnDat.setLocx(activeChar.getX());
			spawnDat.setLocy(activeChar.getY());
			spawnDat.setLocz(activeChar.getZ());
			spawnDat.setHeading(activeChar.getHeading());
			spawnDat.setRespawnDelay(3600);
			_allMobs.add(spawnDat.doSpawn());
			spawnDat.stopRespawn();
		}
	}

	public void spawnExecutionerOfHalisha(L2NpcInstance activeChar)
	{
		if (!isAttackTime())
			return;
		L2Spawn spawnDat = _executionerSpawns.get(activeChar.getNpcId());
		if (spawnDat != null)
		{
			spawnDat.setAmount(1);
			spawnDat.setLocx(activeChar.getX());
			spawnDat.setLocy(activeChar.getY());
			spawnDat.setLocz(activeChar.getZ());
			spawnDat.setHeading(activeChar.getHeading());
			spawnDat.setRespawnDelay(3600);
			_allMobs.add(spawnDat.doSpawn());
			spawnDat.stopRespawn();
		}
	}

	public void spawnArchonOfHalisha(int npcId)
	{
		if (!isAttackTime())
			return;
		if (_archonSpawned.get(npcId))
			return;
		List<L2Spawn> monsterList = _dukeFinalMobs.get(npcId);
		if (monsterList != null)
		{
			for (L2Spawn spawnDat : monsterList)
			{
				L2SepulcherMonsterInstance mob = (L2SepulcherMonsterInstance) spawnDat.doSpawn();
				spawnDat.stopRespawn();
				if (mob != null)
				{
					mob.mysteriousBoxId = npcId;
					_allMobs.add(mob);
				}
			}
			_archonSpawned.put(npcId, true);
		}
	}

	public void spawnEmperorsGraveNpc(int npcId)
	{
		if (!isAttackTime())
			return;
		List<L2Spawn> monsterList = _emperorsGraveNpcs.get(npcId);
		if (monsterList != null)
			for (L2Spawn spawnDat : monsterList)
			{
				_allMobs.add(spawnDat.doSpawn());
				spawnDat.stopRespawn();
			}
	}

	protected void locationShadowSpawns()
	{
		int locNo = Rnd.get(4);
		// _log.info("FourSepulchersManager.LocationShadowSpawns: Location index
		// is " + locNo + ".");
		final int[] gateKeeper = { 31929, 31934, 31939, 31944 };
		L2Spawn spawnDat;
		for (int i = 0; i <= 3; i++)
		{
			int keyNpcId = gateKeeper[i];
			spawnDat = _shadowSpawns.get(keyNpcId);
			spawnDat.setLocx(_shadowSpawnLoc[locNo][i][1]);
			spawnDat.setLocy(_shadowSpawnLoc[locNo][i][2]);
			spawnDat.setLocz(_shadowSpawnLoc[locNo][i][3]);
			spawnDat.setHeading(_shadowSpawnLoc[locNo][i][4]);
			_shadowSpawns.put(keyNpcId, spawnDat);
		}
	}

	public void spawnShadow(int npcId)
	{
		if (!isAttackTime())
			return;
		L2Spawn spawnDat = _shadowSpawns.get(npcId);
		if (spawnDat != null)
		{
			L2SepulcherMonsterInstance mob = (L2SepulcherMonsterInstance) spawnDat.doSpawn();
			spawnDat.stopRespawn();
			if (mob != null)
			{
				mob.mysteriousBoxId = npcId;
				_allMobs.add(mob);
			}
		}
	}

	public void deleteAllMobs()
	{
		// _log.info("FourSepulchersManager.DeleteAllMobs: Try to delete " +
		// _allMobs.size() + " monsters.");
		for (L2NpcInstance mob : _allMobs)
			try
			{
				mob.getSpawn().stopRespawn();
				mob.deleteMe();
			}
			catch (Exception e)
			{
				_log.log(Level.SEVERE, "FourSepulchersManager: Failed deleting mob.", e);
			}
		_allMobs.clear();
		// _log.info("FourSepulchersManager.DeleteAllMobs: Deleted " + delCnt +
		// " monsters.");
	}

	protected void closeAllDoors()
	{
		for (int doorId : _hallGateKeepers.values())
			try
			{
				L2DoorInstance door = DoorTable.getInstance().getDoor(doorId);
				if (door != null)
					door.closeMe();
				else
				{
				}
			}
			catch (Exception e)
			{
				_log.log(Level.SEVERE, "FourSepulchersManager: Failed closing door", e);
			}
	}

	protected byte minuteSelect(byte min)
	{
		if ((double) min % 5 != 0)
			// mad table for selecting proper minutes...
			// may be there is a better way to do this
			switch (min)
			{
				case 6:
				case 7:
					min = 5;
					break;
				case 8:
				case 9:
				case 11:
				case 12:
					min = 10;
					break;
				case 13:
				case 14:
				case 16:
				case 17:
					min = 15;
					break;
				case 18:
				case 19:
				case 21:
				case 22:
					min = 20;
					break;
				case 23:
				case 24:
				case 26:
				case 27:
					min = 25;
					break;
				case 28:
				case 29:
				case 31:
				case 32:
					min = 30;
					break;
				case 33:
				case 34:
				case 36:
				case 37:
					min = 35;
					break;
				case 38:
				case 39:
				case 41:
				case 42:
					min = 40;
					break;
				case 43:
				case 44:
				case 46:
				case 47:
					min = 45;
					break;
				case 48:
				case 49:
				case 51:
				case 52:
					min = 50;
					break;
				case 53:
				case 54:
				case 56:
				case 57:
					min = 55;
					break;
			}
		return min;
	}

	public void managerSay(byte min)
	{
		// for attack phase, sending message every 5 minutes
		if (_inAttackTime)
		{
			if (min < 5)
				return; // do not shout when < 5 minutes
			min = minuteSelect(min);
			String msg = min + " minute(s) have passed."; // now this is a
			// proper message^^
			if (min == 90)
				msg = "Game over. The teleport will appear momentarily";
			for (L2Spawn temp : _managers)
			{
				if (temp == null)
				{
					_log.warning("FourSepulchersManager: managerSay(): manager is null");
					continue;
				}
				if (!(temp.getLastSpawn() instanceof L2SepulcherNpcInstance))
				{
					_log.warning("FourSepulchersManager: managerSay(): manager is not Sepulcher instance");
					continue;
				}
				// hall not used right now, so its manager will not tell you
				// anything :)
				// if you don't need this - delete next two lines.
				if (!_hallInUse.get(temp.getNpcid()).booleanValue())
					continue;
				((L2SepulcherNpcInstance) temp.getLastSpawn()).sayInShout(msg);
			}
		}
		else if (_inEntryTime)
		{
			String msg1 = "You may now enter the Sepulcher";
			String msg2 = "If you place your hand on the stone statue in front of each sepulcher," + " you will be able to enter";
			for (L2Spawn temp : _managers)
			{
				if (temp == null)
				{
					_log.warning("FourSepulchersManager: Something goes wrong in managerSay()...");
					continue;
				}
				if (!(temp.getLastSpawn() instanceof L2SepulcherNpcInstance))
				{
					_log.warning("FourSepulchersManager: Something goes wrong in managerSay()...");
					continue;
				}
				((L2SepulcherNpcInstance) temp.getLastSpawn()).sayInShout(msg1);
				((L2SepulcherNpcInstance) temp.getLastSpawn()).sayInShout(msg2);
			}
		}
	}

	protected class ManagerSay implements Runnable
	{
		public void run()
		{
			if (_inAttackTime)
			{
				Calendar tmp = Calendar.getInstance();
				tmp.setTimeInMillis(Calendar.getInstance().getTimeInMillis() - _warmUpTimeEnd);
				int attackTime = getSettings(SepulchersSettings.class).getAttackTime();
				if (tmp.get(Calendar.MINUTE) + 5 < attackTime)
				{
					managerSay((byte) tmp.get(Calendar.MINUTE)); // byte
					// because
					// minute
					// cannot be
					// more than
					// 59
					ThreadPoolManager.getInstance().scheduleGeneral(new ManagerSay(), 5 * 60000);
				}
				// attack time ending chat
				else if (tmp.get(Calendar.MINUTE) + 5 >= attackTime)
					managerSay((byte) 90); // sending a unique id :D
			}
			else if (_inEntryTime)
				managerSay((byte) 0);
		}
	}

	protected class ChangeEntryTime implements Runnable
	{
		public void run()
		{
			// _log.info("FourSepulchersManager:In Entry Time");
			_inEntryTime = true;
			_inWarmUpTime = false;
			_inAttackTime = false;
			_inCoolDownTime = false;
			long interval = 0;
			// if this is first launch - search time when entry time will be
			// ended:
			// counting difference between time when entry time ends and current
			// time
			// and then launching change time task
			if (_firstTimeRun)
				interval = _entryTimeEnd - Calendar.getInstance().getTimeInMillis();
			else
				interval = minutesToMiliseconds(getSettings(SepulchersSettings.class).getEntryTime()); // else use stupid
			// method
			// launching saying process...
			ThreadPoolManager.getInstance().scheduleGeneral(new ManagerSay(), 0);
			_changeWarmUpTimeTask = ThreadPoolManager.getInstance().scheduleEffect(new ChangeWarmUpTime(), interval);
			if (_changeEntryTimeTask != null)
			{
				_changeEntryTimeTask.cancel(true);
				_changeEntryTimeTask = null;
			}
		}
	}

	protected class ChangeWarmUpTime implements Runnable
	{
		public void run()
		{
			// _log.info("FourSepulchersManager:In Warm-Up Time");
			_inEntryTime = true;
			_inWarmUpTime = false;
			_inAttackTime = false;
			_inCoolDownTime = false;
			long interval = 0;
			// searching time when warmup time will be ended:
			// counting difference between time when warmup time ends and
			// current time
			// and then launching change time task
			if (_firstTimeRun)
				interval = _warmUpTimeEnd - Calendar.getInstance().getTimeInMillis();
			else
				interval = minutesToMiliseconds(getSettings(SepulchersSettings.class).getWarmUpTime());
			_changeAttackTimeTask = ThreadPoolManager.getInstance().scheduleGeneral(new ChangeAttackTime(), interval);
			if (_changeWarmUpTimeTask != null)
			{
				_changeWarmUpTimeTask.cancel(true);
				_changeWarmUpTimeTask = null;
			}
		}
	}

	protected class ChangeAttackTime implements Runnable
	{
		public void run()
		{
			// _log.info("FourSepulchersManager:In Attack Time");
			_inEntryTime = false;
			_inWarmUpTime = false;
			_inAttackTime = true;
			_inCoolDownTime = false;
			locationShadowSpawns();
			spawnMysteriousBox(31921);
			spawnMysteriousBox(31922);
			spawnMysteriousBox(31923);
			spawnMysteriousBox(31924);
			if (!_firstTimeRun)
				_warmUpTimeEnd = Calendar.getInstance().getTimeInMillis();
			long interval = 0;
			// say task
			if (_firstTimeRun)
			{
				for (double min = Calendar.getInstance().get(Calendar.MINUTE); min < _newCycleMin; min++)
					// looking for next shout time....
					if (min % 5 == 0)// check if min can be divided by 5
					{
						_log.info(Calendar.getInstance().getTime() + " Atk announce scheduled to " + min + " minute of this hour.");
						Calendar inter = Calendar.getInstance();
						inter.set(Calendar.MINUTE, (int) min);
						ThreadPoolManager.getInstance().scheduleGeneral(new ManagerSay(), inter.getTimeInMillis() - Calendar.getInstance().getTimeInMillis());
						break;
					}
			} else
				ThreadPoolManager.getInstance().scheduleGeneral(new ManagerSay(), 5 * 60400);
			// searching time when attack time will be ended:
			// counting difference between time when attack time ends and
			// current time
			// and then launching change time task
			if (_firstTimeRun)
				interval = _attackTimeEnd - Calendar.getInstance().getTimeInMillis();
			else
				interval = minutesToMiliseconds(getSettings(SepulchersSettings.class).getAttackTime());
			_changeCoolDownTimeTask = ThreadPoolManager.getInstance().scheduleGeneral(new ChangeCoolDownTime(), interval);
			if (_changeAttackTimeTask != null)
			{
				_changeAttackTimeTask.cancel(true);
				_changeAttackTimeTask = null;
			}
		}
	}

	protected class ChangeCoolDownTime implements Runnable
	{
		public void run()
		{
			// _log.info("FourSepulchersManager:In Cool-Down Time");
			_inEntryTime = false;
			_inWarmUpTime = false;
			_inAttackTime = false;
			_inCoolDownTime = true;
			clean();
			Calendar time = Calendar.getInstance();
			// one hour = 55th min to 55 min of next hour, so we check for this,
			// also check for first launch
			if (Calendar.getInstance().get(Calendar.MINUTE) > _newCycleMin && !_firstTimeRun)
				time.set(Calendar.HOUR, Calendar.getInstance().get(Calendar.HOUR) + 1);
			time.set(Calendar.MINUTE, _newCycleMin);
			_log.info("FourSepulchersManager: Entry time: " + time.getTime());
			if (_firstTimeRun)
				_firstTimeRun = false; // cooldown phase ends event hour, so it
			// will be not first run
			long interval = time.getTimeInMillis() - Calendar.getInstance().getTimeInMillis();
			_changeEntryTimeTask = ThreadPoolManager.getInstance().scheduleGeneral(new ChangeEntryTime(), interval);
			if (_changeCoolDownTimeTask != null)
			{
				_changeCoolDownTimeTask.cancel(true);
				_changeCoolDownTimeTask = null;
			}
		}
	}

	public Map<Integer, Integer> getHallGateKeepers()
	{
		return _hallGateKeepers;
	}

	public void showHtmlFile(L2PcInstance player, String file, L2NpcInstance npc, L2PcInstance member)
	{
		NpcHtmlMessage html = new NpcHtmlMessage(npc.getObjectId());
		html.setFile("data/html/SepulcherNpc/" + file);
		if (member != null)
			html.replace("%member%", member.getName());
		player.sendPacket(html);
	}

	@SuppressWarnings("synthetic-access")
	private static class SingletonHolder
	{
		protected static final FourSepulchersManager _instance = new FourSepulchersManager();
	}
}
