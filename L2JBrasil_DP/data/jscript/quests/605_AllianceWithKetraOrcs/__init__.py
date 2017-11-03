#Made by Emperorc
import sys
from com.it.br import Config
from com.it.br.gameserver.model.quest import State
from com.it.br.gameserver.model.quest import QuestState
from com.it.br.gameserver.model.quest.jython import QuestJython as JQuest

qn = "605_AllianceWithKetraOrcs"

#NPC
Wahkan = 31371

#MOB
    #mobs for Alliance lvl 1:Varka Silenos- Recruit, Footman, Scout, Hunter, Shaman
Varka_One = [ 21350, 21351, 21353, 21354, 21355 ]
    #mobs for Alliance lvl 2 SHOULD BE:Varka Silenos- priests, warriors, mediums, \
    #magi, officers, legionnaire captains, and elite escorts
        #AS LISTED in npc.sql: Varka Silenos-priests, warriors, mediums, magi, officers;\
        #Varka's- Commander, Elite Guard
Varka_Two = [ 21357, 21358, 21360, 21361, 21362, 21369, 21370, ]
    #mobs for Alliance lvl 3 and up SHOULD BE:Varka Silenos- great mystics, captains, \
    #grand seers, prophets, prophet's disciples, prophet's royal guards, chief magi and chief escorts
        #AS LISTED in npc.sql: Varka Silenos-Seer, Great Magus, General, Great Seer,
        #Varka's - Head Magus, Head Guard, Prophet, Prophet Guard, and Disciple of Prophet
Varka_Three = [ 21364, 21365, 21366, 21368, 21371, 21372, 21373, 21374, 21375 ]
    #All Ketra Orc mobs
Ketra_Orcs = [ 21324, 21325, 21327, 21328, 21329, 21331, 21332, 21334, 21335, \
21336, 21338, 21339, 21340, 21342, 21343, 21344, 21345, 21346, 21347, 21348, 21349 ]

Chance = {
  21351:500,#Footman
  21366:628,#General
  21365:500,#Great Magus
  21368:508,#Great Seer
  21354:521,#Hunter
  21361:518,#Magus
  21360:509,#Medium
  21362:500,#Officer
  21357:500,#Priest
  21350:500,#Recruit
  21353:509,#Scout
  21364:527,#Seer
  21355:519,#Shaman
  21358:500,#Warrior
  21369:518,#Commander
  21370:604,#Elite guard
  21372:604,#Head guard
  21371:627,#Head magus
  21374:626,#Prophet Guard
  21375:626,#Disciple of Prophet
  21373:649#Prophet
}

Chance_mane = {
  21366:664,#General
  21365:568,#Great Magus
  21368:568,#Great Seer
  21354:522,#Hunter
  21360:539,#Medium
  21362:568,#Officer
  21357:529,#Priest
  21350:500,#Recruit
  21353:510,#Scout
  21364:558,#Seer
  21355:519,#Shaman
  21358:529,#Warrior
  21369:548,#Commander
  21371:713,#Head magus
  21373:738#Prophet
}

#Quest Items
Varka_Badge_Soldier, Varka_Badge_Officer, Varka_Badge_Captain = [7216, 7217, 7218]
Ketra_Alliance_One, Ketra_Alliance_Two, Ketra_Alliance_Three, \
Ketra_Alliance_Four, Ketra_Alliance_Five = [7211, 7212, 7213, 7214, 7215]
Varka_Alliance_One, Varka_Alliance_Two, Varka_Alliance_Three, \
Varka_Alliance_Four, Varka_Alliance_Five  = [7221, 7222, 7223, 7224, 7225]
Ketra_Badge_Soldier, Ketra_Badge_Officer, Ketra_Badge_Captain  = [7226, 7227,7228]
Valor_Totem, Wisdom_Totem = [ 7219,7220 ]
Mane = 7233

#drop system - cond:[item_id,max,drop_id]
One ={
  1:[57,100,Varka_Badge_Soldier],
  2:[Ketra_Alliance_One,200,Varka_Badge_Soldier],
  3:[Ketra_Alliance_Two,300,Varka_Badge_Soldier],
  4:[Ketra_Alliance_Three,300,Varka_Badge_Soldier],
  5:[Ketra_Alliance_Four,400,Varka_Badge_Soldier]
}
Two ={   
  2:[Ketra_Alliance_One,100,Varka_Badge_Officer],
  3:[Ketra_Alliance_Two,200,Varka_Badge_Officer],
  4:[Ketra_Alliance_Three,300,Varka_Badge_Officer],
  5:[Ketra_Alliance_Four,400,Varka_Badge_Officer]
}
Three ={   
  3:[Ketra_Alliance_Two,100,Varka_Badge_Captain],
  4:[Ketra_Alliance_Three,200,Varka_Badge_Captain],
  5:[Ketra_Alliance_Four,200,Varka_Badge_Captain]
}

def decreaseAlliance(st) :
  if st.getPlayer().isAlliedWithKetra() :
    cond = st.getInt("cond")
    id = st.getInt("id")
    st.takeItems(Varka_Badge_Soldier,-1)
    st.takeItems(Varka_Badge_Officer,-1)
    st.takeItems(Varka_Badge_Captain,-1)
    st.takeItems(Valor_Totem,-1)
    st.takeItems(Wisdom_Totem,-1)
    st.getPlayer().setAllianceWithVarkaKetra(0)
    st.exitQuest(1)
    if cond == 2 :
      if id == 2 :
        st.takeItems(Ketra_Alliance_One,-1)
      else :
        st.takeItems(Ketra_Alliance_Two,-1)
        st.giveItems(Ketra_Alliance_One,1)
    elif cond == 3 :
      if id == 2:
        st.takeItems(Ketra_Alliance_Two,-1)
        st.giveItems(Ketra_Alliance_One,1)
      else :
         st.takeItems(Ketra_Alliance_Three,-1)
         st.giveItems(Ketra_Alliance_Two,1)
    elif cond == 4 :
      if id == 2 :
        st.takeItems(Ketra_Alliance_Three,-1)
        st.giveItems(Ketra_Alliance_Two,1)
      else :
        st.takeItems(Ketra_Alliance_Four,-1)
        st.giveItems(Ketra_Alliance_Three,1)
    elif cond == 5 :
      if id == 2 :
        st.takeItems(Ketra_Alliance_Four,-1)
        st.giveItems(Ketra_Alliance_Three,1)
      else :
        st.takeItems(Ketra_Alliance_Five,-1)
        st.giveItems(Ketra_Alliance_Four,1)
    elif cond == 6 :
      st.takeItems(Ketra_Alliance_Five,-1)
      st.giveItems(Ketra_Alliance_Four,1)

def giveReward(st,item,chance,MAX,drop) :
  if st.getQuestItemsCount(item) > 0 :
    count = st.getQuestItemsCount(drop)
    if count < MAX or drop == Mane :
      numItems,chance = divmod(chance*Config.RATE_DROP_QUEST,1000)
      if st.getRandom(1000) < chance :
        numItems += 1
      numItems = int(numItems)
      if numItems != 0 :
        if count + numItems >= MAX and drop != Mane :
          numItems = MAX - count
          st.playSound("ItemSound.quest_middle")
        elif drop == Mane and int((count+numItems)/100) > int(count/100) :
          st.playSound("ItemSound.quest_middle")
        else :
          st.playSound("ItemSound.quest_itemget")
        st.giveItems(drop,numItems)


class Quest (JQuest) :

 def __init__(self,id,name,descr): JQuest.__init__(self,id,name,descr)

 def onEvent (self,event,st) :
   cond = st.getInt("cond")
   id = st.getInt("id")
   htmltext = event
   player = st.getPlayer()
   if event == "31371-03a.htm" :
       if player.getLevel() >= 74 :
            st.set("cond","1")
            st.set("id","2")
            st.setState(STARTED)
            st.playSound("ItemSound.quest_accept")
            htmltext = "31371-03a.htm"
       else :
            htmltext = "31371-02b.htm"
            st.exitQuest(1)
            player.setAllianceWithVarkaKetra(0)
   elif event == "31371-10-1.htm" :
       htmltext = "31371-10-1.htm"
       st.set("id","3")
       st.takeItems(Varka_Badge_Soldier, 100)
       st.giveItems(Ketra_Alliance_One, 1)
       player.setAllianceWithVarkaKetra(1)
       st.playSound("ItemSound.quest_middle")
   elif event == "31371-10-2.htm" :
       htmltext = "31371-10-2.htm"
       st.set("id","3")
       st.takeItems(Varka_Badge_Soldier, 200)
       st.takeItems(Varka_Badge_Officer, 100)
       st.takeItems(Ketra_Alliance_One, -1)
       st.giveItems(Ketra_Alliance_Two, 1)
       player.setAllianceWithVarkaKetra(2)
       st.playSound("ItemSound.quest_middle")
   elif event == "31371-10-3.htm" :
       htmltext = "31371-10-3.htm"
       st.set("id","3")
       st.takeItems(Varka_Badge_Soldier, 300)
       st.takeItems(Varka_Badge_Officer, 200)
       st.takeItems(Varka_Badge_Captain, 100)
       st.takeItems(Ketra_Alliance_Two, -1)
       st.giveItems(Ketra_Alliance_Three, 1)
       player.setAllianceWithVarkaKetra(3)
       st.playSound("ItemSound.quest_middle")
   elif event == "31371-10-4.htm" :
       htmltext = "31371-10-4.htm"
       st.set("id","3")
       st.takeItems(Varka_Badge_Soldier, 300)
       st.takeItems(Varka_Badge_Officer, 300)
       st.takeItems(Varka_Badge_Captain, 200)
       st.takeItems(Ketra_Alliance_Three, -1)
       st.takeItems(Valor_Totem,-1)
       st.giveItems(Ketra_Alliance_Four, 1)
       player.setAllianceWithVarkaKetra(4)
       st.playSound("ItemSound.quest_middle")
   elif event == "31371-11a.htm" :
       htmltext = "31371-11a.htm"
   elif event == "31371-19.htm" :
       htmltext = "31371-19.htm"
   elif event == "31371-11b.htm" :
       htmltext = "31371-11b.htm"
   elif event == "31371-20.htm" :
       htmltext = "31371-20.htm"
       st.takeItems(Varka_Badge_Soldier, -1)
       st.takeItems(Varka_Badge_Officer, -1)
       st.takeItems(Varka_Badge_Captain, -1)
       st.takeItems(Ketra_Alliance_One, -1)
       st.takeItems(Ketra_Alliance_Two, -1)
       st.takeItems(Ketra_Alliance_Three, -1)
       st.takeItems(Ketra_Alliance_Four, -1)
       st.takeItems(Ketra_Alliance_Five, -1)
       st.takeItems(Valor_Totem,-1)
       st.takeItems(Wisdom_Totem,-1)
       player.setAllianceWithVarkaKetra(0)
       st.exitQuest(1)
   return htmltext

 def onTalk (self,npc,player):
    htmltext = "<html><body>You are either not carrying out your quest or don't meet the criteria.</body></html>"
    st = player.getQuestState(qn)
    if st :
      npcId = npc.getNpcId()
      cond = st.getInt("cond")
      id = st.getInt("id")
      VBadgeS = st.getQuestItemsCount(Varka_Badge_Soldier)
      VBadgeO = st.getQuestItemsCount(Varka_Badge_Officer)
      VBadgeC = st.getQuestItemsCount(Varka_Badge_Captain)
      KAlliance1 = st.getQuestItemsCount(Ketra_Alliance_One)
      KAlliance2 = st.getQuestItemsCount(Ketra_Alliance_Two)
      KAlliance3 = st.getQuestItemsCount(Ketra_Alliance_Three)
      KAlliance4 = st.getQuestItemsCount(Ketra_Alliance_Four)
      KAlliance5 = st.getQuestItemsCount(Ketra_Alliance_Five)
      KAlliance = KAlliance1 + KAlliance2 + KAlliance3 + KAlliance4 + KAlliance5
      VAlliance = st.getQuestItemsCount(Varka_Alliance_One) + \
       st.getQuestItemsCount(Varka_Alliance_Two) + st.getQuestItemsCount(Varka_Alliance_Three) + \
       st.getQuestItemsCount(Varka_Alliance_Four) + st.getQuestItemsCount(Varka_Alliance_Five)
      Valor = st.getQuestItemsCount(Valor_Totem)
      Wisdom = st.getQuestItemsCount(Wisdom_Totem)
      if npcId == Wahkan :
          st.set("id","1")
          if player.isAlliedWithVarka() or VAlliance :
              htmltext= "31371-02a.htm"
              st.exitQuest(1)
          elif KAlliance == 0 :
              if cond != 1 :
                  htmltext = "31371-01.htm"
              else :
                  st.set("id","2")
                  if VBadgeS < 100 :
                      htmltext= "31371-03b.htm"
                  elif VBadgeS >= 100 :
                      htmltext = "31371-09.htm"
          elif KAlliance :
              st.setState(STARTED)
              st.set("id","2")
              if KAlliance1 :
                  if cond != 2 :
                      htmltext = "31371-04.htm"
                      st.set("cond","2")
                      player.setAllianceWithVarkaKetra(1)
                  else :
                      if VBadgeS < 200 or VBadgeO < 100 :
                          htmltext = "31371-12.htm"
                      elif VBadgeS >= 200 and VBadgeO >= 100 :
                          htmltext = "31371-13.htm"
              elif KAlliance2 :
                  if cond != 3 :
                      htmltext = "31371-05.htm"
                      st.set("cond","3")
                      player.setAllianceWithVarkaKetra(2)
                  else :
                      if VBadgeS < 300 or VBadgeO < 200 or VBadgeC < 100 :
                          htmltext = "31371-15.htm"
                      elif VBadgeS >= 300 and VBadgeO >= 200 and VBadgeC >= 100 :
                          htmltext = "31371-16.htm"
              elif KAlliance3 :
                  if cond != 4 :
                      htmltext = "31371-06.htm"
                      st.set("cond","4")
                      player.setAllianceWithVarkaKetra(3)
                  else:
                      if VBadgeS < 300 or VBadgeO < 300 or VBadgeC < 200 or Valor == 0 :
                          htmltext = "31371-21.htm"
                      elif VBadgeS >= 300 and VBadgeO >= 300 and VBadgeC >= 200 and Valor > 0 :
                          htmltext = "31371-22.htm"
              elif KAlliance4 :
                  if cond != 5 :
                      htmltext = "31371-07.htm"
                      st.set("cond","5")
                      player.setAllianceWithVarkaKetra(4)
                  else :
                      if VBadgeS < 400 or VBadgeO < 400 or VBadgeC < 200 or Wisdom == 0 :
                          htmltext = "31371-17.htm"
                      elif VBadgeS >= 400 and VBadgeO >= 400 and VBadgeC >= 200 and Wisdom > 0 :
                          htmltext = "31371-10-5.htm"
                          st.takeItems(Varka_Badge_Soldier, 400)
                          st.takeItems(Varka_Badge_Officer, 400)
                          st.takeItems(Varka_Badge_Captain, 200)
                          st.takeItems(Ketra_Alliance_Four, -1)
                          st.takeItems(Wisdom_Totem,-1)
                          st.giveItems(Ketra_Alliance_Five, 1)
                          player.setAllianceWithVarkaKetra(5)
                          st.set("id","3")
                          st.playSound("ItemSound.quest_middle")
              elif KAlliance5 :
                  if cond != 6 :
                      htmltext = "31371-18.htm"
                      st.set("cond","6")
                      player.setAllianceWithVarkaKetra(5)
                  else:
                      htmltext = "31371-08.htm"
    return htmltext

 def onKill(self,npc,player,isPet):
   partyMember = self.getRandomPartyMemberState(player,STARTED)
   if not partyMember : return
   st = partyMember.getQuestState(qn)
   if st :
      if st.getState() == STARTED :
          npcId = npc.getNpcId()
          cond = st.getInt("cond")
          id = st.getInt("id")
          st2 = partyMember.getQuestState("606_WarWithVarkaSilenos")
          if not partyMember.isAlliedWithVarka() :
              if (npcId in Varka_One) or (npcId in Varka_Two) or (npcId in Varka_Three) :
                  item = 0
                  if cond <= 5 :
                    if npcId in Varka_One :
                      item,MAX,drop = One[cond]
                    elif npcId in Varka_Two and cond > 1:
                      item,MAX,drop = Two[cond]
                    elif npcId in Varka_Three and cond > 2 :
                      item,MAX,drop = Three[cond]
                  if item != 0 :
                    if st.getQuestItemsCount(drop) == MAX :
                      item = 0
                  chance = Chance[npcId]
          #This is support for quest 606: War With Varka Silenos. Basically, if the person has both this quest and 606, then they only get one quest item, 50% chance for 606 quest item and 50% chance for this quest's item
                  if st2 :
                      if (st.getRandom(2) == 1 or item == 0) and npcId in Chance_mane.keys() :
                          item = 57
                          MAX = 100
                          drop = Mane
                          chance = Chance_mane[npcId]
                          giveReward(st,item,chance,MAX,drop)
                      elif id == 2 and item != 0 :
                          giveReward(st,item,chance,MAX,drop)
                  elif id == 2 and item != 0 :
                      giveReward(st,item,chance,MAX,drop)
              elif npcId in Ketra_Orcs :
                  decreaseAlliance(st)
                  party = partyMember.getParty()
                  if party :
                      for player in party.getPartyMembers().toArray() :
                          pst = player.getQuestState(qn)
                          if pst :
                              decreaseAlliance(pst)
   return

QUEST       = Quest(605,qn,"Alliance With Ketra Orcs")
CREATED     = State('Start', QUEST)
STARTED     = State('Started', QUEST)

QUEST.setInitialState(CREATED)
QUEST.addStartNpc(Wahkan)
QUEST.addTalkId(Wahkan)

for mobId in Chance.keys() :
    QUEST.addKillId(mobId)

for mobId in Ketra_Orcs :
    QUEST.addKillId(mobId)