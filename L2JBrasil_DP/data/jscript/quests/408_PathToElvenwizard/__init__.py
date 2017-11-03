# Maked by Mr. Have fun! Version 0.2
# Shadow Weapon Coupons contributed by BiTi for the Official L2J Datapack Project
# Visit http://forum.l2jdp.com for more details
import sys
from com.it.br.gameserver.model.quest import State
from com.it.br.gameserver.model.quest import QuestState
from com.it.br.gameserver.model.quest.jython import QuestJython as JQuest

qn = "408_PathToElvenwizard"

ROGELLIAS_LETTER = 1218
RED_DOWN = 1219
MAGICAL_POWERS_RUBY = 1220
PURE_AQUAMARINE = 1221
APPETIZING_APPLE = 1222
GOLD_LEAVES = 1223
IMMORTAL_LOVE = 1224
AMETHYST = 1225
NOBILITY_AMETHYST = 1226
FERTILITY_PERIDOT = 1229
ETERNITY_DIAMOND = 1230
CHARM_OF_GRAIN = 1272
SAP_OF_WORLD_TREE = 1273
LUCKY_POTPOURI = 1274

class Quest (JQuest) :

 def __init__(self,id,name,descr): JQuest.__init__(self,id,name,descr)

 def onEvent (self,event,st) :
    htmltext = event
    player = st.getPlayer()
    if event == "1" :
        st.set("id","0")
        if player.getClassId().getId() != 0x19 :
          if player.getClassId().getId() == 0x1a :
            htmltext = "30414-02a.htm"
          else:
            htmltext = "30414-03.htm"
        else:
          if player.getLevel()<19 :
            htmltext = "30414-04.htm"
          else:
            if st.getQuestItemsCount(ETERNITY_DIAMOND) != 0 :
              htmltext = "30414-05.htm"
            else:
              st.set("cond","1")
              st.setState(STARTED)
              st.playSound("ItemSound.quest_accept")
              if st.getQuestItemsCount(FERTILITY_PERIDOT) == 0 :
                st.giveItems(FERTILITY_PERIDOT,1)
              htmltext = "30414-06.htm"
    elif event == "408_1" :
          if st.getInt("cond") != 0 and st.getQuestItemsCount(MAGICAL_POWERS_RUBY) != 0 :
            htmltext = "30414-10.htm"
          elif st.getInt("cond") != 0 and st.getQuestItemsCount(MAGICAL_POWERS_RUBY) == 0 and st.getQuestItemsCount(FERTILITY_PERIDOT) != 0 :
            if st.getQuestItemsCount(ROGELLIAS_LETTER) == 0 :
              st.giveItems(ROGELLIAS_LETTER,1)
            htmltext = "30414-07.htm"
    elif event == "408_4" :
          if st.getInt("cond") != 0 and st.getQuestItemsCount(ROGELLIAS_LETTER) != 0 :
            st.takeItems(ROGELLIAS_LETTER,st.getQuestItemsCount(ROGELLIAS_LETTER))
            if st.getQuestItemsCount(CHARM_OF_GRAIN) == 0 :
              st.giveItems(CHARM_OF_GRAIN,1)
            htmltext = "30157-02.htm"
    elif event == "408_2" :
          if st.getInt("cond") != 0 and st.getQuestItemsCount(PURE_AQUAMARINE) != 0 :
            htmltext = "30414-13.htm"
          elif st.getInt("cond") != 0 and st.getQuestItemsCount(PURE_AQUAMARINE) == 0 and st.getQuestItemsCount(FERTILITY_PERIDOT) != 0 :
            if st.getQuestItemsCount(APPETIZING_APPLE) == 0 :
              st.giveItems(APPETIZING_APPLE,1)
            htmltext = "30414-14.htm"
    elif event == "408_5" :
          if st.getInt("cond") != 0 and st.getQuestItemsCount(APPETIZING_APPLE) != 0 :
            st.takeItems(APPETIZING_APPLE,st.getQuestItemsCount(APPETIZING_APPLE))
            if st.getQuestItemsCount(SAP_OF_WORLD_TREE) == 0 :
              st.giveItems(SAP_OF_WORLD_TREE,1)
            htmltext = "30371-02.htm"
    elif event == "408_3" :
          if st.getInt("cond") != 0 and st.getQuestItemsCount(NOBILITY_AMETHYST) != 0 :
            htmltext = "30414-17.htm"
          elif st.getInt("cond") != 0 and st.getQuestItemsCount(NOBILITY_AMETHYST) == 0 and st.getQuestItemsCount(FERTILITY_PERIDOT) != 0 :
            if st.getQuestItemsCount(IMMORTAL_LOVE) == 0 :
              st.giveItems(IMMORTAL_LOVE,1)
            htmltext = "30414-18.htm"
    return htmltext


 def onTalk (self,npc,player):
   htmltext = "<html><body>You are either not carrying out your quest or don't meet the criteria.</body></html>"
   st = player.getQuestState(qn)
   if not st : return htmltext

   npcId = npc.getNpcId()
   id = st.getState()
   if npcId != 30414 and id != STARTED : return htmltext

   if id == CREATED :
     st.setState(STARTING)
     st.set("cond","0")
     st.set("onlyone","0")
     st.set("id","0")
   if npcId == 30414 and st.getInt("cond")==0 :
        if st.getInt("cond")<15 :
          htmltext = "30414-01.htm"
        else:
          htmltext = "30414-01.htm"
   elif npcId == 30414 and st.getInt("cond")!=0 and st.getQuestItemsCount(ROGELLIAS_LETTER)==0 and st.getQuestItemsCount(APPETIZING_APPLE)==0 and st.getQuestItemsCount(IMMORTAL_LOVE)==0 and st.getQuestItemsCount(CHARM_OF_GRAIN)==0 and st.getQuestItemsCount(SAP_OF_WORLD_TREE)==0 and st.getQuestItemsCount(LUCKY_POTPOURI)==0 and st.getQuestItemsCount(FERTILITY_PERIDOT)!=0 and (st.getQuestItemsCount(MAGICAL_POWERS_RUBY)==0 or st.getQuestItemsCount(NOBILITY_AMETHYST)==0 or st.getQuestItemsCount(PURE_AQUAMARINE)==0) :
        htmltext = "30414-11.htm"
   elif npcId == 30414 and st.getInt("cond")!=0 and st.getQuestItemsCount(ROGELLIAS_LETTER)!=0 :
        htmltext = "30414-08.htm"
   elif npcId == 30157 and st.getInt("cond")!=0 and st.getQuestItemsCount(ROGELLIAS_LETTER)!=0 :
        htmltext = "30157-01.htm"
   elif npcId == 30157 and st.getInt("cond")!=0 and st.getQuestItemsCount(CHARM_OF_GRAIN)!=0 and st.getQuestItemsCount(RED_DOWN)<5 :
        htmltext = "30157-03.htm"
   elif npcId == 30157 and st.getInt("cond")!=0 and st.getQuestItemsCount(CHARM_OF_GRAIN)!=0 and st.getQuestItemsCount(RED_DOWN)>=5 :
        st.takeItems(RED_DOWN,st.getQuestItemsCount(RED_DOWN))
        st.takeItems(CHARM_OF_GRAIN,st.getQuestItemsCount(CHARM_OF_GRAIN))
        if st.getQuestItemsCount(MAGICAL_POWERS_RUBY) == 0 :
          st.giveItems(MAGICAL_POWERS_RUBY,1)
        htmltext = "30157-04.htm"
   elif npcId == 30414 and st.getInt("cond")!=0 and st.getQuestItemsCount(CHARM_OF_GRAIN)!=0 and st.getQuestItemsCount(RED_DOWN)<5 :
        htmltext = "30414-09.htm"
   elif npcId == 30414 and st.getInt("cond")!=0 and st.getQuestItemsCount(CHARM_OF_GRAIN)!=0 and st.getQuestItemsCount(RED_DOWN)>=5 :
        htmltext = "30414-25.htm"
   elif npcId == 30414 and st.getInt("cond")!=0 and st.getQuestItemsCount(APPETIZING_APPLE)!=0 :
        htmltext = "30414-15.htm"
   elif npcId == 30371 and st.getInt("cond")!=0 and st.getQuestItemsCount(APPETIZING_APPLE)!=0 :
        htmltext = "30371-01.htm"
   elif npcId == 30371 and st.getInt("cond")!=0 and st.getQuestItemsCount(SAP_OF_WORLD_TREE)!=0 and st.getQuestItemsCount(GOLD_LEAVES)<5 :
        htmltext = "30371-03.htm"
   elif npcId == 30371 and st.getInt("cond")!=0 and st.getQuestItemsCount(SAP_OF_WORLD_TREE)!=0 and st.getQuestItemsCount(GOLD_LEAVES)>=5 :
        st.takeItems(GOLD_LEAVES,st.getQuestItemsCount(GOLD_LEAVES))
        st.takeItems(SAP_OF_WORLD_TREE,st.getQuestItemsCount(SAP_OF_WORLD_TREE))
        if st.getQuestItemsCount(PURE_AQUAMARINE) == 0 :
          st.giveItems(PURE_AQUAMARINE,1)
        htmltext = "30371-04.htm"
   elif npcId == 30414 and st.getInt("cond")!=0 and st.getQuestItemsCount(SAP_OF_WORLD_TREE)!=0 and st.getQuestItemsCount(GOLD_LEAVES)<5 :
        htmltext = "30414-16.htm"
   elif npcId == 30414 and st.getInt("cond")!=0 and st.getQuestItemsCount(CHARM_OF_GRAIN)!=0 and st.getQuestItemsCount(GOLD_LEAVES)>=5 :
        htmltext = "30414-26.htm"
   elif npcId == 30414 and st.getInt("cond")!=0 and st.getQuestItemsCount(IMMORTAL_LOVE)!=0 :
        htmltext = "30414-19.htm"
   elif npcId == 30423 and st.getInt("cond")!=0 and st.getQuestItemsCount(IMMORTAL_LOVE)!=0 :
        st.takeItems(IMMORTAL_LOVE,st.getQuestItemsCount(IMMORTAL_LOVE))
        if st.getQuestItemsCount(LUCKY_POTPOURI) == 0 :
          st.giveItems(LUCKY_POTPOURI,1)
        htmltext = "30423-01.htm"
   elif npcId == 30423 and st.getInt("cond")!=0 and st.getQuestItemsCount(LUCKY_POTPOURI)!=0 and st.getQuestItemsCount(AMETHYST)<2 :
        htmltext = "30423-02.htm"
   elif npcId == 30423 and st.getInt("cond")!=0 and st.getQuestItemsCount(LUCKY_POTPOURI)!=0 and st.getQuestItemsCount(AMETHYST)>=2 :
        st.takeItems(AMETHYST,st.getQuestItemsCount(AMETHYST))
        st.takeItems(LUCKY_POTPOURI,st.getQuestItemsCount(LUCKY_POTPOURI))
        if st.getQuestItemsCount(NOBILITY_AMETHYST) == 0 :
          st.giveItems(NOBILITY_AMETHYST,1)
        htmltext = "30423-03.htm"
   elif npcId == 30414 and st.getInt("cond")!=0 and st.getQuestItemsCount(LUCKY_POTPOURI)!=0 and st.getQuestItemsCount(AMETHYST)<2 :
        htmltext = "30414-20.htm"
   elif npcId == 30414 and st.getInt("cond")!=0 and st.getQuestItemsCount(LUCKY_POTPOURI)!=0 and st.getQuestItemsCount(AMETHYST)>=2 :
        htmltext = "30414-27.htm"
   elif npcId == 30414 and st.getInt("cond")!=0 and st.getQuestItemsCount(ROGELLIAS_LETTER)==0 and st.getQuestItemsCount(APPETIZING_APPLE)==0 and st.getQuestItemsCount(IMMORTAL_LOVE)==0 and st.getQuestItemsCount(CHARM_OF_GRAIN)==0 and st.getQuestItemsCount(SAP_OF_WORLD_TREE)==0 and st.getQuestItemsCount(LUCKY_POTPOURI)==0 and st.getQuestItemsCount(FERTILITY_PERIDOT)!=0 and st.getQuestItemsCount(MAGICAL_POWERS_RUBY)!=0 and st.getQuestItemsCount(NOBILITY_AMETHYST)!=0 and st.getQuestItemsCount(PURE_AQUAMARINE)!=0 :
        st.takeItems(MAGICAL_POWERS_RUBY,st.getQuestItemsCount(MAGICAL_POWERS_RUBY))
        st.takeItems(PURE_AQUAMARINE,st.getQuestItemsCount(PURE_AQUAMARINE))
        st.takeItems(NOBILITY_AMETHYST,st.getQuestItemsCount(NOBILITY_AMETHYST))
        st.takeItems(FERTILITY_PERIDOT,st.getQuestItemsCount(FERTILITY_PERIDOT))
        st.set("cond","0")
        st.setState(COMPLETED)
        st.playSound("ItemSound.quest_finish")
        if st.getQuestItemsCount(ETERNITY_DIAMOND) == 0 :
          st.giveItems(ETERNITY_DIAMOND,1)
        htmltext = "30414-24.htm"
   return htmltext

 def onKill(self,npc,player,isPet):
   st = player.getQuestState(qn)
   if not st : return 
   if st.getState() != STARTED : return 
   
   npcId = npc.getNpcId()
   if npcId == 20466 :
        st.set("id","0")
        if st.getInt("cond") != 0 and st.getQuestItemsCount(CHARM_OF_GRAIN) != 0 and st.getQuestItemsCount(RED_DOWN)<5 and st.getRandom(100)<70 :
            st.giveItems(RED_DOWN,1)
            if st.getQuestItemsCount(RED_DOWN) == 5 :
              st.playSound("ItemSound.quest_middle")
            else:
              st.playSound("ItemSound.quest_itemget")
   elif npcId == 20019 :
        st.set("id","0")
        if st.getInt("cond") != 0 and st.getQuestItemsCount(SAP_OF_WORLD_TREE) != 0 and st.getQuestItemsCount(GOLD_LEAVES)<5 and st.getRandom(100)<40 :
            st.giveItems(GOLD_LEAVES,1)
            if st.getQuestItemsCount(GOLD_LEAVES) == 5 :
              st.playSound("ItemSound.quest_middle")
            else:
              st.playSound("ItemSound.quest_itemget")
   elif npcId == 20047 :
        st.set("id","0")
        if st.getInt("cond") != 0 and st.getQuestItemsCount(LUCKY_POTPOURI) != 0 and st.getQuestItemsCount(AMETHYST)<2 and st.getRandom(100)<40 :
            st.giveItems(AMETHYST,1)
            if st.getQuestItemsCount(AMETHYST) == 2 :
              st.playSound("ItemSound.quest_middle")
            else:
              st.playSound("ItemSound.quest_itemget")
   return

QUEST       = Quest(408,qn,"Path To Elven Wizard")
CREATED     = State('Start', QUEST)
STARTING     = State('Starting', QUEST)
STARTED     = State('Started', QUEST)
COMPLETED   = State('Completed', QUEST)


QUEST.setInitialState(CREATED)
QUEST.addStartNpc(30414)

QUEST.addTalkId(30414)

QUEST.addTalkId(30157)
QUEST.addTalkId(30371)
QUEST.addTalkId(30423)

QUEST.addKillId(20019)
QUEST.addKillId(20466)
QUEST.addKillId(20047)