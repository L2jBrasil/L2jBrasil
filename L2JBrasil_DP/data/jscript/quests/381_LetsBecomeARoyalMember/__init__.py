# Lets Become A Royal Member ver. 0.1 by DrLecter
import sys
from com.it.br import Config
from com.it.br.gameserver.model.quest import State
from com.it.br.gameserver.model.quest import QuestState
from com.it.br.gameserver.model.quest.jython import QuestJython as JQuest

QuestNumber      = 381
QuestName        = "LetsBecomeARoyalMember"
QuestDescription = "Let's become a Royal Member"
qn = "381_LetsBecomeARoyalMember"

#Messages
default = "<html><body>You are either not carrying out your quest or don't meet the criteria.</body></html>"
#Quest items
KAILS_COIN, COIN_ALBUM, MEMBERSHIP_1, CLOVER_COIN, ROYAL_MEMBERSHIP = 5899, 5900, 3813, 7569, 5898
#NPCs
SORINT, SANDRA = 30232, 30090
#MOBs
ANCIENT_GARGOYLE, VEGUS = 21018,27316
#CHANCES (custom values, feel free to change them)
GARGOYLE_CHANCE = 5*Config.RATE_DROP_QUEST
VEGUS_CHANCE = 100*Config.RATE_DROP_QUEST


class Quest (JQuest) :

  def __init__(self,id,name,descr): JQuest.__init__(self,id,name,descr)

  def onEvent (self,event,st) :
      htmltext = event
      if event == "30232-02.htm":
         if st.getPlayer().getLevel() >= 55 and st.getQuestItemsCount(MEMBERSHIP_1) :
            st.set("cond","1")
            st.setState(STARTED)
            st.playSound("ItemSound.quest_accept")
            htmltext = "30232-03.htm"
         else :
            st.exitQuest(1)
      elif event == "30090-02.htm" :
         if st.getInt("cond") == 1 :
            st.set("id","1")
            st.playSound("ItemSound.quest_accept")
         else :
            htmltext = default
      return htmltext

  def onTalk (self,npc,player):
      htmltext = default
      st = player.getQuestState(qn)
      if not st : return htmltext

      npcId = npc.getNpcId()
      id = st.getState()
      if npcId != SORINT and id != STARTED : return htmltext
      
      cond=st.getInt("cond")
      album = st.getQuestItemsCount(COIN_ALBUM)
      if npcId == SORINT :
         if cond == 0 :
            htmltext = "30232-01.htm"
         elif cond == 1 :
            coin = st.getQuestItemsCount(KAILS_COIN)
            if coin and album :
               st.takeItems(KAILS_COIN,-1)
               st.takeItems(COIN_ALBUM,-1)
               st.giveItems(ROYAL_MEMBERSHIP,1)
               st.playSound("ItemSound.quest_finish")
               st.exitQuest(1)
               htmltext = "30232-06.htm"
            elif not album :
               htmltext = "30232-05.htm"
            elif not coin :
               htmltext = "30232-04.htm"
      else :
           clover = st.getQuestItemsCount(CLOVER_COIN)
           if album :
              htmltext = "30090-05.htm"
           else :
              if clover :
                 st.takeItems(CLOVER_COIN,-1)
                 st.giveItems(COIN_ALBUM,1)
                 st.playSound("ItemSound.quest_itemget")
                 htmltext = "30090-04.htm"
              else :
                 if st.getInt("id") == 0 :
                    htmltext = "30090-01.htm"
                 else :
                    htmltext = "30090-03.htm"
      return htmltext

  def onKill(self,npc,player,isPet):
      st = player.getQuestState(qn)
      if not st : return 
      if st.getState() != STARTED : return 
   
      npcId = npc.getNpcId()
      album = st.getQuestItemsCount(COIN_ALBUM)
      coin = st.getQuestItemsCount(KAILS_COIN)
      clover = st.getQuestItemsCount(CLOVER_COIN)
      if npcId == ANCIENT_GARGOYLE and not coin :
         if st.getRandom(100) < GARGOYLE_CHANCE :
            st.giveItems(KAILS_COIN,1)
            if album or clover :
               st.playSound("ItemSound.quest_middle")
            else :
               st.playSound("ItemSound.quest_itemget")
      elif npcId == VEGUS and not (clover + album) and st.getInt("id") :
         if st.getRandom(100) < VEGUS_CHANCE :
            st.giveItems(CLOVER_COIN,1)
            if coin :
               st.playSound("ItemSound.quest_middle")
            else :
               st.playSound("ItemSound.quest_itemget")
      return

QUEST       = Quest(QuestNumber, str(QuestNumber)+"_"+QuestName, QuestDescription)
CREATED     = State('Start',     QUEST)
STARTED     = State('Started',   QUEST)

QUEST.setInitialState(CREATED)
QUEST.addStartNpc(SORINT)

QUEST.addTalkId(SORINT)

QUEST.addTalkId(SANDRA)

QUEST.addKillId(ANCIENT_GARGOYLE)
QUEST.addKillId(VEGUS)