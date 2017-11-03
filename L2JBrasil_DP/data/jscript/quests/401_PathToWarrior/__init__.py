# Maked by Mr. Have fun! Version 0.2
# Updated by ElgarL
# Shadow Weapon Coupons contributed by BiTi for the Official L2J Datapack Project
# Visit http://forum.l2jdp.com for more details
import sys
from com.it.br.gameserver.model.quest import State
from com.it.br.gameserver.model.quest import QuestState
from com.it.br.gameserver.model.quest.jython import QuestJython as JQuest

qn = "401_PathToWarrior"

EINS_LETTER = 1138
WARRIOR_GUILD_MARK = 1139
RUSTED_BRONZE_SWORD1 = 1140
RUSTED_BRONZE_SWORD2 = 1141
SIMPLONS_LETTER = 1143
POISON_SPIDER_LEG2 = 1144
MEDALLION_OF_WARRIOR = 1145
RUSTED_BRONZE_SWORD3 = 1142

class Quest (JQuest) :

 def __init__(self,id,name,descr): JQuest.__init__(self,id,name,descr)

 def onEvent (self,event,st) :
    htmltext = event
    player = st.getPlayer()
    if event == "401_1" :
          if player.getClassId().getId() == 0x00 :
            if player.getLevel() >= 19 :
              if st.getQuestItemsCount(MEDALLION_OF_WARRIOR)>0 :
                htmltext = "30010-04.htm"
              else:
                htmltext = "30010-05.htm"
                return htmltext
            else :
              htmltext = "30010-02.htm"
          else:
            if player.getClassId().getId() == 0x01 :
              htmltext = "30010-02a.htm"
            else:
              htmltext = "30010-03.htm"
    elif event == "401_2" :
          htmltext = "30010-10.htm"
    elif event == "401_3" :
            htmltext = "30010-11.htm"
            st.takeItems(SIMPLONS_LETTER,1)
            st.takeItems(RUSTED_BRONZE_SWORD2,1)
            st.giveItems(RUSTED_BRONZE_SWORD3,1)
            st.set("cond","5")
    elif event == "1" :
      st.set("id","0")
      if st.getQuestItemsCount(EINS_LETTER) == 0 :
        st.set("cond","1")
        st.setState(STARTED)
        st.playSound("ItemSound.quest_accept")
        st.giveItems(EINS_LETTER,1)
        htmltext = "30010-06.htm"
    elif event == "30253_1" :
          htmltext = "30253-02.htm"
          st.takeItems(EINS_LETTER,1)
          st.giveItems(WARRIOR_GUILD_MARK,1)
          st.set("cond","2")
    return htmltext


 def onTalk (self,npc,player):
   htmltext = "<html><body>You are either not carrying out your quest or don't meet the criteria.</body></html>"
   st = player.getQuestState(qn)
   if not st : return htmltext

   npcId = npc.getNpcId()
   id = st.getState()
   if npcId != 30010 and id != STARTED : return htmltext

   if id == CREATED :
     st.setState(STARTING)
     st.set("cond","0")
     st.set("onlyone","0")
     st.set("id","0")
   if npcId == 30010 and st.getInt("cond")==0 :
      htmltext = "30010-01.htm"
   elif npcId == 30010 and st.getInt("cond") and st.getQuestItemsCount(EINS_LETTER)>0 :
      htmltext = "30010-07.htm"
   elif npcId == 30010 and st.getInt("cond") and st.getQuestItemsCount(WARRIOR_GUILD_MARK)==1 :
      htmltext = "30010-08.htm"
   elif npcId == 30253 and st.getInt("cond") and st.getQuestItemsCount(EINS_LETTER) :
      htmltext = "30253-01.htm"
   elif npcId == 30253 and st.getInt("cond") and st.getQuestItemsCount(WARRIOR_GUILD_MARK) :
      if st.getQuestItemsCount(RUSTED_BRONZE_SWORD1)<1 :
        htmltext = "30253-03.htm"
      elif st.getQuestItemsCount(RUSTED_BRONZE_SWORD1)<10 :
        htmltext = "30253-04.htm"
      elif st.getQuestItemsCount(RUSTED_BRONZE_SWORD1) >= 10 :
        st.takeItems(WARRIOR_GUILD_MARK,1)
        st.takeItems(RUSTED_BRONZE_SWORD1,st.getQuestItemsCount(RUSTED_BRONZE_SWORD1))
        st.giveItems(RUSTED_BRONZE_SWORD2,1)
        st.giveItems(SIMPLONS_LETTER,1)
        st.set("cond","4")
        htmltext = "30253-05.htm"
   elif npcId == 30253 and st.getInt("cond") and st.getQuestItemsCount(SIMPLONS_LETTER) :
        htmltext = "30253-06.htm"
   elif npcId == 30010 and st.getInt("cond") and st.getQuestItemsCount(SIMPLONS_LETTER) and st.getQuestItemsCount(RUSTED_BRONZE_SWORD2) and st.getQuestItemsCount(WARRIOR_GUILD_MARK)==0 and st.getQuestItemsCount(EINS_LETTER)==0 :
        htmltext = "30010-09.htm"
   elif npcId == 30010 and st.getInt("cond") and st.getQuestItemsCount(RUSTED_BRONZE_SWORD3) and st.getQuestItemsCount(WARRIOR_GUILD_MARK)==0 and st.getQuestItemsCount(EINS_LETTER)==0 :
        if st.getQuestItemsCount(POISON_SPIDER_LEG2)<20 :
          htmltext = "30010-12.htm"
        elif st.getQuestItemsCount(POISON_SPIDER_LEG2)>19 :
          st.takeItems(POISON_SPIDER_LEG2,st.getQuestItemsCount(POISON_SPIDER_LEG2))
          st.takeItems(RUSTED_BRONZE_SWORD3,1)
          st.giveItems(MEDALLION_OF_WARRIOR,1)
          htmltext = "30010-13.htm"
          st.set("cond","0")
          st.setState(COMPLETED)
          st.playSound("ItemSound.quest_finish")
   return htmltext

 def onKill(self,npc,player,isPet):
   st = player.getQuestState(qn)
   if not st : return 
   if st.getState() != STARTED : return 
   
   npcId = npc.getNpcId()
   if npcId in [20035,20042] :
        st.set("id","0")
        if st.getInt("cond") == 2 and st.getQuestItemsCount(RUSTED_BRONZE_SWORD1)<10 :
          if st.getRandom(10)<4 :
            st.giveItems(RUSTED_BRONZE_SWORD1,1)
            if st.getQuestItemsCount(RUSTED_BRONZE_SWORD1) == 10 :
              st.playSound("ItemSound.quest_middle")
              st.set("cond","3")
            else:
              st.playSound("ItemSound.quest_itemget")
   elif npcId in [20043,20038] :
      st.set("id","0")
      if st.getInt("cond") and st.getQuestItemsCount(POISON_SPIDER_LEG2)<20 and st.getQuestItemsCount(RUSTED_BRONZE_SWORD3) == 1 and st.getItemEquipped(7) == RUSTED_BRONZE_SWORD3:
        st.giveItems(POISON_SPIDER_LEG2,1)
        if st.getQuestItemsCount(POISON_SPIDER_LEG2) == 20 :
          st.playSound("ItemSound.quest_middle")
          st.set("cond","6")
        else:
          st.playSound("ItemSound.quest_itemget")

   return

QUEST       = Quest(401,qn,"Path To Warrior")
CREATED     = State('Start', QUEST)
STARTING     = State('Starting', QUEST)
STARTED     = State('Started', QUEST)
COMPLETED   = State('Completed', QUEST)


QUEST.setInitialState(CREATED)
QUEST.addStartNpc(30010)

QUEST.addTalkId(30010)

QUEST.addTalkId(30253)

QUEST.addKillId(20035)
QUEST.addKillId(20038)
QUEST.addKillId(20042)
QUEST.addKillId(20043)