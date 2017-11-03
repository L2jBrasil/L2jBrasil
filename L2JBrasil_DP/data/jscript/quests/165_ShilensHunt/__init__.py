# Maked by Mr. Have fun! Version 0.2
import sys
from com.it.br import Config 
from com.it.br.gameserver.model.quest import State
from com.it.br.gameserver.model.quest import QuestState
from com.it.br.gameserver.model.quest.jython import QuestJython as JQuest

qn = "165_ShilensHunt"

DARK_BEZOAR_ID = 1160
LESSER_HEALING_POTION_ID = 1060

class Quest (JQuest) :

 def __init__(self,id,name,descr): JQuest.__init__(self,id,name,descr)

 def onEvent (self,event,st) :
    htmltext = event
    if event == "1" :
      st.set("id","0")
      st.set("cond","1")
      st.setState(STARTED)
      st.playSound("ItemSound.quest_accept")
      htmltext = "30348-03.htm"
    return htmltext


 def onTalk (self,npc,player):
   htmltext = "<html><body>You are either not carrying out your quest or don't meet the criteria.</body></html>"
   st = player.getQuestState(qn)
   if not st : return htmltext

   npcId = npc.getNpcId()
   id = st.getState()
   if id == CREATED :
     st.setState(STARTING)
     st.set("cond","0")
     st.set("onlyone","0")
     st.set("id","0")
   if npcId == 30348 and st.getInt("cond")==0 and st.getInt("onlyone")==0 :
      if st.getInt("cond")<15 :
        if player.getRace().ordinal() != 2 :
          htmltext = "30348-00.htm"
        elif player.getLevel() >= 3 :
          htmltext = "30348-02.htm"
          return htmltext
        else:
          htmltext = "30348-01.htm"
          st.exitQuest(1)
      else:
        htmltext = "30348-01.htm"
        st.exitQuest(1)
   elif npcId == 30348 and st.getInt("cond")==0 and st.getInt("onlyone")==1 :
      htmltext = "<html><body>This quest has already been completed.</body></html>"
   elif npcId == 30348 and st.getInt("cond")==1 :
      if st.getQuestItemsCount(DARK_BEZOAR_ID)<13 :
        htmltext = "30348-04.htm"
      elif st.getQuestItemsCount(DARK_BEZOAR_ID) >= 13 and st.getInt("onlyone") == 0 :
          if st.getInt("id") != 165 :
            st.set("id","165")
            htmltext = "30348-05.htm"
            st.takeItems(DARK_BEZOAR_ID,st.getQuestItemsCount(DARK_BEZOAR_ID))
            st.giveItems(LESSER_HEALING_POTION_ID,int(5*Config.RATE_QUESTS_REWARD))
            st.addExpAndSp(1000,0)
            st.set("cond","0")
            st.setState(COMPLETED)
            st.playSound("ItemSound.quest_finish")
            st.set("onlyone","1")
   return htmltext

 def onKill(self,npc,player,isPet):
   st = player.getQuestState(qn)
   if not st : return 
   if st.getState() != STARTED : return 

   npcId = npc.getNpcId()
   if npcId == 20529 :
        st.set("id","0")
        if st.getInt("cond") == 1 :
          if st.getRandom(10)<4 and st.getQuestItemsCount(DARK_BEZOAR_ID)<13 :
            st.giveItems(DARK_BEZOAR_ID,1)
            if st.getQuestItemsCount(DARK_BEZOAR_ID) == 13 :
              st.playSound("ItemSound.quest_middle")
            else:
              st.playSound("ItemSound.quest_itemget")
   elif npcId == 20532 :
        st.set("id","0")
        if st.getInt("cond") == 1 :
          if st.getRandom(10)<4 and st.getQuestItemsCount(DARK_BEZOAR_ID)<13 :
            st.giveItems(DARK_BEZOAR_ID,1)
            if st.getQuestItemsCount(DARK_BEZOAR_ID) == 13 :
              st.playSound("ItemSound.quest_middle")
            else:
              st.playSound("ItemSound.quest_itemget")
   elif npcId == 20536 :
        st.set("id","0")
        if st.getInt("cond") == 1 :
          if st.getRandom(10)<4 and st.getQuestItemsCount(DARK_BEZOAR_ID)<13 :
            st.giveItems(DARK_BEZOAR_ID,1)
            if st.getQuestItemsCount(DARK_BEZOAR_ID) == 13 :
              st.playSound("ItemSound.quest_middle")
            else:
              st.playSound("ItemSound.quest_itemget")
   elif npcId == 20456 :
        st.set("id","0")
        if st.getInt("cond") == 1 :
          if st.getRandom(10)<4 and st.getQuestItemsCount(DARK_BEZOAR_ID)<13 :
            st.giveItems(DARK_BEZOAR_ID,1)
            if st.getQuestItemsCount(DARK_BEZOAR_ID) == 13 :
              st.playSound("ItemSound.quest_middle")
            else:
              st.playSound("ItemSound.quest_itemget")
   return

QUEST       = Quest(165,qn,"Shilens Hunt")
CREATED     = State('Start', QUEST)
STARTING     = State('Starting', QUEST)
STARTED     = State('Started', QUEST)
COMPLETED   = State('Completed', QUEST)


QUEST.setInitialState(CREATED)
QUEST.addStartNpc(30348)

QUEST.addTalkId(30348)

QUEST.addKillId(20456)
QUEST.addKillId(20529)
QUEST.addKillId(20532)
QUEST.addKillId(20536)