package quests.Q031_SecretBuriedInTheSwamp;

import com.it.br.gameserver.model.actor.instance.L2NpcInstance;
import com.it.br.gameserver.model.actor.instance.L2PcInstance;
import com.it.br.gameserver.model.quest.Quest;
import com.it.br.gameserver.model.quest.QuestState;
import com.it.br.gameserver.model.quest.State;

public class Q031_SecretBuriedInTheSwamp extends Quest
{
	private static final String qn = "Q031_SecretBuriedInTheSwamp";

	// Item
	private static final int KRORIN_JOURNAL = 7252;

	// NPCs
	private static final int ABERCROMBIE = 31555;
	private static final int FORGOTTEN_MONUMENT_1 = 31661;
	private static final int FORGOTTEN_MONUMENT_2 = 31662;
	private static final int FORGOTTEN_MONUMENT_3 = 31663;
	private static final int FORGOTTEN_MONUMENT_4 = 31664;
	private static final int CORPSE_OF_DWARF = 31665;

	private State CREATED;
	private State STARTED;
	private State COMPLETED;

	public Q031_SecretBuriedInTheSwamp(int questId, String name, String descr)
	{
		super(questId, name, descr);
		CREATED = new State("Start", this);
		STARTED = new State("Started", this);
		COMPLETED = new State("Completed", this);
		this.setInitialState(CREATED);
		questItemIds(KRORIN_JOURNAL);

		addStartNpc(ABERCROMBIE);
		addTalkId(ABERCROMBIE, CORPSE_OF_DWARF, FORGOTTEN_MONUMENT_1, FORGOTTEN_MONUMENT_2, FORGOTTEN_MONUMENT_3,
		        FORGOTTEN_MONUMENT_4);
	}

	@Override
	public String onAdvEvent(String event, L2NpcInstance npc, L2PcInstance player)
	{
		String htmltext = event;
		QuestState st = player.getQuestState(qn);
		if (st == null)
			return htmltext;

		if (event.equalsIgnoreCase("31555-01.htm"))
		{
			st.setState(STARTED);
			st.set("cond", "1");
			st.playSound(SOUND_QUEST_START);
		}
		else if (event.equalsIgnoreCase("31665-01.htm"))
		{
			st.set("cond", "2");
			st.playSound(SOUND_QUEST_MIDDLE);
			st.giveItems(KRORIN_JOURNAL, 1);
		}
		else if (event.equalsIgnoreCase("31555-04.htm"))
		{
			st.set("cond", "3");
			st.playSound(SOUND_QUEST_MIDDLE);
		}
		else if (event.equalsIgnoreCase("31661-01.htm"))
		{
			st.set("cond", "4");
			st.playSound(SOUND_QUEST_MIDDLE);
		}
		else if (event.equalsIgnoreCase("31662-01.htm"))
		{
			st.set("cond", "5");
			st.playSound(SOUND_QUEST_MIDDLE);
		}
		else if (event.equalsIgnoreCase("31663-01.htm"))
		{
			st.set("cond", "6");
			st.playSound(SOUND_QUEST_MIDDLE);
		}
		else if (event.equalsIgnoreCase("31664-01.htm"))
		{
			st.set("cond", "7");
			st.playSound(SOUND_QUEST_MIDDLE);
		}
		else if (event.equalsIgnoreCase("31555-07.htm"))
		{
			st.takeItems(KRORIN_JOURNAL, 1);
			st.rewardItems(57, 40000);
			st.addExpAndSp(130000, 0);
			st.playSound(SOUND_QUEST_DONE);
			st.exitQuest(false);
		}

		return htmltext;
	}

	@Override
	public String onTalk(L2NpcInstance npc, L2PcInstance player)
	{
		QuestState st = player.getQuestState(qn);
		String htmltext = getNoQuestMsg(player);
		if (st == null)
			return htmltext;

		if (st.getState() == CREATED)
			htmltext = (player.getLevel() < 66) ? "31555-00a.htm" : "31555-00.htm";

		else if (st.getState() == STARTED)
		{
			int cond = st.getInt("cond");
			switch (npc.getNpcId())
			{
				case ABERCROMBIE:
					if (cond == 1)
						htmltext = "31555-02.htm";
					else if (cond == 2)
						htmltext = "31555-03.htm";
					else if (cond > 2 && cond < 7)
						htmltext = "31555-05.htm";
					else if (cond == 7)
						htmltext = "31555-06.htm";
					break;

				case CORPSE_OF_DWARF:
					if (cond == 1)
						htmltext = "31665-00.htm";
					else if (cond > 1)
						htmltext = "31665-02.htm";
					break;

				case FORGOTTEN_MONUMENT_1:
					if (cond == 3)
						htmltext = "31661-00.htm";
					else if (cond > 3)
						htmltext = "31661-02.htm";
					break;

				case FORGOTTEN_MONUMENT_2:
					if (cond == 4)
						htmltext = "31662-00.htm";
					else if (cond > 4)
						htmltext = "31662-02.htm";
					break;

				case FORGOTTEN_MONUMENT_3:
					if (cond == 5)
						htmltext = "31663-00.htm";
					else if (cond > 5)
						htmltext = "31663-02.htm";
					break;

				case FORGOTTEN_MONUMENT_4:
					if (cond == 6)
						htmltext = "31664-00.htm";
					else if (cond > 6)
						htmltext = "31664-02.htm";
					break;
			}
		}
		else if (st.getState() == COMPLETED)
			htmltext = getAlreadyCompletedMsg();

		return htmltext;
	}

	public static void main(String[] args)
	{
		new Q031_SecretBuriedInTheSwamp(31, qn, "Secret Buried in the Swamp");
	}
}