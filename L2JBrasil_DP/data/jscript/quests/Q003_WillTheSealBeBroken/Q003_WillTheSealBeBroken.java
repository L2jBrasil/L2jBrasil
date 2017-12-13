package quests.Q003_WillTheSealBeBroken;

import com.it.br.gameserver.model.actor.instance.L2NpcInstance;
import com.it.br.gameserver.model.actor.instance.L2PcInstance;
import com.it.br.gameserver.model.base.Race;
import com.it.br.gameserver.model.quest.Quest;
import com.it.br.gameserver.model.quest.QuestState;
import com.it.br.gameserver.model.quest.State;

public class Q003_WillTheSealBeBroken extends Quest
{
	private static final String qn = "Q003_WillTheSealBeBroken";

	// Items
	private static final int ONYX_BEAST_EYE = 1081;
	private static final int TAINT_STONE = 1082;
	private static final int SUCCUBUS_BLOOD = 1083;

	// Reward
	private static final int SCROLL_ENCHANT_ARMOR_D = 956;

	private State CREATED;
	private State STARTED;
	private State COMPLETED;

	public Q003_WillTheSealBeBroken(int questId, String name, String descr)
	{
		super(questId, name, descr);
		CREATED = new State("Start", this);
		STARTED = new State("Started", this);
		COMPLETED = new State("Completed", this);
		this.setInitialState(CREATED);
		questItemIds(ONYX_BEAST_EYE, TAINT_STONE, SUCCUBUS_BLOOD);

		addStartNpc(30141); // Talloth
		addTalkId(30141);

		addKillId(20031, 20041, 20046, 20048, 20052, 20057);
	}

	@Override
	public String onAdvEvent(String event, L2NpcInstance npc, L2PcInstance player)
	{
		String htmltext = event;
		QuestState st = player.getQuestState(qn);
		if (st == null)
			return htmltext;

		if (event.equalsIgnoreCase("30141-03.htm"))
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
		QuestState st = player.getQuestState(qn);
		String htmltext = getNoQuestMsg(player);
		if (st == null)
			return htmltext;

		if (st.getState() == CREATED)
		{
			if (player.getRace() != Race.darkelf)
				htmltext = "30141-00.htm";
			else if (player.getLevel() < 16)
				htmltext = "30141-01.htm";
			else
				htmltext = "30141-02.htm";
		}
		else if (st.getState() == STARTED)
		{
			int cond = st.getInt("cond");
			if (cond == 1)
				htmltext = "30141-04.htm";
			else if (cond == 2)
			{
				htmltext = "30141-06.htm";
				st.takeItems(ONYX_BEAST_EYE, 1);
				st.takeItems(SUCCUBUS_BLOOD, 1);
				st.takeItems(TAINT_STONE, 1);
				st.giveItems(SCROLL_ENCHANT_ARMOR_D, 1);
				st.playSound(SOUND_QUEST_DONE);
				st.exitQuest(false);
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

		if (st.getInt("cond") == 1)
		{
			switch (npc.getNpcId())
			{
				case 20031:
					if (!st.hasQuestItems(ONYX_BEAST_EYE))
						st.giveItems(ONYX_BEAST_EYE, 1);
					break;

				case 20041:
				case 20046:
					if (!st.hasQuestItems(TAINT_STONE))
						st.giveItems(TAINT_STONE, 1);
					break;
				case 20048:
				case 20052:
				case 20057:
					if (!st.hasQuestItems(TAINT_STONE))
						st.giveItems(SUCCUBUS_BLOOD, 1);
					break;
			}
			if (st.hasQuestItems(ONYX_BEAST_EYE, TAINT_STONE, SUCCUBUS_BLOOD))
			{
				st.set("cond", "2");
				st.playSound(SOUND_QUEST_MIDDLE);
			}
			else
				st.playSound(SOUND_ITEM_GET);
		}
		return null;
	}

	public static void main(String[] args)
	{
		new Q003_WillTheSealBeBroken(3, qn, "Will the Seal be Broken?");
	}
}