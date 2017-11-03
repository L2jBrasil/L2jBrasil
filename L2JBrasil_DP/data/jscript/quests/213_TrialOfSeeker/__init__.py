# Made by Mr. Have fun!
# Version 0.3 by DrLecter
import sys
from com.it.br.gameserver.model.quest import State
from com.it.br.gameserver.model.quest import QuestState
from com.it.br.gameserver.model.quest.jython import QuestJython as JQuest

qn = "213_TrialOfSeeker"

DUFNERS_LETTER_ID,  TERYS_ORDER1_ID,    TERYS_ORDER2_ID,           TERYS_LETTER_ID,      \
VIKTORS_LETTER_ID,  HAWKEYES_LETTER_ID, MYSTERIOUS_RUNESTONE_ID,   OL_MAHUM_RUNESTONE_ID,\
TUREK_RUNESTONE_ID, ANT_RUNESTONE_ID,   TURAK_BUGBEAR_RUNESTONE_ID,TERYS_BOX_ID,         \
VIKTORS_REQUEST_ID, MEDUSAS_SCALES_ID,  SILENS_RUNESTONE_ID,       ANALYSIS_REQUEST_ID,  \
MARINAS_LETTER_ID,  EXPERIMENT_TOOLS_ID,ANALYSIS_RESULT_ID,        TERYS_ORDER3_ID,      \
LIST_OF_HOST_ID,    ABYSS_RUNESTONE1_ID,ABYSS_RUNESTONE2_ID,       ABYSS_RUNESTONE3_ID,  \
ABYSS_RUNESTONE4_ID,TERYS_REPORT_ID,    MARK_OF_SEEKER_ID = range(2647,2674)

DROPLIST={
20198:[TERYS_ORDER1_ID,MYSTERIOUS_RUNESTONE_ID,   10,1],
20211:[TERYS_ORDER2_ID,OL_MAHUM_RUNESTONE_ID,     25,1],
20495:[TERYS_ORDER2_ID,TUREK_RUNESTONE_ID,        25,1],
20080:[TERYS_ORDER2_ID,ANT_RUNESTONE_ID,          25,1],
20249:[TERYS_ORDER2_ID,TURAK_BUGBEAR_RUNESTONE_ID,25,1],
20234:[LIST_OF_HOST_ID,ABYSS_RUNESTONE1_ID,       25,1],
20270:[LIST_OF_HOST_ID,ABYSS_RUNESTONE2_ID,       25,1],
20088:[LIST_OF_HOST_ID,ABYSS_RUNESTONE3_ID,       25,1],
20580:[LIST_OF_HOST_ID,ABYSS_RUNESTONE4_ID,       25,1],
20158:[VIKTORS_REQUEST_ID,MEDUSAS_SCALES_ID,     100,10]
}

MOBS=DROPLIST.keys()
NPCS = [30106,30064,30106,30526,30684,30715]
class Quest (JQuest) :

 def __init__(self,id,name,descr): JQuest.__init__(self,id,name,descr)

 def onEvent (self,event,st) :
    htmltext = event
    if event == "30106-05.htm" :
      st.set("cond","1")
      st.setState(STARTED)
      st.playSound("ItemSound.quest_accept")
      st.giveItems(DUFNERS_LETTER_ID,1)
    elif event == "30064-03.htm" :
      st.takeItems(DUFNERS_LETTER_ID,1)
      st.giveItems(TERYS_ORDER1_ID,1)
      st.set("cond","2")
      st.playSound("Itemsound.quest_middle")
    elif event == "30064-06.htm" :
      st.takeItems(MYSTERIOUS_RUNESTONE_ID,1)
      st.takeItems(TERYS_ORDER1_ID,1)
      st.giveItems(TERYS_ORDER2_ID,1)
      st.set("cond","4")
      st.playSound("Itemsound.quest_middle")
    elif event == "30064-10.htm" :
      st.takeItems(OL_MAHUM_RUNESTONE_ID,1)
      st.takeItems(TUREK_RUNESTONE_ID,1)
      st.takeItems(ANT_RUNESTONE_ID,1)
      st.takeItems(TURAK_BUGBEAR_RUNESTONE_ID,1)
      st.takeItems(TERYS_ORDER2_ID,1)
      st.giveItems(TERYS_LETTER_ID,1)
      st.giveItems(TERYS_BOX_ID,1)
      st.set("cond","6")
      st.playSound("Itemsound.quest_middle")
    elif event == "30064-18.htm" :
      if st.getPlayer().getLevel()<36 :
        htmltext = "30064-17.htm"
        st.giveItems(TERYS_ORDER3_ID,1)
        st.takeItems(ANALYSIS_RESULT_ID,1)
      else:
        st.giveItems(LIST_OF_HOST_ID,1)
        st.takeItems(ANALYSIS_RESULT_ID,1)
        st.set("cond","16")
    elif event == "30684-05.htm" :
      st.giveItems(VIKTORS_LETTER_ID,1)
      st.takeItems(TERYS_LETTER_ID,1)
      st.set("cond","7")
    elif event == "30684-11.htm" :
      st.takeItems(TERYS_LETTER_ID,1)
      st.takeItems(TERYS_BOX_ID,1)
      st.takeItems(HAWKEYES_LETTER_ID,1)
      st.takeItems(VIKTORS_LETTER_ID,st.getQuestItemsCount(VIKTORS_LETTER_ID))
      st.giveItems(VIKTORS_REQUEST_ID,1)
      st.set("cond","9")
      st.playSound("Itemsound.quest_middle")
    elif event == "30684-15.htm" :
      st.takeItems(VIKTORS_REQUEST_ID,1)
      st.takeItems(MEDUSAS_SCALES_ID,st.getQuestItemsCount(MEDUSAS_SCALES_ID))
      st.giveItems(SILENS_RUNESTONE_ID,1)
      st.giveItems(ANALYSIS_REQUEST_ID,1)
      st.set("cond","11")
      st.playSound("Itemsound.quest_middle")
    elif event == "30715-02.htm" :
      st.takeItems(SILENS_RUNESTONE_ID,1)
      st.takeItems(ANALYSIS_REQUEST_ID,1)
      st.giveItems(MARINAS_LETTER_ID,1)
      st.set("cond","12")
      st.playSound("Itemsound.quest_middle")
    elif event == "30715-05.htm" :
      st.takeItems(EXPERIMENT_TOOLS_ID,1)
      st.giveItems(ANALYSIS_RESULT_ID,1)
      st.set("cond","14")
      st.playSound("Itemsound.quest_middle")
    return htmltext


 def onTalk (self,npc,player):
   htmltext = "<html><body>You are either not carrying out your quest or don't meet the criteria.</body></html>"
   st = player.getQuestState(qn)
   if not st : return htmltext
   cond = st.getInt("cond")
   npcId = npc.getNpcId()
   id = st.getState()
   if npcId != 30106 and id != STARTED : return htmltext

   if id == COMPLETED :
      htmltext = "<html><body>This quest has already been completed.</body></html>"
   elif id == CREATED :
     st.set("cond","0")
     st.set("id","0")
     st.set("onlyone","0")
   if npcId == 30106 and st.getInt("cond")==0 and st.getInt("onlyone")==0 :
     if player.getClassId().getId() in [ 0x07, 0x16, 0x23 ] :
       if player.getLevel() >= 35 :
         htmltext = "30106-03.htm"
       else:
         htmltext = "30106-02.htm"
         st.exitQuest(1)
     else:
       htmltext = "30106-00.htm"
       st.exitQuest(1)

   elif npcId == 30106 :
          if cond == 1 :
            htmltext = "30106-06.htm"
          elif cond >= 1 and st.getInt("id") != 18 :
            htmltext = "30106-07.htm"
          elif cond == 17 and st.getInt("id") == 18 :
              st.addExpAndSp(72126,11000)
              st.giveItems(7562,8)
              htmltext = "30106-08.htm"
              st.set("cond","0")
              st.set("onlyone","1")
              st.set("id","0")
              st.setState(COMPLETED)
              st.playSound("ItemSound.quest_finish")
              st.takeItems(TERYS_REPORT_ID,1)
              st.giveItems(MARK_OF_SEEKER_ID,1)
   elif npcId == 30064 and st.getQuestItemsCount(TERYS_ORDER3_ID)==1 :
      if player.getLevel()<36 :
        htmltext = "30064-20.htm"
      else:
        htmltext = "30064-21.htm"
        st.giveItems(LIST_OF_HOST_ID,1)
        st.takeItems(TERYS_ORDER3_ID,1)
        st.set("cond","16")
        st.playSound("Itemsound.quest_middle")
   elif npcId == 30064 and cond == 1 :
      htmltext = "30064-01.htm"
   elif npcId == 30064 and cond == 2 :
      htmltext = "30064-04.htm"
   elif npcId == 30064 and cond == 3 :
      htmltext = "30064-05.htm"
   elif npcId == 30064 and cond == 4 :
      htmltext = "30064-08.htm"
   elif npcId == 30064 and cond == 5 :
      htmltext = "30064-09.htm"
   elif npcId == 30064 and cond == 6 :
      htmltext = "30064-11.htm"
   elif npcId == 30064 and cond == 7 :
      htmltext = "30064-12.htm"
      st.takeItems(VIKTORS_LETTER_ID,1)
      st.giveItems(HAWKEYES_LETTER_ID,1)
      st.set("cond","8")
      st.playSound("Itemsound.quest_middle")
   elif npcId == 30064 and cond == 8 :
      htmltext = "30064-13.htm"
   elif npcId == 30064 and (cond>8 and cond<14) :
      htmltext = "30064-14.htm"
   elif npcId == 30064 and cond == 14 :
      htmltext = "30064-15.htm"
   elif npcId == 30064 and cond == 16 :
      htmltext = "30064-22.htm"
   elif npcId == 30064 and cond == 17 and st.getInt("id") != 18 :
      htmltext = "30064-23.htm"
      st.takeItems(LIST_OF_HOST_ID,1)
      st.takeItems(ABYSS_RUNESTONE1_ID,1)
      st.takeItems(ABYSS_RUNESTONE2_ID,1)
      st.takeItems(ABYSS_RUNESTONE3_ID,1)
      st.takeItems(ABYSS_RUNESTONE4_ID,1)
      st.giveItems(TERYS_REPORT_ID,1)
      st.set("id","18") #should be cond
      st.playSound("Itemsound.quest_middle")
   elif npcId == 30064 and cond == 17 and st.getInt("id") == 18 :
      htmltext = "30064-24.htm"
   elif npcId == 30684 and cond == 6 :
      htmltext = "30684-01.htm"
   elif npcId == 30684 and cond == 7 :
      htmltext = "30684-05.htm"
   elif npcId == 30684 and cond == 8 :
      htmltext = "30684-12.htm"
   elif npcId == 30684 and cond == 9 :
      htmltext = "30684-13.htm"
   elif npcId == 30684 and cond == 10 :
      htmltext = "30684-14.htm"
   elif npcId == 30684 and cond == 11 :
      htmltext = "30684-16.htm"
   elif npcId == 30684 and cond == 14 :
      htmltext = "30684-17.htm"
   elif npcId == 30715 and cond == 11 :
      htmltext = "30715-01.htm"
   elif npcId == 30715 and cond == 12 :
      htmltext = "30715-03.htm"
   elif npcId == 30715 and cond == 13 :
      htmltext = "30715-04.htm"
   elif npcId == 30715 and cond == 14 :
      htmltext = "30715-06.htm"
   elif npcId == 30526 and cond == 12 :
      htmltext = "30526-01.htm"
      st.takeItems(MARINAS_LETTER_ID,1)
      st.giveItems(EXPERIMENT_TOOLS_ID,1)
      st.set("cond","13")
   elif npcId == 30526 and cond == 13 :
      htmltext = "30526-02.htm"
   return htmltext

 def onKill(self,npc,player,isPet):
   st = player.getQuestState(qn)
   if not st : return 
   if st.getState() != STARTED : return 
   cond = st.getInt("cond")
   npcId = npc.getNpcId()
   required,item,chance,maxqty=DROPLIST[npcId]
   count = st.getQuestItemsCount(item)
   if st.getQuestItemsCount(required) and count < maxqty :
      if st.getRandom(100) < chance :
        st.giveItems(item,1)
        if count+1 == maxqty :
           st.playSound("Itemsound.quest_middle")
           if cond == 4:
              if st.getQuestItemsCount(OL_MAHUM_RUNESTONE_ID)+st.getQuestItemsCount(TUREK_RUNESTONE_ID)+st.getQuestItemsCount(ANT_RUNESTONE_ID)+st.getQuestItemsCount(TURAK_BUGBEAR_RUNESTONE_ID)==4 :
                 st.set("cond",str(cond+1))
              return
           elif cond == 16:
              if st.getQuestItemsCount(ABYSS_RUNESTONE1_ID)+st.getQuestItemsCount(ABYSS_RUNESTONE2_ID)+st.getQuestItemsCount(ABYSS_RUNESTONE3_ID)+st.getQuestItemsCount(ABYSS_RUNESTONE4_ID)==4:
                 st.set("cond",str(cond+1))
              return
           else:
             st.set("cond",str(cond+1))
        else :
           st.playSound("Itemsound.quest_itemget")
   return

QUEST       = Quest(213,qn,"Trial Of Seeker")
CREATED     = State('Start', QUEST)
STARTING     = State('Starting', QUEST)
STARTED     = State('Started', QUEST)
COMPLETED   = State('Completed', QUEST)


QUEST.setInitialState(CREATED)
QUEST.addStartNpc(NPCS[0])

for npcId in NPCS:
  QUEST.addTalkId(npcId)

for mobId in MOBS:
  QUEST.addKillId(mobId)