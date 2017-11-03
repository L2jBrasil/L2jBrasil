# Made by Mr. Have fun! Version 0.2
# fixed by Elektra and Rolarga Version 0.3
# fixed by Mr and Drlecter
import sys
from com.it.br.gameserver.model.quest import State
from com.it.br.gameserver.model.quest import QuestState
from com.it.br.gameserver.model.quest.jython import QuestJython as JQuest

qn = "364_JovialAccordion"

KEY_1 = 4323
KEY_2 = 4324
BEER = 4321
ECHO = 4421

class Quest (JQuest) :

 def __init__(self,id,name,descr): JQuest.__init__(self,id,name,descr)

 def onEvent (self,event,st) :
    htmltext = event
    if event == "30959-02.htm" :
        st.set("cond","1")
        st.setState(STARTED)
        st.playSound("ItemSound.quest_accept")
    elif event == "30957-02.htm" :
        st.set("cond","2")
        st.giveItems(KEY_1,1)
        st.giveItems(KEY_2,1)
    elif event == "30961-03.htm" :
      if st.getQuestItemsCount(KEY_1) :
        st.takeItems(KEY_1,1)
        htmltext = "30961-02.htm"
    elif event == "30960-03.htm" :
      if st.getQuestItemsCount(KEY_2) :
        st.takeItems(KEY_2,1)
        st.giveItems(BEER,1)
        htmltext = "30960-02.htm"
    return htmltext

 def onTalk (self,npc,player):
   htmltext = "<html><body>You are either not carrying out your quest or don't meet the criteria.</body></html>"
   st = player.getQuestState(qn)
   if not st : return htmltext

   npcId = npc.getNpcId()
   id = st.getState()
   if npcId != 30959 and id != STARTED : return htmltext

   if id == CREATED :
     st.set("cond","0")
     st.set("ok","0")
   cond=st.getInt("cond")
   if npcId == 30959 :
     if cond == 0 :
        htmltext = "30959-01.htm"
     elif cond == 3 :
        st.playSound("ItemSound.quest_finish")
        st.giveItems(ECHO,1)
        st.exitQuest(1)
        htmltext = "30959-03.htm"
     elif cond >= 1 :
        htmltext = "30959-02.htm"
   elif npcId == 30957 :
     if cond == 1 :
        htmltext = "30957-01.htm"
     elif cond == 2 and not st.getQuestItemsCount(KEY_1) and st.getInt("ok"):
        st.set("cond","3")
        htmltext = "30957-04.htm"
     elif cond == 3 :
        htmltext = "30957-05.htm"
     elif cond == 2 :
        htmltext = "30957-03.htm"
   elif npcId == 30960 and cond :
        htmltext = "30960-01.htm"
   elif npcId == 30961 and cond :
        htmltext = "30961-01.htm"
   elif npcId == 30060 and st.getQuestItemsCount(BEER) :
        st.set("ok","1")
        st.takeItems(BEER,1)
        htmltext = "30060-01.htm"
   return htmltext

QUEST       = Quest(364,qn,"Ask What You Need to Do")
CREATED     = State('Start', QUEST)
STARTED     = State('Started', QUEST)
COMPLETED   = State('Completed', QUEST)


QUEST.setInitialState(CREATED)
QUEST.addStartNpc(30959)

for npcId in [30959,30957,30060,30961,30960]:
  QUEST.addTalkId(npcId)