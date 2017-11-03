# Weapon SA Quest Written By MickyLee
# rewritten by Questdevs Team
import sys
from com.it.br.gameserver.model.quest import State
from com.it.br.gameserver.model.quest import QuestState
from com.it.br.gameserver.model.quest.jython import QuestJython as JQuest

qn = "350_EnhanceYourWeapon"

NPC=[30115,30856,30194]

RED_SOUL_CRYSTAL0_ID,RED_SOUL_CRYSTAL1_ID,RED_SOUL_CRYSTAL2_ID,RED_SOUL_CRYSTAL3_ID,\
RED_SOUL_CRYSTAL4_ID,RED_SOUL_CRYSTAL5_ID,RED_SOUL_CRYSTAL6_ID,RED_SOUL_CRYSTAL7_ID,\
RED_SOUL_CRYSTAL8_ID,RED_SOUL_CRYSTAL9_ID,RED_SOUL_CRYSTAL10_ID,GREEN_SOUL_CRYSTAL0_ID,\
GREEN_SOUL_CRYSTAL1_ID,GREEN_SOUL_CRYSTAL2_ID,GREEN_SOUL_CRYSTAL3_ID,GREEN_SOUL_CRYSTAL4_ID,\
GREEN_SOUL_CRYSTAL5_ID,GREEN_SOUL_CRYSTAL6_ID,GREEN_SOUL_CRYSTAL7_ID,GREEN_SOUL_CRYSTAL8_ID,\
GREEN_SOUL_CRYSTAL9_ID,GREEN_SOUL_CRYSTAL10_ID,BLUE_SOUL_CRYSTAL0_ID,BLUE_SOUL_CRYSTAL1_ID,\
BLUE_SOUL_CRYSTAL2_ID,BLUE_SOUL_CRYSTAL3_ID,BLUE_SOUL_CRYSTAL4_ID,BLUE_SOUL_CRYSTAL5_ID,\
BLUE_SOUL_CRYSTAL6_ID,BLUE_SOUL_CRYSTAL7_ID,BLUE_SOUL_CRYSTAL8_ID,BLUE_SOUL_CRYSTAL9_ID,\
BLUE_SOUL_CRYSTAL10_ID,RED_SOUL_CRYSTALX_ID,GREEN_SOUL_CRYSTALX_ID,BLUE_SOUL_CRYSTALX_ID = range(4629,4665)

def check(st) :
    for i in range(4629,4665) :
       if st.getQuestItemsCount(i)>0 :
         return True
    return False

class Quest (JQuest) :

 def __init__(self,id,name,descr): JQuest.__init__(self,id,name,descr)

 def onEvent (self,event,st) :
    htmltext = event
    if event in ["30115-04.htm","30856-04.htm","30194-04.htm"] :
        st.set("cond","1")
        st.setState(STARTED)
        st.playSound("ItemSound.quest_accept")
    elif event in ["30115-09.htm","30856-09.htm","30194-09.htm"] :
        st.giveItems(RED_SOUL_CRYSTAL0_ID,1)
    elif event in ["30115-10.htm","30856-10.htm","30194-10.htm"] :
        st.giveItems(GREEN_SOUL_CRYSTAL0_ID,1)
    elif event in ["30115-11.htm","30856-11.htm","30194-11.htm"] :
        st.giveItems(BLUE_SOUL_CRYSTAL0_ID,1)
    elif event == "exit.htm" :
        st.exitQuest(1)
    return htmltext

 def onTalk (self,npc,player):
   htmltext = "<html><body>You are either not carrying out your quest or don't meet the criteria.</body></html>"
   st = player.getQuestState(qn)
   if not st : return htmltext

   npcId = str(npc.getNpcId())
   id = st.getState()
   if id == CREATED :
     st.set("cond","0")
   if st.getInt("cond") == 0:   
     htmltext = npcId+"-01.htm"
   elif check(st) :
     htmltext = npcId+"-03.htm"
   elif st.getQuestItemsCount(RED_SOUL_CRYSTAL0_ID) == st.getQuestItemsCount(GREEN_SOUL_CRYSTAL0_ID) == st.getQuestItemsCount(BLUE_SOUL_CRYSTAL0_ID) == 0 :
     htmltext = npcId+"-21.htm"
   return htmltext

QUEST       = Quest(350,qn,"Enhance Your Weapon")
CREATED     = State('Start', QUEST)
STARTING     = State('Starting', QUEST)
STARTED     = State('Started', QUEST)

QUEST.setInitialState(CREATED)

for npcId in NPC:
  QUEST.addStartNpc(npcId)
  QUEST.addTalkId(npcId)