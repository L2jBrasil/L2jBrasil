# Made by Mr. Have fun! Version 0.2
# rewritten by Rolarga, Version 0.3
# Shadow Weapon Coupons contributed by BiTi for the Official L2J Datapack Project
# Visit http://forum.l2jdp.com for more details
import sys
from com.it.br.gameserver.model.quest import State
from com.it.br.gameserver.model.quest import QuestState
from com.it.br.gameserver.model.quest.jython import QuestJython as JQuest

qn = "224_TestOfSagittarius"

RECOMMENDATION_OF_BALANKI = 2864
RECOMMENDATION_OF_FILAUR = 2865
RECOMMENDATION_OF_ARIN = 2866
MARK_OF_MAESTRO = 2867
LETTER_OF_SOLDER_DETACHMENT = 2868
PAINT_OF_KAMURU = 2869
NECKLACE_OF_KAMURU = 2870
PAINT_OF_TELEPORT_DEVICE = 2871
TELEPORT_DEVICE = 2872
ARCHITECTURE_OF_KRUMA = 2873
REPORT_OF_KRUMA = 2874
INGREDIENTS_OF_ANTIDOTE = 2875
WEIRD_BEES_NEEDLE = 2876
MARSH_SPIDERS_WEB = 2877
BLOOD_OF_LEECH = 2878
BERNARDS_INTRODUCTION = 3294
LETTER_OF_HAMIL3 = 3297
HUNTERS_RUNE2 = 3299
MARK_OF_SAGITTARIUS = 3293
CRESCENT_MOON_BOW = 3028
TALISMAN_OF_KADESH = 3300
BLOOD_OF_LIZARDMAN = 3306
LETTER_OF_HAMIL1 = 3295
LETTER_OF_HAMIL2 = 3296
HUNTERS_RUNE1 = 3298
TALISMAN_OF_SNAKE = 3301
MITHRIL_CLIP = 3302
STAKATO_CHITIN = 3303
ST_BOWSTRING = 3304
MANASHENS_HORN = 3305
WOODEN_ARROW = 17
SHADOW_WEAPON_COUPON_CGRADE = 8870

#This adds all Info to a Mobs ->npcId:(step,dropcond,maxcount,chance,item)
HUNTERS = (3,1,10,50,HUNTERS_RUNE1)
LETO=(13,2,141,100,BLOOD_OF_LIZARDMAN)

DROPLIST={
20079:HUNTERS,
20080:HUNTERS,
20081:HUNTERS,
20082:HUNTERS,
20084:HUNTERS,
20086:HUNTERS,
20089:HUNTERS,
20090:HUNTERS,
20578:LETO,
20577:LETO,
20579:LETO,
20580:LETO,
20581:LETO,
20582:LETO,
20269:(6,1,10,50,HUNTERS_RUNE2),
20270:(6,1,10,60,HUNTERS_RUNE2),
27090:(13,3,1,100,TALISMAN_OF_KADESH),
20230:(10,4,1,10,STAKATO_CHITIN),
20232:(10,4,1,10,STAKATO_CHITIN),
20234:(10,4,1,10,STAKATO_CHITIN),
20563:(10,5,1,10,MANASHENS_HORN),
20233:(10,5,1,10,ST_BOWSTRING),
20551:(10,5,1,10,MITHRIL_CLIP)
}
def giveMiddle(st,itemid,step):
  st.giveItems(itemid,1)
  st.playSound("Itemsound.quest_middle")
  st.set("step",str(step+1))
  return

def giveNormal(st,itemid):
  st.giveItems(itemid,1)
  st.playSound("Itemsound.quest_itemget")
  return


class Quest (JQuest) :

 def __init__(self,id,name,descr): JQuest.__init__(self,id,name,descr)

 def onEvent (self,event,st) :
    htmltext = event
    if event == "1" :
       htmltext = "30702-04.htm"
       st.set("cond","1")
       st.set("step","1")
       st.setState(STARTED)
       st.playSound("ItemSound.quest_accept")
       st.giveItems(BERNARDS_INTRODUCTION,1)
    elif event == "30626_1" :
          htmltext = "30626-02.htm"
    elif event == "30626_2" :
          htmltext = "30626-03.htm"
          st.giveItems(LETTER_OF_HAMIL1,1)
          st.takeItems(BERNARDS_INTRODUCTION,1)
          st.set("step","2")
    elif event == "30626_3" :
          htmltext = "30626-06.htm"
    elif event == "30626_4" :
          htmltext = "30626-07.htm"
          st.giveItems(LETTER_OF_HAMIL2,1)
          st.takeItems(HUNTERS_RUNE1,10)
          st.set("step","5")
    elif event == "30653_1" :
          htmltext = "30653-02.htm"
          st.takeItems(LETTER_OF_HAMIL1,1)
          st.set("step","3")
    elif event == "30514_1" :
          htmltext = "30514-02.htm"
          st.takeItems(LETTER_OF_HAMIL2,1)
          st.set("step","6")
    return htmltext


 def onTalk (self,npc,player):
   htmltext = "<html><body>You are either not carrying out your quest or don't meet the criteria.</body></html>"
   st = player.getQuestState(qn)
   if not st : return htmltext

   npcId = npc.getNpcId()
   id = st.getState()
   if npcId != 30702 and id != STARTED : return htmltext

   step = st.getInt("step")
   onlyone = st.getInt("onlyone")
   if id == CREATED :
     st.set("cond","0")
     st.set("onlyone","0")
     st.set("step","0")
   if npcId == 30702 and step==0 and onlyone==0 :
        if player.getClassId().getId() in [0x07,0x16,0x23]:
          if player.getLevel() >= 39 :
             htmltext = "30702-03.htm"
          else:
             htmltext = "30702-01.htm"
             st.exitQuest(1)
        else:
          htmltext = "30702-02.htm"
          st.exitQuest(1)
   elif id == COMPLETED :
      htmltext = "<html><body>This quest has already been completed.</body></html>"
   elif npcId == 30702 and step==1 and st.getQuestItemsCount(BERNARDS_INTRODUCTION) :
      htmltext = "30702-05.htm"
   elif npcId == 30626 and step==1 and st.getQuestItemsCount(BERNARDS_INTRODUCTION) :
      htmltext = "30626-01.htm"
   elif npcId == 30626 and step==2 and st.getQuestItemsCount(LETTER_OF_HAMIL1) :
      htmltext = "30626-04.htm"
   elif npcId == 30626 and step==4 and st.getQuestItemsCount(HUNTERS_RUNE1)==10 :
      htmltext = "30626-05.htm"
   elif npcId == 30626 and step==5 and st.getQuestItemsCount(LETTER_OF_HAMIL2) :
      htmltext = "30626-08.htm"
   elif npcId == 30626 and step==8 :
      htmltext = "30626-09.htm"
      st.giveItems(LETTER_OF_HAMIL3,1)
      st.set("step","9")
   elif npcId == 30626 and step==9 and st.getQuestItemsCount(LETTER_OF_HAMIL3) :
      htmltext = "30626-10.htm"
   elif npcId == 30626 and step==12 and st.getQuestItemsCount(CRESCENT_MOON_BOW) :
      htmltext = "30626-11.htm"
      st.set("step","13")
   elif npcId == 30626 and step==13 :
      htmltext = "30626-12.htm"
   elif npcId == 30626 and step==14 :
      htmltext = "30626-13.htm"
      st.giveItems(MARK_OF_SAGITTARIUS,1)
      st.giveItems(SHADOW_WEAPON_COUPON_CGRADE,15)
      st.takeItems(CRESCENT_MOON_BOW,1)
      st.takeItems(TALISMAN_OF_KADESH,1)
      st.takeItems(BLOOD_OF_LIZARDMAN,st.getQuestItemsCount(BLOOD_OF_LIZARDMAN))
      st.addExpAndSp(54726,20250)
      st.unset("step")
      st.set("cond","0")
      st.setState(COMPLETED)
      st.playSound("ItemSound.quest_finish")
      st.set("onlyone","1")
   elif npcId == 30653 and step==2 and st.getQuestItemsCount(LETTER_OF_HAMIL1) :
      htmltext = "30653-01.htm"
   elif npcId == 30653 and step==3 :
      htmltext = "30653-03.htm"
   elif npcId == 30514 and step==5 and st.getQuestItemsCount(LETTER_OF_HAMIL2) :
      htmltext = "30514-01.htm"
   elif npcId == 30514 and step==6 :
      htmltext = "30514-03.htm"
   elif npcId == 30514 and step==7 and st.getQuestItemsCount(TALISMAN_OF_SNAKE) :
      htmltext = "30514-04.htm"
      st.takeItems(TALISMAN_OF_SNAKE,1)
      st.set("step","8")
   elif npcId == 30514 and step==8 :
      htmltext = "30514-05.htm"
   elif npcId == 30717 and step==9 and st.getQuestItemsCount(LETTER_OF_HAMIL3) :
      htmltext = "30717-01.htm"
      st.takeItems(LETTER_OF_HAMIL3,1)
      st.set("step","10")
   elif npcId == 30717 and step==10 :
      htmltext = "30717-03.htm"
   elif npcId == 30717 and step==12 :
      htmltext = "30717-04.htm"
   elif npcId == 30717 and step==11 and st.getQuestItemsCount(STAKATO_CHITIN) and st.getQuestItemsCount(MITHRIL_CLIP) and st.getQuestItemsCount(ST_BOWSTRING) and st.getQuestItemsCount(MANASHENS_HORN) :
      htmltext = "30717-02.htm"
      st.takeItems(MITHRIL_CLIP,1)
      st.takeItems(STAKATO_CHITIN,1)
      st.takeItems(ST_BOWSTRING,1)
      st.takeItems(MANASHENS_HORN,1)
      st.giveItems(CRESCENT_MOON_BOW,1)
      st.giveItems(WOODEN_ARROW,10)
      st.set("step","12")
   return htmltext

 def onKill(self,npc,player,isPet):
   st = player.getQuestState(qn)
   if not st : return 
   if st.getState() != STARTED : return 
   
   npcId = npc.getNpcId()
   step, dropcondition, maxcount, chance, itemid = DROPLIST[npcId]
   random = st.getRandom(100)
   
   if st.getInt("step") == step and st.getQuestItemsCount(itemid)<maxcount and random < chance:
    if dropcondition == 1:
     if st.getQuestItemsCount(itemid)== maxcount-1 : 
      giveMiddle(st,itemid,step)
      if npcId==20269 or npcId == 20270:
       st.giveItems(TALISMAN_OF_SNAKE,1)
       st.takeItems(HUNTERS_RUNE2,10)
     else:
      giveNormal(st,itemid)
    elif dropcondition == 2 :
     if ((st.getQuestItemsCount(itemid)-120)*5)> st.getRandom(100) :
      st.addSpawn(27090,npc,True,0)
      st.takeItems(itemid, st.getQuestItemsCount(itemid))
      st.playSound("Itemsound.quest_before_battle")
     else:
      giveNormal(st,itemid)
    elif dropcondition == 3 :
     if st.getItemEquipped(7)==CRESCENT_MOON_BOW:
      giveMiddle(st,itemid,step)
     else:
      st.addSpawn(27090,npc,True,0)
    elif dropcondition == 4 :
     if st.getQuestItemsCount(MITHRIL_CLIP) and st.getQuestItemsCount(ST_BOWSTRING) and st.getQuestItemsCount(MANASHENS_HORN) : 
      giveMiddle(st,itemid,step)
     else:
      giveNormal(st,itemid)
    elif dropcondition == 5:
     if st.getQuestItemsCount(MITHRIL_CLIP) and st.getQuestItemsCount(ST_BOWSTRING) and st.getQuestItemsCount(STAKATO_CHITIN) :
      giveMiddle(st,itemid,step)
     elif st.getQuestItemsCount(MITHRIL_CLIP) and st.getQuestItemsCount(MANASHENS_HORN) and st.getQuestItemsCount(STAKATO_CHITIN) :
      giveMiddle(st,itemid,step)
     elif st.getQuestItemsCount(ST_BOWSTRING) and st.getQuestItemsCount(MANASHENS_HORN) and st.getQuestItemsCount(STAKATO_CHITIN) :
      giveMiddle(st,itemid,step)
     else:
      giveNormal(st,itemid)
   return
   

  
QUEST       = Quest(224,qn,"Test Of Sagittarius")
CREATED     = State('Start', QUEST)
STARTED     = State('Started', QUEST)
COMPLETED   = State('Completed', QUEST)


QUEST.setInitialState(CREATED)
QUEST.addStartNpc(30702)

QUEST.addTalkId(30702)

for npcId in [30514,30626,30653,30717]:
    QUEST.addTalkId(npcId)

for mobId in [20230,20232,20233,20234,20269,20270,27090,20551,20563,20577,20578,20579,20580,20581,20582,20079,20080,20081,20082,20084,20086,20089,20090]:
    QUEST.addKillId(mobId)
