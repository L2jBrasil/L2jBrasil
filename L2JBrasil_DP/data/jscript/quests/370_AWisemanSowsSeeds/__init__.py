# Made by disKret
import sys
from com.it.br.gameserver.model.quest import State
from com.it.br.gameserver.model.quest import QuestState
from com.it.br.gameserver.model.quest.jython import QuestJython as JQuest

qn = "370_AWisemanSowsSeeds"

#NPC
CASIAN = 30612

#MOBS
MOBS = [20082,20084,20086,20089,20090]

#ITEMS
CHAPTER_OF_FIRE,CHAPTER_OF_WATER,CHAPTER_OF_WIND,CHAPTER_OF_EARTH = range(5917,5921)

class Quest (JQuest) :

 def __init__(self,id,name,descr): JQuest.__init__(self,id,name,descr)

 def onEvent (self,event,st) :
   htmltext = event
   if event == "30612-1.htm" :
     st.set("cond","1")
     st.setState(STARTED)
     st.set("awaitsPartyDrop","1")
     st.playSound("ItemSound.quest_accept")
   elif event == "30612-6.htm" :
     if st.getQuestItemsCount(CHAPTER_OF_FIRE) and \
        st.getQuestItemsCount(CHAPTER_OF_WATER) and \
        st.getQuestItemsCount(CHAPTER_OF_WIND) and \
        st.getQuestItemsCount(CHAPTER_OF_EARTH) :
       st.takeItems(CHAPTER_OF_FIRE,1)
       st.takeItems(CHAPTER_OF_WATER,1)
       st.takeItems(CHAPTER_OF_WIND,1)
       st.takeItems(CHAPTER_OF_EARTH,1)
       st.giveItems(57,3600)
       htmltext = "30612-8.htm"
   elif event == "30612-9.htm" :
     st.playSound("ItemSound.quest_finish")
     st.exitQuest(1)
   return htmltext

 def onTalk (self,npc,player):
   htmltext = "<html><body>You are either not carrying out your quest or don't meet the criteria.</body></html>"
   st = player.getQuestState(qn)
   if not st : return htmltext

   npcId = npc.getNpcId()
   id = st.getState()
   cond=st.getInt("cond")
   if cond == 0 :
     if player.getLevel() >= 28 :
       htmltext = "30612-0.htm"
     else:
       htmltext = "30612-0a.htm"
       st.exitQuest(1)
   elif cond :
     htmltext = "30612-4.htm"
   return htmltext

 def onKill(self,npc,player,isPet):
   partyMember = self.getRandomPartyMember(player,"awaitsPartyDrop","1")
   if not partyMember : return
   st = partyMember.getQuestState(qn)

   chance = st.getRandom(100)
   if chance in range(1,15) and st.getQuestItemsCount(CHAPTER_OF_FIRE) == 0 :
     st.giveItems(CHAPTER_OF_FIRE,1)
     st.playSound("ItemSound.quest_itemget")
   elif chance in range(25,40) and st.getQuestItemsCount(CHAPTER_OF_WATER) == 0 :
     st.giveItems(CHAPTER_OF_WATER,1)
     st.playSound("ItemSound.quest_itemget")
   elif chance in range(50,65) and st.getQuestItemsCount(CHAPTER_OF_WIND) == 0 :
     st.giveItems(CHAPTER_OF_WIND,1)
     st.playSound("ItemSound.quest_itemget")
   elif chance in range(75,90) and st.getQuestItemsCount(CHAPTER_OF_EARTH) == 0 :
     st.giveItems(CHAPTER_OF_EARTH,1)
     st.playSound("ItemSound.quest_itemget")
   if st.getQuestItemsCount(CHAPTER_OF_FIRE) and st.getQuestItemsCount(CHAPTER_OF_WATER) and st.getQuestItemsCount(CHAPTER_OF_WIND) and st.getQuestItemsCount(CHAPTER_OF_EARTH) :
       st.playSound("ItemSound.quest_middle")
       st.unset("awaitsPartyDrop")
   return

QUEST       = Quest(370,qn,"A Wiseman Sows Seeds")
CREATED     = State('Start', QUEST)
STARTED     = State('Started', QUEST)

QUEST.setInitialState(CREATED)
QUEST.addStartNpc(CASIAN)
QUEST.addTalkId(CASIAN)

for i in MOBS :
  QUEST.addKillId(i)