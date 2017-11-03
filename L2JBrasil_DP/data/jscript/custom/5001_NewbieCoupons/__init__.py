#Newbie Weapon/Accesories Coupons for the Hellbound opening event.
#written by Vice, based in the Miss Queen script.
import sys
from com.it.br.gameserver.model import L2Multisell
from com.it.br.gameserver.model.quest import State
from com.it.br.gameserver.model.quest import QuestState
from com.it.br.gameserver.model.quest.jython import QuestJython as JQuest

qn = "5001_NewbieCoupons"

# NPCs 
NG_HUMAN          = 30598 
NG_ELF            = 30599 
NG_DARKELF        = 30600 
NG_DWARF          = 30601 
NG_ORC            = 30602 
NG_KAMAEL         = 32135 
NG_COMMON_HUMAN_A = 31076 
NG_COMMON_HUMAN_B = 31077 
 
NEWBIE_GUIDES = [NG_HUMAN, NG_ELF, NG_DARKELF, NG_DWARF, 
               NG_ORC, NG_COMMON_HUMAN_A, NG_COMMON_HUMAN_B, NG_KAMAEL] 
 
# QUEST ITEMS
COUPON_ONE = 7832
COUPON_TWO = 7833

# Multisell
WEAPON_MULTISELL     = 305986001
ACCESORIES_MULTISELL = 305986002

#enable/disable coupon give
NEWBIE_COUPONS_ENABLED=1
#Newbie/one time rewards section
#Any quest should rely on a unique bit, but
#it could be shared among quests that were mutually
#exclusive or race restricted.
#Bit #1 isn't used for backwards compatibility.
#This script uses 2 bits, one for newbie coupons and another for travelers
#These 2 bits happen to be the same used by the Miss Queen script
NEWBIE_WEAPON = 16
NEWBIE_ACCESORY = 32

class Quest (JQuest) :

 def __init__(self,id,name,descr): JQuest.__init__(self,id,name,descr)

 def onAdvEvent (self,event,npc,player) :
    if not NEWBIE_COUPONS_ENABLED : return
    st = player.getQuestState(qn)
    if player.isNewbie() :
       newbie = 1
    else :
       newbie = 0
    level = player.getLevel()
    occupation_level = player.getClassId().level()
    pkkills = player.getPkKills()
    if event == "newbie_give_weapon_coupon" :
       #@TODO: check if this is the very first character for this account
       #would need a bit of SQL, or a core method to determine it.
       #This condition should be stored by the core in the account_data table
       #upon character creation.
       if 6 <= level <= 39 and not pkkills and occupation_level == 0 :
          # check the player state against this quest newbie rewarding mark.
          if newbie | NEWBIE_WEAPON != newbie :
             player.setNewbie(newbie|NEWBIE_WEAPON)
             st.giveItems(COUPON_ONE,5)
             return "30598-2.htm" #here's the coupon you requested
          else :
             return "30598-1.htm" #you got a coupon already!
       else :
          return "30598-3.htm" #you're not eligible to get a coupon (level caps, pkkills or already changed class)
    elif event == "newbie_give_armor_coupon" :
       if 6 <= level <= 39 and not pkkills and occupation_level == 1 :
          # check the player state against this quest newbie rewarding mark.
          if newbie | NEWBIE_ACCESORY != newbie :
             player.setNewbie(newbie|NEWBIE_ACCESORY)
             st.giveItems(COUPON_TWO,1)
             return "30598-5.htm" #here's the coupon you requested
          else :
             return "30598-4.htm" #you got a coupon already!
       else :
          return "30598-6.htm" #you're not eligible to get a coupon (level caps, pkkills or didnt change class yet)
    elif event == "newbie_show_weapon" :
       if 6 <= level <= 39 and not pkkills and occupation_level == 0 :
          L2Multisell.getInstance().SeparateAndSend(WEAPON_MULTISELL, player, False, 0.0)
       else :
          return "30598-7.htm" #you're not eligible to use warehouse
    elif event == "newbie_show_armor" :
       if 6 <= level <= 39 and not pkkills and occupation_level > 0 :
          L2Multisell.getInstance().SeparateAndSend(ACCESORIES_MULTISELL, player, False, 0.0)
       else :
          return "30598-8.htm" #you're not eligible to use warehouse

 def onTalk (self,npc,player):
   st = player.getQuestState(qn)
   if not st : st = self.newQuestState(player)

   return "30598.htm"

QUEST = Quest(-1,qn,"custom")
CREATED     = State('Start', QUEST)
STARTED     = State('Started', QUEST)

QUEST.setInitialState(CREATED)

for i in NEWBIE_GUIDES :
    QUEST.addStartNpc(i)
    QUEST.addTalkId(i)