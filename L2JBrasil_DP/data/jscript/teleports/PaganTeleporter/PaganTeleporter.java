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
package teleports.PaganTeleporter;

import com.it.br.gameserver.model.actor.instance.L2NpcInstance;
import com.it.br.gameserver.model.actor.instance.L2PcInstance;
import com.it.br.gameserver.model.quest.Quest;
import com.it.br.gameserver.model.quest.QuestState;
import com.it.br.gameserver.model.quest.State;

public class PaganTeleporter extends Quest
{
	private static final int[] NPC = {32034,32036,32039,32040};
	private static final int[] ITEM = {8064,8065,8067};
	
	private State CREATED;

	public PaganTeleporter(int questid, String name, String descr)
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
	
	public String onTalk(L2NpcInstance npc, L2PcInstance player)
	{
		QuestState st = player.getQuestState(getName());
		int npcId = npc.getNpcId();
		String htmltext = "You have been teleported.";
		if(player.getLevel() < 73)
			htmltext = "<html><body>Teleport available only for characters with Pagans Mark and level 73 or above.</body></html>";
		else if(npcId == NPC[0] && st.getQuestItemsCount(ITEM[0]) >= 1)
		{
			st.takeItems(ITEM[0],1);
			htmltext = "<html><body>As you pass through the gates your mark fades. Make a note to return to Priest Flauron to inquire about this!</body></html>";
			st.giveItems(ITEM[1],1);
			player.teleToLocation(-16324,-37147,-10724);
		}
		else if(npcId == NPC[0] || npcId == NPC[1])
		{
			if(st.getQuestItemsCount(ITEM[2]) == 0)
				htmltext = "<html><body>Teleport available only for characters with Pagans Mark and level 73 or above.</body></html>";
			else
			{
				if(npcId == NPC[0])
					player.teleToLocation(-16324,-37147,-10724);
				else
					player.teleToLocation(-16324,-44638,-10724);
			}
		}
		else if(npcId == NPC[3] && st.getQuestItemsCount(ITEM[1]) >= 1)
			player.teleToLocation(36640,-51218,718);
		else if(st.getQuestItemsCount(ITEM[0]) == 0 && st.getQuestItemsCount(ITEM[2]) == 0)
			htmltext = "<html>Teleport available only for characters with Pagans Mark or Visitors Mark and level 73 or above.</body></html>";
		else
		{
			if(npcId == NPC[2])
				player.teleToLocation(-12241,-35884,-10856);
			else if(npcId == NPC[3])
				player.teleToLocation(36640,-51218,718);
		}
		st.exitQuest(true);
		return htmltext;
	}

	public static void main(String[] args)
	{
		new PaganTeleporter(1630, "PaganTeleporter", "teleports");
	}
}