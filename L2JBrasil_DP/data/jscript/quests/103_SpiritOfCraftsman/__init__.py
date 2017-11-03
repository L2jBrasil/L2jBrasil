# Made by Mr. Have fun! - Version 0.3 by DrLecter

import sys
from com.it.br import Config
from com.it.br.gameserver.model.quest import State
from com.it.br.gameserver.model.quest import QuestState
from com.it.br.gameserver.model.quest.jython import QuestJython as JQuest

qn = "103_SpiritOfCraftsman"

KAROYDS_LETTER_ID = 968
CECKTINONS_VOUCHER1_ID = 969
CECKTINONS_VOUCHER2_ID = 970
BONE_FRAGMENT1_ID = 1107
SOUL_CATCHER_ID = 971
PRESERVE_OIL_ID = 972
ZOMBIE_HEAD_ID = 973
STEELBENDERS_HEAD_ID = 974
BLOODSABER_ID = 975

class Quest (JQuest) :

 def __init__(self,id,name,descr): 
    JQuest.__init__(self,id,name,descr)
    self.questItemIds = [KAROYDS_LETTER_ID, CECKTINONS_VOUCHER1_ID, CECKTINONS_VOUCHER2_ID, BONE_FRAGMENT1_ID,
    					SOUL_CATCHER_ID, PRESERVE_OIL_ID, ZOMBIE_HEAD_ID, STEELBENDERS_HEAD_ID]

 def onEvent (self,event,st) :
    htmltext = event
    if event == "30307-05.htm" :
        st.giveItems(KAROYDS_LETTER_ID,1)
        st.set("cond","1")
        st.setState(STARTED)
        st.playSound(self.SOUND_QUEST_START)
    return htmltext

 def onTalk (self,npc,player) :
   npcId = npc.getNpcId()
   htmltext = self.NO_QUEST
   st = player.getQuestState(qn)
   if not st: return htmltext
   id = st.getState()
   if id == CREATED :
     st.set("cond","0")
     st.set("onlyone","0")
   if npcId == 30307 and st.getInt("cond")==0 and st.getInt("onlyone")==0 :
     if player.getRace().ordinal() != 2 :
        htmltext = "30307-00.htm"
     elif player.getLevel() >= 10 :
        htmltext = "30307-03.htm"
        return htmltext
     else:
        htmltext = "30307-02.htm"
        st.exitQuest(1)
   elif npcId == 30307 and st.getInt("cond")==0 and st.getInt("onlyone")==1 :
        htmltext = self.QUEST_DONE
   elif id == STARTED : 
       if npcId == 30307 and st.getInt("cond")>=1 and (st.getQuestItemsCount(KAROYDS_LETTER_ID)>=1 or st.getQuestItemsCount(CECKTINONS_VOUCHER1_ID)>=1 or st.getQuestItemsCount(CECKTINONS_VOUCHER2_ID)>=1) :
            htmltext = "30307-06.htm"
       elif npcId == 30132 and st.getInt("cond")==1 and st.getQuestItemsCount(KAROYDS_LETTER_ID)==1 :
            htmltext = "30132-01.htm"
            st.set("cond","2")
            st.takeItems(KAROYDS_LETTER_ID,1)
            st.giveItems(CECKTINONS_VOUCHER1_ID,1)
       elif npcId == 30132 and st.getInt("cond")>=2 and (st.getQuestItemsCount(CECKTINONS_VOUCHER1_ID)>=1 or st.getQuestItemsCount(CECKTINONS_VOUCHER2_ID)>=1) :
            htmltext = "30132-02.htm"
       elif npcId == 30144 and st.getInt("cond")==2 and st.getQuestItemsCount(CECKTINONS_VOUCHER1_ID)>=1 :
            htmltext = "30144-01.htm"
            st.set("cond","3")
            st.takeItems(CECKTINONS_VOUCHER1_ID,1)
            st.giveItems(CECKTINONS_VOUCHER2_ID,1)
       elif npcId == 30144 and st.getInt("cond")==3 and st.getQuestItemsCount(CECKTINONS_VOUCHER2_ID)>=1 and st.getQuestItemsCount(BONE_FRAGMENT1_ID)<10 :
            htmltext = "30144-02.htm"
       elif npcId == 30144 and st.getInt("cond")==4 and st.getQuestItemsCount(CECKTINONS_VOUCHER2_ID)==1 and st.getQuestItemsCount(BONE_FRAGMENT1_ID)>=10 :
            htmltext = "30144-03.htm"
            st.set("cond","5")
            st.takeItems(CECKTINONS_VOUCHER2_ID,1)
            st.takeItems(BONE_FRAGMENT1_ID,10)
            st.giveItems(SOUL_CATCHER_ID,1)
       elif npcId == 30144 and st.getInt("cond")==5 and st.getQuestItemsCount(SOUL_CATCHER_ID)==1 :
            htmltext = "30144-04.htm"
       elif npcId == 30132 and st.getInt("cond")==5 and st.getQuestItemsCount(SOUL_CATCHER_ID)==1 :
            htmltext = "30132-03.htm"
            st.set("cond","6")
            st.takeItems(SOUL_CATCHER_ID,1)
            st.giveItems(PRESERVE_OIL_ID,1)
       elif npcId == 30132 and st.getInt("cond")==6 and st.getQuestItemsCount(PRESERVE_OIL_ID)==1 and st.getQuestItemsCount(ZOMBIE_HEAD_ID)==0 and st.getQuestItemsCount(STEELBENDERS_HEAD_ID)==0 :
            htmltext = "30132-04.htm"
       elif npcId == 30132 and st.getInt("cond")==7 and st.getQuestItemsCount(ZOMBIE_HEAD_ID)==1 :
            htmltext = "30132-05.htm"
            st.set("cond","8")
            st.takeItems(ZOMBIE_HEAD_ID,1)
            st.giveItems(STEELBENDERS_HEAD_ID,1)
       elif npcId == 30132 and st.getInt("cond")==8 and st.getQuestItemsCount(STEELBENDERS_HEAD_ID)==1 :
            htmltext = "30132-06.htm"
       elif npcId == 30307 and st.getInt("cond")==8 and st.getQuestItemsCount(STEELBENDERS_HEAD_ID)==1 :
            htmltext = "30307-07.htm"
            st.takeItems(STEELBENDERS_HEAD_ID,1)
            st.giveItems(BLOODSABER_ID,1)
            st.set("cond","0")
            st.setState(COMPLETED)
            st.playSound(self.SOUND_QUEST_DONE)
            st.set("onlyone","1")
   return htmltext

 def onKill(self,npc,player,isPet):
   st = player.getQuestState(qn)
   if not st: return
   if st.getState() != STARTED : return 
   npcId = npc.getNpcId()
   if npcId in [20517,20518,20455] :
      bones = st.getQuestItemsCount(BONE_FRAGMENT1_ID)
      if st.getQuestItemsCount(CECKTINONS_VOUCHER2_ID) == 1 and bones < 10 :
         numItems, chance = divmod(30*Config.RATE_DROP_QUEST,100)
         if st.getRandom(100) <= chance :
            numItems += 1
         numItems = int(numItems)
         if numItems != 0 :
            if 10 <= (bones + numItems) :
               numItems = 10 - bones
               st.playSound(self.SOUND_QUEST_MIDDLE)
               st.set("cond","4")
            else:
               st.playSound(self.SOUND_ITEM_GET)
            st.giveItems(BONE_FRAGMENT1_ID,numItems)
   elif npcId in [20015,20020] :
      if st.getQuestItemsCount(PRESERVE_OIL_ID) == 1 :
         if st.getRandom(10)<3*Config.RATE_DROP_QUEST :
            st.set("cond","7")
            st.giveItems(ZOMBIE_HEAD_ID,1)
            st.playSound(SOUND_QUEST_MIDDLE)
            st.takeItems(PRESERVE_OIL_ID,1)
   return

QUEST       = Quest(103,qn,"Spirit Of Craftsman")
CREATED     = State('Start', QUEST)
STARTED     = State('Started', QUEST)
COMPLETED   = State('Completed', QUEST)

QUEST.setInitialState(CREATED)
QUEST.addStartNpc(30307)

QUEST.addTalkId(30307)

QUEST.addTalkId(30132)
QUEST.addTalkId(30144)

QUEST.addKillId(20015)
QUEST.addKillId(20020)
QUEST.addKillId(20455)
QUEST.addKillId(20517)
QUEST.addKillId(20518)