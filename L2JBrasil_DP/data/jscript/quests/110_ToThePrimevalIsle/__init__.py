#Made by Ethernaly ethernaly@email.it
import sys
from com.it.br import Config
from com.it.br.gameserver.model.quest import State
from com.it.br.gameserver.model.quest import QuestState
from com.it.br.gameserver.model.quest.jython import QuestJython as JQuest

qn = "110_ToThePrimevalIsle"

#NPC
ANTON   = 31338
MARQUEZ = 32113

#QUEST ITEM and REWARD
ANCIENT_BOOK  = 8777
ADENA_ID      = 57

class Quest (JQuest) :

  def __init__(self,id,name,descr): JQuest.__init__(self,id,name,descr)
  
  def onEvent(self, event, st):
    htmltext = event
    if event == "1" :
      htmltext = "1.htm"
      st.set("cond","1")
      st.giveItems(ANCIENT_BOOK,1)
      st.setState(STARTED)
      st.playSound("ItemSound.quest_accept")
    if event == "2" and st.getQuestItemsCount(ANCIENT_BOOK):
      htmltext="3.htm"
      st.playSound("ItemSound.quest_finish")
      st.giveItems(ADENA_ID,169380)
      st.takeItems(ANCIENT_BOOK,-1)
      st.setState(COMPLETED)
      st.exitQuest(0)
    return htmltext

  def onTalk(self, npc, player):

    st = player.getQuestState(qn)
    if not st : return htmltext    
    npcId=npc.getNpcId()
    htmltext="<html><body>You are either not carrying out your quest or don't meet the criteria.</body></html>" 
    id = st.getState()
    if id == CREATED :
      if st.getPlayer().getLevel() >= 75 :
        htmltext = "0.htm"
      else:
        st.exitQuest(1)
        htmltext = "<html><body>This quest can only be taken by characters that have a minimum level of 75. Return when you are more experienced.</body></html>"
    elif id == STARTED:
      cond = int(st.get("cond"))
      if npcId == MARQUEZ :
        if cond == 1 :
          if not st.getQuestItemsCount(ANCIENT_BOOK):
            htmltext = "1a.htm"
          else :
            htmltext = "2.htm"
    elif id == COMPLETED:
      st.exitQuest(0)
      htmltext = "<html><body>This quest have already been completed.</body></html>"
    return htmltext    

QUEST=Quest(110,qn,"To The Primeval Isle")
CREATED=State('Start', QUEST)
STARTED=State('Started', QUEST)
COMPLETED=State('Completed', QUEST)

QUEST.setInitialState(CREATED)
QUEST.addStartNpc(ANTON)
QUEST.addTalkId (ANTON)

QUEST.addTalkId(MARQUEZ)