# Created by CubicVirtuoso
# Any problems feel free to drop by #l2j-datapack on irc.freenode.net
import sys
from com.it.br.gameserver.model.quest import State
from com.it.br.gameserver.model.quest import QuestState
from com.it.br.gameserver.model.quest.jython import QuestJython as JQuest

qn = "9_IntoTheCityOfHumans"

#NPCs 
PETUKAI = 30583 
TANAPI  = 30571 
TAMIL   = 30576 

#REWARDS  
SCROLL_OF_ESCAPE_GIRAN = 7559 
MARK_OF_TRAVELER = 7570 
 
class Quest (JQuest) :

 def __init__(self,id,name,descr): JQuest.__init__(self,id,name,descr) 

 def onEvent (self,event,st) : 
   htmltext = event 
   if event == "30583-03.htm" : 
     st.set("cond","1") 
     st.set("id","1") 
     st.setState(STARTED) 
     st.playSound("ItemSound.quest_accept") 
   elif event == "30571-02.htm" : 
     st.set("cond","2") 
     st.set("id","2") 
     st.playSound("ItemSound.quest_middle") 
   elif event == "30576-02.htm" :
     st.giveItems(MARK_OF_TRAVELER, 1)
     st.giveItems(SCROLL_OF_ESCAPE_GIRAN,1) 
     st.set("cond","0") 
     st.setState(COMPLETED) 
     st.playSound("ItemSound.quest_finish") 
   return htmltext 

 def onTalk (self,npc,player): 
   htmltext = "<html><body>You are either not carrying out your quest or don't meet the criteria.</body></html>" 
   st = player.getQuestState(qn)
   if not st : return htmltext
   npcId = npc.getNpcId() 
   cond  = st.getInt("cond") 
   id    = st.getState() 

   if id == CREATED : 
     st.set("cond","0") 
     if player.getRace().ordinal() == 3 : 
       if player.getLevel() >= 3 and player.getLevel() <= 10 : 
         htmltext = "30583-02.htm" 
       else: 
         htmltext = "<html><body>Quest for characters level 3 and above.</body></html>" 
         st.exitQuest(1) 
     else : 
       htmltext = "30583-01.htm" 
       st.exitQuest(1) 
   elif npc == PETUKAI and id == COMPLETED : 
     htmltext = "<html><body>I can't supply you with another Giran Scroll of Escape. Sorry traveller.</body></html>" 
   elif npc == PETUKAI and cond == 1 : 
     htmltext = "30583-04.htm"
   if id == STARTED :  
       if npcId == TANAPI and cond : 
         htmltext = "30571-01.htm" 
       elif npcId == TAMIL and cond == 2 : 
         htmltext = "30576-01.htm" 
   return htmltext 

QUEST     = Quest(9,qn,"Into the City of Humans") 
CREATED   = State('Start',     QUEST) 
STARTED   = State('Started',   QUEST) 
COMPLETED = State('Completed', QUEST) 
 
QUEST.setInitialState(CREATED)
QUEST.addStartNpc(PETUKAI) 

QUEST.addTalkId(PETUKAI) 

QUEST.addTalkId(TANAPI) 
QUEST.addTalkId(TAMIL) 