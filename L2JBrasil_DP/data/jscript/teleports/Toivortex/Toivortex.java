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
package teleports.Toivortex;

import com.it.br.gameserver.model.actor.instance.L2NpcInstance;
import com.it.br.gameserver.model.actor.instance.L2PcInstance;
import com.it.br.gameserver.model.quest.Quest;
import com.it.br.gameserver.model.quest.QuestState;

public class Toivortex extends Quest
{
	private static final int[] DIMENSION_STONE = {4401, 4402,4403};
	private static final int[] DIMENSION_VORTEX = {30952, 30953,30954};
	
	public Toivortex(int questid, String name, String descr)
	{
		super(questid, name, descr);
		
		for(int NPC_ID : DIMENSION_VORTEX)
		{
			addStartNpc(NPC_ID);
			addFirstTalkId(NPC_ID);
			addTalkId(NPC_ID);
		}
	}
	
	public String onAdvEvent (String event, L2NpcInstance npc, L2PcInstance player)
	{
		QuestState st = player.getQuestState(getName());
		String htmltext = "";
		int npcId = npc.getNpcId();

		if(event.equalsIgnoreCase("tele_green"))
		{
			if(npcId == DIMENSION_VORTEX[1] || npcId == DIMENSION_VORTEX[2])
				if(st.getQuestItemsCount(DIMENSION_STONE[0]) >= 1)
				{
					st.takeItems(DIMENSION_STONE[0],1);
					player.teleToLocation(110930,15963,-4378);
				}
				else
				{
					htmltext ="<html><body>Dimensional Vortex:<br>You do not have the proper stones needed for teleport.</body></html>";
				}
		}
		else if(event.equalsIgnoreCase("tele_red"))
		{
			if(npcId == DIMENSION_VORTEX[0] || npcId == DIMENSION_VORTEX[1])
				if(st.getQuestItemsCount(DIMENSION_STONE[2]) >= 1)
				{
					st.takeItems(DIMENSION_STONE[2],1);
					player.teleToLocation(118558,16659,5987);
				}
				else
				{
					htmltext = "<html><body>Dimensional Vortex:<br>You do not have the proper stones needed for teleport.</body></html>";
				}
		}
		else if(event.equalsIgnoreCase("tele_blue"))
		{
			if(npcId == DIMENSION_VORTEX[0] || npcId == DIMENSION_VORTEX[2])
				if(st.getQuestItemsCount(DIMENSION_STONE[1]) >= 1)
				{
					st.takeItems(DIMENSION_STONE[1],1);
					player.teleToLocation(114097,19935,935);
				}
				else
				{
					htmltext = "<html><body>Dimensional Vortex:<br>You do not have the proper stones needed for teleport.</body></html>";
				}
		}
		return htmltext;
	}

	public String onFirstTalk(L2NpcInstance npc, L2PcInstance player)
	{
		QuestState st = player.getQuestState(getName());
		if(st == null)
			st = newQuestState(player);
		String htmltext = "";
		int npcId = npc.getNpcId();
		if(npcId == DIMENSION_VORTEX[0])
			htmltext = "30952.htm";
		else if(npcId == DIMENSION_VORTEX[1])
			htmltext = "30953.htm";
		else if(npcId == DIMENSION_VORTEX[2])
			htmltext = "30954.htm";
		return htmltext;
	}

	public static void main(String[] args)
	{
		new Toivortex(1102, "Toivortex", "teleports");
	}
}