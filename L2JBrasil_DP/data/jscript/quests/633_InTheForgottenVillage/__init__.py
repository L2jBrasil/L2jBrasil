### ---------------------------------------------------------------------------
###  Create by Skeleton!!!
### ---------------------------------------------------------------------------
import sys
from com.it.br.gameserver.model.quest import State
from com.it.br.gameserver.model.quest import QuestState
from com.it.br.gameserver.model.quest.jython import QuestJython as JQuest

qn = "633_InTheForgottenVillage"

#NPC
MINA = 31388
#ITEMS
RIB_BONE = 7544
Z_LIVER = 7545
# Mobid : DROP CHANCES
DAMOBS = {
    21557 : 328,#Bone Snatcher
    21558 : 328,#Bone Snatcher
    21559 : 337,#Bone Maker
    21560 : 337,#Bone Shaper
    21563 : 342,#Bone Collector
    21564 : 348,#Skull Collector
    21565 : 351,#Bone Animator
    21566 : 359,#Skull Animator
    21567 : 359,#Bone Slayer
    21572 : 365,#Bone Sweeper
    21574 : 383,#Bone Grinder
    21575 : 383,#Bone Grinder
    21580 : 385,#Bone Caster
    21581 : 395,#Bone Puppeteer
    21583 : 397,#Bone Scavenger
    21584 : 401 #Bone Scavenger
    }
UNDEADS = {
    21553 : 347,#Trampled Man
    21554 : 347,#Trampled Man
    21561 : 450,#Sacrificed Man
    21578 : 501,#Behemoth Zombie
    21596 : 359,#Requiem Lord
    21597 : 370,#Requiem Behemoth
    21598 : 441,#Requiem Behemoth
    21599 : 395,#Requiem Priest
    21600 : 408,#Requiem Behemoth
    21601 : 411 #Requiem Behemoth
    }

class Quest (JQuest):
    
    def __init__(self,id,name,descr): JQuest.__init__(self,id,name,descr)    
    
    def onEvent (self,event,st):
        htmltext = event
        if event == "accept" :
            st.set("cond","1")                        
            st.setState(STARTED)
            st.playSound("ItemSound.quest_accept")
            htmltext = "31388-04.htm"
        if event == "quit":
            st.takeItems(RIB_BONE, -1)
            st.playSound("ItemSound.quest_finish")
            htmltext = "31388-10.htm"
            st.exitQuest(1)
        elif event == "stay":
            htmltext = "31388-07.htm"
        elif event == "reward":
            if st.getInt("cond") == 2:
                if st.getQuestItemsCount(RIB_BONE) >= 200:
                    st.takeItems(RIB_BONE, 200)
                    st.giveItems(57, 25000)
                    st.addExpAndSp(305235, 0)
                    st.playSound("ItemSound.quest_finish")
                    st.set("cond","1")
                    htmltext = "31388-08.htm"
                else :
                    htmltext = "31388-09.htm"
        return htmltext
    

    def onTalk (self,npc,player):        
        htmltext = "<html><body>You are either not carrying out your quest or don't meet the criteria.</body></html>"
        st = player.getQuestState(qn)
        if not st: return
        npcId = npc.getNpcId()
        if npcId == MINA:
            id = st.getState()
            cond = st.getInt("cond")
            if id == CREATED:
                if st.getPlayer().getLevel() > 64:
                    htmltext = "31388-01.htm"
                else:
                    htmltext = "31388-03.htm"
                    st.exitQuest(1)        
            elif cond == 1:
                htmltext = "31388-06.htm"            
            elif cond == 2:
                htmltext = "31388-05.htm"                
        return htmltext

    def onKill(self,npc,player,isPet):        
        npcId = npc.getNpcId()
        if npcId in UNDEADS.keys():            
            partyMember = self.getRandomPartyMemberState(player, STARTED)
            if not partyMember: return
            st = partyMember.getQuestState(qn)
            if not st : return
            if st.getRandom(1000) < UNDEADS[npcId]:  
                st.giveItems(Z_LIVER, 1)  
                st.playSound("ItemSound.quest_itemget")  
        elif npcId in DAMOBS.keys():
            partyMember = self.getRandomPartyMember(player, "cond", "1")
            if not partyMember: return                
            st = partyMember.getQuestState(qn)
            if not st : return
            if st.getRandom(1000) < DAMOBS[npcId]:                  
                st.giveItems(RIB_BONE, 1)  
                if st.getQuestItemsCount(RIB_BONE) == 200:  
                    st.set("cond","2")  
                    st.playSound("ItemSound.quest_middle")  
                else:  
                    st.playSound("ItemSound.quest_itemget") 
        return        

QUEST       = Quest(633, qn, "In The Forgotten Village")
CREATED     = State('Start', QUEST)
STARTED     = State('Started', QUEST)

for i in DAMOBS.keys():
    QUEST.addKillId(i)
for i in UNDEADS.keys():
    QUEST.addKillId(i)
    
QUEST.setInitialState(CREATED)
QUEST.addStartNpc(MINA)
QUEST.addTalkId(MINA)