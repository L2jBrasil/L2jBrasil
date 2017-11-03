# Made by disKret & DrLecter
import sys
from com.it.br import Config
from com.it.br.gameserver.model.quest import State
from com.it.br.gameserver.model.quest import QuestState
from com.it.br.gameserver.model.quest.jython import QuestJython as JQuest

qn = "380_BringOutTheFlavorOfIngredients"

#NPC
ROLLANT = 30069

#MOBS
DIRE_WOLF = 20205
KADIF_WEREWOLF = 20206
GIANT_MIST_LEECH = 20225

#ITEMS
RITRONS_FRUIT,MOON_FACE_FLOWER,LEECH_FLUIDS = range(5895,5898)
ANTIDOTE = 1831
RITRON_JELLY = 5960
JELLY_RECIPE = 5959

#mob:[chance,item,max]
DROPLIST = {
DIRE_WOLF:[10,RITRONS_FRUIT,4],
KADIF_WEREWOLF:[50,MOON_FACE_FLOWER,20],
GIANT_MIST_LEECH:[50,LEECH_FLUIDS,10]
}

#CHANCE
RECIPE_CHANCE = 55

class Quest (JQuest) :

 def __init__(self,id,name,descr): JQuest.__init__(self,id,name,descr)

 def onEvent (self,event,st) :
   htmltext = event
   if event == "30069-4.htm" :
     st.set("cond","1")
     st.setState(STARTED)
     st.playSound("ItemSound.quest_accept")
   elif event == "30069-12.htm" :
     if st.getInt("cond") == 6 :
        st.giveItems(JELLY_RECIPE,1)
        st.playSound("ItemSound.quest_finish")
     else :
        htmltext = "I'll squeeze the jelly from your eyes"
     st.exitQuest(1)
   return htmltext

 def onTalk (self,npc,player):
   htmltext = "<html><body>You are either not carrying out your quest or don't meet the criteria.</body></html>"
   st = player.getQuestState(qn)
   if not st : return htmltext

   npcId = npc.getNpcId()
   id = st.getState()
   cond=st.getInt("cond")
   if cond == 0 :
     if player.getLevel() >= 24 :
       htmltext = "30069-1.htm"
     else:
       htmltext = "30069-0.htm"
       st.exitQuest(1)
   elif cond == 1 :
     htmltext = "30069-6.htm"
   elif cond == 2 :
     if st.getQuestItemsCount(ANTIDOTE) >= 2 and st.getQuestItemsCount(RITRONS_FRUIT) == 4 and st.getQuestItemsCount(MOON_FACE_FLOWER) == 20 and st.getQuestItemsCount(LEECH_FLUIDS) == 10 :
        st.takeItems(RITRONS_FRUIT,-1)
        st.takeItems(MOON_FACE_FLOWER,-1)
        st.takeItems(LEECH_FLUIDS,-1)
        st.takeItems(ANTIDOTE,2)
        st.set("cond","3")
        htmltext = "30069-7.htm"
     else :
        htmltext = "30069-6.htm"
   elif cond == 3 :
     st.set("cond","4")
     htmltext = "30069-8.htm"
   elif cond == 4 :
     st.set("cond","5")
     htmltext = "30069-9.htm"
   elif cond == 5 :
     st.set("cond","6")
     htmltext = "30069-10.htm"
   elif cond == 6 :
     st.giveItems(RITRON_JELLY,1)
     if st.getRandom(100) < RECIPE_CHANCE :
        htmltext = "30069-11.htm"
     else :
        htmltext = "30069-13.htm"
        st.playSound("ItemSound.quest_finish")
        st.exitQuest(1)
   return htmltext

 def onKill(self,npc,player,isPet):
   st = player.getQuestState(qn)
   if not st : return 
   if st.getState() != STARTED : return 
   
   if st.getInt("cond") == 1 :
      chance,item,max = DROPLIST[npc.getNpcId()]
      numItems,chance = divmod(chance*Config.RATE_DROP_QUEST,100)
      count = st.getQuestItemsCount(item)
      if count < max :
         if st.getRandom(100) < chance :
            numItems = numItems + 1
         numItems = int(numItems)
         if count + numItems > max :
            numItems = max - count
         if numItems != 0 :
            st.giveItems(item,numItems)
            if st.getQuestItemsCount(RITRONS_FRUIT) == 4 and st.getQuestItemsCount(MOON_FACE_FLOWER) == 20 and st.getQuestItemsCount(LEECH_FLUIDS) == 10 :
               st.set("cond","2")
               st.playSound("ItemSound.quest_middle")
            else :
               st.playSound("ItemSound.quest_itemget")
   return

QUEST       = Quest(380,qn,"Bring Out The Flavor Of Ingredients")
CREATED     = State('Start', QUEST)
STARTED     = State('Started', QUEST)

QUEST.setInitialState(CREATED)
QUEST.addStartNpc(ROLLANT)

QUEST.addTalkId(ROLLANT)

for mob in DROPLIST.keys():
    QUEST.addKillId(mob)