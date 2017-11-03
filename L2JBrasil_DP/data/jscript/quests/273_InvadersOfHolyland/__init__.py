# Maked by Mr. - Version 0.3 by DrLecter
import sys
from com.it.br.gameserver.model.quest import State
from com.it.br.gameserver.model.quest import QuestState
from com.it.br.gameserver.model.quest.jython import QuestJython as JQuest

qn = "273_InvadersOfHolyland"

BLACK_SOULSTONE = 1475
RED_SOULSTONE = 1476
ADENA = 57

class Quest (JQuest) :

 def __init__(self,id,name,descr): JQuest.__init__(self,id,name,descr)

 def onEvent (self,event,st) :
    htmltext = event
    if event in ["30566-03.htm","30566-08.htm"] : # -i'll continue- event kept here for backwards compatibility only.. should be removed some day
      st.set("cond","1")
      st.setState(STARTED)
      st.playSound("ItemSound.quest_accept")
    elif event == "30566-07.htm" :
      st.exitQuest(1)
      st.playSound("ItemSound.quest_finish")
    return htmltext

 def onTalk (self,npc,player):
   htmltext = "<html><body>You are either not carrying out your quest or don't meet the criteria.</body></html>"
   st = player.getQuestState(qn)
   if not st : return htmltext

   npcId = npc.getNpcId()
   id = st.getState()

   if id in [CREATED,COMPLETED] :
     st.set("cond","0")
   if st.getInt("cond")==0 :
     if player.getRace().ordinal() != 3 :
        htmltext = "30566-00.htm"
        st.exitQuest(1)
     elif player.getLevel() < 6 :
        htmltext = "30566-01.htm"
        st.exitQuest(1)
     else:
        htmltext = "30566-02.htm"
   else :
     red=st.getQuestItemsCount(RED_SOULSTONE)
     black=st.getQuestItemsCount(BLACK_SOULSTONE)
     if red+black == 0 :
        htmltext = "30566-04.htm"
     elif red == 0 :
        htmltext = "30566-05.htm"
        if black > 9 :
           st.giveItems(ADENA,black*3+1500)
        else :
           st.giveItems(ADENA,black*3)
        st.takeItems(BLACK_SOULSTONE,black)
        st.playSound("ItemSound.quest_finish")
     else:
        htmltext = "30566-06.htm"
        amount=0
        if black :
           amount = black*3
           st.takeItems(BLACK_SOULSTONE,black)
        amount += red*10
        if black+red > 9:
           amount += 1800
        st.takeItems(RED_SOULSTONE,red)
        st.giveItems(ADENA,amount)
        st.playSound("ItemSound.quest_finish")
   return htmltext

 def onKill(self,npc,player,isPet):
   st = player.getQuestState(qn)
   if not st : return 
   if st.getState() != STARTED : return 
   
   npcId = npc.getNpcId()
   if npcId == 20311 : chance = 90
   if npcId == 20312 : chance = 87
   if npcId == 20313 : chance = 77
   if st.getRandom(100) <= chance :
      st.giveItems(BLACK_SOULSTONE,1)
   else:
      st.giveItems(RED_SOULSTONE,1)
   st.playSound("ItemSound.quest_itemget")
   return

QUEST       = Quest(273,qn,"Invaders Of Holyland")
CREATED     = State('Start', QUEST)
STARTING    = State('Starting', QUEST)
STARTED     = State('Started', QUEST)
COMPLETED   = State('Completed', QUEST)

QUEST.setInitialState(CREATED)
QUEST.addStartNpc(30566)

QUEST.addTalkId(30566)

QUEST.addKillId(20311)
QUEST.addKillId(20312)
QUEST.addKillId(20313)