# Maked by Mr. Have fun! Version 0.2
# Shadow Weapon Coupons contributed by BiTi for the Official L2J Datapack Project
# Visit http://forum.l2jdp.com for more details
import sys
from com.it.br.gameserver.model.quest import State
from com.it.br.gameserver.model.quest import QuestState
from com.it.br.gameserver.model.quest.jython import QuestJython as JQuest

qn = "412_PathToDarkwizard"

SEEDS_OF_DESPAIR = 1254
SEEDS_OF_ANGER = 1253
SEEDS_OF_HORROR = 1255
SEEDS_OF_LUNACY = 1256
FAMILYS_ASHES = 1257
KNEE_BONE = 1259
HEART_OF_LUNACY = 1260
JEWEL_OF_DARKNESS = 1261
LUCKY_KEY = 1277
CANDLE = 1278
HUB_SCENT = 1279

class Quest (JQuest) :

 def __init__(self,id,name,descr): JQuest.__init__(self,id,name,descr)

 def onEvent (self,event,st) :
    htmltext = event
    level = st.getPlayer().getLevel()
    classId = st.getPlayer().getClassId().getId()
    if event == "1" :
        st.set("id","0")
        if st.getInt("cond") == 0 :
          if level >= 19 and classId == 0x26 and st.getQuestItemsCount(JEWEL_OF_DARKNESS) == 0 :
            st.set("cond","1")
            st.setState(STARTED)
            st.playSound("ItemSound.quest_accept")
            st.giveItems(SEEDS_OF_DESPAIR,1)
            htmltext = "30421-05.htm"
          elif classId != 0x26 :
              if classId == 0x27 :
                htmltext = "30421-02a.htm"
              else:
                htmltext = "30421-03.htm"
          elif level<19 and classId == 0x26 :
              htmltext = "30421-02.htm"
          elif level >= 19 and classId == 0x26 and st.getQuestItemsCount(JEWEL_OF_DARKNESS) == 1 :
              htmltext = "30421-04.htm"
    elif event == "412_1" :
          if st.getQuestItemsCount(SEEDS_OF_ANGER) :
            htmltext = "30421-06.htm"
          else:
            htmltext = "30421-07.htm"
    elif event == "412_2" :
            if st.getQuestItemsCount(SEEDS_OF_HORROR) :
              htmltext = "30421-09.htm"
            else:
              htmltext = "30421-10.htm"
    elif event == "412_3" :
            if st.getQuestItemsCount(SEEDS_OF_LUNACY) :
              htmltext = "30421-12.htm"
            elif st.getQuestItemsCount(SEEDS_OF_LUNACY) == 0 and st.getQuestItemsCount(SEEDS_OF_DESPAIR) :
                htmltext = "30421-13.htm"
                st.giveItems(HUB_SCENT,1)
    elif event == "412_4" :
          htmltext = "30415-03.htm"
          st.giveItems(LUCKY_KEY,1)
    elif event == "30418_1" :
          htmltext = "30418-02.htm"
          st.giveItems(CANDLE,1)
    return htmltext


 def onTalk (self,npc,player):
   htmltext = "<html><body>You are either not carrying out your quest or don't meet the criteria.</body></html>"
   st = player.getQuestState(qn)
   if not st : return htmltext

   npcId = npc.getNpcId()
   id = st.getState()
   if npcId != 30421 and id != STARTED : return htmltext

   if id == CREATED :
     st.setState(STARTING)
     st.set("cond","0")
     st.set("onlyone","0")
     st.set("id","0")
   if npcId == 30421 and st.getInt("cond")==0 :
        if st.getInt("cond")<15 :
          if st.getQuestItemsCount(JEWEL_OF_DARKNESS) == 0 :
            htmltext = "30421-01.htm"
            st.set("cond","0")
            return htmltext
          else:
            htmltext = "30421-04.htm"
        else:
          htmltext = "30421-04.htm"
   elif npcId == 30421 and st.getInt("cond")==1 :
        if st.getQuestItemsCount(SEEDS_OF_DESPAIR) and st.getQuestItemsCount(SEEDS_OF_HORROR) and st.getQuestItemsCount(SEEDS_OF_LUNACY) and st.getQuestItemsCount(SEEDS_OF_ANGER) :
            htmltext = "30421-16.htm"
            st.takeItems(SEEDS_OF_HORROR,1)
            st.takeItems(SEEDS_OF_ANGER,1)
            st.takeItems(SEEDS_OF_LUNACY,1)
            st.takeItems(SEEDS_OF_DESPAIR,1)
            st.giveItems(JEWEL_OF_DARKNESS,1)
            st.set("cond","0")
            st.setState(COMPLETED)
            st.playSound("ItemSound.quest_finish")
        elif st.getQuestItemsCount(SEEDS_OF_DESPAIR) == 1 and st.getQuestItemsCount(FAMILYS_ASHES) == 0 and st.getQuestItemsCount(LUCKY_KEY) == 0 and st.getQuestItemsCount(CANDLE) == 0 and st.getQuestItemsCount(HUB_SCENT) == 0 and st.getQuestItemsCount(KNEE_BONE) == 0 and st.getQuestItemsCount(HEART_OF_LUNACY) == 0 :
          htmltext = "30421-17.htm"
        elif st.getQuestItemsCount(SEEDS_OF_DESPAIR) == 1 and st.getInt("id") == 1 and st.getQuestItemsCount(SEEDS_OF_ANGER) == 0 :
            htmltext = "30421-08.htm"
        elif st.getQuestItemsCount(SEEDS_OF_DESPAIR) == 1 and st.getInt("id") == 2 and st.getQuestItemsCount(SEEDS_OF_HORROR) :
            htmltext = "30421-19.htm"
        elif st.getQuestItemsCount(SEEDS_OF_DESPAIR) == 1 and st.getInt("id") == 3 and st.getQuestItemsCount(HEART_OF_LUNACY) == 0 :
            htmltext = "30421-13.htm"
   elif npcId == 30419 and st.getInt("cond")==1 :
        if st.getQuestItemsCount(HUB_SCENT) == 0 and st.getQuestItemsCount(HEART_OF_LUNACY) == 0 :
            htmltext = "30419-01.htm"
            st.giveItems(HUB_SCENT,1)
        elif st.getQuestItemsCount(HUB_SCENT) and st.getQuestItemsCount(HEART_OF_LUNACY)<3 :
            htmltext = "30419-02.htm"
        elif st.getQuestItemsCount(HUB_SCENT) and st.getQuestItemsCount(HEART_OF_LUNACY) >= 3 :
            htmltext = "30419-03.htm"
            st.giveItems(SEEDS_OF_LUNACY,1)
            st.takeItems(HEART_OF_LUNACY,3)
            st.takeItems(HUB_SCENT,1)
   elif npcId == 30415 and st.getInt("cond")==1 and st.getQuestItemsCount(SEEDS_OF_ANGER)==0 :
        if st.getQuestItemsCount(SEEDS_OF_DESPAIR) == 1 and st.getQuestItemsCount(FAMILYS_ASHES) == 0 and st.getQuestItemsCount(LUCKY_KEY) == 0 :
          htmltext = "30415-01.htm"
        elif st.getQuestItemsCount(SEEDS_OF_DESPAIR) == 1 and st.getQuestItemsCount(FAMILYS_ASHES)<3 and st.getQuestItemsCount(LUCKY_KEY) == 1 :
            htmltext = "30415-04.htm"
        elif st.getQuestItemsCount(SEEDS_OF_DESPAIR) == 1 and st.getQuestItemsCount(FAMILYS_ASHES) >= 3 and st.getQuestItemsCount(LUCKY_KEY) == 1 :
            htmltext = "30415-05.htm"
            st.giveItems(SEEDS_OF_ANGER,1)
            st.takeItems(FAMILYS_ASHES,3)
            st.takeItems(LUCKY_KEY,1)
   elif npcId == 30415 and st.getInt("cond")==1 and st.getQuestItemsCount(SEEDS_OF_ANGER)==1 :
        htmltext = "30415-06.htm"
   elif npcId == 30418 and st.getInt("cond")>0 and st.getQuestItemsCount(SEEDS_OF_HORROR)==0 :
        if st.getQuestItemsCount(SEEDS_OF_DESPAIR) == 1 and st.getQuestItemsCount(CANDLE) == 0 and st.getQuestItemsCount(KNEE_BONE) == 0 :
          htmltext = "30418-01.htm"
        elif st.getQuestItemsCount(SEEDS_OF_DESPAIR) == 1 and st.getQuestItemsCount(CANDLE) == 1 and st.getQuestItemsCount(KNEE_BONE)<2 :
            htmltext = "30418-03.htm"
        elif st.getQuestItemsCount(SEEDS_OF_DESPAIR) == 1 and st.getQuestItemsCount(CANDLE) == 1 and st.getQuestItemsCount(KNEE_BONE) >= 2 :
            htmltext = "30418-04.htm"
            st.giveItems(SEEDS_OF_HORROR,1)
            st.takeItems(CANDLE,1)
            st.takeItems(KNEE_BONE,2)
   return htmltext

 def onKill(self,npc,player,isPet):
   st = player.getQuestState(qn)
   if not st : return 
   if st.getState() != STARTED : return 
   
   npcId = npc.getNpcId()
   if npcId == 20015 :
        st.set("id","0")
        if st.getInt("cond") == 1 and st.getQuestItemsCount(LUCKY_KEY) == 1 and st.getQuestItemsCount(FAMILYS_ASHES)<3 :
          if st.getRandom(2) == 0 :
            st.giveItems(FAMILYS_ASHES,1)
            if st.getQuestItemsCount(FAMILYS_ASHES) == 3 :
              st.playSound("ItemSound.quest_middle")
            else:
              st.playSound("ItemSound.quest_itemget")
   elif npcId == 20517 :
        st.set("id","0")
        if st.getInt("cond") == 1 and st.getQuestItemsCount(CANDLE) == 1 and st.getQuestItemsCount(KNEE_BONE)<2 :
          if st.getRandom(2) == 0 :
            st.giveItems(KNEE_BONE,1)
            if st.getQuestItemsCount(KNEE_BONE) == 2 :
              st.playSound("ItemSound.quest_middle")
            else:
              st.playSound("ItemSound.quest_itemget")
   elif npcId == 20518 :
        st.set("id","0")
        if st.getInt("cond") == 1 and st.getQuestItemsCount(CANDLE) == 1 and st.getQuestItemsCount(KNEE_BONE)<2 :
          if st.getRandom(2) == 0 :
            st.giveItems(KNEE_BONE,1)
            if st.getQuestItemsCount(KNEE_BONE) == 2 :
              st.playSound("ItemSound.quest_middle")
            else:
              st.playSound("ItemSound.quest_itemget")
   elif npcId == 20022 :
        st.set("id","0")
        if st.getInt("cond") == 1 and st.getQuestItemsCount(CANDLE) == 1 and st.getQuestItemsCount(KNEE_BONE)<2 :
          if st.getRandom(2) == 0 :
            st.giveItems(KNEE_BONE,1)
            if st.getQuestItemsCount(KNEE_BONE) == 2 :
              st.playSound("ItemSound.quest_middle")
            else:
              st.playSound("ItemSound.quest_itemget")
   elif npcId == 20045 :
        st.set("id","0")
        if st.getInt("cond") == 1 and st.getQuestItemsCount(HUB_SCENT) == 1 and st.getQuestItemsCount(HEART_OF_LUNACY)<3 :
          if st.getRandom(2) == 0 :
            st.giveItems(HEART_OF_LUNACY,1)
            if st.getQuestItemsCount(HEART_OF_LUNACY) == 3 :
              st.playSound("ItemSound.quest_middle")
            else:
              st.playSound("ItemSound.quest_itemget")
   return

QUEST       = Quest(412,qn,"Path To Darkwizard")
CREATED     = State('Start', QUEST)
STARTING     = State('Starting', QUEST)
STARTED     = State('Started', QUEST)
COMPLETED   = State('Completed', QUEST)


QUEST.setInitialState(CREATED)
QUEST.addStartNpc(30421)

QUEST.addTalkId(30421)

QUEST.addTalkId(30415)
QUEST.addTalkId(30418)
QUEST.addTalkId(30419)

QUEST.addKillId(20015)
QUEST.addKillId(20022)
QUEST.addKillId(20045)
QUEST.addKillId(20517)
QUEST.addKillId(20518)