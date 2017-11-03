# Made by disKret
import sys
from com.it.br.gameserver.model.quest import State
from com.it.br.gameserver.model.quest import QuestState
from com.it.br.gameserver.model.quest.jython import QuestJython as JQuest

qn = "298_LizardmensConspiracy"

PATROLS_REPORT = 7182
SHINING_GEM = 7183
SHINING_RED_GEM = 7184

class Quest (JQuest) :

 def __init__(self,id,name,descr): JQuest.__init__(self,id,name,descr)

 def onEvent (self,event,st) :
   htmltext = event
   if event == "30333-1a.htm" :
     st.set("cond","1")
     st.giveItems(PATROLS_REPORT,1)
     st.setState(STARTED)
     st.set("awaitGem","1")
     st.set("awaitRedGem","1")
     st.playSound("ItemSound.quest_accept")
   if event == "30344-1.htm" :
     st.takeItems(PATROLS_REPORT,1)
     st.set("cond","2")
   if event == "30344-3.htm" :
     if st.getQuestItemsCount(SHINING_RED_GEM) == st.getQuestItemsCount(SHINING_GEM) == 50 :
       st.takeItems(SHINING_GEM,-1)
       st.takeItems(SHINING_RED_GEM,-1)
       st.addExpAndSp(0,42000)
       st.playSound("ItemSound.quest_finish")
       st.exitQuest(1)
     else :
       htmltext = "You don't have required items"
   return htmltext

 def onTalk (self,npc,player):
   htmltext = "<html><body>You are either not carrying out your quest or don't meet the criteria.</body></html>"
   st = player.getQuestState(qn)
   if not st : return htmltext

   npcId = npc.getNpcId()
   id = st.getState()
   if id == CREATED :
     st.set("cond","0")
   cond = st.getInt("cond")
   if npcId == 30333 and cond == 0  :
     if player.getLevel() >= 25 :
       htmltext = "30333-0a.htm"
     else:
       st.exitQuest(1)
   elif npcId == 30344 and id == STARTED:
     if cond == 1 :
       htmltext = "30344-0.htm"
     elif cond == 3 :
       htmltext = "30344-2.htm"
   return htmltext

 def onKill(self,npc,player,isPet):
   npcId = npc.getNpcId()
   if npcId in [20926,20927] :
     partyMember = self.getRandomPartyMember(player,"awaitRedGem","1")
     if not partyMember : return
     st = partyMember.getQuestState(qn)
     count = st.getQuestItemsCount(SHINING_RED_GEM)
     if count == 49 :
         st.unset("awaitRedGem")
     if count < 50 :
       st.giveItems(SHINING_RED_GEM,1)
       if st.getQuestItemsCount(SHINING_GEM) == 50 and count == 49 :
         st.playSound("ItemSound.quest_middle")
         st.set("cond","3")
       else :
         st.playSound("ItemSound.quest_itemget")
   if npcId in [20922,20923,20924] :
     partyMember = self.getRandomPartyMember(player,"awaitGem","1")
     if not partyMember : return
     st = partyMember.getQuestState(qn)
     count = st.getQuestItemsCount(SHINING_GEM)
     if count == 49 :
         st.unset("awaitGem")
     if count < 50 :
       st.giveItems(SHINING_GEM,1)
       if count == 49 and st.getQuestItemsCount(SHINING_RED_GEM) == 50 :
         st.playSound("ItemSound.quest_middle")
         st.set("cond","3")
       else :
         st.playSound("ItemSound.quest_itemget")
   return

QUEST       = Quest(298,qn,"Lizardmen's Conspiracy")
CREATED     = State('Start', QUEST)
STARTED     = State('Started', QUEST)

QUEST.setInitialState(CREATED)
QUEST.addStartNpc(30333)

QUEST.addTalkId(30333)

QUEST.addTalkId(30344)

for i in range(20922,20928) :
    QUEST.addKillId(i)