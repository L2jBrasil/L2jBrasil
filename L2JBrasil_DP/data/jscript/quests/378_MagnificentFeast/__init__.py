# Magnificent Feast - v0.1 by DrLecter
import sys
from com.it.br.gameserver.model.quest import State
from com.it.br.gameserver.model.quest import QuestState
from com.it.br.gameserver.model.quest.jython import QuestJython as JQuest

qn = "378_MagnificentFeast"

#NPC
RANSPO = 30594

#ITEMS
WINE_15,WINE_30,WINE_60 = range(5956,5959)
SCORE = 4421
RP_SALAD,RP_SAUCE,RP_STEAK = range(1455,1458)
RP_DESSERT = 5959
#REWARDS
REWARDS={
9:[847,1,5700],
10:[846,2,0],
12:[909,1,25400],
17:[846,2,1200],
18:[879,1,6900],
20:[890,2,8500],
33:[879,1,8100],
34:[910,1,0],
36:[848,1,2200],
}

class Quest (JQuest) :

 def __init__(self,id,name,descr): JQuest.__init__(self,id,name,descr)

 def onEvent (self,event,st) :
   htmltext = event
   score = st.getInt("score")
   cond = st.getInt("cond")
   if event == "30594-2.htm" and cond == 0 :
     st.set("cond","1")
     st.setState(STARTED)
     st.playSound("ItemSound.quest_accept")
   elif event == "30594-4a.htm" :
     if st.getQuestItemsCount(WINE_15) and cond == 1 :
        st.takeItems(WINE_15,1)
        st.set("cond","2")
        st.set("score",str(score+1))
     else :
        htmltext = "30594-4.htm"
   elif event == "30594-4b.htm" :
     if st.getQuestItemsCount(WINE_30) and cond == 1 :
        st.takeItems(WINE_30,1)
        st.set("cond","2")
        st.set("score",str(score+2))
     else :
        htmltext = "30594-4.htm"
   elif event == "30594-4c.htm" :
     if st.getQuestItemsCount(WINE_60) and cond == 1 :
        st.takeItems(WINE_60,1)
        st.set("cond","2")
        st.set("score",str(score+4))
     else :
        htmltext = "30594-4.htm"
   elif event == "30594-6.htm" :
     if st.getQuestItemsCount(SCORE) and cond == 2  :
        st.takeItems(SCORE,1)
        st.set("cond","3")
     else :
        htmltext = "30594-5.htm"
   elif event == "30594-8a.htm" :
     if st.getQuestItemsCount(RP_SALAD) and cond == 3 :
        st.takeItems(RP_SALAD,1)
        st.set("cond","4")
        st.set("score",str(score+8))
     else :
        htmltext = "30594-8.htm"
   elif event == "30594-8b.htm" :
     if st.getQuestItemsCount(RP_SAUCE) and cond == 3 :
        st.takeItems(RP_SAUCE,1)
        st.set("cond","4")
        st.set("score",str(score+16))
     else :
        htmltext = "30594-8.htm"
   elif event == "30594-8c.htm" :
     if st.getQuestItemsCount(RP_STEAK) and cond == 3 :
        st.takeItems(RP_STEAK,1)
        st.set("cond","4")
        st.set("score",str(score+32))
     else :
        htmltext = "30594-8.htm"

   return htmltext

 def onTalk (self,npc,player):
   htmltext = "<html><body>You are either not carrying out your quest or don't meet the criteria.</body></html>"
   st = player.getQuestState(qn)
   if not st : return htmltext

   npcId = npc.getNpcId()
   id = st.getState()
   cond=st.getInt("cond")
   if cond == 0 :
     if player.getLevel() >= 20 :
       htmltext = "30594-1.htm"
     else:
       htmltext = "30594-0.htm"
       st.exitQuest(1)
   elif cond == 1 :
     htmltext = "30594-3.htm"
   elif cond == 2 :
     if st.getQuestItemsCount(SCORE) :
        htmltext = "30594-5a.htm"
     else :
        htmltext = "30594-5.htm"
   elif cond == 3 :
     htmltext = "30594-7.htm"
   elif cond == 4 :
     score = st.getInt("score")
     if st.getQuestItemsCount(RP_DESSERT) and score in REWARDS.keys() :
        item,qty,adena=REWARDS[score]
        st.giveItems(item,qty)
        if adena :
           st.giveItems(57,adena)
        st.takeItems(RP_DESSERT,1)
        st.playSound("ItemSound.quest_finish")
        htmltext = "30594-10.htm"
        st.exitQuest(1)
     else :
        htmltext = "30594-9.htm"
   return htmltext

QUEST       = Quest(378,qn,"Magnificent Feast")
CREATED     = State('Start', QUEST)
STARTED     = State('Started', QUEST)

QUEST.setInitialState(CREATED)
QUEST.addStartNpc(RANSPO)

QUEST.addTalkId(RANSPO)