# Made by mtrix & DrLecter
import sys
from com.it.br.gameserver.model.quest import State
from com.it.br.gameserver.model.quest import QuestState
from com.it.br.gameserver.model.quest.jython import QuestJython as JQuest

qn = "383_SearchingForTreasure"

SHARK=20314
PIRATES_TREASURE_MAP = 5915
PIRATES_CHEST = 31148
ESPEN = 30890

#itemid:[maxqty,chance in 1000].
REWARDS={1338:[2,150],3452:[1,140],1337:[1,130],3455:[1,120],4409:[1,220],4408:[1,220],4418:[1,220],4419:[1,220],956:[1,15],952:[1,8],2451:[1,2],2450:[1,2]}

class Quest (JQuest) :

 def __init__(self,id,name,descr): JQuest.__init__(self,id,name,descr)

 def onEvent (self,event,st) :
     htmltext = event
     if event == "30890-03.htm" :
        st.set("cond","1")
        st.setState(STARTED)
     elif event == "30890-07.htm" :
        if st.getQuestItemsCount(PIRATES_TREASURE_MAP) :
           st.set("cond","2")
           st.takeItems(PIRATES_TREASURE_MAP,1)
           st.addSpawn(PIRATES_CHEST,106583,197747,-4209,900000)
           st.addSpawn(SHARK,106570,197740,-4209,900000)
           st.addSpawn(SHARK,106580,197747,-4209,900000)
           st.addSpawn(SHARK,106590,197743,-4209,900000)
           st.playSound("ItemSound.quest_accept")
        else:
           htmltext="You don't have required items"
           st.exitquest(1)
     elif event == "30890-02b.htm":
        if st.getQuestItemsCount(PIRATES_TREASURE_MAP) :
           st.takeItems(PIRATES_TREASURE_MAP,1)
           st.giveItems(57,1000)
           st.playSound("ItemSound.quest_finish")
        else:
           htmltext="You don't have required items"
        st.exitQuest(1)
     elif event == "31148-02.htm":
        if st.getQuestItemsCount(1661):
           st.takeItems(1661,1)
           st.giveItems(57,500+(st.getRandom(5)*300))
           count=0
           while count < 1 :
             for item in REWARDS.keys() :
              qty,chance=REWARDS[item]
              if st.getRandom(1000) < chance and count < 2 :
                 st.giveItems(item,st.getRandom(qty)+1)
                 count+=1
              if count < 2 :
                for i in range(4481,4505) :
                  if st.getRandom(500) == 1 and count < 2 :
                     st.giveItems(i,1)
                     count+=1
           st.playSound("ItemSound.quest_finish")
           st.exitQuest(1)
        else :
           htmltext = "31148-03.htm"
     return htmltext

 def onTalk (self,npc,player):
     htmltext = "<html><body>You are either not carrying out your quest or don't meet the criteria.</body></html>"
     st = player.getQuestState(qn)
     if not st : return htmltext

     npcId = npc.getNpcId()
     id = st.getState()
     if id == CREATED :
       if player.getLevel() >= 42:  
          if st.getQuestItemsCount(PIRATES_TREASURE_MAP) :
            htmltext = "30890-01.htm"
          else :
            htmltext = "30890-00.htm"
            st.exitQuest(1)
       else :
          htmltext = "30890-01a.htm"
          st.exitQuest(1)
     elif npcId == ESPEN :
        htmltext = "30890-03a.htm"
     elif npcId == PIRATES_CHEST and st.getInt("cond") == 2 and id == STARTED:
        htmltext = "31148-01.htm"
     return htmltext

QUEST       = Quest(383,qn,"Searching For Treasure")
CREATED     = State('Start', QUEST)
STARTED     = State('Started', QUEST)

QUEST.setInitialState(CREATED)
QUEST.addStartNpc(ESPEN)

QUEST.addTalkId(ESPEN)

QUEST.addTalkId(PIRATES_CHEST)