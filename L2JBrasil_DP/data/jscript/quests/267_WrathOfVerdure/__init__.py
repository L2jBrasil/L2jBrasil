# Made by Mr. Have fun! - Version 0.3 by DrLecter
import sys
from com.it.br.gameserver.model.quest import State
from com.it.br.gameserver.model.quest import QuestState
from com.it.br.gameserver.model.quest.jython import QuestJython as JQuest

qn = "267_WrathOfVerdure"

GOBLIN_CLUB = 1335
SILVERY_LEAF = 1340

class Quest (JQuest) :

 def __init__(self,id,name,descr): JQuest.__init__(self,id,name,descr)

 def onEvent (self,event,st) :
    htmltext = event
    if event == "31853-03.htm" :
      st.set("cond","1")
      st.setState(STARTED)
      st.playSound("ItemSound.quest_accept")
    elif event == "31853-06.htm" :
      st.exitQuest(1)
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
   if st.getInt("cond")==0 :
     if player.getRace().ordinal() != 1 :
       htmltext = "31853-00.htm"
       st.exitQuest(1)
     elif player.getLevel()<4 :
       htmltext = "31853-01.htm"
       st.exitQuest(1)
     else :
       htmltext = "31853-02.htm"
   else :
     count=st.getQuestItemsCount(GOBLIN_CLUB)
     if count :
       st.giveItems(SILVERY_LEAF,count)
       st.takeItems(GOBLIN_CLUB,-1)
       htmltext = "31853-05.htm"
     else:
       htmltext = "31853-04.htm"
   return htmltext

 def onKill(self,npc,player,isPet):
   st = player.getQuestState(qn)
   if not st : return 
   if st.getState() != STARTED : return 
   
   if st.getRandom(10)<5 :
     st.giveItems(GOBLIN_CLUB,1)
     st.playSound("ItemSound.quest_itemget")
   return

QUEST       = Quest(267,qn,"Wrath Of Verdure")
CREATED     = State('Start', QUEST)
STARTING    = State('Starting', QUEST)
STARTED     = State('Started', QUEST)
COMPLETED   = State('Completed', QUEST)

QUEST.setInitialState(CREATED)
QUEST.addStartNpc(31853)

QUEST.addTalkId(31853)

QUEST.addKillId(20325)