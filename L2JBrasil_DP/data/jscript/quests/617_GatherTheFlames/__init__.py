# Created by t0rm3nt0r
# Drop rates and last reorganization by DrLecter
# for the Official L2J Datapack Project.
# Visit http://forum.l2jdp.com for more details.
import sys
from com.it.br import Config
from com.it.br.gameserver.model.quest import State
from com.it.br.gameserver.model.quest import QuestState
from com.it.br.gameserver.model.quest.jython import QuestJython as JQuest

qn = "617_GatherTheFlames"

TORCH = 7264

VULCAN = 31539
ROONEY = 32049

DROPLIST = {21381:51,21653:51,21387:53,21655:53,21390:56,21656:69,21389:55,21388:53,\
            21383:51,21392:56,21382:60,21654:52,21384:64,21394:51,21395:56,21385:52,\
            21391:55,21393:58,21657:57,21386:52,21652:49,21378:49,21376:48,21377:48,\
            21379:59,21380:49,21420:51,21421:49,21430:53,21431:52}

REWARDS = [ 6881,6883,6885,6887,6891,6893,6895,6897,6899,7580 ]
REWARDS2= [ 6882,6884,6886,6888,6892,6894,6896,6898,6900,7581 ]

#Change this value to 1 if you wish 100% recipes, default 60%
ALT_RP100=0

class Quest (JQuest) :

 def __init__(self,id,name,descr): JQuest.__init__(self,id,name,descr)

 def onEvent (self,event,st) :
     htmltext = event
     torches = st.getQuestItemsCount(TORCH)
     if event == "31539-03.htm" :
       if st.getPlayer().getLevel() >= 74 :
         st.set("cond","1")
         st.setState(STARTED)
         st.playSound("ItemSound.quest_accept")
       else :
         htmltext = "31539-02.htm"
         st.exitQuest(1)
     elif event == "31539-05.htm" and torches >= 1000 :
       htmltext = "31539-07.htm"
       st.takeItems(TORCH,1000)
       if ALT_RP100 == 1:
         st.giveItems(REWARDS2[st.getRandom(len(REWARDS2))],1)
       else:
         st.giveItems(REWARDS[st.getRandom(len(REWARDS))],1)
     elif event == "31539-08.htm" :
       st.takeItems(TORCH,-1)
       st.exitQuest(1)
     elif event.isdigit() and int(event) in REWARDS :
       if torches >= 1200 :
          st.takeItems(TORCH,1200)
          if ALT_RP100 == 1:
            st.giveItems(int(event)+1,1)
          else:
            st.giveItems(int(event),1)
          htmltext = None
       else :
          htmltext = "Incorrect item count"
     return htmltext    

 def onTalk (self,npc,player):
     htmltext = "<html><body>You are either not carrying out your quest or don't meet the criteria.</body></html>"
     st = player.getQuestState(qn)
     if not st : return htmltext
     id = st.getState()
     cond = st.getInt("cond")
     torches = st.getQuestItemsCount(TORCH)
     npcId = npc.getNpcId()
     if npcId == VULCAN :
       if id == CREATED :
         if player.getLevel() < 74 :
            st.exitQuest(1)
            htmltext = "31539-02.htm"
         else :
            htmltext = "31539-01.htm"
       elif torches < 1000 :
         htmltext = "31539-05.htm"
       else :
         htmltext = "31539-04.htm"
     elif npcId == ROONEY and id == STARTED :
       if torches >= 1200 :
          htmltext = "32049-01.htm"
       else :
          htmltext = "32049-02.htm"
     return htmltext

 def onKill(self,npc,player,isPet):
     partyMember = self.getRandomPartyMemberState(player, STARTED)
     if not partyMember: return
     st = partyMember.getQuestState(qn)
     if not st : return
     torches = st.getQuestItemsCount(TORCH)
     chance = DROPLIST[npc.getNpcId()]
     drop = st.getRandom(100)
     qty,chance = divmod(chance*Config.RATE_DROP_QUEST,100)
     if drop < chance : qty += 1
     qty = int(qty)
     if qty :
        st.giveItems(TORCH,qty)
        if divmod(torches+1,1000)[1] == 0  or divmod(torches+1,1200)[1] == 0 :
          st.playSound("ItemSound.quest_middle")
        else :
          st.playSound("ItemSound.quest_itemget")
     return

QUEST       = Quest(617, qn, "Gather The Flames")
CREATED     = State('Start', QUEST)
STARTED     = State('Started', QUEST)

QUEST.setInitialState(CREATED)
QUEST.addStartNpc(VULCAN)

QUEST.addTalkId(VULCAN)
QUEST.addTalkId(ROONEY)

for mob in DROPLIST.keys() :
  QUEST.addKillId(mob)