# Made by Mr. Have fun! - Version 0.3 by DrLecter
import sys
from com.it.br.gameserver.model.quest import State
from com.it.br.gameserver.model.quest import QuestState
from com.it.br.gameserver.model.quest.jython import QuestJython as JQuest

qn = "2_WhatWomenWant"

#NPCs 
ARUJIEN = 30223 
MIRABEL = 30146 
HERBIEL = 30150 
GREENIS = 30157 

#ITEMS 
ARUJIENS_LETTER1 = 1092 
ARUJIENS_LETTER2 = 1093 
ARUJIENS_LETTER3 = 1094 
POETRY_BOOK      = 689 
GREENIS_LETTER   = 693 
 
#REWARDS 
ADENA            = 57 
BEGINNERS_POTION = 1073 
 
class Quest (JQuest) :

 def __init__(self,id,name,descr): JQuest.__init__(self,id,name,descr)

 def onEvent (self,event,st) :
   htmltext = event 
   if event == "30223-04.htm" : 
     if st.getQuestItemsCount(ARUJIENS_LETTER1) == 0 and st.getQuestItemsCount(ARUJIENS_LETTER2) == 0 and st.getQuestItemsCount(ARUJIENS_LETTER3) == 0 : 
       st.giveItems(ARUJIENS_LETTER1,1) 
     st.set("cond","1") 
     st.set("id","1") 
     st.setState(STARTED) 
     st.playSound("ItemSound.quest_accept") 
   elif event == "30223-08.htm" : 
     st.takeItems(ARUJIENS_LETTER3,-1) 
     st.giveItems(POETRY_BOOK,1) 
     st.set("cond","4") 
     st.set("id","4") 
     st.playSound("ItemSound.quest_middle") 
   elif event == "30223-10.htm" : 
     st.takeItems(ARUJIENS_LETTER3,-1) 
     st.giveItems(113,1) 
     st.set("cond","0") 
     st.setState(COMPLETED) 
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
     st.set("id","0") 
 
   cond = st.getInt("cond") 
 
   if npcId == ARUJIEN and id == CREATED : 
     if player.getRace().ordinal() != 1 and player.getRace().ordinal() != 0 : 
       htmltext = "30223-00.htm" 
     elif player.getLevel() >= 2 : 
       htmltext = "30223-02.htm" 
     else: 
       htmltext = "30223-01.htm" 
       st.exitQuest(1) 
   elif npcId == ARUJIEN and id == COMPLETED : 
     htmltext = "<html><body>This quest has already been completed.</body></html>" 
   elif npcId == ARUJIEN and cond >= 1 : 
     if st.getQuestItemsCount(ARUJIENS_LETTER1) : 
       htmltext = "30223-05.htm" 
     elif st.getQuestItemsCount(ARUJIENS_LETTER3) : 
       htmltext = "30223-07.htm" 
     elif st.getQuestItemsCount(ARUJIENS_LETTER2) : 
       htmltext = "30223-06.htm" 
     elif st.getQuestItemsCount(POETRY_BOOK) : 
       htmltext = "30223-11.htm" 
     elif st.getQuestItemsCount(GREENIS_LETTER) : 
       htmltext = "30223-10.htm" 
       st.takeItems(GREENIS_LETTER,-1) 
       st.giveItems(113,1)
       st.set("cond","0") 
       st.setState(COMPLETED) 
       st.playSound("ItemSound.quest_finish")
   elif id == STARTED :    
       if npcId == MIRABEL and cond == 1 : 
         if st.getQuestItemsCount(ARUJIENS_LETTER1) : 
           htmltext = "30146-01.htm" 
           st.takeItems(ARUJIENS_LETTER1,-1) 
           st.giveItems(ARUJIENS_LETTER2,1) 
           st.set("cond","2") 
           st.set("id","2") 
           st.playSound("ItemSound.quest_middle") 
         elif st.getQuestItemsCount(ARUJIENS_LETTER2) or st.getQuestItemsCount(ARUJIENS_LETTER3) or st.getQuestItemsCount(POETRY_BOOK) or st.getQuestItemsCount(GREENIS_LETTER) : 
           htmltext = "30146-02.htm" 
       elif npcId == HERBIEL and cond == 2 and st.getQuestItemsCount(ARUJIENS_LETTER1) == 0 : 
         if st.getQuestItemsCount(ARUJIENS_LETTER2) : 
           htmltext = "30150-01.htm" 
           st.takeItems(ARUJIENS_LETTER2,-1) 
           st.giveItems(ARUJIENS_LETTER3,1) 
           st.set("cond","3") 
           st.set("id","3") 
           st.playSound("ItemSound.quest_middle") 
         elif st.getQuestItemsCount(ARUJIENS_LETTER3) or st.getQuestItemsCount(POETRY_BOOK) or st.getQuestItemsCount(GREENIS_LETTER) : 
           htmltext = "30150-02.htm" 
       elif npcId == GREENIS and cond == 4 : 
         if st.getQuestItemsCount(POETRY_BOOK) : 
           htmltext = "30157-02.htm" 
           st.takeItems(POETRY_BOOK,-1) 
           st.giveItems(GREENIS_LETTER,1) 
           st.set("cond","5") 
           st.set("id","5") 
           st.playSound("ItemSound.quest_middle") 
       elif npcId == GREENIS and st.getQuestItemsCount(GREENIS_LETTER) : 
         htmltext = "30157-03.htm" 
       elif npcId == GREENIS and (st.getQuestItemsCount(ARUJIENS_LETTER1) or st.getQuestItemsCount(ARUJIENS_LETTER2) or st.getQuestItemsCount(ARUJIENS_LETTER3)) : 
         htmltext = "30157-01.htm" 
   return htmltext

QUEST     = Quest(2,qn,"What Women Want") 
CREATED   = State('Start',     QUEST) 
STARTED   = State('Started',   QUEST) 
COMPLETED = State('Completed', QUEST) 

QUEST.setInitialState(CREATED)
QUEST.addStartNpc(ARUJIEN) 

QUEST.addTalkId(ARUJIEN) 

QUEST.addTalkId(MIRABEL) 
QUEST.addTalkId(HERBIEL) 
QUEST.addTalkId(GREENIS) 
QUEST.addTalkId(ARUJIEN) 