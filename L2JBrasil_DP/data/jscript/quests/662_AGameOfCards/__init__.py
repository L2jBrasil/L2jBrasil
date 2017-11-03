import sys
from com.it.br.gameserver.model.quest import State
from com.it.br.gameserver.model.quest import QuestState
from com.it.br.gameserver.model.quest.jython import QuestJython as JQuest
from com.it.br.gameserver.cache import HtmCache

qn = "662_AGameOfCards"

KLUMP = 30845
MOBS = [20677,21109,21112,21116,21114,21004,21002,21006,21008,21010,18001,20672,20673,20674,20955,\
        20962,20961,20959,20958,20966,20965,20968,20973,20972,21278,21279,21280,21281,21286,21287,\
        21288,21289,21520,21526,21530,21535,21508,21510,21513,21515]
RED_GEM = 8765
DROP_CHANCE = 60 # Drop chance for Red Gems

# 14 cards; index 0 is for closed card, displayed as '?'
CARD_VALUES = ["?","A","1","2","3","4","5","6","7","8","9","10","J","Q","K"]

# Reward items
ZIGGOS_GEMSTONE = 8868
EWS = 959 # Scroll: Enchant Weapon S
EWA = 729 # Scroll: Enchant Weapon A
EWB = 947 # Scroll: Enchant Weapon B
EWC = 951 # Scroll: Enchant Weapon C
EWD = 955 # Scroll: Enchant Weapon D
EAD = 956 # Scroll: Enchant Armor D

#Rewards format - level : [[item1, amt1],[item2, amt2],...]
REWARDS = {
    1 : [[EAD,2]],
    2 : [[EWC,2]],
    3 : [[EWS,2],[EWC,2]],
    4 : [[ZIGGOS_GEMSTONE,43],[EWS,3],[EWA,1]],
    5 : [[EWC,1]],
    6 : [[EWA,1],[EWB,2],[EWD,1]]
}

REWARDS_TEXT = [
    "Hmmm...? This is... No pair? Tough luck, my friend! Want to try again? Perhaps your luck will take a turn for the better...",
    "Hmmm...? This is... One pair? You got lucky this time, but I wonder if it'll last. Here's your prize.",
    "Hmmm...? This is... Three of a kind? Very good, you are very lucky. Here's your prize.",
    "Hmmm...? This is... Four of a kind! Well done, my young friend! That sort of hand doesn't come up very often, that's for sure. Here's your prize.",
    "Hmmm...? This is... Five of a kind!!!! What luck! The goddess of victory must be with you! Here is your prize! Well earned, well played!",
    "Hmmm...? This is... Two pairs? You got lucky this time, but I wonder if it'll last. Here's your prize.",
    "Hmmm...? This is... A full house? Excellent! you're better than I thought. Here's your prize."
]

class Quest (JQuest) :

 def __init__(self,id,name,descr):
   JQuest.__init__(self,id,name,descr)
   self.questItemIds = []
   self.games = {}

 def onEvent (self,event,st) :
     htmltext = event
     name = st.getPlayer().getName()
     if event == "Klump_AcceptQuest.htm": # quest accepted
         st.setState(STARTED)
         st.playSound("ItemSound.quest_accept")
         st.set("cond","1")
     elif event == "Klump_ExitQuest.htm": # quest finished
         st.playSound("ItemSound.quest_finish")
         st.exitQuest(1)
     elif event == "Klump_QuestInProgress.htm": # does player have 50 gems or not, different progress dialogs
         if st.getQuestItemsCount(RED_GEM) >= 50 :
             htmltext = "Klump_QuestInProgress_Have50Gems.htm" # this dialog allows playing
     elif event == "Klump_PlayBegin.htm":
         if st.getQuestItemsCount(RED_GEM) < 50:    # Not enough gems!!
             return "Klump_NoGems.htm"
         # on play begin remove 50 red gems
         st.takeItems(RED_GEM,50) # take gems ...
         self.games[name] = [0,0,0,0,0]
     elif event == "Klump_PlayField.htm":
         # get vars
         card1,card2,card3,card4,card5 = self.games[name]
         prize = 0
         link1 = link2 = link3 = link4 = link5 = prizestr = ""
         # if all cards are open, game ends and prize is given
         if card1 and card2 and card3 and card4 and card5 : # Game ends
             # make array of card indexes and sort it
             ca = self.games[name]
             ca.sort()
             # now in sorted array all equal elements are near each other, for example [5,5, 3,3, 2] or [5, 4,4, 3,2]
             # this makes much easier conditions checking
             match = []
             for i in range(len(ca)-1) :
                 if ca[i] == ca[i+1] :
                     prize += 1
                     if not ca[i] in match :
                         match.append(ca[i])
             if len(match) == 2 :
                 prize += 3
             # prize = 1 : 1 pair (XX). 4 variants [XX---] [-XX--] [--XX-] [---XX]
             # prize = 2 : 3 cards (XXX). 3 variants [XXX--] [-XXX-] [--XXX]
             # prize = 3 : 4 cards (XXXX). 2 variants [XXXX-] [-XXXX]
             # prize = 4 : 5 cards (XXXXX). 1 variant [XXXXX]
             # prize = 5 : 2 pairs (XXYY). 3 variants [XXYY-] [XX-YY] [-XXYY]
             # prize = 6 : Fullhouse (XXXYY). 2 variants [XXXYY] [YYXXX]
             link1 = "<a action=\"bypass -h Quest 662_AGameOfCards Klump_QuestInProgress.htm\">Play again.</a><br>"
             prizestr = REWARDS_TEXT[prize]
         else : # game still in progress, display links
           link1 = "Put the first card face up.<br>"
           link2 = "Put the second card face up.<br>"
           link3 = "Put the third card face up.<br>"
           link4 = "Put the fourth card face up.<br>"
           link5 = "Put the fifth card face up.<br>"
           if card1 == 0: link1 = "<a action=\"bypass -h Quest 662_AGameOfCards Klump_openCard1.htm\">Put the first card face up.</a><br>"
           if card2 == 0: link2 = "<a action=\"bypass -h Quest 662_AGameOfCards Klump_openCard2.htm\">Put the second card face up.</a><br>"
           if card3 == 0: link3 = "<a action=\"bypass -h Quest 662_AGameOfCards Klump_openCard3.htm\">Put the third card face up.</a><br>"
           if card4 == 0: link4 = "<a action=\"bypass -h Quest 662_AGameOfCards Klump_openCard4.htm\">Put the fourth card face up.</a><br>"
           if card5 == 0: link5 = "<a action=\"bypass -h Quest 662_AGameOfCards Klump_openCard5.htm\">Put the fifth card face up.</a><br>"
         htmltext = HtmCache.getInstance().getHtm("data/scripts/quests/" + qn + "/Klump_PlayField.htm")
         htmltext = htmltext.replace("CARD1",CARD_VALUES[card1]).replace("CARD2",CARD_VALUES[card2]).replace("CARD3",CARD_VALUES[card3]).replace("CARD4",CARD_VALUES[card4]).replace("CARD5",CARD_VALUES[card5])
         htmltext = htmltext.replace("LINK1",link1).replace("LINK2",link2).replace("LINK3",link3).replace("LINK4",link4).replace("LINK5",link5).replace("PRIZE",prizestr)
         if prize :
             for item,amt in REWARDS[prize] :
                 st.giveItems(item,amt)
     elif event.startswith("Klump_openCard") : # 'Open' card
         num = int(event[14])
         self.games[name][num-1] = st.getRandom(14) + 1 # generate index of random card, except index 0, which means 'card is closed'
         htmltext = self.onEvent("Klump_PlayField.htm",st)
     return htmltext

 def onTalk (self,npc,player):
     st = player.getQuestState(qn)
     htmltext = "<html><body>You are either not on a quest that involves this NPC, or you don't meet this NPC's minimum quest requirements.</body></html>"
     if not st : return htmltext
     npcId = npc.getNpcId()
     id = st.getState()
     # first talk to Klump, all quest begins here
     if id == CREATED:
         if player.getLevel() >= 61 : # check player level
             htmltext = "Klump_FirstTalk.htm"
         else:
             htmltext = "<html><body>This quest is for characters level 61 and above.</body></html>"
             st.exitQuest(1)
     # talk to Klump when quest already in progress
     elif id == STARTED :
         htmltext = "Klump_QuestInProgress.htm"
         if st.getQuestItemsCount(RED_GEM) >= 50 :
             htmltext = "Klump_QuestInProgress_Have50Gems.htm"
     return htmltext

 def onKill(self,npc,player,isPet):
     st = player.getQuestState(qn)
     if not st : return
     if st.getState() != STARTED : return
     npcId = npc.getNpcId()
     if npcId in MOBS:
         if st.getRandom(100) < DROP_CHANCE:
             st.giveItems(RED_GEM,1)
             st.playSound("ItemSound.quest_itemget")
     return

QUEST = Quest(662,qn,"A Game Of Cards")
CREATED     = State('Start', QUEST)
STARTED     = State('Started', QUEST)

QUEST.setInitialState(CREATED)

QUEST.addStartNpc(KLUMP)
QUEST.addTalkId(KLUMP)

for mobId in MOBS:
  QUEST.addKillId(mobId)