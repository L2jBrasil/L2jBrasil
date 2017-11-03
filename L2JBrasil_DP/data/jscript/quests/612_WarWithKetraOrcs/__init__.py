# Created by Emperorc
import sys
from com.it.br import Config
from com.it.br.gameserver.model.quest import State
from com.it.br.gameserver.model.quest import QuestState
from com.it.br.gameserver.model.quest.jython import QuestJython as JQuest

qn = "612_WarWithKetraOrcs"

#NPC
Ashas = 31377

#Mobs
Varka_Mobs = [ 21350, 21351, 21353, 21354, 21355, 21357, 21358, 21360, 21361, 21362, 21369, 21370, 21364, 21365, 21366, 21368, 21371, 21372, 21373, 21374, 21375 ]
Ketra_Orcs = [ 21324, 21327, 21328, 21329, 21331, 21332, 21334, 21336, 21338, 21339, 21340, 21342, 21343, 21345, 21347 ]


Chance = {
  21324:500,
  21327:510,
  21328:522,
  21329:519,
  21331:529,
  21332:664,
  21334:539,
  21336:529,
  21338:558,
  21339:568,
  21340:568,
  21342:578,
  21343:548,
  21345:713,
  21347:738
}

#Items
Seed = 7187
Molar = 7234

class Quest (JQuest) :

 def __init__(self,id,name,descr): JQuest.__init__(self,id,name,descr)

 def onEvent (self,event,st) :
     htmltext = event
     Molars = st.getQuestItemsCount(Molar)
     if event == "31377-03.htm" :
       if st.getPlayer().getLevel() >= 74 and st.getPlayer().getAllianceWithVarkaKetra() <= -1 : #the alliance check is only temporary, should be done on core side/AI
            st.set("cond","1")
            st.set("id","1")
            st.setState(STARTED)
            st.playSound("ItemSound.quest_accept")
            htmltext = "31377-03.htm"
       else :
            htmltext = "31377-02.htm"
            st.exitQuest(1)
     elif event == "31377-06.htm" :
         htmltext = "31377-06.htm"
     elif event == "31377-07.htm" :
         if Molars >= 100 :
             htmltext = "31377-07.htm"
             st.takeItems(Molar,100)
             st.giveItems(Seed,20)
         else :
             htmltext = "31377-08.htm"
     elif event == "31377-09.htm" :
         htmltext == "31377-09.htm"
         st.unset("id")
         st.takeItems(Molar,-1)
         st.exitQuest(1)
     return htmltext

 def onTalk (self,npc,player):
     st = player.getQuestState(qn)
     htmltext = "<html><body>You are either not carrying out your quest or don't meet the criteria.</body></html>"
     if st :
         npcId = npc.getNpcId()
         id = st.getInt("id")
         cond = st.getInt("cond")
         Molars = st.getQuestItemsCount(Molar)
         if npcId == Ashas and player.getAllianceWithVarkaKetra() <= -1 : #the alliance check is only temporary, should be done on core side/AI
             if id == 1 :
                 if Molars :
                     htmltext = "31377-04.htm"
                 else :
                    htmltext = "31377-05.htm"
             else :
                 htmltext = "31377-01.htm"
     return htmltext

 def onKill(self,npc,player,isPet):
     partyMember = self.getRandomPartyMemberState(player,STARTED)
     if not partyMember : return
     st = partyMember.getQuestState(qn)
     npcId = npc.getNpcId()
     count = st.getQuestItemsCount(Molar)
     st2 = partyMember.getQuestState("611_AllianceWithVarkaSilenos")
     if npcId in Ketra_Orcs and partyMember.getAllianceWithVarkaKetra() <= -1 :
    #see comments in 611 : Alliance with Varka Silenos for reason for doing st2 check
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
            st.giveItems(Molar,numItems)
     elif npcId in Varka_Mobs :
         st.unset("id")
         st.takeItems(Molar,-1)
         st.exitQuest(1)
     return

QUEST       = Quest(612, qn, "War With Ketra Orcs")
CREATED     = State('Start', QUEST)
STARTED     = State('Started', QUEST)

QUEST.setInitialState(CREATED)
QUEST.addStartNpc(Ashas)

QUEST.addTalkId(Ashas)

for mobId in Ketra_Orcs :
  QUEST.addKillId(mobId)

for mobId in Varka_Mobs :
  QUEST.addKillId(mobId)