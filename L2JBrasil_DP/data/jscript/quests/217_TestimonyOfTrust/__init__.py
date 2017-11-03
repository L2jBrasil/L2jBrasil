# Made by Mr. Have fun!
# Version 0.3 by H1GHL4ND3R
import sys
from com.it.br.gameserver.model.quest import State
from com.it.br.gameserver.model.quest import QuestState
from com.it.br.gameserver.network.serverpackets import SocialAction
from com.it.br.gameserver.model.quest.jython import QuestJython as JQuest

qn = "217_TestimonyOfTrust"

MARK_OF_TRUST_ID = 2734
LETTER_TO_ELF_ID = 1558
LETTER_TO_DARKELF_ID = 1556

LETTER_TO_DWARF_ID,           LETTER_TO_ORC_ID,        LETTER_TO_SERESIN_ID,  SCROLL_OF_DARKELF_TRUST_ID, \
SCROLL_OF_ELF_TRUST_ID,       SCROLL_OF_DWARF_TRUST_ID,SCROLL_OF_ORC_TRUST_ID,RECOMMENDATION_OF_HOLLIN_ID,\
ORDER_OF_OZZY_ID,             BREATH_OF_WINDS_ID,      SEED_OF_VERDURE_ID,    LETTER_OF_THIFIELL_ID,      \
BLOOD_OF_GUARDIAN_BASILISK_ID,GIANT_APHID_ID,          STAKATOS_FLUIDS_ID,    BASILISK_PLASMA_ID,         \
HONEY_DEW_ID,                 STAKATO_ICHOR_ID,        ORDER_OF_CLAYTON_ID,   PARASITE_OF_LOTA_ID,        \
LETTER_TO_MANAKIA_ID,         LETTER_OF_MANAKIA_ID,    LETTER_TO_NICHOLA_ID,  ORDER_OF_NICHOLA_ID,        \
HEART_OF_PORTA_ID = range(2737,2762)

DROPLIST={
# For condition 2
27120:[ORDER_OF_OZZY_ID,BREATH_OF_WINDS_ID,               1],
27121:[ORDER_OF_OZZY_ID,SEED_OF_VERDURE_ID,               1],
# For condition 6
20550 :[ORDER_OF_CLAYTON_ID,BLOOD_OF_GUARDIAN_BASILISK_ID,5],
20082 :[ORDER_OF_CLAYTON_ID,GIANT_APHID_ID,               5],
20084 :[ORDER_OF_CLAYTON_ID,GIANT_APHID_ID,               5],
20086 :[ORDER_OF_CLAYTON_ID,GIANT_APHID_ID,               5],
20087 :[ORDER_OF_CLAYTON_ID,GIANT_APHID_ID,               5],
20088 :[ORDER_OF_CLAYTON_ID,GIANT_APHID_ID,               5],
20157 :[ORDER_OF_CLAYTON_ID,STAKATOS_FLUIDS_ID,           5],
20230 :[ORDER_OF_CLAYTON_ID,STAKATOS_FLUIDS_ID,           5],
20232 :[ORDER_OF_CLAYTON_ID,STAKATOS_FLUIDS_ID,           5],
20234 :[ORDER_OF_CLAYTON_ID,STAKATOS_FLUIDS_ID,           5],
# For condition 19
20213 :[ORDER_OF_NICHOLA_ID,HEART_OF_PORTA_ID,            1]
}


class Quest (JQuest) :

 def __init__(self,id,name,descr): JQuest.__init__(self,id,name,descr)

 def onEvent (self,event,st) :
    htmltext = event
    if event == "30191-04.htm" :
      st.set("cond","1")
      st.setState(STARTED)
      st.playSound("ItemSound.quest_accept")
      st.giveItems(LETTER_TO_ELF_ID,1)
      st.giveItems(LETTER_TO_DARKELF_ID,1)
    elif event == "30154-03.htm" :
      st.takeItems(LETTER_TO_ELF_ID,1)
      st.giveItems(ORDER_OF_OZZY_ID,1)
      st.set("cond","2")
    elif event == "30358-02.htm" :
      st.takeItems(LETTER_TO_DARKELF_ID,1)
      st.giveItems(LETTER_OF_THIFIELL_ID,1)
      st.set("cond","5")
    elif event == "30657-03.htm" :
      if st.getPlayer().getLevel() >= 38 :                 # Condition 12 meet the Lord Kakai (Orc Master)
        st.takeItems(LETTER_TO_SERESIN_ID,1)
        st.giveItems(LETTER_TO_ORC_ID,1)
        st.giveItems(LETTER_TO_DWARF_ID,1)
        st.set("cond","12")
      else:                                                # Condition 11 A lack of Experience
        htmltext = "30657-02.htm"
        st.set("cond","11")
    elif event == "30565-02.htm" :
      st.takeItems(LETTER_TO_ORC_ID,1)
      st.giveItems(LETTER_TO_MANAKIA_ID,1)
      st.set("cond","13")
    elif event == "30515-02.htm" :
      st.takeItems(LETTER_TO_MANAKIA_ID,1)
      st.set("cond","14")
    elif event == "30531-02.htm" :
      st.takeItems(LETTER_TO_DWARF_ID,1)
      st.giveItems(LETTER_TO_NICHOLA_ID,1)
      st.set("cond","18")
    elif event == "30621-02.htm" :
      st.takeItems(LETTER_TO_NICHOLA_ID,1)
      st.giveItems(ORDER_OF_NICHOLA_ID,1)
      st.set("cond","19")
    return htmltext


 def onTalk (self,npc,player):
   htmltext = "<html><body>You are either not carrying out your quest or don't meet the criteria.</body></html>"
   st = player.getQuestState(qn)
   if not st : return htmltext

   npcId = npc.getNpcId()
   id = st.getState()
   if npcId != 30191 and id != STARTED : return htmltext

   if id == CREATED :                                      # Check if is starting the quest
     st.set("cond","0")
     st.set("id","0")
     if npcId == 30191 :
       if player.getRace().ordinal() == 0 :
         if player.getLevel() >= 37 :
           htmltext = "30191-03.htm"
         else:
           htmltext = "30191-01.htm"
           st.exitQuest(1)
       else:
         htmltext = "30191-02.htm"
         st.exitQuest(1)
   elif id == COMPLETED :                                  # Check if the quest is already made
      if npcId == 30191 :
        htmltext = "<html><body>This quest has already been completed.</body></html>"
   else :                                                  # The quest it self
     try :
       cond = st.getInt("cond")
     except :
       cond = None
     if cond == 1 :                                        # Condition 1 take the letter to Hierarch Asterios (Elven Master)
         if npcId == 30191 :
           htmltext = "30191-08.htm"
         elif npcId == 30154 and st.getQuestItemsCount(LETTER_TO_ELF_ID) :
           htmltext = "30154-01.htm"
     elif cond == 2 :                                      # Condition 2 kill the Luel of Zephy and Aktea of the Woods
         if npcId == 30154 and st.getQuestItemsCount(ORDER_OF_OZZY_ID) :
           htmltext = "30154-04.htm"
     elif cond == 3 :                                      # Condition 3 bring back the Breath of winds and Seed of Verdure to Asterios
         if npcId == 30154 and st.getQuestItemsCount(BREATH_OF_WINDS_ID) and st.getQuestItemsCount(SEED_OF_VERDURE_ID) :
           htmltext = "30154-05.htm"
           st.takeItems(BREATH_OF_WINDS_ID,1)
           st.takeItems(SEED_OF_VERDURE_ID,1)
           st.takeItems(ORDER_OF_OZZY_ID,1)
           st.giveItems(SCROLL_OF_ELF_TRUST_ID,1)
           st.set("cond","4")
     elif cond == 4 :                                      # Condition 4 take the letter to Tetrarch Thifiell (Dark Elven Master)
         if npcId == 30154 :
           htmltext = "30154-06.htm"
         elif npcId == 30358 and st.getQuestItemsCount(LETTER_TO_DARKELF_ID) :
           htmltext = "30358-01.htm"
     elif cond == 5 :                                      # Condition 5 meet the Magister Clayton
         if npcId == 30358 :
           htmltext = "30358-05.htm"
         elif npcId == 30464 and st.getQuestItemsCount(LETTER_OF_THIFIELL_ID) :
           htmltext = "30464-01.htm"
           st.takeItems(LETTER_OF_THIFIELL_ID,1)
           st.giveItems(ORDER_OF_CLAYTON_ID,1)
           st.set("cond","6")
     elif cond == 6 :                                      # Condition 6 get 10 of each, Stakato ichor, honey dew and basilisk plasma
         if npcId == 30464 and st.getQuestItemsCount(ORDER_OF_CLAYTON_ID) :
           htmltext = "30464-02.htm"
     elif cond == 7 :                                      # Condition 7 bring back the Stakato ichor, honey dew and basilisk plasma to Magister Clayton
         if npcId == 30464 and st.getQuestItemsCount(ORDER_OF_CLAYTON_ID) and st.getQuestItemsCount(STAKATO_ICHOR_ID) and st.getQuestItemsCount(HONEY_DEW_ID) and st.getQuestItemsCount(BASILISK_PLASMA_ID) :
           htmltext = "30464-03.htm"
           st.set("cond","8")
     elif cond == 8 :                                      # Condition 8 take the Stakato ichor, honey dew and basilisk plasma to Thifiell
         if npcId == 30358 and st.getQuestItemsCount(ORDER_OF_CLAYTON_ID) and st.getQuestItemsCount(STAKATO_ICHOR_ID) and st.getQuestItemsCount(HONEY_DEW_ID) and st.getQuestItemsCount(BASILISK_PLASMA_ID) :
           htmltext = "30358-03.htm"
           st.takeItems(ORDER_OF_CLAYTON_ID,1)
           st.takeItems(BASILISK_PLASMA_ID,1)
           st.takeItems(STAKATO_ICHOR_ID,1)
           st.takeItems(HONEY_DEW_ID,1)
           st.giveItems(SCROLL_OF_DARKELF_TRUST_ID,1)
           st.set("cond","9")
     elif cond == 9 :                                      # Condition 9 take the Elven and Dark Elven scroll to Hollint
         if npcId == 30191 and st.getQuestItemsCount(SCROLL_OF_ELF_TRUST_ID) and st.getQuestItemsCount(SCROLL_OF_DARKELF_TRUST_ID) :
           htmltext = "30191-05.htm"
           st.takeItems(SCROLL_OF_DARKELF_TRUST_ID,1)
           st.takeItems(SCROLL_OF_ELF_TRUST_ID,1)
           st.giveItems(LETTER_TO_SERESIN_ID,1)
           st.set("cond","10")
         elif npcId == 30358 :
           htmltext = "30358-04.htm"
     elif cond in [ 10, 11 ] :                             # Condition 10 meet the Seresin or Condition 11 A lack of Experience 
         if npcId == 30191 :
           htmltext = "30191-09.htm"
         elif npcId == 30657 and st.getQuestItemsCount(LETTER_TO_SERESIN_ID) :
           htmltext = "30657-01.htm"
     elif cond == 12 :                                     # Condition 12 meet the Lord Kakai (Orc Master)
         if npcId == 30657 :
           htmltext = "30657-04.htm"
         elif npcId == 30565 and st.getQuestItemsCount(LETTER_TO_ORC_ID) :
           htmltext = "30565-01.htm"
     elif cond == 13 :                                     # Condition 13 meet the Seer Manakia
         if npcId == 30565 :
           htmltext = "30565-03.htm"
         elif npcId == 30515 and st.getQuestItemsCount(LETTER_TO_MANAKIA_ID) :
           htmltext = "30515-01.htm"
     elif cond == 14 :                                     # Condition 14 get 10 Parasite of lota
         if npcId == 30515 :
           htmltext = "30515-03.htm"
     elif cond == 15 :                                     # Condition 15 bring back the Parasite of lota to Seer Manakia
         if npcId == 30515 and st.getQuestItemsCount(PARASITE_OF_LOTA_ID)==10 :
           htmltext = "30515-04.htm"
           st.takeItems(PARASITE_OF_LOTA_ID,10)
           st.giveItems(LETTER_OF_MANAKIA_ID,1)
           st.set("cond","16")
     elif cond == 16 :                                     # Condition 16 bring the letter of Manakia to the Lord Kakai
         if npcId == 30565 and st.getQuestItemsCount(LETTER_OF_MANAKIA_ID) :
           htmltext = "30565-04.htm"
           st.takeItems(LETTER_OF_MANAKIA_ID,1)
           st.giveItems(SCROLL_OF_ORC_TRUST_ID,1)
           st.set("cond","17")
         elif npcId == 30515 :
           htmltext = "30515-05.htm"
     elif cond == 17 :                                     # Condition 17 meet the Lockirin (Dwarven Master)
         if npcId == 30565 :
           htmltext = "30565-05.htm"
         elif npcId == 30531 and st.getQuestItemsCount(LETTER_TO_DWARF_ID) :
           htmltext = "30531-01.htm"
     elif cond == 18 :                                     # Condition 18 take the letter to Nichola
         if npcId == 30531 :
           htmltext = "30531-03.htm"
         elif npcId == 30621 and st.getQuestItemsCount(LETTER_TO_NICHOLA_ID) :
           htmltext = "30621-01.htm"
     elif cond == 19 :                                     # Condition 19 get 10 Heart of Porta
         if npcId == 30621 :
           htmltext = "30621-03.htm"
     elif cond == 20 :                                     # Condition 20 bring the 10 Heart of Porta to Nichola
         if npcId == 30621 and st.getQuestItemsCount(ORDER_OF_NICHOLA_ID) and st.getQuestItemsCount(HEART_OF_PORTA_ID)==10 :
           htmltext = "30621-04.htm"
           st.takeItems(HEART_OF_PORTA_ID,1)
           st.takeItems(ORDER_OF_NICHOLA_ID,1)
           st.set("cond","21")
     elif cond == 21 :                                     # Condition 21 take the letter to Lockirin
         if npcId == 30621 :
           htmltext = "30621-05.htm"
         elif npcId == 30531 :
           htmltext = "30531-04.htm"
           st.giveItems(SCROLL_OF_DWARF_TRUST_ID,1)
           st.set("cond","22")
     elif cond == 22 :                                     # Condition 22 take the Orc and Dwarven scroll to High Priest Hollint
         if npcId == 30191 and st.getQuestItemsCount(SCROLL_OF_DWARF_TRUST_ID) and st.getQuestItemsCount(SCROLL_OF_ORC_TRUST_ID) :
           htmltext = "30191-06.htm"
           st.takeItems(SCROLL_OF_DWARF_TRUST_ID,1)
           st.takeItems(SCROLL_OF_ORC_TRUST_ID,1)
           st.giveItems(RECOMMENDATION_OF_HOLLIN_ID,1)
           st.set("cond","23")
         elif npcId == 30657 :
           htmltext = "30657-05.htm"
         elif npcId == 30531 :
           htmltext = "30531-05.htm"
     elif cond == 23 :                                     # Condition 23 take the Recommendation of Hollin to the High Priest Biotin
         if npcId == 30191 :
           htmltext = "30191-07.htm"
         elif npcId == 30031 and st.getQuestItemsCount(RECOMMENDATION_OF_HOLLIN_ID) :
           st.addExpAndSp(39571,2500)
           ObjectId=player.getObjectId() 
           player.broadcastPacket(SocialAction(ObjectId,3))
           htmltext = "30031-01.htm"
           st.takeItems(RECOMMENDATION_OF_HOLLIN_ID,1)
           st.giveItems(MARK_OF_TRUST_ID,1)
           st.unset("cond")
           st.unset("id")
           st.setState(COMPLETED)
           st.playSound("ItemSound.quest_finish")
   return htmltext


 def onKill(self,npc,player,isPet):
   st = player.getQuestState(qn)
   if not st : return
   if st.getState() != STARTED : return

   npcId = npc.getNpcId()
   cond = st.getInt("cond")
   if cond == 2 and npcId in [ 20013, 20019, 20036, 20044 ] :          # Condition 2 kill the Luel of Zephy and Aktea of the Woods
     if npcId in [ 20036,20044 ] and st.getQuestItemsCount(BREATH_OF_WINDS_ID) == 0 :
       st.set("id",str(st.getInt("id")+1))
       if st.getRandom(100)<(st.getInt("id")*33) :
         st.playSound("Itemsound.quest_before_battle")
         st.addSpawn(27120,npc.getX(),npc.getY(),npc.getZ(),600000)
     elif npcId in [ 20013,20019 ] and st.getQuestItemsCount(SEED_OF_VERDURE_ID) == 0 :
       st.set("id",str(st.getInt("id")+1))
       if st.getRandom(100)<(st.getInt("id")*33) :
         st.playSound("Itemsound.quest_before_battle")
         st.addSpawn(27121,npc.getX(),npc.getY(),npc.getZ(),600000) 
   elif cond == 14 :                                       # Condition 14 get 10 Parasite of lota
     parasite = st.getQuestItemsCount(PARASITE_OF_LOTA_ID)
     if npcId == 20553 and parasite < 10 :
       if st.getRandom(2) == 1 :
         st.giveItems(PARASITE_OF_LOTA_ID,1)
         if parasite+1 == 10 :
           st.set("cond","15")
           st.playSound("Itemsound.quest_middle")
         else:
           st.playSound("Itemsound.quest_itemget")
   elif cond in [ 2,6,19 ] and npcId in DROPLIST.keys() :
     required,item,maxqty=DROPLIST[npcId]
     count = st.getQuestItemsCount(item)
     if st.getQuestItemsCount(required) and count < maxqty :
        st.giveItems(item,1)
        if count+1 == maxqty :                             # Check if got enough number of items
          # Special Sound event
          if npcId in [ 20550, 20082, 20084, 20086, 20087, 20088, 20157, 20230, 20232, 20234 ] : 
             # Condition 6 get 10 of each, Stakato ichor, honey dew and basilisk plasma, and transform it
             if item == BLOOD_OF_GUARDIAN_BASILISK_ID :
               st.takeItems(BLOOD_OF_GUARDIAN_BASILISK_ID, maxqty)
               st.giveItems(BASILISK_PLASMA_ID, 1)
             elif item == GIANT_APHID_ID :
               st.takeItems(GIANT_APHID_ID, maxqty)
               st.giveItems(HONEY_DEW_ID, 1)
             elif item == STAKATOS_FLUIDS_ID :
               st.takeItems(STAKATOS_FLUIDS_ID, maxqty)
               st.giveItems(STAKATO_ICHOR_ID, 1)
             # Check if player got all the items of condition 6 and set the condition to 7
             if st.getQuestItemsCount(BASILISK_PLASMA_ID) and st.getQuestItemsCount(HONEY_DEW_ID) and st.getQuestItemsCount(STAKATO_ICHOR_ID) :
               st.set("cond","7")
               st.playSound("Itemsound.quest_middle")
             else:
               st.playSound("Itemsound.quest_itemget")
          elif npcId in [ 27120,27121 ] :             # Condition 2 kill the Luel of Zephy and Aktea of the Woods
            # Check if player got all the items of condition 2 and set the condition to 3
            if st.getQuestItemsCount(SEED_OF_VERDURE_ID) and st.getQuestItemsCount(BREATH_OF_WINDS_ID) :
              st.set("cond","3")
              st.playSound("Itemsound.quest_middle")
            else :
              st.playSound("Itemsound.quest_itemget")
          elif npcId == 20213 :                              # Condition 19 Porta
            st.set("cond","20")
            st.playSound("ItemSound.quest_middle")
        else:
          st.playSound("Itemsound.quest_itemget")
   return

QUEST       = Quest(217,qn,"Testimony of Trust")
CREATED     = State('Start', QUEST)
STARTING    = State('Starting', QUEST)
STARTED     = State('Started', QUEST)
COMPLETED   = State('Completed', QUEST)


QUEST.setInitialState(CREATED)
QUEST.addStartNpc(30191)

QUEST.addTalkId(30191)

QUEST.addTalkId(30031)
QUEST.addTalkId(30154)
QUEST.addTalkId(30358)
QUEST.addTalkId(30464)
QUEST.addTalkId(30515)
QUEST.addTalkId(30531)
QUEST.addTalkId(30565)
QUEST.addTalkId(30621)
QUEST.addTalkId(30657)

for i in DROPLIST.keys()+[20013,20019,20036,20044,20553] :
    QUEST.addKillId(i)
