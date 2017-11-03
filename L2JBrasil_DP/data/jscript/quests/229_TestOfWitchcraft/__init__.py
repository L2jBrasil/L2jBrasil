# Made by Mr. Have fun! Version 0.2
# rewritten by Rolarga Version 0.3
# Shadow Weapon Coupons contributed by BiTi for the Official L2J Datapack Project
# Visit http://forum.l2jdp.com for more details
import sys
from com.it.br.gameserver.model.quest import State
from com.it.br.gameserver.model.quest import QuestState
from com.it.br.gameserver.model.quest.jython import QuestJython as JQuest

qn = "229_TestOfWitchcraft"

MARK_OF_WITCHCRAFT   = 3307
ORIMS_DIAGRAM        = 3308
ALEXANDRIAS_BOOK     = 3309
IKERS_LIST           = 3310
DIRE_WYRM_FANG       = 3311
LETO_LIZARDMAN_CHARM = 3312
EN_GOLEM_HEARTSTONE  = 3313
LARS_MEMO1           = 3314
NESTLE_MEMO1         = 3315
LEOPOLDS_JOURNAL1    = 3316
AKLANTOS_GEM1        = 3317
AKLANTOS_GEM2        = 3318
AKLANTOS_GEM3        = 3319
AKLANTOS_GEM4        = 3320
AKLANTOS_GEM5        = 3321
AKLANTOS_GEM6        = 3322
BRIMSTONE1           = 3323
ORIMS_INSTRUCTIONS   = 3324
ORIMS_LETTER1        = 3325
ORIMS_LETTER2        = 3326
SIR_VASPERS_LETTER   = 3327
VADINS_CRUCIFIX      = 3328
TAMLIN_ORC_AMULET    = 3329
VADINS_SANCTIONS     = 3330
IKERS_AMULET         = 3331
SOULTRAP_CRYSTAL     = 3332
PURGATORY_KEY        = 3333
ZERUEL_BIND_CRYSTAL  = 3334
BRIMSTONE2           = 3335
SWORD_OF_BINDING     = 3029
SHADOW_WEAPON_COUPON_CGRADE = 8870

NPC = [30063,30098,30110,30188,30314,30417,30435,30476,30630,30631,30632,30633]

STATS = ["cond","step","gem1","gem2","gem3","gem456"] 

#This handle all Mob-Drop related Data npcId:[var,value,maxcount,chance,giveList,takeList]
DROPLIST={
27101:  ["step",  [3,14],  1,100,[ZERUEL_BIND_CRYSTAL,PURGATORY_KEY],[BRIMSTONE2,SOULTRAP_CRYSTAL]],
27100:  ["gem456",  [3,4,5],1,100,[AKLANTOS_GEM4,AKLANTOS_GEM5,AKLANTOS_GEM6],[LEOPOLDS_JOURNAL1]],
20601:  ["step",  [9],20,50,[TAMLIN_ORC_AMULET],0],
20602:  ["step",  [9],20,55,[TAMLIN_ORC_AMULET],0],
27099:  ["gem3",  [2],1,100,[AKLANTOS_GEM3],[LARS_MEMO1]],
20557:  ["gem1",  [2,3,4],20,100,[DIRE_WYRM_FANG],0],
20565:  ["gem1",  [2,3,4],20,80,[EN_GOLEM_HEARTSTONE],0],
20577:  ["gem1",  [2,3,4],20,50,[LETO_LIZARDMAN_CHARM],0],
20578:  ["gem1",  [2,3,4],20,50,[LETO_LIZARDMAN_CHARM],0],
20579:  ["gem1",  [2,3,4],20,60,[LETO_LIZARDMAN_CHARM],0],
20580:  ["gem1",  [2,3,4],20,60,[LETO_LIZARDMAN_CHARM],0],
20581:  ["gem1",  [2,3,4],20,70,[LETO_LIZARDMAN_CHARM],0],
20582:  ["gem1",  [2,3,4],20,70,[LETO_LIZARDMAN_CHARM],0]
}


class Quest (JQuest) :

  def __init__(self,id,name,descr): JQuest.__init__(self,id,name,descr)

  def onEvent (self,event,st) :
    htmltext = event
    # Orims Events
    if event == "1":
      htmltext = "30630-08.htm"
      st.setState(STARTED)
      st.playSound("ItemSound.quest_accept")
      st.giveItems(ORIMS_DIAGRAM,1)
      for var in STATS:
        st.set(var,"1")
    elif event == "30630_1" :
      htmltext = "30630-04.htm"
    elif event == "30630_2" :
      htmltext = "30630-06.htm"
    elif event == "30630_3" :
      htmltext = "30630-07.htm"
    elif event == "30630_4" :
      htmltext = "30630-12.htm"
    elif event == "30630_5" :
      htmltext = "30630-13.htm"
    elif event == "30630_6" :
      htmltext = "30630-14.htm"
      st.giveItems(BRIMSTONE1,1)
      st.takeItems(ALEXANDRIAS_BOOK,1)
      st.takeItems(AKLANTOS_GEM1,1)
      st.takeItems(AKLANTOS_GEM2,1)
      st.takeItems(AKLANTOS_GEM3,1)
      st.takeItems(AKLANTOS_GEM4,1)
      st.takeItems(AKLANTOS_GEM5,1)
      st.takeItems(AKLANTOS_GEM6,1)
      st.set("step","3")
      st.set("cond", "4")
      st.addSpawn(27101,70381, 109638, -3726)
    elif event == "30630_7" :
      htmltext = "30630-16.htm"
      st.takeItems(BRIMSTONE1,1)
      st.giveItems(ORIMS_INSTRUCTIONS,1)
      st.giveItems(ORIMS_LETTER1,1)
      st.giveItems(ORIMS_LETTER2,1)
      st.set("step","5")
      st.set("cond","6")
    elif event == "30630_8" :
      htmltext = "30630-20.htm"
      st.takeItems(ZERUEL_BIND_CRYSTAL,1)
    elif event == "30630_9" :
      htmltext = "30630-21.htm"
      st.takeItems(PURGATORY_KEY,1)
    elif event == "30630_10" :
      st.takeItems(SWORD_OF_BINDING,1)
      st.takeItems(IKERS_AMULET,1)
      st.takeItems(ORIMS_INSTRUCTIONS,1)
      st.addExpAndSp(139796,40000)
      st.giveItems(MARK_OF_WITCHCRAFT,1)
      st.giveItems(SHADOW_WEAPON_COUPON_CGRADE,15)
      htmltext = "30630-22.htm"
      for var in STATS:
        st.unset(var)
      st.setState(COMPLETED)
      st.playSound("ItemSound.quest_finish")
    # Alexandrias Events
    elif event == "30098_1" :
      htmltext = "30098-02.htm"
    elif event == "30098_2" :
      htmltext = "30098-03.htm"
      st.giveItems(ALEXANDRIAS_BOOK,1)
      st.takeItems(ORIMS_DIAGRAM,1)
      st.set("step","2")
      st.set("cond","2")
    # Ikers Events
    elif event == "30110_1" :
      htmltext = "30110-02.htm"
    elif event == "30110_2" :
      htmltext = "30110-03.htm"
      st.giveItems(IKERS_LIST,1)
      st.set("gem1","2")
    elif event == "30110_3" :
      htmltext = "30110-08.htm"
      st.giveItems(SOULTRAP_CRYSTAL,1)
      st.giveItems(IKERS_AMULET,1)
      st.takeItems(ORIMS_LETTER2,1)
      st.set("step",str(st.getInt("step")+1))
    # Kairas Events
    elif event == "30476_1" :
      htmltext = "30476-02.htm"
      st.giveItems(AKLANTOS_GEM2,1)
      st.set("gem2","2")
    # Laras Events
    elif event == "30063_1" :
      htmltext = "30063-02.htm"
      st.giveItems(LARS_MEMO1,1)
      st.set("gem3","2")
    # Nestles Events
    elif event == "30314_1" :
      htmltext = "30314-02.htm"
      st.giveItems(NESTLE_MEMO1,1)
      st.set("gem456","2")
    # Leopolds Events
    elif event == "30435_1" :
      htmltext = "30435-02.htm"
      st.giveItems(LEOPOLDS_JOURNAL1,1)
      st.takeItems(NESTLE_MEMO1,1)
      st.set("gem456","3")
    # (Vasper) Klaus Events
    elif event == "30417_1" :
      htmltext = "30417-02.htm"
    elif event == "30417_2" :
      htmltext = "30417-03.htm"
      st.giveItems(SIR_VASPERS_LETTER,1)
      st.takeItems(ORIMS_LETTER1,1)
      st.set("step",str(st.getInt("step")+2))
    # Everts Events
    elif event == "30633_1" :
      htmltext = "30633-02.htm"
      st.giveItems(BRIMSTONE2,1)
      st.addSpawn(27101,14027, 169896, -3646)
      st.set("cond","9")
    return htmltext


  def onTalk (self,npc,player):
    htmltext = "<html><body>You are either not carrying out your quest or don't meet the criteria.</body></html>"
    
    st = player.getQuestState(qn)
    if not st : return htmltext

    npcId = npc.getNpcId()
    id = st.getState()
    if npcId != 30630 and id != STARTED : return htmltext
    
    # Start the Quest, initialisation and check if the player can take it
    if id == CREATED:
      for var in STATS:
        st.set(var,"0")
      if player.getClassId().getId() in [0x0b, 0x04, 0x20] :
        if player.getLevel() < 39:
          htmltext = "30630-02.htm"
          st.exitQuest(1)
        else:
          if player.getClassId().getId() == 0x0b :
            htmltext = "30630-03.htm"
          else:
            htmltext = "30630-05.htm"
      else:
        htmltext = "30630-01.htm"
        st.exitQuest(1)
      return htmltext
    # already done
    elif id == COMPLETED:
      return "<html><body>This quest has already been completed.</body></html>"
    # in progress, player is working on the quest
    else:
      step = st.getInt("step")        # var init for easier working with it
      gem1 = st.getInt("gem1")
      gem2 = st.getInt("gem2")
      gem3 = st.getInt("gem3")
      gem456 = st.getInt("gem456")
      
      if npcId == NPC[8]: # orim
        if step == 1:
          htmltext = "30630-09.htm"
        elif step == 2:
          if gem1 == 6 and gem2 == 2 and gem3 == 3 and gem456 == 6:
            htmltext = "30630-11.htm"
          else:
            htmltext = "30630-10.htm"
        elif step == 3:
          htmltext = "30630-14.htm"
        elif step == 4:
          htmltext = "30630-15.htm"
        elif 4 < step < 13:
          htmltext = "30630-17.htm"
        elif step == 13 :
          htmltext = "30630-18.htm"
          st.set("cond", "8")
        elif step == 15 :
          htmltext = "30630-19.htm"
        
      elif npcId == NPC[1]: # alexandria
        if step == 1:
          htmltext = "30098-01.htm"
        elif step == 2:
          htmltext = "30098-04.htm"
        else:
          htmltext = "30098-05.htm"
        
      elif npcId == NPC[7]: #kaira
        if step == 2:
          if gem2 == 1:
            htmltext = "30476-01.htm"
          else :
            htmltext = "30476-03.htm"
        else:
          htmltext = "30476-04.htm"
        
      elif npcId == NPC[2]: # iker
        if step == 2:
          if gem1 == 1:
            htmltext = "30110-01.htm"
          elif gem1 in [2,3,4]:
            htmltext = "30110-04.htm"
          elif gem1 == 5:
            st.giveItems(AKLANTOS_GEM1,1)
            st.takeItems(IKERS_LIST,-1)
            st.takeItems(DIRE_WYRM_FANG,-1)
            st.takeItems(LETO_LIZARDMAN_CHARM,-1)
            st.takeItems(EN_GOLEM_HEARTSTONE,-1)
            htmltext = "30110-05.htm"
            st.set("gem1","6")
          elif gem1 == 6:
            htmltext = "30110-06.htm"
        elif step in [5,12]:
          htmltext = "30110-07.htm"
        elif step == 13 :
          htmltext = "30110-09.htm"
        elif step == 14 :
          htmltext = "30110-10.htm"
        
      elif npcId == NPC[0]: # lara
        if step == 2 :
          if gem3 == 1:
            htmltext = "30063-01.htm"
          elif gem3 == 2:
            htmltext = "30063-03.htm"
          elif gem3 == 3:
            htmltext = "30063-04.htm"
        elif step in [3,4]:
          htmltext = "30063-05.htm"
        
      elif npcId == NPC[9] and gem3 == 2 : #roderik
        htmltext = "30631-01.htm"
        
      elif npcId == NPC[10] and gem3 == 2 : # endrigo
        htmltext = "30632-01.htm"
        
      elif npcId == NPC[4]: # nestle
        if step == 2 :
          if gem456 == 1:
            htmltext = "30314-01.htm"
          elif gem456 == 2:
            htmltext = "30314-03.htm"
          else:
            htmltext = "30314-04.htm"
        
      elif npcId == NPC[6]: # leopold
        if step == 2:
          if gem456 == 2:
            htmltext = "30435-01.htm"
          elif gem456 in [3,4,5] :
            htmltext = "30435-03.htm"
          elif gem456 == 6:
            htmltext = "30435-04.htm"
        else:
          htmltext = "30435-05.htm"  
      
      elif npcId == NPC[5]: # klaus
        if step in [5,6]:
          htmltext = "30417-01.htm"
        elif step in [7,8]:
          htmltext = "30417-04.htm"
        elif step == 11:
          htmltext = "30417-05.htm"
          st.giveItems(SWORD_OF_BINDING,1)
          st.takeItems(VADINS_SANCTIONS,1)
          if st.getQuestItemsCount(SOULTRAP_CRYSTAL) :
            st.set("step","13")
            st.set("cond","7")
          else:
            st.set("step","12")
            st.set("cond","6")
        elif step in [12,13]:
          htmltext = "30417-06.htm"
        
      elif npcId == NPC[3]: # vadin
        if step in [7,8]:
          htmltext = "30188-01.htm"
          st.giveItems(VADINS_CRUCIFIX,1)
          st.takeItems(SIR_VASPERS_LETTER,1)
          st.set("step","9")
        elif step == 9:
          htmltext = "30188-02.htm"
        elif step == 10:
          htmltext = "30188-03.htm"
          st.takeItems(TAMLIN_ORC_AMULET,-1)
          st.giveItems(VADINS_SANCTIONS,1)
          st.takeItems(VADINS_CRUCIFIX,-1)
          st.set("step","11")
        elif step == 11:
          htmltext = "30188-04.htm"
        elif step == 12:
          htmltext = "30188-05.htm"
        
      elif npcId == NPC[11]: # evert
        if step == 13 and st.getQuestItemsCount(BRIMSTONE2)==0:
          htmltext = "30633-01.htm"
        elif step in [13,14]:
          htmltext = "30633-02.htm"
          st.addSpawn(27101,13631,169853,-3697)
          st.set("step","14")
        elif step == 15 :
          htmltext = "30633-03.htm"
      return htmltext  

  def onKill(self,npc,player,isPet):
    st = player.getQuestState(qn)
    if not st : return 
    if st.getState() != STARTED : return 

    npcId = npc.getNpcId()
    var,value,maxcount,chance,giveList,takeList=DROPLIST[npcId]
    random=st.getRandom(100)
    isValue = int(st.get(var))
    if int(st.get(var)) in value and random<chance:
      if takeList:
        if npcId == 27101 :
          if st.getItemEquipped(7) == SWORD_OF_BINDING:
            for give in giveList:
              st.giveItems(give,1)
            for take in takeList:
              st.takeItems(take,1)
              st.set(var,str(isValue+1))
            st.playSound("ItemSound.quest_middle")
            st.set("cond", "10")
            return "You trapped the Seal of Drevanul Prince Zeruel"
          else:
            st.set(var,str(isValue+1))
            st.set("cond","5")
        else:
          for give in giveList:
            count = st.getQuestItemsCount(give)
            if count == 0:
              st.giveItems(give,1)
              st.playSound("ItemSound.quest_middle")
              for take in takeList:
                st.takeItems(take,1)
              st.set(var,str(isValue+1))
              return 
      else:
        for give in giveList:
          count = st.getQuestItemsCount(give)
          if count < maxcount:
            st.giveItems(give,1)
            if count == maxcount-1:
              st.playSound("ItemSound.quest_middle")
              st.set(var,str(isValue+1)) 
            else:
              st.playSound("ItemSound.quest_itemget")
            return
    return
            
            

QUEST       = Quest(229,qn,"Test Of Witchcraft")
CREATED     = State('Start', QUEST)
STARTED     = State('Started', QUEST)
COMPLETED   = State('Completed', QUEST)


QUEST.setInitialState(CREATED)
QUEST.addStartNpc(30630)

for npcId in NPC:
  QUEST.addTalkId(npcId)

for mobId in DROPLIST.keys():
  QUEST.addKillId(mobId)
