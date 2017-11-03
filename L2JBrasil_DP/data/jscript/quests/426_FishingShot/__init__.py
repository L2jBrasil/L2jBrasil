# Made by Drov.
import sys
from com.it.br import Config
from com.it.br.gameserver.model.quest import State
from com.it.br.gameserver.model.quest import QuestState
from com.it.br.gameserver.model.quest.jython import QuestJython as JQuest

qn = "426_FishingShot"

SWEET_FLUID = 7586
MOBS1 = {
    20005:45,20013:100,20016:100,20017:115,20030:105,20132:70,20038:135,20044:125,20046:100,
    20047:100,20050:140,20058:140,20063:160,20066:170,20070:180,20074:195,20077:205,20078:205,
    20079:205,20080:220,20081:370,20083:245,20084:255,20085:265,20087:565,20088:605,20089:250,
    20100:85,20103:110,20105:110,20115:190,20120:20,20131:45,20135:360,20157:235,20162:195,
    20176:280,20211:170,20225:160,20227:180,20230:260,20232:245,20234:290,20241:700,20267:215,
    20268:295,20269:255,20270:365,20271:295,20286:700,20308:110,20312:45,20317:20,20324:85,
    20333:100,20341:100,20346:85,20349:850,20356:165,20357:140,20363:70,20368:85,20371:100,
    20386:85,20389:90,20403:110,20404:95,20433:100,20436:140,20448:45,20456:20,20463:85,20470:45,
    20471:85,20475:20,20478:110,20487:90,20511:100,20525:20,20528:100,20536:15,20537:15,20538:15,
    20539:15,20544:15,20550:300,20551:300,20552:650,20553:335,20554:390,20555:350,20557:390,
    20559:420,20560:440,20562:485,20573:545,20575:645,20630:350,20632:475,20634:960,20636:495,
    20638:540,20641:680,20643:660,20644:645,20659:440,20661:575,20663:525,20665:680,20667:730,
    20766:210,20781:270,20783:140,20784:155,20786:170,20788:325,20790:390,20792:620,20794:635,
    20796:640,20798:850,20800:740,20802:900,20804:775,20806:805,20833:455,20834:680,20836:785,
    20837:835,20839:430,20841:460,20845:605,20847:570,20849:585,20936:290,20937:315,20939:385,
    20940:500,20941:460,20943:345,20944:335,21100:125,21101:155,21103:215,21105:310,21107:600,
    21117:120,21023:170,21024:175,21025:185,21026:200,21034:195,21125:12,21263:650,21520:880,
    21526:970,21536:985,21602:555,21603:750,21605:620,21606:875,21611:590,21612:835,21617:615,
    21618:875,21635:775,21638:165,21639:185,21641:195,21644:170
}

MOBS2 = {
    20579:420,20639:280,20646:145,20648:120,20650:460,20651:260,20652:335,20657:630,20658:570,
    20808:50,20809:865,20832:700,20979:980,20991:665,20994:590,21261:170,21263:795,21508:100,
    21510:280,21511:995,21512:995,21514:185,21516:495,21517:495,21518:255,21636:950
}

MOBS3 = {
    20655:110,20656:150,20772:105,20810:50,20812:490,20814:775,20816:875,20819:280,20955:670,
    20978:555,21058:355,21060:45,21075:110,21078:610,21081:955,21264:920
}

MOBS4 = {
    20815:205,20822:100,20824:665,20825:620,20983:205,21314:145,21316:235,21318:280,21320:355,
    21322:430,21376:280,21378:375,21380:375,21387:640,21393:935,21395:855,21652:375,21655:640,
    21657:935
}

MOBS5 = {
    20828:935,21061:530,21069:825,21382:125,21384:400,21390:750,21654:400,21656:750
}

MOBSspecial = {
    20829:[115,6],20859:[890,8],21066:[5,5],21068:[565,11],21071:[400,12]
}

class Quest (JQuest) :

 def __init__(self,id,name,descr): JQuest.__init__(self,id,name,descr)

 def onEvent (self,event,st) :
    htmltext = event
    if event == "02.htm" :
      st.set("cond","1")
      st.setState(STARTED)
      st.playSound("ItemSound.quest_accept")
    elif event == "07.htm" :
      st.exitQuest(1)
      st.playSound("ItemSound.quest_finish")
    return htmltext

 def onTalk (self,npc,player):
   htmltext = "<html><body>You are either not carrying out your quest or don't meet the criteria.</body></html>"
   st = player.getQuestState(qn)
   if not st : return htmltext

   npcId = npc.getNpcId()
   cond=st.getInt("cond")
   if cond==0 :
     htmltext = "01.htm"
   elif st.getQuestItemsCount(SWEET_FLUID) :
     htmltext = "04.htm"
   else :
     htmltext = "03.htm"
   return htmltext

 def onKill(self,npc,player,isPet) :
   partyMember = self.getRandomPartyMemberState(player, STARTED)
   if not partyMember : return 
   st = partyMember.getQuestState(qn)
   npcId = npc.getNpcId()
   drop = 0
   chance = 0
   if npcId in MOBS1.keys() :
       chance = MOBS1[npcId]
   elif npcId in MOBS2.keys() :
       chance = MOBS2[npcId]
       drop = 1
   elif npcId in MOBS3.keys() :
       chance = MOBS3[npcId]
       drop = 2
   elif npcId in MOBS4.keys() :
       chance = MOBS4[npcId]
       drop = 3
   elif npcId in MOBS5.keys() :
       chance = MOBS5[npcId]
       drop = 4
   elif npcId in MOBSspecial.keys() :
       chance,drop = MOBSspecial[npcId]
   if st.getRandom(1000) <= chance :
       drop += 1
   if drop != 0 : 
       st.giveItems(SWEET_FLUID,drop*int(Config.RATE_DROP_QUEST))
       st.playSound("ItemSound.quest_itemget")  
   return

QUEST       = Quest(426,qn,"Quest for Fishing Shot")
CREATED     = State('Start', QUEST)
STARTED     = State('Started', QUEST)

QUEST.setInitialState(CREATED)

for npc in range(31562,31580)+[31616,31696,31697]:
    QUEST.addStartNpc(npc)
    QUEST.addTalkId(npc)

for mob in MOBS1.keys():
    QUEST.addKillId(mob)
for mob in MOBS2.keys():
    QUEST.addKillId(mob)
for mob in MOBS3.keys():
    QUEST.addKillId(mob)
for mob in MOBS4.keys():
    QUEST.addKillId(mob)
for mob in MOBS5.keys():
    QUEST.addKillId(mob)
for mob in MOBSspecial.keys():
    QUEST.addKillId(mob)