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
package teleports.TeleportWithCharm;

import com.it.br.gameserver.model.actor.instance.L2NpcInstance;
import com.it.br.gameserver.model.actor.instance.L2PcInstance;
import com.it.br.gameserver.model.quest.Quest;
import com.it.br.gameserver.model.quest.QuestState;

public class TeleportWithCharm extends Quest
{
	private static final int[]CHARM = {1658, 1659};
	private static final int[] NPC = {30540, 30576};

	public TeleportWithCharm(int questid, String name, String descr)
	{
		super(questid, name, descr);
		
		for(int NPC_ID : NPC)
		{
			addStartNpc(NPC_ID);
			addTalkId(NPC_ID);
		}
	}
	
	public String onTalk(L2NpcInstance npc, L2PcInstance player)
	{
		QuestState st = player.getQuestState(getName());
		String htmltext = "";
		int npcId = npc.getNpcId();
		if(npcId == NPC[1])
		{
			if(st.getQuestItemsCount(CHARM[0]) >= 1)
			{
				st.takeItems(CHARM[0],1);
				player.teleToLocation(-80826,149775,-3043);
			}
			else
			{

				htmltext = "30576-01.htm";
			}
		}
		else if(npcId == NPC[0])
		{
			if(st.getQuestItemsCount(CHARM[1]) >= 1)
			{
				st.takeItems(CHARM[1],1);
				player.teleToLocation(-80826,149775,-3043);
			}
			else
			{
				htmltext = "30540-01.htm";
			}
		}
		return htmltext;
	}

	public static void main(String[] args)
	{
		new TeleportWithCharm(1100, "TeleportWithCharm", "teleports");
	}
}