# Maked by Mr. Have fun! Version 0.2
# Shadow Weapon Coupons contributed by BiTi for the Official L2J Datapack Project
# Visit http://forum.l2jdp.com for more details
import sys
from com.it.br.gameserver.model.quest import State
from com.it.br.gameserver.model.quest import QuestState
from com.it.br.gameserver.model.quest.jython import QuestJython as JQuest

qn = "403_PathToRogue"

BEZIQUES_LETTER = 1180
SPATOIS_BONES = 1183
HORSESHOE_OF_LIGHT = 1184
WANTED_BILL = 1185
STOLEN_JEWELRY = 1186
STOLEN_TOMES = 1187
STOLEN_RING = 1188
STOLEN_NECKLACE = 1189
BEZIQUES_RECOMMENDATION = 1190
NETIS_BOW = 1181
NETIS_DAGGER = 1182

DROP_CHANCE = { 20035:2, 20042:3, 20045:2, 20051:2, 20054:8, 20060:8 }

STOLEN_ITEM = {
0: (STOLEN_JEWELRY),
1: (STOLEN_TOMES),
2: (STOLEN_RING),
3: (STOLEN_NECKLACE)
}

# Helper function - If player have all stolen items returns 1, otherwise 0
def HaveAllStolenItems (st) :
  for i in STOLEN_ITEM.keys() :
    if st.getQuestItemsCount(STOLEN_ITEM[i]) == 0 :
      return 0
  return 1

# Main Quest code
class Quest (JQuest) :

 def __init__(self,id,name,descr): JQuest.__init__(self,id,name,descr)

 def onEvent (self,event,st) :
    htmltext = event
    player = st.getPlayer()
    if event == "30379_2" :
          if player.getClassId().getId() == 0x00 :
            if player.getLevel() >= 19 :
              if st.getQuestItemsCount(BEZIQUES_RECOMMENDATION)>0 :
                htmltext = "30379-04.htm"
              else:
                htmltext = "30379-05.htm"
                return htmltext
            else :
              htmltext = "30379-03.htm"
          else:
            if player.getClassId().getId() == 0x07 :
              htmltext = "30379-02a.htm"
            else:
              htmltext = "30379-02.htm"
    elif event == "1" :
        st.set("id","0")
        st.set("cond","1")
        st.setState(STARTED)
        st.playSound("ItemSound.quest_accept")
        st.giveItems(BEZIQUES_LETTER,1)
        htmltext = "30379-06.htm"
    elif event == "30425_1" :
          st.takeItems(BEZIQUES_LETTER,1)
          if st.getQuestItemsCount(NETIS_BOW) == 0 :
            st.giveItems(NETIS_BOW,1)
          if st.getQuestItemsCount(NETIS_DAGGER) == 0 :
            st.giveItems(NETIS_DAGGER,1)
          st.set("cond","2")
          htmltext = "30425-05.htm"
    return htmltext


 def onTalk (self,npc,player):
   htmltext = "<html><body>You are either not carrying out your quest or don't meet the criteria.</body></html>"
   st = player.getQuestState(qn)
   if not st : return htmltext

   npcId = npc.getNpcId()
   id = st.getState()
   if npcId != 30379 and id != STARTED : return htmltext

   if id == CREATED :
     st.setState(STARTING)
     st.set("cond","0")
     st.set("onlyone","0")
     st.set("id","0")
   if npcId == 30379 and st.getInt("cond")==0 :
     htmltext = "30379-01.htm"
   elif npcId == 30379 and st.getInt("cond") :
        if st.getQuestItemsCount(HORSESHOE_OF_LIGHT) == 0 and HaveAllStolenItems(st) :
          htmltext = "30379-09.htm"
          st.giveItems(BEZIQUES_RECOMMENDATION,1)
          st.takeItems(NETIS_BOW,1)
          st.takeItems(NETIS_DAGGER,1)
          st.takeItems(WANTED_BILL,1)
          for i in STOLEN_ITEM.keys() :
            st.takeItems(STOLEN_ITEM[i],-1)
          st.set("cond","0")
          st.setState(COMPLETED)
          st.playSound("ItemSound.quest_finish")
        elif st.getQuestItemsCount(HORSESHOE_OF_LIGHT) == 0 and st.getQuestItemsCount(BEZIQUES_LETTER)>0 :
          htmltext = "30379-07.htm"
        elif st.getQuestItemsCount(HORSESHOE_OF_LIGHT)>0 :
          htmltext = "30379-08.htm"
          st.takeItems(HORSESHOE_OF_LIGHT,1)
          st.giveItems(WANTED_BILL,1)
          st.set("cond","5")
        elif st.getQuestItemsCount(NETIS_BOW) and st.getQuestItemsCount(NETIS_DAGGER) and st.getQuestItemsCount(WANTED_BILL) == 0 :
          htmltext = "30379-10.htm"
        elif st.getQuestItemsCount(WANTED_BILL) :
          htmltext = "30379-11.htm"
   elif npcId == 30425 and st.getInt("cond") and st.getQuestItemsCount(BEZIQUES_LETTER)>0 :
        htmltext = "30425-01.htm"
   elif npcId == 30425 and st.getInt("cond") and st.getQuestItemsCount(HORSESHOE_OF_LIGHT)==0 and st.getQuestItemsCount(BEZIQUES_LETTER)==0 :
        if st.getQuestItemsCount(SPATOIS_BONES)<10 :
          htmltext = "30425-06.htm"
        elif st.getQuestItemsCount(WANTED_BILL) :
          htmltext = "30425-08.htm"
        elif st.getQuestItemsCount(SPATOIS_BONES) >= 10 :
          htmltext = "30425-07.htm"
          st.takeItems(SPATOIS_BONES,st.getQuestItemsCount(SPATOIS_BONES))
          st.giveItems(HORSESHOE_OF_LIGHT,1)
          st.set("cond","4")
   elif npcId == 30425 and st.getInt("cond") and st.getQuestItemsCount(HORSESHOE_OF_LIGHT)>0 :
        htmltext = "30425-08.htm"
   return htmltext

 def onKill(self,npc,player,isPet):
   st = player.getQuestState(qn)
   if not st : return 
   if st.getState() != STARTED : return 
   
   npcId = npc.getNpcId()
   if st.getItemEquipped(7) == NETIS_BOW or st.getItemEquipped(7) == NETIS_DAGGER :
     if npcId in (20035, 20042, 20045, 20051, 20054, 20060) :
        st.set("id","0")
        if st.getInt("cond") and st.getQuestItemsCount(SPATOIS_BONES)<10 and st.getRandom(10)<DROP_CHANCE[npcId] :
            st.giveItems(SPATOIS_BONES,1)
            if st.getQuestItemsCount(SPATOIS_BONES) == 10 :
              st.playSound("ItemSound.quest_middle")
              st.set("cond","3")
            else:
              st.playSound("ItemSound.quest_itemget")
     elif npcId == 27038 :
        st.set("id","0")
        if st.getInt("cond") and st.getQuestItemsCount(WANTED_BILL)>0 :
            n = st.getRandom(4)
            if st.getQuestItemsCount(STOLEN_ITEM[n]) == 0 :
                st.giveItems(STOLEN_ITEM[n],1)
                if not HaveAllStolenItems(st) :
                  st.playSound("ItemSound.quest_itemget")
                else:
                  st.playSound("ItemSound.quest_middle")
                  st.set("cond","6")
   return

QUEST       = Quest(403,qn,"Path To Rogue")
CREATED     = State('Start', QUEST)
STARTING     = State('Starting', QUEST)
STARTED     = State('Started', QUEST)
COMPLETED   = State('Completed', QUEST)

QUEST.setInitialState(CREATED)
QUEST.addStartNpc(30379)
QUEST.addTalkId(30379)
QUEST.addTalkId(30425)
QUEST.addKillId(27038)

for mobId in (20035,20042,20045,20051,20054,20060) :
  QUEST.addKillId(mobId)