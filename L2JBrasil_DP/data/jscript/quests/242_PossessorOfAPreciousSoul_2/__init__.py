# Made by disKret
import sys
from com.it.br.gameserver.model.quest import State
from com.it.br.gameserver.model.quest import QuestState
from com.it.br.gameserver.model.quest.jython import QuestJython as JQuest

qn = "242_PossessorOfAPreciousSoul_2"

#NPC
VIRGIL = 31742
KASSANDRA = 31743
OGMAR = 31744
FALLEN_UNICORN = 31746
PURE_UNICORN = 31747
CORNERSTONE = 31748
MYSTERIOUS_KNIGHT = 31751
ANGEL_CORPSE = 31752
KALIS = 30759
MATILD = 30738

#QUEST ITEM
VIRGILS_LETTER = 7677
GOLDEN_HAIR = 7590
ORB_oF_BINDING = 7595
SORCERY_INGREDIENT = 7596
CARADINE_LETTER = 7678

#CHANCE FOR HAIR DROP
CHANCE_FOR_HAIR = 20

#MOB
RESTRAINER_OF_GLORY = 27317

class Quest (JQuest) :

 def __init__(self,id,name,descr): JQuest.__init__(self,id,name,descr)

 def onEvent (self,event,st) :
   htmltext = event
   cond = st.getInt("cond")
   if event == "31742-3.htm" :
     if cond == 0 :
       st.setState(STARTED)
       st.takeItems(VIRGILS_LETTER,1)
       st.set("cond","1")
       st.playSound("ItemSound.quest_accept")
   if event == "31743-2.htm" :
     return htmltext
   if event == "31743-3.htm" :
     return htmltext
   if event == "31743-4.htm" :
     return htmltext
   if event == "31743-5.htm" :
     if cond == 1 :
       st.set("cond","2")
       st.setState(STARTED)
       st.playSound("ItemSound.quest_accept")
   if event == "31744-2.htm" :
     if cond == 2 :
       st.set("cond","3")
       st.playSound("ItemSound.quest_middle")
   if event == "31751-2.htm" :
     if cond == 3 :
       st.set("cond","4")
       st.playSound("ItemSound.quest_middle")
   if event == "30759-2.htm" :
     if cond == 6 :
       st.set("cond","7")
       st.playSound("ItemSound.quest_middle")
   if event == "30738-2.htm" :
     if cond == 7 :
       st.set("cond","8")
       st.giveItems(SORCERY_INGREDIENT,1)
       st.playSound("ItemSound.quest_middle")
   if event == "30759-5.htm" :
     if cond == 8 :
       st.set("cond","9")
       st.set("awaitsDrops","1")
       st.takeItems(GOLDEN_HAIR,1)
       st.takeItems(SORCERY_INGREDIENT,1)
       st.playSound("ItemSound.quest_middle")
   return htmltext

 def onTalk (self,npc,player):
   htmltext = "<html><body>You are either not carrying out your quest or don't meet the criteria.</body></html>"
   st = player.getQuestState(qn)
   if not st : return htmltext

   npcId = npc.getNpcId()
   id = st.getState()
   if npcId != VIRGIL and id != STARTED : return htmltext

   chance = st.getRandom(100)
   cornerstones = st.getInt("cornerstones")
   if id == CREATED :
     st.set("cond","0")
     st.set("cornerstones","0")
   cond = st.getInt("cond")
   if player.isSubClassActive() :
     if npcId == VIRGIL and cond == 0 and st.getQuestItemsCount(VIRGILS_LETTER) == 1 :
       if id == COMPLETED :
         htmltext = "<html><body>This quest has already been completed.</body></html>"
       elif player.getLevel() < 60 : 
         htmltext = "31742-2.htm"
         st.exitQuest(1)
       elif player.getLevel() >= 60 :
         htmltext = "31742-1.htm"
     if npcId == VIRGIL and cond == 1 :
       htmltext = "31742-4.htm"
     if npcId == KASSANDRA and cond == 1 :
       htmltext = "31743-1.htm"
     if npcId == KASSANDRA and cond == 2 :
       htmltext = "31743-6.htm"
     if npcId == OGMAR and cond == 2 :
       htmltext = "31744-1.htm"
     if npcId == OGMAR and cond == 3 :
       htmltext = "31744-3.htm"
     if npcId == MYSTERIOUS_KNIGHT and cond == 3 :
       htmltext = "31751-1.htm"
     if npcId == MYSTERIOUS_KNIGHT and cond == 4 :
       htmltext = "31751-3.htm"
     if npcId == ANGEL_CORPSE and cond == 4 :
       npc.doDie(npc) 
       if CHANCE_FOR_HAIR < chance :
         htmltext = "31752-2.htm"
       else :
         st.set("cond","5")
         st.giveItems(GOLDEN_HAIR,1)
         st.playSound("ItemSound.quest_middle")
         htmltext = "31752-1.htm"
     if npcId == ANGEL_CORPSE and cond == 5 :
       htmltext = "31752-2.htm"
     if npcId == MYSTERIOUS_KNIGHT and cond == 5 and st.getQuestItemsCount(GOLDEN_HAIR) == 1 :
       htmltext = "31751-4.htm"
       st.set("cond","6")
       st.playSound("ItemSound.quest_middle")
     if npcId == MYSTERIOUS_KNIGHT and cond == 6 :
       htmltext = "31751-5.htm"
     if npcId == KALIS and cond == 6 :
       htmltext = "30759-1.htm"
     if npcId == KALIS and cond == 7 :
       htmltext = "30759-3.htm"
     if npcId == MATILD and cond == 7 :
       htmltext = "30738-1.htm"
     if npcId == MATILD and cond == 8 :
       htmltext = "30738-3.htm"
     if npcId == KALIS and cond == 8 and st.getQuestItemsCount(SORCERY_INGREDIENT) == 1 :
       htmltext = "30759-4.htm"
     if npcId == KALIS and cond == 9 :
       htmltext = "30759-6.htm"
     if npcId == FALLEN_UNICORN and cond == 9 :
       htmltext = "31746-1.htm"
     if npcId == CORNERSTONE and cond == 9 and st.getQuestItemsCount(ORB_oF_BINDING) == 0 :
       htmltext = "31748-1.htm"
     if npcId == CORNERSTONE and cond == 9 and st.getQuestItemsCount(ORB_oF_BINDING) >= 1 :
       htmltext = "31748-2.htm"
       st.takeItems(ORB_oF_BINDING,1)
       npc.doDie(npc)
       st.set("cornerstones",str(cornerstones+1))
       st.playSound("ItemSound.quest_middle")
       if cornerstones == 3 :
         st.set("cond","10")
         st.playSound("ItemSound.quest_middle")
     if npcId == FALLEN_UNICORN and cond == 10 :
       htmltext = "31746-2.htm"
       npc.doDie(npc)
       st.addSpawn(PURE_UNICORN,85884,-76588,-3470,59000,True)
     if npcId == PURE_UNICORN and cond == 10 :
       st.set("cond","11")
       st.playSound("ItemSound.quest_middle")
       htmltext = "31747-1.htm"
     if npcId == PURE_UNICORN and cond == 11 :
       htmltext = "31747-2.htm"
     if npcId == KASSANDRA and cond == 11 :
       htmltext = "31743-7.htm"
     if npcId == VIRGIL and cond == 11 :
       htmltext = "31742-6.htm"
       st.set("cond","0")
       st.set("cornerstones","0")
       st.giveItems(CARADINE_LETTER,1)
       st.playSound("ItemSound.quest_finish")
       st.setState(COMPLETED)
   return htmltext

 def onKill(self,npc,player,isPet):
    # get a random party member that awaits for drops from this quest 
    partyMember = self.getRandomPartyMember(player,"awaitsDrops","1")
    if not partyMember : return
    st = partyMember.getQuestState(qn)
    if st.getInt("cond") == 9 and st.getQuestItemsCount(ORB_oF_BINDING) <= 4 :
      st.giveItems(ORB_oF_BINDING,1)
      st.playSound("ItemSound.quest_itemget")
      if st.getQuestItemsCount(ORB_oF_BINDING) == 5 :
          st.unset("awaitsDrops")
    return 

QUEST       = Quest(242,qn,"Possessor Of A Precious Soul - 2")
CREATED     = State('Start', QUEST)
STARTED     = State('Started', QUEST)
COMPLETED   = State('Completed', QUEST)

QUEST.setInitialState(CREATED)
QUEST.addStartNpc(VIRGIL)
QUEST.addTalkId(VIRGIL)

QUEST.addTalkId(KASSANDRA)
QUEST.addTalkId(OGMAR)
QUEST.addTalkId(MYSTERIOUS_KNIGHT)
QUEST.addTalkId(ANGEL_CORPSE)
QUEST.addTalkId(KALIS)
QUEST.addTalkId(MATILD)
QUEST.addTalkId(FALLEN_UNICORN)
QUEST.addTalkId(CORNERSTONE)
QUEST.addTalkId(PURE_UNICORN)

QUEST.addKillId(RESTRAINER_OF_GLORY)