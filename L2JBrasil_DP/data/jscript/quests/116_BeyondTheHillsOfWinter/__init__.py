# Made by Ky6uk, update by Guma

#----------------------------------------------------------------------------
# This quest starts in the Dwarven Village in the Elder Council with
# Gray Pillar Member Filaur. He asks you to bring 20 bandages,
# 5 Energy Stones and 10 "skeleton keys" which are just Key of Thief.
# You can get these at the Grocery store nearby, it cost me 15,590 to do so.
# Reward: 1650 SSD or 16500 adena
#----------------------------------------------------------------------------

import sys
from com.it.br import Config
from com.it.br.gameserver.model.quest import State
from com.it.br.gameserver.model.quest import QuestState
from com.it.br.gameserver.model.quest.jython import QuestJython as JQuest

qn = "116_BeyondTheHillsOfWinter"

# NPCs
FILAUR = 30535
OBI = 32052

# ITEMs
BANDAGE = 1833
ESTONE = 5589
TKEY = 1661
SGOODS = 8098
SS = 1463
ADENA = 57

class Quest (JQuest) :
	
	def __init__(self,id, name, descr) :
		JQuest.__init__(self, id, name, descr)
		self.questItemIds = [SGOODS]
		
	def onEvent (self,event,st) :
		htmltext = event
		id = st.getState()
		cond = st.getInt("cond")
		if st.getPlayer().getClassId().getId() not in [0x35, 0x36, 0x37, 0x38, 0x39, 0x75, 0x76] :
			htmltext = "30535-00.htm"
		elif event == "30535-02.htm" :
			st.set("cond","1")
			st.setState(State.STARTED)
			st.playSound("ItemSound.quest_accept")
		elif event == "30535-05.htm" :
			st.giveItems(SGOODS, 1)
			st.playSound("ItemSound.quest_itemget")
			st.set("cond","3")
		elif event == "32052-01.htm" :
			htmltext = "32052-01.htm"
		elif event == "materials" :
			st.takeItems(SGOODS, -1)
			st.playSound("ItemSound.quest_itemget")
			st.giveItems(SS, int(1650*Config.RATE_QUESTS_REWARD))
			st.addExpAndSp(82792,4981)
			htmltext = "32052-02.htm"
			st.playSound("ItemSound.quest_finish")
			st.exitQuest(False)
		elif event == "adena" :
			st.takeItems(SGOODS, -1)
			st.playSound("ItemSound.quest_itemget")
			st.giveItems(ADENA, int(16500*Config.RATE_QUESTS_REWARD))
			st.addExpAndSp(82792,4981)
			htmltext = "32052-02.htm"
			st.playSound("ItemSound.quest_finish")
			st.exitQuest(False)
		else :
			htmltext = "<html><body>You are either not on a quest that involves this NPC, or you don't meet this NPC's minimum quest requirements.</body></html>"
		return htmltext
		
	def onTalk (self, npc, player) :
		htmltext = "<html><body>You are either not on a quest that involves this NPC, or you don't meet this NPC's minimum quest requirements.</body></html>"
		st = player.getQuestState(qn)
		if not st : return htmltext
		
		npcId = npc.getNpcId()
		id = st.getState()
		cond = st.getInt("cond")
		if id == State.COMPLETED :
			htmltext = "<html><body>This quest has already been completed.</body></html>"
		elif npcId == FILAUR :
			if cond == 0 :
				if player.getLevel() >= 30 :
					htmltext = "30535-01.htm"
				else:
					htmltext = "30535-00.htm"
					st.exitQuest(1)
			elif cond == 1 :
				if st.getQuestItemsCount(BANDAGE) >= 20 and st.getQuestItemsCount(ESTONE) >= 5 and st.getQuestItemsCount(TKEY) >= 10 :
					htmltext = "30535-03.htm"
					st.takeItems(BANDAGE, 20)
					st.takeItems(ESTONE, 5)
					st.takeItems(TKEY, 10)
					st.set("cond","2")
				else :
					htmltext = "30535-04.htm"
			elif cond == 2 :
				htmltext = "30535-03.htm"
			elif cond == 3 :
				htmltext = "30535-05.htm"
		elif npcId == OBI and st.getQuestItemsCount(SGOODS) == 1 :
			if cond == 3 :
				htmltext = "32052-00.htm"
		return htmltext

QUEST = Quest(116,qn,"Beyond The Hills Of Winter")

QUEST.addStartNpc(FILAUR)
QUEST.addTalkId(FILAUR)
QUEST.addTalkId(OBI)
