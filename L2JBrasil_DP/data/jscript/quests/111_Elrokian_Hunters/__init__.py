import sys
from com.it.br.gameserver.model.quest import State
from com.it.br.gameserver.model.quest import QuestState
from com.it.br.gameserver.model.quest.jython import QuestJython as JQuest
from com.it.br.gameserver.network.serverpackets import PlaySound
 
qn = "111_Elrokian_Hunters"
 
#NPCS
MARQUEZ,MUSHIKA,ASHAMAH,KIRIKASHIN = xrange(32113,32117)
 
CHANCE      = 25
CHANCE2     = 75
 
#QUEST ITEM
FRAGMENT    = 8768
ADENA       = 57
 
class Quest (JQuest) :
 
 def __init__(self,id,name,descr):
     JQuest.__init__(self,id,name,descr)
     self.Party = []
     self.questItemIds = [FRAGMENT]
 
 def onTalk (self,npc,player):
     htmltext = "<html><body>This quest can only be undertaken by a party of level 75 or higher. Only the party leader may talk to the quest NPCs.</body></html>"
     st = player.getQuestState(qn)
     if not st : return htmltext
     npcId = npc.getNpcId()
     id = st.getState()
     cond = st.getInt("cond")
     if id == COMPLETED :
         htmltext = "<html><body>This quest has already been completed.</body></html>"
     else :
         party = st.getPlayer().getParty()
         if party :
             level = st.getPlayer().getLevel()
             partyleader = st.getPlayer().getParty().getLeader()
             self.PartyLeader = partyleader
             if level >= 75 and partyleader == player:
                 if npcId == MARQUEZ :
                     if cond == 0 :
                         st.set("cond","1")  
                         st.playSound("ItemSound.quest_accept")
                         st.setState(STARTED)
                         htmltext = "32113-1.htm"
                     elif cond == 3 :
                         st.set("cond","4")
                         st.playSound("ItemSound.quest_middle")
                         htmltext = "32113-2.htm"
                     elif cond == 5 :
                         if st.getQuestItemsCount(FRAGMENT) >= 50 :
                             st.takeItems(FRAGMENT,-1)
                             st.set("cond","6")
                             st.playSound("ItemSound.quest_middle")
                             htmltext = "32113-3.htm"
                 elif npcId == KIRIKASHIN :
                     if cond == 6 :
                         st.set("cond","8")
                         st.playSound("EtcSound.elcroki_song_full")
                         htmltext = "32116-1.htm"
                     elif cond == 12 :
                         if st.getQuestItemsCount(8773) >= 1 :
                             st.takeItems(8773,1)
                             st.giveItems(8763,1)
                             st.giveItems(8764,100)
                             st.giveItems(ADENA,1022636)
                             st.playSound("ItemSound.quest_finish")
                             st.exitQuest(False)
                             htmltext = "32116-2.htm"
                             st.setState(COMPLETED)
                 elif npcId == MUSHIKA :
                     if cond == 1 :
                         st.set("cond","2")
                         st.playSound("ItemSound.quest_middle")
                         htmltext = "32114-1.htm"
                 elif npcId == ASHAMAH :
                     if cond == 2 :
                         st.set("cond","3")
                         st.playSound("ItemSound.quest_middle")
                         htmltext = "32115-1.htm"
                     elif cond == 8 :
                         st.set("cond","9")
                         st.playSound("ItemSound.quest_middle")
                         htmltext = "32115-2.htm"
                     elif cond == 9 :
                         st.set("cond","10")
                         st.playSound("ItemSound.quest_middle")
                         htmltext = "32115-3.htm"
                     elif cond == 11 :
                         st.set("cond","12")
                         st.playSound("ItemSound.quest_middle")
                         st.giveItems(8773,1)
                         htmltext = "32115-5.htm"
     return htmltext
 
 def onKill(self,npc,player,isPet):
     party = player.getParty()
     if not party : return
     partyleader = party.getLeader()
     st = partyleader.getQuestState(qn)
     if not st : return
     state = st.getState()
     if state != STARTED : return
     cond = st.getInt("cond")
     npcId = npc.getNpcId()
 
     if (npcId in range(22196,22199) or npcId == 22218) and cond == 4 :
         if st.getRandom(100) < CHANCE:
             st.giveItems(FRAGMENT,1)
             if st.getQuestItemsCount(FRAGMENT) <= 49:
                 st.playSound("ItemSound.quest_itemget")
             else:
                 st.set("cond","5")  
                 st.playSound("ItemSound.quest_middle")
     elif cond == 10 :
         if npcId in range(22200,22203) or npcId == 22219:
             if st.getRandom(100) < CHANCE2:
                 st.giveItems(8770,1)
                 if st.getQuestItemsCount(8770) <= 9:
                     st.playSound("ItemSound.quest_itemget")
         elif npcId in range(22208,22211) or npcId == 22221:
             if st.getRandom(100) < CHANCE2:
                 st.giveItems(8772,1)
                 if st.getQuestItemsCount(8772) <= 9:
                     st.playSound("ItemSound.quest_itemget")
         elif npcId in range(22203,22206) or npcId == 22220:
             if st.getRandom(100) < CHANCE2:
                 st.giveItems(8771,1)
                 if st.getQuestItemsCount(8771) <= 9:
                     st.playSound("ItemSound.quest_itemget")
         if st.getQuestItemsCount(8770) >= 10 and st.getQuestItemsCount(8771) >= 10 and st.getQuestItemsCount(8772) >= 10:
             st.set("cond","11")  
             st.playSound("ItemSound.quest_middle")
     return
 
QUEST       = Quest(111,qn,"Elrokian Hunter's Proof")

CREATED     = State('Start', QUEST)
STARTED     = State('Started', QUEST)
COMPLETED   = State('Completed', QUEST)

QUEST.setInitialState(CREATED)
 
QUEST.addStartNpc(32113)
 
for i in xrange(32113,32117) :
    QUEST.addTalkId(i)
 
for i in range(22196,22199)+range(22200,22206)+range(22208,22211)+range(22218,22222) :
    QUEST.addKillId(i)