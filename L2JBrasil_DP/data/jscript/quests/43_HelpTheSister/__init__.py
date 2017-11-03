#quest by zerghase
import sys
from com.it.br.gameserver.model.quest import State
from com.it.br.gameserver.model.quest import QuestState
from com.it.br.gameserver.model.quest.jython import QuestJython as JQuest

qn = "43_HelpTheSister"

COOPER=30829
GALLADUCCI=30097

CRAFTED_DAGGER=220
MAP_PIECE=7550
MAP=7551
PET_TICKET=7584

SPECTER=20171
SORROW_MAIDEN=20197

MAX_COUNT=30
MIN_LEVEL=26

class Quest (JQuest) :
  def onEvent(self, event, st):
    htmltext=event
    if event=="1":
      htmltext="30829-01.htm"
      st.set("cond","1")
      st.setState(STARTED)
      st.playSound("ItemSound.quest_accept")
    elif event=="3" and st.getQuestItemsCount(CRAFTED_DAGGER):
      htmltext="30829-03.htm"
      st.takeItems(CRAFTED_DAGGER,1)
      st.set("cond","2")
    elif event=="4" and st.getQuestItemsCount(MAP_PIECE)>=MAX_COUNT:
      htmltext="30829-05.htm"
      st.takeItems(MAP_PIECE,MAX_COUNT)
      st.giveItems(MAP,1)
      st.set("cond", "4")
    elif event=="5" and st.getQuestItemsCount(MAP):
      htmltext="30097-06.htm"
      st.takeItems(MAP,1)
      st.set("cond","5")
    elif event=="7":
      htmltext="30829-07.htm"
      st.giveItems(PET_TICKET,1)
      st.setState(COMPLETED)
      st.exitQuest(0)
    return htmltext

  def onTalk(self, npc, player):
    htmltext="<html><body>You are either not carrying out your quest or don't meet the criteria.</body></html>"
    st = player.getQuestState(qn)
    if not st : return htmltext

    npcId=npc.getNpcId()
    id=st.getState()
    if id==CREATED:
      if player.getLevel()>=MIN_LEVEL:
        htmltext="30829-00.htm"
      else:
        st.exitQuest(1)
        htmltext="<html><body>This quest can only be taken by characters that have a minimum level of %s. Return when you are more experienced.</body></html>" % MIN_LEVEL
    elif id==STARTED:
      cond=st.getInt("cond")
      if npcId==COOPER:
        if cond==1:
          if not st.getQuestItemsCount(CRAFTED_DAGGER):
            htmltext="30829-01a.htm"
          else:
            htmltext="30829-02.htm"
        elif cond==2:
          htmltext="30829-03a.htm"
        elif cond==3:
            htmltext="30829-04.htm"
        elif cond==4:
          htmltext="30829-05a.htm"
        elif cond==5:
          htmltext="30829-06.htm"
      elif npcId==GALLADUCCI:
        if cond==4 and st.getQuestItemsCount(MAP):
          htmltext="30097-05.htm"
    elif id==COMPLETED:
      st.exitQuest(0)
      htmltext="<html><body>This quest has already been completed.</body></html>"
    return htmltext

  def onKill(self,npc,player,isPet):
    st = player.getQuestState(qn)
    if not st : return 
    if st.getState() != STARTED : return
    
    npcId = npc.getNpcId()
    cond=st.getInt("cond")
    if cond==2:
      numItems,chance = divmod(100*Config.RATE_QUESTS_REWARD,100)
      if st.getRandom(100) < chance :
        numItems = numItems +1  
      pieces=st.getQuestItemsCount(MAP_PIECE)
      if pieces + numItems >= MAX_COUNT :
        numItems = MAX_COUNT - pieces
        if numItems != 0:
          st.playSound("ItemSound.quest_middle")
          st.set("cond", "3")
      else :  
        st.playSound("ItemSound.quest_itemget")
      st.giveItems(MAP_PIECE,int(numItems))
    return     

QUEST=Quest(43,qn,"Help The Sister!")
CREATED=State('Start', QUEST)
STARTED=State('Started', QUEST)
COMPLETED=State('Completed', QUEST)

QUEST.setInitialState(CREATED)
QUEST.addStartNpc(COOPER)

QUEST.addTalkId(COOPER)

QUEST.addTalkId(GALLADUCCI)

QUEST.addKillId(SPECTER)
QUEST.addKillId(SORROW_MAIDEN)