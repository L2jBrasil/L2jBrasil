# Contributed by t0rm3nt0r to the Official L2J Datapack Project.

import sys
from com.it.br.gameserver.model.quest import State
from com.it.br.gameserver.model.quest import QuestState
from com.it.br.gameserver.model.quest.jython import QuestJython as JQuest

qn = "371_ShriekOfGhosts"

#NPC
PATRIN = 30929
REVA = 30867
#Quest items
URN = 5903
PORCELAIN = 6002
# item : [chance, html]
PORC = {
    6003 : [ 2 , "30929-03.htm"],
    6004 : [ 32, "30929-04.htm"],
    6005 : [ 62, "30929-05.htm"],
    6006 : [ 77, "30929-06.htm"]
    }
# mobid : [urn chance, porcelain chance]
MOBS = {
    20818 : [38, 43],
    20820 : [48, 56],
    20824 : [50, 58]
    }

class Quest (JQuest) :

 def __init__(self,id,name,descr): JQuest.__init__(self,id,name,descr)
 
 def onEvent (self,event,st) :
     htmltext = event
     urn = st.getQuestItemsCount(URN)
     porcelain = st.getQuestItemsCount(PORCELAIN)
     if event == "30867-03.htm" :
       st.set("cond","1")
       st.setState(STARTED)
       st.playSound("ItemSound.quest_accept")
     elif event == "TRADE" :
       if urn == 0 :
         htmltext = "30867-06.htm"
       elif urn <= 100 :
         st.takeItems(URN,-1)
         st.giveItems(57,7000+urn*1000)
         htmltext = "30867-07.htm"
       elif urn > 100 :
         st.takeItems(URN,-1)
         st.giveItems(57,13000+urn*1000)
         htmltext = "30867-08.htm"
     elif event == "30867-10.htm" :
       htmltext = "30867-10.htm"
       if urn > 0:
           st.giveItems(57,urn*1000)
       st.exitQuest(1)
     elif event == "APPR" :
       if not porcelain :
         htmltext = "30929-02.htm"
       else :
           test = st.getRandom(100)
           st.takeItems(PORCELAIN,1)
           htmltext = "30929-07.htm"
           for item in PORC.keys():
               chance, html = PORC[item]
               if test < chance :
                   st.giveItems(item,1)
                   htmltext = html
                   break
     return htmltext

 def onTalk (self,npc,player):
     npcId = npc.getNpcId()
     htmltext = "<html><body>You are either not carrying out your quest or don't meet the criteria.</body></html>"
     st = player.getQuestState(qn)
     if not st : return htmltext
     id = st.getState()
     cond = st.getInt("cond")
     urn = st.getQuestItemsCount(URN)
     porcelain = st.getQuestItemsCount(PORCELAIN)
     if id == COMPLETED :
       htmltext = "<html><body>This quest has already been completed.</body></html>"
     elif id == CREATED and npcId == REVA :
       if player.getLevel() < 59 :
         htmltext = "30867-01.htm"
         st.exitQuest(1)
       else :
         htmltext = "30867-02.htm"
     elif id == STARTED :
       if npcId == REVA :
         if not porcelain :
           htmltext = "30867-04.htm"
         else :
           htmltext = "30867-05.htm"
       elif npcId == PATRIN :
           htmltext = "30929-01.htm"
     return htmltext
    
 def onKill(self,npc,player,isPet) :
     partyMember = self.getRandomPartyMemberState(player, STARTED)
     if not partyMember: return
     st = partyMember.getQuestState(qn)
     if not st : return
     npcId = npc.getNpcId()
     chance = st.getRandom(100)
     if npcId in MOBS.keys() :
         urnchance, porcchance = MOBS[npcId]
         if chance < urnchance :
             st.giveItems(URN,1)
             st.playSound("ItemSound.quest_itemget")
         elif chance < porcchance :
             st.giveItems(PORCELAIN,1)
             st.playSound("ItemSound.quest_itemget")
     return

QUEST       = Quest(371, qn, "Shriek Of Ghosts")
CREATED     = State('Start', QUEST)
STARTED     = State('Started', QUEST)
COMPLETED   = State('Completed', QUEST)

QUEST.setInitialState(CREATED)
QUEST.addStartNpc(REVA)

QUEST.addTalkId(REVA)
QUEST.addTalkId(PATRIN)

for mob in MOBS.keys() :
    QUEST.addKillId(mob)