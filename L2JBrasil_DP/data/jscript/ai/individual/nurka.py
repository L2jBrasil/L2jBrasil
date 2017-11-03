import sys
from com.it.br.gameserver.ai import CtrlIntention
from com.it.br.gameserver.instancemanager.clanhallsiege import FortResistSiege
from com.it.br.gameserver.model.quest import State
from com.it.br.gameserver.model.quest import QuestState
from com.it.br.gameserver.model.quest.jython import QuestJython as JQuest
from com.it.br.gameserver.instancemanager import ClanHallManager
from com.it.br.util import Rnd
from java.lang import System

NURKA = 35368
MESSENGER = 35382
CLANLEADERS = []

class Nurka(JQuest):

 def __init__(self,id,name,descr): JQuest.__init__(self,id,name,descr)

 def onTalk (self,npc,player):
   global CLANLEADERS
   npcId = npc.getNpcId()
   if npcId == MESSENGER :
     for clname in CLANLEADERS:
       if player.getName() == clname :
         return "<html><body>You already registered!</body></html>"
     if FortResistSiege.getInstance().Conditions(player) :
       CLANLEADERS.append(player.getName())
       return "<html><body>You have successful registered on a battle</body></html>"
     else:
       return "<html><body>Condition are not allow to do that!</body></html>"
   return
 
 def onAttack (self,npc,player,damage,isPet):
   global CLANLEADERS
   for clname in CLANLEADERS:
     if clname <> None :
       if player.getClan().getLeader().getName() == clname :
         FortResistSiege.getInstance().addSiegeDamage(player.getClan(),damage)
   return

 def onKill(self,npc,player,isPet):
   FortResistSiege.getInstance().CaptureFinish()
   return

QUEST = Nurka(-1, "nurka", "ai")
CREATED = State('Start', QUEST)
QUEST.setInitialState(CREATED)

QUEST.addTalkId(MESSENGER)
QUEST.addStartNpc(MESSENGER)

QUEST.addAttackId(NURKA)
QUEST.addKillId(NURKA)