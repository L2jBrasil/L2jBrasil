#made by Kerb
import sys
from com.it.br.gameserver.model.quest import State
from com.it.br.gameserver.model.quest import QuestState
from com.it.br.gameserver.model.quest.jython import QuestJython as JQuest

qn = "660_AidingtheFloranVillage"

# NPC
MARIA = 30608
ALEX = 30291

# MOBS
CARSED_SEER = 21106
PLAIN_WATCMAN = 21102
ROUGH_HEWN_ROCK_GOLEM = 21103
DELU_LIZARDMAN_SHAMAN = 20781
DELU_LIZARDMAN_SAPPLIER = 21104
DELU_LIZARDMAN_COMMANDER = 21107
DELU_LIZARDMAN_SPESIAL_AGENT = 21105

#QUEST ITEMS
WATCHING_EYES = 8074
DELU_LIZARDMAN_SCALE =8076
ROUGHLY_HEWN_ROCK_GOLEM_SHARD = 8075

#REWARDS
ADENA = 57
SCROLL_ENCANT_ARMOR = 956
SCROLL_ENCHANT_WEAPON = 955

class Quest (JQuest) :

 def __init__(self,id,name,descr): JQuest.__init__(self,id,name,descr)

 def onEvent (self,event,st) :
    EYES=st.getQuestItemsCount(WATCHING_EYES)
    SCALE=st.getQuestItemsCount(DELU_LIZARDMAN_SCALE)
    SHARD=st.getQuestItemsCount(ROUGHLY_HEWN_ROCK_GOLEM_SHARD)
    htmltext = event
    if event =="30608-04.htm" :
      st.set("cond","1")
      st.setState(STARTED)
      st.playSound("ItemSound.quest_accept")
    if event == "30291-15.htm" :
      st.playSound("ItemSound.quest_middle")
    if event == "30291-05.htm" :
      if EYES+SCALE+SHARD >= 45 :
        st.giveItems(ADENA, EYES*100+SCALE*100+SHARD*100+9000)
        st.takeItems(WATCHING_EYES,-1)
        st.takeItems(DELU_LIZARDMAN_SCALE,-1)
        st.takeItems(ROUGHLY_HEWN_ROCK_GOLEM_SHARD,-1)
      else :
        st.giveItems(ADENA,EYES*100+SCALE*100+SHARD*100)
        st.takeItems(WATCHING_EYES,-1)
        st.takeItems(DELU_LIZARDMAN_SCALE,-1)
        st.takeItems(ROUGHLY_HEWN_ROCK_GOLEM_SHARD,-1)
      st.playSound("ItemSound.quest_finish")
    if event == "30291-11.htm" :
      if EYES+SCALE+SHARD >= 99 :
        n = 100 - EYES
        t = 100 - SCALE - EYES
        if EYES >= 100 :
          st.takeItems(WATCHING_EYES,100)
        else :
          st.takeItems(WATCHING_EYES,-1)
          if SCALE >= n :
            st.takeItems(DELU_LIZARDMAN_SCALE,n)
          else :
            st.takeItems(DELU_LIZARDMAN_SCALE,-1)
            st.takeItems(ROUGHLY_HEWN_ROCK_GOLEM_SHARD,t)
        if st.getRandom(10)<8 :
          st.giveItems(ADENA,13000)
          st.giveItems(SCROLL_ENCANT_ARMOR,1)
        else :
          st.giveItems(ADENA,1000)
        st.playSound("ItemSound.quest_finish")
      else :
        htmltext="30291-14.htm"
    if event == "30291-12.htm" :
      if EYES+SCALE+SHARD >= 199 :
        n = 200 - EYES
        t = 200 - SCALE - EYES
        luck = st.getRandom(15)
        if EYES >= 200 :
          st.takeItems(WATCHING_EYES,200)
        else :
          st.takeItems(WATCHING_EYES,-1)
          if SCALE >= n :
            st.takeItems(DELU_LIZARDMAN_SCALE,n)
          else :
            st.takeItems(DELU_LIZARDMAN_SCALE,-1)
            st.takeItems(ROUGHLY_HEWN_ROCK_GOLEM_SHARD,t)
        if luck in range (0,8) :
          st.giveItems(ADENA,20000)
          st.giveItems(SCROLL_ENCANT_ARMOR,1)
        if luck in range (8,12) :
          st.giveItems(SCROLL_ENCHANT_WEAPON,1)
        if luck in range (12,15) :
          st.giveItems(ADENA,2000)
        st.playSound("ItemSound.quest_finish")
      else :
        htmltext="30291-14.htm"
    if event == "30291-13.htm" :
      if EYES+SCALE+SHARD >= 499 :
        n = 500 - EYES
        t = 500 - SCALE - EYES
        if EYES >= 500 :
          st.takeItems(WATCHING_EYES,500)
        else :
          st.takeItems(WATCHING_EYES,-1)
          if SCALE >= n :
            st.takeItems(DELU_LIZARDMAN_SCALE,n)
          else :
            st.takeItems(DELU_LIZARDMAN_SCALE,-1)
            st.takeItems(ROUGHLY_HEWN_ROCK_GOLEM_SHARD,t)
        if st.getRandom(10)<8 :
          st.giveItems(ADENA,45000)
          st.giveItems(SCROLL_ENCHANT_WEAPON,1)
        else :
          st.giveItems(ADENA,5000)
        st.playSound("ItemSound.quest_finish")
      else :
        htmltext="30291-14.htm"
    elif event == "30291-06.htm" :
       st.set("cond","0")
       st.setState(COMPLETED)
       st.playSound("ItemSound.quest_finish")
    return htmltext

 def onTalk (self,npc,player):
   htmltext = "<html><body>You are either not carrying out your quest or don't meet the criteria.</body></html>"
   st = player.getQuestState(qn)
   if not st: return
   npcId = npc.getNpcId()
   SHARD=st.getQuestItemsCount(ROUGHLY_HEWN_ROCK_GOLEM_SHARD)
   SCALE=st.getQuestItemsCount(DELU_LIZARDMAN_SCALE)
   EYES=st.getQuestItemsCount(WATCHING_EYES)
   id = st.getState()
   cond = st.getInt("cond")
   if st.getState() == COMPLETED :
     st.setState(CREATED)
   if npcId == MARIA and cond == 0 :
     if st.getPlayer().getLevel() >= 30 :
       htmltext = "30608-02.htm"
     else :
       htmltext = "30608-01.htm"
       st.exitQuest(1)
   if npcId == MARIA and cond == 1 :
     htmltext = "30608-06.htm"
   if npcId == ALEX and cond == 1 :
     htmltext = "30291-01.htm"
     st.playSound("ItemSound.quest_middle")
     st.set("cond","2")
   if npcId == ALEX and cond == 2 :
     if EYES+SCALE+SHARD == 0 :
       htmltext = "30291-02.htm"
     else :
       htmltext = "30291-03.htm"  
   return htmltext

 def onKill(self,npc,player,isPet):
   st = player.getQuestState(qn)
   if not st: return
   npcId = npc.getNpcId()
   chance = st.getRandom(100)
   if st.getInt("cond") == 2 :
     if npcId in [21106,21102] and chance < 79 :
       st.giveItems(WATCHING_EYES,1)
       st.playSound("ItemSound.quest_itemget")
     elif npcId == ROUGH_HEWN_ROCK_GOLEM and chance < 75 :
       st.giveItems(ROUGHLY_HEWN_ROCK_GOLEM_SHARD,1)
       st.playSound("ItemSound.quest_itemget")
     elif npcId in [20781,21104,21107,21105] and chance < 67 :
       st.giveItems(DELU_LIZARDMAN_SCALE,1)
       st.playSound("ItemSound.quest_itemget")
   return

QUEST       = Quest(660,qn,"Aiding the Floran Village")
CREATED     = State('Start', QUEST)
STARTED     = State('Started', QUEST)
COMPLETED   = State('Completed', QUEST)

QUEST.setInitialState(CREATED)
QUEST.addStartNpc(MARIA)

QUEST.addTalkId(MARIA)
QUEST.addTalkId(ALEX)

QUEST.addKillId(CARSED_SEER)
QUEST.addKillId(PLAIN_WATCMAN)
QUEST.addKillId(ROUGH_HEWN_ROCK_GOLEM)
QUEST.addKillId(DELU_LIZARDMAN_SHAMAN)
QUEST.addKillId(DELU_LIZARDMAN_SAPPLIER)
QUEST.addKillId(DELU_LIZARDMAN_COMMANDER)
QUEST.addKillId(DELU_LIZARDMAN_SPESIAL_AGENT)