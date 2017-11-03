import sys
from com.it.br.gameserver import SevenSigns
from com.it.br.gameserver.model.quest        import State
from com.it.br.gameserver.model.quest        import QuestState
from com.it.br.gameserver.model.quest.jython import QuestJython as JQuest
from com.it.br.gameserver.model.actor.instance import L2PcInstance

qn = "505_BloodOffering"

TOWN_DAWN = [31078,31079,31080,31081,31083,31084,31082,31692,31694,31997,31168]
TOWN_DUSK = [31085,31086,31087,31088,31090,31091,31089,31693,31695,31998,31169]
DIM_GK = [31494,31495,31496,31497,31498,31499,31500,31501,31502,31503,31504,31505,31506,31507]
GK_ZIGGURAT = [31095,31096,31097,31098,31099,31100,31101,31102,31103,31104,31105,31106,31107,31108,31109,31110,31114,31115,31116,31117,31118,31119,31120,31121,31122,31123,31124,31125]
FESTIVALGUIDE = [31127,31128,31129,31130,31131,31137,31138,31139,31140,31141]
FESTIVALWITCH = [31132,31133,31134,31135,31136,31142,31143,31144,31145,31146]
RIFTPOST = [31488,31489,31490,31491,31492,31493]

class Quest (JQuest) :

 def __init__(self, id, name, descr): JQuest.__init__(self, id, name, descr)

 def onTalk (Self, npc, player):
    st = player.getQuestState(qn) 
    st2 = player.getQuestState("635_InTheDimensionalRift")
    if not st: return
    npcId = npc.getNpcId()

    if npcId in FESTIVALGUIDE :
       player.teleToLocation(-114796,-179334,-6752)
       if st2 :
          st2.setState(STARTED)
          st2.set("cond","1")
       st.playSound("ItemSound.quest_accept")
       st.exitQuest(1)
       return "guide.htm"

    elif npcId == 31132 :
       player.teleToLocation(-80204,87056,-5154)
       return "witch.htm"
    elif npcId == 31133 :
       player.teleToLocation(-77198,87678,-5182)
       return "witch.htm"
    elif npcId == 31134 :
       player.teleToLocation(-76183,87135,-5179)
       return "witch.htm"
    elif npcId == 31135 :
       player.teleToLocation(-76945,86602,5153)
       return "witch.htm"
    elif npcId == 31136 :
       player.teleToLocation(-79970,85997,-5154)
       return "witch.htm"
    elif npcId == 31142 :
       player.teleToLocation(-79182,111893,-4898)
       return "witch.htm"
    elif npcId == 31143 :
       player.teleToLocation(-76176,112505,-4899)
       return "witch.htm"
    elif npcId == 31144 :
       player.teleToLocation(-75198,111969,-4898)
       return "witch.htm"
    elif npcId == 31145 :
       player.teleToLocation(-75920,111435,-4900)
       return "witch.htm"
    elif npcId == 31146 :
       player.teleToLocation(-78928,110825,-4926)
       return "witch.htm"

    elif npcId in RIFTPOST :
       SEALVALIDATIONPERIOD = SevenSigns.getInstance().isSealValidationPeriod()
       CABAL = SevenSigns.getInstance().getPlayerData(player).getString("cabal")
       if not SEALVALIDATIONPERIOD :
          if CABAL == "dawn" :
             st.setState(STARTED)
             st.set("cond","1")
             st.getPlayer().teleToLocation(-80157,111344,-4901)
             if st2 :
                st2.unset("cond")
             return "riftpost-1.htm"
          else :
             if CABAL == "dusk" :
                st.setState(STARTED)
                st.set("cond","1")
                if st2 :
                   st2.unset("cond")
                st.getPlayer().teleToLocation(-81261,86531,-5157)
                return "riftpost-1.htm"
             else :
                return "riftpost-2.htm"
       else :
          return "riftpost-2.htm"
    return

QUEST    = Quest(505, qn, "BloodOffering")
CREATED     = State('Start',QUEST)
STARTED     = State('Started',QUEST)

QUEST.setInitialState(CREATED)

for i in TOWN_DAWN + TOWN_DUSK + DIM_GK + GK_ZIGGURAT + FESTIVALGUIDE + FESTIVALWITCH + RIFTPOST :
   QUEST.addStartNpc(i)
   QUEST.addTalkId(i)
