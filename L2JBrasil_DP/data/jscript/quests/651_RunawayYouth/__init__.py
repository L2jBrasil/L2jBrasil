# Made by Polo & DrLecter
import sys
from com.it.br.gameserver.model.quest import State
from com.it.br.gameserver.model.quest import QuestState
from com.it.br.gameserver.model.quest.jython import QuestJython as JQuest
from com.it.br.gameserver.network.serverpackets import MagicSkillUser

qn = "651_RunawayYouth"
#Npc
IVAN = 32014
BATIDAE = 31989

#Items
SOE = 736

class Quest (JQuest) :

 def __init__(self,id,name,descr): JQuest.__init__(self,id,name,descr)

 def onAdvEvent (self,event,npc,player) :
    htmltext = event
    st = player.getQuestState(qn)
    if not st : return
    if event == "32014-04.htm" :
      if st.getQuestItemsCount(SOE):
        st.set("cond","1")
        st.setState(STARTED)
        st.playSound("ItemSound.quest_accept")
        st.takeItems(SOE,1)
        htmltext = "32014-03.htm"
        npc.broadcastPacket(MagicSkillUser(npc,npc,2013,1,20000,0))
        st.startQuestTimer("ivan_timer",20000,npc)
    elif event == "32014-04a.htm" :
        st.exitQuest(1)
        st.playSound("ItemSound.quest_giveup")
    elif event == "ivan_timer":
        npc.deleteMe()
        htmltext=None
    return htmltext

 def onTalk (self,npc,player):
   st = player.getQuestState(qn)
   htmltext = "<html><body>You are either not carrying out your quest or don't meet the criteria.</body></html>"
   if not st : return htmltext
   npcId = npc.getNpcId()
   id = st.getState()
   cond=st.getInt("cond")
   if npcId == IVAN and id == CREATED:
      if player.getLevel()>=26 :
         htmltext = "32014-02.htm"
      else:
         htmltext = "32014-01.htm"
         st.exitQuest(1)
   elif npcId == BATIDAE and st.getInt("cond")==1 :
      htmltext = "31989-01.htm"
      st.giveItems(57,2883)
      st.playSound("ItemSound.quest_finish")
      st.exitQuest(1)
   return htmltext


QUEST       = Quest(651,qn,"Runaway Youth")
CREATED     = State('Start', QUEST)
STARTED     = State('Started', QUEST)

QUEST.setInitialState(CREATED)
QUEST.addStartNpc(IVAN)

QUEST.addTalkId(IVAN)
QUEST.addTalkId(BATIDAE)