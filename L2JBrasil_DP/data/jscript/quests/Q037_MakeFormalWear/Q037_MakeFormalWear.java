package quests.Q037_MakeFormalWear;

import com.it.br.gameserver.model.actor.instance.L2NpcInstance;
import com.it.br.gameserver.model.actor.instance.L2PcInstance;
import com.it.br.gameserver.model.quest.Quest;
import com.it.br.gameserver.model.quest.QuestState;
import com.it.br.gameserver.model.quest.State;

public class Q037_MakeFormalWear extends Quest {
	private static final String qn = "Q037_MakeFormalWear";

	// NPCs
	private static final int ALEXIS = 30842;
	private static final int LEIKAR = 31520;
	private static final int JEREMY = 31521;
	private static final int MIST = 31627;

	// Items
	private static final int MYSTERIOUS_CLOTH = 7076;
	private static final int JEWEL_BOX = 7077;
	private static final int SEWING_KIT = 7078;
	private static final int DRESS_SHOES_BOX = 7113;
	private static final int SIGNET_RING = 7164;
	private static final int ICE_WINE = 7160;
	private static final int BOX_OF_COOKIES = 7159;

	// Reward
	private static final int FORMAL_WEAR = 6408;

	private State CREATED;
	private State STARTED;
	private State COMPLETED;

	public Q037_MakeFormalWear(int questId, String name, String descr) {
		super(questId, name, descr);
		CREATED = new State("Start", this);
		STARTED = new State("Started", this);
		COMPLETED = new State("Completed", this);
		this.setInitialState(CREATED);
		questItemIds(SIGNET_RING, ICE_WINE, BOX_OF_COOKIES);

		addStartNpc(ALEXIS);
		addTalkId(ALEXIS);
		addTalkId(LEIKAR);
		addTalkId(JEREMY);
		addTalkId(MIST);
	}

	@Override
	public String onAdvEvent(String event, L2NpcInstance npc, L2PcInstance player) {
		String htmltext = event;
		QuestState st = player.getQuestState(qn);
		if (st == null)
			return htmltext;

		if (event.equalsIgnoreCase("30842-1.htm")) {
			st.setState(STARTED);
			st.set("cond", "1");
			st.playSound(SOUND_QUEST_START);
		} else if (event.equalsIgnoreCase("31520-1.htm")) {
			st.set("cond", "2");
			st.playSound(SOUND_QUEST_MIDDLE);
			st.giveItems(SIGNET_RING, 1);
		} else if (event.equalsIgnoreCase("31521-1.htm")) {
			st.set("cond", "3");
			st.playSound(SOUND_QUEST_MIDDLE);
			st.takeItems(SIGNET_RING, 1);
			st.giveItems(ICE_WINE, 1);
		} else if (event.equalsIgnoreCase("31627-1.htm")) {
			st.set("cond", "4");
			st.playSound(SOUND_QUEST_MIDDLE);
			st.takeItems(ICE_WINE, 1);
		} else if (event.equalsIgnoreCase("31521-3.htm")) {
			st.set("cond", "5");
			st.playSound(SOUND_QUEST_MIDDLE);
			st.giveItems(BOX_OF_COOKIES, 1);
		} else if (event.equalsIgnoreCase("31520-3.htm")) {
			st.set("cond", "6");
			st.playSound(SOUND_QUEST_MIDDLE);
			st.takeItems(BOX_OF_COOKIES, 1);
		} else if (event.equalsIgnoreCase("31520-5.htm")) {
			st.set("cond", "7");
			st.playSound(SOUND_QUEST_MIDDLE);
			st.takeItems(JEWEL_BOX, 1);
			st.takeItems(MYSTERIOUS_CLOTH, 1);
			st.takeItems(SEWING_KIT, 1);
		} else if (event.equalsIgnoreCase("31520-7.htm")) {
			st.takeItems(DRESS_SHOES_BOX, 1);
			st.giveItems(FORMAL_WEAR, 1);
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
			htmltext = (player.getLevel() < 60) ? "30842-0a.htm" : "30842-0.htm";
		}

		else if (st.getState() == STARTED) {
			int cond = st.getInt("cond");
			switch (npc.getNpcId()) {
			case ALEXIS:
				if (cond == 1)
					htmltext = "30842-2.htm";
				break;

			case LEIKAR:
				if (cond == 1)
					htmltext = "31520-0.htm";
				else if (cond == 2)
					htmltext = "31520-1a.htm";
				else if (cond == 5 || cond == 6) {
					if (st.hasQuestItems(MYSTERIOUS_CLOTH, JEWEL_BOX, SEWING_KIT))
						htmltext = "31520-4.htm";
					else if (st.hasQuestItems(BOX_OF_COOKIES))
						htmltext = "31520-2.htm";
					else
						htmltext = "31520-3a.htm";
				} else if (cond == 7)
					htmltext = (st.hasQuestItems(DRESS_SHOES_BOX)) ? "31520-6.htm" : "31520-5a.htm";
				break;

			case JEREMY:
				if (st.hasQuestItems(SIGNET_RING))
					htmltext = "31521-0.htm";
				else if (cond == 3)
					htmltext = "31521-1a.htm";
				else if (cond == 4)
					htmltext = "31521-2.htm";
				else if (cond > 4)
					htmltext = "31521-3a.htm";
				break;

			case MIST:
				if (cond == 3)
					htmltext = "31627-0.htm";
				else if (cond > 3)
					htmltext = "31627-2.htm";
				break;
			}
		} else if (st.getState() == COMPLETED) {
			htmltext = QUEST_DONE;
		}

		return htmltext;
	}

	public static void main(String[] args) {
		new Q037_MakeFormalWear(37, qn, "Make Formal Wear");
	}
}