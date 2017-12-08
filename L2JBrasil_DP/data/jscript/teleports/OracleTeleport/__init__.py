# Originally created by Ham Wong on 2007.03.07 #
import sys

from com.it.br.gameserver.model.actor.instance import L2PcInstance
from com.it.br.gameserver.model.quest          import State
from com.it.br.gameserver.model.quest          import QuestState
from com.it.br.gameserver.model.quest.jython   import QuestJython as JQuest
qn = "1103_OracleTeleport"
TOWN_DAWN = [31078,31079,31080,31081,31083,31084,31082,31692,31694,31997,31168]
TOWN_DUSK = [31085,31086,31087,31088,31090,31091,31089,31693,31695,31998,31169]
TEMPLE_PRIEST = [31127,31128,31129,31130,31131,31137,31138,31139,31140,31141]

TELEPORTERS = {
# Dawn
31078:1,
31079:2,
31080:3,
31081:4,
31083:5,
31084:6,
31082:7,
31692:8,
31694:9,
31997:10,
31168:11,
# Dusk
31085:12,
31086:13,
31087:14,
31088:15,
31090:16,
31091:17,
31089:18,
31693:19,
31695:20,
31998:21,
31169:22
}

RETURN_LOCS = [[-80555,150337,-3040],[-13953,121404,-2984],[16354,142820,-2696],[83369,149253,-3400], \
              [83106,53965,-1488],[146983,26595,-2200],[111386,220858,-3544],[148256,-55454,-2779], \
              [45664,-50318,-800],[86795,-143078,-1341],[115136,74717,-2608],[-82368,151568,-3120], \
              [-14748,123995,-3112],[18482,144576,-3056],[81623,148556,-3464],[82819,54607,-1520], \
              [147570,28877,-2264],[112486,220123,-3592],[149888,-56574,-2979],[44528,-48370,-800], \
              [85129,-142103,-1542],[116642,77510,-2688]]
class Quest (JQuest) :

 def __init__(self, id, name, descr): JQuest.__init__(self, id, name, descr)

 def onTalk (Self, npc, player):
    st = player.getQuestState(qn)
    if not st: return
    npcId = npc.getNpcId()
    ##################
    # Dawn Locations #
    ##################
    if npcId in TOWN_DAWN: 
       st.setState(STARTED)
       st.set("id",str(TELEPORTERS[npcId]))
       st.getPlayer().teleToLocation(-80157,111344,-4901)
    ##################
    # Dusk Locations #
    ##################
    elif npcId in TOWN_DUSK: 
       st.setState(STARTED)
       st.set("id",str(TELEPORTERS[npcId]))
       st.getPlayer().teleToLocation(-81261,86531,-5157)
    #######################
    # Oracle of Dusk/Dawn #
    #######################
    elif npcId in TEMPLE_PRIEST and st.getState() == STARTED :
       return_id = st.getInt("id") - 1
       st.getPlayer().teleToLocation(RETURN_LOCS[return_id][0],RETURN_LOCS[return_id][1],RETURN_LOCS[return_id][2])
       st.exitQuest(1)
    return
   
        
QUEST      = Quest(1103, qn, "Teleports")
CREATED    = State('Start', QUEST)
STARTED    = State('Started', QUEST)

QUEST.setInitialState(CREATED)

for i in TELEPORTERS :
    QUEST.addStartNpc(i)
    QUEST.addTalkId(i)

for j in TEMPLE_PRIEST :
    QUEST.addTalkId(j)