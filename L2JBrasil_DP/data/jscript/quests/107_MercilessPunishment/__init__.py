# Maked by Mr. Have fun! Version 0.3 updated by Censor for www.l2jdp.com 
import sys 
from com.it.br import Config 
from com.it.br.gameserver.model.quest import State 
from com.it.br.gameserver.model.quest import QuestState 
from com.it.br.gameserver.model.quest.jython import QuestJython as JQuest 

qn = "107_MercilessPunishment" 

HATOSS_ORDER1_ID = 1553 
HATOSS_ORDER2_ID = 1554 
HATOSS_ORDER3_ID = 1555 
LETTER_TO_HUMAN_ID = 1557 
LETTER_TO_DARKELF_ID = 1556 
LETTER_TO_ELF_ID = 1558 
BUTCHER_ID = 1510 
LESSER_HEALING_ID = 1060 
CRYSTAL_BATTLE = 4412 
CRYSTAL_LOVE = 4413 
CRYSTAL_SOLITUDE = 4414 
CRYSTAL_FEAST = 4415 
CRYSTAL_CELEBRATION = 4416 
SOULSHOT_NO_GRADE_FOR_BEGINNERS_ID = 5789 

class Quest (JQuest) : 

 def __init__(self,id,name,descr): JQuest.__init__(self,id,name,descr) 

 def onEvent (self,event,st) : 
    htmltext = event 
    if event == "1" : 
          st.set("id","0") 
          htmltext = "30568-03.htm" 
          st.giveItems(HATOSS_ORDER1_ID,1) 
          st.set("cond","1") 
          st.setState(STARTED) 
          st.playSound("ItemSound.quest_accept") 
    elif event == "30568_1" : 
            htmltext = "30568-06.htm" 
            st.takeItems(HATOSS_ORDER2_ID,1) 
            st.takeItems(LETTER_TO_DARKELF_ID,1) 
            st.takeItems(LETTER_TO_HUMAN_ID,1) 
            st.takeItems(LETTER_TO_ELF_ID,1) 
            st.takeItems(HATOSS_ORDER1_ID,1) 
            st.takeItems(HATOSS_ORDER3_ID,1) 
            st.set("cond","0") 
            st.playSound("ItemSound.quest_giveup") 
    elif event == "30568_2" : 
            htmltext = "30568-07.htm" 
            st.takeItems(HATOSS_ORDER1_ID,1) 
            if st.getQuestItemsCount(HATOSS_ORDER2_ID) == 0 : 
              st.giveItems(HATOSS_ORDER2_ID,1) 
    elif event == "30568_3" : 
            htmltext = "30568-06.htm" 
            st.takeItems(HATOSS_ORDER1_ID,1) 
            st.takeItems(LETTER_TO_DARKELF_ID,1) 
            st.takeItems(LETTER_TO_HUMAN_ID,1) 
            st.takeItems(LETTER_TO_ELF_ID,1) 
            st.takeItems(HATOSS_ORDER2_ID,1) 
            st.takeItems(HATOSS_ORDER3_ID,1) 
            st.set("cond","0") 
            st.playSound("ItemSound.quest_giveup") 
    elif event == "30568_4" : 
            htmltext = "30568-09.htm" 
            st.takeItems(HATOSS_ORDER2_ID,1) 
            if st.getQuestItemsCount(HATOSS_ORDER3_ID) == 0 : 
              st.giveItems(HATOSS_ORDER3_ID,1) 
    return htmltext 


 def onTalk (self,npc,player): 

   npcId = npc.getNpcId() 
   htmltext = "<html><body>You are either not carrying out your quest or don't meet the criteria.</body></html>" 
   st = player.getQuestState(qn) 
   if not st : return htmltext 

   id = st.getState() 
   if id == CREATED : 
     st.setState(STARTING) 
     st.set("cond","0") 
     st.set("onlyone","0") 
     st.set("id","0") 
   if npcId == 30568 and st.getInt("cond")==0 and st.getInt("onlyone")==0 : 
        if st.getInt("cond") < 15 : 
          if player.getRace().ordinal() != 3 : 
            htmltext = "30568-00.htm" 
            st.exitQuest(1) 
          elif player.getLevel() >= 12 : 
            htmltext = "30568-02.htm" 
            return htmltext 
          else: 
            htmltext = "30568-01.htm" 
            st.exitQuest(1) 
        else: 
          htmltext = "30568-01.htm" 
          st.exitQuest(1) 
   elif npcId == 30568 and st.getInt("cond")==0 and st.getInt("onlyone")==1 : 
      htmltext = "<html><body>This quest has already been completed.</body></html>" 
   elif npcId == 30568 and st.getInt("cond")==1 and (st.getQuestItemsCount(HATOSS_ORDER1_ID) or st.getQuestItemsCount(HATOSS_ORDER2_ID) or st.getQuestItemsCount(HATOSS_ORDER3_ID)) and ((st.getQuestItemsCount(LETTER_TO_ELF_ID)+st.getQuestItemsCount(LETTER_TO_HUMAN_ID)+st.getQuestItemsCount(LETTER_TO_DARKELF_ID))==0) : 
          htmltext = "30568-04.htm" 
   elif npcId == 30568 and st.getInt("cond")==1 and (st.getQuestItemsCount(HATOSS_ORDER1_ID) or st.getQuestItemsCount(HATOSS_ORDER2_ID) or st.getQuestItemsCount(HATOSS_ORDER3_ID)) and ((st.getQuestItemsCount(LETTER_TO_ELF_ID)+st.getQuestItemsCount(LETTER_TO_HUMAN_ID)+st.getQuestItemsCount(LETTER_TO_DARKELF_ID))==1) : 
          htmltext = "30568-05.htm" 
   elif npcId == 30568 and st.getInt("cond")==1 and (st.getQuestItemsCount(HATOSS_ORDER1_ID) or st.getQuestItemsCount(HATOSS_ORDER2_ID) or st.getQuestItemsCount(HATOSS_ORDER3_ID)) and ((st.getQuestItemsCount(LETTER_TO_ELF_ID)+st.getQuestItemsCount(LETTER_TO_HUMAN_ID)+st.getQuestItemsCount(LETTER_TO_DARKELF_ID))==2) : 
          htmltext = "30568-08.htm" 
   elif npcId == 30568 and st.getInt("cond")==1 and (st.getQuestItemsCount(HATOSS_ORDER1_ID) or st.getQuestItemsCount(HATOSS_ORDER2_ID) or st.getQuestItemsCount(HATOSS_ORDER3_ID)) and ((st.getQuestItemsCount(LETTER_TO_ELF_ID)+st.getQuestItemsCount(LETTER_TO_HUMAN_ID)+st.getQuestItemsCount(LETTER_TO_DARKELF_ID))==3) and st.getInt("onlyone")==0 : 
          if st.getInt("id") != 107 : 
            st.set("id","107") 
            htmltext = "30568-10.htm" 
            st.takeItems(LETTER_TO_DARKELF_ID,1) 
            st.takeItems(LETTER_TO_HUMAN_ID,1) 
            st.takeItems(LETTER_TO_ELF_ID,1) 
            st.takeItems(HATOSS_ORDER3_ID,1)
            st.giveItems(LESSER_HEALING_ID,int(100*Config.RATE_QUESTS_REWARD)) 
            st.giveItems(BUTCHER_ID,1)
            st.giveItems(CRYSTAL_BATTLE,int(10*Config.RATE_QUESTS_REWARD)) 
            st.giveItems(CRYSTAL_LOVE,int(10*Config.RATE_QUESTS_REWARD)) 
            st.giveItems(CRYSTAL_SOLITUDE,int(10*Config.RATE_QUESTS_REWARD)) 
            st.giveItems(CRYSTAL_FEAST,int(10*Config.RATE_QUESTS_REWARD)) 
            st.giveItems(CRYSTAL_CELEBRATION,int(10*Config.RATE_QUESTS_REWARD))
            if player.getLevel() < 25 and st.getInt("onlyone") == 0 and player.isNewbie():
                st.giveItems(SOULSHOT_NO_GRADE_FOR_BEGINNERS_ID,7000) 
            st.set("cond","0") 
            st.setState(COMPLETED) 
            st.playSound("ItemSound.quest_finish") 
            st.set("onlyone","1") 
   elif npcId == 30580 and st.getInt("cond")==1 and id == STARTED and (st.getQuestItemsCount(HATOSS_ORDER1_ID) or st.getQuestItemsCount(HATOSS_ORDER2_ID) or st.getQuestItemsCount(HATOSS_ORDER3_ID)) : 
          htmltext = "30580-01.htm" 
   return htmltext 

 def onKill(self,npc,player,isPet): 
   st = player.getQuestState(qn) 
   if not st : return 
   if st.getState() != STARTED : return 
    
   npcId = npc.getNpcId() 
   if npcId == 27041 : 
        st.set("id","0") 
        if st.getInt("cond") == 1 : 
          if st.getQuestItemsCount(HATOSS_ORDER1_ID) and st.getQuestItemsCount(LETTER_TO_HUMAN_ID) == 0 : 
            st.giveItems(LETTER_TO_HUMAN_ID,1) 
            st.playSound("ItemSound.quest_itemget") 
          if st.getQuestItemsCount(HATOSS_ORDER2_ID) and st.getQuestItemsCount(LETTER_TO_DARKELF_ID) == 0 : 
            st.giveItems(LETTER_TO_DARKELF_ID,1) 
            st.playSound("ItemSound.quest_itemget") 
          if st.getQuestItemsCount(HATOSS_ORDER3_ID) and st.getQuestItemsCount(LETTER_TO_ELF_ID) == 0 : 
            st.giveItems(LETTER_TO_ELF_ID,1) 
            st.playSound("ItemSound.quest_itemget") 
   return 

QUEST       = Quest(107,qn,"Merciless Punishment") 
CREATED     = State('Start', QUEST) 
STARTING     = State('Starting', QUEST) 
STARTED     = State('Started', QUEST) 
COMPLETED   = State('Completed', QUEST) 


QUEST.setInitialState(CREATED) 
QUEST.addStartNpc(30568) 

QUEST.addTalkId(30568) 

QUEST.addTalkId(30580) 

QUEST.addKillId(27041) 