# by disKret
import sys
from com.it.br import Config
from com.it.br.gameserver.model.quest import State
from com.it.br.gameserver.model.quest import QuestState
from com.it.br.gameserver.model.quest.jython import QuestJython as JQuest

qn = "601_WatchingEyes"

#NPC
EYE_OF_ARGOS = 31683
#ITEMS
PROOF_OF_AVENGER = 7188
#CHANCE
DROP_CHANCE = 50
#MOBS
MOBS = [ 21306,21308,21309,21310,21311 ]
#REWARDS
REWARDS = [[6699,90000,0,19],[6698,80000,20,39],[6700,40000,40,49],[0,230000,50,100]]

class Quest (JQuest) :

 def __init__(self,id,name,descr): JQuest.__init__(self,id,name,descr)

 def onEvent (self,event,st) :
   cond = st.getInt("cond")
   htmltext = event
   if event == "31683-1.htm" :
      if st.getPlayer().getLevel() < 71 : 
         htmltext = "31683-0a.htm"
         st.exitQuest(1)
      else :
         st.set("cond","1")
         st.setState(STARTED)
         st.playSound("ItemSound.quest_accept")
   elif event == "31683-4.htm" :
     if st.getQuestItemsCount(PROOF_OF_AVENGER) == 100 :
        random = st.getRandom(100)
        i = 0
        while i < len(REWARDS) :
            item,adena,chance,chance2=REWARDS[i]
            if chance<=random<= chance2 :
              break
            i = i+1
        st.giveItems(57,adena)
        if item :
           st.giveItems(item,5)
           st.addExpAndSp(120000,10000)
        st.takeItems(PROOF_OF_AVENGER,-1)
        st.playSound("ItemSound.quest_finish")
        st.exitQuest(1)
     else :
        htmltext="31683-4a.htm"
   return htmltext

 def onTalk (self,npc,player):
   htmltext = "<html><body>You are either not carrying out your quest or don't meet the criteria.</body></html>"
   st = player.getQuestState(qn)
   if st :
     npcId = npc.getNpcId()
     id = st.getState()
     cond = st.getInt("cond")
     if cond == 0 :
         htmltext = "31683-0.htm"
     elif cond == 1 :
         htmltext = "31683-2.htm"
     elif cond == 2 :
         htmltext = "31683-3.htm"
   return htmltext

 def onKill(self,npc,player,isPet):
   partyMember = self.getRandomPartyMember(player,"1")
   if not partyMember: return
   st = partyMember.getQuestState(qn)
   if st :
     if st.getState() == STARTED :
       count = st.getQuestItemsCount(PROOF_OF_AVENGER)
       if st.getInt("cond") == 1 and count < 100 :
         chance = DROP_CHANCE * Config.RATE_DROP_QUEST
         numItems, chance = divmod(chance,100)
         if st.getRandom(100) < chance : 
           numItems = numItems + 1
         if numItems :
           if count + numItems >= 100 :
             numItems = 100 - count
             st.playSound("ItemSound.quest_middle")
             st.set("cond","2")
           else:
             st.playSound("ItemSound.quest_itemget")   
           st.giveItems(PROOF_OF_AVENGER,int(numItems))       
   return

QUEST       = Quest(601,qn,"Watching Eyes")
CREATED     = State('Start', QUEST)
STARTED     = State('Started', QUEST)

QUEST.setInitialState(CREATED)
QUEST.addStartNpc(EYE_OF_ARGOS)
QUEST.addTalkId(EYE_OF_ARGOS)

for i in MOBS :
  QUEST.addKillId(i)