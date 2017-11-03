# by disKret
import sys
from com.it.br.gameserver.model.quest import State
from com.it.br.gameserver.model.quest import QuestState
from com.it.br.gameserver.model.quest.jython import QuestJython as JQuest

qn = "31_SecretBuriedInTheSwamp"

#NPC
ABERCROMBIE = 31555
FORGOTTEN_MONUMENT_1,FORGOTTEN_MONUMENT_2,FORGOTTEN_MONUMENT_3,FORGOTTEN_MONUMENT_4,CORPSE_OF_DWARF = range(31661,31666)
#ITEMS
KRORINS_JOURNAL = 7252
#MESSAGES
default = "<html><body>You are either not carrying out your quest or don't meet the criteria.</body></html>"
class Quest (JQuest) :

 def __init__(self,id,name,descr): JQuest.__init__(self,id,name,descr)

 def onEvent (self,event,st) :
   id = st.getState()
   cond = st.getInt("cond")
   htmltext = event
   if event == "31555-1.htm" and id == CREATED:
     st.set("cond","1")
     st.setState(STARTED)
     st.playSound("ItemSound.quest_accept")
   elif event == "31665-1.htm" and cond == 1:
     st.set("cond","2")
     st.playSound("ItemSound.quest_itemget")
     st.giveItems(KRORINS_JOURNAL,1)
   elif event == "31555-4.htm" and cond == 2:
     st.set("cond","3")
   elif event == "31661-1.htm" and cond == 3:
     st.set("cond","4")
   elif event == "31662-1.htm" and cond == 4:
     st.set("cond","5")
   elif event == "31663-1.htm" and cond == 5:
     st.set("cond","6")
   elif event == "31664-1.htm" and cond == 6:
     st.set("cond","7")
     st.playSound("ItemSound.quest_middle")
   elif event == "31555-7.htm" and cond == 7:
     st.takeItems(KRORINS_JOURNAL,-1)
     st.addExpAndSp(130000,0)
     st.giveItems(57,40000)
     st.playSound("ItemSound.quest_finish")
     st.setState(COMPLETED)
   elif event != "31663-0a.htm":
     htmltext = default
   return htmltext

 def onTalk (self,npc,player):
   htmltext = default
   st = player.getQuestState(qn)
   if not st : return htmltext
   
   npcId = npc.getNpcId()
   id = st.getState()
   cond = st.getInt("cond")
   if id == COMPLETED :
     htmltext = "<html><body>This quest has already been completed.</body></html>"
   elif npcId == ABERCROMBIE :
     if cond == 0 :
       if player.getLevel() >= 66 :
         htmltext = "31555-0.htm"
       else :
         htmltext = "31555-0a.htm"
         st.exitQuest(1)
     elif cond == 1 :
       htmltext = "31555-2.htm"
     elif cond == 2 :
       htmltext = "31555-3.htm"
     elif cond == 3 :
       htmltext = "31555-5.htm"
     elif cond == 7 :
       htmltext = "31555-6.htm"
   elif id == STARTED : 
       if npcId == CORPSE_OF_DWARF :
         if cond == 1 :
           htmltext = "31665-0.htm"
         elif cond == 2 :
           htmltext = "31665-2.htm"
       elif npcId == FORGOTTEN_MONUMENT_1 :
         if cond == 3 :
           htmltext = "31661-0.htm"
         elif cond > 3 :
           htmltext = "31661-2.htm"
       elif npcId == FORGOTTEN_MONUMENT_2:
         if cond == 4 :
           htmltext = "31662-0.htm"
         elif cond > 4 :
           htmltext = "31662-2.htm"
       elif npcId == FORGOTTEN_MONUMENT_3 :
         if cond == 5 :
           htmltext = "31663-0.htm"
         elif cond > 5 :
           htmltext = "31663-2.htm"
       elif npcId == FORGOTTEN_MONUMENT_4 :
         if cond == 6 :
           htmltext = "31664-0.htm"
         elif cond > 6 :
           htmltext = "31664-2.htm"
   return htmltext

QUEST       = Quest(31,qn,"Secret Buried In The Swamp")
CREATED     = State('Start', QUEST)
STARTED     = State('Started', QUEST)
COMPLETED   = State('Completed', QUEST)

QUEST.setInitialState(CREATED)
QUEST.addStartNpc(ABERCROMBIE)

QUEST.addTalkId(ABERCROMBIE)

for i in range(31661,31666):
    QUEST.addTalkId(i)