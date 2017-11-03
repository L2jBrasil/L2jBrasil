# Made by disKret
import sys
from com.it.br.gameserver.model.quest import State
from com.it.br.gameserver.model.quest import QuestState
from com.it.br.gameserver.model.quest.jython import QuestJython as JQuest

qn = "357_WarehouseKeepersAmbition"

#CUSTOM VALUES
DROPRATE=50
REWARD1=900  #This is paid per item
REWARD2=10000  #Extra reward, if > 100

#NPC
SILVA = 30686

#ITEMS
JADE_CRYSTAL = 5867

class Quest (JQuest) :

 def __init__(self,id,name,descr): JQuest.__init__(self,id,name,descr)

 def onEvent (self,event,st) :
   htmltext = event
   if event == "30686-2.htm" :
     st.set("cond","1")
     st.setState(STARTED)
     st.playSound("ItemSound.quest_accept")
   elif event == "30686-7.htm" :
     count = st.getQuestItemsCount(JADE_CRYSTAL)
     if count:
       reward = count * REWARD1
       if count >= 100 :
         reward = reward + REWARD2
       st.takeItems(JADE_CRYSTAL,-1)
       st.giveItems(57,reward)
     else:
       htmltext="30686-4.htm"
   if event == "30686-8.htm" :
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
   jade = st.getQuestItemsCount(JADE_CRYSTAL)
   if cond == 0 :
     if player.getLevel() >= 47 :
       htmltext = "30686-0.htm"
     else:
       htmltext = "30686-0a.htm"
       st.exitQuest(1)
   elif not jade :
       htmltext = "30686-4.htm"
   elif jade :
       htmltext = "30686-6.htm"
   return htmltext

 def onKill(self,npc,player,isPet):
   partyMember = self.getRandomPartyMemberState(player,STARTED)
   if not partyMember: return
   st = partyMember.getQuestState(qn)
   
   chance = st.getRandom(100) 
   if chance < DROPRATE :
     st.giveItems(JADE_CRYSTAL,1)
     st.playSound("ItemSound.quest_itemget")
   return

QUEST       = Quest(357,qn,"Warehouse Keepers Ambition")
CREATED     = State('Start', QUEST)
STARTED     = State('Started', QUEST)

QUEST.setInitialState(CREATED)
QUEST.addStartNpc(SILVA)
QUEST.addTalkId(SILVA)

for MOBS in range(20594,20598) :
  QUEST.addKillId(MOBS)