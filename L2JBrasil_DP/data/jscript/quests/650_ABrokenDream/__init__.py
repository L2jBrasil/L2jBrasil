#made by ethernaly
import sys
from com.it.br.gameserver.model.quest import State
from com.it.br.gameserver.model.quest import QuestState
from com.it.br.gameserver.model.quest.jython import QuestJython as JQuest

qn = "650_ABrokenDream"

#NPC
GHOST = 32054

#MOBS
CREWMAN = 22027
VAGABOND = 22028

#DROP
DREAM_FRAGMENT_ID = 8514

CHANCE = 68


class Quest (JQuest) :

 def __init__(self,id,name,descr): JQuest.__init__(self,id,name,descr)

 def onEvent (self,event,st) :
    htmltext = event
    if event == "2a.htm" :
      st.setState(STARTED)
      st.playSound("ItemSound.quest_accept")
      st.set("cond","1")
    elif event == "500.htm" :
      st.playSound("ItemSound.quest_finish")
      st.exitQuest(1)
    return htmltext

 def onTalk (self,npc,player):
   st = player.getQuestState(qn)
   if st :
        npcId = npc.getNpcId()
        htmltext = "<html><body>You are either not carrying out your quest or don't meet the criteria.</body></html>"
        id = st.getState()
        if id == CREATED :
            Ocean = player.getQuestState("117_OceanOfDistantStar")
            if st.getPlayer().getLevel() < 39:
                st.exitQuest(1)
                htmltext="100.htm"
            elif Ocean:
                if Ocean.getState().getName() == Completed:
                    htmltext="200.htm"
                else :
                	htmltext = "600.htm"#TODO: This is custom, need to get official text from retail
            else :
            	htmltext = "600.htm" #TODO: This is custom, need to get official text from retail
        elif id == STARTED :
            htmltext = "400.htm"
   return htmltext

 def onKill(self,npc,player,isPet):
   partyMember = self.getRandomPartyMember(player,"1")
   if not partyMember : return
   st = partyMember.getQuestState(qn)
   if st :
        if st.getState() == STARTED and st.getInt("cond") == 1 :
            if st.getRandom(100)<CHANCE :
                st.giveItems(DREAM_FRAGMENT_ID,1)
                st.playSound("ItemSound.quest_itemget")
   return

QUEST       = Quest(650, qn, "A Broken Dream")
CREATED     = State('Start', QUEST)
STARTED     = State('Started', QUEST)

QUEST.setInitialState(CREATED)
QUEST.addStartNpc(GHOST)
QUEST.addTalkId(GHOST)
QUEST.addKillId(CREWMAN)
QUEST.addKillId(VAGABOND)