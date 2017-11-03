# Made by Mr. - Version 0.3 by DrLecter
import sys
from com.it.br.gameserver.model.quest import State
from com.it.br.gameserver.model.quest import QuestState
from com.it.br.gameserver.model.quest.jython import QuestJython as JQuest

qn = "162_CurseOfFortress"

BONE_FRAGMENT3 = 1158
ELF_SKULL = 1159
BONE_SHIELD = 625

class Quest (JQuest) :

 def __init__(self,id,name,descr): JQuest.__init__(self,id,name,descr)

 def onEvent (self,event,st) :
    htmltext = event
    if event == "30147-04.htm" :
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
   if id == COMPLETED :
      htmltext = "<html><body>This quest has already been completed.</body></html>"
   elif st.getInt("cond") == 0 :
      if player.getRace().ordinal() == 2 :
         htmltext = "30147-00.htm"
         st.exitQuest(1)
      elif player.getLevel() >= 12 :
         htmltext = "30147-02.htm"
      else:
         htmltext = "30147-01.htm"
         st.exitQuest(1)
   else :
      if st.getQuestItemsCount(ELF_SKULL) < 3 and st.getQuestItemsCount(BONE_FRAGMENT3) < 10 :
         htmltext = "30147-05.htm"
      else  :
         htmltext = "30147-06.htm"
         st.giveItems(57,24000)
         st.giveItems(BONE_SHIELD,1)
         st.takeItems(ELF_SKULL,-1)
         st.takeItems(BONE_FRAGMENT3,-1)
         st.unset("cond")
         st.setState(COMPLETED)
         st.playSound("ItemSound.quest_finish")
   return htmltext

 def onKill(self,npc,player,isPet):
   st = player.getQuestState(qn)
   if not st : return
   if st.getState() != STARTED : return
   
   if st.getRandom(4) == 1 :
     npcId = npc.getNpcId()
     bones = st.getQuestItemsCount(BONE_FRAGMENT3)
     skulls = st.getQuestItemsCount(ELF_SKULL)
     if npcId in [20464,20463,20504] :
       if bones < 10 :
         st.giveItems(BONE_FRAGMENT3,1)
         if bones == 9 and skulls == 3 :
           st.playSound("ItemSound.quest_middle")
           st.set ("cond","2")
         else:
           st.playSound("ItemSound.quest_itemget")
     elif skulls < 3 :
       st.giveItems(ELF_SKULL,1)
       if bones == 10 and skulls == 2 :
         st.playSound("ItemSound.quest_middle")
         st.set ("cond","2")
       else:
         st.playSound("ItemSound.quest_itemget")
   return

QUEST       = Quest(162,qn,"Curse Of Fortress")
CREATED     = State('Start', QUEST)
STARTED     = State('Started', QUEST)
COMPLETED   = State('Completed', QUEST)

QUEST.setInitialState(CREATED)
QUEST.addStartNpc(30147)

QUEST.addTalkId(30147)

QUEST.addKillId(20033)
QUEST.addKillId(20345)
QUEST.addKillId(20371)
QUEST.addKillId(20463)
QUEST.addKillId(20464)
QUEST.addKillId(20504)