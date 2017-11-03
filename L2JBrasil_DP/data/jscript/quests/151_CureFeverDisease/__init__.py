# Made by Mr. - Version 0.3 by DrLecter
import sys
from com.it.br.gameserver.model.quest import State
from com.it.br.gameserver.model.quest import QuestState
from com.it.br.gameserver.model.quest.jython import QuestJython as JQuest

qn = "151_CureFeverDisease"

POISON_SAC = 703
FEVER_MEDICINE = 704

class Quest (JQuest) :

 def __init__(self,id,name,descr): JQuest.__init__(self,id,name,descr)

 def onEvent (self,event,st) :
    htmltext = event
    if event == "30050-03.htm" :
      st.set("cond","1")
      st.setState(STARTED)
      st.playSound("ItemSound.quest_accept")
    return htmltext

 def onTalk (self,npc,player):
   htmltext = "<html><body>You are either not carrying out your quest or don't meet the criteria.</body></html>"
   st = player.getQuestState(qn)
   if not st : return htmltext

   npcId = npc.getNpcId()
   id = st.getState()
   cond = st.getInt("cond")
   sac = st.getQuestItemsCount(POISON_SAC)
   med = st.getQuestItemsCount(FEVER_MEDICINE)
   if npcId == 30050 :
      if id == COMPLETED :
        htmltext = "<html><body>This quest has already been completed.</body></html>"
      elif cond == 0 :
        if player.getLevel() >= 15 :
          htmltext = "30050-02.htm"
        else:
          htmltext = "30050-01.htm"
          st.exitQuest(1)
      elif cond == 1 and (sac == med == 0) :
        htmltext = "30050-04.htm"
      elif cond == 2 or sac :
        htmltext = "30050-05.htm"
      elif cond == 3 or med :
        st.giveItems(102,1)
        st.takeItems(FEVER_MEDICINE,1)
        htmltext = "30050-06.htm"
        st.unset("cond")
        st.setState(COMPLETED)
        st.playSound("ItemSound.quest_finish")
   elif npcId == 30032 :
      if cond == 2 or sac :
        st.set("cond","3")
        st.takeItems(POISON_SAC,1)
        st.giveItems(FEVER_MEDICINE,1)
        htmltext = "30032-01.htm"
      elif cond == 3 or med :
        htmltext = "30032-02.htm"
   return htmltext

 def onKill(self,npc,player,isPet):
   st = player.getQuestState(qn)
   if not st : return
   if st.getState() != STARTED: return
   
   if not st.getQuestItemsCount(POISON_SAC) and st.getInt("cond") == 1 :
      if st.getRandom(5) == 0 :
         st.giveItems(POISON_SAC,1)
         st.playSound("ItemSound.quest_middle")
         st.set("cond","2")
   return

QUEST       = Quest(151,qn,"Cure for Fever Disease")
CREATED     = State('Start', QUEST)
STARTING    = State('Starting', QUEST)
STARTED     = State('Started', QUEST)
COMPLETED   = State('Completed', QUEST)

QUEST.setInitialState(CREATED)
QUEST.addStartNpc(30050)
QUEST.addTalkId(30050)

QUEST.addTalkId(30032)

for mob in [20103,20106,20108] :
   QUEST.addKillId(mob)