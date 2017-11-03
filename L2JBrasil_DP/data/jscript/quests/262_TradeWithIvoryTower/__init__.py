# Made by Mr. - Version 0.3 by DrLecter
import sys
from com.it.br.gameserver.model.quest import State
from com.it.br.gameserver.model.quest import QuestState
from com.it.br.gameserver.model.quest.jython import QuestJython as JQuest

qn = "262_TradeWithIvoryTower"

FUNGUS_SAC = 707
ADENA = 57

class Quest (JQuest) :

 def __init__(self,id,name,descr): JQuest.__init__(self,id,name,descr)

 def onEvent (self,event,st) :
    htmltext = event
    if event == "30137-03.htm" :
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
     if player.getLevel() >= 8 :
       htmltext = "30137-02.htm"
     else:
       htmltext = "30137-01.htm"
       st.exitQuest(1)
   else :
     if st.getQuestItemsCount(FUNGUS_SAC)<10 :
       htmltext = "30137-04.htm"
     else :
       st.giveItems(ADENA,3000)
       st.takeItems(FUNGUS_SAC,-1)
       st.exitQuest(1)
       st.playSound("ItemSound.quest_finish")
       htmltext = "30137-05.htm"
   return htmltext

 def onKill(self,npc,player,isPet):
   st = player.getQuestState(qn)
   if not st : return 
   if st.getState() != STARTED : return 
   
   count = st.getQuestItemsCount(FUNGUS_SAC)
   chance = 3
   if npc.getNpcId() == 20400 : chance += 1
   if count < 10 and st.getRandom(10) < chance :
     st.giveItems(FUNGUS_SAC,1)
     if count == 9 :
       st.playSound("ItemSound.quest_middle")
       st.set("cond","2")
     else :
       st.playSound("ItemSound.quest_itemget")
   return

QUEST       = Quest(262,qn,"Trade with the Ivory Tower")
CREATED     = State('Start', QUEST)
STARTING     = State('Starting', QUEST)
STARTED     = State('Started', QUEST)
COMPLETED   = State('Completed', QUEST)

QUEST.setInitialState(CREATED)
QUEST.addStartNpc(30137)

QUEST.addTalkId(30137)

QUEST.addKillId(20400)
QUEST.addKillId(20007)