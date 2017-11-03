# Maked by Mr. - Version 0.3 by DrLecter
import sys
from com.it.br.gameserver.model.quest import State
from com.it.br.gameserver.model.quest import QuestState
from com.it.br.gameserver.model.quest.jython import QuestJython as JQuest

qn = "272_WrathOfAncestors"

GRAVE_ROBBERS_HEAD = 1474
ADENA = 57

class Quest (JQuest) :

 def __init__(self,id,name,descr): JQuest.__init__(self,id,name,descr)

 def onEvent (self,event,st) :
    htmltext = event
    if event == "30572-03.htm" :
      st.set("cond","1")
      st.setState(STARTED)
      st.playSound("ItemSound.quest_accept")
    return htmltext

 def onTalk (self,npc,player):
   htmltext = "<html><body>You are either not carrying out your quest or don't meet the criteria.</body></html>"
   st = player.getQuestState(qn)
   if not st : return htmltext

   npcId = npc.getNpcId()
   id = st.getState()

   if id == CREATED :
     st.set("cond","0")
   if st.getInt("cond") == 0 :
     if player.getRace().ordinal() != 3 :
        htmltext = "30572-00.htm"
        st.exitQuest(1)
     else :
        if player.getLevel() < 5 :
          htmltext = "30572-01.htm"
          st.exitQuest(1)
        else:
          htmltext = "30572-02.htm"
   else :
     if st.getQuestItemsCount(GRAVE_ROBBERS_HEAD) < 50 :
        htmltext = "30572-04.htm"
     else:
        htmltext = "30572-05.htm"
        st.exitQuest(1)
        st.playSound("ItemSound.quest_finish")
        st.giveItems(ADENA,1500)
        st.takeItems(GRAVE_ROBBERS_HEAD,-1)
   return htmltext

 def onKill(self,npc,player,isPet):
   st = player.getQuestState(qn)
   if not st : return 
   if st.getState() != STARTED : return 
   
   count = st.getQuestItemsCount(GRAVE_ROBBERS_HEAD)  
   if count < 50 :
      st.giveItems(GRAVE_ROBBERS_HEAD,1)
      if count < 49 :
         st.playSound("ItemSound.quest_itemget")
      else:
         st.playSound("ItemSound.quest_middle")
         st.set("cond","2")
   return

QUEST       = Quest(272,qn,"Wrath Of Ancestors")
CREATED     = State('Start', QUEST)
STARTING    = State('Starting', QUEST)
STARTED     = State('Started', QUEST)
COMPLETED   = State('Completed', QUEST)

QUEST.setInitialState(CREATED)
QUEST.addStartNpc(30572)

QUEST.addTalkId(30572)

QUEST.addKillId(20319)
QUEST.addKillId(20320)