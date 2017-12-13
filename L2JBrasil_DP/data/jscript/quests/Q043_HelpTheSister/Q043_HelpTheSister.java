package quests.Q043_HelpTheSister;

import com.it.br.gameserver.model.actor.instance.L2NpcInstance;
import com.it.br.gameserver.model.actor.instance.L2PcInstance;
import com.it.br.gameserver.model.quest.Quest;
import com.it.br.gameserver.model.quest.QuestState;
import com.it.br.gameserver.model.quest.State;

public class Q043_HelpTheSister extends Quest
{
	private static final String qn = "Q043_HelpTheSister";

	// NPCs
	private static final int COOPER = 30829;
	private static final int GALLADUCCI = 30097;

	// Items
	private static final int CRAFTED_DAGGER = 220;
	private static final int MAP_PIECE = 7550;
	private static final int MAP = 7551;
	private static final int PET_TICKET = 7584;

	// Monsters
	private static final int SPECTER = 20171;
	private static final int SORROW_MAIDEN = 20197;

	private State CREATED;
	private State STARTED;
	private State COMPLETED;

	public Q043_HelpTheSister(int questId, String name, String descr)
	{
		super(questId, name, descr);
		CREATED = new State("Start", this);
		STARTED = new State("Started", this);
		COMPLETED = new State("Completed", this);
		this.setInitialState(CREATED);

		questItemIds(MAP_PIECE, MAP);

		addStartNpc(COOPER);
		addTalkId(COOPER, GALLADUCCI);

		addKillId(SPECTER, SORROW_MAIDEN);
	}

	@Override
	public String onAdvEvent(String event, L2NpcInstance npc, L2PcInstance player)
	{
		String htmltext = event;
		QuestState st = player.getQuestState(qn);
		if (st == null)
			return htmltext;

		if (event.equalsIgnoreCase("30829-01.htm"))
		{
			st.setState(STARTED);
			st.set("cond", "1");
			st.playSound(SOUND_QUEST_START);
		}
		else if (event.equalsIgnoreCase("30829-03.htm") && st.hasQuestItems(CRAFTED_DAGGER))
		{
			st.set("cond", "2");
			st.playSound(SOUND_QUEST_MIDDLE);
			st.takeItems(CRAFTED_DAGGER, 1);
		}
		else if (event.equalsIgnoreCase("30829-05.htm"))
		{
			st.set("cond", "4");
			st.playSound(SOUND_QUEST_MIDDLE);
			st.takeItems(MAP_PIECE, 30);
			st.giveItems(MAP, 1);
		}
		else if (event.equalsIgnoreCase("30097-06.htm"))
		{
			st.set("cond", "5");
			st.playSound(SOUND_QUEST_MIDDLE);
			st.takeItems(MAP, 1);
		}
		else if (event.equalsIgnoreCase("30829-07.htm"))
		{
			st.giveItems(PET_TICKET, 1);
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
			htmltext = (player.getLevel() < 26) ? "30829-00a.htm" : "30829-00.htm";

		else if (st.getState() == STARTED)
		{
			int cond = st.getInt("cond");
			switch (npc.getNpcId())
			{
				case COOPER:
					if (cond == 1)
						htmltext = (!st.hasQuestItems(CRAFTED_DAGGER)) ? "30829-01a.htm" : "30829-02.htm";
					else if (cond == 2)
						htmltext = "30829-03a.htm";
					else if (cond == 3)
						htmltext = "30829-04.htm";
					else if (cond == 4)
						htmltext = "30829-05a.htm";
					else if (cond == 5)
						htmltext = "30829-06.htm";
					break;

				case GALLADUCCI:
					if (cond == 4)
						htmltext = "30097-05.htm";
					else if (cond == 5)
						htmltext = "30097-06a.htm";
					break;
			}
		}
		else if (st.getState() == COMPLETED)
			htmltext = getAlreadyCompletedMsg();

		return htmltext;
	}

	@Override
	public String onKill(L2NpcInstance npc, L2PcInstance player, boolean isPet)
	{
		final QuestState st = player.getQuestState(qn);
		if (st.getState() != STARTED)
			return null;
		if (st.getInt("cond") == 2)
		{
			st.giveItems(MAP_PIECE, 1);
			if (st.getQuestItemsCount(MAP_PIECE) == 30)
				st.set("cond", "3");

			else
				st.playSound(SOUND_ITEM_GET);
		}
		return null;
	}

	public static void main(String[] args)
	{
		new Q043_HelpTheSister(43, qn, "Help the Sister!");
	}

}