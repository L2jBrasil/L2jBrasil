# Made by Mr. - Version 0.3 by DrLecter 
# Shadow Weapon Coupons contributed by BiTi for the Official L2J Datapack Project
# Visit http://forum.l2jdp.com for more details
import sys
from com.it.br.gameserver.model.quest import State
from com.it.br.gameserver.model.quest import QuestState
from com.it.br.gameserver.model.quest.jython import QuestJython as JQuest

qn = "231_TestOfMaestro"

#item definition
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
BROKEN_TELEPORT_DEVICE = 2916

#This handle all Mob-Drop Data.  npcId:[progress,maxcount,item]
DROPLIST={
20225:[13,10,BLOOD_OF_LEECH],
20229:[13,10,WEIRD_BEES_NEEDLE],
20233:[13,10,MARSH_SPIDERS_WEB],
27133:[4,1,NECKLACE_OF_KAMURU]
}

#if you have all three recommendation, it sets final progress
def recommendationCount(st):
  count=0
  for item in [RECOMMENDATION_OF_ARIN,RECOMMENDATION_OF_FILAUR,RECOMMENDATION_OF_BALANKI]:
    count+=st.getQuestItemsCount(item)
  if count == 3:
    st.set("progress","17")
    st.set("cond","2")

class Quest (JQuest) :

 def __init__(self,id,name,descr): JQuest.__init__(self,id,name,descr)

 def onEvent (self,event,st) :
    htmltext = event
    progress = st.getInt("progress")
    id=st.getState()
    if id != COMPLETED :
       if event == "1" and progress == 0 :
          htmltext = "30531-04.htm"
          st.setState(STARTED)
          st.playSound("ItemSound.quest_accept")
          st.set("cond","1")
          st.set("progress","1")
       elif event == "30533_1" and progress in [1,11,16]:
          htmltext = "30533-02.htm"
          st.set("progress","2")
       elif event == "30671_1" and progress == 2:
          htmltext = "30671-02.htm"
          st.giveItems(PAINT_OF_KAMURU,1)
          st.set("progress","3")
       elif event == "30556_1" :
          htmltext = "30556-02.htm"
       elif event == "30556_2" :
          htmltext = "30556-03.htm"
       elif event == "30556_3" and progress == 8 :
          htmltext = "30556-05.htm"
          st.takeItems(PAINT_OF_TELEPORT_DEVICE,1)
          st.getPlayer().teleToLocation(140352,-194133,-2028);
          st.giveItems(BROKEN_TELEPORT_DEVICE,1)
          st.set("progress","9")
       elif event == "30556_4" :
          htmltext = "30556-04.htm"
       elif event == "30673_1" and progress == 14 :
          htmltext = "30673-04.htm"
          st.giveItems(REPORT_OF_KRUMA,1)
          st.takeItems(WEIRD_BEES_NEEDLE,-1)
          st.takeItems(MARSH_SPIDERS_WEB,-1)
          st.takeItems(BLOOD_OF_LEECH,-1)
          st.takeItems(INGREDIENTS_OF_ANTIDOTE,-1)
          st.set("progress","15")
    return htmltext


 def onTalk (self,npc,player):
   htmltext = "<html><body>You are either not carrying out your quest or don't meet the criteria.</body></html>"
   st = player.getQuestState(qn)
   if not st : return htmltext

   npcId = npc.getNpcId()
   id = st.getState()
   if npcId != 30531 and id != STARTED : return htmltext

   if id == CREATED :
     st.setState(STARTING)
   progress = st.getInt("progress")
   if npcId == 30531:
     if id == COMPLETED :
       htmltext = "<html><body>This quest has already been completed.</body></html>"
     elif progress==0 :
        if player.getClassId().getId() == 0x38 and player.getLevel() > 38 :
          htmltext = "30531-03.htm"
        elif player.getClassId().getId() == 0x38 :
          htmltext = "30531-01.htm"
          st.exitQuest(1)
        else:
          htmltext = "30531-02.htm"
          st.exitQuest(1)
     elif progress>0 and progress<17 :
       htmltext = "30531-05.htm"
     elif progress==17 :
       st.addExpAndSp(154499,37500)
       htmltext = "30531-06.htm"
       st.giveItems(MARK_OF_MAESTRO,1)
       st.takeItems(RECOMMENDATION_OF_BALANKI,1)
       st.takeItems(RECOMMENDATION_OF_FILAUR,1)
       st.takeItems(RECOMMENDATION_OF_ARIN,1)
       st.unset("progress")
       st.setState(COMPLETED)
       st.playSound("ItemSound.quest_finish")
   elif npcId == 30533:
     if progress in [1,11,16] and not st.getQuestItemsCount(RECOMMENDATION_OF_BALANKI):
       htmltext = "30533-01.htm"
     elif progress==2:
       htmltext = "30533-03.htm"
     elif progress==6 :
       htmltext = "30533-04.htm"
       st.giveItems(RECOMMENDATION_OF_BALANKI,1)
       st.takeItems(LETTER_OF_SOLDER_DETACHMENT,1)
       st.set("progress","7")
       recommendationCount(st)
     elif progress in [7,17] :
       htmltext = "30533-05.htm"
   elif npcId == 30671:
     if progress==2 :
       htmltext = "30671-01.htm"
     elif progress==3:
       htmltext = "30671-03.htm"
     elif progress==5 :
       htmltext = "30671-04.htm"
       st.giveItems(LETTER_OF_SOLDER_DETACHMENT,1)
       st.takeItems(NECKLACE_OF_KAMURU,1)
       st.takeItems(PAINT_OF_KAMURU,1)
       st.set("progress","6")
     elif progress==6 :
       htmltext = "30671-05.htm"
   elif npcId == 30672 and progress==3 :
       htmltext = "30672-01.htm"
   elif npcId == 30675 and progress==3:
       st.set("progress","4")
       htmltext="30675-01.htm"
   elif npcId == 30536:
     if progress in [1,7,16] and not st.getQuestItemsCount(RECOMMENDATION_OF_ARIN) :
       htmltext = "30536-01.htm"
       st.giveItems(PAINT_OF_TELEPORT_DEVICE,1)
       st.set("progress","8")
     elif progress==8 :
       htmltext = "30536-02.htm"
     elif progress==10:
       htmltext = "30536-03.htm"
       st.giveItems(RECOMMENDATION_OF_ARIN,1)
       st.takeItems(TELEPORT_DEVICE,5)
       st.set("progress","11")
       recommendationCount(st)
     elif progress in [11,17]:
       htmltext = "30536-04.htm"
   elif npcId==30556:
     if progress==8:
       htmltext = "30556-01.htm"
     elif progress==9:
       htmltext = "30556-06.htm"
       st.giveItems(TELEPORT_DEVICE,5)
       st.takeItems(BROKEN_TELEPORT_DEVICE,1)
       st.set("progress","10")
     elif progress==10 :
       htmltext = "30556-07.htm"
   elif npcId==30535:  
     if progress in [1,7,11] and not st.getQuestItemsCount(RECOMMENDATION_OF_FILAUR) :
       htmltext = "30535-01.htm"
       st.giveItems(ARCHITECTURE_OF_KRUMA,1)
       st.set("progress","12")
     elif progress==12 :
       htmltext = "30535-02.htm"
     elif progress==15 :
       htmltext = "30535-03.htm"
       st.giveItems(RECOMMENDATION_OF_FILAUR,1)
       st.takeItems(REPORT_OF_KRUMA,1)
       st.set("progress","16")
       recommendationCount(st)
     elif progress>15:
       htmltext = "30535-04.htm"
   elif npcId == 30673:
     if progress==12 :
       htmltext = "30673-01.htm"
       st.giveItems(INGREDIENTS_OF_ANTIDOTE,1)
       st.takeItems(ARCHITECTURE_OF_KRUMA,1)
       st.set("progress","13")
     elif progress==13 :
       htmltext = "30673-02.htm"
     elif progress==14 :
       htmltext = "30673-03.htm"
     elif progress==15:
       htmltext = "30673-05.htm"
   elif npcId==30532 and progress :
      htmltext = "30532-01.htm"
   return htmltext

 def onKill(self,npc,player,isPet):
   st = player.getQuestState(qn)
   if not st : return 
   if st.getState() != STARTED : return 
   
   npcId = npc.getNpcId()
   progress_drop,maxcount,item=DROPLIST[npcId]
   count=st.getQuestItemsCount(item)
   progress = st.getInt("progress")
   if progress == progress_drop and count < maxcount :
        st.giveItems(item,1)
        if count == maxcount-1 :
          st.playSound("Itemsound.quest_middle")
          itemcount=0
          for id in [WEIRD_BEES_NEEDLE,MARSH_SPIDERS_WEB,BLOOD_OF_LEECH]:
           itemcount+=st.getQuestItemsCount(id)
          if npcId==27133 or itemcount>29:          
            st.set("progress",str(progress+1))
        else:
          st.playSound("Itemsound.quest_itemget")
   return

QUEST       = Quest(231,qn,"Test Of Maestro")
CREATED     = State('Start', QUEST)
STARTING    = State('Starting', QUEST)
STARTED     = State('Started', QUEST)
COMPLETED   = State('Completed', QUEST)


QUEST.setInitialState(CREATED)
QUEST.addStartNpc(30531)

QUEST.addTalkId(30531)

for npcId in [30532,30533,30535,30536,30556,30671,30672,30673,30675]:
  QUEST.addTalkId(npcId)

for mobId in [20225,20229,20233,27133]:
  QUEST.addKillId(mobId)
