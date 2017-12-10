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
package teleports.NoblesseTeleport;

import com.it.br.gameserver.model.actor.instance.L2NpcInstance;
import com.it.br.gameserver.model.actor.instance.L2PcInstance;
import com.it.br.gameserver.model.quest.Quest;
import com.it.br.gameserver.model.quest.State;

public class NoblesseTeleport extends Quest {
	private static final int[] NPC = { 30006, 30059, 30080, 30134, 30146, 30177, 30233, 30256, 30320, 30540, 30576,
			30836, 30848, 30878, 30899, 31275, 31320, 31964 };
	private State CREATED;

	public NoblesseTeleport(int questid, String name, String descr) {
		super(questid, name, descr);
		CREATED = new State("Start", this);
		this.setInitialState(CREATED);

		for (int NPC_ID : NPC) {
			addStartNpc(NPC_ID);
			addTalkId(NPC_ID);
		}
	}

	@Override
	public String onTalk(L2NpcInstance npc, L2PcInstance player) {
		if (player.isNoble())
			return "noble.htm";

		return "nobleteleporter-no.htm";
	}

	public static void main(String[] args) {
		new NoblesseTeleport(2000, "NoblesseTeleport", "teleports");
	}
}