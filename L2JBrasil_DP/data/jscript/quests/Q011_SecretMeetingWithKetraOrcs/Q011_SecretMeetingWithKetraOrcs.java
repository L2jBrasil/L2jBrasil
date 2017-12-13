package quests.Q011_SecretMeetingWithKetraOrcs;

import com.it.br.gameserver.model.actor.instance.L2NpcInstance;
import com.it.br.gameserver.model.actor.instance.L2PcInstance;
import com.it.br.gameserver.model.quest.Quest;
import com.it.br.gameserver.model.quest.QuestState;
import com.it.br.gameserver.model.quest.State;

public class Q011_SecretMeetingWithKetraOrcs extends Quest
{
	private static final String qn = "Q011_SecretMeetingWithKetraOrcs";

	// Npcs
	private static final int CADMON = 31296;
	private static final int LEON = 31256;
	private static final int WAHKAN = 31371;

	// Items
	private static final int MUNITIONS_BOX = 7231;

	private State CREATED;
	private State STARTED;
	private State COMPLETED;

	public Q011_SecretMeetingWithKetraOrcs(int questId, String name, String descr)
	{
		super(questId, name, descr);
		CREATED = new State("Start", this);
		STARTED = new State("Started", this);
		COMPLETED = new State("Completed", this);
		this.setInitialState(CREATED);

		questItemIds(MUNITIONS_BOX);

		addStartNpc(CADMON);
		addTalkId(CADMON, LEON, WAHKAN);
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
		else if (event.equalsIgnoreCase("31256-02.htm"))
		{
			st.giveItems(MUNITIONS_BOX, 1);
			st.set("cond", "2");
			st.playSound(SOUND_QUEST_MIDDLE);
		}
		else if (event.equalsIgnoreCase("31371-02.htm"))
		{
			st.takeItems(MUNITIONS_BOX, 1);
			st.addExpAndSp(79787, 0);
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

				case LEON:
					if (cond == 1)
						htmltext = "31256-01.htm";
					else if (cond == 2)
						htmltext = "31256-03.htm";
					break;

				case WAHKAN:
					if (cond == 2)
						htmltext = "31371-01.htm";
					break;
			}
		}
		else if (st.getState() == COMPLETED)
			htmltext = getAlreadyCompletedMsg();

		return htmltext;
	}

	public static void main(String[] args)
	{
		new Q011_SecretMeetingWithKetraOrcs(11, qn, "Secret Meeting With Ketra Orcs");
	}
}