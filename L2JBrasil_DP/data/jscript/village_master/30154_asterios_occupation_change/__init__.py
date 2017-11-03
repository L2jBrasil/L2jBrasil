#
# Created by DraX on 2005.08.08
#

import sys

from com.it.br.gameserver.model.quest        import State
from com.it.br.gameserver.model.quest        import QuestState
from com.it.br.gameserver.model.quest.jython import QuestJython as JQuest
qn = "30154_asterios_occupation_change"
HIERARCH_ASTERIOS = 30154

class Quest (JQuest) :

 def __init__(self,id,name,descr): JQuest.__init__(self,id,name,descr)

 def onEvent (self,event,st):
   
   htmltext = "No Quest"

   if event == "30154-01.htm":
     htmltext = event

   if event == "30154-02.htm":
     htmltext = event

   if event == "30154-03.htm":
     htmltext = event

   if event == "30154-04.htm":
     htmltext = event

   if event == "30154-05.htm":
     htmltext = event
   
   if event == "30154-06.htm":
     htmltext = event

   if event == "30154-07.htm":
     htmltext = event

   if event == "30154-08.htm":
     htmltext = event

   if event == "30154-09.htm":
     htmltext = event

   if event == "30154-10.htm":
     htmltext = event
   return htmltext
 
 def onTalk (Self,npc,player):
   st = player.getQuestState(qn)
   npcId = npc.getNpcId()
   
   Race    = st.getPlayer().getRace()
   ClassId = st.getPlayer().getClassId()
   
   # Elfs got accepted
   if npcId == HIERARCH_ASTERIOS and Race in [Race.elf]:
     if ClassId in [ClassId.elvenFighter]: 
       st.setState(STARTED)
       return "30154-01.htm"
     if ClassId in [ClassId.elvenMage]:
       st.setState(STARTED)
       return "30154-02.htm"
     if ClassId in [ClassId.elvenWizard, ClassId.oracle, ClassId.elvenKnight, ClassId.elvenScout]:
       st.setState(COMPLETED)
       st.exitQuest(1)
       return "30154-12.htm"
     else:
       st.setState(COMPLETED)
       st.exitQuest(1)
       return "30154-13.htm"

   # All other Races must be out
   if npcId == HIERARCH_ASTERIOS and Race in [Race.dwarf, Race.human, Race.darkelf, Race.orc]:
     st.setState(COMPLETED)
     st.exitQuest(1)
     return "30154-11.htm"

QUEST     = Quest(30154,qn,"village_master")
CREATED   = State('Start',     QUEST)
STARTED   = State('Started',   QUEST)
COMPLETED = State('Completed', QUEST)

QUEST.setInitialState(CREATED)

QUEST.addStartNpc(30154)

QUEST.addTalkId(30154)