package quests.Q018_MeetingWithTheGoldenRam;

import com.it.br.gameserver.model.actor.instance.L2NpcInstance;
import com.it.br.gameserver.model.actor.instance.L2PcInstance;
import com.it.br.gameserver.model.quest.Quest;
import com.it.br.gameserver.model.quest.QuestState;
import com.it.br.gameserver.model.quest.State;

public class Q018_MeetingWithTheGoldenRam extends Quest
{
	private static final String qn = "Q018_MeetingWithTheGoldenRam";

	// Items
	private static final int SUPPLY_BOX = 7245;

	// NPCs
	private static final int DONAL = 31314;
	private static final int DAISY = 31315;
	private static final int ABERCROMBIE = 31555;

	private State CREATED;
	private State STARTED;
	private State COMPLETED;

	public Q018_MeetingWithTheGoldenRam(int questId, String name, String descr)
	{
		super(questId, name, descr);
		CREATED = new State("Start", this);
		STARTED = new State("Started", this);
		COMPLETED = new State("Completed", this);
		this.setInitialState(CREATED);

		questItemIds(SUPPLY_BOX);

		addStartNpc(DONAL);
		addTalkId(DONAL, DAISY, ABERCROMBIE);
	}

	@Override
	public String onAdvEvent(String event, L2NpcInstance npc, L2PcInstance player)
	{
		String htmltext = event;
		QuestState st = player.getQuestState(qn);
		if (st == null)
			return htmltext;

		if (event.equalsIgnoreCase("31314-03.htm"))
		{
			st.setState(STARTED);
			st.set("cond", "1");
			st.playSound(SOUND_QUEST_START);
		}
		else if (event.equalsIgnoreCase("31315-02.htm"))
		{
			st.set("cond", "2");
			st.playSound(SOUND_QUEST_MIDDLE);
			st.giveItems(SUPPLY_BOX, 1);
		}
		else if (event.equalsIgnoreCase("31555-02.htm"))
		{
			st.takeItems(SUPPLY_BOX, 1);
			st.rewardItems(57, 15000);
			st.addExpAndSp(50000, 0);
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
			htmltext = (player.getLevel() < 66) ? "31314-02.htm" : "31314-01.htm";

		else if (st.getState() == STARTED)
		{
			int cond = st.getInt("cond");
			switch (npc.getNpcId())
			{
				case DONAL:
					htmltext = "31314-04.htm";
					break;

				case DAISY:
					if (cond == 1)
						htmltext = "31315-01.htm";
					else if (cond == 2)
						htmltext = "31315-03.htm";
					break;

				case ABERCROMBIE:
					if (cond == 2)
						htmltext = "31555-01.htm";
					break;
			}
		}
		else if (st.getState() == COMPLETED)
			htmltext = getAlreadyCompletedMsg();

		return htmltext;
	}

	public static void main(String[] args)
	{
		new Q018_MeetingWithTheGoldenRam(18, qn, "Meeting with the Golden Ram");
	}
}