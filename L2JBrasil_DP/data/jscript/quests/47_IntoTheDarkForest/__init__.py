# Created by CubicVirtuoso
# Any problems feel free to drop by #l2j-datapack on irc.freenode.net
import sys
from com.it.br.gameserver.model.quest import State
from com.it.br.gameserver.model.quest import QuestState
from com.it.br.gameserver.model.quest.jython import QuestJython as JQuest

qn = "47_IntoTheDarkForest"

TRADER_GALLADUCCI_ID = 30097
GALLADUCCIS_ORDER_DOCUMENT_ID_1 = 7563
GALLADUCCIS_ORDER_DOCUMENT_ID_2 = 7564
GALLADUCCIS_ORDER_DOCUMENT_ID_3 = 7565
MAGIC_TRADER_GENTLER_ID = 30094
MAGIC_SWORD_HILT_ID = 7568
JEWELER_SANDRA_ID = 30090
GEMSTONE_POWDER_ID = 7567
PRIEST_DUSTIN_ID = 30116
PURIFIED_MAGIC_NECKLACE_ID = 7566
MARK_OF_TRAVELER_ID = 7570
SCROLL_OF_ESCAPE_SPECIAL = 7556
ADENA_ID = 57
RACE = 2

class Quest (JQuest) :

    def __init__(self,id,name,descr): JQuest.__init__(self,id,name,descr)

    def onEvent (self,event,st) :
        htmltext = event
        if event == "1" :
            st.set("cond","1")
            st.setState(STARTED)
            st.playSound("ItemSound.quest_accept")
            st.giveItems(GALLADUCCIS_ORDER_DOCUMENT_ID_1,1)
            htmltext = "30097-03.htm"
        elif event == "2" :
            st.set("cond","2")
            st.takeItems(GALLADUCCIS_ORDER_DOCUMENT_ID_1,1)
            st.giveItems(MAGIC_SWORD_HILT_ID,1)
            htmltext = "30094-02.htm"
        elif event == "3" :
            st.set("cond","3")
            st.takeItems(MAGIC_SWORD_HILT_ID,1)
            st.giveItems(GALLADUCCIS_ORDER_DOCUMENT_ID_2,1)
            htmltext = "30097-06.htm"
        elif event == "4" :
            st.set("cond","4")
            st.takeItems(GALLADUCCIS_ORDER_DOCUMENT_ID_2,1)
            st.giveItems(GEMSTONE_POWDER_ID,1)
            htmltext = "30090-02.htm"
        elif event == "5" :
            st.set("cond","5")
            st.takeItems(GEMSTONE_POWDER_ID,1)
            st.giveItems(GALLADUCCIS_ORDER_DOCUMENT_ID_3,1)
            htmltext = "30097-09.htm"
        elif event == "6" :
            st.set("cond","6")
            st.takeItems(GALLADUCCIS_ORDER_DOCUMENT_ID_3,1)
            st.giveItems(PURIFIED_MAGIC_NECKLACE_ID,1)
            htmltext = "30116-02.htm"
        elif event == "7" :
            st.giveItems(SCROLL_OF_ESCAPE_SPECIAL,1)
            st.takeItems(PURIFIED_MAGIC_NECKLACE_ID,1)
            st.takeItems(MARK_OF_TRAVELER_ID,-1)
            htmltext = "30097-12.htm"
            st.unset("cond")
            st.setState(COMPLETED)
            st.playSound("ItemSound.quest_finish")
        return htmltext

    def onTalk (self,npc,player):
        htmltext = "<html><body>You are either not carrying out your quest or don't meet the criteria.</body></html>"
        st = player.getQuestState(qn)
        if not st : return htmltext
        npcId = npc.getNpcId()
        id = st.getState()
        if id == CREATED :
            st.set("cond","0")
            if player.getRace().ordinal() == RACE and st.getQuestItemsCount(MARK_OF_TRAVELER_ID) > 0:
                htmltext = "30097-02.htm"
            else :
                htmltext = "30097-01.htm"
                st.exitQuest(1)
        elif npcId == 30097 and id == COMPLETED :
            htmltext = "<html><body>I can't supply you with another Scroll of Escape. Sorry traveller.</body></html>"
        elif npcId == 30097 and st.getInt("cond")==1 :
            htmltext = "30097-04.htm"
        elif npcId == 30097 and st.getInt("cond")==2 :
            htmltext = "30097-05.htm"
        elif npcId == 30097 and st.getInt("cond")==3 :
            htmltext = "30097-07.htm"
        elif npcId == 30097 and st.getInt("cond")==4 :
            htmltext = "30097-08.htm"
        elif npcId == 30097 and st.getInt("cond")==5 :
            htmltext = "30097-10.htm"
        elif npcId == 30097 and st.getInt("cond")==6 :
            htmltext = "30097-11.htm"
        elif id == STARTED :    
            if npcId == 30094 and st.getInt("cond")==1 :
                htmltext = "30094-01.htm"
            elif npcId == 30094 and st.getInt("cond")==2 :
                htmltext = "30094-03.htm"
            elif npcId == 30090 and st.getInt("cond")==3 :
                htmltext = "30090-01.htm"
            elif npcId == 30090 and st.getInt("cond")==4 :
                htmltext = "30090-03.htm"
            elif npcId == 30116 and st.getInt("cond")==5 :
                htmltext = "30116-01.htm"
            elif npcId == 30116 and st.getInt("cond")==6 :
                htmltext = "30116-03.htm"

        return htmltext

QUEST       = Quest(47,qn,"Into The Dark Forest")
CREATED     = State('Start', QUEST)
STARTED     = State('Started', QUEST)
COMPLETED   = State('Completed', QUEST)

QUEST.setInitialState(CREATED)
QUEST.addStartNpc(30097)

QUEST.addTalkId(30097)

QUEST.addTalkId(30094)
QUEST.addTalkId(30090)
QUEST.addTalkId(30116)