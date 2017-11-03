# Made by Mr. Have fun! - Version 0.3 by DrLecter
import sys
from com.it.br.gameserver.model.quest import State
from com.it.br.gameserver.model.quest import QuestState
from com.it.br.gameserver.model.quest.jython import QuestJython as JQuest

qn = "264_KeenClaws"

WOLF_CLAW = 1367

DROP={20003:[[5,10,8],[0,5,2]],20456:[[16,20,2],[0,16,1]]}

class Quest (JQuest) :

 def __init__(self,id,name,descr): JQuest.__init__(self,id,name,descr)

 def onEvent (self,event,st) :
    htmltext = event
    if event == "30136-03.htm" :
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
     if player.getLevel() >= 3 :
       htmltext = "30136-02.htm"
     else:
       htmltext = "30136-01.htm"
       st.exitQuest(1)
   else:
     count=st.getQuestItemsCount(WOLF_CLAW)
     if count<50 :
       htmltext = "30136-04.htm"
     else :
       st.takeItems(WOLF_CLAW,-1)
       if st.getRandom(17) < 6 :
          st.giveItems(5140,1)
       else :
          st.giveItems(734,1)
       htmltext = "30136-05.htm"
       st.exitQuest(1)
       st.playSound("ItemSound.quest_finish")
   return htmltext

 def onKill(self,npc,player,isPet):
   st = player.getQuestState(qn)
   if not st : return 
   if st.getState() != STARTED : return 
   
   if st.getInt("cond") == 1:
      npcId = npc.getNpcId()
      count=st.getQuestItemsCount(WOLF_CLAW)
      chance = st.getRandom(20)
      qty=0
      for i in DROP[npcId]:
         if i[0]<=chance<i[1]:
            qty=i[2]
      if qty :
        if count+qty>50 :
          qty=50-count
        if count+qty==50:
          st.playSound("ItemSound.quest_middle")
          st.set("cond","2")
        else :
          st.playSound("ItemSound.quest_itemget")
        st.giveItems(WOLF_CLAW,qty)
   return

QUEST       = Quest(264,qn,"Keen Claws")
CREATED     = State('Start', QUEST)
STARTING    = State('Starting', QUEST)
STARTED     = State('Started', QUEST)
COMPLETED   = State('Completed', QUEST)

QUEST.setInitialState(CREATED)
QUEST.addStartNpc(30136)

QUEST.addTalkId(30136)

QUEST.addKillId(20003)
QUEST.addKillId(20456)