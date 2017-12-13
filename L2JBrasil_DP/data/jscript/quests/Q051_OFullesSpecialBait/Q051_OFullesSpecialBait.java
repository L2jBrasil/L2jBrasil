package quests.Q051_OFullesSpecialBait;

import com.it.br.Config;
import com.it.br.gameserver.model.actor.instance.L2NpcInstance;
import com.it.br.gameserver.model.actor.instance.L2PcInstance;
import com.it.br.gameserver.model.quest.Quest;
import com.it.br.gameserver.model.quest.QuestState;
import com.it.br.gameserver.model.quest.State;

public class Q051_OFullesSpecialBait extends Quest
{
	private static final String qn = "Q051_OFullesSpecialBait";
	
	// Item
	private static final int LOST_BAIT = 7622;
	
	// Reward
	private static final int ICY_AIR_LURE = 7611;
	
	private State CREATED;
	private State STARTED;
	private State COMPLETED;
	
	public Q051_OFullesSpecialBait(int questId, String name, String descr)
	{
		super(questId, name, descr);
		CREATED = new State("Start", this);
		STARTED = new State("Started", this);
		COMPLETED = new State("Completed", this);
		this.setInitialState(CREATED);
		
		questItemIds(LOST_BAIT);
		
		addStartNpc(31572); // O'Fulle
		addTalkId(31572);
		
		addKillId(20552); // Fettered Soul
	}
	
	@Override
	public String onAdvEvent(String event, L2NpcInstance npc, L2PcInstance player)
	{
		String htmltext = event;
		QuestState st = player.getQuestState(qn);
		if (st == null)
			return htmltext;
		
		if (event.equalsIgnoreCase("31572-03.htm"))
		{
			st.setState(STARTED);
			st.set("cond", "1");
			st.playSound(SOUND_QUEST_START);
		}
		else if (event.equalsIgnoreCase("31572-07.htm"))
		{
			htmltext = "31572-06.htm";
			st.takeItems(LOST_BAIT, -1);
			st.rewardItems(ICY_AIR_LURE, 4);
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
			htmltext = (player.getLevel() < 36) ? "31572-02.htm" : "31572-01.htm";
		
		else if (st.getState() == STARTED)
			htmltext = (st.getQuestItemsCount(LOST_BAIT) == 100) ? "31572-04.htm" : "31572-05.htm";
		
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
		if (st.getQuestItemsCount(LOST_BAIT) < 100)
		{
			float chance = 33 * Config.RATE_QUESTS_REWARD;
			if (st.getRandom(100) < chance)
			{
				st.rewardItems(LOST_BAIT, 1);
				st.playSound(SOUND_ITEM_GET);
			}
		}
		
		if (st.getQuestItemsCount(LOST_BAIT) >= 100)
			st.set("cond", "2");
		
		return null;
	}
	
	public static void main(String[] args)
	{
		new Q051_OFullesSpecialBait(51, qn, "O'Fulle's Special Bait");
	}
}