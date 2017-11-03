# Created by t0rm3nt0r
import sys
from com.it.br import Config
from com.it.br.gameserver.model.quest import State
from com.it.br.gameserver.model.quest import QuestState
from com.it.br.gameserver.model.quest.jython import QuestJython as JQuest

qn = "619_RelicsOfTheOldEmpire"

RELICS = 7254
ENTRANCE = 7075
GHOST = 31538
MOBS = [ 21396,21397,21398,21399,21400,21401,21402,21403,21404,21405,21406,21407,21408,21409,21410,21411,21412,21413,21414, \
21415,21416,21417,21418,21419,21420,21421,21422,21423,21424,21425,21426,21427,21428,21429,21430,21431,21432,21433,21434,21798, \
21799,21800,18120,18121,18122,18123,18124,18125,18126,18127,18128,18129,18130,18131,18132,18133,18134,18135,18136,18137,18138, \
18139,18140,18141,18142,18143,18144,18145,18146,18147,18148,18149,18150,18151,18152,18153,18154,18155,18156,18157,18158,18159, \
18160,18161,18162,18163,18164,18165,18166,18167,18168,18169,18170,18171,18172,18173,18174,18175,18176,18177,18178,18179,18180, \
18181,18182,18183,18184,18185,18186,18187,18188,18189,18190,18191,18192,18193,18194,18195,18196,18197,18198,18199,18200,18201, \
18202,18203,18204,18205,18206,18207,18208,18209,18210,18211,18212,18213,18214,18215,18216,18217,18218,18219,18220,18221,18222, \
18223,18224,18225,18226,18227,18228,18229,18230,18231,18232,18233,18234,18235,18236,18237,18238,18239,18240,18241,18242,18243, \
18244,18245,18246,18247,18248,18249,18250,18251,18252,18253,18254,18255,18256,13008,13009,13010,13011,13012,13013,13016,13017]

REWARDS = [ 6881,6883,6885,6887,6891,6893,6895,6897,6899,7580 ]
REWARDS2= [ 6882,6884,6886,6888,6892,6894,6896,6898,6900,7581 ]

#Change this value to 1 if you wish 100% recipes, default 60%
ALT_RP100=0

class Quest (JQuest) :

 def __init__(self,id,name,descr): JQuest.__init__(self,id,name,descr)

 def onEvent (self,event,st) :
     htmltext = event
     relics = st.getQuestItemsCount(RELICS)
     if event == "31538-03.htm" :
       if st.getPlayer().getLevel() >= 74 :
          st.set("cond","1")
          st.setState(STARTED)
          st.playSound("ItemSound.quest_accept")
       else :
          htmltext = "31538-02.htm"
          st.exitQuest(1)
     elif event == "31538-07.htm" :
       if relics >= 1000 :
          htmltext = "31538-07.htm"
          st.takeItems(RELICS,1000)
          if ALT_RP100 == 1:
             st.giveItems(REWARDS2[st.getRandom(len(REWARDS2))],1)
          else:
             st.giveItems(REWARDS[st.getRandom(len(REWARDS))],1)
       else :
          htmltext = "31538-05.htm"
     elif event == "31538-08.htm" :
         st.exitQuest(1)
     return htmltext    

 def onTalk (self,npc,player):
     htmltext = "<html><body>You are either not carrying out your quest or don't meet the criteria.</body></html>"
     st = player.getQuestState(qn)
     if st :
        id = st.getState()
        cond = st.getInt("cond")
        relics = st.getQuestItemsCount(RELICS)
        entrance = st.getQuestItemsCount(ENTRANCE)
        if id==CREATED:
           if player.getLevel() >= 74 :
              htmltext="31538-01.htm"
           else :
              htmltext="31538-02.htm"
              st.exitQuest(1)
        else :
           if cond == 1 and relics >= 1000 :
              htmltext = "31538-04.htm"
           elif entrance :
              htmltext = "31538-05.htm"
           else :
              htmltext = "31538-05a.htm"
     return htmltext

 def onKill(self,npc,player,isPet):
     partyMember = self.getRandomPartyMember(player, "1")
     if not partyMember: return
     st = partyMember.getQuestState(qn)
     if st :
       if st.getState() == STARTED :
         numItems, chance = divmod(100*Config.RATE_DROP_QUEST,100)
         if st.getRandom(100) < chance :
            numItems += 1
         st.giveItems(RELICS,int(numItems))
         st.playSound("ItemSound.quest_itemget")
         if st.getRandom(100) < (5*Config.RATE_DROP_QUEST) :
             st.giveItems(ENTRANCE,1)
             st.playSound("ItemSound.quest_middle")
     return

QUEST       = Quest(619, qn, "Relics of the Old Empire")
CREATED     = State('Start', QUEST)
STARTED     = State('Started', QUEST)

QUEST.setInitialState(CREATED)
QUEST.addStartNpc(GHOST)
QUEST.addTalkId(GHOST)

for mobId in MOBS :
  QUEST.addKillId(mobId)