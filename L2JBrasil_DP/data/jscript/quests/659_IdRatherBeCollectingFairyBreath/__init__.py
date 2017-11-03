#Made by Kerb
import sys
from com.it.br.gameserver.model.quest import State
from com.it.br.gameserver.model.quest import QuestState
from com.it.br.gameserver.model.quest.jython import QuestJython as JQuest

qn = "659_IdRatherBeCollectingFairyBreath"

# NPC
GALATEA = 30634

# MOBS
MOBS=[20078,21026,21025,21024,21023]

#QUEST ITEMS
FAIRY_BREATH = 8286


class Quest (JQuest) :

 def __init__(self,id,name,descr): JQuest.__init__(self,id,name,descr)

 def onEvent (self,event,st) :
    htmltext = event
    if event == "30634-03.htm" :
       st.set("cond","1")
       st.setState(STARTED)
       st.playSound("ItemSound.quest_accept")
    elif event == "30634-06.htm" :
       count = st.getQuestItemsCount(FAIRY_BREATH)
       if count > 0 :
          if count<10 :
             reward = count*50
          else :
             reward = count*50+5365
          st.takeItems(FAIRY_BREATH,-1)
          st.giveItems(57,reward)
    elif event == "30634-08.htm" :
       st.exitQuest(1)
    return htmltext

 def onTalk (self,npc,player):
   htmltext = "<html><body>You are either not carrying out your quest or don't meet the criteria.</body></html>"
   st = player.getQuestState(qn)
   if not st: return
   id = st.getState()
   if id == CREATED :
      st.set("cond","0")
   cond = st.getInt("cond") 
   if st.getPlayer().getLevel() < 26 :
      htmltext = "30634-01.htm"
      st.exitQuest(1)
   elif cond == 0 :
      htmltext = "30634-02.htm"
   elif cond == 1 :
      if st.getQuestItemsCount(FAIRY_BREATH) == 0 :
        htmltext = "30634-04.htm"
      else :
        htmltext = "30634-05.htm"
   return htmltext

 def onKill(self,npc,player,isPet):
   st = player.getQuestState(qn)
   if not st: return
   npcId = npc.getNpcId()
   if st.getInt("cond") == 1 :
     chance = st.getRandom(100)  
     if npcId in MOBS and chance < 90 :  
         st.giveItems(FAIRY_BREATH,1) 
         st.playSound("ItemSound.quest_itemget")
   return

QUEST       = Quest(659,qn,"I'd Rather Be Collecting Fairy Breath")
CREATED     = State('Start', QUEST)
STARTED     = State('Started', QUEST)

QUEST.setInitialState(CREATED)
QUEST.addStartNpc(GALATEA)
QUEST.addTalkId(GALATEA)

for mob in MOBS:
   QUEST.addKillId(mob)