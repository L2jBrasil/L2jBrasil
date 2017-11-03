# Made by mtrix
import sys
from com.it.br.gameserver.model.quest import State
from com.it.br.gameserver.model.quest import QuestState
from com.it.br.gameserver.model.quest.jython import QuestJython as JQuest

qn = "368_TrespassingIntoTheSacredArea"

ADENA = 57
BLADE_STAKATO_FANG = 5881
CHANCE = 9

class Quest (JQuest) :

 def __init__(self,id,name,descr): JQuest.__init__(self,id,name,descr)

 def onEvent (self,event,st) :
     htmltext = event
     if event == "30926-02.htm" :
         st.set("cond","1")
         st.setState(STARTED)
         st.playSound("ItemSound.quest_accept")
     elif event == "30926-05.htm" :
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
     amount = st.getQuestItemsCount(BLADE_STAKATO_FANG)
     if id == CREATED :
        if level>=36 :
            htmltext = "30926-01.htm"
        else :
            htmltext = "<html><body>(This is a quest that can only be performed by players of level 36 and above.)</body></html>"
     elif cond and not amount :
         htmltext = "30926-03.htm"
     elif amount :
         htmltext = "30926-04.htm"
         st.giveItems(ADENA,amount*1250)
         st.takeItems(BLADE_STAKATO_FANG,-1)
         st.playSound("ItemSound.quest_middle")
     return htmltext
    
 def onKill(self,npc,player,isPet):
     partyMember = self.getRandomPartyMemberState(player,STARTED)
     if not partyMember : return
     st = partyMember.getQuestState(qn)
   
     npcId = npc.getNpcId()
     random = st.getRandom(100)
     chance = CHANCE + npcId - 20794
     if random<=chance :
         st.giveItems(BLADE_STAKATO_FANG,1)
         st.playSound("ItemSound.quest_itemget")
     return

QUEST       = Quest(368,qn,"Trespassing Into The Sacred Area")
CREATED     = State('Start', QUEST)
STARTED     = State('Started', QUEST)

QUEST.setInitialState(CREATED)
QUEST.addStartNpc(30926)

QUEST.addTalkId(30926)

for i in range(20794,20798) :
    QUEST.addKillId(i)