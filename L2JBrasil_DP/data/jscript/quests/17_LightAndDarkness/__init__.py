# Made with contributions from :
# disKret, Skeleton & DrLecter.
# this script is part of the Official L2J Datapack Project.
# Visit http://forum.l2jdp.com for more details.

import sys
from com.it.br.gameserver.model.quest import State
from com.it.br.gameserver.model.quest import QuestState
from com.it.br.gameserver.model.quest.jython import QuestJython as JQuest

qn = "17_LightAndDarkness"

#NPC
HIERARCH = 31517
SAINT_ALTAR_1 = 31508
SAINT_ALTAR_2 = 31509
SAINT_ALTAR_3 = 31510
SAINT_ALTAR_4 = 31511

#ITEMS
BLOOD_OF_SAINT = 7168

class Quest (JQuest) :

 def __init__(self,id,name,descr): JQuest.__init__(self,id,name,descr)

 def onEvent (self,event,st) :
   htmltext = event
   cond = st.getInt("cond")
   blood = st.getQuestItemsCount(BLOOD_OF_SAINT)
   if event == "31517-02.htm" :
     if st.getPlayer().getLevel() >= 61 :
       st.giveItems(BLOOD_OF_SAINT,4)
       st.set("cond","1")
       st.setState(STARTED)
       st.playSound("ItemSound.quest_accept")
     else :
       htmltext = "31517-02a.htm"
       st.exitQuest(1)
   if event == "31508-02.htm" and cond == 1 and blood :
     htmltext = "31508-01.htm"
     st.takeItems(BLOOD_OF_SAINT,1)
     st.set("cond","2")
     st.playSound("ItemSound.quest_middle")
   elif event == "31509-02.htm" and cond == 2 and blood :
     htmltext = "31509-01.htm"
     st.takeItems(BLOOD_OF_SAINT,1)
     st.set("cond","3")
     st.playSound("ItemSound.quest_middle")
   elif event == "31510-02.htm" and cond == 3 and blood :
     htmltext = "31510-01.htm"
     st.takeItems(BLOOD_OF_SAINT,1)
     st.set("cond","4")
     st.playSound("ItemSound.quest_middle")
   elif event == "31511-02.htm" and cond == 4 and blood :
     htmltext = "31511-01.htm"
     st.takeItems(BLOOD_OF_SAINT,1)
     st.set("cond","5")
     st.playSound("ItemSound.quest_middle")
   return htmltext

 def onTalk (self,npc,player):
   htmltext = "<html><body>You are either not carrying out your quest or don't meet the criteria.</body></html>"
   st = player.getQuestState(qn)
   if not st : return htmltext
   npcId = npc.getNpcId()
   cond = st.getInt("cond")
   blood = st.getQuestItemsCount(BLOOD_OF_SAINT)
   id = st.getState()
   if id == COMPLETED :
      htmltext = "<html><body>This quest has already been completed.</body></html>"
   elif npcId == HIERARCH :
     if cond == 0 :
        htmltext = "31517-00.htm"
     elif cond < 5 :
        if blood == 5 :
           htmltext = "31517-04.htm"
        else :
           htmltext = "31517-05.htm"
           st.exitQuest(1)
           st.playSound("ItemSound.quest_giveup")
     else :
        st.addExpAndSp(105527,0)
        st.unset("cond")
        st.setState(COMPLETED)
        st.playSound("ItemSound.quest_finish")
        htmltext = "31517-03.htm"
   elif id == STARTED :    
     if npcId == SAINT_ALTAR_1 :
        if cond == 1 :
          if blood :
             htmltext = "31508-00.htm"
          else :
             htmltext = "31508-02.htm"
        elif cond > 1 :
          htmltext = "31508-03.htm"
     elif npcId == SAINT_ALTAR_2 :
        if cond == 2 :
          if blood :
             htmltext = "31509-00.htm"
          else :
             htmltext = "31509-02.htm"
        elif cond > 2 :
          htmltext = "31509-03.htm"
     elif npcId == SAINT_ALTAR_3 :
        if cond == 3 :
          if blood :
             htmltext = "31510-00.htm"
          else :
             htmltext = "31510-02.htm"
        elif cond > 3 :
          htmltext = "31510-03.htm"
     elif npcId == SAINT_ALTAR_4 :
        if cond == 4 :
          if blood :
             htmltext = "31511-00.htm"
          else :
             htmltext = "31511-02.htm"
        elif cond > 4 :
          htmltext = "31511-03.htm"
   return htmltext

QUEST       = Quest(17,qn,"Light and Darkness")
CREATED     = State('Start', QUEST)
STARTED     = State('Started', QUEST)
COMPLETED   = State('Completed', QUEST)

QUEST.setInitialState(CREATED)
QUEST.addStartNpc(HIERARCH)
QUEST.addTalkId(HIERARCH)

for altars in range(31508,31512):
  QUEST.addTalkId(altars)