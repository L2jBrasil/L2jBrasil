# Maxi
import sys
from com.it.br.gameserver.model.quest import State
from com.it.br.gameserver.model.quest import QuestState
from com.it.br.gameserver.model.quest.jython import QuestJython as JQuest

qn = "655_AGrandPlanForTamingWildBeasts"

# npcId
Messenger = 35627

# itemId list
CrystalPurity = 8084
License = 8293

class Quest (JQuest) :

	def __init__(self,id,name,descr): JQuest.__init__(self,id,name,descr)

	def onEvent(self, event, st):
		htmltext = event
		if event == "a2.htm" :
			st.set("cond", "1")
			st.setState(STARTED)
			st.playSound("ItemSound.quest_accept")
		if event == "a4.htm" :
			if st.getQuestItemsCount(CrystalPurity) == 10 :
				st.takeItems(CrystalPurity,-10)
				st.giveItems(License,1)
				st.set("cond", "3")
			else :
				htmltext = "a5.htm"
		return htmltext

	def onTalk (self,npc,player):
		htmltext = "<html><body>You are either not on a quest that involves this NPC, or you don't meet this NPC's minimum quest requirements.</body></html>"
		st = player.getQuestState(qn)
		npcId = npc.getNpcId()
		cond = st.getInt("cond")
		clan = player.getClan();
		if clan == None:
			htmltext = "a6.htm"
			return htmltext
		if clan.getLevel() < 4:
			htmltext = "a6.htm"
			return htmltext
		if not clan.getLeaderName() == player.getName():
			htmltext = "a6.htm"
			return htmltext
		if npcId == Messenger :
			if cond == 0 :
				htmltext = "a1.htm"
			elif cond > 1 :
				htmltext = "a3.htm"
		else:
			htmltext = None
			npc.showMessageWindow(player,3)
		return htmltext

QUEST = Quest(655,qn,"A Grand Plan For Taming Wild Beasts")
CREATED     = State('Start', QUEST)
STARTED     = State('Started', QUEST)

QUEST.setInitialState(CREATED)

QUEST.addStartNpc(Messenger)
QUEST.addTalkId(Messenger)