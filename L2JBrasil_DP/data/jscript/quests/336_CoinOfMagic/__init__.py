# Coins of Magic version 0.1 by DrLecter

#Quest info
qn = "336_CoinOfMagic"
QUEST_NUMBER      = 336
QUEST_NAME        = "CoinOfMagic"
QUEST_DESCRIPTION = "Coins of Magic"
#Messages
default = "<html><body>You are either not carrying out your quest or don't meet the criteria.</body></html>"

#Quest items
COIN_DIAGRAM,KALDIS_COIN,MEMBERSHIP_1,MEMBERSHIP_2,MEMBERSHIP_3 = range(3811,3816)

BLOOD_MEDUSA,      BLOOD_WEREWOLF,      BLOOD_BASILISK,        BLOOD_DREVANUL,       \
BLOOD_SUCCUBUS,    BLOOD_DRAGON,        BELETHS_BLOOD,         MANAKS_BLOOD_WEREWOLF,\
NIAS_BLOOD_MEDUSA, GOLD_DRAGON,         GOLD_WYVERN,           GOLD_KNIGHT,          \
GOLD_GIANT,        GOLD_DRAKE,          GOLD_WYRM,             BELETHS_GOLD,         \
MANAKS_GOLD_GIANT, NIAS_GOLD_WYVERN,    SILVER_UNICORN,        SILVER_FAIRY,         \
SILVER_DRYAD,      SILVER_DRAGON,       SILVER_GOLEM,          SILVER_UNDINE,        \
BELETHS_SILVER,    MANAKS_SILVER_DRYAD, NIAS_SILVER_FAIRY = range(3472,3499)

#NPCs
SORINT, BERNARD, PAGE, HAGGER, STAN, RALFORD, FERRIS, COLLOB, PANO, DUNING, LORAIN = \
30232,  30702,   30696,30183,  30200,30165,   30847,  30092,  30078,30688,  30673

#MOBs
TIMAKARCH, TIMAKSOLD, TIMAKSHAM, LAKIN, HATARHANI, PUNISHMENT, SHACKLE, TIMAKORC, HEADLESS, ROYALSERVANT, \
MALRUKTUREN, FORMOR, FORMORELDER, VANORSHAMAN, TARLKHIWARRIOR, OLMAHUM, OLMAHUMW, HARITMATR, HARITSHA, \
SHACKL2, HEADLES2, MALRUKTURE2, ROYALSERVAN2 = \
20584, 20585, 20587, 20604, 20663,  20678, 20235, 20583, 20146, 20240,  20245, 20568, 20569, 20685, 20572,  20161, 20575, 20645, 20644, 20279, 20280, 20284, 20276
#C5 update - drops for these mobs are custom, i tried to make it more balanced. You confirm.
KOOKABU1,KOOKABU2,KOOKABU3,KOOKABU4,ANTELOP1,ANTELOP2,ANTELOP3,ANTELOP4,BANDERSN1,BANDERSN2,BANDERSN3,BANDERSN4,BUFALO1,BUFALO2,BUFALO3,BUFALO4=range(21274,21290)
GRAVE_L,DOOM_ARC,DOOM_KNI,DOOM_SERV,CLAW_SPL,PUNISH_SPL,WISDOM_SPL,WAILING_SPL,HUNGRY_C,BLOODY_G,NIHIL_INV,DARK_GUARD=21003,21008,20674,21006,21521,21531,21526,21539,20954,20960,20957,20959

PROMOTE={3:[BLOOD_WEREWOLF,GOLD_DRAKE,SILVER_FAIRY,BLOOD_DREVANUL,GOLD_KNIGHT,SILVER_GOLEM],
         2:[SILVER_DRYAD,BLOOD_BASILISK,BLOOD_SUCCUBUS,SILVER_UNDINE,GOLD_GIANT,GOLD_WYRM],}

COND={3:9,2:11}

DROP_LIST={
    TIMAKSHAM:[BLOOD_MEDUSA],TIMAKARCH:[BLOOD_MEDUSA],TIMAKSOLD:[BLOOD_MEDUSA],LAKIN:[BLOOD_MEDUSA],PUNISHMENT:[BLOOD_MEDUSA],KOOKABU1:[BLOOD_MEDUSA],KOOKABU2:[BLOOD_MEDUSA],KOOKABU3:[BLOOD_MEDUSA],KOOKABU4:[BLOOD_MEDUSA],BANDERSN1:[BLOOD_MEDUSA],BANDERSN2:[BLOOD_MEDUSA],BANDERSN3:[BLOOD_MEDUSA],BANDERSN4:[BLOOD_MEDUSA],PUNISH_SPL:[BLOOD_MEDUSA],HUNGRY_C:[BLOOD_MEDUSA],BLOODY_G:[BLOOD_MEDUSA],DOOM_SERV:[BLOOD_MEDUSA],GRAVE_L:[BLOOD_MEDUSA],\
    TIMAKORC:[GOLD_WYVERN],HATARHANI:[GOLD_WYVERN],SHACKLE:[GOLD_WYVERN],HEADLESS:[GOLD_WYVERN],ROYALSERVANT:[GOLD_WYVERN],MALRUKTUREN:[GOLD_WYVERN],SHACKL2:[GOLD_WYVERN],HEADLES2:[GOLD_WYVERN],ROYALSERVAN2:[GOLD_WYVERN],MALRUKTURE2:[GOLD_WYVERN],ANTELOP1:[GOLD_WYVERN],ANTELOP2:[GOLD_WYVERN],ANTELOP3:[GOLD_WYVERN],ANTELOP4:[GOLD_WYVERN],WAILING_SPL:[GOLD_WYVERN],CLAW_SPL:[GOLD_WYVERN],NIHIL_INV:[GOLD_WYVERN],DOOM_ARC:[GOLD_WYVERN],\
    FORMOR:[SILVER_UNICORN],FORMORELDER:[SILVER_UNICORN],VANORSHAMAN:[SILVER_UNICORN],TARLKHIWARRIOR:[SILVER_UNICORN],OLMAHUM:[SILVER_UNICORN],OLMAHUMW:[SILVER_UNICORN],BUFALO1:[SILVER_UNICORN],BUFALO2:[SILVER_UNICORN],BUFALO3:[SILVER_UNICORN],BUFALO4:[SILVER_UNICORN],WISDOM_SPL:[SILVER_UNICORN],DARK_GUARD:[SILVER_UNICORN],DOOM_KNI:[SILVER_UNICORN],\
    HARITMATR:[KALDIS_COIN],HARITSHA:[KALDIS_COIN]
    }

EXCHANGE_LIST={
    PAGE:  {GOLD_KNIGHT:{GOLD_WYVERN:10},SILVER_FAIRY:{SILVER_UNICORN:10},BLOOD_WEREWOLF:{BLOOD_MEDUSA:10},NIAS_BLOOD_MEDUSA:{BLOOD_MEDUSA:20}},
    LORAIN:{GOLD_KNIGHT:{GOLD_WYVERN:10},SILVER_GOLEM:{SILVER_UNICORN:10},BLOOD_DREVANUL:{BLOOD_MEDUSA:10},NIAS_GOLD_WYVERN:{GOLD_WYVERN:20}},
    HAGGER:{GOLD_DRAKE:{GOLD_WYVERN:10},SILVER_GOLEM:{SILVER_UNICORN:10},BLOOD_WEREWOLF:{BLOOD_MEDUSA:10},NIAS_SILVER_FAIRY:{SILVER_UNICORN:20}},
    RALFORD:{GOLD_WYRM:{GOLD_DRAKE:5,GOLD_KNIGHT:5},SILVER_DRYAD:{SILVER_GOLEM:5,SILVER_FAIRY:5},SILVER_UNDINE:{SILVER_GOLEM:5,SILVER_FAIRY:5},MANAKS_BLOOD_WEREWOLF:{BLOOD_DREVANUL:10,BLOOD_WEREWOLF:10}},
    STAN:   {GOLD_GIANT:{GOLD_DRAKE:5,GOLD_KNIGHT:5},BLOOD_BASILISK:{BLOOD_DREVANUL:5,BLOOD_WEREWOLF:5},SILVER_UNDINE:{SILVER_GOLEM:5,SILVER_FAIRY:5},MANAKS_SILVER_DRYAD:{SILVER_GOLEM:10,SILVER_FAIRY:10}},
    DUNING: {GOLD_GIANT:{GOLD_DRAKE:5,GOLD_KNIGHT:5},BLOOD_SUCCUBUS:{BLOOD_DREVANUL:5,BLOOD_WEREWOLF:5},SILVER_UNDINE:{SILVER_GOLEM:5,SILVER_FAIRY:5},MANAKS_GOLD_GIANT:{GOLD_DRAKE:10,GOLD_KNIGHT:10}},
    FERRIS:{BLOOD_DRAGON:{BLOOD_SUCCUBUS:5,BLOOD_BASILISK:5},SILVER_DRAGON:{SILVER_DRYAD:5,SILVER_UNDINE:5},GOLD_DRAGON:{GOLD_WYRM:5,GOLD_GIANT:5},BELETHS_BLOOD:{BLOOD_SUCCUBUS:10,BLOOD_BASILISK:10}},
    COLLOB:{BLOOD_DRAGON:{BLOOD_SUCCUBUS:5,BLOOD_BASILISK:5},SILVER_DRAGON:{SILVER_DRYAD:5,SILVER_UNDINE:5},GOLD_DRAGON:{GOLD_WYRM:5,GOLD_GIANT:5},BELETHS_GOLD:{GOLD_WYRM:10,GOLD_GIANT:10}},
    PANO:  {BLOOD_DRAGON:{BLOOD_SUCCUBUS:5,BLOOD_BASILISK:5},SILVER_DRAGON:{SILVER_DRYAD:5,SILVER_UNDINE:5},GOLD_DRAGON:{GOLD_WYRM:5,GOLD_GIANT:5},BELETHS_SILVER:{SILVER_DRYAD:10,SILVER_UNDINE:10}}
    }

GAMBLE_LIST={
    PAGE:  {GOLD_KNIGHT:[GOLD_WYVERN],SILVER_FAIRY:[SILVER_UNICORN],BLOOD_WEREWOLF:[BLOOD_MEDUSA],NIAS_BLOOD_MEDUSA:[BLOOD_MEDUSA]},
    LORAIN:{GOLD_KNIGHT:[GOLD_WYVERN],SILVER_GOLEM:[SILVER_UNICORN],BLOOD_DREVANUL:[BLOOD_MEDUSA],NIAS_GOLD_WYVERN:[GOLD_WYVERN]},
    HAGGER:{GOLD_DRAKE:[GOLD_WYVERN],SILVER_GOLEM:[SILVER_UNICORN],BLOOD_WEREWOLF:[BLOOD_MEDUSA],NIAS_SILVER_FAIRY:[SILVER_UNICORN]},
    RALFORD:{GOLD_WYRM:[GOLD_DRAKE],SILVER_DRYAD:[SILVER_FAIRY],SILVER_UNDINE:[SILVER_GOLEM],MANAKS_BLOOD_WEREWOLF:[BLOOD_DREVANUL,BLOOD_WEREWOLF]},
    STAN:   {GOLD_GIANT:[GOLD_DRAKE],BLOOD_BASILISK:[BLOOD_WEREWOLF],SILVER_UNDINE:[SILVER_FAIRY],MANAKS_SILVER_DRYAD:[SILVER_GOLEM,SILVER_FAIRY]},
    DUNING: {GOLD_GIANT:[GOLD_DRAKE],BLOOD_SUCCUBUS:[BLOOD_DREVANUL],SILVER_UNDINE:[SILVER_FAIRY],MANAKS_GOLD_GIANT:[GOLD_DRAKE,GOLD_KNIGHT]},
    FERRIS:{BLOOD_DRAGON:[BLOOD_BASILISK],SILVER_DRAGON:[SILVER_DRYAD],GOLD_DRAGON:[GOLD_GIANT],BELETHS_BLOOD:[BLOOD_SUCCUBUS,BLOOD_BASILISK]},
    COLLOB:{BLOOD_DRAGON:[BLOOD_SUCCUBUS],SILVER_DRAGON:[SILVER_UNDINE],GOLD_DRAGON:[GOLD_WYRM],BELETHS_GOLD:[GOLD_WYRM,GOLD_GIANT]},
    PANO:  {BLOOD_DRAGON:[BLOOD_BASILISK],SILVER_DRAGON:[SILVER_DRYAD],GOLD_DRAGON:[GOLD_WYRM],BELETHS_SILVER:[SILVER_DRYAD,SILVER_UNDINE]}
    }

GAMBLE_PRICE={3:{2:4,3:8,4:9},2:{2:3,3:7,4:9},1:{2:3,3:7,4:9}}

GAMBLE_COINS={'0':'gold','1':'silver','2':'blood'}

EXCHANGE_LEVEL={PAGE:3,LORAIN:3,HAGGER:3,RALFORD:2,STAN:2,DUNING:2,FERRIS:1,COLLOB:1,PANO:1}

TRADE_LIST={
    206:  [[BELETHS_BLOOD,1],[SILVER_DRAGON,1],[GOLD_WYRM,13]],                       # Demon's Staff
    233:  [[BELETHS_GOLD,1],[BLOOD_DRAGON,1],[SILVER_DRYAD,1],[GOLD_GIANT,1]],        # Dark Screamer
    303:  [[BELETHS_SILVER,1],[GOLD_DRAGON,1],[BLOOD_SUCCUBUS,1],[BLOOD_BASILISK,2]], # Widow Maker
    132:  [[GOLD_DRAGON,1],[SILVER_DRAGON,1],[BLOOD_DRAGON,1],[SILVER_UNDINE,1]],     # Sword of Limit
    
    2435: [[MANAKS_GOLD_GIANT,1]],                                                    # Demon's Boots
    472:  [[MANAKS_SILVER_DRYAD,1],[SILVER_DRYAD,1]],                                 # Demon's Stockings
    2459: [[MANAKS_GOLD_GIANT,1]],                                                    # Demon's Gloves
    2414: [[MANAKS_BLOOD_WEREWOLF,1],[GOLD_WYRM,1],[GOLD_GIANT,1]],                   # Full Plate Helm

    852:  [[NIAS_BLOOD_MEDUSA,2],[BLOOD_DREVANUL,2],[GOLD_DRAKE,2],[GOLD_KNIGHT,3]],  # Moonstone Earring
    855:  [[NIAS_BLOOD_MEDUSA,7],[BLOOD_DREVANUL,5],[SILVER_GOLEM,5],[GOLD_KNIGHT,5]],# Nassens Earring
    886:  [[NIAS_GOLD_WYVERN,5],[GOLD_DRAKE,4],[SILVER_GOLEM,4],[BLOOD_DREVANUL,4]],  # Ring of Binding
    916:  [[NIAS_SILVER_FAIRY,5],[SILVER_FAIRY,3],[GOLD_KNIGHT,3],[BLOOD_DREVANUL,3]],# Necklace of Protection
    }

import sys
from com.it.br import Config
from com.it.br.gameserver.model.quest import State
from com.it.br.gameserver.model.quest import QuestState
from com.it.br.gameserver.model.quest.jython import QuestJython as JQuest

def promote(st) :
   grade = st.getInt("grade")
   if grade == 1 :
      html = "30232-15.htm"
   else :
      h = 0
      for i in range(len(PROMOTE[grade])) :
         if st.getQuestItemsCount(PROMOTE[grade][i]):
            h += 1
      if h == i + 1 :
         for j in PROMOTE[grade] :
             st.takeItems(j,1)
         html = "30232-"+str(19-grade)+".htm"
         st.takeItems(3812+grade,-1)
         st.giveItems(3811+grade,1)
         st.set ("grade",str(grade-1))
         cond=COND[grade]
         st.playSound("ItemSound.quest_fanfare_middle")
      else :
         html = "30232-"+str(16-grade)+".htm"
         cond=COND[grade]-1
      st.set("cond",str(cond))
   return html

# main code
class Quest (JQuest) :

 def __init__(self,id,name,descr): JQuest.__init__(self,id,name,descr)

 def onEvent (self,event,st) :
    htmltext = event
    grade=st.getInt("grade")
    cond = st.getInt("cond")
    if event == "30702-06.htm":
       if cond < 7:
         st.set("cond","7")
         st.playSound("ItemSound.quest_accept")
    elif event == "30232-22.htm" :
       if cond < 6:
         st.set("cond","6")
    elif event == "30232-23.htm" :
       if cond < 5:
         st.set("cond","5")
    elif event == "30702-02.htm":
       st.set("cond","2")
    elif event == "30232-05.htm" :
       st.setState(SOLO)
       st.playSound("ItemSound.quest_accept")
       st.giveItems(COIN_DIAGRAM,1)
       st.set("cond","1")
       st.set("grade","0")
    elif event in ["30232-04.htm","30232-18a.htm"] :
       st.exitQuest(1)
       st.playSound("ItemSound.quest_giveup")
    elif event == "raise" :
       htmltext = promote(st)
    elif event.isdigit() :
       item = int(event)
       if item in TRADE_LIST.keys() :
          j = 0
          k = len(TRADE_LIST[item])
          for i in range(len(TRADE_LIST[item])) :
              if st.getQuestItemsCount(TRADE_LIST[item][i][0]) >= TRADE_LIST[item][i][1] :
                 j += 1
          if j == k :
             for l in range(len(TRADE_LIST[item])) :
                st.takeItems(TRADE_LIST[item][l][0],TRADE_LIST[item][l][1])
             st.giveItems(item,1)
             htmltext = "30232-24a.htm"
          else :
             htmltext = "30232-24.htm"
    elif event.startswith("Li_"):
       action,npc,coin=event.split("_")
       if grade <= EXCHANGE_LEVEL[int(npc)]:
         if int(coin) in EXCHANGE_LIST[int(npc)].keys():
           htmltext=st.showHtmlFile(npc+"-06.htm").replace("%itemid%",coin)
         else:
           htmltext="Cheating huh?"
           st.exitQuest(1)
       else:
         htmltext=npc+"-54.htm"
    elif event.startswith("Ex_"):
       action,npc,coin,qty=event.split("_")
       npc,coin,qty=int(npc),int(coin),int(qty)
       if grade <= EXCHANGE_LEVEL[npc]:
         if coin in EXCHANGE_LIST[npc].keys():
            j=0
            i=len(EXCHANGE_LIST[npc][coin])
            for item in EXCHANGE_LIST[npc][coin].keys():
                if st.getQuestItemsCount(item) >= EXCHANGE_LIST[npc][coin][item]*qty:
                    j+=1
            if i==j:
               for k in EXCHANGE_LIST[npc][coin].keys():
                 st.takeItems(k,EXCHANGE_LIST[npc][coin][k]*qty)
               st.giveItems(coin,qty)
               st.playSound("ItemSound.quest_itemget")
               htmltext=str(npc)+"-07.htm"
            else:
               htmltext=str(npc)+"-10.htm"
         else:
           htmltext="Exchange not possible"
       else:
         htmltext="I can't trade with you"
    elif event.startswith("Ga_"):
        action,npc,coin,tries=event.split("_")
        npc,coin,tries=int(npc),int(coin),int(tries)
        if npc in GAMBLE_LIST.keys() and \
           coin in GAMBLE_LIST[npc].keys() and \
           grade <= EXCHANGE_LEVEL[npc] and \
           tries in range(2,5) :
            required=GAMBLE_PRICE[EXCHANGE_LEVEL[npc]][tries]
            if coin in [NIAS_GOLD_WYVERN,NIAS_SILVER_FAIRY,NIAS_BLOOD_MEDUSA]:
               required *= 2
            j=0
            i=len(GAMBLE_LIST[npc][coin])
            for item in GAMBLE_LIST[npc][coin]:
               if st.getQuestItemsCount(item) >= required :
                  j+=1
            if i==j:
              for k in GAMBLE_LIST[npc][coin]:
                 st.takeItems(k,required)
              grid=[]
              for i in range(3) :
                grid.append(st.getRandom(3))
              for i in range(len(grid)): grid[i]=str(grid[i])
              st.set("chosen","? ? ?")
              st.set("grid"," ".join(grid))
              st.set("tries",str(tries-1))
              st.set("current","1")
              st.set("coin",str(coin))
              st.set("npc",str(npc))
              htmltext=str(npc)+"-11.htm"
            else:
               htmltext=str(npc)+"-10.htm"
        else:
          htmltext="killall nalipriest"
    elif event.startswith("_"):
       event = int(event.replace("_",""))
       npc=st.get("npc")
       if event in range(13,22):
          if event in range(13,16):
            current=1
            next="14"
          elif event in range(16,19):
            current=2
            next="17"
          elif event in range(19,22):
            current=3
          if event in [13,16,19]:
            answer=0
          elif event in [14,17,20]:
            answer=1
          elif event in [15,18,21]:
            answer=2
          stored=st.getInt("current")
          if stored == current :
            chosen = st.get("chosen").split()
            chosen[current-1]=str(answer)
            st.set("chosen"," ".join(chosen))
            if current == 3:
              count=0
              grid = st.get("grid").split()
              tries=st.getInt("tries")
              for i in range(3):
                  if chosen[i]==grid[i]:
                     count+=1
              if count == 3:
                 st.giveItems(st.getInt("coin"),1)
                 next = "20"
              else:
                 if tries :
                    st.set("current","1")
                    st.set("tries",str(tries-1))
                    if count == 1 :
                       next="50"
                    elif count == 2 :
                       next="51"
                    elif count == 0 :
                       next="52"
                 else :
                    next="23"
                    msg=[]
                    for i in grid:
                       msg.append(GAMBLE_COINS[i])
                    for var in ["grid","current","tries","chosen","coin","npc"]:
                       st.unset(var)
                    return st.showHtmlFile(npc+"-"+next+".htm").replace("%first%",msg[0]).replace("%second%",msg[1]).replace("%third%",msg[2])
            else :
              st.set("current",str(current+1))
          htmltext=npc+"-"+next+".htm"
    return htmltext

 def onTalk (self,npc,player):
   htmltext = default
   st = player.getQuestState(qn)
   if not st : return htmltext

   npcId = npc.getNpcId()
   id = st.getState()
   if npcId != SORINT and id == CREATED : return htmltext
   if npcId != SORINT and npcId != BERNARD and id == SOLO : return htmltext
   
   cond=st.getInt("cond")
   grade = st.getInt("grade")
   if npcId == SORINT :
      if id == CREATED :
         if player.getLevel() < 40 :
             htmltext = "30232-01.htm"
             st.exitQuest(1)
         else :
             htmltext = "30232-02.htm"
      else :
         if st.getQuestItemsCount(COIN_DIAGRAM) :
            if st.getQuestItemsCount(KALDIS_COIN) :
              st.takeItems(KALDIS_COIN,-1)
              st.takeItems(COIN_DIAGRAM,-1)
              st.giveItems(MEMBERSHIP_3,1)
              st.setState(PARTY)
              st.set("grade","3")
              st.set("cond","4")
              st.playSound("ItemSound.quest_fanfare_middle")
              htmltext = "30232-07.htm"
            else :
              htmltext = "30232-06.htm"
         else:
            if grade == 3 :
               htmltext = "30232-12.htm"
            elif grade == 2 :
               htmltext = "30232-11.htm"
            elif grade == 1 :
               htmltext = "30232-10.htm"
   elif npcId == BERNARD:
      if st.getQuestItemsCount(COIN_DIAGRAM) and grade == 0:
         htmltext = "30702-01.htm"
      elif grade == 3 :
         htmltext = "30702-05.htm"
   elif npcId in EXCHANGE_LIST.keys() and grade :
      htmltext = str(npcId)+"-01.htm"
   return htmltext

 def onKill(self,npc,player,isPet):
   npcId=npc.getNpcId()
   st = 0
   # solo section of the quest
   if npcId in [HARITMATR, HARITSHA] :
      st = player.getQuestState(qn)
      if not st: return
      if st.getState() != SOLO : return
   if not npcId in [HARITMATR, HARITSHA] :
      # for party-kill mobs of this quest, get a random player among those who await a drop
      partyMember = self.getRandomPartyMemberState(player,PARTY)
      if not partyMember : return
      st = partyMember.getQuestState(qn) 
   
   cond=st.getInt("cond")
   grade=st.getInt("grade")
   chance=int((npc.getLevel() - grade * 3 - 20)*Config.RATE_DROP_QUEST)
   item=DROP_LIST[npcId][0]
   random = st.getRandom(100)
   if item == KALDIS_COIN :
     if cond == 2 :
       if not st.getQuestItemsCount(item) and random < (chance - 10) :
          st.giveItems(item,1)
          st.playSound("ItemSound.quest_middle")
          st.set("cond","3")
   elif random < chance:
      st.giveItems(item,1)
      st.playSound("ItemSound.quest_itemget")
   return  

# Quest class and state definition
QUEST       = Quest(QUEST_NUMBER, str(QUEST_NUMBER)+"_"+QUEST_NAME, QUEST_DESCRIPTION)
CREATED     = State('Start',     QUEST)
SOLO        = State('Solo',   QUEST)
PARTY       = State('Party',   QUEST)
COMPLETED   = State('Completed', QUEST)

QUEST.setInitialState(CREATED)

# Quest NPC starter initialization
QUEST.addStartNpc(SORINT)
# Quest initialization
for npc in [SORINT, BERNARD, PAGE, HAGGER, STAN, RALFORD, FERRIS, COLLOB, PANO, DUNING, LORAIN]:
   QUEST.addTalkId(npc)

for mob in DROP_LIST.keys():
   QUEST.addKillId(mob)