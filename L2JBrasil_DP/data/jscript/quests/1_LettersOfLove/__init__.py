# Maked by Mr. Have fun! Version 0.2
import sys
from com.it.br.gameserver.model.quest import State
from com.it.br.gameserver.model.quest import QuestState
from com.it.br.gameserver.model.quest.jython import QuestJython as JQuest

qn = "1_LettersOfLove"

#NPCs 
DARIN  = 30048 
ROXXY  = 30006 
BAULRO = 30033 

#QUEST ITEMS 
DARINGS_LETTER     = 687 
RAPUNZELS_KERCHIEF = 688 
DARINGS_RECEIPT    = 1079 
BAULS_POTION       = 1080 
 
#REWARD 
NECKLACE = 906
ADENA_ID = 57
 
class Quest (JQuest) :

 def __init__(self,id,name,descr): 
   JQuest.__init__(self,id,name,descr)
   self.questItemIds = [DARINGS_LETTER,RAPUNZELS_KERCHIEF,DARINGS_RECEIPT,BAULS_POTION]

 def onEvent (self,event,st) :
   htmltext = event 
   if event == "30048-06.htm" : 
     st.set("cond","1") 
     st.set("id","1") 
     st.setState(STARTED) 
     st.playSound(self.SOUND_QUEST_START) 
     if st.getQuestItemsCount(DARINGS_LETTER) == 0 : 
       st.giveItems(DARINGS_LETTER,1) 
   return htmltext 

 def onTalk (self,npc,player):
   st = player.getQuestState(qn)
   htmltext = self.NO_QUEST 
   if not st: return htmltext

   npcId = npc.getNpcId()
   id = st.getState()
   if id == CREATED :
     st.setState(STARTING)
     st.set("cond","0")
     st.set("onlyone","0")
     st.set("id","0")
 
   cond = st.getInt("cond") 
   onlyone = st.getInt("onlyone") 
   ItemsCount_DL = st.getQuestItemsCount(DARINGS_LETTER) 
   ItemsCount_RK = st.getQuestItemsCount(RAPUNZELS_KERCHIEF) 
   ItemsCount_DR = st.getQuestItemsCount(DARINGS_RECEIPT) 
   ItemsCount_BP = st.getQuestItemsCount(BAULS_POTION) 
 
   if npcId == DARIN and cond == 0 and onlyone == 0 : 
     if player.getLevel() >= 2 : 
       if cond < 15 : 
         htmltext = "30048-02.htm" 
       else: 
         htmltext = "30048-01.htm" 
         st.exitQuest(1) 
     else: 
       htmltext = "<html><body>Quest for characters level 2 and above.</body></html>" 
       st.exitQuest(1) 
   elif npcId == DARIN and cond == 0 and onlyone == 1 : 
     htmltext = self.QUEST_DONE
   elif id == STARTED :
       if npcId == ROXXY and cond and onlyone == 0: 
         if ItemsCount_RK == 0 and ItemsCount_DL : 
           htmltext = "30006-01.htm" 
           st.takeItems(DARINGS_LETTER,-1) 
           st.giveItems(RAPUNZELS_KERCHIEF,1) 
           st.set("cond","2") 
           st.set("id","2") 
           st.playSound(self.SOUND_QUEST_MIDDLE) 
         elif ItemsCount_BP or ItemsCount_DR : 
           htmltext = "30006-03.htm" 
         elif ItemsCount_RK : 
           htmltext = "30006-02.htm" 
       elif npcId == DARIN and cond and ItemsCount_RK > 0 and onlyone == 0 : 
         htmltext = "30048-08.htm" 
         st.takeItems(RAPUNZELS_KERCHIEF,-1) 
         st.giveItems(DARINGS_RECEIPT,1) 
         st.set("cond","3") 
         st.set("id","3") 
         st.playSound(self.SOUND_QUEST_MIDDLE) 
       elif npcId == BAULRO and cond and onlyone == 0 : 
         if ItemsCount_DR > 0 : 
           htmltext = "30033-01.htm" 
           st.takeItems(DARINGS_RECEIPT,-1) 
           st.giveItems(BAULS_POTION,1) 
           st.set("cond","4") 
           st.set("id","4") 
           st.playSound(self.SOUND_QUEST_MIDDLE) 
         elif ItemsCount_BP > 0 : 
           htmltext = "30033-02.htm" 
       elif npcId == DARIN and cond and ItemsCount_RK == 0 and onlyone == 0 : 
         if ItemsCount_DR > 0 : 
           htmltext = "30048-09.htm" 
         elif ItemsCount_BP > 0 : 
           htmltext = "30048-10.htm" 
           st.takeItems(BAULS_POTION,-1)
           st.rewardItems(ADENA_ID, 2466) 
           st.giveItems(NECKLACE,1)
           st.addExpAndSp(5672,446) 
           st.set("cond","0") 
           st.set("onlyone","1") 
           st.setState(COMPLETED)
           st.playSound(self.SOUND_QUEST_DONE) 
         else: 
           htmltext = "30048-07.htm" 
   return htmltext

QUEST     = Quest(1,qn,"Letters of Love") 
CREATED   = State('Start',     QUEST) 
STARTING  = State('Starting',  QUEST) 
STARTED   = State('Started',   QUEST) 
COMPLETED = State('Completed', QUEST) 

QUEST.setInitialState(CREATED)
QUEST.addStartNpc(DARIN) 

QUEST.addTalkId(DARIN) 

QUEST.addTalkId(ROXXY) 
QUEST.addTalkId(BAULRO) 