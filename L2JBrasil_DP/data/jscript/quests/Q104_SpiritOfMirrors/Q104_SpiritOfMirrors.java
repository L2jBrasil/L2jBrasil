package quests.Q104_SpiritOfMirrors;

import com.it.br.gameserver.model.Inventory;
import com.it.br.gameserver.model.actor.instance.L2NpcInstance;
import com.it.br.gameserver.model.actor.instance.L2PcInstance;
import com.it.br.gameserver.model.base.Race;
import com.it.br.gameserver.model.quest.Quest;
import com.it.br.gameserver.model.quest.QuestState;
import com.it.br.gameserver.model.quest.State;
import com.it.br.gameserver.network.serverpackets.SocialAction;

public class Q104_SpiritOfMirrors extends Quest
{
	private static final String qn = "Q104_SpiritOfMirrors";

	// Items
	private static final int GALLINS_OAK_WAND = 748;
	private static final int WAND_SPIRITBOUND_1 = 1135;
	private static final int WAND_SPIRITBOUND_2 = 1136;
	private static final int WAND_SPIRITBOUND_3 = 1137;

	// Rewards
	private static final int SPIRITSHOT_NO_GRADE = 2509;
	private static final int SOULSHOT_NO_GRADE = 1835;
	private static final int WAND_OF_ADEPT = 747;
	private static final int SPIRITSHOT_FOR_BEGINNERS = 5790;
	private static final int SOULSHOT_FOR_BEGINNERS = 5789;
	private static final int LESSER_HEALING_POT = 1060;
	private static final int ECHO_BATTLE = 4412;
	private static final int ECHO_LOVE = 4413;
	private static final int ECHO_SOLITUDE = 4414;
	private static final int ECHO_FEAST = 4415;
	private static final int ECHO_CELEBRATION = 4416;

	// NPCs
	private static final int GALLINT = 30017;
	private static final int ARNOLD = 30041;
	private static final int JOHNSTONE = 30043;
	private static final int KENYOS = 30045;

	private State CREATED;
	private State STARTED;
	private State COMPLETED;

	public Q104_SpiritOfMirrors(int questId, String name, String descr)
	{
		super(questId, name, descr);
		CREATED = new State("Start", this);
		STARTED = new State("Started", this);
		COMPLETED = new State("Completed", this);
		this.setInitialState(CREATED);

		questItemIds(GALLINS_OAK_WAND, WAND_SPIRITBOUND_1, WAND_SPIRITBOUND_2, WAND_SPIRITBOUND_3);

		addStartNpc(GALLINT);
		addTalkId(GALLINT,ARNOLD,JOHNSTONE,KENYOS);
		addKillId(27003,27004,27005);
	}

	@Override
	public String onAdvEvent(String event, L2NpcInstance npc, L2PcInstance player)
	{
		String htmltext = event;
		QuestState st = player.getQuestState(qn);
		if (st == null)
			return htmltext;

		if (event.equalsIgnoreCase("30017-03.htm"))
		{
			st.setState(STARTED);
			st.set("cond", "1");
			st.playSound(SOUND_QUEST_START);
			st.giveItems(GALLINS_OAK_WAND, 1);
			st.giveItems(GALLINS_OAK_WAND, 1);
			st.giveItems(GALLINS_OAK_WAND, 1);
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
		{
			if (player.getRace() != Race.human)
				htmltext = "30017-00.htm";
			else if (player.getLevel() < 10)
				htmltext = "30017-01.htm";
			else
				htmltext = "30017-02.htm";
		}
		else if (st.getState() == STARTED)
		{
			int cond = st.getInt("cond");
			switch (npc.getNpcId())
			{
				case GALLINT:
					if (cond == 1 || cond == 2)
						htmltext = "30017-04.htm";
					else if (cond == 3)
					{
						htmltext = "30017-05.htm";

						st.takeItems(WAND_SPIRITBOUND_1, -1);
						st.takeItems(WAND_SPIRITBOUND_2, -1);
						st.takeItems(WAND_SPIRITBOUND_3, -1);

						st.giveItems(WAND_OF_ADEPT, 1);
						st.rewardItems(LESSER_HEALING_POT, 100);

						if (player.isMageClass())
							st.giveItems(SPIRITSHOT_NO_GRADE, 500);
						else
							st.giveItems(SOULSHOT_NO_GRADE, 1000);

						if (player.isNewbie())
						{
							st.showQuestionMark(26);
							if (player.isMageClass())
							{
								st.playTutorialVoice("tutorial_voice_027");
								st.giveItems(SPIRITSHOT_FOR_BEGINNERS, 3000);
							}
							else
							{
								st.playTutorialVoice("tutorial_voice_026");
								st.giveItems(SOULSHOT_FOR_BEGINNERS, 7000);
							}
						}

						st.giveItems(ECHO_BATTLE, 10);
						st.giveItems(ECHO_LOVE, 10);
						st.giveItems(ECHO_SOLITUDE, 10);
						st.giveItems(ECHO_FEAST, 10);
						st.giveItems(ECHO_CELEBRATION, 10);
						player.broadcastPacket(new SocialAction(player.getObjectId(), 3));
						st.playSound(SOUND_QUEST_DONE);
						st.exitQuest(false);
					}
					break;

				case KENYOS:
				case JOHNSTONE:
				case ARNOLD:
					htmltext = npc.getNpcId() + "-01.htm";
					if (cond == 1)
					{
						st.set("cond", "2");
						st.playSound(SOUND_QUEST_MIDDLE);
					}
					break;
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
		if (st == null)
			return null;

		if (st.getItemEquipped(Inventory.PAPERDOLL_RHAND) == GALLINS_OAK_WAND)
		{
			switch (npc.getNpcId())
			{
				case 27003:
					if (!st.hasQuestItems(WAND_SPIRITBOUND_1))
					{
						st.takeItems(GALLINS_OAK_WAND, 1);
						st.giveItems(WAND_SPIRITBOUND_1, 1);

						if (st.hasQuestItems(WAND_SPIRITBOUND_2, WAND_SPIRITBOUND_3))
						{
							st.set("cond", "3");
							st.playSound(SOUND_QUEST_MIDDLE);
						}
						else
							st.playSound(SOUND_ITEM_GET);
					}
					break;

				case 27004:
					if (!st.hasQuestItems(WAND_SPIRITBOUND_2))
					{
						st.takeItems(GALLINS_OAK_WAND, 1);
						st.giveItems(WAND_SPIRITBOUND_2, 1);

						if (st.hasQuestItems(WAND_SPIRITBOUND_1, WAND_SPIRITBOUND_3))
						{
							st.set("cond", "3");
							st.playSound(SOUND_QUEST_MIDDLE);
						}
						else
							st.playSound(SOUND_ITEM_GET);
					}
					break;

				case 27005:
					if (!st.hasQuestItems(WAND_SPIRITBOUND_3))
					{
						st.takeItems(GALLINS_OAK_WAND, 1);
						st.giveItems(WAND_SPIRITBOUND_3, 1);

						if (st.hasQuestItems(WAND_SPIRITBOUND_1, WAND_SPIRITBOUND_2))
						{
							st.set("cond", "3");
							st.playSound(SOUND_QUEST_MIDDLE);
						}
						else
							st.playSound(SOUND_ITEM_GET);
					}
					break;
			}
		}

		return null;
	}

	public static void main(String[] args)
	{
		new Q104_SpiritOfMirrors(104, qn, "Spirit Of Mirrors");
	}
}