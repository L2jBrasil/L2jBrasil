import sys
from com.it.br.gameserver.ai import CtrlIntention
from com.it.br.gameserver.model.quest import State
from com.it.br.gameserver.model.quest import QuestState
from com.it.br.gameserver.model.quest.jython import QuestJython as JQuest
from com.it.br.gameserver.network.serverpackets import CreatureSay

qn = "114_ResurrectionOfAnOldManager"

#NPCs
Newyear = 31961
Yumi    = 32041
Stones  = 32046
Wendy   = 32047
Box     = 32050

#Mobs
Guardian = 27318

#Items
Detector   = 8090
Detector2  = 8091
Starstone  = 8287
Letter     = 8288
Starstone2 = 8289

class Quest (JQuest) : 

 def __init__(self,id,name,descr):
    JQuest.__init__(self,id,name,descr)
    self.isSpawned = 0
    self.questItemIds = [Starstone,Detector,Detector2,Letter,Starstone2]

 def activateDetector(self, npc, player, st) :
    for obj in npc.getKnownList().getKnownObjects().values() :
       if obj != None :
          if obj == player and st.getInt("cond") == 17:
             st.playSound("ItemSound.quest_middle")
             st.takeItems(Detector,1)
             st.giveItems(Detector2,1)
             st.set("cond","18")
             return "The radio signal detector is responding. # A suspicious pile of stones catches your eye."
    return

 def onEvent(self, event, st):
    htmltext = event
    if event == "31961-02.htm" :
       st.set("cond","22")
       st.takeItems(Letter,1)
       st.giveItems(Starstone2,1)
       st.playSound("ItemSound.quest_middle")
    if event == "32041-02.htm" :
       st.setState(STARTED)
       st.playSound("ItemSound.quest_accept")
       st.set("cond","1")
       st.set("talk","0")
    elif event == "32041-06.htm" :
       st.set("talk","1")
    elif event == "32041-07.htm" :
       st.set("cond","2")
       st.playSound("ItemSound.quest_middle")
       st.set("talk","0")
    elif event == "32041-10.htm" :
       choice = st.getInt("choice")
       if choice == 1 :
          htmltext = "32041-10.htm"
       elif choice == 2 :
          htmltext = "32041-10a.htm"
       elif choice == 3 :
          htmltext = "32041-10b.htm"
    elif event == "32041-11.htm" :
       st.set("talk","1")
    elif event == "32041-18.htm" :
       st.set("talk","2")
    elif event == "32041-20.htm" :
       st.set("cond","6")
       st.playSound("ItemSound.quest_middle")
       st.set("talk","0")
    elif event == "32041-25.htm" :
       st.set("cond","17")
       st.playSound("ItemSound.quest_middle")
       st.giveItems(Detector,1)
    elif event == "32041-28.htm" :
       st.takeItems(Detector2,1)
       st.set("talk","1")
    elif event == "32041-31.htm" :
       choice = st.getInt("choice")
       if choice > 1 :
          htmltext = "32041-37.htm"
    elif event == "32041-32.htm" :
       st.set("cond","21")
       st.giveItems(Letter,1)
       st.playSound("ItemSound.quest_middle")
    elif event == "32041-36.htm" :
       st.set("cond","20")
       st.playSound("ItemSound.quest_middle")
    elif event == "32046-02.htm" :
       st.set("cond","19")
       st.playSound("ItemSound.quest_middle")
    elif event == "32046-06.htm" :
       st.exitQuest(False)
       st.addExpAndSp(410358,32060)
       st.playSound("ItemSound.quest_finish")
    elif event == "32047-01.htm" :
       if st.getInt("talk") + st.getInt("talk1") == 2:
          htmltext = "32047-04.htm"
       elif st.getInt("talk") + st.getInt("talk1") + st.getInt("talk2")== 6:
          htmltext = "32047-08.htm"
    elif event == "32047-02.htm" :
       if st.getInt("talk") == 0 :
          st.set("talk","1")
    elif event == "32047-03.htm" :
       if st.getInt("talk1") == 0 :
          st.set("talk1","1")
    elif event == "32047-05.htm" :
       st.set("cond","3")
       st.playSound("ItemSound.quest_middle")
       st.set("talk","0")
       st.set("choice","1")
       st.unset("talk1")
    elif event == "32047-06.htm" :
       st.set("cond","4")
       st.playSound("ItemSound.quest_middle")
       st.set("talk","0")
       st.set("choice","2")
       st.unset("talk1")
    elif event == "32047-07.htm" :
       st.set("cond","5")
       st.playSound("ItemSound.quest_middle")
       st.set("talk","0")
       st.set("choice","3")
       st.unset("talk1")
    elif event == "32047-13.htm" :
       st.set("cond","7")
       st.playSound("ItemSound.quest_middle")
    elif event == "32047-13a.htm" :
       st.set("cond","10")
       st.playSound("ItemSound.quest_middle")
    elif event == "32047-15.htm" :
       if st.getInt("talk") == 0 :
          st.set("talk","1")
    elif event == "32047-15a.htm" :
       if self.isSpawned == 0 :
          golem = st.addSpawn(Guardian,96977,-110625,-3280,0,False,900000)
          golem.broadcastPacket(CreatureSay(golem.getObjectId(),0,golem.getName(),"You, "+st.getPlayer().getName()+", you attacked Wendy. Prepare to die!"))
          golem.setRunning()
          golem.addDamageHate(player,0,999)
          golem.getAI().setIntention(CtrlIntention.AI_INTENTION_ATTACK, player)
          self.isSpawned = 1
       else:
          htmltext = "32047-19a.htm"
    elif event == "32047-17a.htm" :
       st.set("cond","12")
       st.playSound("ItemSound.quest_middle")
    elif event == "32047-20.htm" :
          st.set("talk","2")
    elif event == "32047-23.htm" :
       st.set("cond","13")
       st.playSound("ItemSound.quest_middle")
       st.set("talk","0")
    elif event == "32047-25.htm" :
       st.set("cond","15")
       st.playSound("ItemSound.quest_middle")
       st.takeItems(Starstone,1)
    elif event == "32047-30.htm" :
       st.set("talk","2")
    elif event == "32047-33.htm" :
       if st.getInt("cond") == 7:
          st.set("cond","8")
          st.set("talk","0")
          st.playSound("ItemSound.quest_middle")
       elif st.getInt("cond") == 8:
          st.set("cond","9")
          st.playSound("ItemSound.quest_middle")
          htmltext = "32047-34.htm"
    elif event == "32047-34.htm" :
          st.set("cond","9")
          st.playSound("ItemSound.quest_middle")
    elif event == "32047-38.htm" :
       st.giveItems(Starstone2,1)
       st.takeItems(57,3000)
       st.set("cond","26")
       st.playSound("ItemSound.quest_middle")
    elif event == "32050-02.htm" :
       st.playSound("ItemSound.armor_wood_3")
       st.set("talk","1")
    elif event == "32050-04.htm" :
       st.set("cond","14")
       st.giveItems(Starstone,1)
       st.playSound("ItemSound.quest_middle")
       st.set("talk","0")
    return htmltext

 def onFirstTalk (self,npc,player): #atm custom, on retail it is when you walk to npcs radius
    st = player.getQuestState(qn)
    if st : 
        if npc.getNpcId() == Stones and st.getInt("cond") == 17:
           st.playSound("ItemSound.quest_middle")
           st.takeItems(Detector,1)
           st.giveItems(Detector2,1)
           st.set("cond","18")
           return "The radio signal detector is responding. # A suspicious pile of stones catches your eye."
    npc.showChatWindow(player)
    return None

 def onTalk (self,npc,player):
    htmltext = "<html><body>You are either not on a quest that involves this NPC, or you don't meet this NPC's minimum quest requirements.</body></html>"
    st = player.getQuestState(qn)
    if not st : return htmltext
    state = st.getState()
    npcId = npc.getNpcId()
    cond = st.getInt("cond")
    talk = st.getInt("talk")
    talk1 = st.getInt("talk1")
    if state == COMPLETED :
       htmltext = "<html><body>This quest has already been completed.</body></html>"
    elif npcId == Yumi :
       if state == CREATED :
          Pavel = player.getQuestState("121_PavelTheGiants")
          if Pavel:
             if st.getPlayer().getLevel() >= 49 and Pavel.getState() == COMPLETED :
                htmltext = "32041-01.htm"
             else :
                htmltext = "32041-00.htm"
                st.exitQuest(1)
          else :
             htmltext = "32041-00.htm"
             st.exitQuest(1)
       elif cond == 1:
          if talk == 0:
             htmltext = "32041-02.htm"
          else:
             htmltext = "32041-06.htm"
       elif cond == 2:
          htmltext = "32041-08.htm"
       elif cond in [3,4,5]:
          if talk == 0:
             htmltext = "32041-09.htm"
          elif talk == 1:
             htmltext = "32041-11.htm"
          else:
             htmltext = "32041-18.htm"
       elif cond == 6:
          htmltext = "32041-21.htm"
       elif cond in [9,12,16]:
          htmltext = "32041-22.htm"
       elif cond == 17:
          htmltext = "32041-26.htm"
       elif cond == 19:
          if talk == 0:
             htmltext = "32041-27.htm"
          else:
             htmltext = "32041-28.htm"
       elif cond == 20:
          htmltext = "32041-36.htm"
       elif cond == 21:
          htmltext = "32041-33.htm"
       elif cond in [22,26]:
          htmltext = "32041-34.htm"
          st.set("cond","27")
          st.playSound("ItemSound.quest_middle")
       elif cond == 27:
          htmltext = "32041-35.htm"
    elif npcId == Wendy :
       if cond == 2:
          if talk + talk1 < 2:
             htmltext = "32047-01.htm"
          elif talk + talk1 == 2:
             htmltext = "32047-04.htm"
       elif cond == 3:
          htmltext = "32047-09.htm"
       elif cond in [4,5]:
          htmltext = "32047-09a.htm"
       elif cond == 6:
          choice = st.getInt("choice")
          if choice == 1:
             if talk == 0:
                htmltext = "32047-10.htm"
             elif talk == 1:
                htmltext = "32047-20.htm"
             else :
                htmltext = "32047-30.htm"
          elif choice == 2:
             htmltext = "32047-10a.htm"
          elif choice == 3:
            if talk == 0:
               htmltext = "32047-14.htm"
            elif talk == 1:
               htmltext = "32047-15.htm"
            else:
               htmltext = "32047-20.htm"
       elif cond == 7:
          if talk == 0:
             htmltext = "32047-14.htm"
          elif talk == 1:
             htmltext = "32047-15.htm"
          else:
             htmltext = "32047-20.htm"
       elif cond == 8:
          htmltext = "32047-30.htm"
       elif cond == 9:
          htmltext = "32047-27.htm"
       elif cond == 10:
          htmltext = "32047-14a.htm"
       elif cond == 11:
          htmltext = "32047-16a.htm"
       elif cond == 12:
          htmltext = "32047-18a.htm"
       elif cond == 13:
          htmltext = "32047-23.htm"
       elif cond == 14:
          htmltext = "32047-24.htm"
       elif cond == 15:
          htmltext = "32047-26.htm"
          st.set("cond","16")
          st.playSound("ItemSound.quest_middle")
       elif cond == 16:
          htmltext = "32047-27.htm"
       elif cond == 20:
          htmltext = "32047-35.htm"
       elif cond == 26:
          htmltext = "32047-40.htm"
    elif npcId == Box :
       if cond == 13:
          if talk == 0:
             htmltext = "32050-01.htm"
          else:
             htmltext = "32050-03.htm"
       elif cond == 14:
          htmltext = "32050-05.htm"
    elif npcId == Stones :
       if cond == 18:
          htmltext = "32046-01.htm"
       elif cond == 19:
          htmltext = "32046-02.htm"
       elif cond == 27:
          htmltext = "32046-03.htm"
    elif npcId == Newyear :
       if cond == 21:
          htmltext = "31961-01.htm"
       elif cond == 22:
          htmltext = "31961-03.htm"
    return htmltext

 def onKill(self,npc,player,isPet):
    st = player.getQuestState(qn)
    if not st: return 
    npcId = npc.getNpcId()
    if st.getState() == STARTED and st.getInt("cond") == 10:
       if npcId == Guardian :
          npc.broadcastPacket(CreatureSay(npc.getObjectId(),0,npc.getName(),"This enemy is far too powerful for me to fight. I must withdraw"))
          st.set("cond","11")
          st.playSound("ItemSound.quest_middle")
    return

QUEST = Quest(114, qn, "Resurrection Of An Old Manager")

CREATED     = State('Start', QUEST)
STARTED     = State('Started', QUEST)
COMPLETED   = State('Completed', QUEST)

QUEST.setInitialState(CREATED)

QUEST.addStartNpc(Yumi)
QUEST.addFirstTalkId(Stones)

QUEST.addTalkId(Yumi)
QUEST.addTalkId(Wendy)
QUEST.addTalkId(Box)
QUEST.addTalkId(Stones)
QUEST.addTalkId(Newyear)

QUEST.addKillId(Guardian)