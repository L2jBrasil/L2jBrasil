# Maked by Mr. Have fun! Version 0.2
#
# Updated by ElgarL
#
import sys
from com.it.br.gameserver.model.quest import State
from com.it.br.gameserver.model.quest import QuestState
from com.it.br.gameserver.model.quest.jython import QuestJython as JQuest

qn = "216_TrialOfGuildsman"

MARK_OF_GUILDSMAN_ID = 3119
VALKONS_RECOMMEND_ID = 3120
MANDRAGORA_BERRY_ID = 3121
ALLTRANS_INSTRUCTIONS_ID = 3122
ALLTRANS_RECOMMEND1_ID = 3123
ALLTRANS_RECOMMEND2_ID = 3124
NORMANS_INSTRUCTIONS_ID = 3125
NORMANS_RECEIPT_ID = 3126
DUNINGS_INSTRUCTIONS_ID = 3127
DUNINGS_KEY_ID = 3128
NORMANS_LIST_ID = 3129
GRAY_BONE_POWDER_ID = 3130
GRANITE_WHETSTONE_ID = 3131
RED_PIGMENT_ID = 3132
BRAIDED_YARN_ID = 3133
JOURNEYMAN_GEM_ID = 3134
PINTERS_INSTRUCTIONS_ID = 3135
AMBER_BEAD_ID = 3136
AMBER_LUMP_ID = 3137
JOURNEYMAN_DECO_BEADS_ID = 3138
JOURNEYMAN_RING_ID = 3139
RP_JOURNEYMAN_RING_ID = 3024
ADENA_ID = 57
RP_AMBER_BEAD_ID = 3025

class Quest (JQuest) :

 def __init__(self,id,name,descr): JQuest.__init__(self,id,name,descr)

 def onEvent (self,event,st) :
    htmltext = event
    if event == "1" :
        htmltext = "30103-06.htm"
        st.set("cond","1")
        st.setState(STARTED)
        st.playSound("ItemSound.quest_accept")
        st.giveItems(VALKONS_RECOMMEND_ID,1)
        st.takeItems(ADENA_ID,2000)
    elif event == "30103_1" :
          htmltext = "30103-04.htm"
    elif event == "30103_2" :
          if st.getQuestItemsCount(ADENA_ID) >= 2000 :
            htmltext = "30103-05.htm"
          else:
            htmltext = "30103-05a.htm"
    elif event == "30103_3" :
          if st.getGameTicks() != st.getInt("id") :
            st.set("id",str(st.getGameTicks()))
            htmltext = "30103-09a.htm"
            st.set("cond","0")
            st.set("onlyone","1")
            st.setState(COMPLETED)
            st.playSound("ItemSound.quest_finish")
            st.addExpAndSp(32000,3900)
            st.takeItems(JOURNEYMAN_RING_ID,st.getQuestItemsCount(JOURNEYMAN_RING_ID))
            st.takeItems(ALLTRANS_INSTRUCTIONS_ID,1)
            st.takeItems(RP_JOURNEYMAN_RING_ID,1)
            st.giveItems(MARK_OF_GUILDSMAN_ID,1)
    elif event == "30103_4" :
            st.addExpAndSp(80933,12250)
            st.giveItems(7562,8)
            htmltext = "30103-09b.htm"
            st.set("cond","0")
            st.set("onlyone","1")
            st.setState(COMPLETED)
            st.playSound("ItemSound.quest_finish")
            st.takeItems(JOURNEYMAN_RING_ID,st.getQuestItemsCount(JOURNEYMAN_RING_ID))
            st.takeItems(ALLTRANS_INSTRUCTIONS_ID,1)
            st.takeItems(RP_JOURNEYMAN_RING_ID,1)
            st.giveItems(MARK_OF_GUILDSMAN_ID,1)
    elif event == "30283_1" :
          htmltext = "30283-03.htm"
          st.giveItems(ALLTRANS_INSTRUCTIONS_ID,1)
          st.takeItems(VALKONS_RECOMMEND_ID,1)
          st.giveItems(RP_JOURNEYMAN_RING_ID,1)
          st.takeItems(MANDRAGORA_BERRY_ID,1)
          st.giveItems(ALLTRANS_RECOMMEND1_ID,1)
          st.giveItems(ALLTRANS_RECOMMEND2_ID,1)
    elif event == "30210_1" :
          htmltext = "30210-02.htm"
    elif event == "30210_2" :
          htmltext = "30210-03.htm"
    elif event == "30210_3" :
          htmltext = "30210-04.htm"
          st.giveItems(NORMANS_INSTRUCTIONS_ID,1)
          st.takeItems(ALLTRANS_RECOMMEND1_ID,1)
          st.giveItems(NORMANS_RECEIPT_ID,1)
    elif event == "30210_4" :
          htmltext = "30210-08.htm"
    elif event == "30210_5" :
          htmltext = "30210-09.htm"
    elif event == "30210_6" :
          htmltext = "30210-10.htm"
          st.takeItems(DUNINGS_KEY_ID,st.getQuestItemsCount(DUNINGS_KEY_ID))
          st.giveItems(NORMANS_LIST_ID,1)
          st.takeItems(NORMANS_INSTRUCTIONS_ID,1)
    elif event == "30688_1" :
          htmltext = "30688-02.htm"
          st.giveItems(DUNINGS_INSTRUCTIONS_ID,1)
          st.takeItems(NORMANS_RECEIPT_ID,1)
    elif event == "30298_1" :
          htmltext = "30298-03.htm"
    elif event == "30298_2" :
          if st.getPlayer().getClassId().getId() == 0x36 :
            htmltext = "30298-04.htm"
            st.giveItems(PINTERS_INSTRUCTIONS_ID,1)
            st.takeItems(ALLTRANS_RECOMMEND2_ID,1)
          else:
            htmltext = "30298-05.htm"
            st.giveItems(RP_AMBER_BEAD_ID,1)
            st.takeItems(ALLTRANS_RECOMMEND2_ID,1)
            st.giveItems(PINTERS_INSTRUCTIONS_ID,1)
    return htmltext


 def onTalk (self,npc,player):
   htmltext = "<html><body>You are either not carrying out your quest or don't meet the criteria.</body></html>"
   st = player.getQuestState(qn)
   if not st : return htmltext

   npcId = npc.getNpcId()
   id = st.getState()
   if npcId != 30103 and id != STARTED : return htmltext

   if id == CREATED :
     st.setState(STARTING)
     st.set("cond","0")
     st.set("onlyone","0")
     st.set("id","0")
   if npcId == 30103 and st.getInt("cond")==0 and st.getInt("onlyone")==0 :
          if player.getClassId().getId() in [0x38, 0x36] :
            if player.getLevel() < 35 :
              htmltext = "30103-02.htm"
              st.exitQuest(1)
            else:
              htmltext = "30103-03.htm"
          else:
            htmltext = "30103-01.htm"
            st.exitQuest(1)
   elif npcId == 30103 and st.getInt("cond")==0 and st.getInt("onlyone")==1 :
      htmltext = "<html><body>This quest has already been completed.</body></html>"
   elif npcId == 30103 and st.getInt("cond")>=1 and st.getQuestItemsCount(VALKONS_RECOMMEND_ID)==1 :
        htmltext = "30103-07.htm"
   elif npcId == 30103 and st.getInt("cond")>=1 and st.getQuestItemsCount(ALLTRANS_INSTRUCTIONS_ID)==1 :
        if st.getQuestItemsCount(JOURNEYMAN_RING_ID) < 7 :
          htmltext = "30103-08.htm"
        else:
          htmltext = "30103-09.htm"
   elif npcId == 30283 and st.getInt("cond")>=1 and st.getQuestItemsCount(VALKONS_RECOMMEND_ID)==1 and st.getQuestItemsCount(MANDRAGORA_BERRY_ID)==0 :
        htmltext = "30283-01.htm"
   elif npcId == 30283 and st.getInt("cond")>=1 and st.getQuestItemsCount(VALKONS_RECOMMEND_ID)==1 and st.getQuestItemsCount(MANDRAGORA_BERRY_ID)==1 :
        htmltext = "30283-02.htm"
   elif npcId == 30283 and st.getInt("cond")>=1 and st.getQuestItemsCount(ALLTRANS_INSTRUCTIONS_ID)==1 :
        if st.getQuestItemsCount(JOURNEYMAN_RING_ID) < 7 :
          htmltext = "30283-04.htm"
        else:
          htmltext = "30283-05.htm"
   elif npcId == 30210 and st.getInt("cond")>=1 and st.getQuestItemsCount(ALLTRANS_INSTRUCTIONS_ID)==1 and st.getQuestItemsCount(ALLTRANS_RECOMMEND1_ID)==1 :
        htmltext = "30210-01.htm"
   elif npcId == 30210 and st.getInt("cond")>=1 and st.getQuestItemsCount(ALLTRANS_INSTRUCTIONS_ID) and st.getQuestItemsCount(NORMANS_INSTRUCTIONS_ID) and st.getQuestItemsCount(NORMANS_RECEIPT_ID) :
        htmltext = "30210-05.htm"
   elif npcId == 30210 and st.getInt("cond")>=1 and st.getQuestItemsCount(ALLTRANS_INSTRUCTIONS_ID) and st.getQuestItemsCount(NORMANS_INSTRUCTIONS_ID) and st.getQuestItemsCount(DUNINGS_INSTRUCTIONS_ID) :
        htmltext = "30210-06.htm"
   elif npcId == 30210 and st.getInt("cond")>=1 and st.getQuestItemsCount(ALLTRANS_INSTRUCTIONS_ID) and st.getQuestItemsCount(NORMANS_INSTRUCTIONS_ID) and st.getQuestItemsCount(DUNINGS_KEY_ID)>=30 :
        htmltext = "30210-07.htm"
   elif npcId == 30210 and st.getInt("cond")>=1 and st.getQuestItemsCount(ALLTRANS_INSTRUCTIONS_ID) and st.getQuestItemsCount(NORMANS_LIST_ID) :
        if st.getQuestItemsCount(GRAY_BONE_POWDER_ID) >= 70 and st.getQuestItemsCount(GRANITE_WHETSTONE_ID) >= 70  :
          htmltext = "30210-12.htm"
          st.takeItems(NORMANS_LIST_ID,1)
          st.takeItems(GRAY_BONE_POWDER_ID,st.getQuestItemsCount(GRAY_BONE_POWDER_ID))
          st.takeItems(GRANITE_WHETSTONE_ID,st.getQuestItemsCount(GRANITE_WHETSTONE_ID))
          st.takeItems(RED_PIGMENT_ID,st.getQuestItemsCount(RED_PIGMENT_ID))
          st.takeItems(BRAIDED_YARN_ID,st.getQuestItemsCount(BRAIDED_YARN_ID))
          st.giveItems(JOURNEYMAN_GEM_ID,7)
        else:
          htmltext = "30210-11.htm"
   elif npcId == 30210 and st.getInt("cond")>=1 and st.getQuestItemsCount(NORMANS_INSTRUCTIONS_ID) == 0 and st.getQuestItemsCount(NORMANS_LIST_ID) == 0 and st.getQuestItemsCount(ALLTRANS_INSTRUCTIONS_ID)==1 and (st.getQuestItemsCount(JOURNEYMAN_GEM_ID) or st.getQuestItemsCount(JOURNEYMAN_RING_ID)) :
        htmltext = "30210-13.htm"
   elif npcId == 30688 and st.getInt("cond")>=1 and st.getQuestItemsCount(ALLTRANS_INSTRUCTIONS_ID) and st.getQuestItemsCount(NORMANS_INSTRUCTIONS_ID) and st.getQuestItemsCount(NORMANS_RECEIPT_ID) :
        htmltext = "30688-01.htm"
   elif npcId == 30688 and st.getInt("cond")>=1 and st.getQuestItemsCount(ALLTRANS_INSTRUCTIONS_ID) and st.getQuestItemsCount(NORMANS_INSTRUCTIONS_ID) and st.getQuestItemsCount(DUNINGS_INSTRUCTIONS_ID) :
        htmltext = "30688-03.htm"
   elif npcId == 30688 and st.getInt("cond")>=1 and st.getQuestItemsCount(ALLTRANS_INSTRUCTIONS_ID) and st.getQuestItemsCount(NORMANS_INSTRUCTIONS_ID) and st.getQuestItemsCount(DUNINGS_KEY_ID)>=30 :
        htmltext = "30688-04.htm"
   elif npcId == 30688 and st.getInt("cond")>=1 and st.getQuestItemsCount(NORMANS_RECEIPT_ID) == 0 and st.getQuestItemsCount(DUNINGS_INSTRUCTIONS_ID) == 0 and st.getQuestItemsCount(DUNINGS_KEY_ID) == 0 and st.getQuestItemsCount(ALLTRANS_INSTRUCTIONS_ID)==1 :
        htmltext = "30688-01.htm"
   elif npcId == 30298 and st.getInt("cond")>=1 and st.getQuestItemsCount(ALLTRANS_INSTRUCTIONS_ID) and st.getQuestItemsCount(ALLTRANS_RECOMMEND2_ID) :
        if player.getLevel() < 36 :
          htmltext = "30298-01.htm"
        else:
          htmltext = "30298-02.htm"
   elif npcId == 30298 and st.getInt("cond")>=1 and st.getQuestItemsCount(ALLTRANS_INSTRUCTIONS_ID) and st.getQuestItemsCount(PINTERS_INSTRUCTIONS_ID) :
        if st.getQuestItemsCount(AMBER_BEAD_ID) < 70 :
          htmltext = "30298-06.htm"
        else:
          htmltext = "30298-07.htm"
          st.takeItems(PINTERS_INSTRUCTIONS_ID,1)
          st.takeItems(AMBER_BEAD_ID,st.getQuestItemsCount(AMBER_BEAD_ID))
          st.takeItems(RP_AMBER_BEAD_ID,st.getQuestItemsCount(RP_AMBER_BEAD_ID))
          st.takeItems(AMBER_LUMP_ID,st.getQuestItemsCount(AMBER_LUMP_ID))
          st.giveItems(JOURNEYMAN_DECO_BEADS_ID,7)
   elif npcId == 30298 and st.getInt("cond")>=1 and st.getQuestItemsCount(ALLTRANS_INSTRUCTIONS_ID)==1 and st.getQuestItemsCount(PINTERS_INSTRUCTIONS_ID)==0 and (st.getQuestItemsCount(JOURNEYMAN_DECO_BEADS_ID) or st.getQuestItemsCount(JOURNEYMAN_RING_ID)) :
        htmltext = "30298-08.htm"
   return htmltext

 def onKill(self,npc,player,isPet):
   st = player.getQuestState(qn)
   if not st : return 
   if st.getState() != STARTED : return 

   npcId = npc.getNpcId()
   if npcId == 20223 :
     st.set("id","0")
     if st.getInt("cond") >= 1 and st.getQuestItemsCount(VALKONS_RECOMMEND_ID) == 1 and st.getQuestItemsCount(MANDRAGORA_BERRY_ID) == 0 :
       if st.getRandom(100) <= 20 :
         st.giveItems(MANDRAGORA_BERRY_ID,1)
         st.playSound("ItemSound.quest_middle")
   elif npcId in range(20154,20157):
     st.set("id","0")
     if st.getInt("cond") >= 1 and st.getQuestItemsCount(VALKONS_RECOMMEND_ID) == 1 and st.getQuestItemsCount(MANDRAGORA_BERRY_ID) == 0 :
       if st.getRandom(100) <= 50 :
        st.giveItems(MANDRAGORA_BERRY_ID,1)
        st.playSound("ItemSound.quest_middle")
   elif npcId in range(20267,20272):
    st.set("id","0")
    if st.getInt("cond") >= 1 and st.getQuestItemsCount(ALLTRANS_INSTRUCTIONS_ID) == 1 and st.getQuestItemsCount(NORMANS_INSTRUCTIONS_ID) == 1 and st.getQuestItemsCount(DUNINGS_INSTRUCTIONS_ID) == 1 :
     if st.getRandom(100) <= 30 and st.getQuestItemsCount(DUNINGS_KEY_ID) <= 29 :
      if st.getQuestItemsCount(DUNINGS_KEY_ID) == 29 :
        st.giveItems(DUNINGS_KEY_ID,1)
        st.takeItems(DUNINGS_INSTRUCTIONS_ID,1)
        st.playSound("ItemSound.quest_middle")
      else:
        st.giveItems(DUNINGS_KEY_ID,1)
        st.playSound("ItemSound.quest_itemget")
   elif npcId in [20201,20200]:
    st.set("id","0")
    if st.getInt("cond") >= 1 and st.getQuestItemsCount(ALLTRANS_INSTRUCTIONS_ID) == 1 and st.getQuestItemsCount(NORMANS_LIST_ID) == 1 and st.getQuestItemsCount(GRAY_BONE_POWDER_ID) <= 68 :
     if st.getQuestItemsCount(GRAY_BONE_POWDER_ID) == 68 :
      st.giveItems(GRAY_BONE_POWDER_ID,2)
      st.playSound("ItemSound.quest_middle")
     else:
      st.giveItems(GRAY_BONE_POWDER_ID,2)
      st.playSound("ItemSound.quest_itemget")
   elif npcId == 20083 :
    st.set("id","0")
    if st.getInt("cond") >= 1 and st.getQuestItemsCount(ALLTRANS_INSTRUCTIONS_ID) == 1 and st.getQuestItemsCount(NORMANS_LIST_ID) == 1 and st.getQuestItemsCount(GRANITE_WHETSTONE_ID) <= 68 :
     if st.getQuestItemsCount(GRANITE_WHETSTONE_ID) == 68 :
      st.giveItems(GRANITE_WHETSTONE_ID,2)
      st.playSound("ItemSound.quest_middle")
     else:
      st.giveItems(GRANITE_WHETSTONE_ID,2)
      st.playSound("ItemSound.quest_itemget")
   elif npcId == 20202 :
    st.set("id","0")
    if st.getInt("cond") >= 1 and st.getQuestItemsCount(ALLTRANS_INSTRUCTIONS_ID) == 1 and st.getQuestItemsCount(NORMANS_LIST_ID) == 1 and st.getQuestItemsCount(RED_PIGMENT_ID) <= 68 :
     if st.getQuestItemsCount(RED_PIGMENT_ID) == 68 :
      st.giveItems(RED_PIGMENT_ID,2)
      st.playSound("ItemSound.quest_middle")
     else:
      st.giveItems(RED_PIGMENT_ID,2)
      st.playSound("ItemSound.quest_itemget")
   elif npcId == 20168 :
    st.set("id","0")
    if st.getInt("cond") >= 1 and st.getQuestItemsCount(ALLTRANS_INSTRUCTIONS_ID) == 1 and st.getQuestItemsCount(NORMANS_LIST_ID) == 1 and st.getQuestItemsCount(BRAIDED_YARN_ID) <= 68 :
     if st.getQuestItemsCount(BRAIDED_YARN_ID) == 68 :
      st.giveItems(BRAIDED_YARN_ID,2)
      st.playSound("ItemSound.quest_middle")
     else:
      st.giveItems(BRAIDED_YARN_ID,2)
      st.playSound("ItemSound.quest_itemget")
   elif npcId in range(20079,20082) :
    st.set("id","0")
    if st.getInt("cond") >= 1 and st.getQuestItemsCount(ALLTRANS_INSTRUCTIONS_ID) == 1 and st.getQuestItemsCount(PINTERS_INSTRUCTIONS_ID) == 1 :
     if st.getQuestItemsCount(AMBER_BEAD_ID) < 70 :
      if st.getRandom(100) < 50 and player.getClassId().getId() == 0x36: #and IsSpoiled() == 1 :
        st.giveItems(AMBER_BEAD_ID,1)
        st.playSound("Itemsound.quest_itemget")
      if st.getRandom(100) < 50 :
        if st.getQuestItemsCount(AMBER_BEAD_ID) < 69 :
          st.giveItems(AMBER_BEAD_ID,1)
          st.playSound("Itemsound.quest_itemget")
        else:
          st.giveItems(AMBER_BEAD_ID,1)
          st.playSound("ItemSound.quest_middle")

   return

QUEST       = Quest(216,qn,"Trial Of Guildsman")
CREATED     = State('Start', QUEST)
STARTING     = State('Starting', QUEST)
STARTED     = State('Started', QUEST)
COMPLETED   = State('Completed', QUEST)


QUEST.setInitialState(CREATED)
QUEST.addStartNpc(30103)

QUEST.addTalkId(30103)

QUEST.addTalkId(30210)
QUEST.addTalkId(30283)
QUEST.addTalkId(30298)
QUEST.addTalkId(30688)

QUEST.addKillId(20154)
QUEST.addKillId(20155)
QUEST.addKillId(20156)
QUEST.addKillId(20168)
QUEST.addKillId(20200)
QUEST.addKillId(20201)
QUEST.addKillId(20202)
QUEST.addKillId(20223)
QUEST.addKillId(20267)
QUEST.addKillId(20268)
QUEST.addKillId(20269)
QUEST.addKillId(20270)
QUEST.addKillId(20271)
QUEST.addKillId(20079)
QUEST.addKillId(20080)
QUEST.addKillId(20081)
QUEST.addKillId(20083)
