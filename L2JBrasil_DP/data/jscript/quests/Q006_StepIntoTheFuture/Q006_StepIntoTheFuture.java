package quests.Q006_StepIntoTheFuture;

import com.it.br.gameserver.model.actor.instance.L2NpcInstance;
import com.it.br.gameserver.model.actor.instance.L2PcInstance;
import com.it.br.gameserver.model.base.Race;
import com.it.br.gameserver.model.quest.Quest;
import com.it.br.gameserver.model.quest.QuestState;
import com.it.br.gameserver.model.quest.State;

public class Q006_StepIntoTheFuture extends Quest
{
	private static final String qn = "Q006_StepIntoTheFuture";

	// NPCs
	private static final int ROXXY = 30006;
	private static final int BAULRO = 30033;
	private static final int SIR_COLLIN = 30311;

	// Items
	private static final int BAULRO_LETTER = 7571;

	// Rewards
	private static final int MARK_TRAVELER = 7570;
	private static final int SOE_GIRAN = 7559;

	private State CREATED;
	private State STARTED;
	private State COMPLETED;

	public Q006_StepIntoTheFuture(int questId, String name, String descr)
	{
		super(questId, name, descr);
		CREATED = new State("Start", this);
		STARTED = new State("Started", this);
		COMPLETED = new State("Completed", this);
		this.setInitialState(CREATED);
		questItemIds(BAULRO_LETTER);

		addStartNpc(ROXXY);
		addTalkId(ROXXY, BAULRO, SIR_COLLIN);
	}

	@Override
	public String onAdvEvent(String event, L2NpcInstance npc, L2PcInstance player)
	{
		String htmltext = event;
		QuestState st = player.getQuestState(qn);
		if (st == null)
			return htmltext;

		if (event.equalsIgnoreCase("30006-03.htm"))
		{
			st.setState(STARTED);
			st.set("cond", "1");
			st.playSound(SOUND_QUEST_START);
		}
		else if (event.equalsIgnoreCase("30033-02.htm"))
		{
			st.set("cond", "2");
			st.playSound(SOUND_QUEST_MIDDLE);
			st.giveItems(BAULRO_LETTER, 1);
		}
		else if (event.equalsIgnoreCase("30311-02.htm"))
		{
			if (st.hasQuestItems(BAULRO_LETTER))
			{
				st.set("cond", "3");
				st.playSound(SOUND_QUEST_MIDDLE);
				st.takeItems(BAULRO_LETTER, 1);
			}
			else
				htmltext = "30311-03.htm";
		}
		else if (event.equalsIgnoreCase("30006-06.htm"))
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
			if (player.getRace() != Race.human || player.getLevel() < 3)
				htmltext = "30006-01.htm";
			else
				htmltext = "30006-02.htm";
		}
		else if (st.getState() == STARTED)
		{
			int cond = st.getInt("cond");
			switch (npc.getNpcId())
			{
				case ROXXY:
					if (cond == 1 || cond == 2)
						htmltext = "30006-04.htm";
					else if (cond == 3)
						htmltext = "30006-05.htm";
					break;

				case BAULRO:
					if (cond == 1)
						htmltext = "30033-01.htm";
					else if (cond == 2)
						htmltext = "30033-03.htm";
					else
						htmltext = "30033-04.htm";
					break;

				case SIR_COLLIN:
					if (cond == 2)
						htmltext = "30311-01.htm";
					else if (cond == 3)
						htmltext = "30311-03a.htm";
					break;
			}
		}
		else if (st.getState() == COMPLETED)
			htmltext = getAlreadyCompletedMsg();

		return htmltext;
	}

	public static void main(String[] args)
	{
		new Q006_StepIntoTheFuture(6, qn, "Step into the Future");
	}
}