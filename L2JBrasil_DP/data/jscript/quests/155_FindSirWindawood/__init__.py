# Made by Mr. - Version 0.3 by DrLecter
import sys
from com.it.br import Config 
from com.it.br.gameserver.model.quest import State
from com.it.br.gameserver.model.quest import QuestState
from com.it.br.gameserver.model.quest.jython import QuestJython as JQuest

qn = "155_FindSirWindawood"

OFFICIAL_LETTER_ID = 1019
HASTE_POTION_ID = 734

class Quest (JQuest) :

 def __init__(self,id,name,descr): JQuest.__init__(self,id,name,descr)

 def onEvent (self,event,st) :
    htmltext = event
    if event == "1" :
      st.giveItems(OFFICIAL_LETTER_ID,1)
      htmltext = "30042-04.htm"
      st.set("cond","1")
      st.setState(STARTED)
      st.playSound("ItemSound.quest_accept")
    return htmltext

 def onTalk (self,npc,player):
   htmltext = "<html><body>You are either not carrying out your quest or don't meet the criteria.</body></html>"
   st = player.getQuestState(qn)
   if not st : return htmltext

   npcId = npc.getNpcId()
   id = st.getState()
   if id == COMPLETED:
      htmltext = "<html><body>This quest has already been completed.</body></html>" 
   elif npcId == 30042 :
      if not st.getInt("cond") :
         if player.getLevel() >= 3 :
            htmltext = "30042-03.htm"
         else:
            htmltext = "30042-02.htm"
            st.exitQuest(1)
      elif st.getInt("cond") and st.getQuestItemsCount(OFFICIAL_LETTER_ID) :
         htmltext = "30042-05.htm"
   elif npcId == 30311 and st.getInt("cond") and st.getQuestItemsCount(OFFICIAL_LETTER_ID) and id == STARTED:
      st.takeItems(OFFICIAL_LETTER_ID,-1)
      st.giveItems(HASTE_POTION_ID,int(Config.RATE_QUESTS_REWARD))
      st.unset("cond")
      st.setState(COMPLETED)
      st.playSound("ItemSound.quest_finish")
      htmltext = "30311-01.htm"
   return htmltext

QUEST       = Quest(155,qn,"Find Sir Windawood")
CREATED     = State('Start', QUEST)
STARTING    = State('Starting', QUEST)
STARTED     = State('Started', QUEST)
COMPLETED   = State('Completed', QUEST)


QUEST.setInitialState(CREATED)
QUEST.addStartNpc(30042)

QUEST.addTalkId(30042)

QUEST.addTalkId(30311)