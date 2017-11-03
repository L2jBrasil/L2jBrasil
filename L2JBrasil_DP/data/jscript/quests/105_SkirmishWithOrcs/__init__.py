# Maked by Mr. Have fun! Version 0.3 updated by Sh1ning for www.l2jdp.com 
import sys
from com.it.br import Config
from com.it.br.gameserver.model.quest import State 
from com.it.br.gameserver.model.quest import QuestState 
from com.it.br.gameserver.model.quest.jython import QuestJython as JQuest 

qn = "105_SkirmishWithOrcs" 

KENDNELLS_ORDER1_ID = 1836 
KENDNELLS_ORDER2_ID = 1837 
KENDNELLS_ORDER3_ID = 1838 
KENDNELLS_ORDER4_ID = 1839 
KENDNELLS_ORDER5_ID = 1840 
KENDNELLS_ORDER6_ID = 1841 
KENDNELLS_ORDER7_ID = 1842 
KENDNELLS_ORDER8_ID = 1843 
KABOO_CHIEF_TORC1_ID = 1844 
KABOO_CHIEF_TORC2_ID = 1845 
RED_SUNSET_SWORD_ID = 981 
RED_SUNSET_STAFF_ID = 754 
SPIRITSHOT_NO_GRADE_FOR_BEGINNERS_ID = 5790 
SPIRITSHOT_NO_GRADE_ID = 2509 
SOULSHOT_NO_GRADE_FOR_BEGINNERS_ID = 5789
SOULSHOT_NO_GRADE_ID = 1835


class Quest (JQuest) : 

 def __init__(self,id,name,descr): 
    JQuest.__init__(self,id,name,descr)
    self.questItemIds = [KENDNELLS_ORDER1_ID, KENDNELLS_ORDER2_ID, KENDNELLS_ORDER3_ID, KENDNELLS_ORDER4_ID,
    					KENDNELLS_ORDER5_ID, KENDNELLS_ORDER6_ID, KENDNELLS_ORDER7_ID, KENDNELLS_ORDER8_ID,
    					KABOO_CHIEF_TORC1_ID, KABOO_CHIEF_TORC2_ID]

 def onEvent (self,event,st) : 
    htmltext = event 
    if event == "1" : 
      st.set("id","0") 
      st.set("cond","1") 
      st.setState(STARTED) 
      st.playSound(self.SOUND_QUEST_START) 
      htmltext = "30218-03.htm" 
      if st.getQuestItemsCount(KENDNELLS_ORDER1_ID)+st.getQuestItemsCount(KENDNELLS_ORDER2_ID)+st.getQuestItemsCount(KENDNELLS_ORDER3_ID)+st.getQuestItemsCount(KENDNELLS_ORDER4_ID) == 0 : 
        n = st.getRandom(100) 
        if n < 25 : 
          st.giveItems(KENDNELLS_ORDER1_ID,1) 
        elif n < 50 : 
          st.giveItems(KENDNELLS_ORDER2_ID,1) 
        elif n < 75 : 
          st.giveItems(KENDNELLS_ORDER3_ID,1) 
        else: 
          st.giveItems(KENDNELLS_ORDER4_ID,1) 
    return htmltext 


 def onTalk (self,npc,player): 

   npcId = npc.getNpcId() 
   htmltext = self.NO_QUEST 
   st = player.getQuestState(qn) 
   if not st : return htmltext 
   id = st.getState() 
   if id == CREATED : 
     st.setState(STARTING) 
     st.set("cond","0") 
     st.set("onlyone","0") 
     st.set("id","0") 
   if npcId == 30218 and st.getInt("cond")==0 and st.getInt("onlyone")==0 : 
      if st.getInt("cond") < 15 : 
        if player.getLevel() >= 10 and player.getRace().ordinal() == 1 : 
          htmltext = "30218-02.htm" 
          return htmltext 
        elif player.getRace().ordinal() != 1 : 
          htmltext = "30218-00.htm" 
          st.exitQuest(1) 
        else: 
          htmltext = "30218-10.htm" 
          st.exitQuest(1) 
      else: 
        htmltext = "30218-10.htm" 
        st.exitQuest(1) 
   elif npcId == 30218 and st.getInt("cond")==0 and st.getInt("onlyone")==1 : 
      htmltext = self.QUEST_DONE 
   elif npcId == 30218 and st.getInt("cond") : 
      if st.getQuestItemsCount(KABOO_CHIEF_TORC1_ID) : 
        htmltext = "30218-06.htm" 
        if st.getQuestItemsCount(KENDNELLS_ORDER1_ID) : 
          st.takeItems(KENDNELLS_ORDER1_ID,1) 
        if st.getQuestItemsCount(KENDNELLS_ORDER2_ID) : 
          st.takeItems(KENDNELLS_ORDER2_ID,1) 
        if st.getQuestItemsCount(KENDNELLS_ORDER3_ID) : 
          st.takeItems(KENDNELLS_ORDER3_ID,1) 
        if st.getQuestItemsCount(KENDNELLS_ORDER4_ID) : 
          st.takeItems(KENDNELLS_ORDER4_ID,1) 
        st.takeItems(KABOO_CHIEF_TORC1_ID,1) 
        n = st.getRandom(100) 
        if n < 25 : 
          st.giveItems(KENDNELLS_ORDER5_ID,1) 
        elif n < 50 : 
          st.giveItems(KENDNELLS_ORDER6_ID,1) 
        elif n < 75 : 
          st.giveItems(KENDNELLS_ORDER7_ID,1) 
        else: 
          st.giveItems(KENDNELLS_ORDER8_ID,1) 
      elif st.getQuestItemsCount(KENDNELLS_ORDER1_ID) or st.getQuestItemsCount(KENDNELLS_ORDER2_ID) or st.getQuestItemsCount(KENDNELLS_ORDER3_ID) or st.getQuestItemsCount(KENDNELLS_ORDER4_ID) : 
        htmltext = "30218-05.htm" 
      elif st.getQuestItemsCount(KABOO_CHIEF_TORC2_ID) : 
        if st.getInt("id") != 105 :
            st.set("id","105") 
            htmltext = "30218-08.htm" 
            if st.getQuestItemsCount(KENDNELLS_ORDER5_ID) :
                st.takeItems(KENDNELLS_ORDER5_ID,1)
            if st.getQuestItemsCount(KENDNELLS_ORDER6_ID) : 
                st.takeItems(KENDNELLS_ORDER6_ID,1) 
            if st.getQuestItemsCount(KENDNELLS_ORDER7_ID) : 
                st.takeItems(KENDNELLS_ORDER7_ID,1) 
            if st.getQuestItemsCount(KENDNELLS_ORDER8_ID) : 
                st.takeItems(KENDNELLS_ORDER8_ID,1) 
            st.takeItems(KABOO_CHIEF_TORC2_ID,1) 
            if player.getClassId().isMage() and st.getInt("onlyone") == 0:
                st.giveItems(RED_SUNSET_STAFF_ID,1)
                st.giveItems(SPIRITSHOT_NO_GRADE_ID,500)
                if player.getLevel() < 25 and player.isNewbie():
                    st.giveItems(SPIRITSHOT_NO_GRADE_FOR_BEGINNERS_ID,3000)
            elif st.getInt("onlyone") == 0 : 
                st.giveItems(RED_SUNSET_SWORD_ID,1)
                st.giveItems(SOULSHOT_NO_GRADE_ID,1000)
                if player.getLevel() < 25 and player.isNewbie():
                    st.giveItems(SOULSHOT_NO_GRADE_FOR_BEGINNERS_ID,7000)
            st.giveItems(1060,int(100*Config.RATE_QUESTS_REWARD))     # Lesser Healing Potions 
            for item in range(4412,4417) : 
                st.giveItems(item,int(10*Config.RATE_QUESTS_REWARD))   # Echo crystals 
            st.setState(COMPLETED) 
            st.playSound(self.SOUND_QUEST_DONE) 
            st.set("onlyone","1") 
            st.set("cond","0") 
      elif st.getQuestItemsCount(KENDNELLS_ORDER5_ID) or st.getQuestItemsCount(KENDNELLS_ORDER6_ID) or st.getQuestItemsCount(KENDNELLS_ORDER7_ID) or st.getQuestItemsCount(KENDNELLS_ORDER8_ID) : 
        htmltext = "30218-07.htm" 
   return htmltext 

 def onKill(self,npc,player,isPet): 
   st = player.getQuestState(qn) 
   if not st : return 
   if st.getState() != STARTED : return 
   npcId = npc.getNpcId() 
   if npcId == 27059 : 
    st.set("id","0") 
    if st.getInt("cond") == 1 : 
     if st.getQuestItemsCount(KENDNELLS_ORDER1_ID) and st.getQuestItemsCount(KABOO_CHIEF_TORC1_ID) == 0 : 
      st.giveItems(KABOO_CHIEF_TORC1_ID,1) 
      st.playSound(self.SOUND_QUEST_MIDDLE) 
   elif npcId == 27060 : 
    st.set("id","0") 
    if st.getInt("cond") == 1 : 
     if st.getQuestItemsCount(KENDNELLS_ORDER2_ID) and st.getQuestItemsCount(KABOO_CHIEF_TORC1_ID) == 0 : 
      st.giveItems(KABOO_CHIEF_TORC1_ID,1) 
      st.playSound(self.SOUND_QUEST_MIDDLE) 
   elif npcId == 27061 : 
    st.set("id","0") 
    if st.getInt("cond") == 1 : 
     if st.getQuestItemsCount(KENDNELLS_ORDER3_ID) and st.getQuestItemsCount(KABOO_CHIEF_TORC1_ID) == 0 : 
      st.giveItems(KABOO_CHIEF_TORC1_ID,1) 
      st.playSound(self.SOUND_QUEST_MIDDLE) 
   elif npcId == 27062 : 
    st.set("id","0") 
    if st.getInt("cond") == 1 : 
     if st.getQuestItemsCount(KENDNELLS_ORDER4_ID) and st.getQuestItemsCount(KABOO_CHIEF_TORC1_ID) == 0 : 
      st.giveItems(KABOO_CHIEF_TORC1_ID,1) 
      st.playSound(self.SOUND_QUEST_MIDDLE) 
   elif npcId == 27064 : 
    st.set("id","0") 
    if st.getInt("cond") == 1 : 
     if st.getQuestItemsCount(KENDNELLS_ORDER5_ID) and st.getQuestItemsCount(KABOO_CHIEF_TORC2_ID) == 0 : 
      st.giveItems(KABOO_CHIEF_TORC2_ID,1) 
      st.playSound(self.SOUND_QUEST_MIDDLE) 
   elif npcId == 27065 : 
    st.set("id","0") 
    if st.getInt("cond") == 1 : 
     if st.getQuestItemsCount(KENDNELLS_ORDER6_ID) and st.getQuestItemsCount(KABOO_CHIEF_TORC2_ID) == 0 : 
      st.giveItems(KABOO_CHIEF_TORC2_ID,1) 
      st.playSound(self.SOUND_QUEST_MIDDLE) 
   elif npcId == 27067 : 
    st.set("id","0") 
    if st.getInt("cond") == 1 : 
     if st.getQuestItemsCount(KENDNELLS_ORDER7_ID) and st.getQuestItemsCount(KABOO_CHIEF_TORC2_ID) == 0 : 
      st.giveItems(KABOO_CHIEF_TORC2_ID,1) 
      st.playSound(self.SOUND_QUEST_MIDDLE) 
   elif npcId == 27068 : 
    st.set("id","0") 
    if st.getInt("cond") == 1 : 
     if st.getQuestItemsCount(KENDNELLS_ORDER8_ID) and st.getQuestItemsCount(KABOO_CHIEF_TORC2_ID) == 0 : 
      st.giveItems(KABOO_CHIEF_TORC2_ID,1) 
      st.playSound(self.SOUND_QUEST_MIDDLE) 
   return 

QUEST       = Quest(105,qn,"Skirmish With Orcs") 
CREATED     = State('Start', QUEST) 
STARTING     = State('Starting', QUEST) 
STARTED     = State('Started', QUEST) 
COMPLETED   = State('Completed', QUEST) 

QUEST.setInitialState(CREATED) 
QUEST.addStartNpc(30218) 

QUEST.addTalkId(30218) 

QUEST.addKillId(27059) 
QUEST.addKillId(27060) 
QUEST.addKillId(27061) 
QUEST.addKillId(27062) 
QUEST.addKillId(27064) 
QUEST.addKillId(27065) 
QUEST.addKillId(27067) 
QUEST.addKillId(27068) 