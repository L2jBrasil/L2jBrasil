#Made by Kerb
import sys

from com.it.br.gameserver.model.quest import State
from com.it.br.gameserver.model.quest import QuestState
from com.it.br.gameserver.model.quest.jython import QuestJython as JQuest

qn = "124_MeetingTheElroki"

#Npc
MARQUEZ = 32113
MUSHIKA = 32114
ASAMAH = 32115
KARAKAWEI = 32117
MANTARASA = 32118
#Items
M_EGG = 8778

class Quest (JQuest) :

 def __init__(self,id,name,descr): JQuest.__init__(self,id,name,descr)

 def onEvent (self,event,st) :
     cond = st.getInt("cond")
     htmltext = event

     if event == "32113-02.htm" :
       st.setState(STARTED) 
     if event == "32113-03.htm" :
       if cond == 0 :
         st.set("cond","1")
         st.playSound("ItemSound.quest_accept")
     if event == "32113-04.htm" :
       if cond == 1 :
         st.set("cond","2")
         st.playSound("ItemSound.quest_itemget")
     if event == "32114-02.htm" :
       if cond == 2 :
         st.set("cond","3")
         st.playSound("ItemSound.quest_itemget")
     if event == "32115-04.htm" :
       if cond == 3 :
         st.set("cond","4")
         st.playSound("ItemSound.quest_itemget")
     if event == "32117-02.htm" :
       if cond == 4 :
         st.setState(MIDDLE)
     if event == "32117-03.htm" :
       if cond == 4 :
         st.set("cond","5")
         st.playSound("ItemSound.quest_itemget")
     if event == "32118-02.htm" :
       if cond == 5 :
         st.giveItems(M_EGG,1)
         st.set("cond","6")
         st.playSound("ItemSound.quest_middle")
     return htmltext


 def onTalk (self,npc,player):
     htmltext = "<html><body>You are either not carrying out your quest or don't meet the criteria.</body></html>"
     st = player.getQuestState(qn)
     if not st : return htmltext
     npcId = npc.getNpcId()
     id = st.getState()
     cond = st.getInt("cond")

     if id == CREATED :
         st.set("cond","0")
     elif id == COMPLETED :
         htmltext = "<html><body>This quest has already been completed.</body></html>"
     if npcId == MARQUEZ:
         if cond == 0 :
             if player.getLevel() < 75 :
                 htmltext = "32113-01a.htm"
                 st.exitQuest(1)
             else :
                 htmltext = "32113-01.htm"
         elif cond == 1 :
             htmltext = "32113-03.htm"
         elif cond == 2 :
             htmltext = "32113-04a.htm"
     elif npcId == MUSHIKA and cond == 2 :
         htmltext = "32114-01.htm"
     elif npcId == ASAMAH :
         if cond == 3 :
             htmltext = "32115-01.htm"
         elif cond == 6 :
            htmltext = "32115-05.htm"
            st.takeItems(M_EGG,1)
            st.giveItems(57,71318)
            st.setState(COMPLETED)
            st.set("cond","0")
            st.playSound("ItemSound.quest_finish")
     elif npcId == KARAKAWEI :
         if cond == 4 :
             htmltext = "32117-01.htm"
             if id == MIDDLE :
                 htmltext = "32117-02.htm"
         elif cond == 5 :
            htmltext = "32117-04.htm"
     elif npcId == MANTARASA and cond == 5 :
         htmltext = "32118-01.htm"
     return htmltext


QUEST       = Quest(124,qn,"Meeting The Elroki")
CREATED     = State('Start', QUEST)
STARTED     = State('Started', QUEST)
MIDDLE      = State('Middle', QUEST)
COMPLETED   = State('Completed', QUEST)

QUEST.setInitialState(CREATED)
QUEST.addStartNpc(MARQUEZ)
QUEST.addTalkId(MARQUEZ)
QUEST.addTalkId(MUSHIKA)
QUEST.addTalkId(ASAMAH)
QUEST.addTalkId(KARAKAWEI)
QUEST.addTalkId(MANTARASA)