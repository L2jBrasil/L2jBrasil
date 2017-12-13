package quests.Q021_HiddenTruth;

import com.it.br.gameserver.ai.CtrlIntention;
import com.it.br.gameserver.model.Location;
import com.it.br.gameserver.model.actor.instance.L2NpcInstance;
import com.it.br.gameserver.model.actor.instance.L2PcInstance;
import com.it.br.gameserver.model.quest.Quest;
import com.it.br.gameserver.model.quest.QuestState;
import com.it.br.gameserver.model.quest.State;

public class Q021_HiddenTruth extends Quest
{
	private static final String qn = "Q021_HiddenTruth";

	// NPCs
	private static final int MYSTERIOUS_WIZARD = 31522;
	private static final int TOMBSTONE = 31523;
	private static final int VON_HELLMAN_DUKE = 31524;
	private static final int VON_HELLMAN_PAGE = 31525;
	private static final int BROKEN_BOOKSHELF = 31526;
	private static final int AGRIPEL = 31348;
	private static final int DOMINIC = 31350;
	private static final int BENEDICT = 31349;
	private static final int INNOCENTIN = 31328;

	// Items
	private static final int CROSS_OF_EINHASAD = 7140;
	private static final int CROSS_OF_EINHASAD_NEXT_QUEST = 7141;

	private static final Location[] PAGE_LOCS =
	{ new Location(51992, -54424, -3160), new Location(52328, -53400, -3160), new Location(51928, -51656, -3096) };

	private L2NpcInstance _duke;
	private L2NpcInstance _page;

	private State CREATED;
	private State STARTED;
	private State COMPLETED;

	public Q021_HiddenTruth(int questId, String name, String descr)
	{
		super(questId, name, descr);
		CREATED = new State("Start", this);
		STARTED = new State("Started", this);
		COMPLETED = new State("Completed", this);
		this.setInitialState(CREATED);

		questItemIds(CROSS_OF_EINHASAD);

		addStartNpc(MYSTERIOUS_WIZARD);
		addTalkId(MYSTERIOUS_WIZARD, TOMBSTONE, VON_HELLMAN_DUKE, VON_HELLMAN_PAGE, BROKEN_BOOKSHELF, AGRIPEL, DOMINIC,
		        BENEDICT, INNOCENTIN);
	}

	@Override
	public String onAdvEvent(String event, L2NpcInstance npc, L2PcInstance player)
	{
		String htmltext = event;
		QuestState st = player.getQuestState(qn);
		if (st == null)
			return htmltext;

		if (event.equalsIgnoreCase("31522-02.htm"))
		{
			st.setState(STARTED);
			st.set("cond", "1");
			st.playSound(SOUND_QUEST_START);
		}
		else if (event.equalsIgnoreCase("31523-03.htm"))
		{
			st.set("cond", "2");
			st.playSound(SOUND_QUEST_MIDDLE);
			spawnTheDuke(player);
		}
		else if (event.equalsIgnoreCase("31524-06.htm"))
		{
			st.set("cond", "3");
			st.playSound(SOUND_QUEST_MIDDLE);
			spawnThePage(player);
		}
		else if (event.equalsIgnoreCase("31526-08.htm"))
		{
			st.set("cond", "5");
			st.playSound(SOUND_QUEST_MIDDLE);
		}
		else if (event.equalsIgnoreCase("31526-14.htm"))
		{
			st.set("cond", "6");
			st.playSound(SOUND_QUEST_MIDDLE);
			st.giveItems(CROSS_OF_EINHASAD, 1);
		}
		else if (event.equalsIgnoreCase("1"))
		{
			_page.getAI().setIntention(CtrlIntention.AI_INTENTION_MOVE_TO, PAGE_LOCS[0]);
			_page.broadcastNpcSay("Follow me...");

			startQuestTimer("2", 5000, _page, player, false);
			return null;
		}
		else if (event.equalsIgnoreCase("2"))
		{
			_page.getAI().setIntention(CtrlIntention.AI_INTENTION_MOVE_TO, PAGE_LOCS[1]);

			startQuestTimer("3", 12000, _page, player, false);
			return null;
		}
		else if (event.equalsIgnoreCase("3"))
		{
			_page.getAI().setIntention(CtrlIntention.AI_INTENTION_MOVE_TO, PAGE_LOCS[2]);

			startQuestTimer("4", 18000, _page, player, false);
			return null;
		}
		else if (event.equalsIgnoreCase("4"))
		{
			st.set("end_walk", "1");

			_page.broadcastNpcSay("Please check this bookcase, " + player.getName() + ".");

			startQuestTimer("5", 47000, _page, player, false);
			return null;
		}
		else if (event.equalsIgnoreCase("5"))
		{
			_page.broadcastNpcSay("I'm confused! Maybe it's time to go back.");
			return null;
		}
		else if (event.equalsIgnoreCase("31328-05.htm"))
		{
			if (st.hasQuestItems(CROSS_OF_EINHASAD))
			{
				st.takeItems(CROSS_OF_EINHASAD, 1);
				st.giveItems(CROSS_OF_EINHASAD_NEXT_QUEST, 1);
				st.playSound(SOUND_QUEST_DONE);
				st.exitQuest(false);
			}
		}
		else if (event.equalsIgnoreCase("dukeDespawn"))
		{
			_duke.deleteMe();
			_duke = null;

			return null;
		}
		else if (event.equalsIgnoreCase("pageDespawn"))
		{
			_page.deleteMe();
			_page = null;

			return null;
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
			htmltext = (player.getLevel() < 63) ? "31522-03.htm" : "31522-01.htm";

		else if (st.getState() == STARTED)
		{
			int cond = st.getInt("cond");
			switch (npc.getNpcId())
			{
				case MYSTERIOUS_WIZARD:
					htmltext = "31522-05.htm";
					break;

				case TOMBSTONE:
					if (cond == 1)
						htmltext = "31523-01.htm";
					else if (cond == 2 || cond == 3)
					{
						htmltext = "31523-04.htm";
						spawnTheDuke(player);
					}
					else if (cond > 3)
						htmltext = "31523-04.htm";
					break;

				case VON_HELLMAN_DUKE:
					if (cond == 2)
						htmltext = "31524-01.htm";
					else if (cond == 3)
					{
						htmltext = "31524-07.htm";
						spawnThePage(player);
					}
					else if (cond > 3)
						htmltext = "31524-07a.htm";
					break;

				case VON_HELLMAN_PAGE:
					if (cond == 3)
					{
						if (st.getInt("end_walk") == 1)
						{
							htmltext = "31525-02.htm";
							st.set("cond", "4");
							st.playSound(SOUND_QUEST_MIDDLE);
						}
						else
							htmltext = "31525-01.htm";
					}
					else if (cond == 4)
						htmltext = "31525-02.htm";
					break;

				case BROKEN_BOOKSHELF:
					if ((cond == 3 && st.getInt("end_walk") == 1) || cond == 4)
					{
						htmltext = "31526-01.htm";

						st.set("cond", "5");
						st.playSound(SOUND_QUEST_MIDDLE);

						if (_page != null)
						{
							cancelQuestTimer("5", _page, player);
							cancelQuestTimer("pageDespawn", _page, player);

							_page.deleteMe();
							_page = null;
						}

						if (_duke != null)
						{
							cancelQuestTimer("dukeDespawn", _duke, player);

							_duke.deleteMe();
							_duke = null;
						}
					}
					else if (cond == 5)
						htmltext = "31526-10.htm";
					else if (cond > 5)
						htmltext = "31526-15.htm";
					break;

				case AGRIPEL:
				case BENEDICT:
				case DOMINIC:
					if ((cond == 6 || cond == 7) && st.hasQuestItems(CROSS_OF_EINHASAD))
					{
						int npcId = npc.getNpcId();

						// For cond 6, make checks until cond 7 is activated.
						if (cond == 6)
						{
							int npcId1 = 0, npcId2 = 0;
							if (npcId == AGRIPEL)
							{
								npcId1 = BENEDICT;
								npcId2 = DOMINIC;
							}
							else if (npcId == BENEDICT)
							{
								npcId1 = AGRIPEL;
								npcId2 = DOMINIC;
							}
							else if (npcId == DOMINIC)
							{
								npcId1 = AGRIPEL;
								npcId2 = BENEDICT;
							}

							if (st.getInt(String.valueOf(npcId1)) == 1 && st.getInt(String.valueOf(npcId2)) == 1)
							{
								st.set("cond", "7");
								st.playSound(SOUND_QUEST_MIDDLE);
							}
							else
								st.set(String.valueOf(npcId), "1");
						}

						htmltext = npcId + "-01.htm";
					}
					break;

				case INNOCENTIN:
					if (cond == 7 && st.hasQuestItems(CROSS_OF_EINHASAD))
						htmltext = "31328-01.htm";
					break;
			}
		}

		else if (st.getState() == COMPLETED)
		{
			if (npc.getNpcId() == INNOCENTIN)
				htmltext = "31328-06.htm";
			else
				htmltext = getAlreadyCompletedMsg();
		}

		return htmltext;
	}

	private void spawnTheDuke(L2PcInstance player)
	{
		if (_duke == null)
		{
			_duke = addSpawn(VON_HELLMAN_DUKE, 51432, -54570, -3136, 0, false, 0, true);
			_duke.broadcastNpcSay("Who awoke me?");

			startQuestTimer("dukeDespawn", 300000, _duke, player, false);
		}
	}

	private void spawnThePage(L2PcInstance player)
	{
		if (_page == null)
		{
			_page = addSpawn(VON_HELLMAN_PAGE, 51608, -54520, -3168, 0, false, 0, true);
			_page.broadcastNpcSay("My master has instructed me to be your guide, " + player.getName() + ".");

			startQuestTimer("1", 4000, _page, player, false);
			startQuestTimer("pageDespawn", 90000, _page, player, false);
		}
	}

	public static void main(String[] args)
	{
		new Q021_HiddenTruth(21, qn, "Hidden Truth");
	}
}