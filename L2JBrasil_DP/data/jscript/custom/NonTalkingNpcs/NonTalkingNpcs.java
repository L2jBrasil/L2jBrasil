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
package custom.NonTalkingNpcs;

import com.it.br.gameserver.model.actor.instance.L2NpcInstance;
import com.it.br.gameserver.model.actor.instance.L2PcInstance;
import com.it.br.gameserver.model.quest.Quest;
import com.it.br.gameserver.network.serverpackets.ActionFailed;

public final class NonTalkingNpcs extends Quest {
	private static final int[] NPCS = { 31557, 31671, 31672, 31673, 31674, 32026, 32030, 32031, 32032 };

	public NonTalkingNpcs(int questId, String name, String descr) {
		super(questId, name, descr);

		for (int i : NPCS)
			addFirstTalkId(i);
	}

	@Override
	public final String onFirstTalk(L2NpcInstance npc, L2PcInstance player) {
		player.sendPacket(ActionFailed.STATIC_PACKET);
		return "";
	}

	public static void main(String[] args) {
		new NonTalkingNpcs(-1, "NonTalkingNPCs", "custom");
	}
}