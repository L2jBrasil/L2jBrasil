# Made by Kerberos v1.0 on 2009/05/10
# this script is part of the Official L2J Datapack Project.

import sys
import time

from com.it.br.gameserver.ai import CtrlIntention
from com.it.br.gameserver.model.quest			import State
from com.it.br.gameserver.model.quest			import QuestState
from com.it.br.gameserver.model.quest.jython		import QuestJython as JQuest
from com.it.br.gameserver.network.serverpackets import NpcSay

qn = "25_HidingBehindTheTruth"

# Npcs
Agripel = 31348
Benedict = 31349
Wizard = 31522
Tombstone = 31531
Lidia = 31532
Bookshelf = 31533
Bookshelf2 = 31534
Bookshelf3 = 31535
Coffin = 31536
Triol = 27218

# Items
Contract = 7066
Dress = 7155
SuspiciousTotem = 7156
GemstoneKey = 7157
TotemDoll = 7158

class Quest (JQuest) :
    def __init__(self,id,name,descr):
        JQuest.__init__(self,id,name,descr)
        self.questItemIds = [SuspiciousTotem,GemstoneKey,TotemDoll,Dress]


    def onAdvEvent (self,event,npc, player) :
        st = player.getQuestState(qn)
        if not st: return
        htmltext = event
        if event == "31349-02.htm" :
            st.playSound("ItemSound.quest_accept")
            st.set("cond","1")
            st.setState(State.STARTED)
        elif event == "31349-03.htm" :
            if st.getQuestItemsCount(SuspiciousTotem) :
                htmltext = "31349-05.htm"
            else :
                st.playSound("ItemSound.quest_middle")
                st.set("cond","2")
        elif event == "31349-10.htm" :
            st.playSound("ItemSound.quest_middle")
            st.set("cond","4")
        elif event == "31348-02.htm" :
            st.takeItems(SuspiciousTotem,-1)
        elif event == "31348-07.htm" :
            st.playSound("ItemSound.quest_middle")
            st.set("cond","5")
            st.giveItems(GemstoneKey,1)
        elif event == "31522-04.htm" :
            st.playSound("ItemSound.quest_middle")
            st.set("cond","6")
        elif event == "31535-03.htm" :
            if st.getInt("step") == 0:
               st.set("step","1")
               triol = st.addSpawn(Triol,59712,-47568,-2712,0,0,300000,1)
               time.sleep(1)
               triol.broadcastPacket(NpcSay(triol.getObjectId(), 0, triol.getNpcId(), "That box was sealed by my master. Don't touch it!"))
               triol.setRunning()
               triol.addDamageHate(player,0,999)
               triol.getAI().setIntention(CtrlIntention.AI_INTENTION_ATTACK, player)
               st.playSound("ItemSound.quest_middle")
               st.set("cond","7")
            elif st.getInt("step") == 2:
                htmltext = "31535-04.htm"
        elif event == "31535-05.htm" :
            st.giveItems(Contract,1)
            st.takeItems(GemstoneKey,-1)
            st.playSound("ItemSound.quest_middle")
            st.set("cond","9")
        elif event == "31532-02.htm" :
            st.takeItems(Contract,-1)
        elif event == "31532-06.htm" :
            st.playSound("ItemSound.quest_middle")
            st.set("cond","11")
        elif event == "31531-02.htm" :
            st.playSound("ItemSound.quest_middle")
            st.set("cond","12")
            st.addSpawn(Coffin,60104,-35820,-664,0,0,20000,1)
        elif event == "31532-18.htm" :
            st.playSound("ItemSound.quest_middle")
            st.set("cond","15")
        elif event == "31522-12.htm" :
            st.playSound("ItemSound.quest_middle")
            st.set("cond","16")
        elif event == "31348-10.htm" :
            st.takeItems(TotemDoll,-1)
        elif event == "31348-15.htm" :
            st.playSound("ItemSound.quest_middle")
            st.set("cond","17")
        elif event == "31348-16.htm" :
            st.playSound("ItemSound.quest_middle")
            st.set("cond","18")
        elif event == "31532-20.htm" :
            st.giveItems(905,2)
            st.giveItems(874,1)
            st.takeItems(7063,-1)
            st.addExpAndSp(572277,53750)
            st.unset("cond")
            st.exitQuest(False)
            st.playSound("ItemSound.quest_finish")
        elif event == "31522-15.htm" :
            st.giveItems(936,1)
            st.giveItems(874,1)
            st.takeItems(7063,-1)
            st.addExpAndSp(572277,53750)
            st.unset("cond")
            st.exitQuest(False)
            st.playSound("ItemSound.quest_finish")
        return htmltext


    def onTalk (self,npc,player):
        htmltext = "<html><body>You are either not on a quest that involves this NPC, or you don't meet this NPC's minimum quest requirements.</body></html>"
        st = player.getQuestState(qn)
        if not st : return htmltext
        npcId = npc.getNpcId()
        id = st.getState()
        cond = st.getInt("cond")
        if id == State.COMPLETED:
            htmltext = "<html><body>This quest has already been completed.</body></html>"
        elif id == State.CREATED:
            if npcId == Benedict:
                st2 = st.getPlayer().getQuestState("24_InhabitantsOfTheForrestOfTheDead")
                if st2 and st2.getState() == State.COMPLETED and player.getLevel() >= 66 :
                    htmltext = "31349-01.htm"
                else :
                    htmltext = "31349-00.htm"
        elif id == State.STARTED:
            if npcId == Benedict:
                if cond == 1 :
                    htmltext = "31349-02.htm"
                elif cond in [2,3] :
                    htmltext = "31349-04.htm"
                elif cond == 4 :
                    htmltext = "31349-10.htm"
            elif npcId == Wizard:
                if cond == 2 :
                    htmltext = "31522-01.htm"
                    st.playSound("ItemSound.quest_middle")
                    st.set("cond","3")
                    st.giveItems(SuspiciousTotem,1)
                elif cond == 3 :
                    htmltext = "31522-02.htm"
                elif cond == 5 :
                    htmltext = "31522-03.htm"
                elif cond == 6 :
                    htmltext = "31522-04.htm"
                elif cond == 9 :
                    htmltext = "31522-05.htm"
                    st.playSound("ItemSound.quest_middle")
                    st.set("cond","10")
                elif cond == 10 :
                    htmltext = "31522-05.htm"
                elif cond == 15 :
                    htmltext = "31522-06.htm"
                elif cond == 16 :
                    htmltext = "31522-13.htm"
                elif cond == 17 :
                    htmltext = "31522-16.htm"
                elif cond == 18 :
                    htmltext = "31522-14.htm"
            elif npcId == Agripel:
                if cond == 4 :
                    htmltext = "31348-01.htm"
                elif cond == 5 :
                    htmltext = "31348-08.htm"
                elif cond == 16 :
                    htmltext = "31348-09.htm"
                elif cond == 17 :
                    htmltext = "31348-17.htm"
                elif cond == 18 :
                    htmltext = "31348-18.htm"
            elif npcId == Bookshelf:
                if cond == 6 :
                    htmltext = "31533-01.htm"
            elif npcId == Bookshelf2:
                if cond == 6 :
                    htmltext = "31534-01.htm"
            elif npcId == Bookshelf3:
                if cond in [6,7,8] :
                    htmltext = "31535-01.htm"
                elif cond == 9 :
                    htmltext = "31535-06.htm"
            elif npcId == Lidia:
                if cond == 10 :
                    htmltext = "31532-01.htm"
                elif cond in [11,12] :
                    htmltext = "31532-06.htm"
                elif cond == 13 :
                    htmltext = "31532-07.htm"
                    st.set("cond","14")
                    st.takeItems(Dress,-1)
                elif cond == 14 :
                    htmltext = "31532-08.htm"
                elif cond == 15 :
                    htmltext = "31532-18.htm"
                elif cond == 17 :
                    htmltext = "31532-19.htm"
                elif cond == 18 :
                    htmltext = "31532-21.htm"
            elif npcId == Tombstone:
                if cond in [11,12] :
                    htmltext = "31531-01.htm"
                elif cond == 13 :
                    htmltext = "31531-03.htm"
            elif npcId == Coffin:
                if cond == 12 :
                    htmltext = "31536-01.htm"
                    st.giveItems(Dress,1)
                    st.playSound("ItemSound.quest_middle")
                    st.set("cond","13")
                    npc.deleteMe()
        return htmltext

    def onKill(self,npc,player,isPet):
        st = player.getQuestState(qn)
        if not st : return
        if st.getState() != State.STARTED : return
        if st.getInt("cond") == 7:
            st.playSound("ItemSound.quest_itemget")
            st.set("cond","8")
            npc.broadcastPacket(NpcSay(npc.getObjectId(), 0, npc.getNpcId(), "You've ended my immortal life! You've protected by the feudal lord, aren't you?"))
            st.giveItems(TotemDoll,1)
            st.set("step","2")
        return

QUEST       = Quest(25,qn,"Hiding Behind The Truth")

QUEST.addStartNpc(Benedict)
QUEST.addTalkId(Agripel)
QUEST.addTalkId(Benedict)
QUEST.addTalkId(Bookshelf)
QUEST.addTalkId(Bookshelf2)
QUEST.addTalkId(Bookshelf3)
QUEST.addTalkId(Wizard)
QUEST.addTalkId(Lidia)
QUEST.addTalkId(Tombstone)
QUEST.addTalkId(Coffin)
QUEST.addKillId(Triol)