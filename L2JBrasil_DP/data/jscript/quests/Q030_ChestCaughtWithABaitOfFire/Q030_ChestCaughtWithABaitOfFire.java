package quests.Q030_ChestCaughtWithABaitOfFire;

import com.it.br.gameserver.model.actor.instance.L2NpcInstance;
import com.it.br.gameserver.model.actor.instance.L2PcInstance;
import com.it.br.gameserver.model.quest.Quest;
import com.it.br.gameserver.model.quest.QuestState;
import com.it.br.gameserver.model.quest.State;

public class Q030_ChestCaughtWithABaitOfFire extends Quest
{
	private static final String qn = "Q030_ChestCaughtWithABaitOfFire";

	// NPCs
	private static final int LINNAEUS = 31577;
	private static final int RUKAL = 30629;

	// Items
	private static final int RED_TREASURE_BOX = 6511;
	private static final int MUSICAL_SCORE = 7628;
	private static final int NECKLACE_OF_PROTECTION = 916;

	private State CREATED;
	private State STARTED;
	private State COMPLETED;

	public Q030_ChestCaughtWithABaitOfFire(int questId, String name, String descr)
	{
		super(questId, name, descr);
		CREATED = new State("Start", this);
		STARTED = new State("Started", this);
		COMPLETED = new State("Completed", this);
		this.setInitialState(CREATED);

		questItemIds(MUSICAL_SCORE);

		addStartNpc(LINNAEUS);
		addTalkId(LINNAEUS, RUKAL);
	}

	@Override
	public String onAdvEvent(String event, L2NpcInstance npc, L2PcInstance player)
	{
		String htmltext = event;
		QuestState st = player.getQuestState(qn);
		if (st == null)
			return htmltext;

		if (event.equalsIgnoreCase("31577-04.htm"))
		{
			st.setState(STARTED);
			st.set("cond", "1");
			st.playSound(SOUND_QUEST_START);
		}
		else if (event.equalsIgnoreCase("31577-07.htm"))
		{
			if (st.hasQuestItems(RED_TREASURE_BOX))
			{
				st.set("cond", "2");
				st.takeItems(RED_TREASURE_BOX, 1);
				st.giveItems(MUSICAL_SCORE, 1);
			}
			else
				htmltext = "31577-08.htm";
		}
		else if (event.equalsIgnoreCase("30629-02.htm"))
		{
			if (st.hasQuestItems(MUSICAL_SCORE))
			{
				htmltext = "30629-02.htm";
				st.takeItems(MUSICAL_SCORE, 1);
				st.giveItems(NECKLACE_OF_PROTECTION, 1);
				st.playSound(SOUND_QUEST_DONE);
				st.exitQuest(false);
			}
			else
				htmltext = "30629-03.htm";
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
			if (player.getLevel() < 60)
				htmltext = "31577-02.htm";
			else
			{
				QuestState st2 = player.getQuestState("Q053_LinnaeusSpecialBait");
				if (st2 != null && st2.isCompleted())
					htmltext = "31577-01.htm";
				else
					htmltext = "31577-03.htm";
			}
		}
		else if (st.getState() == STARTED)
		{
			int cond = st.getInt("cond");
			switch (npc.getNpcId())
			{
				case LINNAEUS:
					if (cond == 1)
						htmltext = (!st.hasQuestItems(RED_TREASURE_BOX)) ? "31577-06.htm" : "31577-05.htm";
					else if (cond == 2)
						htmltext = "31577-09.htm";
					break;

				case RUKAL:
					if (cond == 2)
						htmltext = "30629-01.htm";
					break;
			}
		}

		else if (st.getState() == COMPLETED)
			htmltext = getAlreadyCompletedMsg();

		return htmltext;
	}

	public static void main(String[] args)
	{
		new Q030_ChestCaughtWithABaitOfFire(30, qn, "Chest caught with a bait of fire");
	}
}