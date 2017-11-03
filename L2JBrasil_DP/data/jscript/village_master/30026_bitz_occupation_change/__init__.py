# This script is part of the Official L2J Datapack Project
# visit us at http://www.l2jdp.com/
# Created by DraX on 08.08.2005
# Updated by ElgarL on 28.09.2005
# Updated by DrLecter on 19.06.2007

import sys
from com.it.br.gameserver.model.quest        import State
from com.it.br.gameserver.model.quest        import QuestState
from com.it.br.gameserver.model.quest.jython import QuestJython as JQuest

qn="30026_bitz_occupation_change"
GRAND_MASTER_BITZ = 30026

class Quest (JQuest) :

 def __init__(self,id,name,descr): JQuest.__init__(self,id,name,descr)

 def onEvent (self,event,st):
   if event in ["30026-01.htm","30026-02.htm","30026-03.htm","30026-04.htm","30026-05.htm","30026-06.htm","30026-07.htm"]:
     htmltext = event
   else :
     htmltext = "No Quest"
     st.exitQuest(1)
   return htmltext

 def onTalk (Self,npc,player):
   st = player.getQuestState(qn)
   npcId = npc.getNpcId()
   Race  = st.getPlayer().getRace()
   pcId  = st.getPlayer().getClassId().getId()
   # Human fighters get accepted
   if npcId == GRAND_MASTER_BITZ and Race in [Race.human] and pcId in range(0x0a)+range(88,94) :
     #fighter
     if pcId == 0x00:
       htmltext = "30026-01.htm"
     #warrior, knight, rogue
     elif pcId in [1, 4, 7] :
       htmltext = "30026-08.htm"
     #warlord, paladin, treasureHunter, adventurer, hell knight, dreadnought
     elif pcId in [3, 5, 8, 93, 91, 89 ] :
       htmltext = "30026-09.htm"
     #gladiator, darkAvenger, hawkeye,  sagitarius, phoenix knight, duelist
     elif pcId in [2, 6, 9, 92, 90, 88 ]:
       htmltext = "30026-09.htm"
     st.setState(STARTED)
   # All other Races and classes must be out
   else :
     st.exitQuest(1)
     htmltext = "30026-10.htm"
   return htmltext

QUEST     = Quest(30026,qn,"village_master")
CREATED   = State('Start',     QUEST)
STARTED   = State('Started',   QUEST)
COMPLETED = State('Completed', QUEST)

QUEST.setInitialState(CREATED)
QUEST.addStartNpc(GRAND_MASTER_BITZ)
QUEST.addTalkId(GRAND_MASTER_BITZ)