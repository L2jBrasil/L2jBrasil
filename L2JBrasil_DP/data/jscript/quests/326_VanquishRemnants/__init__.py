# Made by Mr. - Version 0.3 by DrLecter
import sys
from com.it.br.gameserver.model.quest import State
from com.it.br.gameserver.model.quest import QuestState
from com.it.br.gameserver.model.quest.jython import QuestJython as JQuest

qn = "326_VanquishRemnants"

RED_CROSS_BADGE,BLUE_CROSS_BADGE,BLACK_CROSS_BADGE, = range(1359,1362)
ADENA = 57
BLACK_LION_MARK = 1369

DROPLIST={
20053:[RED_CROSS_BADGE,25],
20437:[RED_CROSS_BADGE,25],
20058:[RED_CROSS_BADGE,25],
20061:[BLUE_CROSS_BADGE,25],
20063:[BLUE_CROSS_BADGE,25],
20436:[BLUE_CROSS_BADGE,25],
20439:[BLUE_CROSS_BADGE,25],
20438:[BLACK_CROSS_BADGE,35],
20066:[BLACK_CROSS_BADGE,25],
}

class Quest (JQuest) :

 def __init__(self,id,name,descr): JQuest.__init__(self,id,name,descr)

 def onEvent (self,event,st) :
    htmltext = event
    if event == "30435-03.htm" :
      st.set("cond","1")
      st.setState(STARTED)
      st.playSound("ItemSound.quest_accept")
    elif event == "30435-07.htm" :
      st.playSound("ItemSound.quest_finish")
      st.exitQuest(1)
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
     if player.getLevel() >= 21 :
       htmltext = "30435-02.htm"
     else:
       htmltext = "30435-01.htm"
       st.exitQuest(1)
   else :
     red=st.getQuestItemsCount(RED_CROSS_BADGE)
     blue=st.getQuestItemsCount(BLUE_CROSS_BADGE)
     black=st.getQuestItemsCount(BLACK_CROSS_BADGE)
     if red+blue+black == 0 :
       htmltext = "30435-04.htm"
     else :
       htmltext = "30435-05.htm"
       st.giveItems(ADENA,60*red+65*blue+70*black)
       st.takeItems(RED_CROSS_BADGE,-1)
       st.takeItems(BLUE_CROSS_BADGE,-1)
       st.takeItems(BLACK_CROSS_BADGE,-1)
       if red+blue+black >= 100 :
         htmltext = "30435-09.htm"
         if st.getQuestItemsCount(BLACK_LION_MARK) ==  0 :
           st.giveItems(BLACK_LION_MARK,1)
           htmltext = "30435-06.htm"
   return htmltext

 def onKill(self,npc,player,isPet):
   st = player.getQuestState(qn)
   if not st : return 
   if st.getState() != STARTED : return 
   
   item,chance=DROPLIST[npc.getNpcId()]
   if st.getRandom(100)<chance :
     st.giveItems(item,1)
     st.playSound("ItemSound.quest_itemget")
   return

QUEST       = Quest(326,qn,"Vanquish Remnants")
CREATED     = State('Start', QUEST)
STARTING    = State('Starting', QUEST)
STARTED     = State('Started', QUEST)
COMPLETED   = State('Completed', QUEST)

QUEST.setInitialState(CREATED)
QUEST.addStartNpc(30435)

QUEST.addTalkId(30435)

QUEST.addKillId(20436)
QUEST.addKillId(20437)
QUEST.addKillId(20438)
QUEST.addKillId(20439)
QUEST.addKillId(20053)
QUEST.addKillId(20058)
QUEST.addKillId(20061)
QUEST.addKillId(20063)
QUEST.addKillId(20066)