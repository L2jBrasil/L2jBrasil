# Made by disKret, as a part of the
# Official L2J Datapack Project, please visit
# http://forum.l2jdp.com to meet the community behind it, or
# http://l2jdp.com/trac if you need to report a bug.
import sys
from com.it.br.gameserver.model.quest import State
from com.it.br.gameserver.model.quest import QuestState
from com.it.br.gameserver.model.quest.jython import QuestJython as JQuest

qn = "39_RedEyedInvaders"

#NPC
BABENCO = 30334
BATHIS = 30332

#MOBS
M_LIZARDMAN = 20919
M_LIZARDMAN_SCOUT = 20920
M_LIZARDMAN_GUARD = 20921
ARANEID = 20925

#QUEST DROPS
BLACK_BONE_NECKLACE,RED_BONE_NECKLACE,INCENSE_POUCH,GEM_OF_MAILLE = range(7178,7182)

NECKLACE={M_LIZARDMAN_GUARD:[RED_BONE_NECKLACE,100,BLACK_BONE_NECKLACE,"3"],
          M_LIZARDMAN:[BLACK_BONE_NECKLACE,100,RED_BONE_NECKLACE,"3"],
          M_LIZARDMAN_SCOUT:[BLACK_BONE_NECKLACE,100,RED_BONE_NECKLACE,"3"]
}
DROPLIST={ARANEID:[GEM_OF_MAILLE,30,INCENSE_POUCH,"5"],
          M_LIZARDMAN_GUARD:[INCENSE_POUCH,30,GEM_OF_MAILLE,"5"],
          M_LIZARDMAN_SCOUT:[INCENSE_POUCH,30,GEM_OF_MAILLE,"5"]
}
#REWARDS
GREEN_COLORED_LURE_HG = 6521
BABY_DUCK_RODE = 6529
FISHING_SHOT_NG = 6535

def drop(partyMember,array) :
    item,max,item2,condition = array
    st = partyMember.getQuestState(qn)
    count = st.getQuestItemsCount(item)
    numItems,chance = divmod(100*Config.RATE_QUESTS_REWARD,100)
    if st.getRandom(100) < chance :
        numItems = numItems + 1
    if count+numItems > max :
        numItems = max - count
    st.giveItems(item,int(numItems))
    if st.getQuestItemsCount(item) == max and st.getQuestItemsCount(item2) == max:
        st.playSound("ItemSound.quest_middle")
        st.set("cond",condition)
    else:
        st.playSound("ItemSound.quest_itemget")
    return
    
class Quest (JQuest) :

 def __init__(self,id,name,descr): JQuest.__init__(self,id,name,descr)

 def onEvent (self,event,st) :
   htmltext = event
   cond = st.getInt("cond")
   if st.getState() != COMPLETED :
    if event == "30334-1.htm" and cond == 0 :
     st.set("cond","1")
     st.setState(STARTED)
     st.playSound("ItemSound.quest_accept")
    elif event == "30332-1.htm" and cond == 1 :
     st.set("cond","2")
    elif event == "30332-3.htm" :
     if st.getQuestItemsCount(BLACK_BONE_NECKLACE) == st.getQuestItemsCount(RED_BONE_NECKLACE) == 100 and cond == 3:
       st.takeItems(BLACK_BONE_NECKLACE,100)
       st.takeItems(RED_BONE_NECKLACE,100)       
       st.set("cond","4")
     else :
       htmltext = "You don't have required items"
    elif event == "30332-5.htm" :
     if st.getQuestItemsCount(INCENSE_POUCH) == st.getQuestItemsCount(GEM_OF_MAILLE) == 30 and cond == 5 :
       st.takeItems(INCENSE_POUCH,30)
       st.takeItems(GEM_OF_MAILLE,30)  
       st.giveItems(GREEN_COLORED_LURE_HG,60)
       st.giveItems(BABY_DUCK_RODE,1)
       st.giveItems(FISHING_SHOT_NG,500)
       st.unset("cond")
       st.playSound("ItemSound.quest_finish")
       st.setState(COMPLETED)
     else :
       htmltext = "You don't have required items"
   return htmltext

 def onTalk (self,npc,player):
   htmltext = "<html><body>You are either not carrying out your quest or don't meet the criteria.</body></html>"
   st = player.getQuestState(qn)
   if not st : return htmltext
   npcId = npc.getNpcId()
   id = st.getState()
   cond = st.getInt("cond")
   if id == COMPLETED :
       htmltext = "<html><body>This quest has already been completed.</body></html>"
   elif npcId == BABENCO :
     if id == CREATED :     
       if player.getLevel() >= 20 :
         htmltext = "30334-0.htm"
       else :
         st.exitQuest(1)
         htmltext = "30334-2.htm"
     else :
       htmltext = "30334-3.htm"
   elif npcId == BATHIS and id == STARTED:
     if cond == 1 :
       htmltext = "30332-0.htm"
     elif st.getQuestItemsCount(BLACK_BONE_NECKLACE) == st.getQuestItemsCount(RED_BONE_NECKLACE) == 100 :
       htmltext = "30332-2.htm"
     elif st.getQuestItemsCount(INCENSE_POUCH) == st.getQuestItemsCount(GEM_OF_MAILLE) == 30 :
       htmltext = "30332-4.htm"
   return htmltext

 def onKill(self,npc,player,isPet):
   npcId = npc.getNpcId()
   partyMember = self.getRandomPartyMember(player,"2")
   if (partyMember and npcId != ARANEID) :
       drop(partyMember,NECKLACE[npcId])
   else:
       partyMember = self.getRandomPartyMember(player,"4")
       if (partyMember and npcId != M_LIZARDMAN) :     
           drop(partyMember,DROPLIST[npcId])
   return

QUEST       = Quest(39,qn,"Red Eyed Invaders")
CREATED     = State('Start', QUEST)
STARTED     = State('Started', QUEST)
COMPLETED   = State('Completed', QUEST)

QUEST.setInitialState(CREATED)
QUEST.addStartNpc(BABENCO)
QUEST.addTalkId(BABENCO)
QUEST.addTalkId(BATHIS)

QUEST.addKillId(M_LIZARDMAN)
QUEST.addKillId(M_LIZARDMAN_SCOUT)
QUEST.addKillId(M_LIZARDMAN_GUARD)
QUEST.addKillId(ARANEID)