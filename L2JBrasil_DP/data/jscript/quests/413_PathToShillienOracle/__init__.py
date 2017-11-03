# Maked by Mr. Have fun! Version 0.2
# Shadow Weapon Coupons contributed by BiTi for the Official L2J Datapack Project
# Visit http://forum.l2jdp.com for more details
import sys
from com.it.br.gameserver.model.quest import State
from com.it.br.gameserver.model.quest import QuestState
from com.it.br.gameserver.model.quest.jython import QuestJython as JQuest

qn = "413_PathToShillienOracle"

SIDRAS_LETTER1 = 1262
BLANK_SHEET1 = 1263
BLOODY_RUNE1 = 1264
GARMIEL_BOOK = 1265
PRAYER_OF_ADON = 1266
PENITENTS_MARK = 1267
ASHEN_BONES = 1268
ANDARIEL_BOOK = 1269
ORB_OF_ABYSS = 1270

class Quest (JQuest) :

 def __init__(self,id,name,descr): JQuest.__init__(self,id,name,descr)

 def onEvent (self,event,st) :
    htmltext = event
    level = st.getPlayer().getLevel()
    classId = st.getPlayer().getClassId().getId()
    if event == "1" :
        st.set("id","0")
        htmltext = "30330-06.htm"
        st.set("cond","1")
        st.setState(STARTED)
        st.playSound("ItemSound.quest_accept")
        st.giveItems(SIDRAS_LETTER1,1)
    elif event == "413_1" :
          if level >= 19 and classId == 0x26 and st.getQuestItemsCount(ORB_OF_ABYSS) == 0 :
            htmltext = "30330-05.htm"
            return htmltext
          elif classId != 0x26 :
              if classId == 0x2a :
                htmltext = "30330-02a.htm"
              else:
                htmltext = "30330-03.htm"
          elif level<19 and classId == 0x26 :
              htmltext = "30330-02.htm"
          elif level >= 19 and classId == 0x26 and st.getQuestItemsCount(ORB_OF_ABYSS) == 1 :
              htmltext = "30330-04.htm"
    elif event == "30377_1" :
          htmltext = "30377-02.htm"
          st.takeItems(SIDRAS_LETTER1,1)
          st.giveItems(BLANK_SHEET1,5)
          st.set("cond","2")
    elif event == "30375_1" :
          htmltext = "30375-02.htm"
    elif event == "30375_2" :
            htmltext = "30375-03.htm"
    elif event == "30375_3" :
            htmltext = "30375-04.htm"
            st.takeItems(PRAYER_OF_ADON,1)
            st.giveItems(PENITENTS_MARK,1)
            st.set("cond","5")
    return htmltext


 def onTalk (self,npc,player):
   htmltext = "<html><body>You are either not carrying out your quest or don't meet the criteria.</body></html>"
   st = player.getQuestState(qn)
   if not st : return htmltext

   npcId = npc.getNpcId()
   id = st.getState()
   if npcId != 30330 and id != STARTED : return htmltext

   if id == CREATED :
     st.setState(STARTING)
     st.set("cond","0")
     st.set("onlyone","0")
     st.set("id","0")
   if npcId == 30330 and st.getInt("cond")==0 :
        if st.getInt("cond")<15 :
          htmltext = "30330-01.htm"
        else:
          htmltext = "30330-01.htm"
   elif npcId == 30330 and st.getInt("cond") :
        if st.getQuestItemsCount(SIDRAS_LETTER1) == 1 :
          htmltext = "30330-07.htm"
        elif st.getQuestItemsCount(BLANK_SHEET1)>0 or st.getQuestItemsCount(BLOODY_RUNE1) == 1 :
            htmltext = "30330-08.htm"
        elif st.getQuestItemsCount(ANDARIEL_BOOK) == 0 and st.getQuestItemsCount(PRAYER_OF_ADON)+st.getQuestItemsCount(GARMIEL_BOOK)+st.getQuestItemsCount(PENITENTS_MARK)+st.getQuestItemsCount(ASHEN_BONES)>0 :
            htmltext = "30330-09.htm"
        elif st.getQuestItemsCount(ANDARIEL_BOOK) == 1 and st.getQuestItemsCount(GARMIEL_BOOK) == 1 :
            htmltext = "30330-10.htm"
            st.takeItems(ANDARIEL_BOOK,1)
            st.takeItems(GARMIEL_BOOK,1)
            st.giveItems(ORB_OF_ABYSS,1)
            st.set("cond","0")
            st.setState(COMPLETED)
            st.playSound("ItemSound.quest_finish")
   elif npcId == 30377 and st.getInt("cond") :
        if st.getQuestItemsCount(SIDRAS_LETTER1) == 1 :
          htmltext = "30377-01.htm"
        elif st.getQuestItemsCount(BLANK_SHEET1) == 5 and st.getQuestItemsCount(BLOODY_RUNE1) == 0 :
            htmltext = "30377-03.htm"
        elif st.getQuestItemsCount(BLOODY_RUNE1)>0 and st.getQuestItemsCount(BLOODY_RUNE1)<5 :
            htmltext = "30377-04.htm"
        elif st.getQuestItemsCount(BLOODY_RUNE1) >= 5 :
            htmltext = "30377-05.htm"
            st.takeItems(BLOODY_RUNE1,st.getQuestItemsCount(BLOODY_RUNE1))
            st.giveItems(GARMIEL_BOOK,1)
            st.giveItems(PRAYER_OF_ADON,1)
            st.set("cond","4")
        elif st.getQuestItemsCount(PRAYER_OF_ADON)+st.getQuestItemsCount(PENITENTS_MARK)+st.getQuestItemsCount(ASHEN_BONES)>0 :
            htmltext = "30377-06.htm"
        elif st.getQuestItemsCount(ANDARIEL_BOOK) == 1 and st.getQuestItemsCount(GARMIEL_BOOK) == 1 :
            htmltext = "30377-07.htm"
   elif npcId == 30375 and st.getInt("cond") :
      if st.getQuestItemsCount(PRAYER_OF_ADON) == 1 :
        htmltext = "30375-01.htm"
      elif st.getQuestItemsCount(PENITENTS_MARK) == 1 and st.getQuestItemsCount(ASHEN_BONES) == 0 and st.getQuestItemsCount(ANDARIEL_BOOK) == 0 :
          htmltext = "30375-05.htm"
      elif st.getQuestItemsCount(PENITENTS_MARK) == 1 and st.getQuestItemsCount(ASHEN_BONES)<10 and st.getQuestItemsCount(ASHEN_BONES)>0 :
          htmltext = "30375-06.htm"
      elif st.getQuestItemsCount(PENITENTS_MARK) == 1 and st.getQuestItemsCount(ASHEN_BONES) >= 10 :
          htmltext = "30375-07.htm"
          st.takeItems(ASHEN_BONES,st.getQuestItemsCount(ASHEN_BONES))
          st.takeItems(PENITENTS_MARK,st.getQuestItemsCount(PENITENTS_MARK))
          st.giveItems(ANDARIEL_BOOK,1)
          st.set("cond","7")
      elif st.getQuestItemsCount(ANDARIEL_BOOK) == 1 :
          htmltext = "30375-08.htm"
   return htmltext

 def onKill(self,npc,player,isPet):
   st = player.getQuestState(qn)
   if not st : return 
   if st.getState() != STARTED : return 
   
   npcId = npc.getNpcId()
   if npcId == 20776 :
        st.set("id","0")
        if st.getInt("cond") and st.getQuestItemsCount(BLANK_SHEET1)>0 :
          st.giveItems(BLOODY_RUNE1,1)
          st.takeItems(BLANK_SHEET1,1)
          if st.getQuestItemsCount(BLANK_SHEET1) == 0 :
            st.playSound("ItemSound.quest_middle")
            st.set("cond","3")
          else:
            st.playSound("ItemSound.quest_itemget")
   elif npcId == 20514 :
        st.set("id","0")
        if st.getInt("cond") and st.getQuestItemsCount(PENITENTS_MARK) == 1 and st.getQuestItemsCount(ASHEN_BONES)<10 :
          st.giveItems(ASHEN_BONES,1)
          if st.getQuestItemsCount(ASHEN_BONES) == 10 :
            st.playSound("ItemSound.quest_middle")
            st.set("cond","6")
          else:
            st.playSound("ItemSound.quest_itemget")
   elif npcId == 20515 :
        st.set("id","0")
        if st.getInt("cond") and st.getQuestItemsCount(PENITENTS_MARK) == 1 and st.getQuestItemsCount(ASHEN_BONES)<10 :
          st.giveItems(ASHEN_BONES,1)
          if st.getQuestItemsCount(ASHEN_BONES) == 10 :
            st.playSound("ItemSound.quest_middle")
            st.set("cond","6")
          else:
            st.playSound("ItemSound.quest_itemget")
   elif npcId == 20457 :
        st.set("id","0")
        if st.getInt("cond") and st.getQuestItemsCount(PENITENTS_MARK) == 1 and st.getQuestItemsCount(ASHEN_BONES)<10 :
          st.giveItems(ASHEN_BONES,1)
          if st.getQuestItemsCount(ASHEN_BONES) == 10 :
            st.playSound("ItemSound.quest_middle")
            st.set("cond","6")
          else:
            st.playSound("ItemSound.quest_itemget")
   elif npcId == 20458 :
        st.set("id","0")
        if st.getInt("cond") and st.getQuestItemsCount(PENITENTS_MARK) == 1 and st.getQuestItemsCount(ASHEN_BONES)<10 :
          st.giveItems(ASHEN_BONES,1)
          if st.getQuestItemsCount(ASHEN_BONES) == 10 :
            st.playSound("ItemSound.quest_middle")
            st.set("cond","6")
          else:
            st.playSound("ItemSound.quest_itemget")
   return

QUEST       = Quest(413,qn,"Path To Shillien Oracle")
CREATED     = State('Start', QUEST)
STARTING     = State('Starting', QUEST)
STARTED     = State('Started', QUEST)
COMPLETED   = State('Completed', QUEST)


QUEST.setInitialState(CREATED)
QUEST.addStartNpc(30330)

QUEST.addTalkId(30330)

QUEST.addTalkId(30375)
QUEST.addTalkId(30377)

QUEST.addKillId(20457)
QUEST.addKillId(20458)
QUEST.addKillId(20514)
QUEST.addKillId(20515)
QUEST.addKillId(20776)