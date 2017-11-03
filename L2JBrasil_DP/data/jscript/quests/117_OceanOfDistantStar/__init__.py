#Made by Ethernaly ethernaly@email.it
import sys
from com.it.br.gameserver.model.quest import State
from com.it.br.gameserver.model.quest import QuestState
from com.it.br.gameserver.model.quest.jython import QuestJython as JQuest

qn = "117_OceanOfDistantStar"

#NPC
ABEY = 32053
GHOST = 32055
GHOST_F = 32054
OBI = 32052
BOX = 32076
#QUEST ITEM, CHANCE and REWARD
GREY_STAR  = 8495
ENGRAVED_HAMMER = 8488
CHANCE = 38

class Quest (JQuest) :

  def __init__(self,id,name,descr): JQuest.__init__(self,id,name,descr)
  
  def onEvent(self, event, st):
    htmltext = event
    if event == "1" :
      htmltext = "0a.htm"
      st.set("cond","1")
      st.setState(STARTED)
      st.playSound("ItemSound.quest_accept")
    if event == "2" :#and cond == 1
      htmltext="1a.htm"
      st.set("cond","2")
    if event == "3" :#and cond == 2
      htmltext="2a.htm"
      st.set("cond","3")
    if event == "4" :#and cond == 3
      htmltext="3a.htm"
      st.set("cond","4")
    if event == "5" :#and cond == 4
      htmltext="4a.htm"
      st.set("cond","5")
      st.giveItems(ENGRAVED_HAMMER,1)
    if event == "6" and st.getQuestItemsCount(ENGRAVED_HAMMER) :
      htmltext="5a.htm"
      st.set("cond","6")
    if event == "7" and st.getQuestItemsCount(ENGRAVED_HAMMER) :
      htmltext="6a.htm"
      st.set("cond","7")
    if event == "8" and st.getQuestItemsCount(GREY_STAR) :
      htmltext="7a.htm"
      st.takeItems(GREY_STAR,1)
      st.set("cond","9")
    if event == "9" and st.getQuestItemsCount(ENGRAVED_HAMMER) :
      htmltext="8a.htm"
      st.takeItems(ENGRAVED_HAMMER,1)
      st.set("cond","10")
    if event == "10" :
      htmltext="9b.htm"
      st.addExpAndSp(63591,0)
      st.playSound("ItemSound.quest_finish")
      st.setState(COMPLETED)
    return htmltext

  def onTalk(self, npc, player):
    st = player.getQuestState(qn)
    if not st : return htmltext    
    npcId=npc.getNpcId()
    htmltext="<html><body>You are either not carrying out your quest or don't meet the criteria.</body></html>"
    id = st.getState()
    if id == CREATED and npcId == ABEY :
      if st.getPlayer().getLevel() >= 39 :
        htmltext = "0.htm" #event 1
      else:
        st.exitQuest(1)
        htmltext = "<html><body>This quest can only be taken by characters that have a minimum level of 39. Return when you are more experienced.</body></html>"
    elif id == STARTED :
      cond = int(st.get("cond"))
      if npcId == GHOST :
        if cond == 1 :
          htmltext = "1.htm" #to event 2
        elif cond == 9 and st.getQuestItemsCount(ENGRAVED_HAMMER) :
          htmltext = "8.htm" #to event 9
      if npcId == OBI :
        if cond == 2 :
          htmltext = "2.htm" #to event 3
        elif cond == 6 and st.getQuestItemsCount(ENGRAVED_HAMMER) :
          htmltext = "6.htm" #to event 7
        elif cond == 7 and st.getQuestItemsCount(ENGRAVED_HAMMER) :
          htmltext = "6a.htm" #to event 7
        elif cond == 8 and st.getQuestItemsCount(GREY_STAR) :
          htmltext = "7.htm" #to event 8
      if npcId == ABEY :
        if cond == 3 :
          htmltext = "3.htm" #to event 4
        elif cond == 5 and st.getQuestItemsCount(ENGRAVED_HAMMER) :
          htmltext = "5.htm" #to event 6
        elif cond == 6 and st.getQuestItemsCount(ENGRAVED_HAMMER) :
          htmltext = "5a.htm" #to event 6
      if npcId == BOX and cond == 4 :
            htmltext = "4.htm" #to event 5
      if npcId == GHOST_F and cond == 10 :
            htmltext = "9.htm" #link to 9a.htm so link to event 10
    elif id == COMPLETED:
      htmltext = "<html><body>This quest has already been completed.</body></html>"
    return htmltext

  def onKill(self,npc,player,isPet):
   st = player.getQuestState(qn)
   if st :
     if st.getState() == STARTED :
       count = st.getQuestItemsCount(GREY_STAR)
       if st.getInt("cond") == 7 and count < 1 and st.getRandom(100)<CHANCE :
          st.giveItems(GREY_STAR,1)
          st.playSound("ItemSound.quest_itemget")
          st.set("cond","8")
   return

QUEST=Quest(117,qn,"Ocean Of Distant Star")
CREATED=State('Start', QUEST)
STARTED=State('Started', QUEST)
COMPLETED=State('Completed', QUEST)

QUEST.setInitialState(CREATED)
QUEST.addStartNpc(ABEY)
QUEST.addTalkId (ABEY)

QUEST.addTalkId(GHOST)
QUEST.addTalkId(OBI)
QUEST.addTalkId(BOX)
QUEST.addTalkId(GHOST_F)

for MOBS in [22023,22024]:
  QUEST.addKillId(MOBS)