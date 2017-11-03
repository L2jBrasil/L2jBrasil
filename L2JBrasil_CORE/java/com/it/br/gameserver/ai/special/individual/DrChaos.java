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

import java.util.Map;

import com.it.br.gameserver.ai.CtrlIntention;
import com.it.br.gameserver.datatables.sql.SpawnTable;
import com.it.br.gameserver.model.L2CharPosition;
import com.it.br.gameserver.model.L2Spawn;
import com.it.br.gameserver.model.actor.instance.L2NpcInstance;
import com.it.br.gameserver.model.actor.instance.L2PcInstance;
import com.it.br.gameserver.model.quest.Quest;
import com.it.br.gameserver.network.serverpackets.NpcSay;
import com.it.br.gameserver.network.serverpackets.PlaySound;
import com.it.br.gameserver.network.serverpackets.SocialAction;
import com.it.br.gameserver.network.serverpackets.SpecialCamera;

public class DrChaos extends Quest
{
	private static final int DOCTOR_CHAOS = 32033;
	private static final int STRANGE_MACHINE = 32032;
	private static final int CHAOS_GOLEM = 25512;
	private static boolean _IsGolemSpawned;

	public DrChaos(int questId, String name, String descr)
	{
		super(questId, name, descr);
		addFirstTalkId(32033);
		_IsGolemSpawned = false;
	}

	public L2NpcInstance FindTemplate(int npcId)
	{
		L2NpcInstance npcInstance = null;
		L2Spawn spawn;
		Map<Integer,L2Spawn> values = SpawnTable.getInstance().getSpawnTable();

		for(int i = 0;i<values.size();i++)
		{
			spawn = values.get(i);

			if(spawn != null && spawn.getNpcid() == npcId)
			{
				npcInstance = spawn.getLastSpawn();
				break;
			}
		}

		return npcInstance;
	}

	@Override
	public String onAdvEvent (String event, L2NpcInstance npc, L2PcInstance player)
	{
		if(event.equalsIgnoreCase("Chat"))
		{
			npc.broadcastPacket(new NpcSay(npc.getObjectId(), 0, npc.getNpcId(), "Fools! Why haven't you fled yet? Prepare you learn a lesson!"));
			startQuestTimer("1", 3000, npc, player);
		}
		else if(event.equalsIgnoreCase("1"))
		{
			L2NpcInstance machine_instance = FindTemplate(STRANGE_MACHINE);

			if(machine_instance != null)
			{
				npc.getAI().setIntention(CtrlIntention.AI_INTENTION_ATTACK, machine_instance);
				machine_instance.broadcastPacket(new SpecialCamera(machine_instance.getObjectId(), 1, -200, 15, 10000, 20000));
			}
			else
			{
				startQuestTimer("2", 2000, npc, player);
			}

			startQuestTimer("3", 10000, npc, player);
		}
		else if(event.equalsIgnoreCase("2"))
		{
			npc.broadcastPacket(new SocialAction(npc.getObjectId(),3));
		}
		else if(event.equalsIgnoreCase("3"))
		{
			npc.broadcastPacket(new SpecialCamera(npc.getObjectId(), 1, -150, 10, 3000, 20000));
			startQuestTimer("4", 2500, npc, player);
		}
		else if(event.equalsIgnoreCase("4"))
		{
			npc.getAI().setIntention(CtrlIntention.AI_INTENTION_MOVE_TO, new L2CharPosition(96055, -110759, -3312, 0));
			startQuestTimer("5", 2000, npc, player);
		}
		else if(event.equalsIgnoreCase("5"))
		{
			player.teleToLocation(94832, -112624, -3304);
			npc.teleToLocation(-113091, -243942, -15536);

			if(!_IsGolemSpawned)
			{
				L2NpcInstance golem = addSpawn(CHAOS_GOLEM, 94640, -112496, -3336, 0, false, 0);
				_IsGolemSpawned = true;
				startQuestTimer("6", 1000, golem, player);
				player.sendPacket(new PlaySound(1, "Rm03_A", 0, 0, 0, 0, 0));
			}
		}
		else if(event.equalsIgnoreCase("6"))
		{
			npc.broadcastPacket(new SpecialCamera(npc.getObjectId(), 30, -200, 20, 6000, 8000));
		}

		return super.onAdvEvent(event, npc, player);
	}

	@Override
	public String onFirstTalk (L2NpcInstance npc, L2PcInstance player)
	{
		if(npc.getNpcId() == DOCTOR_CHAOS)
		{
			npc.broadcastPacket(new NpcSay(npc.getObjectId(), 0, npc.getNpcId(), "How dare you trespass into my territory? Have you no fear?"));
			npc.getAI().setIntention(CtrlIntention.AI_INTENTION_MOVE_TO, new L2CharPosition(96323, -110914, -3328, 0));
			this.startQuestTimer("Chat", 3000, npc, player);
		}

		return null;
	}

}