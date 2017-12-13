package quests.Q050_LanoscosSpecialBait;

import com.it.br.Config;
import com.it.br.gameserver.model.actor.instance.L2NpcInstance;
import com.it.br.gameserver.model.actor.instance.L2PcInstance;
import com.it.br.gameserver.model.quest.Quest;
import com.it.br.gameserver.model.quest.QuestState;
import com.it.br.gameserver.model.quest.State;

public class Q050_LanoscosSpecialBait extends Quest
{
	private static final String qn = "Q050_LanoscosSpecialBait";
	
	// Item
	private static final int ESSENCE_OF_WIND = 7621;
	
	// Reward
	private static final int WIND_FISHING_LURE = 7610;
	
	private State CREATED;
	private State STARTED;
	private State COMPLETED;
	
	public Q050_LanoscosSpecialBait(int questId, String name, String descr)
	{
		super(questId, name, descr);
		CREATED = new State("Start", this);
		STARTED = new State("Started", this);
		COMPLETED = new State("Completed", this);
		this.setInitialState(CREATED);
		
		questItemIds(ESSENCE_OF_WIND);
		
		addStartNpc(31570); // Lanosco
		addTalkId(31570);
		
		addKillId(21026); // Singing wind
	}
	
	@Override
	public String onAdvEvent(String event, L2NpcInstance npc, L2PcInstance player)
	{
		String htmltext = event;
		QuestState st = player.getQuestState(qn);
		if (st == null)
			return htmltext;
		
		if (event.equalsIgnoreCase("31570-03.htm"))
		{
			st.setState(STARTED);
			st.set("cond", "1");
			st.playSound(SOUND_QUEST_START);
		}
		else if (event.equalsIgnoreCase("31570-07.htm"))
		{
			htmltext = "31570-06.htm";
			st.takeItems(ESSENCE_OF_WIND, -1);
			st.rewardItems(WIND_FISHING_LURE, 4);
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
			htmltext = (player.getLevel() < 27) ? "31570-02.htm" : "31570-01.htm";
		
		else if (st.getState() == STARTED)
			htmltext = (st.getQuestItemsCount(ESSENCE_OF_WIND) == 100) ? "31570-04.htm" : "31570-05.htm";
		
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
		
		if (st.getQuestItemsCount(ESSENCE_OF_WIND) < 100)
		{
			float chance = 33 * Config.RATE_QUESTS_REWARD;
			if (st.getRandom(100) < chance)
			{
				st.rewardItems(ESSENCE_OF_WIND, 1);
				st.playSound(SOUND_ITEM_GET);
			}
		}
		
		if (st.getQuestItemsCount(ESSENCE_OF_WIND) >= 100)
			st.set("cond", "2");	
		
		return null;
	}
	
	public static void main(String[] args)
	{
		new Q050_LanoscosSpecialBait(50, qn, "Lanosco's Special Bait");
	}
}