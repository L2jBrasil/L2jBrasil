package quests.Q005_MinersFavor;

import com.it.br.gameserver.model.actor.instance.L2NpcInstance;
import com.it.br.gameserver.model.actor.instance.L2PcInstance;
import com.it.br.gameserver.model.quest.Quest;
import com.it.br.gameserver.model.quest.QuestState;
import com.it.br.gameserver.model.quest.State;

public class Q005_MinersFavor extends Quest
{
	private static final String qn = "Q005_MinersFavor";

	// NPCs
	private static final int BOLTER = 30554;
	private static final int SHARI = 30517;
	private static final int GARITA = 30518;
	private static final int REED = 30520;
	private static final int BRUNON = 30526;

	// Items
	private static final int BOLTERS_LIST = 1547;
	private static final int MINING_BOOTS = 1548;
	private static final int MINERS_PICK = 1549;
	private static final int BOOMBOOM_POWDER = 1550;
	private static final int REDSTONE_BEER = 1551;
	private static final int BOLTERS_SMELLY_SOCKS = 1552;

	// Reward
	private static final int NECKLACE = 906;

	private State CREATED;
	private State STARTED;
	private State COMPLETED;

	public Q005_MinersFavor(int questId, String name, String descr)
	{
		super(questId, name, descr);
		CREATED = new State("Start", this);
		STARTED = new State("Started", this);
		COMPLETED = new State("Completed", this);
		this.setInitialState(CREATED);
		questItemIds(BOLTERS_LIST, MINING_BOOTS, MINERS_PICK, BOOMBOOM_POWDER, REDSTONE_BEER, BOLTERS_SMELLY_SOCKS);

		addStartNpc(BOLTER);
		addTalkId(BOLTER, SHARI, GARITA, REED, BRUNON);
	}

	@Override
	public String onAdvEvent(String event, L2NpcInstance npc, L2PcInstance player)
	{
		String htmltext = event;
		QuestState st = player.getQuestState(qn);
		if (st == null)
			return htmltext;

		if (event.equalsIgnoreCase("30554-03.htm"))
		{
			st.setState(STARTED);
			st.set("cond", "1");
			st.playSound(SOUND_QUEST_START);
			st.giveItems(BOLTERS_LIST, 1);
			st.giveItems(BOLTERS_SMELLY_SOCKS, 1);
		}
		else if (event.equalsIgnoreCase("30526-02.htm"))
		{
			st.takeItems(BOLTERS_SMELLY_SOCKS, 1);
			st.giveItems(MINERS_PICK, 1);

			if (st.hasQuestItems(MINING_BOOTS, BOOMBOOM_POWDER, REDSTONE_BEER))
			{
				st.set("cond", "2");
				st.playSound(SOUND_QUEST_MIDDLE);
			}
			else
				st.playSound(SOUND_ITEM_GET);
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
			htmltext = (player.getLevel() < 2) ? "30554-01.htm" : "30554-02.htm";

		else if (st.getState() == STARTED)
		{
			int cond = st.getInt("cond");
			switch (npc.getNpcId())
			{
				case BOLTER:
					if (cond == 1)
						htmltext = "30554-04.htm";
					else if (cond == 2)
					{
						htmltext = "30554-06.htm";
						st.takeItems(BOLTERS_LIST, 1);
						st.takeItems(BOOMBOOM_POWDER, 1);
						st.takeItems(MINERS_PICK, 1);
						st.takeItems(MINING_BOOTS, 1);
						st.takeItems(REDSTONE_BEER, 1);
						st.giveItems(NECKLACE, 1);
						st.playSound(SOUND_QUEST_DONE);
						st.exitQuest(false);
					}
					break;

				case SHARI:
					if (cond == 1 && !st.hasQuestItems(BOOMBOOM_POWDER))
					{
						htmltext = "30517-01.htm";
						st.giveItems(BOOMBOOM_POWDER, 1);

						if (st.hasQuestItems(MINING_BOOTS, MINERS_PICK, REDSTONE_BEER))
						{
							st.set("cond", "2");
							st.playSound(SOUND_QUEST_MIDDLE);
						}
						else
							st.playSound(SOUND_ITEM_GET);
					}
					else
						htmltext = "30517-02.htm";
					break;

				case GARITA:
					if (cond == 1 && !st.hasQuestItems(MINING_BOOTS))
					{
						htmltext = "30518-01.htm";
						st.giveItems(MINING_BOOTS, 1);

						if (st.hasQuestItems(MINERS_PICK, BOOMBOOM_POWDER, REDSTONE_BEER))
						{
							st.set("cond", "2");
							st.playSound(SOUND_QUEST_MIDDLE);
						}
						else
							st.playSound(SOUND_ITEM_GET);
					}
					else
						htmltext = "30518-02.htm";
					break;

				case REED:
					if (cond == 1 && !st.hasQuestItems(REDSTONE_BEER))
					{
						htmltext = "30520-01.htm";
						st.giveItems(REDSTONE_BEER, 1);

						if (st.hasQuestItems(MINING_BOOTS, MINERS_PICK, BOOMBOOM_POWDER))
						{
							st.set("cond", "2");
							st.playSound(SOUND_QUEST_MIDDLE);
						}
						else
							st.playSound(SOUND_ITEM_GET);
					}
					else
						htmltext = "30520-02.htm";
					break;

				case BRUNON:
					if (cond == 1 && !st.hasQuestItems(MINERS_PICK))
						htmltext = "30526-01.htm";
					else
						htmltext = "30526-03.htm";
					break;
			}
		}

		else if (st.getState() == COMPLETED)
			htmltext = getAlreadyCompletedMsg();
		return htmltext;
	}

	public static void main(String[] args)
	{
		new Q005_MinersFavor(5, qn, "Miner's Favor");
	}
}