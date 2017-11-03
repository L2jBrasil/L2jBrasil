import sys
from com.it.br.gameserver.model.quest import State
from com.it.br.gameserver.model.quest import QuestState
from com.it.br.gameserver.model.quest.jython import QuestJython as JQuest
from com.it.br.gameserver.instancemanager import FourSepulchersManager

qn = "620_FourGoblets"

#NPC
NAMELESS_SPIRIT = 31453

GHOST_OF_WIGOTH_1 = 31452
GHOST_OF_WIGOTH_2 = 31454

CONQ_SM = 31921
EMPER_SM = 31922
SAGES_SM = 31923
JUDGE_SM = 31924

GHOST_CHAMBERLAIN_1 = 31919
GHOST_CHAMBERLAIN_2 = 31920

#ITEMS
ENTRANCE_PASS = 7075
GRAVE_PASS = 7261
GOBLETS = [7256,7257,7258,7259]
RELIC = 7254
SEALED_BOX = 7255

#REWARDS
ANTIQUE_BROOCH = 7262
RCP_REWARDS = [ 6881,6883,6885,6887,6891,6893,6895,6897,6899,7580 ]

class Quest (JQuest) :

  def __init__(self,id,name,descr):
      JQuest.__init__(self,id,name,descr)
      self.questItemIds = [ANTIQUE_BROOCH,SEALED_BOX,7256,7257,7258,7259,GRAVE_PASS]

  def onTalk (Self,npc,player) :
    htmltext = "<html><body>You are either not on a quest that involves this NPC, or you don't meet this NPC's minimum quest requirements.</body></html>"
    st = player.getQuestState(qn)
    id = st.getState()
    if id == CREATED :
      st.set("cond","0")
    npcId = npc.getNpcId()
    if npcId == NAMELESS_SPIRIT:
      if int(st.get("cond")) == 0 :
        if st.getPlayer().getLevel() >= 74 :
          htmltext = "31453-1.htm"
        else :
          htmltext = "31453-12.htm"
          st.exitQuest(1)
      elif int(st.get("cond")) == 1 :
        if st.getQuestItemsCount(GOBLETS[0]) >= 1 and st.getQuestItemsCount(GOBLETS[1]) >= 1 and st.getQuestItemsCount(GOBLETS[2]) >= 1 and st.getQuestItemsCount(GOBLETS[3]) >= 1 :
          htmltext = "31453-15.htm"
        else :
          htmltext = "31453-14.htm"
      elif int(st.get("cond")) == 2 :
          htmltext = "31453-17.htm"
    elif npcId == GHOST_OF_WIGOTH_1 :
      if st.getInt("cond") == 1:
         if st.getQuestItemsCount(GOBLETS[0]) + st.getQuestItemsCount(GOBLETS[1]) + st.getQuestItemsCount(GOBLETS[2]) + st.getQuestItemsCount(GOBLETS[3]) == 1 :
            htmltext = "31452-1.htm"
         elif st.getQuestItemsCount(GOBLETS[0]) + st.getQuestItemsCount(GOBLETS[1]) + st.getQuestItemsCount(GOBLETS[2]) + st.getQuestItemsCount(GOBLETS[3]) > 1 :
            htmltext = "31452-2.htm"
      elif st.getInt("cond") == 2:
         htmltext = "31452-2.htm"
    elif npcId == GHOST_OF_WIGOTH_2 :
      if st.getQuestItemsCount(RELIC) >= 1000 :
         if st.getQuestItemsCount(SEALED_BOX) >= 1 :
             if st.getQuestItemsCount(GOBLETS[0]) >= 1 and st.getQuestItemsCount(GOBLETS[1]) >= 1 and st.getQuestItemsCount(GOBLETS[2]) >= 1 and st.getQuestItemsCount(GOBLETS[3]) >= 1 :
                htmltext = "31454-4.htm"
             else :
                if st.getQuestItemsCount(GOBLETS[0]) + st.getQuestItemsCount(GOBLETS[1]) + st.getQuestItemsCount(GOBLETS[2]) + st.getQuestItemsCount(GOBLETS[3]) > 1 :
                   htmltext = "31454-8.htm"
                else :
                   htmltext = "31454-12.htm"
         else :
             if st.getQuestItemsCount(GOBLETS[0]) >= 1 and st.getQuestItemsCount(GOBLETS[1]) >= 1 and st.getQuestItemsCount(GOBLETS[2]) >= 1 and st.getQuestItemsCount(GOBLETS[3]) >= 1 :
                htmltext = "31454-3.htm"
             else :
                if st.getQuestItemsCount(GOBLETS[0]) + st.getQuestItemsCount(GOBLETS[1]) + st.getQuestItemsCount(GOBLETS[2]) + st.getQuestItemsCount(GOBLETS[3]) > 1 :
                   htmltext = "31454-7.htm"
                else :
                   htmltext = "31454-11.htm"
      else :
         if st.getQuestItemsCount(SEALED_BOX) >= 1 :
             if st.getQuestItemsCount(GOBLETS[0]) >= 1 and st.getQuestItemsCount(GOBLETS[1]) >= 1 and st.getQuestItemsCount(GOBLETS[2]) >= 1 and st.getQuestItemsCount(GOBLETS[3]) >= 1 :
                htmltext = "31454-2.htm"
             else :
                if st.getQuestItemsCount(GOBLETS[0]) + st.getQuestItemsCount(GOBLETS[1]) + st.getQuestItemsCount(GOBLETS[2]) + st.getQuestItemsCount(GOBLETS[3]) > 1 :
                   htmltext = "31454-6.htm"
                else :
                   htmltext = "31454-10.htm"
         else :
             if st.getQuestItemsCount(GOBLETS[0]) >= 1 and st.getQuestItemsCount(GOBLETS[1]) >= 1 and st.getQuestItemsCount(GOBLETS[2]) >= 1 and st.getQuestItemsCount(GOBLETS[3]) >= 1 :
                htmltext = "31454-1.htm"
             else :
                if st.getQuestItemsCount(GOBLETS[0]) + st.getQuestItemsCount(GOBLETS[1]) + st.getQuestItemsCount(GOBLETS[2]) + st.getQuestItemsCount(GOBLETS[3]) > 1 :
                   htmltext = "31454-5.htm"
                else :
                   htmltext = "31454-9.htm"

    elif npcId == CONQ_SM :
      htmltext = "31921-E.htm"
    elif npcId == EMPER_SM :
      htmltext = "31922-E.htm"
    elif npcId == SAGES_SM :
      htmltext = "31923-E.htm"
    elif npcId == JUDGE_SM :
      htmltext = "31924-E.htm"
    elif npcId == GHOST_CHAMBERLAIN_1 :
      htmltext = "31919-1.htm"
    return htmltext

  def onKill (self,npc,player,isPet) :
    st = player.getQuestState(qn)
    npcId = npc.getNpcId()
    if st:
      if int(st.get("cond")) == 1 or int(st.get("cond")) == 2 :
        if npcId in range(18120,18256) :
          if st.getRandom(100) < 30 :
            st.giveItems(SEALED_BOX,1)
            st.playSound("ItemSound.quest_itemget")
      return

  def onAdvEvent (self,event,npc,player) :
    htmltext = event
    st = player.getQuestState(qn)
    if not st : return
    htmltext = event
    if event == "Enter" :
      FourSepulchersManager.getInstance().tryEntry(npc,player)
      return
    elif event == "accept" :
      if int(st.get("cond")) == 0 :
        if st.getPlayer().getLevel() >= 74 :
          st.setState(STARTED)
          st.playSound("ItemSound.quest_accept")
          htmltext = "31453-13.htm"
          st.set("cond","1")
        else :
          htmltext = "31453-12.htm"
          st.exitQuest(1)
    elif event == "11" :
      if st.getQuestItemsCount(SEALED_BOX) >= 1 :
        htmltext = "31454-13.htm"
        st.takeItems(SEALED_BOX,1)
        reward = 0
        rnd = st.getRandom(5)
        if rnd == 0:
          st.giveItems(57,10000)
          reward = 1
        elif rnd == 1:
          if st.getRandom(1000) < 848 :
            reward = 1
            i = st.getRandom(1000)
            if i < 43 :
              st.giveItems(1884,42)
            elif i < 66 :
              st.giveItems(1895,36)
            elif i < 184 :
              st.giveItems(1876,4)
            elif i < 250 :
              st.giveItems(1881,6)
            elif i < 287 :
              st.giveItems(5549,8)
            elif i < 484 :
              st.giveItems(1874,1)
            elif i < 681 :
              st.giveItems(1889,1)
            elif i < 799 :
              st.giveItems(1877,1)
            elif i < 902 :
              st.giveItems(1894,1)
            else:
              st.giveItems(4043,1)
          if st.getRandom(1000) < 323 :
            reward = 1
            i = st.getRandom(1000)
            if i < 335 :
              st.giveItems(1888,1)
            elif i < 556 :
              st.giveItems(4040,1)
            elif i < 725 :
              st.giveItems(1890,1)
            elif i < 872 :
              st.giveItems(5550,1)
            elif i < 962 :
              st.giveItems(1893,1)
            elif i < 986 :
              st.giveItems(4046,1)
            else:
              st.giveItems(4048,1)
        elif rnd == 2:
          if st.getRandom(1000) < 847 :
            reward = 1
            i = st.getRandom(1000)
            if i < 148 :
              st.giveItems(1878,8)
            elif i < 175 :
              st.giveItems(1882,24)
            elif i < 273 :
              st.giveItems(1879,4)
            elif i < 322 :
              st.giveItems(1880,6)
            elif i < 357 :
              st.giveItems(1885,6)
            elif i < 554 :
              st.giveItems(1875,1)
            elif i < 685 :
              st.giveItems(1883,1)
            elif i < 803 :
              st.giveItems(5220,1)
            elif i < 901 :
              st.giveItems(4039,1)
            else:
              st.giveItems(4044,1)
          if st.getRandom(1000) < 251 :
            reward = 1
            i = st.getRandom(1000)
            if i < 350 :
              st.giveItems(1887,1)
            elif i < 587 :
              st.giveItems(4042,1)
            elif i < 798 :
              st.giveItems(1886,1)
            elif i < 922 :
              st.giveItems(4041,1)
            elif i < 966 :
              st.giveItems(1892,1)
            elif i < 996 :
              st.giveItems(1891,1)
            else:
              st.giveItems(4047,1)
        elif rnd == 3:
          if st.getRandom(1000) < 31 :
            reward = 1
            i = st.getRandom(1000)
            if i < 223 :
              st.giveItems(730,1)
            elif i < 893 :
              st.giveItems(948,1)
            else:
              st.giveItems(960,1)
          if st.getRandom(1000) < 5 :
            reward = 1
            i = st.getRandom(1000)
            if i < 202 :
              st.giveItems(729,1)
            elif i < 928 :
              st.giveItems(947,1)
            else:
              st.giveItems(959,1)
        elif rnd == 4:
          if st.getRandom(1000) < 329 :
            reward = 1
            i = st.getRandom(1000)
            if i < 88 :
              st.giveItems(6698,1)
            elif i < 185 :
              st.giveItems(6699,1)
            elif i < 238 :
              st.giveItems(6700,1)
            elif i < 262 :
              st.giveItems(6701,1)
            elif i < 292 :
              st.giveItems(6702,1)
            elif i < 356 :
              st.giveItems(6703,1)
            elif i < 420 :
              st.giveItems(6704,1)
            elif i < 482 :
              st.giveItems(6705,1)
            elif i < 554 :
              st.giveItems(6706,1)
            elif i < 576 :
              st.giveItems(6707,1)
            elif i < 640 :
              st.giveItems(6708,1)
            elif i < 704 :
              st.giveItems(6709,1)
            elif i < 777 :
              st.giveItems(6710,1)
            elif i < 799 :
              st.giveItems(6711,1)
            elif i < 863 :
              st.giveItems(6712,1)
            elif i < 927 :
              st.giveItems(6713,1)
            else:
              st.giveItems(6714,1)
          if st.getRandom(1000) < 54 :
            reward = 1
            i = st.getRandom(1000)
            if i < 100 :
              st.giveItems(6688,1)
            elif i < 198 :
              st.giveItems(6689,1)
            elif i < 298 :
              st.giveItems(6690,1)
            elif i < 398 :
              st.giveItems(6691,1)
            elif i < 499 :
              st.giveItems(7579,1)
            elif i < 601 :
              st.giveItems(6693,1)
            elif i < 703 :
              st.giveItems(6694,1)
            elif i < 801 :
              st.giveItems(6695,1)
            elif i < 902 :
              st.giveItems(6696,1)
            else:
              st.giveItems(6697,1)
        if reward == 0 :
          if st.getRandom(2) == 0 :
             htmltext = "31454-14.htm"
          else :
             htmltext = "31454-15.htm"
    elif event == "12" :
      if st.getQuestItemsCount(GOBLETS[0]) >= 1 and st.getQuestItemsCount(GOBLETS[1]) >= 1 and st.getQuestItemsCount(GOBLETS[2]) >= 1 and st.getQuestItemsCount(GOBLETS[3]) >= 1 :
        st.takeItems(GOBLETS[0],-1)
        st.takeItems(GOBLETS[1],-1)
        st.takeItems(GOBLETS[2],-1)
        st.takeItems(GOBLETS[3],-1)
        st.giveItems(ANTIQUE_BROOCH,1)
        st.set("cond","2")
        st.playSound("ItemSound.quest_finish")
        htmltext = "31453-16.htm"
      else :
        htmltext = "31453-14.htm"
    elif event == "13" :
      st.playSound("ItemSound.quest_finish")
      st.exitQuest(1)
      htmltext = "31453-18.htm"
    elif event == "14" :
      htmltext = "31453-13.htm"
      if st.getInt("cond") == 2:
         htmltext = "31453-19.htm"
    # Ghost Chamberlain of Elmoreden: Teleport to 4th sepulcher
    elif event == "15" :
      if st.getQuestItemsCount(ANTIQUE_BROOCH) >= 1 :
        st.getPlayer().teleToLocation(178298,-84574,-7216)
        htmltext = None
      elif st.getQuestItemsCount(GRAVE_PASS) >= 1 :
        st.takeItems(GRAVE_PASS,1)
        st.getPlayer().teleToLocation(178298,-84574,-7216)
        htmltext = None
      else :
        htmltext = ""+str(npc.getNpcId())+"-0.htm"
    # Ghost Chamberlain of Elmoreden: Teleport to Imperial Tomb entrance
    elif event == "16" :
      if st.getQuestItemsCount(ANTIQUE_BROOCH) >= 1 :
        st.getPlayer().teleToLocation(186942,-75602,-2834)
        htmltext = None
      elif st.getQuestItemsCount(GRAVE_PASS) >= 1 :
        st.takeItems(GRAVE_PASS,1)
        st.getPlayer().teleToLocation(186942,-75602,-2834)
        htmltext = None
      else :
        htmltext = ""+str(npc.getNpcId())+"-0.htm"
    # Teleport to Pilgrims Temple
    elif event == "17" :
      if st.getQuestItemsCount(ANTIQUE_BROOCH) >= 1 :
        st.getPlayer().teleToLocation(169590,-90218,-2914)
      else :
        st.takeItems(GRAVE_PASS,1)
        st.getPlayer().teleToLocation(169590,-90218,-2914)
      htmltext = "31452-6.htm"
    elif event == "18" :
      if st.getQuestItemsCount(GOBLETS[0]) + st.getQuestItemsCount(GOBLETS[1]) + st.getQuestItemsCount(GOBLETS[2]) + st.getQuestItemsCount(GOBLETS[3]) < 3 :
        htmltext = "31452-3.htm"
      elif st.getQuestItemsCount(GOBLETS[0]) + st.getQuestItemsCount(GOBLETS[1]) + st.getQuestItemsCount(GOBLETS[2]) + st.getQuestItemsCount(GOBLETS[3]) == 3 :
        htmltext = "31452-4.htm"
      elif st.getQuestItemsCount(GOBLETS[0]) + st.getQuestItemsCount(GOBLETS[1]) + st.getQuestItemsCount(GOBLETS[2]) + st.getQuestItemsCount(GOBLETS[3]) >= 4 :
        htmltext = "31452-5.htm"
    elif event == "19" :
      if st.getQuestItemsCount(SEALED_BOX) >= 1 :
        htmltext = "31919-3.htm"
        st.takeItems(SEALED_BOX,1)
        reward = 0
        rnd = st.getRandom(5)
        if rnd == 0:
          st.giveItems(57,10000)
          reward = 1
        elif rnd == 1:
          if st.getRandom(1000) < 848 :
            reward = 1
            i = st.getRandom(1000)
            if i < 43 :
              st.giveItems(1884,42)
            elif i < 66 :
              st.giveItems(1895,36)
            elif i < 184 :
              st.giveItems(1876,4)
            elif i < 250 :
              st.giveItems(1881,6)
            elif i < 287 :
              st.giveItems(5549,8)
            elif i < 484 :
              st.giveItems(1874,1)
            elif i < 681 :
              st.giveItems(1889,1)
            elif i < 799 :
              st.giveItems(1877,1)
            elif i < 902 :
              st.giveItems(1894,1)
            else:
              st.giveItems(4043,1)
          if st.getRandom(1000) < 323 :
            reward = 1
            i = st.getRandom(1000)
            if i < 335 :
              st.giveItems(1888,1)
            elif i < 556 :
              st.giveItems(4040,1)
            elif i < 725 :
              st.giveItems(1890,1)
            elif i < 872 :
              st.giveItems(5550,1)
            elif i < 962 :
              st.giveItems(1893,1)
            elif i < 986 :
              st.giveItems(4046,1)
            else:
              st.giveItems(4048,1)
        elif rnd == 2:
          if st.getRandom(1000) < 847 :
            reward = 1
            i = st.getRandom(1000)
            if i < 148 :
              st.giveItems(1878,8)
            elif i < 175 :
              st.giveItems(1882,24)
            elif i < 273 :
              st.giveItems(1879,4)
            elif i < 322 :
              st.giveItems(1880,6)
            elif i < 357 :
              st.giveItems(1885,6)
            elif i < 554 :
              st.giveItems(1875,1)
            elif i < 685 :
              st.giveItems(1883,1)
            elif i < 803 :
              st.giveItems(5220,1)
            elif i < 901 :
              st.giveItems(4039,1)
            else:
              st.giveItems(4044,1)
          if st.getRandom(1000) < 251 :
            reward = 1
            i = st.getRandom(1000)
            if i < 350 :
              st.giveItems(1887,1)
            elif i < 587 :
              st.giveItems(4042,1)
            elif i < 798 :
              st.giveItems(1886,1)
            elif i < 922 :
              st.giveItems(4041,1)
            elif i < 966 :
              st.giveItems(1892,1)
            elif i < 996 :
              st.giveItems(1891,1)
            else:
              st.giveItems(4047,1)
        elif rnd == 3:
          if st.getRandom(1000) < 31 :
            reward = 1
            i = st.getRandom(1000)
            if i < 223 :
              st.giveItems(730,1)
            elif i < 893 :
              st.giveItems(948,1)
            else:
              st.giveItems(960,1)
          if st.getRandom(1000) < 5 :
            reward = 1
            i = st.getRandom(1000)
            if i < 202 :
              st.giveItems(729,1)
            elif i < 928 :
              st.giveItems(947,1)
            else:
              st.giveItems(959,1)
        elif rnd == 4:
          if st.getRandom(1000) < 329 :
            reward = 1
            i = st.getRandom(1000)
            if i < 88 :
              st.giveItems(6698,1)
            elif i < 185 :
              st.giveItems(6699,1)
            elif i < 238 :
              st.giveItems(6700,1)
            elif i < 262 :
              st.giveItems(6701,1)
            elif i < 292 :
              st.giveItems(6702,1)
            elif i < 356 :
              st.giveItems(6703,1)
            elif i < 420 :
              st.giveItems(6704,1)
            elif i < 482 :
              st.giveItems(6705,1)
            elif i < 554 :
              st.giveItems(6706,1)
            elif i < 576 :
              st.giveItems(6707,1)
            elif i < 640 :
              st.giveItems(6708,1)
            elif i < 704 :
              st.giveItems(6709,1)
            elif i < 777 :
              st.giveItems(6710,1)
            elif i < 799 :
              st.giveItems(6711,1)
            elif i < 863 :
              st.giveItems(6712,1)
            elif i < 927 :
              st.giveItems(6713,1)
            else:
              st.giveItems(6714,1)
          if st.getRandom(1000) < 54 :
            reward = 1
            i = st.getRandom(1000)
            if i < 100 :
              st.giveItems(6688,1)
            elif i < 198 :
              st.giveItems(6689,1)
            elif i < 298 :
              st.giveItems(6690,1)
            elif i < 398 :
              st.giveItems(6691,1)
            elif i < 499 :
              st.giveItems(7579,1)
            elif i < 601 :
              st.giveItems(6693,1)
            elif i < 703 :
              st.giveItems(6694,1)
            elif i < 801 :
              st.giveItems(6695,1)
            elif i < 902 :
              st.giveItems(6696,1)
            else:
              st.giveItems(6697,1)
        if reward == 0 :
          if st.getRandom(2) == 0 :
             htmltext = "31919-4.htm"
          else :
             htmltext = "31919-5.htm"
      else :
        htmltext = "31919-6.htm"
    elif event.isdigit() and int(event) in RCP_REWARDS :
      st.takeItems(RELIC,1000)
      st.giveItems(int(event),1)
      htmltext = "31454-17.htm"
    return htmltext

QUEST       = Quest(620,qn,"Four Goblets")
CREATED     = State('Start', QUEST)
STARTED     = State('Started', QUEST)

QUEST.setInitialState(CREATED)

QUEST.addStartNpc(NAMELESS_SPIRIT)

QUEST.addTalkId(NAMELESS_SPIRIT)

for npcTalkId in [GHOST_OF_WIGOTH_1,GHOST_OF_WIGOTH_2,CONQ_SM,EMPER_SM,SAGES_SM,JUDGE_SM,GHOST_CHAMBERLAIN_1,GHOST_CHAMBERLAIN_2] :
  QUEST.addTalkId(npcTalkId)

for npcStartId in [CONQ_SM,EMPER_SM,SAGES_SM,JUDGE_SM,GHOST_CHAMBERLAIN_1,GHOST_CHAMBERLAIN_2] :
  QUEST.addStartNpc(npcStartId)

for npcKillId in range(18120,18256) :
  QUEST.addKillId(npcKillId)