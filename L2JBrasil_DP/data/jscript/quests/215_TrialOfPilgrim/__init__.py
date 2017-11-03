# Made by Mr. Have fun! Version 0.2
# Updated by ElgarL
# Improved a lil' bit by DrLecter
# Latest update by Kerberos

import sys
from com.it.br import Config
from com.it.br.gameserver.model.quest import State
from com.it.br.gameserver.model.quest import QuestState
from com.it.br.gameserver.model.quest.jython import QuestJython as JQuest

qn = "215_TrialOfPilgrim"

MARK_OF_PILGRIM = 2721
BOOK_OF_SAGE = 2722
VOUCHER_OF_TRIAL = 2723
SPIRIT_OF_FLAME = 2724
ESSENSE_OF_FLAME = 2725
BOOK_OF_GERALD = 2726
GREY_BADGE = 2727
PICTURE_OF_NAHIR = 2728
HAIR_OF_NAHIR = 2729
STATUE_OF_EINHASAD = 2730
BOOK_OF_DARKNESS = 2731
DEBRIS_OF_WILLOW = 2732
TAG_OF_RUMOR = 2733
ADENA = 57

class Quest (JQuest) :

 def __init__(self,id,name,descr): JQuest.__init__(self,id,name,descr)

 def onEvent (self,event,st) :
    htmltext = event
    if event == "1" :
      htmltext = "30648-04.htm"
      st.set("cond","1")
      st.setState(STARTED)
      st.playSound("ItemSound.quest_accept")
      st.giveItems(VOUCHER_OF_TRIAL,1)
    elif event == "30648_1" :
          htmltext = "30648-05.htm"
    elif event == "30648_2" :
          htmltext = "30648-06.htm"
    elif event == "30648_3" :
          htmltext = "30648-07.htm"
    elif event == "30648_4" :
          htmltext = "30648-08.htm"
    elif event == "30648_5" :
          htmltext = "30648-05.htm"
    elif event == "30649_1" :
          htmltext = "30649-04.htm"
          st.giveItems(SPIRIT_OF_FLAME,1)
          st.takeItems(ESSENSE_OF_FLAME,1)
          st.set("cond","5")
          st.playSound("ItemSound.quest_middle")
    elif event == "30650_1" :
          if st.getQuestItemsCount(ADENA) >= 100000*int(Config.RATE_DROP_ADENA) :
            htmltext = "30650-02.htm"
            st.giveItems(BOOK_OF_GERALD,1)
            st.takeItems(ADENA,100000*int(Config.RATE_DROP_ADENA))
            st.set("cond","8")
            st.playSound("ItemSound.quest_middle")
          else:
            htmltext = "30650-03.htm"
    elif event == "30650_2" :
          htmltext = "30650-03.htm"
    elif event == "30362_1" :
          htmltext = "30362-05.htm"
          st.takeItems(BOOK_OF_DARKNESS,1)
          st.set("cond","16")
          st.playSound("ItemSound.quest_middle")
    elif event == "30362_2" :
          htmltext = "30362-04.htm"
          st.set("cond","16")
          st.playSound("ItemSound.quest_middle")
    elif event == "30652_1" :
          htmltext = "30652-02.htm"
          st.giveItems(BOOK_OF_DARKNESS,1)
          st.takeItems(DEBRIS_OF_WILLOW,1)
          st.set("cond","15")
          st.playSound("ItemSound.quest_middle")
    return htmltext

 def onTalk (self,npc,player):
   htmltext = "<html><body>You are either not carrying out your quest or don't meet the criteria.</body></html>"
   st = player.getQuestState(qn)
   if not st : return htmltext

   npcId = npc.getNpcId()
   id = st.getState()
   if npcId != 30648 and id != STARTED : return htmltext

   cond=st.getInt("cond")
   if npcId == 30648 and cond==0 and id == CREATED :
        if (player.getClassId().getId() in [0x0f,0x1d,0x2a,0x32]) :
           if player.getLevel() >= 35 :
              htmltext = "30648-03.htm"
           else :
              htmltext = "30648-01.htm"
              st.exitQuest(1)
        else:
          htmltext = "30648-02.htm"
          st.exitQuest(1)
   elif npcId == 30648 and cond==0 and id == COMPLETED :
      htmltext = "<html><body>This quest has already been completed.</body></html>"
   elif npcId == 30648 and cond==1 and st.getQuestItemsCount(VOUCHER_OF_TRIAL) :
      htmltext = "30648-09.htm"
   elif npcId == 30648 and cond==17 and st.getQuestItemsCount(BOOK_OF_SAGE) :
      st.addExpAndSp(77832,16000)
      st.giveItems(7562,8)
      htmltext = "30648-10.htm"
      st.giveItems(MARK_OF_PILGRIM,1)
      st.takeItems(BOOK_OF_SAGE,1)
      st.setState(COMPLETED)
      st.playSound("ItemSound.quest_finish")
      st.unset("cond")
   elif npcId == 30571 and cond==1 and st.getQuestItemsCount(VOUCHER_OF_TRIAL) :
      htmltext = "30571-01.htm"
      st.takeItems(VOUCHER_OF_TRIAL,1)
      st.set("cond","2")
      st.playSound("ItemSound.quest_middle")
   elif npcId == 30571 and cond==2 :
      htmltext = "30571-02.htm"
   elif npcId == 30571 and cond in [5,6] and st.getQuestItemsCount(SPIRIT_OF_FLAME) :
      htmltext = "30571-03.htm"
      st.set("cond","6")
      st.playSound("ItemSound.quest_middle")
   elif npcId == 30649 and cond==2 :
      htmltext = "30649-01.htm"
      st.set("cond","3")
      st.playSound("ItemSound.quest_middle")
   elif npcId == 30649 and cond==3 :
      htmltext = "30649-02.htm"
   elif npcId == 30649 and cond==4 and st.getQuestItemsCount(ESSENSE_OF_FLAME) :
      htmltext = "30649-03.htm"
   elif npcId == 30550 and cond==6 and st.getQuestItemsCount(SPIRIT_OF_FLAME) :
      htmltext = "30550-01.htm"
      st.giveItems(TAG_OF_RUMOR,1)
      st.set("cond","7")
      st.playSound("ItemSound.quest_middle")
   elif npcId == 30550 and cond==7 :
      htmltext = "30550-02.htm"
   elif npcId == 30650 and cond==7 and st.getQuestItemsCount(TAG_OF_RUMOR) :
      htmltext = st.showHtmlFile("30650-01.htm").replace("RequiredAdena", str(100000*int(Config.RATE_DROP_ADENA)))
   elif npcId == 30650 and cond>=9 and st.getQuestItemsCount(GREY_BADGE) and st.getQuestItemsCount(BOOK_OF_GERALD) :
      htmltext = "30650-04.htm"
      st.giveItems(ADENA,int(100000*Config.RATE_DROP_ADENA))
      st.takeItems(BOOK_OF_GERALD,1)
   elif npcId == 30651 and cond==7 and st.getQuestItemsCount(TAG_OF_RUMOR) :
      htmltext = "30651-01.htm"
      st.giveItems(GREY_BADGE,1)
      st.takeItems(TAG_OF_RUMOR,1)
      st.set("cond","9")
      st.playSound("ItemSound.quest_middle")
   elif npcId == 30651 and cond==8 and st.getQuestItemsCount(TAG_OF_RUMOR) :
      htmltext = "30651-02.htm"
      st.giveItems(GREY_BADGE,1)
      st.takeItems(TAG_OF_RUMOR,1)
      st.set("cond","9")
      st.playSound("ItemSound.quest_middle")
   elif npcId == 30651 and cond==9 :
      htmltext = "30651-03.htm"
   elif npcId == 30117 and cond==8 :
      htmltext = "30117-01.htm"
      st.set("cond","9")
      st.playSound("ItemSound.quest_middle")
   elif npcId == 30117 and cond==9 :
      htmltext = "30117-02.htm"
   elif npcId == 30036 and cond==9 :
      htmltext = "30036-01.htm"
      st.giveItems(PICTURE_OF_NAHIR,1)
      st.set("cond","10")
      st.playSound("ItemSound.quest_middle")
   elif npcId == 30036 and cond==10 :
      htmltext = "30036-02.htm"
   elif npcId == 30036 and cond==11 :
      htmltext = "30036-03.htm"
      st.giveItems(STATUE_OF_EINHASAD,1)
      st.takeItems(PICTURE_OF_NAHIR,1)
      st.takeItems(HAIR_OF_NAHIR,1)
      st.set("cond","12")
      st.playSound("ItemSound.quest_middle")
   elif npcId == 30036 and cond==12 and st.getQuestItemsCount(STATUE_OF_EINHASAD) :
      htmltext = "30036-04.htm"
   elif npcId == 30362 and cond==12 :
      htmltext = "30362-01.htm"
      st.set("cond","13")
      st.playSound("ItemSound.quest_middle")
   elif npcId == 30362 and cond==13 :
      htmltext = "30362-02.htm"
   elif npcId == 30362 and cond==15 and st.getQuestItemsCount(BOOK_OF_DARKNESS) :
      htmltext = "30362-03.htm"
   elif npcId == 30362 and cond==16 :
      htmltext = "30362-06.htm"
   elif npcId == 30362 and cond==15 and st.getQuestItemsCount(BOOK_OF_DARKNESS)==0 :
      htmltext = "30362-07.htm"
   elif npcId == 30652 and cond==14 and st.getQuestItemsCount(DEBRIS_OF_WILLOW) :
      htmltext = "30652-01.htm"
   elif npcId == 30652 and cond==15 and st.getQuestItemsCount(BOOK_OF_DARKNESS) :
      htmltext = "30652-03.htm"
   elif npcId == 30612 and cond==16 :
      htmltext = "30612-01.htm"
      st.giveItems(BOOK_OF_SAGE,1)
      if st.getQuestItemsCount(BOOK_OF_DARKNESS) :
        st.takeItems(BOOK_OF_DARKNESS,1)
      st.set("cond","17")
      st.playSound("ItemSound.quest_middle")
      st.takeItems(GREY_BADGE,1)
      st.takeItems(SPIRIT_OF_FLAME,1)
      st.takeItems(STATUE_OF_EINHASAD,1)
   elif npcId == 30612 and cond==17 :
      htmltext = "30612-02.htm"
   return htmltext

 def onKill(self,npc,player,isPet):
   st = player.getQuestState(qn)
   if not st : return 
   if st.getState() != STARTED : return 
   npcId = npc.getNpcId()
   cond=st.getInt("cond")
   if npcId == 27116 :
      if cond == 3 and not st.getQuestItemsCount(ESSENSE_OF_FLAME) :
        if not st.getRandom(5) :
          st.giveItems(ESSENSE_OF_FLAME,1)
          st.set("cond","4")
          st.playSound("ItemSound.quest_middle")
   elif npcId == 27117 :
      if cond == 10 and not st.getQuestItemsCount(HAIR_OF_NAHIR) :
        st.giveItems(HAIR_OF_NAHIR,1)
        st.set("cond","11")
        st.playSound("ItemSound.quest_middle")
   elif npcId == 27118 :
      if cond == 13 and not st.getQuestItemsCount(DEBRIS_OF_WILLOW) :
        if not st.getRandom(5) :
          st.giveItems(DEBRIS_OF_WILLOW,1)
          st.set("cond","14")
          st.playSound("ItemSound.quest_middle")
   return

QUEST       = Quest(215,qn,"Trial Of Pilgrim")
CREATED     = State('Start', QUEST)
STARTING     = State('Starting', QUEST)
STARTED     = State('Started', QUEST)
COMPLETED   = State('Completed', QUEST)

QUEST.setInitialState(CREATED)
QUEST.addStartNpc(30648)

for npcId in [30648,30036,30117,30362,30550,30571,30612,30649,30650,30651,30652] :
  QUEST.addTalkId(npcId)
    
for mobId in range(27116,27119):
    QUEST.addKillId(mobId)