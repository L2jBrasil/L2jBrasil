#Made by Ethernaly ethernaly@email.it
import sys
from com.it.br import Config
from com.it.br.gameserver.model.quest import State
from com.it.br.gameserver.model.quest import QuestState
from com.it.br.gameserver.model.quest.jython import QuestJython as JQuest

qn = "121_PavelTheGiants"

#NPCs
NEWYEAR   = 31961
YUMI      = 32041

class Quest (JQuest) :

  def __init__(self,id,name,descr): JQuest.__init__(self,id,name,descr)
  
  def onEvent(self, event, st):
    htmltext = event
    if event == "32041-2.htm" :
      st.playSound("ItemSound.quest_finish")
      st.addExpAndSp(10000,0)
      st.unset("cond")
      st.setState(COMPLETED)
    return htmltext

  def onTalk(self, npc, player):
    htmltext = "<html><body>You are either not carrying out your quest or don't meet the criteria.</body></html>"
    st = player.getQuestState(qn)
    if not st : return htmltext
    npcId=npc.getNpcId()
    id = st.getState()
    cond = st.getInt("cond")
    if id == COMPLETED:
       htmltext = "<html><body>This quest has already been completed.</body></html>"
    elif id == CREATED and npcId == NEWYEAR :
      if player.getLevel() >= 46 :
        htmltext = "31961-1.htm"
        st.set("cond","1")
        st.setState(STARTED)
        st.playSound("ItemSound.quest_accept")
      else:
        htmltext = "31961-1a.htm"
        st.exitQuest(1)
    elif id == STARTED:
      if npcId == YUMI :
        if cond == 1 :
            htmltext = "32041-1.htm"
      else :
        htmltext = "31961-2.htm"
    return htmltext    

QUEST=Quest(121,qn,"Pavel The Giants")
CREATED=State('Start', QUEST)
STARTED=State('Started', QUEST)
COMPLETED=State('Completed', QUEST)

QUEST.setInitialState(CREATED)
QUEST.addStartNpc(NEWYEAR)
QUEST.addTalkId (NEWYEAR)
QUEST.addTalkId(YUMI)