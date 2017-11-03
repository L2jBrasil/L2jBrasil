# Made by mtrix
import sys
from com.it.br.gameserver.model.quest import State
from com.it.br.gameserver.model.quest import QuestState
from com.it.br.gameserver.model.quest.jython import QuestJython as JQuest

qn = "341_HuntingForWildBeasts"

BEAR_SKIN = 4259
ADENA = 57
CHANCE = 400000

class Quest (JQuest) :

 def __init__(self,id,name,descr): JQuest.__init__(self,id,name,descr)

 def onEvent (self,event,st) :
     htmltext = event
     if event == "30078-02.htm" :
        st.setState(STARTED)
        st.set("cond","1")
        st.playSound("ItemSound.quest_accept")
     return htmltext

 def onTalk (self,npc,player):
     htmltext = "<html><body>You are either not carrying out your quest or don't meet the criteria.</body></html>"
     st = player.getQuestState(qn)
     if not st : return htmltext

     npcId = npc.getNpcId()
     id = st.getState()
     level = player.getLevel()
     cond = st.getInt("cond")
     if id == CREATED :
         if level>=20 :
             htmltext = "30078-01.htm"
         else:
             htmltext = "<html><body>This quest can only be taken by characters level 20 and higher!</body></html>"
             st.exitQuest(1)
     elif cond==1 :
         if st.getQuestItemsCount(BEAR_SKIN)>=20 :
            htmltext = "30078-04.htm"
            st.giveItems(ADENA,3710)
            st.takeItems(BEAR_SKIN,-1)
            st.playSound("ItemSound.quest_finish")
            st.exitQuest(1)
         else :
            htmltext = "30078-03.htm"
     return htmltext

 def onKill(self,npc,player,isPet):
     st = player.getQuestState(qn)
     if not st : return 
     if st.getState() != STARTED : return 

     npcId = npc.getNpcId()
     cond = st.getInt("cond")
     if cond==1 :
         st.dropQuestItems(BEAR_SKIN,1,20,CHANCE,1)
     return

QUEST       = Quest(341,qn,"Hunting For Wild Beasts")
CREATED     = State('Start', QUEST)
STARTED     = State('Started', QUEST)

QUEST.setInitialState(CREATED)
QUEST.addStartNpc(30078)

QUEST.addTalkId(30078)

QUEST.addKillId(20021)
QUEST.addKillId(20203)
QUEST.addKillId(20310)
QUEST.addKillId(20335)