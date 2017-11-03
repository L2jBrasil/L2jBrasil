# Written by
# questdevs Team

import sys
from java.util                                 import Iterator
from com.it.br.util                           import Rnd
from com.it.br.gameserver.network.serverpackets       import CreatureSay
from com.it.br.gameserver.model.quest         import State
from com.it.br.gameserver.model.quest         import QuestState
from com.it.br.gameserver.model.quest.jython  import QuestJython as JQuest
from com.it.br                                import L2DatabaseFactory

qn = "503_PursuitClanAmbition"
qd = "Pursuit Clan Ambition"

# Items
# first part
G_Let_Martien = 3866
Th_Wyrm_Eggs = 3842
Drake_Eggs = 3841
Bl_Wyrm_Eggs = 3840
Mi_Drake_Eggs = 3839
Brooch = 3843
Bl_Anvil_Coin = 3871

# second Part
G_Let_Balthazar = 3867
Recipe_Power_Stone = 3838
Power_Stones = 3846
Nebulite_Crystals = 3844
Broke_Power_Stone = 3845

# third part
G_Let_Rodemai = 3868
Imp_Keys = 3847
Scepter_Judgement = 3869

# the final item
Proof_Aspiration = 3870


EggList = [Mi_Drake_Eggs,Bl_Wyrm_Eggs,Drake_Eggs,Th_Wyrm_Eggs]

# NPC = Martien,Athrea,Kalis,Gustaf,Fritz,Lutz,Kurtz,Kusto,Balthazar,Rodemai,Coffer,Cleo
NPC=[30645,30758,30759,30760,30761,30762,30763,30512,30764,30868,30765,30766]
STATS=["cond","Fritz","Lutz","Kurtz","ImpGraveKeeper"]

# DROPLIST = step,chance,maxcount,item 
# condition,maxcount,chance,itemList = DROPLIST[npcId]
DROPLIST = {
20282: [2,10,20,[Th_Wyrm_Eggs]],                     # Thunder Wyrm 1
20243: [2,10,15,[Th_Wyrm_Eggs]],                     # Thunder Wyrm 2
20137: [2,10,20,[Drake_Eggs]],                     # Drake 1
20285: [2,10,25,[Drake_Eggs]],                     # Drake 2
27178:[2,10,100,[Bl_Wyrm_Eggs]],                    # Blitz Wyrm
20654: [5,10,25,[Broke_Power_Stone,Power_Stones,Nebulite_Crystals]],  # Giant Soldier
20656: [5,10,35,[Broke_Power_Stone,Power_Stones,Nebulite_Crystals]],  # Giant Scouts
20668: [10,0,15,[]],                          # Grave Guard
27179:[10,6,80,[Imp_Keys]],                       # GraveKeyKeeper
27181:[10,0,100,[]]                          # Imperial Gravekeeper
}

def suscribe_members(st) :
  clan=st.getPlayer().getClan().getClanId()
  con=L2DatabaseFactory.getInstance().getConnection(None)
  offline=con.prepareStatement("SELECT obj_Id FROM characters WHERE clanid=? AND online=0")
  offline.setInt(1, clan)
  rs=offline.executeQuery()
  while (rs.next()) :
    char_id=rs.getInt("obj_Id")
    try :
      insertion = con.prepareStatement("INSERT INTO character_quests (char_id,name,var,value) VALUES (?,?,?,?)")
      insertion.setInt(1, char_id)
      insertion.setString(2, qn)
      insertion.setString(3, "<state>")
      insertion.setString(4, "Progress")
      insertion.executeUpdate()
      insertion.close();
    except :
      try : insertion.close()
      except : pass
  try :
    con.close()
  except :
    pass

def offlineMemberExit(st) :
  clan=st.getPlayer().getClan().getClanId()
  con=L2DatabaseFactory.getInstance().getConnection(None)
  offline=con.prepareStatement("DELETE FROM character_quests WHERE name = ? and char_id IN (SELECT obj_id FROM characters WHERE clanId =? AND online=0")
  offline.setString(1, qn)
  offline.setInt(2, clan)
  try :
    offline.executeUpdate()
    offline.close()
    con.close()
  except :
    try : con.close()
    except : pass

# returns leaders quest cond, if he is offline will read out of database :)
def getLeaderVar(st, var) :
  try :
    clan = st.getPlayer().getClan()  
    if clan == None:
      return -1
    leader=clan.getLeader().getPlayerInstance()
    if leader != None :
      return int(leader.getQuestState(qn).get(var))
  except :
    pass
  leaderId=st.getPlayer().getClan().getLeaderId()
  con=L2DatabaseFactory.getInstance().getConnection(None)
  offline=con.prepareStatement("SELECT value FROM character_quests WHERE char_id=? AND var=? AND name=?")
  offline.setInt(1, leaderId)
  offline.setString(2, var)
  offline.setString(3, qn)
  rs=offline.executeQuery()
  if rs :
    rs.next()
    try :
      val=rs.getInt("value")
      con.close()
    except :
      val=-1
      try : con.close()
      except : pass
  else :
    val=-1
  return int(val)

# set's leaders quest cond, if he is offline will read out of database :)
# for now, if the leader is not logged in, this assumes that the variable
# has already been inserted once (initialized, in some sense).
def setLeaderVar(st, var, value) :
  clan = st.getPlayer().getClan()  
  if clan == None: return   
  leader=clan.getLeader().getPlayerInstance()
  if leader != None :
    leader.getQuestState(qn).set(var,value)
  else :
    leaderId=st.getPlayer().getClan().getLeaderId()
    con=L2DatabaseFactory.getInstance().getConnection(None)
    offline=con.prepareStatement("UPDATE character_quests SET value=? WHERE char_id=? AND var=? AND name=?")
    offline.setString(1, value)
    offline.setInt(2, leaderId)
    offline.setString(3, var)
    offline.setString(4, qn)
    try :
      offline.executeUpdate()
      offline.close()
      con.close()
    except :
      try : con.close()
      except : pass 
  return

def checkEggs(st):
  count = 0
  for item in EggList:
    if st.getQuestItemsCount(item) > 9:
      count+=1
  if count > 3 :
    return 1
  else:
    return 0

def giveItem(item,maxcount,st):
  count = st.getQuestItemsCount(item)
  if count < maxcount:
    st.giveItems(item,1)
    if count == maxcount-1:
      st.playSound("ItemSound.quest_middle")
    else:
      st.playSound("ItemSound.quest_itemget")
  return

def exit503(completed,st):
    if completed:
      st.giveItems(Proof_Aspiration,1)
      st.addExpAndSp(0,250000)
      for var in STATS:
        st.unset(var)
      st.setState(COMPLETED)
    else:
      st.exitQuest(1)
    st.takeItems(Scepter_Judgement,-1)
    try:
      members = st.getPlayer().getClan().getOnlineMembers("")[0]
      for i in members:
        st.getPlayer().getClan().getClanMember(i).getPlayerInstance().getQuestState(qn).exitQuest(1)
      offlineMemberExit(st)
    except:
      return "You dont have any members in your Clan, so you can't finish the Pursuit of Aspiration"
    return "Congratulations, you have finished the Pursuit of Clan Ambition"

class Quest (JQuest) :

  def __init__(self,id,name,descr):
    JQuest.__init__(self,id,name,descr)
    self.ImpGraveKepperStat = 1

  def onEvent (self,event,st) :
    htmltext = event
# Events Gustaf
    if event == "30760-08.htm" :
      st.giveItems(G_Let_Martien,1)
      for var in STATS:
        st.set(var,"1")
      st.setState(PROGRESS)
    elif event == "30760-12.htm" :
      st.giveItems(G_Let_Balthazar,1)
      st.set("cond","4")
    elif event == "30760-16.htm" :
      st.giveItems(G_Let_Rodemai,1)
      st.set("cond","7")
    elif event == "30760-20.htm" :
      exit503(1,st)
    elif event == "30760-22.htm" :
      st.set("cond","13")
    elif event == "30760-23.htm" :
      exit503(1,st)
# Events Martien
    elif event == "30645-03.htm":
      st.takeItems(G_Let_Martien,-1)
      st.set("cond","2")
      suscribe_members(st) 
      try:
        members = st.getPlayer().getClan().getOnlineMembers("")[0]
        for i in members:
          pst = QuestManager.getInstance().getQuest(qn).newQuestState(st.getPlayer().getClan().getClanMember(int(i)).getPlayerInstance())
          pst.setState(PROGRESS)
      except:
        return htmltext
# Events Kurtz
    elif event == "30763-03.htm":
      if st.getInt("Kurtz") == 1:
        htmltext = "30763-02.htm"
        st.giveItems(Mi_Drake_Eggs,6)
        st.giveItems(Brooch,1)
        st.set("Kurtz","2")
# Events Lutz
    elif event == "30762-03.htm":
      lutz = st.getInt("Lutz")
      if lutz == 1:
        htmltext = "30762-02.htm"
        st.giveItems(Mi_Drake_Eggs,4)
        st.giveItems(Bl_Wyrm_Eggs,3)
        st.set("Lutz","2")
      st.addSpawn(27178,112268,112761,-2770,120000)
      st.addSpawn(27178,112234,112705,-2770,120000)
# Events Fritz
    elif event == "30761-03.htm":
      fritz = st.getInt("Fritz")
      if fritz == 1:
        htmltext = "30761-02.htm"
        st.giveItems(Bl_Wyrm_Eggs,3)
        st.set("Fritz","2")
      st.addSpawn(27178,103841,116809,-3025,120000)
      st.addSpawn(27178,103848,116910,-3020,120000)
# Events Kusto
    elif event == "30512-03.htm":
      st.takeItems(Brooch,-1)
      st.giveItems(Bl_Anvil_Coin,1)
      st.set("Kurtz","3")
# Events Balthazar
    elif event == "30764-03.htm":
      st.takeItems(G_Let_Balthazar,-1)
      st.set("cond","5")
      st.set("Kurtz","3")
    elif event == "30764-05.htm":
      st.takeItems(G_Let_Balthazar,-1)
      st.set("cond","5")
    elif event == "30764-06.htm":
      st.takeItems(Bl_Anvil_Coin,-1)
      st.set("Kurtz","4")
      st.giveItems(Recipe_Power_Stone,1)
# Events Rodemai
    elif event == "30868-04.htm":
      st.takeItems(G_Let_Rodemai,-1)
      st.set("cond","8")
    elif event == "30868-06a.htm":
      st.set("cond","10")
    elif event == "30868-10.htm":
      st.set("cond","12")
# Events Cleo
    elif event == "30766-04.htm":
      st.set("cond","9")
      spawnedNpc = st.addSpawn(30766,160622,21230,-3710,90000)
      spawnedNpc.broadcastPacket(CreatureSay(spawnedNpc.getObjectId(),0,spawnedNpc.getName(),"Blood and Honour."))
      spawnedNpc = st.addSpawn(30759,160665,21209,-3710,90000)
      spawnedNpc.broadcastPacket(CreatureSay(spawnedNpc.getObjectId(),0,spawnedNpc.getName(),"Ambition and Power"))
      spawnedNpc = st.addSpawn(30758,160665,21291,-3710,90000)
      spawnedNpc.broadcastPacket(CreatureSay(spawnedNpc.getObjectId(),0,spawnedNpc.getName(),"War and Death"))
    elif event == "30766-08.htm":
      st.takeItems(Scepter_Judgement,-1)
      exit503(0,st)
    return htmltext


  def onTalk (self,npc,player):
    htmltext = "<html><body>You are either not carrying out your quest or don't meet the criteria.</body></html>"
    st = player.getQuestState(qn)
    if not st : return htmltext

    npcId = npc.getNpcId()
    id = st.getState()
    if npcId != NPC[3] and id == CREATED : return htmltext

    Martien,Athrea,Kalis,Gustaf,Fritz,Lutz,Kurtz,Kusto,Balthazar,Rodemai,Coffer,Cleo = 30645,30758,30759,30760,30761,30762,30763,30512,30764,30868,30765,30766
    isLeader = player.isClanLeader()
    if id == CREATED and npcId == Gustaf:
      for var in STATS:                                  # adds all the  vars for initialisation
        st.set(var,"0")
      if player.getClan():                            # has Clan
        if isLeader:                                  # check if player is clan leader
          clanLevel = player.getClan().getLevel()
          if st.getQuestItemsCount(Proof_Aspiration):                  # if he has the proof already, tell him what to do now
            htmltext = "30760-03.htm"
            st.exitQuest(1)
          elif clanLevel == 4:                     # if clanLevel == 4 you can take this quest, because repeatable
            htmltext = "30760-04.htm"
          else:                                    # if clanLevel is too low or too high you cant take it
            htmltext = "30760-02.htm"
            st.exitQuest(1)
        else:                                      # player isnt a leader
          htmltext = "30760-04t.htm"
          st.exitQuest(1)
      else:                                        # no Clan
        htmltext = "30760-01.htm"
        st.exitQuest(1)
      return htmltext
    elif player.getClan() and player.getClan().getLevel() >= 5:        # player has level 5 clan already
      return "<html><body>This quest has already been completed.</body></html>"
    elif id == COMPLETED:                                  # player has proof, and has finished quest as leader
      return "<html><body>This quest has already been completed.</body></html>"
    else:
      ######## Leader Area ######
      if isLeader:
        cond   = st.getInt("cond")
        kurtz  = st.getInt("Kurtz")
        lutz  = st.getInt("Lutz")
        fritz  = st.getInt("Fritz")
        
        if npcId == Gustaf :
          if cond == 1:
            htmltext = "30760-09.htm"
          elif cond == 2:
            htmltext = "30760-10.htm"
          elif cond == 3:
            htmltext = "30760-11.htm"
          elif cond == 4:
            htmltext = "30760-13.htm"
          elif cond == 5:
            htmltext = "30760-14.htm"
          elif cond == 6:
            htmltext = "30760-15.htm"
          elif cond == 7:
            htmltext = "30760-17.htm"
          elif cond == 12:
            htmltext = "30760-19.htm"
          elif cond == 13:
            htmltext = "30760-24.htm"
          else:
            htmltext = "30760-18.htm"
        elif npcId == Martien :
          if cond == 1:
            htmltext = "30645-02.htm"
          elif cond == 2:
            if checkEggs(st) and kurtz > 1 and lutz > 1 and fritz > 1:
              htmltext = "30645-05.htm"
              st.set("cond","3")
              for item in EggList:
                st.takeItems(item,-1)
            else:
              htmltext = "30645-04.htm"
          elif cond == 3:
            htmltext = "30645-07.htm"
          else:
            htmltext = "30645-08.htm"
        elif cond == 2:                        # Dwarven Corpse in DV, only needed if condition is 2
          if npcId == Lutz:
            htmltext = "30762-01.htm"
          elif npcId == Kurtz:
            htmltext = "30763-01.htm"
          elif npcId == Fritz:
            htmltext = "30761-01.htm"
        elif npcId == Kusto:
          if kurtz == 1:
            htmltext = "30512-01.htm"
          elif kurtz == 2:
            htmltext = "30512-02.htm"
          else:
            htmltext = "30512-04.htm"
          return htmltext
        elif npcId == Balthazar:
          if cond == 4:
            if kurtz > 2:
              htmltext = "30764-04.htm"
            else:
              htmltext = "30764-02.htm"
          elif cond == 5:
            if st.getQuestItemsCount(Power_Stones) > 9 and st.getQuestItemsCount(Nebulite_Crystals) > 9:
              htmltext = "30764-08.htm"
              st.takeItems(Power_Stones,-1)
              st.takeItems(Nebulite_Crystals,-1)
              st.takeItems(Brooch,-1)
              st.set("cond","6")
            else:
              htmltext = "30764-07.htm"
          elif cond == 6:
            htmltext = "30764-09.htm"
        elif npcId == Rodemai:
          if cond == 7:
            htmltext = "30868-02.htm"
          elif cond == 8:
            htmltext = "30868-05.htm"
          elif cond == 9:
            htmltext = "30868-06.htm"
          elif cond == 10:
            htmltext = "30868-08.htm"
          elif cond == 11:
            htmltext = "30868-09.htm"
          elif cond == 12:
            htmltext = "30868-11.htm"
        elif npcId == Cleo:
          if cond == 8:
            htmltext = "30766-02.htm"
          elif cond == 9:
            htmltext = "30766-05.htm"
          elif cond == 10:
            htmltext = "30766-06.htm"
          elif cond in [11,12,13]:
            htmltext = "30766-07.htm"
        elif npcId == Coffer:
          if st.getInt("cond") == 10:
            if st.getQuestItemsCount(Imp_Keys) < 6:
              htmltext = "30765-03a.htm"
            elif st.getInt("ImpGraveKeeper") == 3:
              htmltext = "30765-02.htm"
              st.set("cond","11")
              st.takeItems(Imp_Keys,6)
              st.giveItems(Scepter_Judgement,1)
            else:
              htmltext = "<html><body>(You and your Clan didn't kill the Imperial Gravekeeper by your own, go and try again.)</body></html>"
          else:
              htmltext = "<html><body>(You already have the Scepter of Judgement.)</body></html>"
        elif npcId == Kalis:
          htmltext = "30759-01.htm"
        elif npcId == Athrea:
          htmltext = "30758-01.htm"
        return htmltext
      ######## Member Area ######
      else:
        cond = getLeaderVar(st,"cond")
        if npcId == Martien and cond in [1,2,3]:
          htmltext = "30645-01.htm"
        elif npcId == Rodemai :
          if cond in [9,10]:
            htmltext = "30868-07.htm"
          elif cond == 7:
            htmltext = "30868-01.htm"
        elif npcId == Balthazar and cond == 4:
          htmltext = "30764-01.htm"
        elif npcId == Cleo and cond == 8:
          htmltext = "30766-01.htm"
        elif npcId == Kusto and 6 > cond > 2:
          htmltext = "30512-01a.htm"
        elif npcId == Coffer and cond == 10:
          htmltext = "30765-01.htm"
        elif npcId == Gustaf:
          if cond == 3:
            htmltext = "30760-11t.htm"
          elif cond == 4:
            htmltext = "30760-15t.htm"
          elif cond == 12:
            htmltext = "30760-19t.htm"
          elif cond == 13:
            htmltext = "30766-24t.htm"
        return htmltext

  def onAttack(self, npc, player, damage, isPet):
    npdId = npc.getNpcId()
    if (npc.getMaxHp()/2) > npc.getStatus().getCurrentHp():
      if Rnd.get(100) < 4:
        if self.ImpGraveKepperStat == 1:
          for j in range(2):
            for k in range(2): 
              self.addSpawn(27180,npc.getX()+70*pow(-1,j%2),npc.getY()+70*pow(-1,k%2),npc.getZ(),0,False,0)
          self.ImpGraveKepperStat = 2
        else:
          players = npc.getKnownList().getKnownPlayers().values().toArray()
          if len(players) :
            playerToTP = players[Rnd.get(int(len(players)))]
            playerToTP.setXYZ(185462,20342,-3250)
    return

  def onKill(self,npc,player,isPet):
    # all kill events triggered by the leader occur automatically.
    # However, kill events that were triggered by members occur via the leader and
    # only if the leader is online and within a certain distance!
    leader_st = 0
    if player.isClanLeader() :
      leader_st = player.getQuestState(qn)
    else :
      clan = player.getClan()
      if clan:
        c_leader=clan.getLeader()
        if c_leader:
           leader=c_leader.getPlayerInstance()
           if leader :
             if player.isInsideRadius(leader, 1600, 1, 0) :
               leader_st = leader.getQuestState(qn)
    if leader_st :
      if leader_st.getState() != PROGRESS : return
      npcId=npc.getNpcId()
      condition,maxcount,chance,itemList = DROPLIST[npcId]
      random = leader_st.getRandom(100)
      cond = leader_st.getInt("cond")
      if cond == condition and random < chance:
        if len(itemList) > 1:
          stoneRandom = leader_st.getRandom(3)
          if stoneRandom == 0 :
            if leader_st.getInt("Kurtz") < 4:
              return
            else:
              maxcount*=4
          giveItem(itemList[stoneRandom],maxcount,leader_st)
        elif len(itemList) :
          giveItem(itemList[0],maxcount,leader_st)
        else:
          if npcId == 27181:                # Imperial Gravekeeper
            spawnedNpc=leader_st.addSpawn(30765,120000)
            npc.broadcastPacket(CreatureSay(spawnedNpc.getObjectId(),0,spawnedNpc.getName(),"Curse of the gods on the one that defiles the property of the empire!"))
            leader_st.set("ImpGraveKeeper","3")
            self.ImpGraveKepperStat = 1
          else:
            leader_st.addSpawn(27179)
    return

QUEST     = Quest(503,qn,"Pursuit of Clan Ambition")
CREATED   = State('Start', QUEST)
PROGRESS  = State('Progress', QUEST)
COMPLETED = State('Completed', QUEST)

QUEST.setInitialState(CREATED)
QUEST.addStartNpc(NPC[3])

for npcId in NPC:
  QUEST.addTalkId(npcId)

for mobId in DROPLIST.keys():
  QUEST.addKillId(mobId)

QUEST.addAttackId(27181)