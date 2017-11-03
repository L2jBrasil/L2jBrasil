# Created by CubicVirtuoso
# Any problems feel free to drop by #l2j-datapack on irc.freenode.net
import sys
from com.it.br.gameserver.model.quest import State
from com.it.br.gameserver.model.quest import QuestState
from com.it.br.gameserver.model.quest.jython import QuestJython as JQuest

qn = "10_IntoTheWorld"

VERY_EXPENSIVE_NECKLACE = 7574
SCROLL_OF_ESCAPE_GIRAN = 7559
MARK_OF_TRAVELER = 7570

class Quest (JQuest) :

    def __init__(self,id,name,descr): 
        JQuest.__init__(self,id,name,descr)
        self.questItemIds = [VERY_EXPENSIVE_NECKLACE]
        
    def onEvent (self,event,st) :
        htmltext = event
        if event == "30533-03.htm" :
            st.set("cond","1")
            st.setState(STARTED)
            st.playSound(self.SOUND_QUEST_START)
        elif event == "30520-02.htm" :
            st.set("cond","2")
            st.giveItems(VERY_EXPENSIVE_NECKLACE,1)
        elif event == "30650-02.htm" :
            st.set("cond","3")
            st.takeItems(VERY_EXPENSIVE_NECKLACE,1)
        elif event == "30533-06.htm" :
            st.giveItems(SCROLL_OF_ESCAPE_GIRAN,1)
            st.giveItems(MARK_OF_TRAVELER, 1)
            st.unset("cond")
            st.setState(COMPLETED)
            st.playSound(self.SOUND_QUEST_DONE)
        return htmltext

    def onTalk (self,npc,player):
        npcId = npc.getNpcId()
        htmltext = self.NO_QUEST 
        st = player.getQuestState(qn)
        if not st: return htmltext
        id = st.getState()
        if id == CREATED :
            st.set("cond","0")
            if player.getRace().ordinal() == 4 :
                htmltext = "30533-02.htm"
            else :
                htmltext = "30533-01.htm"
                st.exitQuest(1)
        elif npcId == 30533 and id == COMPLETED :
            htmltext = "<html><body>I can't supply you with another Giran Scroll of Escape. Sorry traveller.</body></html>"
        elif id == STARTED: 
            if npcId == 30533 and st.getInt("cond")==1 :
                htmltext = "30533-04.htm"
            elif npcId == 30520 and st.getInt("cond") == 3 :
                htmltext = "30520-04.htm"
                st.set("cond","4")
            elif npcId == 30520 and st.getInt("cond") :
                if st.getQuestItemsCount(VERY_EXPENSIVE_NECKLACE) == 0 :
                    htmltext = "30520-01.htm"
                else :
                    htmltext = "30520-03.htm"
            elif npcId == 30650 and st.getInt("cond")==2 :
                if st.getQuestItemsCount(VERY_EXPENSIVE_NECKLACE) :
                    htmltext = "30650-01.htm"
            elif npcId == 30533 and st.getInt("cond")==4 :
                htmltext = "30533-05.htm"
    	return htmltext

QUEST       = Quest(10,qn,"Into The World")
CREATED     = State('Start', QUEST)
STARTED     = State('Started', QUEST)
COMPLETED   = State('Completed', QUEST)

QUEST.setInitialState(CREATED)
QUEST.addStartNpc(30533)

QUEST.addTalkId(30533)
QUEST.addTalkId(30520)
QUEST.addTalkId(30650)