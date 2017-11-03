# Made by Emperorc
import sys
from com.it.br.gameserver.model.quest.jython import QuestJython as JQuest
from com.it.br.gameserver.model.quest import State
from com.it.br.gameserver.model.quest import QuestState
from com.it.br.gameserver.network.serverpackets import CreatureSay
from com.it.br.gameserver.datatables.sql import SpawnTable
from com.it.br.gameserver.model import L2Spawn
from com.it.br.gameserver.ai import CtrlIntention
from com.it.br.gameserver.ai import CtrlEvent
from com.it.br.gameserver.network.serverpackets import MagicSkillUser
from com.it.br.gameserver.model import L2World
from java.util import Iterator
from com.it.br.util import Rnd

qn = "SagasSuperclass"
Archon_Minions = range(21646,21652)
Guardian_Angels = [27214, 27215, 27216]
Archon_Hellisha_Norm = [18212, 18214, 18215, 18216, 18218]
Mobs_Norm = Guardian_Angels+Archon_Minions+Archon_Hellisha_Norm
Quests = {
"70":"70_SagaOfThePhoenixKnight",
"71":"71_SagaOfEvasTemplar",
"72":"72_SagaOfTheSwordMuse",
"73":"73_SagaOfTheDuelist",
"74":"74_SagaOfTheDreadnoughts",
"75":"75_SagaOfTheTitan",
"76":"76_SagaOfTheGrandKhavatari",
"77":"77_SagaOfTheDominator",
"78":"78_SagaOfTheDoomcryer",
"79":"79_SagaOfTheAdventurer",
"80":"80_SagaOfTheWindRider",
"81":"81_SagaOfTheGhostHunter",
"82":"82_SagaOfTheSagittarius",
"83":"83_SagaOfTheMoonlightSentinel",
"84":"84_SagaOfTheGhostSentinel",
"85":"85_SagaOfTheCardinal",
"86":"86_SagaOfTheHierophant",
"87":"87_SagaOfEvasSaint",
"88":"88_SagaOfTheArchmage",
"89":"89_SagaOfTheMysticMuse",
"90":"90_SagaOfTheStormScreamer",
"91":"91_SagaOfTheArcanaLord",
"92":"92_SagaOfTheElementalMaster",
"93":"93_SagaOfTheSpectralMaster",
"94":"94_SagaOfTheSoultaker",
"95":"95_SagaOfTheHellKnight",
"96":"96_SagaOfTheSpectralDancer",
"97":"97_SagaOfTheShillienTemplar",
"98":"98_SagaOfTheShillienSaint",
"99":"99_SagaOfTheFortuneSeeker",
"100":"100_SagaOfTheMaestro"
}
QuestClass = [0x05,0x14,0x15,0x02,0x03,0x2e,0x30,0x33,0x34,0x08,0x17,0x24,0x09,0x18,0x25,0x10,0x11,0x1e,0x0c,0x1b,0x28,0x0e,0x1c,0x29,0x0d,0x06,0x22,0x21,0x2b,0x37,0x39]
PartyQuestMembers = []

class Quest (JQuest) :

 def __init__(self,id,name,descr):
     JQuest.__init__(self,id,name,descr)
     # All of these are overridden in the subclasses
     self.NPC = [0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10]
     self.Items = [0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10]
     self.Mob = [0, 1, 2]
     self.classid = 0
     self.prevclass = 0
     self.qn = "SagasSuperclass"
     self.X = [0, 1, 2]
     self.Y = [0, 1, 2]
     self.Z = [0, 1, 2]
     self.Text = [0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17]
     self.Spawn_List = []
     #all these are not overridden by the subclasses (either cause they are constant or used only for this script)
     self.CREATED     = State('Start', self)
     self.STARTED     = State('Started', self)
     self.COMPLETED   = State('Completed', self)

 # this function is called by subclasses in order to add their own NPCs
 def registerNPCs(self) :
     self.addStartNpc(self.NPC[0])
     self.addAttackId(self.Mob[2])
     self.addFirstTalkId(self.NPC[4])
     for npc in self.NPC :
         self.addTalkId(npc)
     for mobid in self.Mob :
         self.addKillId(mobid)

 def Cast(self, npc,target,skillId,level):
    target.broadcastPacket(MagicSkillUser(target,target,skillId,level,6000,1))
    target.broadcastPacket(MagicSkillUser(npc,npc,skillId,level,6000,1))

 def FindTemplate (self, npcId) :
    for spawn in SpawnTable.getInstance().getSpawnTable().values():
        if spawn.getNpcId() == npcId:
            npcinstance = spawn.getLastSpawn()
            break
    return npcinstance

 def AutoChat(self, npc,text) :
    chars = npc.getKnownList().getKnownPlayers().values().toArray()
    if chars != None:
       for pc in chars :
          sm = CreatureSay(npc.getObjectId(), 0, npc.getName(), text)
          pc.sendPacket(sm)

 def AddSpawn(self, st,mob) :
    name = st.getPlayer().getName()
    self.Spawn_List.append([mob.getObjectId(),name,mob])
    return

 def FindSpawn (self, player, npcObjectId) :
    for mobId, playerName, mob in self.Spawn_List:
        if mobId == npcObjectId and playerName == player.getName():
            return mob
    return None

 def DeleteSpawn(self, st,mobid) :
    name = st.getPlayer().getName()
    for npcId,playerName,mob in self.Spawn_List:
        if (mobid,name) ==  (npcId,playerName):
            self.Spawn_List.remove([mobid,name,mob])
            mob.decayMe()
            return
    return

 def giveHallishaMark(self, st2) :
     if st2.getInt("spawned") == 0 :
        if st2.getQuestItemsCount(self.Items[3]) >= 700:
            st2.takeItems(self.Items[3],20)
            xx = int(st2.getPlayer().getX())
            yy = int(st2.getPlayer().getY())
            zz = int(st2.getPlayer().getZ())
            Archon = st2.addSpawn(self.Mob[1],xx,yy,zz)
            ArchonId = Archon.getObjectId()
            st2.set("Archon",str(ArchonId))
            self.AddSpawn(st2,Archon)
            st2.set("spawned","1")
            st2.startQuestTimer("Archon Hellisha has despawned",600000,Archon)
            self.AutoChat(Archon,self.Text[13].replace('PLAYERNAME',st2.getPlayer().getName()))
            Archon.addDamageHate(st2.getPlayer(),0,99999)
            Archon.getAI().setIntention(CtrlIntention.AI_INTENTION_ATTACK,st2.getPlayer(),None)
        else :
            st2.giveItems(self.Items[3],1)
     return

 def findRightState(self, player,mob) :
     mobid = mob.getObjectId()
     name = player.getName()
     st1 = None
     if [mobid,name] in self.Spawn_List :
         st1 = L2World.getInstance().getPlayer(name)
     else :
         for entry in self.Spawn_List :
             if entry[0] == mobid :
                 name = entry[1]
                 st1 = L2World.getInstance().getPlayer(name)
                 break
     if st1 :
         return st1.getQuestState(self.qn)
     return st1

 def onAdvEvent (self,event,npc, player) :
   st = player.getQuestState(self.qn)
   if not st: return
   htmltext = ""  # simple initialization...if none of the events match, return nothing.  
   cond = st.getInt("cond")
   id = st.getInt("id")
   player = st.getPlayer()
   if event == "accept" :
       st.set("cond","1")
       st.setState(self.STARTED)
       st.playSound("ItemSound.quest_accept")
       st.giveItems(self.Items[10],1)
       htmltext = "0-03.htm"
   elif event == "0-1" :
       if player.getLevel() < 76 :
           htmltext = "0-02.htm"
           st.exitQuest(1)
       else :
           htmltext = "0-05.htm"
   elif event == "0-2" :
       if player.getLevel() >= 76 :
           st.setState(self.COMPLETED)
           st.set("cond","0")
           htmltext = "0-07.htm"
           st.takeItems(self.Items[10],-1)
           st.addExpAndSp(2299404,0)
           st.giveItems(57,5000000)
           st.giveItems(6622,1)
           player.setClassId(self.classid)
           if not player.isSubClassActive() and player.getBaseClass() == self.prevclass :
               player.setBaseClass(self.classid)
           player.broadcastUserInfo()
           self.Cast(self.FindTemplate(self.NPC[0]),player,4339,1)
       else :
           st.takeItems(self.Items[10],-1)
           st.playSound("ItemSound.quest_middle")
           st.set("cond","20")
           htmltext = "0-08.htm"
   elif event == "1-3" :
       st.set("cond","3")
       htmltext = "1-05.htm"
   elif event == "1-4" :
       st.set("cond","4")
       st.takeItems(self.Items[0],1)
       if self.Items[11] != 0 :
           st.takeItems(self.Items[11],1)
       st.giveItems(self.Items[1],1)
       htmltext = "1-06.htm" 
   elif event == "2-1" :
       st.set("cond","2")
       htmltext = "2-05.htm"
   elif event == "2-2" :
       st.set("cond","5")
       st.takeItems(self.Items[1],1)
       st.giveItems(self.Items[4],1)
       htmltext = "2-06.htm"
   elif event == "3-5" :
       htmltext = "3-07.htm"
   elif event == "3-6" :
       st.set("cond","11")
       htmltext = "3-02.htm"
   elif event == "3-7" :
       st.set("cond","12")
       htmltext = "3-03.htm"
   elif event == "3-8" :
       st.set("cond","13")
       st.takeItems(self.Items[2],1)
       st.giveItems(self.Items[7],1)
       htmltext = "3-08.htm"
   elif event == "4-1" :
       htmltext = "4-010.htm"
   elif event == "4-2" :
       st.giveItems(self.Items[9],1)
       st.set("cond","18")
       st.playSound("ItemSound.quest_middle")
       htmltext = "4-011.htm"
   elif event == "4-3" :
       st.giveItems(self.Items[9],1)
       st.set("cond","18")
       Mob_2 = self.FindSpawn(player, st.getInt("Mob_2"))
       self.AutoChat(Mob_2,self.Text[13].replace('PLAYERNAME',player.getName()))
       st.set("Quest0","0")
       self.DeleteSpawn(st,Mob_2.getObjectId())
       st.playSound("ItemSound.quest_middle")
       return
   elif event == "5-1" :
       st.set("cond","6")
       st.takeItems(self.Items[4],1)
       self.Cast(self.FindTemplate(self.NPC[5]),player,4546,1)
       st.playSound("ItemSound.quest_middle")
       htmltext =  "5-02.htm"
   elif event == "6-1" :
       st.set("cond","8")
       st.takeItems(self.Items[5],1)
       self.Cast(self.FindTemplate(self.NPC[6]),player,4546,1)
       st.playSound("ItemSound.quest_middle")
       htmltext =  "6-03.htm"
   elif event == "7-1" :
       if st.getInt("spawned") == 1 :
           htmltext = "7-03.htm"
       elif st.getInt("spawned") == 0 :
           Mob_1 = st.addSpawn(self.Mob[0],self.X[0],self.Y[0],self.Z[0])
           st.set("Mob_1",str(Mob_1.getObjectId()))
           st.set("spawned","1")
           st.startQuestTimer("Mob_1 Timer 1",500,Mob_1)
           st.startQuestTimer("Mob_1 has despawned",300000,Mob_1)
           self.AddSpawn(st,Mob_1)
           htmltext = "7-02.htm"
       else :
           htmltext = "7-04.htm"
   elif event == "7-2" :
       st.set("cond","10")
       st.takeItems(self.Items[6],1)
       self.Cast(self.FindTemplate(self.NPC[7]),player,4546,1)
       st.playSound("ItemSound.quest_middle")
       htmltext = "7-06.htm"
   elif event == "8-1" :
       st.set("cond","14")
       st.takeItems(self.Items[7],1)
       self.Cast(self.FindTemplate(self.NPC[8]),player,4546,1)
       st.playSound("ItemSound.quest_middle")
       htmltext = "8-02.htm"
   elif event == "9-1" :
       st.set("cond","17")
       st.takeItems(self.Items[8],1)
       self.Cast(self.FindTemplate(self.NPC[9]),player,4546,1)
       st.playSound("ItemSound.quest_middle")
       htmltext = "9-03.htm"
   elif event == "10-1" :
       if st.getInt("Quest0") == 0 :
           Mob_3 = st.addSpawn(self.Mob[2],self.X[1],self.Y[1],self.Z[1])
           Mob_2 = st.addSpawn(self.NPC[4],self.X[2],self.Y[2],self.Z[2])
           self.AddSpawn(st,Mob_3)
           self.AddSpawn(st,Mob_2)
           st.set("Mob_3",str(Mob_3.getObjectId()))
           st.set("Mob_2",str(Mob_2.getObjectId()))
           st.set("Quest0","1")
           st.set("Quest1","45")
           st.startQuestTimer("Mob_3 Timer 1",500,Mob_3)
           st.startQuestTimer("Mob_3 has despawned",59000,Mob_3)
           st.startQuestTimer("Mob_2 Timer 1",500,Mob_2)
           st.startQuestTimer("Mob_2 has despawned",60000,Mob_2)
           htmltext = "10-02.htm"
       elif st.getInt("Quest1") == 45 :
           htmltext = "10-03.htm"
       else :
           htmltext = "10-04.htm"
   elif event == "10-2" :
       st.set("cond","19")
       st.takeItems(self.Items[9],1)
       self.Cast(self.FindTemplate(self.NPC[10]),player,4546,1)
       st.playSound("ItemSound.quest_middle")
       htmltext = "10-06.htm"
   elif event == "11-9" :
       st.set("cond","15")
       htmltext = "11-03.htm"
   elif event == "Mob_1 Timer 1" :
       self.AutoChat(npc,self.Text[0].replace('PLAYERNAME',player.getName()))
       return
   elif event == "Mob_1 has despawned" :
       self.AutoChat(npc,self.Text[1].replace('PLAYERNAME',player.getName()))
       self.DeleteSpawn(st,npc.getObjectId())
       st.set("spawned","0")
       return
   elif event == "Archon of Hellisha has despawned" :
       self.AutoChat(npc,self.Text[6].replace('PLAYERNAME',player.getName()))
       self.DeleteSpawn(st,npc.getObjectId())
       st.set("spawned","0")
       return
   elif event == "Mob_3 Timer 1" :
       Mob_2 = self.FindSpawn(player,st.getInt("Mob_2"))
       if npc.getKnownList().knowsObject(Mob_2) :
           npc.addDamageHate(Mob_2,0,99999)
           npc.getAI().setIntention(CtrlIntention.AI_INTENTION_ATTACK,Mob_2,None)
           #Mob_2.addDamageHate(npc,0,99999)
           Mob_2.getAI().setIntention(CtrlIntention.AI_INTENTION_ATTACK,npc,None)
           self.AutoChat(npc,self.Text[14].replace('PLAYERNAME',player.getName()))
       else :
           st.startQuestTimer("Mob_3 Timer 1",500,npc)
       return
   elif event == "Mob_3 has despawned" :
       self.AutoChat(npc,self.Text[15].replace('PLAYERNAME',player.getName()))
       st.set("Quest0","2")
       npc.reduceCurrentHp(9999999,npc)
       self.DeleteSpawn(st,npc.getObjectId())
       return
   elif event == "Mob_2 Timer 1" :
       self.AutoChat(npc,self.Text[7].replace('PLAYERNAME',player.getName()))
       st.startQuestTimer("Mob_2 Timer 2",1500,npc)
       if st.getInt("Quest1") == 45 :
           st.set("Quest1","0")
       return
   elif event == "Mob_2 Timer 2" :
       self.AutoChat(npc,self.Text[8].replace('PLAYERNAME',player.getName()))
       st.startQuestTimer("Mob_2 Timer 3",10000,npc)
       return
   elif event == "Mob_2 Timer 3" :
       if st.getInt("Quest0") == 0 :
           st.startQuestTimer("Mob_2 Timer 3",13000,npc)
           if st.getRandom(2) == 0 :
               self.AutoChat(npc,self.Text[9].replace('PLAYERNAME',player.getName()))
           else :
               self.AutoChat(npc,self.Text[10].replace('PLAYERNAME',player.getName()))
       return
   elif event == "Mob_2 has despawned" :
       st.set("Quest1",str(st.getInt("Quest1")+1))
       if st.getInt("Quest0") == 1 or st.getInt("Quest0") == 2 or st.getInt("Quest1") > 3 :
           st.set("Quest0","0")
           if st.getInt("Quest0") == 1 :
               self.AutoChat(npc,self.Text[11].replace('PLAYERNAME',player.getName()))
           else :
               self.AutoChat(npc,self.Text[12].replace('PLAYERNAME',player.getName()))
           npc.reduceCurrentHp(9999999,npc)
           self.DeleteSpawn(st,npc.getObjectId())
       else :
           st.startQuestTimer("Mob_2 has despawned",1000,npc)
       return
   return htmltext

 def onTalk (self,npc,player):
    htmltext = "<html><body>You are either not carrying out your quest or don't meet the criteria.</body></html>"
    st = player.getQuestState(self.qn)
    if st :
      npcId = npc.getNpcId()
      cond = st.getInt("cond")
      if st.getState() == self.COMPLETED and npcId == self.NPC[0] :
          htmltext == "<html><body>You have already completed this quest!</body></html>"
      elif player.getClassId().getId() == self.prevclass :
          if cond == 0 :
              if npcId == self.NPC[0]:
                  htmltext = "0-01.htm"
          elif cond == 1 :
              if npcId == self.NPC[0] :
                  htmltext = "0-04.htm"
              elif npcId == self.NPC[2] :
                  htmltext = "2-01.htm"
          elif cond == 2 :
              if npcId == self.NPC[2] :
                  htmltext = "2-02.htm"
              elif npcId == self.NPC[1] :
                  htmltext = "1-01.htm"
          elif cond == 3 :
              if npcId == self.NPC[1] :
                  if st.getQuestItemsCount(self.Items[0]) :
                      if self.Items[11] == 0 :
                          htmltext = "1-03.htm"
                      elif st.getQuestItemsCount(self.Items[11]) :
                          htmltext = "1-03.htm"
                      else :
                          htmltext = "1-02.htm"
                  else :
                      htmltext = "1-02.htm"
          elif cond == 4 :
              if npcId == self.NPC[1] :
                  htmltext = "1-04.htm"
              elif npcId == self.NPC[2] :
                  htmltext = "2-03.htm"
          elif cond == 5 :
              if npcId == self.NPC[2] :
                  htmltext = "2-04.htm"
              elif npcId == self.NPC[5] :
                  htmltext = "5-01.htm"
          elif cond == 6 :
              if npcId == self.NPC[5] :
                  htmltext = "5-03.htm"
              elif npcId == self.NPC[6] :
                  htmltext = "6-01.htm"
          elif cond == 7 :
              if npcId == self.NPC[6] :
                  htmltext = "6-02.htm"
          elif cond == 8 :
              if npcId == self.NPC[6] :
                  htmltext = "6-04.htm"
              elif npcId == self.NPC[7] :
                  htmltext = "7-01.htm"
          elif cond == 9 :
              if npcId == self.NPC[7] :
                  htmltext = "7-05.htm"
          elif cond == 10 :
              if npcId == self.NPC[7] :
                  htmltext = "7-07.htm"
              elif npcId == self.NPC[3] :
                  htmltext = "3-01.htm"
          elif cond == 11 or cond == 12 :
              if npcId == self.NPC[3] :
                  if st.getQuestItemsCount(self.Items[2]) :
                      htmltext = "3-05.htm"
                  else :
                      htmltext = "3-04.htm"
          elif cond == 13 :
              if npcId == self.NPC[3] :
                  htmltext = "3-06.htm"
              elif npcId == self.NPC[8] :
                  htmltext = "8-01.htm"
          elif cond == 14 :
              if npcId == self.NPC[8] :
                  htmltext = "8-03.htm"
              elif npcId == self.NPC[11] :
                  htmltext = "11-01.htm"
          elif cond == 15 :
              if npcId == self.NPC[11] :
                  htmltext = "11-02.htm"
              elif npcId == self.NPC[9] :
                  htmltext = "9-01.htm"
          elif cond == 16 :
              if npcId == self.NPC[9] :
                  htmltext = "9-02.htm"
          elif cond == 17 :
              if npcId == self.NPC[9] :
                  htmltext = "9-04.htm"
              elif npcId == self.NPC[10] :
                  htmltext = "10-01.htm"   
          elif cond == 18 :
              if npcId == self.NPC[10] :
                  htmltext = "10-05.htm"
          elif cond == 19 :
              if npcId == self.NPC[10] :
                  htmltext = "10-07.htm"
              if npcId == self.NPC[0] :
                  htmltext = "0-06.htm"
          elif cond == 20 :
              if npcId == self.NPC[0] :
                  if player.getLevel() >= 76 :
                      st.setState(self.COMPLETED)
                      st.set("cond","0")
                      htmltext = "0-07.htm"
                      st.addExpAndSp(2299404,0)
                      st.giveItems(57,5000000)
                      st.giveItems(6622,1)
                      player.setClassId(self.classid)
                      if not player.isSubClassActive() and player.getBaseClass() == self.prevclass :
                          player.setBaseClass(self.classid)
                      player.broadcastUserInfo()
                      self.Cast(self.FindTemplate(self.NPC[0]),player,4339,1)
                  else :
                      htmltext = "0-010.htm"
    return htmltext

 def onFirstTalk (self,npc,player):
    htmltext = ""
    st = player.getQuestState(self.qn)
    npcId = npc.getNpcId()
    if st :
      cond = st.getInt("cond")
      if npcId == self.NPC[4] :
          if cond == 17 :
              st2 = self.findRightState(player,npc)
              if st2 :
                  if st == st2 :
                      if st.getInt("Tab") == 1 :
                          if st.getInt("Quest0") == 0 :
                              htmltext = "4-04.htm"
                          elif st.getInt("Quest0") == 1 :
                              htmltext = "4-06.htm"
                      else :
                          if st.getInt("Quest0") == 0 :
                              htmltext = "4-01.htm"
                          elif st.getInt("Quest0") == 1 :
                              htmltext = "4-03.htm"
                  else:
                      if st.getInt("Tab") == 1 :
                          if st.getInt("Quest0") == 0 :
                              htmltext = "4-05.htm"
                          elif st.getInt("Quest0") == 1 :
                              htmltext = "4-07.htm"
                      else :
                          if st.getInt("Quest0") == 0 :
                              htmltext = "4-02.htm"
          elif cond == 18 :
              htmltext = "4-08.htm"
    return htmltext

 def onAttack (self, npc, player, damage, isPet):
   st = player.getQuestState(self.qn)
   if st :
    if st.getInt("cond") == 17 :
        if npc.getNpcId() == self.Mob[2] :
            st2 = self.findRightState(player,npc)
            if st == st2 :
                st.set("Quest0",str(st.getInt("Quest0")+1))
                if st.getInt("Quest0") == 1 :
                    self.AutoChat(npc,self.Text[16].replace('PLAYERNAME',player.getName()))
                if st.getInt("Quest0") > 15 :
                    st.set("Quest0","1")
                    self.AutoChat(npc,self.Text[17].replace('PLAYERNAME',player.getName()))
                    npc.reduceCurrentHp(9999999,npc)
                    self.DeleteSpawn(st,st.getInt("Mob_3"))
                    st.getQuestTimer("Mob_3 has despawned").cancel()
                    st.set("Tab","1")
   return

 def onKill(self,npc,player,isPet):
    npcId = npc.getNpcId()
    st = player.getQuestState(self.qn)
    if npcId in Archon_Minions :
        party = player.getParty()
        if party :
            PartyQuestMembers = []
            for player1 in party.getPartyMembers().toArray() :
                for q in Quests.keys() :
                    st1 = player1.getQuestState(Quests[q])
                    if st1 :
                        if player1.getClassId().getId() == QuestClass[int(q)-70]:
                            if st1.getInt("cond") == 15 :
                                PartyQuestMembers.append(st1)
                                break
            if len(PartyQuestMembers) > 0 :
                st2 = PartyQuestMembers[Rnd.get(len(PartyQuestMembers))]
                st2.getQuest().giveHallishaMark(st2)
        else :
            for q in Quests.keys() :
                st1 = player.getQuestState(Quests[q])
                if st1 :
                    if player.getClassId().getId() == QuestClass[int(q)-70]:
                        if st1.getInt("cond") == 15 :
                            st1.getQuest().giveHallishaMark(st1)
                            break
    elif npcId in Archon_Hellisha_Norm :
        for q in Quests.keys() :
            st1 = player.getQuestState(Quests[q])
            if st1 :
                if player.getClassId().getId() == QuestClass[int(q)-70]:
                    if st1.getInt("cond") == 15 :
                        #This is just a guess....not really sure what it actually says, if anything
                        self.AutoChat(npc,st1.getQuest().Text[4].replace('PLAYERNAME',st1.getPlayer().getName()))
                        st1.giveItems(st1.getQuest().Items[8],1)
                        st1.takeItems(st1.getQuest().Items[3],-1)
                        st1.set("cond","16")
                        st1.playSound("ItemSound.quest_middle")
                        break
    elif npcId in Guardian_Angels :
        for q in Quests.keys() :
            st1 = player.getQuestState(Quests[q])
            if st1 :
                if player.getClassId().getId() == QuestClass[int(q)-70]:
                    if st1.getInt("cond") == 6 :
                        if st1.getInt("kills") < 9 :
                            st1.set("kills",str(st1.getInt("kills")+1))
                        else :
                            st1.playSound("ItemSound.quest_middle")
                            st1.giveItems(st1.getQuest().Items[5],1)
                            st1.set("cond","7")
                        break
    elif st :
        cond = st.getInt("cond")
        if npcId == self.Mob[0] and cond == 8 :
            st2 = self.findRightState(player,npc)
            if st2 :
                if not player.isInParty():
                    if st == st2 :
                        self.AutoChat(npc,self.Text[12].replace('PLAYERNAME',player.getName()))
                        st.giveItems(self.Items[6],1)
                        st.set("cond","9")
                        st.playSound("ItemSound.quest_middle")
                st2.getQuestTimer("Mob_1 has despawned").cancel()
                self.DeleteSpawn(st2,st2.getInt("Mob_1"))
                st2.set("spawned","0")
        elif npcId == self.Mob[1] :
            if cond == 15 :
                st2 = self.findRightState(player,npc)
                if st2 :
                    if not player.isInParty():
                        if st == st2 :
                            self.AutoChat(npc,self.Text[4].replace('PLAYERNAME',player.getName()))
                            st.giveItems(self.Items[8],1)
                            st.takeItems(self.Items[3],-1)
                            st.set("cond","16")
                            st.playSound("ItemSound.quest_middle")
                        else :
                            self.AutoChat(npc,self.Text[5].replace('PLAYERNAME',player.getName()))
                    st2.getQuestTimer("Archon Hellisha has despawned").cancel()
                    self.DeleteSpawn(st2,st2.getInt("Archon"))
                    st2.set("spawned","0")
    else :
        if npcId == self.Mob[0] :
            st = self.findRightState(player,npc)
            if st:
                st.getQuestTimer("Mob_1 has despawned").cancel()
                self.DeleteSpawn(st,st.getInt("Mob_1"))
                st.set("spawned","0")
        elif npcId == self.Mob[1] :
            st = self.findRightState(player,npc)
            if st:
                st.getQuestTimer("Archon Hellisha has despawned").cancel()
                self.DeleteSpawn(st,st.getInt("Archon"))
                st.set("spawned","0")
    return

QUEST = Quest(-1,qn,"Saga's Superclass")

for mobid in Mobs_Norm :
    QUEST.addKillId(mobid)