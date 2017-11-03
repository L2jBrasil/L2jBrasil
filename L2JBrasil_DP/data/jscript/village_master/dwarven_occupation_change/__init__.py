# Created by DrLecter, based on DraX' and Ariakas work
# 
import sys
from com.it.br.gameserver.model.quest        import State
from com.it.br.gameserver.model.quest        import QuestState
from com.it.br.gameserver.model.quest.jython import QuestJython as JQuest

qn = "dwarven_occupation_change"

#GESTO,CROOP,BRAXT,KLUMP,NATOOLS,MONA,DONALD,YASHENI
BH_NPCS=[30511,30676,30685,30845,30894,31269,31314,31958]
#SEARCHER,GUILDSMAN,PROSPERITY
BH_MARKS=[2809,3119,3238]
#KUSTO,FLUTTER,VERGARA,FERRIS,ROMAN,NOEL,LOMBERT,NEWYEAR
WS_NPCS=[30512,30677,30687,30847,30897,31272,31317,31961]
#MAESTRO,GUILDSMAN,PROSPERITY
WS_MARKS=[2867,3119,3238]
#RING OF RAVEN
SCAV_MARKS=[1642]
#RIKADIO,RANSPO,MOKE,ALDER,BOLIN
SCAV_NPCS=[30503,30594,30498,32092,32093]
#FINAL PASS
ARTI_MARKS = [1635]
#MENDIO,OPIX,TAPOY
ARTI_NPCS=[30504,30595,30499]
#TRANSFER REWARDS
SHADOW_WEAPON_COUPON_DGRADE = 8869
SHADOW_WEAPON_COUPON_CGRADE = 8870
#Classes dictionary goes like this:
#event:[default_npc_prefix,race,req_class,1_class_denied,2_class_denied,[req_items],min_level,new_class,shadow_coupon_itemid]
#bountyhunter transfer rewards on hold until the rest of VMs get updated, so
#dont report this zero as a bug :)
CLASSES={
   "BH":["30511-",4,[0x36],[0x35],[0x37,0x39,0x75,0x76],BH_MARKS,40,0x37,0],
   "WS":["30512-",4,[0x38],[0x35],[0x37,0x39,0x75,0x76],WS_MARKS,40,0x39,SHADOW_WEAPON_COUPON_CGRADE],
   "SC":["30503-",4,[0x35],[0x36,0x38],[0x37,0x39,0x75,0x76],SCAV_MARKS,20,0x36,SHADOW_WEAPON_COUPON_DGRADE],
   "AR":["30504-",4,[0x35],[0x36,0x38],[0x37,0x39,0x75,0x76],ARTI_MARKS,20,0x38,SHADOW_WEAPON_COUPON_DGRADE],
}

UNIQUE_DIALOGS=[30594,30595,30498,30499]

default = "No Quest"

class Quest (JQuest) :

 def __init__(self,id,name,descr): JQuest.__init__(self,id,name,descr)

 def onAdvEvent (self,event,npc,player) :
   npcId    = npc.getNpcId()
   htmltext = event
   st = player.getQuestState(qn)
   if not st : return
   race     = player.getRace().ordinal()
   classid  = player.getClassId().getId()
   level    = player.getLevel()
   if event in CLASSES.keys():
      prefix,intended_race,required_class,denial1,denial2,required_marks,required_level,new_class,reward = CLASSES[event]
      if npcId in UNIQUE_DIALOGS : prefix = str(npcId)+"-"
      if classid in required_class and race == intended_race :
         marks=0
         for item in required_marks :
            if st.getQuestItemsCount(item) :
              marks+=1
         if level < required_level :
           if marks < len(required_marks) :
              htmltext = prefix+"05.htm"
           else:
              htmltext = prefix+"06.htm"
         else:
           if marks < len(required_marks) :
              htmltext = prefix+"07.htm"
           else :
            for item in required_marks :
               st.takeItems(item,1)
            if reward :
               st.giveItems(reward,15)
            player.setClassId(new_class)
            player.setBaseClass(new_class)
            player.broadcastUserInfo()
            st.playSound("ItemSound.quest_fanfare_2")
            htmltext = prefix+"08.htm"
      else :
        htmltext = default
      st.exitQuest(1)
   return htmltext

 def onTalk (Self,npc,player) :
   htmltext = default
   key      = 0
   npcId    = npc.getNpcId()
   race     = player.getRace().ordinal()
   classid  = player.getClassId().getId()
   st = player.getQuestState(qn)
   if player.isSubClassActive() :
      st.exitQuest(1)
      return htmltext
   if npcId in BH_NPCS : key = "BH"
   elif npcId in WS_NPCS : key = "WS"
   elif npcId in SCAV_NPCS : key = "SC"
   elif npcId in ARTI_NPCS : key = "AR"
   if key :
     prefix,intended_race,required_class,denial1,denial2,required_marks,required_level,new_class,reward = CLASSES[key]
     if npcId in UNIQUE_DIALOGS : prefix = str(npcId)+"-"
     htmltext=prefix+"11.htm"
     if race == intended_race :
       if classid in required_class :
          htmltext = prefix+"01.htm"
       elif classid in denial1 :
          htmltext = prefix+"09.htm"
          st.exitQuest(1)
       elif classid in denial2 :
          htmltext = prefix+"10.htm"
          st.exitQuest(1)
       else :
          st.exitQuest(1)
     else :
       st.exitQuest(1)
   return htmltext

QUEST   = Quest(99999,qn,"village_master")
CREATED   = State('Start',     QUEST)

QUEST.setInitialState(CREATED)

for npc in SCAV_NPCS :
    QUEST.addStartNpc(npc)
    QUEST.addTalkId(npc)

for npc in ARTI_NPCS :
    QUEST.addStartNpc(npc)
    QUEST.addTalkId(npc)

for npc in BH_NPCS :
    QUEST.addStartNpc(npc)
    QUEST.addTalkId(npc)

for npc in WS_NPCS :
    QUEST.addStartNpc(npc)
    QUEST.addTalkId(npc)