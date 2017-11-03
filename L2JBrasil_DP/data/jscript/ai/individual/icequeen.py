import sys
from com.it.br.gameserver.instancemanager import GrandBossManager
from com.it.br.gameserver.model.quest import State
from com.it.br.gameserver.model.quest import QuestState
from com.it.br.gameserver.model.quest.jython import QuestJython as JQuest
from com.it.br.gameserver.datatables.sql import SkillTable
from java.lang import System

qn = "icequeen"

STEWARD = 32029

ICE_QUEEEN = 29056
ICE_CAPTAIN = 29060
FR_GARDENER = 22100
FR_SERVANT = 18327
FR_DOG = 22104

HEMOSYCLE = 8057

class icequeen (JQuest) :

 def __init__(self,id,name,descr):
   JQuest.__init__(self,id,name,descr)
   self.IQ = []

 def onAdvEvent (self,event,npc,player):
   if event == "prosnuca" :
     self.IQ = self.addSpawn(ICE_QUEEEN,102727,-125655,-2846,0,False,0)
     self.startQuestTimer("vkrovatku",1800000,self.IQ,None)
     self.cancelQuestTimer("prosnuca",npc,None)
   elif event == "captain" :
     npc = self.addSpawn(ICE_CAPTAIN,105804,-127721,-2769,0,False,0)
     self.startQuestTimer("sleepcap",1800000,npc,None)
     self.cancelQuestTimer("prosnuca",npc,None)
   elif event == "resist" :
     npc = self.addSpawn(FR_GARDENER,111375,-126645,-2991,0,False,0)
     self.startQuestTimer("sleepres",1800000,npc,None)
     self.cancelQuestTimer("resist",npc,None)
   elif event == "mdef" :
     npc = self.addSpawn(FR_SERVANT,108885,-129124,-3218,0,False,0)
     self.startQuestTimer("sleepmdef",1800000,npc,None)
     self.cancelQuestTimer("mdef",npc,None)
   elif event == "pdef" :
     npc = self.addSpawn(FR_DOG,109784,-125793,-3142,0,False,0)
     self.startQuestTimer("sleeppdef",1800000,npc,None)
     self.cancelQuestTimer("pdef",npc,None)
   elif event == "vkrovatku":
     npc.deleteMe()
     self.cancelQuestTimer("vkrovatku",npc,None)
   elif event == "sleepcap":
     npc.deleteMe()
     self.cancelQuestTimer("sleepcap",npc,None)
   elif event == "sleepres":
     npc.deleteMe()
     self.cancelQuestTimer("sleepres",npc,None)
   elif event == "sleepmdef":
     npc.deleteMe()
     self.cancelQuestTimer("sleepmdef",npc,None)
   elif event == "sleeppdef":
     npc.deleteMe()
     self.cancelQuestTimer("sleeppdef",npc,None)  
   elif event == "open" :
     self.deleteGlobalQuestVar("closed")
     self.deleteGlobalQuestVar("first")
     self.cancelQuestTimer("open",npc,None)
   elif event == "buff" :
     npc.setTarget(player)
     npc.doCast(SkillTable.getInstance().getInfo(4479,1))
     return
   elif event == "teleout" :
     if player:
       player.teleToLocation(115525,-125724,-3439)
       return "<html><body><font color=LEVEL>Time out...</font></body></html>"
     self.cancelQuestTimer("teleout",None,player)
   return

 def onTalk (self,npc,player):
   st = player.getQuestState(qn)
   npcId = npc.getNpcId()
   party = player.getParty()
   closed = self.loadGlobalQuestVar("closed")
   if npcId == STEWARD :
     if st.getQuestItemsCount(HEMOSYCLE) >= 10 :
       if closed == "" :
         if party:
           st.takeItems(HEMOSYCLE,10)
           for player in party.getPartyMembers() :
             GrandBossManager.getInstance().getZone(113669,-126122,-3489).allowPlayerEntry(player, 7200)
             player.teleToLocation(113669,-126122,-3489)
           self.saveGlobalQuestVar("closed", "1")
           self.startQuestTimer("prosnuca",1,None,None)
           self.startQuestTimer("captain",1,None,None)
           self.startQuestTimer("mdef",1,None,None)
           self.startQuestTimer("pdef",1,None,None)
           self.startQuestTimer("resist",1,None,None)
           self.startQuestTimer("teleout",1800000,None,player)
         else :
           return "<html><body><font color=LEVEL>Only with party...</font></body></html>"
       else :
         return "<html><body>The Raid currently<font color=LEVEL> in process.<font>.<br1>Try later.</body></html>"
     else : 
       return "<html><body>You dont have <font color=LEVEL>10 Silver Hemocyte<font>.</body></html>"
   return

 def onKill(self,npc,player,isPet):
   st = player.getQuestState(qn)
   npcId = npc.getNpcId()
   if npcId == FR_GARDENER :
     self.startQuestTimer("buff",1,npc,player)
   if npcId == FR_SERVANT :
     st.getPlayer().setTarget(self.IQ)
     st.getPlayer().useMagic(SkillTable.getInstance().getInfo(4480,1),True,True)
   if npcId == FR_DOG :
     st.getPlayer().setTarget(self.IQ)
     st.getPlayer().useMagic(SkillTable.getInstance().getInfo(4481,1),True,True)
   if npcId == ICE_CAPTAIN :
     st.getPlayer().setTarget(self.IQ)
     st.getPlayer().useMagic(SkillTable.getInstance().getInfo(4482,1),True,True)
   if npcId == ICE_QUEEEN :
     self.cancelQuestTimer("vkrovatku",npc,None)
   return

QUEST       = icequeen(-1, qn, "ai")
CREATED     = State('Start', QUEST)

QUEST.setInitialState(CREATED)

QUEST.addStartNpc(STEWARD)
QUEST.addTalkId(STEWARD)
QUEST.addKillId(ICE_QUEEEN)
QUEST.addKillId(FR_GARDENER)
QUEST.addKillId(FR_SERVANT)
QUEST.addKillId(FR_DOG)
