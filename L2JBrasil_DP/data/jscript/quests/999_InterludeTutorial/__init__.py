# Made by Mr. Have fun! - version 0.2 by Rolarga
# C5 addons by DrLecter
# updated by KhayrusS
import sys
from com.it.br.gameserver.cache import HtmCache
from com.it.br.gameserver.model.quest import State
from com.it.br.gameserver.model.quest import QuestState
from com.it.br.gameserver.model.quest.jython import QuestJython as JQuest

qn = "999_InterludeTutorial"
qnTutorial = "255_Tutorial"

RECOMMENDATION_01 = 1067
RECOMMENDATION_02 = 1068
LEAF_OF_MOTHERTREE = 1069
BLOOD_OF_JUNDIN = 1070
LICENSE_OF_MINER = 1498
VOUCHER_OF_FLAME = 1496
SOULSHOT_NOVICE = 5789
SPIRITSHOT_NOVICE = 5790
BLUE_GEM=6353
TOKEN = 8542
SCROLL= 8594
# event:[htmlfile,radarX,radarY,radarZ,item,classId1,gift1,count1,classId2,gift2,count2]
EVENTS={
"30008_02":["30008-03.htm",-84058, 243239,-3730,RECOMMENDATION_01 ,0x00,SOULSHOT_NOVICE  ,200,0x00,              0,  0],
"30008_04":["30008-04.htm",-84058, 243239,-3730,                 0,0x00,                0,  0,   0,              0,  0],
"30017_02":["30017-03.htm",-84058, 243239,-3730,RECOMMENDATION_02 ,0x0a,SPIRITSHOT_NOVICE,100,0x00,              0,  0],
"30017_04":["30017-04.htm",-84058, 243239,-3730,                 0,0x0a,                0,  0,0x00,              0,  0],
"30370_02":["30370-03.htm", 45491,  48359,-3086,LEAF_OF_MOTHERTREE,0x19,SPIRITSHOT_NOVICE,100,0x12,SOULSHOT_NOVICE,200],
"30370_04":["30370-04.htm", 45491,  48359,-3086,                 0,0x19,                0,  0,0x12,              0,  0],
"30129_02":["30129-03.htm", 12116,  16666,-4610,BLOOD_OF_JUNDIN   ,0x26,SPIRITSHOT_NOVICE,100,0x1f,SOULSHOT_NOVICE,200],
"30129_04":["30129-04.htm", 12116,  16666,-4610,                 0,0x26,                0,  0,0x1f,              0,  0],
"30528_02":["30528-03.htm",115642,-178046, -941,LICENSE_OF_MINER  ,0x35,SOULSHOT_NOVICE  ,200,0x00,              0,  0],
"30528_04":["30528-04.htm",115642,-178046, -941,                 0,0x35,                0,  0,0x00,              0,  0],
"30573_02":["30573-03.htm",-45067,-113549, -235,VOUCHER_OF_FLAME  ,0x31,SPIRITSHOT_NOVICE,100,0x2c,SOULSHOT_NOVICE,200],
"30573_04":["30573-04.htm",-45067,-113549, -235,                 0,0x31,                0,  0,0x2c,              0,  0]
}

# npcId:[raceId,[htmlfiles],npcTyp,item]
TALKS={
30017:[0,["30017-01.htm","30017-02.htm","30017-04.htm"],0,0],
30008:[0,["30008-01.htm","30008-02.htm","30008-04.htm"],0,0],
30370:[1,["30370-01.htm","30370-02.htm","30370-04.htm"],0,0],
30129:[2,["30129-01.htm","30129-02.htm","30129-04.htm"],0,0],
30573:[3,["30573-01.htm","30573-02.htm","30573-04.htm"],0,0],
30528:[4,["30528-01.htm","30528-02.htm","30528-04.htm"],0,0],
30018:[0,["30131-01.htm",0,"30019-03a.htm","30019-04.htm",],1,RECOMMENDATION_02],
30019:[0,["30131-01.htm",0,"30019-03a.htm","30019-04.htm",],1,RECOMMENDATION_02],
30020:[0,["30131-01.htm",0,"30019-03a.htm","30019-04.htm",],1,RECOMMENDATION_02],
30021:[0,["30131-01.htm",0,"30019-03a.htm","30019-04.htm",],1,RECOMMENDATION_02],
30009:[0,["30530-01.htm","30009-03.htm",0,"30009-04.htm",],1,RECOMMENDATION_01],
30011:[0,["30530-01.htm","30009-03.htm",0,"30009-04.htm",],1,RECOMMENDATION_01],
30012:[0,["30530-01.htm","30009-03.htm",0,"30009-04.htm",],1,RECOMMENDATION_01],
30056:[0,["30530-01.htm","30009-03.htm",0,"30009-04.htm",],1,RECOMMENDATION_01],
30400:[1,["30131-01.htm","30400-03.htm","30400-03a.htm","30400-04.htm",],1,LEAF_OF_MOTHERTREE],
30401:[1,["30131-01.htm","30400-03.htm","30400-03a.htm","30400-04.htm",],1,LEAF_OF_MOTHERTREE],
30402:[1,["30131-01.htm","30400-03.htm","30400-03a.htm","30400-04.htm",],1,LEAF_OF_MOTHERTREE],
30403:[1,["30131-01.htm","30400-03.htm","30400-03a.htm","30400-04.htm",],1,LEAF_OF_MOTHERTREE],
30131:[2,["30131-01.htm","30131-03.htm","30131-03a.htm","30131-04.htm",],1,BLOOD_OF_JUNDIN],
30404:[2,["30131-01.htm","30131-03.htm","30131-03a.htm","30131-04.htm",],1,BLOOD_OF_JUNDIN],
30574:[3,["30575-01.htm","30575-03.htm","30575-03a.htm","30575-04.htm",],1,VOUCHER_OF_FLAME],
30575:[3,["30575-01.htm","30575-03.htm","30575-03a.htm","30575-04.htm",],1,VOUCHER_OF_FLAME],
30530:[4,["30530-01.htm","30530-03.htm",0,"30530-04.htm",],1,LICENSE_OF_MINER]
}    

class Quest (JQuest) :

 def __init__(self,id,name,descr): JQuest.__init__(self,id,name,descr)

 def onAdvEvent (self,event,npc,player) :
    st = player.getQuestState(qn)
    if not st: return
    qs = st.getPlayer().getQuestState(qnTutorial)
    if not qs: return
    htmltext = event
    if qs != None :
       Ex = int(qs.get("Ex"))
       classId = int(st.getPlayer().getClassId().getId())
       if event == "TimerEx_NewbieHelper" :
          if Ex == 0 :
             if player.getClassId().isMage() :
                st.playTutorialVoice("tutorial_voice_009b")
             else :
                st.playTutorialVoice("tutorial_voice_009a")
             qs.set("Ex","1")
          elif Ex == 3 :
             st.playTutorialVoice("tutorial_voice_010a")
             qs.set("Ex","4")
          return
       elif event == "TimerEx_GrandMaster" :
          if Ex >= 4 :
             st.showQuestionMark(7)
             st.playSound("ItemSound.quest_tutorial")
             st.playTutorialVoice("tutorial_voice_025")
          return
       else:
          htmlfile,radarX,radarY,radarZ,item,classId1,gift1,count1,classId2,gift2,count2 = EVENTS[event]
          if radarX != 0:
             st.addRadar(radarX,radarY,radarZ);
          htmltext=htmlfile
          if st.getQuestItemsCount(item) and st.getInt("onlyone") == 0:
             st.addExpAndSp(0,50)
             st.takeItems(item,1)
             st.startQuestTimer("TimerEx_GrandMaster",60000)
             if Ex <= 3 :
                qs.set("Ex","4")
             if st.getPlayer().getClassId().getId() == classId1 :
                 st.giveItems(gift1,count1)
             elif st.getPlayer().getClassId().getId() == classId2 :
                if gift2:
                   st.giveItems(gift2,count2)
             st.unset("cond")
             st.set("onlyone","1")
    text = HtmCache.getInstance().getHtm("data/jscript/quests/" + qn + "/" +htmltext)
    return text

 def onFirstTalk (self,npc,player):
   st = player.getQuestState(qn)
   if not st :
      st = self.newQuestState(player)
   qs = player.getQuestState(qnTutorial)
   if not qs : 
      npc.showChatWindow(player)
      return
   htmltext = ""
   Ex = qs.getInt("Ex")
   id = st.getState()
   npcId = npc.getNpcId()
   cond=st.getInt("cond")
   onlyone=st.getInt("onlyone")
   level=player.getLevel()
   isMage = player.getClassId().isMage()
   npcTyp=0
   if id == CREATED :
      st.setState(STARTING)
      st.set("onlyone","0")
   if npcId in TALKS.keys():
      raceId,htmlfiles,npcTyp,item = TALKS[npcId]
   if (level >= 10 or onlyone) and npcTyp == 1:
       htmltext = "30575-05.htm"
   elif npcId in [30600, 30601, 30602, 30598, 30599, 32135]:
      reward=qs.getInt("reward")
      if reward == 0:
         if player.getClassId().isMage() :
            st.giveItems(SPIRITSHOT_NOVICE,100)
         else:
            st.giveItems(SOULSHOT_NOVICE,200)
         st.giveItems(TOKEN,12)
         st.giveItems(SCROLL,2)
         qs.set("reward","1")
         st.exitQuest(False)
      npc.showChatWindow(player)
      return
   elif onlyone == 0 and level < 10 :
      if player.getRace().ordinal() == raceId :
         htmltext=htmlfiles[0]
         if npcTyp==1:
            if cond == 0 and Ex < 0:
               qs.set("Ex","0")
               st.startQuestTimer("TimerEx_NewbieHelper",30000)
               if not isMage :
                  htmltext="30530-01.htm"
               st.set("cond","1")
               st.setState(STARTED)
            elif cond==1 and st.getQuestItemsCount(item)==0 :
               if st.getQuestItemsCount(BLUE_GEM) :
                  st.takeItems(BLUE_GEM,st.getQuestItemsCount(BLUE_GEM))
                  st.giveItems(item,1)
                  st.set("cond","2")
                  st.playSound("ItemSound.quest_middle")
                  if isMage :
                     st.giveItems(SPIRITSHOT_NOVICE,100)
                     htmltext = htmlfiles[2]
                     if htmltext == 0 :
                        htmltext = "<html><body>I am sorry.  I only help warriors.  Please go to another Newbie Helper who may assist you.</body></html>"
                  else:
                     st.giveItems(SOULSHOT_NOVICE,200)
                     htmltext = htmlfiles[1]
                     if htmltext == 0 :
                        htmltext = "<html><body>I am sorry.  I only help mystics.  Please go to another Newbie Helper who may assist you.</body></html>"
            elif cond==2 :
               htmltext = htmlfiles[3]
            elif cond==2 :
               htmltext = htmlfiles[1]
            elif cond==3 :
               htmltext = htmlfiles[2] 
   else:
      htmltext = EVENTS[event][0]                              
   if htmltext == None or htmltext == "":
     npc.showChatWindow(player)
   text = HtmCache.getInstance().getHtm("data/jscript/quests/" + qn + "/" +htmltext)
   return text

 def onTalk (self,npc,player):
   htmltext = "<html><body>You are either not carrying out your quest or don't meet the criteria.</body></html>"
   st = player.getQuestState(qn)
   if not st : return htmltext  
   npcId = npc.getNpcId()
   id = st.getState()
   cond=st.getInt("cond")
   onlyone=st.getInt("onlyone")
   level=player.getLevel()
   isMage = player.getClassId().isMage()
   npcTyp=0
   if id == CREATED :
     st.setState(STARTING)
     st.set("onlyone","0")
   raceId,htmlfiles,npcTyp,item = TALKS[npcId]
   if (level >= 10 or onlyone) and npcTyp == 1:
       htmltext = "30575-05.htm"
   elif onlyone == 0 and level < 10 :
    if player.getRace().ordinal() == raceId :
      htmltext=htmlfiles[0]
      if npcTyp==1:
       if cond==0 :
        if isMage :
         st.set("cond","1")
         st.setState(STARTED)
         st.playSound("ItemSound.quest_tutorial")
        else:
         htmltext="30530-01.htm"
         st.set("cond","1")
         st.setState(STARTED)
         st.playSound("ItemSound.quest_tutorial")
       elif cond==1 and st.getQuestItemsCount(item)==0 :
         if st.getQuestItemsCount(BLUE_GEM) :
           st.takeItems(BLUE_GEM,st.getQuestItemsCount(BLUE_GEM))
           st.giveItems(item,1)
           st.set("cond","2")
           st.playSound("ItemSound.quest_middle")
           if isMage :
             st.giveItems(SPIRITSHOT_NOVICE,100)
             htmltext = htmlfiles[2]
             if htmltext == 0 :
                 htmltext = "<html><body>I am sorry.  I only help warriors.  Please go to another Newbie Helper who may assist you.</body></html>"
           else:
             st.giveItems(SOULSHOT_NOVICE,200)
             htmltext = htmlfiles[1]
             if htmltext == 0 :
                 htmltext = "<html><body>I am sorry.  I only help mystics.  Please go to another Newbie Helper who may assist you.</body></html>"
         else:
           if isMage :
             htmltext = "30131-02.htm"
             if player.getRace().ordinal() == 3 :
              htmltext = "30575-02.htm"
           else:
             htmltext = "30530-02.htm"
       elif cond==2 :
        htmltext = htmlfiles[3]
      elif npcTyp == 0 :
        if cond==1 :
          htmltext = htmlfiles[0]
        elif cond==2 :
          htmltext = htmlfiles[1]
        elif cond==3 :
          htmltext = htmlfiles[2] 
   else:
       htmltext = TALKS[npcId][1][-1]
   return htmltext

 def onKill(self,npc,player,isPet):
   st = player.getQuestState(qn)
   if not st : return
   qs = st.getPlayer().getQuestState(qnTutorial)
   if not qs : return
   Ex = int(qs.get("Ex"))
   if qs != None :
      if Ex in [0,1] :
         st.playTutorialVoice("tutorial_voice_011")
         st.showQuestionMark(3)
         qs.set("Ex","2")
   if Ex in [0,1,2] and st.getRandom(100) < 50 and st.getQuestItemsCount(BLUE_GEM) < 1 :
      st.dropItem(npc,player,BLUE_GEM,1)
      st.playSound("ItemSound.quest_tutorial")
   return

QUEST       = Quest(999,qn,"Interlude Tutorial")
CREATED     = State('Start', QUEST)
STARTING    = State('Starting', QUEST)
STARTED     = State('Started', QUEST)
COMPLETED   = State('Completed', QUEST)

QUEST.setInitialState(CREATED)

for startNpc in [30008,30009,30017,30019,30129,30131,30573,30575,30370,30528,30530,30400,30401,30402,30403,30404]:
  QUEST.addStartNpc(startNpc)
  QUEST.addTalkId(startNpc)

for npc in [30009, 30019, 30131, 30400, 30530, 30600, 30601, 30602, 30575, 30598, 30599]:
  QUEST.addFirstTalkId(npc)

QUEST.addKillId(18342)
QUEST.addKillId(20001)