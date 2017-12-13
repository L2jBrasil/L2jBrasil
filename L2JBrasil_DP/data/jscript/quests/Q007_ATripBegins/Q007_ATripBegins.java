package quests.Q007_ATripBegins;

import com.it.br.gameserver.model.actor.instance.L2NpcInstance;
import com.it.br.gameserver.model.actor.instance.L2PcInstance;
import com.it.br.gameserver.model.base.Race;
import com.it.br.gameserver.model.quest.Quest;
import com.it.br.gameserver.model.quest.QuestState;
import com.it.br.gameserver.model.quest.State;

public class Q007_ATripBegins extends Quest
{
	private static final String qn = "Q007_ATripBegins";

	// NPCs
	private static final int MIRABEL = 30146;
	private static final int ARIEL = 30148;
	private static final int ASTERIOS = 30154;

	// Items
	private static final int ARIEL_RECO = 7572;

	// Rewards
	private static final int MARK_TRAVELER = 7570;
	private static final int SOE_GIRAN = 7559;

	private State CREATED;
	private State STARTED;
	private State COMPLETED;

	public Q007_ATripBegins(int questId, String name, String descr)
	{
		super(questId, name, descr);
		CREATED = new State("Start", this);
		STARTED = new State("Started", this);
		COMPLETED = new State("Completed", this);
		this.setInitialState(CREATED);
		questItemIds(ARIEL_RECO);

		addStartNpc(MIRABEL);
		addTalkId(MIRABEL, ARIEL, ASTERIOS);
	}

	@Override
	public String onAdvEvent(String event, L2NpcInstance npc, L2PcInstance player)
	{
		String htmltext = event;
		QuestState st = player.getQuestState(qn);
		if (st == null)
			return htmltext;

		if (event.equalsIgnoreCase("30146-03.htm"))
		{
			st.setState(STARTED);
			st.set("cond", "1");
			st.playSound(SOUND_QUEST_START);
		}
		else if (event.equalsIgnoreCase("30148-02.htm"))
		{
			st.set("cond", "2");
			st.playSound(SOUND_QUEST_MIDDLE);
			st.giveItems(ARIEL_RECO, 1);
		}
		else if (event.equalsIgnoreCase("30154-02.htm"))
		{
			st.set("cond", "3");
			st.playSound(SOUND_QUEST_MIDDLE);
			st.takeItems(ARIEL_RECO, 1);
		}
		else if (event.equalsIgnoreCase("30146-06.htm"))
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
			if (player.getRace() != Race.elf)
				htmltext = "30146-01.htm";
			else if (player.getLevel() < 3)
				htmltext = "30146-01a.htm";
			else
				htmltext = "30146-02.htm";
		}
		else if (st.getState() == STARTED)
		{
			int cond = st.getInt("cond");
			switch (npc.getNpcId())
			{
				case MIRABEL:
					if (cond == 1 || cond == 2)
						htmltext = "30146-04.htm";
					else if (cond == 3)
						htmltext = "30146-05.htm";
					break;

				case ARIEL:
					if (cond == 1)
						htmltext = "30148-01.htm";
					else if (cond == 2)
						htmltext = "30148-03.htm";
					break;

				case ASTERIOS:
					if (cond == 2)
						htmltext = "30154-01.htm";
					else if (cond == 3)
						htmltext = "30154-03.htm";
					break;
			}
		}
		else if (st.getState() == COMPLETED)
			htmltext = getAlreadyCompletedMsg();

		return htmltext;
	}

	public static void main(String[] args)
	{
		new Q007_ATripBegins(7, qn, "A Trip Begins");
	}
}