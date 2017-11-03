# Created by DrLecter, based on DraX' scripts
# This script is part of the L2J Official Datapack Project
# Visit us at http://www.l2jdp.com/
# See readme-dp.txt and gpl.txt for license and distribution details
# Let us know if you did not receive a copy of such files.
import sys

from com.it.br.gameserver.model.quest        import State
from com.it.br.gameserver.model.quest        import QuestState
from com.it.br.gameserver.model.quest.jython import QuestJython as JQuest

qn = "dark_elven_change_2"
#Quest items
MARK_OF_CHALLENGER  = 2627
MARK_OF_DUTY        = 2633
MARK_OF_SEEKER      = 2673
MARK_OF_SCHOLAR     = 2674
MARK_OF_PILGRIM     = 2721
MARK_OF_DUELIST     = 2762
MARK_OF_SEARCHER    = 2809
MARK_OF_REFORMER    = 2821
MARK_OF_MAGUS       = 2840
MARK_OF_FATE        = 3172
MARK_OF_SAGITTARIUS = 3293
MARK_OF_WITCHCRAFT  = 3307
MARK_OF_SUMMONER    = 3336

#INNOCENTIN,BRECSON,MEDOWN,ANGUS,ANDROMEDA,OLTLIN,XAIRAKIN,SAMAEL,VALDIS,TIFAREN,DRIZZIT,HELMINTER
NPCS=[31328,30195,30699,30474,31324,30862,30910,31285,31331,31334,31974,32096]
#event:[newclass,req_class,req_race,low_ni,low_i,ok_ni,ok_i,req_item]
#low_ni : level too low, and you dont have quest item
#low_i: level too low, despite you have the item
#ok_ni: level ok, but you don't have quest item
#ok_i: level ok, you got quest item, class change takes place
CLASSES = {
    "SK":[33,32,2,"26","27","28","29",[MARK_OF_DUTY,MARK_OF_FATE,MARK_OF_WITCHCRAFT]],
    "BD":[34,32,2,"30","31","32","33",[MARK_OF_CHALLENGER,MARK_OF_FATE,MARK_OF_DUELIST]],
    "SE":[43,42,2,"34","35","36","37",[MARK_OF_PILGRIM,MARK_OF_FATE,MARK_OF_REFORMER]],
    "AW":[36,35,2,"38","39","40","41",[MARK_OF_SEEKER,MARK_OF_FATE,MARK_OF_SEARCHER]],
    "PR":[37,35,2,"42","43","44","45",[MARK_OF_SEEKER,MARK_OF_FATE,MARK_OF_SAGITTARIUS]],
    "SH":[40,39,2,"46","47","48","49",[MARK_OF_SCHOLAR,MARK_OF_FATE,MARK_OF_MAGUS]],
    "PS":[41,39,2,"50","51","52","53",[MARK_OF_SCHOLAR,MARK_OF_FATE,MARK_OF_SUMMONER]],
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
     htmltext = "30474-"+suffix+".htm"
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
   # Dark Elves only
   if npcId in NPCS :
     htmltext = "30474"
     if race in [2] :
       if id == 32 :      # palus knight
         return htmltext+"-01.htm"
       elif id == 42 :                        # shillien oracle
         return htmltext+"-08.htm"
       elif id == 35 :    # assassin
         return htmltext+"-12.htm"
       elif id == 39 :                        # dark wizard
         return htmltext+"-19.htm"
       elif classId.level() == 0 :            # first occupation change not made yet
         htmltext += "-55.htm"
       elif classId.level() >= 2 :            # second/third occupation change already made
         htmltext += "-54.htm"
       else :
         htmltext += "-56.htm"                # other conditions
     else :
       htmltext += "-56.htm"                  # other races
   st.exitQuest(1)
   return htmltext

QUEST   = Quest(99993,qn,"village_master")
CREATED   = State('Start',     QUEST)

QUEST.setInitialState(CREATED)
for npc in NPCS:
    QUEST.addStartNpc(npc)
    QUEST.addTalkId(npc)
