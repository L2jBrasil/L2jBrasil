### ---------------------------------------------------------------------------
###  Create by Skeleton!!!
### ---------------------------------------------------------------------------
import sys
from com.it.br.gameserver.model.quest import State
from com.it.br.gameserver.model.quest import QuestState
from com.it.br.gameserver.model.quest.jython import QuestJython as JQuest

qn = "29_ChestCaughtWithABaitOfEarth"

# NPC List
Willie =31574
Anabel =30909
# ~~~
# Item List
SmallPurpleTreasureChest =6507
SmallGlassBox =7627
PlatedLeatherGloves =2455
# ~~~
class Quest (JQuest) :
    def __init__(self,id,name,descr): JQuest.__init__(self,id,name,descr)
    def onEvent (self,event,st) :
        htmltext =event
        if event =="31574-04.htm" :
            st.set("cond","1")
            st.playSound("ItemSound.quest_accept")
        elif event=="31574-07.htm" :
            if st.getQuestItemsCount(SmallPurpleTreasureChest) :
                st.set("cond","2")
                st.takeItems(SmallPurpleTreasureChest,1)
                st.giveItems(SmallGlassBox,1)
            else :
                htmltext="31574-08.htm"
        elif event =="30909-02.htm" :
            if st.getQuestItemsCount(SmallGlassBox)==1 :
                st.takeItems(SmallGlassBox,-1)
                st.giveItems(PlatedLeatherGloves,1)
                st.set("cond","0")
                st.setState(COMPLETED)
                st.playSound("ItemSound.quest_finish")
            else :
                htmltext ="30909-03.htm"
        return htmltext

    def onTalk (self,npc,player):
        htmltext = "<html><body>You are either not carrying out your quest or don't meet the criteria.</body></html>"
        st = player.getQuestState(qn)
        if not st : return htmltext
        npcId = npc.getNpcId()
        id = st.getState()
        if id==CREATED :
            st.setState(STARTED)
            st.set("cond","0")
        cond=st.getInt("cond")
        id = st.getState()
        if npcId ==Willie :
            if cond==0 and id==STARTED :
                PlayerLevel = player.getLevel()
                if PlayerLevel >= 48 :
                    WilliesSpecialBait = player.getQuestState("52_WilliesSpecialBait")
                    if WilliesSpecialBait:
                        if WilliesSpecialBait.getState().getName() == 'Completed':
                            htmltext="31574-01.htm"
                        else :
                            htmltext="31574-02.htm"
                            st.exitQuest(1)
                    else :
                        htmltext="31574-03.htm"
                        st.exitQuest(1)
                else :
                   htmltext="31574-02.htm"
                   st.exitQuest(1) 
            elif cond==0 and id==COMPLETED :
                htmltext ="<html><body>This quest has already been completed.</body></html>"
            elif cond==1 :
                htmltext="31574-05.htm"
                if st.getQuestItemsCount(SmallPurpleTreasureChest)==0 :
                    htmltext ="31574-06.htm"
            elif cond==2 :
                htmltext="31574-09.htm"
        elif npcId ==Anabel :
            if cond==2 :
                htmltext="30909-01.htm"
        return htmltext

QUEST       = Quest(29,qn,"Chest Caught With A Bait Of Earth")
CREATED     = State('Start', QUEST)
STARTED     = State('Started', QUEST)
COMPLETED   = State('Completed', QUEST)

QUEST.setInitialState(CREATED)
QUEST.addStartNpc(Willie)
QUEST.addTalkId(Willie)
QUEST.addTalkId(Anabel)