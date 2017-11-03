# Made by disKret
import sys
from com.it.br.gameserver.model.quest import State
from com.it.br.gameserver.model.quest import QuestState
from com.it.br.gameserver.model.quest.jython import QuestJython as JQuest

qn = "355_FamilyHonor"

#NPC
GALIBREDO = 30181
PATRIN = 30929

#CHANCES
CHANCE_FOR_GALFREDOS_BUST = 80
CHANCE_FOR_GODDESS_BUST = 30

#ITEMS
GALFREDOS_BUST = 4252
BUST_OF_ANCIENT_GODDESS = 4349
WORK_OF_BERONA = 4350
STATUE_PROTOTYPE = 4351
STATUE_ORIGINAL = 4352
STATUE_REPLICA = 4353
STATUE_FORGERY = 4354


class Quest (JQuest) :

 def __init__(self,id,name,descr): JQuest.__init__(self,id,name,descr)

 def onEvent (self,event,st) :
   htmltext = event
   if event == "30181-1.htm" :
     return htmltext
   if event == "30181-2.htm" :
     st.set("cond","1")
     st.setState(STARTED)
     st.playSound("ItemSound.quest_accept")
   if event == "30181-4.htm" :
     count = st.getQuestItemsCount(BUST_OF_ANCIENT_GODDESS)
     st.takeItems(BUST_OF_ANCIENT_GODDESS,count)
     st.giveItems(WORK_OF_BERONA,count)
   if event == "30929-0.htm" :
     return htmltext
   if event == "30929-1.htm" :
     return htmltext
   if event == "appraise" :
     appraising = st.getRandom(100)
     if appraising in range(0,20) : 
       htmltext = "30929-2.htm"       
       st.takeItems(WORK_OF_BERONA,1)
     elif appraising in range(20,40) : 
       htmltext = "30929-3.htm"       
       st.takeItems(WORK_OF_BERONA,1)
       st.giveItems(STATUE_REPLICA,1)
     elif appraising in range(40,60) : 
       htmltext = "30929-4.htm"       
       st.takeItems(WORK_OF_BERONA,1)
       st.giveItems(STATUE_ORIGINAL,1)
     elif appraising in range(60,80) : 
       htmltext = "30929-5.htm"       # custom txt
       st.takeItems(WORK_OF_BERONA,1)
       st.giveItems(STATUE_FORGERY,1)
     elif appraising in range(80,100) : 
       htmltext = "30929-6.htm"       # custom txt
       st.takeItems(WORK_OF_BERONA,1)
       st.giveItems(STATUE_PROTOTYPE,1)
   if event == "30181-5.htm" :
     st.playSound("ItemSound.quest_finish")
     st.exitQuest(1)
   return htmltext

 def onTalk (self,npc,player):
   htmltext = "<html><body>You are either not carrying out your quest or don't meet the criteria.</body></html>"
   st = player.getQuestState(qn)
   if not st : return htmltext

   npcId = npc.getNpcId()
   id = st.getState()
   if npcId != GALIBREDO and id != STARTED : return htmltext

   cond = st.getInt("cond")
   count = st.getQuestItemsCount(GALFREDOS_BUST)
   if npcId == GALIBREDO :
     if cond == 0 :
       if player.getLevel() >= 36 : 
         htmltext = "30181-0.htm"
       else:
         htmltext = "30181-0a.htm"
         st.exitQuest(1)
     elif cond == 1 :
       if count :
         reward = count * 232
         if count >= 100 :
           reward = reward + 5000   # custom - need more info
         st.takeItems(GALFREDOS_BUST,count)
         st.giveItems(57,reward)
         htmltext = "30181-3.htm"
       else :
         htmltext = "30181-2a.htm"
   elif npcId == PATRIN :
     if st.getQuestItemsCount(WORK_OF_BERONA) :   
       htmltext = "30929-0.htm"
     else :
       htmltext = "<html><body>You have nothing to appraise.</body></html>"
   return htmltext

 def onKill(self,npc,player,isPet):
   st = player.getQuestState(qn)
   if not st : return 
   if st.getState() != STARTED : return 
   
   if st.getInt("cond") == 1 :
     chance_1 = st.getRandom(100)
     chance_2 = st.getRandom(100)
     if chance_1 < CHANCE_FOR_GALFREDOS_BUST :
       st.giveItems(GALFREDOS_BUST,1)
       st.playSound("ItemSound.quest_itemget")
     if chance_2 < CHANCE_FOR_GODDESS_BUST :
       st.giveItems(BUST_OF_ANCIENT_GODDESS,1)      
   return

QUEST       = Quest(355,qn,"Family Honor")
CREATED     = State('Start', QUEST)
STARTED     = State('Started', QUEST)

QUEST.setInitialState(CREATED)
QUEST.addStartNpc(GALIBREDO)
QUEST.addTalkId(GALIBREDO)
QUEST.addTalkId(PATRIN)

#MOBS TIMAK ORC TROOPS
for MOBS in range(20767,20771) :
  QUEST.addKillId(MOBS)
