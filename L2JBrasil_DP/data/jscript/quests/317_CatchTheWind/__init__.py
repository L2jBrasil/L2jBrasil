# Made by Mr. - Version 0.3 by DrLecter
import sys
from com.it.br.gameserver.model.quest import State
from com.it.br.gameserver.model.quest import QuestState
from com.it.br.gameserver.model.quest.jython import QuestJython as JQuest

qn = "317_CatchTheWind"

WIND_SHARD = 1078
ADENA = 57

class Quest (JQuest) :

 def __init__(self,id,name,descr): JQuest.__init__(self,id,name,descr)

 def onEvent (self,event,st) :
    htmltext = event
    if event == "30361-04.htm" :
      st.set("cond","1")
      st.setState(STARTED)
      st.playSound("ItemSound.quest_accept")
    elif event == "30361-08.htm" :
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
     if player.getLevel() >= 18 :
       htmltext = "30361-03.htm"
     else:
       htmltext = "30361-02.htm"
       st.exitQuest(1)
   else :
     count = st.getQuestItemsCount(WIND_SHARD)
     if count :
       if count > 9 :
          st.giveItems(ADENA,2988+40*count)
       else :
          st.giveItems(ADENA,40*count)
       st.takeItems(WIND_SHARD,-1)
       htmltext = "30361-07.htm"
     else :
       htmltext = "30361-05.htm"
   return htmltext

 def onKill(self,npc,player,isPet):
   st = player.getQuestState(qn)
   if not st : return 
   if st.getState() != STARTED : return 
   
   if st.getRandom(100) < 50:
      st.giveItems(WIND_SHARD,1)
      st.playSound("ItemSound.quest_itemget")
   return

QUEST       = Quest(317,qn,"Catch The Wind")
CREATED     = State('Start', QUEST)
STARTING     = State('Starting', QUEST)
STARTED     = State('Started', QUEST)
COMPLETED   = State('Completed', QUEST)

QUEST.setInitialState(CREATED)
QUEST.addStartNpc(30361)

QUEST.addTalkId(30361)

QUEST.addKillId(20036)
QUEST.addKillId(20044)