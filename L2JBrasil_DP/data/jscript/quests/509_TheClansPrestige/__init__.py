import sys
from com.it.br.gameserver.model.quest import State
from com.it.br.gameserver.model.quest import QuestState
from com.it.br.gameserver.model.quest.jython import QuestJython as JQuest
from com.it.br.gameserver.network.serverpackets import PledgeShowInfoUpdate
from com.it.br.gameserver.network.serverpackets import SystemMessage
from com.it.br.util import Rnd

qn="509_TheClansPrestige"

# Quest NPC
VALDIS = 31331

# Quest Items
DAIMONS_EYES                = 8489 # Daimon's Eyes: Eyes obtained by killing Daimon the White-Eyed.
HESTIAS_FAIRY_STONE         = 8490 # Hestia's Fairy Stone: Fairy Stone obtained by defeating Hestia, the Guardian Deity of the Hot Springs.
NUCLEUS_OF_LESSER_GOLEM     = 8491 # Nucleus of Lesser Golem: Nucleus obtained by defeating the Lesser Golem.
FALSTON_FANG                = 8492 # Falston's Fang: Fangs obtained by killing Falston, the Demon's Agent.
SHAIDS_TALON                = 8493 # Shaid's Talon: Talon obtained by defeating Spike Stakato Queen Shaid.

#Quest Raid Bosses
DAIMON_THE_WHITE_EYED  = 25290
HESTIA_GUARDIAN_DEITY  = 25293
PLAGUE_GOLEM           = 25523
DEMONS_AGENT_FALSTON   = 25322
QUEEN_SHYEED           = 25514

# id:[RaidBossNpcId,questItemId,minClanPoints,maxClanPoints]
REWARDS_LIST={
    1:[DAIMON_THE_WHITE_EYED,DAIMONS_EYES,180,215],
    2:[HESTIA_GUARDIAN_DEITY,HESTIAS_FAIRY_STONE,430,465],
    3:[PLAGUE_GOLEM,NUCLEUS_OF_LESSER_GOLEM,380,415],
    4:[DEMONS_AGENT_FALSTON,FALSTON_FANG,220,255],
    5:[QUEEN_SHYEED,SHAIDS_TALON,130,165]
    }

RADAR={
    1:[186320,-43904,-3175],
    2:[134672,-115600,-1216],
    3:[0,0,0], # not spawned yet
    4:[93296,-75104,-1824],
    5:[79635,-55612,-5980]
    }

class Quest (JQuest) :

 def __init__(self,id,name,descr) :
     JQuest.__init__(self,id,name,descr)
     self.questItemIds = [DAIMONS_EYES,HESTIAS_FAIRY_STONE,NUCLEUS_OF_LESSER_GOLEM,FALSTON_FANG,SHAIDS_TALON]

 def onAdvEvent (self,event,npc,player) :
  st = player.getQuestState(qn)
  if not st: return
  cond = st.getInt("cond")
  htmltext=event
  if event == "31331-0.htm" :
    if cond == 0 :
      st.set("cond","1")
      st.setState(STARTED)
  elif event.isdigit() :
    if int(event) in REWARDS_LIST.keys():
      st.set("raid",event)
      htmltext="31331-"+event+".htm"
      x,y,z=RADAR[int(event)]
      if x+y+z:
        st.addRadar(x, y, z)
      st.playSound("ItemSound.quest_accept")
  elif event == "31331-6.htm" :
    st.playSound("ItemSound.quest_finish")
    st.exitQuest(1)
  return htmltext

 def onTalk (self,npc,player) :
  htmltext = "<html><body>You are either not on a quest that involves this NPC, or you don't meet this NPC's minimum quest requirements.</body></html>"
  st = player.getQuestState(qn)
  if not st : return htmltext
  clan = player.getClan()
  npcId = npc.getNpcId()
  if player.getClan() == None or player.isClanLeader() == 0 :
     st.exitQuest(1)
     htmltext = "31331-0a.htm"
  elif player.getClan().getLevel() < 6 :
     st.exitQuest(1)
     htmltext =  "31331-0b.htm"
  else :
     cond = st.getInt("cond")
     raid = st.getInt("raid")
     id = st.getState()
     if id == CREATED and cond == 0 :
        htmltext =  "31331-0c.htm"
     elif id == STARTED and cond == 1 and raid in REWARDS_LIST.keys() :
        npc,item,min,max=REWARDS_LIST[raid]
        count = st.getQuestItemsCount(item)
        CLAN_POINTS_REWARD = Rnd.get(min, max)
        if not count :
           htmltext = "31331-"+str(raid)+"a.htm"
        elif count == 1 :
           htmltext = "31331-"+str(raid)+"b.htm"
           st.takeItems(item,1)
           clan.setReputationScore(clan.getReputationScore()+CLAN_POINTS_REWARD,True)
           player.sendPacket(SystemMessage(1777).addNumber(CLAN_POINTS_REWARD))
           clan.broadcastToOnlineMembers(PledgeShowInfoUpdate(clan))
  return htmltext

 def onKill(self,npc,player,isPet) :
  st = 0
  if player.isClanLeader() :
   st = player.getQuestState(qn)
  else:
   clan = player.getClan()
   if clan:
    leader=clan.getLeader()
    if leader :
     pleader= leader.getPlayerInstance()
     if pleader :
      if player.isInsideRadius(pleader, 1600, 1, 0) :
       st = pleader.getQuestState(qn)
  if not st : return
  option=st.getInt("raid")
  if st.getInt("cond") == 1 and st.getState() == STARTED and option in REWARDS_LIST.keys():
   raid,item,min,max = REWARDS_LIST[option]
   npcId=npc.getNpcId()
   if npcId == raid and not st.getQuestItemsCount(item) :
      st.giveItems(item,1)
      st.playSound("ItemSound.quest_middle")
  return


# Quest class and state definition
QUEST       = Quest(509,qn,"The Clan's Prestige")
CREATED     = State('Start',QUEST)
STARTED     = State('Started',QUEST)

QUEST.setInitialState(CREATED)

QUEST.addStartNpc(VALDIS)
QUEST.addTalkId(VALDIS)

for npc,item,min,max in REWARDS_LIST.values():
    QUEST.addKillId(npc)