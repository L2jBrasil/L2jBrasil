# Maked by Mr. Have fun! - Version 0.3 by kmarty
import sys
from com.it.br.gameserver.model.quest import State
from com.it.br.gameserver.model.quest import QuestState
from com.it.br.gameserver.model.quest.jython import QuestJython as JQuest

qn = "276_HestuiTotem"

KASHA_PARASITE_ID = 1480
KASHA_CRYSTAL_ID = 1481
HESTUIS_TOTEM_ID = 1500
LEATHER_PANTS_ID = 29

class Quest (JQuest) :

 def __init__(self,id,name,descr): JQuest.__init__(self,id,name,descr)

 def onEvent (self,event,st) :
    htmltext = event
    if event == "1" :
      st.set("cond","1")
      st.setState(STARTED)
      st.playSound("ItemSound.quest_accept")
      htmltext = "30571-03.htm"
    return htmltext

 def onTalk (self,npc,player):
   htmltext = "<html><body>You are either not carrying out your quest or don't meet the criteria.</body></html>"
   st = player.getQuestState(qn)
   if not st : return htmltext

   npcId = npc.getNpcId()
   id = st.getState()
   if id == CREATED :
     st.set("cond","0")
   if npcId == 30571 and st.getInt("cond")==0 :
      if player.getRace().ordinal() != 3 :
        htmltext = "30571-00.htm"
        st.exitQuest(1)
      elif player.getLevel() < 15 :
        htmltext = "30571-01.htm"
        st.exitQuest(1)
      else:
         htmltext = "30571-02.htm"
   elif npcId == 30571 and st.getInt("cond") :
      if st.getQuestItemsCount(KASHA_CRYSTAL_ID) == 0 :
        htmltext = "30571-04.htm"
      else:
        htmltext = "30571-05.htm"
        st.exitQuest(1)
        st.playSound("ItemSound.quest_finish")
        st.takeItems(KASHA_CRYSTAL_ID,-1)
        st.takeItems(KASHA_PARASITE_ID,-1)
        st.giveItems(HESTUIS_TOTEM_ID,1)
        st.giveItems(LEATHER_PANTS_ID,1)
   return htmltext

 def onKill(self,npc,player,isPet):
   st = player.getQuestState(qn)
   if not st : return 
   if st.getState() != STARTED : return 
   
   npcId = npc.getNpcId()
   if npcId == 20479 :
      if st.getInt("cond")==1 and st.getQuestItemsCount(KASHA_CRYSTAL_ID) == 0 :
        count = st.getQuestItemsCount(KASHA_PARASITE_ID)
        random = st.getRandom(100)
        if (count >= 70 and random < 90) or \
           (count >= 65 and random < 75) or \
           (count >= 60 and random < 60) or \
           (count >= 52 and random < 45) or \
           (count >  50 and random < 30) :
                st.addSpawn(27044)
                st.takeItems(KASHA_PARASITE_ID,count)
        else :
                st.giveItems(KASHA_PARASITE_ID,1)
                st.playSound("ItemSound.quest_itemget")
   elif npcId == 27044 :
      if st.getInt("cond")==1 and st.getQuestItemsCount(KASHA_CRYSTAL_ID) == 0 :
        st.giveItems(KASHA_CRYSTAL_ID,1)
        st.playSound("ItemSound.quest_middle")
        st.set("cond","2")
   return

QUEST       = Quest(276,qn,"Hestui Totem")
CREATED     = State('Start', QUEST)
STARTED     = State('Started', QUEST)
COMPLETED   = State('Completed', QUEST)

QUEST.setInitialState(CREATED)

QUEST.addStartNpc(30571)
QUEST.addTalkId(30571)

QUEST.addKillId(20479)
QUEST.addKillId(27044)