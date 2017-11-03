# Collector of Jewels - Version 0.1 by DrLecter
import sys
from com.it.br.gameserver.model.quest import State
from com.it.br.gameserver.model.quest import QuestState
from com.it.br.gameserver.model.quest.jython import QuestJython as JQuest

qn = "369_CollectorOfJewels"

#NPC
NELL=30376
#Items
FLARE_SHARD=5882
FREEZING_SHARD=5883
ADENA=57
#MOBS & DROP
DROPLIST_FREEZE={20747:[FREEZING_SHARD,85], #Roxide
          20619:[FREEZING_SHARD,73], #Rowin Undine
          20616:[FREEZING_SHARD,60], #Undine Lakin
          }       
DROPLIST_FLARE={20612:[FLARE_SHARD,77],    #Salamander Rowin
          20609:[FLARE_SHARD,77],    #Salamander Lakin
          20749:[FLARE_SHARD,85]     #Death Fire
          }

class Quest (JQuest) :

 def __init__(self,id,name,descr): JQuest.__init__(self,id,name,descr)

 def onEvent (self,event,st) :
   htmltext = event
   cond = st.getInt("cond")
   if event == "30376-03.htm" and cond == 0 :
     st.set("cond","1")
     st.setState(STARTED)
     st.set("awaitsFreezing","1")
     st.set("awaitsFlare","1")
     st.playSound("ItemSound.quest_accept")
   elif event == "30376-07.htm" :
     st.playSound("ItemSound.quest_itemget")
   elif event == "30376-08.htm" :
     st.exitQuest(1)
     st.playSound("ItemSound.quest_finish")
   return htmltext

 def onTalk (self,npc,player):
   htmltext = "<html><body>You are either not carrying out your quest or don't meet the criteria.</body></html>"
   st = player.getQuestState(qn)
   if not st : return htmltext

   npcId = npc.getNpcId()
   id = st.getState()
   cond=st.getInt("cond")
   flare = st.getQuestItemsCount(FLARE_SHARD)
   freezing = st.getQuestItemsCount(FREEZING_SHARD)
   if cond == 0 :
     if player.getLevel() >= 25 :
       htmltext = "30376-02.htm"
     else:
       htmltext = "30376-01.htm"
       st.exitQuest(1)
   elif cond == 1 :
     htmltext = "30376-04.htm"
   elif cond == 2 and flare == freezing == 50 :
     st.set("cond","3")
     st.set("awaitsFreezing","1")
     st.set("awaitsFlare","1")
     st.giveItems(ADENA,12500)
     st.takeItems(FLARE_SHARD,-1)
     st.takeItems(FREEZING_SHARD,-1)
     htmltext = "30376-05.htm"
   elif cond == 3 :
     htmltext = "30376-09.htm"
   elif cond == 4 and flare == freezing == 200 :
     htmltext = "30376-10.htm"
     st.playSound("ItemSound.quest_finish")
     st.giveItems(ADENA,63500)
     st.takeItems(FLARE_SHARD,-1)
     st.takeItems(FREEZING_SHARD,-1)
     st.exitQuest(1)
   return htmltext

 def onKill(self,npc,player,isPet):
   partyMember, st, item, chance = 0,0,0,0
   npcId = npc.getNpcId()
   # get a random party member that still awaits drop from this NPC
   if npcId in DROPLIST_FREEZE.keys() :
       partyMember = self.getRandomPartyMember(player,"awaitsFreezing","1")
       item,chance=DROPLIST_FREEZE[npc.getNpcId()]
   elif npcId in DROPLIST_FLARE.keys() :
       partyMember = self.getRandomPartyMember(player,"awaitsFlare","1")
       item,chance=DROPLIST_FLARE[npc.getNpcId()]

   if partyMember :
       st = partyMember.getQuestState(qn)
   if not st: return
   if st.getState() != STARTED : return 
   
   cond = st.getInt("cond")
   if cond in [1,3] :      
      if cond == 1 :
        max = 50
      elif cond == 3 :
        max = 200
      if st.getRandom(100) < chance and st.getQuestItemsCount(item) < max :
         st.giveItems(item,1)
         # if collection of this item is completed, mark it (so that this person
         # no longer participate in the party-quest pool for this item)
         if st.getQuestItemsCount(FLARE_SHARD) == max :
             st.unset("awaitsFlare")  
         elif  st.getQuestItemsCount(FREEZING_SHARD) == max :
             st.unset("awaitsFreezing")
             
         if st.getQuestItemsCount(FLARE_SHARD) == st.getQuestItemsCount(FREEZING_SHARD) == max :
            st.set("cond",str(cond+1))
            st.playSound("ItemSound.quest_middle")
         else :
            st.playSound("ItemSound.quest_itemget")
   return

QUEST       = Quest(369,qn,"Collector of Jewels")
CREATED     = State('Start', QUEST)
STARTED     = State('Started', QUEST)

QUEST.setInitialState(CREATED)
QUEST.addStartNpc(NELL)

QUEST.addTalkId(NELL)

for mob in DROPLIST_FREEZE.keys() :
    QUEST.addKillId(mob)
for mob in DROPLIST_FLARE.keys() :
    QUEST.addKillId(mob)