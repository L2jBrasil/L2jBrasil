package quests.Q032_AnObviousLie;

import com.it.br.gameserver.model.actor.instance.L2NpcInstance;
import com.it.br.gameserver.model.actor.instance.L2PcInstance;
import com.it.br.gameserver.model.quest.Quest;
import com.it.br.gameserver.model.quest.QuestState;
import com.it.br.gameserver.model.quest.State;

public class Q032_AnObviousLie extends Quest
{
	private static final String qn = "Q032_AnObviousLie";

	// Items
	private static final int SUEDE = 1866;
	private static final int THREAD = 1868;
	private static final int SPIRIT_ORE = 3031;
	private static final int MAP = 7165;
	private static final int MEDICINAL_HERB = 7166;

	// Rewards
	private static final int CAT_EARS = 6843;
	private static final int RACOON_EARS = 7680;
	private static final int RABBIT_EARS = 7683;

	// NPCs
	private static final int GENTLER = 30094;
	private static final int MAXIMILIAN = 30120;
	private static final int MIKI_THE_CAT = 31706;

	private State CREATED;
	private State STARTED;
	private State COMPLETED;

	public Q032_AnObviousLie(int questId, String name, String descr)
	{
		super(questId, name, descr);
		CREATED = new State("Start", this);
		STARTED = new State("Started", this);
		COMPLETED = new State("Completed", this);
		this.setInitialState(CREATED);

		questItemIds(MAP, MEDICINAL_HERB);

		addStartNpc(MAXIMILIAN);
		addTalkId(MAXIMILIAN, GENTLER, MIKI_THE_CAT);

		addKillId(20135); // Alligator
	}

	@Override
	public String onAdvEvent(String event, L2NpcInstance npc, L2PcInstance player)
	{
		String htmltext = event;
		QuestState st = player.getQuestState(qn);
		if (st == null)
			return htmltext;

		if (event.equalsIgnoreCase("30120-1.htm"))
		{
			st.setState(STARTED);
			st.set("cond", "1");
			st.playSound(SOUND_QUEST_START);
		}
		else if (event.equalsIgnoreCase("30094-1.htm"))
		{
			st.set("cond", "2");
			st.playSound(SOUND_QUEST_MIDDLE);
			st.giveItems(MAP, 1);
		}
		else if (event.equalsIgnoreCase("31706-1.htm"))
		{
			st.set("cond", "3");
			st.playSound(SOUND_QUEST_MIDDLE);
			st.takeItems(MAP, 1);
		}
		else if (event.equalsIgnoreCase("30094-4.htm"))
		{
			st.set("cond", "5");
			st.playSound(SOUND_QUEST_MIDDLE);
			st.takeItems(MEDICINAL_HERB, 20);
		}
		else if (event.equalsIgnoreCase("30094-7.htm"))
		{
			if (st.getQuestItemsCount(SPIRIT_ORE) < 500)
				htmltext = "30094-5.htm";
			else
			{
				st.set("cond", "6");
				st.playSound(SOUND_QUEST_MIDDLE);
				st.takeItems(SPIRIT_ORE, 500);
			}
		}
		else if (event.equalsIgnoreCase("31706-4.htm"))
		{
			st.set("cond", "7");
			st.playSound(SOUND_QUEST_MIDDLE);
		}
		else if (event.equalsIgnoreCase("30094-10.htm"))
		{
			st.set("cond", "8");
			st.playSound(SOUND_QUEST_MIDDLE);
		}
		else if (event.equalsIgnoreCase("30094-13.htm"))
			st.playSound(SOUND_QUEST_MIDDLE);
		else if (event.equalsIgnoreCase("cat"))
		{
			if (st.getQuestItemsCount(THREAD) < 1000 || st.getQuestItemsCount(SUEDE) < 500)
				htmltext = "30094-11.htm";
			else
			{
				htmltext = "30094-14.htm";
				st.takeItems(SUEDE, 500);
				st.takeItems(THREAD, 1000);
				st.giveItems(CAT_EARS, 1);
				st.playSound(SOUND_QUEST_DONE);
				st.exitQuest(false);
			}
		}
		else if (event.equalsIgnoreCase("racoon"))
		{
			if (st.getQuestItemsCount(THREAD) < 1000 || st.getQuestItemsCount(SUEDE) < 500)
				htmltext = "30094-11.htm";
			else
			{
				htmltext = "30094-14.htm";
				st.takeItems(SUEDE, 500);
				st.takeItems(THREAD, 1000);
				st.giveItems(RACOON_EARS, 1);
				st.playSound(SOUND_QUEST_DONE);
				st.exitQuest(false);
			}
		}
		else if (event.equalsIgnoreCase("rabbit"))
		{
			if (st.getQuestItemsCount(THREAD) < 1000 || st.getQuestItemsCount(SUEDE) < 500)
				htmltext = "30094-11.htm";
			else
			{
				htmltext = "30094-14.htm";
				st.takeItems(SUEDE, 500);
				st.takeItems(THREAD, 1000);
				st.giveItems(RABBIT_EARS, 1);
				st.playSound(SOUND_QUEST_DONE);
				st.exitQuest(false);
			}
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
			htmltext = (player.getLevel() < 45) ? "30120-0a.htm" : "30120-0.htm";

		else if (st.getState() == STARTED)
		{
			int cond = st.getInt("cond");
			switch (npc.getNpcId())
			{
				case MAXIMILIAN:
					htmltext = "30120-2.htm";
					break;

				case GENTLER:
					if (cond == 1)
						htmltext = "30094-0.htm";
					else if (cond == 2 || cond == 3)
						htmltext = "30094-2.htm";
					else if (cond == 4)
						htmltext = "30094-3.htm";
					else if (cond == 5)
						htmltext = (st.getQuestItemsCount(SPIRIT_ORE) < 500) ? "30094-5.htm" : "30094-6.htm";
					else if (cond == 6)
						htmltext = "30094-8.htm";
					else if (cond == 7)
						htmltext = "30094-9.htm";
					else if (cond == 8)
						htmltext = (st.getQuestItemsCount(THREAD) < 1000 || st.getQuestItemsCount(SUEDE) < 500)
						        ? "30094-11.htm" : "30094-12.htm";
					break;

				case MIKI_THE_CAT:
					if (cond == 2)
						htmltext = "31706-0.htm";
					else if (cond > 2 && cond < 6)
						htmltext = "31706-2.htm";
					else if (cond == 6)
						htmltext = "31706-3.htm";
					else if (cond > 6)
						htmltext = "31706-5.htm";
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

		int count = st.getQuestItemsCount(MEDICINAL_HERB);

		if (st.getRandom(100) < 30 && st.getInt("cond") == 3)
		{
			if (count < 20)
				st.giveItems(MEDICINAL_HERB, 1);
			if (count == 19)
			{
				st.playSound(SOUND_QUEST_MIDDLE);
				st.set("cond", "4");
			}
			else
				st.playSound(SOUND_ITEM_GET);
		}
		return null;
	}

	public static void main(String[] args)
	{
		new Q032_AnObviousLie(32, qn, "An Obvious Lie");
	}
}