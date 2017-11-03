# Maked by Mr. Have fun! Version 0.2
import sys
from com.it.br.gameserver.model.quest import State
from com.it.br.gameserver.model.quest import QuestState
from com.it.br.gameserver.model.quest.jython import QuestJython as JQuest

qn = "221_TestimonyOfProsperity"

MARK_OF_PROSPERITY_ID = 3238
RING_OF_TESTIMONY1_ID = 3239
RING_OF_TESTIMONY2_ID = 3240
OLD_ACCOUNT_BOOK_ID = 3241
BLESSED_SEED_ID = 3242
RECIPE_OF_EMILLY_ID = 3243
LILITH_ELVEN_WAFER_ID = 3244
MAPHR_TABLET_FRAGMENT_ID = 3245
COLLECTION_LICENSE_ID = 3246
LOCKIRINS_NOTICE1_ID = 3247
LOCKIRINS_NOTICE2_ID = 3248
LOCKIRINS_NOTICE3_ID = 3249
LOCKIRINS_NOTICE4_ID = 3250
LOCKIRINS_NOTICE5_ID = 3251
CONTRIBUTION_OF_CHALI_ID = 3252
CONTRIBUTION_OF_MION_ID = 3253
CONTRIBUTION_OF_MARIFE_ID = 3254
MARIFES_REQUEST_ID = 3255
CONTRIBUTION_OF_TOMA_ID = 3256
RECEIPT_OF_BOLTER_ID = 3257
RECEIPT_OF_CONTRIBUTION1_ID = 3258
RECEIPT_OF_CONTRIBUTION2_ID = 3259
RECEIPT_OF_CONTRIBUTION3_ID = 3260
RECEIPT_OF_CONTRIBUTION4_ID = 3261
RECEIPT_OF_CONTRIBUTION5_ID = 3262
PROCURATION_OF_TOROCCO_ID = 3263
BRIGHTS_LIST_ID = 3264
MANDRAGORA_PETAL_ID = 3265
CRIMSON_MOSS_ID = 3266
MANDRAGORA_BOUQUET_ID = 3267
PARMANS_INSTRUCTIONS_ID = 3268
PARMANS_LETTER_ID = 3269
CLAY_DOUGH_ID = 3270
PATTERN_OF_KEYHOLE_ID = 3271
NIKOLAS_LIST_ID = 3272
STAKATO_SHELL_ID = 3273
INPICIO_SAC_ID = 3274
SPIDER_THORN_ID = 3275
CRYSTAL_BROOCH_ID = 3428
ADENA_ID = 57
ANIMAL_SKIN_ID = 1867
RP_TITAN_KEY_ID = 3023
KEY_OF_TITAN_ID = 3030

class Quest (JQuest) :

 def __init__(self,id,name,descr): JQuest.__init__(self,id,name,descr)

 def onEvent (self,event,st) :
    htmltext = event
    if event == "1" :
        htmltext = "30104-04.htm"
        st.set("cond","1")
        st.setState(STARTED)
        st.playSound("ItemSound.quest_accept")
        st.giveItems(RING_OF_TESTIMONY1_ID,1)
    elif event == "30104_1" :
          if st.getPlayer().getLevel() < 38 :
            htmltext = "30104-07.htm"
            st.takeItems(RING_OF_TESTIMONY1_ID,1)
            st.takeItems(OLD_ACCOUNT_BOOK_ID,1)
            st.takeItems(BLESSED_SEED_ID,1)
            st.takeItems(RECIPE_OF_EMILLY_ID,1)
            st.takeItems(LILITH_ELVEN_WAFER_ID,1)
            st.giveItems(PARMANS_INSTRUCTIONS_ID,1)
          else:
            htmltext = "30104-08.htm"
            st.takeItems(RING_OF_TESTIMONY1_ID,1)
            st.takeItems(OLD_ACCOUNT_BOOK_ID,1)
            st.takeItems(BLESSED_SEED_ID,1)
            st.takeItems(RECIPE_OF_EMILLY_ID,1)
            st.takeItems(LILITH_ELVEN_WAFER_ID,1)
            st.giveItems(RING_OF_TESTIMONY2_ID,1)
            st.giveItems(PARMANS_LETTER_ID,1)
    elif event == "30531_1" and st.getQuestItemsCount(COLLECTION_LICENSE_ID) :
          htmltext = "30531-04.htm"
    elif event == "30531_1" :
          htmltext = "30531-02.htm"
    elif event == "30531_2" and st.getQuestItemsCount(COLLECTION_LICENSE_ID) == 0 :
          htmltext = "30531-03.htm"
          st.giveItems(COLLECTION_LICENSE_ID,1)
          st.giveItems(LOCKIRINS_NOTICE1_ID,1)
          st.giveItems(LOCKIRINS_NOTICE2_ID,1)
          st.giveItems(LOCKIRINS_NOTICE3_ID,1)
          st.giveItems(LOCKIRINS_NOTICE4_ID,1)
          st.giveItems(LOCKIRINS_NOTICE5_ID,1)
    elif event == "30534_1" :
          if st.getQuestItemsCount(ADENA_ID) < 5000 :
            htmltext = "30534-03a.htm"
          else:
            htmltext = "30534-03b.htm"
            st.takeItems(ADENA_ID,5000)
            st.giveItems(RECEIPT_OF_CONTRIBUTION3_ID,1)
            st.takeItems(PROCURATION_OF_TOROCCO_ID,1)
    elif event == "30555_1" :
          htmltext = "30555-02.htm"
          st.giveItems(PROCURATION_OF_TOROCCO_ID,1)
    elif event == "30597_1" :
          htmltext = "30597-02.htm"
          st.giveItems(BLESSED_SEED_ID,1)
    elif event == "30005_1" :
          htmltext = "30005-02.htm"
    elif event == "30005_2" :
          htmltext = "30005-03.htm"
    elif event == "30005_3" :
          htmltext = "30005-04.htm"
          st.giveItems(CRYSTAL_BROOCH_ID,1)
    elif event == "30368_1" :
          htmltext = "30368-02.htm"
    elif event == "30368_2" :
          htmltext = "30368-03.htm"
          st.giveItems(LILITH_ELVEN_WAFER_ID,1)
          st.takeItems(CRYSTAL_BROOCH_ID,1)
    elif event == "30466_1" :
          htmltext = "30466-02.htm"
    elif event == "30466_2" :
          htmltext = "30466-03.htm"
          st.giveItems(BRIGHTS_LIST_ID,1)
    elif event == "30620_1" :
          htmltext = "30620-02.htm"
    elif event == "30620_2" :
          htmltext = "30620-03.htm"
          st.giveItems(RECIPE_OF_EMILLY_ID,1)
          st.takeItems(MANDRAGORA_BOUQUET_ID,1)
    elif event == "30621_1" :
          htmltext = "30621-02.htm"
    elif event == "30621_2" :
          htmltext = "30621-03.htm"
    elif event == "30621_3" :
          htmltext = "30621-04.htm"
          st.giveItems(CLAY_DOUGH_ID,1)
    elif event == "30622_1" :
          htmltext = "30622-02.htm"
          st.giveItems(PATTERN_OF_KEYHOLE_ID,1)
          st.takeItems(CLAY_DOUGH_ID,1)
    elif event == "30622_2" :
          htmltext = "30622-04.htm"
          st.takeItems(NIKOLAS_LIST_ID,1)
          st.takeItems(RP_TITAN_KEY_ID,1)
          st.takeItems(STAKATO_SHELL_ID,st.getQuestItemsCount(STAKATO_SHELL_ID))
          st.takeItems(INPICIO_SAC_ID,st.getQuestItemsCount(INPICIO_SAC_ID))
          st.takeItems(SPIDER_THORN_ID,st.getQuestItemsCount(SPIDER_THORN_ID))
          st.giveItems(MAPHR_TABLET_FRAGMENT_ID,1)
          st.takeItems(KEY_OF_TITAN_ID,1)
    return htmltext


 def onTalk (self,npc,player):
   htmltext = "<html><body>You are either not carrying out your quest or don't meet the criteria.</body></html>"
   st = player.getQuestState(qn)
   if not st : return htmltext

   npcId = npc.getNpcId()
   id = st.getState()
   if npcId != 30104 and id != STARTED : return htmltext

   if id == CREATED :
     st.setState(STARTING)
     st.set("cond","0")
     st.set("onlyone","0")
     st.set("id","0")
   if npcId == 30104 and st.getInt("cond")==0 and st.getInt("onlyone")==0 :
        if player.getRace().ordinal() != 4 :
          htmltext = "30104-01.htm"
          st.exitQuest(1)
        else:
          if player.getLevel() < 37 :
            htmltext = "30104-02.htm"
            st.exitQuest(1)
          else:
            htmltext = "30104-03.htm"
   elif npcId == 30104 and st.getInt("cond")==0 and st.getInt("onlyone")==1 :
      htmltext = "<html><body>This quest has already been completed.</body></html>"
   elif npcId == 30104 and st.getInt("cond")>=1 and st.getQuestItemsCount(RING_OF_TESTIMONY1_ID)==1 :
        if st.getQuestItemsCount(OLD_ACCOUNT_BOOK_ID) and st.getQuestItemsCount(BLESSED_SEED_ID) and st.getQuestItemsCount(RECIPE_OF_EMILLY_ID) and st.getQuestItemsCount(LILITH_ELVEN_WAFER_ID) :
          htmltext = "30104-06.htm"
        else:
          htmltext = "30104-05.htm"
   elif npcId == 30104 and st.getInt("cond")>=1 and st.getQuestItemsCount(PARMANS_INSTRUCTIONS_ID)==1 :
        if player.getLevel() < 38 :
          htmltext = "30104-09.htm"
        else:
          htmltext = "30104-10.htm"
          st.giveItems(RING_OF_TESTIMONY2_ID,1)
          st.takeItems(PARMANS_INSTRUCTIONS_ID,1)
          st.giveItems(PARMANS_LETTER_ID,1)
   elif npcId == 30104 and st.getInt("cond")>=1 and st.getQuestItemsCount(RING_OF_TESTIMONY2_ID) and st.getQuestItemsCount(PARMANS_LETTER_ID) and st.getQuestItemsCount(MAPHR_TABLET_FRAGMENT_ID) == 0 :
        htmltext = "30104-11.htm"
   elif npcId == 30104 and st.getInt("cond")>=1 and (st.getQuestItemsCount(CLAY_DOUGH_ID) or st.getQuestItemsCount(PATTERN_OF_KEYHOLE_ID) or st.getQuestItemsCount(NIKOLAS_LIST_ID)) and st.getQuestItemsCount(RING_OF_TESTIMONY2_ID)==1 :
        htmltext = "30104-12.htm"
   elif npcId == 30104 and st.getInt("cond")>=1 and st.getQuestItemsCount(RING_OF_TESTIMONY2_ID) and st.getQuestItemsCount(MAPHR_TABLET_FRAGMENT_ID) :
          st.addExpAndSp(12969,1000)
          st.rewardItems(57,108841)
          st.giveItems(7562,16)
          st.takeItems(RING_OF_TESTIMONY2_ID,1)
          st.giveItems(MARK_OF_PROSPERITY_ID,1)
          st.takeItems(MAPHR_TABLET_FRAGMENT_ID,1)
          htmltext = "30104-13.htm"
          st.set("cond","0")
          st.set("onlyone","1")
          st.setState(COMPLETED)
          st.playSound("ItemSound.quest_finish")
   elif npcId == 30531 and st.getInt("cond")>=1 and st.getQuestItemsCount(OLD_ACCOUNT_BOOK_ID) == 0 and st.getQuestItemsCount(COLLECTION_LICENSE_ID) == 0 and st.getQuestItemsCount(RING_OF_TESTIMONY1_ID)==1 :
        htmltext = "30531-01.htm"
   elif npcId == 30531 and st.getInt("cond")>=1 and st.getQuestItemsCount(RING_OF_TESTIMONY1_ID) and st.getQuestItemsCount(COLLECTION_LICENSE_ID) :
        if st.getQuestItemsCount(RECEIPT_OF_CONTRIBUTION1_ID) and st.getQuestItemsCount(RECEIPT_OF_CONTRIBUTION2_ID) and st.getQuestItemsCount(RECEIPT_OF_CONTRIBUTION3_ID) and st.getQuestItemsCount(RECEIPT_OF_CONTRIBUTION4_ID) and st.getQuestItemsCount(RECEIPT_OF_CONTRIBUTION5_ID) :
          htmltext = "30531-05.htm"
          st.giveItems(OLD_ACCOUNT_BOOK_ID,1)
          st.takeItems(COLLECTION_LICENSE_ID,1)
          st.takeItems(RECEIPT_OF_CONTRIBUTION1_ID,1)
          st.takeItems(RECEIPT_OF_CONTRIBUTION2_ID,1)
          st.takeItems(RECEIPT_OF_CONTRIBUTION3_ID,1)
          st.takeItems(RECEIPT_OF_CONTRIBUTION4_ID,1)
          st.takeItems(RECEIPT_OF_CONTRIBUTION5_ID,1)
        else:
          htmltext = "30531-04.htm"
   elif npcId == 30531 and st.getInt("cond") >= 1 and st.getQuestItemsCount(RING_OF_TESTIMONY1_ID) and st.getQuestItemsCount(OLD_ACCOUNT_BOOK_ID) and st.getQuestItemsCount(COLLECTION_LICENSE_ID)==0 :
        htmltext = "30531-06.htm"
   elif npcId == 30531 and st.getInt("cond") >= 1 and st.getQuestItemsCount(RING_OF_TESTIMONY2_ID) == 1 :
        htmltext = "30531-07.htm"
   elif npcId == 30532 and st.getInt("cond") >= 1 and st.getQuestItemsCount(RING_OF_TESTIMONY1_ID) and st.getQuestItemsCount(COLLECTION_LICENSE_ID) and st.getQuestItemsCount(LOCKIRINS_NOTICE1_ID) and st.getQuestItemsCount(RECEIPT_OF_CONTRIBUTION1_ID) == 0 and st.getQuestItemsCount(CONTRIBUTION_OF_CHALI_ID) == 0 :
        htmltext = "30532-01.htm"
        st.takeItems(LOCKIRINS_NOTICE1_ID,1)
   elif npcId == 30532 and st.getInt("cond")>=1 and st.getQuestItemsCount(RING_OF_TESTIMONY1_ID) and st.getQuestItemsCount(COLLECTION_LICENSE_ID) and st.getQuestItemsCount(RECEIPT_OF_CONTRIBUTION1_ID) == 0 and st.getQuestItemsCount(CONTRIBUTION_OF_CHALI_ID) == 0 and st.getQuestItemsCount(LOCKIRINS_NOTICE1_ID) == 0 :
        htmltext = "30532-02.htm"
   elif npcId == 30532 and st.getInt("cond")>=1 and st.getQuestItemsCount(RING_OF_TESTIMONY1_ID) and st.getQuestItemsCount(COLLECTION_LICENSE_ID) and st.getQuestItemsCount(CONTRIBUTION_OF_CHALI_ID) and st.getQuestItemsCount(RECEIPT_OF_CONTRIBUTION1_ID) == 0 and st.getQuestItemsCount(LOCKIRINS_NOTICE1_ID) == 0 :
        htmltext = "30532-03.htm"
        st.giveItems(RECEIPT_OF_CONTRIBUTION1_ID,1)
        st.takeItems(CONTRIBUTION_OF_CHALI_ID,1)
   elif npcId == 30532 and st.getInt("cond")>=1 and st.getQuestItemsCount(CONTRIBUTION_OF_CHALI_ID) == 0 and st.getQuestItemsCount(LOCKIRINS_NOTICE1_ID) == 0 and st.getQuestItemsCount(RING_OF_TESTIMONY1_ID) and st.getQuestItemsCount(COLLECTION_LICENSE_ID) and st.getQuestItemsCount(RECEIPT_OF_CONTRIBUTION1_ID) :
        htmltext = "30532-04.htm"
   elif npcId == 30533 and st.getInt("cond")>=1 and st.getQuestItemsCount(RING_OF_TESTIMONY1_ID) and st.getQuestItemsCount(COLLECTION_LICENSE_ID) and st.getQuestItemsCount(LOCKIRINS_NOTICE2_ID) and st.getQuestItemsCount(LOCKIRINS_NOTICE2_ID)==1 and st.getQuestItemsCount(RECEIPT_OF_CONTRIBUTION2_ID)==0 and (st.getQuestItemsCount(CONTRIBUTION_OF_MION_ID)+st.getQuestItemsCount(CONTRIBUTION_OF_MARIFE_ID)<2) :
        htmltext = "30533-01.htm"
        st.takeItems(LOCKIRINS_NOTICE2_ID,1)
   elif npcId == 30533 and st.getInt("cond")>=1 and st.getQuestItemsCount(RING_OF_TESTIMONY1_ID) and st.getQuestItemsCount(COLLECTION_LICENSE_ID) and st.getQuestItemsCount(LOCKIRINS_NOTICE2_ID)==0 and st.getQuestItemsCount(RECEIPT_OF_CONTRIBUTION2_ID)==0 and (st.getQuestItemsCount(CONTRIBUTION_OF_MION_ID)+st.getQuestItemsCount(CONTRIBUTION_OF_MARIFE_ID)<2) :
        htmltext = "30533-02.htm"
   elif npcId == 30533 and st.getInt("cond")>=1 and st.getQuestItemsCount(RING_OF_TESTIMONY1_ID) and st.getQuestItemsCount(COLLECTION_LICENSE_ID) and st.getQuestItemsCount(RECEIPT_OF_CONTRIBUTION2_ID) == 0 and st.getQuestItemsCount(LOCKIRINS_NOTICE2_ID) == 0 and (st.getQuestItemsCount(CONTRIBUTION_OF_MION_ID)+st.getQuestItemsCount(CONTRIBUTION_OF_MARIFE_ID)>=2) :
        htmltext = "30533-03.htm"
        st.takeItems(CONTRIBUTION_OF_MARIFE_ID,1)
        st.giveItems(RECEIPT_OF_CONTRIBUTION2_ID,1)
        st.takeItems(CONTRIBUTION_OF_MION_ID,1)
   elif npcId == 30533 and st.getInt("cond")>=1 and st.getQuestItemsCount(LOCKIRINS_NOTICE2_ID)==0 and st.getQuestItemsCount(RING_OF_TESTIMONY1_ID) and st.getQuestItemsCount(COLLECTION_LICENSE_ID) and st.getQuestItemsCount(RECEIPT_OF_CONTRIBUTION2_ID) and (st.getQuestItemsCount(CONTRIBUTION_OF_MION_ID)+st.getQuestItemsCount(CONTRIBUTION_OF_MARIFE_ID)<2) :
        htmltext = "30533-04.htm"
   elif npcId == 30534 and st.getInt("cond")>=1 and st.getQuestItemsCount(RING_OF_TESTIMONY1_ID) and st.getQuestItemsCount(COLLECTION_LICENSE_ID) and st.getQuestItemsCount(LOCKIRINS_NOTICE3_ID) and st.getQuestItemsCount(RECEIPT_OF_CONTRIBUTION3_ID) == 0 and st.getQuestItemsCount(PROCURATION_OF_TOROCCO_ID) == 0 :
        htmltext = "30534-01.htm"
        st.takeItems(LOCKIRINS_NOTICE3_ID,1)
   elif npcId == 30534 and st.getInt("cond")>=1 and st.getQuestItemsCount(RECEIPT_OF_CONTRIBUTION3_ID) == 0 and st.getQuestItemsCount(PROCURATION_OF_TOROCCO_ID) == 0 and st.getQuestItemsCount(LOCKIRINS_NOTICE3_ID) == 0 and st.getQuestItemsCount(RING_OF_TESTIMONY1_ID) and st.getQuestItemsCount(COLLECTION_LICENSE_ID) :
        htmltext = "30534-02.htm"
   elif npcId == 30534 and st.getInt("cond")>=1 and st.getQuestItemsCount(RING_OF_TESTIMONY1_ID) and st.getQuestItemsCount(COLLECTION_LICENSE_ID) and st.getQuestItemsCount(PROCURATION_OF_TOROCCO_ID) and st.getQuestItemsCount(LOCKIRINS_NOTICE3_ID) == 0 and st.getQuestItemsCount(RECEIPT_OF_CONTRIBUTION3_ID) == 0 :
        htmltext = "30534-03.htm"
   elif npcId == 30534 and st.getInt("cond")>=1 and st.getQuestItemsCount(RING_OF_TESTIMONY1_ID) and st.getQuestItemsCount(COLLECTION_LICENSE_ID) and st.getQuestItemsCount(RECEIPT_OF_CONTRIBUTION3_ID) and st.getQuestItemsCount(PROCURATION_OF_TOROCCO_ID) == 0 and st.getQuestItemsCount(LOCKIRINS_NOTICE3_ID) == 0 :
        htmltext = "30534-04.htm"
   elif npcId == 30535 and st.getInt("cond")>=1 and st.getQuestItemsCount(RING_OF_TESTIMONY1_ID) and st.getQuestItemsCount(COLLECTION_LICENSE_ID) and st.getQuestItemsCount(LOCKIRINS_NOTICE4_ID) and st.getQuestItemsCount(RECEIPT_OF_CONTRIBUTION4_ID) == 0 and st.getQuestItemsCount(RECEIPT_OF_BOLTER_ID) == 0 :
        htmltext = "30535-01.htm"
        st.takeItems(LOCKIRINS_NOTICE4_ID,1)
   elif npcId == 30535 and st.getInt("cond")>=1 and st.getQuestItemsCount(RECEIPT_OF_CONTRIBUTION4_ID) == 0 and st.getQuestItemsCount(RECEIPT_OF_BOLTER_ID) == 0 and st.getQuestItemsCount(LOCKIRINS_NOTICE4_ID) == 0 and st.getQuestItemsCount(RING_OF_TESTIMONY1_ID) and st.getQuestItemsCount(COLLECTION_LICENSE_ID) :
        htmltext = "30535-02.htm"
   elif npcId == 30535 and st.getInt("cond")>=1 and st.getQuestItemsCount(RING_OF_TESTIMONY1_ID) and st.getQuestItemsCount(COLLECTION_LICENSE_ID) and st.getQuestItemsCount(RECEIPT_OF_BOLTER_ID) and st.getQuestItemsCount(RECEIPT_OF_CONTRIBUTION4_ID) == 0 and st.getQuestItemsCount(LOCKIRINS_NOTICE4_ID) == 0 :
        htmltext = "30535-03.htm"
        st.giveItems(RECEIPT_OF_CONTRIBUTION4_ID,1)
        st.takeItems(RECEIPT_OF_BOLTER_ID,1)
   elif npcId == 30535 and st.getInt("cond")>=1 and st.getQuestItemsCount(RING_OF_TESTIMONY1_ID) and st.getQuestItemsCount(COLLECTION_LICENSE_ID) and st.getQuestItemsCount(RECEIPT_OF_CONTRIBUTION4_ID) and st.getQuestItemsCount(RECEIPT_OF_BOLTER_ID) == 0 and st.getQuestItemsCount(LOCKIRINS_NOTICE4_ID) == 0 :
        htmltext = "30535-04.htm"
   elif npcId == 30536 and st.getInt("cond")>=1 and st.getQuestItemsCount(RING_OF_TESTIMONY1_ID) and st.getQuestItemsCount(COLLECTION_LICENSE_ID) and st.getQuestItemsCount(LOCKIRINS_NOTICE5_ID) and st.getQuestItemsCount(RECEIPT_OF_CONTRIBUTION5_ID) == 0 and st.getQuestItemsCount(CONTRIBUTION_OF_TOMA_ID) == 0 :
        htmltext = "30536-01.htm"
        st.takeItems(LOCKIRINS_NOTICE5_ID,1)
   elif npcId == 30536 and st.getInt("cond")>=1 and st.getQuestItemsCount(RECEIPT_OF_CONTRIBUTION5_ID) == 0 and st.getQuestItemsCount(CONTRIBUTION_OF_TOMA_ID) == 0 and st.getQuestItemsCount(LOCKIRINS_NOTICE5_ID) == 0 and st.getQuestItemsCount(RING_OF_TESTIMONY1_ID) and st.getQuestItemsCount(COLLECTION_LICENSE_ID) :
        htmltext = "30536-02.htm"
   elif npcId == 30536 and st.getInt("cond")>=1 and st.getQuestItemsCount(RING_OF_TESTIMONY1_ID) and st.getQuestItemsCount(COLLECTION_LICENSE_ID) and st.getQuestItemsCount(CONTRIBUTION_OF_TOMA_ID) and st.getQuestItemsCount(RECEIPT_OF_CONTRIBUTION5_ID) == 0 and st.getQuestItemsCount(LOCKIRINS_NOTICE5_ID) == 0 :
        htmltext = "30536-03.htm"
        st.giveItems(RECEIPT_OF_CONTRIBUTION5_ID,1)
        st.takeItems(CONTRIBUTION_OF_TOMA_ID,1)
   elif npcId == 30536 and st.getInt("cond")>=1 and st.getQuestItemsCount(RING_OF_TESTIMONY1_ID) and st.getQuestItemsCount(COLLECTION_LICENSE_ID) and st.getQuestItemsCount(RECEIPT_OF_CONTRIBUTION5_ID) and st.getQuestItemsCount(CONTRIBUTION_OF_TOMA_ID) == 0 and st.getQuestItemsCount(LOCKIRINS_NOTICE5_ID) == 0 :
        htmltext = "30536-04.htm"
   elif npcId == 30517 and st.getInt("cond")>=1 and st.getQuestItemsCount(RECEIPT_OF_CONTRIBUTION1_ID) == 0 and st.getQuestItemsCount(CONTRIBUTION_OF_CHALI_ID) == 0 and st.getQuestItemsCount(LOCKIRINS_NOTICE1_ID) == 0 and st.getQuestItemsCount(RING_OF_TESTIMONY1_ID) and st.getQuestItemsCount(COLLECTION_LICENSE_ID) :
        htmltext = "30517-01.htm"
        st.giveItems(CONTRIBUTION_OF_CHALI_ID,1)
   elif npcId == 30517 and st.getInt("cond")>=1 and st.getQuestItemsCount(RING_OF_TESTIMONY1_ID) and st.getQuestItemsCount(COLLECTION_LICENSE_ID) and st.getQuestItemsCount(CONTRIBUTION_OF_CHALI_ID) and st.getQuestItemsCount(LOCKIRINS_NOTICE1_ID) == 0 and st.getQuestItemsCount(RECEIPT_OF_CONTRIBUTION1_ID) == 0 :
        htmltext = "30517-02.htm"
   elif npcId == 30519 and st.getInt("cond")>=1 and st.getQuestItemsCount(RECEIPT_OF_CONTRIBUTION2_ID) == 0 and st.getQuestItemsCount(CONTRIBUTION_OF_MION_ID) == 0 and st.getQuestItemsCount(LOCKIRINS_NOTICE2_ID) == 0 and st.getQuestItemsCount(RING_OF_TESTIMONY1_ID) and st.getQuestItemsCount(COLLECTION_LICENSE_ID) :
        htmltext = "30519-01.htm"
        st.giveItems(CONTRIBUTION_OF_MION_ID,1)
   elif npcId == 30519 and st.getInt("cond")>=1 and st.getQuestItemsCount(RING_OF_TESTIMONY1_ID) and st.getQuestItemsCount(COLLECTION_LICENSE_ID) and st.getQuestItemsCount(CONTRIBUTION_OF_MION_ID) and st.getQuestItemsCount(RECEIPT_OF_CONTRIBUTION2_ID) == 0 and st.getQuestItemsCount(LOCKIRINS_NOTICE2_ID) == 0 :
        htmltext = "30519-02.htm"
   elif npcId == 30553 and st.getInt("cond")>=1 and st.getQuestItemsCount(RECEIPT_OF_CONTRIBUTION2_ID) == 0 and st.getQuestItemsCount(CONTRIBUTION_OF_MARIFE_ID) == 0 and st.getQuestItemsCount(LOCKIRINS_NOTICE2_ID) == 0 and st.getQuestItemsCount(MARIFES_REQUEST_ID) == 0 and st.getQuestItemsCount(RING_OF_TESTIMONY1_ID) and st.getQuestItemsCount(COLLECTION_LICENSE_ID) :
        htmltext = "30553-01.htm"
        st.giveItems(MARIFES_REQUEST_ID,1)
   elif npcId == 30553 and st.getInt("cond")>=1 and st.getQuestItemsCount(RING_OF_TESTIMONY1_ID) and st.getQuestItemsCount(COLLECTION_LICENSE_ID) and st.getQuestItemsCount(MARIFES_REQUEST_ID) and st.getQuestItemsCount(RECEIPT_OF_CONTRIBUTION2_ID) == 0 and st.getQuestItemsCount(CONTRIBUTION_OF_MARIFE_ID) == 0 and st.getQuestItemsCount(LOCKIRINS_NOTICE2_ID) == 0 :
        if st.getQuestItemsCount(ANIMAL_SKIN_ID) < 100 :
          htmltext = "30553-02.htm"
        else:
          htmltext = "30553-03.htm"
          st.takeItems(ANIMAL_SKIN_ID,100)
          st.giveItems(CONTRIBUTION_OF_MARIFE_ID,1)
          st.takeItems(MARIFES_REQUEST_ID,1)
   elif npcId == 30553 and st.getInt("cond")>=1 and st.getQuestItemsCount(RING_OF_TESTIMONY1_ID) and st.getQuestItemsCount(COLLECTION_LICENSE_ID) and st.getQuestItemsCount(CONTRIBUTION_OF_MARIFE_ID) and st.getQuestItemsCount(RECEIPT_OF_CONTRIBUTION2_ID) == 0 and st.getQuestItemsCount(LOCKIRINS_NOTICE2_ID) == 0 and st.getQuestItemsCount(MARIFES_REQUEST_ID) == 0 :
        htmltext = "30553-04.htm"
   elif npcId == 30555 and st.getInt("cond")>=1 and st.getQuestItemsCount(RECEIPT_OF_CONTRIBUTION3_ID) == 0 and st.getQuestItemsCount(PROCURATION_OF_TOROCCO_ID) == 0 and st.getQuestItemsCount(LOCKIRINS_NOTICE3_ID) == 0 and st.getQuestItemsCount(RING_OF_TESTIMONY1_ID) and st.getQuestItemsCount(COLLECTION_LICENSE_ID) :
        htmltext = "30555-01.htm"
   elif npcId == 30555 and st.getInt("cond")>=1 and st.getQuestItemsCount(RING_OF_TESTIMONY1_ID) and st.getQuestItemsCount(COLLECTION_LICENSE_ID) and st.getQuestItemsCount(PROCURATION_OF_TOROCCO_ID) and st.getQuestItemsCount(RECEIPT_OF_CONTRIBUTION3_ID) == 0 and st.getQuestItemsCount(LOCKIRINS_NOTICE3_ID) == 0 :
        htmltext = "30555-03.htm"
   elif npcId == 30554 and st.getInt("cond")>=1 and st.getQuestItemsCount(RECEIPT_OF_CONTRIBUTION4_ID) == 0 and st.getQuestItemsCount(RECEIPT_OF_BOLTER_ID) == 0 and st.getQuestItemsCount(LOCKIRINS_NOTICE4_ID) == 0 and st.getQuestItemsCount(RING_OF_TESTIMONY1_ID) and st.getQuestItemsCount(COLLECTION_LICENSE_ID) :
        htmltext = "30554-01.htm"
        st.giveItems(RECEIPT_OF_BOLTER_ID,1)
   elif npcId == 30554 and st.getInt("cond")>=1 and st.getQuestItemsCount(RING_OF_TESTIMONY1_ID) and st.getQuestItemsCount(COLLECTION_LICENSE_ID) and st.getQuestItemsCount(RECEIPT_OF_BOLTER_ID) and st.getQuestItemsCount(RECEIPT_OF_CONTRIBUTION4_ID) == 0 and st.getQuestItemsCount(LOCKIRINS_NOTICE4_ID) == 0 :
        htmltext = "30554-02.htm"
   elif npcId == 30556 and st.getInt("cond")>=1 and st.getQuestItemsCount(RECEIPT_OF_CONTRIBUTION5_ID) == 0 and st.getQuestItemsCount(CONTRIBUTION_OF_TOMA_ID) == 0 and st.getQuestItemsCount(LOCKIRINS_NOTICE5_ID) == 0 and st.getQuestItemsCount(RING_OF_TESTIMONY1_ID) and st.getQuestItemsCount(COLLECTION_LICENSE_ID) :
        htmltext = "30556-01.htm"
        st.giveItems(CONTRIBUTION_OF_TOMA_ID,1)
   elif npcId == 30556 and st.getInt("cond")>=1 and st.getQuestItemsCount(RING_OF_TESTIMONY1_ID) and st.getQuestItemsCount(COLLECTION_LICENSE_ID) and st.getQuestItemsCount(CONTRIBUTION_OF_TOMA_ID) and st.getQuestItemsCount(RECEIPT_OF_CONTRIBUTION5_ID) == 0 and st.getQuestItemsCount(LOCKIRINS_NOTICE5_ID) == 0 :
        htmltext = "30556-02.htm"
   elif npcId == 30597 and st.getInt("cond")>=1 and st.getQuestItemsCount(RING_OF_TESTIMONY1_ID)==1 and st.getQuestItemsCount(BLESSED_SEED_ID)==0 :
        htmltext = "30597-01.htm"
   elif npcId == 30597 and st.getInt("cond")>=1 and st.getQuestItemsCount(RING_OF_TESTIMONY1_ID)==1 and st.getQuestItemsCount(BLESSED_SEED_ID)==1 :
        htmltext = "30597-03.htm"
   elif npcId == 30597 and st.getInt("cond")>=1 and st.getQuestItemsCount(RING_OF_TESTIMONY2_ID)==1 :
        htmltext = "30597-04.htm"
   elif npcId == 30005 and st.getInt("cond")>=1 and st.getQuestItemsCount(RING_OF_TESTIMONY1_ID)==1 and st.getQuestItemsCount(LILITH_ELVEN_WAFER_ID) == 0 and st.getQuestItemsCount(CRYSTAL_BROOCH_ID) == 0 :
        htmltext = "30005-01.htm"
   elif npcId == 30005 and st.getInt("cond")>=1 and st.getQuestItemsCount(LILITH_ELVEN_WAFER_ID)==0 and st.getQuestItemsCount(RING_OF_TESTIMONY1_ID) and st.getQuestItemsCount(CRYSTAL_BROOCH_ID) :
        htmltext = "30005-05.htm"
   elif npcId == 30005 and st.getInt("cond")>=1 and st.getQuestItemsCount(RING_OF_TESTIMONY1_ID) and st.getQuestItemsCount(LILITH_ELVEN_WAFER_ID) :
        htmltext = "30005-06.htm"
   elif npcId == 30005 and st.getInt("cond")>=1 and st.getQuestItemsCount(RING_OF_TESTIMONY2_ID)==1 :
        htmltext = "30005-07.htm"
   elif npcId == 30368 and st.getInt("cond")>=1 and st.getQuestItemsCount(CRYSTAL_BROOCH_ID) and st.getQuestItemsCount(RING_OF_TESTIMONY1_ID) and st.getQuestItemsCount(LILITH_ELVEN_WAFER_ID)==0 :
        htmltext = "30368-01.htm"
   elif npcId == 30368 and st.getInt("cond")>=1 and st.getQuestItemsCount(RING_OF_TESTIMONY1_ID) and st.getQuestItemsCount(LILITH_ELVEN_WAFER_ID) and st.getQuestItemsCount(CRYSTAL_BROOCH_ID)==0 :
        htmltext = "30368-04.htm"
   elif npcId == 30368 and st.getInt("cond")>=1 and st.getQuestItemsCount(RING_OF_TESTIMONY2_ID)==1 :
        htmltext = "30368-05.htm"
   elif npcId == 30466 and st.getInt("cond")>=1 and st.getQuestItemsCount(RING_OF_TESTIMONY1_ID)==1 and st.getQuestItemsCount(RECIPE_OF_EMILLY_ID) == 0 and st.getQuestItemsCount(BRIGHTS_LIST_ID) == 0 and st.getQuestItemsCount(MANDRAGORA_BOUQUET_ID) == 0 :
        htmltext = "30466-01.htm"
   elif npcId == 30466 and st.getInt("cond")>=1 and st.getQuestItemsCount(RECIPE_OF_EMILLY_ID)==0 and st.getQuestItemsCount(RING_OF_TESTIMONY1_ID) and st.getQuestItemsCount(BRIGHTS_LIST_ID) :
        if st.getQuestItemsCount(MANDRAGORA_PETAL_ID) < 20 or st.getQuestItemsCount(CRIMSON_MOSS_ID) < 10 :
          htmltext = "30466-04.htm"
        else:
          htmltext = "30466-05.htm"
          st.takeItems(MANDRAGORA_PETAL_ID,st.getQuestItemsCount(MANDRAGORA_PETAL_ID))
          st.takeItems(CRIMSON_MOSS_ID,st.getQuestItemsCount(CRIMSON_MOSS_ID))
          st.giveItems(MANDRAGORA_BOUQUET_ID,1)
          st.takeItems(BRIGHTS_LIST_ID,1)
   elif npcId == 30466 and st.getInt("cond")>=1 and st.getQuestItemsCount(RING_OF_TESTIMONY1_ID) and st.getQuestItemsCount(MANDRAGORA_BOUQUET_ID) and st.getQuestItemsCount(RECIPE_OF_EMILLY_ID) == 0 and st.getQuestItemsCount(BRIGHTS_LIST_ID) == 0 :
        htmltext = "30466-06.htm"
   elif npcId == 30466 and st.getInt("cond")>=1 and st.getQuestItemsCount(RING_OF_TESTIMONY1_ID) and st.getQuestItemsCount(RECIPE_OF_EMILLY_ID) :
        htmltext = "30466-07.htm"
   elif npcId == 30466 and st.getInt("cond")>=1 and st.getQuestItemsCount(RING_OF_TESTIMONY2_ID)==1 :
        htmltext = "30466-08.htm"
   elif npcId == 30620 and st.getInt("cond")>=1 and st.getQuestItemsCount(RING_OF_TESTIMONY1_ID) and st.getQuestItemsCount(MANDRAGORA_BOUQUET_ID) and st.getQuestItemsCount(RECIPE_OF_EMILLY_ID) == 0 and st.getQuestItemsCount(BRIGHTS_LIST_ID) == 0 :
        htmltext = "30620-01.htm"
   elif npcId == 30620 and st.getInt("cond")>=1 and st.getQuestItemsCount(RING_OF_TESTIMONY1_ID) and st.getQuestItemsCount(RECIPE_OF_EMILLY_ID) :
        htmltext = "30620-04.htm"
   elif npcId == 30620 and st.getInt("cond")>=1 and st.getQuestItemsCount(RING_OF_TESTIMONY2_ID)==1 :
        htmltext = "30620-05.htm"
   elif npcId == 30621 and st.getInt("cond")>=1 and st.getQuestItemsCount(RING_OF_TESTIMONY2_ID)==1 and st.getQuestItemsCount(CLAY_DOUGH_ID) == 0 and st.getQuestItemsCount(PATTERN_OF_KEYHOLE_ID) == 0 and st.getQuestItemsCount(NIKOLAS_LIST_ID) == 0 and st.getQuestItemsCount(MAPHR_TABLET_FRAGMENT_ID) == 0 :
        htmltext = "30621-01.htm"
   elif npcId == 30621 and st.getInt("cond")>=1 and st.getQuestItemsCount(RING_OF_TESTIMONY2_ID) and st.getQuestItemsCount(CLAY_DOUGH_ID) and st.getQuestItemsCount(PATTERN_OF_KEYHOLE_ID) == 0 and st.getQuestItemsCount(NIKOLAS_LIST_ID) == 0 and st.getQuestItemsCount(MAPHR_TABLET_FRAGMENT_ID) == 0 :
        htmltext = "30621-05.htm"
   elif npcId == 30621 and st.getInt("cond")>=1 and st.getQuestItemsCount(RING_OF_TESTIMONY2_ID) and st.getQuestItemsCount(PATTERN_OF_KEYHOLE_ID) and st.getQuestItemsCount(CLAY_DOUGH_ID) == 0 and st.getQuestItemsCount(NIKOLAS_LIST_ID) == 0 and st.getQuestItemsCount(MAPHR_TABLET_FRAGMENT_ID) == 0 :
        htmltext = "30621-06.htm"
        st.giveItems(NIKOLAS_LIST_ID,1)
        st.takeItems(PATTERN_OF_KEYHOLE_ID,1)
        st.giveItems(RP_TITAN_KEY_ID,1)
        st.takeItems(PARMANS_LETTER_ID,1)
   elif npcId == 30621 and st.getInt("cond")>=1 and st.getQuestItemsCount(RING_OF_TESTIMONY2_ID) and st.getQuestItemsCount(NIKOLAS_LIST_ID) and st.getQuestItemsCount(CLAY_DOUGH_ID) == 0 and st.getQuestItemsCount(PATTERN_OF_KEYHOLE_ID) == 0 and st.getQuestItemsCount(MAPHR_TABLET_FRAGMENT_ID) == 0 and st.getQuestItemsCount(KEY_OF_TITAN_ID) == 0 :
        htmltext = "30621-07.htm"
   elif npcId == 30621 and st.getInt("cond")>=1 and st.getQuestItemsCount(RING_OF_TESTIMONY2_ID) and st.getQuestItemsCount(NIKOLAS_LIST_ID) and st.getQuestItemsCount(KEY_OF_TITAN_ID) and st.getQuestItemsCount(CLAY_DOUGH_ID) == 0 and st.getQuestItemsCount(PATTERN_OF_KEYHOLE_ID) == 0 and st.getQuestItemsCount(MAPHR_TABLET_FRAGMENT_ID) == 0 :
        htmltext = "30621-08.htm"
   elif npcId == 30621 and st.getInt("cond")>=1 and st.getQuestItemsCount(RING_OF_TESTIMONY2_ID) and st.getQuestItemsCount(MAPHR_TABLET_FRAGMENT_ID) and st.getQuestItemsCount(CLAY_DOUGH_ID) == 0 and st.getQuestItemsCount(PATTERN_OF_KEYHOLE_ID) == 0 and st.getQuestItemsCount(NIKOLAS_LIST_ID) == 0 :
        htmltext = "30621-09.htm"
   elif npcId == 30622 and st.getInt("cond")>=1 and st.getQuestItemsCount(RING_OF_TESTIMONY2_ID) and st.getQuestItemsCount(CLAY_DOUGH_ID) and st.getQuestItemsCount(PATTERN_OF_KEYHOLE_ID)==0 :
        htmltext = "30622-01.htm"
   elif npcId == 30622 and st.getInt("cond")>=1 and st.getQuestItemsCount(RING_OF_TESTIMONY2_ID) and st.getQuestItemsCount(KEY_OF_TITAN_ID) and st.getQuestItemsCount(MAPHR_TABLET_FRAGMENT_ID)==0 :
        htmltext = "30622-03.htm"
   return htmltext

 def onKill(self,npc,player,isPet):
   st = player.getQuestState(qn)
   if not st : return 
   if st.getState() != STARTED : return 
   
   npcId = npc.getNpcId()
   if npcId == 20223 :
    st.set("id","0")
    if st.getInt("cond") and st.getQuestItemsCount(RING_OF_TESTIMONY1_ID) == 1  :
     if st.getQuestItemsCount(MANDRAGORA_PETAL_ID)<20 and st.getRandom(100)<30 :
      st.giveItems(MANDRAGORA_PETAL_ID,1)
      st.playSound("ItemSound.quest_middle")
   elif npcId == 20154 :
    st.set("id","0")
    if st.getInt("cond") and st.getQuestItemsCount(RING_OF_TESTIMONY1_ID) == 1  :
     if st.getQuestItemsCount(MANDRAGORA_PETAL_ID)<20 and st.getRandom(100)<60 :
      st.giveItems(MANDRAGORA_PETAL_ID,1)
      st.playSound("ItemSound.quest_middle")
   elif npcId == 20155 :
    st.set("id","0")
    if st.getInt("cond") and st.getQuestItemsCount(RING_OF_TESTIMONY1_ID) == 1  :
     if st.getQuestItemsCount(MANDRAGORA_PETAL_ID)<20 and st.getRandom(100)<80 :
      st.giveItems(MANDRAGORA_PETAL_ID,1)
      st.playSound("ItemSound.quest_middle")
   elif npcId == 20156 :
    st.set("id","0")
    if st.getInt("cond") and st.getQuestItemsCount(RING_OF_TESTIMONY1_ID) == 1  :
     if st.getQuestItemsCount(MANDRAGORA_PETAL_ID)<20 :
      st.giveItems(MANDRAGORA_PETAL_ID,1)
      st.playSound("ItemSound.quest_middle")
   elif npcId == 20228 :
    st.set("id","0")
    if st.getInt("cond") and st.getQuestItemsCount(RING_OF_TESTIMONY1_ID) == 1 and st.getQuestItemsCount(CRIMSON_MOSS_ID)<10 :
     if st.getQuestItemsCount(CRIMSON_MOSS_ID) == 9 :
      st.giveItems(CRIMSON_MOSS_ID,1)
      st.playSound("ItemSound.quest_middle")
     else :
      st.giveItems(CRIMSON_MOSS_ID,1)
      st.playSound("ItemSound.quest_itemget")
   elif npcId == 20157 :
    st.set("id","0")
    if st.getInt("cond") and st.getQuestItemsCount(RING_OF_TESTIMONY2_ID) == 1 and st.getQuestItemsCount(STAKATO_SHELL_ID) <20  :
     if st.getRandom(100)<20 :
      st.giveItems(STAKATO_SHELL_ID,1)
      st.playSound("ItemSound.quest_middle")
   elif npcId == 20230 :
    st.set("id","0")
    if st.getInt("cond") and st.getQuestItemsCount(RING_OF_TESTIMONY2_ID) == 1 and st.getQuestItemsCount(STAKATO_SHELL_ID) <20  :
     if st.getRandom(100)<30 :
      st.giveItems(STAKATO_SHELL_ID,1)
      st.playSound("ItemSound.quest_middle")
   elif npcId == 20232 :
    st.set("id","0")
    if st.getInt("cond") and st.getQuestItemsCount(RING_OF_TESTIMONY2_ID) == 1 and st.getQuestItemsCount(STAKATO_SHELL_ID) <20  :
     if st.getRandom(100)<50 :
      st.giveItems(STAKATO_SHELL_ID,1)
      st.playSound("ItemSound.quest_middle")
   elif npcId == 20234 :
    st.set("id","0")
    if st.getInt("cond") and st.getQuestItemsCount(RING_OF_TESTIMONY2_ID) == 1 and st.getQuestItemsCount(STAKATO_SHELL_ID) <20 :
     if st.getRandom(100)<60 :
      st.giveItems(STAKATO_SHELL_ID,1)
      st.playSound("ItemSound.quest_middle")
   elif npcId == 20231 :
    st.set("id","0")
    if st.getInt("cond") and st.getQuestItemsCount(RING_OF_TESTIMONY2_ID) == 1 and st.getQuestItemsCount(INPICIO_SAC_ID) <10  :
     if st.getRandom(100)<50 :
      st.giveItems(INPICIO_SAC_ID,1)
      st.playSound("ItemSound.quest_middle")
   elif npcId == 20233 :
    st.set("id","0")
    if st.getInt("cond") and st.getQuestItemsCount(RING_OF_TESTIMONY2_ID) == 1 and st.getQuestItemsCount(SPIDER_THORN_ID) <10  :
     if st.getRandom(100)<50 :
      st.giveItems(SPIDER_THORN_ID,1)
      st.playSound("ItemSound.quest_middle")
   return

QUEST       = Quest(221,qn,"Testimony Of Prosperity")
CREATED     = State('Start', QUEST)
STARTING    = State('Starting', QUEST)
STARTED     = State('Started', QUEST)
COMPLETED   = State('Completed', QUEST)


QUEST.setInitialState(CREATED)
QUEST.addStartNpc(30104)

QUEST.addTalkId(30104)

QUEST.addTalkId(30005)
QUEST.addTalkId(30368)
QUEST.addTalkId(30466)
QUEST.addTalkId(30517)
QUEST.addTalkId(30519)
QUEST.addTalkId(30531)
QUEST.addTalkId(30532)
QUEST.addTalkId(30533)
QUEST.addTalkId(30534)
QUEST.addTalkId(30535)
QUEST.addTalkId(30536)
QUEST.addTalkId(30553)
QUEST.addTalkId(30554)
QUEST.addTalkId(30555)
QUEST.addTalkId(30556)
QUEST.addTalkId(30597)
QUEST.addTalkId(30620)
QUEST.addTalkId(30621)
QUEST.addTalkId(30622)

QUEST.addKillId(20154)
QUEST.addKillId(20155)
QUEST.addKillId(20156)
QUEST.addKillId(20157)
QUEST.addKillId(20223)
QUEST.addKillId(20228)
QUEST.addKillId(20230)
QUEST.addKillId(20231)
QUEST.addKillId(20232)
QUEST.addKillId(20233)
QUEST.addKillId(20234)
