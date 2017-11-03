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
package custom.MeetBaium;

import com.it.br.gameserver.instancemanager.GrandBossManager;
import com.it.br.gameserver.model.actor.instance.L2NpcInstance;
import com.it.br.gameserver.model.actor.instance.L2PcInstance;
import com.it.br.gameserver.model.quest.Quest;
import com.it.br.gameserver.model.quest.QuestState;
import com.it.br.gameserver.model.quest.State;

public class MeetBaium extends Quest
{
	private static final int NPC_ID = 31862;

	private State CREATED;
	
	public MeetBaium(int questid, String name, String descr)
	{
		super(questid, name, descr);
		CREATED = new State("Start", this);
		this.setInitialState(CREATED);
		
		addStartNpc(NPC_ID);
		addFirstTalkId(NPC_ID);
		addTalkId(NPC_ID);
	}
	
		
	public String onFirstTalk(L2NpcInstance npc, L2PcInstance player)
	{
		QuestState st = player.getQuestState(getName());
		if(st == null)
			st = newQuestState(player);

		int baiumStatus = GrandBossManager.getInstance().getBossStatus(29020);
		if(baiumStatus != 2 && st.getQuestItemsCount(4295) == 1)
		{
			st.exitQuest(true);
			return "31862.htm";
		}
		else
		{
			npc.showChatWindow(player);
			st.exitQuest(true);
			return null;
		}
	}

	public static void main(String[] args)
	{
		new MeetBaium(8003, "MeetBaium", "custom");
	}
}