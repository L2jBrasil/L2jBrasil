# version 0.2
# by DrLecter, with fixes from Ryo_Saeba

import sys
from com.it.br import Config
from com.it.br.gameserver.model.quest import State
from com.it.br.gameserver.model.quest import QuestState
from com.it.br.gameserver.model.quest.jython import QuestJython as JQuest

qn = "419_GetAPet"

# constants section

REQUIRED_SPIDER_LEGS = 50
#Quest items
ANIMAL_LOVERS_LIST1,ANIMAL_SLAYER_LIST1,ANIMAL_SLAYER_LIST2,ANIMAL_SLAYER_LIST3,\
ANIMAL_SLAYER_LIST4,ANIMAL_SLAYER_LIST5,SPIDER_LEG1,SPIDER_LEG2,SPIDER_LEG3,    \
SPIDER_LEG4,SPIDER_LEG5 = range(3417,3428)
#Chance of drop in %
SPIDER_LEG_DROP = 100
#mobs
#1 humans
SPIDER_H1 = 20103 # Giant Spider
SPIDER_H2 = 20106 # Talon Spider
SPIDER_H3 = 20108 # Blade Spider
#2 elves
SPIDER_LE1 = 20460 # Crimson Spider
SPIDER_LE2 = 20308 # Hook Spider
SPIDER_LE3 = 20466 # Pincer Spider
#3 dark elves
SPIDER_DE1 = 20025 # Lesser Dark Horror
SPIDER_DE2 = 20105 # Dark Horror 
SPIDER_DE3 = 20034 # Prowler
#4 orcs
SPIDER_O1 = 20474 # Kasha Spider
SPIDER_O2 = 20476 # Kasha Fang Spider
SPIDER_O3 = 20478 # Kasha Blade Spider
#5 dwarves
SPIDER_D1 = 20403 # Hunter Tarantula
SPIDER_D2 = 20508 # Plunder Tarantula

#NPCs
PET_MANAGER_MARTIN = 30731
GK_BELLA = 30256
MC_ELLIE = 30091
GD_METTY = 30072

#Rewards
WOLF_COLLAR = 2375

# helper functions section
def getCount_proof(st) :
  race = st.getPlayer().getRace().ordinal()
  if race == 0: proofs = st.getQuestItemsCount(SPIDER_LEG1)
  if race == 1: proofs = st.getQuestItemsCount(SPIDER_LEG2)
  if race == 2: proofs = st.getQuestItemsCount(SPIDER_LEG3)
  if race == 3: proofs = st.getQuestItemsCount(SPIDER_LEG4)
  if race == 4: proofs = st.getQuestItemsCount(SPIDER_LEG5)
  return proofs

def check_questions(st) :
  question = 1  
  quiz = st.get("quiz")
  answers = st.getInt("answers")
  if answers < 10 :
    questions = quiz.split()
    index = st.getRandom(len(questions) - 1)
    question = questions[index]
    if len(questions) > 10 - answers :
      questions[index] = questions[-1]
      del questions[-1]
    st.set("quiz"," ".join(questions))
    htmltext = "419_q"+str(question)+".htm"
    return htmltext
  elif answers == 10 :
    st.giveItems(WOLF_COLLAR,1)
    st.exitQuest(1)
    st.playSound("ItemSound.quest_finish")
    htmltext="Completed.htm"
  return htmltext
  
# Main Quest Code
class Quest (JQuest):

  def __init__(self,id,name,descr): JQuest.__init__(self,id,name,descr)

  def onEvent (self,event,st):
    id = st.getState()
    if id == CREATED :
      st.set("cond","0")
      if event == "details" :
        return "419_confirm.htm"
      elif event == "agree" :
        st.setState(STARTED)
        st.set("progress","STARTED") 
        st.set("cond","1")
        race = st.getPlayer().getRace().ordinal()
        if race == 0:
           st.giveItems(ANIMAL_SLAYER_LIST1,1)
           htmltext = "419_slay_0.htm"
        if race == 1:
           st.giveItems(ANIMAL_SLAYER_LIST2,1)
           htmltext = "419_slay_1.htm"
        if race == 2:
           st.giveItems(ANIMAL_SLAYER_LIST3,1)
           htmltext = "419_slay_2.htm"
        if race == 3:
           st.giveItems(ANIMAL_SLAYER_LIST4,1)
           htmltext = "419_slay_3.htm"
        if race == 4:
           st.giveItems(ANIMAL_SLAYER_LIST5,1)
           htmltext = "419_slay_4.htm"
        st.playSound("ItemSound.quest_accept")
        return htmltext
      elif event == "disagree" :
        st.exitQuest(1)
        return "419_cancelled.htm"
    elif id == SLAYED :
      if event == "talk"  :
        st.giveItems(ANIMAL_LOVERS_LIST1,1)
        return "419_talk.htm"
      if event == "talk1" :
        return "419_bella_2.htm"
      if event == "talk2" :
        st.set("progress", str(st.getInt("progress") | 1))
        return "419_bella_3.htm"
      if event == "talk3" :
        st.set("progress", str(st.getInt("progress") | 2))
        return "419_ellie_2.htm"
      if event == "talk4" :
        st.set("progress", str(st.getInt("progress") | 4))
        return "419_metty_2.htm"
    elif id == TALKED :
      if event == "tryme" :
        return check_questions(st) 
      elif event == "wrong" :
        st.setState(SLAYED)
        st.set("progress","0")
        st.unset("quiz")
        st.unset("answers")
        st.giveItems(ANIMAL_LOVERS_LIST1,1)
        return "419_failed.htm"
      elif event == "right" :
        st.set("answers",str(st.getInt("answers") + 1))
        return check_questions(st)
    return

  def onTalk (self,npc,player):
    htmltext = "<html><body>You are either not carrying out your quest or don't meet the criteria.</body></html>"
    st = player.getQuestState(qn)
    if not st : return htmltext

    npcId = npc.getNpcId()
    id = st.getState()
    if npcId != PET_MANAGER_MARTIN and id != SLAYED : return htmltext

    if id == COMPLETED: st.setState(CREATED)
    if npcId == PET_MANAGER_MARTIN :
      if id == CREATED  :
         if player.getLevel() < 15 :
            st.exitQuest(1)
            return "419_low_level.htm"
         return "Start.htm"
      if id == STARTED  :
         if getCount_proof(st) == 0 :
            return "419_no_slay.htm"  
         elif getCount_proof(st) < REQUIRED_SPIDER_LEGS :
            return "419_pending_slay.htm"
         else :
            st.setState(SLAYED)
            st.set("progress","0")
            race = player.getRace().ordinal()
            if race == 0:
                st.takeItems(SPIDER_LEG1,REQUIRED_SPIDER_LEGS)
                st.takeItems(ANIMAL_SLAYER_LIST1,1)
            elif race == 1:
                st.takeItems(SPIDER_LEG2,REQUIRED_SPIDER_LEGS)
                st.takeItems(ANIMAL_SLAYER_LIST2,1)
            elif race == 2:
                st.takeItems(SPIDER_LEG3,REQUIRED_SPIDER_LEGS)
                st.takeItems(ANIMAL_SLAYER_LIST3,1)
            elif race == 3:
                st.takeItems(SPIDER_LEG4,REQUIRED_SPIDER_LEGS)
                st.takeItems(ANIMAL_SLAYER_LIST4,1)
            else:
                st.takeItems(SPIDER_LEG5,REQUIRED_SPIDER_LEGS)
                st.takeItems(ANIMAL_SLAYER_LIST5,1)
            return "Slayed.htm"
      if id == SLAYED :
        if st.getInt("progress") == 7 :
           st.takeItems(ANIMAL_LOVERS_LIST1,1)
           st.setState(TALKED)
           st.set("quiz","1 2 3 4 5 6 7 8 9 10 11 12 13 14")
           st.set("answers","0")
           return "Talked.htm"
        return "419_pending_talk.htm"
    elif id == SLAYED:
      if npcId == GK_BELLA :
         return "419_bella_1.htm"
      elif npcId == MC_ELLIE :
         return "419_ellie_1.htm"
      elif npcId == GD_METTY :
         return "419_metty_1.htm"
    return

  def onKill(self,npc,player,isPet):
      st = player.getQuestState(qn)
      if not st : return 
      if st.getState() != STARTED : return 
   
      npcId = npc.getNpcId()
      collected = getCount_proof(st)
      if collected < REQUIRED_SPIDER_LEGS:
         race = player.getRace().ordinal()
         if race == 0 :
            npcs = [ SPIDER_H1, SPIDER_H2, SPIDER_H3 ]
            item = SPIDER_LEG1
         if race == 1 :
            npcs = [ SPIDER_LE1, SPIDER_LE2, SPIDER_LE3 ]
            item = SPIDER_LEG2
         if race == 2 :
            npcs = [ SPIDER_DE1, SPIDER_DE2, SPIDER_DE3 ]
            item = SPIDER_LEG3
         if race == 3 :
            npcs = [ SPIDER_O1, SPIDER_O2, SPIDER_O3 ]
            item = SPIDER_LEG4
         if race == 4 :
            npcs = [ SPIDER_D1, SPIDER_D2 ]
            item = SPIDER_LEG5
         if npcId in npcs :
            chance = SPIDER_LEG_DROP * Config.RATE_DROP_QUEST
            numItems, chance = divmod(chance,100)
            count = st.getQuestItemsCount(item)
            if st.getRandom(100) < chance :
               numItems += 1
            if numItems :
               if count + numItems >= REQUIRED_SPIDER_LEGS :
                  numItems = REQUIRED_SPIDER_LEGS - count
                  st.playSound("ItemSound.quest_middle")
               else:
                  st.playSound("ItemSound.quest_itemget")
               st.giveItems(item,int(numItems))
      return

# Quest class and state definition
QUEST       = Quest(419, qn, "Wolf Collar")
CREATED     = State('Start',       QUEST)
STARTED     = State('Started',     QUEST)
SLAYED      = State('Slayed',      QUEST)
TALKED      = State('Talked',      QUEST)
COMPLETED   = State('Completed',   QUEST)

# Quest initialization
QUEST.setInitialState(CREATED)
# Quest NPC starter initialization
QUEST.addStartNpc(PET_MANAGER_MARTIN)

# Quest mob initialization
for mob in [SPIDER_H1,SPIDER_H2,SPIDER_H3,SPIDER_LE1,SPIDER_LE2,SPIDER_LE3,SPIDER_DE1,SPIDER_DE2,SPIDER_DE3,SPIDER_O1,SPIDER_O2,SPIDER_O3,SPIDER_D1,SPIDER_D2]:
    QUEST.addKillId(mob)

# Quest NPC initialization
QUEST.addTalkId(PET_MANAGER_MARTIN)

QUEST.addTalkId(GK_BELLA)
QUEST.addTalkId(MC_ELLIE)
QUEST.addTalkId(GD_METTY)