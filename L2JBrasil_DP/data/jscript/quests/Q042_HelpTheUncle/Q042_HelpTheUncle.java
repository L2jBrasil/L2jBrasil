package quests.Q042_HelpTheUncle;

import com.it.br.gameserver.model.actor.instance.L2NpcInstance;
import com.it.br.gameserver.model.actor.instance.L2PcInstance;
import com.it.br.gameserver.model.quest.Quest;
import com.it.br.gameserver.model.quest.QuestState;
import com.it.br.gameserver.model.quest.State;

public class Q042_HelpTheUncle extends Quest
{
	private static final String qn = "Q042_HelpTheUncle";

	// NPCs
	private static final int WATERS = 30828;
	private static final int SOPHYA = 30735;

	// Items
	private static final int TRIDENT = 291;
	private static final int MAP_PIECE = 7548;
	private static final int MAP = 7549;
	private static final int PET_TICKET = 7583;

	// Monsters
	private static final int MONSTER_EYE_DESTROYER = 20068;
	private static final int MONSTER_EYE_GAZER = 20266;

	private State CREATED;
	private State STARTED;
	private State COMPLETED;

	public Q042_HelpTheUncle(int questId, String name, String descr)
	{
		super(questId, name, descr);
		CREATED = new State("Start", this);
		STARTED = new State("Started", this);
		COMPLETED = new State("Completed", this);
		this.setInitialState(CREATED);
		questItemIds(MAP_PIECE, MAP);

		addStartNpc(WATERS);
		addTalkId(WATERS, SOPHYA);

		addKillId(MONSTER_EYE_DESTROYER, MONSTER_EYE_GAZER);
	}

	@Override
	public String onAdvEvent(String event, L2NpcInstance npc, L2PcInstance player)
	{
		String htmltext = event;
		QuestState st = player.getQuestState(qn);
		if (st == null)
			return htmltext;

		if (event.equalsIgnoreCase("30828-01.htm"))
		{
			st.setState(STARTED);
			st.set("cond", "1");
			st.playSound(SOUND_QUEST_START);
		}
		else if (event.equalsIgnoreCase("30828-03.htm") && st.hasQuestItems(TRIDENT))
		{
			st.set("cond", "2");
			st.playSound(SOUND_QUEST_MIDDLE);
			st.takeItems(TRIDENT, 1);
		}
		else if (event.equalsIgnoreCase("30828-05.htm"))
		{
			st.set("cond", "4");
			st.playSound(SOUND_QUEST_MIDDLE);
			st.takeItems(MAP_PIECE, 30);
			st.giveItems(MAP, 1);
		}
		else if (event.equalsIgnoreCase("30735-06.htm"))
		{
			st.set("cond", "5");
			st.playSound(SOUND_QUEST_MIDDLE);
			st.takeItems(MAP, 1);
		}
		else if (event.equalsIgnoreCase("30828-07.htm"))
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
			htmltext = (player.getLevel() < 25) ? "30828-00a.htm" : "30828-00.htm";

		else if (st.getState() == STARTED)
		{
			int cond = st.getInt("cond");
			switch (npc.getNpcId())
			{
				case WATERS:
					if (cond == 1)
						htmltext = (!st.hasQuestItems(TRIDENT)) ? "30828-01a.htm" : "30828-02.htm";
					else if (cond == 2)
						htmltext = "30828-03a.htm";
					else if (cond == 3)
						htmltext = "30828-04.htm";
					else if (cond == 4)
						htmltext = "30828-05a.htm";
					else if (cond == 5)
						htmltext = "30828-06.htm";
					break;

				case SOPHYA:
					if (cond == 4)
						htmltext = "30735-05.htm";
					else if (cond == 5)
						htmltext = "30735-06a.htm";
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
		new Q042_HelpTheUncle(42, qn, "Help the Uncle!");
	}
}