package quests.Q008_AnAdventureBegins;

import com.it.br.gameserver.model.actor.instance.L2NpcInstance;
import com.it.br.gameserver.model.actor.instance.L2PcInstance;
import com.it.br.gameserver.model.base.Race;
import com.it.br.gameserver.model.quest.Quest;
import com.it.br.gameserver.model.quest.QuestState;
import com.it.br.gameserver.model.quest.State;

public class Q008_AnAdventureBegins extends Quest
{
	private static final String qn = "Q008_AnAdventureBegins";

	// NPCs
	private static final int JASMINE = 30134;
	private static final int ROSELYN = 30355;
	private static final int HARNE = 30144;

	// Items
	private static final int ROSELYN_NOTE = 7573;

	// Rewards
	private static final int SOE_GIRAN = 7559;
	private static final int MARK_TRAVELER = 7570;

	private State CREATED;
	private State STARTED;
	private State COMPLETED;

	public Q008_AnAdventureBegins(int questId, String name, String descr)
	{
		super(questId, name, descr);
		CREATED = new State("Start", this);
		STARTED = new State("Started", this);
		COMPLETED = new State("Completed", this);
		this.setInitialState(CREATED);
		questItemIds(ROSELYN_NOTE);

		addStartNpc(JASMINE);
		addTalkId(JASMINE, ROSELYN, HARNE);
	}

	@Override
	public String onAdvEvent(String event, L2NpcInstance npc, L2PcInstance player)
	{
		String htmltext = event;
		QuestState st = player.getQuestState(qn);
		if (st == null)
			return htmltext;

		if (event.equalsIgnoreCase("30134-03.htm"))
		{
			st.setState(STARTED);
			st.set("cond", "1");
			st.playSound(SOUND_QUEST_START);
		}
		else if (event.equalsIgnoreCase("30355-02.htm"))
		{
			st.set("cond", "2");
			st.playSound(SOUND_QUEST_MIDDLE);
			st.giveItems(ROSELYN_NOTE, 1);
		}
		else if (event.equalsIgnoreCase("30144-02.htm"))
		{
			st.set("cond", "3");
			st.playSound(SOUND_QUEST_MIDDLE);
			st.takeItems(ROSELYN_NOTE, 1);
		}
		else if (event.equalsIgnoreCase("30134-06.htm"))
		{
			st.giveItems(MARK_TRAVELER, 1);
			st.rewardItems(SOE_GIRAN, 1);
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
		{
			if (player.getLevel() >= 3 && player.getRace() == Race.darkelf)
				htmltext = "30134-02.htm";
			else
				htmltext = "30134-01.htm";
		}
		else if (st.getState() == STARTED)
		{
			int cond = st.getInt("cond");
			switch (npc.getNpcId())
			{
				case JASMINE:
					if (cond == 1 || cond == 2)
						htmltext = "30134-04.htm";
					else if (cond == 3)
						htmltext = "30134-05.htm";
					break;

				case ROSELYN:
					if (cond == 1)
						htmltext = "30355-01.htm";
					else if (cond == 2)
						htmltext = "30355-03.htm";
					break;

				case HARNE:
					if (cond == 2)
						htmltext = "30144-01.htm";
					else if (cond == 3)
						htmltext = "30144-03.htm";
					break;
			}
		}
		else if (st.getState() == COMPLETED)
			htmltext = getAlreadyCompletedMsg();

		return htmltext;
	}

	public static void main(String[] args)
	{
		new Q008_AnAdventureBegins(8, qn, "An Adventure Begins");
	}
}