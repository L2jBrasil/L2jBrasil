# Created by DrLecter, based on DraX' scripts,
# this script is part of the L2J Official Datapack Project
# Visit us at http://www.l2jdp.com/
# See readme-dp.txt and gpl.txt for license and distribution details
# Let us know if you did not receive a copy of such files.
import sys

from com.it.br.gameserver.model.quest        import State
from com.it.br.gameserver.model.quest        import QuestState
from com.it.br.gameserver.model.quest.jython import QuestJython as JQuest

qn = "dark_elven_change_1"
#Quest items
GAZE_OF_ABYSS     = 1244
IRON_HEART        = 1252
JEWEL_OF_DARKNESS = 1261
ORB_OF_ABYSS      = 1270
#Reward Item
SHADOW_WEAPON_COUPON_DGRADE = 8869
#XENOS,TOBIAS,TRONIX
NPCS=[30290,30297,30462]
#event:[newclass,req_class,req_race,low_ni,low_i,ok_ni,ok_i,req_item]
#low_ni : level too low, and you don't have quest item
#low_i: level too low, despite you have the item
#ok_ni: level ok, but you don't have quest item
#ok_i: level ok, you got quest item, class change takes place
CLASSES = {
    "PK":[32,31,2,"15","16","17","18",GAZE_OF_ABYSS],
    "AS":[35,31,2,"19","20","21","22",IRON_HEART],
    "DW":[39,38,2,"23","24","25","26",JEWEL_OF_DARKNESS],
    "SO":[42,38,2,"27","28","29","30",ORB_OF_ABYSS]
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
   # Dark Elves only
   if npcId in NPCS :
     htmltext = str(npcId)
     if race in [2] :
       if classId.level() == 1 : # first occupation change already made
         htmltext += "-32.htm"
       elif classId.level() >= 2 : # second/third occupation change already made
         htmltext += "-31.htm"
       elif id == 31 :      # DE Fighter
         return htmltext+"-01.htm"
       elif id == 38 :      # DE Mystic
         return htmltext+"-08.htm"
     else :
       htmltext += "-33.htm"  # other races
   st.exitQuest(1)
   return htmltext

QUEST   = Quest(99997,qn,"village_master")
CREATED = State('Start', QUEST)

QUEST.setInitialState(CREATED)

for npc in NPCS :
    QUEST.addStartNpc(npc)
    QUEST.addTalkId(npc)