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
package teleports.ElrokiTeleporters;

import com.it.br.gameserver.model.actor.instance.L2NpcInstance;
import com.it.br.gameserver.model.actor.instance.L2PcInstance;
import com.it.br.gameserver.model.quest.Quest;
import com.it.br.gameserver.model.quest.State;

public class ElrokiTeleporters extends Quest
{
	private static final int[] NPC = {32111,32112};

	private State CREATED;
	
	public ElrokiTeleporters(int questId, String name, String descr)
	{
		super(questId, name, descr);
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
		int npcId = npc.getNpcId();
		if(npcId == NPC[0])
			player.teleToLocation(4990,-1879,-3178);
		if(npcId == NPC[1])
			player.teleToLocation(7557,-5513,-3221);

		return null;
	}

	public static void main(String[] args)
	{
		new ElrokiTeleporters(6111, "ElrokiTeleporters", "teleports");
	}
}