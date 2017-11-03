# Made by disKret
import sys
from com.it.br.gameserver.model.quest import State
from com.it.br.gameserver.model.quest import QuestState
from com.it.br.gameserver.model.quest.jython import QuestJython as JQuest

qn = "36_MakeASewingKit"

REINFORCED_STEEL = 7163
ARTISANS_FRAME   = 1891
ORIHARUKON       = 1893
SEWING_KIT       = 7078

class Quest (JQuest) :

 def __init__(self,id,name,descr): JQuest.__init__(self,id,name,descr)

 def onEvent (self,event,st) :
   htmltext = event
   cond = st.getInt("cond")
   if event == "30847-1.htm" and cond == 0 :
     st.set("cond","1")
     st.setState(STARTED)
     st.playSound("ItemSound.quest_accept")
   if event == "30847-3.htm" and cond == 2 :
     st.takeItems(REINFORCED_STEEL,5)
     st.set("cond","3")
   return htmltext

 def onTalk (self,npc,player) :
   htmltext = "<html><body>You are either not carrying out your quest or don't meet the criteria.</body></html>"
   st = player.getQuestState(qn)
   if not st : return htmltext
   id = st.getState()
   cond = st.getInt("cond")
   if cond == 0 and st.getQuestItemsCount(SEWING_KIT) == 0 :
     fwear=player.getQuestState("37_PleaseMakeMeFormalWear")
     if fwear:
         if fwear.get("cond") == "6" :
           htmltext = "30847-0.htm"
         else:
           st.exitQuest(1)
     else:
       st.exitQuest(1)
   elif st.getQuestItemsCount(REINFORCED_STEEL) == 5 :
     htmltext = "30847-2.htm"
   elif cond == 3 and st.getQuestItemsCount(ORIHARUKON) >= 10 and st.getQuestItemsCount(ARTISANS_FRAME) >= 10 :
     st.takeItems(ORIHARUKON,10)
     st.takeItems(ARTISANS_FRAME,10)
     st.giveItems(SEWING_KIT,1)
     st.playSound("ItemSound.quest_finish")
     htmltext = "30847-4.htm"
     st.exitQuest(1)
   return htmltext

 def onKill(self,npc,player,isPet):
   partyMember = self.getRandomPartyMember(player,"1")
   if not partyMember : return
   st = partyMember.getQuestState(qn)
   
   count = st.getQuestItemsCount(REINFORCED_STEEL)
   if count < 5 :
     st.giveItems(REINFORCED_STEEL,1)
     if count == 4 :
       st.playSound("ItemSound.quest_middle")
       st.set("cond","2")
     else:
       st.playSound("ItemSound.quest_itemget")
   return

QUEST       = Quest(36,qn,"Make A Sewing Kit")
CREATED     = State('Start', QUEST)
STARTED     = State('Started', QUEST)

QUEST.setInitialState(CREATED)
QUEST.addStartNpc(30847)
QUEST.addTalkId(30847)
QUEST.addKillId(20566)