# Maked by Mr. Have fun! Version 0.2
# rewritten by Rolarga Version 0.3
# version 0.4 - fixed on 2005.11.08
# version 0.5 - updated by Kerberos on 2007.11.10
# Shadow Weapon Coupons contributed by BiTi for the Official L2J Datapack Project
# Visit http://forum.l2jdp.com for more details

import sys
from com.it.br.gameserver.model.quest        import State
from com.it.br.gameserver.model.quest        import QuestState
from com.it.br.gameserver.model.quest.jython import QuestJython as JQuest

qn = "223_TestOfChampion"

MARK_OF_CHAMPION     = 3276
ASCALONS_LETTER1     = 3277
MASONS_LETTER        = 3278
IRON_ROSE_RING       = 3279
ASCALONS_LETTER2     = 3280
WHITE_ROSE_INSIGNIA  = 3281
GROOTS_LETTER        = 3282
ASCALONS_LETTER3     = 3283
MOUENS_ORDER1        = 3284
MOUENS_ORDER2        = 3285
MOUENS_LETTER        = 3286
HARPYS_EGG1          = 3287
MEDUSA_VENOM1        = 3288
WINDSUS_BILE1        = 3289
BLOODY_AXE_HEAD      = 3290
ROAD_RATMAN_HEAD     = 3291
LETO_LIZARDMAN_FANG1 = 3292
SHADOW_WEAPON_COUPON_CGRADE = 8870

DROPLIST ={
20780:(2,100,100,BLOODY_AXE_HEAD),
20145:(6,30,50,HARPYS_EGG1),
27088:(6,30,50,HARPYS_EGG1),
20158:(6,30,50,MEDUSA_VENOM1),
20553:(6,30,50,WINDSUS_BILE1),
20551:(10,100,100,ROAD_RATMAN_HEAD),
27089:(10,100,100,ROAD_RATMAN_HEAD),
20577:(12,100,50,LETO_LIZARDMAN_FANG1),  
20578:(12,100,60,LETO_LIZARDMAN_FANG1),   
20579:(12,100,70,LETO_LIZARDMAN_FANG1),   
20580:(12,100,80,LETO_LIZARDMAN_FANG1),   
20581:(12,100,90,LETO_LIZARDMAN_FANG1),   
20582:(12,100,95,LETO_LIZARDMAN_FANG1)
}


class Quest (JQuest) :

 def __init__(self,id,name,descr): JQuest.__init__(self,id,name,descr)

 def onEvent (self,event,st) :
    htmltext = event
    if event == "1" :
      htmltext = "30624-06.htm"
      st.set("cond","1")
      st.setState(STARTED)
      st.playSound("ItemSound.quest_accept")
      st.giveItems(ASCALONS_LETTER1,1)
    elif event == "30624_1" :
          htmltext = "30624-05.htm"
    elif event == "30624_2" :
          htmltext = "30624-10.htm"
          st.giveItems(ASCALONS_LETTER2,1)
          st.takeItems(MASONS_LETTER,1)
          st.playSound("Itemsound.quest_middle")
          st.set("cond","5")
    elif event == "30624_3" :
          htmltext = "30624-14.htm"
          st.giveItems(ASCALONS_LETTER3,1)
          st.takeItems(GROOTS_LETTER,1)
          st.playSound("Itemsound.quest_middle")
          st.set("cond","9")
    elif event == "30625_1" :
          htmltext = "30625-02.htm"
    elif event == "30625_2" :
          htmltext = "30625-03.htm"
          st.giveItems(IRON_ROSE_RING,1)
          st.takeItems(ASCALONS_LETTER1,1)
          st.playSound("Itemsound.quest_middle")
          st.set("cond","2")
    elif event == "30093_1" :
          htmltext = "30093-02.htm"
          st.giveItems(WHITE_ROSE_INSIGNIA,1)
          st.takeItems(ASCALONS_LETTER2,1)
          st.playSound("Itemsound.quest_middle")
          st.set("cond","6")
    elif event == "30196_1" :
          htmltext = "30196-02.htm"
    elif event == "30196_2" :
          htmltext = "30196-03.htm"
          st.giveItems(MOUENS_ORDER1,1)
          st.takeItems(ASCALONS_LETTER3,1)
          st.playSound("Itemsound.quest_middle")
          st.set("cond","10")
    elif event == "30196_3" :
          htmltext = "30196-06.htm"
          st.giveItems(MOUENS_ORDER2,1)
          st.takeItems(MOUENS_ORDER1,1)
          st.takeItems(ROAD_RATMAN_HEAD,-1)
          st.playSound("Itemsound.quest_middle")
          st.set("cond","12")
    return htmltext


 def onTalk (self,npc,player):
   htmltext = "<html><body>You are either not carrying out your quest or don't meet the criteria.</body></html>"
   st = player.getQuestState(qn)
   if not st : return htmltext
   npcId = npc.getNpcId()
   id = st.getState()
   if npcId != 30624 and id != STARTED : return htmltext
   if id == COMPLETED :
      htmltext = "<html><body>This quest has already been completed.</body></html>"
   elif id == CREATED :
     st.set("cond","0")
   if npcId == 30624 and st.getInt("cond") == 0 :
        if player.getClassId().getId() in [0x01, 0x2d] and player.getLevel() > 38 :
          if player.getClassId().getId() == 0x01 :
            htmltext = "30624-03.htm"
          else:
            htmltext = "30624-04.htm"
        elif player.getClassId().getId() in [0x01, 0x2d] :
          htmltext = "30624-02.htm"
        else:
          htmltext = "30624-01.htm"
          st.exitQuest(1)
   elif npcId == 30624 and st.getInt("cond") == 1 :
      htmltext = "30624-07.htm"
   elif npcId == 30624 and st.getInt("cond") == 4  and st.getQuestItemsCount(MASONS_LETTER) :
      htmltext = "30624-09.htm"
   elif npcId == 30624 and st.getInt("cond") == 5  and st.getQuestItemsCount(ASCALONS_LETTER2) :
      htmltext = "30624-11.htm"
   elif npcId == 30624 and st.getInt("cond") == 2 :
      htmltext = "30624-08.htm"
   elif npcId == 30624 and st.getInt("cond") == 6 and st.getQuestItemsCount(WHITE_ROSE_INSIGNIA) :
      htmltext = "30624-12.htm"
   elif npcId == 30624 and st.getInt("cond") == 8 and st.getQuestItemsCount(GROOTS_LETTER) :
      htmltext = "30624-13.htm"
   elif npcId == 30624 and st.getInt("cond") == 9 and st.getQuestItemsCount(ASCALONS_LETTER3) :
      htmltext = "30624-15.htm"
   elif npcId == 30624 and st.getInt("cond") == 14 and st.getQuestItemsCount(MOUENS_LETTER) :
      st.addExpAndSp(117454,25000)
      htmltext = "30624-17.htm"
      st.giveItems(MARK_OF_CHAMPION,1)
      st.giveItems(SHADOW_WEAPON_COUPON_CGRADE,15)
      st.takeItems(MOUENS_LETTER,1)
      st.set("cond","0")
      st.setState(COMPLETED)
      st.playSound("ItemSound.quest_finish")
   elif npcId == 30624 and st.getInt("step") in [10,11,12,13] :
      htmltext = "30624-16.htm"
   elif npcId == 30625 and st.getInt("cond") == 1 and st.getQuestItemsCount(ASCALONS_LETTER1) :
      htmltext = "30625-01.htm"
   elif npcId == 30625 and st.getInt("cond") == 2 :
      htmltext = "30625-04.htm"
   elif npcId == 30625 and st.getInt("cond") == 3 :
      htmltext = "30625-05.htm"
      st.giveItems(MASONS_LETTER,1)
      st.takeItems(IRON_ROSE_RING,1)
      st.takeItems(BLOODY_AXE_HEAD,-1)
      st.playSound("Itemsound.quest_middle")
      st.set("cond","4")
   elif npcId == 30625 and st.getInt("cond") == 4 :
      htmltext = "30625-06.htm"
   elif npcId == 30625 and st.getInt("cond") >= 5 and (st.getQuestItemsCount(ASCALONS_LETTER2) or st.getQuestItemsCount(WHITE_ROSE_INSIGNIA) or st.getQuestItemsCount(GROOTS_LETTER) or st.getQuestItemsCount(ASCALONS_LETTER3) or st.getQuestItemsCount(MOUENS_ORDER1) or st.getQuestItemsCount(MOUENS_ORDER2) or st.getQuestItemsCount(MOUENS_LETTER)) :
      htmltext = "30625-07.htm"
   elif npcId == 30093 and st.getInt("cond") == 5 and st.getQuestItemsCount(ASCALONS_LETTER2) :
      htmltext = "30093-01.htm"
   elif npcId == 30093 and st.getInt("cond") == 6 :
      htmltext = "30093-03.htm"
   elif npcId == 30093 and st.getInt("cond") == 7 :
      htmltext = "30093-04.htm"
      st.playSound("Itemsound.quest_middle")
      st.set("cond","8")
      st.giveItems(GROOTS_LETTER,1)
      st.takeItems(WHITE_ROSE_INSIGNIA,1)
      st.takeItems(HARPYS_EGG1,st.getQuestItemsCount(HARPYS_EGG1))
      st.takeItems(MEDUSA_VENOM1,st.getQuestItemsCount(MEDUSA_VENOM1))
      st.takeItems(WINDSUS_BILE1,st.getQuestItemsCount(WINDSUS_BILE1))
   elif npcId == 30093 and st.getInt("cond") == 8 :
      htmltext = "30093-05.htm"
   elif npcId == 30093 and st.getInt("step") >= 9 and (st.getQuestItemsCount(ASCALONS_LETTER3) or st.getQuestItemsCount(MOUENS_ORDER1) or st.getQuestItemsCount(MOUENS_ORDER2) or st.getQuestItemsCount(MOUENS_LETTER)) :
      htmltext = "30093-06.htm"
   elif npcId == 30196 and st.getInt("cond") == 9 :
      htmltext = "30196-01.htm"
   elif npcId == 30196 and st.getInt("cond") == 10 :
      htmltext = "30196-04.htm"
   elif npcId == 30196 and st.getInt("cond") == 11 :
      htmltext = "30196-05.htm"
   elif npcId == 30196 and st.getInt("cond") == 12 :
      htmltext = "30196-07.htm"
   elif npcId == 30196 and st.getInt("cond") == 13 :
      htmltext = "30196-08.htm"
      st.giveItems(MOUENS_LETTER,1)
      st.takeItems(MOUENS_ORDER2,1)
      st.takeItems(LETO_LIZARDMAN_FANG1,st.getQuestItemsCount(LETO_LIZARDMAN_FANG1))
      st.playSound("Itemsound.quest_middle")
      st.set("cond","14")
   elif npcId == 30196 and st.getInt("cond") == 14 :
      htmltext = "30196-09.htm"
   return htmltext


 def onKill(self,npc,player,isPet):
   st = player.getQuestState(qn)
   if not st : return 
   if st.getState() != STARTED : return 
   cond = st.getInt("cond")
   npcId = npc.getNpcId()
   step, maxcount, chance, itemid = DROPLIST[npcId]
   if cond == step and st.getQuestItemsCount(itemid) < maxcount and st.getRandom(100) < chance:
     if st.getQuestItemsCount(itemid) == (maxcount-1):
       st.giveItems(itemid,1)
       st.playSound("Itemsound.quest_middle")
       if cond == 6:
          h_egg = st.getQuestItemsCount(HARPYS_EGG1)
          m_ven = st.getQuestItemsCount(MEDUSA_VENOM1)
          w_bil = st.getQuestItemsCount(WINDSUS_BILE1)
          if h_egg ==30 and m_ven == 30 and w_bil == 30:
             st.set("cond",str(cond+1))
       else:
          st.set("cond",str(cond+1))
     else:
       st.giveItems(itemid,1)
       st.playSound("Itemsound.quest_itemget")
   return

QUEST     = Quest(223,qn,"Test Of Champion")
CREATED   = State('Start',     QUEST)
STARTING  = State('Starting',  QUEST)
STARTED   = State('Started',   QUEST)
COMPLETED = State('Completed', QUEST)


QUEST.setInitialState(CREATED)
QUEST.addStartNpc(30624)

QUEST.addTalkId(30624)

for npcId in [30093,30196,30625]:
    QUEST.addTalkId(npcId)

for mobId in [20145,20158,27088,27089,20551,20553,20577,20578,20579,20580,20581,20582,20780]:
    QUEST.addKillId(mobId)
