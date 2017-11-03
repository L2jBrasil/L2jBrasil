# Made by Mr. Have fun! - Version 0.3 by DrLecter
import sys
from com.it.br.gameserver.model.quest import State
from com.it.br.gameserver.model.quest import QuestState
from com.it.br.gameserver.model.quest.jython import QuestJython as JQuest

qn = "325_GrimCollector"

ZOMBIE_HEAD1_ID = 1350
ZOMBIE_HEART1_ID = 1351
ZOMBIE_LIVER1_ID = 1352
SKULL1_ID = 1353
RIB_BONE1_ID = 1354
SPINE1_ID = 1355
ARM_BONE1_ID = 1356
THIGH_BONE1_ID = 1357
COMPLETE_SKELETON_ID = 1358
ANATOMY_DIAGRAM_ID = 1349
ADENA_ID = 57

def pieces(st):
    return st.getQuestItemsCount(ZOMBIE_HEAD1_ID)+\
           st.getQuestItemsCount(SPINE1_ID)+\
           st.getQuestItemsCount(ARM_BONE1_ID)+\
           st.getQuestItemsCount(ZOMBIE_HEART1_ID)+\
           st.getQuestItemsCount(ZOMBIE_LIVER1_ID)+\
           st.getQuestItemsCount(SKULL1_ID)+\
           st.getQuestItemsCount(RIB_BONE1_ID)+\
           st.getQuestItemsCount(THIGH_BONE1_ID)+\
           st.getQuestItemsCount(COMPLETE_SKELETON_ID)

def payback(st):
    count = pieces(st)
    amount = 0
    if count :
       amount = 30*st.getQuestItemsCount(ZOMBIE_HEAD1_ID)+20*st.getQuestItemsCount(ZOMBIE_HEART1_ID)+20*st.getQuestItemsCount(ZOMBIE_LIVER1_ID)+100*st.getQuestItemsCount(SKULL1_ID)+40*st.getQuestItemsCount(RIB_BONE1_ID)+14*st.getQuestItemsCount(SPINE1_ID)+14*st.getQuestItemsCount(ARM_BONE1_ID)+14*st.getQuestItemsCount(THIGH_BONE1_ID)+341*st.getQuestItemsCount(COMPLETE_SKELETON_ID)
       if count > 10:
          amount += 1629
       if st.getQuestItemsCount(COMPLETE_SKELETON_ID):
          amount +=543
       st.giveItems(ADENA_ID,amount)
       st.takeItems(ZOMBIE_HEAD1_ID,-1)
       st.takeItems(ZOMBIE_HEART1_ID,-1)
       st.takeItems(ZOMBIE_LIVER1_ID,-1)
       st.takeItems(SKULL1_ID,-1)
       st.takeItems(RIB_BONE1_ID,-1)
       st.takeItems(SPINE1_ID,-1)
       st.takeItems(ARM_BONE1_ID,-1)
       st.takeItems(THIGH_BONE1_ID,-1)
       st.takeItems(COMPLETE_SKELETON_ID,-1)
    return amount

class Quest (JQuest) :

 def __init__(self,id,name,descr): JQuest.__init__(self,id,name,descr)

 def onEvent (self,event,st) :
    htmltext = event
    if event == "30336-03.htm" :
      st.set("cond","1")
      st.setState(STARTED)
      st.playSound("ItemSound.quest_accept")
    elif event == "30434-03.htm" :
      st.giveItems(ANATOMY_DIAGRAM_ID,1)
    elif event == "30434-06.htm" :
      payback(st)
      st.takeItems(ANATOMY_DIAGRAM_ID,-1)
      st.playSound("ItemSound.quest_finish")
      st.exitQuest(1)
    elif event == "30434-07.htm" :
      if not payback(st) :
         htmltext = "You don't have required items"
    elif event == "30434-09.htm" :
      st.giveItems(ADENA_ID,543+(341*st.getQuestItemsCount(COMPLETE_SKELETON_ID)))
      st.takeItems(COMPLETE_SKELETON_ID,-1)
    elif event == "30342-03.htm" :
      if st.getQuestItemsCount(SPINE1_ID) and st.getQuestItemsCount(ARM_BONE1_ID) and st.getQuestItemsCount(SKULL1_ID) and st.getQuestItemsCount(RIB_BONE1_ID) and st.getQuestItemsCount(THIGH_BONE1_ID) :
         st.takeItems(SPINE1_ID,1)
         st.takeItems(SKULL1_ID,1)
         st.takeItems(ARM_BONE1_ID,1)
         st.takeItems(RIB_BONE1_ID,1)
         st.takeItems(THIGH_BONE1_ID,1) 
         if st.getRandom(5)<4 :
            st.giveItems(COMPLETE_SKELETON_ID,1)
         else:
            htmltext = "30342-04.htm"
      else:
         htmltext = "30342-02.htm"
    return htmltext

 def onTalk (self,npc,player):
   htmltext = "<html><body>You are either not carrying out your quest or don't meet the criteria.</body></html>"
   st = player.getQuestState(qn)
   if not st : return htmltext

   npcId = npc.getNpcId()
   id = st.getState()
   if npcId != 30336 and id != STARTED : return htmltext

   cond = st.getInt("cond")
   if npcId == 30336 and cond==0 :
      if player.getLevel() >= 15 :
         htmltext = "30336-02.htm"
         return htmltext
      else:
         htmltext = "30336-01.htm"
         st.exitQuest(1)
   elif npcId == 30336 and cond and not st.getQuestItemsCount(ANATOMY_DIAGRAM_ID) :
      htmltext = "30336-04.htm"
   elif npcId == 30336 and cond and st.getQuestItemsCount(ANATOMY_DIAGRAM_ID) :
      htmltext = "30336-05.htm"
   elif npcId == 30434 and cond and not st.getQuestItemsCount(ANATOMY_DIAGRAM_ID) :
      htmltext = "30434-01.htm"
   elif npcId == 30434 and cond and st.getQuestItemsCount(ANATOMY_DIAGRAM_ID) and not pieces(st) :
      htmltext = "30434-04.htm"
   elif npcId == 30434 and cond and st.getQuestItemsCount(ANATOMY_DIAGRAM_ID) and pieces(st) and not st.getQuestItemsCount(COMPLETE_SKELETON_ID):
      htmltext = "30434-05.htm"
   elif npcId == 30434 and cond and st.getQuestItemsCount(ANATOMY_DIAGRAM_ID) and pieces(st) and st.getQuestItemsCount(COMPLETE_SKELETON_ID) :
      htmltext = "30434-08.htm"
   elif npcId == 30342 and cond and st.getQuestItemsCount(ANATOMY_DIAGRAM_ID) :
      htmltext = "30342-01.htm"
   return htmltext

 def onKill(self,npc,player,isPet):
   st = player.getQuestState(qn)
   if not st : return 
   if st.getState() != STARTED : return 
   
   npcId = npc.getNpcId()
   if st.getQuestItemsCount(ANATOMY_DIAGRAM_ID) :
    n = st.getRandom(100)
    if npcId == 20026 :
     if n<90 :
      st.playSound("ItemSound.quest_itemget")   
      if n<40 :
         st.giveItems(ZOMBIE_HEAD1_ID,1)
      elif n<60 :
         st.giveItems(ZOMBIE_HEART1_ID,1)
      else :
         st.giveItems(ZOMBIE_LIVER1_ID,1)
    elif npcId == 20029 :
      st.playSound("ItemSound.quest_itemget")  
      if n<44 :
         st.giveItems(ZOMBIE_HEAD1_ID,1)
      elif n<66 :
         st.giveItems(ZOMBIE_HEART1_ID,1)
      else :
        st.giveItems(ZOMBIE_LIVER1_ID,1)
    elif npcId == 20035 :
     if n<79 :
      st.playSound("ItemSound.quest_itemget")
      if n<5 :
        st.giveItems(SKULL1_ID,1)
      elif n<15 :
         st.giveItems(RIB_BONE1_ID,1)
      elif n<29 :
         st.giveItems(SPINE1_ID,1)
      else :
         st.giveItems(THIGH_BONE1_ID,1)
    elif npcId == 20042 :
     if n<86 :
      st.playSound("ItemSound.quest_itemget")   
      if n<6 :
         st.giveItems(SKULL1_ID,1)
      elif n<19 :
         st.giveItems(RIB_BONE1_ID,1)
      elif n<69 :
         st.giveItems(ARM_BONE1_ID,1)
      else :
         st.giveItems(THIGH_BONE1_ID,1)
    elif npcId == 20045 :
     if n<97 :
      st.playSound("ItemSound.quest_itemget")
      if n<9 :
         st.giveItems(SKULL1_ID,1)
      elif n<59 :
         st.giveItems(SPINE1_ID,1)
      elif n<77 :
         st.giveItems(ARM_BONE1_ID,1)
      else :
         st.giveItems(THIGH_BONE1_ID,1)
    elif npcId == 20051 :
     if n<99 :
      st.playSound("ItemSound.quest_itemget")
      if n<9 :
         st.giveItems(SKULL1_ID,1)
      elif n<59 :
         st.giveItems(RIB_BONE1_ID,1)
      elif n<79 :
         st.giveItems(SPINE1_ID,1)
      else :
         st.giveItems(ARM_BONE1_ID,1)
    elif npcId == 20514 :
     if n<51 :
      st.playSound("ItemSound.quest_itemget")
      if n<2 :
         st.giveItems(SKULL1_ID,1)
      elif n<8 :
         st.giveItems(RIB_BONE1_ID,1)
      elif n<17 :
         st.giveItems(SPINE1_ID,1)
      elif n<18 :
         st.giveItems(ARM_BONE1_ID,1)
      else :
         st.giveItems(THIGH_BONE1_ID,1)
    elif npcId == 20515 :
     if n<60 :
      st.playSound("ItemSound.quest_itemget")   
      if n<3 :
         st.giveItems(SKULL1_ID,1)
      elif n<11 :
         st.giveItems(RIB_BONE1_ID,1)
      elif n<22 :
         st.giveItems(SPINE1_ID,1)
      elif n<24 :
         st.giveItems(ARM_BONE1_ID,1)
      else :
         st.giveItems(THIGH_BONE1_ID,1)
    elif npcId == 20457 :
      st.playSound("ItemSound.quest_itemget")
      if n<42 :
         st.giveItems(ZOMBIE_HEAD1_ID,1)
      elif n<67 :
         st.giveItems(ZOMBIE_HEART1_ID,1)
      else :
         st.giveItems(ZOMBIE_LIVER1_ID,1)
    elif npcId == 20458 :
      st.playSound("ItemSound.quest_itemget")  
      if n<42 :
         st.giveItems(ZOMBIE_HEAD1_ID,1)
      elif n<67 :
         st.giveItems(ZOMBIE_HEART1_ID,1)
      else :
         st.giveItems(ZOMBIE_LIVER1_ID,1)
   return

QUEST       = Quest(325,qn,"Grim Collector")
CREATED     = State('Start', QUEST)
STARTED     = State('Started', QUEST)
COMPLETED   = State('Completed', QUEST)

QUEST.setInitialState(CREATED)
QUEST.addStartNpc(30336)
QUEST.addTalkId(30336)

QUEST.addTalkId(30342)
QUEST.addTalkId(30434)

for i in [20026,20029,20035,20042,20045,20457,20458,20051,20514,20515] :
    QUEST.addKillId(i)