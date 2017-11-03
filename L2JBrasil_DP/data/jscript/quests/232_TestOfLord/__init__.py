#completely rewritten by Rolarga, original from mr
# Shadow Weapon Coupons contributed by BiTi for the Official L2J Datapack Project
# Visit http://forum.l2jdp.com for more details

import sys
from com.it.br.gameserver.model.quest import State
from com.it.br.gameserver.model.quest import QuestState
from com.it.br.gameserver.model.quest.jython import QuestJython as JQuest

qn = "232_TestOfLord"

MARK_OF_LORD            = 3390
ORDEAL_NECKLACE         = 3391
VARKEES_CHARM           = 3392
TANTUS_CHARM            = 3393
HATOS_CHARM             = 3394
TAKUNA_CHARM            = 3395
CHIANTA_CHARM           = 3396
MANAKIAS_ORDERS         = 3397
BREKA_ORC_FANG          = 3398
MANAKIAS_AMULET         = 3399
HUGE_ORC_FANG           = 3400
SUMARIS_LETTER          = 3401
URUTU_BLADE             = 3402
TIMAK_ORC_SKULL         = 3403
SWORD_INTO_SKULL        = 3404
NERUGA_AXE_BLADE        = 3405
AXE_OF_CEREMONY         = 3406
MARSH_SPIDER_FEELER     = 3407
MARSH_SPIDER_FEET       = 3408
HANDIWORK_SPIDER_BROOCH = 3409
CORNEA_OF_EN_MONSTEREYE = 3410
MONSTEREYE_WOODCARVING  = 3411
BEAR_FANG_NECKLACE      = 3412
MARTANKUS_CHARM         = 3413
RAGNA_ORC_HEAD          = 3414
RAGNA_CHIEF_NOTICE      = 3415
IMMORTAL_FLAME          = 3416
BONE_ARROW              = 1341
ADENA                   = 57
SHADOW_WEAPON_COUPON_CGRADE = 8870

NPC=[30510,30515,30558,30564,30565,30566,30567,30568,30641,30642,30643,30649]

MOBS=[20233,20269,20270,20564,20583,20584,20585,20586,20587,20588,20778,20779]

STATS=[["atubaStat","nerugaStat","urutuStat","urutuDrop","dudaStat","gandiStat","markantusStat"],["cond","phase"]]

#This handle all Dropdata for the Mobs in this Quest    npcId:[var,value,newValue,chance,maxcount,item]
DROPLIST={
20269:["atubaStat",    2,3, 40,20,BREKA_ORC_FANG ],
20270:["atubaStat",    2,3, 50,20,BREKA_ORC_FANG ],
20583:["urutuDrop",    0,1, 50,10,TIMAK_ORC_SKULL],
20584:["urutuDrop",    0,1, 55,10,TIMAK_ORC_SKULL],
20585:["urutuDrop",    0,1, 60,10,TIMAK_ORC_SKULL],
20586:["urutuDrop",    0,1, 65,10,TIMAK_ORC_SKULL],
20587:["urutuDrop",    0,1, 70,10,TIMAK_ORC_SKULL],
20588:["urutuDrop",    0,1, 75,10,TIMAK_ORC_SKULL],
20233:["dudaStat",     1,2,100,10,MARSH_SPIDER_FEELER],
20564:["gandiStat",    1,2, 90,20,CORNEA_OF_EN_MONSTEREYE],
20778:["markantusStat",1,1,100, 1,RAGNA_ORC_HEAD],
20779:["markantusStat",1,1,100, 1,RAGNA_CHIEF_NOTICE]
}   


class Quest (JQuest) :

  def __init__(self,id,name,descr): JQuest.__init__(self,id,name,descr)
  
  def onEvent (self,event,st):
    htmltext=event
    if st.getInt("phase")==0:
      if event=="1":
        st.setState(STARTED)
        st.giveItems(ORDEAL_NECKLACE,1)
        st.playSound("ItemSound.quest_accept")
        htmltext="30565-05.htm"
        for var in STATS[0]:
          st.set(var,"0")
        st.set("cond","1")
        st.set("phase","1")
    elif st.getInt("phase")==1:
      if event == "30565_1" :
        htmltext = "30565-08.htm"
        st.takeItems(SWORD_INTO_SKULL,1)
        st.takeItems(AXE_OF_CEREMONY,1)
        st.takeItems(MONSTEREYE_WOODCARVING,1)
        st.takeItems(HANDIWORK_SPIDER_BROOCH,1)
        st.takeItems(ORDEAL_NECKLACE,1)
        st.giveItems(BEAR_FANG_NECKLACE,1)
        st.takeItems(HUGE_ORC_FANG,1)
        st.set("phase","2")
      elif event == "30566_1" :
        st.set("atubaStat","1")
        st.giveItems(VARKEES_CHARM,1)
        htmltext = "30566-02.htm"
      elif event == "30567_1" :
        st.set("nerugaStat","1")
        htmltext = "30567-02.htm"
        st.giveItems(TANTUS_CHARM,1)
      elif event == "30558_1" :
        st.set("nerugaStat","2")
        htmltext = "30558-02.htm"
        st.giveItems(NERUGA_AXE_BLADE,1)
        st.takeItems(ADENA,1000)
      elif event == "30568_1" :
        st.set("urutuStat","1")
        st.set("urutuDrop","0")
        htmltext = "30568-02.htm"
        st.giveItems(HATOS_CHARM,1)
      elif event == "30641_1" :
        st.set("dudaStat","1")
        htmltext = "30641-02.htm"
        st.giveItems(TAKUNA_CHARM,1)
      elif event == "30642_1" :
        st.set("gandiStat","1")
        htmltext = "30642-02.htm"
        st.giveItems(CHIANTA_CHARM,1)
    elif st.getInt("phase")==2:
      if event == "30565_2":
        htmltext = "30565-12.htm"
        st.addExpAndSp(92955,16250)
        st.giveItems(MARK_OF_LORD,1)
        st.giveItems(SHADOW_WEAPON_COUPON_CGRADE,15)
        st.takeItems(IMMORTAL_FLAME,1)
        st.playSound("ItemSound.quest_finish")
        for var in STATS[0]:
          st.unset(var)
        for var in STATS[1]:
          st.unset(var)
        st.setState(COMPLETED)
      elif event == "30649_1" :
        htmltext = "30649-02.htm"
      elif event == "30649_2" :
        htmltext = "30649-03.htm"
      elif event == "30649_3" :
        st.set("markantusStat","1")
        htmltext = "30649-04.htm"
        st.giveItems(MARTANKUS_CHARM,1)
        st.takeItems(BEAR_FANG_NECKLACE,1)
      elif event == "30649_4" :
        htmltext = "30649-07.htm"
        st.addSpawn(30643,21036,-107690,-3038)
        st.set("markantusStat","4")
      elif event == "30643_1" :
        htmltext = "30643-02.htm"
      elif event == "30643_2" :
        htmltext = "30643-03.htm"
    return htmltext
    
  def onTalk (self,npc,player):
    htmltext = "<html><body>You are either not carrying out your quest or don't meet the criteria.</body></html>"
    st = player.getQuestState(qn)
    if not st : return htmltext

    npcId = npc.getNpcId()
    id = st.getState()
    if npcId != NPC[4] and id != STARTED : return htmltext
    
    if id == CREATED:
      for var in STATS[1]:
       st.set(var,"0")
      if npcId == NPC[4]:
        if st.getInt("cond")==0:
          if player.getRace().ordinal() != 3 :
            htmltext = "30565-01.htm"
            st.exitQuest(1)
          else:
            if player.getClassId().getId() != 0x32 :
              htmltext = "30565-02.htm"
              st.exitQuest(1)
            else:
              if player.getLevel() < 39 :
                htmltext = "30565-03.htm"
                st.exitQuest(1)
              else:
                htmltext = "30565-04.htm"
    elif id == COMPLETED:
      htmltext = "<html><body>This quest has already been completed.</body></html>"
    else:
      if st.getInt("phase") == 1:
        atuba=st.getInt("atubaStat")
        neruga=st.getInt("nerugaStat")
        urutu=st.getInt("urutuStat")
        duda=st.getInt("dudaStat")
        gandi=st.getInt("gandiStat")
#             Atuba Part
        if npcId == NPC[5]:
          if atuba==0:
            htmltext = "30566-01.htm"
          elif atuba>0 and atuba<4:
            htmltext = "30566-03.htm"
          elif atuba==4:
            st.set("atubaStat","5")
            htmltext = "30566-04.htm"
            st.takeItems(VARKEES_CHARM,1)
            st.giveItems(HUGE_ORC_FANG,1)
            st.takeItems(MANAKIAS_AMULET,1)
          elif atuba>4:
            htmltext = "30566-05.htm"
        elif npcId == NPC[1]:
          if atuba==1:
            htmltext = "30515-01.htm"
            st.giveItems(MANAKIAS_ORDERS,1)
            st.set("atubaStat","2")
          elif atuba==2:
            htmltext = "30515-02.htm"
          elif atuba==3:
            st.set("atubaStat","4")
            htmltext = "30515-03.htm"
            st.giveItems(MANAKIAS_AMULET,1)
            st.takeItems(MANAKIAS_ORDERS,1)
            st.takeItems(DROPLIST[20269][5],DROPLIST[20269][4])
          elif atuba==4:
            htmltext = "30515-04.htm"
          elif atuba==5:
            htmltext = "30515-05.htm"
#             Neruga Part
        elif npcId == NPC[6]:
          if neruga==0:
            htmltext = "30567-01.htm"
          elif neruga==1:
            htmltext = "30567-03.htm"
          elif neruga==2:
            if st.getQuestItemsCount(BONE_ARROW)>999:
              st.set("nerugaStat","3")
              st.takeItems(BONE_ARROW,1000)
              st.takeItems(NERUGA_AXE_BLADE,1)
              st.takeItems(TANTUS_CHARM,1)
              st.giveItems(AXE_OF_CEREMONY,1)
              htmltext = "30567-04.htm"
            else:
              htmltext = "30567-03.htm"
          elif neruga==3:
            htmltext = "30567-05.htm"
        elif npcId == NPC[2]:
          if neruga==1:
            if st.getQuestItemsCount(ADENA)>999:
              htmltext = "30558-01.htm"
            else:
              htmltext = "30558-03.htm"
          elif neruga==2:
            htmltext = "30558-04.htm"
#             Urutu Part
        elif npcId == NPC[7]:
          if urutu==0:
            htmltext = "30568-01.htm"
          elif urutu==3 and st.getInt("urutuDrop")==1:
            st.set("urutuStat","4")
            htmltext = "30568-04.htm"
            st.takeItems(HATOS_CHARM,1)
            st.takeItems(URUTU_BLADE,1)
            st.takeItems(DROPLIST[20587][5],DROPLIST[20587][4])
            st.giveItems(SWORD_INTO_SKULL,1)
          elif urutu>0 and urutu<4:
            htmltext = "30568-03.htm"
          elif urutu==4:
            htmltext = "30568-05.htm"
        elif npcId == NPC[3]:
          if urutu == 1:
            st.set("urutuStat","2")
            htmltext = "30564-01.htm"
            st.giveItems(SUMARIS_LETTER,1)
        elif npcId == NPC[0]:
          if urutu==2:
            st.set("urutuStat","3")
            st.giveItems(URUTU_BLADE,1)
            st.takeItems(SUMARIS_LETTER,1)
            htmltext = "30510-01.htm"
          elif urutu==3:
            htmltext = "30510-02.htm"
          elif urutu==4:
            htmltext = "30510-03.htm"
#             Duda Part
        elif npcId == NPC[8]:
          if duda==0:
            htmltext = "30641-01.htm"
          elif duda in [1,2]:
            htmltext = "30641-03.htm"
          elif duda==3:
            st.set("dudaStat","4")
            htmltext = "30641-04.htm"
            st.takeItems(DROPLIST[20233][5],DROPLIST[20233][4])
            st.takeItems(MARSH_SPIDER_FEET,st.getQuestItemsCount(MARSH_SPIDER_FEET))
            st.giveItems(HANDIWORK_SPIDER_BROOCH,1)
            st.takeItems(TAKUNA_CHARM,1)
          elif duda==4:
            htmltext = "30641-05.htm"
#             Gandi Part
        elif npcId == NPC[9]:
          if gandi==0:
            htmltext = "30642-01.htm"
          elif gandi==1:
            htmltext = "30642-03.htm"
          elif gandi==2:
            st.set("gandiStat","3")
            htmltext = "30642-04.htm"
            st.takeItems(DROPLIST[20564][5],DROPLIST[20564][4])
            st.giveItems(MONSTEREYE_WOODCARVING,1)
            st.takeItems(CHIANTA_CHARM,1)
          elif gandi==3:
            htmltext = "30642-05.htm"
#             end of phase 1  
        elif npcId == NPC[4]:
          if gandi==3 and duda==4 and urutu==4 and neruga==3 and atuba==5:
            htmltext = "30565-07.htm"
          else:
            htmltext = "30565-06.htm"
      elif st.getInt("phase")==2:
        markantus=st.getInt("markantusStat")
        if npcId == NPC[11]:
          if markantus==0:
            htmltext = "30649-01.htm"
          elif markantus==1:
            htmltext = "30649-05.htm"
          elif markantus==2:
            st.set("markantusStat","3")
            htmltext = "30649-06.htm"
            st.takeItems(MARTANKUS_CHARM,1)
            st.takeItems(RAGNA_ORC_HEAD,1)
            st.giveItems(IMMORTAL_FLAME,1)
            st.takeItems(RAGNA_CHIEF_NOTICE,1)
          elif markantus==3:
            htmltext = "30649-07.htm"
            st.addSpawn(30643,21036,-107690,-3038)
            st.set("markantusStat","4")
          elif markantus>3:
            htmltext = "30649-08.htm"
        elif npcId == NPC[10]:
          if markantus>2:
            htmltext = "30643-01.htm"
        elif npcId == NPC[4]:
          if markantus==0:
            htmltext = "30565-09.htm"
          elif markantus==1 or markantus==2:
            htmltext = "30565-10.htm"
          elif markantus>2:
            htmltext = "30565-11.htm"
    return htmltext      

  def onKill(self,npc,player,isPet):
    st = player.getQuestState(qn)
    if not st : return 
    if st.getState() != STARTED : return 

    npcId = npc.getNpcId()
    var,value,newValue,chance,maxcount,item=DROPLIST[npcId]
    random=st.getRandom(100)
    count=st.getQuestItemsCount(item)
    spiderCount=st.getQuestItemsCount(MARSH_SPIDER_FEET)
    if item == MARSH_SPIDER_FEELER and int(st.get(var)) == value:
      if spiderCount<10:
        st.giveItems(MARSH_SPIDER_FEET,1)
        st.playSound("ItemSound.quest_itemget")
      elif st.getQuestItemsCount(MARSH_SPIDER_FEELER)<9:
        st.giveItems(MARSH_SPIDER_FEELER,1)
        st.playSound("ItemSound.quest_itemget")
      elif st.getQuestItemsCount(MARSH_SPIDER_FEELER)==9:
        st.giveItems(MARSH_SPIDER_FEELER,1)
        st.playSound("ItemSound.quest_middle")
        st.set("dudaStat","3")
    elif int(st.get(var)) == value and random < chance and count < maxcount:
      st.giveItems(item,1)
      if count == maxcount-1:
        st.playSound("ItemSound.quest_middle")
        if newValue == 1 and st.getQuestItemsCount(RAGNA_ORC_HEAD) and st.getQuestItemsCount(RAGNA_CHIEF_NOTICE):
          st.set(var,"2")
        else:
          st.set(var,str(newValue))
      else:
        st.playSound("ItemSound.quest_itemget")
    return

QUEST     = Quest(232,qn,"Test Of Lord")
CREATED   = State('Start', QUEST)
STARTED   = State('Started', QUEST)
COMPLETED = State('Completed', QUEST)

QUEST.setInitialState(CREATED)
QUEST.addStartNpc(NPC[4])

for npcId in NPC:
  QUEST.addTalkId(npcId)

for mobId in MOBS:
  QUEST.addKillId(mobId)
