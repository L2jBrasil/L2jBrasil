package quests.Q019_GoToThePastureland;

import com.it.br.gameserver.model.actor.instance.L2NpcInstance;
import com.it.br.gameserver.model.actor.instance.L2PcInstance;
import com.it.br.gameserver.model.quest.Quest;
import com.it.br.gameserver.model.quest.QuestState;
import com.it.br.gameserver.model.quest.State;

public class Q019_GoToThePastureland extends Quest
{
	private static final String qn = "Q019_GoToThePastureland";

	// Items
	private static final int YOUNG_WILD_BEAST_MEAT = 7547;

	// NPCs
	private static final int VLADIMIR = 31302;
	private static final int TUNATUN = 31537;

	private State CREATED;
	private State STARTED;
	private State COMPLETED;

	public Q019_GoToThePastureland(int questId, String name, String descr)
	{
		super(questId, name, descr);
		CREATED = new State("Start", this);
		STARTED = new State("Started", this);
		COMPLETED = new State("Completed", this);
		this.setInitialState(CREATED);

		questItemIds(YOUNG_WILD_BEAST_MEAT);

		addStartNpc(VLADIMIR);
		addTalkId(VLADIMIR, TUNATUN);
	}

	@Override
	public String onAdvEvent(String event, L2NpcInstance npc, L2PcInstance player)
	{
		String htmltext = event;
		QuestState st = player.getQuestState(qn);
		if (st == null)
			return htmltext;

		if (event.equalsIgnoreCase("31302-01.htm"))
		{
			st.setState(STARTED);
			st.set("cond", "1");
			st.playSound(SOUND_QUEST_START);
			st.giveItems(YOUNG_WILD_BEAST_MEAT, 1);
		}
		else if (event.equalsIgnoreCase("019_finish"))
		{
			if (st.hasQuestItems(YOUNG_WILD_BEAST_MEAT))
			{
				htmltext = "31537-01.htm";
				st.takeItems(YOUNG_WILD_BEAST_MEAT, 1);
				st.rewardItems(57, 30000);
				st.playSound(SOUND_QUEST_DONE);
				st.exitQuest(false);
			}
			else
				htmltext = "31537-02.htm";
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
			htmltext = (player.getLevel() < 63) ? "31302-03.htm" : "31302-00.htm";

		else if (st.getState() == STARTED)
		{
			switch (npc.getNpcId())
			{
				case VLADIMIR:
					htmltext = "31302-02.htm";
					break;

				case TUNATUN:
					htmltext = "31537-00.htm";
					break;
			}
		}
		else if (st.getState() == COMPLETED)
			htmltext = getAlreadyCompletedMsg();

		return htmltext;
	}

	public static void main(String[] args)
	{
		new Q019_GoToThePastureland(19, qn, "Go to the Pastureland");
	}
}