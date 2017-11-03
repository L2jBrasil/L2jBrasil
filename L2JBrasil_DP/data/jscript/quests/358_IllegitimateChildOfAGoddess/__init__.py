# Illegitimate Child Of A Goddess version 0.1 
# by DrLecter
import sys
from com.it.br import Config
from com.it.br.gameserver.model.quest import State
from com.it.br.gameserver.model.quest import QuestState
from com.it.br.gameserver.model.quest.jython import QuestJython as JQuest

#Quest info
QUEST_NUMBER,QUEST_NAME,QUEST_DESCRIPTION = 358,"IllegitimateChildOfAGoddess","Illegitimate Child Of A Goddess"
qn = "358_IllegitimateChildOfAGoddess"

#Variables
DROP_RATE=12*Config.RATE_DROP_QUEST  #in %
REQUIRED=108 #how many items will be paid for a reward (affects onkill sounds too)

#Quest items
SN_SCALE = 5868

#Rewards
REWARDS=range(6329,6340,2)+range(5364,5367,2)

#Changing this value to non-zero, will turn recipes to 100% instead of 70/60%
ALT_RP_100 = 0

#Messages
default   = "<html><body>You are either not carrying out your quest or don't meet the criteria.</body></html>"

#NPCs
OLTLIN = 30862

#Mobs
MOBS = [ 20672,20673 ]

class Quest (JQuest) :

 def __init__(self,id,name,descr): JQuest.__init__(self,id,name,descr)

 def onEvent (self,event,st) :
    htmltext = event
    if event == "30862-5.htm" :
       st.setState(STARTED)
       st.set("cond","1")
       st.playSound("ItemSound.quest_accept")
    elif event == "30862-6.htm" :
       st.exitQuest(1)
    elif event == "30862-7.htm" :
       if st.getQuestItemsCount(SN_SCALE) >= REQUIRED :
          st.takeItems(SN_SCALE,REQUIRED)
          item=REWARDS[st.getRandom(len(REWARDS))]
          if ALT_RP_100: item +=1
          st.giveItems(item ,1)
          st.exitQuest(1)
          st.playSound("ItemSound.quest_finish")
       else :
          htmltext = "30862-4.htm"
    return htmltext

 def onTalk (self,npc,player):
   htmltext = default
   st = player.getQuestState(qn)
   if not st : return htmltext

   npcId = npc.getNpcId()
   id = st.getState()
   if id == CREATED :
      st.set("cond","0")
      if player.getLevel() < 63 :
         st.exitQuest(1)
         htmltext = "30862-1.htm"
      else :
         htmltext = "30862-2.htm"
   elif id == STARTED :
      if st.getQuestItemsCount(SN_SCALE) >= REQUIRED :
         htmltext = "30862-3.htm"
      else :
         htmltext = "30862-4.htm"
   return htmltext

 def onKill(self,npc,player,isPet):
     st = player.getQuestState(qn)
     if not st : return 
     if st.getState() != STARTED : return 
   
     count = st.getQuestItemsCount(SN_SCALE)
     numItems, chance = divmod(DROP_RATE,100)
     if st.getRandom(100) < chance :
        numItems += 1
     if numItems != 0 :
        if count + numItems >= REQUIRED :
           numItems = REQUIRED - count
           if numItems != 0 :
              st.playSound("ItemSound.quest_middle")
              st.set("cond","2")
        else :
           st.playSound("ItemSound.quest_itemget")
        st.giveItems(SN_SCALE,int(numItems))   
     return

# Quest class and state definition
QUEST       = Quest(QUEST_NUMBER, str(QUEST_NUMBER)+"_"+QUEST_NAME, QUEST_DESCRIPTION)

CREATED     = State('Start',     QUEST)
STARTED     = State('Started',   QUEST)
COMPLETED   = State('Completed', QUEST)

QUEST.setInitialState(CREATED)

# Quest NPC starter initialization
QUEST.addStartNpc(OLTLIN)
# Quest initialization
QUEST.addTalkId(OLTLIN)

for i in MOBS :
  QUEST.addKillId(i)