package quests.Q044_HelpTheSon;

import com.it.br.gameserver.model.actor.instance.L2NpcInstance;
import com.it.br.gameserver.model.actor.instance.L2PcInstance;
import com.it.br.gameserver.model.quest.Quest;
import com.it.br.gameserver.model.quest.QuestState;
import com.it.br.gameserver.model.quest.State;

public class Q044_HelpTheSon extends Quest
{
	private static final String qn = "Q044_HelpTheSon";

	// Npcs
	private static final int LUNDY = 30827;
	private static final int DRIKUS = 30505;

	// Items
	private static final int WORK_HAMMER = 168;
	private static final int GEMSTONE_FRAGMENT = 7552;
	private static final int GEMSTONE = 7553;
	private static final int PET_TICKET = 7585;

	// Monsters
	private static final int MAILLE = 20919;
	private static final int MAILLE_SCOUT = 20920;
	private static final int MAILLE_GUARD = 20921;

	private State CREATED;
	private State STARTED;
	private State COMPLETED;

	public Q044_HelpTheSon(int questId, String name, String descr)
	{
		super(questId, name, descr);
		CREATED = new State("Start", this);
		STARTED = new State("Started", this);
		COMPLETED = new State("Completed", this);
		this.setInitialState(CREATED);

		questItemIds(GEMSTONE_FRAGMENT, GEMSTONE);

		addStartNpc(LUNDY);
		addTalkId(LUNDY, DRIKUS);

		addKillId(MAILLE, MAILLE_SCOUT, MAILLE_GUARD);
	}

	@Override
	public String onAdvEvent(String event, L2NpcInstance npc, L2PcInstance player)
	{
		String htmltext = event;
		QuestState st = player.getQuestState(qn);
		if (st == null)
			return htmltext;

		if (event.equalsIgnoreCase("30827-01.htm"))
		{
			st.setState(STARTED);
			st.set("cond", "1");
			st.playSound(SOUND_QUEST_START);
		}
		else if (event.equalsIgnoreCase("30827-03.htm") && st.hasQuestItems(WORK_HAMMER))
		{
			st.set("cond", "2");
			st.playSound(SOUND_QUEST_MIDDLE);
			st.takeItems(WORK_HAMMER, 1);
		}
		else if (event.equalsIgnoreCase("30827-05.htm"))
		{
			st.set("cond", "4");
			st.playSound(SOUND_QUEST_MIDDLE);
			st.takeItems(GEMSTONE_FRAGMENT, 30);
			st.giveItems(GEMSTONE, 1);
		}
		else if (event.equalsIgnoreCase("30505-06.htm"))
		{
			st.set("cond", "5");
			st.playSound(SOUND_QUEST_MIDDLE);
			st.takeItems(GEMSTONE, 1);
		}
		else if (event.equalsIgnoreCase("30827-07.htm"))
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
			htmltext = (player.getLevel() < 24) ? "30827-00a.htm" : "30827-00.htm";

		else if (st.getState() == STARTED)
		{
			int cond = st.getInt("cond");
			switch (npc.getNpcId())
			{
				case LUNDY:
					if (cond == 1)
						htmltext = (!st.hasQuestItems(WORK_HAMMER)) ? "30827-01a.htm" : "30827-02.htm";
					else if (cond == 2)
						htmltext = "30827-03a.htm";
					else if (cond == 3)
						htmltext = "30827-04.htm";
					else if (cond == 4)
						htmltext = "30827-05a.htm";
					else if (cond == 5)
						htmltext = "30827-06.htm";
					break;

				case DRIKUS:
					if (cond == 4)
						htmltext = "30505-05.htm";
					else if (cond == 5)
						htmltext = "30505-06a.htm";
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
			st.giveItems(GEMSTONE_FRAGMENT, 1);
			if (st.getQuestItemsCount(GEMSTONE_FRAGMENT) == 30)
				st.set("cond", "3");

			else
				st.playSound(SOUND_ITEM_GET);
		}
		return null;
	}

	public static void main(String[] args)
	{
		new Q044_HelpTheSon(44, qn, "Help the Son!");
	}
}