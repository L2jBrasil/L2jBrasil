### ---------------------------------------------------------------------------
###  Create by Skeleton!!!
### ---------------------------------------------------------------------------
import sys
from com.it.br.gameserver.model.quest import State
from com.it.br.gameserver.model.quest import QuestState
from com.it.br.gameserver.model.quest.jython import QuestJython as JQuest

qn = "28_ChestCaughtWithABaitOfIcyAir"

# NPC List
OFulle=31572
Kiki=31442
# ~~~
# Item List
BigYellowTreasureChest=6503
KikisLetter=7626
ElvenRing=881
# ~~~
class Quest (JQuest) :
    def __init__(self,id,name,descr): JQuest.__init__(self,id,name,descr)
    def onEvent (self,event,st) :
        htmltext=event
        if event=="31572-04.htm" :
            st.set("cond","1")
            st.playSound("ItemSound.quest_accept")
        elif event=="31572-07.htm" :
            if st.getQuestItemsCount(BigYellowTreasureChest) :
                st.set("cond","2")
                st.takeItems(BigYellowTreasureChest,1)
                st.giveItems(KikisLetter,1)
            else :
                htmltext="31572-08.htm"
        elif event=="31442-02.htm" :
            if st.getQuestItemsCount(KikisLetter)==1 :
                htmltext="31442-02.htm"
                st.takeItems(KikisLetter,-1)
                st.giveItems(ElvenRing,1)
                st.set("cond","0")
                st.setState(COMPLETED)
                st.playSound("ItemSound.quest_finish")
            else :
                htmltext="31442-03.htm"
        return htmltext

    def onTalk (self,npc,player):
        htmltext = "<html><body>You are either not carrying out your quest or don't meet the criteria.</body></html>"
        st = player.getQuestState(qn)
        if not st : return htmltext
        npcId = npc.getNpcId()
        id=st.getState()
        if id==CREATED :
            st.setState(STARTED)
            st.set("cond","0")
        cond=st.getInt("cond")
        id = st.getState()
        if npcId==OFulle :
            if cond==0 and id==STARTED:
                PlayerLevel = st.player.getLevel()
                if PlayerLevel >= 36 :
                    OFullesSpecialBait= st.player.getQuestState("51_OFullesSpecialBait")
                    if OFullesSpecialBait :
                        if OFullesSpecialBait.getState().getName() == 'Completed':
                            htmltext="31572-01.htm"
                        else :
                            htmltext="31572-02.htm"
                            st.exitQuest(1)
                    else :
                        htmltext="31572-03.htm"
                        st.exitQuest(1)
                else :
                    htmltext="31572-02.htm"
            elif cond==1 :
                htmltext="31572-05.htm"
                if st.getQuestItemsCount(BigYellowTreasureChest)==0 :
                    htmltext="31572-06.htm"
            elif cond==2 :
                htmltext="31572-09.htm"
            elif cond==0 and id==COMPLETED :
                htmltext="<html><body>This quest has already been completed.</body></html>"
        elif npcId==Kiki :
            if cond==2 :
                htmltext="31442-01.htm"
        return htmltext

QUEST      = Quest(28,qn,"Chest Caught With A Bait Of Icy Air")
CREATED    = State('Start', QUEST)
STARTED    = State('Started', QUEST)
COMPLETED  = State('Completed', QUEST)

QUEST.setInitialState(CREATED)
QUEST.addStartNpc(OFulle)
QUEST.addTalkId(OFulle)
QUEST.addTalkId(Kiki)