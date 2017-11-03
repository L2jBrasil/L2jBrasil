# Made by disKret & DrLecter
import sys
from com.it.br import Config
from com.it.br.gameserver.model.quest import State
from com.it.br.gameserver.model.quest import QuestState
from com.it.br.gameserver.model.quest.jython import QuestJython as JQuest

qn = "379_FantasyWine"

#NPC
HARLAN = 30074

#MOBS
ENKU_CHAMPION = 20291
ENKU_SHAMAN = 20292

#ITEMS
LEAF = 5893
STONE = 5894

MOB = {
ENKU_CHAMPION:[LEAF,80],
ENKU_SHAMAN:[STONE,100]
}

class Quest (JQuest) :

 def __init__(self,id,name,descr): JQuest.__init__(self,id,name,descr)

 def onEvent (self,event,st) :
   htmltext = event
   leaf = st.getQuestItemsCount(LEAF)
   stone = st.getQuestItemsCount(STONE)
   if event == "30074-3.htm" :
     st.set("cond","1")
     st.setState(STARTED)
     st.playSound("ItemSound.quest_accept")
   elif event == "30074-6.htm" :
     if leaf == 80 and stone == 100 :
        st.takeItems(LEAF,leaf)
        st.takeItems(STONE,stone)
        item = st.getRandom(3)
        st.giveItems(5956+item,1)
        htmltext = "30074-"+str(6+item)+".htm"
        st.playSound("ItemSound.quest_finish")
        st.exitQuest(1)
     else :
        htmltext = "30074-4.htm"
   elif event == "30074-2a.htm" :
     st.exitQuest(1)
   return htmltext

 def onTalk (self,npc,player):
   htmltext = "<html><body>You are either not carrying out your quest or don't meet the criteria.</body></html>"
   st = player.getQuestState(qn)
   if not st : return htmltext

   cond = st.getInt("cond")
   leaf = st.getQuestItemsCount(LEAF)
   stone = st.getQuestItemsCount(STONE)
   if cond == 0 :
     if player.getLevel() >= 20 :
       htmltext = "30074-0.htm"
     else:
       htmltext = "30074-0a.htm"
       st.exitQuest(1)
   elif cond == 1 :
     if leaf < 80 and stone  < 100 :
       htmltext = "30074-4.htm"
     elif leaf == 80 and stone < 100 :
       htmltext = "30074-4a.htm"
     elif leaf < 80 and stone == 100 :
       htmltext = "30074-4b.htm"
   elif cond == 2 and leaf == 80 and stone == 100 :
       htmltext = "30074-5.htm"
   return htmltext

 def onKill(self,npc,player,isPet):
   st = player.getQuestState(qn)
   if not st : return 
   if st.getState() != STARTED : return 
   
   npcId = npc.getNpcId()
   numItems,chance = divmod(100*Config.RATE_DROP_QUEST,100)
   item,count = MOB[npcId]
   if item :
      if st.getRandom(100) <chance :
         numItems = numItems + 1
      prevItems = st.getQuestItemsCount(item)
      if prevItems < count :
         if prevItems + numItems > count :
            numItems = count - prevItems
         if int(numItems) != 0 :
            st.giveItems(item,int(numItems))
         if (st.getQuestItemsCount(LEAF) != 80 and st.getQuestItemsCount(STONE) != 100) :
            st.playSound("ItemSound.quest_itemget")
         else :
            st.playSound("ItemSound.quest_middle")
            st.set("cond","2")
   return

QUEST       = Quest(379,qn,"Fantasy Wine")
CREATED     = State('Start', QUEST)
STARTED     = State('Started', QUEST)

QUEST.setInitialState(CREATED)
QUEST.addStartNpc(HARLAN)
QUEST.addTalkId(HARLAN)

QUEST.addKillId(ENKU_CHAMPION)
QUEST.addKillId(ENKU_SHAMAN)