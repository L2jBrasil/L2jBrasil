#Made by Kerb
import sys

from com.it.br import Config
from com.it.br.gameserver.model.quest import State
from com.it.br.gameserver.model.quest import QuestState
from com.it.br.gameserver.model.quest.jython import QuestJython as JQuest 

qn = "644_GraveRobberAnnihilation" 

#Drop rate
DROP_CHANCE = 75
#Npc
KARUDA = 32017
#Items
ORC_GOODS = 8088
#Rewards
REWARDS = {
    "1" : [1865 , 30], #Varnish
    "2" : [1867 , 40], #Animal Skin
    "3" : [1872 , 40], #Animal Bone
    "4" : [1871 , 30], #Charcoal
    "5" : [1870 , 30], #Coal
    "6" : [1869 , 30], #Iron Ore
    }
#Mobs
MOBS = [ 22003,22004,22005,22006,22008 ]

class Quest (JQuest) :

 def onEvent (self,event,st) :
   cond = st.getInt("cond")
   htmltext = event
   if event == "32017-03.htm" :
      if st.getPlayer().getLevel() < 20 : 
         htmltext = "32017-02.htm"
         st.exitQuest(1)
      else :
         st.set("cond","1")
         st.setState(STARTED)
         st.playSound("ItemSound.quest_accept")
   elif event in REWARDS.keys() :
       item, amount = REWARDS[event]
       st.takeItems(ORC_GOODS,-1)
       st.giveItems(item, amount)
       st.playSound("ItemSound.quest_finish")
       st.exitQuest(1)
       return
   return htmltext

 def onTalk (self,npc,player):
   htmltext = "<html><body>You are either not carrying out your quest or don't meet the criteria.</body></html>"
   st = player.getQuestState(qn)
   if st :
     npcId = npc.getNpcId()
     id = st.getState()
     cond = st.getInt("cond")
     if cond == 0 :
         htmltext = "32017-01.htm"
     elif cond == 1 :
         htmltext = "32017-04.htm"
     elif cond == 2 :
         if st.getQuestItemsCount(ORC_GOODS) >= 120 :
             htmltext = "32017-05.htm"
         else :
             htmltext = "32017-04.htm"
   return htmltext

 def onKill(self,npc,player,isPet):
   partyMember = self.getRandomPartyMember(player,"1")
   if not partyMember: return
   st = partyMember.getQuestState(qn)
   if st :
      if st.getState() == STARTED :
         count = st.getQuestItemsCount(ORC_GOODS)
         if st.getInt("cond") == 1 and count < 120 :
            chance = DROP_CHANCE * Config.RATE_DROP_QUEST
            numItems, chance = divmod(chance,100)
            if st.getRandom(100) < chance : 
               numItems += 1
            if numItems :
               if count + numItems >= 120 :
                  numItems = 120 - count
                  st.playSound("ItemSound.quest_middle")
                  st.set("cond","2")
               else:
                  st.playSound("ItemSound.quest_itemget")   
               st.giveItems(ORC_GOODS,int(numItems))       
   return


QUEST       = Quest(644, qn, "Grave Robber Annihilation")
CREATED     = State('Start', QUEST)
STARTED     = State('Started', QUEST)

QUEST.setInitialState(CREATED)
QUEST.addStartNpc(KARUDA)
QUEST.addTalkId(KARUDA) 

for i in MOBS :
  QUEST.addKillId(i)