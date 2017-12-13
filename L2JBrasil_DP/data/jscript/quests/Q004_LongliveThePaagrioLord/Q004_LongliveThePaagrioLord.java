package quests.Q004_LongliveThePaagrioLord;

import java.util.HashMap;
import java.util.Map;

import com.it.br.gameserver.model.actor.instance.L2NpcInstance;
import com.it.br.gameserver.model.actor.instance.L2PcInstance;
import com.it.br.gameserver.model.base.Race;
import com.it.br.gameserver.model.quest.Quest;
import com.it.br.gameserver.model.quest.QuestState;
import com.it.br.gameserver.model.quest.State;

public class Q004_LongliveThePaagrioLord extends Quest
{
	private static final String qn = "Q004_LongliveThePaagrioLord";

	private State CREATED;
	private State STARTED;
	private State COMPLETED;

	private static final Map<Integer, Integer> NPC_GIFTS = new HashMap<>();
	{
		NPC_GIFTS.put(30585, 1542);
		NPC_GIFTS.put(30566, 1541);
		NPC_GIFTS.put(30562, 1543);
		NPC_GIFTS.put(30560, 1544);
		NPC_GIFTS.put(30559, 1545);
		NPC_GIFTS.put(30587, 1546);
	}

	public Q004_LongliveThePaagrioLord(int questId, String name, String descr)
	{
		super(questId, name, descr);
		CREATED = new State("Start", this);
		STARTED = new State("Started", this);
		COMPLETED = new State("Completed", this);
		this.setInitialState(CREATED);
		questItemIds(1541, 1542, 1543, 1544, 1545, 1546);

		addStartNpc(30578); // Nakusin
		addTalkId(30578, 30585, 30566, 30562, 30560, 30559, 30587);
	}

	@Override
	public String onAdvEvent(String event, L2NpcInstance npc, L2PcInstance player)
	{
		String htmltext = event;
		QuestState st = player.getQuestState(qn);
		if (st == null)
			return htmltext;

		if (event.equalsIgnoreCase("30578-03.htm"))
		{
			st.setState(STARTED);
			st.set("cond", "1");
			st.playSound(SOUND_QUEST_START);
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
			if (player.getRace() != Race.orc)
				htmltext = "30578-00.htm";
			else if (player.getLevel() < 2)
				htmltext = "30578-01.htm";
			else
				htmltext = "30578-02.htm";
		}
		else if (st.getState() == STARTED)
		{
			int cond = st.getInt("cond");
			int npcId = npc.getNpcId();

			if (npcId == 30578)
			{
				if (cond == 1)
					htmltext = "30578-04.htm";
				else if (cond == 2)
				{
					htmltext = "30578-06.htm";
					st.giveItems(4, 1);
					for (int item : NPC_GIFTS.values())
						st.takeItems(item, -1);

					st.playSound(SOUND_QUEST_DONE);
					st.exitQuest(false);
				}
			}
			else
			{
				int i = NPC_GIFTS.get(npcId);
				if (st.hasQuestItems(i))
					htmltext = npcId + "-02.htm";
				else
				{
					st.giveItems(i, 1);
					htmltext = npcId + "-01.htm";

					int count = 0;
					for (int item : NPC_GIFTS.values())
						count += st.getQuestItemsCount(item);

					if (count == 6)
					{
						st.set("cond", "2");
						st.playSound(SOUND_QUEST_MIDDLE);
					}
					else
						st.playSound(SOUND_ITEM_GET);
				}
			}
		}

		else if (st.getState() == COMPLETED)
			htmltext = getAlreadyCompletedMsg();
		return htmltext;
	}

	public static void main(String[] args)
	{
		new Q004_LongliveThePaagrioLord(4, qn, "Long live the Pa'agrio Lord!");
	}
}