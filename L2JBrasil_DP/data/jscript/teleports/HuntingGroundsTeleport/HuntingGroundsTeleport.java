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
package teleports.HuntingGroundsTeleport;

import com.it.br.gameserver.model.actor.instance.L2NpcInstance;
import com.it.br.gameserver.model.actor.instance.L2PcInstance;
import com.it.br.gameserver.model.quest.Quest;

public class HuntingGroundsTeleport extends Quest
{
	private static final int[] NPC =
	{
		31078,31079,31080,31081,31082,31083,31084,31085,31086,31087,31088,31089,31090,31091,//type 1
		31168,31169,//type 2
		31692,31963,31964,31695,//type 3
		31997,31998//type 4
	};

	public HuntingGroundsTeleport(int questid, String name, String descr)
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
		String htmltext = "";
		int npcId = npc.getNpcId();
		if(npcId == NPC[0] || npcId == NPC[7])
			htmltext = "hg_gludin.htm";
		else if(npcId == NPC[1] || npcId == NPC[8])
			htmltext = "hg_gludio.htm";
		else if(npcId == NPC[2] || npcId == NPC[9])
			htmltext = "hg_dion.htm";
		else if(npcId == NPC[3] || npcId == NPC[10])
			htmltext = "hg_giran.htm";
		else if(npcId == NPC[4] || npcId == NPC[11])
			htmltext = "hg_heine.htm";
		else if(npcId == NPC[5] || npcId == NPC[12])
			htmltext = "hg_oren.htm";
		else if(npcId == NPC[6] || npcId == NPC[13])
			htmltext = "hg_aden.htm";
		else if(npcId == NPC[14] || npcId == NPC[15])
			htmltext = "hg_hw.htm";
		else if(npcId == NPC[16] || npcId == NPC[17])
			htmltext = "hg_goddard.htm";
		else if(npcId == NPC[18] || npcId == NPC[19])
			htmltext = "hg_rune.htm";
		else if(npcId == NPC[20] || npcId == NPC[21])
			htmltext = "hg_schuttgart.htm";
		else
			htmltext = "hg_wrong.htm";

		return htmltext;
	}

	public static void main(String[] args)
	{
		new HuntingGroundsTeleport(2211, "HuntingGroundsTeleport", "teleports");
	}
}