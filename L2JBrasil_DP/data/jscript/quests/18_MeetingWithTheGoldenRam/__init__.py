# Contributed by t0rm3nt0r to the Official L2J Datapack Project.
# With some minor cleanup by DrLecter.
# Visit http://forum.l2jdp.com for more details.

import sys
from com.it.br.gameserver.model.quest import State
from com.it.br.gameserver.model.quest import QuestState
from com.it.br.gameserver.model.quest.jython import QuestJython as JQuest

qn = "18_MeetingWithTheGoldenRam"

DONAL = 31314
DAISY = 31315
ABERCROMBIE = 31555
BOX = 7245

class Quest (JQuest) :

 def __init__(self,id,name,descr): JQuest.__init__(self,id,name,descr)
 
 def onEvent (self,event,st) :
     htmltext = event
     if event == "31314-03.htm" :
       if st.getPlayer().getLevel() >= 66 :
         st.set("cond","1")
         st.setState(STARTED)
         st.playSound("ItemSound.quest_accept")
       else :
         htmltext = "31314-02.htm"
         st.exitQuest(1)
     elif event == "31315-02.htm" :
       st.set("cond","2")
       htmltext = "31315-02.htm"
       st.giveItems(BOX,1)
     elif event == "31555-02.htm" :
       st.giveItems(57,15000)
       st.takeItems(BOX,-1)
       st.addExpAndSp(50000,0)
       st.unset("cond")
       st.playSound("ItemSound.quest_finish")
       st.setState(COMPLETED)
     return htmltext

 def onTalk (self,npc,player):
     npcId = npc.getNpcId()
     htmltext = "<html><body>You are either not carrying out your quest or don't meet the criteria.</body></html>"
     st = player.getQuestState(qn)
     if not st : return htmltext
     id = st.getState()
     cond = st.getInt("cond")
     if id == COMPLETED :
       htmltext = htmltext = "<html><body>This quest has already been completed.</body></html>"
     elif id == CREATED and npcId == DONAL :
       htmltext = "31314-01.htm"
     elif id == STARTED :
       if npcId == DONAL : 
         htmltext = "31314-04.htm"
       elif npcId == DAISY :
         if cond < 2 :
           htmltext = "31315-01.htm"
         else :
           htmltext = "31315-03.htm"
       elif npcId == ABERCROMBIE and cond == 2 and st.getQuestItemsCount(BOX):
           htmltext = "31555-01.htm"
     return htmltext

QUEST       = Quest(18, qn, "Meeting With The Golden Ram")
CREATED     = State('Start', QUEST)
STARTED     = State('Started', QUEST)
COMPLETED   = State('Completed', QUEST)

QUEST.setInitialState(CREATED)
QUEST.addStartNpc(DONAL)

QUEST.addTalkId(DONAL)
QUEST.addTalkId(DAISY)
QUEST.addTalkId(ABERCROMBIE)