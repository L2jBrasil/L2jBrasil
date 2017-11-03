# Electrifying Recharge! - v0.1 by DrLecter
import sys
from com.it.br.gameserver.datatables.sql import SkillTable
from com.it.br.gameserver.model.quest import State
from com.it.br.gameserver.model.quest import QuestState
from com.it.br.gameserver.model.quest.jython import QuestJython as JQuest

qn = "367_ElectrifyingRecharge"

#NPC
LORAIN = 30673
#MOBS
CATHEROK=21035

class Quest (JQuest) :

 def __init__(self,id,name,descr): JQuest.__init__(self,id,name,descr)

 def onEvent (self,event,st) :
   htmltext = event
   cond = st.getInt("cond")
   lamp = st.getQuestItemsCount(5875)
   if event == "30673-03.htm" and cond == 0 and not lamp:
     if st.getPlayer().getLevel() >= 37 :
        st.set("cond","1")
        st.setState(STARTED)
        st.playSound("ItemSound.quest_accept")
        st.giveItems(5875,1)
     else :
        htmltext = "30673-02.htm"
        st.exitQuest(1)
   elif event == "30673-08.htm" :
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
   relic=st.getQuestItemsCount(5879)
   broken=st.getQuestItemsCount(5880)
   if cond == 0 :
      htmltext = "30673-01.htm"
   elif cond == 1 :
     if not relic and not broken :
        htmltext = "30673-04.htm"
     elif broken :
        htmltext = "30673-05.htm"
        st.takeItems(5880,-1)
        st.giveItems(5875,1)
   elif cond == 2 and relic :
     st.takeItems(5879,-1)
     st.giveItems(4553+st.getRandom(12),1)
     st.giveItems(5875,1)
     st.set("cond","1")
     htmltext = "30673-06.htm"
   return htmltext

 def onAttack (self,npc,player,damage,isPet):
   st = player.getQuestState(qn)
   if not st : return 
   if st.getState() != STARTED : return 
   
   chance=st.getRandom(100)
   if chance < 3 :
      count = 0
      for item in range(5875,5879):
         if st.getQuestItemsCount(item) :
            count += 1
            st.takeItems(item,-1)
      if count:
         st.giveItems(5880,1)
   elif chance < 7 :
      for item in range(5875,5879):
         if st.getQuestItemsCount(item) :
            npc.doCast(SkillTable.getInstance().getInfo(4072,4))
            st.takeItems(item,-1)
            st.giveItems(item+1,1)
            if item < 5878 :
               st.playSound("ItemSound.quest_itemget")
            elif item == 5878 :
               st.playSound("ItemSound.quest_middle")
               st.set("cond","2")
            break
   return

QUEST       = Quest(367,qn,"Electrifying Recharge")
CREATED     = State('Start', QUEST)
STARTED     = State('Started', QUEST)

QUEST.setInitialState(CREATED)
QUEST.addStartNpc(LORAIN)
QUEST.addTalkId(LORAIN)

QUEST.addAttackId(CATHEROK)