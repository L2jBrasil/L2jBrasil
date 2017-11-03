# Made by disKret
import sys
from com.it.br.gameserver.model.quest import State
from com.it.br.gameserver.model.quest import QuestState
from com.it.br.gameserver.model.quest.jython import QuestJython as JQuest

qn = "247_PossessorOfAPreciousSoul_4"

#NPC
CARADINE = 31740
LADY_OF_LAKE = 31745

#QUEST ITEM
CARADINE_LETTER_LAST = 7679
NOBLESS_TIARA = 7694

class Quest (JQuest) :

 def __init__(self,id,name,descr): JQuest.__init__(self,id,name,descr)

 def onEvent (self,event,st) :
   htmltext = event
   cond = st.getInt("cond") 
   if event == "31740-3.htm" :
     if cond == 0 :
       st.set("cond","1")
       st.setState(STARTED)
       st.playSound("ItemSound.quest_accept")
   if event == "31740-4.htm" :
     if cond == 1 :
       return htmltext
   if event == "31740-5.htm" :
     if cond == 1 :
       st.set("cond","2")
       st.takeItems(CARADINE_LETTER_LAST,1)
       st.getPlayer().teleToLocation(143209,43968,-3038)
       return htmltext
   if event == "31740-5.htm" :
     if cond == 2 :
       return htmltext
   if event == "31745-2.htm" :
     if cond == 2 :
       return htmltext
   if event == "31745-3.htm" :
     if cond == 2 :
       return htmltext
   if event == "31745-4.htm" :
     if cond == 2 :
       return htmltext
   if event == "31745-5.htm" :
     if cond == 2 :
       st.set("cond","0")
       st.getPlayer().setNoble(True)
       st.giveItems(NOBLESS_TIARA,1)
       st.playSound("ItemSound.quest_finish")
       st.setState(COMPLETED)
   return htmltext

 def onTalk (self,npc,player):
   htmltext = "<html><body>You are either not carrying out your quest or don't meet the criteria.</body></html>"
   st = player.getQuestState(qn)
   if not st : return htmltext

   npcId = npc.getNpcId()
   id = st.getState()
   if npcId != CARADINE and id != STARTED : return htmltext

   cond = st.getInt("cond")
   if id == CREATED :
     st.set("cond","0")
   if player.isSubClassActive() :
     if npcId == CARADINE and st.getQuestItemsCount(CARADINE_LETTER_LAST) == 1 :
       if cond in [0,1] :
         if id == COMPLETED :
           htmltext = "<html><body>This quest has already been completed.</body></html>"
         elif player.getLevel() < 75 : 
           htmltext = "31740-2.htm"
           st.exitQuest(1)
         elif player.getLevel() >= 75 :
           htmltext = "31740-1.htm"
     if npcId == CARADINE and cond == 2 :
         htmltext = "31740-6.htm"
     if npcId == LADY_OF_LAKE and cond == 2 :
       htmltext = "31745-6.htm"
     if npcId == LADY_OF_LAKE and cond == 2 :
       htmltext = "31745-1.htm"
   return htmltext

QUEST       = Quest(247,qn,"Possessor Of A Precious Soul - 4")
CREATED     = State('Start', QUEST)
STARTED     = State('Started', QUEST)
COMPLETED   = State('Completed', QUEST)

QUEST.setInitialState(CREATED)
QUEST.addStartNpc(CARADINE)
QUEST.addTalkId(CARADINE)

QUEST.addTalkId(LADY_OF_LAKE)