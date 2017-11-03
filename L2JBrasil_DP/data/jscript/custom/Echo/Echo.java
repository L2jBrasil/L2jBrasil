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
package custom.Echo;

import com.it.br.gameserver.model.actor.instance.L2NpcInstance;
import com.it.br.gameserver.model.actor.instance.L2PcInstance;
import com.it.br.gameserver.model.quest.Quest;
import com.it.br.gameserver.model.quest.QuestState;
import com.it.br.gameserver.model.quest.State;

public class Echo extends Quest
{
	private static final int[] NPC = {31042, 31043};
	private static final int[] SCORE = {4410,4409,4408,4420,4421,4419,4418};
	private static final int[] CRYSTAL = {4411,4412,4413,4414,4415,4417,4416};

	private State CREATED;
	
	public Echo(int questid, String name, String descr)
	{
		super(questid, name, descr);
		CREATED = new State("Start", this);
		this.setInitialState(CREATED);
		for(int NPC_ID : NPC)
		{
			addStartNpc(NPC_ID);
			addTalkId(NPC_ID);
		}
	}

	public String onAdvEvent(String event, L2NpcInstance npc, L2PcInstance player)
	{
		QuestState st = player.getQuestState(getName());
		String htmltext = "";
		if(st != null)
		{
			if(event.equalsIgnoreCase(String.valueOf(SCORE[0])))
			{
				if(st.getQuestItemsCount(SCORE[0]) == 0)
					htmltext = String.valueOf(npc.getNpcId()) + "-03.htm";
				else if(st.getQuestItemsCount(57) < 200)
					htmltext = String.valueOf(npc.getNpcId()) + "-02.htm";
				else
				{
					st.takeItems(57,200);
					st.giveItems(CRYSTAL[0],1);
					htmltext = String.valueOf(npc.getNpcId()) + "-01.htm";
				}
			}
			else if(event.equalsIgnoreCase(String.valueOf(SCORE[1])))
			{
				if(st.getQuestItemsCount(SCORE[1]) == 0)
					htmltext = String.valueOf(npc.getNpcId()) + "-06.htm";
				else if(st.getQuestItemsCount(57) < 200)
					htmltext = String.valueOf(npc.getNpcId()) + "-05.htm";
				else
				{
					st.takeItems(57,200);
					st.giveItems(CRYSTAL[1],1);
					htmltext = String.valueOf(npc.getNpcId()) + "-04.htm";
				}
			}
			else if(event.equalsIgnoreCase(String.valueOf(SCORE[2])))
			{
				if(st.getQuestItemsCount(SCORE[2]) == 0)
					htmltext = String.valueOf(npc.getNpcId()) + "-09.htm";
				else if(st.getQuestItemsCount(57) < 200)
					htmltext = String.valueOf(npc.getNpcId()) + "-08.htm";
				else
				{
					st.takeItems(57,200);
					st.giveItems(CRYSTAL[2],1);
					htmltext = String.valueOf(npc.getNpcId()) + "-07.htm";
				}
			}
			else if(event.equalsIgnoreCase(String.valueOf(SCORE[3])))
			{
				if(st.getQuestItemsCount(SCORE[3]) == 0)
					htmltext = String.valueOf(npc.getNpcId()) + "-12.htm";
				else if(st.getQuestItemsCount(57) < 200)
					htmltext = String.valueOf(npc.getNpcId()) + "-11.htm";
				else
				{
					st.takeItems(57,200);
					st.giveItems(CRYSTAL[3],1);
					htmltext = String.valueOf(npc.getNpcId()) + "-10.htm";
				}
			}
			else if(event.equalsIgnoreCase(String.valueOf(SCORE[4])))
			{
				if(st.getQuestItemsCount(SCORE[4]) == 0)
					htmltext = String.valueOf(npc.getNpcId()) + "-15.htm";
				else if(st.getQuestItemsCount(57) < 200)
					htmltext = String.valueOf(npc.getNpcId()) + "-14.htm";
				else
				{
					st.takeItems(57,200);
					st.giveItems(CRYSTAL[4],1);
					htmltext = String.valueOf(npc.getNpcId()) + "-13.htm";
				}
			}
			else if(event.equalsIgnoreCase(String.valueOf(SCORE[5])))
			{
				if(st.getQuestItemsCount(SCORE[5]) == 0)
					htmltext = String.valueOf(npc.getNpcId()) + "-06.htm";
				else if(st.getQuestItemsCount(57) < 200)
					htmltext = String.valueOf(npc.getNpcId()) + "-05.htm";
				else
				{
					st.takeItems(57,200);
					st.giveItems(CRYSTAL[5],1);
					htmltext = String.valueOf(npc.getNpcId()) + "-16.htm";
				}
			}
			else if(event.equalsIgnoreCase(String.valueOf(SCORE[6])))
			{
				if(st.getQuestItemsCount(SCORE[6]) == 0)
					htmltext = String.valueOf(npc.getNpcId()) + "-06.htm";
				else if(st.getQuestItemsCount(57) < 200)
					htmltext = String.valueOf(npc.getNpcId()) + "-05.htm";
				else
				{
					st.takeItems(57,200);
					st.giveItems(CRYSTAL[6],1);
					htmltext = String.valueOf(npc.getNpcId()) + "-17.htm";
				}
			}	
			st.exitQuest(true);
			return htmltext;
		}
		else
			return null;
	}

	public String onTalk(L2NpcInstance npc, L2PcInstance player)
	{
		return "1.htm";
	}

	public static void main(String[] args)
	{
		new Echo(3995, "Echo", "custom");
	}
}