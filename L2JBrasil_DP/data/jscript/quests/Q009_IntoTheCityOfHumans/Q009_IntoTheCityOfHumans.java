package quests.Q009_IntoTheCityOfHumans;

import com.it.br.gameserver.model.actor.instance.L2NpcInstance;
import com.it.br.gameserver.model.actor.instance.L2PcInstance;
import com.it.br.gameserver.model.base.Race;
import com.it.br.gameserver.model.quest.Quest;
import com.it.br.gameserver.model.quest.QuestState;
import com.it.br.gameserver.model.quest.State;

public class Q009_IntoTheCityOfHumans extends Quest
{
	private static final String qn = "Q009_IntoTheCityOfHumans";

	// NPCs
	private static final int PETUKAI = 30583;
	private static final int TANAPI = 30571;
	private static final int TAMIL = 30576;

	// Rewards
	private static final int MARK_OF_TRAVELER = 7570;
	private static final int SOE_GIRAN = 7126;

	private State CREATED;
	private State STARTED;
	private State COMPLETED;

	public Q009_IntoTheCityOfHumans(int questId, String name, String descr)
	{
		super(questId, name, descr);
		CREATED = new State("Start", this);
		STARTED = new State("Started", this);
		COMPLETED = new State("Completed", this);
		this.setInitialState(CREATED);

		addStartNpc(PETUKAI);
		addTalkId(PETUKAI, TANAPI, TAMIL);
	}

	@Override
	public String onAdvEvent(String event, L2NpcInstance npc, L2PcInstance player)
	{
		String htmltext = event;
		QuestState st = player.getQuestState(qn);
		if (st == null)
			return htmltext;

		if (event.equalsIgnoreCase("30583-01.htm"))
		{
			st.setState(STARTED);
			st.set("cond", "1");
			st.playSound(SOUND_QUEST_START);
		}
		else if (event.equalsIgnoreCase("30571-01.htm"))
		{
			st.set("cond", "2");
			st.playSound(SOUND_QUEST_MIDDLE);
		}
		else if (event.equalsIgnoreCase("30576-01.htm"))
		{
			st.giveItems(MARK_OF_TRAVELER, 1);
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
			if (player.getLevel() >= 3 && player.getRace() == Race.orc)
				htmltext = "30583-00.htm";
			else
				htmltext = "30583-00a.htm";
		}
		else if (st.getState() == STARTED)
		{
			int cond = st.getInt("cond");
			switch (npc.getNpcId())
			{
				case PETUKAI:
					if (cond == 1)
						htmltext = "30583-01a.htm";
					break;

				case TANAPI:
					if (cond == 1)
						htmltext = "30571-00.htm";
					else if (cond == 2)
						htmltext = "30571-01a.htm";
					break;

				case TAMIL:
					if (cond == 2)
						htmltext = "30576-00.htm";
					break;
			}
		}
		else if (st.getState() == COMPLETED)
			htmltext = getAlreadyCompletedMsg();

		return htmltext;
	}

	public static void main(String[] args)
	{
		new Q009_IntoTheCityOfHumans(9, qn, "Into the City of Humans");
	}
}