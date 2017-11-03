# Contributed by t0rm3nt0r to the Official L2J Datapack Project.

import sys
from com.it.br.gameserver.model.quest import State
from com.it.br.gameserver.model.quest import QuestState
from com.it.br.gameserver.model.quest.jython import QuestJython as JQuest

qn = "340_SubjugationOfLizardmen"

#NPC
WEIZ = 30385
ADONIUS = 30375
LEVIAN = 30037
CHEST = 30989
#Quest item
CARGO = 4255
HOLY = 4256
ROSARY = 4257
TOTEM = 4258
#Mobs
MOBS_1 = [ 20008, 20010, 20014 ]
MOBS_2 = [ 20357,21100,20356,21101 ]
BIFRON = 25146

class Quest (JQuest) :

 def __init__(self,id,name,descr): JQuest.__init__(self,id,name,descr)
 
 def onAdvEvent (self, event, npc, player) :
     st = player.getQuestState(qn)
     if not st: return
     htmltext = event
     if event == "30385-03.htm" :
       st.set("cond","1")
       st.setState(STARTED)
       st.playSound("ItemSound.quest_accept")
     elif event == "30385-07.htm" :
       st.takeItems(CARGO,-1)
       st.giveItems(57,4090)       
       st.set("cond","2")
       st.playSound("ItemSound.quest_middle")
     elif event == "30385-09.htm" :
       st.takeItems(CARGO,-1)
       st.giveItems(57,4090)
     elif event == "30385-10.htm" :
       st.takeItems(CARGO,-1)
       st.giveItems(57,4090)
       st.exitQuest(1)
     elif event == "30375-02.htm" :
       st.set("cond","3")
       st.playSound("ItemSound.quest_middle")
     elif event == "30037-02.htm" :
       st.set("cond","5")
       st.playSound("ItemSound.quest_middle")
     elif event == "30989-02.htm" :
       st.giveItems(TOTEM,1)
       st.set("cond","6")
       st.playSound("ItemSound.quest_middle")
       npc.reduceCurrentHp(9999999,npc)
     return htmltext

 def onTalk (self,npc,player):
     npcId = npc.getNpcId()
     htmltext = "<html><body>You are either not carrying out your quest or don't meet the criteria.</body></html>"
     st = player.getQuestState(qn)
     if not st : return htmltext
     id = st.getState()
     cond = st.getInt("cond")
     kargo = st.getQuestItemsCount(CARGO)
     rosary = st.getQuestItemsCount(ROSARY)
     holy = st.getQuestItemsCount(HOLY)
     totem = st.getQuestItemsCount(TOTEM)
     if id == COMPLETED :
       htmltext = "<html><body>This quest has already been completed.</body></html>"
     elif id == CREATED and npcId == WEIZ :
       if player.getLevel() < 17 :
         htmltext = "30385-01.htm"
         st.exitQuest(1)
       else :
         htmltext = "30385-02.htm"
     elif id == STARTED :
       if npcId == WEIZ :
         if cond == 1 :
           if kargo < 30 :
             htmltext = "30385-05.htm"
           else :
             htmltext = "30385-06.htm"
         elif cond == 2 :
           htmltext = "30385-11.htm"
         elif cond == 7 :
           st.giveItems(57,14700)
           htmltext = "30385-13.htm"
           st.set("cond","0")
           st.setState(COMPLETED)
           st.playSound("ItemSound.quest_finish")
       elif npcId == ADONIUS :
         if cond == 2 :
           htmltext = "30375-01.htm"
         elif cond == 3 :
           if rosary == 1 and holy == 1 :
             st.takeItems(HOLY,-1)
             st.takeItems(ROSARY,-1)             
             htmltext = "30375-04.htm"
             st.set("cond","4")
             st.playSound("ItemSound.quest_middle")
           else :
             htmltext = "30375-03.htm"
         elif cond == 4 :
           htmltext = "30375-05.htm"
       elif npcId == LEVIAN :
         if cond == 4 :
           htmltext = "30037-01.htm"
         elif cond == 5 :
           htmltext = "30037-03.htm"
         elif cond == 6 :
           st.takeItems(TOTEM,-1)
           st.set("cond","7")
           st.playSound("ItemSound.quest_middle")
           htmltext = "30037-04.htm"
         elif cond == 7 :
           htmltext = "30037-05.htm"
       elif npcId == CHEST :
         if cond == 5 :
           htmltext = "30989-01.htm"
         elif cond == 6 :
           htmltext = "30989-03.htm"
     return htmltext
    
 def onKill(self,npc,player,isPet):
     st = player.getQuestState(qn)
     if not st : return 
     npcId = npc.getNpcId()
     chanse = st.getRandom(100)
     kargo = st.getQuestItemsCount(CARGO)
     holy = st.getQuestItemsCount(HOLY)
     rosary = st.getQuestItemsCount(ROSARY)
     if st:
       if npcId in MOBS_1 :
         if (chanse < 40) and (kargo < 30) :
           st.giveItems(CARGO,1)
           st.playSound("ItemSound.quest_itemget")
       elif npcId in MOBS_2 :
         if (chanse < 15) and (not holy) :
           st.giveItems(HOLY,1)
           st.playSound("ItemSound.quest_middle")
         elif (chanse < 15) and (not rosary) :
           st.giveItems(ROSARY,1)
           st.playSound("ItemSound.quest_middle")
       elif npcId == BIFRON :
         st.addSpawn(CHEST,npc,30000)
     return

QUEST       = Quest(340, qn, "Subjugation of Lizardmen")
CREATED     = State('Start', QUEST)
STARTED     = State('Started', QUEST)
COMPLETED   = State('Completed', QUEST)

QUEST.setInitialState(CREATED)
QUEST.addStartNpc(WEIZ)

QUEST.addTalkId(WEIZ)
QUEST.addTalkId(ADONIUS)
QUEST.addTalkId(LEVIAN)
QUEST.addTalkId(CHEST)

for i in MOBS_1 + MOBS_2 + [25146] :
    QUEST.addKillId(i)