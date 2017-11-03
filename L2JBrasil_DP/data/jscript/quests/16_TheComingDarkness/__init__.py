# Made by disKret, Ancient Legion Server
import sys
from com.it.br.gameserver.model.quest import State
from com.it.br.gameserver.model.quest import QuestState
from com.it.br.gameserver.model.quest.jython import QuestJython as JQuest

qn = "16_TheComingDarkness"

#NPC
HIERARCH = 31517
EVIL_ALTAR_1 = 31512
EVIL_ALTAR_2 = 31513
EVIL_ALTAR_3 = 31514
EVIL_ALTAR_4 = 31515
EVIL_ALTAR_5 = 31516

#ITEMS
CRYSTAL_OF_SEAL = 7167

class Quest (JQuest) :

 def __init__(self,id,name,descr): JQuest.__init__(self,id,name,descr)

 def onEvent (self,event,st) :
   htmltext = event
   cond = st.getInt("cond")
   if event == "31517-1.htm" :
     return htmltext
   if event == "31517-2.htm" :
     st.giveItems(CRYSTAL_OF_SEAL,5)
     st.set("cond","1")
     st.setState(STARTED)
     st.playSound("ItemSound.quest_accept")
   if event == "31512-1.htm" :
     if cond == 1 :
       st.takeItems(CRYSTAL_OF_SEAL,1)
       st.set("cond","2")
       st.playSound("ItemSound.quest_middle")
   if event == "31513-1.htm" :
     if cond == 2 :
       st.takeItems(CRYSTAL_OF_SEAL,1)
       st.set("cond","3")
       st.playSound("ItemSound.quest_middle")
   if event == "31514-1.htm" :
     if cond == 3 :
       st.takeItems(CRYSTAL_OF_SEAL,1)
       st.set("cond","4")
       st.playSound("ItemSound.quest_middle")
   if event == "31515-1.htm" :
     if cond == 4 :
       st.takeItems(CRYSTAL_OF_SEAL,1)
       st.set("cond","5")
       st.playSound("ItemSound.quest_middle")
   if event == "31516-1.htm" :
     if cond == 5 :
       st.takeItems(CRYSTAL_OF_SEAL,1)
       st.set("cond","6")
       st.playSound("ItemSound.quest_middle")
   return htmltext

 def onTalk (self,npc,player):
   htmltext = "<html><body>You are either not carrying out your quest or don't meet the criteria.</body></html>"
   st = player.getQuestState(qn)
   if not st : return htmltext

   npcId = npc.getNpcId()
   cond = st.getInt("cond")
   id = st.getState()
   if id == CREATED :
     st.set("cond","0")
   if npcId == HIERARCH and st.getInt("cond") == 0 :
     if player.getLevel() >= 62 :
       htmltext = "31517-0.htm"
     if id == COMPLETED :
       htmltext = "<html><body>This quest has already been completed.</body></html>"
     else:
       return htmltext
       st.exitQuest(1)
   if id == STARTED :    
       if npcId == EVIL_ALTAR_1 and cond == 1 :
         htmltext = "31512-0.htm"
       if npcId == EVIL_ALTAR_2 and cond == 2 :
         htmltext = "31513-0.htm"
       if npcId == EVIL_ALTAR_3 and cond == 3 :
         htmltext = "31514-0.htm"
       if npcId == EVIL_ALTAR_4 and cond== 4 :
         htmltext = "31515-0.htm"
       if npcId == EVIL_ALTAR_5 and cond == 5 :
         htmltext = "31516-0.htm"
       if npcId == HIERARCH and cond == 6 :
         st.addExpAndSp(221958,0)
         st.set("cond","0")
         st.setState(COMPLETED)
         st.playSound("ItemSound.quest_finish")
         htmltext = "31517-3.htm"
   return htmltext

QUEST       = Quest(16,qn,"The Coming Darkness")
CREATED     = State('Start', QUEST)
STARTED     = State('Started', QUEST)
COMPLETED   = State('Completed', QUEST)

QUEST.setInitialState(CREATED)
QUEST.addStartNpc(31517)
QUEST.addTalkId(31517)

for altars in range(31512,31517):
  QUEST.addTalkId(altars)