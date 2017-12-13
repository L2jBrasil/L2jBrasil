package quests.Q001_LettersOfLove;

import com.it.br.gameserver.model.actor.instance.L2NpcInstance;
import com.it.br.gameserver.model.actor.instance.L2PcInstance;
import com.it.br.gameserver.model.quest.Quest;
import com.it.br.gameserver.model.quest.QuestState;
import com.it.br.gameserver.model.quest.State;

public class Q001_LettersOfLove extends Quest
{
	private static final String qn = "Q001_LettersOfLove";

	// Npcs
	private static final int DARIN = 30048;
	private static final int ROXXY = 30006;
	private static final int BAULRO = 30033;

	// Items
	private static final int DARIN_LETTER = 687;
	private static final int ROXXY_KERCHIEF = 688;
	private static final int DARIN_RECEIPT = 1079;
	private static final int BAULRO_POTION = 1080;

	// Reward
	private static final int NECKLACE = 906;

	private State CREATED;
	private State STARTED;
	private State COMPLETED;

	public Q001_LettersOfLove(int questId, String name, String descr)
	{
		super(questId, name, descr);
		CREATED = new State("Start", this);
		STARTED = new State("Started", this);
		COMPLETED = new State("Completed", this);
		this.setInitialState(CREATED);
		questItemIds(DARIN_LETTER, ROXXY_KERCHIEF, DARIN_RECEIPT, BAULRO_POTION);

		addStartNpc(DARIN);
		addTalkId(DARIN, ROXXY, BAULRO);
	}

	@Override
	public String onAdvEvent(String event, L2NpcInstance npc, L2PcInstance player)
	{
		String htmltext = event;
		QuestState st = player.getQuestState(qn);
		if (st == null)
			return htmltext;

		if (event.equalsIgnoreCase("30048-06.htm"))
		{
			st.setState(STARTED);
			st.set("cond", "1");
			st.playSound(SOUND_QUEST_START);
			st.giveItems(DARIN_LETTER, 1);
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
			htmltext = (player.getLevel() < 2) ? "30048-01.htm" : "30048-02.htm";

		else if (st.getState() == STARTED)
		{
			int cond = st.getInt("cond");
			switch (npc.getNpcId())
			{
				case DARIN:
					if (cond == 1)
						htmltext = "30048-07.htm";
					else if (cond == 2)
					{
						htmltext = "30048-08.htm";
						st.set("cond", "3");
						st.playSound(SOUND_QUEST_MIDDLE);
						st.takeItems(ROXXY_KERCHIEF, 1);
						st.giveItems(DARIN_RECEIPT, 1);
					}
					else if (cond == 3)
						htmltext = "30048-09.htm";
					else if (cond == 4)
					{
						htmltext = "30048-10.htm";
						st.takeItems(BAULRO_POTION, 1);
						st.giveItems(NECKLACE, 1);
						st.playSound(QUEST_DONE);
						st.exitQuest(false);
					}
					break;

				case ROXXY:
					if (cond == 1)
					{
						htmltext = "30006-01.htm";
						st.set("cond", "2");
						st.playSound(SOUND_QUEST_MIDDLE);
						st.takeItems(DARIN_LETTER, 1);
						st.giveItems(ROXXY_KERCHIEF, 1);
					}
					else if (cond == 2)
						htmltext = "30006-02.htm";
					else if (cond > 2)
						htmltext = "30006-03.htm";
					break;

				case BAULRO:
					if (cond == 3)
					{
						htmltext = "30033-01.htm";
						st.set("cond", "4");
						st.playSound(SOUND_QUEST_MIDDLE);
						st.takeItems(DARIN_RECEIPT, 1);
						st.giveItems(BAULRO_POTION, 1);
					}
					else if (cond == 4)
						htmltext = "30033-02.htm";
					break;
			}
		}

		else if (st.getState() == COMPLETED)
			htmltext = getAlreadyCompletedMsg();
		return htmltext;
	}

	public static void main(String[] args)
	{
		new Q001_LettersOfLove(1, qn, "Letters of Love");
	}
}