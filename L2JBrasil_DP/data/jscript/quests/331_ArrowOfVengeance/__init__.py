# Made by Mr. - Version 0.3 by DrLecter
import sys
from com.it.br.gameserver.model.quest import State
from com.it.br.gameserver.model.quest import QuestState
from com.it.br.gameserver.model.quest.jython import QuestJython as JQuest

qn = "331_ArrowOfVengeance"

HARPY_FEATHER = 1452
MEDUSA_VENOM = 1453
WYRMS_TOOTH = 1454
ADENA = 57

class Quest (JQuest) :

 def __init__(self,id,name,descr): JQuest.__init__(self,id,name,descr)

 def onEvent (self,event,st) :
   htmltext = event
   if event == "30125-03.htm" :
      st.set("cond","1")
      st.setState(STARTED)
      st.playSound("ItemSound.quest_accept")
   elif event == "30125-06.htm" :
      st.exitQuest(1)
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
   if npcId == 30125 and st.getInt("cond")==0 :
      if player.getLevel() >= 32 :
         htmltext = "30125-02.htm"
         return htmltext
      else:
         htmltext = "30125-01.htm"
         st.exitQuest(1)
   elif npcId == 30125 and st.getInt("cond")==1 :
     if st.getQuestItemsCount(HARPY_FEATHER)+st.getQuestItemsCount(MEDUSA_VENOM)+st.getQuestItemsCount(WYRMS_TOOTH)>0 :
        st.giveItems(ADENA,80*st.getQuestItemsCount(HARPY_FEATHER)+90*st.getQuestItemsCount(MEDUSA_VENOM)+100*st.getQuestItemsCount(WYRMS_TOOTH))
        st.takeItems(HARPY_FEATHER,-1)
        st.takeItems(MEDUSA_VENOM,-1)
        st.takeItems(WYRMS_TOOTH,-1)
        htmltext = "30125-05.htm"
     else:
        htmltext = "30125-04.htm"
   return htmltext

 def onKill(self,npc,player,isPet):
   st = player.getQuestState(qn)
   if not st : return 
   if st.getState() != STARTED : return 
   
   npcId = npc.getNpcId()
   n = st.getRandom(10)
   if n<5 :
      if npcId == 20145 :
         st.giveItems(HARPY_FEATHER,1)
      elif npcId == 20158 :
         st.giveItems(MEDUSA_VENOM,1)
      elif npcId == 20176 :
         st.giveItems(WYRMS_TOOTH,1)
      st.playSound("ItemSound.quest_itemget")
   return

QUEST       = Quest(331,qn,"Arrow Of Vengeance")
CREATED     = State('Start', QUEST)
STARTED     = State('Started', QUEST)
COMPLETED   = State('Completed', QUEST)

QUEST.setInitialState(CREATED)

QUEST.addStartNpc(30125)
QUEST.addTalkId(30125)

QUEST.addKillId(20145)
QUEST.addKillId(20158)
QUEST.addKillId(20176)