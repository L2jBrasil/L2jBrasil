# completely rewritten by Rolarga, original from Mr
# modified by Ariakas 08.12.2005
# Version 0.4 by DrLecter
# Shadow Weapon Coupons contributed by BiTi for the Official L2J Datapack Project
# Version 0.6  - updated by Kerberos on 2007.11.15
# Visit http://forum.l2jdp.com for more details
import sys
from com.it.br.gameserver.model.quest import State
from com.it.br.gameserver.model.quest import QuestState
from com.it.br.gameserver.model.quest.jython import QuestJython as JQuest

qn = "225_TestOfSearcher"

LUTHERS_LETTER,     ALANKELLS_WARRANT,LEIRYNNS_ORDER1,DELU_TOTEM,  \
LEIRYNNS_ORDER2,    CHIEF_KALKIS_FANG,LEIRYNNS_REPORT,STRANGE_MAP, \
LAMBERTS_MAP,       ALANKELLS_LETTER, ALANKELLS_ORDER,WINE_CATALOG,\
TWEETYS_CONTRACT,   RED_SPORE_DUST,   MALRUKIAN_WINE, OLD_ORDER,   \
REXS_DIARY,         TORN_MAP_PIECE1,  TORN_MAP_PIECE2,SOLTS_MAP,   \
MAKELS_MAP,         COMBINED_MAP,     RUSTED_KEY1,    GOLD_BAR,    \
ALANKELLS_RECOMMEND,MARK_OF_SEARCHER = range(2784,2810)

#Shadow Weapon Exchange Coupon
SHADOW_WEAPON_COUPON_CGRADE = 8870

#This handle all mob drops   npcId:[condition,maxcount,chance,itemid]
DROPLIST={
20781:[3,10,100,DELU_TOTEM],
27094:[3,10,100,DELU_TOTEM],
27093:[5,1,100,CHIEF_KALKIS_FANG],
20555:[10,10,100,RED_SPORE_DUST],
20551:[14,4,50,TORN_MAP_PIECE1],
20144:[14,4,50,TORN_MAP_PIECE2]
}

NPC=[30291,30420,30628,30690,30728,30729,30730,30627]

MOB=DROPLIST.keys()

class Quest (JQuest) :

 def __init__(self,id,name,descr): JQuest.__init__(self,id,name,descr)

 def onEvent (self,event,st) :
    htmltext = event
    if event == "30690-05.htm" :
        st.set("cond","1")
        st.setState(STARTED)
        st.playSound("ItemSound.quest_accept")
        st.giveItems(LUTHERS_LETTER,1)
    elif event == "30291-07.htm" :
        st.giveItems(ALANKELLS_LETTER,1)
        st.giveItems(ALANKELLS_ORDER,1)
        st.giveItems(LAMBERTS_MAP,1)
        st.takeItems(STRANGE_MAP,1)
        st.takeItems(LEIRYNNS_REPORT,1)
        st.set("cond","8")
        st.playSound("ItemSound.quest_middle")
    elif event == "30420-01a.htm" :
        st.takeItems(WINE_CATALOG,1)
        st.giveItems(TWEETYS_CONTRACT,1)
        st.set("cond","10")
        st.playSound("ItemSound.quest_middle")
    elif event == "30730-01d.htm" :
        st.giveItems(REXS_DIARY,1)
        st.takeItems(OLD_ORDER,1)
        st.set("cond","14")
        st.playSound("ItemSound.quest_middle")
    elif event == "30627-01a.htm" :
        st.giveItems(RUSTED_KEY1,1)
#        st.addSpawn(30628,10011,157449,-2374,300000)
        st.addSpawn(30628,10098,157287,-2406,300000)
        st.set("cond","17")
        st.playSound("ItemSound.quest_middle")
    elif event == "30628-01a.htm" :
        st.giveItems(GOLD_BAR,20)
        st.takeItems(RUSTED_KEY1,1)
        st.set("cond","18")
        st.playSound("ItemSound.quest_middle")
    return htmltext

 def onTalk (self,npc,player):
   htmltext = "<html><body>You are either not carrying out your quest or don't meet the criteria.</body></html>"
   st = player.getQuestState(qn)
   if not st : return htmltext
   cond = st.getInt("cond")
   npcId = npc.getNpcId()
   id = st.getState()
   if npcId != 30690 and id != STARTED : return htmltext

   if id == COMPLETED :
      htmltext = "<html><body>This quest has already been completed.</body></html>"
   elif id == CREATED :
     st.set("cond","0")
     if npcId == NPC[3]:
          if player.getClassId().getId() in [ 0x07, 0x16, 0x23, 0x36] :
           if player.getLevel() > 38 :
            if player.getClassId().getId() == 0x36 :
              htmltext = "30690-04.htm"
            else:
              htmltext = "30690-03.htm"
           else:
             htmltext = "30690-02.htm"
             st.exitQuest(1)
          else:
           htmltext = "30690-01.htm"
           st.exitQuest(1)
   else:
     if npcId== NPC[3]:
       if cond==1 :
         htmltext = "30690-06.htm"
       elif cond>1 and cond<19 :
         htmltext = "30690-07.htm"
       elif cond==19 :
         st.addExpAndSp(447444,30704)
         st.rewardItems(57,80903)
         st.giveItems(7562,82)
         htmltext = "30690-08.htm"
         st.set("cond","0")
         st.setState(COMPLETED)
         st.playSound("ItemSound.quest_finish")
         st.takeItems(ALANKELLS_RECOMMEND,1)
         st.rewardItems(57,80903)
         st.giveItems(MARK_OF_SEARCHER,1)
         st.giveItems(SHADOW_WEAPON_COUPON_CGRADE,15)
         st.addExpAndSp(447444,30704)
     elif npcId == NPC[0] :
      if cond==1 :
        htmltext = "30291-01.htm"
        st.takeItems(LUTHERS_LETTER,1)
        st.giveItems(ALANKELLS_WARRANT,1)
        st.set("cond","2")
        st.playSound("ItemSound.quest_middle")
      elif cond == 2:
        htmltext = "30291-02.htm"
      elif cond>2 and cond<7 :
        htmltext = "30291-03.htm"
      elif cond==7 :
        htmltext = "30291-04.htm"
      elif cond==8 :
        htmltext = "30291-08.htm"
      elif cond==13 or cond==14 :
        htmltext = "30291-09.htm"
      elif cond==16 :
        htmltext = "30291-10.htm"
      elif cond==18 :
        htmltext = "30291-11.htm"
        st.takeItems(ALANKELLS_ORDER,1)
        st.takeItems(COMBINED_MAP,1)
        st.takeItems(GOLD_BAR,-1)
        st.giveItems(ALANKELLS_RECOMMEND,1)
        st.set("cond","19")
        st.playSound("ItemSound.quest_middle")
      elif cond==19 :
        htmltext = "30291-12.htm"
     elif npcId == NPC[4] :
      if cond==2 :
        htmltext = "30728-01.htm"
        st.takeItems(ALANKELLS_WARRANT,1)
        st.giveItems(LEIRYNNS_ORDER1,1)
        st.set("cond","3")
        st.playSound("ItemSound.quest_middle")
      elif cond==3 :
        htmltext = "30728-02.htm"
      elif cond==4 :
        htmltext = "30728-03.htm"
        st.takeItems(DELU_TOTEM,-1)
        st.takeItems(LEIRYNNS_ORDER1,1)
        st.giveItems(LEIRYNNS_ORDER2,1)
        st.set("cond","5")
        st.playSound("ItemSound.quest_middle")
      elif cond==5 :
        htmltext = "30728-04.htm"
      elif cond==6 :
        htmltext = "30728-05.htm"
        st.takeItems(CHIEF_KALKIS_FANG,1)
        st.takeItems(LEIRYNNS_ORDER2,1)
        st.giveItems(LEIRYNNS_REPORT,1)
        st.set("cond","7")
        st.playSound("ItemSound.quest_middle")
      elif cond==7 :
        htmltext = "30728-06.htm"
      elif cond==8 :
        htmltext = "30728-07.htm"
     elif npcId == NPC[5]: 
      if cond==8 :
        htmltext = "30729-01.htm"
        st.takeItems(ALANKELLS_LETTER,1)
        st.giveItems(WINE_CATALOG,1)
        st.set("cond","9")
        st.playSound("ItemSound.quest_middle")
      elif cond==9 :
        htmltext = "30729-02.htm"
      elif cond==12 :
        htmltext = "30729-03.htm"
        st.takeItems(WINE_CATALOG,1)
        st.takeItems(MALRUKIAN_WINE,1)
        st.set("cond","13")
        st.playSound("ItemSound.quest_middle")
        st.giveItems(OLD_ORDER,1)
      elif cond==13 :
        htmltext = "30729-04.htm"
      elif cond in [8,14] :
        htmltext = "30729-05.htm"
     elif npcId == NPC[1] :
      if cond==10 :
        htmltext = "30420-02.htm"
      elif cond==11 :
          htmltext = "30420-03.htm"
          st.takeItems(TWEETYS_CONTRACT,1)
          st.takeItems(RED_SPORE_DUST,-1)
          st.set("cond","12")
          st.playSound("ItemSound.quest_middle")
          st.giveItems(MALRUKIAN_WINE,1)
      elif cond in [12,13]  :
        htmltext = "30420-04.htm"
      elif cond==9 :
        htmltext = "30420-01.htm"
     elif npcId == NPC[6] :
      if cond==13 :
        htmltext = "30730-01.htm"
      elif cond==14 :
         htmltext = "30730-02.htm"
      elif cond == 15:
         htmltext = "30730-03.htm"
         st.takeItems(LAMBERTS_MAP,1)
         st.takeItems(TORN_MAP_PIECE2,4)
         st.takeItems(TORN_MAP_PIECE1,4)
         st.takeItems(REXS_DIARY,1)
         st.takeItems(SOLTS_MAP,1)
         st.takeItems(MAKELS_MAP,1)
         st.set("cond","16")
         st.giveItems(COMBINED_MAP,1)
      elif cond>15 :
        htmltext = "30730-04.htm"
     elif npcId == NPC[7] and cond==16:
        htmltext = "30627-01.htm"
     elif npcId == NPC[2] :
        if cond==17 :
          htmltext = "30628-01.htm"
        else:
          htmltext = "<html><body>You haven't got a Key for this Chest.</body></html>"
   return htmltext

 def onKill(self,npc,player,isPet):
   st = player.getQuestState(qn)
   if not st : return 
   if st.getState() != STARTED : return 
   cond = st.getInt("cond")
   npcId = npc.getNpcId()
   status,maxcount,chance,itemid=DROPLIST[npcId]
   random = st.getRandom(100)
   count=st.getQuestItemsCount(itemid)
   if cond==status and count<maxcount and random<chance :
    if cond == 14:
     if npcId==20144:
      if st.getQuestItemsCount(MAKELS_MAP) ==0:
       st.giveItems(itemid,1)
       if count==maxcount-1:
        st.playSound("ItemSound.quest_middle")
        st.giveItems(MAKELS_MAP,1)
        st.takeItems(TORN_MAP_PIECE2,4)
        if st.getQuestItemsCount(MAKELS_MAP) ==1 and st.getQuestItemsCount(SOLTS_MAP) ==1 :
           st.set("cond",str(cond+1))
       else:
        st.playSound("Itemsound.quest_itemget")
     elif npcId==20551:
      if st.getQuestItemsCount(SOLTS_MAP) ==0:
       st.giveItems(itemid,1)
       if count==maxcount-1:
        st.playSound("ItemSound.quest_middle")
        st.giveItems(SOLTS_MAP,1)
        st.takeItems(TORN_MAP_PIECE1,4)
        if st.getQuestItemsCount(MAKELS_MAP) ==1 and st.getQuestItemsCount(SOLTS_MAP) ==1 :
           st.set("cond",str(cond+1))
       else:
        st.playSound("Itemsound.quest_itemget")
    else:     
     st.giveItems(itemid,1)
     if count==maxcount-1:
      st.playSound("ItemSound.quest_middle")
      st.set("cond",str(cond+1))
      if npcId == 27093:
         st.giveItems(STRANGE_MAP,1)
     else:
      st.playSound("Itemsound.quest_itemget")
   if npcId==20781 and random<30 and count<maxcount:
     st.addSpawn(27094,npc.getX(),npc.getY(),npc.getZ(),npc.getHeading(),True,300000)
   return

QUEST       = Quest(225,qn,"Test Of Searcher")
CREATED     = State('Start', QUEST)
STARTED     = State('Started', QUEST)
COMPLETED   = State('Completed', QUEST)

QUEST.setInitialState(CREATED)
QUEST.addStartNpc(30690)

for npcId in NPC:
 QUEST.addTalkId(npcId)

for mobId in MOB:
 QUEST.addKillId(mobId)