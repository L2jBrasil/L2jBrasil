# Made by Mr - Version 0.3 by DrLecter
import sys
from com.it.br import Config
from com.it.br.gameserver.model.quest import State
from com.it.br.gameserver.model.quest import QuestState
from com.it.br.gameserver.model.quest.jython import QuestJython as JQuest

qn = "275_DarkWingedSpies"

DARKWING_BAT_FANG = 1478
VARANGKAS_PARASITE = 1479
ADENA = 57

class Quest (JQuest) :

 def __init__(self,id,name,descr): JQuest.__init__(self,id,name,descr)

 def onEvent (self,event,st) :
    htmltext = event
    if event == "30567-03.htm" :
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
   if st.getInt("cond")==0 :
     if player.getRace().ordinal() != 3 :
       htmltext = "30567-00.htm"
       st.exitQuest(1)
     elif player.getLevel() < 11 :
       htmltext = "30567-01.htm"
       st.exitQuest(1)
     else :
       htmltext = "30567-02.htm"
   else :
     if st.getQuestItemsCount(DARKWING_BAT_FANG) < 70 :
       htmltext = "30567-04.htm"
     else:
       htmltext = "30567-05.htm"
       st.exitQuest(1)
       st.playSound("ItemSound.quest_finish")
       st.giveItems(ADENA,4200)
       st.takeItems(DARKWING_BAT_FANG,-1)
       st.takeItems(VARANGKAS_PARASITE,-1)
   return htmltext

 def onKill(self,npc,player,isPet):
   st = player.getQuestState(qn)
   if not st : return 
   if st.getState() != STARTED : return 
   
   npcId = npc.getNpcId()
   if npcId == 20316 :
     if st.getQuestItemsCount(DARKWING_BAT_FANG) < 70 :
        if st.getQuestItemsCount(DARKWING_BAT_FANG) < 69 :
          st.playSound("ItemSound.quest_itemget")
        else:
          st.playSound("ItemSound.quest_middle")
          st.set("cond","2")
        st.giveItems(DARKWING_BAT_FANG,1)
        if 66>st.getQuestItemsCount(DARKWING_BAT_FANG)>10 and st.getRandom(100) < 10 :
          st.addSpawn(27043)
          st.giveItems(VARANGKAS_PARASITE,1)
   else :
      if st.getQuestItemsCount(DARKWING_BAT_FANG) < 66 and st.getQuestItemsCount(VARANGKAS_PARASITE) :
        if st.getQuestItemsCount(DARKWING_BAT_FANG) < 65 :
          st.playSound("ItemSound.quest_itemget")
        else:
          st.playSound("ItemSound.quest_middle")
          st.set("cond","2")
        st.giveItems(DARKWING_BAT_FANG,5)
        st.takeItems(VARANGKAS_PARASITE,-1)
   return

QUEST       = Quest(275,qn,"Dark Winged Spies")
CREATED     = State('Start', QUEST)
STARTING    = State('Starting', QUEST)
STARTED     = State('Started', QUEST)
COMPLETED   = State('Completed', QUEST)


QUEST.setInitialState(CREATED)
QUEST.addStartNpc(30567)

QUEST.addTalkId(30567)

QUEST.addKillId(20316)
QUEST.addKillId(27043)