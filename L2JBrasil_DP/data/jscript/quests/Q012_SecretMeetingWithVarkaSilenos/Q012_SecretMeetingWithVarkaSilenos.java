package quests.Q012_SecretMeetingWithVarkaSilenos;

import com.it.br.gameserver.model.actor.instance.L2NpcInstance;
import com.it.br.gameserver.model.actor.instance.L2PcInstance;
import com.it.br.gameserver.model.quest.Quest;
import com.it.br.gameserver.model.quest.QuestState;
import com.it.br.gameserver.model.quest.State;

public class Q012_SecretMeetingWithVarkaSilenos extends Quest
{
	private static final String qn = "Q012_SecretMeetingWithVarkaSilenos";

	// NPCs
	private static final int CADMON = 31296;
	private static final int HELMUT = 31258;
	private static final int NARAN_ASHANUK = 31378;

	// Items
	private static final int MUNITIONS_BOX = 7232;

	private State CREATED;
	private State STARTED;
	private State COMPLETED;

	public Q012_SecretMeetingWithVarkaSilenos(int questId, String name, String descr)
	{
		super(questId, name, descr);
		CREATED = new State("Start", this);
		STARTED = new State("Started", this);
		COMPLETED = new State("Completed", this);
		this.setInitialState(CREATED);

		questItemIds(MUNITIONS_BOX);

		addStartNpc(CADMON);
		addTalkId(CADMON, HELMUT, NARAN_ASHANUK);
	}

	@Override
	public String onAdvEvent(String event, L2NpcInstance npc, L2PcInstance player)
	{
		String htmltext = event;
		QuestState st = player.getQuestState(qn);
		if (st == null)
			return htmltext;

		if (event.equalsIgnoreCase("31296-03.htm"))
		{
			st.setState(STARTED);
			st.set("cond", "1");
			st.playSound(SOUND_QUEST_START);
		}
		else if (event.equalsIgnoreCase("31258-02.htm"))
		{
			st.giveItems(MUNITIONS_BOX, 1);
			st.set("cond", "2");
			st.playSound(SOUND_QUEST_MIDDLE);
		}
		else if (event.equalsIgnoreCase("31378-02.htm"))
		{
			st.takeItems(MUNITIONS_BOX, 1);
			st.addExpAndSp(79761, 0);
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
			htmltext = (player.getLevel() < 74) ? "31296-02.htm" : "31296-01.htm";

		else if (st.getState() == STARTED)
		{
			int cond = st.getInt("cond");
			switch (npc.getNpcId())
			{
				case CADMON:
					if (cond == 1)
						htmltext = "31296-04.htm";
					break;

				case HELMUT:
					if (cond == 1)
						htmltext = "31258-01.htm";
					else if (cond == 2)
						htmltext = "31258-03.htm";
					break;

				case NARAN_ASHANUK:
					if (cond == 2)
						htmltext = "31378-01.htm";
					break;
			}
		}
		else if (st.getState() == COMPLETED)
			htmltext = getAlreadyCompletedMsg();

		return htmltext;
	}

	public static void main(String[] args)
	{
		new Q012_SecretMeetingWithVarkaSilenos(12, qn, "Secret Meeting With Varka Silenos");
	}
}