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
package com.it.br.gameserver.ai.special.individual;

import java.util.Collection;

import com.it.br.gameserver.ai.CtrlIntention;
import com.it.br.gameserver.datatables.sql.SpawnTable;
import com.it.br.gameserver.model.L2Attackable;
import com.it.br.gameserver.model.L2CharPosition;
import com.it.br.gameserver.model.L2Spawn;
import com.it.br.gameserver.model.actor.instance.L2NpcInstance;
import com.it.br.gameserver.model.actor.instance.L2PcInstance;
import com.it.br.gameserver.model.quest.Quest;

/**
 * Gordon AI
 * @reworked *slayer
 * @author TOFIZ
 * @version $Revision: 1.1 $ $Date: 2008/08/21 $
 */
public class Gordon extends Quest implements Runnable
{
	private static final int GORDON = 29095;
	private static int _npcMoveX = 0;
	private static int _npcMoveY = 0;
	private static int _isWalkTo = 0;
	private static int _npcBlock = 0;
	private static int X = 0;
	private static int Y = 0;
	private static int Z = 0;
	private static final int[][] WALKS =
	{
		{ 141569, -45908, -2387	}, { 142494, -45456, -2397 }, { 142922, -44561, -2395 }, { 143672, -44130, -2398 },
		{ 144557, -43378, -2325 }, { 145839, -43267, -2301 }, { 147044, -43601, -2307 }, { 148140, -43206, -2303 },
		{ 148815, -43434, -2328	}, { 149862, -44151, -2558 }, { 151037, -44197, -2708 }, { 152555, -42756, -2836 },
		{ 154808, -39546, -3236 }, { 155333, -39962, -3272 }, { 156531, -41240, -3470 }, { 156863, -43232, -3707 },
		{ 156783, -44198, -3764 }, { 158169, -45163, -3541 }, { 158952, -45479, -3473 }, { 160039, -46514, -3634 },
		{ 160244, -47429, -3656 }, { 159155, -48109, -3665 }, { 159558, -51027, -3523 }, { 159396, -53362, -3244 },
		{ 160872, -56556, -2789 }, { 160857, -59072, -2613 }, { 60410, -59888, -2647 }, { 158770, -60173, -2673 },
		{ 156368, -59557, -2638 }, { 155188, -59868, -2642 }, { 154118, -60591, -2731 }, { 153571, -61567, -2821 },
		{ 153457, -62819, -2886 }, { 152939, -63778, -3003 }, { 151816, -64209, -3120 }, { 147655, -64826, -3433 },
		{ 145422, -64576, -3369 }, { 144097, -64320, -3404 }, { 140780, -61618, -3096 }, { 139688, -61450, -3062 },
		{ 138267, -61743, -3056 }, { 138613, -58491, -3465 }, { 138139, -57252, -3517 }, { 139555, -56044, -3310 },
		{ 139107, -54537, -3240 },{ 139279, -53781, -3091 }, { 139810, -52687, -2866 },{ 139657, -52041, -2793 },
		{ 139215, -51355, -2698 },{ 139334, -50514, -2594 }, { 139817, -49715, -2449 },{ 139824, -48976, -2263 },
		{ 140130, -47578, -2213 },{ 140483, -46339, -2382 }, { 141569, -45908, -2387 }
	};

	private static boolean _isAttacked = false;
	private static boolean _isSpawned = false;

	public Gordon(int id, String name, String descr)
	{
		super(id, name, descr);
		addEventId(GORDON, Quest.QuestEventType.ON_KILL);
		addEventId(GORDON, Quest.QuestEventType.ON_ATTACK);
		addEventId(GORDON, Quest.QuestEventType.ON_SPAWN);

		// wait 2 minutes after Start AI
		startQuestTimer("check_ai", 120000, null, null,true);

		_isSpawned = false;
		_isAttacked = false;
		_isWalkTo = 1;
		_npcMoveX = 0;
		_npcMoveY = 0;
		_npcBlock = 0;
	}

	@Override
	public String onAdvEvent(String event, L2NpcInstance npc, L2PcInstance player)
	{
		X = WALKS[_isWalkTo - 1][0];
		Y = WALKS[_isWalkTo - 1][1];
		Z = WALKS[_isWalkTo - 1][2];
		if(event == "time_isAttacked")
		{
			_isAttacked = false;
			if(npc.getNpcId() == GORDON)
			{
				npc.setWalking();
				npc.getAI().setIntention(CtrlIntention.AI_INTENTION_MOVE_TO, new L2CharPosition(X, Y, Z, 0));
			}
		}
		else if(event == "check_ai")
		{
			cancelQuestTimer("check_ai", null, null);
			if(_isSpawned == false)
			{
				L2NpcInstance gordon_ai = findTemplate(GORDON);
				if(gordon_ai != null)
				{
					_isSpawned = true;
					startQuestTimer("Start", 1000, gordon_ai, null,true);
					return super.onAdvEvent(event, npc, player);
				}
			}
		}
		else if(event == "Start")
		{
			startQuestTimer("Start", 1000, npc, null);
			if(npc != null && _isSpawned == true)
			{
				// check if player have Cursed Weapon and in radius
				if(npc.getNpcId() == GORDON)
				{
					Collection<L2PcInstance> chars = npc.getKnownList().getKnownPlayers().values();
					if(chars != null && chars.size() > 0)
					{
						for(L2PcInstance pc : chars)
						{
							if(pc.isCursedWeaponEquipped() && pc.isInsideRadius(npc, 5000, false, false))
							{
								npc.setRunning();
								((L2Attackable) npc).addDamageHate(pc, 0, 9999);
								npc.getAI().setIntention(CtrlIntention.AI_INTENTION_ATTACK, pc);
								_isAttacked = true;
								cancelQuestTimer("time_isAttacked", null, null);
								startQuestTimer("time_isAttacked", 180000, npc, null);
								return super.onAdvEvent(event, npc, player);
							}
						}
					}
				}
				// end check
				if(_isAttacked == true)
					return super.onAdvEvent(event, npc, player);

				if(npc.getNpcId() == GORDON && npc.getX() - 50 <= X && npc.getX() + 50 >= X && npc.getY() - 50 <= Y && npc.getY() + 50 >= Y)
				{
					_isWalkTo++;
					if(_isWalkTo > 55)
					{
						_isWalkTo = 1;
					}
					X = WALKS[_isWalkTo - 1][0];
					Y = WALKS[_isWalkTo - 1][1];
					Z = WALKS[_isWalkTo - 1][2];
					npc.setWalking();
					//TODO: find better way to prevent teleporting to the home location
					npc.getSpawn().setLocx(X);
					npc.getSpawn().setLocy(Y);
					npc.getSpawn().setLocz(Z);
					npc.getAI().setIntention(CtrlIntention.AI_INTENTION_MOVE_TO, new L2CharPosition(X, Y, Z, 0));
				}
				// Test for unblock Npc
				if(npc.getX() != _npcMoveX && npc.getY() != _npcMoveY)
				{
					_npcMoveX = npc.getX();
					_npcMoveY = npc.getY();
					_npcBlock = 0;
				}
				else if(npc.getNpcId() == GORDON)
				{
					_npcBlock++;
					if(_npcBlock > 2)
					{
						npc.teleToLocation(X, Y, Z);
						return super.onAdvEvent(event, npc, player);
					}
					if(_npcBlock > 0)
					{
						//TODO: find better way to prevent teleporting to the home location
						npc.getSpawn().setLocx(X);
						npc.getSpawn().setLocy(Y);
						npc.getSpawn().setLocz(Z);
						npc.getAI().setIntention(CtrlIntention.AI_INTENTION_MOVE_TO, new L2CharPosition(X, Y, Z, 0));
					}
				}
				// End Test unblock Npc
			}
		}
		return super.onAdvEvent(event, npc, player);
	}

	@Override
	public String onSpawn(L2NpcInstance npc)
	{
		if(npc.getNpcId() == GORDON && _npcBlock == 0)
		{
			_isSpawned = true;
			_isWalkTo = 1;
			startQuestTimer("Start", 1000, npc, null);
		}
		return super.onSpawn(npc);
	}

	@Override
	public String onAttack(L2NpcInstance npc, L2PcInstance player, int damage, boolean isPet)
	{
		if(npc.getNpcId() == GORDON)
		{
			_isAttacked = true;
			cancelQuestTimer("time_isAttacked", null, null);
			startQuestTimer("time_isAttacked", 180000, npc, null);
			if(player != null)
			{
				npc.setRunning();
				((L2Attackable) npc).addDamageHate(player, 0, 100);
				npc.getAI().setIntention(CtrlIntention.AI_INTENTION_ATTACK, player);
			}
		}
		return super.onAttack(npc, player, damage, isPet);
	}

	@Override
	public String onKill(L2NpcInstance npc, L2PcInstance killer, boolean isPet)
	{
		if(npc.getNpcId() == GORDON)
		{
			cancelQuestTimer("Start", null, null);
			cancelQuestTimer("time_isAttacked", null, null);
			_isSpawned = false;
		}
		return super.onKill(npc, killer, isPet);
	}

	public L2NpcInstance findTemplate(int npcId)
	{
		L2NpcInstance npc = null;
		for(L2Spawn spawn : SpawnTable.getInstance().getSpawnTable().values())
		{
			if(spawn != null && spawn.getNpcid() == npcId)
			{
				npc = spawn.getLastSpawn();
				break;
			}
		}
		return npc;
	}

	@Override
	public void run()
	{}
}