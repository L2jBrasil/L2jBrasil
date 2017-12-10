package quests.Q033_MakeAPairOfDressShoes;

import com.it.br.gameserver.model.actor.instance.L2NpcInstance;
import com.it.br.gameserver.model.actor.instance.L2PcInstance;
import com.it.br.gameserver.model.quest.Quest;
import com.it.br.gameserver.model.quest.QuestState;
import com.it.br.gameserver.model.quest.State;

public class Q033_MakeAPairOfDressShoes extends Quest {
	private static final String qn = "Q033_MakeAPairOfDressShoes";

	// NPCs
	private static final int WOODLEY = 30838;
	private static final int IAN = 30164;
	private static final int LEIKAR = 31520;

	// Items
	private static final int LEATHER = 1882;
	private static final int THREAD = 1868;
	private static final int ADENA = 57;

	// Rewards
	public static int DRESS_SHOES_BOX = 7113;

	private State CREATED;
	private State STARTED;
	private State COMPLETED;

	public Q033_MakeAPairOfDressShoes(int questId, String name, String descr) {
		super(questId, name, descr);
		CREATED = new State("Start", this);
		STARTED = new State("Started", this);
		COMPLETED = new State("Completed", this);
		this.setInitialState(CREATED);
		addStartNpc(WOODLEY);
		addTalkId(WOODLEY);
		addTalkId(IAN);
		addTalkId(LEIKAR);
	}

	@Override
	public String onAdvEvent(String event, L2NpcInstance npc, L2PcInstance player) {
		String htmltext = event;
		QuestState st = player.getQuestState(qn);
		if (st == null)
			return htmltext;

		if (event.equalsIgnoreCase("30838-1.htm")) {
			st.setState(STARTED);
			st.set("cond", "1");
			st.playSound(SOUND_QUEST_START);
		} else if (event.equalsIgnoreCase("31520-1.htm")) {
			st.set("cond", "2");
			st.playSound(SOUND_QUEST_MIDDLE);
		} else if (event.equalsIgnoreCase("30838-3.htm")) {
			st.set("cond", "3");
			st.playSound(SOUND_QUEST_MIDDLE);
		} else if (event.equalsIgnoreCase("30838-5.htm")) {
			if (st.getQuestItemsCount(LEATHER) >= 200 && st.getQuestItemsCount(THREAD) >= 600
					&& st.getQuestItemsCount(ADENA) >= 200000) {
				st.set("cond", "4");
				st.playSound(SOUND_QUEST_MIDDLE);
				st.takeItems(ADENA, 200000);
				st.takeItems(LEATHER, 200);
				st.takeItems(THREAD, 600);
			} else
				htmltext = "30838-4a.htm";
		} else if (event.equalsIgnoreCase("30164-1.htm")) {
			if (st.getQuestItemsCount(ADENA) >= 300000) {
				st.set("cond", "5");
				st.playSound(SOUND_QUEST_MIDDLE);
				st.takeItems(ADENA, 300000);
			} else
				htmltext = "30164-1a.htm";
		} else if (event.equalsIgnoreCase("30838-7.htm")) {
			st.giveItems(DRESS_SHOES_BOX, 1);
			st.playSound(SOUND_QUEST_DONE);
			st.exitQuest(false);
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
				if (fwear != null && fwear.getInt("cond") == 7)
					htmltext = "30838-0.htm";
				else
					htmltext = "30838-0a.htm";
			} else
				htmltext = "30838-0b.htm";
		}

		else if (st.getState() == STARTED) {
			int cond = st.getInt("cond");
			switch (npc.getNpcId()) {
			case WOODLEY:
				if (cond == 1)
					htmltext = "30838-1.htm";
				else if (cond == 2)
					htmltext = "30838-2.htm";
				else if (cond == 3) {
					if (st.getQuestItemsCount(LEATHER) >= 200 && st.getQuestItemsCount(THREAD) >= 600
							&& st.getQuestItemsCount(ADENA) >= 200000)
						htmltext = "30838-4.htm";
					else
						htmltext = "30838-4a.htm";
				} else if (cond == 4)
					htmltext = "30838-5a.htm";
				else if (cond == 5)
					htmltext = "30838-6.htm";
				break;

			case LEIKAR:
				if (cond == 1)
					htmltext = "31520-0.htm";
				else if (cond > 1)
					htmltext = "31520-1a.htm";
				break;

			case IAN:
				if (cond == 4)
					htmltext = "30164-0.htm";
				else if (cond == 5)
					htmltext = "30164-2.htm";
				break;
			}
		}

		else if (st.getState() == COMPLETED) {
			htmltext = QUEST_DONE;
		}
		return htmltext;
	}

	public static void main(String[] args) {
		new Q033_MakeAPairOfDressShoes(33, qn, "Make a Pair of Dress Shoes");
	}
}