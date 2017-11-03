# Created by CubicVirtuoso
# Any problems feel free to drop by #l2j-datapack on irc.freenode.net
import sys
from com.it.br import Config 
from com.it.br.gameserver.model.quest import State
from com.it.br.gameserver.model.quest import QuestState
from com.it.br.gameserver.model.quest.jython import QuestJython as JQuest

qn = "432_BirthdayPartySong"

MELODY_MAESTRO_OCTAVIA_ID = 31043
RED_CRYSTALS_ID = 7541
ROUGH_HEWN_ROCK_GOLEMS_ID = 21103
BIRTHDAY_ECHO_CRYSTAL_ID = 7061

class Quest (JQuest) :

 def __init__(self,id,name,descr): JQuest.__init__(self,id,name,descr)
 
 def onEvent (self,event,st) :
     htmltext = event
     cond = st.getInt("cond")
     if event == "1" and cond == 0 :
         htmltext = "31043-02.htm"
         st.set("cond","1")
         st.setState(STARTED)
         st.playSound("ItemSound.quest_accept")
     elif event == "3" and st.getQuestItemsCount(RED_CRYSTALS_ID) == 50 and cond == 2 :
         st.giveItems(BIRTHDAY_ECHO_CRYSTAL_ID,25)
         st.takeItems(RED_CRYSTALS_ID,50)
         htmltext = "31043-05.htm"
         st.exitQuest(1)
         st.playSound("ItemSound.quest_finish")
     return htmltext
 
 def onTalk (self,npc,player):
     htmltext = "<html><body>You are either not carrying out your quest or don't meet the criteria.</body></html>"
     st = player.getQuestState(qn)
     if not st : return htmltext

     npcId = npc.getNpcId()
     id = st.getState()
     cond = st.getInt("cond")
     if id == CREATED :
         htmltext = "31043-01.htm"
     elif cond ==1 :
         htmltext = "31043-03.htm"
     elif cond == 2 :
         htmltext = "31043-04.htm"
     return htmltext
 
 def onKill(self,npc,player,isPet):
     st = player.getQuestState(qn)
     if not st : return 
     if st.getState() != STARTED : return 
     if st.getInt("cond") == 1 :
             numItems, chance = divmod(100*Config.RATE_DROP_QUEST,100)
             if st.getRandom(100) < chance :
                 numItems = numItems + 1
             count = st.getQuestItemsCount(RED_CRYSTALS_ID)
             if count + numItems >= 50:
                 numItems = 50 - count
                 if numItems != 0 :    
                     st.playSound("ItemSound.quest_middle")
                     st.set("cond","2")
             else :
                 st.playSound("ItemSound.quest_itemget")
             st.giveItems(RED_CRYSTALS_ID,int(numItems))
     return
 
QUEST       = Quest(432,qn,"Birthday Party Song")
CREATED     = State('Start', QUEST)
STARTED     = State('Started', QUEST)
COMPLETED   = State('Completed', QUEST)

QUEST.setInitialState(CREATED)
QUEST.addStartNpc(31043)

QUEST.addTalkId(31043)

QUEST.addKillId(21103)