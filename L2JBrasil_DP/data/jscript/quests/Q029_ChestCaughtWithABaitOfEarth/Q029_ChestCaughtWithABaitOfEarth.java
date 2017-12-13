package quests.Q029_ChestCaughtWithABaitOfEarth;

import com.it.br.gameserver.model.actor.instance.L2NpcInstance;
import com.it.br.gameserver.model.actor.instance.L2PcInstance;
import com.it.br.gameserver.model.quest.Quest;
import com.it.br.gameserver.model.quest.QuestState;
import com.it.br.gameserver.model.quest.State;

public class Q029_ChestCaughtWithABaitOfEarth extends Quest
{
	private static final String qn = "Q029_ChestCaughtWithABaitOfEarth";

	// NPCs
	private static final int WILLIE = 31574;
	private static final int ANABEL = 30909;

	// Items
	private static final int SMALL_PURPLE_TREASURE_CHEST = 6507;
	private static final int SMALL_GLASS_BOX = 7627;
	private static final int PLATED_LEATHER_GLOVES = 2455;

	private State CREATED;
	private State STARTED;
	private State COMPLETED;

	public Q029_ChestCaughtWithABaitOfEarth(int questId, String name, String descr)
	{
		super(questId, name, descr);
		CREATED = new State("Start", this);
		STARTED = new State("Started", this);
		COMPLETED = new State("Completed", this);
		this.setInitialState(CREATED);
		questItemIds(SMALL_GLASS_BOX);

		addStartNpc(WILLIE);
		addTalkId(WILLIE, ANABEL);
	}

	@Override
	public String onAdvEvent(String event, L2NpcInstance npc, L2PcInstance player)
	{
		String htmltext = event;
		QuestState st = player.getQuestState(qn);
		if (st == null)
			return htmltext;

		if (event.equalsIgnoreCase("31574-04.htm"))
		{
			st.setState(STARTED);
			st.set("cond", "1");
			st.playSound(SOUND_QUEST_START);
		}
		else if (event.equalsIgnoreCase("31574-07.htm"))
		{
			if (st.hasQuestItems(SMALL_PURPLE_TREASURE_CHEST))
			{
				st.set("cond", "2");
				st.takeItems(SMALL_PURPLE_TREASURE_CHEST, 1);
				st.giveItems(SMALL_GLASS_BOX, 1);
			}
			else
				htmltext = "31574-08.htm";
		}
		else if (event.equalsIgnoreCase("30909-02.htm"))
		{
			if (st.hasQuestItems(SMALL_GLASS_BOX))
			{
				htmltext = "30909-02.htm";
				st.takeItems(SMALL_GLASS_BOX, 1);
				st.giveItems(PLATED_LEATHER_GLOVES, 1);
				st.playSound(SOUND_QUEST_DONE);
				st.exitQuest(false);
			}
			else
				htmltext = "30909-03.htm";
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
			if (player.getLevel() < 48)
				htmltext = "31574-02.htm";
			else
			{
				QuestState st2 = player.getQuestState("Q052_WilliesSpecialBait");
				if (st2 != null && st2.isCompleted())
					htmltext = "31574-01.htm";
				else
					htmltext = "31574-03.htm";
			}
		}
		else if (st.getState() == STARTED)
		{
			int cond = st.getInt("cond");
			switch (npc.getNpcId())
			{
				case WILLIE:
					if (cond == 1)
						htmltext = (!st.hasQuestItems(SMALL_PURPLE_TREASURE_CHEST)) ? "31574-06.htm" : "31574-05.htm";
					else if (cond == 2)
						htmltext = "31574-09.htm";
					break;

				case ANABEL:
					if (cond == 2)
						htmltext = "30909-01.htm";
					break;
			}
		}
		else if (st.getState() == COMPLETED)
			htmltext = getAlreadyCompletedMsg();

		return htmltext;
	}

	public static void main(String[] args)
	{
		new Q029_ChestCaughtWithABaitOfEarth(29, qn, "Chest caught with a bait of earth");
	}
}