#
# Created by DraX on 2005.08.23
#

import sys

from com.it.br.gameserver.model.quest        import State
from com.it.br.gameserver.model.quest        import QuestState
from com.it.br.gameserver.model.quest.jython import QuestJython as JQuest
qn = "30109_hannavalt_occupation_change"

MARK_OF_CHALLENGER_ID  = 2627
MARK_OF_DUTY_ID        = 2633
MARK_OF_SEEKER_ID      = 2673
MARK_OF_TRUST_ID       = 2734
MARK_OF_DUELIST_ID     = 2762
MARK_OF_SEARCHER_ID    = 2809
MARK_OF_HEALER_ID      = 2820
MARK_OF_LIFE_ID        = 3140
MARK_OF_CHAMPION_ID    = 3276
MARK_OF_SAGITTARIUS_ID = 3293
MARK_OF_WITCHCRAFT_ID  = 3307
GRAND_MASTER_HANNAVALT = 30109
GRAND_MASTER_BLACKBIRD = 30187
GRAND_MASTER_SIRIA     = 30689
GRAND_MASTER_SEDRICK   = 30849
GRAND_MASTER_MARCUS    = 30900

class Quest (JQuest) :

 def __init__(self,id,name,descr): JQuest.__init__(self,id,name,descr)

 def onEvent (self,event,st):
   
   htmltext = "No Quest"
   
   Race     = st.getPlayer().getRace()
   ClassId  = st.getPlayer().getClassId()
   Level    = st.getPlayer().getLevel()

   if event == "30109-01.htm":
     return "30109-01.htm"

   if event == "30109-02.htm":
     return "30109-02.htm"

   if event == "30109-03.htm":
     return "30109-03.htm"

   if event == "30109-04.htm":
     return "30109-04.htm"

   if event == "30109-05.htm":
     return "30109-05.htm"

   if event == "30109-06.htm":
     return "30109-06.htm"

   if event == "30109-07.htm":
     return "30109-07.htm"

   if event == "30109-08.htm":
     return "30109-08.htm"

   if event == "30109-09.htm":
     return "30109-09.htm"

   if event == "30109-10.htm":
     return "30109-10.htm"

   if event == "30109-11.htm":
     return "30109-11.htm"

   if event == "30109-12.htm":
     return "30109-12.htm"

   if event == "30109-13.htm":
     return "30109-13.htm"

   if event == "30109-14.htm":
     return "30109-14.htm"

   if event == "30109-15.htm":
     return "30109-15.htm"

   if event == "30109-16.htm":
     return "30109-16.htm"

   if event == "30109-17.htm":
     return "30109-17.htm"

   if event == "30109-18.htm":
     return "30109-18.htm"

   if event == "30109-19.htm":
     return "30109-19.htm"

   if event == "30109-20.htm":
     return "30109-20.htm"

   if event == "30109-21.htm":
     return "30109-21.htm"

   if event == "30109-22.htm":
     return "30109-22.htm"

   if event == "30109-23.htm":
     return "30109-23.htm"

   if event == "30109-24.htm":
     return "30109-24.htm"

   if event == "30109-25.htm":
     return "30109-25.htm"

   if event == "30109-26.htm":
     return "30109-26.htm"

   if event == "30109-27.htm":
     return "30109-27.htm"

   if event == "30109-28.htm":
     return "30109-28.htm"

   if event == "30109-29.htm":
     return "30109-29.htm"

   if event == "30109-30.htm":
     return "30109-30.htm"

   if event == "30109-31.htm":
     return "30109-31.htm"

   if event == "30109-32.htm":
     return "30109-32.htm"

   if event == "30109-33.htm":
     return "30109-33.htm"

   if event == "30109-34.htm":
     return "30109-34.htm"

   if event == "30109-35.htm":
     return "30109-35.htm"

   if event == "class_change_20":
     if ClassId in [ClassId.elvenKnight]:
        if Level <= 39:
          if st.getQuestItemsCount(MARK_OF_DUTY_ID) == 0 or st.getQuestItemsCount(MARK_OF_LIFE_ID) == 0 or st.getQuestItemsCount(MARK_OF_HEALER_ID) == 0:
            htmltext = "30109-36.htm"
          else:
            htmltext = "30109-37.htm"
        else:
          if st.getQuestItemsCount(MARK_OF_DUTY_ID) == 0 or st.getQuestItemsCount(MARK_OF_LIFE_ID) == 0 or st.getQuestItemsCount(MARK_OF_HEALER_ID) == 0:
            htmltext = "30109-38.htm"
          else:
            st.takeItems(MARK_OF_DUTY_ID,1)
            st.takeItems(MARK_OF_LIFE_ID,1)
            st.takeItems(MARK_OF_HEALER_ID,1)
            st.getPlayer().setClassId(20)
            st.getPlayer().setBaseClass(20)
            st.getPlayer().broadcastUserInfo()
            st.playSound("ItemSound.quest_fanfare_2")
            htmltext = "30109-39.htm"

   if event == "class_change_21":
     if ClassId in [ClassId.elvenKnight]:
        if Level <= 39:
          if st.getQuestItemsCount(MARK_OF_CHALLENGER_ID) == 0 or st.getQuestItemsCount(MARK_OF_LIFE_ID) == 0 or st.getQuestItemsCount(MARK_OF_DUELIST_ID) == 0:
            htmltext = "30109-40.htm"
          else:
            htmltext = "30109-41.htm"
        else:
          if st.getQuestItemsCount(MARK_OF_CHALLENGER_ID) == 0 or st.getQuestItemsCount(MARK_OF_LIFE_ID) == 0 or st.getQuestItemsCount(MARK_OF_DUELIST_ID) == 0:
            htmltext = "30109-42.htm"
          else:
            st.takeItems(MARK_OF_CHALLENGER_ID,1)
            st.takeItems(MARK_OF_LIFE_ID,1)
            st.takeItems(MARK_OF_DUELIST_ID,1)
            st.getPlayer().setClassId(21)
            st.getPlayer().setBaseClass(21)
            st.getPlayer().broadcastUserInfo()
            st.playSound("ItemSound.quest_fanfare_2")
            htmltext = "30109-43.htm"

   if event == "class_change_5":
     if ClassId in [ClassId.knight]:
        if Level <= 39:
          if st.getQuestItemsCount(MARK_OF_DUTY_ID) == 0 or st.getQuestItemsCount(MARK_OF_TRUST_ID) == 0 or st.getQuestItemsCount(MARK_OF_HEALER_ID) == 0:
            htmltext = "30109-44.htm"
          else:
            htmltext = "30109-45.htm"
        else:
          if st.getQuestItemsCount(MARK_OF_DUTY_ID) == 0 or st.getQuestItemsCount(MARK_OF_TRUST_ID) == 0 or st.getQuestItemsCount(MARK_OF_HEALER_ID) == 0:
            htmltext = "30109-46.htm"
          else:
            st.takeItems(MARK_OF_DUTY_ID,1)
            st.takeItems(MARK_OF_TRUST_ID,1)
            st.takeItems(MARK_OF_HEALER_ID,1)
            st.getPlayer().setClassId(5)
            st.getPlayer().setBaseClass(5)
            st.getPlayer().broadcastUserInfo()
            st.playSound("ItemSound.quest_fanfare_2")
            htmltext = "30109-47.htm"

   if event == "class_change_6":
     if ClassId in [ClassId.knight]:
        if Level <= 39:
          if st.getQuestItemsCount(MARK_OF_DUTY_ID) == 0 or st.getQuestItemsCount(MARK_OF_TRUST_ID) == 0 or st.getQuestItemsCount(MARK_OF_WITCHCRAFT_ID) == 0:
            htmltext = "30109-48.htm"
          else:
            htmltext = "30109-49.htm"
        else:
          if st.getQuestItemsCount(MARK_OF_DUTY_ID) == 0 or st.getQuestItemsCount(MARK_OF_TRUST_ID) == 0 or st.getQuestItemsCount(MARK_OF_WITCHCRAFT_ID) == 0:
            htmltext = "30109-50.htm"
          else:
            st.takeItems(MARK_OF_DUTY_ID,1)
            st.takeItems(MARK_OF_TRUST_ID,1)
            st.takeItems(MARK_OF_WITCHCRAFT_ID,1)
            st.getPlayer().setClassId(6)
            st.getPlayer().setBaseClass(6)
            st.getPlayer().broadcastUserInfo()
            st.playSound("ItemSound.quest_fanfare_2")
            htmltext = "30109-51.htm"

   if event == "class_change_8":
     if ClassId in [ClassId.rogue]:
        if Level <= 39:
          if st.getQuestItemsCount(MARK_OF_SEEKER_ID) == 0 or st.getQuestItemsCount(MARK_OF_TRUST_ID) == 0 or st.getQuestItemsCount(MARK_OF_SEARCHER_ID) == 0:
            htmltext = "30109-52.htm"
          else:
            htmltext = "30109-53.htm"
        else:
          if st.getQuestItemsCount(MARK_OF_SEEKER_ID) == 0 or st.getQuestItemsCount(MARK_OF_TRUST_ID) == 0 or st.getQuestItemsCount(MARK_OF_SEARCHER_ID) == 0:
            htmltext = "30109-54.htm"
          else:
            st.takeItems(MARK_OF_SEEKER_ID,1)
            st.takeItems(MARK_OF_TRUST_ID,1)
            st.takeItems(MARK_OF_SEARCHER_ID,1)
            st.getPlayer().setClassId(8)
            st.getPlayer().setBaseClass(8)
            st.getPlayer().broadcastUserInfo()
            st.playSound("ItemSound.quest_fanfare_2")
            htmltext = "30109-55.htm"

   if event == "class_change_9":
     if ClassId in [ClassId.rogue]:
        if Level <= 39:
          if st.getQuestItemsCount(MARK_OF_SEEKER_ID) == 0 or st.getQuestItemsCount(MARK_OF_TRUST_ID) == 0 or st.getQuestItemsCount(MARK_OF_SAGITTARIUS_ID) == 0:
            htmltext = "30109-56.htm"
          else:
            htmltext = "30109-57.htm"
        else:
          if st.getQuestItemsCount(MARK_OF_SEEKER_ID) == 0 or st.getQuestItemsCount(MARK_OF_TRUST_ID) == 0 or st.getQuestItemsCount(MARK_OF_SAGITTARIUS_ID) == 0:
            htmltext = "30109-58.htm"
          else:
            st.takeItems(MARK_OF_SEEKER_ID,1)
            st.takeItems(MARK_OF_TRUST_ID,1)
            st.takeItems(MARK_OF_SAGITTARIUS_ID,1)
            st.getPlayer().setClassId(9)
            st.getPlayer().setBaseClass(9)
            st.getPlayer().broadcastUserInfo()
            st.playSound("ItemSound.quest_fanfare_2")
            htmltext = "30109-59.htm"

   if event == "class_change_23":
     if ClassId in [ClassId.elvenScout]:
        if Level <= 39:
          if st.getQuestItemsCount(MARK_OF_SEEKER_ID) == 0 or st.getQuestItemsCount(MARK_OF_LIFE_ID) == 0 or st.getQuestItemsCount(MARK_OF_SEARCHER_ID) == 0:
            htmltext = "30109-60.htm"
          else:
            htmltext = "30109-61.htm"
        else:
          if st.getQuestItemsCount(MARK_OF_SEEKER_ID) == 0 or st.getQuestItemsCount(MARK_OF_LIFE_ID) == 0 or st.getQuestItemsCount(MARK_OF_SEARCHER_ID) == 0:
            htmltext = "30109-62.htm"
          else:
            st.takeItems(MARK_OF_SEEKER_ID,1)
            st.takeItems(MARK_OF_LIFE_ID,1)
            st.takeItems(MARK_OF_SEARCHER_ID,1)
            st.getPlayer().setClassId(23)
            st.getPlayer().setBaseClass(23)
            st.getPlayer().broadcastUserInfo()
            st.playSound("ItemSound.quest_fanfare_2")
            htmltext = "30109-63.htm"

   if event == "class_change_24":
     if ClassId in [ClassId.elvenScout]:
        if Level <= 39:
          if st.getQuestItemsCount(MARK_OF_SEEKER_ID) == 0 or st.getQuestItemsCount(MARK_OF_LIFE_ID) == 0 or st.getQuestItemsCount(MARK_OF_SAGITTARIUS_ID) == 0:
            htmltext = "30109-64.htm"
          else:
            htmltext = "30109-65.htm"
        else:
          if st.getQuestItemsCount(MARK_OF_SEEKER_ID) == 0 or st.getQuestItemsCount(MARK_OF_LIFE_ID) == 0 or st.getQuestItemsCount(MARK_OF_SAGITTARIUS_ID) == 0:
            htmltext = "30109-66.htm"
          else:
            st.takeItems(MARK_OF_SEEKER_ID,1)
            st.takeItems(MARK_OF_LIFE_ID,1)
            st.takeItems(MARK_OF_SAGITTARIUS_ID,1)
            st.getPlayer().setClassId(24)
            st.getPlayer().setBaseClass(24)
            st.getPlayer().broadcastUserInfo()
            st.playSound("ItemSound.quest_fanfare_2")
            htmltext = "30109-67.htm"

   if event == "class_change_2":
     if ClassId in [ClassId.warrior]:
        if Level <= 39:
          if st.getQuestItemsCount(MARK_OF_CHALLENGER_ID) == 0 or st.getQuestItemsCount(MARK_OF_TRUST_ID) == 0 or st.getQuestItemsCount(MARK_OF_DUELIST_ID) == 0:
            htmltext = "30109-68.htm"
          else:
            htmltext = "30109-69.htm"
        else:
          if st.getQuestItemsCount(MARK_OF_CHALLENGER_ID) == 0 or st.getQuestItemsCount(MARK_OF_TRUST_ID) == 0 or st.getQuestItemsCount(MARK_OF_DUELIST_ID) == 0:
            htmltext = "30109-70.htm"
          else:
            st.takeItems(MARK_OF_CHALLENGER_ID,1)
            st.takeItems(MARK_OF_TRUST_ID,1)
            st.takeItems(MARK_OF_DUELIST_ID,1)
            st.getPlayer().setClassId(2)
            st.getPlayer().setBaseClass(2)
            st.getPlayer().broadcastUserInfo()
            st.playSound("ItemSound.quest_fanfare_2")
            htmltext = "30109-71.htm"

   if event == "class_change_3":
     if ClassId in [ClassId.warrior]:
        if Level <= 39:
          if st.getQuestItemsCount(MARK_OF_CHALLENGER_ID) == 0 or st.getQuestItemsCount(MARK_OF_TRUST_ID) == 0 or st.getQuestItemsCount(MARK_OF_CHAMPION_ID) == 0:
            htmltext = "30109-72.htm"
          else:
            htmltext = "30109-73.htm"
        else:
          if st.getQuestItemsCount(MARK_OF_CHALLENGER_ID) == 0 or st.getQuestItemsCount(MARK_OF_TRUST_ID) == 0 or st.getQuestItemsCount(MARK_OF_CHAMPION_ID) == 0:
            htmltext = "30109-74.htm"
          else:
            st.takeItems(MARK_OF_CHALLENGER_ID,1)
            st.takeItems(MARK_OF_TRUST_ID,1)
            st.takeItems(MARK_OF_CHAMPION_ID,1)
            st.getPlayer().setClassId(3)
            st.getPlayer().setBaseClass(3)
            st.getPlayer().broadcastUserInfo()
            st.playSound("ItemSound.quest_fanfare_2")
            htmltext = "30109-75.htm"
          
   st.setState(COMPLETED)
   st.exitQuest(1)
   return htmltext


 def onTalk (Self,npc,player):
   st = player.getQuestState(qn)
   npcId = npc.getNpcId()
   
   Race    = st.getPlayer().getRace()
   ClassId = st.getPlayer().getClassId()
   
   # Humans and Elfs got accepted
   if npcId == GRAND_MASTER_HANNAVALT or GRAND_MASTER_SIRIA or GRAND_MASTER_BLACKBIRD or GRAND_MASTER_SEDRICK or GRAND_MASTER_MARCUS and Race in [Race.elf, Race.human]:
     if ClassId in [ClassId.elvenKnight]:
       st.setState(STARTED)
       return "30109-01.htm"
     elif ClassId in [ClassId.knight]:
       st.setState(STARTED)
       return "30109-08.htm"
     elif ClassId in [ClassId.rogue]:
       st.setState(STARTED)
       return "30109-15.htm"
     elif ClassId in [ClassId.elvenScout]:
       st.setState(STARTED)
       return "30109-22.htm"
     elif ClassId in [ClassId.warrior]:
       st.setState(STARTED)
       return "30109-29.htm"
     elif ClassId in [ClassId.elvenFighter, ClassId.fighter]:
       st.setState(COMPLETED)
       st.exitQuest(1)
       return "30109-76.htm"     
     elif ClassId in [ClassId.templeKnight, ClassId.plainsWalker, ClassId.swordSinger, ClassId.silverRanger]:
       st.setState(COMPLETED)
       st.exitQuest(1)
       return "30109-77.htm"
     elif ClassId in [ClassId.warlord, ClassId.paladin, ClassId.treasureHunter]:
       st.setState(COMPLETED)
       st.exitQuest(1)
       return "30109-77.htm"
     elif ClassId in [ClassId.gladiator, ClassId.darkAvenger, ClassId.hawkeye]:
       st.setState(COMPLETED)
       st.exitQuest(1)
       return "30109-77.htm"
     else:
       st.setState(COMPLETED)
       st.exitQuest(1)
       return "30109-78.htm"

   # All other Races must be out
   else:
     st.setState(COMPLETED)
     st.exitQuest(1)
     return "30109-78.htm"

QUEST     = Quest(30109,qn,"village_master")
CREATED   = State('Start',     QUEST)
STARTED   = State('Started',   QUEST)
COMPLETED = State('Completed', QUEST)

QUEST.setInitialState(CREATED)

QUEST.addStartNpc(30109)
QUEST.addStartNpc(30187)
QUEST.addStartNpc(30689)
QUEST.addStartNpc(30849)
QUEST.addStartNpc(30900)
QUEST.addStartNpc(31965)
QUEST.addStartNpc(32094)

QUEST.addTalkId(30109)
QUEST.addTalkId(30187)
QUEST.addTalkId(30689)
QUEST.addTalkId(30849)
QUEST.addTalkId(30900)
QUEST.addTalkId(31965)
QUEST.addTalkId(32094)
