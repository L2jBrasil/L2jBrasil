package quests.Q002_WhatWomenWant;

import com.it.br.gameserver.model.actor.instance.L2NpcInstance;
import com.it.br.gameserver.model.actor.instance.L2PcInstance;
import com.it.br.gameserver.model.base.Race;
import com.it.br.gameserver.model.quest.Quest;
import com.it.br.gameserver.model.quest.QuestState;
import com.it.br.gameserver.model.quest.State;

public class Q002_WhatWomenWant extends Quest
{
	private static final String qn = "Q002_WhatWomenWant";

	// NPCs
	private static final int ARUJIEN = 30223;
	private static final int MIRABEL = 30146;
	private static final int HERBIEL = 30150;
	private static final int GREENIS = 30157;

	// Items
	private static final int ARUJIEN_LETTER_1 = 1092;
	private static final int ARUJIEN_LETTER_2 = 1093;
	private static final int ARUJIEN_LETTER_3 = 1094;
	private static final int POETRY_BOOK = 689;
	private static final int GREENIS_LETTER = 693;

	// Rewards
	private static final int MYSTICS_EARRING = 113;

	private State CREATED;
	private State STARTED;
	private State COMPLETED;

	public Q002_WhatWomenWant(int questId, String name, String descr)
	{
		super(questId, name, descr);
		CREATED = new State("Start", this);
		STARTED = new State("Started", this);
		COMPLETED = new State("Completed", this);
		this.setInitialState(CREATED);
		questItemIds(ARUJIEN_LETTER_1, ARUJIEN_LETTER_2, ARUJIEN_LETTER_3, POETRY_BOOK, GREENIS_LETTER);

		addStartNpc(ARUJIEN);
		addTalkId(ARUJIEN, MIRABEL, HERBIEL, GREENIS);
	}

	@Override
	public String onAdvEvent(String event, L2NpcInstance npc, L2PcInstance player)
	{
		String htmltext = event;
		QuestState st = player.getQuestState(qn);
		if (st == null)
			return htmltext;

		if (event.equalsIgnoreCase("30223-04.htm"))
		{
			st.setState(STARTED);
			st.set("cond", "1");
			st.playSound(SOUND_QUEST_START);
			st.giveItems(ARUJIEN_LETTER_1, 1);
		}
		else if (event.equalsIgnoreCase("30223-08.htm"))
		{
			st.set("cond", "4");
			st.playSound(SOUND_QUEST_MIDDLE);
			st.takeItems(ARUJIEN_LETTER_3, 1);
			st.giveItems(POETRY_BOOK, 1);
		}
		else if (event.equalsIgnoreCase("30223-09.htm"))
		{
			st.takeItems(ARUJIEN_LETTER_3, 1);
			st.rewardItems(57, 450);
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
		{
			if (player.getRace() != Race.elf && player.getRace() != Race.human)
				htmltext = "30223-00.htm";
			else if (player.getLevel() < 2)
				htmltext = "30223-01.htm";
			else
				htmltext = "30223-02.htm";
		}
		else if (st.getState() == STARTED)
		{
			int cond = st.getInt("cond");
			switch (npc.getNpcId())
			{
				case ARUJIEN:
					if (st.hasQuestItems(ARUJIEN_LETTER_1))
						htmltext = "30223-05.htm";
					else if (st.hasQuestItems(ARUJIEN_LETTER_3))
						htmltext = "30223-07.htm";
					else if (st.hasQuestItems(ARUJIEN_LETTER_2))
						htmltext = "30223-06.htm";
					else if (st.hasQuestItems(POETRY_BOOK))
						htmltext = "30223-11.htm";
					else if (st.hasQuestItems(GREENIS_LETTER))
					{
						htmltext = "30223-10.htm";
						st.takeItems(GREENIS_LETTER, 1);
						st.giveItems(MYSTICS_EARRING, 1);
						st.playSound(SOUND_QUEST_DONE);
						st.exitQuest(false);
					}
					break;

				case MIRABEL:
					if (cond == 1)
					{
						htmltext = "30146-01.htm";
						st.set("cond", "2");
						st.playSound(SOUND_QUEST_MIDDLE);
						st.takeItems(ARUJIEN_LETTER_1, 1);
						st.giveItems(ARUJIEN_LETTER_2, 1);
					}
					else if (cond > 1)
						htmltext = "30146-02.htm";
					break;

				case HERBIEL:
					if (cond == 2)
					{
						htmltext = "30150-01.htm";
						st.set("cond", "3");
						st.playSound(SOUND_QUEST_MIDDLE);
						st.takeItems(ARUJIEN_LETTER_2, 1);
						st.giveItems(ARUJIEN_LETTER_3, 1);
					}
					else if (cond > 2)
						htmltext = "30150-02.htm";
					break;

				case GREENIS:
					if (cond < 4)
						htmltext = "30157-01.htm";
					else if (cond == 4)
					{
						htmltext = "30157-02.htm";
						st.set("cond", "5");
						st.playSound(SOUND_QUEST_MIDDLE);
						st.takeItems(POETRY_BOOK, 1);
						st.giveItems(GREENIS_LETTER, 1);
					}
					else if (cond == 5)
						htmltext = "30157-03.htm";
					break;
			}
		}

		else if (st.getState() == COMPLETED)
			htmltext = getAlreadyCompletedMsg();
		return htmltext;
	}

	public static void main(String[] args)
	{
		new Q002_WhatWomenWant(2, qn, "What Women Want");
	}
}