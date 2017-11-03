# Made by Mr. - Version 0.3 by DrLecter
# Shadow Weapon Coupons contributed by BiTi for the Official L2J Datapack Project
# Visit http://forum.l2jdp.com for more details
import sys
from com.it.br.gameserver.model.quest import State
from com.it.br.gameserver.model.quest import QuestState
from com.it.br.gameserver.model.quest.jython import QuestJython as JQuest

qn = "402_PathToKnight"

MARK_OF_ESQUIRE = 1271
SWORD_OF_RITUAL,COIN_OF_LORDS1,COIN_OF_LORDS2,COIN_OF_LORDS3,COIN_OF_LORDS4,COIN_OF_LORDS5,COIN_OF_LORDS6,GLUDIO_GUARDS_MARK1,\
BUGBEAR_NECKLACE,EINHASAD_CHURCH_MARK1,EINHASAD_CRUCIFIX,GLUDIO_GUARDS_MARK2,POISON_SPIDER_LEG1,EINHASAD_CHURCH_MARK2,LIZARDMAN_TOTEM,\
GLUDIO_GUARDS_MARK3,GIANT_SPIDER_HUSK,EINHASAD_CHURCH_MARK3,HORRIBLE_SKULL = range(1161,1180)

default = "<html><body>You are either not carrying out your quest or don't meet the criteria.</body></html>" 

DROPLIST={
   20775:[GLUDIO_GUARDS_MARK1,BUGBEAR_NECKLACE,10,100],
   27024:[EINHASAD_CHURCH_MARK1,EINHASAD_CRUCIFIX,12,100],
   20038:[GLUDIO_GUARDS_MARK2,POISON_SPIDER_LEG1,20,100],
   20043:[GLUDIO_GUARDS_MARK2,POISON_SPIDER_LEG1,20,100],
   20050:[GLUDIO_GUARDS_MARK2,POISON_SPIDER_LEG1,20,100],
   20030:[EINHASAD_CHURCH_MARK2,LIZARDMAN_TOTEM,20,50],
   20027:[EINHASAD_CHURCH_MARK2,LIZARDMAN_TOTEM,20,100],
   20024:[EINHASAD_CHURCH_MARK2,LIZARDMAN_TOTEM,20,100],
   20103:[GLUDIO_GUARDS_MARK3,GIANT_SPIDER_HUSK,20,40],
   20106:[GLUDIO_GUARDS_MARK3,GIANT_SPIDER_HUSK,20,40],
   20108:[GLUDIO_GUARDS_MARK3,GIANT_SPIDER_HUSK,20,40],
   20404:[EINHASAD_CHURCH_MARK3,HORRIBLE_SKULL,10,100]
}

class Quest (JQuest) :

 def __init__(self,id,name,descr): JQuest.__init__(self,id,name,descr)

 def onEvent (self,event,st) :
    htmltext = event
    classid = st.getPlayer().getClassId().getId()
    level = st.getPlayer().getLevel()
    squire = st.getQuestItemsCount(MARK_OF_ESQUIRE)
    coin1,coin2,coin3,coin4,coin5,coin6 = st.getQuestItemsCount(COIN_OF_LORDS1),st.getQuestItemsCount(COIN_OF_LORDS2),st.getQuestItemsCount(COIN_OF_LORDS3),st.getQuestItemsCount(COIN_OF_LORDS4),st.getQuestItemsCount(COIN_OF_LORDS5),st.getQuestItemsCount(COIN_OF_LORDS6)
    guards_mark1,guards_mark2,guards_mark3=st.getQuestItemsCount(GLUDIO_GUARDS_MARK1),st.getQuestItemsCount(GLUDIO_GUARDS_MARK2),st.getQuestItemsCount(GLUDIO_GUARDS_MARK3)
    church_mark1,church_mark2,church_mark3=st.getQuestItemsCount(EINHASAD_CHURCH_MARK1),st.getQuestItemsCount(EINHASAD_CHURCH_MARK2),st.getQuestItemsCount(EINHASAD_CHURCH_MARK3)
    if event == "30417-02a.htm" :
       if classid == 0x00 :
          if level >= 19 :
             if st.getQuestItemsCount(SWORD_OF_RITUAL)>0 :
                htmltext = "30417-04.htm"
             else:
                htmltext = "30417-05.htm"
          else :
             htmltext = "30417-02.htm"
             st.exitQuest(1)
       elif classid != 0x04 :
          htmltext = "30417-03.htm"
          st.exitQuest(1)
    elif event == "30417-08.htm" :
        if st.getInt("cond")== 0 and classid == 0x00 and level >= 19 :
           st.set("id","0")
           st.set("cond","1")
           st.setState(STARTED)
           st.playSound("ItemSound.quest_accept")
           st.giveItems(MARK_OF_ESQUIRE,1)
        else:
           htmltext = default
    elif event == "30332-02.htm" :
        if squire and not guards_mark1 and not coin1:
          st.giveItems(GLUDIO_GUARDS_MARK1,1)
        else:
          htmltext=default
    elif event == "30289-03.htm" :
        if squire and not church_mark1 and not coin2:
          st.giveItems(EINHASAD_CHURCH_MARK1,1)
        else:
          htmltext=default
    elif event == "30379-02.htm" :
        if squire and not guards_mark2 and not coin3:
          st.giveItems(GLUDIO_GUARDS_MARK2,1)
        else:
          htmltext=default
    elif event == "30037-02.htm" :
        if squire and not church_mark2 and not coin4:
          st.giveItems(EINHASAD_CHURCH_MARK2,1)
        else:
          htmltext=default
    elif event == "30039-02.htm" :
        if squire and not guards_mark3 and not coin5:
          st.giveItems(GLUDIO_GUARDS_MARK3,1)
        else:
          htmltext=default
    elif event == "30031-02.htm" :
        if squire and not church_mark3 and not coin6:
          st.giveItems(EINHASAD_CHURCH_MARK3,1)
        else:
          htmltext=default
    elif event == "30417-13.htm" :
        if squire and (coin1+coin2+coin3+coin4+coin5+coin6)>=3 :
          for item in range(1162,1180) :
               st.takeItems(item,-1)
          st.takeItems(MARK_OF_ESQUIRE,-1)
          st.giveItems(SWORD_OF_RITUAL,1)
          st.set("cond","0")
          st.setState(COMPLETED)
          st.playSound("ItemSound.quest_finish")
        else:
          htmltext=default
    elif event == "30417-14.htm" :
        if squire and (coin1+coin2+coin3+coin4+coin5+coin6)>=3 :
          for item in range(1162,1180) :
               st.takeItems(item,-1)
          st.takeItems(MARK_OF_ESQUIRE,-1)
          st.giveItems(SWORD_OF_RITUAL,1)
          st.set("cond","0")
          st.setState(COMPLETED)
          st.playSound("ItemSound.quest_finish")
        else:
          htmltext=default
    return htmltext


 def onTalk (self,npc,player):
   htmltext = "<html><body>You are either not carrying out your quest or don't meet the criteria.</body></html>"
   st = player.getQuestState(qn)
   if not st : return htmltext

   npcId = npc.getNpcId()
   id = st.getState()
   if npcId != 30417 and id != STARTED : return htmltext

   squire = st.getQuestItemsCount(MARK_OF_ESQUIRE)
   coin1,coin2,coin3,coin4,coin5,coin6 = st.getQuestItemsCount(COIN_OF_LORDS1),st.getQuestItemsCount(COIN_OF_LORDS2),st.getQuestItemsCount(COIN_OF_LORDS3),st.getQuestItemsCount(COIN_OF_LORDS4),st.getQuestItemsCount(COIN_OF_LORDS5),st.getQuestItemsCount(COIN_OF_LORDS6)
   guards_mark1,guards_mark2,guards_mark3=st.getQuestItemsCount(GLUDIO_GUARDS_MARK1),st.getQuestItemsCount(GLUDIO_GUARDS_MARK2),st.getQuestItemsCount(GLUDIO_GUARDS_MARK3)
   church_mark1,church_mark2,church_mark3=st.getQuestItemsCount(EINHASAD_CHURCH_MARK1),st.getQuestItemsCount(EINHASAD_CHURCH_MARK2),st.getQuestItemsCount(EINHASAD_CHURCH_MARK3)
   cond = st.getInt("cond")
   if id == COMPLETED:
      htmltext="<html><body>This quest has already been completed.</body></html>"
   elif npcId == 30417 :
      if cond==0 :
         htmltext = "30417-01.htm"
      elif cond==1 and squire :
        if (coin1+coin2+coin3+coin4+coin5+coin6) < 3 :
           htmltext = "30417-09.htm"
        elif (coin1+coin2+coin3+coin4+coin5+coin6) == 3 :
           htmltext = "30417-10.htm"
        elif 3<(coin1+coin2+coin3+coin4+coin5+coin6)<6 :
           htmltext = "30417-11.htm"
        elif (coin1+coin2+coin3+coin4+coin5+coin6) == 6 :
           htmltext = "30417-12.htm"
           for item in range(1162,1180) :
               st.takeItems(item,-1)
           st.takeItems(MARK_OF_ESQUIRE,-1)
           st.giveItems(SWORD_OF_RITUAL,1)
           st.set("cond","0")
           st.setState(COMPLETED)
           st.playSound("ItemSound.quest_finish")
   elif npcId == 30332 and cond==1 and squire :
       if not guards_mark1 and not coin1 :
          htmltext = "30332-01.htm"
       elif guards_mark1 :
          if st.getQuestItemsCount(BUGBEAR_NECKLACE)<10 :
             htmltext = "30332-03.htm"
          else:
             htmltext = "30332-04.htm"
             st.takeItems(BUGBEAR_NECKLACE,-1)
             st.takeItems(GLUDIO_GUARDS_MARK1,1)
             st.giveItems(COIN_OF_LORDS1,1)
       elif coin1 :
          htmltext = "30332-05.htm"
   elif npcId == 30289 and cond==1 and squire :
       if not church_mark1 and not coin2 :
          htmltext = "30289-01.htm"
       elif church_mark1 :
        if st.getQuestItemsCount(EINHASAD_CRUCIFIX)<12 :
          htmltext = "30289-04.htm"
        else:
          htmltext = "30289-05.htm"
          st.takeItems(EINHASAD_CRUCIFIX,-1)
          st.takeItems(EINHASAD_CHURCH_MARK1,1)
          st.giveItems(COIN_OF_LORDS2,1)
       elif coin2 :
          htmltext = "30289-06.htm"
   elif npcId == 30379 and cond==1 and squire :
       if not coin3 and not guards_mark2 :
          htmltext = "30379-01.htm"
       elif guards_mark2 :
          if st.getQuestItemsCount(POISON_SPIDER_LEG1)<20 :
            htmltext = "30379-03.htm"
          else:
            htmltext = "30379-04.htm"
            st.takeItems(POISON_SPIDER_LEG1,-1)
            st.takeItems(GLUDIO_GUARDS_MARK2,1)
            st.giveItems(COIN_OF_LORDS3,1)
       elif coin3 :
          htmltext = "30379-05.htm"
   elif npcId == 30037 and cond==1 and squire :
       if not coin4 and not church_mark2 :
          htmltext = "30037-01.htm"
       elif church_mark2 :
          if st.getQuestItemsCount(LIZARDMAN_TOTEM)<20 :
            htmltext = "30037-03.htm"
          else:
            htmltext = "30037-04.htm"
            st.takeItems(LIZARDMAN_TOTEM,-1)
            st.takeItems(EINHASAD_CHURCH_MARK2,1)
            st.giveItems(COIN_OF_LORDS4,1)
       elif coin4 :
          htmltext = "30037-05.htm"
   elif npcId == 30039 and cond==1 and squire :
       if not guards_mark3 and not coin5 :
          htmltext = "30039-01.htm"
       elif guards_mark3 :
          if st.getQuestItemsCount(GIANT_SPIDER_HUSK)<20 :
            htmltext = "30039-03.htm"
          else:
            htmltext = "30039-04.htm"
            st.takeItems(GIANT_SPIDER_HUSK,-1)
            st.takeItems(GLUDIO_GUARDS_MARK3,1)
            st.giveItems(COIN_OF_LORDS5,1)
       elif coin5 :
          htmltext = "30039-05.htm"
   elif npcId == 30031 and cond==1 and squire :
       if not church_mark3 and not coin6 :
         htmltext = "30031-01.htm"
       elif church_mark3 :
         if st.getQuestItemsCount(HORRIBLE_SKULL)<10 :
           htmltext = "30031-03.htm"
         else:
           htmltext = "30031-04.htm"
           st.takeItems(HORRIBLE_SKULL,-1)
           st.takeItems(EINHASAD_CHURCH_MARK3,1)
           st.giveItems(COIN_OF_LORDS6,1)
       elif coin6 :
         htmltext = "30031-05.htm"
   elif npcId == 30311 and cond==1 and squire :
        htmltext = "30311-01.htm"
   elif npcId == 30653 and cond==1 and squire :
        htmltext = "30653-01.htm"
   return htmltext

 def onKill(self,npc,player,isPet):
   st = player.getQuestState(qn)
   if not st : return 
   if st.getState() != STARTED : return 
   
   if st.getInt("cond") :
      item_required,item,max,chance=DROPLIST[npc.getNpcId()]
      if st.getQuestItemsCount(item_required) and st.getQuestItemsCount(item)<max and st.getRandom(100)<chance :
        st.giveItems(item,1)
        if st.getQuestItemsCount(item) == max :
          st.playSound("ItemSound.quest_middle")
        else:
          st.playSound("ItemSound.quest_itemget")
   return

QUEST       = Quest(402,qn,"Path To Knight")
CREATED     = State('Start', QUEST)
STARTING    = State('Starting', QUEST)
STARTED     = State('Started', QUEST)
COMPLETED   = State('Completed', QUEST)


QUEST.setInitialState(CREATED)
QUEST.addStartNpc(30417)

QUEST.addTalkId(30417)

QUEST.addTalkId(30031)
QUEST.addTalkId(30037)
QUEST.addTalkId(30039)
QUEST.addTalkId(30289)
QUEST.addTalkId(30311)
QUEST.addTalkId(30332)
QUEST.addTalkId(30379)
QUEST.addTalkId(30417)
QUEST.addTalkId(30653)

for mob in DROPLIST.keys():
    QUEST.addKillId(mob)