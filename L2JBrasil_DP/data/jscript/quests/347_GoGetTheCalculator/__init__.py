# Made by Fulminus
# Quest 347: Go Get The Calculator.

qn = "347_GoGetTheCalculator"

import sys
from com.it.br.gameserver.model.quest import State
from com.it.br.gameserver.model.quest import QuestState
from com.it.br.gameserver.model.quest.jython import QuestJython as JQuest

# NPCs to talk to
BRUNON = 30526
SILVERA = 30527
SPIRON = 30532
BALANKI = 30533

# MOBs to kill
GEMSTONE_BEAST = 20540    # drops "gemstone beast's crystal" at 50% chance

# quest items
GEMSTONE_BEAST_CRYSTAL = 4286
ADENA = 57
CALCULATOR_Q = 4285
CALCULATOR = 4393

class Quest (JQuest) :

 def __init__(self,id,name,descr): JQuest.__init__(self,id,name,descr)

 def onEvent (self,event,st) :
    htmltext = event
    if event == "1" :
        st.set("id","0")
        st.set("cond","1")
        st.setState(STARTED)
        st.playSound("ItemSound.quest_accept")
        htmltext = str(BRUNON)+"-02.htm"
    elif event == "30533_1" :
        if st.getQuestItemsCount(ADENA)>100 :
            st.takeItems(ADENA, 100)
            if st.getInt("cond")== 1:
                st.set("cond","2")
            else :
                st.set("cond","4")
            htmltext = str(BALANKI)+"-02.htm"
        else :
            htmltext = str(BALANKI)+"-03.htm"
    elif event == "30532_1" :
        htmltext = str(SPIRON)+"-02a.htm"
        if st.getInt("cond")== 1:
            st.set("cond","3")
        else :
            st.set("cond","4")
    elif event == "30532_2" :
        htmltext = str(SPIRON)+"-02b.htm"
    elif event == "30532_3" :
        htmltext = str(SPIRON)+"-02c.htm"
    elif event == "30526_1" :
        st.giveItems(CALCULATOR,1)
        st.takeItems(CALCULATOR_Q,1)
        st.playSound("ItemSound.quest_middle")
        st.setState(COMPLETED)
        st.set("cond","0")
        st.exitQuest(1)
        htmltext = str(BRUNON)+"-05.htm"
    elif event == "30526_2" :
        st.giveItems(ADENA,1000)
        st.takeItems(CALCULATOR_Q,1)
        st.playSound("ItemSound.quest_middle")
        st.setState(COMPLETED)
        st.set("cond","0")
        st.exitQuest(1)
        htmltext = str(BRUNON)+"-06.htm"
    return htmltext


 def onTalk (self,npc,player):
    htmltext = "<html><body>You are either not carrying out your quest or don't meet the criteria.</body></html>"
    st = player.getQuestState(qn)
    if not st : return htmltext

    npcId = npc.getNpcId()
    id = st.getState()
    if npcId != BRUNON and id != STARTED : return htmltext

    if npcId == BRUNON and id==CREATED :
        st.set("id","0")
        st.set("cond","0")
        htmltext = str(BRUNON)+"-01.htm"
    elif npcId == BRUNON and st.getInt("cond")>0 and st.getQuestItemsCount(CALCULATOR_Q)==0 :
        htmltext = str(BRUNON)+"-03.htm"
    elif npcId == BALANKI and (st.getInt("cond")==1 or st.getInt("cond")==3):
        htmltext = str(BALANKI)+"-01.htm"
    elif npcId == SPIRON and (st.getInt("cond")==1 or st.getInt("cond")==2) :
        htmltext = str(SPIRON)+"-01.htm"
    elif npcId == SILVERA and st.getInt("cond")==4 :
        st.set("cond","5")
        htmltext = str(SILVERA)+"-01.htm"
    elif npcId == SILVERA and st.getInt("cond")==5 and st.getQuestItemsCount(GEMSTONE_BEAST_CRYSTAL)<10 :
        htmltext = str(SILVERA)+"-02.htm"
    elif npcId == SILVERA and st.getInt("cond")==5 and st.getQuestItemsCount(GEMSTONE_BEAST_CRYSTAL)==10 :
        htmltext = str(SILVERA)+"-03.htm"
        st.takeItems(GEMSTONE_BEAST_CRYSTAL,10)
        st.giveItems(CALCULATOR_Q,1)
        st.playSound("ItemSound.quest_itemget")
        st.set("cond","6")
    elif npcId == BRUNON and st.getInt("cond")==6 and st.getQuestItemsCount(CALCULATOR_Q)==1 :
        htmltext = str(BRUNON)+"-04.htm"
    return htmltext

 def onKill(self,npc,player,isPet):
   st = player.getQuestState(qn)
   if not st : return 
   if st.getState() != STARTED : return 
   
   npcId = npc.getNpcId()
   if npcId == GEMSTONE_BEAST and st.getInt("cond")==5 and st.getRandom(2)==1 and st.getQuestItemsCount(GEMSTONE_BEAST_CRYSTAL)<10 :
        st.giveItems(GEMSTONE_BEAST_CRYSTAL,1)
        if st.getQuestItemsCount(GEMSTONE_BEAST_CRYSTAL) == 10 :
            st.playSound("ItemSound.quest_middle")
        else:
            st.playSound("ItemSound.quest_itemget")
   return

QUEST       = Quest(347,qn,"Calculator")
CREATED     = State('Start', QUEST)
STARTED     = State('Started', QUEST)
COMPLETED   = State('Completed', QUEST)


QUEST.setInitialState(CREATED)
QUEST.addStartNpc(BRUNON)

QUEST.addTalkId(BRUNON)

QUEST.addTalkId(SILVERA)
QUEST.addTalkId(SPIRON)
QUEST.addTalkId(BALANKI)

QUEST.addKillId(GEMSTONE_BEAST)