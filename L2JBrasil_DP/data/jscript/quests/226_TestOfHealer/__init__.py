# Made by Mr. Have fun! Version 0.2
# Shadow Weapon Coupons contributed by BiTi for the Official L2J Datapack Project
# Visit http://forum.l2jdp.com for more details
import sys
from com.it.br.gameserver.model.quest import State
from com.it.br.gameserver.model.quest import QuestState
from com.it.br.gameserver.model.quest.jython import QuestJython as JQuest

qn = "226_TestOfHealer"

REPORT_OF_PERRIN = 2810
CRISTINAS_LETTER = 2811
PICTURE_OF_WINDY = 2812
GOLDEN_STATUE = 2813
WINDYS_PEBBLES = 2814
ORDER_OF_SORIUS = 2815
SECRET_LETTER1 = 2816
SECRET_LETTER2 = 2817
SECRET_LETTER3 = 2818
SECRET_LETTER4 = 2819
MARK_OF_HEALER = 2820
ADENA = 57
SHADOW_WEAPON_COUPON_CGRADE = 8870

#this handels all dropdata, npcId:[condition,maxcount,item]
DROPLIST={
27134:[1,1,0],
27123:[6,1,SECRET_LETTER1],
27124:[9,1,SECRET_LETTER2],
27125:[11,1,SECRET_LETTER3],
27127:[13,1,SECRET_LETTER4]
}

def radar2(st):
  st.addRadar(-44358,79442,-3634)
  
def radar1(st):
  st.addRadar(-59985,79234,-3502)
  
def radar3(st):
  st.addRadar(-14158,44953,-3556)
  

class Quest (JQuest) :

 def __init__(self,id,name,descr): JQuest.__init__(self,id,name,descr)

 def onEvent (self,event,st) :
    htmltext = event
    if event == "1" :
      htmltext = "30473-04.htm"
      st.set("cond","1")
      st.setState(STARTED)
      st.playSound("ItemSound.quest_accept")
      st.giveItems(REPORT_OF_PERRIN,1)
    elif event == "30473_1" :
          htmltext = "30473-08.htm"
    elif event == "30473_2" :
          htmltext = "30473-09.htm"
          if st.getQuestItemsCount(GOLDEN_STATUE):
            st.addExpAndSp(134839,50000)
          else:
            st.addExpAndSp(118304,26250)
          st.giveItems(MARK_OF_HEALER,1)
          st.giveItems(SHADOW_WEAPON_COUPON_CGRADE,15)
          st.takeItems(GOLDEN_STATUE,1)
          st.set("cond","0")
          st.setState(COMPLETED)
          st.playSound("ItemSound.quest_finish")
          st.set("onlyone","1")
    elif event == "30428_1" :
          htmltext = "30428-02.htm"
          st.addSpawn(27134,-93254,147559,-2679)
    elif event == "30658_1" :
          if st.getQuestItemsCount(ADENA) >= 100000 :
            htmltext = "30658-02.htm"
            st.takeItems(ADENA,100000)
            st.giveItems(PICTURE_OF_WINDY,1)
          else:
            htmltext = "30658-05.htm"
    elif event == "30658_2" :
          htmltext = "30658-03.htm"
          st.set("cond","5")
    elif event == "30660_1" :
          htmltext = "30660-02.htm"
    elif event == "30660_2" :
          htmltext = "30660-03.htm"
          st.takeItems(PICTURE_OF_WINDY,1)
          st.giveItems(WINDYS_PEBBLES,1)
    elif event == "30674_1" :
          htmltext = "30674-02.htm"
          st.takeItems(ORDER_OF_SORIUS,1)
          st.addSpawn(27122)
          st.addSpawn(27122)
          st.addSpawn(27123)
          st.playSound("Itemsound.quest_before_battle")
    elif event == "30665_1" :
          htmltext = "30665-02.htm"
          st.takeItems(SECRET_LETTER1,1)
          st.takeItems(SECRET_LETTER2,1)
          st.takeItems(SECRET_LETTER3,1)
          st.takeItems(SECRET_LETTER4,1)
          st.giveItems(CRISTINAS_LETTER,1)
          st.set("cond","14")
    return htmltext


 def onTalk (self,npc,player):
   htmltext = "<html><body>You are either not carrying out your quest or don't meet the criteria.</body></html>"
   st = player.getQuestState(qn)
   if not st : return htmltext

   npcId = npc.getNpcId()
   id = st.getState()
   if npcId != 30473 and id != STARTED : return htmltext

   if id == CREATED :
     st.setState(STARTING)
     st.set("cond","0")
     st.set("onlyone","0")
     st.set("id","0")
   if npcId == 30473 :
     if st.getInt("cond")==0 and st.getInt("onlyone")==0 :
      if st.getInt("cond") < 15 :
        if (player.getClassId().getId() in [0x04, 0x0f, 0x1d, 0x13]) and player.getLevel() > 38 :
          htmltext = "30473-03.htm"
        elif player.getClassId().getId() in [0x04, 0x0f, 0x1d, 0x13] :
          htmltext = "30473-01.htm"
        else:
          htmltext = "30473-02.htm"
          st.exitQuest(1)
      else:
        htmltext = "30473-02.htm"
        st.exitQuest(1)
     elif st.getInt("cond")==0 and st.getInt("onlyone")==1 :
      htmltext = "<html><body>This quest has already been completed.</body></html>"
     elif npcId == 30473 and st.getInt("cond")<10 and st.getInt("cond")>0 :
      htmltext = "30473-05.htm"
     elif st.getInt("cond")==15 and st.getQuestItemsCount(GOLDEN_STATUE)==0 :
      htmltext = "30473-06.htm"
      st.addExpAndSp(32000,4100)
      st.giveItems(MARK_OF_HEALER,1)
      st.set("cond","0")
      st.setState(COMPLETED)
      st.playSound("ItemSound.quest_finish")
      st.set("onlyone","1")
     elif st.getInt("cond")==15 and st.getQuestItemsCount(GOLDEN_STATUE) :
      htmltext = "30473-07.htm"
   elif npcId == 30428:
     if st.getInt("cond")==1 and st.getQuestItemsCount(REPORT_OF_PERRIN) :
      htmltext = "30428-01.htm"
     elif npcId == 30428 and st.getInt("cond")==2 :
      htmltext = "30428-03.htm"
      st.set("cond","3")
      st.takeItems(REPORT_OF_PERRIN,1)
   elif npcId == 30428 and st.getInt("cond")==3 :
      htmltext = "30428-04.htm"
   elif npcId == 30659 and st.getInt("cond")==4 :
        n = st.getRandom(5)
        if n == 0:
          htmltext = "30659-01.htm"
        if n == 1:
          htmltext = "30659-02.htm"
        if n == 2:
          htmltext = "30659-03.htm"
        if n == 3:
          htmltext = "30659-04.htm"
        if n == 4:
          htmltext = "30659-05.htm"
   elif npcId == 30424:
     if st.getInt("cond")==3 :
      htmltext = "30424-01.htm"
      st.set("cond","4")
     elif npcId == 30424 and st.getInt("cond")==4 :
      htmltext = "30424-02.htm"
      st.set("cond","4")
   elif npcId == 30658 :
     if st.getInt("cond")==4 and st.getQuestItemsCount(PICTURE_OF_WINDY)==0 and st.getQuestItemsCount(WINDYS_PEBBLES)==0 and st.getQuestItemsCount(GOLDEN_STATUE)==0 :
      htmltext = "30658-01.htm"
     elif st.getInt("cond")==4 and st.getQuestItemsCount(PICTURE_OF_WINDY) :
      htmltext = "30658-04.htm"
     elif st.getInt("cond")==4 and st.getQuestItemsCount(WINDYS_PEBBLES) :
      htmltext = "30658-06.htm"
      st.giveItems(GOLDEN_STATUE,1)
      st.takeItems(WINDYS_PEBBLES,1)
      st.set("cond","5")
     elif st.getInt("cond")==5 :
      htmltext = "30658-07.htm"
   elif npcId == 30660:
     if st.getInt("cond")==4 and st.getQuestItemsCount(PICTURE_OF_WINDY) :
      htmltext = "30660-01.htm"
     elif st.getInt("cond")==4 and st.getQuestItemsCount(WINDYS_PEBBLES) :
      htmltext = "30660-04.htm"
   elif npcId == 30327 :
     if st.getInt("cond")==5 :
      htmltext = "30327-01.htm"
      st.giveItems(ORDER_OF_SORIUS,1)
      st.set("cond","6")
     elif st.getInt("cond")>5 and st.getInt("cond")<12 :
      htmltext = "30327-02.htm"
     elif st.getInt("cond")==14 :
      htmltext = "30327-03.htm"
      st.takeItems(CRISTINAS_LETTER,1)
      st.set("cond","15")
   elif npcId == 30674:
     if st.getInt("cond")==6 and st.getQuestItemsCount(ORDER_OF_SORIUS) :
      htmltext = "30674-01.htm"
      st.addSpawn(27122,-97547,106503,-3405)
      st.addSpawn(27122,-97526,106584,-3405)
      st.addSpawn(27123,-97441,106585,-3405)
     elif st.getInt("cond")==6 and st.getQuestItemsCount(SECRET_LETTER1) :
      htmltext = "30674-03.htm"
      st.set("cond","7")
   elif npcId == 30662 :
     if st.getInt("cond")==7 or st.getInt("cond")==8:
      htmltext = "30662-01.htm"
      st.set("cond","8")
      radar1(st)
     elif st.getInt("cond")==9 or st.getInt("cond")==10:
      htmltext = "30662-03.htm"
      st.set("cond","10")
      radar2(st)
     elif st.getInt("cond")==11 or st.getInt("cond")==12 :
      htmltext = "30662-02.htm"
      st.set("cond","12")
      radar3(st)
     elif st.getInt("cond")==13:
      htmltext = "30662-04.htm"
   elif npcId == 30663:
     if st.getInt("cond")==7 or st.getInt("cond")==8:
      htmltext = "30663-01.htm"
      st.set("cond","8")
      radar1(st)
     elif st.getInt("cond")==9 or st.getInt("cond")==10:
      htmltext = "30663-03.htm"
      st.set("cond","10")
      radar2(st)
     elif st.getInt("cond")==11 or st.getInt("cond")==12:
      htmltext = "30663-02.htm"
      st.set("cond","12")
      radar3(st)
     elif st.getInt("cond")==13:
      htmltext = "30663-04.htm"
   elif npcId == 30664:
     if st.getInt("cond")==7 or st.getInt("cond")==8:
      htmltext = "30664-01.htm"
      st.set("cond","8")
      radar1(st)
     elif st.getInt("cond")==9 or st.getInt("cond")==10:
      htmltext = "30664-03.htm"
      st.set("cond","10")
      radar2(st)
     elif st.getInt("cond")==11 or st.getInt("cond")==12:
      htmltext = "30664-02.htm"
      st.set("cond","12")
      radar3(st)
     elif st.getInt("cond")==13:
      htmltext = "30664-04.htm"
   elif npcId == 30661:
     if st.getInt("cond")==8 :
      htmltext = "30661-01.htm"
      st.addSpawn(27124,-59863,79210,-3521)
      st.addSpawn(27124,-59848,79300,-3526)
      st.addSpawn(27124,-59763,79263,-3525)
      st.playSound("Itemsound.quest_before_battle")
      st.set("cond","9")
     elif st.getInt("cond")==10:
      htmltext = "30661-02.htm"
      st.addSpawn(27125,-44399,79348,-3713)
      st.addSpawn(27125,-44458,79481,-3713)
      st.addSpawn(27125,-44533,79345,-3713)
      st.playSound("Itemsound.quest_before_battle")
      st.set("cond","11")
     elif st.getInt("cond")==12:
      htmltext = "30661-03.htm"
      st.addSpawn(27126,-14244,44948,-3593)
      st.addSpawn(27126,-14316,44844,-3593)
      st.addSpawn(27127,-14166,44779,-3593)
      st.playSound("Itemsound.quest_before_battle")
      st.set("cond","13")
     elif st.getInt("cond") == 13 :
      htmltext = "30661-04.htm"
   elif npcId == 30665 :
     if st.getInt("cond")==13 :
      htmltext = "30665-01.htm"
     else :
      htmltext = "30665-03.htm"
   return htmltext

 def onKill(self,npc,player,isPet):
   st = player.getQuestState(qn)
   if not st : return 
   if st.getState() != STARTED : return 
   
   npcId = npc.getNpcId()
   condition,maxcount,item=DROPLIST[npcId] 
   if st.getInt("cond")==condition and st.getQuestItemsCount(item)<maxcount:
     if item != 0:
       st.giveItems(item,1)
     if npcId == 27134:
       st.set("cond","2")
     st.playSound("Itemsound.quest_middle")
   return

QUEST       = Quest(226,qn,"Test Of Healer")
CREATED     = State('Start', QUEST)
STARTING     = State('Starting', QUEST)
STARTED     = State('Started', QUEST)
COMPLETED   = State('Completed', QUEST)


QUEST.setInitialState(CREATED)
QUEST.addStartNpc(30473)

QUEST.addTalkId(30473)

for npcId in [30327,30424,30428,30658,30659,30660,30661,30662,30663,30664,30665,30674]:
  QUEST.addTalkId(npcId)
 
for mobId in [20150,27123,27124,27125,27127,27134]:
  QUEST.addKillId(mobId)