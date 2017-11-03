# Made by Mr. - Version 0.3 by DrLecter
import sys
from com.it.br.gameserver.model.quest import State
from com.it.br.gameserver.model.quest import QuestState
from com.it.br.gameserver.model.quest.jython import QuestJython as JQuest

qn = "4_LongLiveLordOfFlame"

#NPC
NAKUSIN          = 30578
KUNAI            = 30559
USKA             = 30560
GR00KIN          = 30562
VARKEES          = 30566
TATARU_ZU_HESTUI = 30585
GANTAKI_ZU_URUTU = 30587

#GIFTS
HONEY_KHANDAR  = 1541
BEAR_FUR_CLOAK = 1542
BLOODY_AXE     = 1543
ANCESTOR_SKULL = 1544
SPIDER_DUST    = 1545
DEEP_SEA_ORB   = 1546
NPC_GIFTS = {TATARU_ZU_HESTUI:BEAR_FUR_CLOAK,VARKEES:HONEY_KHANDAR,GR00KIN:BLOODY_AXE,USKA:ANCESTOR_SKULL,KUNAI:SPIDER_DUST,GANTAKI_ZU_URUTU:DEEP_SEA_ORB}

#REWARDS
CLUB     = 4
ADENA_ID = 57


class Quest (JQuest) :

 def __init__(self,id,name,descr):
     JQuest.__init__(self,id,name,descr)
     self.questItemIds = range(1541,1547)

 def onEvent (self,event,st) :
    htmltext = event
    if event == "30578-03.htm" :
      st.set("cond","1")
      st.set("id","1")
      st.setState(State.STARTED)
      st.playSound("ItemSound.quest_accept")
    return htmltext

 def onTalk (self,npc,player):
   htmltext = "<html><body>You are either not on a quest that involves this NPC, or you don't meet this NPC's minimum quest requirements.</body></html>"
   st = player.getQuestState(qn)
   if not st : return htmltext

   npcId = npc.getNpcId()
   id = st.getState()
   cond = st.getInt("cond")
   if id == State.COMPLETED :
     htmltext = "<html><body>This quest has already been completed.</body></html>"
   elif npcId == NAKUSIN :
     if cond == 0 :
       if player.getRace().ordinal() != 3 :
         htmltext = "30578-00.htm"
         st.exitQuest(1)
       elif player.getLevel() >= 2 and player.getLevel() <= 5 :
         htmltext = "30578-02.htm"
       else:
         htmltext = "30578-01.htm"
         st.exitQuest(1)
     elif cond == 1 :
       htmltext = "30578-04.htm"
     elif cond == 2 :
       htmltext = "30578-06.htm"
       st.giveItems(CLUB,1)
       st.rewardItems(ADENA_ID,1850)
       for item in NPC_GIFTS.values():
           st.takeItems(item,-1)
       st.addExpAndSp(4254,335)
       st.unset("cond")
       st.exitQuest(False)
       st.playSound("ItemSound.quest_finish")
   elif npcId in NPC_GIFTS.keys() and cond == 1 and id == State.STARTED:
     item=NPC_GIFTS[npcId]
     npc=str(npcId)
     if st.getQuestItemsCount(item) :
       htmltext = npc+"-02.htm"
     else :
       st.giveItems(item,1)
       htmltext = npc+"-01.htm"
       count = 0
       for item in NPC_GIFTS.values():
         count += st.getQuestItemsCount(item)
       if count == 6 :
         st.set("cond","2")
         st.set("id","2")
         st.playSound("ItemSound.quest_middle")
       else :
         st.playSound("ItemSound.quest_itemget")
   return htmltext

QUEST     = Quest(4,qn,"Long Live the Paagrio Lord")

QUEST.addStartNpc(NAKUSIN)

QUEST.addTalkId(NAKUSIN)

QUEST.addTalkId(KUNAI)
QUEST.addTalkId(USKA)
QUEST.addTalkId(GR00KIN)
QUEST.addTalkId(VARKEES)
QUEST.addTalkId(NAKUSIN)
QUEST.addTalkId(TATARU_ZU_HESTUI)
QUEST.addTalkId(GANTAKI_ZU_URUTU)
