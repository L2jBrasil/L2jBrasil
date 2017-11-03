# Made by mtrix - v0.2 by DrLecter
import sys
from com.it.br import Config
from com.it.br.gameserver.model.quest import State
from com.it.br.gameserver.model.quest import QuestState
from com.it.br.gameserver.model.quest.jython import QuestJython as JQuest

qn = "343_UnderTheShadowOfTheIvoryTower"

ORB = 4364
ECTOPLASM = 4365
ADENA = 57
CHANCE = 50
RANDOM_REWARDS=[[951,1],   #Enchant Weapon C
                [955,1],   #Enchant Weapon D
                [2511,550],#SpiritShot: Grade C
                [736,1],   #SoE
               ]
#Roshambo
OPTIONS={0:"Scissors",1:"Rock",2:"Paper"}
OUTCOME={0:1,1:2,2:0}
#Coin Toss
TOSS={0:"Heads",1:"Tails"}
ORBS=[10,30,70,150,310,0]
#Messages
start_msg=["One~ Two~ Three~","Go! One~ Two~ Three~","Ready? Go! One~ Two~ Three~","Here we go! One~ Two~ Three~"]
tie_msg=["Ah ha! A tie! Take back the orbs that you bet. Well, shall we play again?",\
         "Ha! A tie! Take back the orbs that you bet. Shall we try again?"]
win_msg=["Well, you certainly got lucky that time! Take all the orbs we put up as a bet. Come on! Let's play another round!",\
         "Oh no! I lose! Go ahead. Take all the orbs we put up as a bet. Come on! Let's play again!",\
         "Oh no! I lose! Go ahead. Take all the orbs we put up as a bet. Humph... Come on! Let's play again!"]
lose_msg=["Oh, too bad. You lose! Shall we play another round?",\
          "Oh...! You lose! Oh well, the orbs are mine. Shall we play another round?",\
          "Oh, too bad, you lose! I'll take those orbs now... Hey now, shall we play another round?"]
again_msg=["Play the game.","Play the rock paper scissors game."]
toss_msg=[["You're right!","You win!"],\
          ["Hu wah! Right again!","You won twice in a row!"],\
          ["Hu wah! Right again!","You won three times in a row!"],\
          ["Ho ho! Right again!","You won four times in a row!"]]
class Quest (JQuest) :

 def __init__(self,id,name,descr): JQuest.__init__(self,id,name,descr)

 def onEvent (self,event,st) :
     htmltext = event
     marsha = st.getRandom(3)
     random2 = st.getRandom(2)
     orbs = st.getQuestItemsCount(ORB)
     if event == "30834-02.htm" :
        st.setState(STARTED)
        st.set("cond","1")
        st.playSound("ItemSound.quest_accept")
     elif event == "30834-05.htm" :
        if orbs :
           st.giveItems(ADENA,orbs*125)
           st.takeItems(ORB,-1)
        else :
           htmltext = "30834-09.htm"
     elif event == "30835-02.htm":
        if st.getQuestItemsCount(ECTOPLASM) :
           st.takeItems(ECTOPLASM,1)
           item=RANDOM_REWARDS[st.getRandom(len(RANDOM_REWARDS))]
           st.giveItems(item[0],int(item[1]*Config.RATE_QUESTS_REWARD))
           htmltext="30835-02a.htm"
     elif event == "30934-02.htm" :
        if orbs < 10 :
             htmltext = "30934-03a.htm"
        else:
             st.set("rps_1sttime","1")
     elif event == "30934-03.htm" :
        if orbs>=10 :
             st.takeItems(ORB,10)
             st.set("playing","1")
             htmltext = st.showHtmlFile("30934-03.htm").replace("<msg>", start_msg[st.getRandom(len(start_msg))])
        else :
             htmltext = "30934-03a.htm"
     elif event in [ "1","2","3" ]:
        if st.getInt("playing"):
           player=int(event)-1
           if OUTCOME[player] == marsha:
              msg=lose_msg
           elif OUTCOME[marsha] == player:
              st.giveItems(ORB,20)
              msg=win_msg
           else:
              st.giveItems(ORB,10)
              msg=tie_msg
           st.unset("playing")
           htmltext = st.showHtmlFile("30934-04.htm").replace("%player%", OPTIONS[player]).\
                      replace("%marsha%", OPTIONS[marsha]).replace("%msg%", msg[st.getRandom(len(msg))]).\
                      replace("%again%", again_msg[st.getRandom(len(again_msg))])
        else:
           htmltext="Player is cheating"
           st.takeItems(ORB,-1)
           
     elif event == "30935-02.htm" :
        if orbs < 10 :
             htmltext = "30935-02a.htm"
        else:
             st.set("ct_1sttime","1")
     elif event == "30935-03.htm" :
        if orbs>=10 :
             st.set("toss","1")
        else :
             st.unset("row")
             htmltext = "30935-02a.htm"
     elif event in ["4","5"] :
        if st.getInt("toss"):
          if orbs>=10:
            if random2==int(event)-4 :
              row = st.getInt("row")
              if row<4 :
                row += 1
                template="30935-06d.htm"
              else:
                st.giveItems(ORB,310)
                row=0
                template="30935-06c.htm"
            else :
              row = 0
              st.takeItems(ORB,10)
              template="30935-06b.htm"
            st.set("row",str(row))
            htmltext = st.showHtmlFile(template).replace("%toss%",TOSS[random2]).\
                      replace("%msg1%",toss_msg[row-1][0]).replace("%msg2%",toss_msg[row-1][1]).\
                      replace("%orbs%",str(ORBS[row-1])).replace("%next%",str(ORBS[row]))
          else:
           st.unset("row")
           htmltext = "30935-02a.htm"
          st.unset("toss") 
        else:
           st.takeItems(ORB,-1)
           htmltext="Player is cheating"
     elif event == "quit":
        if st.getInt("row"):
            qty=st.getInt("row")-1
            st.giveItems(ORB,ORBS[qty])
            st.unset("row")
            htmltext = st.showHtmlFile("30935-06a.htm").replace("%nebulites%",str(ORBS[qty]))
        else:
           st.takeItems(ORB,-1)
           htmltext="Player is cheating"
     elif event in ["30834-06.htm","30834-02b.htm"] :
        st.playSound("ItemSound.quest_finish")
        st.exitQuest(1)
     return htmltext

 def onTalk (self,npc,player):
     htmltext = "<html><body>You are either not carrying out your quest or don't meet the criteria.</body></html>"
     st = player.getQuestState(qn)
     if not st : return htmltext

     npcId = npc.getNpcId()
     id = st.getState()
     level = player.getLevel()
     cond = st.getInt("cond")
     if npcId==30834 :
         if id == CREATED :
             if player.getClassId().getId() in [ 0x11,0xc,0xd,0xe,0x10,0x1a,0x1b,0x1c,0x1e,0x28,0x29,0x2b,0x5e,0x5f,0x60,0x61,0x62,0x67,0x68,0x69,0x6e,0x6f,0x70]:
               if level >= 40:
                 htmltext = "30834-01.htm"
               else:
                 htmltext = "30834-01a.htm"
                 st.exitQuest(1)
             else:
                 htmltext = "30834-01b.htm"
                 st.exitQuest(1)
         elif cond==1 :
             if st.getQuestItemsCount(ORB) :
               htmltext = "30834-04.htm"
             else :
               htmltext = "30834-03.htm"
     elif npcId==30835 :
         htmltext = "30835-01.htm"
     elif npcId==30934 :
         if st.getInt("rps_1sttime") :
            htmltext = "30934-01a.htm"
         else :
            htmltext = "30934-01.htm"
     elif npcId==30935 :
         st.unset("row")
         if st.getInt("ct_1sttime") :
            htmltext = "30935-01a.htm"
         else :
            htmltext = "30935-01.htm"
     return htmltext

 def onKill(self,npc,player,isPet):
     st = player.getQuestState(qn)
     if not st : return 
     if st.getState() != STARTED : return 
   
     npcId = npc.getNpcId()
     if st.getRandom(100) < CHANCE :
         st.giveItems(ORB,1)
         st.playSound("ItemSound.quest_itemget")
     return

QUEST       = Quest(343,qn,"Under The Shadow Of The Ivory Tower")
CREATED     = State('Start', QUEST)
STARTED     = State('Started', QUEST)

QUEST.setInitialState(CREATED)
QUEST.addStartNpc(30834)

QUEST.addTalkId(30834)
QUEST.addTalkId(30835)
QUEST.addTalkId(30935)
QUEST.addTalkId(30934)

for i in range(20563,20567) :
    QUEST.addKillId(i)