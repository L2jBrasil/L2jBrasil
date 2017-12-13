package quests.Q027_ChestCaughtWithABaitOfWind;

import com.it.br.gameserver.model.actor.instance.L2NpcInstance;
import com.it.br.gameserver.model.actor.instance.L2PcInstance;
import com.it.br.gameserver.model.quest.Quest;
import com.it.br.gameserver.model.quest.QuestState;
import com.it.br.gameserver.model.quest.State;

public class Q027_ChestCaughtWithABaitOfWind extends Quest
{
	private static final String qn = "Q027_ChestCaughtWithABaitOfWind";

	// NPCs
	private static final int LANOSCO = 31570;
	private static final int SHALING = 31442;

	// Items
	private static final int LARGE_BLUE_TREASURE_CHEST = 6500;
	private static final int STRANGE_BLUEPRINT = 7625;
	private static final int BLACK_PEARL_RING = 880;

	private State CREATED;
	private State STARTED;
	private State COMPLETED;

	public Q027_ChestCaughtWithABaitOfWind(int questId, String name, String descr)
	{
		super(questId, name, descr);
		CREATED = new State("Start", this);
		STARTED = new State("Started", this);
		COMPLETED = new State("Completed", this);
		this.setInitialState(CREATED);

		questItemIds(STRANGE_BLUEPRINT);

		addStartNpc(LANOSCO);
		addTalkId(LANOSCO, SHALING);
	}

	@Override
	public String onAdvEvent(String event, L2NpcInstance npc, L2PcInstance player)
	{
		String htmltext = event;
		QuestState st = player.getQuestState(qn);
		if (st == null)
			return htmltext;

		if (event.equalsIgnoreCase("31570-04.htm"))
		{
			st.setState(STARTED);
			st.set("cond", "1");
			st.playSound(SOUND_QUEST_START);
		}
		else if (event.equalsIgnoreCase("31570-07.htm"))
		{
			if (st.hasQuestItems(LARGE_BLUE_TREASURE_CHEST))
			{
				st.set("cond", "2");
				st.takeItems(LARGE_BLUE_TREASURE_CHEST, 1);
				st.giveItems(STRANGE_BLUEPRINT, 1);
			}
			else
				htmltext = "31570-08.htm";
		}
		else if (event.equalsIgnoreCase("31434-02.htm"))
		{
			if (st.hasQuestItems(STRANGE_BLUEPRINT))
			{
				htmltext = "31434-02.htm";
				st.takeItems(STRANGE_BLUEPRINT, 1);
				st.giveItems(BLACK_PEARL_RING, 1);
				st.playSound(SOUND_QUEST_DONE);
				st.exitQuest(false);
			}
			else
				htmltext = "31434-03.htm";
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
			if (player.getLevel() < 27)
				htmltext = "31570-02.htm";
			else
			{
				QuestState st2 = player.getQuestState("Q050_LanoscosSpecialBait");
				if (st2 != null && st2.isCompleted())
					htmltext = "31570-01.htm";
				else
					htmltext = "31570-03.htm";
			}
		}
		else if (st.getState() == STARTED)
		{
			int cond = st.getInt("cond");
			switch (npc.getNpcId())
			{
				case LANOSCO:
					if (cond == 1)
						htmltext = (!st.hasQuestItems(LARGE_BLUE_TREASURE_CHEST)) ? "31570-06.htm" : "31570-05.htm";
					else if (cond == 2)
						htmltext = "31570-09.htm";
					break;

				case SHALING:
					if (cond == 2)
						htmltext = "31434-01.htm";
					break;
			}
		}
		else if (st.getState() == COMPLETED)
			htmltext = getAlreadyCompletedMsg();
		return htmltext;
	}

	public static void main(String[] args)
	{
		new Q027_ChestCaughtWithABaitOfWind(27, qn, "Chest caught with a bait of wind");
	}
}