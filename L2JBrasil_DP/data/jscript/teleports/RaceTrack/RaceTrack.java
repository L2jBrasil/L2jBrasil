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
package teleports.RaceTrack;
import java.util.HashMap;
import java.util.Map;

import com.it.br.gameserver.model.actor.instance.L2NpcInstance;
import com.it.br.gameserver.model.actor.instance.L2PcInstance;
import com.it.br.gameserver.model.quest.Quest;
import com.it.br.gameserver.model.quest.QuestState;
import com.it.br.gameserver.model.quest.State;

public class RaceTrack extends Quest
{
	private final static int RACE_MANAGER = 30995;
	private static Map<Integer, Integer> data = new HashMap<>();
	private final static int[] TELEPORT_NPCs =
	{
		30320, 30256, 30059, 30080, 30899, 30177, 30848, 30233, 31320, 31275, 30727, 30836, 31964, 31210
	};
	private final static int[][] RETURN_LOCS =
	{
		{-80826,149775,-3043},{-12672,122776,-3116},{15670,142983,-2705},
		{83400,147943,-3404},{43835,-47749,-792},{147930,-55281,-2728},
		{85335,16177,-3694},{105857,109763,-3202},{87386,-143246,-1293},{12882,181053,-3560}
	};
	
	private State CREATED;
	private State STARTED;

	public RaceTrack(int questId, String name, String descr)
	{
		super(questId, name, descr);
		CREATED = new State("Start", this);
		STARTED = new State("Started", this);
		this.setInitialState(CREATED);
		addTalkId(RACE_MANAGER);
		for (int id : TELEPORT_NPCs)
		{
			addStartNpc(id);
			addTalkId(id);
		}
		data.put(30059,3); // TRISHA
		data.put(30080,4); // CLARISSA
		data.put(30177,6); // VALENTIA
		data.put(30233,8); // ESMERALDA
		data.put(30256,2); // BELLA
		data.put(30320,1); // RICHLIN
		data.put(30848,7); // ELISA
		data.put(30899,5); // FLAUEN
		data.put(31320,9); // ILYANA
		data.put(31275,10); // TATIANA
		data.put(30727,11); // VERONA
		data.put(30836,12); // MINERVA
		data.put(31964,13); // BILIA
		data.put(31210,14); // RACE TRACK GK
	}

	@Override
	public String onTalk(L2NpcInstance npc, L2PcInstance player)
	{
		QuestState st = player.getQuestState(getName());
		int npcId = npc.getNpcId();
		if (data.containsKey(npcId))
		{
			player.teleToLocation(12661,181687,-3560);
			st.setState(STARTED);
			st.set("id",String.valueOf(data.get(npcId)));
		}
		else if(st.getState() == STARTED && npcId == RACE_MANAGER)
		{
			// back to start location
			int return_id = st.getInt("id") - 1;
			player.teleToLocation(RETURN_LOCS[return_id][0],RETURN_LOCS[return_id][1],RETURN_LOCS[return_id][2]);
			st.exitQuest(true);
		}
		return null;
	}

	public static void main(String[] args)
	{
		new RaceTrack(1101, "RaceTrack", "teleports");
	}
}