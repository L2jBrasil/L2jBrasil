# Made by Mr. - Version 0.3 by DrLecter
import sys
from com.it.br.gameserver.model.quest import State
from com.it.br.gameserver.model.quest import QuestState
from com.it.br.gameserver.model.quest.jython import QuestJython as JQuest

qn = "156_MillenniumLove"

RYLITHS_LETTER_ID = 1022
THEONS_DIARY_ID = 1023
ADENA_ID = 57

class Quest (JQuest) :

 def __init__(self,id,name,descr): JQuest.__init__(self,id,name,descr)

 def onEvent (self,event,st) :
    htmltext = event
    if event == "1" :
       if st.getPlayer().getLevel() >= 15 :
          htmltext = "30368-06.htm"
          st.giveItems(RYLITHS_LETTER_ID,1)
          st.set("cond","1")
          st.setState(STARTED)
          st.playSound("ItemSound.quest_accept")
       else:
          htmltext = "30368-05.htm"
          st.exitQuest(1)
    elif event == "156_1" :
       st.takeItems(RYLITHS_LETTER_ID,-1)
       if not st.getQuestItemsCount(THEONS_DIARY_ID) :
          st.giveItems(THEONS_DIARY_ID,1)
       htmltext = "30369-03.htm"
    elif event == "156_2" :
       st.takeItems(RYLITHS_LETTER_ID,-1)
       st.unset("cond")
       st.setState(COMPLETED)
       st.playSound("ItemSound.quest_finish")
       st.giveItems(5250,1)
       st.addExpAndSp(3000,0)
       htmltext = "30369-04.htm"
    return htmltext

 def onTalk (self,npc,player):
   htmltext = "<html><body>You are either not carrying out your quest or don't meet the criteria.</body></html>"
   st = player.getQuestState(qn)
   if not st : return htmltext

   npcId = npc.getNpcId()
   id = st.getState()
   if id == COMPLETED :
      htmltext = "<html><body>This quest has already been completed.</body></html>"
   elif npcId == 30368 :
      if not st.getInt("cond") :
         htmltext = "30368-04.htm"
      elif st.getInt("cond") :
        if st.getQuestItemsCount(RYLITHS_LETTER_ID) :
           htmltext = "30368-07.htm"
        elif st.getQuestItemsCount(THEONS_DIARY_ID) :
           st.takeItems(THEONS_DIARY_ID,-1)
           st.unset("cond")
           st.setState(COMPLETED)
           st.playSound("ItemSound.quest_finish")
           st.addExpAndSp(3000,0)
           st.giveItems(5250,1)
           htmltext = "30368-08.htm"
   elif npcId == 30369 and id == STARTED:
      if st.getQuestItemsCount(RYLITHS_LETTER_ID) :
         htmltext = "30369-02.htm"
      elif st.getQuestItemsCount(THEONS_DIARY_ID) :
         htmltext = "30369-05.htm"
   return htmltext

QUEST       = Quest(156,qn,"Millennium Love")
CREATED     = State('Start', QUEST)
STARTING    = State('Starting', QUEST)
STARTED     = State('Started', QUEST)
COMPLETED   = State('Completed', QUEST)

QUEST.setInitialState(CREATED)
QUEST.addStartNpc(30368)

QUEST.addTalkId(30368)

QUEST.addTalkId(30369)