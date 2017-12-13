package quests.Q020_BringUpWithLove;

import com.it.br.gameserver.model.actor.instance.L2NpcInstance;
import com.it.br.gameserver.model.actor.instance.L2PcInstance;
import com.it.br.gameserver.model.quest.Quest;
import com.it.br.gameserver.model.quest.QuestState;
import com.it.br.gameserver.model.quest.State;

public class Q020_BringUpWithLove extends Quest
{
	public static final String qn = "Q020_BringUpWithLove";

	// Item
	private static final int JEWEL_OF_INNOCENCE = 7185;

	private State CREATED;
	private State STARTED;
	private State COMPLETED;

	public Q020_BringUpWithLove(int questId, String name, String descr)
	{
		super(questId, name, descr);
		CREATED = new State("Start", this);
		STARTED = new State("Started", this);
		COMPLETED = new State("Completed", this);
		this.setInitialState(CREATED);

		questItemIds(JEWEL_OF_INNOCENCE);

		addStartNpc(31537); // Tunatun
		addTalkId(31537);
	}

	@Override
	public String onAdvEvent(String event, L2NpcInstance npc, L2PcInstance player)
	{
		String htmltext = event;
		QuestState st = player.getQuestState(qn);
		if (st == null)
			return htmltext;

		if (event.equalsIgnoreCase("31537-09.htm"))
		{
			st.setState(STARTED);
			st.set("cond", "1");
			st.playSound(SOUND_QUEST_START);
		}
		else if (event.equalsIgnoreCase("31537-12.htm"))
		{
			st.takeItems(JEWEL_OF_INNOCENCE, -1);
			st.rewardItems(57, 68500);
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
			htmltext = (player.getLevel() < 65) ? "31537-02.htm" : "31537-01.htm";

		else if (st.getState() == STARTED)
		{
			if (st.getInt("cond") == 2)
				htmltext = "31537-11.htm";
			else
				htmltext = "31537-10.htm";
		}
		else if (st.getState() == COMPLETED)
			htmltext = getAlreadyCompletedMsg();

		return htmltext;
	}

	public static void main(String[] args)
	{
		new Q020_BringUpWithLove(20, qn, "Bring Up With Love");
	}
}