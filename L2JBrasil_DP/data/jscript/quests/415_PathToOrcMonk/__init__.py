# Maked by Mr. Have fun! Version 0.2
# Shadow Weapon Coupons contributed by BiTi for the Official L2J Datapack Project
# Visit http://forum.l2jdp.com for more details
import sys
from com.it.br.gameserver.model.quest import State
from com.it.br.gameserver.model.quest import QuestState
from com.it.br.gameserver.model.quest.jython import QuestJython as JQuest

qn = "415_PathToOrcMonk"

POMEGRANATE = 1593
LEATHER_POUCH1 = 1594
LEATHER_POUCH2 = 1595
LEATHER_POUCH3 = 1596
LEATHER_POUCH1FULL = 1597
LEATHER_POUCH2FULL = 1598
LEATHER_POUCH3FULL = 1599
KASHA_BEAR_CLAW = 1600
KASHA_BSPIDER_TALON = 1601
S_SALAMANDER_SCALE = 1602
SCROLL_FIERY_SPIRIT = 1603
ROSHEEKS_LETTER = 1604
GANTAKIS_LETTER = 1605
FIG = 1606
LEATHER_PURSE4 = 1607
LEATHER_POUCH4FULL = 1608
VUKU_TUSK = 1609
RATMAN_FANG = 1610
LANGK_TOOTH = 1611
FELIM_TOOTH = 1612
SCROLL_IRON_WILL = 1613
TORUKUS_LETTER = 1614
KHAVATARI_TOTEM = 1615

class Quest (JQuest) :

 def __init__(self,id,name,descr): JQuest.__init__(self,id,name,descr)

 def onEvent (self,event,st) :
    htmltext = event
    player = st.getPlayer()
    if event == "30587_1" :
          if player.getClassId().getId() != 0x2c :
            if player.getClassId().getId() == 0x2f :
              htmltext = "30587-02a.htm"
              st.exitQuest(1)
            else:
              htmltext = "30587-02.htm"
              st.exitQuest(1)
          else:
            if player.getLevel()<19 :
              htmltext = "30587-03.htm"
            else:
              if st.getQuestItemsCount(KHAVATARI_TOTEM) != 0 :
                htmltext = "30587-04.htm"
              else:
                htmltext = "30587-05.htm"
                return htmltext
    elif event == "1" :
        st.set("id","0")
        st.set("cond","1")
        st.setState(STARTED)
        st.playSound("ItemSound.quest_accept")
        htmltext = "30587-06.htm"
        st.giveItems(POMEGRANATE,1)
    return htmltext


 def onTalk (self,npc,player):
   htmltext = "<html><body>You are either not carrying out your quest or don't meet the criteria.</body></html>"
   st = player.getQuestState(qn)
   if not st : return htmltext

   npcId = npc.getNpcId()
   id = st.getState()
   if npcId != 30587 and id != STARTED : return htmltext

   if id == CREATED :
     st.setState(STARTING)
     st.set("cond","0")
     st.set("onlyone","0")
     st.set("id","0")
   if npcId == 30587 and st.getInt("cond")==0 and st.getInt("onlyone")==0 :
        htmltext = "30587-01.htm"
   elif npcId == 30587 and st.getInt("cond")==0 and st.getInt("onlyone")==1 :
      htmltext = "30587-04.htm"
   elif npcId == 30587 and st.getInt("cond") and st.getQuestItemsCount(SCROLL_FIERY_SPIRIT)==0 and st.getQuestItemsCount(POMEGRANATE)==1 and st.getQuestItemsCount(GANTAKIS_LETTER)==0 and st.getQuestItemsCount(ROSHEEKS_LETTER)==0 and ((st.getQuestItemsCount(LEATHER_POUCH1)+st.getQuestItemsCount(LEATHER_POUCH2)+st.getQuestItemsCount(LEATHER_POUCH3)+st.getQuestItemsCount(LEATHER_POUCH1FULL)+st.getQuestItemsCount(LEATHER_POUCH2FULL)+st.getQuestItemsCount(LEATHER_POUCH3FULL))==0) :
        htmltext = "30587-07.htm"
   elif npcId == 30587 and st.getInt("cond") and st.getQuestItemsCount(SCROLL_FIERY_SPIRIT)==0 and st.getQuestItemsCount(POMEGRANATE)==0 and st.getQuestItemsCount(GANTAKIS_LETTER)==0 and st.getQuestItemsCount(ROSHEEKS_LETTER)==0 and ((st.getQuestItemsCount(LEATHER_POUCH1)+st.getQuestItemsCount(LEATHER_POUCH2)+st.getQuestItemsCount(LEATHER_POUCH3)+st.getQuestItemsCount(LEATHER_POUCH1FULL)+st.getQuestItemsCount(LEATHER_POUCH2FULL)+st.getQuestItemsCount(LEATHER_POUCH3FULL))==1) :
        htmltext = "30587-08.htm"
   elif npcId == 30587 and st.getInt("cond") and st.getQuestItemsCount(SCROLL_FIERY_SPIRIT)==1 and st.getQuestItemsCount(POMEGRANATE)==0 and st.getQuestItemsCount(GANTAKIS_LETTER)==0 and st.getQuestItemsCount(ROSHEEKS_LETTER)==1 and ((st.getQuestItemsCount(LEATHER_POUCH1)+st.getQuestItemsCount(LEATHER_POUCH2)+st.getQuestItemsCount(LEATHER_POUCH3)+st.getQuestItemsCount(LEATHER_POUCH1FULL)+st.getQuestItemsCount(LEATHER_POUCH2FULL)+st.getQuestItemsCount(LEATHER_POUCH3FULL))==0) :
        htmltext = "30587-09.htm"
        st.takeItems(ROSHEEKS_LETTER,1)
        st.giveItems(GANTAKIS_LETTER,1)
        st.set("cond","9")
   elif npcId == 30587 and st.getInt("cond") and st.getQuestItemsCount(SCROLL_FIERY_SPIRIT)==1 and st.getQuestItemsCount(POMEGRANATE)==0 and st.getQuestItemsCount(GANTAKIS_LETTER)==1 and st.getQuestItemsCount(ROSHEEKS_LETTER)==0 and ((st.getQuestItemsCount(LEATHER_POUCH1)+st.getQuestItemsCount(LEATHER_POUCH2)+st.getQuestItemsCount(LEATHER_POUCH3)+st.getQuestItemsCount(LEATHER_POUCH1FULL)+st.getQuestItemsCount(LEATHER_POUCH2FULL)+st.getQuestItemsCount(LEATHER_POUCH3FULL))==0) :
        htmltext = "30587-10.htm"
   elif npcId == 30587 and st.getInt("cond") and st.getQuestItemsCount(SCROLL_FIERY_SPIRIT)==1 and st.getQuestItemsCount(POMEGRANATE)==0 and st.getQuestItemsCount(GANTAKIS_LETTER)==0 and st.getQuestItemsCount(ROSHEEKS_LETTER)==0 and ((st.getQuestItemsCount(LEATHER_POUCH1)+st.getQuestItemsCount(LEATHER_POUCH2)+st.getQuestItemsCount(LEATHER_POUCH3)+st.getQuestItemsCount(LEATHER_POUCH1FULL)+st.getQuestItemsCount(LEATHER_POUCH2FULL)+st.getQuestItemsCount(LEATHER_POUCH3FULL))==0) :
        htmltext = "30587-11.htm"
   elif npcId == 30590 and st.getInt("cond") and st.getQuestItemsCount(POMEGRANATE) :
        htmltext = "30590-01.htm"
        st.takeItems(POMEGRANATE,1)
        st.giveItems(LEATHER_POUCH1,1)
        st.set("cond","2")
   elif npcId == 30590 and st.getInt("cond") and st.getQuestItemsCount(LEATHER_POUCH1) and st.getQuestItemsCount(LEATHER_POUCH1FULL)==0 :
        htmltext = "30590-02.htm"
   elif npcId == 30590 and st.getInt("cond") and st.getQuestItemsCount(LEATHER_POUCH1)==0 and st.getQuestItemsCount(LEATHER_POUCH1FULL) :
        htmltext = "30590-03.htm"
        st.takeItems(LEATHER_POUCH1FULL,1)
        st.giveItems(LEATHER_POUCH2,1)
        st.set("cond","4")
   elif npcId == 30590 and st.getInt("cond") and st.getQuestItemsCount(LEATHER_POUCH2)==1 and st.getQuestItemsCount(LEATHER_POUCH2FULL)==0 :
        htmltext = "30590-04.htm"
   elif npcId == 30590 and st.getInt("cond") and st.getQuestItemsCount(LEATHER_POUCH2)==0 and st.getQuestItemsCount(LEATHER_POUCH2FULL)==1 :
        htmltext = "30590-05.htm"
        st.takeItems(LEATHER_POUCH2FULL,1)
        st.giveItems(LEATHER_POUCH3,1)
        st.set("cond","6")
   elif npcId == 30590 and st.getInt("cond") and st.getQuestItemsCount(LEATHER_POUCH3)==1 and st.getQuestItemsCount(LEATHER_POUCH3FULL)==0 :
        htmltext = "30590-06.htm"
   elif npcId == 30590 and st.getInt("cond") and st.getQuestItemsCount(LEATHER_POUCH3)==0 and st.getQuestItemsCount(LEATHER_POUCH3FULL)==1 :
        htmltext = "30590-07.htm"
        st.takeItems(LEATHER_POUCH3FULL,1)
        st.giveItems(SCROLL_FIERY_SPIRIT,1)
        st.giveItems(ROSHEEKS_LETTER,1)
        st.set("cond","8")
   elif npcId == 30590 and st.getInt("cond") and st.getQuestItemsCount(ROSHEEKS_LETTER)==1 and st.getQuestItemsCount(SCROLL_FIERY_SPIRIT)==1 :
        htmltext = "30590-08.htm"
   elif npcId == 30590 and st.getInt("cond") and st.getQuestItemsCount(ROSHEEKS_LETTER)==0 and st.getQuestItemsCount(SCROLL_FIERY_SPIRIT)==1 :
        htmltext = "30590-09.htm"
   elif npcId == 30501 and st.getInt("cond") and st.getQuestItemsCount(GANTAKIS_LETTER) :
        htmltext = "30501-01.htm"
        st.takeItems(GANTAKIS_LETTER,1)
        st.giveItems(FIG,1)
        st.set("cond","10")
   elif npcId == 30501 and st.getInt("cond") and st.getQuestItemsCount(FIG) and (st.getQuestItemsCount(LEATHER_PURSE4)==0 or st.getQuestItemsCount(LEATHER_POUCH4FULL)==0) :
        htmltext = "30501-02.htm"
   elif npcId == 30501 and st.getInt("cond") and st.getQuestItemsCount(FIG)==0 and (st.getQuestItemsCount(LEATHER_PURSE4)==1 or st.getQuestItemsCount(LEATHER_POUCH4FULL)==1) :
        htmltext = "30501-03.htm"
   elif npcId == 30501 and st.getInt("cond") and st.getQuestItemsCount(SCROLL_IRON_WILL) :
        htmltext = "30501-04.htm"
        st.takeItems(SCROLL_IRON_WILL,1)
        st.takeItems(SCROLL_FIERY_SPIRIT,1)
        st.takeItems(TORUKUS_LETTER,1)
        st.giveItems(KHAVATARI_TOTEM,1)
        st.set("cond","0")
        st.set("onlyone","1")
        st.setState(COMPLETED)
        st.playSound("ItemSound.quest_finish")
   elif npcId == 30591 and st.getInt("cond") and st.getQuestItemsCount(FIG) :
        htmltext = "30591-01.htm"
        st.takeItems(FIG,1)
        st.giveItems(LEATHER_PURSE4,1)
        st.set("cond","11")
   elif npcId == 30591 and st.getInt("cond") and st.getQuestItemsCount(LEATHER_PURSE4) and st.getQuestItemsCount(LEATHER_POUCH4FULL)==0 :
        htmltext = "30591-02.htm"
   elif npcId == 30591 and st.getInt("cond") and st.getQuestItemsCount(LEATHER_PURSE4)==0 and st.getQuestItemsCount(LEATHER_POUCH4FULL)==1 :
        htmltext = "30591-03.htm"
        st.takeItems(LEATHER_POUCH4FULL,1)
        st.giveItems(SCROLL_IRON_WILL,1)
        st.giveItems(TORUKUS_LETTER,1)
        st.set("cond","13")
   elif npcId == 30591 and st.getInt("cond") and st.getQuestItemsCount(SCROLL_IRON_WILL)==1 and st.getQuestItemsCount(TORUKUS_LETTER)==1 :
        htmltext = "30591-04.htm"
   return htmltext

 def onKill(self,npc,player,isPet):
   st = player.getQuestState(qn)
   if not st : return 
   if st.getState() != STARTED : return 
   
   npcId = npc.getNpcId()
   if npcId == 20479 :
        st.set("id","0")
        if st.getInt("cond")and st.getQuestItemsCount(LEATHER_POUCH1) == 1 :
          if st.getQuestItemsCount(KASHA_BEAR_CLAW) == 4 :
            st.takeItems(KASHA_BEAR_CLAW,st.getQuestItemsCount(KASHA_BEAR_CLAW))
            st.takeItems(LEATHER_POUCH1,st.getQuestItemsCount(LEATHER_POUCH1))
            st.giveItems(LEATHER_POUCH1FULL,1)
            st.playSound("ItemSound.quest_middle")
            st.set("cond","3")
          else:
            st.giveItems(KASHA_BEAR_CLAW,1)
            st.playSound("ItemSound.quest_itemget")
   elif npcId == 20415 :
        st.set("id","0")
        if st.getInt("cond") and st.getQuestItemsCount(LEATHER_POUCH3) == 1 :
          if st.getQuestItemsCount(S_SALAMANDER_SCALE) == 4 :
            st.takeItems(S_SALAMANDER_SCALE,st.getQuestItemsCount(S_SALAMANDER_SCALE))
            st.takeItems(LEATHER_POUCH3,st.getQuestItemsCount(LEATHER_POUCH3))
            st.giveItems(LEATHER_POUCH3FULL,1)
            st.playSound("ItemSound.quest_middle")
            st.set("cond","7")
          else:
            st.giveItems(S_SALAMANDER_SCALE,1)
            st.playSound("ItemSound.quest_itemget")
   elif npcId == 20478 :
        st.set("id","0")
        if st.getInt("cond") and st.getQuestItemsCount(LEATHER_POUCH2) == 1 :
          if st.getQuestItemsCount(KASHA_BSPIDER_TALON) == 4 :
            st.takeItems(KASHA_BSPIDER_TALON,st.getQuestItemsCount(KASHA_BSPIDER_TALON))
            st.takeItems(LEATHER_POUCH2,st.getQuestItemsCount(LEATHER_POUCH2))
            st.giveItems(LEATHER_POUCH2FULL,1)
            st.playSound("ItemSound.quest_middle")
            st.set("cond","5")
          else:
            st.giveItems(KASHA_BSPIDER_TALON,1)
            st.playSound("ItemSound.quest_itemget")
   elif npcId == 20017 :
        st.set("id","0")
        if st.getInt("cond") and st.getQuestItemsCount(LEATHER_PURSE4) == 1 and st.getQuestItemsCount(VUKU_TUSK)<3 :
          if st.getQuestItemsCount(RATMAN_FANG)+st.getQuestItemsCount(LANGK_TOOTH)+st.getQuestItemsCount(FELIM_TOOTH)+st.getQuestItemsCount(VUKU_TUSK) >= 11 :
            st.takeItems(VUKU_TUSK,st.getQuestItemsCount(VUKU_TUSK))
            st.takeItems(RATMAN_FANG,st.getQuestItemsCount(RATMAN_FANG))
            st.takeItems(LANGK_TOOTH,st.getQuestItemsCount(LANGK_TOOTH))
            st.takeItems(FELIM_TOOTH,st.getQuestItemsCount(FELIM_TOOTH))
            st.takeItems(LEATHER_PURSE4,1)
            st.giveItems(LEATHER_POUCH4FULL,1)
            st.playSound("ItemSound.quest_middle")
            st.set("cond","12")
          else:
            st.giveItems(VUKU_TUSK,1)
            st.playSound("ItemSound.quest_itemget")
   elif npcId == 20359 :
        st.set("id","0")
        if st.getInt("cond") and st.getQuestItemsCount(LEATHER_PURSE4) == 1 and st.getQuestItemsCount(RATMAN_FANG)<3 :
          if st.getQuestItemsCount(RATMAN_FANG)+st.getQuestItemsCount(LANGK_TOOTH)+st.getQuestItemsCount(FELIM_TOOTH)+st.getQuestItemsCount(VUKU_TUSK) >= 11 :
            st.takeItems(VUKU_TUSK,st.getQuestItemsCount(VUKU_TUSK))
            st.takeItems(RATMAN_FANG,st.getQuestItemsCount(RATMAN_FANG))
            st.takeItems(LANGK_TOOTH,st.getQuestItemsCount(LANGK_TOOTH))
            st.takeItems(FELIM_TOOTH,st.getQuestItemsCount(FELIM_TOOTH))
            st.takeItems(LEATHER_PURSE4,1)
            st.giveItems(LEATHER_POUCH4FULL,1)
            st.playSound("ItemSound.quest_middle")
            st.set("cond","12")
          else:
            st.giveItems(RATMAN_FANG,1)
            st.playSound("ItemSound.quest_itemget")
   elif npcId == 20024 :
        st.set("id","0")
        if st.getInt("cond") and st.getQuestItemsCount(LEATHER_PURSE4) == 1 and st.getQuestItemsCount(LANGK_TOOTH)<3 :
          if st.getQuestItemsCount(RATMAN_FANG)+st.getQuestItemsCount(LANGK_TOOTH)+st.getQuestItemsCount(FELIM_TOOTH)+st.getQuestItemsCount(VUKU_TUSK) >= 11 :
            st.takeItems(VUKU_TUSK,st.getQuestItemsCount(VUKU_TUSK))
            st.takeItems(RATMAN_FANG,st.getQuestItemsCount(RATMAN_FANG))
            st.takeItems(LANGK_TOOTH,st.getQuestItemsCount(LANGK_TOOTH))
            st.takeItems(FELIM_TOOTH,st.getQuestItemsCount(FELIM_TOOTH))
            st.takeItems(LEATHER_PURSE4,1)
            st.giveItems(LEATHER_POUCH4FULL,1)
            st.playSound("ItemSound.quest_middle")
            st.set("cond","12")
          else:
            st.giveItems(LANGK_TOOTH,1)
            st.playSound("ItemSound.quest_itemget")
   elif npcId == 20014 :
        st.set("id","0")
        if st.getInt("cond") and st.getQuestItemsCount(LEATHER_PURSE4) == 1 and st.getQuestItemsCount(FELIM_TOOTH)<3 :
          if st.getQuestItemsCount(RATMAN_FANG)+st.getQuestItemsCount(LANGK_TOOTH)+st.getQuestItemsCount(FELIM_TOOTH)+st.getQuestItemsCount(VUKU_TUSK) >= 11 :
            st.takeItems(VUKU_TUSK,st.getQuestItemsCount(VUKU_TUSK))
            st.takeItems(RATMAN_FANG,st.getQuestItemsCount(RATMAN_FANG))
            st.takeItems(LANGK_TOOTH,st.getQuestItemsCount(LANGK_TOOTH))
            st.takeItems(FELIM_TOOTH,st.getQuestItemsCount(FELIM_TOOTH))
            st.takeItems(LEATHER_PURSE4,1)
            st.giveItems(LEATHER_POUCH4FULL,1)
            st.playSound("ItemSound.quest_middle")
            st.set("cond","12")
          else:
            st.giveItems(FELIM_TOOTH,1)
            st.playSound("ItemSound.quest_itemget")
   return

QUEST       = Quest(415,qn,"Path To Orc Monk")
CREATED     = State('Start', QUEST)
STARTING     = State('Starting', QUEST)
STARTED     = State('Started', QUEST)
COMPLETED   = State('Completed', QUEST)


QUEST.setInitialState(CREATED)
QUEST.addStartNpc(30587)

QUEST.addTalkId(30587)

QUEST.addTalkId(30501)
QUEST.addTalkId(30590)
QUEST.addTalkId(30591)

QUEST.addKillId(20014)
QUEST.addKillId(20017)
QUEST.addKillId(20024)
QUEST.addKillId(20359)
QUEST.addKillId(20415)
QUEST.addKillId(20478)
QUEST.addKillId(20479)