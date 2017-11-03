# Created by DrLecter, based on DraX' scripts
# This script is part of the L2J Official Datapack Project
# Visit us at http://www.l2jdp.com/
# See readme-dp.txt and gpl.txt for license and distribution details
# Let us know if you did not receive a copy of such files.
import sys

from com.it.br.gameserver.model.quest        import State
from com.it.br.gameserver.model.quest        import QuestState
from com.it.br.gameserver.model.quest.jython import QuestJython as JQuest

qn = "elven_human_buffers_2"
#Quest items
MARK_OF_PILGRIM     = 2721
MARK_OF_TRUST       = 2734
MARK_OF_HEALER      = 2820
MARK_OF_REFORMER    = 2821
MARK_OF_LIFE        = 3140
#MAXIMILIAN, HOLLINT,ORVEN,SQUILLARI,BERNHARD,SIEGMUND,GREGORY,HALASTER,BARYL,MARIE,RAHORAKI
NPCS=[30120,30191,30857,30905,31276,31321,31279,31755,31968,32095,31336]
#event:[newclass,req_class,req_race,low_ni,low_i,ok_ni,ok_i,req_item]
#low_ni : level too low, and you dont have quest item
#low_i: level too low, despite you have the item
#ok_ni: level ok, but you don't have quest item
#ok_i: level ok, you got quest item, class change takes place
CLASSES = {
    "BI":[16,15,0,"16","17","18","19",[MARK_OF_PILGRIM,MARK_OF_TRUST,MARK_OF_HEALER]],
    "PH":[17,15,0,"20","21","22","23",[MARK_OF_PILGRIM,MARK_OF_TRUST,MARK_OF_REFORMER]],
    "EE":[30,29,1,"12","13","14","15",[MARK_OF_PILGRIM,MARK_OF_LIFE,MARK_OF_HEALER]],
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
     htmltext = "30120-"+suffix+".htm"
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
     htmltext = "30120"
     if race in [0,1] :
       if id == 29 :                          # oracle
         return htmltext+"-01.htm"
       elif id == 15 :                        # cleric
         return htmltext+"-05.htm"
       elif classId.level() == 0 :            # first occupation change not made yet
         htmltext += "-24.htm"
       elif classId.level() >= 2 :            # second/third occupation change already made
         htmltext += "-25.htm"
       else :
         htmltext += "-26.htm"                # other conditions
     else :
       htmltext += "-26.htm"                  # other races
   st.exitQuest(1)
   return htmltext

QUEST   = Quest(99992,qn,"village_master")
CREATED   = State('Start',     QUEST)

QUEST.setInitialState(CREATED)
for npc in NPCS:
    QUEST.addStartNpc(npc)
    QUEST.addTalkId(npc)
