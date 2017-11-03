# Made by Kerberos
# this script is part of the Official L2J Datapack Project.
# Visit http://www.l2jdp.com/forum/ for more details.
import sys
from com.it.br import Config
from com.it.br.gameserver.model.quest import State
from com.it.br.gameserver.model.quest import QuestState
from com.it.br.gameserver.model.quest.jython import QuestJython as JQuest

qn = "648_AnIceMerchantsDream"

#NPCs
Rafforty = 32020
Ice_Shelf = 32023

#MOBs
MOBS = range(22080,22095)+range(22096,22099)

#Items
Hemocyte = 8057
Silver_Ice = 8077
Black_Ice = 8078

class Quest (JQuest) :
 def __init__(self,id,name,descr):
    JQuest.__init__(self,id,name,descr)

 def onEvent (self,event,st) :
    htmltext = event
    if event == "32020-02.htm" :
       st.setState(STARTED)
       st.playSound("ItemSound.quest_accept")
       st.set("cond","1")
    elif event == "32020-07.htm" :
       silver = st.getQuestItemsCount(Silver_Ice)
       black = st.getQuestItemsCount(Black_Ice)
       r1 = silver * 300
       r2 = black * 1200
       reward = r1 + r2
       st.giveItems(57,reward)
       st.takeItems(Silver_Ice,silver)
       st.takeItems(Black_Ice,black)
    elif event == "32020-09.htm" :
       st.exitQuest(1)
       st.playSound("ItemSound.quest_finish")
    elif event == "32023-04.htm" :
       st.playSound("ItemSound2.broken_key")
       st.takeItems(Silver_Ice,1)
    elif event == "32023-05.htm" :
       if st.getRandom(100) <= 25 :
          st.giveItems(Black_Ice,1)
          st.playSound("ItemSound3.sys_enchant_sucess")
       else:
          htmltext = "32023-06.htm"
          st.playSound("ItemSound3.sys_enchant_failed")
    return htmltext

 def onTalk (self,npc,player):
    st = player.getQuestState(qn)
    htmltext = "<html><body>You are either not on a quest that involves this NPC, or you don't meet this NPC's minimum quest requirements.</body></html>" 
    if not st: return htmltext
    npcId = npc.getNpcId()
    id = st.getState()
    cond = st.getInt("cond")
    silver = st.getQuestItemsCount(Silver_Ice)
    black = st.getQuestItemsCount(Black_Ice)
    if npcId == Rafforty :
       if id == CREATED :
          if player.getLevel() >= 53 :
             htmltext = "32020-01.htm"
          else :
             htmltext = "32020-00.htm"
             st.exitQuest(1)
       elif cond == 1:
          if silver or black :
             st2 = player.getQuestState("115_TheOtherSideOfTruth")
             htmltext = "32020-05.htm"
             if st2 :
                if st2.getState() == COMPLETED :
                   htmltext = "32020-10.htm"
                   st.playSound("ItemSound.quest_middle")
                   st.set("cond","2")
          else:
             htmltext = "32020-04.htm"
       elif cond == 2:
          if silver or black :
             htmltext = "32020-10.htm"
          else:
             htmltext = "32020-04a.htm"
    elif npcId == Ice_Shelf :
       if id == CREATED :
          htmltext = "32023-00.htm"
       else:
          if silver > 0 :
             htmltext = "32023-02.htm"
          else:
             htmltext = "32023-01.htm"
    return htmltext

 def onKill(self,npc,player,isPet):
    partyMember = self.getRandomPartyMemberState(player, STARTED)
    if not partyMember : return
    st = partyMember.getQuestState(qn)
    if st :
        chance = int((npc.getNpcId() - 22050)*Config.RATE_DROP_QUEST)
        numItems, chance = divmod(chance,100)
        random = st.getRandom(100)
        if random <= chance:
           numItems += 1
        if int(numItems) != 0 :
           st.giveItems(Silver_Ice,int(numItems))
           st.playSound("ItemSound.quest_itemget")
    #solo part
    st = player.getQuestState(qn)
    if st:
        cond=st.getInt("cond")
        random = st.getRandom(100)
        if cond == 2 and random <=10:
            st.giveItems(Hemocyte,1)
            st.playSound("ItemSound.quest_itemget")
    return  

QUEST = Quest(648,qn,"An Ice Merchant's Dream")
CREATED     = State('Start', QUEST)
STARTED     = State('Started', QUEST)
COMPLETED   = State('Completed', QUEST)

QUEST.setInitialState(CREATED)

QUEST.addStartNpc(Rafforty)
QUEST.addStartNpc(Ice_Shelf)
QUEST.addTalkId(Rafforty) 
QUEST.addTalkId(Ice_Shelf)

for m in MOBS:
   QUEST.addKillId(m)