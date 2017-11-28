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

import static com.it.br.configuration.Configurator.getSettings;

import java.util.ArrayList;
import java.util.List;

import com.it.br.Config;
import com.it.br.configuration.settings.L2JBrasilSettings;
import com.it.br.gameserver.Announcements;
import com.it.br.gameserver.instancemanager.GrandBossManager;
import com.it.br.gameserver.model.L2Attackable;
import com.it.br.gameserver.model.actor.instance.L2GrandBossInstance;
import com.it.br.gameserver.model.actor.instance.L2NpcInstance;
import com.it.br.gameserver.model.actor.instance.L2PcInstance;
import com.it.br.gameserver.model.quest.Quest;
import com.it.br.gameserver.network.serverpackets.CreatureSay;
import com.it.br.gameserver.network.serverpackets.PlaySound;
import com.it.br.gameserver.templates.StatsSet;
import com.it.br.util.Rnd;

/**
 * @reworked *slayer
 * @author qwerty
 */

public class Core extends Quest implements Runnable
{
	private static final int CORE = 29006;
	private static final int DEATH_KNIGHT = 29007;
	private static final int DOOM_WRAITH = 29008;
	//private static final int DICOR = 29009;
	//private static final int VALIDUS = 29010;
	private static final int SUSCEPTOR = 29011;
	//private static final int PERUM = 29012;
	//private static final int PREMO = 29013;

	//CORE Status Tracking :
	private static final byte ALIVE = 0; //Core is spawned.
	private static final byte DEAD = 1; //Core has been killed.

	private static boolean _FirstAttacked;

	List<L2Attackable> Minions = new ArrayList<>();
	private Integer status;
	//private static final Logger _log = Logger.getLogger(Core.class.getName());

	public Core(int id, String name, String descr)
	{
		super(id, name, descr);
		
		int[] mobs =
		{
				CORE, DEATH_KNIGHT, DOOM_WRAITH, SUSCEPTOR
		};
		for(int mob : mobs)
		{
			addEventId(mob, Quest.QuestEventType.ON_KILL);
			addEventId(mob, Quest.QuestEventType.ON_ATTACK);
		}
		_FirstAttacked = false;
		StatsSet info = GrandBossManager.getInstance().getStatsSet(CORE);
		status = GrandBossManager.getInstance().getBossStatus(CORE);
		if(status == DEAD)
		{
			// load the unlock date and time for Core from DB
			long temp = info.getLong("respawn_time") - System.currentTimeMillis();
			if(temp > 0)
			{
				startQuestTimer("core_unlock", temp, null, null);
			}
			else
			{
				// the time has already expired while the server was offline. Immediately spawn Core.
				L2GrandBossInstance core = (L2GrandBossInstance) addSpawn(CORE, 17726, 108915, -6480, 0, false, 0);
				if(getSettings(L2JBrasilSettings.class).isAnnounceSpawnRaidEnabled()) {
					Announcements.getInstance().announceToAll("Raid boss " + core.getName() + " spawned in world.");
				}
				GrandBossManager.getInstance().setBossStatus(CORE, ALIVE);
				spawnBoss(core);
			}
		}
		else
		{
			String test = loadGlobalQuestVar("Core_Attacked");
			if(test.equalsIgnoreCase("true"))
			{
				_FirstAttacked = true;
			}
			/*int loc_x = info.getInteger("loc_x");
			int loc_y = info.getInteger("loc_y");
			int loc_z = info.getInteger("loc_z");
			int heading = info.getInteger("heading");
			int hp = info.getInteger("currentHP");
			int mp = info.getInteger("currentMP");
			L2GrandBossInstance core = (L2GrandBossInstance) addSpawn(CORE,loc_x,loc_y,loc_z,heading,false,0);
			core.setCurrentHpMp(hp,mp);*/
			L2GrandBossInstance core = (L2GrandBossInstance) addSpawn(CORE, 17726, 108915, -6480, 0, false, 0);
			if(getSettings(L2JBrasilSettings.class).isAnnounceSpawnRaidEnabled()) {
				Announcements.getInstance().announceToAll("Raid boss " + core.getName() + " spawned in world.");
			}
			spawnBoss(core);
		}
	}

	@Override
	public void saveGlobalData()
	{
		String val = "" + _FirstAttacked;
		saveGlobalQuestVar("Core_Attacked", val);
	}

	@Override
	public String onAdvEvent(String event, L2NpcInstance npc, L2PcInstance player)
	{
		status = GrandBossManager.getInstance().getBossStatus(CORE);
		if(event.equalsIgnoreCase("core_unlock"))
		{
			L2GrandBossInstance core = (L2GrandBossInstance) addSpawn(CORE, 17726, 108915, -6480, 0, false, 0);
			if(getSettings(L2JBrasilSettings.class).isAnnounceSpawnRaidEnabled()) {
				Announcements.getInstance().announceToAll("Raid boss " + core.getName() + " spawned in world.");
			}
			GrandBossManager.getInstance().setBossStatus(CORE, ALIVE);
			spawnBoss(core);
		}
		else if(status == null)
		{
			_log.warning("GrandBoss with Id "+ CORE +" has not valid status into GrandBossManager");
		}
		else if(event.equalsIgnoreCase("spawn_minion") && status == ALIVE)
		{
			Minions.add((L2Attackable) addSpawn(npc.getNpcId(), npc.getX(), npc.getY(), npc.getZ(), npc.getHeading(), false, 0));
		}
		else if(event.equalsIgnoreCase("despawn_minions"))
		{
			for(int i = 0; i < Minions.size(); i++)
			{
				L2Attackable mob = Minions.get(i);
				if(mob != null)
				{
					mob.decayMe();
				}
			}
			Minions.clear();
		}
		return super.onAdvEvent(event, npc, player);
	}

	@Override
	public String onAttack(L2NpcInstance npc, L2PcInstance attacker, int damage, boolean isPet)
	{
		if(npc.getNpcId() == CORE)
		{
			if(_FirstAttacked)
			{
				if(Rnd.get(100) == 0)
				{
					npc.broadcastPacket(new CreatureSay(npc.getObjectId(), 0, npc.getName(), "Removing intruders."));
				}
			}
			else
			{
				_FirstAttacked = true;
				npc.broadcastPacket(new CreatureSay(npc.getObjectId(), 0, npc.getName(), "A non-permitted target has been discovered."));
				npc.broadcastPacket(new CreatureSay(npc.getObjectId(), 0, npc.getName(), "Starting intruder removal system."));
			}
		}
		return super.onAttack(npc, attacker, damage, isPet);
	}

	@Override
	public String onKill(L2NpcInstance npc, L2PcInstance killer, boolean isPet)
	{
		int npcId = npc.getNpcId();
		String name = npc.getName();
		if(npcId == CORE)
		{
			int objId = npc.getObjectId();
			npc.broadcastPacket(new PlaySound(1, "BS02_D", 1, objId, npc.getX(), npc.getY(), npc.getZ()));
			npc.broadcastPacket(new CreatureSay(objId, 0, name, "A fatal error has occurred."));
			npc.broadcastPacket(new CreatureSay(objId, 0, name, "System is being shut down..."));
			npc.broadcastPacket(new CreatureSay(objId, 0, name, "......"));
			_FirstAttacked = false;
			addSpawn(31842, 16502, 110165, -6394, 0, false, 900000);
			addSpawn(31842, 18948, 110166, -6397, 0, false, 900000);
			GrandBossManager.getInstance().setBossStatus(CORE, DEAD);
			//time is 60hour	+/- 23hour
			long respawnTime = (Config.CORE_RESP_FIRST + Rnd.get(Config.CORE_RESP_SECOND)) * 3600000;
			startQuestTimer("core_unlock", respawnTime, null, null);
			// also save the respawn time so that the info is maintained past reboots
			StatsSet info = GrandBossManager.getInstance().getStatsSet(CORE);
			info.set("respawn_time", (System.currentTimeMillis() + respawnTime));
			GrandBossManager.getInstance().setStatsSet(CORE, info);
			startQuestTimer("despawn_minions", 20000, null, null);
		}
		else
		{
			status = GrandBossManager.getInstance().getBossStatus(CORE);
			if(status == ALIVE && Minions.contains(npc))
			{
				Minions.remove(npc);
				startQuestTimer("spawn_minion", Config.CORE_RESP_MINION * 1000, npc, null);
			}
		}
		return super.onKill(npc, killer, isPet);
	}

	public void spawnBoss(L2GrandBossInstance npc)
	{
		GrandBossManager.getInstance().addBoss(npc);
		npc.broadcastPacket(new PlaySound(1, "BS01_A", 1, npc.getObjectId(), npc.getX(), npc.getY(), npc.getZ()));
		//Spawn minions
		for(int i = 0; i < 5; i++)
		{
			int x = 16800 + i * 360;
			Minions.add((L2Attackable) addSpawn(DEATH_KNIGHT, x, 110000, npc.getZ(), 280 + Rnd.get(40), false, 0));
			Minions.add((L2Attackable) addSpawn(DEATH_KNIGHT, x, 109000, npc.getZ(), 280 + Rnd.get(40), false, 0));
			int x2 = 16800 + i * 600;
			Minions.add((L2Attackable) addSpawn(DOOM_WRAITH, x2, 109300, npc.getZ(), 280 + Rnd.get(40), false, 0));
		}
		for(int i = 0; i < 4; i++)
		{
			int x = 16800 + i * 450;
			Minions.add((L2Attackable) addSpawn(SUSCEPTOR, x, 110300, npc.getZ(), 280 + Rnd.get(40), false, 0));
		}
	}

	@Override
	public void run()
	{}
}