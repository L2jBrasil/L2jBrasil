# Made by Emperorc
import sys
from com.it.br.gameserver.model.quest import State
from com.it.br.gameserver.model.quest import QuestState
from com.it.br.gameserver.model.quest.jython import QuestJython as JQuest
from com.it.br.gameserver.network.serverpackets import CreatureSay
from com.it.br.gameserver.ai import CtrlIntention

qn = "22_TragedyInVonHellmannForest"

#NPCS
INNOCENTIN = 31328
TIFAREN = 31334
WELL = 31527
GHOST_PRIEST = 31528
GHOST_ADVENTURER = 31529
NPCS = range(31527,31530) + [31328, 31334]

#MOBS
SOUL_OF_WELL = 27217
MOBS = range(21553,21557) + [21561]

#ITEMS 
CROSS, SKULL, LETTER, JEWEL1, JEWEL2, SEALED_BOX, BOX = range(7141,7148)

def AutoChat(npc,text) :
    chars = npc.getKnownList().getKnownPlayers().values().toArray()
    if chars != None:
       for pc in chars :
          sm = CreatureSay(npc.getObjectId(), 0, npc.getName(), text)
          pc.sendPacket(sm)

class Quest (JQuest) :

 def __init__(self,id,name,descr):
     JQuest.__init__(self,id,name,descr)
     self.priest = ""
     self.tifaren = 0
     self.soul = 0
     self.well = 0

 def onAdvEvent (self,event,npc, player) :
   st = player.getQuestState(qn)
   if not st: return
   htmltext = event 
   if event == "31334-02.htm" :
       st2 = player.getQuestState("21_HiddenTruth")
       if st2 :
           if not (st2.getState().getName() == 'Completed' and player.getLevel() >= 63) :
               htmltext = "31334-03.htm"
               st.exitQuest(1)
       else :
           htmltext = "31334-03.htm"
           st.exitQuest(1)
   elif event == "31334-04.htm" :
       st.set("cond","1")
       st.set("id","1")
       st.setState(STARTED)
       st.playSound("ItemSound.quest_accept")
   elif event == "31334-06.htm" :
       if st.getQuestItemsCount(CROSS) == 0 :
           htmltext = "31334-07.htm"
           st.set("cond","2")
   elif event == "31334-08.htm" :
       st.set("cond","4")
       st.set("id","2")
   elif event == "31334-13.htm" :
       if st.getInt("id") == 2 and st.getQuestItemsCount(CROSS) > 0 and st.getQuestItemsCount(SKULL) > 0 :
           if self.tifaren == 1 :
               htmltext = "31334-14.htm"
               st.set("cond","6")
           else :
               self.tifaren = 1
               st.set("cond","7")
               st.set("id","4")
               st.takeItems(SKULL,-1)
               priest = st.addSpawn(GHOST_PRIEST,38354,-49777,-1128)
               st.startQuestTimer("Despawn Ghost Priest",120000,priest)
               AutoChat(priest,player.getName()+", you have awoken me...")
               self.priest = player.getName()
       elif st.getInt("id") == 4 and st.getQuestItemsCount(CROSS) > 0 :
           if self.tifaren == 1 :
               htmltext = "31334-14.htm"
               st.set("cond","6")
           else :
               self.tifaren = 1
               st.takeItems(SKULL,-1)
               priest = st.addSpawn(GHOST_PRIEST,38354,-49777,-1128)
               st.startQuestTimer("Despawn Ghost Priest",120000,priest)
               AutoChat(priest,player.getName()+", you have awoken me...")
               self.priest = player.getName()
   elif event == "31528-05.htm" :
       st.playSound("AmbSound.d_horror_03")
   elif event == "31528-09.htm" :
       st.set("id","5")
       st.set("cond","8")
       st.startQuestTimer("Despawn Ghost Priest 2",3000,npc)
   elif event == "31328-04.htm" :
       st.takeItems(CROSS,-1)
       st.set("id","6")
   elif event == "31328-10.htm" :
       st.giveItems(LETTER,1)
       st.set("id","7")
       st.set("cond","9")
   elif event == "31529-03.htm" :
       st.takeItems(LETTER,-1)
       st.set("id","8")
   elif event == "31529-09.htm" :
       st.set("id","9")
   elif event == "31529-12.htm" :
       st.giveItems(JEWEL1,1)
       st.set("id","10")
       st.set("cond","10")
   elif event == "31527-02.htm" :
       if self.well == 0 :
           self.well = 1
           soul = st.addSpawn(SOUL_OF_WELL,34706,-54590,-2054)
           self.soul = 0
           st.playSound("SkillSound3.antaras_fear")
           st.startQuestTimer("Soul of Well 1",90000,soul)
           st.startQuestTimer("Soul of Well Despawn",120000,soul)
           soul.addDamageHate(player,0,99999)
           soul.getAI().setIntention(CtrlIntention.AI_INTENTION_ATTACK,player,None)
       else :
           htmltext = "31527-03.htm"
   elif event == "31328-13.htm" :
       st.takeItems(BOX,-1)
       st.set("id","13")
       st.set("cond","15")
   elif event == "31328-21.htm" :
       st.set("id","14")
       st.set("cond","16")
   elif event == "Despawn Ghost Priest" :
       npc.reduceCurrentHp(9999999,npc)
       self.tifaren = 0
       if st.getQuestTimer("Despawn Ghost Priest 2") :
           st.getQuestTimer("Despawn Ghost Priest 2").cancel()
       return
   elif event == "Despawn Ghost Priest 2" :
       npc.reduceCurrentHp(9999999,npc)
       self.tifaren = 0
       AutoChat(npc,"My train of thought is chaotic. It goes back to the beginning of time...")
       if st.getQuestTimer("Despawn Ghost Priest") :
           st.getQuestTimer("Despawn Ghost Priest").cancel()
       return
   elif event == "Soul of Well 1" :
       self.soul = 1
       return
   elif event == "Soul of Well Despawn" :
       npc.reduceCurrentHp(9999999,npc)
       self.well = 0
       return
   return htmltext 

 def onTalk (self,npc,player):
   st = player.getQuestState(qn)
   htmltext = "<html><body>You are either not carrying out your quest or don't meet the criteria.</body></html>" 
   if not st: return htmltext
   npcId = npc.getNpcId()
   state = st.getState()
   id = st.getInt("id")
   ex = st.getInt("ex")
   cond = st.getInt("cond") 
   onlyone = st.getInt("onlyone")
   if state == COMPLETED :
       htmltext = "<html><body>This quest has already been completed.</body></html>"
   elif npcId == INNOCENTIN :
       if state == CREATED :
           st2 = player.getQuestState("21_HiddenTruth")
           if st2 :
               if st2.getState().getName() == Completed :
                   htmltext = "31328-00.htm"
       if id < 5 :
           if st.getQuestItemsCount(CROSS) == 0 :
               st.giveItems(CROSS,1)
               st.set("cond","3")
               htmltext = "31328-01.htm"
           else :
               htmltext = "31328-02.htm"
       elif id == 5 :
           htmltext = "31328-03.htm"
       elif id == 6 :
           htmltext = "31328-05.htm"
       elif id == 7 :
           htmltext = "31328-11.htm"
       elif id == 12 and st.getQuestItemsCount(BOX) > 0 :
           htmltext = "31328-12.htm"
       elif id == 13 :
           htmltext = "31328-14.htm"
       elif id == 14 :
           st.playSound("ItemSound.quest_finish")
           st.setState(COMPLETED)
           st.unset("id")
           if player.getLevel() < 64 :
               htmltext = "31328-23.htm"
           else :
               htmltext = "31328-22.htm"
   elif npcId == TIFAREN :
       if state == CREATED :
           htmltext = "31334-01.htm"
       elif id == 1 :
           htmltext = "31334-05.htm"
       elif id == 2 :
           if st.getQuestItemsCount(CROSS) >= 1 and st.getQuestItemsCount(SKULL) > 0 :
               if self.tifaren == 1 :
                   htmltext = "31334-11.htm"
               else :
                   htmltext = "31334-10.htm"
           else :
               htmltext = "31334-09.htm"
       elif id == 4 :
           if self.tifaren == 1 :
               if str(self.priest) == player.getName() :
                   htmltext = "31334-15.htm"
               else :
                   htmltext = "31334-16.htm"
                   st.set("cond","6")
           else :
               htmltext = "31334-17.htm"
       elif id == 5 :
           htmltext = "31334-18.htm"
   elif npcId == GHOST_PRIEST :
       st.playSound("AmbSound.d_horror_15")
       if str(self.priest) == player.getName() :
           htmltext = "31528-01.htm"
       else :
           htmltext = "31528-02.htm"
   elif npcId == GHOST_ADVENTURER :
       if id == 7 and st.getQuestItemsCount(LETTER) > 0 :
           htmltext = "31529-01.htm"
       elif id == 8 :
           htmltext = "31529-04.htm"
       elif id == 9 :
           htmltext = "31529-11.htm"
       elif id == 10 and st.getQuestItemsCount(JEWEL1) > 0 :
           htmltext = "31529-13.htm"
       elif id == 11 and st.getQuestItemsCount(JEWEL1) > 0 :
           htmltext = "31529-16.htm"
       elif id == 11 and st.getQuestItemsCount(JEWEL2) > 0 :
           if st.getQuestItemsCount(SEALED_BOX) == 0 :
               htmltext = "31529-17.htm"
               st.set("cond","12")
           else :
               st.takeItems(JEWEL2,-1)
               st.takeItems(SEALED_BOX,-1)
               st.giveItems(BOX,1)
               st.set("id","12")
               st.set("cond","14")
               htmltext = "31529-18.htm"
       elif id == 12 :
           htmltext = "31529-19.htm"
   elif npcId == WELL :
       if (id == 10 or id == 11) and st.getQuestItemsCount(JEWEL1) > 0 :
           htmltext = "31527-01.htm"
           st.playSound("AmbSound.dd_horror_01")
       elif id == 11 and st.getQuestItemsCount(JEWEL2) > 0 :
           if st.getQuestItemsCount(SEALED_BOX) == 0 :
               htmltext = "31527-04.htm"
               st.giveItems(SEALED_BOX,1)
               st.set("cond","13")
           else :
               htmltext = "31527-05.htm"
       elif id > 11 :
           htmltext = "31527-05.htm"
   return htmltext

 def onAttack (self,npc,player,damage,isPet):
   st = player.getQuestState(qn)
   if st :
       npcId = npc.getNpcId()
       id = st.getInt("id")
       if npcId == SOUL_OF_WELL :
           if id == 10 and st.getQuestItemsCount(JEWEL1) > 0 :
               st.set("id","11")
           elif id == 11 and st.getQuestItemsCount(JEWEL1) > 0 :
               st.takeItems(JEWEL1,-1)
               st.giveItems(JEWEL2,1)
               st.playSound("ItemSound.quest_itemget")
               st.set("cond","11")
   return

 def onKill(self,npc,player,isPet):
   npcId = npc.getNpcId()
   st = player.getQuestState(qn)
   if st :
       if npcId == SOUL_OF_WELL :
           self.well = 0
       elif npcId in MOBS and st.getState() == State.STARTED: 
           if st.getRandom(10) < 1 and st.getQuestItemsCount(SKULL) < 1:
               st.giveItems(SKULL,1)
               st.playSound("ItemSound.quest_itemget")
               st.set("cond","5")
   return

QUEST     = Quest(22,qn,"Tragedy In Von Hellmann Forest") 
CREATED   = State('Start',     QUEST) 
STARTED   = State('Started',   QUEST) 
COMPLETED = State('Completed', QUEST) 

QUEST.setInitialState(CREATED)
QUEST.addStartNpc(INNOCENTIN)
QUEST.addStartNpc(TIFAREN)

for npcid in NPCS :
    QUEST.addTalkId(npcid)

QUEST.addAttackId(SOUL_OF_WELL)

for mobid in MOBS + [SOUL_OF_WELL] :
    QUEST.addKillId(mobid)