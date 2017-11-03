#Made by Kerb
import sys

from com.it.br import Config
from com.it.br.gameserver.model.quest import State
from com.it.br.gameserver.model.quest import QuestState
from com.it.br.gameserver.model.quest.jython import QuestJython as JQuest 

qn = "645_GhostsOfBatur" 

#Drop rate
DROP_CHANCE = 75
#Npc
KARUDA = 32017
#Items
GRAVE_GOODS = 8089
#Rewards
REWARDS={
    "BDH":[1878,18],
    "CKS":[1879, 7],
    "STL":[1880, 4],
    "CBP":[1881, 6],
    "LTR":[1882,10],
    "STM":[1883, 2]
    }
#Mobs
MOBS = [ 22007,22009,22010,22011,22012,22013,22014,22015,22016 ]

class Quest (JQuest) :

 def onEvent (self,event,st) :
   htmltext = event
   if event == "32017-03.htm" :
      if st.getPlayer().getLevel() < 23 : 
         htmltext = "32017-02.htm"
         st.exitQuest(1)
      else :
         st.set("cond","1")
         st.setState(STARTED)
         st.playSound("ItemSound.quest_accept")
   elif event in REWARDS.keys() :
      if st.getQuestItemsCount(GRAVE_GOODS) == 180 :
         item,qty = REWARDS[event]
         st.takeItems(GRAVE_GOODS,-1)
         st.giveItems(item,int(qty*Config.RATE_QUESTS_REWARD))
         st.playSound("ItemSound.quest_finish")
         st.exitQuest(1)
         htmltext = "32017-07.htm"
      else :
         htmltext = "32017-04.htm"
   return htmltext

 def onTalk (self,npc,player):
   htmltext = "<html><body>You are either not carrying out your quest or don't meet the criteria.</body></html>"
   st = player.getQuestState(qn)
   if not st : return htmltext
   npcId = npc.getNpcId()
   id = st.getState()
   cond = st.getInt("cond")
   if cond == 0 :
      htmltext = "32017-01.htm"
   elif cond == 1 :
      htmltext = "32017-04.htm"
   elif cond == 2 :
      if st.getQuestItemsCount(GRAVE_GOODS) == 180 : 
         htmltext = "32017-05.htm"
      else :
         htmltext = "32017-01.htm"
   return htmltext

 def onKill(self,npc,player,isPet):
  partyMember = self.getRandomPartyMember(player,"1")
  if not partyMember: return
  st = partyMember.getQuestState(qn)
  if st :
    count = st.getQuestItemsCount(GRAVE_GOODS)
    if st.getInt("cond") == 1 and count < 180 :
      chance = DROP_CHANCE * Config.RATE_DROP_QUEST
      numItems, chance = divmod(chance,100)
      if st.getRandom(100) < chance : 
         numItems += 1
      if numItems :
         if count + numItems >= 180 :
            numItems = 180 - count
            st.playSound("ItemSound.quest_middle")
            st.set("cond","2")
         else:
            st.playSound("ItemSound.quest_itemget")   
         st.giveItems(GRAVE_GOODS,int(numItems))       
  return


QUEST       = Quest(645, qn, "Ghosts of Batur")
CREATED     = State('Start', QUEST)
STARTED     = State('Started', QUEST)

QUEST.setInitialState(CREATED)
QUEST.addStartNpc(KARUDA)
QUEST.addTalkId(KARUDA) 

for i in MOBS :
  QUEST.addKillId(i)