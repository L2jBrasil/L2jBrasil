# Made by Mr. Have fun! Version 0.2
import sys
from com.it.br import Config 
from com.it.br.gameserver.model.quest import State
from com.it.br.gameserver.model.quest import QuestState
from com.it.br.gameserver.model.quest.jython import QuestJython as JQuest

qn = "102_SeaOfSporesFever"

ALBERRYUS_LETTER_ID = 964
EVERGREEN_AMULET_ID = 965
DRYAD_TEARS_ID = 966
ALBERRYUS_LIST_ID = 746
COBS_MEDICINE1_ID = 1130
COBS_MEDICINE2_ID = 1131
COBS_MEDICINE3_ID = 1132
COBS_MEDICINE4_ID = 1133
COBS_MEDICINE5_ID = 1134

SWORD_OF_SENTINEL_ID = 743
STAFF_OF_SENTINEL_ID = 744

def check(st) :
   if (st.getQuestItemsCount(COBS_MEDICINE1_ID)==\
       st.getQuestItemsCount(COBS_MEDICINE2_ID)==\
       st.getQuestItemsCount(COBS_MEDICINE3_ID)==\
       st.getQuestItemsCount(COBS_MEDICINE4_ID)==\
       st.getQuestItemsCount(COBS_MEDICINE5_ID)==0) :
       st.set("cond","6")
       st.playSound(self.SOUND_QUEST_MIDDLE)

class Quest (JQuest) :

 def __init__(self,id,name,descr): 
    JQuest.__init__(self,id,name,descr)
    self.questItemIds = [ALBERRYUS_LETTER_ID, EVERGREEN_AMULET_ID, DRYAD_TEARS_ID, ALBERRYUS_LIST_ID, 
    COBS_MEDICINE1_ID, COBS_MEDICINE2_ID, COBS_MEDICINE3_ID, COBS_MEDICINE4_ID, COBS_MEDICINE5_ID]

 def onEvent (self,event,st) :
    htmltext = event
    if event == "1" :
        htmltext = "30284-02.htm"
        st.giveItems(ALBERRYUS_LETTER_ID,1)
        st.set("cond","1")
        st.setState(STARTED)
        st.playSound(self.SOUND_QUEST_START)
    return htmltext


 def onTalk (self,npc,player):
   npcId = npc.getNpcId()
   htmltext = self.NO_QUEST
   st = player.getQuestState(qn)
   if not st: return htmltext

   id = st.getState()
   if id == CREATED :
     st.set("cond","0")
     st.set("onlyone","0")
   if npcId == 30284 and st.getInt("cond")==0 and st.getInt("onlyone")==0 :
      if player.getRace().ordinal() != 1 :
         htmltext = "30284-00.htm"
         st.exitQuest(1)
      elif player.getLevel() >= 12 :
         htmltext = "30284-07.htm"
         return htmltext
      else:
         htmltext = "30284-08.htm"
         st.exitQuest(1)
   elif npcId == 30284 and st.getInt("cond")==0 and st.getInt("onlyone")==1 :
        htmltext = self.QUEST_DONE
   elif id == STARTED :
      if npcId == 30284 and st.getInt("cond")==1 and st.getQuestItemsCount(ALBERRYUS_LETTER_ID)==1 :
           htmltext = "30284-03.htm"
      elif npcId == 30284 and st.getInt("cond")==1 and st.getQuestItemsCount(EVERGREEN_AMULET_ID)==1 :
           htmltext = "30284-09.htm"
      elif npcId == 30156 and st.getInt("cond")==1 and st.getQuestItemsCount(ALBERRYUS_LETTER_ID)==1 :
           st.giveItems(EVERGREEN_AMULET_ID,1)
           st.takeItems(ALBERRYUS_LETTER_ID,1)
           st.set("cond","2")
           htmltext = "30156-03.htm"
      elif npcId == 30156 and st.getInt("cond")==2 and st.getQuestItemsCount(EVERGREEN_AMULET_ID)>0 and st.getQuestItemsCount(DRYAD_TEARS_ID)<10 :
           htmltext = "30156-04.htm"
      elif npcId == 30156 and st.getInt("cond")==5 and st.getQuestItemsCount(ALBERRYUS_LIST_ID)>0 :
           htmltext = "30156-07.htm"
      elif npcId == 30156 and st.getInt("cond")==3 and st.getQuestItemsCount(EVERGREEN_AMULET_ID)>0 and st.getQuestItemsCount(DRYAD_TEARS_ID)>=10 :
           st.takeItems(EVERGREEN_AMULET_ID,1)
           st.takeItems(DRYAD_TEARS_ID,-1)
           st.giveItems(COBS_MEDICINE1_ID,1)
           st.giveItems(COBS_MEDICINE2_ID,1)
           st.giveItems(COBS_MEDICINE3_ID,1)
           st.giveItems(COBS_MEDICINE4_ID,1)
           st.giveItems(COBS_MEDICINE5_ID,1)
           st.set("cond","4")
           htmltext = "30156-05.htm"
      elif npcId == 30156 and st.getInt("cond")==4 and st.getQuestItemsCount(ALBERRYUS_LIST_ID)==0 and (st.getQuestItemsCount(COBS_MEDICINE1_ID)==1 or st.getQuestItemsCount(COBS_MEDICINE2_ID)==1 or st.getQuestItemsCount(COBS_MEDICINE3_ID)==1 or st.getQuestItemsCount(COBS_MEDICINE4_ID)==1 or st.getQuestItemsCount(COBS_MEDICINE5_ID)==1) :
           htmltext = "30156-06.htm"
      elif npcId == 30284 and st.getInt("cond")==4 and st.getQuestItemsCount(ALBERRYUS_LIST_ID)==0 and st.getQuestItemsCount(COBS_MEDICINE1_ID)==1 :
           st.takeItems(COBS_MEDICINE1_ID,1)
           st.giveItems(ALBERRYUS_LIST_ID,1)
           st.set("cond","5")
           htmltext = "30284-04.htm"
      elif npcId == 30284 and st.getInt("cond")==5 and st.getQuestItemsCount(ALBERRYUS_LIST_ID)==1 and (st.getQuestItemsCount(COBS_MEDICINE1_ID)==1 or st.getQuestItemsCount(COBS_MEDICINE2_ID)==1 or st.getQuestItemsCount(COBS_MEDICINE3_ID)==1 or st.getQuestItemsCount(COBS_MEDICINE4_ID)==1 or st.getQuestItemsCount(COBS_MEDICINE5_ID)==1) :
           htmltext = "30284-05.htm"
      elif npcId == 30217 and st.getInt("cond")==5 and st.getQuestItemsCount(ALBERRYUS_LIST_ID)==1 and st.getQuestItemsCount(COBS_MEDICINE2_ID)==1 :
           st.takeItems(COBS_MEDICINE2_ID,1)
           check(st)
           htmltext = "30217-01.htm"
      elif npcId == 30219 and st.getInt("cond")==5 and st.getQuestItemsCount(ALBERRYUS_LIST_ID)==1 and st.getQuestItemsCount(COBS_MEDICINE3_ID)==1 :
           st.takeItems(COBS_MEDICINE3_ID,1)
           check(st)
           htmltext = "30219-01.htm"
      elif npcId == 30221 and st.getInt("cond")==5 and st.getQuestItemsCount(ALBERRYUS_LIST_ID)==1 and st.getQuestItemsCount(COBS_MEDICINE4_ID)==1 :
           st.takeItems(COBS_MEDICINE4_ID,1)
           check(st)
           htmltext = "30221-01.htm"
      elif npcId == 30285 and st.getInt("cond")==5 and st.getQuestItemsCount(ALBERRYUS_LIST_ID)==1 and st.getQuestItemsCount(COBS_MEDICINE5_ID)==1 :
           st.takeItems(COBS_MEDICINE5_ID,1)
           check(st)
           htmltext = "30285-01.htm"
      elif npcId == 30284 and st.getInt("cond")==6 and st.getQuestItemsCount(ALBERRYUS_LIST_ID)==1 :
           st.takeItems(ALBERRYUS_LIST_ID,1)
           st.set("cond","0")
           st.setState(COMPLETED)
           st.playSound(self.SOUND_QUEST_DONE)
           htmltext = "30284-06.htm"
           st.set("onlyone","1")
           if player.getClassId().getId() in range(18,25) :
             st.giveItems(SWORD_OF_SENTINEL_ID,1)
             st.giveItems(1835,int(1000*Config.RATE_QUESTS_REWARD))
           else:
             st.giveItems(STAFF_OF_SENTINEL_ID,1)
             st.giveItems(2509,int(1000*Config.RATE_QUESTS_REWARD))
           for item in range(4412,4417) :
             st.giveItems(item,int(10*Config.RATE_QUESTS_REWARD))
           st.giveItems(1060,int(100*Config.RATE_QUESTS_REWARD))
   return htmltext

 def onKill(self,npc,player,isPet):
   st = player.getQuestState(qn)
   if not st: return 

   if st.getState() == STARTED :       
      npcId = npc.getNpcId()
      if npcId in [20013,20019] :
         if st.getQuestItemsCount(EVERGREEN_AMULET_ID)>0 and st.getQuestItemsCount(DRYAD_TEARS_ID)<10 :
            if st.getRandom(10)<3 :
               st.giveItems(DRYAD_TEARS_ID,1)
               if st.getQuestItemsCount(DRYAD_TEARS_ID) == 10 :
                 st.playSound(self.SOUND_QUEST_MIDDLE)
                 st.set("cond","3")
               else:
                 st.playSound(self.SOUND_ITEM_GET)
   return

QUEST       = Quest(102,qn,"Sea of Spores Fever")
CREATED     = State('Start', QUEST)
STARTED     = State('Started', QUEST)
COMPLETED   = State('Completed', QUEST)

QUEST.setInitialState(CREATED)
QUEST.addStartNpc(30284)
QUEST.addTalkId(30284)

QUEST.addTalkId(30156)
QUEST.addTalkId(30217)
QUEST.addTalkId(30219)
QUEST.addTalkId(30221)
QUEST.addTalkId(30285)

QUEST.addKillId(20013)
QUEST.addKillId(20019)