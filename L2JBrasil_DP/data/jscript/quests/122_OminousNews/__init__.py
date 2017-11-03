# Made by Polo
import sys
from com.it.br.gameserver.model.quest import State
from com.it.br.gameserver.model.quest import QuestState
from com.it.br.gameserver.model.quest.jython import QuestJython as JQuest

qn = "122_OminousNews"

#Npc
MOIRA = 31979
KARUDA = 32017

default="<html><body>You are either not carrying out your quest or don't meet the criteria.</body></html>"

class Quest (JQuest) :

 def __init__(self,id,name,descr): JQuest.__init__(self,id,name,descr)

 def onEvent (self,event,st) :
   htmltext = default
   id = st.getState()
   cond = st.getInt("cond")
   if id != COMPLETED :
     htmltext = event
     if htmltext == "31979-03.htm" and cond == 0 :
       st.set("cond","1")
       st.setState(STARTED)
       st.playSound("ItemSound.quest_accept")
     elif htmltext == "32017-02.htm" :
       if cond == 1 and st.getInt("ok") :
         st.giveItems(57,1695)
         st.unset("cond")
         st.unset("ok")
         st.setState(COMPLETED)
         st.playSound("ItemSound.quest_finish")
       else :
         htmltext=default
   return htmltext

 def onTalk (self,npc,player):
   npcId = npc.getNpcId()
   htmltext = default
   st = player.getQuestState(qn)
   if not st : return htmltext

   id = st.getState()
   cond = st.getInt("cond")
   if id == COMPLETED :
      htmltext="<html><body>This quest have already been completed</body></html>"
   elif npcId == MOIRA :
      if cond == 0 :
         if player.getLevel()>=20 :
            htmltext = "31979-02.htm"
         else :
            htmltext = "31979-01.htm"
            st.exitQuest(1)
      else:
         htmltext = "31979-03.htm"
   elif npcId == KARUDA and cond==1 and id == STARTED:
      htmltext = "32017-01.htm"
      st.set("ok","1")
   return htmltext

QUEST       = Quest(122,qn,"Ominous News")
CREATED     = State('Start', QUEST)
STARTED     = State('Started', QUEST)
COMPLETED   = State('Completed', QUEST)

QUEST.setInitialState(CREATED)
QUEST.addStartNpc(MOIRA)

QUEST.addTalkId(MOIRA)

QUEST.addTalkId(KARUDA)