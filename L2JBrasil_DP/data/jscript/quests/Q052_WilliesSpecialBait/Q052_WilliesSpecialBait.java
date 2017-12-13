package quests.Q052_WilliesSpecialBait;

import com.it.br.Config;
import com.it.br.gameserver.model.actor.instance.L2NpcInstance;
import com.it.br.gameserver.model.actor.instance.L2PcInstance;
import com.it.br.gameserver.model.quest.Quest;
import com.it.br.gameserver.model.quest.QuestState;
import com.it.br.gameserver.model.quest.State;

public class Q052_WilliesSpecialBait extends Quest
{
	private static final String qn = "Q052_WilliesSpecialBait";
	
	// Item
	private static final int TARLK_EYE = 7623;
	
	// Reward
	private static final int EARTH_FISHING_LURE = 7612;
	
	private State CREATED;
	private State STARTED;
	private State COMPLETED;
	
	public Q052_WilliesSpecialBait(int questId, String name, String descr)
	{
		super(questId, name, descr);
		CREATED = new State("Start", this);
		STARTED = new State("Started", this);
		COMPLETED = new State("Completed", this);
		this.setInitialState(CREATED);
		
		questItemIds(TARLK_EYE);
		
		addStartNpc(31574); // Willie
		addTalkId(31574);
		
		addKillId(20573); // Tarlk Basilik
	}
	
	@Override
	public String onAdvEvent(String event, L2NpcInstance npc, L2PcInstance player)
	{
		String htmltext = event;
		QuestState st = player.getQuestState(qn);
		if (st == null)
			return htmltext;
		
		if (event.equalsIgnoreCase("31574-03.htm"))
		{
			st.setState(STARTED);
			st.set("cond", "1");
			st.playSound(SOUND_QUEST_START);
		}
		else if (event.equalsIgnoreCase("31574-07.htm"))
		{
			htmltext = "31574-06.htm";
			st.takeItems(TARLK_EYE, -1);
			st.rewardItems(EARTH_FISHING_LURE, 4);
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
			htmltext = (player.getLevel() < 48) ? "31574-02.htm" : "31574-01.htm";
		
		else if (st.getState() == STARTED)
			htmltext = (st.getQuestItemsCount(TARLK_EYE) == 100) ? "31574-04.htm" : "31574-05.htm";
		
		else if (st.getState() == COMPLETED)
			htmltext = getAlreadyCompletedMsg();
		
		return htmltext;
	}
	
	@Override
	public String onKill(L2NpcInstance npc, L2PcInstance player, boolean isPet)
	{
		final L2PcInstance partyMember = getRandomPartyMember(player, 1);
		if (partyMember == null)
			return null;

		final QuestState st = getQuestState(partyMember, false);
		if (st.getQuestItemsCount(TARLK_EYE) < 100)
		{
			float chance = 33 * Config.RATE_QUESTS_REWARD;
			if (st.getRandom(100) < chance)
			{
				st.rewardItems(TARLK_EYE, 1);
				st.playSound(SOUND_ITEM_GET);
			}
		}
		
		if (st.getQuestItemsCount(TARLK_EYE) >= 100)
			st.set("cond", "2");
		
		return null;
	}

	public static void main(String[] args)
	{
		new Q052_WilliesSpecialBait(52, qn, "Willie's Special Bait");
	}
}