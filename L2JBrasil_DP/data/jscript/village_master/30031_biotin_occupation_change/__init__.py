#
# Created by DraX on 2005.08.08
#

import sys

from com.it.br.gameserver.model.quest        import State
from com.it.br.gameserver.model.quest        import QuestState
from com.it.br.gameserver.model.quest.jython import QuestJython as JQuest
qn = "30031_biotin_occupation_change"
HIGH_PRIEST_BIOTIN = 30031

class Quest (JQuest) :

 def __init__(self,id,name,descr): JQuest.__init__(self,id,name,descr)

 def onEvent (self,event,st):
   
   htmltext = "No Quest"

   if event == "30031-01.htm":
     htmltext = event

   if event == "30031-02.htm":
     htmltext = event

   if event == "30031-03.htm":
     htmltext = event

   if event == "30031-04.htm":
     htmltext = event

   if event == "30031-05.htm":
     htmltext = event

   return htmltext

 
 def onTalk (Self,npc,player):
   st = player.getQuestState(qn)
   npcId = npc.getNpcId()
   
   Race    = st.getPlayer().getRace()
   ClassId = st.getPlayer().getClassId()
   
   # Humans got accepted
   if npcId == HIGH_PRIEST_BIOTIN and Race in [Race.human]:
     if ClassId in [ClassId.fighter, ClassId.warrior, ClassId.knight, ClassId.rogue]:
       htmltext = "30031-08.htm"
     if ClassId in [ClassId.warlord, ClassId.paladin, ClassId.treasureHunter]:
       htmltext = "30031-08.htm"
     if ClassId in [ClassId.gladiator, ClassId.darkAvenger, ClassId.hawkeye]:
       htmltext = "30031-08.htm"
     if ClassId in [ClassId.wizard, ClassId.cleric]:
       htmltext = "30031-06.htm"
     if ClassId in [ClassId.sorceror, ClassId.necromancer, ClassId.warlock, ClassId.bishop, ClassId.prophet]:
       htmltext = "30031-07.htm"
     else:
       htmltext = "30031-01.htm"

     st.setState(STARTED)
     return htmltext

   # All other Races must be out
   if npcId == HIGH_PRIEST_BIOTIN and Race in [Race.dwarf, Race.darkelf, Race.elf, Race.orc]:
     st.setState(COMPLETED)
     st.exitQuest(1)
     return "30031-08.htm"

QUEST     = Quest(30031,qn,"village_master")
CREATED   = State('Start',     QUEST)
STARTED   = State('Started',   QUEST)
COMPLETED = State('Completed', QUEST)

QUEST.setInitialState(CREATED)

QUEST.addStartNpc(30031)

QUEST.addTalkId(30031)
