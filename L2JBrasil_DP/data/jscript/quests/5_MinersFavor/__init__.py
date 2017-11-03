# Maked by Mr. Have fun! Version 0.2
import sys
from com.it.br.gameserver.model.quest import State
from com.it.br.gameserver.model.quest import QuestState
from com.it.br.gameserver.model.quest.jython import QuestJython as JQuest

qn = "5_MinersFavor"

#NPCs 
BOLTER = 30554 
SHARI  = 30517 
GARITA = 30518 
REED   = 30520 
BRUNON = 30526 

#QUEST ITEMS 
BOLTERS_LIST         = 1547 
MINING_BOOTS         = 1548 
MINERS_PICK          = 1549 
BOOMBOOM_POWDER      = 1550 
REDSTONE_BEER        = 1551 
BOLTERS_SMELLY_SOCKS = 1552 
 
#REWARDS 
NECKLACE = 906 
ADENA_ID = 57  
 
class Quest (JQuest) :

 def __init__(self,id,name,descr): JQuest.__init__(self,id,name,descr)

 def onEvent (self,event,st) :
   htmltext = event 
   if event == "30554-03.htm" : 
     st.giveItems(BOLTERS_LIST,1) 
     st.giveItems(BOLTERS_SMELLY_SOCKS,1) 
     st.set("cond","1") 
     st.set("id","1") 
     st.setState(STARTED) 
     st.playSound("ItemSound.quest_accept") 
   elif event == "30526-02.htm" : 
     st.takeItems(BOLTERS_SMELLY_SOCKS,-1) 
     st.giveItems(MINERS_PICK,1) 
     if st.getQuestItemsCount(BOLTERS_LIST) and (st.getQuestItemsCount(MINING_BOOTS) + st.getQuestItemsCount(MINERS_PICK) + st.getQuestItemsCount(BOOMBOOM_POWDER) + st.getQuestItemsCount(REDSTONE_BEER) >= 4) : 
       st.set("cond","2") 
       st.set("id","2") 
       st.playSound("ItemSound.quest_middle") 
     else: 
       st.playSound("ItemSound.quest_itemget") 
   return htmltext 

 def onTalk (self,npc,player):
   htmltext = "<html><body>You are either not carrying out your quest or don't meet the criteria.</body></html>"
   st = player.getQuestState(qn)
   if not st : return htmltext

   npcId = npc.getNpcId()
   id = st.getState()
 
   if id == CREATED :
     st.setState(STARTING)
     st.set("cond","0")
     st.set("onlyone","0")
     st.set("id","0")
 
   cond    = st.getInt("cond") 
   onlyone = st.getInt("onlyone") 
 
   if npcId == BOLTER and cond == 0 : 
     if onlyone == 1 : 
       htmltext = "<html><body>This quest has already been completed.</body></html>" 
     elif player.getLevel() >= 2 and player.getLevel() <= 5 : 
       htmltext = "30554-02.htm" 
     else: 
       htmltext = "30554-01.htm" 
       st.exitQuest(1) 
   elif npcId == BOLTER and cond == 1 : 
     htmltext = "30554-04.htm" 
   elif npcId == BOLTER and cond == 2 : 
     htmltext = "30554-06.htm" 
     st.takeItems(MINING_BOOTS,-1) 
     st.takeItems(MINERS_PICK,-1) 
     st.takeItems(BOOMBOOM_POWDER,-1) 
     st.takeItems(REDSTONE_BEER,-1) 
     st.takeItems(BOLTERS_LIST,-1)
     st.rewardItems(ADENA_ID,2466) 
     st.giveItems(NECKLACE,1)
     st.addExpAndSp(5762,446) 
     st.set("cond","0") 
     st.set("onlyone","1") 
     st.setState(COMPLETED) 
     st.playSound("ItemSound.quest_finish")
   elif id == STARTED :  
       if npcId == SHARI and cond == 1 and st.getQuestItemsCount(BOLTERS_LIST) : 
         if st.getQuestItemsCount(BOOMBOOM_POWDER) == 0 : 
           htmltext = "30517-01.htm" 
           st.giveItems(BOOMBOOM_POWDER,1) 
           st.playSound("ItemSound.quest_itemget") 
         else: 
           htmltext = "30517-02.htm" 
       elif npcId == GARITA and cond == 1 and st.getQuestItemsCount(BOLTERS_LIST) : 
         if st.getQuestItemsCount(MINING_BOOTS) == 0 : 
           htmltext = "30518-01.htm" 
           st.giveItems(MINING_BOOTS,1) 
           st.playSound("ItemSound.quest_itemget") 
         else: 
           htmltext = "30518-02.htm" 
       elif npcId == REED and cond == 1 and st.getQuestItemsCount(BOLTERS_LIST) : 
         if st.getQuestItemsCount(REDSTONE_BEER) == 0 : 
           htmltext = "30520-01.htm" 
           st.giveItems(REDSTONE_BEER,1) 
           st.playSound("ItemSound.quest_itemget") 
         else: 
           htmltext = "30520-02.htm" 
       elif npcId == BRUNON and cond == 1 and st.getQuestItemsCount(BOLTERS_LIST) : 
         if st.getQuestItemsCount(MINERS_PICK) == 0 : 
           htmltext = "30526-01.htm" 
         else: 
           htmltext = "30526-03.htm" 
       if st.getQuestItemsCount(BOLTERS_LIST) and (st.getQuestItemsCount(MINING_BOOTS) + st.getQuestItemsCount(MINERS_PICK) + st.getQuestItemsCount(BOOMBOOM_POWDER) + st.getQuestItemsCount(REDSTONE_BEER) >= 4) : 
         st.set("cond","2") 
         st.set("id","2") 
         st.playSound("ItemSound.quest_middle") 
   return htmltext

QUEST     = Quest(5,qn,"Miner's Favor") 
CREATED   = State('Start',     QUEST) 
STARTING  = State('Starting',  QUEST) 
STARTED   = State('Started',   QUEST) 
COMPLETED = State('Completed', QUEST) 

QUEST.setInitialState(CREATED)
QUEST.addStartNpc(BOLTER) 

QUEST.addTalkId(BOLTER) 

QUEST.addTalkId(SHARI) 
QUEST.addTalkId(GARITA) 
QUEST.addTalkId(REED) 
QUEST.addTalkId(BRUNON) 
QUEST.addTalkId(BOLTER)