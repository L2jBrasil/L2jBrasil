package quests.Q036_MakeASewingKit;

import com.it.br.gameserver.model.actor.instance.L2NpcInstance;
import com.it.br.gameserver.model.actor.instance.L2PcInstance;
import com.it.br.gameserver.model.quest.Quest;
import com.it.br.gameserver.model.quest.QuestState;
import com.it.br.gameserver.model.quest.State;

public class Q036_MakeASewingKit extends Quest {
	private static final String qn = "Q036_MakeASewingKit";

	// Items
	private static final int REINFORCED_STEEL = 7163;
	private static final int ARTISANS_FRAME = 1891;
	private static final int ORIHARUKON = 1893;

	// Reward
	private static final int SEWING_KIT = 7078;

	private State CREATED;
	private State STARTED;
	private State COMPLETED;

	public Q036_MakeASewingKit(int questId, String name, String descr) {
		super(questId, name, descr);
		CREATED = new State("Start", this);
		STARTED = new State("Started", this);
		COMPLETED = new State("Completed", this);
		this.setInitialState(CREATED);
		questItemIds(REINFORCED_STEEL);

		addStartNpc(30847); // Ferris
		addTalkId(30847);

		addKillId(20566); // Iron Golem
	}

	@Override
	public String onAdvEvent(String event, L2NpcInstance npc, L2PcInstance player) {
		String htmltext = event;
		QuestState st = player.getQuestState(qn);
		if (st == null)
			return htmltext;

		if (event.equalsIgnoreCase("30847-1.htm")) {
			st.setState(STARTED);
			st.set("cond", "1");
			st.playSound(SOUND_QUEST_START);
		} else if (event.equalsIgnoreCase("30847-3.htm")) {
			st.set("cond", "3");
			st.playSound(SOUND_QUEST_MIDDLE);
			st.takeItems(REINFORCED_STEEL, 5);
		} else if (event.equalsIgnoreCase("30847-5.htm")) {
			if (st.getQuestItemsCount(ORIHARUKON) >= 10 && st.getQuestItemsCount(ARTISANS_FRAME) >= 10) {
				st.takeItems(ARTISANS_FRAME, 10);
				st.takeItems(ORIHARUKON, 10);
				st.giveItems(SEWING_KIT, 1);
				st.playSound(SOUND_QUEST_DONE);
				st.exitQuest(false);
			} else
				htmltext = "30847-4a.htm";
		}

		return htmltext;
	}

	@Override
	public String onTalk(L2NpcInstance npc, L2PcInstance player) {
		QuestState st = player.getQuestState(qn);
		String htmltext = getNoQuestMsg(player);
		if (st == null)
			return htmltext;

		if (st.getState() == CREATED) {
			if (player.getLevel() >= 60) {
				QuestState fwear = player.getQuestState("Q037_MakeFormalWear");
				if (fwear != null && fwear.getInt("cond") == 6)
					htmltext = "30847-0.htm";
				else
					htmltext = "30847-0a.htm";
			} else
				htmltext = "30847-0b.htm";
		}
		if (st.getState() == STARTED) {
			int cond = st.getInt("cond");
			if (cond == 1)
				htmltext = "30847-1a.htm";
			else if (cond == 2)
				htmltext = "30847-2.htm";
			else if (cond == 3)
				htmltext = (st.getQuestItemsCount(ORIHARUKON) < 10 || st.getQuestItemsCount(ARTISANS_FRAME) < 10)
						? "30847-4a.htm"
						: "30847-4.htm";
		}

		else if (st.getState() == COMPLETED) {
			htmltext = QUEST_DONE;
		}
		return htmltext;
	}

	@Override
	public String onKill(L2NpcInstance npc, L2PcInstance player, boolean isPet) {
		final QuestState st = player.getQuestState(qn);
		if (st.getState() != STARTED)
			return null;

		int count = st.getQuestItemsCount(REINFORCED_STEEL);
		if (count < 5)
			st.giveItems(REINFORCED_STEEL, 1);
		if (count == 49) {
			st.playSound(SOUND_QUEST_MIDDLE);
			st.set("cond", "2");
		} else
			st.playSound(SOUND_ITEM_GET);

		return null;
	}

	public static void main(String[] args) {
		new Q036_MakeASewingKit(36, qn, "Make a Sewing Kit");
	}
}