#Made by Emperorc
import sys
from com.it.br.gameserver.model.quest import State
from com.it.br.gameserver.model.quest import QuestState
from com.it.br.gameserver.model.actor.instance import L2NpcInstance
from com.it.br.gameserver.model.quest.jython import QuestJython as JQuest
from com.it.br.gameserver.datatables.sql import SpawnTable
from com.it.br.gameserver.network.serverpackets import CreatureSay
from java.util import Iterator
from com.it.br.gameserver.datatables.xml import NpcTable
from com.it.br.gameserver.model import L2Spawn
from com.it.br.gameserver.model.actor.instance import L2MonsterInstance

qn = "610_MagicalPowerOfWaterPart2"

#NPC
Asefa = 31372
Alter = 31560


#MOBS
Ketra_Orcs = [ 21324, 21325, 21327, 21328, 21329, 21331, 21332, 21334, 21335, \
21336, 21338, 21339, 21340, 21342, 21343, 21344, 21345, 21346, 21347, 21348, 21349 ]
Ashutar = 25316

#ITEMS
Totem2 = 7238
Ice_Heart = 7239

def FindTemplate (npcId) :
    for spawn in SpawnTable.getInstance().getSpawnTable().values():
        if spawn.getNpcId() == npcId:
            npcinstance=spawn.getLastSpawn()
            break
    return npcinstance

def AutoChat(npc,text) :
    chars = npc.getKnownList().getKnownPlayers().values().toArray()
    if chars != None:
       for pc in chars :
          sm = CreatureSay(npc.getObjectId(), 0, npc.getName(), text)
          pc.sendPacket(sm)

class Quest (JQuest) :

 def __init__(self,id,name,descr): JQuest.__init__(self,id,name,descr)

 def onAdvEvent (self, event, npc, player) :
   st = player.getQuestState(qn)
   if not st: return
   cond = st.getInt("cond")
   id = st.getInt("id")
   Green_Totem = st.getQuestItemsCount(Totem2)
   Heart = st.getQuestItemsCount(Ice_Heart)
   htmltext = event
   if event == "31372-04.htm" :
       if st.getPlayer().getLevel() >= 75 and st.getPlayer().getAllianceWithVarkaKetra() >= 2 :
           if Green_Totem :
                st.set("cond","1")
                st.set("id","1")
                st.setState(STARTED)
                st.playSound("ItemSound.quest_accept")
                htmltext = "31372-04.htm"
           else :
                htmltext = "31372-02.htm"
                st.exitQuest(1)
       else :
           htmltext = "31372-03.htm"
           st.exitQuest(1)
   elif event == "31372-08.htm" :
       if Heart:
           htmltext = "31372-08.htm"
           st.takeItems(Ice_Heart,-1)
           st.addExpAndSp(10000,0)
           st.unset("id")
           st.unset("cond")
           st.playSound("ItemSound.quest_finish")
           st.exitQuest(1)
       else :
           htmltext = "31372-09.htm"

   elif event == "31560-02.htm" :
       if Green_Totem :
           htmletext = "31560-02.htm"   #TODO add lights from above
           spawnedNpc = st.addSpawn(Ashutar,104825,-36926,-1136)
           st.takeItems(Totem2,1)
           st.set("id","2")
           npc.setBusy(True)
           st.set("cond","2")
           st.startQuestTimer("Soul of Water Ashutar has despawned",1200000, spawnedNpc)
           AutoChat(spawnedNpc,"Hey! I'll kick your arse!")#this is only a temp message until we find out what it actually is! string = 61050
       else :
           htmltext = "31560-04.htm"
   elif event == "Soul of Water Ashutar has despawned" :
       npc.reduceCurrentHp(9999999,npc)
       st.unset("id")
       st.unset("cond")
       FindTemplate(Alter).setBusy(False)
       st.exitQuest(1)
       AutoChat(npc,"May the gods forever condemn you! Udan Mardui, your power weakens!") #this is only a temp message until we find out what it actually is! string = 61051
   return htmltext

 def onTalk (self, npc, player):
   st = player.getQuestState(qn)
   htmltext = "<html><body>You are either not carrying out your quest or don't meet the criteria.</body></html>"
   if st :
    npcId = npc.getNpcId()
    cond = st.getInt("cond")
    id = st.getInt("id")
    Green_Totem = st.getQuestItemsCount(Totem2)
    Heart = st.getQuestItemsCount(Ice_Heart)
    if npcId == Asefa :
        if st.getState()== CREATED :
            htmltext = "31372-01.htm"
        elif id == 1 or id == 2 :
            htmltext = "31372-05.htm"
        elif id == 3:
            if Heart :
                htmltext = "31372-06.htm"
            else :
                htmltext = "31372-07.htm"
    elif npcId == Alter :
       if npc.isBusy() :
           htmltext = "31560-03.htm"
       else :
        if id == 1 :
            htmltext = "31560-01.htm"
        elif id == 2 or id == 3 :
            htmltext = "31560-05.htm"
    return htmltext

 def onKill(self,npc,player,isPet):
   npcId = npc.getNpcId()
   if npcId == Ashutar :
      FindTemplate(Alter).setBusy(False)
      party = player.getParty()
      if party :
         for partyMember in party.getPartyMembers().toArray() :
             pst = partyMember.getQuestState(qn)
             if pst :
                if pst.getInt("cond") >= 1 :
                    if pst.getInt("cond") == 1 :
                        st.takeItems(Totem2,1)
                    if pst.getQuestItemsCount(Ice_Heart) < 1 :
                       pst.giveItems(Ice_Heart,1)
                       pst.playSound("ItemSound.quest_middle")
                       pst.set("cond","3")
                       pst.set("id","3")
                    if pst.getQuestTimer("Soul of Water Ashutar has despawned") :
                       pst.getQuestTimer("Soul of Water Ashutar has despawned").cancel()
      else :
         pst = player.getQuestState(qn)
         if pst :
            if pst.getInt("cond") >= 1 :
                if pst.getInt("cond") == 1 :
                    st.takeItems(Totem2,1)
                if pst.getQuestItemsCount(Ice_Heart) < 1 :
                   pst.giveItems(Ice_Heart,1)
                   pst.playSound("ItemSound.quest_middle")
                   pst.set("cond","3")
                   pst.set("id","3")
                if pst.getQuestTimer("Soul of Water Ashutar has despawned") :
                   pst.getQuestTimer("Soul of Water Ashutar has despawned").cancel()
   elif npcId in Ketra_Orcs :
      st = player.getQuestState(qn)
      if st :
         if st.getQuestItemsCount(Ice_Heart) :
             st.takeItems(Ice_Heart,-1)
         st.unset("cond")
         st.unset("id")
         st.exitQuest(1)
   return

QUEST       = Quest(610,qn,"Magical Power of Water - Part 2")
CREATED     = State('Start', QUEST)
STARTED     = State('Started', QUEST)

QUEST.setInitialState(CREATED)
QUEST.addStartNpc(Asefa)

QUEST.addTalkId(Asefa)
QUEST.addTalkId(Alter)

QUEST.addKillId(Ashutar)

for mobId in Ketra_Orcs:
    QUEST.addKillId(mobId)