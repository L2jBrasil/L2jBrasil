# Original code by mtrix, Updated by Emperorc
import sys
from com.it.br.gameserver.model.quest import State
from com.it.br.gameserver.model.quest import QuestState
from com.it.br.gameserver.model.quest.jython import QuestJython as JQuest

qn = "337_AudienceWithTheLandDragon"

#NPCS
MOKE = 30498
HELTON = 30678
CHAKIRIS = 30705
KAIENA = 30720
GABRIELLE = 30753
GILMORE = 30754
THEODRIC = 30755
KENDRA = 30851
ORVEN = 30857
NPCS = [30678, 30498, 30705, 30720, 30753, 30754, 30755, 30851, 30857]

#MOBS
HAMRUT = 20649
KRANROT = 20650
BLOODY_QUEEN = 18001
BLOODY_QUEEN2 = 18002
SACRIFICE_OF_THE_SACRIFICED = 27171
HARIT_LIZARDMAN_SHAMAN = 20644
HARIT_LIZARDMAN_ZEALOT = 27172
MARSH_STALKER = 20679
MARSH_DRAKE = 20680
ABYSS_JEWEL1 = 27165
GUARDIAN1 = 27168
ABYSS_JEWEL2 = 27166
GUARDIAN2 = 27169
ABYSS_JEWEL3 = 27167
GUARDIAN3 = 27170
CAVE_KEEPER = 20277
CAVE_MAIDEN = 20287
CAVE_KEEPER1 = 20246
CAVE_MAIDEN1 = 20134
MOBS = [18001, 18002, 20277, 20287, 20246, 20134, 20644, 20649, 20650, 20679, 20680] + range(27165, 27173)

FEATHER_OF_GABRIELLE,MARSH_STALKER_HORN,MARSH_DRAKE_TALONS,KRANROT_SKIN,\
HAMRUT_LEG,REMAINS_OF_SACRIFICED,TOTEM_OF_LAND_DRAGON,FIRST_FRAGMENT_OF_ABYSS_JEWEL,\
SECOND_FRAGMENT_OF_ABYSS_JEWEL,THIRD_FRAGMENT_OF_ABYSS_JEWEL,MARA_FANG,MUSFEL_FANG,\
MARK_OF_WATCHMAN,PORTAL_STONE,HERALD_OF_SLAYER = range(3852,3866)+[3890]

def checkCond(st) :
    if st.getInt("orven")== 1 and st.getInt("kendra")==1 and st.getInt("chakiris")==1 and st.getInt("kaiena")==1 :
        st.set("all","1")

class Quest (JQuest) :

 def __init__(self,id,name,descr): JQuest.__init__(self,id,name,descr)

 def onAdvEvent (self,event,npc,player):
     if player :
         st = player.getQuestState(qn)
         if not st : return
         htmltext = event
         if event == "30753-02.htm" :
             st.exitQuest(1)
         elif event == "30753-06.htm" :
             st.setState(STARTED)
             st.set("cond","1")
             st.set("all","0")
             st.set("orven","0")
             st.set("kendra","0")
             st.set("chakiris","0")
             st.set("kaiena","0")
             st.set("moke","0")
             st.set("helton","0")
             st.giveItems(FEATHER_OF_GABRIELLE,1)
             st.playSound("ItemSound.quest_accept")
         elif event == "30753-10.htm" :
             st.set("cond","2")
             st.takeItems(MARK_OF_WATCHMAN,-1)
         elif event == "30754-03.htm" :
             st.set("cond","4")
         elif event == "30755-05.htm" :
             st.giveItems(PORTAL_STONE,1)
             st.takeItems(HERALD_OF_SLAYER,-1)
             st.takeItems(THIRD_FRAGMENT_OF_ABYSS_JEWEL,-1)
             st.playSound("ItemSound.quest_finish")
             st.exitQuest(1)
         return htmltext
     elif event == "Jewel1_Timer1" :
         npc.decayMe()
         self.cancelQuestTimer("Jewel1_Timer2",npc,None)
     elif event == "Jewel1_Timer2" :
         npc.decayMe()
         self.cancelQuestTimer("Jewel1_Timer1",npc,None)
     elif event == "Jewel2_Timer1" :
         npc.decayMe()
         self.cancelQuestTimer("Jewel2_Timer2",npc,None)
     elif event == "Jewel2_Timer2" :
         npc.decayMe()
         self.cancelQuestTimer("Jewel2_Timer1",npc,None)
     return

 def onTalk (self,npc,player):
    htmltext = "<html><body>You are either not carrying out your quest or don't meet the criteria.</body></html>"
    st = player.getQuestState(qn)
    if not st : return htmltext
    npcId = npc.getNpcId()
    cond = st.getInt("cond")
    id = st.getState()
    level = player.getLevel()
    if npcId == GABRIELLE :
         if id == CREATED :
             if level>=50 :
                 htmltext = "30753-03.htm"
             else :
                 htmltext = "30753-01.htm"
         elif cond == 1 :
             if st.getInt("all") == 0 :
                 htmltext = "30753-07.htm"
             else :
                 htmltext = "30753-09.htm"
         elif cond == 2 :
             if st.getInt("all") < 2 :
                 htmltext = "30753-11.htm"
             else :
                 htmltext = "30753-12.htm"
                 st.takeItems(MARK_OF_WATCHMAN,-1)
                 st.takeItems(FEATHER_OF_GABRIELLE,-1)
                 st.giveItems(HERALD_OF_SLAYER,1)
                 st.set("cond","3")
         elif cond == 3 :
             htmltext = "30753-13.htm"
         elif cond == 4 :
             htmltext = "30753-14.htm"
    if npcId == CHAKIRIS :
         if st.getInt("all") == 1 : #if all 4 tasks have been done
             htmltext = "30705-04.htm"
         elif st.getInt("chakiris")== 1 : #if not all 4 are done
             htmltext = "30705-03.htm"
         elif cond == 1:
             if st.getQuestItemsCount(HAMRUT_LEG)==0 or st.getQuestItemsCount(KRANROT_SKIN)==0 :
                 htmltext = "30705-01.htm"
             else :
                 st.giveItems(MARK_OF_WATCHMAN,1)
                 st.takeItems(HAMRUT_LEG,-1)
                 st.takeItems(KRANROT_SKIN,-1)
                 htmltext = "30705-02.htm"
                 st.set("chakiris","1")
                 checkCond(st)
    if npcId == KAIENA :
         if st.getInt("all") == 1 : #if all 4 tasks have been done
             htmltext = "30720-04.htm"
         elif st.getInt("kaiena")== 1 : #if not all 4 are done
             htmltext = "30720-03.htm"
         elif cond == 1:
             if st.getQuestItemsCount(MARSH_STALKER_HORN)==0 or st.getQuestItemsCount(MARSH_DRAKE_TALONS)==0 :
                 htmltext = "30720-01.htm"
             else :
                 st.giveItems(MARK_OF_WATCHMAN,1)
                 st.takeItems(MARSH_STALKER_HORN,-1)
                 st.takeItems(MARSH_DRAKE_TALONS,-1)
                 htmltext = "30720-02.htm"
                 st.set("kaiena","1")
                 checkCond(st)
    if npcId == KENDRA :
         if st.getInt("all") == 1 : #if all 4 tasks have been done
             htmltext = "30851-04.htm"
         elif st.getInt("kendra")== 1 : #if not all 4 are done
             htmltext = "30851-03.htm"
         elif cond == 1:
             if st.getQuestItemsCount(TOTEM_OF_LAND_DRAGON)==0 :
                 htmltext = "30851-01.htm"
             else :
                 st.giveItems(MARK_OF_WATCHMAN,1)
                 st.takeItems(TOTEM_OF_LAND_DRAGON,-1)
                 htmltext = "30851-02.htm"
                 st.set("kendra","1")
                 checkCond(st)
    if npcId == ORVEN :
         if st.getInt("all") == 1 : #if all 4 tasks have been done
             htmltext = "30857-04.htm"
         elif st.getInt("orven")== 1 : #if not all 4 are done
             htmltext = "30857-03.htm"
         elif cond == 1:
             if st.getQuestItemsCount(REMAINS_OF_SACRIFICED)==0 :
                 htmltext = "30857-01.htm"
             else :
                 st.giveItems(MARK_OF_WATCHMAN,1)
                 st.takeItems(REMAINS_OF_SACRIFICED,-1)
                 htmltext = "30857-02.htm"
                 st.set("orven","1")
                 checkCond(st)
    if npcId == MOKE :
         if st.getInt("all") == 2 :
             htmltext = "30498-05.htm"
         elif st.getInt("moke") == 1 :
             htmltext = "30498-04.htm"
         elif cond == 2 :
             if st.getQuestItemsCount(MARA_FANG) == 0 or st.getQuestItemsCount(FIRST_FRAGMENT_OF_ABYSS_JEWEL) == 0 :
                 htmltext = "30498-01.htm"
             else :
                 htmltext = "30498-03.htm"
                 st.giveItems(MARK_OF_WATCHMAN,1)
                 st.takeItems(MARA_FANG,-1)
                 st.takeItems(FIRST_FRAGMENT_OF_ABYSS_JEWEL,-1)
                 if st.getInt("helton") == 1 :
                     st.set("all","2")
                 else :
                     st.set("moke","1")
    if npcId == HELTON :
         if st.getInt("all") == 2 :
             htmltext = "30678-05.htm"
         elif st.getInt("helton") == 1 :
             htmltext = "30678-04.htm"
         elif cond == 2 :
             if st.getQuestItemsCount(MUSFEL_FANG) == 0 or st.getQuestItemsCount(SECOND_FRAGMENT_OF_ABYSS_JEWEL) == 0 :
                 htmltext = "30678-01.htm"
             else :
                 htmltext = "30678-03.htm"
                 st.giveItems(MARK_OF_WATCHMAN,1)
                 st.takeItems(MUSFEL_FANG,-1)
                 st.takeItems(SECOND_FRAGMENT_OF_ABYSS_JEWEL,-1)
                 if st.getInt("moke") == 1 :
                     st.set("all","2")
                 else :
                     st.set("helton","1")
    if npcId == GILMORE :
         if cond < 3 :
             htmltext = "30754-01.htm"
         elif cond == 3 and st.getQuestItemsCount(HERALD_OF_SLAYER)==1 :
             htmltext = "30754-02.htm"
         elif cond==4 :
             if st.getQuestItemsCount(THIRD_FRAGMENT_OF_ABYSS_JEWEL)==1 :
                 htmltext = "30754-05.htm"
             else :
                 htmltext = "30754-04.htm"
    if npcId == THEODRIC :
         if cond<3 :
             htmltext = "30755-01.htm"
         elif cond==3 and st.getQuestItemsCount(HERALD_OF_SLAYER)==1 :
             htmltext = "30755-02.htm"
         elif cond==4 :
             if st.getQuestItemsCount(THIRD_FRAGMENT_OF_ABYSS_JEWEL) == 0 :
                 htmltext = "30755-03.htm"
             else :
                 htmltext = "30755-04.htm"
    return htmltext

 def onAttack (self, npc, player, damage, isPet):
   st = player.getQuestState(qn)
   if st :
     npcId = npc.getNpcId()
     maxHp = npc.getMaxHp()
     nowHp = npc.getStatus().getCurrentHp()
     cond = st.getInt("cond")
     if npcId == ABYSS_JEWEL1 :
         if cond == 2 and st.getInt("moke")<>1:
             if nowHp < maxHp*0.8 and st.getInt("aspawned")<>1 :
                 for i in range(10):
                    st.addSpawn(GUARDIAN1,-81260+st.getRandom(-75, 75),75639+st.getRandom(-75, 70),-3300,180000)
                    st.addSpawn(GUARDIAN1,-81240+st.getRandom(100),75639+st.getRandom(80),-3300,180000)
                 st.set("aspawned","1")
                 self.startQuestTimer("Jewel1_Timer1",900000,npc,None)
             elif nowHp < maxHp*0.4 and st.getQuestItemsCount(FIRST_FRAGMENT_OF_ABYSS_JEWEL)==0 :
                 st.giveItems(FIRST_FRAGMENT_OF_ABYSS_JEWEL,1)
                 st.playSound("ItemSound.quest_itemget")
                 self.startQuestTimer("Jewel1_Timer2",240000,npc,None)
         if nowHp < maxHp*0.1 :
             npc.decayMe()
             self.cancelQuestTimer("Jewel1_Timer1",npc,None)
             self.cancelQuestTimer("Jewel1_Timer2",npc,None)
     if npcId == ABYSS_JEWEL2 :
         if cond == 2 and st.getInt("helton")<>1:
             if nowHp < maxHp*0.8 and st.getInt("bspawned")<>1 :
                 for i in range(0,70,7) :
                    st.addSpawn(GUARDIAN2,63766+i,31139,-3400,180000)
                    st.addSpawn(GUARDIAN2,63706,31139+i,-3400,180000)
                 st.set("bspawned","1")
                 self.startQuestTimer("Jewel2_Timer1",900000,npc,None)
             elif nowHp < maxHp*0.4 and st.getQuestItemsCount(SECOND_FRAGMENT_OF_ABYSS_JEWEL)==0 :
                 st.giveItems(SECOND_FRAGMENT_OF_ABYSS_JEWEL,1)
                 st.playSound("ItemSound.quest_itemget")
                 self.startQuestTimer("Jewel2_Timer2",240000,npc,None)
         if nowHp < maxHp*0.1 :
             npc.decayMe()
             self.cancelQuestTimer("Jewel2_Timer1",npc,None)
             self.cancelQuestTimer("Jewel2_Timer2",npc,None)
     if npcId == ABYSS_JEWEL3 :
         if cond == 4 :
             if nowHp < maxHp*0.8 and st.getInt("cspawned")<>1 :
                 for i in range(1,5) :
                    st.addSpawn(GUARDIAN3, npc.getX()+st.getRandom(-100, 100), npc.getY()+st.getRandom(-100, 100), npc.getZ(),180000)
                 st.set("cspawned","1")
             elif nowHp < maxHp*0.4 and st.getQuestItemsCount(THIRD_FRAGMENT_OF_ABYSS_JEWEL)==0 :
                 st.giveItems(THIRD_FRAGMENT_OF_ABYSS_JEWEL,1)
                 st.playSound("ItemSound.quest_itemget")
         if nowHp < maxHp*0.1 :
             npc.decayMe()
     return

 def onKill(self,npc,player,isPet):
    npcId = npc.getNpcId()
    st = player.getQuestState(qn)
    if st :
        cond = st.getInt("cond")
        if cond == 1 :
            if npcId == HAMRUT and st.getQuestItemsCount(HAMRUT_LEG)==0 and st.getInt("chakiris") == 0 :
                st.giveItems(HAMRUT_LEG,1)
                st.playSound("ItemSound.quest_itemget")
            elif npcId == KRANROT and st.getQuestItemsCount(KRANROT_SKIN)==0 and st.getInt("chakiris") == 0 :
                st.giveItems(KRANROT_SKIN,1)
                st.playSound("ItemSound.quest_itemget")
            elif npcId == MARSH_STALKER and st.getQuestItemsCount(MARSH_STALKER_HORN)==0 and st.getInt("kaiena") == 0 :
                st.giveItems(MARSH_STALKER_HORN,1)
                st.playSound("ItemSound.quest_itemget")
            elif npcId == MARSH_DRAKE and st.getQuestItemsCount(MARSH_DRAKE_TALONS)==0 and st.getInt("kaiena") == 0 :
                st.giveItems(MARSH_DRAKE_TALONS,1)
                st.playSound("ItemSound.quest_itemget")
            elif npcId in (BLOODY_QUEEN, BLOODY_QUEEN2) and st.getQuestItemsCount(REMAINS_OF_SACRIFICED)==0 and st.getInt("orven")== 0 :
                for i in range(8) :
                    st.addSpawn(SACRIFICE_OF_THE_SACRIFICED, player.getX()+st.getRandom(-300, 300), player.getY()+st.getRandom(-300, 300), player.getZ(), 180000)
            elif npcId == SACRIFICE_OF_THE_SACRIFICED and st.getQuestItemsCount(REMAINS_OF_SACRIFICED)==0 and st.getInt("orven")== 0 :
                st.giveItems(REMAINS_OF_SACRIFICED,1)
                st.playSound("ItemSound.quest_itemget")
            elif npcId == HARIT_LIZARDMAN_SHAMAN and st.getRandom(5) == 0 and st.getQuestItemsCount(TOTEM_OF_LAND_DRAGON)==0 and st.getInt("kendra")== 0 :
                for i in range(3) :
                    st.addSpawn(HARIT_LIZARDMAN_ZEALOT,player.getX()+st.getRandom(-300, 300), player.getY()+st.getRandom(-300, 300), player.getZ(),180000)
            elif npcId == HARIT_LIZARDMAN_ZEALOT and st.getQuestItemsCount(TOTEM_OF_LAND_DRAGON)==0 and st.getInt("kendra")== 0 :
                st.giveItems(TOTEM_OF_LAND_DRAGON,1)
                st.playSound("ItemSound.quest_itemget")
        elif cond == 2 :
            if npcId == GUARDIAN1 and st.getQuestItemsCount(MARA_FANG)==0 and st.getInt("moke")<>1 :
                st.giveItems(MARA_FANG,1)
                st.playSound("ItemSound.quest_itemget")
            elif npcId == GUARDIAN2 and st.getQuestItemsCount(MUSFEL_FANG)==0 and st.getInt("helton")<>1 :
                st.giveItems(MUSFEL_FANG,1)
                st.playSound("ItemSound.quest_itemget")
        elif cond == 4:
            if npcId in (CAVE_MAIDEN, CAVE_KEEPER, CAVE_KEEPER1, CAVE_MAIDEN1) and st.getQuestItemsCount(THIRD_FRAGMENT_OF_ABYSS_JEWEL)==0 and st.getRandom(5) == 0 :
                mob = st.addSpawn(ABYSS_JEWEL3,180000)
    elif npcId == ABYSS_JEWEL1 :
        self.cancelQuestTimer("Jewel1_Timer1",npc,None)
        self.cancelQuestTimer("Jewel1_Timer2",npc,None)
    elif npcId == ABYSS_JEWEL2 :
        self.cancelQuestTimer("Jewel2_Timer1",npc,None)
        self.cancelQuestTimer("Jewel2_Timer2",npc,None)
    return

QUEST       = Quest(337,qn,"Audience With The Land Dragon")
CREATED     = State('Start', QUEST)
STARTED     = State('Started', QUEST)

QUEST.setInitialState(CREATED)

QUEST.addStartNpc(GABRIELLE)

QUEST.addAttackId(ABYSS_JEWEL1)
QUEST.addAttackId(ABYSS_JEWEL2)
QUEST.addAttackId(ABYSS_JEWEL3)

for npc in NPCS :
    QUEST.addTalkId(npc)

for mob in MOBS :
    QUEST.addKillId(mob)