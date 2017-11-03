# Made by Mr - Version 0.3 by DrLecter
import sys
from com.it.br.gameserver.model.quest import State
from com.it.br.gameserver.model.quest import QuestState
from com.it.br.gameserver.model.quest.jython import QuestJython as JQuest

qn = "3_WillSealBeBroken"

#NPC
TALLOTH = 30141

#ITEMS
ONYX_BEAST_EYE,TAINT_STONE,SUCCUBUS_BLOOD = range(1081,1084)

#MOBS
OMEN_BEAST            = 20031
TAINTED_ZOMBIE        = 20041
STINK_ZOMBIE          = 20046
LESSER_SUCCUBUS       = 20048
LESSER_SUCCUBUS_TUREN = 20052
LESSER_SUCCUBUS_TILFO = 20057

#REWARDS
ADENA = 57

class Quest (JQuest) :

 def __init__(self,id,name,descr): JQuest.__init__(self,id,name,descr)

 def onEvent (self,event,st) :
   htmltext = event
   if event == "30141-03.htm" :
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
   cond = st.getInt("cond")
   if id == COMPLETED :
      htmltext = "<html><body>This quest has already been completed.</body></html>"
   elif cond == 0 :
      if player.getRace().ordinal() != 2 :
         htmltext = "30141-00.htm"
         st.exitQuest(1)
      elif player.getLevel() >= 16 :
         htmltext = "30141-02.htm"
      else :
         htmltext = "30141-01.htm"
         st.exitQuest(1)
   elif cond == 1 :
     htmltext = "30141-04.htm"
   elif cond == 2 :
     htmltext = "30141-06.htm"
     st.takeItems(ONYX_BEAST_EYE,-1)
     st.takeItems(TAINT_STONE,-1)
     st.takeItems(SUCCUBUS_BLOOD,-1)
     st.giveItems(956,1)
     st.unset("cond")
     st.setState(COMPLETED)
     st.playSound("ItemSound.quest_finish")
   return htmltext

 def onKill(self,npc,player,isPet):
   st = player.getQuestState(qn)
   if not st : return 
   if st.getState() != STARTED : return 

   npcId = npc.getNpcId()
   if st.getInt("cond") == 1 :
     if npcId == OMEN_BEAST and not st.getQuestItemsCount(ONYX_BEAST_EYE) :
       st.giveItems(ONYX_BEAST_EYE,1)
     elif npcId in [TAINTED_ZOMBIE,STINK_ZOMBIE] and not st.getQuestItemsCount(TAINT_STONE) :
       st.giveItems(TAINT_STONE,1)
     elif npcId in [LESSER_SUCCUBUS,LESSER_SUCCUBUS_TUREN,LESSER_SUCCUBUS_TILFO] and not st.getQuestItemsCount(SUCCUBUS_BLOOD) :
       st.giveItems(SUCCUBUS_BLOOD,1)
     if st.getQuestItemsCount(ONYX_BEAST_EYE) and st.getQuestItemsCount(TAINT_STONE) and st.getQuestItemsCount(SUCCUBUS_BLOOD) :
       st.set("cond","2")
       st.playSound("ItemSound.quest_middle")
     else :
       st.playSound("ItemSound.quest_itemget")
   return

QUEST     = Quest(3,qn,"Will the Seal be Broken?")
CREATED   = State('Start',     QUEST)
STARTING  = State('Starting',  QUEST)
STARTED   = State('Started',   QUEST)
COMPLETED = State('Completed', QUEST)

QUEST.setInitialState(CREATED)
QUEST.addStartNpc(TALLOTH)

QUEST.addTalkId(TALLOTH)

QUEST.addKillId(OMEN_BEAST)
QUEST.addKillId(TAINTED_ZOMBIE)
QUEST.addKillId(STINK_ZOMBIE)
QUEST.addKillId(LESSER_SUCCUBUS)
QUEST.addKillId(LESSER_SUCCUBUS_TUREN)
QUEST.addKillId(LESSER_SUCCUBUS_TILFO)