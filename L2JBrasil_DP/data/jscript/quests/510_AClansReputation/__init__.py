import sys
from com.it.br.gameserver.model.quest            import State
from com.it.br.gameserver.model.quest            import QuestState
from com.it.br.gameserver.model.quest.jython    import QuestJython as JQuest
from com.it.br.gameserver.network.serverpackets import PledgeShowInfoUpdate
from com.it.br.gameserver.network.serverpackets import SystemMessage

qn="510_AClansReputation"

# Quest NPC
Valdis = 31331

# Quest Items
Claw = 8767

# Reward
CLAN_POINTS_REWARD = 50 # Rep Points Per Tyrannosaurus Item - need to be confirmed

class Quest (JQuest) :

 def __init__(self,id,name,descr) :
     JQuest.__init__(self,id,name,descr)
     self.questItemIds = [Claw]

 def onAdvEvent (self,event,npc,player) :
  st = player.getQuestState(qn)
  if not st: return
  cond = st.getInt("cond")
  htmltext=event
  if event == "31331-3.htm" :
    if cond == 0 :
      st.set("cond","1")
      st.setState(STARTED)
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
     htmltext = "31331-0.htm"
  elif player.getClan().getLevel() < 5 :
     st.exitQuest(1)
     htmltext =  "31331-0.htm"
  else :
     cond = st.getInt("cond")
     id = st.getState()
     if id == CREATED and cond == 0 :
        htmltext =  "31331-1.htm"
     elif id == STARTED and cond == 1 :
        count = st.getQuestItemsCount(Claw)
        if not count :
           htmltext = "31331-4.htm"
        elif count >= 1 :
           htmltext = "31331-7.htm" # custom html
           st.takeItems(Claw,-1)
           reward = int(CLAN_POINTS_REWARD * count)
           clan.setReputationScore(clan.getReputationScore()+reward,True)
           player.sendPacket(SystemMessage(1777).addNumber(reward))
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
  if st.getState() == STARTED :
   npcId=npc.getNpcId()
   if npcId in range(22215,22218) :
      st.giveItems(Claw,1)
      st.playSound("ItemSound.quest_itemget")
  return


# Quest class and state definition
QUEST       = Quest(510,qn,"A Clan's Reputation")
CREATED     = State('Start',QUEST)
STARTED     = State('Started',QUEST)

QUEST.setInitialState(CREATED)

QUEST.addStartNpc(Valdis)
QUEST.addTalkId(Valdis)

for npc in range(22215,22218):
    QUEST.addKillId(npc)