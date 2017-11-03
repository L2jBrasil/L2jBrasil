# Made by Emperorc
import sys
from com.it.br.gameserver.model.quest import State
from com.it.br.gameserver.model.quest import QuestState
from com.it.br.gameserver.model.quest.jython import QuestJython as JQuest

qn = "20_BringUpWithLove"

#NPCs
TUNATUN = 31537

#ITEMS 
GEM = 7185

#NOTE: This quest requires the giving of item GEM upon successful growth and taming of a wild beast, so the rewarding of
# the gem is handled by the feedable_beasts ai script.

class Quest (JQuest) :

 def __init__(self,id,name,descr): JQuest.__init__(self,id,name,descr)

 def onEvent (self,event,st) :
   htmltext = event 
   if event == "31537-09.htm" : 
     st.set("cond","1") 
     st.setState(STARTED) 
     st.playSound("ItemSound.quest_accept")
   elif event == "31537-12.htm" :
       st.giveItems(57,68500)
       st.takeItems(GEM,-1)
       st.playSound("ItemSound.quest_finish")
       st.setState(COMPLETED)
       st.set("onlyone","1")
   return htmltext 

 def onTalk (self,npc,player):
   st = player.getQuestState(qn)
   htmltext = "<html><body>You are either not carrying out your quest or don't meet the criteria.</body></html>" 
   if not st: return htmltext
   npcId = npc.getNpcId()
   id = st.getState()
   cond = st.getInt("cond") 
   onlyone = st.getInt("onlyone")
   GEM_COUNT = st.getQuestItemsCount(GEM)
   if id == COMPLETED :
       htmltext = "<html><body>This quest has already been completed.</body></html>"
   elif id == CREATED and onlyone == 0 : 
     if player.getLevel() >= 65 :
         htmltext = "31537-01.htm" 
     else: 
         htmltext = "31537-02.htm" 
         st.exitQuest(1) 
   elif id == STARTED :
       if GEM_COUNT < 1 :
           htmltext = "31537-10.htm"
       else :
           htmltext = "31537-11.htm"
   return htmltext

QUEST     = Quest(20,qn,"Bring up with Love") 
CREATED   = State('Start',     QUEST) 
STARTED   = State('Started',   QUEST) 
COMPLETED = State('Completed', QUEST) 

QUEST.setInitialState(CREATED)
QUEST.addStartNpc(TUNATUN) 
QUEST.addTalkId(TUNATUN)