# Maked by Mr. Have fun! Version 0.2
# Shadow Weapon Coupons contributed by BiTi for the Official L2J Datapack Project
# Visit http://forum.l2jdp.com for more details
import sys
from com.it.br.gameserver.model.quest import State
from com.it.br.gameserver.model.quest import QuestState
from com.it.br.gameserver.model.quest.jython import QuestJython as JQuest

qn = "418_PathToArtisan"

SILVERYS_RING = 1632
PASS_1ST = 1633
PASS_2ND = 1634
PASS_FINAL = 1635
RATMAN_TOOTH = 1636
BIG_RATMAN_TOOTH = 1637
KLUTOS_LETTER = 1638
FOOTPRINT = 1639
SECRET_BOX1 = 1640
SECRET_BOX2 = 1641
TOTEM_SPIRIT_CLAW = 1622
TATARUS_LETTER = 1623

class Quest (JQuest) :

 def __init__(self,id,name,descr): JQuest.__init__(self,id,name,descr)

 def onEvent (self,event,st) :
    htmltext = event
    player = st.getPlayer()
    if event == "30527_1" :
          if player.getClassId().getId() != 0x35 :
            if player.getClassId().getId() == 0x38 :
              htmltext = "30527-02a.htm"
            else:
              htmltext = "30527-02.htm"
          else:
            if player.getLevel()<19 :
              htmltext = "30527-03.htm"
            else:
              if st.getQuestItemsCount(PASS_FINAL) != 0 :
                htmltext = "30527-04.htm"
              else:
                htmltext = "30527-05.htm"
                return htmltext
    elif event == "30527_2" :
          htmltext = "30527-11.htm"
          st.takeItems(TOTEM_SPIRIT_CLAW,1)
          st.giveItems(TATARUS_LETTER,1)
    elif event == "1" :
        st.set("id","0")
        st.set("cond","1")
        st.setState(STARTED)
        st.playSound("ItemSound.quest_accept")
        htmltext = "30527-06.htm"
        st.giveItems(SILVERYS_RING,1)
    elif event == "30317_1" :
          htmltext = "30317-02.htm"
    elif event == "30317_2" :
          htmltext = "30317-05.htm"
    elif event == "30317_3" :
          htmltext = "30317-03.htm"
    elif event == "30317_4" :
          htmltext = "30317-04.htm"
          st.giveItems(KLUTOS_LETTER,1)
          st.set("cond","4")
    elif event == "30317_5" :
          htmltext = "30317-06.htm"
    elif event == "30317_6" :
          htmltext = "30317-07.htm"
          st.giveItems(KLUTOS_LETTER,1)
          st.set("cond","4")
    elif event == "30317_7" :
        if st.getQuestItemsCount(PASS_1ST) and st.getQuestItemsCount(PASS_2ND) and st.getQuestItemsCount(SECRET_BOX2) :
          htmltext = "30317-10.htm"
          st.takeItems(PASS_1ST,1)
          st.takeItems(PASS_2ND,1)
          st.takeItems(SECRET_BOX2,1)
          st.giveItems(PASS_FINAL,1)
          st.set("cond","0")
          st.setState(COMPLETED)
          st.playSound("ItemSound.quest_finish")
        else :
          htmltext = "30317-08.htm"
    elif event == "30317_8" :
          htmltext = "30317-11.htm"
    elif event == "30317_9" :
        if st.getQuestItemsCount(PASS_1ST) and st.getQuestItemsCount(PASS_2ND) and st.getQuestItemsCount(SECRET_BOX2) :
          htmltext = "30317-12.htm"
          st.takeItems(PASS_1ST,1)
          st.takeItems(PASS_2ND,1)
          st.takeItems(SECRET_BOX2,1)
          st.giveItems(PASS_FINAL,1)
          st.set("cond","0")
          st.setState(COMPLETED)
          st.playSound("ItemSound.quest_finish")
        else :
          htmltext = "30317-08.htm"
    elif event == "30298_1" :
          htmltext = "30298-02.htm"
    elif event == "30298_2" :
          htmltext = "30298-03.htm"
          st.takeItems(KLUTOS_LETTER,1)
          st.giveItems(FOOTPRINT,1)
          st.set("cond","5")
    elif event == "30298_3" :
          htmltext = "30298-06.htm"
          st.takeItems(SECRET_BOX1,1)
          st.takeItems(FOOTPRINT,1)
          st.giveItems(SECRET_BOX2,1)
          st.giveItems(PASS_2ND,1)
          st.set("cond","7")
    return htmltext


 def onTalk (self,npc,player):
   htmltext = "<html><body>You are either not carrying out your quest or don't meet the criteria.</body></html>"
   st = player.getQuestState(qn)
   if not st : return htmltext

   npcId = npc.getNpcId()
   id = st.getState()
   if npcId != 30527 and id != STARTED : return htmltext

   if id == CREATED :
     st.setState(STARTING)
     st.set("cond","0")
     st.set("onlyone","0")
     st.set("id","0")
   if npcId == 30527 and st.getInt("cond")==0 :
          htmltext = "30527-01.htm"
   elif npcId == 30527 and st.getInt("cond") and st.getQuestItemsCount(SILVERYS_RING)==1 and (st.getQuestItemsCount(RATMAN_TOOTH)+st.getQuestItemsCount(BIG_RATMAN_TOOTH))<12 :
        htmltext = "30527-07.htm"
   elif npcId == 30527 and st.getInt("cond") and st.getQuestItemsCount(SILVERYS_RING)==1 and st.getQuestItemsCount(RATMAN_TOOTH)>=10 and st.getQuestItemsCount(BIG_RATMAN_TOOTH)>=2 :
        htmltext = "30527-08.htm"
        st.takeItems(SILVERYS_RING,st.getQuestItemsCount(SILVERYS_RING))
        st.takeItems(RATMAN_TOOTH,st.getQuestItemsCount(RATMAN_TOOTH))
        st.takeItems(BIG_RATMAN_TOOTH,st.getQuestItemsCount(BIG_RATMAN_TOOTH))
        st.giveItems(PASS_1ST,1)
        st.set("cond","3")
   elif npcId == 30527 and st.getInt("cond") and st.getQuestItemsCount(PASS_1ST)==1 :
        htmltext = "30527-09.htm"
   elif npcId == 30317 and st.getInt("cond") and st.getQuestItemsCount(KLUTOS_LETTER)==0 and st.getQuestItemsCount(PASS_1ST) and st.getQuestItemsCount(PASS_2ND)==0 and st.getQuestItemsCount(SECRET_BOX2)==0 :
        htmltext = "30317-01.htm"
   elif npcId == 30317 and st.getInt("cond") and st.getQuestItemsCount(PASS_1ST) and (st.getQuestItemsCount(KLUTOS_LETTER) or st.getQuestItemsCount(FOOTPRINT)) :
        htmltext = "30317-08.htm"
   elif npcId == 30317 and st.getInt("cond") and st.getQuestItemsCount(PASS_1ST) and st.getQuestItemsCount(PASS_2ND) and st.getQuestItemsCount(SECRET_BOX2) :
        htmltext = "30317-09.htm"
   elif npcId == 30298 and st.getInt("cond") and st.getQuestItemsCount(PASS_1ST) and st.getQuestItemsCount(KLUTOS_LETTER) :
        htmltext = "30298-01.htm"
   elif npcId == 30298 and st.getInt("cond") and st.getQuestItemsCount(PASS_1ST) and st.getQuestItemsCount(FOOTPRINT) and st.getQuestItemsCount(SECRET_BOX1)==0 :
        htmltext = "30298-04.htm"
   elif npcId == 30298 and st.getInt("cond") and st.getQuestItemsCount(PASS_1ST) and st.getQuestItemsCount(FOOTPRINT) and st.getQuestItemsCount(SECRET_BOX1) :
        htmltext = "30298-05.htm"
   elif npcId == 30298 and st.getInt("cond") and st.getQuestItemsCount(PASS_1ST) and st.getQuestItemsCount(PASS_2ND) and st.getQuestItemsCount(SECRET_BOX2) :
        htmltext = "30298-07.htm"
   return htmltext

 def onKill(self,npc,player,isPet):
   st = player.getQuestState(qn)
   if not st : return 
   if st.getState() != STARTED : return
   npcId = npc.getNpcId()
   if npcId == 20389 :
        st.set("id","0")
        if st.getInt("cond") and st.getQuestItemsCount(SILVERYS_RING) == 1 and st.getQuestItemsCount(RATMAN_TOOTH)<10 :
          if st.getRandom(10)<7 :
            if st.getQuestItemsCount(RATMAN_TOOTH) == 9 and st.getQuestItemsCount(BIG_RATMAN_TOOTH) == 2 :
              st.giveItems(RATMAN_TOOTH,1)
              st.playSound("ItemSound.quest_middle")
              st.set("cond","2")
            else:
              st.giveItems(RATMAN_TOOTH,1)
              st.playSound("ItemSound.quest_itemget")
   elif npcId == 20390 :
        st.set("id","0")
        if st.getInt("cond") and st.getQuestItemsCount(SILVERYS_RING) == 1 and st.getQuestItemsCount(BIG_RATMAN_TOOTH)<2 :
          if st.getRandom(10)<5 :
            if st.getQuestItemsCount(BIG_RATMAN_TOOTH) == 1 and st.getQuestItemsCount(RATMAN_TOOTH) == 10 :
              st.giveItems(BIG_RATMAN_TOOTH,1)
              st.playSound("ItemSound.quest_middle")
              st.set("cond","2")
            else:
              st.giveItems(BIG_RATMAN_TOOTH,1)
              st.playSound("ItemSound.quest_itemget")
   elif npcId == 20017 :
        st.set("id","0")
        if st.getInt("cond") and st.getQuestItemsCount(FOOTPRINT) == 1 and st.getQuestItemsCount(SECRET_BOX1)<1 :
          if st.getRandom(10)<2 :
            st.giveItems(SECRET_BOX1,1)
            st.playSound("ItemSound.quest_middle")
            st.set("cond","6")
   return

QUEST       = Quest(418,qn,"Path To Artisan")
CREATED     = State('Start', QUEST)
STARTING     = State('Starting', QUEST)
STARTED     = State('Started', QUEST)
COMPLETED   = State('Completed', QUEST)

QUEST.setInitialState(CREATED)
QUEST.addStartNpc(30527)

QUEST.addTalkId(30527)

QUEST.addTalkId(30298)
QUEST.addTalkId(30317)

QUEST.addKillId(20017)
QUEST.addKillId(20389)
QUEST.addKillId(20390)