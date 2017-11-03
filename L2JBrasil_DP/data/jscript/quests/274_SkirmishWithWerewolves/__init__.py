# Maked by Mr. - Version 0.3 by DrLecter
import sys
from com.it.br import Config
from com.it.br.gameserver.model.quest import State
from com.it.br.gameserver.model.quest import QuestState
from com.it.br.gameserver.model.quest.jython import QuestJython as JQuest

qn = "274_SkirmishWithWerewolves"

MARAKU_WEREWOLF_HEAD = 1477
NECKLACE_OF_VALOR = 1507
NECKLACE_OF_COURAGE = 1506
ADENA = 57
MARAKU_WOLFMEN_TOTEM = 1501

class Quest (JQuest) :

 def __init__(self,id,name,descr): JQuest.__init__(self,id,name,descr)

 def onEvent (self,event,st) :
    htmltext = event
    if event == "30569-03.htm" :
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
   totems=st.getQuestItemsCount(MARAKU_WOLFMEN_TOTEM)
   if id == CREATED :
     st.set("cond","0")
   if st.getInt("cond")==0 :
     if player.getRace().ordinal() == 3 :
       if player.getLevel() > 8 :
         if st.getQuestItemsCount(NECKLACE_OF_VALOR) or st.getQuestItemsCount(NECKLACE_OF_COURAGE) :
           htmltext = "30569-02.htm"
         else :
           htmltext = "30569-07.htm"
           st.exitQuest(1)
       else :
         htmltext = "30569-01.htm"
         st.exitQuest(1)
     else :
       htmltext = "30569-00.htm"
       st.exitQuest(1)
   else :
     if st.getQuestItemsCount(MARAKU_WEREWOLF_HEAD) < 40 :
       htmltext = "30569-04.htm"
     else :
       amount = 3500
       if totems :
         amount += 600*totems
       htmltext = "30569-05.htm"
       st.playSound("ItemSound.quest_finish")
       st.giveItems(ADENA,amount)
       st.takeItems(MARAKU_WEREWOLF_HEAD,-1)
       st.takeItems(MARAKU_WOLFMEN_TOTEM,-1)
       st.exitQuest(1)
   return htmltext

 def onKill(self,npc,player,isPet):
   st = player.getQuestState(qn)
   if not st : return 
   if st.getState() != STARTED : return 
   
   count=st.getQuestItemsCount(MARAKU_WEREWOLF_HEAD)
   if count < 40 :
     if count < 39 :
       st.playSound("ItemSound.quest_itemget")
     else:
       st.playSound("ItemSound.quest_middle")
       st.set("cond","2")
     st.giveItems(MARAKU_WEREWOLF_HEAD,1)
     if st.getRandom(100) <= 15 :
       st.giveItems(MARAKU_WOLFMEN_TOTEM,1)
   return

QUEST       = Quest(274,qn,"Skirmish with the Werewolves")
CREATED     = State('Start', QUEST)
STARTING    = State('Starting', QUEST)
STARTED     = State('Started', QUEST)
COMPLETED   = State('Completed', QUEST)

QUEST.setInitialState(CREATED)
QUEST.addStartNpc(30569)

QUEST.addTalkId(30569)

QUEST.addKillId(20363)
QUEST.addKillId(20364)