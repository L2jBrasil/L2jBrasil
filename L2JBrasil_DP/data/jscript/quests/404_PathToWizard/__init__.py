# Maked by Mr. Have fun! Version 0.2
# Shadow Weapon Coupons contributed by BiTi for the Official L2J Datapack Project
# Visit http://forum.l2jdp.com for more details
import sys
from com.it.br.gameserver.model.quest import State
from com.it.br.gameserver.model.quest import QuestState
from com.it.br.gameserver.model.quest.jython import QuestJython as JQuest

qn = "404_PathToWizard"

MAP_OF_LUSTER = 1280
KEY_OF_FLAME = 1281
FLAME_EARING = 1282
BROKEN_BRONZE_MIRROR = 1283
WIND_FEATHER = 1284
WIND_BANGEL = 1285
RAMAS_DIARY = 1286
SPARKLE_PEBBLE = 1287
WATER_NECKLACE = 1288
RUST_GOLD_COIN = 1289
RED_SOIL = 1290
EARTH_RING = 1291
BEAD_OF_SEASON = 1292

class Quest (JQuest) :

 def __init__(self,id,name,descr): JQuest.__init__(self,id,name,descr)

 def onEvent (self,event,st) :
    htmltext = event
    player = st.getPlayer()
    if event == "1" :
      st.set("id","0")
      if player.getClassId().getId() == 0x0a :
        if player.getLevel() >= 19 :
          if st.getQuestItemsCount(BEAD_OF_SEASON) :
            htmltext = "30391-03.htm"
          else:
            htmltext = "30391-08.htm"
            st.set("cond","1")
            st.setState(STARTED)
            st.playSound("ItemSound.quest_accept")
        else:
            htmltext = "30391-02.htm"
      else:
        if player.getClassId().getId() == 0x0b :
          htmltext = "30391-02a.htm"
        else:
          htmltext = "30391-01.htm"
    elif event == "30410_1" :
          if st.getQuestItemsCount(WIND_FEATHER) == 0 :
            htmltext = "30410-03.htm"
            st.giveItems(WIND_FEATHER,1)
            st.set("cond","6")
    return htmltext


 def onTalk (self,npc,player):
   htmltext = "<html><body>You are either not carrying out your quest or don't meet the criteria.</body></html>"
   st = player.getQuestState(qn)
   if not st : return htmltext

   npcId = npc.getNpcId()
   id = st.getState()
   if npcId != 30391 and id != STARTED : return htmltext

   if id == CREATED :
     st.setState(STARTING)
     st.set("cond","0")
     st.set("onlyone","0")
     st.set("id","0")
   if npcId == 30391 and st.getInt("cond")==0 :
      #Talking to Parina before completing this quest
      if st.getInt("cond")<15 :
        htmltext = "30391-04.htm"
        return htmltext
      else:
        htmltext = "30391-04.htm"
   elif npcId == 30391 and st.getInt("cond")!=0 and (st.getQuestItemsCount(FLAME_EARING)==0 or st.getQuestItemsCount(WIND_BANGEL)==0 or st.getQuestItemsCount(WATER_NECKLACE)==0 or st.getQuestItemsCount(EARTH_RING)==0) :
      htmltext = "30391-05.htm"
   elif npcId == 30411 and st.getInt("cond")!=0 and st.getQuestItemsCount(MAP_OF_LUSTER)==0 and st.getQuestItemsCount(FLAME_EARING)==0 :
        #Taking to the Flame salamander for the first time
        #gains us the MAP_OF_LUSTER
        #and flags cond = 2
        if st.getQuestItemsCount(MAP_OF_LUSTER) == 0 :
          st.giveItems(MAP_OF_LUSTER,1)
        htmltext = "30411-01.htm"
        st.set("cond","2")
   elif npcId == 30411 and st.getInt("cond")!=0 and st.getQuestItemsCount(MAP_OF_LUSTER)!=0 and st.getQuestItemsCount(KEY_OF_FLAME)==0 :
        #Talking to the Flame Salamander more than once
        #without the KEY_OF_FLAME
        #But with the MAP_OF_LUSTER
        #results in the following text
        htmltext = "30411-02.htm"
   elif npcId == 30411 and st.getInt("cond")!=0 and st.getQuestItemsCount(MAP_OF_LUSTER)!=0 and st.getQuestItemsCount(KEY_OF_FLAME)!=0 :
        #Talking to the Flame Salamander when Cond != 0
        #while we have a KEY_OF_FLAME from the ratmen and the MAP_OF_LUSTER

        #Remove both Items and give a FLAME_EARING
        #Set the cond flag to 4 to signify we have completed the first part
        st.takeItems(KEY_OF_FLAME,st.getQuestItemsCount(KEY_OF_FLAME))
        st.takeItems(MAP_OF_LUSTER,st.getQuestItemsCount(MAP_OF_LUSTER))
        if st.getQuestItemsCount(FLAME_EARING) == 0 :
          st.giveItems(FLAME_EARING,1)
        htmltext = "30411-03.htm"
        st.set("cond","4")
   elif npcId == 30411 and st.getInt("cond")!=0 and st.getQuestItemsCount(FLAME_EARING)!=0 :
        #Talking to the Flame Salamander
        #after finishing the Fire component results
        #in the following text
        htmltext = "30411-04.htm"
   elif npcId == 30412 and st.getInt("cond")!=0 and st.getQuestItemsCount(FLAME_EARING)!=0 and st.getQuestItemsCount(BROKEN_BRONZE_MIRROR)==0 and st.getQuestItemsCount(WIND_BANGEL)==0 :
        #Talking to the Wind Sylph for the first time
        #With a FLAME_EARING (fire component complete)

        #Gives us a BROKEN_BRONZE_MIRROR
        #and sets cond = 5
        if st.getQuestItemsCount(BROKEN_BRONZE_MIRROR) == 0 :
          st.giveItems(BROKEN_BRONZE_MIRROR,1)
        htmltext = "30412-01.htm"
        st.set("cond","5")
   elif npcId == 30412 and st.getInt("cond")!=0 and st.getQuestItemsCount(BROKEN_BRONZE_MIRROR)!=0 and st.getQuestItemsCount(WIND_FEATHER)==0 :
        #Talking to the Wind Sylph for a second time
        #results in the following text
        htmltext = "30412-02.htm"
   elif npcId == 30412 and st.getInt("cond")!=0 and st.getQuestItemsCount(BROKEN_BRONZE_MIRROR)!=0 and st.getQuestItemsCount(WIND_FEATHER)!=0 :
        #Talking to the Wind Sylph with cond != 0
        #while having a BROKEN_BRONZE_MIRROR and a WIND_FEATHER

        #Removes both items
        #Gives a WIND_BANGEL
        #and sets cond = 7
        st.takeItems(WIND_FEATHER,st.getQuestItemsCount(WIND_FEATHER))
        st.takeItems(BROKEN_BRONZE_MIRROR,st.getQuestItemsCount(BROKEN_BRONZE_MIRROR))
        if st.getQuestItemsCount(WIND_BANGEL) == 0 :
          st.giveItems(WIND_BANGEL,1)
        htmltext = "30412-03.htm"
        st.set("cond","7")
   elif npcId == 30412 and st.getInt("cond")!=0 and st.getQuestItemsCount(WIND_BANGEL)!=0 :
        #Talking to the Wind Sylph after we get the WIND_BANGLE
        #results in the following text
        htmltext = "30412-04.htm"
   elif npcId == 30410 and st.getInt("cond")!=0 and st.getQuestItemsCount(BROKEN_BRONZE_MIRROR)!=0 and st.getQuestItemsCount(WIND_FEATHER)==0 :
        #Talking to the Lizardman of the Wastelands for the first time
        #begins this conversation
        htmltext = "30410-01.htm"
   elif npcId == 30410 and st.getInt("cond")!=0 and st.getQuestItemsCount(BROKEN_BRONZE_MIRROR)!=0 and st.getQuestItemsCount(WIND_FEATHER)!=0 :
        #Talking to the Lizardman of the Wastelands after obtaining
        #the WIND_FEATHER
        htmltext = "30410-04.htm"
   elif npcId == 30413 and st.getInt("cond")!=0 and st.getQuestItemsCount(WIND_BANGEL)!=0 and st.getQuestItemsCount(RAMAS_DIARY)==0 and st.getQuestItemsCount(WATER_NECKLACE)==0 :
        #Talking to the Water Undine for the first time
        #gives RAMAS_DIARY
        #and sets cond = 8
        if st.getQuestItemsCount(RAMAS_DIARY) == 0 :
          st.giveItems(RAMAS_DIARY,1)
        htmltext = "30413-01.htm"
        st.set("cond","8")
   elif npcId == 30413 and st.getInt("cond")!=0 and st.getQuestItemsCount(RAMAS_DIARY)!=0 and st.getQuestItemsCount(SPARKLE_PEBBLE)<2 :
        #Talking to the Water Undine for a second time
        #without 2 SPARKLE_PEBLE
        htmltext = "30413-02.htm"
   elif npcId == 30413 and st.getInt("cond")!=0 and st.getQuestItemsCount(RAMAS_DIARY)!=0 and st.getQuestItemsCount(SPARKLE_PEBBLE)>=2 :
        #Talking to the Water Undine with the 2 SPARKLE_PEBLE

        #removes both items
        #and gives WATER_NECKLACE
        #sets cond = 10
        st.takeItems(SPARKLE_PEBBLE,st.getQuestItemsCount(SPARKLE_PEBBLE))
        st.takeItems(RAMAS_DIARY,st.getQuestItemsCount(RAMAS_DIARY))
        if st.getQuestItemsCount(WATER_NECKLACE) == 0 :
          st.giveItems(WATER_NECKLACE,1)
        htmltext = "30413-03.htm"
        st.set("cond","10")
   elif npcId == 30413 and st.getInt("cond")!=0 and st.getQuestItemsCount(WATER_NECKLACE)!=0 :
        #Talking to the Water Undine after completing it's task
        htmltext = "30413-04.htm"
   elif npcId == 30409 and st.getInt("cond")!=0 and st.getQuestItemsCount(WATER_NECKLACE)!=0 and st.getQuestItemsCount(RUST_GOLD_COIN)==0 and st.getQuestItemsCount(EARTH_RING)==0 :
        #Talking to the Earth Snake for the first time
        if st.getQuestItemsCount(RUST_GOLD_COIN) == 0 :
          st.giveItems(RUST_GOLD_COIN,1)
        htmltext = "30409-01.htm"
        st.set("cond","11")
   elif npcId == 30409 and st.getInt("cond")!=0 and st.getQuestItemsCount(RUST_GOLD_COIN)!=0 and st.getQuestItemsCount(RED_SOIL)==0 :
        #Talking to the Earth Snake for a second time
        #without RED_SOIL
        htmltext = "30409-02.htm"
   elif npcId == 30409 and st.getInt("cond")!=0 and st.getQuestItemsCount(RUST_GOLD_COIN)!=0 and st.getQuestItemsCount(RED_SOIL)!=0 :
        #Talking to the Earth Snake afket collecting the RED_SOIL

        #Gives EARTH_RING
        #and sets cond = 13
        st.takeItems(RED_SOIL,st.getQuestItemsCount(RED_SOIL))
        st.takeItems(RUST_GOLD_COIN,st.getQuestItemsCount(RUST_GOLD_COIN))
        if st.getQuestItemsCount(EARTH_RING) == 0 :
          st.giveItems(EARTH_RING,1)
        htmltext = "30409-03.htm"
        st.set("cond","13")
   elif npcId == 30409 and st.getInt("cond")!=0 and st.getQuestItemsCount(EARTH_RING)!=0 :
        #Talking to the Earth Snake after completing his task
        htmltext = "30409-03.htm"
   elif npcId == 30391 and st.getInt("cond")!=0 and st.getQuestItemsCount(FLAME_EARING)!=0 and st.getQuestItemsCount(WIND_BANGEL)!=0 and st.getQuestItemsCount(WATER_NECKLACE)!=0 and st.getQuestItemsCount(EARTH_RING)!=0 :
        #Talking to Parina after gathering all 4 tokens
        #Gains BEAD_OF_SEASON
        #Resets cond so these NPC's will no longer speak to you
        #and Sets the quest as completed
        st.takeItems(FLAME_EARING,st.getQuestItemsCount(FLAME_EARING))
        st.takeItems(WIND_BANGEL,st.getQuestItemsCount(WIND_BANGEL))
        st.takeItems(WATER_NECKLACE,st.getQuestItemsCount(WATER_NECKLACE))
        st.takeItems(EARTH_RING,st.getQuestItemsCount(EARTH_RING))
        st.set("cond","0")
        st.setState(COMPLETED)
        st.playSound("ItemSound.quest_finish")
        if st.getQuestItemsCount(BEAD_OF_SEASON) == 0 :
          st.giveItems(BEAD_OF_SEASON,1)
        htmltext = "30391-06.htm"
        
   return htmltext

 def onKill(self,npc,player,isPet):
   st = player.getQuestState(qn)
   if not st : return 
   if st.getState() != STARTED : return 
   
   npcId = npc.getNpcId()
   if npcId == 20359 :    #Ratman Warrior, as of C3.
        st.set("id","0")
        #Only get a KEY_OF_FLAME if we are on the quest for the Fire Salamander
        if st.getInt("cond") == 2 :
            st.giveItems(KEY_OF_FLAME,1)
            st.playSound("ItemSound.quest_middle")
            #Increase the Cond so we can only get one key
            st.set("cond","3")
   elif npcId == 27030 : #water seer
        st.set("id","0")
        #Only get a SPARKLE_PEBBLE if we are on the quest for the Water Undine
        if st.getInt("cond") == 8 and st.getQuestItemsCount(SPARKLE_PEBBLE) < 2:
            st.giveItems(SPARKLE_PEBBLE,1)
            if st.getQuestItemsCount(SPARKLE_PEBBLE) == 2 :
              st.playSound("ItemSound.quest_middle")
              st.set("cond","9")
            else:
              st.playSound("ItemSound.quest_itemget")
   elif npcId == 20021 :   #Red Bear
        st.set("id","0")
        #Only get a RED_SOIL if we are on the quest for the Earth Snake
        if st.getInt("cond") == 11 :
            st.giveItems(RED_SOIL,1)
            st.playSound("ItemSound.quest_middle")
            st.set("cond","12")
   return

QUEST       = Quest(404,qn,"Path To Wizard")
CREATED     = State('Start', QUEST)
STARTING     = State('Starting', QUEST)
STARTED     = State('Started', QUEST)
COMPLETED   = State('Completed', QUEST)


QUEST.setInitialState(CREATED)
QUEST.addStartNpc(30391)

QUEST.addTalkId(30391)

QUEST.addTalkId(30409)
QUEST.addTalkId(30410)
QUEST.addTalkId(30411)
QUEST.addTalkId(30412)
QUEST.addTalkId(30413)

QUEST.addKillId(20021)
QUEST.addKillId(20359)
QUEST.addKillId(27030)