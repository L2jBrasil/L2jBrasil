# Created by DrLecter, based on DraX' scripts,
# this script is part of the L2J Official Datapack Project
# Visit us at http://www.l2jdp.com/
# See readme-dp.txt and gpl.txt for license and distribution details
# Let us know if you did not receive a copy of such files.
import sys

from com.it.br.gameserver.model.quest        import State
from com.it.br.gameserver.model.quest        import QuestState
from com.it.br.gameserver.model.quest.jython import QuestJython as JQuest

qn = "orc_occupation_change_1"
#Quest items
MARK_OF_RAIDER   = 1592
KHAVATARI_TOTEM  = 1615
MASK_OF_MEDIUM   = 1631
#Reward Item
SHADOW_WEAPON_COUPON_DGRADE = 8869
#OSBORN,DRIKUS,CASTOR
NPCS=[30500,30505,30508]
#event:[newclass,req_class,req_race,low_ni,low_i,ok_ni,ok_i,req_item]
#low_ni : level too low, and you don't have quest item
#low_i: level too low, despite you have the item
#ok_ni: level ok, but you don't have quest item
#ok_i: level ok, you got quest item, class change takes place
CLASSES = {
    "OR":[45,44,3,"09","10","11","12",MARK_OF_RAIDER],
    "OM":[47,44,3,"13","14","15","16",KHAVATARI_TOTEM],
    "OS":[50,49,3,"17","18","19","20",MASK_OF_MEDIUM],
    }
#Messages
default = "No Quest"

def change(st,player,newclass,item) :
   st.takeItems(item,1)
   st.playSound("ItemSound.quest_fanfare_2")
   player.setClassId(newclass)
   player.setBaseClass(newclass)
   player.broadcastUserInfo()
   return

class Quest (JQuest) :

 def __init__(self,id,name,descr): JQuest.__init__(self,id,name,descr)

 def onAdvEvent (self,event,npc,player) :
   npcId    = npc.getNpcId()
   htmltext = default
   suffix = ''
   st = player.getQuestState(qn)
   if not st : return
   race     = player.getRace().ordinal()
   classid  = player.getClassId().getId()
   level    = player.getLevel()
   if npcId not in NPCS : return
   if not event in CLASSES.keys() :
     return event
   else :
     newclass,req_class,req_race,low_ni,low_i,ok_ni,ok_i,req_item=CLASSES[event]
     if race == req_race and classid == req_class :
        item = st.getQuestItemsCount(req_item)
        if level < 20 :
           suffix = "-"+low_i+".htm"
           if not item :
              suffix = "-"+low_ni+".htm"
        else :
           if not item :
              suffix = "-"+ok_ni+".htm"
           else :
              suffix = "-"+ok_i+".htm"
              st.giveItems(SHADOW_WEAPON_COUPON_DGRADE,15)
              change(st,player,newclass,req_item)
     st.exitQuest(1)
     htmltext = str(npcId)+suffix
   return htmltext

 def onTalk (self,npc,player):
   st = player.getQuestState(qn)
   npcId = npc.getNpcId()
   race    = player.getRace().ordinal()
   classId = player.getClassId()
   id = classId.getId()
   htmltext = default
   if player.isSubClassActive() :
      st.exitQuest(1)
      return htmltext
   # Orcs only
   if npcId in NPCS :
     htmltext = str(npcId)
     if race in [3] :
       if classId.level() == 1 : # first occupation change already made
         htmltext += "-21.htm"
       elif classId.level() >= 2 : # second/third occupation change already made
         htmltext += "-22.htm"
       elif id == 44 :      # Orc Fighter
         return htmltext+"-01.htm"
       elif id == 49 :      # Orc Mystic
         return htmltext+"-06.htm"
     else :
       htmltext += "-23.htm"  # other races
   st.exitQuest(1)
   return htmltext

QUEST   = Quest(99996,qn,"village_master")
CREATED = State('Start', QUEST)

QUEST.setInitialState(CREATED)

for npc in NPCS :
    QUEST.addStartNpc(npc)
    QUEST.addTalkId(npc)