# Made by mtrix
import sys
from com.it.br.gameserver.model.quest import State
from com.it.br.gameserver.model.quest import QuestState
from com.it.br.gameserver.model.quest.jython import QuestJython as JQuest

qn = "171_ActsOfEvil"

BLADE_MOLD,TYRAS_BILL,RANGERS_REPORT1,RANGERS_REPORT2,RANGERS_REPORT3,RANGERS_REPORT4,\
WEAPON_TRADE_CONTRACT,ATTACK_DIRECTIVES,CERTIFICATE,CARGOBOX,OL_MAHUM_HEAD = range(4239,4250)

ADENA = 57

CHANCE1  = 50
CHANCE11 = 10
CHANCE2  = 100
CHANCE21 = 20
CHANCE22 = 20
CHANCE23 = 20
CHANCE24 = 10
CHANCE25 = 10
CHANCE3  = 50

ALVAH,ARODIN,TYRA,ROLENTO,NETI,BURAI=30381,30207,30420,30437,30425,30617

class Quest (JQuest) :

 def __init__(self,id,name,descr): JQuest.__init__(self,id,name,descr)

 def onEvent (self,event,st) :
     htmltext = event
     cond = st.getInt("cond")
     if st.getState() != COMPLETED :
       if event == "30381-02.htm" and cond == 0 :
         st.setState(STARTED)
         st.set("cond","1")
         st.playSound("ItemSound.quest_accept")
       elif event == "30207-02.htm" and cond == 1 :
         st.set("cond","2")
       elif event == "30381-04.htm" and cond == 4:
         st.set("cond","5")
       elif event == "30381-07.htm" and cond == 6:
         st.set("cond","7")
         st.takeItems(WEAPON_TRADE_CONTRACT,-1)
         st.playSound("ItemSound.quest_middle")
       elif event == "30437-03.htm" and cond == 8:
         st.giveItems(CARGOBOX,1)
         st.giveItems(CERTIFICATE,1)
         st.set("cond","9")
       elif event == "30617-04.htm" and cond == 9:
         st.takeItems(CERTIFICATE,-1)
         st.takeItems(ATTACK_DIRECTIVES,-1)
         st.takeItems(CARGOBOX,-1)
         st.set("cond","10")
     return htmltext

 def onTalk (self,npc,player):
     htmltext = "<html><body>You are either not carrying out your quest or don't meet the criteria.</body></html>"
     st = player.getQuestState(qn)
     if not st : return htmltext

     npcId = npc.getNpcId()
     id = st.getState()
     level = player.getLevel()
     cond = st.getInt("cond")
     if npcId==ALVAH :
         if id == CREATED :
            if level > 26:
               htmltext = "30381-01.htm"
            else :
               htmltext = "30381-01a.htm"
               st.exitQuest(1)
         elif id == COMPLETED :
             htmltext = "<html><body>This quest has already been completed!</body></html>"
         elif cond==1 :
             htmltext = "30381-02a.htm"
         elif cond==4 :
             htmltext = "30381-03.htm"
         elif cond==5 :
             if st.getQuestItemsCount(RANGERS_REPORT1) and st.getQuestItemsCount(RANGERS_REPORT2) and st.getQuestItemsCount(RANGERS_REPORT3) and st.getQuestItemsCount(RANGERS_REPORT4) :
                 htmltext = "30381-05.htm"
                 st.takeItems(RANGERS_REPORT1,-1)
                 st.takeItems(RANGERS_REPORT2,-1)
                 st.takeItems(RANGERS_REPORT3,-1)
                 st.takeItems(RANGERS_REPORT4,-1)
                 st.set("cond","6")
             else :
                 htmltext = "30381-04a.htm"
         elif cond==6 :
             if st.getQuestItemsCount(WEAPON_TRADE_CONTRACT) and st.getQuestItemsCount(ATTACK_DIRECTIVES) :
                 htmltext = "30381-06.htm"
             else :
                 htmltext = "30381-05a.htm"
         elif cond==7 :
             htmltext = "30381-07a.htm"
         elif cond==11 :
             htmltext = "30381-08.htm"
             st.giveItems(ADENA,90000)
             st.playSound("ItemSound.quest_finish")
             st.setState(COMPLETED)
     elif id == STARTED :
         if npcId==ARODIN :
             if cond==1 :
                 htmltext = "30207-01.htm"
             elif cond==2 :
                 htmltext = "30207-01a.htm"
             elif cond==3 :
                 if st.getQuestItemsCount(TYRAS_BILL) :
                     st.takeItems(TYRAS_BILL,-1)
                     htmltext = "30207-03.htm"
                     st.set("cond","4")
                 else :
                     htmltext = "30207-01a.htm"
             elif cond==4 :
                 htmltext = "30207-03a.htm"
         elif npcId==TYRA :
             if cond==2 :
                if st.getQuestItemsCount(BLADE_MOLD)>=20 :
                   st.takeItems(BLADE_MOLD,-1)
                   st.giveItems(TYRAS_BILL,1)
                   htmltext = "30420-01.htm"
                   st.set("cond","3")
                else :
                   htmltext = "30420-01b.htm"
             elif cond==3 :
                 htmltext = "30420-01a.htm"
             elif cond > 3 :
                 htmltext = "30420-02.htm"
         elif npcId==NETI :
             if cond==7 :
                 htmltext = "30425-01.htm"
                 st.set("cond","8")
             elif cond==8 :
                 htmltext = "30425-02.htm"
         elif npcId==ROLENTO :
             if cond==8 :
                 htmltext = "30437-01.htm"
             elif cond==9 :
                 htmltext = "30437-03a.htm"
         elif npcId==BURAI :
             if cond==9 and st.getQuestItemsCount(CERTIFICATE) and st.getQuestItemsCount(CARGOBOX) and st.getQuestItemsCount(ATTACK_DIRECTIVES) :
                 htmltext = "30617-01.htm"
             if cond==10 :
                 if st.getQuestItemsCount(OL_MAHUM_HEAD)>=30 :
                    htmltext = "30617-05.htm"
                    st.giveItems(ADENA,8000)
                    st.takeItems(OL_MAHUM_HEAD,-1)
                    st.set("cond","11")
                    st.playSound("ItemSound.quest_itemget")
                 else :
                    htmltext = "30617-04a.htm"
     return htmltext

 def onKill(self,npc,player,isPet):
     st = player.getQuestState(qn)
     if not st : return 
     if st.getState() != STARTED : return 

     npcId = npc.getNpcId()
     cond = st.getInt("cond")
     chance=st.getRandom(100)
     if cond==2 and npcId in range(20496,20500) :
        blades = st.getQuestItemsCount(BLADE_MOLD)
        if chance < CHANCE11 :
           st.addSpawn(27190)
        if chance < CHANCE1 and blades < 20 :
           st.giveItems(BLADE_MOLD,1)
           if blades == 19 :
              st.playSound("ItemSound.quest_middle")
           else :
              st.playSound("ItemSound.quest_itemget")
     elif cond==5 and npcId == 20062 :
         if not st.getQuestItemsCount(RANGERS_REPORT1) and chance < CHANCE2:
            st.giveItems(RANGERS_REPORT1,1)
            st.playSound("ItemSound.quest_itemget")
         elif not st.getQuestItemsCount(RANGERS_REPORT2) and chance < CHANCE21:
            st.giveItems(RANGERS_REPORT2,1)
            st.playSound("ItemSound.quest_itemget")
         elif not st.getQuestItemsCount(RANGERS_REPORT3) and chance < CHANCE22:
            st.giveItems(RANGERS_REPORT3,1)
            st.playSound("ItemSound.quest_itemget")
         elif not st.getQuestItemsCount(RANGERS_REPORT4) and chance < CHANCE23:
            st.giveItems(RANGERS_REPORT4,1)
            st.playSound("ItemSound.quest_itemget")
     elif cond==6 and npcId==20438 :
         if not st.getQuestItemsCount(WEAPON_TRADE_CONTRACT) and chance < CHANCE24:
            st.giveItems(WEAPON_TRADE_CONTRACT,1)
            st.playSound("ItemSound.quest_itemget")
         elif not st.getQuestItemsCount(ATTACK_DIRECTIVES) and chance < CHANCE25:
            st.giveItems(ATTACK_DIRECTIVES,1)
            st.playSound("ItemSound.quest_itemget")
     elif cond==10 and npcId==20066 :
         heads=st.getQuestItemsCount(OL_MAHUM_HEAD)
         if heads < 30 and chance < CHANCE3 :
            st.giveItems(OL_MAHUM_HEAD,1)
            if heads == 29 :
               st.playSound("ItemSound.quest_middle")
            else :
               st.playSound("ItemSound.quest_itemget")
     return

QUEST       = Quest(171,qn,"Acts of Evil")
CREATED     = State('Start', QUEST)
STARTED     = State('Started', QUEST)
COMPLETED   = State('Completed', QUEST)

QUEST.setInitialState(CREATED)
QUEST.addStartNpc(ALVAH)

QUEST.addTalkId(ALVAH)

QUEST.addTalkId(ARODIN)
QUEST.addTalkId(TYRA)
QUEST.addTalkId(ROLENTO)
QUEST.addTalkId(NETI)
QUEST.addTalkId(BURAI)

for i in range(20494,20500)+[20062,20066,20438] :
    QUEST.addKillId(i)