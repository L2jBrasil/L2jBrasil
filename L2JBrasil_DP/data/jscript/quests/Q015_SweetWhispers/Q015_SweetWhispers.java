package quests.Q015_SweetWhispers;

import com.it.br.gameserver.model.actor.instance.L2NpcInstance;
import com.it.br.gameserver.model.actor.instance.L2PcInstance;
import com.it.br.gameserver.model.quest.Quest;
import com.it.br.gameserver.model.quest.QuestState;
import com.it.br.gameserver.model.quest.State;

public class Q015_SweetWhispers extends Quest
{
	private static final String qn = "Q015_SweetWhispers";

	// NPCs
	private static final int VLADIMIR = 31302;
	private static final int HIERARCH = 31517;
	private static final int MYSTERIOUS_NECRO = 31518;

	private State CREATED;
	private State STARTED;
	private State COMPLETED;

	public Q015_SweetWhispers(int questId, String name, String descr)
	{
		super(questId, name, descr);
		CREATED = new State("Start", this);
		STARTED = new State("Started", this);
		COMPLETED = new State("Completed", this);
		this.setInitialState(CREATED);

		addStartNpc(VLADIMIR);
		addTalkId(VLADIMIR, HIERARCH, MYSTERIOUS_NECRO);
	}

	@Override
	public String onAdvEvent(String event, L2NpcInstance npc, L2PcInstance player)
	{
		String htmltext = event;
		QuestState st = player.getQuestState(qn);
		if (st == null)
			return htmltext;

		if (event.equalsIgnoreCase("31302-01.htm"))
		{
			st.setState(STARTED);
			st.set("cond", "1");
			st.playSound(SOUND_QUEST_START);
		}
		else if (event.equalsIgnoreCase("31518-01.htm"))
		{
			st.set("cond", "2");
			st.playSound(SOUND_QUEST_MIDDLE);
		}
		else if (event.equalsIgnoreCase("31517-01.htm"))
		{
			st.addExpAndSp(60217, 0);
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
			htmltext = (player.getLevel() < 60) ? "31302-00a.htm" : "31302-00.htm";

		else if (st.getState() == STARTED)
		{
			int cond = st.getInt("cond");
			switch (npc.getNpcId())
			{
				case VLADIMIR:
					htmltext = "31302-01a.htm";
					break;

				case MYSTERIOUS_NECRO:
					if (cond == 1)
						htmltext = "31518-00.htm";
					else if (cond == 2)
						htmltext = "31518-01a.htm";
					break;

				case HIERARCH:
					if (cond == 2)
						htmltext = "31517-00.htm";
					break;
			}
		}
		else if (st.getState() == COMPLETED)
			htmltext = getAlreadyCompletedMsg();

		return htmltext;
	}

	public static void main(String[] args)
	{
		new Q015_SweetWhispers(15, qn, "Sweet Whispers");
	}
}