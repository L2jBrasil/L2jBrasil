# Made by disKret
import sys
from com.it.br.gameserver.model.quest import State
from com.it.br.gameserver.model.quest import QuestState
from com.it.br.gameserver.model.quest.jython import QuestJython as JQuest

qn = "365_DevilsLegacy"

#NPC
RANDOLF = 30095
#MOBS
MOBS=[20836,29027,20845,21629,21630,29026]
#CHANCE OF DROP
CHANCE_OF_DROP = 20
#ITEMS
TREASURE_CHEST = 5873

class Quest (JQuest) :

 def __init__(self,id,name,descr): JQuest.__init__(self,id,name,descr)

 def onEvent (self,event,st) :
   htmltext = event
   if event == "30095-1.htm" :
     st.set("cond","1")
     st.setState(STARTED)
     st.playSound("ItemSound.quest_accept")
   elif event == "30095-5.htm" :
     count = st.getQuestItemsCount(TREASURE_CHEST)
     if count :
        reward = (count*1600)
        st.takeItems(TREASURE_CHEST,-1)
        st.giveItems(57,reward)
     else:
        htmltext="You don't have required items"
   elif event == "30095-6.htm" :
     st.playSound("ItemSound.quest_finish")
     st.exitQuest(1)
   return htmltext

 def onTalk (self,npc,player):
   htmltext = "<html><body>You are either not carrying out your quest or don't meet the criteria.</body></html>"
   st = player.getQuestState(qn)
   if not st : return htmltext

   npcId = npc.getNpcId()
   id = st.getState()
   cond=st.getInt("cond")
   if cond == 0 :
     if player.getLevel() >= 39 :
       htmltext = "30095-0.htm"
     else :
       htmltext = "30095-0a.htm"
       st.exitQuest(1)
   elif cond == 1 :
     if not st.getQuestItemsCount(TREASURE_CHEST) :
        htmltext = "30095-2.htm"
     else :
        htmltext = "30095-4.htm"
   return htmltext

 def onKill(self,npc,player,isPet):
   partyMember = self.getRandomPartyMemberState(player,STARTED)
   if not partyMember : return
   st = partyMember.getQuestState(qn)
   
   chance = st.getRandom(100)
   if chance < CHANCE_OF_DROP :
     st.giveItems(TREASURE_CHEST,1)
     st.playSound("ItemSound.quest_itemget")
   return

QUEST       = Quest(365,qn,"Devil's Legacy")
CREATED     = State('Start', QUEST)
STARTED     = State('Started', QUEST)

QUEST.setInitialState(CREATED)
QUEST.addStartNpc(RANDOLF)
QUEST.addTalkId(RANDOLF)
for mob in MOBS:
    QUEST.addKillId(mob)