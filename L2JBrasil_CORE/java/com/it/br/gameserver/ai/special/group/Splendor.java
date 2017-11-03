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
package com.it.br.gameserver.ai.special.group;

import com.it.br.Config;
import com.it.br.gameserver.ai.CtrlIntention;
import com.it.br.gameserver.model.L2Attackable;
import com.it.br.gameserver.model.actor.instance.L2NpcInstance;
import com.it.br.gameserver.model.actor.instance.L2PcInstance;
import com.it.br.gameserver.model.quest.Quest;
import com.it.br.util.Rnd;

import java.util.HashMap;
import java.util.Map;

public class Splendor extends Quest
{
	private static boolean AlwaysSpawn;

	private static Map<Integer, int[]> SplendorId = new HashMap<>();

	public Splendor(int questId, String name, String descr)
	{
		super(questId, name, descr);

		AlwaysSpawn = false;

		SplendorId.put(21521, new int[] {21522,5,1});
		SplendorId.put(21524, new int[] {21525,5,1});
		SplendorId.put(21527, new int[] {21528,5,1});
		SplendorId.put(21537, new int[] {21538,5,1});
		SplendorId.put(21539, new int[] {21540,100,2});

		for(int NPC_ID: SplendorId.keySet())
		{
			addEventId(NPC_ID, Quest.QuestEventType.ON_ATTACK);
			addEventId(NPC_ID, Quest.QuestEventType.ON_KILL);
		}
	}

	@Override
	public String onAttack (L2NpcInstance npc, L2PcInstance attacker, int damage, boolean isPet)
	{
		int npcId = npc.getNpcId();
		int NewMob = SplendorId.get(npcId)[0];
		int chance = SplendorId.get(npcId)[1];
		int ModeSpawn = SplendorId.get(npcId)[2];
		if(Rnd.get(100) <= chance * Config.RATE_DROP_QUEST)
		{
			if(SplendorId.containsKey(npcId))
			{
				if(ModeSpawn == 1)
				{
					npc.deleteMe();
					L2Attackable newNpc = (L2Attackable) addSpawn(NewMob, npc);
					newNpc.addDamageHate(attacker,0,999);
					newNpc.getAI().setIntention(CtrlIntention.AI_INTENTION_ATTACK, attacker);
				}
				else if(AlwaysSpawn)
				{
					return super.onAttack(npc, attacker, damage, isPet);
				}
				else if(ModeSpawn == 2)
				{
					AlwaysSpawn = true;
					L2Attackable newNpc1 = (L2Attackable) addSpawn(NewMob, npc);
					newNpc1.addDamageHate(attacker,0,999);
					newNpc1.getAI().setIntention(CtrlIntention.AI_INTENTION_ATTACK, attacker);
				}
			}
		}

		return super.onAttack(npc, attacker, damage, isPet);
	}

	@Override
	public String onKill(L2NpcInstance npc, L2PcInstance killer, boolean isPet)
	{
		int npcId = npc.getNpcId();
		int ModeSpawn = SplendorId.get(npcId)[2];
		if(SplendorId.containsKey(npcId))
		{
			if(ModeSpawn == 2)
			{
				AlwaysSpawn = false;
			}
		}

		return super.onKill(npc,killer,isPet);
	}

}