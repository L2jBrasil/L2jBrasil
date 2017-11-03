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
package teleports.ToiVortexExit;

import com.it.br.gameserver.model.actor.instance.L2NpcInstance;
import com.it.br.gameserver.model.actor.instance.L2PcInstance;
import com.it.br.gameserver.model.quest.Quest;
import com.it.br.gameserver.model.quest.QuestState;

public class ToiVortexExit extends Quest
{
	private final static int NPC = 29055;
	
	public ToiVortexExit(int questId, String name, String descr)
	{
		super(questId, name, descr);

		addStartNpc(NPC);
		addTalkId(NPC);
	}

	public String onTalk(L2NpcInstance npc, L2PcInstance player)
	{
		QuestState st = player.getQuestState(getName());
		int chance = st.getRandom(3);
		if (chance == 0)
		{
			int x = 108784 + st.getRandom(100);
			int y = 16000 + st.getRandom(100);
			int z = -4928;
			player.teleToLocation(x, y, z);
		}
		else if (chance == 1)
		{
			int x = 113824 + st.getRandom(100);
			int y = 10448 + st.getRandom(100);
			int z = -5164;
			player.teleToLocation(x, y, z);
		}
		else
		{
			int x = 115488 + st.getRandom(100);
			int y = 22096 + st.getRandom(100);
			int z = -5168;
			player.teleToLocation(x, y, z);
		}

		st.exitQuest(true);
		return null;
	}

	public static void main(String[] args)
	{
		new ToiVortexExit(2400, "ToiVortexExit", "teleports");
	}
}