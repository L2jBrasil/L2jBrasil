# Made by Mr. Have fun! - Version 0.5 updated by Censor for www.l2jdp.com 
import sys 
from com.it.br.gameserver.model.quest import State 
from com.it.br.gameserver.model.quest import QuestState 
from com.it.br.gameserver.model.quest.jython import QuestJython as JQuest 
from com.it.br import Config 

qn = "104_SpiritOfMirrors" 

GALLINS_OAK_WAND_ID = 748 
WAND_SPIRITBOUND1_ID = 1135 
WAND_SPIRITBOUND2_ID = 1136 
WAND_SPIRITBOUND3_ID = 1137 
WAND_OF_ADEPT_ID = 747 
SPIRITSHOT_NO_GRADE_FOR_BEGINNERS_ID = 5790 
SPIRITSHOT_NO_GRADE = 2509 
SOULSHOT_NO_GRADE_FOR_BEGINNERS_ID = 5789
SOULSHOT_NO_GRADE = 1835

DROPLIST = { 
27003: (WAND_SPIRITBOUND1_ID), 
27004: (WAND_SPIRITBOUND2_ID), 
27005: (WAND_SPIRITBOUND3_ID) 
} 

# Helper function - If player have all quest items returns 1, otherwise 0 
def HaveAllQuestItems (st) : 
  for mobId in DROPLIST.keys() : 
    if st.getQuestItemsCount(DROPLIST[mobId]) == 0 : 
      return 0 
  return 1 

# Main Quest code 
class Quest (JQuest) : 

 def __init__(self,id,name,descr): 
    JQuest.__init__(self,id,name,descr)
    self.questItemIds = [GALLINS_OAK_WAND_ID, WAND_SPIRITBOUND1_ID, WAND_SPIRITBOUND2_ID, WAND_SPIRITBOUND3_ID]

 def onEvent (self,event,st) : 
    htmltext = event 
    if event == "30017-03.htm" : 
      st.set("cond","1") 
      st.setState(STARTED)
      st.playSound(self.SOUND_QUEST_START) 
      st.giveItems(GALLINS_OAK_WAND_ID,1) 
      st.giveItems(GALLINS_OAK_WAND_ID,1) 
      st.giveItems(GALLINS_OAK_WAND_ID,1) 
    return htmltext 

 def onTalk (self,npc,player): 
   npcId = npc.getNpcId() 
   htmltext = self.NO_QUEST 
   st = player.getQuestState(qn) 
   if not st: return htmltext 
   id = st.getState() 
   if id == CREATED : 
     st.set("cond","0") 
     st.set("onlyone","0") 
   if npcId == 30017 and st.getInt("cond")==0 and st.getInt("onlyone")==0 : 
     if player.getRace().ordinal() != 0 : 
        htmltext = "30017-00.htm" 
     elif player.getLevel() >= 10 : 
        htmltext = "30017-02.htm" 
        return htmltext 
     else: 
        htmltext = "30017-06.htm" 
        st.exitQuest(1) 
   elif npcId == 30017 and st.getInt("cond")==0 and st.getInt("onlyone")==1 : 
      htmltext = self.QUEST_DONE 
   elif id == STARTED : 
     if npcId == 30017 and st.getInt("cond") and st.getQuestItemsCount(GALLINS_OAK_WAND_ID)>=1 and not HaveAllQuestItems(st) : 
        htmltext = "30017-04.htm" 
     elif npcId == 30017 and st.getInt("cond")==3 and HaveAllQuestItems(st) : 
        for mobId in DROPLIST.keys() :
            st.takeItems(DROPLIST[mobId],-1)
        if player.getClassId().isMage() and st.getInt("onlyone") == 0:
          st.giveItems(SPIRITSHOT_NO_GRADE,500)
          if player.getLevel() < 25 and player.isNewbie(): 
            st.rewardItems(SPIRITSHOT_NO_GRADE_FOR_BEGINNERS_ID,3000)
            st.playTutorialVoice("tutorial_voice_027") 
        elif st.getInt("onlyone") == 0:
          st.giveItems(SOULSHOT_NO_GRADE,1000)
        st.giveItems(1060,int(100*Config.RATE_QUESTS_REWARD))     # Lesser Healing Potions
        st.giveItems(WAND_OF_ADEPT_ID,1)
        for item in range(4412,4417) :
            st.giveItems(item,int(10*Config.RATE_QUESTS_REWARD))   # Echo crystals
        htmltext = "30017-05.htm" 
        st.set("cond","0") 
        st.setState(COMPLETED) 
        st.playSound(self.SOUND_QUEST_DONE) 
        st.set("onlyone","1")       
     elif npcId == 30045 and st.getInt("cond") : 
        htmltext = "30045-01.htm" 
        st.set("cond","2") 
     elif npcId == 30043 and st.getInt("cond") : 
        htmltext = "30043-01.htm" 
        st.set("cond","2") 
     elif npcId == 30041 and st.getInt("cond") : 
        htmltext = "30041-01.htm" 
        st.set("cond","2") 
   return htmltext 

 def onKill(self,npc,player,isPet): 
   st = player.getQuestState(qn) 
   if not st: return 
   if st.getState() != STARTED : return 
   npcId = npc.getNpcId() 
   if st.getInt("cond") >= 1 and st.getItemEquipped(7) == GALLINS_OAK_WAND_ID and not st.getQuestItemsCount(DROPLIST[npcId]) : # (7) means weapon slot 
     st.takeItems(GALLINS_OAK_WAND_ID,1) 
     st.giveItems(DROPLIST[npcId],1) 
     if HaveAllQuestItems(st) : 
       st.set("cond","3") 
     st.playSound(self.SOUND_QUEST_MIDDLE)
   return 

QUEST       = Quest(104,qn,"Spirit Of Mirrors") 
CREATED     = State('Start', QUEST) 
STARTED     = State('Started', QUEST) 
COMPLETED   = State('Completed', QUEST) 

QUEST.setInitialState(CREATED) 
QUEST.addStartNpc(30017) 

QUEST.addTalkId(30017) 

QUEST.addTalkId(30041) 
QUEST.addTalkId(30043) 
QUEST.addTalkId(30045) 

for mobId in DROPLIST.keys(): 
  QUEST.addKillId(mobId)
