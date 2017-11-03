# Made by Mr. - Version 0.3 by DrLecter
import sys
from com.it.br.gameserver.model.quest import State
from com.it.br.gameserver.model.quest import QuestState
from com.it.br.gameserver.model.quest.jython import QuestJython as JQuest

qn = "306_CrystalOfFireice"

FLAME_SHARD = 1020
ICE_SHARD = 1021
ADENA = 57

DROPLIST={
20109:[30,FLAME_SHARD],
20110:[30,ICE_SHARD],
20112:[40,FLAME_SHARD],
20113:[40,ICE_SHARD],
20114:[50,FLAME_SHARD],
20115:[50,ICE_SHARD]
}

class Quest (JQuest) :

 def __init__(self,id,name,descr): JQuest.__init__(self,id,name,descr)

 def onEvent (self,event,st) :
    htmltext = event
    if event == "30004-04.htm" :
      st.set("cond","1")
      st.setState(STARTED)
      st.playSound("ItemSound.quest_accept")
    elif event == "30004-08.htm" :
      st.exitQuest(1)
      st.playSound("ItemSound.quest_finish")
    return htmltext

 def onTalk (self,npc,player):
   htmltext = "<html><body>You are either not carrying out your quest or don't meet the criteria.</body></html>"
   st = player.getQuestState(qn)
   if not st : return htmltext

   npcId = npc.getNpcId()
   id = st.getState()
   if id == CREATED :
     st.set("cond","0")
   if st.getInt("cond")==0 :
     if player.getLevel() >= 17 :
       htmltext = "30004-03.htm"
     else:
       htmltext = "30004-02.htm"
       st.exitQuest(1)
   else :
     flame=st.getQuestItemsCount(FLAME_SHARD)
     ice=st.getQuestItemsCount(ICE_SHARD)
     if flame==ice==0 :
       htmltext = "30004-05.htm"
     else :
       if flame+ice > 9 :
          st.giveItems(ADENA,5000+30*(flame+ice))
       else :
          st.giveItems(ADENA,30*(flame+ice))
       st.takeItems(FLAME_SHARD,-1)
       st.takeItems(ICE_SHARD,-1)
       htmltext = "30004-07.htm"
   return htmltext

 def onKill(self,npc,player,isPet):
   st = player.getQuestState(qn)
   if not st : return 
   if st.getState() != STARTED : return 
   
   npcId = npc.getNpcId()
   chance,item=DROPLIST[npcId]
   if st.getRandom(100)<chance :
     st.giveItems(item,1)
     st.playSound("ItemSound.quest_itemget")
   return

QUEST       = Quest(306,qn,"Crystal Of Fireice")
CREATED     = State('Start', QUEST)
STARTING    = State('Starting', QUEST)
STARTED     = State('Started', QUEST)
COMPLETED   = State('Completed', QUEST)

QUEST.setInitialState(CREATED)
QUEST.addStartNpc(30004)

QUEST.addTalkId(30004)

QUEST.addKillId(20109)
QUEST.addKillId(20110)
QUEST.addKillId(20112)
QUEST.addKillId(20113)
QUEST.addKillId(20114)
QUEST.addKillId(20115)