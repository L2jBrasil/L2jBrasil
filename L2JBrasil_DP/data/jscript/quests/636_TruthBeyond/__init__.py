# Made by Polo - Have fun! - Fixed by BiTi
# v0.3.1 by DrLecter
import sys
from com.it.br.gameserver.model.quest import State
from com.it.br.gameserver.model.quest import QuestState
from com.it.br.gameserver.model.quest.jython import QuestJython as JQuest

qn = "636_TruthBeyond"

#Npc
ELIYAH = 31329
FLAURON = 32010

#Items
MARK = 8064

class Quest (JQuest) :

 def __init__(self,id,name,descr): JQuest.__init__(self,id,name,descr)

 def onEvent (self,event,st) :
    htmltext = event
    if htmltext == "31329-04.htm" :
       st.set("cond","1")
       st.setState(STARTED)
       st.playSound("ItemSound.quest_accept")
    elif htmltext == "32010-02.htm" :
       st.playSound("ItemSound.quest_finish")
       st.giveItems(MARK,1)
       st.unset("cond")
       st.setState(COMPLETED)
    return htmltext

 def onTalk (self,npc,player):
   st = player.getQuestState(qn)
   htmltext = "<html><body>You are either not carrying out your quest or don't meet the criteria.</body></html>"
   if st :
     npcId = npc.getNpcId()
     id = st.getState()
     cond = st.getInt("cond")
     if id == COMPLETED and not st.getQuestItemsCount(MARK) and not st.getQuestItemsCount(8065) and not st.getQuestItemsCount(8067) :
       st.set("cond","0")
       st.setState(CREATED)
       cond = 0
       id = CREATED
     elif id == COMPLETED and st.getQuestItemsCount(MARK) == 1 :
       return "<html><body>Go to the temple and talk to the teleporter near the front gate.</body></html>"
     elif id == COMPLETED and st.getQuestItemsCount(8065) == 1 :
       return "<html><body>This quest is already completed. To become a pagan you should try to go <font color = \"LEVEL\">Through the gate once more</font>.</body></html>"
     elif id == COMPLETED and st.getQuestItemsCount(8067) == 1 :
       return "<html><body>You are already a Pagan. There are no further tasks for you here.</body></html>"
     if cond == 0 and id == CREATED :
       if npcId == ELIYAH :
         if player.getLevel()>72 :
           htmltext = "31329-02.htm"
       else:
         htmltext = "31329-01.htm"
         st.exitQuest(1)
     elif id == STARTED :
       if npcId == ELIYAH :
         htmltext = "31329-05.htm"
       elif npcId == FLAURON :
         if cond == 1 :
           htmltext = "32010-01.htm"
           st.set("cond","2")
         else :
           htmltext = "32010-03.htm"
   return htmltext


QUEST       = Quest(636,qn,"The Truth Beyond the Gate")
CREATED     = State('Start', QUEST)
STARTED     = State('Started', QUEST)
COMPLETED   = State('Completed', QUEST)

QUEST.setInitialState(CREATED)
QUEST.addStartNpc(ELIYAH)

QUEST.addTalkId(ELIYAH)
QUEST.addTalkId(FLAURON)