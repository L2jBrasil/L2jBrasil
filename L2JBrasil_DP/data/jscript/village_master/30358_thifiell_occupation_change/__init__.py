#
# Created by DraX on 2005.08.08
#

import sys

from com.it.br.gameserver.model.quest        import State
from com.it.br.gameserver.model.quest        import QuestState
from com.it.br.gameserver.model.quest.jython import QuestJython as JQuest
qn = "30358_thifiell_occupation_change"
TETRARCH_THIFIELL = 30358

class Quest (JQuest) :

 def __init__(self,id,name,descr): JQuest.__init__(self,id,name,descr)

 def onEvent (self,event,st):
   
   htmltext = event

   if event == "30358-01.htm":
     return "30358-01.htm"

   if event == "30358-02.htm":
     return "30358-02.htm"

   if event == "30358-03.htm":
     return "30358-03.htm"

   if event == "30358-04.htm":
     return "30358-04.htm"

   if event == "30358-05.htm":
     return "30358-05.htm"
   
   if event == "30358-06.htm":
     return "30358-06.htm"

   if event == "30358-07.htm":
     return "30358-07.htm"

   if event == "30358-08.htm":
     return "30358-08.htm"

   if event == "30358-09.htm":
     return "30358-09.htm"

   if event == "30358-10.htm":
     return "30358-10.htm"

 def onTalk (Self,npc,player):
   st = player.getQuestState(qn)
   npcId = npc.getNpcId()
   
   Race    = st.getPlayer().getRace()
   ClassId = st.getPlayer().getClassId()
   
   # DarkElfs got accepted
   if npcId == TETRARCH_THIFIELL and Race in [Race.darkelf]:
     if ClassId in [ClassId.darkFighter]: 
       st.setState(STARTED)
       return "30358-01.htm"
     if ClassId in [ClassId.darkMage]:
       st.setState(STARTED)
       return "30358-02.htm"
     if ClassId in [ClassId.darkWizard, ClassId.shillienOracle, ClassId.palusKnight, ClassId.assassin]:
       st.setState(COMPLETED)
       st.exitQuest(1)
       return "30358-12.htm"
     else:
       st.setState(COMPLETED)
       st.exitQuest(1)
       return "30358-13.htm"

   # All other Races must be out
   if npcId == TETRARCH_THIFIELL and Race in [Race.dwarf, Race.human, Race.elf, Race.orc]:
     st.setState(COMPLETED)
     st.exitQuest(1)
     return "30358-11.htm"

QUEST     = Quest(30358,qn,"village_master")
CREATED   = State('Start',     QUEST)
STARTED   = State('Started',   QUEST)
COMPLETED = State('Completed', QUEST)

QUEST.setInitialState(CREATED)

QUEST.addStartNpc(30358)

QUEST.addTalkId(30358)