# Created by Emperorc
import sys
from com.it.br import Config
from com.it.br.gameserver.model.quest import State
from com.it.br.gameserver.model.quest import QuestState
from com.it.br.gameserver.model.quest.jython import QuestJython as JQuest

qn = "606_WarWithVarkaSilenos"

#NPC
Kadun = 31370

#Mobs
Varka_Mobs = [ 21350, 21353, 21354, 21355, 21357, 21358, 21360, 21362, 21364, 21365, 21366, 21368, 21369, 21371, 21373 ]
Ketra_Orcs = [ 21324, 21325, 21327, 21328, 21329, 21331, 21332, 21334, 21335, 21336, 21338, 21339, 21340, 21342, 21343, 21344, 21345, 21346, 21347, 21348, 21349 ]

Chance = {
  21350:500,#Recruit
  21353:510,#Scout
  21354:522,#Hunter
  21355:519,#Shaman
  21357:529,#Priest
  21358:529,#Warrior  
  21360:539,#Medium
  21362:568,#Officer
  21364:558,#Seer
  21365:568,#Great Magus
  21366:664,#General
  21368:568,#Great Seer
  21369:548,#Commander
  21371:713,#Head magus
  21373:738 #Prophet
}

#Items
Horn = 7186
Mane = 7233

class Quest (JQuest) :

 def __init__(self,id,name,descr): JQuest.__init__(self,id,name,descr)

 def onEvent (self,event,st) :
     htmltext = event
     manes = st.getQuestItemsCount(Mane)
     if event == "31370-03.htm" :
       if st.getPlayer().getLevel() >= 74 and st.getPlayer().getAllianceWithVarkaKetra() >= 1 : #the alliance check is only temporary, should be done on core side/AI
            st.set("cond","1")
            st.set("id","1")
            st.setState(STARTED)
            st.playSound("ItemSound.quest_accept")
            htmltext = "31370-03.htm"
       else :
            htmltext = "31370-02.htm"
            st.exitQuest(1)
     elif event == "31370-06.htm" :
         htmltext = "31370-06.htm"
     elif event == "31370-07.htm" :
         if manes >= 100 :
             htmltext = "31370-07.htm"
             st.takeItems(Mane,100)
             st.giveItems(Horn,20)
         else :
             htmltext = "31370-08.htm"
     elif event == "31370-09.htm" :
         htmltext == "31370-09.htm"
         st.unset("id")
         st.takeItems(Mane,-1)
         st.exitQuest(1)
     return htmltext

 def onTalk (self,npc,player):
     htmltext = "<html><body>You are either not carrying out your quest or don't meet the criteria.</body></html>"
     st = player.getQuestState(qn)
     if st :
        npcId = npc.getNpcId()
        id = st.getInt("id")
        cond = st.getInt("cond")
        manes = st.getQuestItemsCount(Mane)
        if npcId == Kadun :
         if id == 1 :
             if manes :
                 htmltext = "31370-04.htm"
                 st.set("cond","2")
             else :
                htmltext = "31370-05.htm"
         else :
             htmltext = "31370-01.htm"
     return htmltext

 def onKill(self,npc,player,isPet):
     partyMember = self.getRandomPartyMemberState(player, STARTED)
     if not partyMember: return
     st = partyMember.getQuestState(qn)
     if st :
        if st.getState() == STARTED :
         npcId = npc.getNpcId()
         count = st.getQuestItemsCount(Mane)
         st2 = partyMember.getQuestState("605_AllianceWithKetraOrcs")
         if npcId in Varka_Mobs and partyMember.getAllianceWithVarkaKetra() >= 1 :
        #see comments in 605 : Alliance with Ketra Orcs for reason for doing st2 check
            if not st2 :
                numItems,chance = divmod(Chance[npcId]*Config.RATE_DROP_QUEST,1000)
                if st.getRandom(1000) < chance :
                    numItems += 1
                numItems = int(numItems)
                if numItems != 0 :
                    if int((count+numItems)/100) > int(count/100) :
                        st.playSound("ItemSound.quest_middle")
                    else :
                        st.playSound("ItemSound.quest_itemget")
                    st.giveItems(Mane,numItems)
                st.giveItems(Mane,1)
         elif npcId in Ketra_Orcs :
             st.unset("id")
             st.takeItems(Mane,-1)
             st.exitQuest(1)
     return

QUEST       = Quest(606, qn, "War With Varka Silenos")
CREATED     = State('Start', QUEST)
STARTED     = State('Started', QUEST)

QUEST.setInitialState(CREATED)
QUEST.addStartNpc(Kadun)
QUEST.addTalkId(Kadun)

for mobId in Varka_Mobs :
  QUEST.addKillId(mobId)

for mobId in Ketra_Orcs :
  QUEST.addKillId(mobId)