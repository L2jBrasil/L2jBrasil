# Legacy of Insolence version 0.1 
# by DrLecter
import sys
from com.it.br import Config
from com.it.br.gameserver.model.quest import State
from com.it.br.gameserver.model.quest import QuestState
from com.it.br.gameserver.model.quest.jython import QuestJython as JQuest

qn = "372_LegacyOfInsolence"

# 1- Variables: Maybe you would like to change something here:
# If a non-zero value is set here, recipes will be 100% instead of 60%
# (default setting matches retail rewards)
ALT_RP_100=0
# Cummulative chances to get: [ "3 recipes", "4000 adena", "2 recipes", max_chance]
# Default is: 1%,2%,2% (read give_reward method downwards if something isn't clear)
# In order to make special rewards harder to get, you could set max_chance to 1000, or slt
REWARD_RATE = [1,3,5,100]
# 2- Quest info: You prolly won't need to change this
QUEST_NUMBER,QUEST_NAME,QUEST_DESCRIPTION = 372, "LegacyOfInsolence", "Legacy of Insolence"
# 3- Quest specific definitions: Don't mess with it unless you know what you're doing
#Quest items: Papyrus
RE_PAP,BL_PAP,BK_PAP,WH_PAP=range(5966,5970)
# Collectibles:
COLLECTION = {
"Rev":range(5972,5979), #Revelations of the Seals
"Anc":range(5979,5984), #Ancient Epics
"Imp":range(5984,5989), #Imperial Genealogy
"ToI":range(5989,6002)  #ToI Blueprints
}
#name:[boots,gloves,helm],
REWARD={
"DarkCryst":[5368,5392,5426],
"Tallum":   [5370,5394,5428],
"Nightmare":[5380,5404,5430],
"Majestic": [5382,5406,5432],
}
#NPCs Area
WALDERAL,DESMOND,CLAUDIA,PATRIN,HOLLY=30844,30855,31001,30929,30839
#Npc: ("Needed Collectibles","Reward recipes")
NPC = {
WALDERAL: ("ToI"),            #Well, this guy is special
DESMOND:  ("Rev","Majestic"),
CLAUDIA:  ("Rev","Nightmare"),
PATRIN:   ("Anc","Tallum"),
HOLLY:    ("Imp","DarkCryst")
}
#Mobs & Drop
CORRUPT_SAGE,ERIN_EDIUNCE,HALLATE_INSP,PLATINUM_OVL,PLATINUM_PRE,MESSENGER_A1,MESSENGER_A2=20817,20821,20825,20829,21069,21062,21063
# This drop distribution should match retail.
MOB = {
CORRUPT_SAGE:[RE_PAP,35],
ERIN_EDIUNCE:[RE_PAP,40],
HALLATE_INSP:[RE_PAP,45],
PLATINUM_OVL:[BL_PAP,40],
PLATINUM_PRE:[BK_PAP,25],
MESSENGER_A1:[WH_PAP,25],
MESSENGER_A2:[WH_PAP,25]
}
#Messages
default = "<html><body>You are either not carrying out your quest or don't meet the criteria.</body></html>"
#Helpers
def check_n_take(st,collection) :
    result = False
    coll2check = COLLECTION[collection]
    dec = 2**len(coll2check)
    for i in range(len(coll2check)) :
      if st.getQuestItemsCount(coll2check[i]) > 0 :
         dec = dec >> 1
    if dec == 1 :
       for k in range(len(coll2check)) :
         st.takeItems(coll2check[k], 1) 
       result = True
    return result

def give_reward(st,reward) :
    luck = st.getRandom(REWARD_RATE[-1])
    prize = []   # dirty iterated copy to avoid reference problems after lists' element deletion
    for h in range(len(REWARD[reward])):
        prize.append(REWARD[reward][h])
    if ALT_RP_100 != 0 :
       for i in range(len(prize)) :
           prize[i]+=1
    if luck < REWARD_RATE[0] :            # best reward: 3 recipes
       for j in range(len(prize)) :
           st.giveItems(prize[j],1)
    elif luck < REWARD_RATE[1] :          # worst reward: 4000a
       st.giveItems(57,4000) 
    elif luck < REWARD_RATE[2] :          # quite nice : 2 recipes
       for k in range(2) :
          l = st.getRandom(len(prize))
          st.giveItems(prize[l],1)
          del prize[l]
    else :                                # ordinary reward: 1 recipe
       st.giveItems(prize[st.getRandom(3)],1)


class Quest (JQuest) :

 def __init__(self,id,name,descr): JQuest.__init__(self,id,name,descr)

 def onEvent (self,event,st) :
    id = st.getState() 
    htmltext = event
    if event == "30844-6.htm":
       st.set("cond","1")
       st.setState(STARTED)
       st.playSound("ItemSound.quest_accept")
    elif event == "30844-7.htm" :
       st.playSound("ItemSound.quest_finish")
       st.exitQuest(1)
    elif event == "30844-9.htm" :
       st.set("cond","2") 
    elif len(event) == 5 and int(event) in NPC.keys() :
       if event == "30844" :
          htmltext = "30844-2.htm"
       else :
          if check_n_take(st,NPC[int(event)][0]) :
             give_reward(st,NPC[int(event)][1])
             htmltext = event+"-2.htm"
          else :
             htmltext = event+"-3.htm"
    elif event in REWARD.keys() :
       if check_n_take(st,"ToI") :
          give_reward(st,event)
          htmltext = "30844-11.htm"
       else :
          htmltext = "30844-12.htm"
    return htmltext

 def onTalk (self,npc,player):
   htmltext = default
   st = player.getQuestState(qn)
   if not st : return htmltext

   npcId = npc.getNpcId()
   id = st.getState()
   if npcId != WALDERAL and id != STARTED : return htmltext

   if id == CREATED :
      st.set("cond","0")
      htmltext = "30844-4.htm"
      if player.getLevel() < 59 :
         st.exitQuest(1)
         htmltext = "30844-5.htm"
   elif id == STARTED :
      htmltext = str(npcId)+"-1.htm"
   return htmltext

 def onKill(self,npc,player,isPet) :
     partyMember = self.getRandomPartyMemberState(player,STARTED)
     if not partyMember : return
     st = partyMember.getQuestState(qn)
     
     npcId = npc.getNpcId()
     item,chance=MOB[npcId]
     chance*=Config.RATE_DROP_QUEST
     chance = int(chance)
     numItems,chance = divmod(chance,100)
     if st.getRandom(100) < chance :
         numItems = numItems + 1
     if numItems :
        st.giveItems(item,numItems)
        st.playSound("ItemSound.quest_itemget")
     return

# Quest class and state definition
QUEST       = Quest(QUEST_NUMBER, str(QUEST_NUMBER)+"_"+QUEST_NAME, QUEST_DESCRIPTION)

CREATED     = State('Start',     QUEST)
STARTED     = State('Started',   QUEST)
COMPLETED   = State('Completed', QUEST)

QUEST.setInitialState(CREATED)
QUEST.addStartNpc(WALDERAL)

for i in NPC.keys() :
  QUEST.addTalkId(i)

for i in MOB.keys() :
  QUEST.addKillId(i)