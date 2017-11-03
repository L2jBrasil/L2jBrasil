# Made by Kerb
import sys
from com.it.br.gameserver.model.quest import State
from com.it.br.gameserver.model.quest import QuestState
from com.it.br.gameserver.model.quest.jython import QuestJython as JQuest
from com.it.br.gameserver.model.actor.instance import L2NpcInstance
from com.it.br.gameserver.datatables.sql import SpawnTable

qn = "652_AnAgedExAdventurer"
#Npc
TANTAN = 32012
SARA = 30180

#Items
CSS = 1464

class Quest (JQuest) :

 def __init__(self,id,name,descr): JQuest.__init__(self,id,name,descr)

 def onAdvEvent (self,event,npc,player) :
    st = player.getQuestState(qn)
    if not st: return
    htmltext = event
    if event == "32012-02.htm" :
      if st.getQuestItemsCount(CSS) > 99 :
        st.set("cond","1")
        st.setState(STARTED)
        st.playSound("ItemSound.quest_accept")
        st.takeItems(CSS,100)
        htmltext = "32012-03.htm"
        npc.deleteMe()
    elif event == "32012-02a.htm" :
        st.exitQuest(1)
        st.playSound("ItemSound.quest_giveup")
    return htmltext

 def onTalk (Self,npc,player):
   st = player.getQuestState(qn)
   htmltext = "<html><body>You are either not carrying out your quest or don't meet the criteria.</body></html>"
   if not st : return htmltext
   npcId = npc.getNpcId()
   id = st.getState()
   cond=st.getInt("cond")
   if npcId == TANTAN and id == CREATED:
       if st.getPlayer().getLevel() >= 46 :
           htmltext = "32012-01.htm"
       else:
           htmltext = "32012-00.htm"
           st.exitQuest(1)
   elif npcId == SARA and st.getInt("cond")==1 :
       htmltext = "30180-01.htm"
       EAD_CHANCE = st.getRandom(100)
       st.giveItems(57,5026)
       if EAD_CHANCE <= 50:
          st.giveItems(956,int(1*Config.RATE_QUESTS_REWARD))
       st.playSound("ItemSound.quest_finish")
       st.exitQuest(1)
   return htmltext

QUEST       = Quest(652,qn,"AnAgedExAdventurer")
CREATED     = State('Start', QUEST)
STARTED     = State('Started', QUEST)

QUEST.setInitialState(CREATED)
QUEST.addStartNpc(TANTAN)

QUEST.addTalkId(TANTAN)
QUEST.addTalkId(SARA)