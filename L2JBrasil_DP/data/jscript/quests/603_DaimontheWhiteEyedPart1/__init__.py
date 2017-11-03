#Made by Kerb
import sys

from com.it.br import Config
from com.it.br.gameserver.model.quest import State
from com.it.br.gameserver.model.quest import QuestState
from com.it.br.gameserver.model.quest.jython import QuestJython as JQuest

qn = "603_DaimontheWhiteEyedPart1" 

#Npcs
EYE = 31683
TABLE1,TABLE2,TABLE3,TABLE4,TABLE5 = range(31548,31553)
#Items
EVIL_SPIRIT,BROKEN_CRYSTAL,U_SUMMON = range (7190,7193)
#Mobs
BUFFALO = 21299
BANDERSNATCH = 21297
GRENDEL = 21304
#Chance
DROP_CHANCE = 100

class Quest (JQuest) :
 def __init__(self,id,name,descr):
 	JQuest.__init__(self,id,name,descr)
 	self.questItemIds = [EVIL_SPIRIT,BROKEN_CRYSTAL]

 def onEvent (self,event,st) :
   cond = st.getInt("cond")
   htmltext = event
   if event == "31683-02.htm" :
      if st.getPlayer().getLevel() < 73 : 
         htmltext = "31683-01a.htm"
         st.exitQuest(1)
      else :
         st.set("cond","1")
         st.setState(STARTED)
         st.playSound("ItemSound.quest_accept")
   elif event == "31548-02.htm" :
      st.set("cond","2")
      st.playSound("ItemSound.quest_middle")
      st.giveItems(BROKEN_CRYSTAL,1)
   elif event == "31549-02.htm" :
      st.set("cond","3")
      st.playSound("ItemSound.quest_middle")
      st.giveItems(BROKEN_CRYSTAL,1)
   elif event == "31550-02.htm" :
      st.set("cond","4")
      st.playSound("ItemSound.quest_middle")
      st.giveItems(BROKEN_CRYSTAL,1)
   elif event == "31551-02.htm" :
      st.set("cond","5")
      st.playSound("ItemSound.quest_middle")
      st.giveItems(BROKEN_CRYSTAL,1)
   elif event == "31552-02.htm" :
      st.set("cond","6")
      st.playSound("ItemSound.quest_middle")
      st.giveItems(BROKEN_CRYSTAL,1)
   elif event == "31683-04.htm" :
      if st.getQuestItemsCount(BROKEN_CRYSTAL) < 5 :
          htmltext = "31683-08.htm"
      else :
          st.set("cond","7")
          st.takeItems(BROKEN_CRYSTAL,-1)
          st.playSound("ItemSound.quest_middle")
   elif event == "31683-07.htm" :
      if st.getQuestItemsCount(EVIL_SPIRIT) < 200 :
          htmltext = "31683-09.htm"
      else :
          st.takeItems(EVIL_SPIRIT,-1)
          st.giveItems(U_SUMMON,1)
          st.playSound("ItemSound.quest_finish")
          st.exitQuest(1)
   return htmltext


 def onTalk (self,npc,player):
   htmltext = "<html><body>You are either not on a quest that involves this NPC, or you don't meet this NPC's minimum quest requirements.</body></html>"
   st = player.getQuestState(qn)
   if st :
     npcId = npc.getNpcId()
     id = st.getState()
     cond = st.getInt("cond")
     if cond == 0 :
       if npcId == EYE :
         htmltext = "31683-01.htm"
     elif cond == 1 :
       if npcId == EYE :
         htmltext = "31683-02a.htm"
       elif npcId == TABLE1 :
         htmltext = "31548-01.htm"
     elif cond == 2 :
       if npcId == EYE :
         htmltext = "31683-02a.htm"
       elif npcId == TABLE2 :
         htmltext = "31549-01.htm"
       else:
         htmltext = "table-no.htm"
     elif cond == 3 :
       if npcId == EYE :
         htmltext = "31683-02a.htm"
       elif npcId == TABLE3 :
         htmltext = "31550-01.htm"
       else:
         htmltext = "table-no.htm"
     elif cond == 4 :
       if npcId == EYE :
         htmltext = "31683-02a.htm"
       elif npcId == TABLE4 :
         htmltext = "31551-01.htm"
       else:
         htmltext = "table-no.htm"
     elif cond == 5 :
       if npcId == EYE :
         htmltext = "31683-02a.htm"
       elif npcId == TABLE5 :
         htmltext = "31552-01.htm"
       else:
         htmltext = "table-no.htm"
     elif cond == 6 :
       if npcId == EYE :
         htmltext = "31683-03.htm"
       else:
         htmltext = "table-no.htm"
     elif cond == 7 :
       if npcId == EYE :
         htmltext = "31683-05.htm"
     elif cond == 8 :
       if npcId == EYE :
           htmltext = "31683-06.htm"
   return htmltext

 def onKill(self,npc,player,isPet):
     partyMember = self.getRandomPartyMember(player,"7")
     if not partyMember: return
     st = partyMember.getQuestState(qn)
     if st :
         count = st.getQuestItemsCount(EVIL_SPIRIT)
         if st.getInt("cond") == 7 and count < 200 :
            chance = DROP_CHANCE * Config.RATE_DROP_QUEST
            numItems, chance = divmod(chance,100)
            if st.getRandom(100) < chance : 
               numItems += 1
            if numItems :
               if count + numItems >= 200 :
                  numItems = 200 - count
                  st.playSound("ItemSound.quest_middle")
                  st.set("cond","8")
               else:
                  st.playSound("ItemSound.quest_itemget")   
               st.giveItems(EVIL_SPIRIT,int(numItems))
     return

QUEST = Quest(603,qn,"Daimon the White-Eyed - Part 1")
CREATED     = State('Start',QUEST)
STARTED     = State('Started',QUEST)

QUEST.setInitialState(CREATED)

QUEST.addStartNpc(EYE)

QUEST.addTalkId(EYE)
QUEST.addTalkId(TABLE1)
QUEST.addTalkId(TABLE2)
QUEST.addTalkId(TABLE3)
QUEST.addTalkId(TABLE4)
QUEST.addTalkId(TABLE5)

QUEST.addKillId(BUFFALO)
QUEST.addKillId(BANDERSNATCH)
QUEST.addKillId(GRENDEL)