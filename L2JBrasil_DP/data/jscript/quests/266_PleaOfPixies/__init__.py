# Made by Mr. - Version 0.3 by DrLecter
import sys
from com.it.br.gameserver.model.quest import State
from com.it.br.gameserver.model.quest import QuestState
from com.it.br.gameserver.model.quest.jython import QuestJython as JQuest

qn = "266_PleaOfPixies"

PREDATORS_FANG = 1334
EMERALD = 1337
BLUE_ONYX = 1338
ONYX = 1339
GLASS_SHARD = 1336
REC_LEATHER_BOOT = 2176
REC_SPIRITSHOT = 3032

DROP={20530:[[0,8,1]],20534:[[4,10,1],[0,4,2]],20537:[[0,10,2]],20525:[[5,10,2],[0,5,3]]}

class Quest (JQuest) :

 def __init__(self,id,name,descr): JQuest.__init__(self,id,name,descr)

 def onEvent (self,event,st) :
    htmltext = event
    if event == "31852-03.htm" :
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
     if player.getRace().ordinal() != 1 :
       htmltext = "31852-00.htm"
       st.exitQuest(1)
     elif player.getLevel()<3 :
       htmltext = "31852-01.htm"
       st.exitQuest(1)
     else :
          htmltext = "31852-02.htm"
   else :
     if st.getQuestItemsCount(PREDATORS_FANG)<100 :
       htmltext = "31852-04.htm"
     else :
       st.takeItems(PREDATORS_FANG,-1)
       n = st.getRandom(100)
       if n<2 :
          st.giveItems(EMERALD,1)
          st.giveItems(REC_SPIRITSHOT,1)
          st.playSound("ItemSound.quest_jackpot")
       elif n<20 :
          st.giveItems(BLUE_ONYX,1)
          st.giveItems(REC_LEATHER_BOOT,1)
       elif n<45 :
          st.giveItems(ONYX,1)
       else:
          st.giveItems(GLASS_SHARD,1)
       htmltext = "31852-05.htm"
       st.exitQuest(1)
       st.playSound("ItemSound.quest_finish")
   return htmltext

 def onKill(self,npc,player,isPet):
   st = player.getQuestState(qn)
   if not st : return 
   if st.getState() != STARTED : return 
   
   if st.getInt("cond") == 1:
      npcId = npc.getNpcId()
      count = st.getQuestItemsCount(PREDATORS_FANG)
      chance = st.getRandom(10)
      qty = 0
      for i in DROP[npcId] :
         if i[0] <= chance < i[1] :
            qty = i[2]
      if qty :
        if count+qty>100 :
          qty=100-count
        if count+qty==100 :
          st.playSound("ItemSound.quest_middle")
          st.set("cond","2")
        else :
          st.playSound("ItemSound.quest_itemget")
        st.giveItems(PREDATORS_FANG,qty)
   return

QUEST       = Quest(266,qn,"Plea Of Pixies")
CREATED     = State('Start', QUEST)
STARTING    = State('Starting', QUEST)
STARTED     = State('Started', QUEST)
COMPLETED   = State('Completed', QUEST)

QUEST.setInitialState(CREATED)
QUEST.addStartNpc(31852)

QUEST.addTalkId(31852)

QUEST.addKillId(20525)
QUEST.addKillId(20530)
QUEST.addKillId(20534)
QUEST.addKillId(20537)