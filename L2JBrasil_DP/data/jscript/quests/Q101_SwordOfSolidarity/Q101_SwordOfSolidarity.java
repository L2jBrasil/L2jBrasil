package quests.Q101_SwordOfSolidarity;

import com.it.br.gameserver.model.actor.instance.L2NpcInstance;
import com.it.br.gameserver.model.actor.instance.L2PcInstance;
import com.it.br.gameserver.model.base.Race;
import com.it.br.gameserver.model.quest.Quest;
import com.it.br.gameserver.model.quest.QuestState;
import com.it.br.gameserver.model.quest.State;
import com.it.br.gameserver.network.serverpackets.SocialAction;

public class Q101_SwordOfSolidarity extends Quest {
	private static final String qn = "Q101_SwordOfSolidarity";

	// NPCs
	private static final int ROIEN = 30008;
	private static final int ALTRAN = 30283;

	// Items
	private static final int BROKEN_SWORD_HANDLE = 739;
	private static final int BROKEN_BLADE_BOTTOM = 740;
	private static final int BROKEN_BLADE_TOP = 741;
	private static final int ROIENS_LETTER = 796;
	private static final int DIR_TO_RUINS = 937;
	private static final int ALTRANS_NOTE = 742;

	private static final int SWORD_OF_SOLIDARITY = 738;
	private static final int SPIRITSHOT_FOR_BEGINNERS = 5790;
	private static final int SOULSHOT_FOR_BEGINNERS = 5789;
	private static final int LESSER_HEALING_POT = 1060;
	private static final int ECHO_BATTLE = 4412;
	private static final int ECHO_LOVE = 4413;
	private static final int ECHO_SOLITUDE = 4414;
	private static final int ECHO_FEAST = 4415;
	private static final int ECHO_CELEBRATION = 4416;

	private State CREATED;
	private State STARTED;
	private State COMPLETED;

	public Q101_SwordOfSolidarity(int questId, String name, String descr) {
		super(questId, name, descr);
		CREATED = new State("Start", this);
		STARTED = new State("Started", this);
		COMPLETED = new State("Completed", this);
		this.setInitialState(CREATED);

		questItemIds(BROKEN_SWORD_HANDLE, BROKEN_BLADE_BOTTOM, BROKEN_BLADE_TOP);

		addStartNpc(ROIEN);
		addTalkId(ROIEN);
		addTalkId(ALTRAN);
		addKillId(20361);
		addKillId(20362);
	}

	@Override
	public String onAdvEvent(String event, L2NpcInstance npc, L2PcInstance player) {
		String htmltext = event;
		QuestState st = player.getQuestState(qn);
		if (st == null)
			return htmltext;

		if (event.equalsIgnoreCase("30008-03.htm")) {
			st.setState(STARTED);
			st.set("cond", "1");
			st.playSound(SOUND_QUEST_START);
			st.giveItems(ROIENS_LETTER, 1);
		} else if (event.equalsIgnoreCase("30283-02.htm")) {
			st.set("cond", "2");
			st.playSound(SOUND_QUEST_MIDDLE);
			st.takeItems(ROIENS_LETTER, 1);
			st.giveItems(DIR_TO_RUINS, 1);
		} else if (event.equalsIgnoreCase("30283-06.htm")) {
			st.takeItems(BROKEN_SWORD_HANDLE, 1);
			st.giveItems(SWORD_OF_SOLIDARITY, 1);
			st.giveItems(LESSER_HEALING_POT, 100);

			if (player.isNewbie()) {
				st.showQuestionMark(26);
				if (player.isMageClass()) {
					st.playTutorialVoice("tutorial_voice_027");
					st.giveItems(SPIRITSHOT_FOR_BEGINNERS, 3000);
				} else {
					st.playTutorialVoice("tutorial_voice_026");
					st.giveItems(SOULSHOT_FOR_BEGINNERS, 7000);
				}
			}

			st.giveItems(ECHO_BATTLE, 10);
			st.giveItems(ECHO_LOVE, 10);
			st.giveItems(ECHO_SOLITUDE, 10);
			st.giveItems(ECHO_FEAST, 10);
			st.giveItems(ECHO_CELEBRATION, 10);
			player.broadcastPacket(new SocialAction(player.getObjectId(), 3));
			st.playSound(SOUND_QUEST_DONE);
			st.exitQuest(false);
		}

		return htmltext;
	}

	@Override
	public String onTalk(L2NpcInstance npc, L2PcInstance player) {
		String htmltext = getNoQuestMsg(player);
		QuestState st = player.getQuestState(qn);
		if (st == null)
			return htmltext;

		if (st.getState() == CREATED) {
			if (player.getRace() != Race.human)
				htmltext = "30008-01a.htm";
			else if (player.getLevel() < 9)
				htmltext = "30008-01.htm";
			else
				htmltext = "30008-02.htm";
		} else if (st.getState() == STARTED) {
			int cond = (st.getInt("cond"));
			switch (npc.getNpcId()) {
			case ROIEN:
				if (cond == 1)
					htmltext = "30008-04.htm";
				else if (cond == 2)
					htmltext = "30008-03a.htm";
				else if (cond == 3)
					htmltext = "30008-06.htm";
				else if (cond == 4) {
					htmltext = "30008-05.htm";
					st.set("cond", "5");
					st.playSound(SOUND_QUEST_MIDDLE);
					st.takeItems(ALTRANS_NOTE, 1);
					st.giveItems(BROKEN_SWORD_HANDLE, 1);
				} else if (cond == 5)
					htmltext = "30008-05a.htm";
				break;

			case ALTRAN:
				if (cond == 1)
					htmltext = "30283-01.htm";
				else if (cond == 2)
					htmltext = "30283-03.htm";
				else if (cond == 3) {
					htmltext = "30283-04.htm";
					st.set("cond", "4");
					st.playSound(SOUND_QUEST_MIDDLE);
					st.takeItems(DIR_TO_RUINS, 1);
					st.takeItems(BROKEN_BLADE_TOP, 1);
					st.takeItems(BROKEN_BLADE_BOTTOM, 1);
					st.giveItems(ALTRANS_NOTE, 1);
				} else if (cond == 4)
					htmltext = "30283-04a.htm";
				else if (cond == 5)
					htmltext = "30283-05.htm";
				break;
			}
		} else if (st.getState() == COMPLETED) {
			htmltext = QUEST_DONE;
		}

		return htmltext;
	}

	@Override
	public String onKill(L2NpcInstance npc, L2PcInstance player, boolean isPet) {

		final QuestState st = player.getQuestState(getName());
		if (st == null)
			return null;

		if (st.getState() == STARTED) {
			switch (npc.getNpcId()) {

			case 20361:
			case 20362:

				if (st.hasQuestItems(DIR_TO_RUINS)) {
					if (st.getQuestItemsCount(BROKEN_BLADE_TOP) == 0) {
						if (st.getRandom(5) == 0) {
							st.giveItems(BROKEN_BLADE_TOP, 1);
							st.playSound(SOUND_QUEST_MIDDLE);
						}
					} else if (st.getQuestItemsCount(BROKEN_BLADE_BOTTOM) == 0) {
						if (st.getRandom(5) == 0) {
							st.giveItems(BROKEN_BLADE_BOTTOM, 1);
							st.playSound(SOUND_QUEST_MIDDLE);
						}
					}
				}
				if (st.hasQuestItems(BROKEN_BLADE_TOP) && st.hasQuestItems(BROKEN_BLADE_BOTTOM))
					st.set("cond", "3");
				break;
			}
		}
		return null;
	}

	public static void main(String[] args) {
		new Q101_SwordOfSolidarity(101, qn, "Sword Of Solidarity");
	}
}