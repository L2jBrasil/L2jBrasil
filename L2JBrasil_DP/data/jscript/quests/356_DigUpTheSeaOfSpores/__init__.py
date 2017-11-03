# Made by disKret
import sys
from com.it.br.gameserver.model.quest import State
from com.it.br.gameserver.model.quest import QuestState
from com.it.br.gameserver.model.quest.jython import QuestJython as JQuest

qn = "356_DigUpTheSeaOfSpores"

#NPC
GAUEN = 30717

#MOBS
SPORE_ZOMBIE = 20562
ROTTING_TREE = 20558

#QUEST ITEMS
CARNIVORE_SPORE = 5865
HERBIBOROUS_SPORE = 5866

class Quest (JQuest) :

 def __init__(self,id,name,descr): JQuest.__init__(self,id,name,descr)

 def onEvent (self,event,st) :
   htmltext = event
   carn=st.getQuestItemsCount(CARNIVORE_SPORE)
   herb=st.getQuestItemsCount(HERBIBOROUS_SPORE)
   if event == "30717-5.htm" :
     if st.getPlayer().getLevel() >= 43 :
       st.set("cond","1")
       st.setState(STARTED)
       st.playSound("ItemSound.quest_accept")
     else :
       htmltext = "30717-4.htm"
       st.exitQuest(1)
   elif event in [ "30717-10.htm", "30717-9.htm" ] and (carn>=50 and herb>=50) :
     if event == "30717-9.htm" :
        st.giveItems(57,44000)
     else :
        st.addExpAndSp(36000,2600)
     st.takeItems(CARNIVORE_SPORE,-1)
     st.takeItems(HERBIBOROUS_SPORE,-1)
     st.playSound("ItemSound.quest_finish")
     st.exitQuest(1)
   return htmltext

 def onTalk (self,npc,player):
   htmltext = "<html><body>You are either not carrying out your quest or don't meet the criteria.</body></html>"
   st = player.getQuestState(qn)
   if not st : return htmltext

   npcId = npc.getNpcId()
   id = st.getState()
   cond = st.getInt("cond")
   carn=st.getQuestItemsCount(CARNIVORE_SPORE)
   herb=st.getQuestItemsCount(HERBIBOROUS_SPORE)
   if cond == 0 :
     htmltext = "30717-0.htm"
   elif cond != 3 :
     htmltext = "30717-6.htm"
   elif cond == 3 or (carn>=50 and herb>=50) :
     htmltext = "30717-7.htm"
   return htmltext

 def onKill(self,npc,player,isPet):
   st = player.getQuestState(qn)
   if not st : return 
   if st.getState() != STARTED : return 
   
   npcId = npc.getNpcId()
   carn=st.getQuestItemsCount(CARNIVORE_SPORE)
   herb=st.getQuestItemsCount(HERBIBOROUS_SPORE)
   if npcId == SPORE_ZOMBIE and carn < 50 :
     st.giveItems(CARNIVORE_SPORE,1)
     if carn == 49 :
       if herb >= 50 :
         st.playSound("ItemSound.quest_middle")
         st.set("cond","3")
       else :
         st.playSound("ItemSound.quest_middle")
         st.set("cond","2")
     else :
       st.playSound("ItemSound.quest_itemget")
   elif npcId == ROTTING_TREE and herb < 50 :
     st.giveItems(HERBIBOROUS_SPORE,1)
     if herb == 49 :
       if carn >= 50 :
         st.playSound("ItemSound.quest_middle")
         st.set("cond","3")
       else :
         st.playSound("ItemSound.quest_middle")
         st.set("cond","2")
     else:
       st.playSound("ItemSound.quest_itemget")
   return

QUEST       = Quest(356,qn,"Dig Up The Sea Of Spores")
CREATED     = State('Start', QUEST)
STARTED     = State('Started', QUEST)

QUEST.setInitialState(CREATED)
QUEST.addStartNpc(GAUEN)

QUEST.addTalkId(GAUEN)

QUEST.addKillId(SPORE_ZOMBIE)
QUEST.addKillId(ROTTING_TREE)