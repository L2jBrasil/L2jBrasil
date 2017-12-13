package quests.Q047_IntoTheDarkForest;

import com.it.br.gameserver.model.actor.instance.L2NpcInstance;
import com.it.br.gameserver.model.actor.instance.L2PcInstance;
import com.it.br.gameserver.model.base.Race;
import com.it.br.gameserver.model.quest.Quest;
import com.it.br.gameserver.model.quest.QuestState;
import com.it.br.gameserver.model.quest.State;

public class Q047_IntoTheDarkForest extends Quest
{
	private static final String qn = "Q047_IntoTheDarkForest";
	
	// NPCs
	private static final int GALLADUCCI = 30097;
	private static final int GENTLER = 30094;
	private static final int SANDRA = 30090;
	private static final int DUSTIN = 30116;
	
	// Items
	private static final int ORDER_DOCUMENT_1 = 7563;
	private static final int ORDER_DOCUMENT_2 = 7564;
	private static final int ORDER_DOCUMENT_3 = 7565;
	private static final int MAGIC_SWORD_HILT = 7568;
	private static final int GEMSTONE_POWDER = 7567;
	private static final int PURIFIED_MAGIC_NECKLACE = 7566;
	private static final int MARK_OF_TRAVELER = 7570;
	private static final int SCROLL_OF_ESCAPE_SPECIAL = 7556;
	
	private State CREATED;
	private State STARTED;
	private State COMPLETED;
	
	public Q047_IntoTheDarkForest(int questId, String name, String descr)
	{
		super(questId, name, descr);
		CREATED = new State("Start", this);
		STARTED = new State("Started", this);
		COMPLETED = new State("Completed", this);
		this.setInitialState(CREATED);
		
		questItemIds(ORDER_DOCUMENT_1, ORDER_DOCUMENT_2, ORDER_DOCUMENT_3, MAGIC_SWORD_HILT, GEMSTONE_POWDER, PURIFIED_MAGIC_NECKLACE);
		
		addStartNpc(GALLADUCCI);
		addTalkId(GALLADUCCI, SANDRA, DUSTIN, GENTLER);
	}
	
	@Override
	public String onAdvEvent(String event, L2NpcInstance npc, L2PcInstance player)
	{
		String htmltext = event;
		QuestState st = player.getQuestState(qn);
		if (st == null)
			return htmltext;
		
		if (event.equalsIgnoreCase("30097-03.htm"))
		{
			st.setState(STARTED);
			st.set("cond", "1");
			st.playSound(SOUND_QUEST_START);
			st.giveItems(ORDER_DOCUMENT_1, 1);
		}
		else if (event.equalsIgnoreCase("30094-02.htm"))
		{
			st.set("cond", "2");
			st.playSound(SOUND_QUEST_MIDDLE);
			st.takeItems(ORDER_DOCUMENT_1, 1);
			st.giveItems(MAGIC_SWORD_HILT, 1);
		}
		else if (event.equalsIgnoreCase("30097-06.htm"))
		{
			st.set("cond", "3");
			st.playSound(SOUND_QUEST_MIDDLE);
			st.takeItems(MAGIC_SWORD_HILT, 1);
			st.giveItems(ORDER_DOCUMENT_2, 1);
		}
		else if (event.equalsIgnoreCase("30090-02.htm"))
		{
			st.set("cond", "4");
			st.playSound(SOUND_QUEST_MIDDLE);
			st.takeItems(ORDER_DOCUMENT_2, 1);
			st.giveItems(GEMSTONE_POWDER, 1);
		}
		else if (event.equalsIgnoreCase("30097-09.htm"))
		{
			st.set("cond", "5");
			st.playSound(SOUND_QUEST_MIDDLE);
			st.takeItems(GEMSTONE_POWDER, 1);
			st.giveItems(ORDER_DOCUMENT_3, 1);
		}
		else if (event.equalsIgnoreCase("30116-02.htm"))
		{
			st.set("cond", "6");
			st.playSound(SOUND_QUEST_MIDDLE);
			st.takeItems(ORDER_DOCUMENT_3, 1);
			st.giveItems(PURIFIED_MAGIC_NECKLACE, 1);
		}
		else if (event.equalsIgnoreCase("30097-12.htm"))
		{
			st.takeItems(MARK_OF_TRAVELER, -1);
			st.takeItems(PURIFIED_MAGIC_NECKLACE, 1);
			st.rewardItems(SCROLL_OF_ESCAPE_SPECIAL, 1);
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
		{
			if (player.getRace() == Race.darkelf && player.getLevel() >= 3)
			{
				if (st.hasQuestItems(MARK_OF_TRAVELER))
					htmltext = "30097-02.htm";
				else
					htmltext = "30097-01.htm";
			}
			else
				htmltext = "30097-01a.htm";
		}
		else if (st.getState() == STARTED)
		{
			int cond = st.getInt("cond");
			switch (npc.getNpcId())
			{
				case GALLADUCCI:
					if (cond == 1)
						htmltext = "30097-04.htm";
					else if (cond == 2)
						htmltext = "30097-05.htm";
					else if (cond == 3)
						htmltext = "30097-07.htm";
					else if (cond == 4)
						htmltext = "30097-08.htm";
					else if (cond == 5)
						htmltext = "30097-10.htm";
					else if (cond == 6)
						htmltext = "30097-11.htm";
					break;
				
				case GENTLER:
					if (cond == 1)
						htmltext = "30094-01.htm";
					else if (cond > 1)
						htmltext = "30094-03.htm";
					break;
				
				case SANDRA:
					if (cond == 3)
						htmltext = "30090-01.htm";
					else if (cond > 3)
						htmltext = "30090-03.htm";
					break;
				
				case DUSTIN:
					if (cond == 5)
						htmltext = "30116-01.htm";
					else if (cond == 6)
						htmltext = "30116-03.htm";
					break;
			}
		}
		else if (st.getState() == COMPLETED)
			htmltext = getAlreadyCompletedMsg();
		
		return htmltext;
	}
	
	public static void main(String[] args)
	{
		new Q047_IntoTheDarkForest(47, qn, "Into the Dark Forest");
	}
}