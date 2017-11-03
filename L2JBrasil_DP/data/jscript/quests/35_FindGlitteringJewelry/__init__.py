# Made by disKret
import sys
from com.it.br.util import Rnd
from com.it.br.gameserver.model.quest import State
from com.it.br.gameserver.model.quest import QuestState
from com.it.br.gameserver.model.quest.jython import QuestJython as JQuest

qn = "35_FindGlitteringJewelry"

ROUGH_JEWEL = 7162
ORIHARUKON = 1893
SILVER_NUGGET = 1873
THONS = 4044
JEWEL_BOX = 7077

class Quest (JQuest) :

 def __init__(self,id,name,descr): JQuest.__init__(self,id,name,descr)

 def onEvent (self,event,st) :
   htmltext = event
   cond = st.getInt("cond")
   if event == "30091-1.htm" and cond == 0 :
     st.set("cond","1")
     st.setState(STARTED)
     st.playSound("ItemSound.quest_accept")
   if event == "30879-1.htm" and cond == 1:
     st.set("cond","2")
   if event == "30091-3.htm" and cond == 3:
     st.takeItems(ROUGH_JEWEL,10)
     st.set("cond","4")
   if event == "30091-5.htm" and cond == 4:
     if st.getQuestItemsCount(ORIHARUKON) >= 5 and st.getQuestItemsCount(SILVER_NUGGET) >= 500 and st.getQuestItemsCount(THONS) >= 150 :
       st.takeItems(ORIHARUKON,5)
       st.takeItems(SILVER_NUGGET,500)
       st.takeItems(THONS,150)
       st.giveItems(JEWEL_BOX,1)
       st.playSound("ItemSound.quest_finish")
       st.exitQuest(1)
     else :
       htmltext = "You don't have enough materials"
   return htmltext

 def onTalk (self,npc,player):
   htmltext = "<html><body>You are either not carrying out your quest or don't meet the criteria.</body></html>"
   st = player.getQuestState(qn)
   if not st : return htmltext
   npcId = npc.getNpcId()
   cond = st.getInt("cond")
   id = st.getState()
   if npcId == 30091 and cond == 0 and st.getQuestItemsCount(JEWEL_BOX) == 0 :
     fwear=player.getQuestState("37_PleaseMakeMeFormalWear")
     if not fwear is None :
       if fwear.get("cond") == "6" :
         htmltext = "30091-0.htm"
         return htmltext
       else:
         htmltext = "<html><body>You are either not carrying out your quest or don't meet the criteria.</body></html>" 
         st.exitQuest(1)
     else:
       htmltext = "<html><body>You are either not carrying out your quest or don't meet the criteria.</body></html>" 
       st.exitQuest(1)
   elif npcId == 30879 and cond == 1 :
     htmltext = "30879-0.htm"
   elif id == STARTED :  
       if npcId == 30091 and st.getQuestItemsCount(ROUGH_JEWEL) == 10 :
         htmltext = "30091-2.htm"
       elif npcId == 30091 and cond == 4 and st.getQuestItemsCount(ORIHARUKON) >= 5 and st.getQuestItemsCount(SILVER_NUGGET) >= 500 and st.getQuestItemsCount(THONS) >= 150 :
         htmltext = "30091-4.htm"
   return htmltext


 def onKill(self,npc,player,isPet):
   partyMember1 = self.getRandomPartyMember(player,"1")
   partyMember2 = self.getRandomPartyMember(player,"2")
   partyMember = partyMember1 # initialize
   if not partyMember1 and not partyMember2: return
   elif not partyMember2 : partyMember = partyMember1
   elif not partyMember1 : partyMember = partyMember2
   else :
       if Rnd.get(2): partyMember = partyMember2
   
   if not partyMember : return
   st = partyMember.getQuestState(qn)
   if not st : return 
   if st.getState() != STARTED : return   
   count = st.getQuestItemsCount(ROUGH_JEWEL)
   if count<10 :
     st.giveItems(ROUGH_JEWEL,1)
     if count == 9 :
       st.playSound("ItemSound.quest_middle")
       st.set("cond","3")
     else:
       st.playSound("ItemSound.quest_itemget")
   return

QUEST       = Quest(35,qn,"Find Glittering Jewelry")
CREATED     = State('Start', QUEST)
STARTED     = State('Started', QUEST)

QUEST.setInitialState(CREATED)
QUEST.addStartNpc(30091)
QUEST.addTalkId(30091)

QUEST.addTalkId(30879)
QUEST.addKillId(20135)