#Jython-based miss queen implementation v0.1
#written by DrLecter, based in Eduu, biti and Newbie contributions.
import sys
from com.it.br.gameserver.model.quest import State
from com.it.br.gameserver.model.quest import QuestState
from com.it.br.gameserver.model.quest.jython import QuestJython as JQuest

qn = "5000_MissQueen"

#QUEST ITENS
COUPON_ONE = 7832
COUPON_TWO = 7833

# NPCs 
MISS_QUEEN_A = 31760 
MISS_QUEEN_B = 31760 
MISS_QUEEN_C = 31760 
MISS_QUEEN_D = 31760 
MISS_QUEEN_E = 31760 
MISS_QUEEN_F = 31760 
MISS_QUEEN_G = 31766 
 
MISS_QUEENs = [MISS_QUEEN_A, MISS_QUEEN_B, 
               MISS_QUEEN_C, MISS_QUEEN_D, 
               MISS_QUEEN_E, MISS_QUEEN_F, 
               MISS_QUEEN_G] 

#enable/disable coupon give
QUEEN_ENABLED=1
#Newbie/one time rewards section
#Any quest should rely on a unique bit, but
#it could be shared among quest that were mutually
#exclusive or race restricted.
#Bit #1 isn't used for backwards compatibility.
#This script uses 2 bits, one for newbie coupons and another for travelers
NEWBIE_REWARD = 16
TRAVELER_REWARD = 32

class Quest (JQuest) :

 def __init__(self,id,name,descr): JQuest.__init__(self,id,name,descr)

 def onAdvEvent (self,event,npc,player) :
    if not QUEEN_ENABLED : return
    st = player.getQuestState(qn)
    newbie = player.getNewbie()
    level = player.getLevel()
    occupation_level = player.getClassId().level()
    pkkills = player.getPkKills()
    if event == "newbie_give_coupon" :
       #@TODO: check if this is the very first character for this account
       #would need a bit of SQL, or a core method to determine it.
       #This condition should be stored by the core in the account_data table
       #upon character creation.
       if 6 <= level <= 25 and not pkkills and occupation_level == 0 :
          # check the player state against this quest newbie rewarding mark.
          if newbie | NEWBIE_REWARD != newbie :
             player.setNewbie(newbie|NEWBIE_REWARD)
             st.giveItems(COUPON_ONE,1)
             return "31760-2.htm" #here's the coupon you requested
          else :
             return "31760-1.htm" #you got a coupon already!
       else :
          return "31760-3.htm" #you're not eligible to get a coupon (level caps, pkkills or already changed class)
    elif event == "traveller_give_coupon" :
       if 6 <= level <= 25 and not pkkills and occupation_level == 1 :
          # check the player state against this quest newbie rewarding mark.
          if newbie | TRAVELER_REWARD != newbie :
             player.setNewbie(newbie|TRAVELER_REWARD)
             st.giveItems(COUPON_TWO,1)
             return "31760-5.htm" #here's the coupon you requested
          else :
             return "31760-4.htm" #you got a coupon already!
       else :
          return "31760-6.htm" #you're not eligible to get a coupon (level caps, pkkills or already changed class)

 def onFirstTalk (self,npc,player):
   st = player.getQuestState(qn)
   if not st :
      st = self.newQuestState(player)
   return "31760.htm"

QUEST = Quest(5000,qn,"custom")

for i in MISS_QUEENs :
    QUEST.addStartNpc(i)
    QUEST.addFirstTalkId(i)
    QUEST.addTalkId(i)
