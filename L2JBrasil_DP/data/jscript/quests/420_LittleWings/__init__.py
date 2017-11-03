# version 0.1 
# by DrLecter
import sys
from com.it.br import Config
from com.it.br.gameserver.model.quest import State
from com.it.br.gameserver.model.quest import QuestState
from com.it.br.gameserver.model.quest.jython import QuestJython as JQuest

qn = "420_LittleWings"

# variables section
REQUIRED_EGGS = 20

#Drop rates in %
BACK_DROP = 30
EGG_DROP  = 50

#Quest items
FRY_STN,FRY_STN_DLX,FSN_LIST,FSN_LIST_DLX,TD_BCK_SKN,JUICE,SCALE_1,EX_EGG,\
SCALE_2,ZW_EGG,SCALE_3,KA_EGG,SCALE_4,SU_EGG,SCALE_5,SH_EGG,FRY_DUST = range(3816,3832)+[3499]

#NPCs
PM_COOPER,SG_CRONOS,GD_BYRON,MC_MARIA,FR_MYMYU = 30829,30610,30711,30608,30747
DK_EXARION,DK_ZWOV,DK_KALIBRAN,WM_SUZET,WM_SHAMHAI = range(30748,30753)

#mobs
TD_LORD   = 20231 #toad lord
LO_LZRD_W = 20580 #exarion's
MS_SPIDER = 20233 #zwov's
RD_SCVNGR = 20551 #kalibran's
BO_OVERLD = 20270 #suzet's
DD_SEEKER = 20202 #shamhai's

#Rewards
FOOD        = 4038
ARMOR       = 3912

# helper functions section
def check_level(st) :
  if st.getPlayer().getLevel() < 35 :
    st.exitQuest(True)
    return "420_low_level.htm"
  return "Start.htm"

def check_stone(st,progress) :
    if st.getQuestItemsCount(FRY_STN) == 1 :
       st.set("cond","3") 
       if progress == 1 :
          st.set("progress","3")
          return "420_cronos_8.htm"
       elif progress == 8 :
          st.set("progress","10")
          return "420_cronos_14.htm"
    elif st.getQuestItemsCount(FRY_STN_DLX) == 1 :
       if progress == 2 :
          st.set("progress","4")
          return "420_cronos_8.htm"
       elif progress == 9 :
          st.set("progress","11")
          return "420_cronos_14.htm"
    else :
       return "420_cronos_7.htm"

def check_elements(st,progress) :
  coal  = st.getQuestItemsCount(1870)
  char  = st.getQuestItemsCount(1871)
  gemd  = st.getQuestItemsCount(2130)
  gemc  = st.getQuestItemsCount(2131)
  snug  = st.getQuestItemsCount(1873)
  sofp  = st.getQuestItemsCount(1875)
  tdbk  = st.getQuestItemsCount(TD_BCK_SKN)
  if progress in [1,8] :
     if coal >= 10 and char >= 10 and gemd >= 1 and snug >= 3 and tdbk >= 10 :
        return "420_maria_2.htm"
     else :
        return "420_maria_1.htm"
  elif progress in [2,9] :
     if coal >= 10 and char >= 10 and gemc >= 1 and snug >= 5 and sofp >= 1 and tdbk >= 20 :
        return "420_maria_4.htm"
     else :
        return "420_maria_1.htm"


def craft_stone(st,progress) :
    if progress in [1,8]:
       st.takeItems(1870,10)
       st.takeItems(1871,10)
       st.takeItems(2130,1)
       st.takeItems(1873,3)
       st.takeItems(TD_BCK_SKN,10)
       st.takeItems(FSN_LIST,1) 
       st.giveItems(FRY_STN,1)
       st.playSound("ItemSound.quest_itemget")
       return "420_maria_3.htm"
    elif progress in [2,9]:
       st.takeItems(1870,10)
       st.takeItems(1871,10)
       st.takeItems(2131,1)
       st.takeItems(1873,5)
       st.takeItems(1875,1)
       st.takeItems(TD_BCK_SKN,20)
       st.takeItems(FSN_LIST_DLX,1) 
       st.giveItems(FRY_STN_DLX,1)
       st.playSound("ItemSound.quest_itemget")
       return "420_maria_5.htm"

def check_eggs(st, npc, progress) :
    whom = int(st.get("dragon"))
    if   whom == 1 : eggs = EX_EGG
    elif whom == 2 : eggs = ZW_EGG
    elif whom == 3 : eggs = KA_EGG
    elif whom == 4 : eggs = SU_EGG
    elif whom == 5 : eggs = SH_EGG
    if npc == "mymyu" :
       if progress in [19,20] and st.getQuestItemsCount(eggs) == 1 :
          return "420_"+npc+"_10.htm"
       else :
          if st.getQuestItemsCount(eggs) >= 20 :
             return "420_"+npc+"_9.htm"
          else : 
             return "420_"+npc+"_8.htm"
    elif npc == "exarion" and whom == 1 :
       if st.getQuestItemsCount(eggs) < 20 :
          return "420_"+npc+"_3.htm"
       else :
          st.takeItems(eggs,20)
          st.takeItems(SCALE_1,1)
          if progress in [14,21] :
             st.set("progress","19")
          elif progress in [15,22] :
             st.set("progress","20") 
          st.giveItems(eggs,1)
          st.playSound("ItemSound.quest_itemget")
          st.set("cond","7")
          return "420_"+npc+"_4.htm"
    elif npc == "zwov" and whom == 2 :
       if st.getQuestItemsCount(eggs) < 20 :
          return "420_"+npc+"_3.htm"
       else :
          st.takeItems(eggs,20)
          st.takeItems(SCALE_2,1)
          if progress in [14,21] :
             st.set("progress","19")
          elif progress in [15,22] :
             st.set("progress","20") 
          st.giveItems(eggs,1)
          st.set("cond","7")
          st.playSound("ItemSound.quest_itemget")
          return "420_"+npc+"_4.htm"
    elif npc == "kalibran" and whom == 3 :
       if st.getQuestItemsCount(eggs) < 20 :
          return "420_"+npc+"_3.htm"
       else :
          st.takeItems(eggs,20)
#          st.takeItems(SCALE_3,1)
          return "420_"+npc+"_4.htm"
    elif npc == "suzet" and whom == 4 :
       if st.getQuestItemsCount(eggs) < 20 :
          return "420_"+npc+"_4.htm"
       else :
          st.takeItems(eggs,20)
          st.takeItems(SCALE_4,1)
          if progress in [14,21] :
             st.set("progress","19")
          elif progress in [15,22] :
             st.set("progress","20") 
          st.giveItems(eggs,1)
          st.set("cond","7")
          st.playSound("ItemSound.quest_itemget")
          return "420_"+npc+"_5.htm"
    elif npc == "shamhai" and whom == 5 :
       if st.getQuestItemsCount(eggs) < 20 :
          return "420_"+npc+"_3.htm"
       else :
          st.takeItems(eggs,20)
          st.takeItems(SCALE_5,1)
          if progress in [14,21] :
             st.set("progress","19")
          elif progress in [15,22] :
             st.set("progress","20") 
          st.giveItems(eggs,1)
          st.set("cond","7")
          st.playSound("ItemSound.quest_itemget")
          return "420_"+npc+"_4.htm"
    return "check_eggs sux"


# Main Quest Code
class Quest (JQuest):
  def __init__(self,id,name,descr): JQuest.__init__(self,id,name,descr)
  
  def onEvent (self,event,st):
    id   = st.getState()
    progress = st.getInt("progress")
    if id == CREATED :
      st.set("cond","0")
      if event == "ido" :
         st.setState(STARTING)
         st.set("progress","0")
         st.set("cond","1")
         st.set("dragon","0")
         st.playSound("ItemSound.quest_accept")
         return "Starting.htm"
    elif id == STARTING :
         if event == "wait" :
            return craft_stone(st,progress)
         elif event == "cronos_2" :
            return "420_cronos_2.htm" 
         elif event == "cronos_3" :
            return "420_cronos_3.htm" 
         elif event == "cronos_4" :
            return "420_cronos_4.htm" 
         elif event == "fsn" :
            st.set("cond","2") 
            if progress == 0:
               st.set("progress","1")
               st.giveItems(FSN_LIST,1)
               st.playSound("ItemSound.quest_itemget")
               return "420_cronos_5.htm"
            elif progress == 7:
               st.set("progress","8")
               st.giveItems(FSN_LIST,1)
               st.playSound("ItemSound.quest_itemget")
               return "420_cronos_12.htm"
         elif event == "fsn_dlx" :
            st.set("cond","2") 
            if progress == 0:
               st.set("progress","2")
               st.giveItems(FSN_LIST_DLX,1)
               st.playSound("ItemSound.quest_itemget")
               return "420_cronos_6.htm"
            if progress == 7:
               st.set("progress","9")
               st.giveItems(FSN_LIST_DLX,1)
               st.playSound("ItemSound.quest_itemget")
               return "420_cronos_13.htm"
         elif event == "showfsn" :
            return "420_byron_2.htm"
         elif event == "askmore" :
            st.set("cond","4") 
            if progress == 3 :
               st.set("progress","5")
               return "420_byron_3.htm"
            elif progress == 4 :
               st.set("progress","6")
               return "420_byron_4.htm"
         elif event == "give_fsn" :
            st.takeItems(FRY_STN,1)
            return "420_mymyu_2.htm"
         elif event == "give_fsn_dlx" :
            st.takeItems(FRY_STN_DLX,1)
            st.giveItems(FRY_DUST,1)
            st.playSound("ItemSound.quest_itemget")
            return "420_mymyu_4.htm"
         elif event == "fry_ask" :
            return "420_mymyu_5.htm"
         elif event == "ask_abt" :
            st.setState(STARTED)
            st.set("cond","5")
            st.giveItems(JUICE,1)
            st.playSound("ItemSound.quest_itemget")
            return "420_mymyu_6.htm"
    elif id == STARTED :
         if event == "exarion_1" :
             st.giveItems(SCALE_1,1)
             st.playSound("ItemSound.quest_itemget")
             st.set("dragon","1")
             st.set("cond","6")
             st.set("progress",str(progress+9)) 
             return "420_exarion_2.htm"
         elif event == "kalibran_1" :
             st.set("dragon","3")
             st.set("cond","6")
             st.giveItems(SCALE_3,1)
             st.playSound("ItemSound.quest_itemget")
             st.set("progress",str(progress+9))
             return "420_kalibran_2.htm"
         elif event == "kalibran_2" :
            if st.getQuestItemsCount(SCALE_3) :
             if progress in [14,21] :
                st.set("progress","19")
             elif progress in [15,22] :
                st.set("progress","20")
             st.takeItems(SCALE_3,1)   
             st.giveItems(KA_EGG,1)
             st.set("cond","7")
             st.playSound("ItemSound.quest_itemget")
             return "420_kalibran_5.htm"
         elif event == "zwov_1" :
             st.set("dragon","2")
             st.set("cond","6")
             st.giveItems(SCALE_2,1)
             st.playSound("ItemSound.quest_itemget")
             st.set("progress",str(progress+9))
             return "420_zwov_2.htm"
         elif event == "shamhai_1" :
             st.set("dragon","5")
             st.set("cond","6")
             st.giveItems(SCALE_5,1)
             st.playSound("ItemSound.quest_itemget")
             st.set("progress",str(progress+9))
             return "420_shamhai_2.htm"
         elif event == "suzet_1" :
             return "420_suzet_2.htm"
         elif event == "suzet_2" :
             st.set("dragon","4")
             st.set("cond","6")
             st.giveItems(SCALE_4,1)
             st.playSound("ItemSound.quest_itemget")
             st.set("progress",str(progress+9))
             return "420_suzet_3.htm"
         elif event == "hatch" :
             whom = int(st.get("dragon"))
             if   whom == 1 : eggs = EX_EGG
             elif whom == 2 : eggs = ZW_EGG
             elif whom == 3 : eggs = KA_EGG
             elif whom == 4 : eggs = SU_EGG
             elif whom == 5 : eggs = SH_EGG
             if st.getQuestItemsCount(eggs) and progress in [19,20] :
              st.takeItems(eggs,1)
              st.set("cond","8")
              if progress == 19 :
                 st.giveItems(3500+st.getRandom(3),1)
                 st.exitQuest(True)
                 st.playSound("ItemSound.quest_finish")
                 return "420_mymyu_15.htm"
              elif progress == 20 :
                 return "420_mymyu_11.htm"
         elif event == "give_dust" :
             if st.getQuestItemsCount(FRY_DUST):
              st.takeItems(FRY_DUST,1)
              luck = st.getRandom(2)
              if luck == 0 :
                 extra = ARMOR
                 qty = 1
                 htmltext = "420_mymyu_13.htm"
              else :
                 extra = FOOD
                 qty = 100
                 htmltext = "420_mymyu_14.htm"
              st.giveItems(3500+st.getRandom(3),1)
              st.giveItems(extra,qty)
              st.exitQuest(True)
              st.playSound("ItemSound.quest_finish")
              return htmltext
         elif event == "no_dust" :
              st.giveItems(3500+st.getRandom(3),1)
              st.exitQuest(True)
              st.playSound("ItemSound.quest_finish")
              return "420_mymyu_12.htm"


  def onTalk (self,npc,player):
    htmltext = "<html><body>You are either not carrying out your quest or don't meet the criteria.</body></html>"
    st = player.getQuestState(qn)
    if not st : return htmltext

    npcId = npc.getNpcId()
    id = st.getState()
    if id == COMPLETED:
       st.setState(CREATED)
       id = CREATED
    progress = st.getInt("progress")
    if npcId == PM_COOPER :
      if id == CREATED :
        return check_level(st)
      elif id == STARTING and progress == 0 :
        return "Starting.htm"
      else :
        return "Started.htm"
    elif npcId == SG_CRONOS :
      if id == STARTING :
         if progress == 0 :
            return "420_cronos_1.htm"
         elif progress in [ 1,2,8,9 ] :
            return check_stone(st,progress)
         elif progress in [ 3,4,10,11 ] :
            return "420_cronos_9.htm"
         elif progress in  [5,6,12,13 ]:
            return "420_cronos_11.htm"
         elif progress == 7 :
            return "420_cronos_10.htm"

    elif npcId == MC_MARIA :
      if id == STARTING :
         if ((progress in [ 1,8 ] )  and st.getQuestItemsCount(FSN_LIST)==1) or ((progress in [ 2,9 ] ) and st.getQuestItemsCount(FSN_LIST_DLX)==1):
            return check_elements(st,progress)
         elif progress in [ 3,4,5,6,7,10,11 ] :
            return "420_maria_6.htm"
    elif npcId == GD_BYRON :
       if id == STARTING :
          if ((progress in [ 1,8 ] )  and st.getQuestItemsCount(FSN_LIST)==1) or ((progress in [ 2,9 ] ) and st.getQuestItemsCount(FSN_LIST_DLX)==1):
             return "420_byron_10.htm"
          elif progress == 7 :
             return "420_byron_9.htm"
          elif (progress == 3 and st.getQuestItemsCount(FRY_STN)==1) or (progress == 4  and st.getQuestItemsCount(FRY_STN_DLX)==1):
             return "420_byron_1.htm"
          elif progress == 10  and st.getQuestItemsCount(FRY_STN)==1 :
              st.set("progress","12")
              return "420_byron_5.htm"
          elif progress == 11  and st.getQuestItemsCount(FRY_STN_DLX)==1 :
              st.set("progress","13")
              return "420_byron_6.htm"
          elif progress in [5,12] :
             return "420_byron_7.htm"
          elif progress in [6,13] :
             return "420_byron_8.htm"
    elif npcId == FR_MYMYU :
       if id == STARTING :
          if ( progress in [5,12] ) and st.getQuestItemsCount(FRY_STN) == 1 :
             return "420_mymyu_1.htm"
          elif ( progress in [6,13] ) and st.getQuestItemsCount(FRY_STN_DLX) == 1 :
             return "420_mymyu_3.htm"
       elif id == STARTED :
          if progress < 14 and st.getQuestItemsCount(JUICE) == 1  :
             return "420_mymyu_7.htm"
          elif progress > 13 :
             return check_eggs(st,"mymyu",progress)
    elif npcId == DK_EXARION :
       if id == STARTED :
          if progress in [ 5,6,12,13 ] and st.getQuestItemsCount(JUICE) == 1:
             st.takeItems(JUICE,1) 
             return "420_exarion_1.htm"
          elif progress > 13 and st.getQuestItemsCount(SCALE_1) == 1:
              return check_eggs(st,"exarion",progress)
          elif progress in [ 19,20 ] and st.getQuestItemsCount(EX_EGG) == 1 :
              return "420_exarion_5.htm"
    elif npcId == DK_ZWOV :
       if id == STARTED :
          if progress in [ 5,6,12,13 ]  and st.getQuestItemsCount(JUICE) == 1:
             st.takeItems(JUICE,1)  
             return "420_zwov_1.htm"
          elif progress > 13 and st.getQuestItemsCount(SCALE_2) == 1:
              return check_eggs(st,"zwov",progress)
          elif progress in [ 19,20 ] and st.getQuestItemsCount(ZW_EGG) == 1 :
              return "420_zwov_5.htm"
    elif npcId == DK_KALIBRAN :
       if id == STARTED :
          if progress in [ 5,6,12,13 ] and st.getQuestItemsCount(JUICE) == 1:
             st.takeItems(JUICE,1)  
             return "420_kalibran_1.htm"
          elif progress > 13 and st.getQuestItemsCount(SCALE_3) == 1:
              return check_eggs(st,"kalibran",progress)
          elif progress in [ 19,20 ] and st.getQuestItemsCount(KA_EGG) == 1 :
              return "420_kalibran_6.htm"
    elif npcId == WM_SUZET :
       if id == STARTED :
          if progress in [ 5,6,12,13 ] and st.getQuestItemsCount(JUICE) == 1:
             st.takeItems(JUICE,1)  
             return "420_suzet_1.htm"
          elif progress > 13 and st.getQuestItemsCount(SCALE_4) == 1:
              return check_eggs(st,"suzet",progress)
          elif progress in [ 19,20 ] and st.getQuestItemsCount(SU_EGG) == 1 :
              return "420_suzet_6.htm"
    elif npcId == WM_SHAMHAI :
       if id == STARTED :
          if progress in [ 5,6,12,13 ] and st.getQuestItemsCount(JUICE) == 1:
             st.takeItems(JUICE,1)  
             return "420_shamhai_1.htm"
          elif progress > 13 and st.getQuestItemsCount(SCALE_5) == 1:
              return check_eggs(st,"shamhai",progress)
          elif progress in [ 19,20 ] and st.getQuestItemsCount(SH_EGG) == 1 :
              return "420_shamhai_5.htm"
    return "<html><body>I have nothing to say to you</body></html>"

  def onKill(self,npc,player,isPet):
    st = player.getQuestState(qn)
    if not st : return 
   
    id   = st.getState()
    npcId = npc.getNpcId()
  #incipios drop
    skins = st.getQuestItemsCount(TD_BCK_SKN)
    if id == STARTING and (st.getQuestItemsCount(FSN_LIST) == 1 and skins < 10) or (st.getQuestItemsCount(FSN_LIST_DLX) == 1 and skins < 20) :
      if npcId ==  TD_LORD :
        count = 0
        if st.getQuestItemsCount(FSN_LIST) == 1 :
           count = 10
        else :
           count = 20
        numItems, chance = divmod(BACK_DROP*Config.RATE_DROP_QUEST,100)
        if st.getRandom(100) <= chance :
          numItems += 1
        numItems = int(numItems)
        if numItems != 0 :
          if count <= (skins + numItems) :
              numItems = count - skins
              st.playSound("ItemSound.quest_middle")
          else :
              st.playSound("ItemSound.quest_itemget")
          st.giveItems(TD_BCK_SKN,numItems)
  #dragon detection
    elif id == STARTED and (st.get("progress") in [ "14","15","21","22" ]) :
      whom = int(st.get("dragon"))
      if whom == 1 :
         eggs = EX_EGG
         scale = SCALE_1
         eggdropper = LO_LZRD_W
      elif whom == 2 :
         eggs = ZW_EGG
         scale = SCALE_2
         eggdropper = MS_SPIDER
      elif whom == 3 :
         eggs = KA_EGG
         scale = SCALE_3
         eggdropper = RD_SCVNGR
      elif whom == 4 :
         eggs = SU_EGG
         scale = SCALE_4
         eggdropper = BO_OVERLD
      elif whom == 5 :
         eggs = SH_EGG
         scale = SCALE_5
         eggdropper = DD_SEEKER
      prevItems = st.getQuestItemsCount(eggs)
      if st.getQuestItemsCount(scale) == 1 and prevItems < REQUIRED_EGGS :
         if npcId == eggdropper :
            chance = EGG_DROP*Config.RATE_DROP_QUEST
            numItems, chance = divmod(chance,100)
            if st.getRandom(100) <= chance :
               numItems += 1
            numItems = int(numItems)
            if numItems != 0 :
               if REQUIRED_EGGS <= (prevItems + numItems) :
                  numItems = REQUIRED_EGGS - prevItems
                  st.playSound("ItemSound.quest_middle")
               else:
                  st.playSound("ItemSound.quest_itemget")   
               st.giveItems(eggs,numItems)                 
  #fairy stone destruction    
    elif id == STARTING and st.getQuestItemsCount(FRY_STN_DLX) == 1 :
      if npcId in range(20589,20600)+[20719]:
         st.takeItems(FRY_STN_DLX,1)
         st.set("progress","7")
         return "you lost fairy stone deluxe!"


# Quest class and state definition
QUEST       = Quest(420, qn, "Little Wings")
CREATED     = State('Start',     QUEST)
STARTING    = State('Starting',  QUEST)
STARTED     = State('Started',   QUEST)
COMPLETED   = State('Completed', QUEST)

# Quest initialization
QUEST.setInitialState(CREATED)
# Quest NPC starter initialization
QUEST.addStartNpc(PM_COOPER)

# Quest mob initialization
#back skins
QUEST.addKillId(TD_LORD)
#fairy stone dlx destroyers
for i in range(20589,20600)+[21797]:
    QUEST.addKillId(i)
#eggs
QUEST.addKillId(LO_LZRD_W)
QUEST.addKillId(RD_SCVNGR)
QUEST.addKillId(MS_SPIDER)
QUEST.addKillId(DD_SEEKER)
QUEST.addKillId(BO_OVERLD)

# Quest NPC initialization
QUEST.addTalkId(PM_COOPER)
QUEST.addTalkId(SG_CRONOS)
QUEST.addTalkId(GD_BYRON)
QUEST.addTalkId(MC_MARIA)
QUEST.addTalkId(FR_MYMYU)
for i in range(30748,30753):
    QUEST.addTalkId(i)