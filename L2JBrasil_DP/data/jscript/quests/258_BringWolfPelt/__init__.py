# Made by Mr. Have fun! - Version 0.3 by DrLecter
import sys
from com.it.br import Config 
from com.it.br.gameserver.model.quest import State
from com.it.br.gameserver.model.quest import QuestState
from com.it.br.gameserver.model.quest.jython import QuestJython as JQuest

qn = "258_BringWolfPelt"

WOLF_PELT = 702
REWARDS={429:[1,6],42:[1,19],41:[1,19],462:[1,19],18:[1,20],426:[1,5],29:[1,2],22:[1,2],390:[1,3]}

class Quest (JQuest) :

 def __init__(self,id,name,descr): JQuest.__init__(self,id,name,descr)

 def onEvent (self,event,st) :
    htmltext = event
    if event == "30001-03.htm" :
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
   if id == CREATED :
     st.set("cond","0")
   if st.getInt("cond")==0 :
     if player.getLevel() >= 3 :
       htmltext = "30001-02.htm"
     else:
       htmltext = "30001-01.htm"
       st.exitQuest(1)
   else :
     if st.getQuestItemsCount(WOLF_PELT) < 40 :
       htmltext = "30001-05.htm"
     else :
       st.takeItems(WOLF_PELT,-1)
       count=0
       while not count :
          for item in REWARDS.keys() :
              qty,chance=REWARDS[item]
              if st.getRandom(100) < chance and count == 0 :
                 st.giveItems(item,st.getRandom(qty)+1)
                 count+=1
       if chance < 7 :
         st.playSound("ItemSound.quest_jackpot")
       htmltext = "30001-06.htm"
       st.exitQuest(1)
       st.playSound("ItemSound.quest_finish")
   return htmltext

 def onKill(self,npc,player,isPet):
   st = player.getQuestState(qn)
   if not st : return 
   if st.getState() != STARTED : return 
   
   count = st.getQuestItemsCount(WOLF_PELT)
   numItems, chance = divmod(100,100)
   if st.getRandom(100) <chance :
     numItems = numItems + 1
   if count+numItems>=40 :
     numItems = 40 - count
     if numItems != 0 :
       st.playSound("ItemSound.quest_middle")
       st.set("cond","2")
   else :
     st.playSound("ItemSound.quest_itemget")
   st.giveItems(WOLF_PELT,int(numItems))
   return

QUEST       = Quest(258,qn,"Bring Wolf Pelt")
CREATED     = State('Start', QUEST)
STARTING    = State('Starting', QUEST)
STARTED     = State('Started', QUEST)
COMPLETED   = State('Completed', QUEST)

QUEST.setInitialState(CREATED)
QUEST.addStartNpc(30001)

QUEST.addTalkId(30001)

QUEST.addKillId(20120)
QUEST.addKillId(20442)