# Maked by Mr. Have fun! Version 0.2
# rewritten by Rolarga Version 0.3
# Shadow Weapon Coupons contributed by BiTi for the Official L2J Datapack Project
# Visit http://forum.l2jdp.com for more details
import sys
from com.it.br.gameserver.model.quest import State
from com.it.br.gameserver.model.quest import QuestState
from com.it.br.gameserver.model.quest.jython import QuestJython as JQuest

qn = "222_TestOfDuelist"

MARK_OF_DUELIST,  ORDER_GLUDIO,      ORDER_DION,        ORDER_GIRAN,      ORDER_OREN,      \
ORDER_ADEN,       PUNCHERS_SHARD,    NOBLE_ANTS_FEELER, DRONES_CHITIN,    DEADSEEKER_FANG, \
OVERLORD_NECKLACE,CRIMSONBINDS_CHAIN,CHIEFS_AMULET,     TEMPERED_EYE_MEAT,TAMRIN_ORCS_RING,\
TAMRIN_ORCS_ARROW,FINAL_ORDER,       EXCUROS_SKIN,      KRATORS_SHARD,    GRANDIS_SKIN,    \
TIMAK_ORCS_BELT,  RAKINS_MACE = range(2762,2784)

#Shadow Weapon Exchange Coupon
SHADOW_WEAPON_COUPON_CGRADE = 8870

DROPLIST={
20085:(1,10,PUNCHERS_SHARD),
20090:(1,10,NOBLE_ANTS_FEELER),
20234:(1,10,DRONES_CHITIN),
20202:(1,10,DEADSEEKER_FANG),
20270:(1,10,OVERLORD_NECKLACE),
20552:(1,10,CRIMSONBINDS_CHAIN),
20582:(1,10,CHIEFS_AMULET),
20564:(1,10,TEMPERED_EYE_MEAT),
20601:(1,10,TAMRIN_ORCS_RING),
20602:(1,10,TAMRIN_ORCS_ARROW),
20604:(2,3,RAKINS_MACE),
20214:(2,3,EXCUROS_SKIN),
20217:(2,3,KRATORS_SHARD),
20588:(2,3,TIMAK_ORCS_BELT),
20554:(2,3,GRANDIS_SKIN)
}


class Quest (JQuest) :

 def __init__(self,id,name,descr): JQuest.__init__(self,id,name,descr)

 def onEvent (self,event,st) :
    htmltext = event
    if event == "30623-07.htm" :
        if st.getInt("step")==0 :
           st.set("cond","1")
           st.set("step","1")
           st.setState(STARTED)
           st.playSound("ItemSound.quest_accept")
           st.giveItems(ORDER_GLUDIO,1)
           st.giveItems(ORDER_DION,1)
           st.giveItems(ORDER_GIRAN,1)
           st.giveItems(ORDER_OREN,1)
           st.giveItems(ORDER_ADEN,1)
    elif event == "30623-04.htm" :
          if st.getPlayer().getRace().ordinal() == 3 :
            htmltext = "30623-05.htm"
    elif event == "30623-16.htm" :
        if st.getQuestItemsCount(FINAL_ORDER)==0:
            for i in [
            PUNCHERS_SHARD,
            NOBLE_ANTS_FEELER,
            DEADSEEKER_FANG,
            DRONES_CHITIN,
            OVERLORD_NECKLACE,
            CRIMSONBINDS_CHAIN,
            CHIEFS_AMULET,
            TEMPERED_EYE_MEAT,
            TAMRIN_ORCS_RING,
            TAMRIN_ORCS_ARROW,
            ORDER_GLUDIO,
            ORDER_DION,
            ORDER_GIRAN,
            ORDER_OREN,
            ORDER_ADEN
            ]:
             st.takeItems(i,-1)
            st.set("step","2")
            st.giveItems(FINAL_ORDER,1)
    return htmltext


 def onTalk (self,npc,player):
   htmltext = "<html><body>You are either not carrying out your quest or don't meet the criteria.</body></html>"
   st = player.getQuestState(qn)
   if not st : return htmltext

   npcId = npc.getNpcId()
   id = st.getState()
   if id == CREATED :
     st.set("step","0")
     st.set("cond","0")
   if id == COMPLETED :
      htmltext = "<html><body>This quest has already been completed.</body></html>"
   elif st.getInt("step")==0 :
      if player.getClassId().getId() in [0x01,0x2f,0x13,0x20] :
         if player.getLevel() >= 39 :
            htmltext = "30623-03.htm"
         else:
            htmltext = "30623-01.htm"
            st.exitQuest(1)
      else:
         htmltext = "30623-02.htm"
         st.exitQuest(1)
   elif st.getInt("step")==1 :
      if st.getQuestItemsCount(ORDER_GLUDIO) and st.getQuestItemsCount(ORDER_DION) and st.getQuestItemsCount(ORDER_GIRAN) and st.getQuestItemsCount(ORDER_OREN) and st.getQuestItemsCount(ORDER_ADEN) :
        if st.getQuestItemsCount(PUNCHERS_SHARD)==st.getQuestItemsCount(NOBLE_ANTS_FEELER)==st.getQuestItemsCount(DRONES_CHITIN)==st.getQuestItemsCount(DEADSEEKER_FANG)==st.getQuestItemsCount(OVERLORD_NECKLACE)==st.getQuestItemsCount(CRIMSONBINDS_CHAIN)==st.getQuestItemsCount(CHIEFS_AMULET)==st.getQuestItemsCount(TEMPERED_EYE_MEAT)==st.getQuestItemsCount(TAMRIN_ORCS_RING)==st.getQuestItemsCount(TAMRIN_ORCS_ARROW) == 10 :
          htmltext = "30623-13.htm"
        else:
          htmltext = "30623-14.htm"
      else:
          htmltext = "30623-14.htm"
          for i in [ORDER_GLUDIO,ORDER_DION,ORDER_GIRAN,ORDER_OREN,ORDER_ADEN]:
            if st.getQuestItemsCount(i)==0:
                st.giveItems(i,1) 
   elif st.getInt("step")==2 and st.getQuestItemsCount(FINAL_ORDER) :
        if st.getQuestItemsCount(EXCUROS_SKIN)==st.getQuestItemsCount(KRATORS_SHARD)==st.getQuestItemsCount(RAKINS_MACE)==st.getQuestItemsCount(GRANDIS_SKIN)==st.getQuestItemsCount(TIMAK_ORCS_BELT)>2 :
            st.takeItems(EXCUROS_SKIN,-1)
            st.takeItems(KRATORS_SHARD,-1)
            st.takeItems(GRANDIS_SKIN,-1)
            st.takeItems(TIMAK_ORCS_BELT,-1)
            st.takeItems(RAKINS_MACE,-1)
            st.addExpAndSp(47015,20000)
            st.giveItems(MARK_OF_DUELIST,1)
            st.giveItems(SHADOW_WEAPON_COUPON_CGRADE,15)
            st.takeItems(FINAL_ORDER,1)
            htmltext = "30623-18.htm"
            st.unset("step")
            st.set("cond","0")
            st.setState(COMPLETED)
            st.playSound("ItemSound.quest_finish")
        else :
          htmltext = "30623-17.htm"
   return htmltext

 def onKill(self,npc,player,isPet):
  st = player.getQuestState(qn)
  if not st : return 
  if st.getState() != STARTED : return 
   
  npcId = npc.getNpcId()
  step,maxcount,item=DROPLIST[npcId]
  count=st.getQuestItemsCount(item)
  if st.getInt("step")==step and count<maxcount:
   st.giveItems(item,1)
   if count == maxcount-1:
     st.playSound("ItemSound.quest_middle")
   else:
     st.playSound("ItemSound.quest_itemget")
  return

QUEST       = Quest(222,qn,"Test Of Duelist")
CREATED     = State('Start', QUEST)
STARTING    = State('Starting', QUEST)
STARTED     = State('Started', QUEST)
COMPLETED   = State('Completed', QUEST)


QUEST.setInitialState(CREATED)
QUEST.addStartNpc(30623)

QUEST.addTalkId(30623)

for i in DROPLIST.keys():
    QUEST.addKillId(i)