# Made by Mr. Have fun! Version 0.2
# Version 0.3 by H1GHL4ND3R
import sys
from com.it.br.gameserver.model.quest import State
from com.it.br.gameserver.model.quest import QuestState
from com.it.br.gameserver.model.quest.jython import QuestJython as JQuest

qn = "220_TestimonyOfGlory"

# if the cross compatible is turned on,
# the code will try convert the info of database from old code
# to a compatible with the new one
# Warning: untested
# The cross compatible mode can be deleted from the code any time
# without any problem, or simply turn off
CROSS_COMPATIBLE = True

MARK_OF_GLORY,          VOKIYANS_ORDER1,    MANASHEN_SHARD,        TYRANT_TALON,       \
GUARDIAN_BASILISK_FANG, VOKIYANS_ORDER2,    NECKLACE_OF_AUTHORITY, CHIANTAS_ORDER1,    \
SCEPTER_OF_BREKA,       SCEPTER_OF_ENKU,    SCEPTER_OF_VUKU,       SCEPTER_OF_TUREK,   \
SCEPTER_OF_TUNATH,      CHIANTAS_ORDER2,    CHIANTAS_ORDER3,       TAMLIN_ORC_SKULL,   \
TIMAK_ORC_HEAD,         SCEPTER_BOX,        PASHIKAS_HEAD,         VULTUS_HEAD,        \
GLOVE_OF_VOLTAR,        ENKU_OVERLORD_HEAD, GLOVE_OF_KEPRA,        MAKUM_BUGBEAR_HEAD, \
GLOVE_OF_BURAI,         MANAKIAS_LETTER1,   MANAKIAS_LETTER2,      KASMANS_LETTER1,    \
KASMANS_LETTER2,        KASMANS_LETTER3,    DRIKOS_CONTRACT,       STAKATO_DRONE_HUSK1,\
TANAPIS_ORDER1,         SCEPTER_OF_TANTOS,  RITUAL_BOX = range(3203,3238)


# ID :[ REQUIRED, ITEM, MAX_QUANTY]
DROPLIST_COND_1={
20192  :[VOKIYANS_ORDER1, TYRANT_TALON,           10],  # Tyrant
20193  :[VOKIYANS_ORDER1, TYRANT_TALON,           10],  # Tyrant Kingpin
20550  :[VOKIYANS_ORDER1, GUARDIAN_BASILISK_FANG, 10],  # Guardian Basilisk
20563  :[VOKIYANS_ORDER1, MANASHEN_SHARD,         10]   # Manashen Gargoyle
}

# ID :[ REQUIRED, ITEM, MAX_QUANTY]
DROPLIST_COND_4={
20234 :[DRIKOS_CONTRACT, STAKATO_DRONE_HUSK1, 30],     # Stakato Drone
27080 :[GLOVE_OF_VOLTAR, PASHIKAS_HEAD,        1],     # Pashikas Son Of Voltar Quest Monster
27081 :[GLOVE_OF_VOLTAR, VULTUS_HEAD,          1],     # Vultus Son Of Voltar Quest Monster
27082 :[GLOVE_OF_KEPRA,  ENKU_OVERLORD_HEAD,   4],     # Enku Orc Overlord Quest Monster
27083 :[GLOVE_OF_BURAI,  MAKUM_BUGBEAR_HEAD,   2]      # Makum Bugbear Thug Quest Monster
}

# ID : [ REQUIRED, ITEM, MAX_QUANTY, CHANCE]
DROPLIST_COND_6={
20583  :[CHIANTAS_ORDER3, TIMAK_ORC_HEAD,   20,  50],   # Timak Orc
20584  :[CHIANTAS_ORDER3, TIMAK_ORC_HEAD,   20,  60],   # Timak Orc Archer
20585  :[CHIANTAS_ORDER3, TIMAK_ORC_HEAD,   20,  70],   # Timak Orc Soldier
20586  :[CHIANTAS_ORDER3, TIMAK_ORC_HEAD,   20,  80],   # Timak Orc Warrior
20587  :[CHIANTAS_ORDER3, TIMAK_ORC_HEAD,   20,  90],   # Timak Orc Shaman
20588  :[CHIANTAS_ORDER3, TIMAK_ORC_HEAD,   20, 100],   # Timak Orc Overlord
20601  :[CHIANTAS_ORDER3, TAMLIN_ORC_SKULL, 20,  50],   # Tamlin Orc
20602  :[CHIANTAS_ORDER3, TAMLIN_ORC_SKULL, 20,  60]    # Tamlin Orc Archer
}

# For condition 9
#   ID, NAME :
#  20778, Ragna Orc Overlord
#  20779, Ragna Orc Seer
# 27086, Revenant of Tantos Chief Quest Monster

class Quest (JQuest) :

 def __init__(self,id,name,descr): JQuest.__init__(self,id,name,descr)

 def onEvent (self,event,st) :
    htmltext = event
    if event == "RETURN" :
          return
    elif event == "30514-05.htm" :                           # Starting
          st.set("cond","1")
          st.setState(STARTED)
          st.playSound("ItemSound.quest_accept")
          st.giveItems(VOKIYANS_ORDER1,1)
    elif event == "30642-03.htm" :                           # Condition 3
          st.giveItems(CHIANTAS_ORDER1,1)
          st.set("cond","4")
          st.takeItems(VOKIYANS_ORDER2,1)
    elif event == "30571-03.htm" :                           # Condition 8
          st.giveItems(TANAPIS_ORDER1,1)
          st.set("cond","9")
          st.takeItems(SCEPTER_BOX,1)
    # Everything bellow here is for condition 4 and 5
    elif event == "30642-07.htm" :
          st.takeItems(SCEPTER_OF_BREKA,1)
          st.takeItems(SCEPTER_OF_ENKU,1)
          st.takeItems(SCEPTER_OF_VUKU,1)
          st.takeItems(SCEPTER_OF_TUREK,1)
          st.takeItems(SCEPTER_OF_TUNATH,1)
          st.takeItems(CHIANTAS_ORDER1,1)
          st.takeItems(MANAKIAS_LETTER1,1)
          st.takeItems(MANAKIAS_LETTER2,1)
          st.takeItems(KASMANS_LETTER1,1)
          if st.getPlayer().getLevel() >= 38 :
            st.giveItems(CHIANTAS_ORDER3,1)
            st.set("cond","6")
          else :                                            # lack experience
            htmltext = "30642-06.htm"
            st.giveItems(CHIANTAS_ORDER2,1)
    elif event == "BREKA" :
          if st.getQuestItemsCount(SCEPTER_OF_BREKA) :
            htmltext = "30515-02.htm"
          else :
            st.addRadar(80100, 119991, -2289)
            if st.getQuestItemsCount(MANAKIAS_LETTER1) :
              htmltext = "30515-04.htm"
            else :
              htmltext = "30515-03.htm"
              st.giveItems(MANAKIAS_LETTER1,1)
    elif event == "ENKU" :
          if st.getQuestItemsCount(SCEPTER_OF_ENKU) :
            htmltext = "30515-05.htm"
          else :
            st.addRadar(17744, 189834, -3506)
            if st.getQuestItemsCount(MANAKIAS_LETTER2) :
              htmltext = "30515-07.htm"
            else :
              htmltext = "30515-06.htm"
              st.giveItems(MANAKIAS_LETTER2,1)
    elif event == "VUKU" :
          if st.getQuestItemsCount(SCEPTER_OF_VUKU) :
            htmltext = "30501-02.htm"
          else :
            st.addRadar(-2150, 124443, -3649)
            if st.getQuestItemsCount(KASMANS_LETTER1) :
              htmltext = "30501-04.htm"
            else :
              htmltext = "30501-03.htm"
              st.giveItems(KASMANS_LETTER1,1)
    elif event == "TUREK" :
          if st.getQuestItemsCount(SCEPTER_OF_TUREK) :
            htmltext = "30501-05.htm"
          else :
            st.addRadar(-94294, 110818, -3488)
            if st.getQuestItemsCount(KASMANS_LETTER2) :
              htmltext = "30501-07.htm"
            else :
              htmltext = "30501-06.htm"
              st.giveItems(KASMANS_LETTER2,1)
    elif event == "TUNATH" :
          if st.getQuestItemsCount(SCEPTER_OF_TUNATH) :
            htmltext = "30501-08.htm"
          else :
            st.addRadar(-55217, 200628, -3649)
            if st.getQuestItemsCount(KASMANS_LETTER3) :
              htmltext = "30501-10.htm"
            else :
              htmltext = "30501-09.htm"
              st.giveItems(KASMANS_LETTER3,1)
    elif event == "30615-04.htm" :                           # Chief of Breka Orcs
          st.playSound("Itemsound.quest_before_battle")
          st.addSpawn(27080,80117,120039,-2259)
          st.addSpawn(27081,80058,120038,-2259)
          st.giveItems(GLOVE_OF_VOLTAR,1)
          st.takeItems(MANAKIAS_LETTER1,1)
    elif event == "30616-04.htm" :                           # Chief of Enku Orcs
          st.playSound("Itemsound.quest_before_battle")
          st.addSpawn(27082,19456,192245,-3730)
          st.addSpawn(27082,19539,192343,-3728)
          st.addSpawn(27082,19500,192449,-3729)
          st.addSpawn(27082,19569,192482,-3728) 
          st.giveItems(GLOVE_OF_KEPRA,1)
          st.takeItems(MANAKIAS_LETTER2,1)
    elif event == "30617-04.htm" :                           # Chief of Turek Orcs
          st.playSound("Itemsound.quest_before_battle")
          st.addSpawn(27083,-94292,110781,-3701)
          st.addSpawn(27083,-94293,110861,-3701)
          st.giveItems(GLOVE_OF_BURAI,1)
          st.takeItems(KASMANS_LETTER2,1)
    elif event == "30618-03.htm" :                           # Chief of Tunath Orcs
          st.giveItems(SCEPTER_OF_TUNATH,1)
          st.takeItems(KASMANS_LETTER3,1)
          if st.getQuestItemsCount(SCEPTER_OF_BREKA) and st.getQuestItemsCount(SCEPTER_OF_ENKU) and st.getQuestItemsCount(SCEPTER_OF_VUKU) and st.getQuestItemsCount(SCEPTER_OF_TUREK) and st.getQuestItemsCount(SCEPTER_OF_TUNATH) :
            st.set("cond","5")
    elif event == "30619-03.htm" :                           # Chief of Vuku Orcs
          st.giveItems(DRIKOS_CONTRACT,1)
          st.takeItems(KASMANS_LETTER1,1)
    return htmltext


 def onTalk (self,npc,player):
   htmltext = "<html><body>You are either not carrying out your quest or don't meet the criteria.</body></html>"
   st = player.getQuestState(qn)
   if not st : return htmltext

   npcId = npc.getNpcId()
   id = st.getState()
   if npcId != 30514 and id != STARTED : return htmltext

   if id == CREATED :                                       # Check if is starting the quest
     st.set("cond","0")
     st.set("id","0")
     if npcId == 30514 :
       if player.getRace().ordinal() == 3 and player.getClassId().getId() in [ 45, 47, 50 ] :
         if player.getLevel() >= 37 :
           htmltext = "30514-03.htm"
         else:
           htmltext = "30514-01.htm"
           st.exitQuest(1)
       else:
         htmltext = "30514-02.htm"
         st.exitQuest(1)
   elif id == COMPLETED :                                   # Check if the quest is already made
      if npcId == 30514 :
        htmltext = "<html><body>This quest has already been completed.</body></html>"
   else :                                                   # The quest it self
     try :
       cond = st.getInt("cond")
     except :
       cond = None
     # Default text after condition 4 for the bellow NPCs
     if npcId == 30501 and cond >= 5 :
       htmltext = "30501-11.htm"
     elif npcId == 30515 and cond >= 5 :
       htmltext = "30515-08.htm"
     elif npcId == 30615 and cond >= 5 :
       htmltext = "30615-08.htm"
     elif npcId == 30616 and cond >= 5 :
       htmltext = "30616-08.htm"
     elif npcId == 30617 and cond >= 5 :
       htmltext = "30617-07.htm"
     elif npcId == 30618 and cond >= 5 :
       htmltext = "30618-05.htm"
     elif npcId == 30619 and cond >= 5 :
       htmltext = "30619-07.htm"
     elif npcId == 30565 and cond in [ 8, 9, 10 ] :          # Paagrio Lord Kakai
       htmltext = "30565-01.htm"
     # Condition Oriented code for optimal performance
     elif cond == 1 :                                       # Trial of Vokian
         if npcId == 30514 and st.getQuestItemsCount(VOKIYANS_ORDER1) and (st.getQuestItemsCount(MANASHEN_SHARD) < 10 or st.getQuestItemsCount(TYRANT_TALON) < 10 or st.getQuestItemsCount(GUARDIAN_BASILISK_FANG) < 10) :
           htmltext = "30514-06.htm"
         # Begin of the cross compatible code
         elif CROSS_COMPATIBLE :
           if st.getQuestItemsCount(VOKIYANS_ORDER1) and st.getQuestItemsCount(MANASHEN_SHARD) == 10 and st.getQuestItemsCount(TYRANT_TALON) == 10 and st.getQuestItemsCount(GUARDIAN_BASILISK_FANG) == 10 :
             st.set("cond","2")
           elif st.getQuestItemsCount(NECKLACE_OF_AUTHORITY) and st.getQuestItemsCount(VOKIYANS_ORDER2) :
             st.set("cond","3")
           elif st.getQuestItemsCount(NECKLACE_OF_AUTHORITY) and st.getQuestItemsCount(CHIANTAS_ORDER1) :
             st.set("cond","4")
           elif st.getQuestItemsCount(NECKLACE_OF_AUTHORITY) and (st.getQuestItemsCount(CHIANTAS_ORDER2) or (st.getQuestItemsCount(CHIANTAS_ORDER1) and st.getQuestItemsCount(SCEPTER_OF_BREKA) and st.getQuestItemsCount(SCEPTER_OF_VUKU) and st.getQuestItemsCount(SCEPTER_OF_TUREK) and st.getQuestItemsCount(SCEPTER_OF_TUNATH) and st.getQuestItemsCount(SCEPTER_OF_ENKU))) :
             st.set("cond","5")
           elif st.getQuestItemsCount(NECKLACE_OF_AUTHORITY) and st.getQuestItemsCount(CHIANTAS_ORDER3) :
             if st.getQuestItemsCount(TAMLIN_ORC_SKULL) == 20 and st.getQuestItemsCount(TIMAK_ORC_HEAD) == 20 :
               st.set("cond","7")
             else :
               st.set("cond","6")
           elif st.getQuestItemsCount(SCEPTER_BOX) :
             st.set("cond","8")
           elif st.getQuestItemsCount(TANAPIS_ORDER1) :
             if st.getQuestItemsCount(SCEPTER_OF_TANTOS) :
               st.set("cond","10")
             else :
               st.set("cond","9")
           elif st.getQuestItemsCount(RITUAL_BOX) :
             st.set("cond","11")
         # end of the cross compatible code
     elif cond == 2 :                                       # Return to Vokian
         if npcId == 30514 :
           if st.getQuestItemsCount(VOKIYANS_ORDER1) and st.getQuestItemsCount(MANASHEN_SHARD) == 10 and st.getQuestItemsCount(TYRANT_TALON) == 10 and st.getQuestItemsCount(GUARDIAN_BASILISK_FANG) == 10 :
             htmltext = "30514-08.htm"
             st.giveItems(VOKIYANS_ORDER2,1)
             st.giveItems(NECKLACE_OF_AUTHORITY,1)
             st.set("cond","3")
             st.takeItems(VOKIYANS_ORDER1,1)
             st.takeItems(MANASHEN_SHARD,10)
             st.takeItems(TYRANT_TALON,10)
             st.takeItems(GUARDIAN_BASILISK_FANG,10)
     elif cond == 3 :                                       # Chianta, Chief of the Gandi Tribe
         if npcId == 30514 :
           htmltext = "30514-09.htm"
         elif npcId == 30642 and st.getQuestItemsCount(NECKLACE_OF_AUTHORITY) and st.getQuestItemsCount(VOKIYANS_ORDER2) :
           htmltext = "30642-01.htm"
     elif cond == 4 :                                       # Five Unique and Honorable Deaths
         if npcId == 30514 and st.getQuestItemsCount(VOKIYANS_ORDER2) :
           htmltext = "30514-10.htm"
         elif npcId == 30642 :
           htmltext = "30642-04.htm"
         elif npcId == 30515 and st.getQuestItemsCount(NECKLACE_OF_AUTHORITY) and st.getQuestItemsCount(CHIANTAS_ORDER1) :
           htmltext = "30515-01.htm"
         elif npcId == 30501 and st.getQuestItemsCount(NECKLACE_OF_AUTHORITY) and st.getQuestItemsCount(CHIANTAS_ORDER1) :
           htmltext = "30501-01.htm"
         elif npcId == 30615 :                               # Chief of Breka Orcs
           if st.getQuestItemsCount(MANAKIAS_LETTER1) :
               htmltext = "30615-02.htm"
           elif st.getQuestItemsCount(GLOVE_OF_VOLTAR) :
               htmltext = "30615-05.htm"
               st.playSound("Itemsound.quest_before_battle")
               st.addSpawn(27080,80117,120039,-2259)
               st.addSpawn(27081,80058,120038,-2259)
           elif st.getQuestItemsCount(PASHIKAS_HEAD) and st.getQuestItemsCount(VULTUS_HEAD) :
               htmltext = "30615-06.htm"
               st.giveItems(SCEPTER_OF_BREKA,1)
               st.takeItems(PASHIKAS_HEAD,1)
               st.takeItems(VULTUS_HEAD,1)
               if st.getQuestItemsCount(SCEPTER_OF_BREKA) and st.getQuestItemsCount(SCEPTER_OF_ENKU) and st.getQuestItemsCount(SCEPTER_OF_VUKU) and st.getQuestItemsCount(SCEPTER_OF_TUREK) and st.getQuestItemsCount(SCEPTER_OF_TUNATH) :
                 st.set("cond","5")
                 st.playSound("Itemsound.quest_middle")
               else :
                 st.playSound("Itemsound.quest_itemget")
           elif st.getQuestItemsCount(SCEPTER_OF_BREKA) :
               htmltext = "30615-07.htm"
           else :
               htmltext = "30615-01.htm"
         elif npcId == 30616 :                               # Chief of Enku Orcs
           if st.getQuestItemsCount(MANAKIAS_LETTER2) :
               htmltext = "30616-02.htm"
           elif st.getQuestItemsCount(GLOVE_OF_KEPRA) :
               htmltext = "30616-05.htm"
               st.playSound("Itemsound.quest_before_battle")
               st.addSpawn(27082,17710,189813,-3581)
               st.addSpawn(27082,17674,189798,-3581)
               st.addSpawn(27082,17770,189852,-3581)
               st.addSpawn(27082,17803,189873,-3581)
           elif st.getQuestItemsCount(ENKU_OVERLORD_HEAD) == 4 :
               htmltext = "30616-06.htm"
               st.giveItems(SCEPTER_OF_ENKU,1)
               st.takeItems(ENKU_OVERLORD_HEAD,4)
               if st.getQuestItemsCount(SCEPTER_OF_BREKA) and st.getQuestItemsCount(SCEPTER_OF_ENKU) and st.getQuestItemsCount(SCEPTER_OF_VUKU) and st.getQuestItemsCount(SCEPTER_OF_TUREK) and st.getQuestItemsCount(SCEPTER_OF_TUNATH) :
                 st.set("cond","5")
                 st.playSound("Itemsound.quest_middle")
               else :
                 st.playSound("Itemsound.quest_itemget")
           elif st.getQuestItemsCount(SCEPTER_OF_ENKU) :
               htmltext = "30616-07.htm"
           else :
               htmltext = "30616-01.htm"
         elif npcId == 30617 :                               # Chief of Turek Orcs
           if st.getQuestItemsCount(KASMANS_LETTER2) :
               htmltext = "30617-02.htm"
           elif st.getQuestItemsCount(GLOVE_OF_BURAI) :
               htmltext = "30617-05.htm"
               st.playSound("Itemsound.quest_before_battle")
               st.addSpawn(27083,-94292,110781,-3701)
               st.addSpawn(27083,-94293,110861,-3701)
           elif st.getQuestItemsCount(MAKUM_BUGBEAR_HEAD) == 2 :
               htmltext = "30617-06.htm"
               st.giveItems(SCEPTER_OF_TUREK,1)
               st.takeItems(MAKUM_BUGBEAR_HEAD,2)
               if st.getQuestItemsCount(SCEPTER_OF_BREKA) and st.getQuestItemsCount(SCEPTER_OF_ENKU) and st.getQuestItemsCount(SCEPTER_OF_VUKU) and st.getQuestItemsCount(SCEPTER_OF_TUREK) and st.getQuestItemsCount(SCEPTER_OF_TUNATH) :
                 st.set("cond","5")
                 st.playSound("Itemsound.quest_middle")
               else :
                 st.playSound("Itemsound.quest_itemget")
           elif st.getQuestItemsCount(SCEPTER_OF_TUREK) :
               htmltext = "30617-07.htm"
           else :
               htmltext = "30617-01.htm"
         elif npcId == 30618 :                               # Chief of Tunath Orcs
           if st.getQuestItemsCount(KASMANS_LETTER3) :
               htmltext = "30618-02.htm"
           elif st.getQuestItemsCount(SCEPTER_OF_TUNATH) :
               htmltext = "30618-04.htm"
           else :
               htmltext = "30618-01.htm"
         elif npcId == 30619 :                               # Chief of Vuku Orcs
           if st.getQuestItemsCount(KASMANS_LETTER1) :
               htmltext = "30619-02.htm"
           elif st.getQuestItemsCount(DRIKOS_CONTRACT) :
             if st.getQuestItemsCount(STAKATO_DRONE_HUSK1) == 30 :
                 htmltext = "30619-05.htm"
                 st.giveItems(SCEPTER_OF_VUKU,1)
                 st.takeItems(STAKATO_DRONE_HUSK1,30)
                 st.takeItems(DRIKOS_CONTRACT,1)
                 if st.getQuestItemsCount(SCEPTER_OF_BREKA) and st.getQuestItemsCount(SCEPTER_OF_ENKU) and st.getQuestItemsCount(SCEPTER_OF_VUKU) and st.getQuestItemsCount(SCEPTER_OF_TUREK) and st.getQuestItemsCount(SCEPTER_OF_TUNATH) :
                   st.set("cond","5")
                   st.playSound("Itemsound.quest_middle")
                 else :
                   st.playSound("Itemsound.quest_itemget")
             else :
                 htmltext = "30619-04.htm"
           elif st.getQuestItemsCount(SCEPTER_OF_VUKU) :
               htmltext = "30619-06.htm"
           else :
               htmltext = "30619-01.htm"
     elif cond == 5 :                                       # Return to Chianta
         if npcId == 30642 and st.getQuestItemsCount(NECKLACE_OF_AUTHORITY) :
           if st.getQuestItemsCount(CHIANTAS_ORDER1) and st.getQuestItemsCount(SCEPTER_OF_BREKA) and st.getQuestItemsCount(SCEPTER_OF_VUKU) and st.getQuestItemsCount(SCEPTER_OF_TUREK) and st.getQuestItemsCount(SCEPTER_OF_TUNATH) and st.getQuestItemsCount(SCEPTER_OF_ENKU) :
             htmltext = "30642-05.htm"
           elif st.getQuestItemsCount(CHIANTAS_ORDER2) :
             if player.getLevel() >= 38 :
               htmltext = "30642-09.htm"
               st.giveItems(CHIANTAS_ORDER3,1)
               st.set("cond","6")
               st.takeItems(CHIANTAS_ORDER2,1)
             else:
               htmltext = "30642-08.htm"
     elif cond == 6 :                                       # Punish The Betrayers
         if npcId == 30642 and st.getQuestItemsCount(NECKLACE_OF_AUTHORITY) and st.getQuestItemsCount(CHIANTAS_ORDER3) :
             htmltext = "30642-10.htm"
     elif cond == 7 :                                       # The Punishment Ends
         if npcId == 30642 and st.getQuestItemsCount(TAMLIN_ORC_SKULL) == 20 and st.getQuestItemsCount(TIMAK_ORC_HEAD) == 20 :
           st.giveItems(SCEPTER_BOX,1)
           st.set("cond","8")
           st.takeItems(NECKLACE_OF_AUTHORITY,1)
           st.takeItems(CHIANTAS_ORDER3,1)
           st.takeItems(TAMLIN_ORC_SKULL,20)
           st.takeItems(TIMAK_ORC_HEAD,20)
           htmltext = "30642-11.htm"
     elif cond == 8 :                                       # Priest of the Immortal Plateau
         if npcId == 30642 and st.getQuestItemsCount(SCEPTER_BOX) :
           htmltext = "30642-12.htm"
         elif npcId == 30571 and st.getQuestItemsCount(SCEPTER_BOX) :
           htmltext = "30571-01.htm"
     elif cond == 9 :                                       # The Scepter of Tantos
       if npcId == 30571 and st.getQuestItemsCount(TANAPIS_ORDER1) :
         htmltext = "30571-04.htm"
       elif npcId == 30642 and st.getQuestItemsCount(TANAPIS_ORDER1) :
         htmltext = "30642-13.htm"
     elif cond == 10 :                                      # Recovered Scepter of Tantos
       if npcId == 30571 and st.getQuestItemsCount(TANAPIS_ORDER1) and st.getQuestItemsCount(SCEPTER_OF_TANTOS) :
         htmltext = "30571-05.htm"
         st.giveItems(RITUAL_BOX,1)
         st.set("cond","11")
         st.takeItems(SCEPTER_OF_TANTOS,1)
         st.takeItems(TANAPIS_ORDER1,1)
     elif cond == 11 :                                      # To the Lord of Flame
       if npcId == 30571 and st.getQuestItemsCount(RITUAL_BOX) :
         htmltext = "30571-06.htm"
       elif npcId == 30565 and st.getQuestItemsCount(RITUAL_BOX) :
         st.addExpAndSp(91457,2500)
         st.giveItems(7562,16)
         htmltext = "30565-02.htm"
         st.giveItems(MARK_OF_GLORY,1)
         st.takeItems(RITUAL_BOX,1)
         st.set("cond","0")
         st.setState(COMPLETED)
         st.playSound("ItemSound.quest_finish")
       else :
         htmltext = "30565-01.htm"
   return htmltext

 def onKill(self,npc,player,isPet):
   st = player.getQuestState(qn)
   if not st : return
   if st.getState() != STARTED : return

   npcId = npc.getNpcId()
   cond = st.getInt("cond")
   if cond == 1 and npcId in DROPLIST_COND_1.keys():
     required,item,maxquanty=DROPLIST_COND_1[npcId]
     count = st.getQuestItemsCount(item)
     if st.getQuestItemsCount(required) and count < maxquanty :
       st.giveItems(item,1)
       if count+1 == maxquanty :                            # Check if got enough number of items
         if st.getQuestItemsCount(MANASHEN_SHARD) == maxquanty and st.getQuestItemsCount(TYRANT_TALON) == maxquanty and st.getQuestItemsCount(GUARDIAN_BASILISK_FANG) == maxquanty :
           st.set("cond","2")
           st.playSound("Itemsound.quest_middle")
         else :
           st.playSound("Itemsound.quest_itemget")
       else :
         st.playSound("Itemsound.quest_itemget")
   elif cond == 4 and npcId in DROPLIST_COND_4.keys():
     required,item,maxquanty=DROPLIST_COND_4[npcId]
     count = st.getQuestItemsCount(item)
     if st.getQuestItemsCount(required) and count < maxquanty :
       if npcId == 20234 :
         if st.getRandom(4) <= 2 :
           st.giveItems(item,1)
           if count+1 == maxquanty :                        # Check if got enough number of items
             st.playSound("Itemsound.quest_middle")
           else :
             st.playSound("Itemsound.quest_itemget")
       else :
         st.giveItems(item,1)
         if count+1 == maxquanty :                          # Check if got enough number of items
           if npcId in [ 27080, 27081 ] :
             if st.getQuestItemsCount(PASHIKAS_HEAD) and st.getQuestItemsCount(VULTUS_HEAD) :
               st.takeItems(required,1)
               st.playSound("Itemsound.quest_middle")
             else :
               st.playSound("Itemsound.quest_itemget")
           else :
             st.takeItems(required,1)
             st.playSound("Itemsound.quest_middle")
         else :
           st.playSound("Itemsound.quest_itemget")
   elif cond == 6 and npcId in DROPLIST_COND_6.keys():
     required,item,maxquanty,chance=DROPLIST_COND_6[npcId]
     count = st.getQuestItemsCount(item)
     if st.getQuestItemsCount(required) and count < maxquanty :
       if st.getRandom(100) < chance :
         st.giveItems(item,1)
         if count+1 == maxquanty :                          # Check if got enough number of items
           if st.getQuestItemsCount(TIMAK_ORC_HEAD) == maxquanty and st.getQuestItemsCount(TAMLIN_ORC_SKULL) == maxquanty :
             st.set("cond","7")
             st.playSound("Itemsound.quest_middle")
           else :
             st.playSound("Itemsound.quest_itemget")
         else :
           st.playSound("Itemsound.quest_itemget")
   elif cond == 9 and st.getQuestItemsCount(TANAPIS_ORDER1) and st.getQuestItemsCount(SCEPTER_OF_TANTOS) == 0 :
     if npcId in [ 20778, 20779 ] :
       st.playSound("Itemsound.quest_before_battle")
       #st.addSpawn(27086)
       st.addSpawn(27086, 11839,-106261,-3550,300000)
       return "Revenant of Tantos Chief has spawned at X=11839 Y=-106261 Z=-3550"
       # Alternate coord. set:
       #st.addSpawn(27086,11567,-106785,-3520)
     elif npcId == 27086 :
       st.giveItems(SCEPTER_OF_TANTOS,1)
       st.set("cond","10")
       st.playSound("Itemsound.quest_middle")
   return

QUEST       = Quest(220,qn,"Testimony Of Glory")
CREATED     = State('Start', QUEST)
STARTING    = State('Starting', QUEST)
STARTED     = State('Started', QUEST)
COMPLETED   = State('Completed', QUEST)


QUEST.setInitialState(CREATED)
QUEST.addStartNpc(30514)

QUEST.addTalkId(30514)

QUEST.addTalkId(30501)
QUEST.addTalkId(30515)
QUEST.addTalkId(30565)
QUEST.addTalkId(30571)
QUEST.addTalkId(30615)
QUEST.addTalkId(30616)
QUEST.addTalkId(30617)
QUEST.addTalkId(30618)
QUEST.addTalkId(30619)
QUEST.addTalkId(30642)

for i in DROPLIST_COND_1.keys()+DROPLIST_COND_4.keys()+DROPLIST_COND_6.keys()+[20778,20779,27086] :
    QUEST.addKillId(i)