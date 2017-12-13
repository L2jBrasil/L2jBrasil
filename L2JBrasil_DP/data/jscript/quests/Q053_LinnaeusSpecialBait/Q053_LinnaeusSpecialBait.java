package quests.Q053_LinnaeusSpecialBait;

import com.it.br.Config;
import com.it.br.gameserver.model.actor.instance.L2NpcInstance;
import com.it.br.gameserver.model.actor.instance.L2PcInstance;
import com.it.br.gameserver.model.quest.Quest;
import com.it.br.gameserver.model.quest.QuestState;
import com.it.br.gameserver.model.quest.State;

public class Q053_LinnaeusSpecialBait extends Quest
{
	private static final String qn = "Q053_LinnaeusSpecialBait";
	
	// Item
	private static final int CRIMSON_DRAKE_HEART = 7624;
	
	// Reward
	private static final int FLAMING_FISHING_LURE = 7613;
	
	private State CREATED;
	private State STARTED;
	private State COMPLETED;
	
	public Q053_LinnaeusSpecialBait(int questId, String name, String descr)
	{
		super(questId, name, descr);
		CREATED = new State("Start", this);
		STARTED = new State("Started", this);
		COMPLETED = new State("Completed", this);
		this.setInitialState(CREATED);

		questItemIds(CRIMSON_DRAKE_HEART);
		
		addStartNpc(31577); // Linnaeus
		addTalkId(31577);
		
		addKillId(20670); // Crimson Drake
	}

	@Override
	public String onAdvEvent(String event, L2NpcInstance npc, L2PcInstance player)
	{
		String htmltext = event;
		QuestState st = player.getQuestState(qn);
		if (st == null)
			return htmltext;
		
		if (event.equalsIgnoreCase("31577-03.htm"))
		{
			st.setState(STARTED);
			st.set("cond", "1");
			st.playSound(SOUND_QUEST_START);
		}
		else if (event.equalsIgnoreCase("31577-07.htm"))
		{
			htmltext = "31577-06.htm";
			st.takeItems(CRIMSON_DRAKE_HEART, -1);
			st.rewardItems(FLAMING_FISHING_LURE, 4);
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
			htmltext = (player.getLevel() < 60) ? "31577-02.htm" : "31577-01.htm";
		
		else if (st.getState() == STARTED)
			htmltext = (st.getQuestItemsCount(CRIMSON_DRAKE_HEART) == 100) ? "31577-04.htm" : "31577-05.htm";
		
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
		
		if (st.getQuestItemsCount(CRIMSON_DRAKE_HEART) < 100)
		{
			float chance = 33 * Config.RATE_QUESTS_REWARD;
			if (st.getRandom(100) < chance)
			{
				st.rewardItems(CRIMSON_DRAKE_HEART, 1);
				st.playSound(SOUND_ITEM_GET);
			}
		}
		
		if (st.getQuestItemsCount(CRIMSON_DRAKE_HEART) >= 100)
			st.set("cond", "2");
		
		return null;
	}
	
	public static void main(String[] args)
	{
		new Q053_LinnaeusSpecialBait(53, qn, "Linnaues' Special Bait");
	}
}