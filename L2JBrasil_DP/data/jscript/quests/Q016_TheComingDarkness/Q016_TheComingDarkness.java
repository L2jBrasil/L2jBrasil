package quests.Q016_TheComingDarkness;

import com.it.br.gameserver.model.actor.instance.L2NpcInstance;
import com.it.br.gameserver.model.actor.instance.L2PcInstance;
import com.it.br.gameserver.model.quest.Quest;
import com.it.br.gameserver.model.quest.QuestState;
import com.it.br.gameserver.model.quest.State;

public class Q016_TheComingDarkness extends Quest
{
	private static final String qn = "Q016_TheComingDarkness";

	// NPCs
	private static final int HIERARCH = 31517;
	private static final int EVIL_ALTAR_1 = 31512;
	private static final int EVIL_ALTAR_2 = 31513;
	private static final int EVIL_ALTAR_3 = 31514;
	private static final int EVIL_ALTAR_4 = 31515;
	private static final int EVIL_ALTAR_5 = 31516;

	// Item
	private static final int CRYSTAL_OF_SEAL = 7167;

	private State CREATED;
	private State STARTED;
	private State COMPLETED;

	public Q016_TheComingDarkness(int questId, String name, String descr)
	{
		super(questId, name, descr);
		CREATED = new State("Start", this);
		STARTED = new State("Started", this);
		COMPLETED = new State("Completed", this);
		this.setInitialState(CREATED);

		questItemIds(CRYSTAL_OF_SEAL);

		addStartNpc(HIERARCH);
		addTalkId(HIERARCH, EVIL_ALTAR_1, EVIL_ALTAR_2, EVIL_ALTAR_3, EVIL_ALTAR_4, EVIL_ALTAR_5);
	}

	@Override
	public String onAdvEvent(String event, L2NpcInstance npc, L2PcInstance player)
	{
		String htmltext = event;
		QuestState st = player.getQuestState(qn);
		if (st == null)
			return htmltext;

		if (event.equalsIgnoreCase("31517-2.htm"))
		{
			st.setState(STARTED);
			st.set("cond", "1");
			st.playSound(SOUND_QUEST_START);
			st.giveItems(CRYSTAL_OF_SEAL, 5);
		}
		else if (event.equalsIgnoreCase("31512-1.htm"))
		{
			st.set("cond", "2");
			st.playSound(SOUND_QUEST_MIDDLE);
			st.takeItems(CRYSTAL_OF_SEAL, 1);
		}
		else if (event.equalsIgnoreCase("31513-1.htm"))
		{
			st.set("cond", "3");
			st.playSound(SOUND_QUEST_MIDDLE);
			st.takeItems(CRYSTAL_OF_SEAL, 1);
		}
		else if (event.equalsIgnoreCase("31514-1.htm"))
		{
			st.set("cond", "4");
			st.playSound(SOUND_QUEST_MIDDLE);
			st.takeItems(CRYSTAL_OF_SEAL, 1);
		}
		else if (event.equalsIgnoreCase("31515-1.htm"))
		{
			st.set("cond", "5");
			st.playSound(SOUND_QUEST_MIDDLE);
			st.takeItems(CRYSTAL_OF_SEAL, 1);
		}
		else if (event.equalsIgnoreCase("31516-1.htm"))
		{
			st.set("cond", "6");
			st.playSound(SOUND_QUEST_MIDDLE);
			st.takeItems(CRYSTAL_OF_SEAL, 1);
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
			htmltext = (player.getLevel() < 62) ? "31517-0a.htm" : "31517-0.htm";

		else if (st.getState() == STARTED)
		{
			int cond = st.getInt("cond");
			int npcId = npc.getNpcId();

			switch (npcId)
			{
				case HIERARCH:
					if (cond == 6)
					{
						htmltext = "31517-4.htm";
						st.addExpAndSp(221958, 0);
						st.playSound(SOUND_QUEST_DONE);
						st.exitQuest(false);
					}
					else
					{
						if (st.hasQuestItems(CRYSTAL_OF_SEAL))
							htmltext = "31517-3.htm";
						else
						{
							htmltext = "31517-3a.htm";
							st.exitQuest(true);
						}
					}
					break;

				case EVIL_ALTAR_1:
				case EVIL_ALTAR_2:
				case EVIL_ALTAR_3:
				case EVIL_ALTAR_4:
				case EVIL_ALTAR_5:
					final int condAltar = npcId - 31511;

					if (cond == condAltar)
					{
						if (st.hasQuestItems(CRYSTAL_OF_SEAL))
							htmltext = npcId + "-0.htm";
						else
							htmltext = "altar_nocrystal.htm";
					}
					else if (cond > condAltar)
						htmltext = npcId + "-2.htm";
					break;
			}
		}
		else if (st.getState() == COMPLETED)
			htmltext = getAlreadyCompletedMsg();

		return htmltext;
	}

	public static void main(String[] args)
	{
		new Q016_TheComingDarkness(16, qn, "The Coming Darkness");
	}
}