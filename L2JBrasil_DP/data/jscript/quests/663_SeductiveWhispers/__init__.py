import sys
from com.it.br.gameserver.model.quest import State
from com.it.br.gameserver.model.quest import QuestState
from com.it.br.gameserver.model.quest.jython import QuestJython as JQuest

qn = "663_SeductiveWhispers"

# Npc
WILBERT = 30846

MOBS = [20674, 20678, 20954, 20955, 20956, 20957, 20958, 20959, 20960, 20961, 20962, 20974, 20975, 20976, 20996, 20997, 20998, 20999, 21001, 21002, 21006, 21007, 21008, 21009, 21010]

# Quest Item
SPIRIT_BEAD = 8766

# Drop chance, win chance need check for correct values
DROP_CHANCE = 80
WIN_ROUND_CHANCE = 60

# Reward items
ADENA = 57
EWA = 729 # Scroll: Enchant Weapon A
EAA = 730 # Scroll: Enchant Armor A
EWB = 947 # Scroll: Enchant Weapon B
EAB = 948 # Scroll: Enchant Armor B
EWC = 951 # Scroll: Enchant Weapon C
EWD = 955 # Scroll: Enchant Weapon D

# ====== Rewards -  B grade 60% weapon recipes & keymats =========
# These are just most popular B weapons, need retail check here
# Blunts: Art of Battle Axe, Staff of Evil Spirits (2)
# Bows: Bow of Peril (1)
# Daggers: Demon Dagger, Kris (2)
# Fists: Bellion Cestus (1)
# Polearms: Lance (1)
# Swords: Great Sword, Keshanberk, Sword of Valhalla (3)
# ====== Total: 10; In that order they come in a set below: ======
B_RECIPES = [4963, 4966, 4967, 4968, 5001, 5003, 5004, 5005, 5006, 5007]
B_KEYMATS = [4101, 4107, 4108, 4109, 4115, 4117, 4118, 4119, 4120, 4121]

class Quest (JQuest) :

 def __init__(self,id,name,descr):
   JQuest.__init__(self,id,name,descr)
   self.questItemIds = []

 def onEvent (self,event,st) :
   htmltext = event
   if event == "Wilbert_IWantToPlay.htm": # quest accepted
     st.setState(STARTED)
     st.playSound("ItemSound.quest_accept")
     st.set("cond","1")
     st.set("round","0")
   elif event == "Wilbert_ExitQuest.htm": # quest finished
     st.playSound("ItemSound.quest_finish")
     st.exitQuest(1)
   elif event == "Wilbert_IWantToPractice.htm": # practice start dialog
     beads=st.getQuestItemsCount(SPIRIT_BEAD)
     if beads<1:
       htmltext = "Wilbert_Practice_NotEnoughBeads.htm"
   elif event == "Wilbert_Practice.htm": # "try luck" pressed (Practice)
     beads=st.getQuestItemsCount(SPIRIT_BEAD) # get beads count, it must be > 1
     if beads<1:
       htmltext = "Wilbert_Practice_NotEnoughBeads.htm"
     else:
       st.takeItems(SPIRIT_BEAD,1) # take one bead as payment for luck test
       random=st.getRandom(100)
       if random<WIN_ROUND_CHANCE: # random value is in range [0..WIN_ROUND_CHANCE]
         htmltext = "Wilbert_PracticeWon.htm"
       else: # lose practice :(
         htmltext = "Wilbert_PracticeLost.htm"
   elif event == "Wilbert_LetsPlay.htm": # "Let's play" pressed
     beads=st.getQuestItemsCount(SPIRIT_BEAD)
     if beads<50:
       htmltext = "Wilbert_Practice_NotEnoughBeads.htm"
     else:
       htmltext = "Wilbert_PlayRound1.htm"
       st.set("round","0")
   elif event == "Wilbert_PullCard.htm": # "Pull first or next card" pressed
     round=st.getInt("round")
     beads=st.getQuestItemsCount(SPIRIT_BEAD)
     if beads<50 and round == 0: # check for 50 beads when game just starts only (round=0)
       htmltext = "Wilbert_Practice_NotEnoughBeads.htm"
     else:
       if round == 0: # take 50 beads when game just starts only (round=0)
         st.takeItems(SPIRIT_BEAD,50)
       random=st.getRandom(100)
       if random>WIN_ROUND_CHANCE: # random value is in range [WIN_ROUND_CHANCE..100]
         htmltext = "Wilbert_PlayLose.htm"
         st.set("round","0") # restart game
       else: # next round won
         round = round + 1
         htmltext = st.showHtmlFile("Wilbert_PlayWin.htm").replace("NROUND", str(round))
         if round == 1:
           htmltext = htmltext.replace("MYPRIZE","40,000 adena")
         if round == 2:
           htmltext = htmltext.replace("MYPRIZE","80,000 adena")
         if round == 3:
           htmltext = htmltext.replace("MYPRIZE","110,000 adena, D-grade Enchant Weapon Scroll")
         if round == 4:
           htmltext = htmltext.replace("MYPRIZE","199,000 adena, C-grade Enchant Weapon Scroll")
         if round == 5:
           htmltext = htmltext.replace("MYPRIZE","388,000 adena, 1 recipe for a B-grade weapon")
         if round == 6:
           htmltext = htmltext.replace("MYPRIZE","675,000 adena, 1 essential ingredient for a B-grade weapon")
         if round == 7:
           htmltext = htmltext.replace("MYPRIZE","1,284,000 adena, 2 B-grade Enchant Weapon Scrolls, 2 B-grade Enchat Armor Scrolls")
         if round == 8: # reached round 8; give prizes and restart game
           round = 0
           st.giveItems(ADENA,2384000)
           st.giveItems(EWA,1) # Scroll: Enchant Weapon A
           st.giveItems(EAA,2) # Scroll: Enchant Armor A
           htmltext = "Wilbert_PlayWonRound8.htm"
         st.set("round",str(round))
   elif event == "Wilbert_TakePrize.htm": # player won round and wants to stop game and take prize
     round=st.getInt("round")
     if round == 0: # player did not win any round but wants to take prize? O_o
       htmltext = "<html><body>You did not win any round! No prizes.</body></html>"
       return htmltext
     if round > 8: # some bug or hack?
       st.set("round","0")
       htmltext = "<html><body>Round cannot be > 8 !!!</body></html>"
       return htmltext
     st.set("round","0") # first set round to 0 - game ended.
     htmltext = "Wilbert_PrizeTaken.htm"
     # give prize depending on current round won
     if round == 1:
       st.giveItems(ADENA,40000)
     elif round == 2:
       st.giveItems(ADENA,80000)
     elif round == 3:
       st.giveItems(ADENA,110000)
       st.giveItems(EWD,1) # Scroll: Enchant Weapon D
     elif round == 4:
       st.giveItems(ADENA,199000)
       st.giveItems(EWC,1) # Scroll: Enchant Weapon C
     elif round == 5:
       st.giveItems(ADENA,388000)
       # 60% B-weap. rec number is random
       st.giveItems(B_RECIPES[st.getRandom(len(B_RECIPES))], 1)
     elif round == 6:
       st.giveItems(ADENA,675000)
       # B-weap. key number is random
       st.giveItems(B_KEYMATS[st.getRandom(len(B_KEYMATS))], 1)
     elif round == 7:
       st.giveItems(ADENA,1284000)
       st.giveItems(EWB,2) # Scroll: Enchant Weapon B
       st.giveItems(EAB,2) # Scroll: Enchant Armor B
     # for round 8 prize is automatically when player wins 8 round
   return htmltext

 def onTalk (self,npc,player):
   st = player.getQuestState(qn)
   htmltext = "<html><body>You are either not on a quest that involves this NPC, or you don't meet this NPC's minimum quest requirements.</body></html>"
   if not st : return htmltext
   npcId = npc.getNpcId()
   id = st.getState()
   # first talk to Wilbert
   if npcId == WILBERT and id == CREATED:
       if player.getLevel() >= 50 : # check player level
           htmltext = "Wilbert_start.htm"
       else:
           htmltext = "<html><body>This quest is for characters above level 50 only.</body></html>"
           st.exitQuest(1)
   # talk to Wilbert when quest already in progress
   elif npcId == WILBERT and id == STARTED :
       htmltext = "Wilbert_QuestInProgress.htm"
   return htmltext
 
 def onKill(self,npc,player,isPet):
   st = player.getQuestState(qn)
   if not st : return
   if st.getState() != STARTED : return
   npcId = npc.getNpcId()
   if npcId in MOBS:
     if st.getRandom(100) < DROP_CHANCE:
       st.giveItems(SPIRIT_BEAD,1)
       st.playSound("ItemSound.quest_itemget")
   return

QUEST = Quest(663,qn,"Seductive Whispers")
CREATED     = State('Start', QUEST)
STARTED     = State('Started', QUEST)

QUEST.setInitialState(CREATED)

QUEST.addStartNpc(WILBERT)
QUEST.addTalkId(WILBERT)

for mobId in MOBS:
  QUEST.addKillId(mobId)