# Made by Kerberos v1.0 on 2009/05/08
# this script is part of the Official L2J Datapack Project.
# Visit http://www.l2jdp.com/forum for more details.
import sys

from com.it.br import Config
from com.it.br.gameserver.model.quest import State
from com.it.br.gameserver.model.quest import QuestState
from com.it.br.gameserver.model.quest.jython import QuestJython as JQuest 

qn = "640_TheZeroHour"

#NPC
Kahman = 31554

#MONSTERS
MONSTERS = range(22105,22112)+range(22113,22120)+[22121]

#ITEMS
Fang = 8085

REWARDS={
    "1":[12 ,4042, 1],
    "2":[6  ,4043, 1],
    "3":[6  ,4044, 1],
    "4":[81 ,1887,10],
    "5":[33 ,1888, 5],
    "6":[30 ,1889,10],
    "7":[150,5550,10],
    "8":[131,1890,10],
    "9":[123,1893, 5],
    }

class Quest (JQuest) :

    def __init__(self,id,name,descr):
        JQuest.__init__(self,id,name,descr)
        self.questItemIds = [Fang]

    def onAdvEvent (self,event,npc, player) :
        htmltext = event
        st = player.getQuestState(qn)
        if not st : return
        if event == "31554-02.htm" :
            st.set("cond","1")
            st.setState(STARTED)
            st.playSound("ItemSound.quest_accept")
        elif event == "31554-08.htm" :
            st.playSound("ItemSound.quest_finish")
            st.exitQuest(True)
        elif event in REWARDS.keys() :
            cost,item,amount = REWARDS[event]
            if st.getQuestItemsCount(Fang)>=cost :
                st.takeItems(Fang,cost)
                st.giveItems(item, amount)
                htmltext = "31554-09.htm"
            else :
                htmltext = "31554-06.htm"
        return htmltext

    def onTalk (self, npc, player) :
        htmltext = "<html><body>You are either not on a quest that involves this NPC, or you don't meet this NPC's minimum quest requirements.</body></html>"
        st = player.getQuestState(qn)
        if not st : return htmltext

        id = st.getState()
        if id == CREATED :
            if player.getLevel() >= 66 :
                st2 = player.getQuestState("109_InSearchOfTheNest")
                if st2 and st2.getState().getName() == 'Completed' :
                    htmltext = "31554-01.htm"
                else :
                    htmltext = "31554-00.htm" #todo: missing retail html
            else :
                htmltext = "31554-00.htm" 
        elif st.getQuestItemsCount(Fang) >= 1 :
            htmltext = "31554-04.htm"
        else :
            htmltext = "31554-03.htm"
        return htmltext

    def onKill(self, npc, player, isPet) :
        st = player.getQuestState(qn)
        if not st : return
        st.giveItems(Fang,int(Config.RATE_DROP_QUEST))
        st.playSound("ItemSound.quest_itemget")
        return

QUEST       = Quest(640,qn,"The Zero Hour")
CREATED     = State('Start', QUEST)
STARTED     = State('Started', QUEST)
COMPLETED   = State('Completed', QUEST)

QUEST.setInitialState(CREATED)

QUEST.addStartNpc(Kahman)
QUEST.addTalkId(Kahman)

for i in MONSTERS :
    QUEST.addKillId(i)