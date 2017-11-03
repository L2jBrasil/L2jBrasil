# Made by Mr. - Version 0.2
import sys
from com.it.br.gameserver.model.quest import State
from com.it.br.gameserver.model.quest import QuestState
from com.it.br.gameserver.model.quest.jython import QuestJython as JQuest

qn = "152_ShardsOfGolem"

HARRYS_RECEIPT1_ID = 1008
HARRYS_RECEIPT2_ID = 1009
GOLEM_SHARD_ID = 1010
TOOL_BOX_ID = 1011
WOODEN_BP_ID = 23
#NPC
HARRIS=30035
ALTRAN=30283
class Quest (JQuest) :

 def __init__(self,id,name,descr): JQuest.__init__(self,id,name,descr)

 def onEvent (self,event,st) :
    htmltext = event
    id = st.getState()
    cond = st.getInt("cond")
    if id != COMPLETED :
       if event == "30035-04.htm" and cond == 0 :
          st.set("cond","1")
          st.setState(STARTED)
          st.playSound("ItemSound.quest_accept")
          st.giveItems(HARRYS_RECEIPT1_ID,1)
       elif event == "30283-02.htm" and cond == 1 and st.getQuestItemsCount(HARRYS_RECEIPT1_ID) :
          st.takeItems(HARRYS_RECEIPT1_ID,-1)
          st.giveItems(HARRYS_RECEIPT2_ID,1)
          st.set("cond","2")
    return htmltext

 def onTalk (self,npc,player):
   htmltext = "<html><body>You are either not carrying out your quest or don't meet the criteria.</body></html>"
   st = player.getQuestState(qn)
   if not st : return htmltext

   npcId = npc.getNpcId()
   id = st.getState()
   cond = st.getInt("cond")
   receipt1 = st.getQuestItemsCount(HARRYS_RECEIPT1_ID)
   receipt2 = st.getQuestItemsCount(HARRYS_RECEIPT2_ID)
   toolbox = st.getQuestItemsCount(TOOL_BOX_ID)
   shards = st.getQuestItemsCount(GOLEM_SHARD_ID)
   if id == COMPLETED :
      htmltext = "<html><body>This quest has already been completed.</body></html>"
   elif npcId == HARRIS :
      if cond == 0 :
         if player.getLevel() >= 10 :
            htmltext = "30035-03.htm"
         else:
            htmltext = "30035-02.htm"
            st.exitQuest(1)
      elif cond == 1 and receipt1 and not toolbox :
        htmltext = "30035-05.htm"
      elif cond == 3 and toolbox :
        st.takeItems(TOOL_BOX_ID,-1)
        st.takeItems(HARRYS_RECEIPT2_ID,-1)
        st.unset("cond")
        st.setState(COMPLETED)
        st.playSound("ItemSound.quest_finish")
        st.giveItems(WOODEN_BP_ID,1)
        st.addExpAndSp(5000,0)
        htmltext = "30035-06.htm"
   elif npcId == ALTRAN and id == STARTED:
      if cond == 1 and receipt1 :
        htmltext = "30283-01.htm"
      elif cond == 2 and receipt2 and shards < 5 and not toolbox :
        htmltext = "30283-03.htm"
      elif cond == 3 and receipt2 and shards >= 5 and not toolbox :
        st.takeItems(GOLEM_SHARD_ID,-1)
        st.giveItems(TOOL_BOX_ID,1)
        htmltext = "30283-04.htm"
      elif cond == 3 and receipt2 and toolbox :
        htmltext = "30283-05.htm"
   return htmltext

 def onKill(self,npc,player,isPet):
   st = player.getQuestState(qn)
   if not st : return
   if st.getState() != STARTED : return

   count=st.getQuestItemsCount(GOLEM_SHARD_ID)
   if st.getInt("cond")==2 and st.getRandom(100) < 30 and count < 5 :
      st.giveItems(GOLEM_SHARD_ID,1)
      if count == 4 :
         st.playSound("ItemSound.quest_middle")
         st.set("cond","3")
      else :
         st.playSound("ItemSound.quest_itemget")
   return

QUEST       = Quest(152,qn,"Shards Of Golem")
CREATED     = State('Start', QUEST)
STARTING    = State('Starting', QUEST)
STARTED     = State('Started', QUEST)
COMPLETED   = State('Completed', QUEST)


QUEST.setInitialState(CREATED)
QUEST.addStartNpc(HARRIS)

QUEST.addTalkId(HARRIS)

QUEST.addTalkId(ALTRAN)

QUEST.addKillId(20016)