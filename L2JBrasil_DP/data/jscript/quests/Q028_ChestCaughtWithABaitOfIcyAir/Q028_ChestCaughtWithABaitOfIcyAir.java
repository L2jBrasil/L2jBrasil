package quests.Q028_ChestCaughtWithABaitOfIcyAir;

import com.it.br.gameserver.model.actor.instance.L2NpcInstance;
import com.it.br.gameserver.model.actor.instance.L2PcInstance;
import com.it.br.gameserver.model.quest.Quest;
import com.it.br.gameserver.model.quest.QuestState;
import com.it.br.gameserver.model.quest.State;

public class Q028_ChestCaughtWithABaitOfIcyAir extends Quest
{
	private static final String qn = "Q028_ChestCaughtWithABaitOfIcyAir";

	// NPCs
	private static final int OFULLE = 31572;
	private static final int KIKI = 31442;

	// Items
	private static final int BIG_YELLOW_TREASURE_CHEST = 6503;
	private static final int KIKI_LETTER = 7626;
	private static final int ELVEN_RING = 881;

	private State CREATED;
	private State STARTED;
	private State COMPLETED;

	public Q028_ChestCaughtWithABaitOfIcyAir(int questId, String name, String descr)
	{
		super(questId, name, descr);
		CREATED = new State("Start", this);
		STARTED = new State("Started", this);
		COMPLETED = new State("Completed", this);
		this.setInitialState(CREATED);

		questItemIds(KIKI_LETTER);

		addStartNpc(OFULLE);
		addTalkId(OFULLE, KIKI);
	}

	@Override
	public String onAdvEvent(String event, L2NpcInstance npc, L2PcInstance player)
	{
		String htmltext = event;
		QuestState st = player.getQuestState(qn);
		if (st == null)
			return htmltext;

		if (event.equalsIgnoreCase("31572-04.htm"))
		{
			st.setState(STARTED);
			st.set("cond", "1");
			st.playSound(SOUND_QUEST_START);
		}
		else if (event.equalsIgnoreCase("31572-07.htm"))
		{
			if (st.hasQuestItems(BIG_YELLOW_TREASURE_CHEST))
			{
				st.set("cond", "2");
				st.takeItems(BIG_YELLOW_TREASURE_CHEST, 1);
				st.giveItems(KIKI_LETTER, 1);
			}
			else
				htmltext = "31572-08.htm";
		}
		else if (event.equalsIgnoreCase("31442-02.htm"))
		{
			if (st.hasQuestItems(KIKI_LETTER))
			{
				htmltext = "31442-02.htm";
				st.takeItems(KIKI_LETTER, 1);
				st.giveItems(ELVEN_RING, 1);
				st.playSound(SOUND_QUEST_DONE);
				st.exitQuest(false);
			}
			else
				htmltext = "31442-03.htm";
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
			if (player.getLevel() < 36)
				htmltext = "31572-02.htm";
			else
			{
				QuestState st2 = player.getQuestState("Q051_OFullesSpecialBait");
				if (st2 != null && st2.isCompleted())
					htmltext = "31572-01.htm";
				else
					htmltext = "31572-03.htm";
			}
		}
		else if (st.getState() == STARTED)
		{
			int cond = st.getInt("cond");
			switch (npc.getNpcId())
			{
				case OFULLE:
					if (cond == 1)
						htmltext = (!st.hasQuestItems(BIG_YELLOW_TREASURE_CHEST)) ? "31572-06.htm" : "31572-05.htm";
					else if (cond == 2)
						htmltext = "31572-09.htm";
					break;

				case KIKI:
					if (cond == 2)
						htmltext = "31442-01.htm";
					break;
			}
		}
		else if (st.getState() == COMPLETED)
			htmltext = getAlreadyCompletedMsg();
		return htmltext;
	}

	public static void main(String[] args)
	{
		new Q028_ChestCaughtWithABaitOfIcyAir(28, qn, "Chest caught with a bait of icy air");
	}
}