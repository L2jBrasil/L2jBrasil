#Fixed by Cromir, expanded upon by Emperorc
#Quest: Repent Your Sins
import sys
from com.it.br.gameserver.model.quest import State
from com.it.br.gameserver.model.quest import QuestState
from com.it.br.gameserver.model.quest.jython import QuestJython as JQuest

qn = "422_RepentYourSins"

#Items
SCAVENGER_WERERAT_SKULL = 4326
TUREK_WARHOUND_TAIL = 4327
TYRANT_KINGPIN_HEART = 4328
TRISALIM_TARANTULAS_VENOM_SAC = 4329
MANUAL_OF_MANACLES = 4331
PENITENTS_MANACLES = 4425
PENITENTS_MANACLES1 = 4330
PENITENTS_MANACLES2 = 4426
SILVER_NUGGET = 1873
ADAMANTINE_NUGGET = 1877
BLACKSMITHS_FRAME = 1892
COKES = 1879
STEEL = 1880

#Mobs
SCAVENGER_WERERAT = 20039
TUREK_WARHOUND = 20494
TYRANT_KINGPIN = 20193
TRISALIM_TARANTULA = 20561

def findPetLvl (player, itemid) :
    pet = player.getPet()
    if pet:
        if pet.getNpcId() == 12564 :
            level = pet.getStat().getLevel()
        else :
            item = player.getInventory().getItemByItemId(itemid)
            level = item.getEnchantLevel()
    else :
        item = player.getInventory().getItemByItemId(itemid)
        level = item.getEnchantLevel()
    return level

class Quest (JQuest) :

 def __init__(self,id,name,descr): JQuest.__init__(self,id,name,descr)

 def onAdvEvent (self,event,npc,player) :
    htmltext = event
    st = player.getQuestState(qn)
    if not st: return
    if event == "Start" :
        st.playSound("ItemSound.quest_accept")
        st.setState(STARTED)
        if player.getLevel() <= 20 :
            htmltext = "30981-03.htm"
            st.set("cond","1")
            st.set("cond","2")
        elif player.getLevel() <= 30 :
            htmltext = "30981-04.htm"
            st.set("cond","3")
        elif player.getLevel() <= 40 :
            htmltext = "30981-05.htm"
            st.set("cond","4")
        else :
            htmltext = "30981-06.htm"
            st.set("cond","5")
    elif event == "1" :
      if st.getQuestItemsCount(PENITENTS_MANACLES1) >= 1:
          st.takeItems(PENITENTS_MANACLES1,-1)
      if st.getQuestItemsCount(PENITENTS_MANACLES2) >= 1:
          st.takeItems(PENITENTS_MANACLES2,-1)
      if st.getQuestItemsCount(PENITENTS_MANACLES) >= 1:
          st.takeItems(PENITENTS_MANACLES,-1)
      htmltext = "30981-11.htm"
      st.set("cond","16")
      st.set("level",str(player.getLevel()))
      st.giveItems(PENITENTS_MANACLES,1)
    elif event == "2" :
      htmltext = "30981-14.htm"
    elif event == "3" :
        plevel = findPetLvl(player,PENITENTS_MANACLES)
        level = player.getLevel()
        olevel = st.getInt("level")
        pet = player.getPet()
        if pet:
            if pet.getNpcId() == 12564 :
                htmltext = "30981-16.htm"
        else :
            if level > olevel :
                Pk_remove = plevel - level
            else :
                Pk_remove = plevel - olevel
            if Pk_remove < 0 :
                Pk_remove = 0
            Pk_remove = st.getRandom(10 + Pk_remove) + 1
            if player.getPkKills() <= Pk_remove :
                st.giveItems(PENITENTS_MANACLES2,1)
                st.takeItems(PENITENTS_MANACLES,1)
                htmltext = "30981-15.htm"
                player.setPkKills(0)
                st.playSound("ItemSound.quest_finished")
                st.exitQuest(1)
            else :
                st.giveItems(PENITENTS_MANACLES2,1)
                st.takeItems(PENITENTS_MANACLES,1)
                htmltext = "30981-17.htm"
                Pk_new = player.getPkKills() - Pk_remove
                player.setPkKills(Pk_new)
                st.set("level","0")
    elif event == "4" :
      htmltext = "30981-19.htm"
    elif event == "Quit" :
        htmltext = "30981-20.htm"
        st.playSound("ItemSound.quest_finished")
        st.takeItems(SCAVENGER_WERERAT_SKULL,-1)
        st.takeItems(TUREK_WARHOUND_TAIL,-1)
        st.takeItems(TYRANT_KINGPIN_HEART,-1)
        st.takeItems(TRISALIM_TARANTULAS_VENOM_SAC,-1)
        st.takeItems(PENITENTS_MANACLES1,-1)
        st.takeItems(MANUAL_OF_MANACLES,-1)
        st.takeItems(PENITENTS_MANACLES,-1)
        st.exitQuest(1)
    return htmltext

 def onTalk (Self,npc,player):
   htmltext = "<html><body>You are either not carrying out your quest or don't meet the criteria.</body></html>"
   st = player.getQuestState(qn)
   if not st : return htmltext
   condition = st.getInt("cond")
   npcId = npc.getNpcId()
   id = st.getState()
   if npcId == 30981 : #Black Judge
       if id == CREATED :
           if player.getPkKills() >= 1:
               htmltext = "30981-02.htm"
           else:
               htmltext = "30981-01.htm"
               st.exitQuest(1)
       elif condition <= 9 :
           htmltext = "30981-07.htm"
       elif condition <= 13 and condition > 9 and st.getQuestItemsCount(MANUAL_OF_MANACLES) == 0 :
           htmltext = "30981-08.htm"
           st.set("cond","14")
           st.giveItems(MANUAL_OF_MANACLES,1)
       elif condition == 14 and st.getQuestItemsCount(MANUAL_OF_MANACLES) > 0 :
           htmltext = "30981-09.htm"
       elif condition == 15 and st.getQuestItemsCount(PENITENTS_MANACLES1) > 0 :
           htmltext = "30981-10.htm"
       elif condition >= 16 :
           if st.getQuestItemsCount(PENITENTS_MANACLES) > 0 :
               plevel = findPetLvl(player,PENITENTS_MANACLES)
               level = player.getLevel()
               if st.getInt("level") > level :
                   level = st.getInt("level")
               if plevel :
                   if plevel > level:
                       htmltext = "30981-13.htm"
                   else :
                       htmltext = "30981-12.htm"
               else :
                   htmltext = "30981-12.htm"
           else :
               htmltext = "30981-18.htm"
   elif npcId == 30668 : # Katari
       if condition == 2 :
           st.set("cond","6")
           htmltext = "30668-01.htm"
       elif condition == 6 :
           if st.getQuestItemsCount(SCAVENGER_WERERAT_SKULL) < 10 :
               htmltext = "30668-02.htm"
           else :
               st.set("cond","10")
               htmltext = "30668-03.htm"
               st.takeItems(SCAVENGER_WERERAT_SKULL,-1)
       elif condition == 10 :
           htmltext = "30668-04.htm"
   elif npcId == 30597 : # Piotur
       if condition == 3 :
           st.set("cond","7")
           htmltext = "30597-01.htm"
       elif condition == 7 :
           if st.getQuestItemsCount(TUREK_WARHOUND_TAIL) < 10 :
               htmltext = "30597-02.htm"
           else :
               st.set("cond","11")
               htmltext = "30597-03.htm"
               st.takeItems(TUREK_WARHOUND_TAIL,-1)
       elif condition == 11 :
           htmltext = "30597-04.htm"
   elif npcId == 30612 : # Casian
       if condition == 4 :
           st.set("cond","8")
           htmltext = "30612-01.htm"
       elif condition == 8 :
           if st.getQuestItemsCount(TYRANT_KINGPIN_HEART) < 1 :
               htmltext = "30612-02.htm"
           else :
               st.set("cond","12")
               htmltext = "30612-03.htm"
               st.takeItems(TYRANT_KINGPIN_HEART,-1)
       elif condition == 12 :
           htmltext = "30612-04.htm"
   elif npcId == 30718 : # Joan
       if condition == 5 :
           st.set("cond","9")
           htmltext = "30718-01.htm"
       elif condition == 9 :
           if st.getQuestItemsCount(TRISALIM_TARANTULAS_VENOM_SAC) < 3 :
               htmltext = "30718-02.htm"
           elif st.getQuestItemsCount(TRISALIM_TARANTULAS_VENOM_SAC) >= 3 :
               st.set("cond","13")
               htmltext = "30718-03.htm"
               st.takeItems(TRISALIM_TARANTULAS_VENOM_SAC,-1)
       elif condition == 13 :
           htmltext = "30718-04.htm"
   elif npcId == 30300: #Pushkin
       if condition >= 14 :
           if st.getQuestItemsCount(MANUAL_OF_MANACLES) == 1 :
               if st.getQuestItemsCount(SILVER_NUGGET) < 10 or st.getQuestItemsCount(STEEL) < 5 or st.getQuestItemsCount(ADAMANTINE_NUGGET) < 2 \
                  or st.getQuestItemsCount(COKES) < 10 or st.getQuestItemsCount(BLACKSMITHS_FRAME) < 1 :
                   htmltext = "30300-02.htm"
               elif st.getQuestItemsCount(SILVER_NUGGET) >= 10 and st.getQuestItemsCount(STEEL) >= 5 and st.getQuestItemsCount(ADAMANTINE_NUGGET) >= 2 \
                    and st.getQuestItemsCount(COKES) >= 10 and st.getQuestItemsCount(BLACKSMITHS_FRAME) >= 1 :
                   htmltext = "30300-02.htm"
                   st.set("cond","15")
                   st.takeItems(MANUAL_OF_MANACLES,1)
                   st.takeItems(SILVER_NUGGET,10)
                   st.takeItems(ADAMANTINE_NUGGET,2)
                   st.takeItems(COKES,10)
                   st.takeItems(STEEL,5)
                   st.takeItems(BLACKSMITHS_FRAME,1)
                   st.giveItems(PENITENTS_MANACLES1,1)
                   st.playSound("ItemSound.quest_middle")
           elif st.getQuestItemsCount(PENITENTS_MANACLES1) or st.getQuestItemsCount(PENITENTS_MANACLES) or st.getQuestItemsCount(PENITENTS_MANACLES2) :
               htmltext = "30300-03.htm"
   return htmltext


 def onKill(self,npc,player,isPet) :
   st = player.getQuestState(qn)
   if not st : return
   if st.getState() != STARTED : return
   condition = st.getInt("cond")
   npcId = npc.getNpcId()
   skulls = st.getQuestItemsCount(SCAVENGER_WERERAT_SKULL)
   tails = st.getQuestItemsCount(TUREK_WARHOUND_TAIL)
   heart = st.getQuestItemsCount(TYRANT_KINGPIN_HEART)
   sacs = st.getQuestItemsCount(TRISALIM_TARANTULAS_VENOM_SAC)
   if npcId == SCAVENGER_WERERAT :
       if condition == 6 :
           if skulls < 10 :
               st.giveItems(SCAVENGER_WERERAT_SKULL,1)
               if skulls == 10 :
                   st.playSound("ItemSound.quest_middle")
               else :
                   st.playSound("ItemSound.quest_itemget")
   elif npcId == TUREK_WARHOUND :
       if condition == 7 :
           if tails < 10 :
               st.giveItems(TUREK_WARHOUND_TAIL,1)
               if tails == 10 :
                   st.playSound("ItemSound.quest_middle")
               else :
                   st.playSound("ItemSound.quest_itemget")
   elif npcId == TYRANT_KINGPIN :
       if condition == 8 :
           if heart < 1 :
               st.giveItems(TYRANT_KINGPIN_HEART,1)
               st.playSound("ItemSound.quest_middle")
   elif npcId == TRISALIM_TARANTULA :
       if condition == 9 :
           if sacs < 3 :
               st.giveItems(TRISALIM_TARANTULAS_VENOM_SAC,1)
               if skulls == 3 :
                   st.playSound("ItemSound.quest_middle")
               else :
                   st.playSound("ItemSound.quest_itemget")
   return

QUEST       = Quest(422,qn,"Repent your Sins")
CREATED     = State('Start', QUEST)
STARTED     = State('Started', QUEST)

QUEST.setInitialState(CREATED)
QUEST.addStartNpc(30981)

QUEST.addTalkId(30981)
QUEST.addTalkId(30668)
QUEST.addTalkId(30597)
QUEST.addTalkId(30612)
QUEST.addTalkId(30718)
QUEST.addTalkId(30300)

QUEST.addKillId(SCAVENGER_WERERAT)
QUEST.addKillId(TUREK_WARHOUND)
QUEST.addKillId(TYRANT_KINGPIN)
QUEST.addKillId(TRISALIM_TARANTULA)