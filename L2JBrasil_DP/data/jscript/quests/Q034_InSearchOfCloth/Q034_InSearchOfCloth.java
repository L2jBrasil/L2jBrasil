package quests.Q034_InSearchOfCloth;

import com.it.br.gameserver.model.actor.instance.L2NpcInstance;
import com.it.br.gameserver.model.actor.instance.L2PcInstance;
import com.it.br.gameserver.model.quest.Quest;
import com.it.br.gameserver.model.quest.QuestState;
import com.it.br.gameserver.model.quest.State;

public class Q034_InSearchOfCloth extends Quest
{
	private static final String qn = "Q034_InSearchOfCloth";

	// NPCs
	private static final int RADIA = 30088;
	private static final int RALFORD = 30165;
	private static final int VARAN = 30294;

	// Monsters
	private static final int TRISALIM_SPIDER = 20560;
	private static final int TRISALIM_TARANTULA = 20561;

	// Items
	private static final int SPINNERET = 7528;
	private static final int SUEDE = 1866;
	private static final int THREAD = 1868;
	private static final int SPIDERSILK = 7161;

	// Rewards
	private static final int MYSTERIOUS_CLOTH = 7076;

	private State CREATED;
	private State STARTED;
	private State COMPLETED;

	public Q034_InSearchOfCloth(int questId, String name, String descr)
	{
		super(questId, name, descr);
		CREATED = new State("Start", this);
		STARTED = new State("Started", this);
		COMPLETED = new State("Completed", this);
		this.setInitialState(CREATED);
		questItemIds(SPINNERET, SPIDERSILK);

		addStartNpc(RADIA);
		addTalkId(RADIA,RALFORD,VARAN);
		addKillId(TRISALIM_SPIDER,TRISALIM_TARANTULA);
	}

	@Override
	public String onAdvEvent(String event, L2NpcInstance npc, L2PcInstance player)
	{
		String htmltext = event;
		QuestState st = player.getQuestState(qn);
		if (st == null)
			return htmltext;

		if (event.equalsIgnoreCase("30088-1.htm"))
		{
			st.setState(STARTED);
			st.set("cond", "1");
			st.playSound(SOUND_QUEST_START);
		}
		else if (event.equalsIgnoreCase("30294-1.htm"))
		{
			st.set("cond", "2");
			st.playSound(SOUND_QUEST_MIDDLE);
		}
		else if (event.equalsIgnoreCase("30088-3.htm"))
		{
			st.set("cond", "3");
			st.playSound(SOUND_QUEST_MIDDLE);
		}
		else if (event.equalsIgnoreCase("30165-1.htm"))
		{
			st.set("cond", "4");
			st.playSound(SOUND_QUEST_MIDDLE);
		}
		else if (event.equalsIgnoreCase("30165-3.htm"))
		{
			st.set("cond", "6");
			st.playSound(SOUND_QUEST_MIDDLE);
			st.takeItems(SPINNERET, 10);
			st.giveItems(SPIDERSILK, 1);
		}
		else if (event.equalsIgnoreCase("30088-5.htm"))
		{
			if (st.getQuestItemsCount(SUEDE) >= 3000 && st.getQuestItemsCount(THREAD) >= 5000 && st.hasQuestItems(SPIDERSILK))
			{
				st.takeItems(SPIDERSILK, 1);
				st.takeItems(SUEDE, 3000);
				st.takeItems(THREAD, 5000);
				st.giveItems(MYSTERIOUS_CLOTH, 1);
				st.playSound(SOUND_QUEST_DONE);
				st.exitQuest(false);
			}
			else
				htmltext = "30088-4a.htm";
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
			if (player.getLevel() >= 60)
			{
				QuestState fwear = player.getQuestState("Q037_MakeFormalWear");
				if (fwear != null && fwear.getInt("cond") == 6)
					htmltext = "30088-0.htm";
				else
					htmltext = "30088-0a.htm";
			}
			else
				htmltext = "30088-0b.htm";
		}
		else if (st.getState() == STARTED)
		{
			int cond = st.getInt("cond");
			switch (npc.getNpcId())
			{
				case RADIA:
					if (cond == 1)
						htmltext = "30088-1a.htm";
					else if (cond == 2)
						htmltext = "30088-2.htm";
					else if (cond == 3)
						htmltext = "30088-3a.htm";
					else if (cond == 6)
					{
						if (st.getQuestItemsCount(SUEDE) < 3000 || st.getQuestItemsCount(THREAD) < 5000 || !st.hasQuestItems(SPIDERSILK))
							htmltext = "30088-4a.htm";
						else
							htmltext = "30088-4.htm";
					}
					break;

				case VARAN:
					if (cond == 1)
						htmltext = "30294-0.htm";
					else if (cond > 1)
						htmltext = "30294-1a.htm";
					break;

				case RALFORD:
					if (cond == 3)
						htmltext = "30165-0.htm";
					else if (cond == 4 && st.getQuestItemsCount(SPINNERET) < 10)
						htmltext = "30165-1a.htm";
					else if (cond == 5)
						htmltext = "30165-2.htm";
					else if (cond > 5)
						htmltext = "30165-3a.htm";
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

		int count = st.getQuestItemsCount(SPINNERET);
		if (count < 10)
			st.giveItems(SPINNERET, 1);
		if (count == 9)
		{
			st.playSound(SOUND_QUEST_MIDDLE);
			st.set("cond", "5");
		}
		else
			st.playSound(SOUND_ITEM_GET);

		return null;
	}

	public static void main(String[] args)
	{
		new Q034_InSearchOfCloth(34, qn, "In Search of Cloth");
	}
}