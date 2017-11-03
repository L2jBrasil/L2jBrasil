# Maked by Mr. Have fun! Version 0.2
import sys
from com.it.br import Config
from com.it.br.gameserver.model.quest import State
from com.it.br.gameserver.model.quest import QuestState
from com.it.br.gameserver.model.quest.jython import QuestJython as JQuest

qn = "292_BrigandsSweep"

GOBLIN_NECKLACE = 1483
GOBLIN_PENDANT = 1484
GOBLIN_LORD_PENDANT = 1485
SUSPICIOUS_MEMO = 1486
SUSPICIOUS_CONTRACT = 1487
ADENA = 57

class Quest (JQuest) :

 def __init__(self,id,name,descr): JQuest.__init__(self,id,name,descr)

 def onEvent (self,event,st) :
    htmltext = event
    if event == "30532-03.htm" :
      st.set("cond","1")
      st.setState(STARTED)
      st.playSound("ItemSound.quest_accept")
    elif event == "30532-06.htm" :
      st.takeItems(SUSPICIOUS_MEMO,-1)
      st.exitQuest(1)
      st.playSound("ItemSound.quest_finish")
    return htmltext

 def onTalk (self,npc,player):
   htmltext = "<html><body>You are either not carrying out your quest or don't meet the criteria.</body></html>"
   st = player.getQuestState(qn)
   if not st : return htmltext

   npcId = npc.getNpcId()
   id = st.getState()
   if npcId != 30532 and id != STARTED : return htmltext

   if id == CREATED :
     st.set("cond","0")
   if npcId == 30532 :
     if st.getInt("cond")==0 :
       if player.getRace().ordinal() != 4 :
         htmltext = "30532-00.htm"
         st.exitQuest(1)
       elif player.getLevel() >= 5 :
         htmltext = "30532-02.htm"
         return htmltext
       else:
         htmltext = "30532-01.htm"
         st.exitQuest(1)
     else :
      neckl=st.getQuestItemsCount(GOBLIN_NECKLACE)
      penda=st.getQuestItemsCount(GOBLIN_PENDANT)
      lordp=st.getQuestItemsCount(GOBLIN_LORD_PENDANT)
      smemo=st.getQuestItemsCount(SUSPICIOUS_MEMO)
      scont=st.getQuestItemsCount(SUSPICIOUS_CONTRACT)
      if neckl==penda==lordp==smemo==scont==0 :
        htmltext = "30532-04.htm"
      else :
        st.takeItems(GOBLIN_NECKLACE,-1)
        st.takeItems(GOBLIN_PENDANT,-1)
        st.takeItems(GOBLIN_LORD_PENDANT,-1)
        if scont == 0 :
          if smemo == 1 :
            htmltext = "30532-08.htm"
          elif smemo >= 2 :
            htmltext = "30532-09.htm"
          else :
            htmltext = "30532-05.htm"
        else :
           htmltext = "30532-10.htm"
           st.takeItems(SUSPICIOUS_CONTRACT,-1)
        st.giveItems(ADENA,12*neckl+36*penda+33*lordp+100*scont*int(Config.RATE_DROP_ADENA))
   elif npcId == 30533 :
      if st.getQuestItemsCount(SUSPICIOUS_CONTRACT)==0 :
        htmltext = "30533-01.htm"
      else :
        htmltext = "30533-02.htm"
        st.giveItems(ADENA,st.getQuestItemsCount(SUSPICIOUS_CONTRACT)*120*int(Config.RATE_DROP_ADENA))
        st.takeItems(SUSPICIOUS_CONTRACT,-1)
   return htmltext

 def onKill(self,npc,player,isPet):
   st = player.getQuestState(qn)
   if not st : return 
   if st.getState() != STARTED : return 
   
   npcId = npc.getNpcId()
   if npcId in [20322, 20323]: item = GOBLIN_NECKLACE
   if npcId in [20324, 20327]: item = GOBLIN_PENDANT
   if npcId == 20528 : item = GOBLIN_LORD_PENDANT
   if st.getInt("cond") :
     n = st.getRandom(10)
     if n > 5 :
       st.giveItems(item,1)
       st.playSound("ItemSound.quest_itemget")
     elif n > 4 :
       if st.getQuestItemsCount(SUSPICIOUS_CONTRACT) == 0 :
          if st.getQuestItemsCount(SUSPICIOUS_MEMO) < 3 :
            st.giveItems(SUSPICIOUS_MEMO,1)
            st.playSound("ItemSound.quest_itemget")
          else :
            st.giveItems(SUSPICIOUS_CONTRACT,1)
            st.takeItems(SUSPICIOUS_MEMO,-1)
            st.playSound("ItemSound.quest_middle")
            st.set("cond","2")
   return

QUEST       = Quest(292,qn,"Brigands Sweep")
CREATED     = State('Start', QUEST)
STARTING    = State('Starting', QUEST)
STARTED     = State('Started', QUEST)
COMPLETED   = State('Completed', QUEST)

QUEST.setInitialState(CREATED)
QUEST.addStartNpc(30532)

QUEST.addTalkId(30532)

QUEST.addTalkId(30533)

QUEST.addKillId(20322)
QUEST.addKillId(20323)
QUEST.addKillId(20324)
QUEST.addKillId(20327)
QUEST.addKillId(20528)