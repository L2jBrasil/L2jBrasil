# Maked by Mr. Have fun! Version 0.2
# rewritten by Rolarga, Version 0.3
# Shadow Weapon Coupons contributed by BiTi for the Official L2J Datapack Project
# Visit http://forum.l2jdp.com for more details

import sys
from com.it.br.gameserver.model.quest import State
from com.it.br.gameserver.model.quest import QuestState
from com.it.br.gameserver.model.quest.jython import QuestJython as JQuest

qn = "233_TestOfWarspirit"

MARK_OF_WARSPIRIT = 2879
VENDETTA_TOTEM = 2880
TAMLIN_ORC_HEAD = 2881
WARSPIRIT_TOTEM = 2882
ORIMS_CONTRACT = 2883
PORTAS_EYE = 2884
BRAKIS_REMAINS1 = 2887
HERMODTS_REMAINS1 = 2901
KIRUNAS_REMAINS1 = 2910
TONARS_REMAINS1 = 2894
BRAKIS_REMAINS2 = 2911
HERMODTS_REMAINS2 = 2913
KIRUNAS_REMAINS2 = 2914
TONARS_REMAINS2 = 2912
EXCUROS_SCALE = 2885
MORDEOS_TALON = 2886
PEKIRONS_TOTEM = 2888
TONARS_SKULL = 2889
TONARS_RIB_BONE = 2890
TONARS_SPINE = 2891
TONARS_ARM_BONE = 2892
TONARS_THIGH_BONE = 2893
MANAKIAS_TOTEM = 2895
HERMODTS_SKULL = 2896
HERMODTS_RIB_BONE = 2897
HERMODTS_SPINE = 2898
HERMODTS_ARM_BONE = 2899
HERMODTS_THIGH_BONE = 2900
RACOYS_TOTEM = 2902
KIRUNAS_SKULL = 2905
KIRUNAS_RIB_BONE = 2906
KIRUNAS_SPINE = 2907
KIRUNAS_ARM_BONE = 2908
KIRUNAS_THIGH_BONE = 2909
INSECT_DIAGRAM_BOOK = 2904
VIVIANTES_LETTER = 2903
SHADOW_WEAPON_COUPON_CGRADE = 8870

NPC=[30030,30436,30507,30510,30515,30630,30649,30682]

STATS=["cond","step","Orim","Racoy","Perkiron","Manakia","Manakia_Queen"]


#npcId=[[accepted values for this part],variable for the current part from the mob,maxcount,chance in %, items to give(one per kill max)]
DROPLIST={
20213:[[2,3,4],"Orim",10,100,[PORTAS_EYE]],
20214:[[2,3,4],"Orim",10,100,[EXCUROS_SCALE]],
20215:[[2,3,4],"Orim",10,100,[MORDEOS_TALON]],
20601:[[1],"step",13,50,[TAMLIN_ORC_HEAD]],
20602:[[1],"step",13,50,[TAMLIN_ORC_HEAD]],
27108:[[2],"Manakia_Queen",1,100,[HERMODTS_SKULL]],
20581:[[2,3,4,5,6],"Perkiron",1,50,[TONARS_RIB_BONE,TONARS_SPINE,TONARS_ARM_BONE,TONARS_SKULL,TONARS_THIGH_BONE]],
20582:[[2,3,4,5,6],"Perkiron",1,50,[TONARS_SKULL,TONARS_ARM_BONE,TONARS_RIB_BONE,TONARS_SPINE,TONARS_THIGH_BONE]],
20158:[[2,3,4,5],"Manakia",1,50,[HERMODTS_RIB_BONE,HERMODTS_SPINE,HERMODTS_ARM_BONE,HERMODTS_THIGH_BONE]],
20089:[[4,5,6,7,8,9],"Racoy",1,100,[[KIRUNAS_THIGH_BONE,KIRUNAS_ARM_BONE],[KIRUNAS_SPINE,KIRUNAS_RIB_BONE],[KIRUNAS_SKULL]]],
20090:[[4,5,6,7,8,9],"Racoy",1,100,[[KIRUNAS_THIGH_BONE,KIRUNAS_ARM_BONE],[KIRUNAS_SPINE,KIRUNAS_RIB_BONE],[KIRUNAS_SKULL]]]
}

# Mob List initialisation for the different Parts
PART2_MOBS = [20601,20602]
PART1_MOBS = []

for mob in DROPLIST.keys():
  if mob in PART2_MOBS:
    continue
  PART1_MOBS.append(mob)


class Quest (JQuest) :

  def __init__(self,id,name,descr): JQuest.__init__(self,id,name,descr)

  def onEvent (self,event,st) :
    htmltext = event
    if event == "1" :
      htmltext = "30510-05.htm"
      for var in STATS:
        st.set(var,"1")
      st.setState(PART1)
      st.playSound("ItemSound.quest_accept")
    elif event == "30630_1" :
      htmltext = "30630-02.htm"
    elif event == "30630_2" :
      htmltext = "30630-03.htm"
    elif event == "30630_3" :
      htmltext = "30630-04.htm"
      st.giveItems(ORIMS_CONTRACT,1)
      st.set("Orim","2")
    elif event == "30682_1" :
      htmltext = "30682-02.htm"
      st.giveItems(PEKIRONS_TOTEM,1)
      st.set("Perkiron","2")
    elif event == "30515_1" :
      htmltext = "30515-02.htm"
      st.giveItems(MANAKIAS_TOTEM,1)
      st.set("Manakia","2")
      st.set("Manakia_Queen","2")
    elif event == "30507_1" :
      htmltext = "30507-02.htm"
      st.giveItems(RACOYS_TOTEM,1)
      st.set("Racoy","2")
    elif event == "30030_1" :
      htmltext = "30030-02.htm"
    elif event == "30030_2" :
      htmltext = "30030-03.htm"
    elif event == "30030_3" :
      htmltext = "30030-04.htm"
      st.giveItems(VIVIANTES_LETTER,1)
      st.set("Racoy","3")
    elif event == "30649_1" :
      htmltext = "30649-02.htm"
    elif event == "30649_2" :
      st.takeItems(WARSPIRIT_TOTEM,-1)
      st.takeItems(BRAKIS_REMAINS2,-1)
      st.takeItems(HERMODTS_REMAINS2,-1)
      st.takeItems(KIRUNAS_REMAINS2,-1)
      st.addExpAndSp(63483,17500)
      st.takeItems(TONARS_REMAINS2,-1)
      st.giveItems(MARK_OF_WARSPIRIT,1)
      st.giveItems(SHADOW_WEAPON_COUPON_CGRADE,15)
      htmltext = "30649-03.htm"
      for var in STATS:
        st.unset(var)
      st.setState(COMPLETED)
      st.playSound("ItemSound.quest_finish")
    return htmltext



  def onTalk (self,npc,player):
    htmltext = "<html><body>You are either not carrying out your quest or don't meet the criteria.</body></html>"
    st = player.getQuestState(qn)
    if not st : return htmltext

    npcId = npc.getNpcId()
    id = st.getState()
    
    # first time when a player join the quest
    if id == CREATED:
      for var in STATS:
        st.set(var,"0")
      if player.getClassId().getId() == 0x32:
        if player.getLevel() > 38:
          htmltext = "30510-04.htm"
        else :
          htmltext = "30510-03.htm"
          st.exitQuest(1)
      elif player.getRace().ordinal() == 3:
        htmltext = "30510-02.htm"
        st.exitQuest(1)
      else:
        htmltext = "30510-01.htm"
        st.exitQuest(1)
      return htmltext
    # if quest is already completed
    elif id == COMPLETED:
      return "<html><body>This quest has already been completed.</body></html>"
    # if quest is accepted and in progress
    elif id == PART1:
        step=st.getInt("step")
        Orim=st.getInt("Orim")
        Racoy=st.getInt("Racoy")
        Perkiron=st.getInt("Perkiron")
        Manakia=st.getInt("Manakia")
        #Somak 
        if npcId == NPC[3]:
          if Orim == 6 and Racoy == 11 and Perkiron == 8 and Manakia == 7:          # Step 1 finished
            htmltext = "30510-07.htm"
            st.takeItems(BRAKIS_REMAINS1,1)
            st.takeItems(HERMODTS_REMAINS1,1)
            st.takeItems(KIRUNAS_REMAINS1,1)
            st.takeItems(TONARS_REMAINS1,1)
            st.giveItems(VENDETTA_TOTEM,1)
            st.setState(PART2)
          else:                                        # shows you again his List
            htmltext = "30510-06.htm"
        # Orim and his Part, he sends you out to hunt Portas, Mordeos and Excuros
        elif npcId == NPC[5]:
          if Orim == 1:
            htmltext = "30630-01.htm"
          elif Orim in [2,3,4]:
            htmltext = "30630-05.htm"
          elif Orim == 5:
            htmltext = "30630-06.htm"
            st.takeItems(ORIMS_CONTRACT,-1)
            st.takeItems(PORTAS_EYE,-1)
            st.takeItems(EXCUROS_SCALE,-1)
            st.takeItems(MORDEOS_TALON,-1)
            st.giveItems(BRAKIS_REMAINS1,1)
            st.set("Orim","6")
          else:
            htmltext = "30630-07.htm"
        # Racyos Part he sends you into the church and then to the wastelands... after wastelands he give you his item      
        elif npcId == NPC[2]:
          if Racoy == 1:
            htmltext = "30507-01.htm"
          elif Racoy == 2:
            htmltext = "30507-03.htm"
          elif Racoy == 3:
            htmltext = "30507-04.htm"
          elif 10 > Racoy > 3:
            htmltext = "30507-05.htm"
          elif Racoy == 10:
            htmltext = "30507-06.htm"
            st.takeItems(RACOYS_TOTEM,-1)
            st.takeItems(KIRUNAS_SKULL,-1)
            st.takeItems(KIRUNAS_RIB_BONE,-1)
            st.takeItems(KIRUNAS_SPINE,-1)
            st.takeItems(KIRUNAS_ARM_BONE,-1)
            st.takeItems(KIRUNAS_THIGH_BONE,-1)
            st.takeItems(INSECT_DIAGRAM_BOOK,-1)
            st.giveItems(KIRUNAS_REMAINS1,1)
            st.set("Racoy","11")
          else:
            htmltext = "30507-07.htm"
        # Racoy Part, lady in the church (Viviana)
        elif npcId == NPC[0]:
          if Racoy == 2:                # explainations
            htmltext = "30030-01.htm"
          elif Racoy == 3:              # go to sarien, hurry up
            htmltext = "30030-05.htm"
          elif 10 > Racoy > 3:             # bring more
            htmltext = "30030-06.htm"
          elif Racoy in [10,11]:           # this part is finished, for this npc
            htmltext = "30030-07.htm"
        # Racoy Part, Wastelands Trader Sarien tells: "Hunt noble ant leaders and bring the items to Racoy"
        elif npcId == NPC[1]:
          if Racoy == 3:                # explanation about hunting noble ants
            htmltext = "30436-01.htm"
            st.giveItems(INSECT_DIAGRAM_BOOK,1)
            st.takeItems(VIVIANTES_LETTER,1)
            st.set("Racoy","4")
          elif 10 > Racoy > 3:             # bring more
            htmltext = "30436-02.htm"
          elif Racoy in [10,11]:           # this part is finished, for this npc
            htmltext = "30436-03.htm"
        # Perkirons Part, just hunt Lizzardsman near Oren    
        elif npcId == NPC[7]:
          if Perkiron == 1:              # explanation
            htmltext = "30682-01.htm"
          elif Perkiron in [2,3,4,5,6]:        # bring more
            htmltext = "30682-03.htm"
          elif Perkiron == 7:            # ah you got anything i need
            htmltext = "30682-04.htm"
            st.takeItems(PEKIRONS_TOTEM,1)
            st.takeItems(TONARS_SKULL,1)
            st.takeItems(TONARS_RIB_BONE,1)
            st.takeItems(TONARS_SPINE,1)
            st.takeItems(TONARS_ARM_BONE,1)
            st.takeItems(TONARS_THIGH_BONE,1)
            st.giveItems(TONARS_REMAINS1,1)
            st.set("Perkiron","8")
          else:                    # part is finished for this npc
            htmltext = "30682-05.htm"
        # Manakias Part, hunt Medusas Steona Gorgogon Queen
        elif npcId == NPC[4]:
            if Manakia == 1:                            # explanation
              htmltext = "30515-01.htm"
            elif Manakia == 7:                            # this part is finished for this npc
              htmltext = "30515-05.htm"
            elif Manakia == 6 and st.getInt("Manakia_Queen")==3:        # ah you got both items i need
              htmltext = "30515-04.htm"
              st.takeItems(MANAKIAS_TOTEM,1)
              st.takeItems(HERMODTS_SKULL,1)
              st.takeItems(HERMODTS_RIB_BONE,1)
              st.takeItems(HERMODTS_SPINE,1)
              st.takeItems(HERMODTS_ARM_BONE,1)
              st.takeItems(HERMODTS_THIGH_BONE,1)
              st.giveItems(HERMODTS_REMAINS1,1)
              st.set("Manakia","7")  
            else:                                  # bring me more, because two vars are required , Manakia and Manakia_Queen
              htmltext = "30515-03.htm"
    elif id == PART2:
        step=st.getInt("step")
        if npcId == NPC[3]:                                
          if step == 1:                                # explain Part 2 again or bring more skulls
            htmltext = "30510-08.htm"
          elif step == 2:                                # ah you got the items i need
            htmltext = "30510-09.htm"
            st.takeItems(VENDETTA_TOTEM,1)
            st.takeItems(TAMLIN_ORC_HEAD,st.getQuestItemsCount(TAMLIN_ORC_HEAD))
            st.giveItems(WARSPIRIT_TOTEM,1)
            st.giveItems(BRAKIS_REMAINS2,1)
            st.giveItems(HERMODTS_REMAINS2,1)
            st.giveItems(KIRUNAS_REMAINS2,1)
            st.giveItems(TONARS_REMAINS2,1)
            st.set("step","3")
          else:                              # this part is finished for this npc
            htmltext = "30510-10.htm"
        elif npcId == NPC[6] and step == 3:
          htmltext = "30649-01.htm"                    # ah thx.. i will give you the mark of War Spirit
    return htmltext    
        
  def onKill(self,npc,player,isPet):
    st = player.getQuestState(qn)
    if not st : return 
    npcId=npc.getNpcId()

    if (st.getState() == PART1) and not (npcId in PART1_MOBS) : return 
    if (st.getState() == PART2) and not (npcId in PART2_MOBS) : return 

#    [accepted values for this part],variable for the current part from the mob,maxcount,chance in %, items to give(one per kill max)=DROPLIST[npcId]
    value,var,maxcount,chance,itemList=DROPLIST[npcId]
    random=st.getRandom(100)
#    return the current value of the var
    isValue = st.getInt(var)
    if st.getInt(var) in value and random<chance:
      # special part for Noble Ants
      if npcId in [20089,20090]:
        if random>70:
          list=0
        elif random>40:
          list=1
        elif random>10 and st.getQuestItemsCount(KIRUNAS_SKULL)==0:
          list=2
        else:
          return
        for item in itemList[list]:
          count = st.getQuestItemsCount(item)
          st.giveItems(item,1)
          if int(st.get(var)) < 9:
            st.set(var,str(isValue+1))
          if st.getQuestItemsCount(KIRUNAS_SKULL) and int(st.get(var))>6:
            st.set(var,"10")
            st.playSound("ItemdSound.quest_middle")
            return
          st.playSound("ItemSound.quest_itemget")
        return
      # Drop part for any other mobs
      else:    
        for item in itemList:
          count = st.getQuestItemsCount(item)
          if count<maxcount:
            st.giveItems(item,1)
            # spawns 5 new medusas around the dead queen *muha*
            if npcId == 27108:
              for i in range(5):
                st.addSpawn(20158)
            if count == maxcount-1:
              st.playSound("ItemSound.quest_middle")
              st.set(var,str(isValue+1))
            else:
              st.playSound("ItemSound.quest_itemget")
            return


QUEST     = Quest(233,qn,"Test Of Warspirit")
CREATED   = State('Start', QUEST)
PART1     = State('Part1', QUEST)
PART2     = State('Part2', QUEST)
COMPLETED = State('Completed', QUEST)


QUEST.setInitialState(CREATED)
QUEST.addStartNpc(30510)

for npcId in NPC:
  QUEST.addTalkId(npcId)

for mobId in PART1_MOBS:
  QUEST.addKillId(mobId)

for mobId in PART2_MOBS:
  QUEST.addKillId(mobId)
