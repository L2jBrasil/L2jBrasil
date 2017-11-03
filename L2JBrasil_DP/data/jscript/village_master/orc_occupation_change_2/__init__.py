# Created by DrLecter, based on DraX' scripts
# This script is part of the L2J Official Datapack Project
# Visit us at http://www.l2jdp.com/
# See readme-dp.txt and gpl.txt for license and distribution details
# Let us know if you did not receive a copy of such files.
import sys

from com.it.br.gameserver.model.quest        import State
from com.it.br.gameserver.model.quest        import QuestState
from com.it.br.gameserver.model.quest.jython import QuestJython as JQuest

qn = "orc_occupation_change_2"

MARK_OF_CHALLENGER   = 2627
MARK_OF_PILGRIM      = 2721
MARK_OF_DUELIST      = 2762
MARK_OF_WARSPIRIT    = 2879
MARK_OF_GLORY        = 3203
MARK_OF_CHAMPION     = 3276
MARK_OF_LORD         = 3390
#PENATUS, KARIA, GARVARENTZ, LADANZA, TUSHKU, AKLAN, LAMBAC, SHAKA
NPCS=[30513,30681,30704,30865,30913,31288,31326,31977]

#event:[newclass,req_class,req_race,low_ni,low_i,ok_ni,ok_i,[req_items]]
#low_ni : level too low, and you dont have quest item
#low_i: level too low, despite you have the item
#ok_ni: level ok, but you don't have quest item
#ok_i: level ok, you got quest item, class change takes place
CLASSES = {
    "TY":[48,47,3,"16","17","18","19",[MARK_OF_CHALLENGER,MARK_OF_GLORY,MARK_OF_DUELIST]],
    "DE":[46,45,3,"20","21","22","23",[MARK_OF_CHALLENGER,MARK_OF_GLORY,MARK_OF_CHAMPION]],
    "OL":[51,50,3,"24","25","26","27",[MARK_OF_PILGRIM,MARK_OF_GLORY,MARK_OF_LORD]],
    "WC":[52,50,3,"28","29","30","31",[MARK_OF_PILGRIM,MARK_OF_GLORY,MARK_OF_WARSPIRIT]],
    }
#Messages
default = "No Quest"

def change(st,player,newclass,items) :
   for item in items :
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
        item = True
        for i in req_item :
            if not st.getQuestItemsCount(i):
               item = False
        if level < 40 :
           suffix = low_i
           if not item :
              suffix = low_ni
        else :
           if not item :
              suffix = ok_ni
           else :
              suffix = ok_i
              change(st,player,newclass,req_item)
     st.exitQuest(1)
     htmltext = "30513-"+suffix+".htm"
   return htmltext

 def onTalk (self,npc,player):
   st = player.getQuestState(qn)
   npcId = npc.getNpcId()
   race = player.getRace().ordinal()
   classId = player.getClassId()
   id = classId.getId()
   htmltext = default
   if player.isSubClassActive() :
      st.exitQuest(1)
      return htmltext
   # Orcs only
   if npcId in NPCS :
     htmltext = "30513"
     if race in [3] :
       if id == 47 :      # orc monk
         return htmltext+"-01.htm"
       elif id == 45 :    # orc raider
         return htmltext+"-05.htm"
       elif id == 50 :    # orc shaman
         return htmltext+"-09.htm"
       elif classId.level() == 0 : # first occupation change not made yet
         htmltext += "-33.htm"
       elif classId.level() >= 2 : # second/third occupation change already made
         htmltext += "-32.htm"
     else :
       htmltext += "-34.htm"  # other races
   st.exitQuest(1)
   return htmltext

QUEST   = Quest(99993,qn,"village_master")
CREATED = State('Start', QUEST)

QUEST.setInitialState(CREATED)

for npc in NPCS :
    QUEST.addStartNpc(npc)
    QUEST.addTalkId(npc)
