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

import com.it.br.gameserver.model.actor.instance.L2NpcInstance;
import com.it.br.gameserver.model.actor.instance.L2PcInstance;
import com.it.br.gameserver.model.quest.Quest;
import com.it.br.util.Rnd;

public class ScarletStokateNoble extends Quest
{
	private static final int NPC[] = {21378,21652};

	public ScarletStokateNoble(int questId, String name, String descr)
	{
		super(questId, name, descr);

		addEventId(NPC[0], Quest.QuestEventType.ON_KILL);
	}

	@Override
	public String onKill(L2NpcInstance npc, L2PcInstance killer, boolean isPet)
	{
		if(npc.getNpcId() == NPC[0])
		{
			if(Rnd.get(100) <= 20)
			{
				addSpawn(NPC[1],npc.getX(), npc.getY(), npc.getZ(),npc.getHeading(),true,0);
				addSpawn(NPC[1],npc.getX(), npc.getY(), npc.getZ(),npc.getHeading(),true,0);
				addSpawn(NPC[1],npc.getX(), npc.getY(), npc.getZ(),npc.getHeading(),true,0);
				addSpawn(NPC[1],npc.getX(), npc.getY(), npc.getZ(),npc.getHeading(),true,0);
				addSpawn(NPC[1],npc.getX(), npc.getY(), npc.getZ(),npc.getHeading(),true,0);
			}
		}
		return super.onKill(npc,killer,isPet);
	}

}