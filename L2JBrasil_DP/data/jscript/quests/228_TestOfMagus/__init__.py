# Maked by Mr. Have fun! Version 0.2
# Shadow Weapon Coupons contributed by BiTi for the Official L2J Datapack Project
# Visit http://forum.l2jdp.com for more details
import sys
from com.it.br.gameserver.model.quest import State
from com.it.br.gameserver.model.quest import QuestState
from com.it.br.gameserver.model.quest.jython import QuestJython as JQuest

qn = "228_TestOfMagus"

MARK_OF_MAGUS = 2840
RUKALS_LETTER = 2841
PARINAS_LETTER = 2842
LILAC_CHARM = 2843
GOLDEN_SEED1 = 2844
GOLDEN_SEED2 = 2845
GOLDEN_SEED3 = 2846
SCORE_OF_ELEMENTS = 2847
TONE_OF_WATER = 2856
TONE_OF_FIRE = 2857
TONE_OF_WIND = 2858
TONE_OF_EARTH = 2859
UNDINE_CHARM = 2862
DAZZLING_DROP = 2848
SALAMANDER_CHARM = 2860
FLAME_CRYSTAL = 2849
SYLPH_CHARM = 2861
HARPYS_FEATHER = 2850
WYRMS_WINGBONE = 2851
WINDSUS_MANE = 2852
SERPENT_CHARM = 2863
EN_MONSTEREYE_SHELL = 2853
EN_STONEGOLEM_POWDER = 2854
EN_IRONGOLEM_SCRAP = 2855
SHADOW_WEAPON_COUPON_CGRADE = 8870

#This handels all drops from mobs.   npcId:[condition,maxcount,chance,item,part]
DROPLIST={
27095:[3,1,100,GOLDEN_SEED1,1],
27096:[3,1,100,GOLDEN_SEED2,1],
27097:[3,1,100,GOLDEN_SEED3,1],
27098:[7,5,50,FLAME_CRYSTAL,2],
20230:[7,20,30,DAZZLING_DROP,2],
20231:[7,20,30,DAZZLING_DROP,2],
20157:[7,20,30,DAZZLING_DROP,2],
20232:[7,20,40,DAZZLING_DROP,2],
20234:[7,20,50,DAZZLING_DROP,2],
20145:[7,20,50,HARPYS_FEATHER,2],
20176:[7,10,50,WYRMS_WINGBONE,2],
20553:[7,10,50,WINDSUS_MANE,2],
20564:[7,10,100,EN_MONSTEREYE_SHELL,2],
20565:[7,10,100,EN_STONEGOLEM_POWDER,2],
20566:[7,10,100,EN_IRONGOLEM_SCRAP,2]
}


class Quest (JQuest) :

 def __init__(self,id,name,descr): JQuest.__init__(self,id,name,descr)

 def onEvent (self,event,st) :
    htmltext = event
    if event == "1" :
        htmltext = "30629-04.htm"
        st.set("cond","1")
        st.setState(STARTED)
        st.playSound("ItemSound.quest_accept")
        st.giveItems(RUKALS_LETTER,1)
    elif event == "30629_1" :
          htmltext = "30629-09.htm"
    elif event == "30629_2" :
          htmltext = "30629-10.htm"
          st.takeItems(LILAC_CHARM,1)
          st.takeItems(GOLDEN_SEED1,1)
          st.takeItems(GOLDEN_SEED2,1)
          st.takeItems(GOLDEN_SEED3,1)
          st.giveItems(SCORE_OF_ELEMENTS,1)
          st.playSound("ItemSound.quest_middle")
          st.set("cond","6")
    elif event == "30391_1" :
          htmltext = "30391-02.htm"
          st.giveItems(PARINAS_LETTER,1)
          st.takeItems(RUKALS_LETTER,1)
          st.playSound("ItemSound.quest_middle")
          st.set("cond","2")
    elif event == "30612_1" :
          htmltext = "30612-02.htm"
          st.giveItems(LILAC_CHARM,1)
          st.takeItems(PARINAS_LETTER,1)
          st.playSound("ItemSound.quest_middle")
          st.set("cond","3")
    elif event == "30412_1" :
          htmltext = "30412-02.htm"
          st.giveItems(SYLPH_CHARM,1)
          st.playSound("ItemSound.quest_middle")
          st.set("cond","7")
    elif event == "30409_1" :
          htmltext = "30409-02.htm"
    elif event == "30409_2" :
          htmltext = "30409-03.htm"
          st.giveItems(SERPENT_CHARM,1)
          st.playSound("ItemSound.quest_middle")
          st.set("cond","7")
    return htmltext


 def onTalk (self,npc,player):
   htmltext = "<html><body>You are either not carrying out your quest or don't meet the criteria.</body></html>"
   st = player.getQuestState(qn)
   if not st : return htmltext

   npcId = npc.getNpcId()
   id = st.getState()
   if npcId != 30629 and id != STARTED : return htmltext

   if id == CREATED :
     st.setState(STARTING)
     st.set("cond","0")
     st.set("onlyone","0")
     st.set("id","0")
   if npcId == 30629 :
     if st.getInt("cond")==0 and st.getInt("onlyone")==0 :
        if st.getInt("cond") < 15 :
          if player.getClassId().getId() in [ 0x0b, 0x1a, 0x27] :
            if player.getLevel() < 39 :
              htmltext = "30629-02.htm"
            else:
              htmltext = "30629-03.htm"
          else:
            htmltext = "30629-01.htm"
            st.exitQuest(1)
        else:
          htmltext = "30629-01.htm"
          st.exitQuest(1)
     elif st.getInt("cond")==0 and st.getInt("onlyone")==1 :
      htmltext = "<html><body>This quest has already been completed.</body></html>"
     elif st.getInt("cond")==1:
        htmltext = "30629-05.htm"
     elif st.getInt("cond")==2:
        htmltext = "30629-06.htm"
     elif st.getInt("cond")==3:
        htmltext = "30629-07.htm"
     elif st.getInt("cond")==5:
        htmltext = "30629-08.htm"
     elif st.getInt("cond") and st.getQuestItemsCount(SCORE_OF_ELEMENTS)==1 :
        if st.getQuestItemsCount(TONE_OF_WATER) and st.getQuestItemsCount(TONE_OF_FIRE) and st.getQuestItemsCount(TONE_OF_WIND) and st.getQuestItemsCount(TONE_OF_EARTH) :
            st.takeItems(SCORE_OF_ELEMENTS,1)
            st.takeItems(TONE_OF_WATER,1)
            st.takeItems(TONE_OF_FIRE,1)
            st.takeItems(TONE_OF_WIND,1)
            st.takeItems(TONE_OF_EARTH,1)
            st.giveItems(MARK_OF_MAGUS,1)
            st.giveItems(SHADOW_WEAPON_COUPON_CGRADE,15)
            st.addExpAndSp(139039,40000)
            htmltext = "30629-12.htm"
            st.set("cond","0")
            st.set("onlyone","1")
            st.setState(COMPLETED)
            st.playSound("ItemSound.quest_finish")
        else:
          htmltext = "30629-11.htm"
   elif npcId == 30391:
     if st.getInt("cond")==1:
        htmltext = "30391-01.htm"
     elif st.getInt("cond")==2:
        htmltext = "30391-03.htm"
     elif st.getInt("cond")<6 and st.getInt("cond")>2:
        htmltext = "30391-04.htm"
     elif st.getInt("cond")>5 :
        htmltext = "30391-05.htm"
   elif npcId == 30612:
     if st.getInt("cond")==2 :
        htmltext = "30612-01.htm"
     elif st.getInt("cond")<5 and st.getInt("cond")>2:
        htmltext = "30612-03.htm"
     elif st.getInt("cond")==5:
        htmltext = "30612-04.htm"
     elif st.getInt("cond")>5:
        htmltext = "30612-05.htm"
   elif npcId == 30413:
     if st.getInt("cond") and st.getQuestItemsCount(SCORE_OF_ELEMENTS)==1 and st.getQuestItemsCount(TONE_OF_WATER)==0 and st.getQuestItemsCount(UNDINE_CHARM)==0 :
        htmltext = "30413-01.htm"
        st.giveItems(UNDINE_CHARM,1)
        st.set("cond","7")
     elif st.getInt("cond") and st.getQuestItemsCount(SCORE_OF_ELEMENTS)==1 and st.getQuestItemsCount(UNDINE_CHARM)==1 :
        if st.getQuestItemsCount(DAZZLING_DROP) < 20 :
          htmltext = "30413-02.htm"
        else:
          htmltext = "30413-03.htm"
          st.takeItems(DAZZLING_DROP,st.getQuestItemsCount(DAZZLING_DROP))
          st.takeItems(UNDINE_CHARM,1)
          st.giveItems(TONE_OF_WATER,1)
          st.playSound("ItemSound.quest_middle")
     elif st.getInt("cond") and st.getQuestItemsCount(SCORE_OF_ELEMENTS)==1 and st.getQuestItemsCount(TONE_OF_WATER)==1 and st.getQuestItemsCount(UNDINE_CHARM)==0 :
        htmltext = "30413-04.htm"
   elif npcId == 30411 :
     if st.getInt("cond") and st.getQuestItemsCount(SCORE_OF_ELEMENTS)==1 and st.getQuestItemsCount(TONE_OF_FIRE)==0 and st.getQuestItemsCount(SALAMANDER_CHARM)==0 :
        htmltext = "30411-01.htm"
        st.giveItems(SALAMANDER_CHARM,1)
        st.playSound("ItemSound.quest_middle")
        st.set("cond","7")
     elif st.getInt("cond") and st.getQuestItemsCount(SCORE_OF_ELEMENTS)==1 and st.getQuestItemsCount(SALAMANDER_CHARM)==1 :
        if st.getQuestItemsCount(FLAME_CRYSTAL) < 5 :
          htmltext = "30411-02.htm"
        else:
          htmltext = "30411-03.htm"
          st.takeItems(FLAME_CRYSTAL,st.getQuestItemsCount(FLAME_CRYSTAL))
          st.giveItems(TONE_OF_FIRE,1)
          st.takeItems(SALAMANDER_CHARM,1)
          st.playSound("ItemSound.quest_middle")
     elif st.getInt("cond") and st.getQuestItemsCount(SCORE_OF_ELEMENTS)==1 and st.getQuestItemsCount(TONE_OF_FIRE)==1 and st.getQuestItemsCount(SALAMANDER_CHARM)==0 :
        htmltext = "30411-04.htm"
   elif npcId == 30412 :
     if st.getInt("cond") and st.getQuestItemsCount(SCORE_OF_ELEMENTS)==1 and st.getQuestItemsCount(TONE_OF_WIND)==0 and st.getQuestItemsCount(SYLPH_CHARM)==0 :
        htmltext = "30412-01.htm"
     elif st.getInt("cond") and st.getQuestItemsCount(SCORE_OF_ELEMENTS)==1 and st.getQuestItemsCount(SYLPH_CHARM)==1 :
        if st.getQuestItemsCount(HARPYS_FEATHER)+st.getQuestItemsCount(WYRMS_WINGBONE)+st.getQuestItemsCount(WINDSUS_MANE) < 40 :
          htmltext = "30412-03.htm"
        else:
          htmltext = "30412-04.htm"
          st.takeItems(HARPYS_FEATHER,st.getQuestItemsCount(HARPYS_FEATHER))
          st.takeItems(WYRMS_WINGBONE,st.getQuestItemsCount(WYRMS_WINGBONE))
          st.takeItems(WINDSUS_MANE,st.getQuestItemsCount(WINDSUS_MANE))
          st.giveItems(TONE_OF_WIND,1)
          st.takeItems(SYLPH_CHARM,1)
          st.playSound("ItemSound.quest_middle")
     elif st.getInt("cond") and st.getQuestItemsCount(SCORE_OF_ELEMENTS)==1 and st.getQuestItemsCount(TONE_OF_WIND)==1 and st.getQuestItemsCount(SYLPH_CHARM)==0 :
        htmltext = "30412-05.htm"
   elif npcId == 30409 :
     if st.getInt("cond") and st.getQuestItemsCount(SCORE_OF_ELEMENTS)==1 and st.getQuestItemsCount(TONE_OF_EARTH)==0 and st.getQuestItemsCount(SERPENT_CHARM)==0 :
        htmltext = "30409-01.htm"
     elif st.getInt("cond") and st.getQuestItemsCount(SCORE_OF_ELEMENTS)==1 and st.getQuestItemsCount(SERPENT_CHARM)==1 :
        if st.getQuestItemsCount(EN_MONSTEREYE_SHELL)+st.getQuestItemsCount(EN_STONEGOLEM_POWDER)+st.getQuestItemsCount(EN_IRONGOLEM_SCRAP) < 30 :
          htmltext = "30409-04.htm"
        else:
          htmltext = "30409-05.htm"
          st.takeItems(EN_MONSTEREYE_SHELL,st.getQuestItemsCount(EN_MONSTEREYE_SHELL))
          st.takeItems(EN_STONEGOLEM_POWDER,st.getQuestItemsCount(EN_STONEGOLEM_POWDER))
          st.takeItems(EN_IRONGOLEM_SCRAP,st.getQuestItemsCount(EN_IRONGOLEM_SCRAP))
          st.giveItems(TONE_OF_EARTH,1)
          st.takeItems(SERPENT_CHARM,1)
          st.playSound("ItemSound.quest_middle")
     elif st.getInt("cond") and st.getQuestItemsCount(SCORE_OF_ELEMENTS)==1 and st.getQuestItemsCount(TONE_OF_EARTH)==1 and st.getQuestItemsCount(SERPENT_CHARM)==0 :
        htmltext = "30409-06.htm"
   return htmltext
                             
 def onKill(self,npc,player,isPet):
   st = player.getQuestState(qn)
   if not st : return 
   if st.getState() != STARTED : return 
   
   npcId = npc.getNpcId()
   condition,maxcount,chance,item,part = DROPLIST[npcId]
   random = st.getRandom(100)
   itemcount = st.getQuestItemsCount(item)
   if st.getInt("cond") == condition and itemcount < maxcount and random < chance :
    if itemcount == maxcount-1:
     st.giveItems(item,1)
     st.playSound("ItemSound.quest_middle")
     if part==1:
       count=0
       for items in [GOLDEN_SEED1,GOLDEN_SEED2,GOLDEN_SEED3]:
        count+=st.getQuestItemsCount(items)
       if count>2:
        st.set("cond","5")
    else:
     st.giveItems(item,1)
     st.playSound("ItemSound.quest_itemget")
   return


QUEST       = Quest(228,qn,"Test Of Magus")
CREATED     = State('Start', QUEST)
STARTING     = State('Starting', QUEST)
STARTED     = State('Started', QUEST)
COMPLETED   = State('Completed', QUEST)


QUEST.setInitialState(CREATED)
QUEST.addStartNpc(30629)

QUEST.addTalkId(30629)

for npcId in [30391,30409,30411,30412,30413,30612]:
   QUEST.addTalkId(npcId)
  
for mobId in [20145,20157,20176,20230,20231,20232,20234,27095,27096,27097,27098,20553,20564,20565,20566]:
   QUEST.addKillId(mobId)
