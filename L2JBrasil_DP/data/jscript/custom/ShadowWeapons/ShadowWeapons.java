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
package custom.ShadowWeapons;

import com.it.br.gameserver.model.actor.instance.L2NpcInstance;
import com.it.br.gameserver.model.actor.instance.L2PcInstance;
import com.it.br.gameserver.model.quest.Quest;
import com.it.br.gameserver.model.quest.QuestState;
import com.it.br.gameserver.model.quest.State;

public class ShadowWeapons extends Quest
{
	private static final int[] NPC =
	{
		30026,30037,30066,30070,30109,30115,30120,30174,30175,30176,30187,30191,
		30195,30288,30289,30290,30297,30373,30462,30474,30498,30499,30500,30503,
		30504,30505,30511,30512,30513,30676,30677,30681,30685,30687,30689,30694,
		30699,30704,30845,30847,30849,30854,30857,30862,30865,30894,30897,30900,
		30905,30910,30913,31269,31272,31288,31314,31317,31321,31324,31326,31328,
		31331,31334,31336,31965,31974,31276,31285,31996,32094,32096,32098
	};

	private static final int[] ITEM = {8869, 8870};

	private State CREATED;
	
	public ShadowWeapons(int questid, String name, String descr)
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
		String htmltext = "";
		QuestState st = player.getQuestState(getName());
		int has_d = st.getQuestItemsCount(ITEM[0]);
		int has_c = st.getQuestItemsCount(ITEM[1]);
		if(has_d != 0 || has_c != 0)
		{
			int multisell = 306893003;
			if(has_d == 0)
				multisell=306893002;
			else if(has_c == 0)
				multisell=306893001;
			htmltext = st.showHtmlFile("exchange.htm").replace("%msid%",String.valueOf(multisell));
		}
		else
		{
			htmltext = "exchange-no.htm";
			st.exitQuest(true);
		}
		return htmltext;
	}

	public static void main(String[] args)
	{
		new ShadowWeapons(4000, "ShadowWeapons", "custom");
	}
}