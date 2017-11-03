# Maked by Mr. Have fun! Version 0.2
import sys
from com.it.br.gameserver.model.quest import State
from com.it.br.gameserver.model.quest import QuestState
from com.it.br.gameserver.model.quest.jython import QuestJython as JQuest

qn = "170_DangerousSeduction"

NIGHTMARE_CRYSTAL = 1046

class Quest (JQuest) :

 def __init__(self,id,name,descr): JQuest.__init__(self,id,name,descr)

 def onEvent (self,event,st) :
    htmltext = event
    if event == "1" :
       htmltext = "30305-04.htm"
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
   cond=st.getInt("cond")
   if id == COMPLETED :
      htmltext = "<html><body>This quest has already been completed.</body></html>"
   elif cond == 0 :
      if player.getRace().ordinal() != 2 :
         htmltext = "30305-00.htm"
         st.exitQuest(1)
      elif player.getLevel() > 20 :
         htmltext = "30305-03.htm"
      else:
         htmltext = "30305-02.htm"
         st.exitQuest(1)
   elif cond :
      if st.getQuestItemsCount(NIGHTMARE_CRYSTAL) :
         htmltext = "30305-06.htm"
         st.giveItems(57,102680)
         st.takeItems(NIGHTMARE_CRYSTAL,-1)
         st.setState(COMPLETED)
         st.playSound("ItemSound.quest_finish")
      else :
         htmltext = "30305-05.htm"
   return htmltext

 def onKill(self,npc,player,isPet):
   st = player.getQuestState(qn)
   if not st : return 
   if st.getState() != STARTED : return

   npcId = npc.getNpcId()
   if st.getInt("cond") == 1 :
      st.giveItems(NIGHTMARE_CRYSTAL,1)
      st.playSound("ItemSound.quest_middle")
      st.set("cond","2")
   return

QUEST       = Quest(170,qn,"Dangerous Seduction")
CREATED     = State('Start', QUEST)
STARTING     = State('Starting', QUEST)
STARTED     = State('Started', QUEST)
COMPLETED   = State('Completed', QUEST)


QUEST.setInitialState(CREATED)
QUEST.addStartNpc(30305)

QUEST.addTalkId(30305)

QUEST.addKillId(27022)