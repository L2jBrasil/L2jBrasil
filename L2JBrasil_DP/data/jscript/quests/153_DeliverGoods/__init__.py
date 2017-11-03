# Maked by Mr. Have fun! Version 0.2
import sys
from com.it.br import Config 
from com.it.br.gameserver.model.quest import State
from com.it.br.gameserver.model.quest import QuestState
from com.it.br.gameserver.model.quest.jython import QuestJython as JQuest

qn = "153_DeliverGoods"

DELIVERY_LIST_ID = 1012
HEAVY_WOOD_BOX_ID = 1013
CLOTH_BUNDLE_ID = 1014
CLAY_POT_ID = 1015
JACKSONS_RECEIPT_ID = 1016
SILVIAS_RECEIPT_ID = 1017
RANTS_RECEIPT_ID = 1018
LESSER_HEALING_POTION_ID = 1060
RING_ID = 875

class Quest (JQuest) :

 def __init__(self,id,name,descr): JQuest.__init__(self,id,name,descr)

 def onEvent (self,event,st) :
    htmltext = event
    if event == "1" :
        st.set("id","0")
        st.set("cond","1")
        st.setState(STARTED)
        st.playSound("ItemSound.quest_accept")
        if st.getQuestItemsCount(DELIVERY_LIST_ID) == 0 :
          st.giveItems(DELIVERY_LIST_ID,1)
        if st.getQuestItemsCount(HEAVY_WOOD_BOX_ID) == 0 :
          st.giveItems(HEAVY_WOOD_BOX_ID,1)
        if st.getQuestItemsCount(CLOTH_BUNDLE_ID) == 0 :
          st.giveItems(CLOTH_BUNDLE_ID,1)
        if st.getQuestItemsCount(CLAY_POT_ID) == 0 :
          st.giveItems(CLAY_POT_ID,1)
        htmltext = "30041-04.htm"
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
   if npcId == 30041 and st.getInt("cond")==0 and st.getInt("onlyone")==0 :
        if st.getInt("cond")<15 :
          if player.getLevel() >= 2 :
            htmltext = "30041-03.htm"
            return htmltext
          else:
            htmltext = "30041-02.htm"
            st.exitQuest(1)
        else:
          htmltext = "30041-02.htm"
          st.exitQuest(1)
   elif npcId == 30041 and st.getInt("cond")==0 and st.getInt("onlyone")==1 :
        htmltext = "<html><body>This quest has already been completed.</body></html>"
   elif npcId == 30041 and st.getInt("cond")!=0 and (st.getQuestItemsCount(JACKSONS_RECEIPT_ID)!=0 and st.getQuestItemsCount(SILVIAS_RECEIPT_ID)!=0 and st.getQuestItemsCount(RANTS_RECEIPT_ID)!=0)==0 :
        htmltext = "30041-05.htm"
   if id == STARTED :     
       if npcId == 30002 and st.getInt("cond")!=0 and st.getQuestItemsCount(HEAVY_WOOD_BOX_ID)!=0 :
            st.takeItems(HEAVY_WOOD_BOX_ID,st.getQuestItemsCount(HEAVY_WOOD_BOX_ID))
            if st.getQuestItemsCount(JACKSONS_RECEIPT_ID) == 0 :
              st.giveItems(JACKSONS_RECEIPT_ID,1)
            htmltext = "30002-01.htm"
       elif npcId == 30002 and st.getInt("cond")!=0 and st.getQuestItemsCount(JACKSONS_RECEIPT_ID)!=0 :
            htmltext = "30002-02.htm"
       elif npcId == 30003 and st.getInt("cond")!=0 and st.getQuestItemsCount(CLOTH_BUNDLE_ID)!=0 :
            st.takeItems(CLOTH_BUNDLE_ID,st.getQuestItemsCount(CLOTH_BUNDLE_ID))
            if st.getQuestItemsCount(SILVIAS_RECEIPT_ID) == 0 :
              st.giveItems(SILVIAS_RECEIPT_ID,1)
              st.giveItems(LESSER_HEALING_POTION_ID,int(Config.RATE_QUESTS_REWARD))
            htmltext = "30003-01.htm"
       elif npcId == 30003 and st.getInt("cond")!=0 and st.getQuestItemsCount(SILVIAS_RECEIPT_ID)!=0 :
            htmltext = "30003-02.htm"
       elif npcId == 30054 and st.getInt("cond")!=0 and st.getQuestItemsCount(CLAY_POT_ID)!=0 :
            st.takeItems(CLAY_POT_ID,st.getQuestItemsCount(CLAY_POT_ID))
            if st.getQuestItemsCount(RANTS_RECEIPT_ID) == 0 :
              st.giveItems(RANTS_RECEIPT_ID,1)
            htmltext = "30054-01.htm"
       elif npcId == 30054 and st.getInt("cond")!=0 and st.getQuestItemsCount(RANTS_RECEIPT_ID)!=0 :
            htmltext = "30054-02.htm"
       elif npcId == 30041 and st.getInt("cond")!=0 and (st.getQuestItemsCount(JACKSONS_RECEIPT_ID)!=0 and st.getQuestItemsCount(SILVIAS_RECEIPT_ID)!=0 and st.getQuestItemsCount(RANTS_RECEIPT_ID)!=0)!=0 and st.getInt("onlyone")==0 :
            if st.getInt("id") != 153 :
              st.set("id","153")
              st.set("cond","0")
              st.setState(COMPLETED)
              st.playSound("ItemSound.quest_finish")
              st.set("onlyone","1")
              st.giveItems(RING_ID,1)
              st.giveItems(RING_ID,1)
              st.takeItems(DELIVERY_LIST_ID,-1)
              st.takeItems(JACKSONS_RECEIPT_ID,-1)
              st.takeItems(SILVIAS_RECEIPT_ID,-1)
              st.takeItems(RANTS_RECEIPT_ID,-1)
              st.addExpAndSp(600,0)
              htmltext = "30041-06.htm"
   return htmltext

QUEST       = Quest(153,qn,"Deliver Goods")
CREATED     = State('Start', QUEST)
STARTING     = State('Starting', QUEST)
STARTED     = State('Started', QUEST)
COMPLETED   = State('Completed', QUEST)


QUEST.setInitialState(CREATED)
QUEST.addStartNpc(30041)

QUEST.addTalkId(30041)

QUEST.addTalkId(30002)
QUEST.addTalkId(30003)
QUEST.addTalkId(30054)