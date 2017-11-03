import sys
from com.it.br.gameserver.model.quest import State
from com.it.br.gameserver.model.quest import QuestState
from com.it.br.gameserver.model.quest.jython import QuestJython as JQuest
from com.it.br.gameserver.network.serverpackets import MagicSkillUser

qn = "120_PavelsResearch"

#NPCs
Yumi = 32041
Weather1 = 32042 # north
Weather2 = 32043 # east
Weather3 = 32044 # west
BookShelf = 32045
Stones = 32046
Wendy = 32047

#Items
EarBinding = 854
Report = 8058
Report2 = 8059
Enigma = 8060
Flower = 8290
Heart = 8291
Necklace = 8292

class Quest (JQuest) : 

 def __init__(self,id,name,descr):
    JQuest.__init__(self,id,name,descr)
    self.questItemIds = [Flower,Report,Report2,Enigma,Heart,Necklace]

 def onAdvEvent (self,event,npc, player) :
    st = player.getQuestState(qn)
    if not st: return
    htmltext = event
    if event == "32041-03.htm" :
       st.set("cond","3")
       st.playSound("ItemSound.quest_middle")
    elif event == "32041-04.htm" :
       st.set("cond","4")
       st.playSound("ItemSound.quest_middle")
    elif event == "32041-12.htm" :
       st.set("cond","8")
       st.playSound("ItemSound.quest_middle")
    elif event == "32041-16.htm" :
       st.set("cond","16")
       st.giveItems(Enigma,1)
       st.playSound("ItemSound.quest_middle")
    elif event == "32041-22.htm" :
       st.set("cond","17")
       st.takeItems(Enigma,1)
       st.playSound("ItemSound.quest_middle")
    elif event == "32041-32.htm" :
       st.takeItems(Necklace,1)
       st.giveItems(EarBinding,1)
       st.exitQuest(False)
       st.playSound("ItemSound.quest_finish")
    elif event == "32042-06.htm" :
       if st.getInt("cond") == 10 :
          if st.getInt("talk") + st.getInt("talk1") == 2 :
             st.set("cond","11")
             st.set("talk","0")
             st.set("talk1","0")
             st.playSound("ItemSound.quest_middle")
          else:
             htmltext = "32042-03.htm"
    elif event == "32042-10.htm" :
      if st.getInt("talk") + st.getInt("talk1") + st.getInt("talk2")== 3:
         htmltext = "32042-14.htm"
    elif event == "32042-11.htm" :
       if st.getInt("talk") == 0 :
          st.set("talk","1")
    elif event == "32042-12.htm" :
       if st.getInt("talk1") == 0 :
          st.set("talk1","1")
    elif event == "32042-13.htm" :
       if st.getInt("talk2") == 0 :
          st.set("talk2","1")
    elif event == "32042-15.htm" :
       st.set("cond","12")
       st.set("talk","0")
       st.set("talk1","0")
       st.set("talk2","0")
       st.playSound("ItemSound.quest_middle")
    elif event == "32043-06.htm" :
       if st.getInt("cond") == 17 :
          if st.getInt("talk") + st.getInt("talk1") == 2 :
             st.set("cond","18")
             st.set("talk","0")
             st.set("talk1","0")
             st.playSound("ItemSound.quest_middle")
          else :
             htmltext = "32043-03.htm"
    elif event == "32043-15.htm" :
       if st.getInt("talk") + st.getInt("talk1") == 2 :
          htmltext = "32043-29.htm"
    elif event == "32043-18.htm" :
       if st.getInt("talk") == 1 :
          htmltext = "32043-21.htm"
    elif event == "32043-20.htm" :
       st.set("talk","1")
       st.playSound("AmbSound.ed_drone_02")
    elif event == "32043-28.htm" :
       st.set("talk1","1")
    elif event == "32043-30.htm" :
       st.set("cond","19")
       st.set("talk","0")
       st.set("talk1","0")
    elif event == "32044-06.htm" :
       if st.getInt("cond") == 20 :
          if st.getInt("talk") + st.getInt("talk1") == 2 :
             st.set("cond","21")
             st.set("talk","0")
             st.set("talk1","0")
             st.playSound("ItemSound.quest_middle")
          else :
             htmltext = "32044-03.htm"
    elif event == "32044-08.htm" :
       if st.getInt("talk") + st.getInt("talk1") == 2 :
          htmltext = "32044-11.htm"
    elif event == "32044-09.htm" :
       if st.getInt("talk") == 0 :
          st.set("talk","1")
    elif event == "32044-10.htm" :
       if st.getInt("talk1") == 0 :
          st.set("talk1","1")
    elif event == "32044-17.htm" :
       st.set("cond","22")
       st.set("talk","0")
       st.set("talk1","0")
       st.playSound("ItemSound.quest_middle")
    elif event == "32045-02.htm" :
       st.set("cond","15")
       st.playSound("ItemSound.quest_middle")
       st.giveItems(Report,1)
       npc.broadcastPacket(MagicSkillUser(npc,st.getPlayer(),5073,5,1500,0))
    elif event in ["32046-04.htm","32046-05.htm"] :
       st.exitQuest(1)
    elif event == "32046-06.htm" :
       if st.getPlayer().getLevel() >= 50 :
          st.setState(State.STARTED)
          st.playSound("ItemSound.quest_accept")
          st.set("cond","1")
       else:
          htmltext = "32046-00.htm"
          st.exitQuest(1)
    elif event == "32046-08.htm" :
       st.set("cond","2")
       st.playSound("ItemSound.quest_middle")
    elif event == "32046-12.htm" :
       st.set("cond","6")
       st.playSound("ItemSound.quest_middle")
       st.giveItems(Flower,1)
    elif event == "32046-22.htm" :
       st.set("cond","10")
       st.playSound("ItemSound.quest_middle")
    elif event == "32046-29.htm" :
       st.set("cond","13")
       st.playSound("ItemSound.quest_middle")
    elif event == "32046-35.htm" :
       st.set("cond","20")
       st.playSound("ItemSound.quest_middle")
    elif event == "32046-38.htm" :
       st.set("cond","23")
       st.playSound("ItemSound.quest_middle")
       st.giveItems(Heart,1)
    elif event == "32047-06.htm" :
       st.set("cond","5")
       st.playSound("ItemSound.quest_middle")
    elif event == "32047-10.htm" :
       st.set("cond","7")
       st.playSound("ItemSound.quest_middle")
       st.takeItems(Flower,1)
    elif event == "32047-15.htm" :
       st.set("cond","9")
       st.playSound("ItemSound.quest_middle")
    elif event == "32047-18.htm" :
       st.set("cond","14")
       st.playSound("ItemSound.quest_middle")
    elif event == "32047-26.htm" :
       st.set("cond","24")
       st.playSound("ItemSound.quest_middle")
       st.takeItems(Heart,1)
    elif event == "32047-32.htm" :
       st.set("cond","25")
       st.playSound("ItemSound.quest_middle")
       st.giveItems(Necklace,1)
    elif event == "w1_1" :
       st.set("talk","1")
       htmltext = "32042-04.htm"
    elif event == "w1_2" :
       st.set("talk1","1")
       htmltext = "32042-05.htm"
    elif event == "w2_1" :
       st.set("talk","1")
       htmltext = "32043-04.htm"
    elif event == "w2_2" :
       st.set("talk1","1")
       htmltext = "32043-05.htm"
    elif event == "w3_1" :
       st.set("talk","1")
       htmltext = "32044-04.htm"
    elif event == "w3_2" :
       st.set("talk1","1")
       htmltext = "32044-05.htm"
    return htmltext

 def onTalk (self,npc,player):
    htmltext = "<html><head><body>You are either not on a quest that involves this NPC, or you don't meet this NPC's minimum quest requirements.</body></html>"
    st = player.getQuestState(qn)
    if not st : return htmltext
    state = st.getState()
    npcId = npc.getNpcId()
    cond = st.getInt("cond")
    if state == State.COMPLETED :
       htmltext = "<html><body>This quest has already been completed.</body></html>"
    elif npcId == Stones :
       if state == State.CREATED :
          Pavel = player.getQuestState("114_ResurrectionOfAnOldManager")
          if Pavel :
              if player.getLevel() >= 49 and Pavel.getState() == State.COMPLETED :
                 htmltext = "32046-01.htm"
              else :
                 htmltext = "32046-00.htm"
                 st.exitQuest(1)
          else :
             htmltext = "32046-00.htm"
             st.exitQuest(1)
       elif cond == 1:
          htmltext = "32046-06.htm"
       elif cond == 2:
          htmltext = "32046-09.htm"
       elif cond == 5:
          htmltext = "32046-10.htm"
       elif cond == 6:
          htmltext = "32046-13.htm"
       elif cond == 9:
          htmltext = "32046-14.htm"
       elif cond == 10:
          htmltext = "32046-23.htm"
       elif cond == 12:
          htmltext = "32046-26.htm"
       elif cond == 13:
          htmltext = "32046-30.htm"
       elif cond == 19:
          htmltext = "32046-31.htm"
       elif cond == 20:
          htmltext = "32046-36.htm"
       elif cond == 22:
          htmltext = "32046-37.htm"
       elif cond == 23:
          htmltext = "32046-39.htm"
    elif npcId == Wendy :
       if cond in [2,3,4]:
          htmltext = "32047-01.htm"
       elif cond == 5:
          htmltext = "32047-07.htm"
       elif cond == 6:
          htmltext = "32047-08.htm"
       elif cond == 7:
          htmltext = "32047-11.htm"
       elif cond == 8:
          htmltext = "32047-12.htm"
       elif cond == 9:
          htmltext = "32047-15.htm"
       elif cond == 13:
          htmltext = "32047-16.htm"
       elif cond == 14:
          htmltext = "32047-19.htm"
       elif cond == 15:
          htmltext = "32047-20.htm"
       elif cond == 23:
          htmltext = "32047-21.htm"
       elif cond == 24:
          htmltext = "32047-26.htm"
       elif cond == 25:
          htmltext = "32047-33.htm"
    elif npcId == Yumi :
       if cond == 2:
          htmltext = "32041-01.htm"
       elif cond == 3:
          htmltext = "32041-05.htm"
       elif cond == 4:
          htmltext = "32041-06.htm"
       elif cond == 7:
          htmltext = "32041-07.htm"
       elif cond == 8:
          htmltext = "32041-13.htm"
       elif cond == 15:
          htmltext = "32041-14.htm"
       elif cond == 16:
          if st.getQuestItemsCount(Report2) == 0 :
             htmltext = "32041-17.htm"
          else :
             htmltext = "32041-18.htm"
       elif cond == 17:
          htmltext = "32041-22.htm"
       elif cond == 25:
          htmltext = "32041-26.htm"
    elif npcId == Weather1 :
       if cond == 10:
          htmltext = "32042-01.htm"
       elif cond == 11:
          if st.getInt("talk") + st.getInt("talk1") + st.getInt("talk2")== 3:
             htmltext = "32042-14.htm"
          else:
             htmltext = "32042-06.htm"
       elif cond == 12:
          htmltext = "32042-15.htm"
    elif npcId == Weather2 :
       if cond == 17:
          htmltext = "32043-01.htm"
       elif cond == 18:
          if st.getInt("talk") + st.getInt("talk1") == 2:
             htmltext = "32043-29.htm"
          else:
             htmltext = "32043-06.htm"
       elif cond == 19:
          htmltext = "32043-30.htm"
    elif npcId == Weather3 :
       if cond == 20:
          htmltext = "32044-01.htm"
       elif cond == 21:
          htmltext = "32044-06.htm"
       elif cond == 22:
          htmltext = "32044-18.htm"
    elif npcId == BookShelf :
       if cond == 14:
          htmltext = "32045-01.htm"
       elif cond == 15:
          htmltext = "32045-03.htm"
    return htmltext

QUEST = Quest(120,qn,"Pavel's Research")

QUEST.addStartNpc(Stones)

QUEST.addTalkId(BookShelf)
QUEST.addTalkId(Stones)
QUEST.addTalkId(Weather1)
QUEST.addTalkId(Weather2)
QUEST.addTalkId(Weather3)
QUEST.addTalkId(Wendy)
QUEST.addTalkId(Yumi)