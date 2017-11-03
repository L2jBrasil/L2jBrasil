# Made by BiTi! v0.2
# v0.2.1 by DrLecter
import sys
from com.it.br.gameserver.model.quest import State
from com.it.br.gameserver.model.quest import QuestState
from com.it.br.gameserver.model.quest.jython import QuestJython as JQuest

qn = "637_ThroughOnceMore"

#Drop rate
CHANCE=40
#Npc
FLAURON = 32010
#Items
VISITORSMARK,NECROHEART,MARK,ANTKEY = 8065,8066,8067,8273
Quest_Mobs = [ 21565, 21566, 21567, 21568 ]
class Quest (JQuest) :


 def __init__(self,id,name,descr): JQuest.__init__(self,id,name,descr)

 def onEvent (self,event,st) :
    htmltext = "<html><body>You are either not carrying out your quest or don't meet the criteria.</body></html>"    
    if event == "2" :
       return "32010-02.htm"
    if event == "3" :
       return "32010-03.htm"
    if event == "6" :
       return "32010-06.htm"
    if event == "7" :
       return "32010-07.htm"
    if event == "4" :
       st.set("cond","1")
       st.setState(STARTED)
       st.takeItems(VISITORSMARK,1)
       st.playSound("ItemSound.quest_accept")
       htmltext = "32010-04.htm"
    return htmltext

 def onTalk (self, npc, player):
   htmltext = "<html><body>You are either not carrying out your quest or don't meet the criteria.</body></html>"
   st = player.getQuestState(qn)
   if st :
     id = st.getState()
     cond = st.getInt("cond")
     if id == CREATED:
        if player.getLevel()>72 and st.getQuestItemsCount(VISITORSMARK) :
           htmltext = "32010-02.htm"
        if player.getLevel()<73 or not st.getQuestItemsCount(VISITORSMARK) :
           htmltext = "32010-01.htm"
           st.exitQuest(1)
     elif id == STARTED :
       if cond == 2 and st.getQuestItemsCount(NECROHEART)==10:
          htmltext = "32010-05.htm"
          st.takeItems(NECROHEART,10)
          st.giveItems(MARK,1)
          st.giveItems(ANTKEY,10)
          st.setState(COMPLETED)
          st.playSound("ItemSound.quest_finish")
       else :
          htmltext = "32010-04.htm"
   return htmltext

 def onKill(self,npc,player,isPet):
   st = player.getQuestState(qn)
   if st :
     if st.getState() == STARTED :
       npcId = npc.getNpcId()
       count = st.getQuestItemsCount(NECROHEART)
       if npcId in Quest_Mobs:
          if st.getRandom(100)<CHANCE and count<10 :
             st.giveItems(NECROHEART,1)
             if count == 9 :
                st.playSound("ItemSound.quest_middle")
                st.set("cond","2")
             else:
                st.playSound("ItemSound.quest_itemget")
   return

QUEST       = Quest(637,qn,"Through the Gate Once More")
CREATED     = State('Start', QUEST)
STARTED     = State('Started', QUEST)
COMPLETED   = State('Completed', QUEST)

QUEST.setInitialState(CREATED)
QUEST.addStartNpc(FLAURON)

QUEST.addTalkId(FLAURON)

for mobId in Quest_Mobs :
  QUEST.addKillId(mobId)