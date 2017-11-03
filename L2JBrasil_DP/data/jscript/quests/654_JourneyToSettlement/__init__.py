import sys
from com.it.br import Config
from com.it.br.gameserver.model.quest import State
from com.it.br.gameserver.model.quest import QuestState
from com.it.br.gameserver.model.quest.jython import QuestJython as JQuest

qn = "654_JourneyToSettlement"

#NPCs
NAM_SPIRIT = 31453

#MOBs
MOBS = [21294,21295]

#Items
ANTILOPE_SKIN = 8072
FRINT_SCROLL = 8073

DROP_CHANCE = 5

class Quest (JQuest) :
 def __init__(self,id,name,descr):
    JQuest.__init__(self,id,name,descr)

 def onEvent (self,event,st) :
    htmltext = event
    if event == "problema.htm" :
      st.setState(STARTED)
      st.playSound("ItemSound.quest_accept")
      st.set("cond","1")
    if event == "ok.htm" :
      st.set("cond","2")
    elif event == "prines.htm" :
      st.playSound("ItemSound.quest_finish")
      st.takeItems(ANTILOPE_SKIN,1)
      st.giveItems(FRINT_SCROLL,1)
      st.unset("cond")
      st.setState(COMPLETED)  
    return htmltext

 def onTalk (self,npc,player):
    st = player.getQuestState(qn)
    htmltext = "<html><body>You are either not on a quest that involves this NPC, or you don't meet this NPC's minimum quest requirements.</body></html>" 
    if not st: return htmltext
    npcId = npc.getNpcId()
    id = st.getState()
    cond = st.getInt("cond")
    if npcId == NAM_SPIRIT :
      if id == CREATED :
        st2 = player.getQuestState("119_LastImperialPrince")
        if not st2 == None :
          if st2.getState().getName() == 'Completed' :
            if player.getLevel() >= 74 :
              htmltext = "privetstvie.htm"
            else :
              htmltext = "lvl.htm"
              st.exitQuest(1)
          else :
            htmltext = "no.htm"
            st.exitQuest(1)
      elif cond == 1:
        htmltext = "problema.htm"
      elif cond == 2:
        htmltext = "oleni.htm"
      elif cond == 3:
        htmltext = "vernulsa.htm"
    return htmltext

 def onKill (self, npc, player,isPet):
    st = player.getQuestState(qn)
    if not st : return
    cond = st.getInt("cond")
    npcId = npc.getNpcId()
    if cond == 2:
      chance = DROP_CHANCE*Config.RATE_DROP_QUEST
      random = st.getRandom(100)
      if npcId in MOBS :
        if random <= chance:
          st.giveItems(ANTILOPE_SKIN,1)
          st.playSound("ItemSound.quest_middle")
          st.set("cond","3")
    return


QUEST = Quest(654,qn,"Journey To Settlement")
CREATED     = State('Start',     QUEST)
STARTED     = State('Started',   QUEST)
COMPLETED   = State('Completed', QUEST)

QUEST.setInitialState(CREATED)

QUEST.addStartNpc(NAM_SPIRIT)
QUEST.addTalkId(NAM_SPIRIT)

for npcId in MOBS:
   QUEST.addKillId(npcId)