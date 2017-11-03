# Created by Emperorc
# Finished by Kerberos_20 10/23/07
import sys
from com.it.br.gameserver.model.quest import State
from com.it.br.gameserver.model.quest import QuestState
from com.it.br.gameserver.model.quest.jython import QuestJython as JQuest
from com.it.br.gameserver.datatables.sql import SkillTable
from com.it.br.gameserver.network.serverpackets import WareHouseWithdrawalList
from com.it.br.gameserver.network.serverpackets import ActionFailed

qn = "6051_VarkaSilenosSupport"


Ashas = 31377 #Hierarch
Naran = 31378 #Messenger
Udan  = 31379 #Buffer
Diyabu= 31380 #Grocer
Hagos = 31381 #Warehouse Keeper
Shikon= 31382 #Trader
Teranu= 31383 #Teleporter
NPCS = range(31377,31384)

Seed = 7187
#"event number":[Buff Id,Buff Level,Cost]
BUFF={
"1":[4359,1,2],#Focus: Requires 2 Nepenthese Seeds
"2":[4360,1,2],#Death Whisper: Requires 2 Nepenthese Seeds
"3":[4345,1,3],#Might: Requires 3 Nepenthese Seeds
"4":[4355,1,3],#Acumen: Requires 3 Nepenthese Seeds
"5":[4352,1,3],#Berserker: Requires 3 Nepenthese Seeds
"6":[4354,1,3],#Vampiric Rage: Requires 3 Nepenthese Seeds
"7":[4356,1,6],#Empower: Requires 6 Nepenthese Seeds
"8":[4357,1,6],#Haste: Requires 6 Nepenthese Seeds
}

class Quest (JQuest) :

 def __init__(self,id,name,descr): JQuest.__init__(self,id,name,descr)

 def onAdvEvent (self,event,npc,player) :
    htmltext = event
    st = player.getQuestState(qn)
    if not st: return
    Alevel = player.getAllianceWithVarkaKetra()
    if str(event) in BUFF.keys() :
        skillId,level,seeds=BUFF[event]
        if st.getQuestItemsCount(Seed) >= seeds :
            st.takeItems(Seed,seeds)
            npc.setTarget(player)
            npc.doCast(SkillTable.getInstance().getInfo(skillId,level))
            npc.setCurrentHpMp(npc.getMaxHp(), npc.getMaxMp())
            htmltext = "31379-4.htm"
    elif event == "Withdraw" :
        if player.getWarehouse().getSize() == 0 :
            htmltext = "31381-0.htm"
        else :
            player.sendPacket(ActionFailed())
            player.setActiveWarehouse(player.getWarehouse())
            player.sendPacket(WareHouseWithdrawalList(player, 1))
    elif event == "Teleport" :
        if Alevel == -4 :
            htmltext = "31383-4.htm"
        elif Alevel == -5 :
            htmltext = "31383-5.htm"
    return htmltext

 def onFirstTalk (self,npc,player):
    htmltext = "<html><body>You are either not carrying out your quest or don't meet the criteria.</body></html>"
    st = player.getQuestState(qn)
    if not st :
        st = self.newQuestState(player)
    npcId = npc.getNpcId()
    Alevel = player.getAllianceWithVarkaKetra()
    Seeds = st.getQuestItemsCount(Seed)
    if npcId == Ashas :
        if Alevel < 0 :
            htmltext = "31377-friend.htm"
        else:
            htmltext = "31377-no.htm"
    elif npcId == Naran :
        if Alevel < 0 :
            htmltext = "31378-friend.htm"
        else :
            htmltext = "31378-no.htm"
    elif npcId == Udan :
        st.setState(STARTED)
        if Alevel > -1 :
            htmltext = "31379-3.htm"
        elif Alevel > -3 and Alevel < 0:
            htmltext = "31379-1.htm"
        elif Alevel < -2 :
            if Seeds :
                htmltext = "31379-4.htm"
            else :
                htmltext = "31379-2.htm"
    elif npcId == Diyabu :
        if player.getKarma() >= 1: 
            htmltext = "31380-pk.htm"
        elif Alevel >= 0 :
            htmltext = "31380-no.htm"
        elif Alevel == -1 or Alevel == -2:
            htmltext = "31380-1.htm"
        else:
            htmltext = "31380-2.htm"
    elif npcId == Hagos :
        if Alevel > 0 :
            htmltext = "31381-no.htm"
        elif Alevel == -1 :
            htmltext = "31381-1.htm"
        elif player.getWarehouse().getSize() == 0 :
            htmltext = "31381-3.htm"
        elif Alevel == -2 or Alevel == -3:
            htmltext = "31381-2.htm"
        else :
            htmltext = "31381-4.htm"
    elif npcId == Shikon :
        if Alevel == -2 :
            htmltext = "31382-1.htm"
        elif Alevel == -3 or Alevel == -4 :
            htmltext = "31382-2.htm"
        elif Alevel == -5 :
            htmltext = "31382-3.htm"
        else :
            htmltext = "31382-no.htm"
    elif npcId == Teranu :
        if Alevel >= 0 :
            htmltext = "31383-no.htm"
        elif Alevel < 0 and Alevel > -4 :
            htmltext = "31383-1.htm"
        elif Alevel == -4 :
            htmltext = "31383-2.htm"
        else :
            htmltext = "31383-3.htm"
    return htmltext

QUEST       = Quest(6051, qn, "custom")
CREATED     = State('Start', QUEST)
STARTED     = State('Started', QUEST)

QUEST.setInitialState(CREATED)
for i in NPCS:
   QUEST.addFirstTalkId(i)
QUEST.addTalkId(Udan)
QUEST.addTalkId(Hagos)
QUEST.addTalkId(Teranu)