# Made by Mr. Have fun! Version 0.2
# Version 0.3 by H1GHL4ND3R
import sys
from com.it.br import Config 
from com.it.br.gameserver.model.quest import State
from com.it.br.gameserver.model.quest import QuestState
from com.it.br.gameserver.model.quest.jython import QuestJython as JQuest

qn = "106_ForgottenTruth"

ONYX_TALISMAN1,      ONYX_TALISMAN2,     ANCIENT_SCROLL,  \
ANCIENT_CLAY_TABLET, KARTAS_TRANSLATION, ELDRITCH_DAGGER  \
= range(984,990)

ORC = 27070

class Quest (JQuest) :

 def __init__(self,id,name,descr): 
    JQuest.__init__(self,id,name,descr)
    self.questItemIds = [ONYX_TALISMAN1, ONYX_TALISMAN2, ANCIENT_SCROLL, ANCIENT_CLAY_TABLET, KARTAS_TRANSLATION]

 def onEvent (self,event,st) :
    htmltext = event
    if event == "30358-05.htm" :
        st.giveItems(ONYX_TALISMAN1,1)
        st.set("cond","1")
        st.setState(STARTED)
        st.playSound(self.SOUND_QUEST_START)
    return htmltext

 def onTalk (self,npc,player):
   npcId = npc.getNpcId()
   htmltext = self.NO_QUEST
   st = player.getQuestState(qn)
   if not st : return htmltext

   id = st.getState()
   if id == CREATED :   # Check if is starting the quest
     st.set("cond","0")
     if player.getRace().ordinal() == 2 :
       if player.getLevel() >= 10 :
         htmltext = "30358-03.htm"
       else:
         htmltext = "30358-02.htm"
         st.exitQuest(1)
     else :
       htmltext = "30358-00.htm"
       st.exitQuest(1)
   elif id == COMPLETED :   # Check if the quest is already made
     htmltext = self.QUEST_DONE
   else :     # The quest itself
     try :
       cond = st.getInt("cond")
     except :
       cond = None
     if cond == 1 :
       if npcId == 30358 :
         htmltext = "30358-06.htm"
       elif npcId == 30133 and st.getQuestItemsCount(ONYX_TALISMAN1) and id == STARTED : 
         htmltext = "30133-01.htm"
         st.takeItems(ONYX_TALISMAN1,1)
         st.giveItems(ONYX_TALISMAN2,1)
         st.set("cond","2")
     elif cond == 2 :
       if npcId == 30358 :
         htmltext = "30358-06.htm"
       elif npcId == 30133 :
         htmltext = "30133-02.htm"
     elif cond == 3 :
       if npcId == 30358 :
         htmltext = "30358-06.htm"
       elif npcId == 30133 and st.getQuestItemsCount(ANCIENT_SCROLL) and st.getQuestItemsCount(ANCIENT_CLAY_TABLET) and id == STARTED :
         htmltext = "30133-03.htm"
         st.takeItems(ONYX_TALISMAN2,1)
         st.takeItems(ANCIENT_SCROLL,1)
         st.takeItems(ANCIENT_CLAY_TABLET,1)
         st.giveItems(KARTAS_TRANSLATION,1)
         st.set("cond","4")
     elif cond == 4 :
       if npcId == 30358 and st.getQuestItemsCount(KARTAS_TRANSLATION) :
         htmltext = "30358-07.htm"
         st.takeItems(KARTAS_TRANSLATION,1)
         st.giveItems(ELDRITCH_DAGGER,1)
         for item in range(4412,4417) :
               st.giveItems(item,int(10*Config.RATE_QUESTS_REWARD))
         st.giveItems(1060,int(100*Config.RATE_QUESTS_REWARD))
         if player.getClassId().isMage() and st.getInt("onlyone") == 0:
             st.giveItems(2509,500)
             if player.getLevel() < 25 and player.isNewbie():
                 st.giveItems(5790,3000)
         elif st.getInt("onlyone") == 0:
             st.giveItems(1835,1000)
         st.unset("cond")
         st.setState(COMPLETED)
         st.playSound(SOUND_QUEST_DONE)
       elif npcId == 30133 and id == STARTED :
         htmltext = "30133-04.htm"
   return htmltext

 def onKill(self,npc,player,isPet):
   st = player.getQuestState(qn)
   if not st : return
   if st.getState() != STARTED : return
   
   if st.getInt("cond") == 2 :
     if st.getRandom(100) < 20 :
       if st.getQuestItemsCount(ANCIENT_SCROLL) == 0 :
         st.giveItems(ANCIENT_SCROLL,1)
         st.playSound(self.SOUND_ITEM_GET)
       elif st.getQuestItemsCount(ANCIENT_CLAY_TABLET) == 0 :
         st.giveItems(ANCIENT_CLAY_TABLET,1)
         st.playSound(self.SOUND_QUEST_MIDDLE)
         st.set("cond","3")
   return

QUEST       = Quest(106,qn,"Forgotten Truth")
CREATED     = State('Start', QUEST)
STARTING    = State('Starting', QUEST)
STARTED     = State('Started', QUEST)
COMPLETED   = State('Completed', QUEST)

QUEST.setInitialState(CREATED)
QUEST.addStartNpc(30358)

QUEST.addTalkId(30358)

QUEST.addTalkId(30133)

QUEST.addKillId(27070)