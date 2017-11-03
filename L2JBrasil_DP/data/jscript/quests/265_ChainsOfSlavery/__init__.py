# Made by Mr. Have fun! - Version 0.3 by DrLecter
import sys
from com.it.br.gameserver.model.quest import State
from com.it.br.gameserver.model.quest import QuestState
from com.it.br.gameserver.model.quest.jython import QuestJython as JQuest

qn = "265_ChainsOfSlavery"

IMP_SHACKLES = 1368
ADENA = 57

class Quest (JQuest) :

 def __init__(self,id,name,descr): JQuest.__init__(self,id,name,descr)

 def onEvent (self,event,st) :
    htmltext = event
    if event == "30357-03.htm" :
      st.set("cond","1")
      st.setState(STARTED)
      st.playSound("ItemSound.quest_accept")
    elif event == "30357-06.htm" :
      st.exitQuest(1)
      st.playSound("ItemSound.quest_finish")
    return htmltext

 def onTalk (self,npc,player):
   htmltext = "<html><body>You are either not carrying out your quest or don't meet the criteria.</body></html>"
   st = player.getQuestState(qn)
   if not st : return htmltext

   npcId = npc.getNpcId()
   id = st.getState()

   if id == CREATED :
     st.set("cond","0")
   if st.getInt("cond")==0 :
     if player.getRace().ordinal() != 2 :
       htmltext = "30357-00.htm"
       st.exitQuest(1)
     else :
       if player.getLevel()<6 :
          htmltext = "30357-01.htm"
          st.exitQuest(1)
       else:
          htmltext = "30357-02.htm"
   else :
     count=st.getQuestItemsCount(IMP_SHACKLES)
     if count :
       if count >= 10:
          st.giveItems(ADENA,13*count+500)
       else :
          st.giveItems(ADENA,13*count)
       st.takeItems(IMP_SHACKLES,-1)
       htmltext = "30357-05.htm"
     else:
       htmltext = "30357-04.htm"
   return htmltext

 def onKill(self,npc,player,isPet):
   st = player.getQuestState(qn)
   if not st : return 
   if st.getState() != STARTED : return 
   
   if st.getRandom(10) < (5+((npc.getNpcId()-20000)^4)) :
     st.giveItems(IMP_SHACKLES,1)
     st.playSound("ItemSound.quest_itemget")
   return

QUEST       = Quest(265,qn,"Chains Of Slavery")
CREATED     = State('Start', QUEST)
STARTING    = State('Starting', QUEST)
STARTED     = State('Started', QUEST)
COMPLETED   = State('Completed', QUEST)

QUEST.setInitialState(CREATED)
QUEST.addStartNpc(30357)

QUEST.addTalkId(30357)

QUEST.addKillId(20004)
QUEST.addKillId(20005)