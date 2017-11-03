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
package com.it.br.gameserver.ai.special.group;

import com.it.br.gameserver.datatables.sql.SkillTable;
import com.it.br.gameserver.model.actor.instance.L2NpcInstance;
import com.it.br.gameserver.model.actor.instance.L2PcInstance;
import com.it.br.gameserver.model.quest.Quest;

public class AncientEgg extends Quest
{
	private static final int NPC = 18344;

	public AncientEgg(int questId, String name, String descr)
	{
		super(questId, name, descr);

		addEventId(NPC, Quest.QuestEventType.ON_ATTACK);
	}

	@Override
	public String onAttack (L2NpcInstance npc, L2PcInstance attacker, int damage, boolean isPet)
	{
		if(npc.getNpcId() == NPC)
		{
			npc.setTarget(attacker);
			npc.doCast(SkillTable.getInstance().getInfo(5088,1));
		}
		return super.onAttack(npc, attacker, damage, isPet);
	}

}