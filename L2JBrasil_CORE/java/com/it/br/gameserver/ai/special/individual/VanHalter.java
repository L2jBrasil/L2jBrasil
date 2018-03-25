/*
 * This program is free software; you can redistribute it and/or modify
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
package com.it.br.gameserver.ai.special.individual;

import com.it.br.Config;
import com.it.br.gameserver.ThreadPoolManager;
import com.it.br.gameserver.ai.CtrlIntention;
import com.it.br.gameserver.database.dao.VanHalterDao;
import com.it.br.gameserver.datatables.sql.SkillTable;
import com.it.br.gameserver.datatables.sql.SpawnTable;
import com.it.br.gameserver.datatables.xml.DoorTable;
import com.it.br.gameserver.datatables.xml.NpcTable;
import com.it.br.gameserver.instancemanager.GrandBossManager;
import com.it.br.gameserver.model.L2CharPosition;
import com.it.br.gameserver.model.L2Effect;
import com.it.br.gameserver.model.L2Skill;
import com.it.br.gameserver.model.L2Spawn;
import com.it.br.gameserver.model.actor.instance.L2DoorInstance;
import com.it.br.gameserver.model.actor.instance.L2NpcInstance;
import com.it.br.gameserver.model.actor.instance.L2PcInstance;
import com.it.br.gameserver.model.actor.instance.L2RaidBossInstance;
import com.it.br.gameserver.model.quest.Quest;
import com.it.br.gameserver.network.serverpackets.MagicSkillUser;
import com.it.br.gameserver.network.serverpackets.SpecialCamera;
import com.it.br.gameserver.templates.L2NpcTemplate;
import com.it.br.gameserver.templates.StatsSet;
import com.it.br.util.Rnd;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;

/**
 * This class ... control for sequence of fight against "High Priestess van Halter".
 * @refactor by Tayran
 * @version 3.0.4
 * @author L2J_JP SANDMAN
 **/

public class VanHalter extends Quest implements Runnable
{
	private static final Logger _log = LoggerFactory.getLogger(VanHalter.class);

	// List of intruders.
	protected Map<Integer, List<L2PcInstance>> _bleedingPlayers = new HashMap<>();

	// Spawn data of monsters.
	protected Map<Integer, L2Spawn> _monsterSpawn = new HashMap<>();
	protected List<L2Spawn> _royalGuardSpawn = new ArrayList<>();
	protected List<L2Spawn> _royalGuardCaptainSpawn = new ArrayList<>();
	protected List<L2Spawn> _royalGuardHelperSpawn = new ArrayList<>();
	protected List<L2Spawn> _triolRevelationSpawn = new ArrayList<>();
	protected List<L2Spawn> _triolRevelationAlive = new ArrayList<>();
	protected List<L2Spawn> _guardOfAltarSpawn = new ArrayList<>();
	protected Map<Integer, L2Spawn> _cameraMarkerSpawn = new HashMap<>();
	protected L2Spawn _ritualOfferingSpawn = null;
	protected L2Spawn _ritualSacrificeSpawn = null;
	protected L2Spawn _vanHalterSpawn = null;

	// Instance of monsters.
	protected List<L2NpcInstance> _monsters = new ArrayList<>();
	protected List<L2NpcInstance> _royalGuard = new ArrayList<>();
	protected List<L2NpcInstance> _royalGuardCaptain = new ArrayList<>();
	protected List<L2NpcInstance> _royalGuardHepler = new ArrayList<>();
	protected List<L2NpcInstance> _triolRevelation = new ArrayList<>();
	protected List<L2NpcInstance> _guardOfAltar = new ArrayList<>();
	protected Map<Integer, L2NpcInstance> _cameraMarker = new HashMap<>();
	protected List<L2DoorInstance> _doorOfAltar = new ArrayList<>();
	protected List<L2DoorInstance> _doorOfSacrifice = new ArrayList<>();
	protected L2NpcInstance _ritualOffering = null;
	protected L2NpcInstance _ritualSacrifice = null;
	protected L2RaidBossInstance _vanHalter = null;

	// Task
	protected ScheduledFuture<?> _movieTask = null;
	protected ScheduledFuture<?> _closeDoorOfAltarTask = null;
	protected ScheduledFuture<?> _openDoorOfAltarTask = null;
	protected ScheduledFuture<?> _lockUpDoorOfAltarTask = null;
	protected ScheduledFuture<?> _callRoyalGuardHelperTask = null;
	protected ScheduledFuture<?> _timeUpTask = null;
	protected ScheduledFuture<?> _intervalTask = null;
	protected ScheduledFuture<?> _halterEscapeTask = null;
	protected ScheduledFuture<?> _setBleedTask = null;

	// State of High Priestess van Halter
	private boolean _isLocked = false;
    private boolean _isHalterSpawned = false;
    private boolean _isSacrificeSpawned = false;
    private boolean _isCaptainSpawned = false;
    private boolean _isHelperCalled = false;

	//VanHalter Status Tracking :
	private static final byte INTERVAL = 0;
	private static final byte NOTSPAWN = 1;
	private static final byte ALIVE = 2;

	// Initialize
	public VanHalter(int questId, String name, String descr)
	{
		super(questId, name, descr);

		int[] mobs =
		{
				29062, 22188, 32058, 32059, 32060, 32061, 32062, 32063, 32064, 32065, 32066
		};

		addEventId(29062, Quest.QuestEventType.ON_ATTACK);
		for(int mob : mobs)
		{
			addEventId(mob, Quest.QuestEventType.ON_KILL);
		}

		//GrandBossManager.getInstance().addBoss(29062);
		// Clear flag.
		_isLocked = false;
		_isCaptainSpawned = false;
		_isHelperCalled = false;
		_isHalterSpawned = false;

		// Setting door state.
		_doorOfAltar.add(DoorTable.getInstance().getDoor(19160014));
		_doorOfAltar.add(DoorTable.getInstance().getDoor(19160015));
		openDoorOfAltar(true);
		_doorOfSacrifice.add(DoorTable.getInstance().getDoor(19160016));
		_doorOfSacrifice.add(DoorTable.getInstance().getDoor(19160017));
		closeDoorOfSacrifice();

		// Load spawn data of monsters.
		loadRoyalGuard();
		loadTriolRevelation();
		loadRoyalGuardCaptain();
		loadRoyalGuardHelper();
		loadGuardOfAltar();
		loadVanHalter();
		loadRitualOffering();
		loadRitualSacrifice();

		// Spawn monsters.
		spawnRoyalGuard();
		spawnTriolRevelation();
		spawnVanHalter();
		spawnRitualOffering();

		// Setting spawn data of Dummy camera marker.
		_cameraMarkerSpawn.clear();
		try
		{
			L2NpcTemplate template1 = NpcTable.getInstance().getTemplate(13014); // Dummy npc
			L2Spawn tempSpawn;

			// Dummy camera marker.
			tempSpawn = new L2Spawn(template1);
			tempSpawn.setLocx(-16397);
			tempSpawn.setLocy(-55200);
			tempSpawn.setLocz(-10449);
			tempSpawn.setHeading(16384);
			tempSpawn.setAmount(1);
			tempSpawn.setRespawnDelay(60000);
			SpawnTable.getInstance().addNewSpawn(tempSpawn, false);
			_cameraMarkerSpawn.put(1, tempSpawn);

			tempSpawn = new L2Spawn(template1);
			tempSpawn.setLocx(-16397);
			tempSpawn.setLocy(-55200);
			tempSpawn.setLocz(-10051);
			tempSpawn.setHeading(16384);
			tempSpawn.setAmount(1);
			tempSpawn.setRespawnDelay(60000);
			SpawnTable.getInstance().addNewSpawn(tempSpawn, false);
			_cameraMarkerSpawn.put(2, tempSpawn);

			tempSpawn = new L2Spawn(template1);
			tempSpawn.setLocx(-16397);
			tempSpawn.setLocy(-55200);
			tempSpawn.setLocz(-9741);
			tempSpawn.setHeading(16384);
			tempSpawn.setAmount(1);
			tempSpawn.setRespawnDelay(60000);
			SpawnTable.getInstance().addNewSpawn(tempSpawn, false);
			_cameraMarkerSpawn.put(3, tempSpawn);

			tempSpawn = new L2Spawn(template1);
			tempSpawn.setLocx(-16397);
			tempSpawn.setLocy(-55200);
			tempSpawn.setLocz(-9394);
			tempSpawn.setHeading(16384);
			tempSpawn.setAmount(1);
			tempSpawn.setRespawnDelay(60000);
			SpawnTable.getInstance().addNewSpawn(tempSpawn, false);
			_cameraMarkerSpawn.put(4, tempSpawn);

			tempSpawn = new L2Spawn(template1);
			tempSpawn.setLocx(-16397);
			tempSpawn.setLocy(-55197);
			tempSpawn.setLocz(-8739);
			tempSpawn.setHeading(16384);
			tempSpawn.setAmount(1);
			tempSpawn.setRespawnDelay(60000);
			SpawnTable.getInstance().addNewSpawn(tempSpawn, false);
			_cameraMarkerSpawn.put(5, tempSpawn);
		}
		catch(Exception e)
		{
			if(Config.DEBUG)
				e.printStackTrace();
			
			_log.warn("VanHalterManager : " + e.getMessage() + " :" + e);
		}

		// Set time up.
		if(_timeUpTask != null)
		{
			_timeUpTask.cancel(false);
		}
		_timeUpTask = ThreadPoolManager.getInstance().scheduleGeneral(new TimeUp(), Config.HPH_ACTIVITYTIMEOFHALTER);

		// Set bleeding to palyers.
		if(_setBleedTask != null)
		{
			_setBleedTask.cancel(false);
		}
		_setBleedTask = ThreadPoolManager.getInstance().scheduleGeneral(new Bleeding(), 2000);

		Integer status = GrandBossManager.getInstance().getBossStatus(29062);
		if(status == INTERVAL)
		{
			enterInterval();
		}
		else
		{
			GrandBossManager.getInstance().setBossStatus(29062, NOTSPAWN);
		}
	}

	@Override
	public String onAttack(L2NpcInstance npc, L2PcInstance attacker, int damage, boolean isPet)
	{
		if(npc.getNpcId() == 29062)
		{
			if((int) (npc.getStatus().getCurrentHp() / npc.getMaxHp()) * 100 <= 20)
			{
				callRoyalGuardHelper();
			}
		}
		return super.onAttack(npc, attacker, damage, isPet);
	}

	@Override
	public String onKill(L2NpcInstance npc, L2PcInstance killer, boolean isPet)
	{
		int npcId = npc.getNpcId();
		if(npcId == 32058 || npcId == 32059 || npcId == 32060 || npcId == 32061 || npcId == 32062 || npcId == 32063 || npcId == 32064 || npcId == 32065 || npcId == 32066)
		{
			removeBleeding(npcId);
		}
		checkTriolRevelationDestroy();
		if(npcId == 22188)
		{
			checkRoyalGuardCaptainDestroy();
		}
		if(npcId == 29062)
		{
			enterInterval();
		}
		return super.onKill(npc, killer, isPet);
	}

	// Load Royal Guard.
	private void loadRoyalGuard()
	{
		_royalGuardSpawn.clear();

		List<L2Spawn> spawnList = VanHalterDao.loadFromSpawnListBetweenIds(22175, 22176);

		spawnList.forEach(spawnDat -> {
			SpawnTable.getInstance().addNewSpawn(spawnDat, false);
			_royalGuardSpawn.add(spawnDat);
		});
	}

	private void spawnRoyalGuard()
	{
		if(!_royalGuard.isEmpty())
		{
			deleteRoyalGuard();
		}

		for(L2Spawn rgs : _royalGuardSpawn)
		{
			rgs.startRespawn();
			_royalGuard.add(rgs.doSpawn());
		}
	}

	private void deleteRoyalGuard()
	{
		for(L2NpcInstance rg : _royalGuard)
		{
			rg.getSpawn().stopRespawn();
			rg.deleteMe();
		}

		_royalGuard.clear();
	}

	// Load Triol's Revelation.
	private void loadTriolRevelation()
	{
		_triolRevelationSpawn.clear();


		List<L2Spawn> spawnList = VanHalterDao.loadFromSpawnListBetweenIds(32058, 32068);

		spawnList.forEach(spawnDat -> {
			SpawnTable.getInstance().addNewSpawn(spawnDat, false);
			_triolRevelationSpawn.add(spawnDat);
		});
	}

	private void spawnTriolRevelation()
	{
		if(!_triolRevelation.isEmpty())
		{
			deleteTriolRevelation();
		}

		for(L2Spawn trs : _triolRevelationSpawn)
		{
			trs.startRespawn();
			_triolRevelation.add(trs.doSpawn());
			if(trs.getNpcid() != 32067 && trs.getNpcid() != 32068)
			{
				_triolRevelationAlive.add(trs);
			}
		}
	}

	private void deleteTriolRevelation()
	{
		for(L2NpcInstance tr : _triolRevelation)
		{
			tr.getSpawn().stopRespawn();
			tr.deleteMe();
		}
		_triolRevelation.clear();
		_bleedingPlayers.clear();
	}

	// Load Royal Guard Captain.
	private void loadRoyalGuardCaptain()
	{
		_royalGuardCaptainSpawn.clear();

		L2Spawn spawnDat = VanHalterDao.loadFromSpawnListById(22188);

		SpawnTable.getInstance().addNewSpawn(spawnDat, false);
		_royalGuardCaptainSpawn.add(spawnDat);

		if (Config.DEBUG) {
			_log.info("VanHalterManager.loadRoyalGuardCaptain: Loaded " + _royalGuardCaptainSpawn.size() + " Royal Guard Captain spawn locations.");
		}
	}

	private void spawnRoyalGuardCaptain()
	{
		if(!_royalGuardCaptain.isEmpty())
		{
			deleteRoyalGuardCaptain();
		}

		for(L2Spawn trs : _royalGuardCaptainSpawn)
		{
			trs.startRespawn();
			_royalGuardCaptain.add(trs.doSpawn());
		}
		_isCaptainSpawned = true;
	}

	private void deleteRoyalGuardCaptain()
	{
		for(L2NpcInstance tr : _royalGuardCaptain)
		{
			tr.getSpawn().stopRespawn();
			tr.deleteMe();
		}

		_royalGuardCaptain.clear();
	}

	// Load Royal Guard Helper.
	private void loadRoyalGuardHelper()
	{
		_royalGuardHelperSpawn.clear();

		L2Spawn spawnDat = VanHalterDao.loadFromSpawnListById(22191);

		SpawnTable.getInstance().addNewSpawn(spawnDat, false);
		_royalGuardHelperSpawn.add(spawnDat);

		if (Config.DEBUG) {
			_log.info("VanHalterManager.loadRoyalGuardHelper: Loaded " + _royalGuardHelperSpawn.size() + " Royal Guard Helper spawn locations.");
		}
	}

	private void spawnRoyalGuardHepler()
	{
		for(L2Spawn trs : _royalGuardHelperSpawn)
		{
			trs.startRespawn();
			_royalGuardHepler.add(trs.doSpawn());
		}
	}

	private void deleteRoyalGuardHepler()
	{
		for(L2NpcInstance tr : _royalGuardHepler)
		{
			tr.getSpawn().stopRespawn();
			tr.deleteMe();
		}
		_royalGuardHepler.clear();
	}

	// Load Guard Of Altar
	private void loadGuardOfAltar()
	{
		_guardOfAltarSpawn.clear();

		L2Spawn spawnDat = VanHalterDao.loadFromSpawnListById(32051);

		SpawnTable.getInstance().addNewSpawn(spawnDat, false);
		_guardOfAltarSpawn.add(spawnDat);

		if (Config.DEBUG) {
			_log.info("VanHalterManager.loadGuardOfAltar: Loaded " + _guardOfAltarSpawn.size() + " Guard Of Altar spawn locations.");
		}
	}

	private void spawnGuardOfAltar()
	{
		if(!_guardOfAltar.isEmpty())
		{
			deleteGuardOfAltar();
		}

		for(L2Spawn trs : _guardOfAltarSpawn)
		{
			trs.startRespawn();
			_guardOfAltar.add(trs.doSpawn());
		}
	}

	private void deleteGuardOfAltar()
	{
		for(L2NpcInstance tr : _guardOfAltar)
		{
			tr.getSpawn().stopRespawn();
			tr.deleteMe();
		}

		_guardOfAltar.clear();
	}

	// Load High Priestess van Halter.
	private void loadVanHalter()
	{
		_vanHalterSpawn = null;

        L2Spawn spawnDat = VanHalterDao.loadFromSpawnListById(29062);

		SpawnTable.getInstance().addNewSpawn(spawnDat, false);
		_vanHalterSpawn = spawnDat;

		if (Config.DEBUG) {
			_log.info("VanHalterManager.loadVanHalter: Loaded High Priestess van Halter spawn locations.");
		}
	}

	private void spawnVanHalter()
	{
		_vanHalter = (L2RaidBossInstance) _vanHalterSpawn.doSpawn();
		//_vanHalter.setIsImmobilized(true);
		_vanHalter.setIsInvul(true);
		_isHalterSpawned = true;
	}

	private void deleteVanHalter()
	{
		//_vanHalter.setIsImmobilized(false);
		_vanHalter.setIsInvul(false);
		_vanHalter.getSpawn().stopRespawn();
		_vanHalter.deleteMe();
	}

	// Load Ritual Offering.
	private void loadRitualOffering()
	{
		_ritualOfferingSpawn = null;

		L2Spawn spawnDat = VanHalterDao.loadFromSpawnListById(32038);

		SpawnTable.getInstance().addNewSpawn(spawnDat, false);
		_ritualOfferingSpawn = spawnDat;

		if (Config.DEBUG) {
			_log.info("VanHalterManager.loadRitualOffering: Loaded Ritual Offering spawn locations.");
		}
	}

	private void spawnRitualOffering()
	{
		_ritualOffering = _ritualOfferingSpawn.doSpawn();
		//_ritualOffering.setIsImmobilized(true);
		_ritualOffering.setIsInvul(true);
		_ritualOffering.setIsParalyzed(true);
	}

	private void deleteRitualOffering()
	{
		//_ritualOffering.setIsImmobilized(false);
		_ritualOffering.setIsInvul(false);
		_ritualOffering.setIsParalyzed(false);
		_ritualOffering.getSpawn().stopRespawn();
		_ritualOffering.deleteMe();
	}

	// Load Ritual Sacrifice.
	private void loadRitualSacrifice()
	{
		_ritualSacrificeSpawn = null;

        L2Spawn spawnDat = VanHalterDao.loadFromSpawnListById(32038);

		SpawnTable.getInstance().addNewSpawn(spawnDat, false);
		_ritualSacrificeSpawn = spawnDat;

		if (Config.DEBUG) {
			_log.info("VanHalterManager.loadRitualSacrifice: Loaded Ritual Sacrifice spawn locations.");
		}
	}

	private void spawnRitualSacrifice()
	{
		_ritualSacrifice = _ritualSacrificeSpawn.doSpawn();
		//_ritualSacrifice.setIsImmobilized(true);
		_ritualSacrifice.setIsInvul(true);
		_isSacrificeSpawned = true;
	}

	private void deleteRitualSacrifice()
	{
		if(!_isSacrificeSpawned)
			return;

		_ritualSacrifice.getSpawn().stopRespawn();
		_ritualSacrifice.deleteMe();
		_isSacrificeSpawned = false;
	}

	private void spawnCameraMarker()
	{
		_cameraMarker.clear();
		for(int i = 1; i <= _cameraMarkerSpawn.size(); i++)
		{
			_cameraMarker.put(i, _cameraMarkerSpawn.get(i).doSpawn());
			_cameraMarker.get(i).getSpawn().stopRespawn();
			//_cameraMarker.get(i).setIsImmobilized(true);
		}
	}

	private void deleteCameraMarker()
	{
		if(_cameraMarker.isEmpty())
			return;

		for(int i = 1; i <= _cameraMarker.size(); i++)
		{
			_cameraMarker.get(i).deleteMe();
		}
		_cameraMarker.clear();
	}

	public void intruderDetection()
	{
		if(_lockUpDoorOfAltarTask == null && !_isLocked && _isCaptainSpawned)
		{
			_lockUpDoorOfAltarTask = ThreadPoolManager.getInstance().scheduleGeneral(new LockUpDoorOfAltar(), Config.HPH_TIMEOFLOCKUPDOOROFALTAR);
		}
	}

	public class LockUpDoorOfAltar implements Runnable
	{
		public void run()
		{
			closeDoorOfAltar(false);
			_isLocked = true;
			_lockUpDoorOfAltarTask = null;
		}
	}

	private void openDoorOfAltar(boolean loop)
	{
		for(L2DoorInstance door : _doorOfAltar)
		{
			try
			{
				door.openMe();
			}
			catch(Exception e)
			{
				if(Config.DEBUG)
					e.printStackTrace();
				
				_log.warn(e.getMessage() + " :" + e);
			}
		}

		if(loop)
		{
			_isLocked = false;

			if(_closeDoorOfAltarTask != null)
			{
				_closeDoorOfAltarTask.cancel(false);
			}
			_closeDoorOfAltarTask = null;
			_closeDoorOfAltarTask = ThreadPoolManager.getInstance().scheduleGeneral(new CloseDoorOfAltar(), Config.HPH_INTERVALOFDOOROFALTER);
		}
		else
		{
			if(_closeDoorOfAltarTask != null)
			{
				_closeDoorOfAltarTask.cancel(false);
			}
			_closeDoorOfAltarTask = null;
		}
	}

	public class OpenDoorOfAltar implements Runnable
	{
		public void run()
		{
			openDoorOfAltar(true);
		}
	}

	private void closeDoorOfAltar(boolean loop)
	{
		for(L2DoorInstance door : _doorOfAltar)
		{
			door.closeMe();
		}

		if(loop)
		{
			if(_openDoorOfAltarTask != null)
			{
				_openDoorOfAltarTask.cancel(false);
			}
			_openDoorOfAltarTask = null;
			_openDoorOfAltarTask = ThreadPoolManager.getInstance().scheduleGeneral(new OpenDoorOfAltar(), Config.HPH_INTERVALOFDOOROFALTER);
		}
		else
		{
			if(_openDoorOfAltarTask != null)
			{
				_openDoorOfAltarTask.cancel(false);
			}
			_openDoorOfAltarTask = null;
		}
	}

	public class CloseDoorOfAltar implements Runnable
	{
		public void run()
		{
			closeDoorOfAltar(true);
		}
	}

	private void openDoorOfSacrifice()
	{
		for(L2DoorInstance door : _doorOfSacrifice)
		{
			try
			{
				door.openMe();
			}
			catch(Exception e)
			{
				if(Config.DEBUG)
					e.printStackTrace();
				
				_log.warn(e.getMessage() + " :" + e);
			}
		}
	}

	private void closeDoorOfSacrifice()
	{
		for(L2DoorInstance door : _doorOfSacrifice)
		{
			try
			{
				door.closeMe();
			}
			catch(Exception e)
			{
				if(Config.DEBUG)
					e.printStackTrace();
				
				_log.warn(e.getMessage() + " :" + e);
			}
		}
	}

	// event
	private void checkTriolRevelationDestroy()
	{
		if(_isCaptainSpawned)
			return;

		boolean isTriolRevelationDestroyed = true;
		for(L2Spawn tra : _triolRevelationAlive)
		{
			if(!tra.getLastSpawn().isDead())
			{
				isTriolRevelationDestroyed = false;
			}
		}

		if(isTriolRevelationDestroyed)
		{
			spawnRoyalGuardCaptain();
		}
	}

	private void checkRoyalGuardCaptainDestroy()
	{
		if(!_isHalterSpawned)
			return;

		deleteRoyalGuard();
		deleteRoyalGuardCaptain();
		spawnGuardOfAltar();
		openDoorOfSacrifice();

		//_vanHalter.setIsImmobilized(true);
		_vanHalter.setIsInvul(true);
		spawnCameraMarker();

		if(_timeUpTask != null)
		{
			_timeUpTask.cancel(false);
		}
		_timeUpTask = null;

		_movieTask = ThreadPoolManager.getInstance().scheduleGeneral(new Movie(1), Config.HPH_APPTIMEOFHALTER);
	}

	// Start fight against High Priestess van Halter.
	private void combatBeginning()
	{
		if(_timeUpTask != null)
		{
			_timeUpTask.cancel(false);
		}
		_timeUpTask = ThreadPoolManager.getInstance().scheduleGeneral(new TimeUp(), Config.HPH_FIGHTTIMEOFHALTER);

		Map<Integer, L2PcInstance> _targets = new HashMap<>();
		int i = 0;

		for(L2PcInstance pc : _vanHalter.getKnownList().getKnownPlayers().values())
		{
			i++;
			_targets.put(i, pc);
		}

		_vanHalter.reduceCurrentHp(1, _targets.get(Rnd.get(1, i)));
	}

	// Call Royal Guard Helper and escape from player.
	private void callRoyalGuardHelper()
	{
		if(!_isHelperCalled)
		{
			_isHelperCalled = true;
			_halterEscapeTask = ThreadPoolManager.getInstance().scheduleGeneral(new HalterEscape(), 500);
			_callRoyalGuardHelperTask = ThreadPoolManager.getInstance().scheduleGeneral(new CallRoyalGuardHelper(), 1000);
		}
	}

	public class CallRoyalGuardHelper implements Runnable
	{
		public void run()
		{
			spawnRoyalGuardHepler();

			if(_royalGuardHepler.size() <= Config.HPH_CALLROYALGUARDHELPERCOUNT && !_vanHalter.isDead())
			{
				if(_callRoyalGuardHelperTask != null)
				{
					_callRoyalGuardHelperTask.cancel(false);
				}
				_callRoyalGuardHelperTask = ThreadPoolManager.getInstance().scheduleGeneral(new CallRoyalGuardHelper(), Config.HPH_CALLROYALGUARDHELPERINTERVAL);
			}
			else
			{
				if(_callRoyalGuardHelperTask != null)
				{
					_callRoyalGuardHelperTask.cancel(false);
				}
				_callRoyalGuardHelperTask = null;
			}
		}
	}

	public class HalterEscape implements Runnable
	{
		public void run()
		{
			if(_royalGuardHepler.size() <= Config.HPH_CALLROYALGUARDHELPERCOUNT && !_vanHalter.isDead())
			{
				if(_vanHalter.isAfraid())
				{
					_vanHalter.stopEffects(L2Effect.EffectType.FEAR);
					_vanHalter.setIsAfraid(false);
					_vanHalter.updateAbnormalEffect();
				}
				else
				{
					_vanHalter.startFear();
					if(_vanHalter.getZ() >= -10476)
					{
						L2CharPosition pos = new L2CharPosition(-16397, -53308, -10448, 0);
						if(_vanHalter.getX() == pos.x && _vanHalter.getY() == pos.y)
						{
							_vanHalter.stopEffects(L2Effect.EffectType.FEAR);
							_vanHalter.setIsAfraid(false);
							_vanHalter.updateAbnormalEffect();
						}
						else
						{
							_vanHalter.getAI().setIntention(CtrlIntention.AI_INTENTION_MOVE_TO, pos);
						}
					}
					else if(_vanHalter.getX() >= -16397)
					{
						L2CharPosition pos = new L2CharPosition(-15548, -54830, -10475, 0);
						_vanHalter.getAI().setIntention(CtrlIntention.AI_INTENTION_MOVE_TO, pos);
					}
					else
					{
						L2CharPosition pos = new L2CharPosition(-17248, -54830, -10475, 0);
						_vanHalter.getAI().setIntention(CtrlIntention.AI_INTENTION_MOVE_TO, pos);
					}
				}
				if(_halterEscapeTask != null)
				{
					_halterEscapeTask.cancel(false);
				}
				_halterEscapeTask = ThreadPoolManager.getInstance().scheduleGeneral(new HalterEscape(), 5000);
			}
			else
			{
				_vanHalter.stopEffects(L2Effect.EffectType.FEAR);
				_vanHalter.setIsAfraid(false);
				_vanHalter.updateAbnormalEffect();
				if(_halterEscapeTask != null)
				{
					_halterEscapeTask.cancel(false);
				}
				_halterEscapeTask = null;
			}
		}
	}

	// Check bleeding player.
	private void addBleeding()
	{
		L2Skill bleed = SkillTable.getInstance().getInfo(4615, 12);

		for(L2NpcInstance tr : _triolRevelation)
		{
			if(!tr.getKnownList().getKnownPlayersInRadius(tr.getAggroRange()).iterator().hasNext() || tr.isDead())
			{
				continue;
			}

			List<L2PcInstance> bpc = new ArrayList<>();

			for(L2PcInstance pc : tr.getKnownList().getKnownPlayersInRadius(tr.getAggroRange()))
			{
				if(pc.getFirstEffect(bleed) == null)
				{
					bleed.getEffects(tr, pc);
					tr.broadcastPacket(new MagicSkillUser(tr, pc, bleed.getId(), 12, 1, 1));
				}

				bpc.add(pc);
			}
			_bleedingPlayers.remove(tr.getNpcId());
			_bleedingPlayers.put(tr.getNpcId(), bpc);
		}
	}

	private void removeBleeding(int npcId)
	{
		if(_bleedingPlayers.get(npcId) == null)
			return;
		for(L2PcInstance pc : _bleedingPlayers.get(npcId))
		{
			if(pc.getFirstEffect(L2Effect.EffectType.DMG_OVER_TIME) != null)
			{
				pc.stopEffects(L2Effect.EffectType.DMG_OVER_TIME);
			}
		}
		_bleedingPlayers.remove(npcId);
	}

	public class Bleeding implements Runnable
	{
		public void run()
		{
			addBleeding();

			if(_setBleedTask != null)
			{
				_setBleedTask.cancel(false);
			}
			_setBleedTask = ThreadPoolManager.getInstance().scheduleGeneral(new Bleeding(), 2000);
		}
	}

	// High Priestess van Halter dead or time up.
	private void enterInterval()
	{
		// Cancel all task
		if(_callRoyalGuardHelperTask != null)
		{
			_callRoyalGuardHelperTask.cancel(false);
		}
		_callRoyalGuardHelperTask = null;

		if(_closeDoorOfAltarTask != null)
		{
			_closeDoorOfAltarTask.cancel(false);
		}
		_closeDoorOfAltarTask = null;

		if(_halterEscapeTask != null)
		{
			_halterEscapeTask.cancel(false);
		}
		_halterEscapeTask = null;

		if(_intervalTask != null)
		{
			_intervalTask.cancel(false);
		}
		_intervalTask = null;

		if(_lockUpDoorOfAltarTask != null)
		{
			_lockUpDoorOfAltarTask.cancel(false);
		}
		_lockUpDoorOfAltarTask = null;

		if(_movieTask != null)
		{
			_movieTask.cancel(false);
		}
		_movieTask = null;

		if(_openDoorOfAltarTask != null)
		{
			_openDoorOfAltarTask.cancel(false);
		}
		_openDoorOfAltarTask = null;

		if(_timeUpTask != null)
		{
			_timeUpTask.cancel(false);
		}
		_timeUpTask = null;

		// Delete monsters
		if(_vanHalter.isDead())
		{
			_vanHalter.getSpawn().stopRespawn();
		}
		else
		{
			deleteVanHalter();
		}
		deleteRoyalGuardHepler();
		deleteRoyalGuardCaptain();
		deleteRoyalGuard();
		deleteRitualOffering();
		deleteRitualSacrifice();
		deleteGuardOfAltar();

		// Set interval end.
		if(_intervalTask != null)
		{
			_intervalTask.cancel(false);
		}

		Integer status = GrandBossManager.getInstance().getBossStatus(29062);
		
		if(status != INTERVAL)
		{
			long interval = Rnd.get(Config.HPH_FIXINTERVALOFHALTER, Config.HPH_FIXINTERVALOFHALTER + Config.HPH_RANDOMINTERVALOFHALTER) * 3600000;
			StatsSet info = GrandBossManager.getInstance().getStatsSet(29062);
			info.set("respawn_time", (System.currentTimeMillis() + interval));
			GrandBossManager.getInstance().setStatsSet(29062, info);
			GrandBossManager.getInstance().setBossStatus(29062, INTERVAL);
		}

		StatsSet info = GrandBossManager.getInstance().getStatsSet(29062);
		long temp = info.getLong("respawn_time") - System.currentTimeMillis();
		_intervalTask = ThreadPoolManager.getInstance().scheduleGeneral(new Interval(), temp);
	}

	// Interval.
	public class Interval implements Runnable
	{
		public void run()
		{
			setupAltar();
		}
	}

	// Interval end.
	private void setupAltar()
	{
		// Cancel all task
		if(_callRoyalGuardHelperTask != null)
		{
			_callRoyalGuardHelperTask.cancel(false);
		}
		_callRoyalGuardHelperTask = null;

		if(_closeDoorOfAltarTask != null)
		{
			_closeDoorOfAltarTask.cancel(false);
		}
		_closeDoorOfAltarTask = null;

		if(_halterEscapeTask != null)
		{
			_halterEscapeTask.cancel(false);
		}
		_halterEscapeTask = null;

		if(_intervalTask != null)
		{
			_intervalTask.cancel(false);
		}
		_intervalTask = null;

		if(_lockUpDoorOfAltarTask != null)
		{
			_lockUpDoorOfAltarTask.cancel(false);
		}
		_lockUpDoorOfAltarTask = null;

		if(_movieTask != null)
		{
			_movieTask.cancel(false);
		}
		_movieTask = null;

		if(_openDoorOfAltarTask != null)
		{
			_openDoorOfAltarTask.cancel(false);
		}
		_openDoorOfAltarTask = null;

		if(_timeUpTask != null)
		{
			_timeUpTask.cancel(false);
		}
		_timeUpTask = null;

		// Delete all monsters
		deleteVanHalter();
		deleteTriolRevelation();
		deleteRoyalGuardHepler();
		deleteRoyalGuardCaptain();
		deleteRoyalGuard();
		deleteRitualSacrifice();
		deleteRitualOffering();
		deleteGuardOfAltar();
		deleteCameraMarker();

		// Clear flag.
		_isLocked = false;
		_isCaptainSpawned = false;
		_isHelperCalled = false;
		_isHalterSpawned = false;

		// Set door state
		closeDoorOfSacrifice();
		openDoorOfAltar(true);

		// Respawn monsters.
		spawnTriolRevelation();
		spawnRoyalGuard();
		spawnRitualOffering();
		spawnVanHalter();

		GrandBossManager.getInstance().setBossStatus(29062, NOTSPAWN);

		// Set time up.
		if(_timeUpTask != null)
		{
			_timeUpTask.cancel(false);
		}
		_timeUpTask = ThreadPoolManager.getInstance().scheduleGeneral(new TimeUp(), Config.HPH_ACTIVITYTIMEOFHALTER);
	}

	// Time up.
	public class TimeUp implements Runnable
	{
		public void run()
		{
			enterInterval();
		}
	}

	// Appearance movie.
	public class Movie implements Runnable
	{
		private final int _distance = 6502500;
		private final int _taskId;

		private Movie(int taskId)
		{
			_taskId = taskId;
		}

		public void run()
		{
			_vanHalter.setHeading(16384);
			_vanHalter.setTarget(_ritualOffering);

			switch(_taskId)
			{
				case 1:
					GrandBossManager.getInstance().setBossStatus(29062, ALIVE);

					// Set camera.
					for(L2PcInstance pc : _vanHalter.getKnownList().getKnownPlayers().values())
					{
						if(pc.getPlanDistanceSq(_vanHalter) <= _distance)
						{
							_vanHalter.broadcastPacket(new SpecialCamera(_vanHalter.getObjectId(), 50, 90, 0, 0, 15000));
						}
					}

					// Set next task.
					if(_movieTask != null)
					{
						_movieTask.cancel(false);
					}
					_movieTask = null;
					_movieTask = ThreadPoolManager.getInstance().scheduleGeneral(new Movie(2), 16);

					break;

				case 2:
					// Set camera.
					for(L2PcInstance pc : _vanHalter.getKnownList().getKnownPlayers().values())
					{
						if(pc.getPlanDistanceSq(_cameraMarker.get(5)) <= _distance)
						{
							_cameraMarker.get(5).broadcastPacket(new SpecialCamera(_cameraMarker.get(5).getObjectId(), 1842, 100, -3, 0, 15000));
						}
					}

					// Set next task.
					if(_movieTask != null)
					{
						_movieTask.cancel(false);
					}
					_movieTask = null;
					_movieTask = ThreadPoolManager.getInstance().scheduleGeneral(new Movie(3), 1);

					break;

				case 3:
					// Set camera.
					for(L2PcInstance pc : _vanHalter.getKnownList().getKnownPlayers().values())
					{
						if(pc.getPlanDistanceSq(_cameraMarker.get(5)) <= _distance)
						{
							_cameraMarker.get(5).broadcastPacket(new SpecialCamera(_cameraMarker.get(5).getObjectId(), 1861, 97, -10, 1500, 15000));
						}
					}

					// Set next task.
					if(_movieTask != null)
					{
						_movieTask.cancel(false);
					}
					_movieTask = null;
					_movieTask = ThreadPoolManager.getInstance().scheduleGeneral(new Movie(4), 1500);

					break;

				case 4:
					// Set camera.
					for(L2PcInstance pc : _vanHalter.getKnownList().getKnownPlayers().values())
					{
						if(pc.getPlanDistanceSq(_cameraMarker.get(4)) <= _distance)
						{
							_cameraMarker.get(4).broadcastPacket(new SpecialCamera(_cameraMarker.get(4).getObjectId(), 1876, 97, 12, 0, 15000));
						}
					}

					// Set next task.
					if(_movieTask != null)
					{
						_movieTask.cancel(false);
					}
					_movieTask = null;
					_movieTask = ThreadPoolManager.getInstance().scheduleGeneral(new Movie(5), 1);

					break;

				case 5:
					// Set camera.
					for(L2PcInstance pc : _vanHalter.getKnownList().getKnownPlayers().values())
					{
						if(pc.getPlanDistanceSq(_cameraMarker.get(4)) <= _distance)
						{
							_cameraMarker.get(4).broadcastPacket(new SpecialCamera(_cameraMarker.get(4).getObjectId(), 1839, 94, 0, 1500, 15000));
						}
					}

					// Set next task.
					if(_movieTask != null)
					{
						_movieTask.cancel(false);
					}
					_movieTask = null;
					_movieTask = ThreadPoolManager.getInstance().scheduleGeneral(new Movie(6), 1500);

					break;

				case 6:
					// Set camera.
					for(L2PcInstance pc : _vanHalter.getKnownList().getKnownPlayers().values())
					{
						if(pc.getPlanDistanceSq(_cameraMarker.get(3)) <= _distance)
						{
							_cameraMarker.get(3).broadcastPacket(new SpecialCamera(_cameraMarker.get(3).getObjectId(), 1872, 94, 15, 0, 15000));
						}
					}

					// Set next task.
					if(_movieTask != null)
					{
						_movieTask.cancel(false);
					}
					_movieTask = null;
					_movieTask = ThreadPoolManager.getInstance().scheduleGeneral(new Movie(7), 1);

					break;

				case 7:
					// Set camera.
					for(L2PcInstance pc : _vanHalter.getKnownList().getKnownPlayers().values())
					{
						if(pc.getPlanDistanceSq(_cameraMarker.get(3)) <= _distance)
						{
							_cameraMarker.get(3).broadcastPacket(new SpecialCamera(_cameraMarker.get(3).getObjectId(), 1839, 92, 0, 1500, 15000));
						}
					}

					// Set next task.
					if(_movieTask != null)
					{
						_movieTask.cancel(false);
					}
					_movieTask = null;
					_movieTask = ThreadPoolManager.getInstance().scheduleGeneral(new Movie(8), 1500);

					break;

				case 8:
					// Set camera.
					for(L2PcInstance pc : _vanHalter.getKnownList().getKnownPlayers().values())
					{
						if(pc.getPlanDistanceSq(_cameraMarker.get(2)) <= _distance)
						{
							_cameraMarker.get(2).broadcastPacket(new SpecialCamera(_cameraMarker.get(2).getObjectId(), 1872, 92, 15, 0, 15000));
						}
					}

					// Set next task.
					if(_movieTask != null)
					{
						_movieTask.cancel(false);
					}
					_movieTask = null;
					_movieTask = ThreadPoolManager.getInstance().scheduleGeneral(new Movie(9), 1);

					break;

				case 9:
					// Set camera.
					for(L2PcInstance pc : _vanHalter.getKnownList().getKnownPlayers().values())
					{
						if(pc.getPlanDistanceSq(_cameraMarker.get(2)) <= _distance)
						{
							_cameraMarker.get(2).broadcastPacket(new SpecialCamera(_cameraMarker.get(2).getObjectId(), 1839, 90, 5, 1500, 15000));
						}
					}

					// Set next task.
					if(_movieTask != null)
					{
						_movieTask.cancel(false);
					}
					_movieTask = null;
					_movieTask = ThreadPoolManager.getInstance().scheduleGeneral(new Movie(10), 1500);

					break;

				case 10:
					// Set camera.
					for(L2PcInstance pc : _vanHalter.getKnownList().getKnownPlayers().values())
					{
						if(pc.getPlanDistanceSq(_cameraMarker.get(1)) <= _distance)
						{
							_cameraMarker.get(1).broadcastPacket(new SpecialCamera(_cameraMarker.get(1).getObjectId(), 1872, 90, 5, 0, 15000));
						}
					}

					// Set next task.
					if(_movieTask != null)
					{
						_movieTask.cancel(false);
					}
					_movieTask = null;
					_movieTask = ThreadPoolManager.getInstance().scheduleGeneral(new Movie(11), 1);

					break;

				case 11:
					// Set camera.
					for(L2PcInstance pc : _vanHalter.getKnownList().getKnownPlayers().values())
					{
						if(pc.getPlanDistanceSq(_cameraMarker.get(1)) <= _distance)
						{
							_cameraMarker.get(1).broadcastPacket(new SpecialCamera(_cameraMarker.get(1).getObjectId(), 2002, 90, 2, 1500, 15000));
						}
					}

					// Set next task.
					if(_movieTask != null)
					{
						_movieTask.cancel(false);
					}
					_movieTask = null;
					_movieTask = ThreadPoolManager.getInstance().scheduleGeneral(new Movie(12), 2000);

					break;

				case 12:
					// Set camera.
					for(L2PcInstance pc : _vanHalter.getKnownList().getKnownPlayers().values())
					{
						if(pc.getPlanDistanceSq(_vanHalter) <= _distance)
						{
							_vanHalter.broadcastPacket(new SpecialCamera(_vanHalter.getObjectId(), 50, 90, 10, 0, 15000));
						}
					}

					// Set next task.
					if(_movieTask != null)
					{
						_movieTask.cancel(false);
					}
					_movieTask = null;
					_movieTask = ThreadPoolManager.getInstance().scheduleGeneral(new Movie(13), 1000);

					break;

				case 13:
					// High Priestess van Halter uses the skill to kill Ritual Offering.
					L2Skill skill = SkillTable.getInstance().getInfo(1168, 7);
					_ritualOffering.setIsInvul(false);
					_vanHalter.setTarget(_ritualOffering);
					//_vanHalter.setIsImmobilized(false);
					_vanHalter.doCast(skill);
					//_vanHalter.setIsImmobilized(true);

					// Set next task.
					if(_movieTask != null)
					{
						_movieTask.cancel(false);
					}
					_movieTask = null;
					_movieTask = ThreadPoolManager.getInstance().scheduleGeneral(new Movie(14), 4700);

					break;

				case 14:
					_ritualOffering.setIsInvul(false);
					_ritualOffering.reduceCurrentHp(_ritualOffering.getMaxHp() + 1, _vanHalter);

					// Set next task.
					if(_movieTask != null)
					{
						_movieTask.cancel(false);
					}
					_movieTask = null;
					_movieTask = ThreadPoolManager.getInstance().scheduleGeneral(new Movie(15), 4300);

					break;

				case 15:
					spawnRitualSacrifice();
					deleteRitualOffering();

					// Set camera.
					for(L2PcInstance pc : _vanHalter.getKnownList().getKnownPlayers().values())
					{
						if(pc.getPlanDistanceSq(_vanHalter) <= _distance)
						{
							_vanHalter.broadcastPacket(new SpecialCamera(_vanHalter.getObjectId(), 100, 90, 15, 1500, 15000));
						}
					}

					// Set next task.
					if(_movieTask != null)
					{
						_movieTask.cancel(false);
					}
					_movieTask = null;
					_movieTask = ThreadPoolManager.getInstance().scheduleGeneral(new Movie(16), 2000);

					break;

				case 16:
					// Set camera.
					for(L2PcInstance pc : _vanHalter.getKnownList().getKnownPlayers().values())
					{
						if(pc.getPlanDistanceSq(_vanHalter) <= _distance)
						{
							_vanHalter.broadcastPacket(new SpecialCamera(_vanHalter.getObjectId(), 5200, 90, -10, 9500, 6000));
						}
					}

					// Set next task.
					if(_movieTask != null)
					{
						_movieTask.cancel(false);
					}
					_movieTask = null;
					_movieTask = ThreadPoolManager.getInstance().scheduleGeneral(new Movie(17), 6000);

					break;

				case 17:
					deleteRitualSacrifice();
					deleteCameraMarker();
					//_vanHalter.setIsImmobilized(false);
					_vanHalter.setIsInvul(false);

					if(_movieTask != null)
					{
						_movieTask.cancel(false);
					}
					_movieTask = null;
					_movieTask = ThreadPoolManager.getInstance().scheduleGeneral(new Movie(18), 1000);

					break;

				case 18:
					combatBeginning();
					if(_movieTask != null)
					{
						_movieTask.cancel(false);
					}
					_movieTask = null;
			}
		}
	}

	@Override
	public void run()
	{}
}
