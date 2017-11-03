#
# Created by DraX on 2005.09.08
# C4 Update by DrLecter
#

import sys
from com.it.br.gameserver.model.quest        import State
from com.it.br.gameserver.model.quest        import QuestState
from com.it.br.gameserver.model.quest.jython import QuestJython as JQuest
from com.it.br.gameserver.network.serverpackets      import CreatureSay
from com.it.br.gameserver.datatables.sql         import SpawnTable

qn = "334_TheWishingPotion"

# General Rewards
ADENA                  =   57
NECKLACE_OF_GRACE      =  931
HEART_OF_PAAGRIO       = 3943
R1=[3081,3076,3075,3074,4917,3077,3080,3079,3078,4928,4931,4932,5013,3067,3064,3061,3062,3058,4206,3065,3060,3063,4208,3057,3059,3066,4911,4918,3092,3039,4922,3091,3093,3431]
R2=[3430,3429,3073,3941,3071,3069,3072,4200,3068,3070,4912,3100,3101,3098,3094,3102,4913,3095,3096,3097,3099,3085,3086,3082,4907,3088,4207,3087,3084,3083,4929,4933,4919,3045]
R3=[4923,4201,4914,3942,3090,4909,3089,4930,4934,4920,3041,4924,3114,3105,3110,3104,3113,3103,4204,3108,4926,3112,3107,4205,3109,3111,3106,4925,3117,3115,3118,3116,4927]
R4=[1979,1980,2952,2953]
#Quest ingredients and rewards
WISH_POTION,ANCIENT_CROWN,CERTIFICATE_OF_ROYALTY = range(3467,3470)
ALCHEMY_TEXT,SECRET_BOOK,POTION_RECIPE_1,POTION_RECIPE_2,MATILDS_ORB,FORBIDDEN_LOVE_SCROLL  = range(3678,3684)
AMBER_SCALE,WIND_SOULSTONE,GLASS_EYE,HORROR_ECTOPLASM,SILENOS_HORN,ANT_SOLDIER_APHID,TYRANTS_CHITIN,BUGBEAR_BLOOD = range(3684,3692)
#NPCs
GRIMA                     = 27135
SUCCUBUS_OF_SEDUCTION     = 27136
GREAT_DEMON_KING          = 27138
SECRET_KEEPER_TREE        = 27139
SANCHES                   = 27153
BONAPARTERIUS             = 27154
RAMSEBALIUS               = 27155
TORAI                     = 30557
ALCHEMIST_MATILD          = 30738
RUPINA                    = 30742
WISDOM_CHEST              = 30743
#MOBs
WHISPERING_WIND           = 20078
ANT_RECRUIT               = 20087
ANT_WARRIOR_CAPTAIN       = 20088
SILENOS                   = 20168
TYRANT                    = 20192
TYRANT_KINGPIN            = 20193
AMBER_BASILISK            = 20199
HORROR_MIST_RIPPER        = 20227
TURAK_BUGBEAR             = 20248
TURAK_BUGBEAR_WARRIOR     = 20249
GLASS_JAGUAR              = 20250
#DROPLIST
DROPLIST={AMBER_BASILISK:[AMBER_SCALE,15],WHISPERING_WIND:[WIND_SOULSTONE,20],GLASS_JAGUAR:[GLASS_EYE,35],HORROR_MIST_RIPPER:[HORROR_ECTOPLASM,15],
          SILENOS:[SILENOS_HORN,30],ANT_RECRUIT:[ANT_SOLDIER_APHID,40],ANT_WARRIOR_CAPTAIN:[ANT_SOLDIER_APHID,40],TYRANT:[TYRANTS_CHITIN,50],
          TYRANT_KINGPIN:[TYRANTS_CHITIN,50],TURAK_BUGBEAR:[BUGBEAR_BLOOD,25],TURAK_BUGBEAR_WARRIOR:[BUGBEAR_BLOOD,25]}

# set of random messages
MESSAGES={SUCCUBUS_OF_SEDUCTION:["Do you wanna be loved?","Do you need love?","Let me love you...","Want to know what love is?","Are you in need of love?","Me love you long time"],
          GRIMA:["hey hum hum!","boom! boom!","...","Ki ab kya karein hum"],
          }

def check_ingredients(st,required) :
    if st.getQuestItemsCount(AMBER_SCALE) != required : return 0
    if st.getQuestItemsCount(WIND_SOULSTONE) != required : return 0
    if st.getQuestItemsCount(GLASS_EYE) != required : return 0
    if st.getQuestItemsCount(HORROR_ECTOPLASM) != required : return 0
    if st.getQuestItemsCount(SILENOS_HORN) != required : return 0
    if st.getQuestItemsCount(ANT_SOLDIER_APHID) != required : return 0
    if st.getQuestItemsCount(TYRANTS_CHITIN) != required : return 0
    if st.getQuestItemsCount(BUGBEAR_BLOOD) != required : return 0
    return 1

def autochat(npc,text) :
    if npc: npc.broadcastPacket(CreatureSay(npc.getObjectId(),0,npc.getName(),text))
    return

class Quest (JQuest) :

 def __init__(self,id,name,descr): JQuest.__init__(self,id,name,descr)

 def onAdvEvent (self,event,npc,player):
   st = player.getQuestState(qn)
   if not st: return
   htmltext = event
   player=st.getPlayer()
   if event == "30738-03.htm":
     st.set("cond","1")
     st.setState(STARTED)
     st.playSound("ItemSound.quest_accept")
     if st.getQuestItemsCount(ALCHEMY_TEXT) >= 2: st.takeItems(ALCHEMY_TEXT,-1)
     if st.getQuestItemsCount(ALCHEMY_TEXT) == 0: st.giveItems(ALCHEMY_TEXT,1)
     htmltext = "30738-03.htm"
   if event == "30738-06.htm":
     if st.getQuestItemsCount(WISH_POTION) :
       htmltext = "30738-13.htm"
     else :
       st.playSound("ItemSound.quest_accept")
       st.set("cond","3")
       if st.getQuestItemsCount(ALCHEMY_TEXT) >= 1: st.takeItems(ALCHEMY_TEXT,-1)
       if st.getQuestItemsCount(SECRET_BOOK) >= 1: st.takeItems(SECRET_BOOK,-1)
       if st.getQuestItemsCount(POTION_RECIPE_1) >= 2: st.takeItems(POTION_RECIPE_1,-1)
       if st.getQuestItemsCount(POTION_RECIPE_1) == 0: st.giveItems(POTION_RECIPE_1,1)
       if st.getQuestItemsCount(POTION_RECIPE_2) >= 2: st.takeItems(POTION_RECIPE_2,-1)
       if st.getQuestItemsCount(POTION_RECIPE_2) == 0: st.giveItems(POTION_RECIPE_2,1)
       if st.getQuestItemsCount(MATILDS_ORB) : htmltext = "30738-12.htm"
   if event == "30738-10.htm":
     if check_ingredients(st,1) :
       st.playSound("ItemSound.quest_finish")
       st.takeItems(ALCHEMY_TEXT,-1)
       st.takeItems(SECRET_BOOK,-1)
       st.takeItems(POTION_RECIPE_1,-1)
       st.takeItems(POTION_RECIPE_2,-1)
       st.takeItems(AMBER_SCALE,-1)
       st.takeItems(WIND_SOULSTONE,-1)
       st.takeItems(GLASS_EYE,-1)
       st.takeItems(HORROR_ECTOPLASM,-1)
       st.takeItems(SILENOS_HORN,-1)
       st.takeItems(ANT_SOLDIER_APHID,-1)
       st.takeItems(TYRANTS_CHITIN,-1)
       st.takeItems(BUGBEAR_BLOOD,-1)
       if not st.getQuestItemsCount(MATILDS_ORB) : st.giveItems(MATILDS_ORB,1)
       st.giveItems(WISH_POTION,1)
       st.set("cond","5")
     else :
       htmltext="You don't have required items"
   elif event == "30738-14.htm":
     # if you dropped or destroyed your wish potion, you are not able to see the wish list
     if st.getQuestItemsCount(WISH_POTION) :
       htmltext = "30738-15.htm"
#### WISH I : Please make me into a loving person.
   elif event == "30738-16.htm":
     if st.getQuestItemsCount(WISH_POTION) :
       st.set("wish","1")
       st.startQuestTimer("matild_timer1",3000,npc)
       st.takeItems(WISH_POTION,1)
       npc.setBusy(True)
     else:
       htmltext = "30738-14.htm"
#### WISH II : I want to become an extremely rich person. How about 100 million adena?! 
   elif event == "30738-17.htm":
     if st.getQuestItemsCount(WISH_POTION) :
       st.set("wish","2")
       st.startQuestTimer("matild_timer1",3000,npc)
       st.takeItems(WISH_POTION,1)
       npc.setBusy(True)
     else:
       htmltext = "30738-14.htm"
#### WISH III : I want to be a king in this world.
   elif event == "30738-18.htm":
     if st.getQuestItemsCount(WISH_POTION) :
       st.set("wish","3")
       st.startQuestTimer("matild_timer1",3000,npc)
       st.takeItems(WISH_POTION,1)
       npc.setBusy(True)
     else:
       htmltext = "30738-14.htm"
#### WISH IV : I'd like to become the wisest person in the world.
   elif event == "30738-19.htm":
     if st.getQuestItemsCount(WISH_POTION) >= 1:
       st.set("wish","4")
       st.startQuestTimer("matild_timer1",3000,npc)
       st.takeItems(WISH_POTION,1)
       npc.setBusy(True)
     else:
       htmltext = "30738-14.htm"
   elif event == "matild_timer1":
     autochat(npc,"OK, everybody pray fervently!")
     st.startQuestTimer("matild_timer2",4000,npc)
     return
   elif event == "matild_timer2":
     autochat(npc,"Both hands to heaven, everybody yell together!")
     st.startQuestTimer("matild_timer3",4000,npc)
     return
   elif event == "matild_timer3":
     autochat(npc,"One! Two! May your dreams come true!")
     wish = st.getInt("wish")
     WISH_CHANCE = st.getRandom(100)
     if wish == 1 :
       if WISH_CHANCE <= 50:
         autochat(st.addSpawn(SUCCUBUS_OF_SEDUCTION,200000),MESSAGES[SUCCUBUS_OF_SEDUCTION][st.getRandom(len(MESSAGES))])
         autochat(st.addSpawn(SUCCUBUS_OF_SEDUCTION,200000),MESSAGES[SUCCUBUS_OF_SEDUCTION][st.getRandom(len(MESSAGES))])
         autochat(st.addSpawn(SUCCUBUS_OF_SEDUCTION,200000),MESSAGES[SUCCUBUS_OF_SEDUCTION][st.getRandom(len(MESSAGES))])
       else:
         autochat(st.addSpawn(RUPINA,120000),"Your love... love!")
     elif wish == 2 :
       if WISH_CHANCE <= 33 :
         autochat(st.addSpawn(GRIMA,200000),MESSAGES[GRIMA][st.getRandom(len(MESSAGES))])
         autochat(st.addSpawn(GRIMA,200000),MESSAGES[GRIMA][st.getRandom(len(MESSAGES))])
         autochat(st.addSpawn(GRIMA,200000),MESSAGES[GRIMA][st.getRandom(len(MESSAGES))])
       else :
         st.giveItems(ADENA,10000)
     elif wish == 3 :
       if WISH_CHANCE <= 33 :
         st.giveItems(CERTIFICATE_OF_ROYALTY,1)
       elif WISH_CHANCE >= 66 :
         st.giveItems(ANCIENT_CROWN,1)
       else:
         spawnedNpc=st.addSpawn(SANCHES,player,True,0)
         autochat(spawnedNpc,"Who dares to call the dark Monarch?!")
         st.startQuestTimer("sanches_timer1",200000,spawnedNpc)
     elif wish == 4 :
       if WISH_CHANCE <= 33:
         st.giveItems(R1[st.getRandom(len(R1))],1)
         st.giveItems(R2[st.getRandom(len(R2))],1)
         st.giveItems(R3[st.getRandom(len(R3))],1)
         if not st.getRandom(3):
            st.giveItems(HEART_OF_PAAGRIO,1)
       else:
         autochat(st.addSpawn(WISDOM_CHEST,120000),"I contain the wisdom, I am the wisdom box!")
     npc.setBusy(False)
     return
   elif event == "sanches_timer1" :
     autochat(npc,"Hehehe, i'm just wasting my time here!")
     npc.deleteMe()
     return
   elif event == "bonaparterius_timer1" :
     autochat(npc,"A worth opponent would be a good thing")
     npc.deleteMe()
   elif event == "ramsebalius_timer1" :
     autochat(npc,"Your time is up!")
     npc.deleteMe()
     return
   elif event == "greatdemon_timer1" :
     autochat(npc,"Do not interrupt my eternal rest again!")
     npc.deleteMe()
     return
   return htmltext

 def onTalk (self,npc,player):
   htmltext = "<html><body>You are either not carrying out your quest or don't meet the criteria.</body></html>"
   st = player.getQuestState(qn)
   if not st : return htmltext
   npcId = npc.getNpcId()
   cond = st.getInt("cond")
   id = st.getState()
   if npcId != ALCHEMIST_MATILD and id == CREATED : return htmltext
   if npcId == TORAI and st.getQuestItemsCount(FORBIDDEN_LOVE_SCROLL) :
       st.takeItems(FORBIDDEN_LOVE_SCROLL,1)     
       st.giveItems(ADENA,500000)
       htmltext = "30557-01.htm"
   elif npcId == WISDOM_CHEST :
     st.giveItems(R1[st.getRandom(len(R1))],1)
     st.giveItems(R2[st.getRandom(len(R2))],1)
     st.giveItems(R3[st.getRandom(len(R3))],1)
     if not st.getRandom(3):
        st.giveItems(HEART_OF_PAAGRIO,1)
     st.giveItems(4409,1)
     st.giveItems(4408,1)
     htmltext = "30743-0"+str(st.getRandom(6)+1)+".htm"
     npc.deleteMe()
   elif npcId == RUPINA:
     if st.getRandom(100) <= 4:
       st.giveItems(NECKLACE_OF_GRACE,1)
       htmltext = "30742-01.htm"
     else:
       st.giveItems(R4[st.getRandom(len(R4))],1)
       htmltext = "30742-02.htm"
     npc.decayMe()
   elif npcId == ALCHEMIST_MATILD:
     if npc.isBusy() :
       htmltext = "30738-20.htm"
     elif player.getLevel() <= 29 :
       htmltext = "30738-21.htm"
       st.exitQuest(1)
     elif cond == 5 and st.getQuestItemsCount(MATILDS_ORB) :
       htmltext = "30738-11.htm"
     elif cond == 4 and check_ingredients(st,1):
       htmltext = "30738-08.htm"
     elif cond == 3 and not check_ingredients(st,1):
       htmltext = "30738-07.htm"       
     elif cond == 2 or (st.getQuestItemsCount(ALCHEMY_TEXT) and st.getQuestItemsCount(SECRET_BOOK)) :
       htmltext = "30738-05.htm"
     elif cond == 1 or (st.getQuestItemsCount(ALCHEMY_TEXT) and not st.getQuestItemsCount(SECRET_BOOK)) :
       htmltext = "30738-04.htm"
     else:
       htmltext = "30738-01.htm"
   return htmltext

 def onKill(self,npc,player,isPet):
   st = player.getQuestState(qn)
   if not st : return
   id = st.getState()
   if id == CREATED: return
   if id != STARTED: st.setState(STARTED)
   npcId = npc.getNpcId()
   cond = st.getInt("cond")
   if npcId == SECRET_KEEPER_TREE and cond == 1 and not st.getQuestItemsCount(SECRET_BOOK):
      st.set("cond","2")
      st.giveItems(SECRET_BOOK,1)
      st.playSound("ItemSound.quest_itemget")
   elif npcId in DROPLIST.keys() and cond == 3 :
      item,chance=DROPLIST[npcId]
      if st.getRandom(100) <= chance and not st.getQuestItemsCount(item) :
         st.giveItems(item,1)
         if check_ingredients(st,1):
            st.playSound("ItemSound.quest_middle")
            st.set("cond","4")
         else: st.playSound("ItemSound.quest_itemget")
   else:
     if npcId == SUCCUBUS_OF_SEDUCTION:
       if st.getRandom(100) <= 3 :
         st.playSound("ItemSound.quest_itemget")
         st.giveItems(FORBIDDEN_LOVE_SCROLL,1)
     elif npcId == GRIMA:
       if st.getRandom(100) < 4 :
          st.playSound("ItemSound.quest_itemget")
          if st.getRandom(1000) == 0 :
             st.giveItems(ADENA,100000000)
          else:
             st.giveItems(ADENA,900000)
     elif npcId == SANCHES :
       try :
         st.getQuestTimer("sanches_timer1").cancel()
         if st.getRandom(100) <= 50 :
            autochat(npc,"It's time to come out my Remless... Bonaparterius!")
            spawnedNpc=st.addSpawn(BONAPARTERIUS,npc,True,0)
            autochat(spawnedNpc,"I am the Great Emperor's son!")
            st.startQuestTimer("bonaparterius_timer1",600000,spawnedNpc)
         else :
            st.giveItems(R4[st.getRandom(len(R4))],1)
       except : pass
     elif npcId == BONAPARTERIUS:
       try :
         st.getQuestTimer("bonaparterius_timer1").cancel()
         autochat(npc,"Only Ramsebalius would be able to avenge me!")
         if st.getRandom(100) <= 50 :
           spawnedNpc=st.addSpawn(RAMSEBALIUS,npc,True,0)
           autochat(spawnedNpc,"Meet the absolute ruler!")
           st.startQuestTimer("ramsebalius_timer1",600000,spawnedNpc)
         else :
           st.giveItems(R4[st.getRandom(len(R4))],1)
       except : pass
     elif npcId == RAMSEBALIUS:
       try :
         st.getQuestTimer("ramsebalius_timer1").cancel()
         autochat(npc,"You evil piece of...")
         if st.getRandom(100) <= 50 :
           spawnedNpc=st.addSpawn(GREAT_DEMON_KING,npc,True,0)
           autochat(spawnedNpc,"Who dares to kill my fiendly minion?!")
           st.startQuestTimer("greatdemon_timer1",600000,spawnedNpc)
         else :
           st.giveItems(R4[st.getRandom(len(R4))],1)
       except: pass
     elif npcId == GREAT_DEMON_KING:
       try :
         st.getQuestTimer("greatdemon_timer1").cancel()
         st.giveItems(ADENA,1412965)
         st.playSound("ItemSound.quest_itemget")
       except: pass
   return

QUEST     = Quest(334,qn,"The Wishing Potion")
CREATED   = State('Start',     QUEST)
STARTED   = State('started',   QUEST)
#Following states kept for backwards compatibility only.
MIDDLE    = State('middle',    QUEST)
END       = State('end',       QUEST)
COMPLETED = State('completed', QUEST)

QUEST.setInitialState(CREATED)

QUEST.addStartNpc(ALCHEMIST_MATILD)

QUEST.addTalkId(ALCHEMIST_MATILD)
QUEST.addTalkId(TORAI)
QUEST.addTalkId(RUPINA)
QUEST.addTalkId(WISDOM_CHEST)

QUEST.addKillId(SECRET_KEEPER_TREE)

for mob in DROPLIST.keys():
    QUEST.addKillId(mob)

QUEST.addKillId(SUCCUBUS_OF_SEDUCTION)
QUEST.addKillId(GRIMA)
QUEST.addKillId(SANCHES)
QUEST.addKillId(RAMSEBALIUS)
QUEST.addKillId(BONAPARTERIUS)
QUEST.addKillId(GREAT_DEMON_KING)