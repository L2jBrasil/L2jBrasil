import sys
from com.it.br.gameserver.model.quest import State
from com.it.br.gameserver.model.quest import QuestState
from com.it.br.gameserver.model.quest.jython import QuestJython as JQuest

qn = "661_TheHarvestGroundsSafe"

# NPC
NORMAN = 30210

# MOBS
GIANT_POISON_BEE = 21095
CLOUDY_BEAST = 21096
YOUNG_ARANEID = 21097

#QUEST ITEMS
STING_OF_GIANT_POISON = 8283
TALON_OF_YOUNG_ARANEID = 8285
CLOUDY_GEM = 8284

#Droplist format - npcId : [item,chance]
DROPLIST = {
   GIANT_POISON_BEE : [STING_OF_GIANT_POISON,75],
   CLOUDY_BEAST : [CLOUDY_GEM,71],
   YOUNG_ARANEID : [TALON_OF_YOUNG_ARANEID,67]
   }

class Quest (JQuest) :

 def __init__(self,id,name,descr):
 	JQuest.__init__(self,id,name,descr)
 	self.questItemIds = range(8283,8286)

 def onEvent (self,event,st) :
    htmltext = event
    if event in ["30210-03.htm","30210-09.htm"] :
       st.set("cond","1")
       st.setState(STARTED)
       st.playSound("ItemSound.quest_accept")
    if event == "30210-08.htm" :
      STING = st.getQuestItemsCount(STING_OF_GIANT_POISON)    
      TALON = st.getQuestItemsCount(TALON_OF_YOUNG_ARANEID)
      GEM = st.getQuestItemsCount(CLOUDY_GEM)
      amount = 0
      if STING+GEM+TALON >= 10 :
          amount = 2800
      st.giveItems(57,STING*50+GEM*60+TALON*70 + amount)
      st.takeItems(STING_OF_GIANT_POISON,-1)
      st.takeItems(TALON_OF_YOUNG_ARANEID,-1)
      st.takeItems(CLOUDY_GEM,-1)
      st.playSound("ItemSound.quest_middle")
    elif event == "30210-06.htm" :
       st.exitQuest(1)
       st.playSound("ItemSound.quest_finish")
    return htmltext

 def onTalk (self,npc,player):
   htmltext = "<html><body>You are either not on a quest that involves this NPC, or you don't meet this NPC's minimum quest requirements.</body></html>"
   st = player.getQuestState(qn)
   if not st : return htmltext
   npcid = npc.getNpcId()
   cond = st.getInt("cond")
   if not cond :
      if st.getPlayer().getLevel() >= 21 :
         htmltext = "30210-02.htm"
      else :
         htmltext = "30210-01.htm"
         st.exitQuest(1)
   if cond :
      S = st.getQuestItemsCount(STING_OF_GIANT_POISON)  
      T = st.getQuestItemsCount(TALON_OF_YOUNG_ARANEID)
      C = st.getQuestItemsCount(CLOUDY_GEM)     
      if S+T+C == 0 :
         htmltext = "30210-04.htm"
      else :
         htmltext = "30210-05.htm"  
   return htmltext

 def onKill(self,npc,player,isPet):
    st = player.getQuestState(qn)
    if not st : return
    if st.getState() != STARTED : return
    npcId = npc.getNpcId()
    rand = st.getRandom(100)
    if npcId in DROPLIST.keys() :
        item,chance = DROPLIST[npcId]
        if rand < chance :
            st.giveItems(item,1)
            st.playSound("ItemSound.quest_itemget")
    return

QUEST = Quest(661,qn,"Making the Harvest Grounds Safe")
CREATED     = State('Start', QUEST)
STARTED     = State('Started', QUEST)

QUEST.setInitialState(CREATED)

QUEST.addStartNpc(NORMAN)
QUEST.addTalkId(NORMAN)

for id in DROPLIST.keys() :
    QUEST.addKillId(id)