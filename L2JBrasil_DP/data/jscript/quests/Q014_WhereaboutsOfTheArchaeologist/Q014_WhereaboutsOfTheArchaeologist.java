package quests.Q014_WhereaboutsOfTheArchaeologist;

import com.it.br.gameserver.model.actor.instance.L2NpcInstance;
import com.it.br.gameserver.model.actor.instance.L2PcInstance;
import com.it.br.gameserver.model.quest.Quest;
import com.it.br.gameserver.model.quest.QuestState;
import com.it.br.gameserver.model.quest.State;

public class Q014_WhereaboutsOfTheArchaeologist extends Quest
{
	private static final String qn = "Q014_WhereaboutsOfTheArchaeologist";

	// NPCs
	private static final int LIESEL = 31263;
	private static final int GHOST_OF_ADVENTURER = 31538;

	// Items
	private static final int LETTER = 7253;

	private State CREATED;
	private State STARTED;
	private State COMPLETED;

	public Q014_WhereaboutsOfTheArchaeologist(int questId, String name, String descr)
	{
		super(questId, name, descr);
		CREATED = new State("Start", this);
		STARTED = new State("Started", this);
		COMPLETED = new State("Completed", this);
		this.setInitialState(CREATED);

		questItemIds(LETTER);

		addStartNpc(LIESEL);
		addTalkId(LIESEL, GHOST_OF_ADVENTURER);
	}

	@Override
	public String onAdvEvent(String event, L2NpcInstance npc, L2PcInstance player)
	{
		String htmltext = event;
		QuestState st = player.getQuestState(qn);
		if (st == null)
			return htmltext;

		if (event.equalsIgnoreCase("31263-2.htm"))
		{
			st.setState(STARTED);
			st.set("cond", "1");
			st.playSound(SOUND_QUEST_START);
			st.giveItems(LETTER, 1);
		}
		else if (event.equalsIgnoreCase("31538-1.htm"))
		{
			st.takeItems(LETTER, 1);
			st.rewardItems(57, 113228);
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
			htmltext = (player.getLevel() < 74) ? "31263-1.htm" : "31263-0.htm";

		else if (st.getState() == STARTED)
		{
			switch (npc.getNpcId())
			{
				case LIESEL:
					htmltext = "31263-2.htm";
					break;

				case GHOST_OF_ADVENTURER:
					htmltext = "31538-0.htm";
					break;
			}
		}
		else if (st.getState() == COMPLETED)
			htmltext = getAlreadyCompletedMsg();

		return htmltext;
	}

	public static void main(String[] args)
	{
		new Q014_WhereaboutsOfTheArchaeologist(14, qn, "Whereabouts of the Archaeologist");
	}
}