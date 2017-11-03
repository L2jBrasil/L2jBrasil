# Kail's Magic Coin ver. 0.1 by DrLecter
import sys
from com.it.br import Config
from com.it.br.gameserver.model.quest import State
from com.it.br.gameserver.model.quest import QuestState
from com.it.br.gameserver.model.quest.jython import QuestJython as JQuest

qn = "382_KailsMagicCoin"

#Messages
default = "<html><body>You are either not carrying out your quest or don't meet the criteria.</body></html>"
#Quest items
ROYAL_MEMBERSHIP = 5898
#NPCs
VERGARA = 30687
#MOBs and CHANCES
MOBS={21017:[5961],21019:[5962],21020:[5963],21022:[5961,5962,5963]}
CHANCE = 10
MAX = 100

class Quest (JQuest) :

  def __init__(self,id,name,descr): JQuest.__init__(self,id,name,descr)

  def onEvent (self,event,st) :
      htmltext = event
      if event == "30687-03.htm":
         if st.getPlayer().getLevel() >= 55 and st.getQuestItemsCount(ROYAL_MEMBERSHIP) :
            st.set("cond","1")
            st.setState(STARTED)
            st.playSound("ItemSound.quest_accept")
         else :
            htmltext = "30687-01.htm"
            st.exitQuest(1)
      return htmltext

  def onTalk (self,npc,player):
      htmltext = default
      st = player.getQuestState(qn)
      if not st : return htmltext
      npcId = npc.getNpcId()
      id = st.getState()
      cond=st.getInt("cond")
      if st.getQuestItemsCount(ROYAL_MEMBERSHIP) == 0 or player.getLevel() < 55 :
         htmltext = "30687-01.htm"
         st.exitQuest(1)
      else :
         if cond == 0 :
            htmltext = "30687-02.htm"
         else :
            htmltext = "30687-04.htm"
      return htmltext

  def onKill(self,npc,player,isPet):
      st = player.getQuestState(qn)
      if not st : return 
      if st.getState() != STARTED : return 
      numItems,chance = divmod(CHANCE*Config.RATE_DROP_QUEST,MAX)
      if st.getQuestItemsCount(ROYAL_MEMBERSHIP) :
         if st.getRandom(MAX) < chance :
            numItems = numItems + 1
         npcId = npc.getNpcId()
         if numItems != 0 :
            st.giveItems(MOBS[npcId][st.getRandom(len(MOBS[npcId]))],int(numItems))
            st.playSound("ItemSound.quest_itemget")
      return

QUEST       = Quest(382, qn, "Kail's Magic Coin")
CREATED     = State('Start',     QUEST)
STARTED     = State('Started',   QUEST)

QUEST.setInitialState(CREATED)
QUEST.addStartNpc(VERGARA)

QUEST.addTalkId(VERGARA)

for npc in MOBS.keys():
    QUEST.addKillId(npc)