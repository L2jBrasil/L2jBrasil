package quests.Q112_WalkOfFate;

import com.it.br.Config;
import com.it.br.gameserver.model.actor.instance.L2NpcInstance;
import com.it.br.gameserver.model.actor.instance.L2PcInstance;
import com.it.br.gameserver.model.quest.Quest;
import com.it.br.gameserver.model.quest.QuestState;
import com.it.br.gameserver.model.quest.State;

public class Q112_WalkOfFate extends Quest
{
	private static final String qn = "Q112_WalkOfFate";

	// NPCs
	private static final int LIVINA = 30572;
	private static final int KARUDA = 32017;

	// Rewards
	private static final int ENCHANT_D = 956;

	private State CREATED;
	private State STARTED;
	private State COMPLETED;

	public Q112_WalkOfFate(int questId, String name, String descr)
	{
		super(questId, name, descr);
		CREATED = new State("Start", this);
		STARTED = new State("Started", this);
		COMPLETED = new State("Completed", this);
		this.setInitialState(CREATED);

		addStartNpc(LIVINA);
		addTalkId(LIVINA, KARUDA);
	}

	@Override
	public String onAdvEvent(String event, L2NpcInstance npc, L2PcInstance player)
	{
		String htmltext = event;
		QuestState st = player.getQuestState(qn);
		if (st == null)
			return htmltext;

		if (event.equalsIgnoreCase("30572-02.htm"))
		{
			st.setState(STARTED);
			st.set("cond", "1");
			st.playSound(SOUND_QUEST_START);
		}
		else if (event.equalsIgnoreCase("32017-02.htm"))
		{
			st.giveItems(ENCHANT_D, 1);
			st.giveItems(57, (int) (4665 * Config.RATE_DROP_ADENA));
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
			htmltext = (player.getLevel() < 20) ? "30572-00.htm" : "30572-01.htm";
		
		else if (st.getState() == STARTED)
		{
			switch (npc.getNpcId())
			{
				case LIVINA:
					htmltext = "30572-03.htm";
					break;

				case KARUDA:
					htmltext = "32017-01.htm";
					break;
			}
		}
		else if (st.getState() == COMPLETED)
			htmltext = getAlreadyCompletedMsg();

		return htmltext;
	}

	public static void main(String[] args)
	{
		new Q112_WalkOfFate(112, qn, "Walk Of Fat");
	}
}