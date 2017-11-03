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

package village_master.Alliance;

import com.it.br.gameserver.model.actor.instance.L2NpcInstance;
import com.it.br.gameserver.model.actor.instance.L2PcInstance;
import com.it.br.gameserver.model.quest.Quest;
import com.it.br.gameserver.model.quest.QuestState;
import com.it.br.gameserver.model.quest.State;

public class Alliance extends Quest
{
	private final static int[] NPC =
	{
		30026,30031,30037,30066,30070,30109,30115,
		30120,30154,30174,30175,30176,30187,30191,
		30195,30288,30289,30290,30297,30358,30373,
		30462,30474,30498,30499,30500,30503,30504,
		30505,30508,30511,30512,30513,30520,30525,
		30565,30594,30595,30676,30677,30681,30685,
		30687,30689,30694,30699,30704,30845,30847,
		30849,30854,30857,30862,30865,30894,30897,
		30900,30905,30910,30913,31269,31272,31276,
		31279,31285,31288,31314,31317,31321,31324,
		31326,31328,31331,31334,31755,31958,31961,
		31965,31968,31974,31977,31996,32092,32093,
		32094,32095,32096,32097,32098
	};

	private State CREATED;
	private State STARTED;
	//private State COMPLETED;

	public Alliance(int questId, String name, String descr)
	{
		super(questId, name, descr);
		CREATED = new State("Start", this);
		STARTED = new State("Started", this);
		//COMPLETED = new State("Completed", this);
		this.setInitialState(CREATED);
		
		for(int NPC_ID : NPC)
		{
			addStartNpc(NPC_ID);
			addTalkId(NPC_ID);
		}
	}

	public String onEvent(String event, QuestState st)
	{
		boolean ClanLeader = st.getPlayer().isClanLeader();
		int Clan = st.getPlayer().getClanId();
		String htmltext = event;
		if(event.equalsIgnoreCase("9001-01.htm"))
		{
			htmltext = "9001-01.htm";
		}
		else if(Clan == 0)
		{
			st.exitQuest(true);
			htmltext = "<html><body>You must be in Clan.</body></html";
		}
		else if(Clan != 0 && ClanLeader == false)
		{
			st.exitQuest(true);
			htmltext = "<html><body>You must be Clan Leader.</body></html";
		}
		else if(event.equalsIgnoreCase("9001-02.htm"))
		{
			htmltext = "9001-02.htm";
		}
		return htmltext;
	}

	public String onTalk(L2NpcInstance npc, L2PcInstance talker)
	{
		String htmltext = ""; 
		QuestState st = talker.getQuestState(getName());
		int npcId = npc.getNpcId();
		for(int NPC_ID : NPC)
		{
			if(npcId == NPC_ID)
			{
				st.set("cond","0");
				st.setState(STARTED);
				htmltext = "9001-01.htm";
			}
		}
		return htmltext;
	}

	public static void main(String[] args)
	{
		new Alliance(9001, "Alliance", "village_master");    	
	}
}