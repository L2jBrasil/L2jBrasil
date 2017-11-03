# Made by Mr. - Version 0.3 by DrLecter
import sys
from com.it.br.gameserver.model.quest import State
from com.it.br.gameserver.model.quest import QuestState
from com.it.br.gameserver.model.quest.jython import QuestJython as JQuest

qn = "260_HuntForOrcs"

ORC_AMULET = 1114
ORC_NECKLACE = 1115
ADENA = 57

class Quest (JQuest) :

 def __init__(self,id,name,descr): JQuest.__init__(self,id,name,descr)

 def onEvent (self,event,st) :
    htmltext = event
    if event == "30221-03.htm" :
      st.set("cond","1")
      st.setState(STARTED)
      st.playSound("ItemSound.quest_accept")
    elif event == "30221-06.htm" :
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
     if player.getRace().ordinal() != 1 :
       htmltext = "30221-00.htm"
       st.exitQuest(1)
     elif player.getLevel()<6 :
       htmltext = "30221-01.htm"
       st.exitQuest(1)
     else :
       htmltext = "30221-02.htm"
   else :
     amulet = st.getQuestItemsCount(ORC_AMULET)
     necklace = st.getQuestItemsCount(ORC_NECKLACE)
     if amulet == necklace == 0 :
       htmltext = "30221-04.htm"
     else :
       htmltext = "30221-05.htm"
       st.giveItems(ADENA,amulet*5+necklace*15)
       st.takeItems(ORC_AMULET,-1)
       st.takeItems(ORC_NECKLACE,-1)
   return htmltext

 def onKill(self,npc,player,isPet):
   st = player.getQuestState(qn)
   if not st : return 
   if st.getState() != STARTED : return 
   
   item=ORC_AMULET
   if npc.getNpcId() in range(20471,20474) :
     item = ORC_NECKLACE
   if st.getRandom(10)>4 :
     st.giveItems(item,1)
     st.playSound("ItemSound.quest_itemget")
   return

QUEST       = Quest(260,qn,"Hunt For Orcs")
CREATED     = State('Start', QUEST)
STARTING    = State('Starting', QUEST)
STARTED     = State('Started', QUEST)
COMPLETED   = State('Completed', QUEST)

QUEST.setInitialState(CREATED)
QUEST.addStartNpc(30221)

QUEST.addTalkId(30221)

QUEST.addKillId(20468)
QUEST.addKillId(20469)
QUEST.addKillId(20470)
QUEST.addKillId(20471)
QUEST.addKillId(20472)
QUEST.addKillId(20473)