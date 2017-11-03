# Made by Mr. - Version 0.3 by DrLecter
import sys
from com.it.br.gameserver.model.quest import State
from com.it.br.gameserver.model.quest import QuestState
from com.it.br.gameserver.model.quest.jython import QuestJython as JQuest

qn = "324_SweetestVenom"

VENOM_SAC = 1077
ADENA = 57

class Quest (JQuest) :

 def __init__(self,id,name,descr): JQuest.__init__(self,id,name,descr)

 def onEvent (self,event,st) :
    htmltext = event
    if event == "30351-04.htm" :
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
   if st.getInt("cond")==0 :
     if player.getLevel() >= 18 :
       htmltext = "30351-03.htm"
     else:
       htmltext = "30351-02.htm"
       st.exitQuest(1)
   else :
     if st.getQuestItemsCount(VENOM_SAC)<10 :
       htmltext = "30351-05.htm"
     else :
       st.takeItems(VENOM_SAC,-1)
       st.giveItems(ADENA,5810)
       st.exitQuest(1)
       st.playSound("ItemSound.quest_finish")
       htmltext = "30351-06.htm"
   return htmltext

 def onKill(self,npc,player,isPet):
   st = player.getQuestState(qn)
   if not st : return 
   if st.getState() != STARTED : return 
   
   chance=22+(((npc.getNpcId()-20000)^34)/4)
   count=st.getQuestItemsCount(VENOM_SAC)
   if count < 10 and st.getRandom(100) < chance :
     st.giveItems(VENOM_SAC,1)
     if count == 9 :
       st.playSound("ItemSound.quest_middle")
       st.set("cond","2")
     else :
       st.playSound("ItemSound.quest_itemget")
   return

QUEST       = Quest(324,qn,"Sweetest Venom")
CREATED     = State('Start', QUEST)
STARTING     = State('Starting', QUEST)
STARTED     = State('Started', QUEST)
COMPLETED   = State('Completed', QUEST)

QUEST.setInitialState(CREATED)
QUEST.addStartNpc(30351)

QUEST.addTalkId(30351)

QUEST.addKillId(20034)
QUEST.addKillId(20038)
QUEST.addKillId(20043)