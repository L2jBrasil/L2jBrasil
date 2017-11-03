# Created by Gigiikun
import sys

from com.it.br import Config
from com.it.br.gameserver.model.quest import State
from com.it.br.gameserver.model.quest import QuestState
from com.it.br.gameserver.model.quest.jython import QuestJython as JQuest 

qn = "642_APowerfulPrimevalCreature"

#Settings: drop chance in %
EGG_DROP_CHANCE = 1
TISSUE_DROP_CHANCE = 33

#Set this to non-zero to use 100% recipes as reward instead of default 60%
ALT_RP_100 = 0

#NPC 
DINN = 32105 

DINOSAUR_TISSUE = 8774
DINOSAUR_EGG = 8775
DINOSAURS = [22196,22197,22198,22199,22200,22201,22202,22203,22204,22205,22218,22219,22220,22223,22224,22225,18344]
REWARDS = [8690,8692,8694,8696,8698,8700,8702,8704,8706,8708,8710]


class Quest (JQuest) :

 def __init__(self,id,name,descr):
 	JQuest.__init__(self,id,name,descr)
 	self.questItemIds = [DINOSAUR_TISSUE, DINOSAUR_EGG]

 def onAdvEvent (self,event,npc, player) :
    htmltext = event
    st = player.getQuestState(qn)
    if not st : return
    count_tissue = st.getQuestItemsCount(DINOSAUR_TISSUE)
    count_egg = st.getQuestItemsCount(DINOSAUR_EGG)
    if event == "None":
        return
    elif event == "32105-04.htm" :
       st.set("cond","1")
       st.setState(STARTED)
       st.playSound("ItemSound.quest_accept")
    elif event == "32105-08.htm" :
       st.takeItems(DINOSAUR_TISSUE,-1)
       st.giveItems(57,count_tissue*5000)
    elif event == "32105-07.htm" :
       if count_tissue < 150 or count_egg == 0 :
          htmltext = "32105-05.htm"
       elif ALT_RP_100 != 0 :
          htmltext = st.showHtmlFile("32105-07.htm").replace("60%","100%")
    elif event.isdigit() and int(event) in REWARDS :
       if count_tissue >= 150 and count_egg >= 1 :
          htmltext = "32105-08.htm"
          st.takeItems(DINOSAUR_TISSUE,150)
          st.takeItems(DINOSAUR_EGG,1)
          st.giveItems(57,44000)
          if ALT_RP_100 != 0 :
             st.giveItems(int(event)+1,1)
          else :
             st.giveItems(int(event),1)
       else :
          htmltext = "Incorrect item count"
    return htmltext

 def onTalk (self, npc, player):
    st = player.getQuestState(qn)
    htmltext = "<html><body>You are either not on a quest that involves this NPC, or you don't meet this NPC's minimum quest requirements.</body></html>"
    if st :
       cond = st.getInt("cond")
       count = st.getQuestItemsCount(DINOSAUR_TISSUE)
       if cond == 0 :
          if player.getLevel() >= 75 :
             htmltext = "32105-01.htm"
          else :
             htmltext = "32105-00.htm"
             st.exitQuest(1)
       elif st.getState() == STARTED :
          if count == 0 :
             htmltext = "32105-05.htm"
          else :
             htmltext = "32105-06.htm"
    return htmltext

 def onKill (self, npc, player,isPet):
    st = player.getQuestState(qn)
    if st :
       if st.getState() == STARTED :
          npcId = npc.getNpcId()
          cond = st.getInt("cond")
          count = st.getQuestItemsCount(DINOSAUR_TISSUE)
          if cond == 1 :
             if npcId == 18344 :
                itemId = DINOSAUR_EGG
                chance = EGG_DROP_CHANCE*Config.RATE_DROP_QUEST
                numItems, chance = divmod(chance,100)
             else :
                itemId = DINOSAUR_TISSUE
                chance = TISSUE_DROP_CHANCE*Config.RATE_DROP_QUEST
                numItems, chance = divmod(chance,100)
             if st.getRandom(100) < chance : 
                numItems += 1
             if numItems :
                if int(count + numItems)/150 > int(count)/150 and itemId == DINOSAUR_TISSUE :
                   st.playSound("ItemSound.quest_middle")
                else :
                   st.playSound("ItemSound.quest_itemget")
                st.giveItems(itemId,int(numItems))
    return

QUEST = Quest(642,qn,"A Powerful Primeval creature")
CREATED     = State('Start', QUEST)
STARTED     = State('Started', QUEST)
COMPLETED   = State('Completed', QUEST)

QUEST.setInitialState(CREATED)

QUEST.addStartNpc(DINN)

QUEST.addTalkId(DINN)

for mob in DINOSAURS :
   QUEST.addKillId(mob)
