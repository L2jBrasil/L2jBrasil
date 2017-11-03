#Made by Kerb
import sys 

from java.lang import System
from com.it.br import Config
from com.it.br.gameserver.model.quest import State
from com.it.br.gameserver.model.quest import QuestState
from com.it.br.gameserver.model.quest.jython import QuestJython as JQuest
from com.it.br.gameserver.network.serverpackets import CreatureSay
from com.it.br.util import Rnd

qn = "604_DaimontheWhiteEyedPart2" 
#Npcs 
EYE = 31683 
ALTAR = 31541 
#RaidBoss 
DAIMON = 25290 
#Items 
U_SUMMON,S_SUMMON,ESSENCE = range(7192,7195) 
#Rewards dye +2int-2men/+2int-2wit/+2men-2int/+2men-2wit/+2wit-2int/+2wit-2men 
REWARDS = range(4595,4601) 


def AutoChat(npc,text) : 
    chars = npc.getKnownList().getKnownPlayers().values().toArray()
    if chars != None:
       for pc in chars :
          sm = CreatureSay(npc.getObjectId(),0,npc.getName(), text)
          pc.sendPacket(sm)

class Quest (JQuest) : 
 def __init__(self,id,name,descr):
     JQuest.__init__(self,id,name,descr)
     self.questItemIds = range(7193,7195)
     test = self.loadGlobalQuestVar("604_respawn")
     if test.isdigit() :
        remain = long(test) - System.currentTimeMillis()
        if remain <= 0 :
           self.addSpawn(31541,186304,-43744,-3193,0, False, 57000)
        else :
           self.startQuestTimer("spawn_npc", remain, None, None)
     else :
        self.addSpawn(31541,186304,-43744,-3193,0, False, 57000)
     

 def onAdvEvent (self, event, npc, player) :
   if event == "Daimon the White-Eyed has despawned" : 
      npc.reduceCurrentHp(9999999,npc)
      AutoChat(npc,"Darkness could not have ray?")
      self.addSpawn(31541,186304,-43744,-3193,0, False, 57000)
      return
   elif event == "spawn_npc" :
      self.addSpawn(31541,186304,-43744,-3193,0, False, 57000)
      return
   st = player.getQuestState(qn)
   if not st: return
   cond = st.getInt("cond") 
   htmltext = event 
   if event == "31683-02.htm" : 
      if st.getPlayer().getLevel() < 73 : 
         htmltext = "31683-00b.htm" 
         st.exitQuest(1) 
      else: 
         st.set("cond","1") 
         st.setState(STARTED) 
         st.takeItems(U_SUMMON,1) 
         st.giveItems(S_SUMMON,1) 
         st.playSound("ItemSound.quest_accept") 
   elif event == "31541-02.htm" :
       if st.getQuestItemsCount(S_SUMMON) == 0 :
           htmltext = "31541-04.htm"
       else:
         spawnId = st.addSpawn(DAIMON,186320,-43904,-3175) 
         npc.deleteMe()
         st.takeItems(S_SUMMON,1) 
         st.set("cond","2") 
         self.startQuestTimer("Daimon the White-Eyed has despawned",1200000,spawnId,None) 
         AutoChat(spawnId,"Who called me?") 
   elif event == "31683-04.htm" : 
      if st.getQuestItemsCount(ESSENCE) >= 1 : 
         st.takeItems(ESSENCE,1) 
         st.giveItems(REWARDS[st.getRandom(len(REWARDS))],5) 
         st.playSound("ItemSound.quest_finish")
         st.exitQuest(1) 
         htmltext = "31683-04.htm"
      else:
         htmltext = "31683-05.htm" 
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
         if st.getQuestItemsCount(U_SUMMON) >= 1 : 
           htmltext = "31683-01.htm" 
         else: 
           htmltext = "31683-00a.htm" 
     elif cond == 1 : 
       if npcId == EYE : 
         htmltext = "31683-02a.htm" 
       if npcId == ALTAR : 
         htmltext = "31541-01.htm" 
     elif cond == 2 : 
       if npcId == ALTAR :
         htmltext = "31541-01.htm" 
     elif cond == 3 : 
       if npcId == EYE :
            if st.getQuestItemsCount(ESSENCE) >= 1 :
                htmltext = "31683-03.htm"
            else :
                htmltext = "31683-06.htm"
       if npcId == ALTAR : 
         htmltext = "31541-05.htm" 
     return htmltext 

 def onKill(self,npc,player,isPet):
     npcId = npc.getNpcId()
     if npcId == DAIMON :
        respawnMinDelay = 43200000  * int(Config.RAID_MIN_RESPAWN_MULTIPLIER)
        respawnMaxDelay = 129600000 * int(Config.RAID_MAX_RESPAWN_MULTIPLIER)
        respawn_delay = Rnd.get(respawnMinDelay,respawnMaxDelay)
        self.saveGlobalQuestVar("604_respawn", str(System.currentTimeMillis()+respawn_delay))
        self.startQuestTimer("spawn_npc", respawn_delay, None, None)
        self.cancelQuestTimer("Daimon the White-Eyed has despawned",npc,None)
        party = player.getParty()
        if party :
            PartyQuestMembers = []
            for player1 in party.getPartyMembers().toArray() :
                st1 = player1.getQuestState(qn)
                if st1 :
                    if st1.getState() == STARTED and (st1.getInt("cond") == 1 or st1.getInt("cond") == 2) :
                        PartyQuestMembers.append(st1)
            if len(PartyQuestMembers) == 0 : return
            st = PartyQuestMembers[Rnd.get(len(PartyQuestMembers))]
            if st.getQuestItemsCount(S_SUMMON) > 0 :
                st.takeItems(S_SUMMON,1)
            st.giveItems(ESSENCE,1) 
            st.set("cond","3") 
            st.playSound("ItemSound.quest_middle")
        else :
            st = player.getQuestState(qn)
            if not st : return
            if st.getState() == STARTED and (st.getInt("cond") == 1 or st.getInt("cond") == 2) :
                if st.getQuestItemsCount(S_SUMMON) > 0 :
                    st.takeItems(S_SUMMON,1)
                st.giveItems(ESSENCE,1) 
                st.set("cond","3") 
                st.playSound("ItemSound.quest_middle")
     return


QUEST = Quest(604,qn,"Daimon the White-Eyed - Part 2")
CREATED     = State('Start',QUEST)
STARTED     = State('Started',QUEST)

QUEST.setInitialState(CREATED)

QUEST.addStartNpc(EYE) 

QUEST.addTalkId(EYE) 
QUEST.addTalkId(ALTAR) 

QUEST.addKillId(DAIMON)