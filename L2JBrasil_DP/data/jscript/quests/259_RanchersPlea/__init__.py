# Made by Mr. - Version 0.3 by DrLecter
import sys
from com.it.br.gameserver.model.quest import State
from com.it.br.gameserver.model.quest import QuestState
from com.it.br.gameserver.model.quest.jython import QuestJython as JQuest

qn = "259_RanchersPlea"

GIANT_SPIDER_SKIN = 1495
ADENA = 57
HEALING_POTION = 1061
WOODEN_ARROW = 17

class Quest (JQuest) :

 def __init__(self,id,name,descr): JQuest.__init__(self,id,name,descr)

 def onEvent (self,event,st) :
    htmltext = event
    count=st.getQuestItemsCount(GIANT_SPIDER_SKIN)
    if event == "30497-03.htm" :
      st.set("cond","1")
      st.setState(STARTED)
      st.playSound("ItemSound.quest_accept")
    elif event == "30497-06.htm" :
      st.exitQuest(1)
      st.playSound("ItemSound.quest_finish")
    elif event == "30405-04.htm" :
      if count >= 10 :
         st.giveItems(HEALING_POTION,1)
         st.takeItems(GIANT_SPIDER_SKIN,10)
      else:
         htmltext="Incorrect item count"
    elif event == "30405-05.htm" :
      if count >= 10 :
         st.giveItems(WOODEN_ARROW,50)
         st.takeItems(GIANT_SPIDER_SKIN,10)
      else:
         htmltext="Incorrect item count"
    elif event == "30405-07.htm" :
      if st.getQuestItemsCount(GIANT_SPIDER_SKIN) >= 10 :
        htmltext = "30405-06.htm"
    return htmltext

 def onTalk (self,npc,player):
   htmltext = "<html><body>You are either not carrying out your quest or don't meet the criteria.</body></html>"
   st = player.getQuestState(qn)
   if not st : return htmltext

   npcId = npc.getNpcId()
   id = st.getState()
   if npcId != 30497 and id != STARTED : return htmltext

   if id != STARTED :
     st.set("cond","0")
   if st.getInt("cond")==0 :
     if player.getLevel() >= 15 :
       htmltext = "30497-02.htm"
     else:
       htmltext = "30497-01.htm"
       st.exitQuest(1)
   else :
     count=st.getQuestItemsCount(GIANT_SPIDER_SKIN)
     if npcId == 30497 :
       if count == 0 :
         htmltext = "30497-04.htm"
       else :
         htmltext = "30497-05.htm"
         amount = count*25
         if count > 9:
            amount += 250
         st.rewardItems(ADENA,amount)
         st.takeItems(GIANT_SPIDER_SKIN,-1)
     else :
       if count < 10 :
         htmltext = "30405-01.htm"
       else :
         htmltext = "30405-02.htm"
   return htmltext

 def onKill(self,npc,player,isPet):
   st = player.getQuestState(qn)
   if not st : return 
   if st.getState() != STARTED : return 
   
   npcId = npc.getNpcId()
   st.giveItems(GIANT_SPIDER_SKIN,1)
   st.playSound("ItemSound.quest_itemget")
   return

QUEST       = Quest(259,qn,"Ranchers Plea")
CREATED     = State('Start', QUEST)
STARTING    = State('Starting', QUEST)
STARTED     = State('Started', QUEST)
COMPLETED   = State('Completed', QUEST)

QUEST.setInitialState(CREATED)
QUEST.addStartNpc(30497)
QUEST.addTalkId(30497)

QUEST.addTalkId(30405)

QUEST.addKillId(20103)
QUEST.addKillId(20106)
QUEST.addKillId(20108)