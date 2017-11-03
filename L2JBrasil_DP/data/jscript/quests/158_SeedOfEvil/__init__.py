# Maked by Mr. Have fun! Version 0.2
import sys
from com.it.br.gameserver.model.quest import State
from com.it.br.gameserver.model.quest import QuestState
from com.it.br.gameserver.model.quest.jython import QuestJython as JQuest

qn = "158_SeedOfEvil"

CLAY_TABLET_ID = 1025
ENCHANT_ARMOR_D = 956

class Quest (JQuest) :

 def __init__(self,id,name,descr): JQuest.__init__(self,id,name,descr)

 def onEvent (self,event,st) :
    htmltext = event
    if event == "1" :
        st.set("id","0")
        st.set("cond","1")
        st.setState(STARTED)
        st.playSound("ItemSound.quest_accept")
        htmltext = "30031-04.htm"
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
   if npcId == 30031 and st.getInt("cond")==0 and st.getInt("onlyone")==0 :
        if st.getInt("cond")<15 :
          if player.getLevel() >= 21 :
            htmltext = "30031-03.htm"
            return htmltext
          else:
            htmltext = "30031-02.htm"
            st.exitQuest(1)
        else:
          htmltext = "30031-02.htm"
          st.exitQuest(1)
   elif npcId == 30031 and st.getInt("cond")==0 and st.getInt("onlyone")==1 :
      htmltext = "<html><body>This quest has already been completed.</body></html>"
   elif npcId == 30031 and st.getInt("cond")!=0 and st.getQuestItemsCount(CLAY_TABLET_ID)==0 :
        htmltext = "30031-05.htm"
   elif npcId == 30031 and st.getInt("cond")!=0 and st.getQuestItemsCount(CLAY_TABLET_ID)!=0 and st.getInt("onlyone")==0 :
        if st.getInt("id") != 158 :
          st.set("id","158")
          st.takeItems(CLAY_TABLET_ID,st.getQuestItemsCount(CLAY_TABLET_ID))
          st.set("cond","0")
          st.setState(COMPLETED)
          st.playSound("ItemSound.quest_finish")
          st.set("onlyone","1")
          st.giveItems(ENCHANT_ARMOR_D,1)
          htmltext = "30031-06.htm"
   return htmltext

 def onKill(self,npc,player,isPet):
   st = player.getQuestState(qn)
   if not st : return 
   if st.getState() != STARTED : return 

   npcId = npc.getNpcId()
   if npcId == 27016 :
        st.set("id","0")
        if st.getInt("cond") != 0 and st.getQuestItemsCount(CLAY_TABLET_ID) == 0 :
          st.giveItems(CLAY_TABLET_ID,1)
          st.playSound("ItemSound.quest_middle")
   return

QUEST       = Quest(158,qn,"Seed Of Evil")
CREATED     = State('Start', QUEST)
STARTING     = State('Starting', QUEST)
STARTED     = State('Started', QUEST)
COMPLETED   = State('Completed', QUEST)


QUEST.setInitialState(CREATED)
QUEST.addStartNpc(30031)

QUEST.addTalkId(30031)

QUEST.addKillId(27016)