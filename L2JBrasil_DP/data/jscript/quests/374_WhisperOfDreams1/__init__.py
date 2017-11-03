# Whisper of Dreams, part 1 version 0.1 
# by DrLecter
import sys
from com.it.br import Config
from com.it.br.gameserver.model.quest import State
from com.it.br.gameserver.model.quest import QuestState
from com.it.br.gameserver.model.quest.jython import QuestJython as JQuest
#Quest info
QUEST_NUMBER,QUEST_NAME,QUEST_DESCRIPTION = 374,"WhisperOfDreams1","Whisper of Dreams, part 1"
qn = "374_WhisperOfDreams1"

#Variables
#Quest items drop rate
DROP_RATE   = 50*Config.RATE_DROP_QUEST
DROP_MAX = 100 #in % unless you change this
#Mysterious Stone drop rate
DROP_RATE_2 = 1*Config.RATE_DROP_QUEST
DROP_MAX_2 = 1000 # default: ~ 1/1000
#Rewards
SHOP_LIST={
5485:["etc_leather_yellow_i00",4,10450,"Sealed Tallum Tunic Textures"    ],# 4xTallum Tunic Textures: 10450a
5486:["etc_leather_gray_i00",  3,2950,"Sealed Dark Crystal Robe Fabrics"],
5487:["etc_leather_gray_i00",  2,18050,"Sealed Robe of Nightmare Fabrics"],
5488:["etc_leather_gray_i00",  2,18050,"Sealed Majestic Robe Fabrics"   ],
5489:["etc_leather_gray_i00",  6,15550,"Sealed Tallum Stockings Fabrics"] 
}
 
ADENA_X=int(Config.RATE_DROP_ADENA)
 
#Quest items
CB_TOOTH, DW_LIGHT, SEALD_MSTONE, MSTONE = range(5884,5888)
#Messages
default   = "<html><body>You are either not carrying out your quest or don't meet the criteria.</body></html>"
 
#NPCs
MANAKIA,TORAI = 30515, 30557
 
#Mobs & Drop
DROPLIST = {20620:[CB_TOOTH,"awaitTooth"],20621:[DW_LIGHT,"awaitLight"]}
 
def render_shop() :
    html = "<html><body><font color=\"LEVEL\">Robe Armor Fabrics:</font><table border=0 width=300>"
    for i in SHOP_LIST.keys() :
       html += "<tr><td width=35 height=45><img src=icon."+SHOP_LIST[i][0]+" width=32 height=32 align=left></td><td width=365 valign=top><table border=0 width=100%>"
       html += "<tr><td><a action=\"bypass -h Quest 374_WhisperOfDreams1 "+str(i)+"\"><font color=\"FFFFFF\">"+SHOP_LIST[i][3]+" x"+str(SHOP_LIST[i][1])+"</font></a></td></tr>"
       html += "<tr><td><a action=\"bypass -h Quest 374_WhisperOfDreams1 "+str(i)+"\"><font color=\"B09878\">"+str(SHOP_LIST[i][2]*ADENA_X)+" adena</font></a></td></tr></table></td></tr>"
    html += "</table></body></html>"
    return html
 
class Quest (JQuest) :
 
 def __init__(self,id,name,descr): JQuest.__init__(self,id,name,descr)
 
 def onEvent (self,event,st) :
    id = st.getState() 
    htmltext = event
    if event == "30515-4.htm" :
       st.setState(STARTING)
       st.set("awaitSealedMStone","1")
       st.set("awaitTooth","1")
       st.set("awaitLight","1")
       st.set("cond","1")
       st.playSound("ItemSound.quest_accept")
    elif event == "30515-5.htm" :
       st.takeItems(CB_TOOTH,-1)
       st.takeItems(DW_LIGHT,-1)
       st.takeItems(SEALD_MSTONE,-1)
       st.exitQuest(1)
    elif event == "30515-6.htm" :
       if st.getQuestItemsCount(CB_TOOTH)==st.getQuestItemsCount(DW_LIGHT)==65 :
          st.set("allow","1")
          st.takeItems(CB_TOOTH,-1)
          st.takeItems(DW_LIGHT,-1)
          st.set("awaitTooth","1")
          st.set("awaitLight","1")
          htmltext = "30515-7.htm"
    elif event == "30515-8.htm" :
       if st.getQuestItemsCount(SEALD_MSTONE) :
          if id == STARTING :
             st.setState(STARTED)
             st.set("cond","2")
             htmltext = "30515-9.htm"
          elif id == STARTED :
             htmltext = "30515-10.htm"
    elif event == "buy" :
       htmltext = render_shop()
    elif int(event) in SHOP_LIST.keys() :
       st.set("allow","0")
       st.giveItems(57,SHOP_LIST[int(event)][2])
       st.giveItems(int(event),SHOP_LIST[int(event)][1])
       st.playSound("ItemSound.quest_finish")
       htmltext = "30515-11.htm"
    return htmltext
 
 def onTalk (self,npc,player):
   htmltext = default
   st = player.getQuestState(qn)
   if not st : return htmltext

   npcId = npc.getNpcId()
   id = st.getState()
   if npcId != MANAKIA and id != STARTED : return htmltext

   if npcId == MANAKIA:
      if id == CREATED :
         st.set("cond","0")
         st.set("allow","0")
         htmltext = "30515-1.htm"
         if player.getLevel() < 56 :
            st.exitQuest(1)
            htmltext = "30515-2.htm"
      else :
         if st.getInt("allow") :
            htmltext = "30515-3.htm"
         else :
            htmltext = "30515-3a.htm"
   elif npcId == TORAI :
      if st.getQuestItemsCount(SEALD_MSTONE) :
         htmltext = "30557-1.htm"
         st.takeItems(SEALD_MSTONE,1)
         st.giveItems(MSTONE,1)
         st.set("cond","3")
         st.playSound("ItemSound.quest_middle")
   return htmltext
 
 def onKill(self,npc,player,isPet) :
     #both mobs may give SEALD_MSTONE to a player
     partyMember = self.getRandomPartyMember(player,"awaitSealedMStone","1")
     if partyMember :
        st = partyMember.getQuestState(qn)
        if st.getRandom(DROP_MAX_2) < DROP_RATE_2  and not st.getQuestItemsCount(SEALD_MSTONE) :
           st.giveItems(SEALD_MSTONE,1)
           st.unset("awaitSealedMStone")
           st.playSound("ItemSound.quest_middle")

     #also, each mob might give a CB_TOOTH or DW_LIGHT
     npcId = npc.getNpcId()
     item, partyCond = DROPLIST[npcId]
     partyMember = self.getRandomPartyMember(player,partyCond,"1")
     if partyMember :
         st = partyMember.getQuestState(qn)
         count = st.getQuestItemsCount(item)
         if count < 65 and st.getRandom(DROP_MAX) < DROP_RATE :
            st.giveItems(item,1)
            if count + 1 >= 65 :
               st.playSound("ItemSound.quest_middle")
               st.unset(partyCond)
            else :
               st.playSound("ItemSound.quest_itemget")
     return  
 
# Quest class and state definition
QUEST       = Quest(QUEST_NUMBER, str(QUEST_NUMBER)+"_"+QUEST_NAME, QUEST_DESCRIPTION)
 
CREATED     = State('Start',     QUEST)
STARTING    = State('Starting',  QUEST)
STARTED     = State('Started',   QUEST)
COMPLETED   = State('Completed', QUEST)
 
QUEST.setInitialState(CREATED)
 
# Quest NPC starter initialization
QUEST.addStartNpc(MANAKIA)
# Quest initialization
QUEST.addTalkId(MANAKIA)

QUEST.addTalkId(TORAI)
 
for i in DROPLIST.keys() :
  QUEST.addKillId(i)