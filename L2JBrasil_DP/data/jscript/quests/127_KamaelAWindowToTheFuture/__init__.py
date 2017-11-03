# Contributed by t0rm3nt0r

import sys
from com.it.br.gameserver.model.quest import State
from com.it.br.gameserver.model.quest import QuestState
from com.it.br.gameserver.model.quest.jython import QuestJython as JQuest
from com.it.br.gameserver.network.serverpackets import ExShowSlideshowKamael

qn = "127_KamaelAWindowToTheFuture"

AKLAN = 31288
ALDER = 32092
DOMINIC = 31350
JURIS = 30113
KLAUS = 30187
OLTLIN = 30862
RODEMAI = 30756

MARK_DOMINIC = 8939
MARK_HUMAN = 8940
MARK_DWARF = 8941
MARK_ELF = 8942
MARK_DELF = 8943
MARK_ORC = 8944

class Quest (JQuest) :

 def __init__(self,id,name,descr): JQuest.__init__(self,id,name,descr)
 
 def onEvent (self,event,st) :
     htmltext = event
     if event == "dominic4.htm" :
         st.set("cond","1")
         st.setState(STARTED)
         st.giveItems(MARK_DOMINIC,1)
         st.playSound("ItemSound.quest_accept")
     elif event == "dominic6.htm" :
         st.takeItems(MARK_HUMAN,-1)
         st.takeItems(MARK_DWARF,-1)
         st.takeItems(MARK_ELF,-1)
         st.takeItems(MARK_DELF,-1)
         st.takeItems(MARK_ORC,-1)
         st.takeItems(MARK_DOMINIC,-1)
         st.giveItems(57,159100)
         st.unset("cond")
         st.playSound("ItemSound.quest_finish")
         st.setState(COMPLETED)
     elif event == "klaus6.htm" :
         st.set("cond","2")
     elif event == "klaus8.htm" :
         st.set("cond","3")
         st.setState(STARTED)
         st.giveItems(MARK_HUMAN,1)
         st.playSound("ItemSound.quest_accept") 
     elif event == "alder5.htm" :
         st.set("cond","4")
         st.setState(STARTED)
         st.giveItems(MARK_DWARF,1)
         st.playSound("ItemSound.quest_accept")
     elif event == "aklan4.htm" :
         st.set("cond","5")
         st.setState(STARTED)
         st.giveItems(MARK_ORC,1)
         st.playSound("ItemSound.quest_accept")
     elif event == "oltlin4.htm" :
         st.set("cond","6")
         st.setState(STARTED)
         st.giveItems(MARK_DELF,1)
         st.playSound("ItemSound.quest_accept")
     elif event == "juris4.htm" :
         st.set("cond","7")
         st.setState(STARTED)
         st.giveItems(MARK_ELF,1)
         st.playSound("ItemSound.quest_accept")
     elif event == "kamaelstory" :
         st.set("cond","8")
         st.getPlayer().sendPacket(ExShowSlideshowKamael())
         return
     elif event == "rodemai5.htm" :
         st.set("cond","9")
         st.playSound("ItemSound.quest_accept")
     return htmltext

 def onTalk (self,npc,player):
     npcId = npc.getNpcId()
     htmltext = "<html><body>You are either not carrying out your quest or don't meet the criteria.</body></html>"
     st = player.getQuestState(qn)
     if not st : return htmltext
     id = st.getState()
     cond = st.getInt("cond")
     if id == COMPLETED :
       htmltext = "<html><body>This quest has already been completed.</body></html>"
     elif id == CREATED and npcId == DOMINIC :
       htmltext = "dominic1.htm"
     elif id == STARTED :
       if npcId == KLAUS and cond == 1 :
         htmltext = "klaus1.htm"
       elif npcId == KLAUS and cond == 2 :
         htmltext = "klaus6.htm"
       elif npcId == ALDER and cond == 3 :
         htmltext = "alder1.htm"
       elif npcId == AKLAN and cond == 4 :
         htmltext = "aklan1.htm"
       elif npcId == OLTLIN and cond == 5 :
         htmltext = "oltlin1.htm"
       elif npcId == JURIS and cond == 6 :
         htmltext = "juris1.htm"
       elif npcId == RODEMAI and cond == 7 :
         htmltext = "rodemai1.htm"
       elif npcId == RODEMAI and cond == 8 :
         htmltext = "rodemai4.htm"
       elif npcId == DOMINIC and cond == 9 :
         htmltext = "dominic5.htm"
     return htmltext

QUEST       = Quest(127, qn, "Kamael A Window To The Future")
CREATED     = State('Start', QUEST)
STARTED     = State('Started', QUEST)
COMPLETED   = State('Completed', QUEST)

QUEST.setInitialState(CREATED)
QUEST.addStartNpc(DOMINIC)

QUEST.addTalkId(DOMINIC)
QUEST.addTalkId(KLAUS)
QUEST.addTalkId(ALDER)
QUEST.addTalkId(AKLAN)
QUEST.addTalkId(OLTLIN)
QUEST.addTalkId(JURIS)
QUEST.addTalkId(RODEMAI)