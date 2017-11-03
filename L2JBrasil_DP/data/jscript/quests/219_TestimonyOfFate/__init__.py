# Made by Mr. Have fun! Version 0.2
import sys
from com.it.br.gameserver.model.quest import State
from com.it.br.gameserver.model.quest import QuestState
from com.it.br.gameserver.model.quest.jython import QuestJython as JQuest

qn = "219_TestimonyOfFate"

MARK_OF_FATE_ID = 3172
KAIRAS_LETTER1_ID = 3173
METHEUS_FUNERAL_JAR_ID = 3174
KASANDRAS_REMAINS_ID = 3175
HERBALISM_TEXTBOOK_ID = 3176
IXIAS_LIST_ID = 3177
MEDUSA_ICHOR_ID = 3178
M_SPIDER_FLUIDS_ID = 3179
DEAD_SEEKER_DUNG_ID = 3180
TYRANTS_BLOOD_ID = 3181
NIGHTSHADE_ROOT_ID = 3182
BELLADONNA_ID = 3183
ALDERS_SKULL1_ID = 3184
ALDERS_SKULL2_ID = 3185
ALDERS_RECEIPT_ID = 3186
ROAD_RATMAN_HEAD_ID = 3291
LETO_LIZARDMAN_FANG1_ID = 3292
KAIRAS_RECOMMEND_ID = 3189
KAIRAS_INSTRUCTIONS_ID = 3188
REVELATIONS_MANUSCRIPT_ID = 3187
THIFIELS_LETTER_ID = 3191
PALUS_CHARM_ID = 3190
ARKENIAS_LETTER_ID = 1246
ARKENIAS_NOTE_ID = 3192
RED_FAIRY_DUST_ID = 3198
TIMIRIRAN_SAP_ID = 3201
PIXY_GARNET_ID = 3193
GRANDIS_SKULL_ID = 3194
KARUL_BUGBEAR_SKULL_ID = 3195
BREKA_OVERLORD_SKULL_ID = 3196
LETO_OVERLORD_SKULL_ID = 3197
BLACK_WILLOW_LEAF_ID = 3200
TIMIRIRAN_SEED_ID = 3199

class Quest (JQuest) :

 def __init__(self,id,name,descr): JQuest.__init__(self,id,name,descr)

 def onEvent (self,event,st) :
    htmltext = event
    if event == "1" :
      htmltext = "30476-05.htm"
      st.set("cond","1")
      st.setState(STARTED)
      st.playSound("ItemSound.quest_accept")
      st.giveItems(KAIRAS_LETTER1_ID,1)
    elif event == "30476_1" :
          htmltext = "30476-04.htm"
    elif event == "30476_2" :
          if st.getPlayer().getLevel() >= 38 :
            htmltext = "30476-12.htm"
            st.giveItems(KAIRAS_RECOMMEND_ID,1)
            st.takeItems(REVELATIONS_MANUSCRIPT_ID,1)
          else:
            htmltext = "30476-13.htm"
            st.giveItems(KAIRAS_INSTRUCTIONS_ID,1)
            st.takeItems(REVELATIONS_MANUSCRIPT_ID,1)
    elif event == "30114_1" :
          htmltext = "30114-02.htm"
    elif event == "30114_2" :
          htmltext = "30114-03.htm"
    elif event == "30114_3" :
          htmltext = "30114-04.htm"
          st.giveItems(ALDERS_RECEIPT_ID,1)
          st.takeItems(ALDERS_SKULL2_ID,1)
    elif event == "30419_1" :
          htmltext = "30419-02.htm"
          st.giveItems(ARKENIAS_NOTE_ID,1)
          st.takeItems(THIFIELS_LETTER_ID,1)
    elif event == "30419_2" :
          htmltext = "30419-05.htm"
          st.giveItems(ARKENIAS_LETTER_ID,1)
          st.takeItems(ARKENIAS_NOTE_ID,1)
          st.takeItems(RED_FAIRY_DUST_ID,1)
          st.takeItems(TIMIRIRAN_SAP_ID,1)
    elif event == "31845_1" :
          htmltext = "31845-02.htm"
          st.giveItems(PIXY_GARNET_ID,1)
    elif event == "31850_1" :
          htmltext = "31850-02.htm"
          st.giveItems(TIMIRIRAN_SEED_ID,1)
    return htmltext


 def onTalk (self,npc,player):
   htmltext = "<html><body>You are either not carrying out your quest or don't meet the criteria.</body></html>"
   st = player.getQuestState(qn)
   if not st : return htmltext

   npcId = npc.getNpcId()
   id = st.getState()
   if npcId != 30476 and id != STARTED : return htmltext

   if id == CREATED :
     st.setState(STARTING)
     st.set("cond","0")
     st.set("onlyone","0")
     st.set("id","0")
   if npcId == 30476 and st.getInt("cond")==0 and st.getInt("onlyone")==0 :
      if st.getInt("cond") < 15 :
        if player.getRace().ordinal() == 2 and player.getLevel() >= 37 :
          htmltext = "30476-03.htm"
        elif player.getRace().ordinal() == 2 :
          htmltext = "30476-02.htm"
          st.exitQuest(1)
        else:
          htmltext = "30476-01.htm"
          st.exitQuest(1)
      else:    
        htmltext = "30476-01.htm"
        st.exitQuest(1)
   elif npcId == 30476 and st.getInt("cond")==0 and st.getInt("onlyone")==1 :
      htmltext = "<html><body>This quest has already been completed.</body></html>"
   elif npcId == 30476 and st.getInt("cond")==1 and st.getQuestItemsCount(KAIRAS_LETTER1_ID) :
      htmltext = "30476-06.htm"
   elif npcId == 30476 and st.getInt("cond")==1 and (st.getQuestItemsCount(METHEUS_FUNERAL_JAR_ID) or st.getQuestItemsCount(KASANDRAS_REMAINS_ID)) :
      htmltext = "30476-07.htm"
   elif npcId == 30476 and st.getInt("cond")==1 and (st.getQuestItemsCount(HERBALISM_TEXTBOOK_ID) or st.getQuestItemsCount(IXIAS_LIST_ID)) :
      htmltext = "30476-08.htm"
   elif npcId == 30476 and st.getInt("cond")==1 and st.getQuestItemsCount(ALDERS_SKULL1_ID) :
      htmltext = "30476-09.htm"
      st.giveItems(ALDERS_SKULL2_ID,1)
      st.takeItems(ALDERS_SKULL1_ID,1)
      st.addSpawn(30613,78977,149036,-3597,300000)
   elif npcId == 30476 and st.getInt("cond")==1 and (st.getQuestItemsCount(ALDERS_SKULL2_ID) or st.getQuestItemsCount(ALDERS_RECEIPT_ID)) :
      htmltext = "30476-10.htm"
   elif npcId == 30476 and st.getInt("cond")==1 and st.getQuestItemsCount(REVELATIONS_MANUSCRIPT_ID) :
      htmltext = "30476-11.htm"
   elif npcId == 30476 and st.getInt("cond")==1 and st.getQuestItemsCount(KAIRAS_INSTRUCTIONS_ID) and player.getLevel()<38 :
      htmltext = "30476-14.htm"
   elif npcId == 30476 and st.getInt("cond")==1 and st.getQuestItemsCount(KAIRAS_INSTRUCTIONS_ID) and player.getLevel()>=38 :
      htmltext = "30476-15.htm"
      st.giveItems(KAIRAS_RECOMMEND_ID,1)
      st.takeItems(KAIRAS_INSTRUCTIONS_ID,1)
   elif npcId == 30476 and st.getInt("cond")==1 and st.getQuestItemsCount(KAIRAS_RECOMMEND_ID) and player.getLevel()>=38 :
      htmltext = "30476-16.htm"
   elif npcId == 30476 and st.getInt("cond")==1 and st.getQuestItemsCount(PALUS_CHARM_ID) :
      htmltext = "30476-17.htm"
   elif npcId == 30614 and st.getInt("cond")>=1 and st.getQuestItemsCount(KAIRAS_LETTER1_ID) :
      htmltext = "30614-01.htm"
      st.giveItems(METHEUS_FUNERAL_JAR_ID,1)
      st.takeItems(KAIRAS_LETTER1_ID,1)
   elif npcId == 30614 and st.getInt("cond")==1 and st.getQuestItemsCount(METHEUS_FUNERAL_JAR_ID) and st.getQuestItemsCount(KASANDRAS_REMAINS_ID)==0 :
      htmltext = "30614-02.htm"
   elif npcId == 30614 and st.getInt("cond")==1 and st.getQuestItemsCount(METHEUS_FUNERAL_JAR_ID)==0 and st.getQuestItemsCount(KASANDRAS_REMAINS_ID) :
      htmltext = "30614-03.htm"
      st.giveItems(HERBALISM_TEXTBOOK_ID,1)
      st.takeItems(KASANDRAS_REMAINS_ID,1)
   elif npcId == 30614 and st.getInt("cond")==1 and (st.getQuestItemsCount(HERBALISM_TEXTBOOK_ID) or st.getQuestItemsCount(IXIAS_LIST_ID)) :
      htmltext = "30614-04.htm"
   elif npcId == 30614 and st.getInt("cond")==1 and st.getQuestItemsCount(BELLADONNA_ID) :
      htmltext = "30614-05.htm"
      st.giveItems(ALDERS_SKULL1_ID,1)
      st.takeItems(BELLADONNA_ID,1)
   elif npcId == 30614 and st.getInt("cond")==1 and (st.getQuestItemsCount(ALDERS_SKULL1_ID) or st.getQuestItemsCount(ALDERS_SKULL2_ID) or st.getQuestItemsCount(ALDERS_RECEIPT_ID) or st.getQuestItemsCount(REVELATIONS_MANUSCRIPT_ID) or st.getQuestItemsCount(KAIRAS_INSTRUCTIONS_ID) or st.getQuestItemsCount(KAIRAS_RECOMMEND_ID)) :
      htmltext = "30614-06.htm"
   elif npcId == 30463 and st.getInt("cond")==1 and st.getQuestItemsCount(HERBALISM_TEXTBOOK_ID) :
      htmltext = "30463-01.htm"
      st.giveItems(IXIAS_LIST_ID,1)
      st.takeItems(HERBALISM_TEXTBOOK_ID,1)
   elif npcId == 30463 and st.getInt("cond")==1 and st.getQuestItemsCount(IXIAS_LIST_ID) and (st.getQuestItemsCount(MEDUSA_ICHOR_ID)<10 or st.getQuestItemsCount(M_SPIDER_FLUIDS_ID)<10 or st.getQuestItemsCount(DEAD_SEEKER_DUNG_ID)<10 or st.getQuestItemsCount(TYRANTS_BLOOD_ID)<10 or st.getQuestItemsCount(NIGHTSHADE_ROOT_ID)<10) :
      htmltext = "30463-02.htm"
   elif npcId == 30463 and st.getInt("cond")==1 and st.getQuestItemsCount(IXIAS_LIST_ID) and st.getQuestItemsCount(MEDUSA_ICHOR_ID)>=10 and st.getQuestItemsCount(M_SPIDER_FLUIDS_ID)>=10 and st.getQuestItemsCount(DEAD_SEEKER_DUNG_ID)>=10 and st.getQuestItemsCount(TYRANTS_BLOOD_ID)>=10 and st.getQuestItemsCount(NIGHTSHADE_ROOT_ID)>=10 :
      htmltext = "30463-03.htm"
      st.giveItems(BELLADONNA_ID,1)
      st.takeItems(IXIAS_LIST_ID,1)
      st.takeItems(MEDUSA_ICHOR_ID,st.getQuestItemsCount(MEDUSA_ICHOR_ID))
      st.takeItems(TYRANTS_BLOOD_ID,st.getQuestItemsCount(TYRANTS_BLOOD_ID))
      st.takeItems(M_SPIDER_FLUIDS_ID,st.getQuestItemsCount(M_SPIDER_FLUIDS_ID))
      st.takeItems(DEAD_SEEKER_DUNG_ID,st.getQuestItemsCount(DEAD_SEEKER_DUNG_ID))
      st.takeItems(NIGHTSHADE_ROOT_ID,st.getQuestItemsCount(NIGHTSHADE_ROOT_ID))
   elif npcId == 30463 and st.getInt("cond")==1 and st.getQuestItemsCount(BELLADONNA_ID) :
      htmltext = "30463-04.htm"
   elif npcId == 30463 and st.getInt("cond")==1 and (st.getQuestItemsCount(ALDERS_SKULL1_ID) or st.getQuestItemsCount(ALDERS_SKULL2_ID) or st.getQuestItemsCount(ALDERS_RECEIPT_ID) or st.getQuestItemsCount(REVELATIONS_MANUSCRIPT_ID) or st.getQuestItemsCount(KAIRAS_INSTRUCTIONS_ID) or st.getQuestItemsCount(KAIRAS_RECOMMEND_ID)) :
      htmltext = "30463-05.htm"
   elif npcId == 30613 and st.getInt("cond")==1 and (st.getQuestItemsCount(ALDERS_SKULL1_ID) or st.getQuestItemsCount(ALDERS_SKULL2_ID)) :
      htmltext = "30613-02.htm"
   elif npcId == 30114 and st.getInt("cond")==1 and st.getQuestItemsCount(ALDERS_SKULL2_ID) :
      htmltext = "30114-01.htm"
   elif npcId == 30114 and st.getInt("cond")==1 and st.getQuestItemsCount(ALDERS_RECEIPT_ID) :
      htmltext = "30114-05.htm"
   elif npcId == 30114 and st.getInt("cond")==1 and (st.getQuestItemsCount(REVELATIONS_MANUSCRIPT_ID) or st.getQuestItemsCount(KAIRAS_INSTRUCTIONS_ID) or st.getQuestItemsCount(KAIRAS_RECOMMEND_ID)) :
      htmltext = "30114-06.htm"
   elif npcId == 30210 and st.getInt("cond")==1 and st.getQuestItemsCount(ALDERS_RECEIPT_ID) :
      htmltext = "30210-01.htm"
      st.giveItems(REVELATIONS_MANUSCRIPT_ID,1)
      st.takeItems(ALDERS_RECEIPT_ID,1)
   elif npcId == 30210 and st.getInt("cond")==1 and st.getQuestItemsCount(REVELATIONS_MANUSCRIPT_ID) :
      htmltext = "30210-02.htm"
   elif npcId == 30358 and st.getInt("cond")==1 and st.getQuestItemsCount(KAIRAS_RECOMMEND_ID) :
      htmltext = "30358-01.htm"
      st.giveItems(THIFIELS_LETTER_ID,1)
      st.giveItems(PALUS_CHARM_ID,1)
      st.takeItems(KAIRAS_RECOMMEND_ID,1)
   elif npcId == 30358 and st.getInt("cond")==1 and st.getQuestItemsCount(PALUS_CHARM_ID) and st.getQuestItemsCount(THIFIELS_LETTER_ID) :
      htmltext = "30358-02.htm"
   elif npcId == 30358 and st.getInt("cond")==1 and st.getQuestItemsCount(PALUS_CHARM_ID) and st.getQuestItemsCount(ARKENIAS_NOTE_ID) :
      htmltext = "30358-03.htm"
   elif npcId == 30358 and st.getInt("cond")==1 and st.getQuestItemsCount(PALUS_CHARM_ID) and st.getQuestItemsCount(ARKENIAS_LETTER_ID) :
      st.addExpAndSp(68183,1750)
      st.giveItems(7562,16)
      htmltext = "30358-04.htm"
      st.giveItems(MARK_OF_FATE_ID,1)
      st.takeItems(ARKENIAS_LETTER_ID,1)
      st.takeItems(PALUS_CHARM_ID,1)
      st.set("cond","0")
      st.setState(COMPLETED)
      st.playSound("ItemSound.quest_finish")
      st.set("onlyone","1")
   elif npcId == 30419 and st.getInt("cond")==1 and st.getQuestItemsCount(PALUS_CHARM_ID) and st.getQuestItemsCount(THIFIELS_LETTER_ID) :
      htmltext = "30419-01.htm"
   elif npcId == 30419 and st.getInt("cond")==1 and st.getQuestItemsCount(PALUS_CHARM_ID) and st.getQuestItemsCount(ARKENIAS_NOTE_ID) and (st.getQuestItemsCount(RED_FAIRY_DUST_ID)<1 or st.getQuestItemsCount(TIMIRIRAN_SAP_ID)<1) :
      htmltext = "30419-03.htm"
   elif npcId == 30419 and st.getInt("cond")==1 and st.getQuestItemsCount(PALUS_CHARM_ID) and st.getQuestItemsCount(ARKENIAS_NOTE_ID) and (st.getQuestItemsCount(RED_FAIRY_DUST_ID)>=1 and st.getQuestItemsCount(TIMIRIRAN_SAP_ID)>=1) :
      htmltext = "30419-04.htm"
   elif npcId == 30419 and st.getInt("cond")==1 and st.getQuestItemsCount(PALUS_CHARM_ID) and st.getQuestItemsCount(ARKENIAS_LETTER_ID) :
      htmltext = "30419-06.htm"
   elif npcId == 31845 and st.getInt("cond")==1 and st.getQuestItemsCount(PALUS_CHARM_ID) and st.getQuestItemsCount(ARKENIAS_NOTE_ID) and st.getQuestItemsCount(RED_FAIRY_DUST_ID)==0 and st.getQuestItemsCount(PIXY_GARNET_ID)==0 :
      htmltext = "31845-01.htm"
   elif npcId == 31845 and st.getInt("cond")==1 and st.getQuestItemsCount(PALUS_CHARM_ID) and st.getQuestItemsCount(ARKENIAS_NOTE_ID) and st.getQuestItemsCount(RED_FAIRY_DUST_ID)==0 and st.getQuestItemsCount(PIXY_GARNET_ID) and (st.getQuestItemsCount(GRANDIS_SKULL_ID)<10 or st.getQuestItemsCount(KARUL_BUGBEAR_SKULL_ID)<10 or st.getQuestItemsCount(BREKA_OVERLORD_SKULL_ID)<10 or st.getQuestItemsCount(LETO_OVERLORD_SKULL_ID)<10) :
      htmltext = "31845-03.htm"
   elif npcId == 31845 and st.getInt("cond")==1 and st.getQuestItemsCount(PALUS_CHARM_ID) and st.getQuestItemsCount(ARKENIAS_NOTE_ID) and st.getQuestItemsCount(RED_FAIRY_DUST_ID)==0 and st.getQuestItemsCount(PIXY_GARNET_ID) and st.getQuestItemsCount(GRANDIS_SKULL_ID)>=10 and st.getQuestItemsCount(KARUL_BUGBEAR_SKULL_ID)>=10 and st.getQuestItemsCount(BREKA_OVERLORD_SKULL_ID)>=10 and st.getQuestItemsCount(LETO_OVERLORD_SKULL_ID)>=10 :
      htmltext = "31845-04.htm"
      st.giveItems(RED_FAIRY_DUST_ID,1)
      st.takeItems(PIXY_GARNET_ID,1)
      st.takeItems(GRANDIS_SKULL_ID,st.getQuestItemsCount(GRANDIS_SKULL_ID))
      st.takeItems(KARUL_BUGBEAR_SKULL_ID,st.getQuestItemsCount(KARUL_BUGBEAR_SKULL_ID))
      st.takeItems(BREKA_OVERLORD_SKULL_ID,st.getQuestItemsCount(BREKA_OVERLORD_SKULL_ID))
      st.takeItems(LETO_OVERLORD_SKULL_ID,st.getQuestItemsCount(LETO_OVERLORD_SKULL_ID))
   elif npcId == 31845 and st.getInt("cond")==1 and st.getQuestItemsCount(PALUS_CHARM_ID) and st.getQuestItemsCount(ARKENIAS_NOTE_ID) and st.getQuestItemsCount(RED_FAIRY_DUST_ID) and st.getQuestItemsCount(PIXY_GARNET_ID)==0 :
      htmltext = "31845-05.htm"
   elif npcId == 31850 and st.getInt("cond")==1 and st.getQuestItemsCount(PALUS_CHARM_ID) and st.getQuestItemsCount(ARKENIAS_NOTE_ID) and st.getQuestItemsCount(TIMIRIRAN_SAP_ID)==0 and st.getQuestItemsCount(TIMIRIRAN_SEED_ID)==0 :
      htmltext = "31850-01.htm"
   elif npcId == 31850 and st.getInt("cond")==1 and st.getQuestItemsCount(PALUS_CHARM_ID) and st.getQuestItemsCount(ARKENIAS_NOTE_ID) and st.getQuestItemsCount(TIMIRIRAN_SAP_ID)==0 and st.getQuestItemsCount(TIMIRIRAN_SEED_ID) and st.getQuestItemsCount(BLACK_WILLOW_LEAF_ID)==0 :
      htmltext = "31850-03.htm"
   elif npcId == 31850 and st.getInt("cond")==1 and st.getQuestItemsCount(PALUS_CHARM_ID) and st.getQuestItemsCount(ARKENIAS_NOTE_ID) and st.getQuestItemsCount(TIMIRIRAN_SAP_ID)==0 and st.getQuestItemsCount(TIMIRIRAN_SEED_ID) and st.getQuestItemsCount(BLACK_WILLOW_LEAF_ID) :
      htmltext = "31850-04.htm"
      st.giveItems(TIMIRIRAN_SAP_ID,1)
      st.takeItems(BLACK_WILLOW_LEAF_ID,1)
      st.takeItems(TIMIRIRAN_SEED_ID,1)
   elif npcId == 31850 and st.getInt("cond")==1 and st.getQuestItemsCount(PALUS_CHARM_ID) and st.getQuestItemsCount(ARKENIAS_NOTE_ID) and st.getQuestItemsCount(TIMIRIRAN_SAP_ID) and st.getQuestItemsCount(TIMIRIRAN_SEED_ID)==0 :
      htmltext = "31850-05.htm"
   return htmltext

 def onKill(self,npc,player,isPet):
   st = player.getQuestState(qn)
   if not st : return
   if st.getState() != STARTED : return

   npcId = npc.getNpcId()
   if npcId == 20144 :
      if st.getInt("cond") and st.getQuestItemsCount(METHEUS_FUNERAL_JAR_ID) and st.getQuestItemsCount(KASANDRAS_REMAINS_ID) == 0 :
        st.giveItems(KASANDRAS_REMAINS_ID,1)
        st.takeItems(METHEUS_FUNERAL_JAR_ID,1)
        st.playSound("Itemsound.quest_middle")
   elif npcId == 20158 :
      if st.getInt("cond") and st.getQuestItemsCount(IXIAS_LIST_ID) and st.getQuestItemsCount(MEDUSA_ICHOR_ID) < 10 :
        if st.getRandom(2) == 1 :
          if st.getQuestItemsCount(MEDUSA_ICHOR_ID) == 9 :
            st.giveItems(MEDUSA_ICHOR_ID,1)
            st.playSound("Itemsound.quest_middle")
          else:
            st.giveItems(MEDUSA_ICHOR_ID,1)
            st.playSound("Itemsound.quest_itemget")
   elif npcId == 20233 :
      if st.getInt("cond") and st.getQuestItemsCount(IXIAS_LIST_ID) and st.getQuestItemsCount(M_SPIDER_FLUIDS_ID) < 10 :
        if st.getRandom(2) == 1 :
          if st.getQuestItemsCount(M_SPIDER_FLUIDS_ID) == 9 :
            st.giveItems(M_SPIDER_FLUIDS_ID,1)
            st.playSound("Itemsound.quest_middle")
          else:
            st.giveItems(M_SPIDER_FLUIDS_ID,1)
            st.playSound("Itemsound.quest_itemget")
   elif npcId == 20202 :
      if st.getInt("cond") and st.getQuestItemsCount(IXIAS_LIST_ID) and st.getQuestItemsCount(DEAD_SEEKER_DUNG_ID) < 10 :
        if st.getRandom(2) == 1 :
          if st.getQuestItemsCount(DEAD_SEEKER_DUNG_ID) == 9 :
            st.giveItems(DEAD_SEEKER_DUNG_ID,1)
            st.playSound("Itemsound.quest_middle")
          else:
            st.giveItems(DEAD_SEEKER_DUNG_ID,1)
            st.playSound("Itemsound.quest_itemget")
   elif npcId == 20192 :
      if st.getInt("cond") and st.getQuestItemsCount(IXIAS_LIST_ID) and st.getQuestItemsCount(TYRANTS_BLOOD_ID) < 10 :
        if st.getRandom(2) == 1 :
          if st.getQuestItemsCount(TYRANTS_BLOOD_ID) == 9 :
            st.giveItems(TYRANTS_BLOOD_ID,1)
            st.playSound("Itemsound.quest_middle")
          else:
            st.giveItems(TYRANTS_BLOOD_ID,1)
            st.playSound("Itemsound.quest_itemget")
   elif npcId == 20193 :
      if st.getInt("cond") and st.getQuestItemsCount(IXIAS_LIST_ID) and st.getQuestItemsCount(TYRANTS_BLOOD_ID) < 10 :
        if st.getRandom(10) < 6 :
          if st.getQuestItemsCount(TYRANTS_BLOOD_ID) == 9 :
            st.giveItems(TYRANTS_BLOOD_ID,1)
            st.playSound("Itemsound.quest_middle")
          else:
            st.giveItems(TYRANTS_BLOOD_ID,1)
            st.playSound("Itemsound.quest_itemget")
   elif npcId == 20230 :
      if st.getInt("cond") and st.getQuestItemsCount(IXIAS_LIST_ID) and st.getQuestItemsCount(NIGHTSHADE_ROOT_ID) < 10 :
        if st.getRandom(10) < 3 :
          if st.getQuestItemsCount(NIGHTSHADE_ROOT_ID) == 9 :
            st.giveItems(NIGHTSHADE_ROOT_ID,1)
            st.playSound("Itemsound.quest_middle")
          else:
            st.giveItems(NIGHTSHADE_ROOT_ID,1)
            st.playSound("Itemsound.quest_itemget")
   elif npcId == 20157 :
      if st.getInt("cond") and st.getQuestItemsCount(IXIAS_LIST_ID) and st.getQuestItemsCount(NIGHTSHADE_ROOT_ID) < 10 :
        if st.getRandom(10) < 4 :
          if st.getQuestItemsCount(NIGHTSHADE_ROOT_ID) == 9 :
            st.giveItems(NIGHTSHADE_ROOT_ID,1)
            st.playSound("Itemsound.quest_middle")
          else:
            st.giveItems(NIGHTSHADE_ROOT_ID,1)
            st.playSound("Itemsound.quest_itemget")
   elif npcId == 20232 :
      if st.getInt("cond") and st.getQuestItemsCount(IXIAS_LIST_ID) and st.getQuestItemsCount(NIGHTSHADE_ROOT_ID) < 10 :
        if st.getRandom(10) < 5 :
          if st.getQuestItemsCount(NIGHTSHADE_ROOT_ID) == 9 :
            st.giveItems(NIGHTSHADE_ROOT_ID,1)
            st.playSound("Itemsound.quest_middle")
          else:
            st.giveItems(NIGHTSHADE_ROOT_ID,1)
            st.playSound("Itemsound.quest_itemget")
   elif npcId == 20234 :
      if st.getInt("cond") and st.getQuestItemsCount(IXIAS_LIST_ID) and st.getQuestItemsCount(NIGHTSHADE_ROOT_ID) < 10 :
        if st.getRandom(10) < 6 :
          if st.getQuestItemsCount(NIGHTSHADE_ROOT_ID) == 9 :
            st.giveItems(NIGHTSHADE_ROOT_ID,1)
            st.playSound("Itemsound.quest_middle")
          else:
            st.giveItems(NIGHTSHADE_ROOT_ID,1)
            st.playSound("Itemsound.quest_itemget")
   elif npcId == 27079 :
      if st.getInt("cond") and st.getQuestItemsCount(PALUS_CHARM_ID) and st.getQuestItemsCount(ARKENIAS_NOTE_ID) and st.getQuestItemsCount(TIMIRIRAN_SAP_ID) == 0 and st.getQuestItemsCount(TIMIRIRAN_SEED_ID) and st.getQuestItemsCount(BLACK_WILLOW_LEAF_ID) == 0 :
        st.giveItems(BLACK_WILLOW_LEAF_ID,1)
        st.playSound("Itemsound.quest_middle")
   elif npcId == 20554 :
      if st.getInt("cond") and st.getQuestItemsCount(PALUS_CHARM_ID) and st.getQuestItemsCount(ARKENIAS_NOTE_ID) and st.getQuestItemsCount(RED_FAIRY_DUST_ID) == 0 and st.getQuestItemsCount(PIXY_GARNET_ID) and st.getQuestItemsCount(GRANDIS_SKULL_ID) < 10 :
        if st.getQuestItemsCount(GRANDIS_SKULL_ID) == 9 :
          st.giveItems(GRANDIS_SKULL_ID,1)
          st.playSound("Itemsound.quest_middle")
        else:
          st.giveItems(GRANDIS_SKULL_ID,1)
          st.playSound("Itemsound.quest_itemget")
   elif npcId == 20600 :
      if st.getInt("cond") and st.getQuestItemsCount(PALUS_CHARM_ID) and st.getQuestItemsCount(ARKENIAS_NOTE_ID) and st.getQuestItemsCount(RED_FAIRY_DUST_ID) == 0 and st.getQuestItemsCount(PIXY_GARNET_ID) and st.getQuestItemsCount(KARUL_BUGBEAR_SKULL_ID) < 10 :
        if st.getQuestItemsCount(KARUL_BUGBEAR_SKULL_ID) == 9 :
          st.giveItems(KARUL_BUGBEAR_SKULL_ID,1)
          st.playSound("Itemsound.quest_middle")
        else:
          st.giveItems(KARUL_BUGBEAR_SKULL_ID,1)
          st.playSound("Itemsound.quest_itemget")
   elif npcId == 20270 :
      if st.getInt("cond") and st.getQuestItemsCount(PALUS_CHARM_ID) and st.getQuestItemsCount(ARKENIAS_NOTE_ID) and st.getQuestItemsCount(RED_FAIRY_DUST_ID) == 0 and st.getQuestItemsCount(PIXY_GARNET_ID) and st.getQuestItemsCount(BREKA_OVERLORD_SKULL_ID) < 10 :
        if st.getQuestItemsCount(BREKA_OVERLORD_SKULL_ID) == 9 :
          st.giveItems(BREKA_OVERLORD_SKULL_ID,1)
          st.playSound("Itemsound.quest_middle")
        else:
          st.giveItems(BREKA_OVERLORD_SKULL_ID,1)
          st.playSound("Itemsound.quest_itemget")
   elif npcId == 20582 :
      if st.getInt("cond") and st.getQuestItemsCount(PALUS_CHARM_ID) and st.getQuestItemsCount(ARKENIAS_NOTE_ID) and st.getQuestItemsCount(RED_FAIRY_DUST_ID) == 0 and st.getQuestItemsCount(PIXY_GARNET_ID) and st.getQuestItemsCount(LETO_OVERLORD_SKULL_ID) < 10 :
        if st.getQuestItemsCount(LETO_OVERLORD_SKULL_ID) == 9 :
          st.giveItems(LETO_OVERLORD_SKULL_ID,1)
          st.playSound("Itemsound.quest_middle")
        else:
          st.giveItems(LETO_OVERLORD_SKULL_ID,1)
          st.playSound("Itemsound.quest_itemget")
   return

QUEST       = Quest(219,qn,"Testimony Of Fate")
CREATED     = State('Start', QUEST)
STARTING     = State('Starting', QUEST)
STARTED     = State('Started', QUEST)
COMPLETED   = State('Completed', QUEST)


QUEST.setInitialState(CREATED)
QUEST.addStartNpc(30476)

QUEST.addTalkId(30476)

QUEST.addTalkId(31845)
QUEST.addTalkId(31850)
QUEST.addTalkId(30114)
QUEST.addTalkId(30210)
QUEST.addTalkId(30358)
QUEST.addTalkId(30419)
QUEST.addTalkId(30463)
QUEST.addTalkId(30613)
QUEST.addTalkId(30614)

QUEST.addKillId(20144)
QUEST.addKillId(20157)
QUEST.addKillId(20158)
QUEST.addKillId(20192)
QUEST.addKillId(20193)
QUEST.addKillId(20202)
QUEST.addKillId(20230)
QUEST.addKillId(20232)
QUEST.addKillId(20233)
QUEST.addKillId(20234)
QUEST.addKillId(20270)
QUEST.addKillId(27079)
QUEST.addKillId(20554)
QUEST.addKillId(20582)
QUEST.addKillId(20600)
