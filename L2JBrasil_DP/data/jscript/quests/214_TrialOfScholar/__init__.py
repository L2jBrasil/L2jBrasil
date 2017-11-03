# Made by Mr. Have fun! Version 0.2
# version 0.3 - updated by Kerberos on 2007.11.10
# Visit http://forum.l2jdp.com for more details

import sys
from com.it.br.gameserver.model.quest import State
from com.it.br.gameserver.model.quest import QuestState
from com.it.br.gameserver.model.quest.jython import QuestJython as JQuest

qn = "214_TrialOfScholar"

#Quest items (ugly isn't? :P)
MARK_OF_SCHOLAR_ID,MIRIENS_SIGIL1_ID,MIRIENS_SIGIL2_ID,MIRIENS_SIGIL3_ID,MIRIENS_INSTRUCTION_ID, \
MARIAS_LETTER1_ID,MARIAS_LETTER2_ID,LUKAS_LETTER_ID,LUCILLAS_HANDBAG_ID,CRETAS_LETTER1_ID, \
CRETAS_PAINTING1_ID,CRETAS_PAINTING2_ID,CRETAS_PAINTING3_ID,BROWN_SCROLL_SCRAP_ID, \
CRYSTAL_OF_PURITY1_ID,HIGHPRIESTS_SIGIL_ID,GMAGISTERS_SIGIL_ID,CRONOS_SIGIL_ID,SYLVAINS_LETTER_ID, \
SYMBOL_OF_SYLVAIN_ID,JUREKS_LIST_ID,MEYEDESTROYERS_SKIN_ID,SHAMANS_NECKLACE_ID,SHACKLES_SCALP_ID, \
SYMBOL_OF_JUREK_ID,CRONOS_LETTER_ID,DIETERS_KEY_ID,CRETAS_LETTER2_ID,DIETERS_LETTER_ID, \
DIETERS_DIARY_ID,RAUTS_LETTER_ENVELOPE_ID,TRIFFS_RING_ID,SCRIPTURE_CHAPTER_1_ID,SCRIPTURE_CHAPTER_2_ID, \
SCRIPTURE_CHAPTER_3_ID,SCRIPTURE_CHAPTER_4_ID,VALKONS_REQUEST_ID,POITANS_NOTES_ID = range(2674,2712)

STRONG_LIQUOR_ID,CRYSTAL_OF_PURITY2_ID,CASIANS_LIST_ID,GHOULS_SKIN_ID,MEDUSAS_BLOOD_ID, \
FETTEREDSOULS_ICHOR_ID,ENCHT_GARGOYLES_NAIL_ID,SYMBOL_OF_CRONOS_ID = range (2713,2721)

#npcs
NPC = [30461,30070,30071,30103,30111,30115,30230,30316,30458,30608,30609,30610,30611,30612]

#mobs
MOBS = [20158,20201,20235,20269,20552,20554,20567,20580,20068]

class Quest (JQuest) :

 def __init__(self,id,name,descr): 
    JQuest.__init__(self,id,name,descr)
    self.questItemIds = range(2675,2712)+range(2713,2721)

 def onEvent (self,event,st) :
    htmltext = event
    if event == "1" :
        htmltext = "30461-04.htm"
        st.set("cond","1")
        st.setState(STARTED)
        st.playSound(self.SOUND_QUEST_START)
        st.giveItems(MIRIENS_SIGIL1_ID,1)
    elif event == "30461_1" :
          if st.getPlayer().getLevel()<36 :
            htmltext = "30461-09.htm"
            st.takeItems(SYMBOL_OF_JUREK_ID,1)
            st.takeItems(MIRIENS_SIGIL2_ID,1)
            st.giveItems(MIRIENS_INSTRUCTION_ID,1)
          else:
            htmltext = "30461-10.htm"
            st.takeItems(SYMBOL_OF_JUREK_ID,1)
            st.takeItems(MIRIENS_SIGIL2_ID,1)
            st.giveItems(MIRIENS_SIGIL3_ID,1)
            st.playSound(self.SOUND_QUEST_MIDDLE)
            st.set("cond","19")
    elif event == "30070_1" :
          htmltext = "30070-02.htm"
          st.giveItems(HIGHPRIESTS_SIGIL_ID,1)
          st.giveItems(SYLVAINS_LETTER_ID,1)
          st.playSound(self.SOUND_QUEST_MIDDLE)
          st.set("cond","2")
    elif event == "30608_1" :
          htmltext = "30608-02.htm"
          st.takeItems(SYLVAINS_LETTER_ID,1)
          st.giveItems(MARIAS_LETTER1_ID,1)
          st.playSound(self.SOUND_QUEST_MIDDLE)
          st.set("cond","3")
    elif event == "30608_2" :
          htmltext = "30608-07.htm"
    elif event == "30608_3" :
          htmltext = "30608-08.htm"
          st.takeItems(CRETAS_LETTER1_ID,1)
          st.giveItems(LUCILLAS_HANDBAG_ID,1)
          st.playSound(self.SOUND_QUEST_MIDDLE)
          st.set("cond","7")
    elif event == "30608_4" :
          htmltext = "30608-14.htm"
          st.takeItems(BROWN_SCROLL_SCRAP_ID,-1)
          st.takeItems(CRETAS_PAINTING3_ID,1)
          st.giveItems(CRYSTAL_OF_PURITY1_ID,1)
          st.playSound(self.SOUND_QUEST_MIDDLE)
          st.set("cond","13")
    elif event == "30115_1" :
          htmltext = "30115-02.htm"
    elif event == "30115_2" :
          htmltext = "30115-03.htm"
          st.giveItems(JUREKS_LIST_ID,1)
          st.giveItems(GMAGISTERS_SIGIL_ID,1)
          st.playSound(self.SOUND_QUEST_MIDDLE)
          st.set("cond","16")
    elif event == "30071_1" :
          htmltext = "30071-04.htm"
          st.takeItems(CRETAS_PAINTING2_ID,1)
          st.giveItems(CRETAS_PAINTING3_ID,1)
          st.playSound(self.SOUND_QUEST_MIDDLE)
          st.set("cond","10")
    elif event == "30609_1" :
          htmltext = "30609-02.htm"
    elif event == "30609_2" :
          htmltext = "30609-03.htm"
    elif event == "30609_3" :
          htmltext = "30609-04.htm"
    elif event == "30609_4" :
          htmltext = "30609-05.htm"
          st.takeItems(MARIAS_LETTER2_ID,1)
          st.giveItems(CRETAS_LETTER1_ID,1)
          st.playSound(self.SOUND_QUEST_MIDDLE)
          st.set("cond","6")
    elif event == "30609_5" :
          htmltext = "30609-08.htm"
    elif event == "30609_6" :
          htmltext = "30609-09.htm"
          st.takeItems(LUCILLAS_HANDBAG_ID,1)
          st.giveItems(CRETAS_PAINTING1_ID,1)
          st.playSound(self.SOUND_QUEST_MIDDLE)
          st.set("cond","8")
    elif event == "30609_7" :
          htmltext = "30609-13.htm"
    elif event == "30609_8" :
          htmltext = "30609-14.htm"
          st.takeItems(DIETERS_KEY_ID,1)
          st.giveItems(CRETAS_LETTER2_ID,1)
          st.playSound(self.SOUND_QUEST_MIDDLE)
          st.set("cond","22")
    elif event == "30610_1" :
          htmltext = "30610-02.htm"
    elif event == "30610_2" :
          htmltext = "30610-03.htm"
    elif event == "30610_3" :
          htmltext = "30610-04.htm"
    elif event == "30610_4" :
          htmltext = "30610-05.htm"
    elif event == "30610_5" :
          htmltext = "30610-06.htm"
    elif event == "30610_6" :
          htmltext = "30610-07.htm"
    elif event == "30610_7" :
          htmltext = "30610-08.htm"
    elif event == "30610_8" :
          htmltext = "30610-09.htm"
    elif event == "30610_9" :
          htmltext = "30610-10.htm"
          st.giveItems(CRONOS_SIGIL_ID,1)
          st.giveItems(CRONOS_LETTER_ID,1)
          st.playSound(self.SOUND_QUEST_MIDDLE)
          st.set("cond","20")
    elif event == "30610_10" :
          htmltext = "30610-13.htm"
    elif event == "30610_11" :
          htmltext = "30610-14.htm"
          st.takeItems(SCRIPTURE_CHAPTER_1_ID,1)
          st.takeItems(SCRIPTURE_CHAPTER_2_ID,1)
          st.takeItems(SCRIPTURE_CHAPTER_3_ID,1)
          st.takeItems(SCRIPTURE_CHAPTER_4_ID,1)
          st.takeItems(CRONOS_SIGIL_ID,1)
          st.takeItems(TRIFFS_RING_ID,1)
          st.takeItems(DIETERS_DIARY_ID,1)
          st.giveItems(SYMBOL_OF_CRONOS_ID,1)
          st.playSound(self.SOUND_QUEST_MIDDLE)
          st.set("cond","31")
    elif event == "30111_1" :
          htmltext = "30111-02.htm"
    elif event == "30111_2" :
          htmltext = "30111-03.htm"
    elif event == "30111_3" :
          htmltext = "30111-04.htm"
    elif event == "30111_4" :
          htmltext = "30111-05.htm"
          st.takeItems(CRONOS_LETTER_ID,1)
          st.giveItems(DIETERS_KEY_ID,1)
          st.playSound(self.SOUND_QUEST_MIDDLE)
          st.set("cond","21")
    elif event == "30111_5" :
          htmltext = "30111-08.htm"
    elif event == "30111_6" :
          htmltext = "30111-09.htm"
          st.takeItems(CRETAS_LETTER2_ID,1)
          st.giveItems(DIETERS_LETTER_ID,1)
          st.giveItems(DIETERS_DIARY_ID,1)
          st.playSound(self.SOUND_QUEST_MIDDLE)
          st.set("cond","23")
    elif event == "30230_1" :
          htmltext = "30230-02.htm"
          st.takeItems(DIETERS_LETTER_ID,1)
          st.giveItems(RAUTS_LETTER_ENVELOPE_ID,1)
          st.playSound(self.SOUND_QUEST_MIDDLE)
          st.set("cond","24")
    elif event == "30316_1" :
          htmltext = "30316-02.htm"
          st.takeItems(RAUTS_LETTER_ENVELOPE_ID,1)
          st.giveItems(SCRIPTURE_CHAPTER_1_ID,1)
          st.giveItems(STRONG_LIQUOR_ID,1)
          st.playSound(self.SOUND_QUEST_MIDDLE)
          st.set("cond","25")
    elif event == "30611_1" :
          htmltext = "30611-02.htm"
    elif event == "30611_2" :
          htmltext = "30611-03.htm"
    elif event == "30611_3" :
          htmltext = "30611-04.htm"
          st.takeItems(STRONG_LIQUOR_ID,1)
          st.giveItems(TRIFFS_RING_ID,1)
          st.playSound(self.SOUND_QUEST_MIDDLE)
          st.set("cond","26")
    elif event == "30103_1" :
          htmltext = "30103-02.htm"
    elif event == "30103_2" :
          htmltext = "30103-03.htm"
    elif event == "30103_3" :
          htmltext = "30103-04.htm"
          st.giveItems(VALKONS_REQUEST_ID,1)
          st.playSound(self.SOUND_QUEST_MIDDLE)
    elif event == "30612_1" :
          htmltext = "30612-03.htm"
    elif event == "30612_2" :
          htmltext = "30612-04.htm"
          st.giveItems(CASIANS_LIST_ID,1)
          st.playSound(self.SOUND_QUEST_MIDDLE)
          st.set("cond","28")
    elif event == "30612_3" :
          htmltext = "30612-07.htm"
          st.giveItems(SCRIPTURE_CHAPTER_4_ID,1)
          st.takeItems(CASIANS_LIST_ID,1)
          st.takeItems(GHOULS_SKIN_ID,st.getQuestItemsCount(GHOULS_SKIN_ID))
          st.takeItems(MEDUSAS_BLOOD_ID,st.getQuestItemsCount(MEDUSAS_BLOOD_ID))
          st.takeItems(FETTEREDSOULS_ICHOR_ID,st.getQuestItemsCount(FETTEREDSOULS_ICHOR_ID))
          st.takeItems(ENCHT_GARGOYLES_NAIL_ID,st.getQuestItemsCount(ENCHT_GARGOYLES_NAIL_ID))
          st.takeItems(POITANS_NOTES_ID,1)
          st.playSound(self.SOUND_QUEST_MIDDLE)
          st.set("cond","30")
    return htmltext

 def onTalk (self,npc,player):
   htmltext = self.NO_QUEST
   st = player.getQuestState(qn)
   if not st : return htmltext
   cond = st.getInt("cond")
   npcId = npc.getNpcId()
   id = st.getState()
   if npcId != 30461 and id != STARTED : return htmltext

   if id == CREATED :
      st.setState(STARTING)
      st.set("cond","0")
      st.set("onlyone","0")
   if npcId == 30461 and cond == 0 :
     if st.getInt("onlyone") == 0 :
        if player.getClassId().getId() in [0x0b,  0x1a, 0x27] :
           if player.getLevel() >= 35 :
              htmltext = "30461-03.htm"
           else:
              htmltext = "30461-02.htm"
              st.exitQuest(1)
        else:
           htmltext = "30461-01.htm"
           st.exitQuest(1)
     else:
        htmltext = self.QUEST_DONE
   elif npcId == 30461 and cond == 1 :
        htmltext = "30461-05.htm"
   elif npcId == 30461 and cond == 14 :
        htmltext = "30461-06.htm"
        st.takeItems(SYMBOL_OF_SYLVAIN_ID,1)
        st.takeItems(MIRIENS_SIGIL1_ID,1)
        st.giveItems(MIRIENS_SIGIL2_ID,1)
        st.playSound(self.SOUND_QUEST_MIDDLE)
        st.set("cond","15")
   elif npcId == 30461 and (cond > 14 and cond < 18) :
        htmltext = "30461-07.htm"
   elif npcId == 30461 and cond == 18 and st.getQuestItemsCount(MIRIENS_INSTRUCTION_ID)==1 :
        if player.getLevel()<36 :
          htmltext = "30461-11.htm"
        else:
          htmltext = "30461-12.htm"
          st.giveItems(MIRIENS_SIGIL3_ID,1)
          st.takeItems(MIRIENS_INSTRUCTION_ID,1)
          st.playSound(self.SOUND_QUEST_MIDDLE)
          st.set("cond","19")
   elif npcId == 30461 and cond == 18 :
        htmltext = "30461-08.htm"
   elif npcId == 30461 and cond == 19 :
        htmltext = "30461-13.htm"
   elif npcId == 30461 and cond == 31 and st.getQuestItemsCount(SYMBOL_OF_CRONOS_ID) == 1:
            htmltext = "30461-14.htm"
            st.takeItems(MIRIENS_SIGIL3_ID,1)
            st.takeItems(SYMBOL_OF_CRONOS_ID,1)
            st.addExpAndSp(80265,30000)
            st.giveItems(7562,8)
            st.giveItems(MARK_OF_SCHOLAR_ID,1)
            st.set("cond","0")
            st.set("onlyone","1")
            st.setState(COMPLETED)
            st.playSound(self.SOUND_QUEST_DONE)
   elif npcId == 30070 and cond == 1 :
        htmltext = "30070-01.htm"
   elif npcId == 30070 and cond == 2 :
        htmltext = "30070-03.htm"
   elif npcId == 30070 and cond == 13 :
        htmltext = "30070-04.htm"
        st.giveItems(SYMBOL_OF_SYLVAIN_ID,1)
        st.takeItems(HIGHPRIESTS_SIGIL_ID,1)
        st.takeItems(CRYSTAL_OF_PURITY1_ID,1)
        st.playSound(self.SOUND_QUEST_MIDDLE)
        st.set("cond","14")
   elif npcId == 30070 and cond == 14 :
        htmltext = "30070-05.htm"
   elif npcId == 30070 and cond > 14 :
        htmltext = "30070-06.htm"
   elif npcId == 30608 and cond == 2:
        htmltext = "30608-01.htm"
   elif npcId == 30608 and cond == 3:
        htmltext = "30608-03.htm"
   elif npcId == 30608 and cond == 4:
        htmltext = "30608-04.htm"
        st.giveItems(MARIAS_LETTER2_ID,1)
        st.takeItems(LUKAS_LETTER_ID,1)
        st.set("cond","5")
        st.playSound(self.SOUND_QUEST_MIDDLE)
   elif npcId == 30608 and cond == 5 :
        htmltext = "30608-05.htm"
   elif npcId == 30608 and cond == 6 :
        htmltext = "30608-06.htm"
   elif npcId == 30608 and cond == 7 :
        htmltext = "30608-09.htm"
   elif npcId == 30608 and cond == 8 :
        htmltext = "30608-10.htm"
        st.giveItems(CRETAS_PAINTING2_ID,1)
        st.takeItems(CRETAS_PAINTING1_ID,1)
        st.playSound(self.SOUND_QUEST_MIDDLE)
        st.set("cond","9")
   elif npcId == 30608 and cond == 9 :
        htmltext = "30608-11.htm"
   elif npcId == 30608 and cond == 10 :
        htmltext = "30608-12.htm"
        st.playSound(self.SOUND_QUEST_MIDDLE)
        st.set("cond","11")
   elif npcId == 30608 and cond == 12 :
        htmltext = "30608-13.htm"
   elif npcId == 30608 and cond == 13 :
        htmltext = "30608-15.htm"
   elif npcId == 30608 and (st.getQuestItemsCount(SYMBOL_OF_SYLVAIN_ID) or st.getQuestItemsCount(MIRIENS_SIGIL2_ID)) :
        htmltext = "30608-16.htm"
   elif npcId == 30608 and st.getQuestItemsCount(MIRIENS_SIGIL3_ID)==1 and st.getQuestItemsCount(VALKONS_REQUEST_ID)==0 :
        htmltext = "30608-17.htm"
   elif npcId == 30608 and cond==26 and st.getQuestItemsCount(VALKONS_REQUEST_ID)==1 :
        htmltext = "30608-18.htm"
        st.giveItems(CRYSTAL_OF_PURITY2_ID,1)
        st.takeItems(VALKONS_REQUEST_ID,1)
        st.playSound(self.SOUND_QUEST_MIDDLE)
   elif npcId == 30115 and cond == 15 :
        htmltext = "30115-01.htm"
   elif npcId == 30115 and cond == 16 :
        htmltext = "30115-04.htm"
   elif npcId == 30115 and cond == 17 :
        htmltext = "30115-05.htm"
        st.takeItems(JUREKS_LIST_ID,1)
        st.takeItems(MEYEDESTROYERS_SKIN_ID,-1)
        st.takeItems(SHAMANS_NECKLACE_ID,-1)
        st.takeItems(SHACKLES_SCALP_ID,-1)
        st.takeItems(GMAGISTERS_SIGIL_ID,1)
        st.giveItems(SYMBOL_OF_JUREK_ID,1)
        st.playSound(self.SOUND_QUEST_MIDDLE)
        st.set("cond","18")
   elif npcId == 30115 and cond == 18 :
        htmltext = "30115-06.htm"
   elif npcId == 30115 and cond > 18 :
        htmltext = "30115-07.htm"
   elif npcId == 30071 and cond == 3:
        htmltext = "30071-01.htm"
        st.set("cond","4")
        st.giveItems(LUKAS_LETTER_ID,1)
        st.takeItems(MARIAS_LETTER1_ID,1)
        st.playSound(self.SOUND_QUEST_MIDDLE)
   elif npcId == 30071 and cond == 4:
        htmltext = "30071-02.htm"
   elif npcId == 30071 and cond == 9:
        htmltext = "30071-03.htm"
   elif npcId == 30071 and cond == 10 :
        if st.getQuestItemsCount(BROWN_SCROLL_SCRAP_ID)<5 :
          htmltext = "30071-05.htm"
        else:
          htmltext = "30071-06.htm"
   elif npcId == 30071 and cond < 10 :
        htmltext = "30071-07.htm"
   elif npcId == 30609 and cond == 5:
        htmltext = "30609-01.htm"
   elif npcId == 30609 and cond == 6 :
        htmltext = "30609-06.htm"
   elif npcId == 30609 and cond == 7 :
        htmltext = "30609-07.htm"
   elif npcId == 30609 and cond == 8 :
        htmltext = "30609-10.htm"
   elif npcId == 30609 and (cond > 9 and cond < 21) :
        htmltext = "30609-11.htm"
   elif npcId == 30609 and cond == 21 :
        htmltext = "30609-12.htm"
   elif npcId == 30609 and cond == 22 :
        htmltext = "30609-14.htm"
   elif npcId == 30609 and cond > 22 :
        htmltext = "30609-15.htm"
   elif npcId == 30610 and cond == 19 :
        htmltext = "30610-01.htm"
   elif npcId == 30610 and cond == 20 :
        htmltext = "30610-11.htm"
   elif npcId == 30610 and cond == 30 :
        htmltext = "30610-12.htm"
   elif npcId == 30610 and cond == 31 :
        htmltext = "30610-15.htm"
   elif npcId == 30111 and cond == 20 :
        htmltext = "30111-01.htm"
   elif npcId == 30111 and cond == 21 :
          htmltext = "30111-06.htm"
   elif npcId == 30111 and cond == 22 :
          htmltext = "30111-07.htm"
   elif npcId == 30111 and cond == 23 :
          htmltext = "30111-10.htm"
   elif npcId == 30111 and st.getQuestItemsCount(MIRIENS_SIGIL3_ID) and st.getQuestItemsCount(CRONOS_SIGIL_ID) and st.getQuestItemsCount(DIETERS_DIARY_ID) and st.getQuestItemsCount(RAUTS_LETTER_ENVELOPE_ID) :
          htmltext = "30111-11.htm"
   elif npcId == 30111 and st.getQuestItemsCount(MIRIENS_SIGIL3_ID) and st.getQuestItemsCount(CRONOS_SIGIL_ID) and st.getQuestItemsCount(DIETERS_DIARY_ID) and st.getQuestItemsCount(DIETERS_LETTER_ID)==0 and st.getQuestItemsCount(RAUTS_LETTER_ENVELOPE_ID)==0 :
          if st.getQuestItemsCount(SCRIPTURE_CHAPTER_1_ID) and st.getQuestItemsCount(SCRIPTURE_CHAPTER_2_ID) and st.getQuestItemsCount(SCRIPTURE_CHAPTER_3_ID) and st.getQuestItemsCount(SCRIPTURE_CHAPTER_4_ID) :
            htmltext = "30111-13.htm"
          else:
            htmltext = "30111-12.htm"
   elif npcId == 30111 and st.getQuestItemsCount(SYMBOL_OF_CRONOS_ID)==1 :
          htmltext = "30111-15.htm"
   elif npcId == 30230 and cond == 23 :
        htmltext = "30230-01.htm"
   elif npcId == 30230 and cond == 24 :
        htmltext = "30230-03.htm"
   elif npcId == 30230 and st.getQuestItemsCount(DIETERS_DIARY_ID)==1 and (st.getQuestItemsCount(STRONG_LIQUOR_ID) or st.getQuestItemsCount(TRIFFS_RING_ID)) :
        htmltext = "30230-04.htm"
   elif npcId == 30316 and cond == 24 :
        htmltext = "30316-01.htm"
   elif npcId == 30316 and cond == 25 :
        htmltext = "30316-04.htm"
   elif npcId == 30316 and st.getQuestItemsCount(DIETERS_DIARY_ID) and st.getQuestItemsCount(SCRIPTURE_CHAPTER_1_ID) and st.getQuestItemsCount(TRIFFS_RING_ID) :
        htmltext = "30316-05.htm"
   elif npcId == 30611 and cond == 25 :
        htmltext = "30611-01.htm"
   elif npcId == 30611 and cond > 25 :
        htmltext = "30611-05.htm"
   elif npcId == 30103 and cond == 26 and st.getQuestItemsCount(CRYSTAL_OF_PURITY2_ID)==1 :
        htmltext = "30103-06.htm"
        st.giveItems(SCRIPTURE_CHAPTER_2_ID,1)
        st.takeItems(CRYSTAL_OF_PURITY2_ID,1)
        st.playSound(self.SOUND_QUEST_MIDDLE)
   elif npcId == 30103 and cond == 26 and st.getQuestItemsCount(VALKONS_REQUEST_ID)==1 :
        htmltext = "30103-05.htm"
   elif npcId == 30103 and cond == 26 and st.getQuestItemsCount(SCRIPTURE_CHAPTER_2_ID) == 0:
        htmltext = "30103-01.htm"
   elif npcId == 30103 and st.getQuestItemsCount(SCRIPTURE_CHAPTER_2_ID)==1 :
        htmltext = "30103-07.htm"
   elif npcId == 30458 and cond ==26 and st.getQuestItemsCount(POITANS_NOTES_ID) == 0 :
        htmltext = "30458-01.htm"
        st.giveItems(POITANS_NOTES_ID,1)
        st.playSound(self.SOUND_QUEST_MIDDLE)
   elif npcId == 30458 and cond == 26 and st.getQuestItemsCount(POITANS_NOTES_ID) and st.getQuestItemsCount(CASIANS_LIST_ID) == 0 :
        htmltext = "30458-02.htm"
   elif npcId == 30458 and cond == 27 and st.getQuestItemsCount(POITANS_NOTES_ID) and st.getQuestItemsCount(CASIANS_LIST_ID) :
        htmltext = "30458-03.htm"
   elif npcId == 30458 and cond>=28 :
        htmltext = "30458-04.htm"
   elif npcId == 30612 and cond == 26 :
        if st.getQuestItemsCount(SCRIPTURE_CHAPTER_1_ID) and st.getQuestItemsCount(SCRIPTURE_CHAPTER_2_ID) and st.getQuestItemsCount(SCRIPTURE_CHAPTER_3_ID) :
          htmltext = "30612-02.htm"
        else:
          htmltext = "30612-01.htm"
   elif npcId == 30612 and cond==28 :
        if st.getQuestItemsCount(GHOULS_SKIN_ID)+st.getQuestItemsCount(MEDUSAS_BLOOD_ID)+st.getQuestItemsCount(FETTEREDSOULS_ICHOR_ID)+st.getQuestItemsCount(ENCHT_GARGOYLES_NAIL_ID)<32 :
          htmltext = "30612-05.htm"
   elif npcId == 30612 and cond==29 :
        htmltext = "30612-06.htm"
   elif npcId == 30612 and cond >= 30 :
        htmltext = "30612-08.htm"
   return htmltext

 def onKill(self,npc,player,isPet):
   st = player.getQuestState(qn)
   if not st : return
   if st.getState() != STARTED : return
   npcId = npc.getNpcId()
   if npcId == 20580 :
    if st.getInt("cond") == 11 and st.getQuestItemsCount(BROWN_SCROLL_SCRAP_ID)<5 :
      if st.getRandom(100) < 50 :
        st.giveItems(BROWN_SCROLL_SCRAP_ID,1)
        if st.getQuestItemsCount(BROWN_SCROLL_SCRAP_ID) < 5 :
          st.playSound(self.SOUND_ITEM_GET)
        else:
          st.playSound(self.SOUND_QUEST_MIDDLE)
          st.set("cond","12")
   if st.getInt("cond") == 16 :
      if npcId == 20068 :
         if st.getQuestItemsCount(MEYEDESTROYERS_SKIN_ID)<5 :
            if st.getRandom(100) < 50 :
               st.giveItems(MEYEDESTROYERS_SKIN_ID,1)
               if st.getQuestItemsCount(MEYEDESTROYERS_SKIN_ID) < 5 :
                  st.playSound(self.SOUND_ITEM_GET)
               else:
                  st.playSound(self.SOUND_QUEST_MIDDLE)
                  if st.getQuestItemsCount(SHACKLES_SCALP_ID)==2 and st.getQuestItemsCount(SHAMANS_NECKLACE_ID) == 5 and st.getQuestItemsCount(MEYEDESTROYERS_SKIN_ID) == 5:
                     st.set("cond","17")
      elif npcId == 20269 :
         if st.getQuestItemsCount(SHAMANS_NECKLACE_ID)<5 :
            if st.getRandom(100) < 50 :
               st.giveItems(SHAMANS_NECKLACE_ID,1)
               if st.getQuestItemsCount(SHAMANS_NECKLACE_ID) < 5 :
                  st.playSound(self.SOUND_ITEM_GET)
               else:
                  st.playSound(self.SOUND_QUEST_MIDDLE)
                  if st.getQuestItemsCount(SHACKLES_SCALP_ID)==2 and st.getQuestItemsCount(SHAMANS_NECKLACE_ID) == 5 and st.getQuestItemsCount(MEYEDESTROYERS_SKIN_ID) == 5:
                     st.set("cond","17")
      elif npcId == 20235 :
         if st.getQuestItemsCount(SHACKLES_SCALP_ID)<2 :
            st.giveItems(SHACKLES_SCALP_ID,1)
            if st.getQuestItemsCount(SHACKLES_SCALP_ID) < 2 :
               st.playSound(self.SOUND_ITEM_GET)
            else:
               st.playSound(self.SOUND_QUEST_MIDDLE)
               if st.getQuestItemsCount(SHACKLES_SCALP_ID)==2 and st.getQuestItemsCount(SHAMANS_NECKLACE_ID) == 5 and st.getQuestItemsCount(MEYEDESTROYERS_SKIN_ID) == 5:
                  st.set("cond","17")
   elif npcId == 20554 and st.getInt("cond") in [26,27] and st.getQuestItemsCount(SCRIPTURE_CHAPTER_3_ID) == 0 :
      if st.getRandom(100) < 30 :
        st.giveItems(SCRIPTURE_CHAPTER_3_ID,1)
        st.playSound(self.SOUND_QUEST_MIDDLE)
   elif npcId == 20201 :
    if st.getInt("cond") == 28  and st.getQuestItemsCount(GHOULS_SKIN_ID)<10 :
      st.giveItems(GHOULS_SKIN_ID,1)
      if st.getQuestItemsCount(GHOULS_SKIN_ID) < 10 :
        st.playSound(self.SOUND_ITEM_GET)
      else:
        st.playSound(self.SOUND_QUEST_MIDDLE)
        if st.getQuestItemsCount(GHOULS_SKIN_ID) == 10 and st.getQuestItemsCount(MEDUSAS_BLOOD_ID) == 12 and st.getQuestItemsCount(FETTEREDSOULS_ICHOR_ID) == 5 and st.getQuestItemsCount(ENCHT_GARGOYLES_NAIL_ID) == 5:
           st.set("cond","29")
   elif npcId == 20158 :
    if st.getInt("cond") == 28 and st.getQuestItemsCount(MEDUSAS_BLOOD_ID)<12 :
      st.giveItems(MEDUSAS_BLOOD_ID,1)
      if st.getQuestItemsCount(MEDUSAS_BLOOD_ID) < 12 :
        st.playSound(self.SOUND_ITEM_GET)
      else:
        st.playSound(self.SOUND_QUEST_MIDDLE)
        if st.getQuestItemsCount(GHOULS_SKIN_ID) == 10 and st.getQuestItemsCount(MEDUSAS_BLOOD_ID) == 12 and st.getQuestItemsCount(FETTEREDSOULS_ICHOR_ID) == 5 and st.getQuestItemsCount(ENCHT_GARGOYLES_NAIL_ID) == 5:
           st.set("cond","29")
   elif npcId == 20552 :
    if st.getInt("cond") == 28 and st.getQuestItemsCount(FETTEREDSOULS_ICHOR_ID)<5 :
      st.giveItems(FETTEREDSOULS_ICHOR_ID,1)
      if st.getQuestItemsCount(FETTEREDSOULS_ICHOR_ID) < 5 :
        st.playSound(self.SOUND_ITEM_GET)
      else:
        st.playSound(self.SOUND_QUEST_MIDDLE)
        if st.getQuestItemsCount(GHOULS_SKIN_ID) == 10 and st.getQuestItemsCount(MEDUSAS_BLOOD_ID) == 12 and st.getQuestItemsCount(FETTEREDSOULS_ICHOR_ID) == 5 and st.getQuestItemsCount(ENCHT_GARGOYLES_NAIL_ID) == 5:
           st.set("cond","29")
   elif npcId == 20567 :
    if st.getInt("cond") == 28 and st.getQuestItemsCount(ENCHT_GARGOYLES_NAIL_ID)<5 :
      st.giveItems(ENCHT_GARGOYLES_NAIL_ID,1)
      if st.getQuestItemsCount(ENCHT_GARGOYLES_NAIL_ID) < 5 :
        st.playSound(self.SOUND_ITEM_GET)
      else:
        st.playSound(self.SOUND_QUEST_MIDDLE)
        if st.getQuestItemsCount(GHOULS_SKIN_ID) == 10 and st.getQuestItemsCount(MEDUSAS_BLOOD_ID) == 12 and st.getQuestItemsCount(FETTEREDSOULS_ICHOR_ID) == 5 and st.getQuestItemsCount(ENCHT_GARGOYLES_NAIL_ID) == 5:
           st.set("cond","29")
   return

QUEST       = Quest(214,qn,"Trial Of Scholar")
CREATED     = State('Start', QUEST)
STARTING     = State('Starting', QUEST)
STARTED     = State('Started', QUEST)
COMPLETED   = State('Completed', QUEST)

QUEST.setInitialState(CREATED)
QUEST.addStartNpc(NPC[0])

for npcId in NPC:
  QUEST.addTalkId(npcId)

for mobId in MOBS:
  QUEST.addKillId(mobId)