# Made by Mr. Have fun! Version 0.2
# Shadow Weapon Coupons contributed by BiTi for the Official L2J Datapack Project
# Visit http://forum.l2jdp.com for more details
import sys
from com.it.br.gameserver.model.quest import State
from com.it.br.gameserver.model.quest import QuestState
from com.it.br.gameserver.model.quest.jython import QuestJython as JQuest

qn = "411_PathToAssassin"

SHILENS_CALL = 1245
ARKENIAS_LETTER = 1246
LEIKANS_NOTE = 1247
ONYX_BEASTS_MOLAR = 1248
SHILENS_TEARS = 1250
ARKENIA_RECOMMEND = 1251
IRON_HEART = 1252

class Quest (JQuest) :

 def __init__(self,id,name,descr): JQuest.__init__(self,id,name,descr)

 def onEvent (self,event,st) :
    htmltext = event
    level = st.getPlayer().getLevel()
    classId = st.getPlayer().getClassId().getId()
    if event == "1" :
        if level >= 19 and classId == 0x1f and st.getQuestItemsCount(IRON_HEART) == 0 :
          st.set("cond","1")
          st.setState(STARTED)
          st.playSound("ItemSound.quest_accept")
          st.giveItems(SHILENS_CALL,1)
          htmltext = "30416-05.htm"
        elif classId != 0x1f :
            if classId == 0x23 :
              htmltext = "30416-02a.htm"
            else:
              htmltext = "30416-02.htm"
              st.exitQuest(1)
        elif level<19 and classId == 0x1f :
            htmltext = "30416-03.htm"
            st.exitQuest(1)
        elif level >= 19 and classId == 0x1f and st.getQuestItemsCount(IRON_HEART) == 1 :
            htmltext = "30416-04.htm"
    elif event == "30419_1" :
          htmltext = "30419-05.htm"
          st.giveItems(ARKENIAS_LETTER,1)
          st.takeItems(SHILENS_CALL,1)
          st.set("cond","2")
          st.playSound("ItemSound.quest_middle")
    elif event == "30382_1" :
          htmltext = "30382-03.htm"
          st.giveItems(LEIKANS_NOTE,1)
          st.takeItems(ARKENIAS_LETTER,1)
          st.set("cond","3")
          st.playSound("ItemSound.quest_middle")
    return htmltext


 def onTalk (self,npc,player):
   htmltext = "<html><body>You are either not carrying out your quest or don't meet the criteria.</body></html>"
   st = player.getQuestState(qn)
   if not st : return htmltext

   npcId = npc.getNpcId()
   id = st.getState()
   if npcId != 30416 and id != STARTED : return htmltext

   if id == CREATED :
     st.set("cond","0")
     st.set("onlyone","0")
   if npcId == 30416 and st.getInt("cond")==0 :
     if st.getQuestItemsCount(IRON_HEART) == 0 :
        htmltext = "30416-01.htm"
     else:
        htmltext = "30416-04.htm"
   elif npcId == 30416 and st.getInt("cond")>=1 :
        if st.getQuestItemsCount(ARKENIAS_LETTER) == 0 and st.getQuestItemsCount(LEIKANS_NOTE) == 0 and st.getQuestItemsCount(SHILENS_TEARS) == 0 and st.getQuestItemsCount(ARKENIA_RECOMMEND) == 1 and st.getQuestItemsCount(IRON_HEART) == 0 :
          htmltext = "30416-06.htm"
          st.takeItems(ARKENIA_RECOMMEND,1)
          st.giveItems(IRON_HEART,1)
          st.set("cond","0")
          st.setState(COMPLETED)
          st.playSound("ItemSound.quest_finish")
        elif st.getQuestItemsCount(ARKENIAS_LETTER) == 1 and st.getQuestItemsCount(LEIKANS_NOTE) == 0 and st.getQuestItemsCount(SHILENS_TEARS) == 0 and st.getQuestItemsCount(ARKENIA_RECOMMEND) == 0 and st.getQuestItemsCount(IRON_HEART) == 0 and st.getQuestItemsCount(SHILENS_CALL) == 0 :
            htmltext = "30416-07.htm"
        elif st.getQuestItemsCount(ARKENIAS_LETTER) == 0 and st.getQuestItemsCount(LEIKANS_NOTE) == 1 and st.getQuestItemsCount(SHILENS_TEARS) == 0 and st.getQuestItemsCount(ARKENIA_RECOMMEND) == 0 and st.getQuestItemsCount(IRON_HEART) == 0 and st.getQuestItemsCount(SHILENS_CALL) == 0 :
            htmltext = "30416-08.htm"
        elif st.getQuestItemsCount(ARKENIAS_LETTER) == 0 and st.getQuestItemsCount(LEIKANS_NOTE) == 0 and st.getQuestItemsCount(SHILENS_TEARS) == 0 and st.getQuestItemsCount(ARKENIA_RECOMMEND) == 0 and st.getQuestItemsCount(IRON_HEART) == 0 and st.getQuestItemsCount(SHILENS_CALL) == 0 :
            htmltext = "30416-09.htm"
        elif st.getQuestItemsCount(ARKENIAS_LETTER) == 0 and st.getQuestItemsCount(LEIKANS_NOTE) == 0 and st.getQuestItemsCount(SHILENS_TEARS) == 1 and st.getQuestItemsCount(ARKENIA_RECOMMEND) == 0 and st.getQuestItemsCount(IRON_HEART) == 0 and st.getQuestItemsCount(SHILENS_CALL) == 0 :
            htmltext = "30416-10.htm"
        elif st.getQuestItemsCount(ARKENIAS_LETTER) == 0 and st.getQuestItemsCount(LEIKANS_NOTE) == 0 and st.getQuestItemsCount(SHILENS_TEARS) == 0 and st.getQuestItemsCount(ARKENIA_RECOMMEND) == 0 and st.getQuestItemsCount(IRON_HEART) == 0 and st.getQuestItemsCount(SHILENS_CALL) == 1 :
            htmltext = "30416-11.htm"
   elif npcId == 30419 and st.getInt("cond")>=1 :
        if st.getQuestItemsCount(ARKENIAS_LETTER) == 0 and st.getQuestItemsCount(LEIKANS_NOTE) == 0 and st.getQuestItemsCount(SHILENS_TEARS) == 0 and st.getQuestItemsCount(ARKENIA_RECOMMEND) == 0 and st.getQuestItemsCount(IRON_HEART) == 0 and st.getQuestItemsCount(SHILENS_CALL) == 1 :
          htmltext = "30419-01.htm"
        elif st.getQuestItemsCount(ARKENIAS_LETTER) == 1 and st.getQuestItemsCount(LEIKANS_NOTE) == 0 and st.getQuestItemsCount(SHILENS_TEARS) == 0 and st.getQuestItemsCount(ARKENIA_RECOMMEND) == 0 and st.getQuestItemsCount(IRON_HEART) == 0 and st.getQuestItemsCount(SHILENS_CALL) == 0 :
            htmltext = "30419-07.htm"
        elif st.getQuestItemsCount(ARKENIAS_LETTER) == 0 and st.getQuestItemsCount(LEIKANS_NOTE) == 0 and st.getQuestItemsCount(SHILENS_TEARS) == 1 and st.getQuestItemsCount(ARKENIA_RECOMMEND) == 0 and st.getQuestItemsCount(IRON_HEART) == 0 and st.getQuestItemsCount(SHILENS_CALL) == 0 :
            htmltext = "30419-08.htm"
            st.giveItems(ARKENIA_RECOMMEND,1)
            st.takeItems(SHILENS_TEARS,1)
            st.set("cond","7")
            st.playSound("ItemSound.quest_middle")
        elif st.getQuestItemsCount(ARKENIAS_LETTER) == 0 and st.getQuestItemsCount(LEIKANS_NOTE) == 0 and st.getQuestItemsCount(SHILENS_TEARS) == 0 and st.getQuestItemsCount(ARKENIA_RECOMMEND) == 1 and st.getQuestItemsCount(IRON_HEART) == 0 and st.getQuestItemsCount(SHILENS_CALL) == 0 :
            htmltext = "30419-09.htm"
        elif st.getQuestItemsCount(ARKENIAS_LETTER) == 0 and st.getQuestItemsCount(LEIKANS_NOTE) == 1 and st.getQuestItemsCount(SHILENS_TEARS) == 0 and st.getQuestItemsCount(ARKENIA_RECOMMEND) == 0 and st.getQuestItemsCount(IRON_HEART) == 0 and st.getQuestItemsCount(SHILENS_CALL) == 0 :
            htmltext = "30419-10.htm"
        elif st.getQuestItemsCount(ARKENIAS_LETTER) == 0 and st.getQuestItemsCount(LEIKANS_NOTE) == 0 and st.getQuestItemsCount(SHILENS_TEARS) == 0 and st.getQuestItemsCount(ARKENIA_RECOMMEND) == 0 and st.getQuestItemsCount(IRON_HEART) == 0 and st.getQuestItemsCount(SHILENS_CALL) == 0 :
            htmltext = "30419-11.htm"
   elif npcId == 30382 and st.getInt("cond")>=1 :
        if st.getQuestItemsCount(ARKENIAS_LETTER) == 1 and st.getQuestItemsCount(LEIKANS_NOTE) == 0 and st.getQuestItemsCount(SHILENS_TEARS) == 0 and st.getQuestItemsCount(ARKENIA_RECOMMEND) == 0 and st.getQuestItemsCount(IRON_HEART) == 0 and st.getQuestItemsCount(SHILENS_CALL) == 0 and st.getQuestItemsCount(ONYX_BEASTS_MOLAR) == 0 :
          htmltext = "30382-01.htm"
        elif st.getQuestItemsCount(ARKENIAS_LETTER) == 0 and st.getQuestItemsCount(LEIKANS_NOTE) == 1 and st.getQuestItemsCount(SHILENS_TEARS) == 0 and st.getQuestItemsCount(ARKENIA_RECOMMEND) == 0 and st.getQuestItemsCount(IRON_HEART) == 0 and st.getQuestItemsCount(SHILENS_CALL) == 0 and st.getQuestItemsCount(ONYX_BEASTS_MOLAR) == 0 :
          htmltext = "30382-05.htm"
        elif st.getQuestItemsCount(ARKENIAS_LETTER) == 0 and st.getQuestItemsCount(LEIKANS_NOTE) == 1 and st.getQuestItemsCount(SHILENS_TEARS) == 0 and st.getQuestItemsCount(ARKENIA_RECOMMEND) == 0 and st.getQuestItemsCount(IRON_HEART) == 0 and st.getQuestItemsCount(SHILENS_CALL) == 0 and st.getQuestItemsCount(ONYX_BEASTS_MOLAR)<10 :
            htmltext = "30382-06.htm"
        elif st.getQuestItemsCount(ARKENIAS_LETTER) == 0 and st.getQuestItemsCount(LEIKANS_NOTE) == 1 and st.getQuestItemsCount(SHILENS_TEARS) == 0 and st.getQuestItemsCount(ARKENIA_RECOMMEND) == 0 and st.getQuestItemsCount(IRON_HEART) == 0 and st.getQuestItemsCount(SHILENS_CALL) == 0 and st.getQuestItemsCount(ONYX_BEASTS_MOLAR) >= 10 :
            st.set("cond","5")
            st.playSound("ItemSound.quest_middle")
            htmltext = "30382-07.htm"
            st.takeItems(ONYX_BEASTS_MOLAR,10)
            st.takeItems(LEIKANS_NOTE,1)
        elif st.getQuestItemsCount(SHILENS_TEARS) == 1 :
            htmltext = "30382-08.htm"
        elif st.getInt("cond") >= 1 and st.getQuestItemsCount(ARKENIAS_LETTER) == 0 and st.getQuestItemsCount(LEIKANS_NOTE) == 0 and st.getQuestItemsCount(SHILENS_TEARS) == 0 and st.getQuestItemsCount(ARKENIA_RECOMMEND) == 0 and st.getQuestItemsCount(IRON_HEART) == 0 and st.getQuestItemsCount(SHILENS_CALL) == 0 and st.getQuestItemsCount(ONYX_BEASTS_MOLAR) == 0 :
            htmltext = "30382-09.htm"
   return htmltext

 def onKill(self,npc,player,isPet):
   st = player.getQuestState(qn)
   if not st : return 
   if st.getState() != STARTED : return 
   
   npcId = npc.getNpcId()
   if npcId == 27036 :
        if st.getInt("cond") >= 1 and st.getQuestItemsCount(SHILENS_TEARS) == 0 :
          st.giveItems(SHILENS_TEARS,1)
          st.playSound("ItemSound.quest_middle")
          st.set("cond","6")
   elif npcId == 20369 :
        if st.getInt("cond") >= 1 and st.getQuestItemsCount(LEIKANS_NOTE) == 1 and st.getQuestItemsCount(ONYX_BEASTS_MOLAR)<10 :
          st.giveItems(ONYX_BEASTS_MOLAR,1)
          if st.getQuestItemsCount(ONYX_BEASTS_MOLAR) == 10 :
              st.playSound("ItemSound.quest_middle")
              st.set("cond","4")              
          else:
              st.playSound("ItemSound.quest_itemget")
   return

QUEST       = Quest(411,qn,"Path To Assassin")
CREATED     = State('Start', QUEST)
STARTING     = State('Starting', QUEST)
STARTED     = State('Started', QUEST)
COMPLETED   = State('Completed', QUEST)

QUEST.setInitialState(CREATED)
QUEST.addStartNpc(30416)

QUEST.addTalkId(30416)

QUEST.addTalkId(30382)
QUEST.addTalkId(30419)

QUEST.addKillId(20369)
QUEST.addKillId(27036)