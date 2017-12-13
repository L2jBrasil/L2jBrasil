package quests.Q010_IntoTheWorld;

import com.it.br.gameserver.model.actor.instance.L2NpcInstance;
import com.it.br.gameserver.model.actor.instance.L2PcInstance;
import com.it.br.gameserver.model.base.Race;
import com.it.br.gameserver.model.quest.Quest;
import com.it.br.gameserver.model.quest.QuestState;
import com.it.br.gameserver.model.quest.State;

public class Q010_IntoTheWorld extends Quest
{
	private static final String qn = "Q010_IntoTheWorld";

	// Items
	private static final int VERY_EXPENSIVE_NECKLACE = 7574;

	// Rewards
	private static final int SOE_GIRAN = 7559;
	private static final int MARK_OF_TRAVELER = 7570;

	// NPCs
	private static final int REED = 30520;
	private static final int BALANKI = 30533;
	private static final int GERALD = 30650;

	private State CREATED;
	private State STARTED;
	private State COMPLETED;

	public Q010_IntoTheWorld(int questId, String name, String descr)
	{
		super(questId, name, descr);
		CREATED = new State("Start", this);
		STARTED = new State("Started", this);
		COMPLETED = new State("Completed", this);
		this.setInitialState(CREATED);

		questItemIds(VERY_EXPENSIVE_NECKLACE);

		addStartNpc(BALANKI);
		addTalkId(BALANKI, REED, GERALD);
	}

	@Override
	public String onAdvEvent(String event, L2NpcInstance npc, L2PcInstance player)
	{
		String htmltext = event;
		QuestState st = player.getQuestState(qn);
		if (st == null)
			return htmltext;

		if (event.equalsIgnoreCase("30533-02.htm"))
		{
			st.setState(STARTED);
			st.set("cond", "1");
			st.playSound(SOUND_QUEST_START);
		}
		else if (event.equalsIgnoreCase("30520-02.htm"))
		{
			st.set("cond", "2");
			st.playSound(SOUND_QUEST_MIDDLE);
			st.giveItems(VERY_EXPENSIVE_NECKLACE, 1);
		}
		else if (event.equalsIgnoreCase("30650-02.htm"))
		{
			st.set("cond", "3");
			st.playSound(SOUND_QUEST_MIDDLE);
			st.takeItems(VERY_EXPENSIVE_NECKLACE, 1);
		}
		else if (event.equalsIgnoreCase("30520-04.htm"))
		{
			st.set("cond", "4");
			st.playSound(SOUND_QUEST_MIDDLE);
		}
		else if (event.equalsIgnoreCase("30533-05.htm"))
		{
			st.giveItems(SOE_GIRAN, 1);
			st.rewardItems(MARK_OF_TRAVELER, 1);
			st.playSound(SOUND_QUEST_DONE);
			st.exitQuest(false);
		}

		return htmltext;
	}

	@Override
	public String onTalk(L2NpcInstance npc, L2PcInstance player)
	{
		String htmltext = getNoQuestMsg(player);
		QuestState st = player.getQuestState(qn);
		if (st == null)
			return htmltext;

		if (st.getState() == CREATED)
		{
			if (player.getLevel() >= 3 && player.getRace() == Race.dwarf)
				htmltext = "30533-01.htm";
			else
				htmltext = "30533-01a.htm";
		}
		else if (st.getState() == STARTED)
		{
			int cond = st.getInt("cond");
			switch (npc.getNpcId())
			{
				case BALANKI:
					if (cond < 4)
						htmltext = "30533-03.htm";
					else if (cond == 4)
						htmltext = "30533-04.htm";
					break;

				case REED:
					if (cond == 1)
						htmltext = "30520-01.htm";
					else if (cond == 2)
						htmltext = "30520-02a.htm";
					else if (cond == 3)
						htmltext = "30520-03.htm";
					else if (cond == 4)
						htmltext = "30520-04a.htm";
					break;

				case GERALD:
					if (cond == 2)
						htmltext = "30650-01.htm";
					else if (cond > 2)
						htmltext = "30650-04.htm";
					break;
			}

		}
		else if (st.getState() == COMPLETED)
			htmltext = getAlreadyCompletedMsg();

		return htmltext;
	}

	public static void main(String[] args)
	{
		new Q010_IntoTheWorld(10, qn, "Into the World");
	}
}