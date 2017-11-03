# Made by mtrix
import sys
from com.it.br.gameserver.model.quest import State
from com.it.br.gameserver.model.quest import QuestState
from com.it.br.gameserver.model.quest.jython import QuestJython as JQuest

qn = "352_HelpRoodRaiseANewPet"

ADENA = 57
LIENRIK_EGG1 = 5860
LIENRIK_EGG2 = 5861
CHANCE = 30
CHANCE2 = 7

class Quest (JQuest) :

 def __init__(self,id,name,descr): JQuest.__init__(self,id,name,descr)

 def onEvent (self,event,st) :
     htmltext = event
     if event == "31067-04.htm" :
         st.set("cond","1")
         st.setState(STARTED)
         st.playSound("ItemSound.quest_accept")
     elif event == "31067-09.htm" :
         st.playSound("ItemSound.quest_finish")
         st.exitQuest(1)
     return htmltext

 def onTalk (self,npc,player):
     htmltext = "<html><body>You are either not carrying out your quest or don't meet the criteria.</body></html>"
     st = player.getQuestState(qn)
     if not st : return htmltext

     npcId = npc.getNpcId()
     id = st.getState()
     level = player.getLevel()
     cond = st.getInt("cond")
     eggs1 = st.getQuestItemsCount(LIENRIK_EGG1)
     eggs2 = st.getQuestItemsCount(LIENRIK_EGG2)
     if id == CREATED :
        if level>=39 :
            htmltext = "31067-01.htm"
        else :
            htmltext = "<html><body>(This is a quest that can only be performed by players of level 39 and above.)</body></html>"
            st.exitQuest(1)
     elif cond==1 :
        if not eggs1 and not eggs2 :
          htmltext = "31067-05.htm"
        elif eggs1 and not eggs2 :
          htmltext = "31067-06.htm"
          st.giveItems(ADENA,eggs1*209)
          st.takeItems(LIENRIK_EGG1,-1)
          st.playSound("ItemSound.quest_itemget")
        elif not eggs1 and eggs2 :
          htmltext = "31067-08.htm"
          st.giveItems(ADENA,eggs2*2050)
          st.takeItems(LIENRIK_EGG2,-1)
          st.playSound("ItemSound.quest_itemget")
        elif eggs1 and eggs2 :
          htmltext = "31067-08.htm"
          st.giveItems(ADENA,eggs1*209+eggs2*2050)
          st.takeItems(LIENRIK_EGG1,-1)
          st.takeItems(LIENRIK_EGG2,-1)
          st.playSound("ItemSound.quest_itemget")
     return htmltext

 def onKill(self,npc,player,isPet):
     st = player.getQuestState(qn)
     if not st : return 
     if st.getState() != STARTED : return 
     npcId = npc.getNpcId()
     random = st.getRandom(100)
     if random<=CHANCE :
         st.giveItems(LIENRIK_EGG1,1)
     if random<=CHANCE2 :
         st.giveItems(LIENRIK_EGG2,1)
     return

QUEST       = Quest(352,qn,"Help Rood Raise A New Pet")
CREATED     = State('Start', QUEST)
STARTED     = State('Started', QUEST)

QUEST.setInitialState(CREATED)
QUEST.addStartNpc(31067)

QUEST.addTalkId(31067)

QUEST.addKillId(20786)
QUEST.addKillId(20787)