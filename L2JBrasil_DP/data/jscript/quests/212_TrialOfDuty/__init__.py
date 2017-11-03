# Made by Mr. Have fun! Version 0.2
# Fixed by Artful (http://L2PLanet.ru Lineage2 C3 Server)
# version 0.4  - updated by Kerberos on 2007.11.10
import sys
from com.it.br.gameserver.model.quest import State
from com.it.br.gameserver.model.quest import QuestState
from com.it.br.gameserver.model.quest.jython import QuestJython as JQuest

qn = "212_TrialOfDuty"

MARK_OF_DUTY = 2633
LETTER_OF_DUSTIN = 2634
KNIGHTS_TEAR = 2635
MIRROR_OF_ORPIC = 2636
TEAR_OF_CONFESSION = 2637
REPORT_PIECE = 2638
TALIANUSS_REPORT = 2639
TEAR_OF_LOYALTY = 2640
MILITAS_ARTICLE = 2641
SAINTS_ASHES_URN = 2642
ATEBALTS_SKULL = 2643
ATEBALTS_RIBS = 2644
ATEBALTS_SHIN = 2645
LETTER_OF_WINDAWOOD = 2646
OLD_KNIGHT_SWORD = 3027

class Quest (JQuest) :

 def __init__(self,id,name,descr): JQuest.__init__(self,id,name,descr)

 def onEvent (self,event,st) :
    htmltext = event
    if event == "1" :
      htmltext = "30109-04.htm"
      st.setState(STARTED)
      st.playSound("ItemSound.quest_accept")
      st.set("cond","1")
    elif event == "30116_1" :
          htmltext = "30116-02.htm"
    elif event == "30116_2" :
          htmltext = "30116-03.htm"
    elif event == "30116_3" :
          htmltext = "30116-04.htm"
    elif event == "30116_4" :
          htmltext = "30116-05.htm"
          st.takeItems(TEAR_OF_LOYALTY,1)
          st.set("cond","14")
          st.playSound("ItemSound.quest_middle")
    return htmltext


 def onTalk (self,npc,player):
   htmltext = "<html><body>You are either not carrying out your quest or don't meet the criteria.</body></html>"
   st = player.getQuestState(qn)
   if not st : return htmltext
   cond=st.getInt("cond")
   npcId = npc.getNpcId()
   id = st.getState()
   if npcId != 30109 and id != STARTED : return htmltext
   if id == CREATED :
     st.set("cond","0")
     st.set("onlyone","0")
     st.set("id","0")
   if npcId == 30109 and cond == 0 and st.getInt("onlyone")==0 :
      if player.getClassId().ordinal() in [ 0x04, 0x13, 0x20] :
         if player.getLevel() >= 35 :
            htmltext = "30109-03.htm"
         else :
            htmltext = "30109-01.htm"
            st.exitQuest(1)
      else:
         htmltext = "30109-02.htm"
         st.exitQuest(1)
   elif npcId == 30109 and cond == 0 and st.getInt("onlyone")==1 :
      htmltext = "<html><body>This quest has already been completed.</body></html>"
   elif npcId == 30109 and cond == 18  and st.getQuestItemsCount(LETTER_OF_DUSTIN):
      st.addExpAndSp(79832,3750)
      st.giveItems(7562,8)
      htmltext = "30109-05.htm"
      st.takeItems(LETTER_OF_DUSTIN,1)
      st.giveItems(MARK_OF_DUTY,1)
      st.setState(COMPLETED)
      st.playSound("ItemSound.quest_finish")
      st.set("onlyone","1")
      st.set("cond","0")
   elif npcId == 30109 and cond == 1 :
      htmltext = "30109-04.htm"
   elif npcId == 30653 and cond == 1 :
      htmltext = "30653-01.htm"
      if st.getQuestItemsCount(OLD_KNIGHT_SWORD) == 0 :
        st.giveItems(OLD_KNIGHT_SWORD,1)
      st.set("cond","2")
      st.playSound("ItemSound.quest_middle")
   elif npcId == 30653 and cond == 2 and st.getQuestItemsCount(KNIGHTS_TEAR)==0 :
      htmltext = "30653-02.htm"
   elif npcId == 30653 and cond == 3 and st.getQuestItemsCount(KNIGHTS_TEAR) :
      htmltext = "30653-03.htm"
      st.takeItems(KNIGHTS_TEAR,1)
      st.takeItems(OLD_KNIGHT_SWORD,1)
      st.set("cond","4")
      st.playSound("ItemSound.quest_middle")
   elif npcId == 30653 and cond == 4 :
      htmltext = "30653-04.htm"
   elif npcId == 30654 and cond == 4 :
      htmltext = "30654-01.htm"
      st.set("cond","5")
      st.playSound("ItemSound.quest_middle")
   elif npcId == 30654 and cond == 5 and st.getQuestItemsCount(TALIANUSS_REPORT)==0 :
      htmltext = "30654-02.htm"
   elif npcId == 30654 and cond == 6 and st.getQuestItemsCount(TALIANUSS_REPORT) :
      htmltext = "30654-03.htm"
      st.set("cond","7")
      st.playSound("ItemSound.quest_middle")
      st.giveItems(MIRROR_OF_ORPIC,1)
   elif npcId == 30654 and cond == 7 :
      htmltext = "30654-04.htm"
   elif npcId == 30654 and cond == 9 and st.getQuestItemsCount(TEAR_OF_CONFESSION) :
      htmltext = "30654-05.htm"
      st.takeItems(TEAR_OF_CONFESSION,1)
      st.set("cond","10")
      st.playSound("ItemSound.quest_middle")
   elif npcId == 30654 and cond == 10 :
      htmltext = "30654-06.htm"
   elif npcId == 30656 and cond == 8 and st.getQuestItemsCount(MIRROR_OF_ORPIC) :
      htmltext = "30656-01.htm"
      st.takeItems(MIRROR_OF_ORPIC,1)
      st.takeItems(TALIANUSS_REPORT,1)
      st.giveItems(TEAR_OF_CONFESSION,1)
      st.set("cond","9")
      st.playSound("ItemSound.quest_middle")
   elif npcId == 30655 and cond == 10 :
      if player.getLevel() >= 36 :
        htmltext = "30655-02.htm"
        st.set("cond","11")
        st.playSound("ItemSound.quest_middle")
      else:
        htmltext = "30655-01.htm"
   elif npcId == 30655 and cond == 11 :
      htmltext = "30655-03.htm"
   elif npcId == 30655 and cond == 12 :
      htmltext = "30655-04.htm"
      st.takeItems(MILITAS_ARTICLE,st.getQuestItemsCount(MILITAS_ARTICLE))
      st.giveItems(TEAR_OF_LOYALTY,1)
      st.set("cond","13")
      st.playSound("ItemSound.quest_middle")
   elif npcId == 30655 and cond == 13 :
      htmltext = "30655-05.htm"
   elif npcId == 30116 and cond == 13 and st.getQuestItemsCount(TEAR_OF_LOYALTY) :
      htmltext = "30116-01.htm"
   elif npcId == 30116 and cond == 14 :
      htmltext = "30116-06.htm"
   elif npcId == 30116 and cond == 15 :
      htmltext = "30116-07.htm"
      st.takeItems(ATEBALTS_SKULL,1)
      st.takeItems(ATEBALTS_RIBS,1)
      st.takeItems(ATEBALTS_SHIN,1)
      st.giveItems(SAINTS_ASHES_URN,1)
      st.set("cond","16")
      st.playSound("ItemSound.quest_middle")
   elif npcId == 30116 and cond == 17 :
      htmltext = "30116-08.htm"
      st.takeItems(LETTER_OF_WINDAWOOD,1)
      st.giveItems(LETTER_OF_DUSTIN,1)
      st.set("cond","18")
      st.playSound("ItemSound.quest_middle")
   elif npcId == 30116 and cond == 16 :
      htmltext = "30116-09.htm"
   elif npcId == 30116 and cond == 18 :
      htmltext = "30116-10.htm"
   elif npcId == 30311 and cond == 16 and st.getQuestItemsCount(SAINTS_ASHES_URN) :
      htmltext = "30311-01.htm"
      st.takeItems(SAINTS_ASHES_URN,1)
      st.giveItems(LETTER_OF_WINDAWOOD,1)
      st.set("cond","17")
      st.playSound("ItemSound.quest_middle")
   elif npcId == 30311 and cond == 14 :
      htmltext = "30311-02.htm"
   return htmltext

 def onKill(self,npc,player,isPet):
   st = player.getQuestState(qn)
   if not st : return 
   if st.getState() != STARTED : return
   cond=st.getInt("cond")
   npcId = npc.getNpcId()
   if npcId in [20190,20191] :
      if cond == 2 :
        if st.getRandom(50)<2 :
          st.addSpawn(27119,npc,True,0)
          st.playSound("Itemsound.quest_before_battle")
   elif npcId == 27119 :
      if cond == 2 and st.getQuestItemsCount(OLD_KNIGHT_SWORD) > 0 :
        st.giveItems(KNIGHTS_TEAR,1)
        st.playSound("ItemSound.quest_middle")
        st.set("cond","3")
   elif npcId == 20200 :
      if cond == 5 and st.getQuestItemsCount(REPORT_PIECE) < 10 and st.getQuestItemsCount(TALIANUSS_REPORT) == 0 :
        if st.getQuestItemsCount(REPORT_PIECE) == 9 :
          if st.getRandom(2) == 1 :
            st.takeItems(REPORT_PIECE,st.getQuestItemsCount(REPORT_PIECE))
            st.giveItems(TALIANUSS_REPORT,1)
            st.playSound("ItemSound.quest_middle")
            st.set("cond","6")
        elif st.getRandom(2) == 1 :
          st.giveItems(REPORT_PIECE,1)
          st.playSound("ItemSound.quest_itemget")
   elif npcId == 20201 :
      if cond == 5 and st.getQuestItemsCount(REPORT_PIECE) < 10 and st.getQuestItemsCount(TALIANUSS_REPORT) == 0 :
        if st.getQuestItemsCount(REPORT_PIECE) == 9 :
          if st.getRandom(2) == 1 :
            st.takeItems(REPORT_PIECE,st.getQuestItemsCount(REPORT_PIECE))
            st.giveItems(TALIANUSS_REPORT,1)
            st.playSound("ItemSound.quest_middle")
            st.set("cond","6")
        elif st.getRandom(2) == 1 :
          st.giveItems(REPORT_PIECE,1)
          st.playSound("ItemSound.quest_itemget")
   elif npcId == 20144 :
      if cond == 7 :
        if st.getRandom(100)<33 :
           st.addSpawn(30656,npc.getX(),npc.getY(),npc.getZ(),npc.getHeading(),True,300000)
           st.playSound("Itemsound.quest_middle")
           st.set("cond","8")
   elif npcId == 20577 :
      if cond == 11 and st.getQuestItemsCount(MILITAS_ARTICLE) < 20 :
        if st.getQuestItemsCount(MILITAS_ARTICLE) == 19 :
          st.giveItems(MILITAS_ARTICLE,1)
          st.playSound("ItemSound.quest_middle")
          st.set("cond","12")
        else:
          st.giveItems(MILITAS_ARTICLE,1)
          st.playSound("ItemSound.quest_itemget")
   elif npcId == 20578 :
      if cond == 11 and st.getQuestItemsCount(MILITAS_ARTICLE) < 20 :
        if st.getQuestItemsCount(MILITAS_ARTICLE) == 19 :
          st.giveItems(MILITAS_ARTICLE,1)
          st.playSound("ItemSound.quest_middle")
          st.set("cond","12")
        else:
          st.giveItems(MILITAS_ARTICLE,1)
          st.playSound("ItemSound.quest_itemget")
   elif npcId == 20579 :
      if cond == 11 and st.getQuestItemsCount(MILITAS_ARTICLE) < 20 :
        if st.getQuestItemsCount(MILITAS_ARTICLE) == 19 :
          st.giveItems(MILITAS_ARTICLE,1)
          st.playSound("ItemSound.quest_middle")
          st.set("cond","12")
        else:
          st.giveItems(MILITAS_ARTICLE,1)
          st.playSound("ItemSound.quest_itemget")
   elif npcId == 20580 :
      if cond == 11 and st.getQuestItemsCount(MILITAS_ARTICLE) < 20 :
        if st.getQuestItemsCount(MILITAS_ARTICLE) == 19 :
          st.giveItems(MILITAS_ARTICLE,1)
          st.playSound("ItemSound.quest_middle")
          st.set("cond","12")
        else:
          st.giveItems(MILITAS_ARTICLE,1)
          st.playSound("ItemSound.quest_itemget")
   elif npcId == 20581 :
      if cond == 11 and st.getQuestItemsCount(MILITAS_ARTICLE) < 20 :
        if st.getQuestItemsCount(MILITAS_ARTICLE) == 19 :
          st.giveItems(MILITAS_ARTICLE,1)
          st.playSound("ItemSound.quest_middle")
          st.set("cond","12")
        else:
          st.giveItems(MILITAS_ARTICLE,1)
          st.playSound("ItemSound.quest_itemget")
   elif npcId == 20582 :
      if cond == 11 and st.getQuestItemsCount(MILITAS_ARTICLE) < 20 :
        if st.getQuestItemsCount(MILITAS_ARTICLE) == 19 :
          st.giveItems(MILITAS_ARTICLE,1)
          st.playSound("ItemSound.quest_middle")
          st.set("cond","12")
        else:
          st.giveItems(MILITAS_ARTICLE,1)
          st.playSound("ItemSound.quest_itemget")
   elif npcId == 20270 :
      if cond == 14 :
        if st.getRandom(2) == 1 :
          if st.getQuestItemsCount(ATEBALTS_SKULL) == 0 :
            st.giveItems(ATEBALTS_SKULL,1)
            st.playSound("ItemSound.quest_itemget")
          elif st.getQuestItemsCount(ATEBALTS_RIBS) == 0 :
            st.giveItems(ATEBALTS_RIBS,1)
            st.playSound("ItemSound.quest_itemget")
          elif st.getQuestItemsCount(ATEBALTS_SHIN) == 0 :
            st.giveItems(ATEBALTS_SHIN,1)
            st.set("cond","15")
            st.playSound("ItemSound.quest_middle")
   return

QUEST       = Quest(212,qn,"Trial Of Duty")
CREATED     = State('Start', QUEST)
STARTED     = State('Started', QUEST)
COMPLETED   = State('Completed', QUEST)


QUEST.setInitialState(CREATED)
QUEST.addStartNpc(30109)

QUEST.addTalkId(30109)

QUEST.addTalkId(30116)
QUEST.addTalkId(30311)
QUEST.addTalkId(30653)
QUEST.addTalkId(30654)
QUEST.addTalkId(30655)
QUEST.addTalkId(30656)

QUEST.addKillId(20144)
QUEST.addKillId(20190)
QUEST.addKillId(20191)
QUEST.addKillId(20200)
QUEST.addKillId(20201)
QUEST.addKillId(20270)
QUEST.addKillId(27119)
QUEST.addKillId(20577)
QUEST.addKillId(20578)
QUEST.addKillId(20579)
QUEST.addKillId(20580)
QUEST.addKillId(20581)
QUEST.addKillId(20582)