package quests.Q035_FindGlitteringJewelry;

import com.it.br.gameserver.model.actor.instance.L2NpcInstance;
import com.it.br.gameserver.model.actor.instance.L2PcInstance;
import com.it.br.gameserver.model.quest.Quest;
import com.it.br.gameserver.model.quest.QuestState;
import com.it.br.gameserver.model.quest.State;

public class Q035_FindGlitteringJewelry extends Quest {
	private static final String qn = "Q035_FindGlitteringJewelry";

	// NPCs
	private static final int ELLIE = 30091;
	private static final int FELTON = 30879;

	// Items
	private static final int ROUGH_JEWEL = 7162;
	private static final int ORIHARUKON = 1893;
	private static final int SILVER_NUGGET = 1873;
	private static final int THONS = 4044;

	// Reward
	private static final int JEWEL_BOX = 7077;

	private State CREATED;
	private State STARTED;
	private State COMPLETED;

	public Q035_FindGlitteringJewelry(int questId, String name, String descr) {
		super(questId, name, descr);
		CREATED = new State("Start", this);
		STARTED = new State("Started", this);
		COMPLETED = new State("Completed", this);
		this.setInitialState(CREATED);
		questItemIds(ROUGH_JEWEL);

		addStartNpc(ELLIE);
		addTalkId(ELLIE);
		addTalkId(FELTON);
		addKillId(20135); // Alligator
	}

	@Override
	public String onAdvEvent(String event, L2NpcInstance npc, L2PcInstance player) {
		String htmltext = event;
		QuestState st = player.getQuestState(qn);
		if (st == null)
			return htmltext;

		if (event.equalsIgnoreCase("30091-1.htm")) {
			st.setState(STARTED);
			st.set("cond", "1");
			st.playSound(SOUND_QUEST_START);
		} else if (event.equalsIgnoreCase("30879-1.htm")) {
			st.set("cond", "2");
			st.playSound(SOUND_QUEST_MIDDLE);
		} else if (event.equalsIgnoreCase("30091-3.htm")) {
			st.set("cond", "4");
			st.playSound(SOUND_QUEST_MIDDLE);
			st.takeItems(ROUGH_JEWEL, 10);
		} else if (event.equalsIgnoreCase("30091-5.htm")) {
			if (st.getQuestItemsCount(ORIHARUKON) >= 5 && st.getQuestItemsCount(SILVER_NUGGET) >= 500
					&& st.getQuestItemsCount(THONS) >= 150) {
				st.takeItems(ORIHARUKON, 5);
				st.takeItems(SILVER_NUGGET, 500);
				st.takeItems(THONS, 150);
				st.giveItems(JEWEL_BOX, 1);
				st.playSound(SOUND_QUEST_DONE);
				st.exitQuest(false);
			} else
				htmltext = "30091-4a.htm";
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
					htmltext = "30091-0.htm";
				else
					htmltext = "30091-0a.htm";
			} else
				htmltext = "30091-0b.htm";
		} else if (st.getState() == STARTED) {
			int cond = st.getInt("cond");
			switch (npc.getNpcId()) {
			case ELLIE:
				if (cond == 1 || cond == 2)
					htmltext = "30091-1a.htm";
				else if (cond == 3)
					htmltext = "30091-2.htm";
				else if (cond == 4)
					htmltext = (st.getQuestItemsCount(ORIHARUKON) >= 5 && st.getQuestItemsCount(SILVER_NUGGET) >= 500
							&& st.getQuestItemsCount(THONS) >= 150) ? "30091-4.htm" : "30091-4a.htm";
				break;

			case FELTON:
				if (cond == 1)
					htmltext = "30879-0.htm";
				else if (cond > 1)
					htmltext = "30879-1a.htm";
				break;
			}
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

		int count = st.getQuestItemsCount(ROUGH_JEWEL);
		if (count < 10)
			st.giveItems(ROUGH_JEWEL, 1);
		if (count == 9) {
			st.playSound(SOUND_QUEST_MIDDLE);
			st.set("cond", "5");
		} else
			st.playSound(SOUND_ITEM_GET);

		return null;
	}

	public static void main(String[] args) {
		new Q035_FindGlitteringJewelry(35, qn, "Find Glittering Jewelry");
	}
}