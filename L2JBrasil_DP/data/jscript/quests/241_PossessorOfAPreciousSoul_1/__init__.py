# Made by disKret
import sys
from com.it.br.gameserver.model.quest import State
from com.it.br.gameserver.model.quest import QuestState
from com.it.br.gameserver.model.quest.jython import QuestJython as JQuest

qn = "241_PossessorOfAPreciousSoul_1"

#NPC
STEDMIEL = 30692
GABRIELLE = 30753
GILMORE = 30754
KANTABILON = 31042
NOEL = 31272
RAHORAKTI = 31336
TALIEN = 31739
CARADINE = 31740
VIRGIL = 31742
KASSANDRA = 31743
OGMAR = 31744

#QUEST ITEM
LEGEND_OF_SEVENTEEN = 7587
MALRUK_SUCCUBUS_CLAW = 7597
ECHO_CRYSTAL = 7589
POETRY_BOOK = 7588
CRIMSON_MOSS = 7598
RAHORAKTIS_MEDICINE = 7599
LUNARGENT = 6029
HELLFIRE_OIL = 6033
VIRGILS_LETTER = 7677

#CHANCE
#
CRIMSON_MOSS_CHANCE = 5
MALRUK_SUCCUBUS_CLAW_CHANCE = 10

#MOB
BARAHAM = 27113

class Quest (JQuest) :

 def __init__(self,id,name,descr): JQuest.__init__(self,id,name,descr)

 def onEvent (self,event,st) :
   htmltext = event
   cond = st.getInt("cond")
   if event == "31739-4.htm" :
     if cond == 0 and st.getPlayer().isSubClassActive() :
       st.setState(STARTED)
       st.set("cond","1")
       st.playSound("ItemSound.quest_accept")
   if event == "30753-2.htm" :
     if cond == 1 and st.getPlayer().isSubClassActive() :
       st.set("cond","2")
       st.playSound("ItemSound.quest_middle")
   if event == "30754-2.htm" :
     if cond == 2 and st.getPlayer().isSubClassActive() :
       st.set("cond","3")
       st.playSound("ItemSound.quest_middle")
   if event == "31739-8.htm" :
     if cond == 4 and st.getPlayer().isSubClassActive() :
       st.set("cond","5")
       st.takeItems(LEGEND_OF_SEVENTEEN,1)
       st.playSound("ItemSound.quest_middle")
   if event == "31042-2.htm" :
     if cond == 5 and st.getPlayer().isSubClassActive() :
       st.set("cond","6")
       st.playSound("ItemSound.quest_middle")
   if event == "31042-5.htm" :
     if cond == 7 and st.getPlayer().isSubClassActive() :
       st.set("cond","8")
       st.takeItems(MALRUK_SUCCUBUS_CLAW,10)
       st.giveItems(ECHO_CRYSTAL,1)
       st.playSound("ItemSound.quest_middle")
   if event == "31739-12.htm" :
     if cond == 8 and st.getPlayer().isSubClassActive() :
       st.set("cond","9")
       st.takeItems(ECHO_CRYSTAL,1)
       st.playSound("ItemSound.quest_accept")
   if event == "30692-2.htm" :
     if cond == 9 and st.getPlayer().isSubClassActive() :
       st.set("cond","10")
       st.giveItems(POETRY_BOOK,1)
       st.playSound("ItemSound.quest_accept")
   if event == "31739-15.htm" :
     if cond == 10 and st.getPlayer().isSubClassActive() :
       st.set("cond","11")
       st.takeItems(POETRY_BOOK,1)
       st.playSound("ItemSound.quest_accept")
   if event == "31742-2.htm" :
     if cond == 11 and st.getPlayer().isSubClassActive() :
       st.set("cond","12")
       st.playSound("ItemSound.quest_accept")
   if event == "31744-2.htm" :
     if cond == 12 and st.getPlayer().isSubClassActive() :
       st.set("cond","13")
       st.playSound("ItemSound.quest_accept")
   if event == "31336-2.htm" :
     if cond == 13 and st.getPlayer().isSubClassActive() :
       st.set("cond","14")
       st.playSound("ItemSound.quest_accept")
   if event == "31336-5.htm" :
     if cond == 15 and st.getPlayer().isSubClassActive() :
       st.set("cond","16")
       st.takeItems(CRIMSON_MOSS,5)
       st.giveItems(RAHORAKTIS_MEDICINE,1)
       st.playSound("ItemSound.quest_accept")
   if event == "31743-2.htm" :
     if cond == 16 and st.getPlayer().isSubClassActive() :
       st.set("cond","17")
       st.takeItems(RAHORAKTIS_MEDICINE,1)
       st.playSound("ItemSound.quest_accept")
   if event == "31742-5.htm" :
     if cond == 17 and st.getPlayer().isSubClassActive() :
       st.set("cond","18")
       st.playSound("ItemSound.quest_accept")
   if event == "31740-2.htm" :
     if cond == 18 and st.getPlayer().isSubClassActive() :
       st.set("cond","19")
       st.playSound("ItemSound.quest_accept")
   if event == "31272-2.htm" :
     if cond == 19 and st.getPlayer().isSubClassActive() :
       st.set("cond","20")
       st.playSound("ItemSound.quest_accept")
   if event == "31272-5.htm" :
     if cond == 20 and st.getPlayer().isSubClassActive() :
       st.takeItems(LUNARGENT,5)
       st.takeItems(HELLFIRE_OIL,1)
       st.set("cond","21")
       st.playSound("ItemSound.quest_accept")
   if event == "31740-5.htm" :
     if cond == 21 and st.getPlayer().isSubClassActive() :
       st.giveItems(VIRGILS_LETTER,1)
       st.addExpAndSp(263043,0)
       st.set("cond","0")
       st.playSound("ItemSound.quest_finish")
       st.setState(COMPLETED)
   return htmltext

 def onTalk (self,npc,player):
   htmltext = "<html><body>You are either not carrying out your quest or don't meet the criteria.</body></html>"
   st = player.getQuestState(qn)
   if not st : return htmltext
   npcId = npc.getNpcId()
   id = st.getState()
   if npcId != TALIEN and id != STARTED : return htmltext
   cond = st.getInt("cond")
   id = st.getState()
   if player.isSubClassActive() :
     if npcId == TALIEN :
       if cond == 0 :
         if id == COMPLETED :
           htmltext = "<html><body>This quest has already been completed.</body></html>"
         elif player.getLevel() < 50 : 
           htmltext = "31739-2.htm"
           st.exitQuest(1)
         elif player.getLevel() >= 50 :
           htmltext = "31739-1.htm"
       elif cond == 1 :
         htmltext = "31739-5.htm"
       elif cond == 4 and st.getQuestItemsCount(LEGEND_OF_SEVENTEEN) == 1 :
         htmltext = "31739-6.htm"
       elif cond == 5 :
         htmltext = "31739-9.htm"
       elif cond == 8 and st.getQuestItemsCount(ECHO_CRYSTAL) == 1 :
         htmltext = "31739-11.htm"
       elif cond == 9 :
         htmltext = "31739-13.htm"
       elif cond == 10 and st.getQuestItemsCount(POETRY_BOOK) == 1 :
         htmltext = "31739-14.htm"
       elif cond == 11 :
         htmltext = "31739-16.htm"
     elif npcId == GABRIELLE :
       if cond == 1 :
         htmltext = "30753-1.htm"
       elif cond == 2 :
         htmltext = "30753-3.htm"
     elif npcId == GILMORE :
       if cond == 2 :
         htmltext = "30754-1.htm"
       elif cond == 3 :
         htmltext = "30754-3.htm"
     elif npcId == KANTABILON :
       if cond == 5 :
         htmltext = "31042-1.htm"
       elif cond == 6 :
         htmltext = "31042-4.htm"
       elif cond == 7 and st.getQuestItemsCount(MALRUK_SUCCUBUS_CLAW) == 10 :
         htmltext = "31042-3.htm"
       elif cond == 8 :
         htmltext = "31042-6.htm"
     elif npcId == STEDMIEL :
       if cond == 9 :
         htmltext = "30692-1.htm"
       elif cond == 10 :
         htmltext = "30692-3.htm"
     elif npcId == VIRGIL :
       if cond == 11 :
         htmltext = "31742-1.htm"
       elif cond == 12 :
         htmltext = "31742-3.htm"
       elif cond == 17 :
         htmltext = "31742-4.htm"
       elif cond == 18 :
         htmltext = "31742-6.htm"
     elif npcId == OGMAR :
       if cond == 12 :
         htmltext = "31744-1.htm"
       elif cond == 13 :
         htmltext = "31744-3.htm"
     elif npcId == RAHORAKTI :
       if cond == 13 :
         htmltext = "31336-1.htm"
       elif cond == 14 :
         htmltext = "31336-4.htm"
       elif cond == 15 and st.getQuestItemsCount(CRIMSON_MOSS) == 5 :
         htmltext = "31336-3.htm"
       elif cond == 16 :
         htmltext = "31336-6.htm"
     elif npcId == KASSANDRA :
       if cond == 16 and st.getQuestItemsCount(RAHORAKTIS_MEDICINE) == 1 :
         htmltext = "31743-1.htm"
       elif cond == 17 :
         htmltext = "31743-3.htm"
     elif npcId == CARADINE :
       if cond == 18 :
         htmltext = "31740-1.htm"
       elif cond == 19 :
         htmltext = "31740-3.htm"
       elif cond == 21 :
         htmltext = "31740-4.htm"
     elif npcId == NOEL :
       if cond == 19 :
         htmltext = "31272-1.htm"
       elif cond == 20 and st.getQuestItemsCount(LUNARGENT) < 5 and not st.getQuestItemsCount(HELLFIRE_OIL) :
         htmltext = "31272-4.htm"
       elif cond == 20 and st.getQuestItemsCount(LUNARGENT) >= 5 and st.getQuestItemsCount(HELLFIRE_OIL) :
         htmltext = "31272-3.htm"
       elif cond == 21 :
         htmltext = "31272-7.htm"
   else :
     htmltext = "31739-2.htm"
     st.exitQuest(1)
   return htmltext

 def onKill(self,npc,player,isPet):
   npcId = npc.getNpcId()
   if npcId == BARAHAM:
     # get a random party member who is doing this quest and is at cond == 3  
     partyMember = self.getRandomPartyMember(player, "3")
     if partyMember :
         st = partyMember.getQuestState(qn)
         st.set("cond","4")
         st.giveItems(LEGEND_OF_SEVENTEEN,1)
         st.playSound("ItemSound.quest_itemget")
   elif npcId in [20244,20245,20283,20284] :
     # get a random party member who is doing this quest and is at cond == 6  
     partyMember = self.getRandomPartyMember(player, "6")
     if partyMember :
         st = partyMember.getQuestState(qn)
         chance = st.getRandom(100)
         if MALRUK_SUCCUBUS_CLAW_CHANCE >= chance and st.getQuestItemsCount(MALRUK_SUCCUBUS_CLAW) < 10 :
           st.giveItems(MALRUK_SUCCUBUS_CLAW,1)
           st.playSound("ItemSound.quest_itemget")
           if st.getQuestItemsCount(MALRUK_SUCCUBUS_CLAW) == 10 :
             st.set("cond","7")
             st.playSound("ItemSound.quest_middle")
   elif npcId in range(21508,21513) :
     # get a random party member who is doing this quest and is at cond == 14  
     partyMember = self.getRandomPartyMember(player, "14")
     if partyMember :
         st = partyMember.getQuestState(qn)
         chance = st.getRandom(100)
         if CRIMSON_MOSS_CHANCE >= chance and st.getQuestItemsCount(CRIMSON_MOSS) < 5 :
           st.giveItems(CRIMSON_MOSS,1)
           st.playSound("ItemSound.quest_itemget")
           if st.getQuestItemsCount(CRIMSON_MOSS) == 5 :
             st.set("cond","15")
             st.playSound("ItemSound.quest_middle")
   return

QUEST       = Quest(241,qn,"Possessor Of A Precious Soul - 1")
CREATED     = State('Start', QUEST)
STARTED     = State('Started', QUEST)
COMPLETED   = State('Completed', QUEST)

QUEST.setInitialState(CREATED)
QUEST.addStartNpc(TALIEN)
QUEST.addTalkId(TALIEN)

QUEST.addTalkId(STEDMIEL)
QUEST.addTalkId(GABRIELLE)
QUEST.addTalkId(GILMORE)
QUEST.addTalkId(KANTABILON)
QUEST.addTalkId(NOEL)
QUEST.addTalkId(RAHORAKTI)
QUEST.addTalkId(CARADINE)
QUEST.addTalkId(VIRGIL)
QUEST.addTalkId(KASSANDRA)
QUEST.addTalkId(OGMAR)

QUEST.addKillId(BARAHAM)
QUEST.addKillId(20244)
QUEST.addKillId(20245)
QUEST.addKillId(20283)
QUEST.addKillId(21508)

QUEST.addKillId(21509)
QUEST.addKillId(21510)
QUEST.addKillId(21511)
QUEST.addKillId(21512)