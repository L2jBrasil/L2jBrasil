# By Evil33t
import sys
from com.it.br.gameserver.datatables.sql import ItemTable
from com.it.br.gameserver.model.quest import State
from com.it.br.gameserver.model.quest import QuestState
from com.it.br.gameserver.model.quest.jython import QuestJython as JQuest
from com.it.br.util import Rnd

KISS_OF_EVA = [1073,3141,3252]
BOX = 32342
REWARDS = [9692,9693]

def dropItem(npc,itemId,count,player):
	ditem = ItemTable.getInstance().createItem("Loot", itemId, count, player)
	ditem.dropMe(npc, npc.getX(),npc.getY(),npc.getZ()); 

class evabox(JQuest):
	def __init__(self,id,name,descr):
		self.isSpawned = False
		JQuest.__init__(self,id,name,descr)

	def onKill (self,npc,player,isPet):
		found = False
		for effect in player.getAllEffects():
			if effect.getSkill().getId() in KISS_OF_EVA:
				found = True
		if found:
			dropid = Rnd.get(len(REWARDS))
			dropItem(npc,REWARDS[dropid],1,player)
		return

QUEST = evabox(-1, "evabox", "ai")
CREATED     = State('Start',QUEST)

QUEST.setInitialState(CREATED)

QUEST.addKillId(BOX)