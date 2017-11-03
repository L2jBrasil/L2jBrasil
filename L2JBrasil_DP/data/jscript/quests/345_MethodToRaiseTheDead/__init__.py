# Made by mtrix
import sys
from com.it.br.gameserver.model.quest import State
from com.it.br.gameserver.model.quest import QuestState
from com.it.br.gameserver.model.quest.jython import QuestJython as JQuest

qn = "345_MethodToRaiseTheDead"

ADENA = 57
VICTIMS_ARM_BONE = 4274
VICTIMS_THIGH_BONE = 4275
VICTIMS_SKULL = 4276
VICTIMS_RIB_BONE = 4277
VICTIMS_SPINE = 4278
USELESS_BONE_PIECES = 4280
POWDER_TO_SUMMON_DEAD_SOULS = 4281
BILL_OF_IASON_HEINE = 4310
CHANCE = 15
CHANCE2 = 50

class Quest (JQuest) :

 def __init__(self,id,name,descr): JQuest.__init__(self,id,name,descr)

 def onEvent (self,event,st) :
     htmltext = event
     if event == "1" :
         st.set("cond","1")
         st.setState(STARTED)
         htmltext = "30970-02.htm"
         st.playSound("ItemSound.quest_accept")
     elif event == "2" :
         st.set("cond","2")
         htmltext = "30970-06.htm"
     elif event == "3" :
         if st.getQuestItemsCount(ADENA)>=1000 :
             st.takeItems(ADENA,1000)
             st.giveItems(POWDER_TO_SUMMON_DEAD_SOULS,1)
             st.set("cond","3")
             htmltext = "30912-03.htm"
             st.playSound("ItemSound.quest_itemget")
         else :
             htmltext = "<html><body>You dont have enough adena!</body></html>"
     elif event == "4" :
         htmltext = "30973-02.htm"
         st.takeItems(POWDER_TO_SUMMON_DEAD_SOULS,-1)
         st.takeItems(VICTIMS_ARM_BONE,-1)
         st.takeItems(VICTIMS_THIGH_BONE,-1)
         st.takeItems(VICTIMS_SKULL,-1)
         st.takeItems(VICTIMS_RIB_BONE,-1)
         st.takeItems(VICTIMS_SPINE,-1)
         st.set("cond","6")
     return htmltext

 def onTalk (self,npc,player):
     htmltext = "<html><body>You are either not carrying out your quest or don't meet the criteria.</body></html>"
     st = player.getQuestState(qn)
     if not st : return htmltext

     npcId = npc.getNpcId()
     id = st.getState()
     if npcId != 30970 and id != STARTED : return htmltext

     level = player.getLevel()
     cond = st.getInt("cond")
     amount = st.getQuestItemsCount(USELESS_BONE_PIECES)
     if npcId==30970 :
         if id == CREATED :
             if level>=35 :
                 htmltext = "30970-01.htm"
             else :
                 htmltext = "<html><body>(This is a quest that can only be performed by players of level 35 and above.)</body></html>"
                 st.exitQuest(1)
         elif cond==1 and st.getQuestItemsCount(VICTIMS_ARM_BONE) and st.getQuestItemsCount(VICTIMS_THIGH_BONE) and st.getQuestItemsCount(VICTIMS_SKULL) and st.getQuestItemsCount(VICTIMS_RIB_BONE) and st.getQuestItemsCount(VICTIMS_SPINE) :
             htmltext = "30970-05.htm"
         elif cond==1 and (st.getQuestItemsCount(VICTIMS_ARM_BONE)+st.getQuestItemsCount(VICTIMS_THIGH_BONE)+st.getQuestItemsCount(VICTIMS_SKULL)+st.getQuestItemsCount(VICTIMS_RIB_BONE)+st.getQuestItemsCount(VICTIMS_SPINE)<5) :
             htmltext = "30970-04.htm"
         elif cond==7 :
             htmltext = "30970-07.htm"
             st.set("cond","1")
             st.giveItems(ADENA,amount*238)
             st.giveItems(BILL_OF_IASON_HEINE,st.getRandom(7)+1)
             st.takeItems(USELESS_BONE_PIECES,-1)
     if npcId==30912 :
         if cond == 2 :
             htmltext = "30912-01.htm"
             st.playSound("ItemSound.quest_middle")
         elif cond == 3 :
             htmltext = "<html><body>What did the urn say?</body></html>"
         elif cond == 6 :
             htmltext = "30912-04.htm"
             st.set("cond","7")
     if npcId==30973 :
         if cond==3 :
             htmltext = "30973-01.htm"
     return htmltext

 def onKill(self,npc,player,isPet):
     st = player.getQuestState(qn)
     if not st : return 
     if st.getState() != STARTED : return 
   
     npcId = npc.getNpcId()
     random = st.getRandom(100)
     if random<=CHANCE :
         if not st.getQuestItemsCount(VICTIMS_ARM_BONE) :
            st.giveItems(VICTIMS_ARM_BONE,1)
         elif not st.getQuestItemsCount(VICTIMS_THIGH_BONE) :
            st.giveItems(VICTIMS_THIGH_BONE,1)
         elif not st.getQuestItemsCount(VICTIMS_SKULL) :
            st.giveItems(VICTIMS_SKULL,1)
         elif not st.getQuestItemsCount(VICTIMS_RIB_BONE) :
            st.giveItems(VICTIMS_RIB_BONE,1)
         elif not st.getQuestItemsCount(VICTIMS_SPINE) :
            st.giveItems(VICTIMS_SPINE,1)
     if random<=CHANCE2 :
         st.giveItems(USELESS_BONE_PIECES,st.getRandom(8)+1)
     return

QUEST       = Quest(345,qn,"Method To Raise The Dead")
CREATED     = State('Start', QUEST)
STARTED     = State('Started', QUEST)

QUEST.setInitialState(CREATED)
QUEST.addStartNpc(30970)

QUEST.addTalkId(30970)

QUEST.addTalkId(30912)
QUEST.addTalkId(30973)

QUEST.addKillId(20789)
QUEST.addKillId(20791)