package quests.Q013_ParcelDelivery;

import com.it.br.gameserver.model.actor.instance.L2NpcInstance;
import com.it.br.gameserver.model.actor.instance.L2PcInstance;
import com.it.br.gameserver.model.quest.Quest;
import com.it.br.gameserver.model.quest.QuestState;
import com.it.br.gameserver.model.quest.State;

public class Q013_ParcelDelivery extends Quest
{
	private static final String qn = "Q013_ParcelDelivery";

	// NPCs
	private static final int FUNDIN = 31274;
	private static final int VULCAN = 31539;

	// Item
	private static final int PACKAGE = 7263;

	private State CREATED;
	private State STARTED;
	private State COMPLETED;

	public Q013_ParcelDelivery(int questId, String name, String descr)
	{
		super(questId, name, descr);
		CREATED = new State("Start", this);
		STARTED = new State("Started", this);
		COMPLETED = new State("Completed", this);
		this.setInitialState(CREATED);

		questItemIds(PACKAGE);

		addStartNpc(FUNDIN);
		addTalkId(FUNDIN, VULCAN);
	}

	@Override
	public String onAdvEvent(String event, L2NpcInstance npc, L2PcInstance player)
	{
		String htmltext = event;
		QuestState st = player.getQuestState(qn);
		if (st == null)
			return htmltext;

		if (event.equalsIgnoreCase("31274-2.htm"))
		{
			st.setState(STARTED);
			st.set("cond", "1");
			st.playSound(SOUND_QUEST_START);
			st.giveItems(PACKAGE, 1);
		}
		else if (event.equalsIgnoreCase("31539-1.htm"))
		{
			st.takeItems(PACKAGE, 1);
			st.rewardItems(57, 82656);
			st.playSound(SOUND_QUEST_DONE);
			st.exitQuest(false);
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
			htmltext = (player.getLevel() < 74) ? "31274-1.htm" : "31274-0.htm";

		else if (st.getState() == STARTED)
		{
			switch (npc.getNpcId())
			{
				case FUNDIN:
					htmltext = "31274-2.htm";
					break;

				case VULCAN:
					htmltext = "31539-0.htm";
					break;
			}
		}
		else if (st.getState() == COMPLETED)
			htmltext = getAlreadyCompletedMsg();

		return htmltext;
	}

	public static void main(String[] args)
	{
		new Q013_ParcelDelivery(13, qn, "Parcel Delivery");
	}
}