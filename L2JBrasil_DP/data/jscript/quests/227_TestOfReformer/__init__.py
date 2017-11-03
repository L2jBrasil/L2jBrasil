# Made by Mr. Have fun! Version 0.2
# Shadow Weapon Coupons contributed by BiTi for the Official L2J Datapack Project
# Visit http://forum.l2jdp.com for more details
import sys
from com.it.br.gameserver.model.quest import State
from com.it.br.gameserver.model.quest import QuestState
from com.it.br.gameserver.model.quest.jython import QuestJython as JQuest

qn = "227_TestOfReformer"

MARK_OF_REFORMER = 2821
BOOK_OF_REFORM = 2822
LETTER_OF_INTRODUCTION = 2823
SLAS_LETTER = 2824
GREETINGS = 2825
OLMAHUMS_MONEY = 2826
KATARIS_LETTER = 2827
NYAKURIS_LETTER = 2828
UNDEAD_LIST = 2829
RAMUSS_LETTER = 2830
RIPPED_DIARY = 2831
HUGE_NAIL = 2832
LETTER_OF_BETRAYER = 2833
BONE_FRAGMENT4 = 2834
BONE_FRAGMENT5 = 2835
BONE_FRAGMENT6 = 2836
BONE_FRAGMENT7 = 2837
BONE_FRAGMENT8 = 2838
BONE_FRAGMENT9 = 2839
KAKANS_LETTER = 3037
SHADOW_WEAPON_COUPON_CGRADE = 8870

class Quest (JQuest) :

 def __init__(self,id,name,descr): JQuest.__init__(self,id,name,descr)

 def onEvent (self,event,st) :
    htmltext = event
    if event == "1" :
        htmltext = "30118-04.htm"
        st.set("cond","1")
        st.setState(STARTED)
        st.playSound("ItemSound.quest_accept")
        st.giveItems(BOOK_OF_REFORM,1)
    elif event == "30118_1" :
          htmltext = "30118-06.htm"
          st.giveItems(LETTER_OF_INTRODUCTION,1)
          st.takeItems(BOOK_OF_REFORM,1)
          st.set("cond","4")
          st.takeItems(HUGE_NAIL,1)
    elif event == "30666_1" :
          htmltext = "30666-03.htm"
    elif event == "30666_2" :
          htmltext = "30666-02.htm"
    elif event == "30666_3" :
          htmltext = "30666-04.htm"
          st.giveItems(SLAS_LETTER,1)
          st.takeItems(LETTER_OF_INTRODUCTION,1)
          st.set("cond","5")
    elif event == "30666_4" :
          htmltext = "30666-02.htm"
    elif event == "30669_1" :
          htmltext = "30669-02.htm"
    elif event == "30669_2" :
          htmltext = "30669-03.htm"
          st.addSpawn(27131,-9382,-89852,-2333)
    elif event == "30669_3" :
          htmltext = "30669-05.htm"
    elif event == "30670_1" :
          htmltext = "30670-03.htm"
          st.addSpawn(27132,126019,-179983,-1781)
    elif event == "30670_2" :
          htmltext = "30670-02.htm"
    return htmltext


 def onTalk (self,npc,player):
   htmltext = "<html><body>You are either not carrying out your quest or don't meet the criteria.</body></html>"
   st = player.getQuestState(qn)
   if not st : return htmltext

   npcId = npc.getNpcId()
   id = st.getState()
   if npcId != 30118 and id != STARTED : return htmltext

   if id == CREATED :
     st.setState(STARTING)
     st.set("cond","0")
     st.set("onlyone","0")
     st.set("id","0")
   if npcId == 30118 and st.getInt("cond")==0 and st.getInt("onlyone")==0 :
      if player.getClassId().getId() in [ 0x0f,0x2a ] :
         if player.getLevel() >= 39 :
            htmltext = "30118-03.htm"
         else:
            htmltext = "30118-01.htm"
            st.exitQuest(1)
      else:
         htmltext = "30118-02.htm"
         st.exitQuest(1)
   elif npcId == 30118 and st.getInt("cond")==0 and st.getInt("onlyone")==1 :
      htmltext = "<html><body>This quest has already been completed.</body></html>"
   elif npcId == 30118 and st.getInt("cond")==3 and st.getQuestItemsCount(HUGE_NAIL)>=1:
        htmltext = "30118-05.htm"
   elif npcId == 30118 and st.getInt("cond")>=4 :
        htmltext = "30118-07.htm"
   elif npcId == 30666 and st.getInt("cond")==4 and st.getQuestItemsCount(LETTER_OF_INTRODUCTION)>0 :
        htmltext = "30666-01.htm"
   elif npcId == 30666 and st.getInt("cond")==5 and st.getQuestItemsCount(SLAS_LETTER)>0 :
        htmltext = "30666-05.htm"
   elif npcId == 30666 and st.getInt("cond")==10 :
        htmltext = "30666-06.htm"
        st.set("cond","11")
        st.takeItems(OLMAHUMS_MONEY,1)
        st.giveItems(GREETINGS,3)
   elif npcId == 30666 and st.getInt("cond")==18 and st.getQuestItemsCount(KATARIS_LETTER)>0 and st.getQuestItemsCount(KAKANS_LETTER)>0 and st.getQuestItemsCount(NYAKURIS_LETTER)>0 and st.getQuestItemsCount(RAMUSS_LETTER)>0 :
          st.giveItems(MARK_OF_REFORMER,1)
          st.giveItems(SHADOW_WEAPON_COUPON_CGRADE,15)
          st.addExpAndSp(164032,17500)
          htmltext = "30666-07.htm"
          st.set("cond","0")
          st.set("onlyone","1")
          st.setState(COMPLETED)
          st.playSound("ItemSound.quest_finish")
          st.takeItems(KATARIS_LETTER,1)
          st.takeItems(KAKANS_LETTER,1)
          st.takeItems(NYAKURIS_LETTER,1)
          st.takeItems(RAMUSS_LETTER,1)
   elif npcId == 30668 and (st.getInt("cond")==5 or st.getInt("cond")==6) :
        htmltext = "30668-01.htm"
        st.set("cond","6")
        st.takeItems(SLAS_LETTER,1)
        st.addSpawn(30732,-4015,40141,-3664)
        st.addSpawn(27129,-4034,40201,-3665)
   elif npcId == 30668 and st.getInt("cond")==8:
        htmltext = "30668-02.htm"
        st.addSpawn(27130,-4106,40174,-3660)
   elif npcId == 30668 and st.getInt("cond")==9 :
        htmltext = "30668-03.htm"
        st.set("cond","10")
        st.giveItems(KATARIS_LETTER,1)
        st.takeItems(LETTER_OF_BETRAYER,1)
   elif npcId == 30732 and st.getInt("cond")==7 :
        htmltext = "30732-01.htm"
        st.set("cond","8")
        st.giveItems(OLMAHUMS_MONEY,1)
   elif npcId == 30669 and st.getInt("cond")==11 and st.getQuestItemsCount(GREETINGS)>0 :
        htmltext = "30669-01.htm"
   elif npcId == 30669 and st.getInt("cond")==12 :
        htmltext = "30669-04.htm"
        st.set("cond","13")
        st.giveItems(KAKANS_LETTER,1)
        st.takeItems(GREETINGS,1)
   elif npcId == 30670 and st.getInt("cond")==13 and st.getQuestItemsCount(GREETINGS)>0 :
        htmltext = "30670-01.htm"
   elif npcId == 30670 and st.getInt("cond")==14 and st.getQuestItemsCount(GREETINGS)>0 :
        htmltext = "30670-04.htm"
        st.set("cond","15")
        st.giveItems(NYAKURIS_LETTER,1)
        st.takeItems(GREETINGS,1)
   elif npcId == 30667 and st.getInt("cond")==15 and st.getQuestItemsCount(GREETINGS)>0 :
        htmltext = "30667-01.htm"
        st.set("cond","16")
        st.giveItems(UNDEAD_LIST,1)
        st.takeItems(GREETINGS,1)
   elif npcId == 30667 and st.getQuestItemsCount(BONE_FRAGMENT4)>0 and st.getQuestItemsCount(BONE_FRAGMENT5)>0 and st.getQuestItemsCount(BONE_FRAGMENT6)>0 and st.getQuestItemsCount(BONE_FRAGMENT7)>0 and st.getQuestItemsCount(BONE_FRAGMENT8)>0 :
        htmltext = "30667-03.htm"
        st.set("cond","18")
        st.takeItems(BONE_FRAGMENT4,1)
        st.takeItems(BONE_FRAGMENT5,1)
        st.takeItems(BONE_FRAGMENT6,1)
        st.takeItems(BONE_FRAGMENT7,1)
        st.takeItems(BONE_FRAGMENT8,1)
        st.giveItems(RAMUSS_LETTER,1)
        st.takeItems(UNDEAD_LIST,1)
   elif npcId == 30667 and st.getInt("cond")==16 :
        htmltext = "30667-02.htm"
   return htmltext

 def onKill(self,npc,player,isPet):
   st = player.getQuestState(qn)
   if not st : return 
   if st.getState() != STARTED : return 
   npcId = npc.getNpcId()
   if npcId == 27099 :
    if st.getInt("cond") == 1 and st.getQuestItemsCount(RIPPED_DIARY) < 7 and st.getQuestItemsCount(BOOK_OF_REFORM) >= 1 :
      if st.getQuestItemsCount(RIPPED_DIARY) == 6 :
        st.set("cond","2")
        st.addSpawn(27128,npc.getX(),npc.getY(),npc.getZ(),npc.getHeading(),True,300000)
        st.takeItems(RIPPED_DIARY,st.getQuestItemsCount(RIPPED_DIARY))
      else:
        st.giveItems(RIPPED_DIARY,1)
      st.playSound("ItemSound.quest_itemget")
   elif npcId == 27128 :
    if st.getInt("cond") == 2 and st.getQuestItemsCount(HUGE_NAIL) == 0 :
      st.giveItems(HUGE_NAIL,1)
      st.playSound("ItemSound.quest_middle")
      st.set("cond","3")
   elif npcId == 27129:
      st.set("cond","7")
   elif npcId == 27130 :
    if st.getInt("cond") == 8 :
      st.set("cond","9")
      st.giveItems(LETTER_OF_BETRAYER,1)
   elif npcId == 27131 :
    if st.getInt("cond") == 11 :
      st.set("cond","12")
   elif npcId == 27132 :
    if st.getInt("cond") == 13 :
      st.set("cond","14")
   elif npcId == 20404 :
    if st.getInt("cond") == 16 and st.getQuestItemsCount(BONE_FRAGMENT4) == 0 :
      st.giveItems(BONE_FRAGMENT4,1)
      st.playSound("ItemSound.quest_itemget")
   elif npcId == 20104 :
    if st.getInt("cond") == 16 and st.getQuestItemsCount(BONE_FRAGMENT5) == 0 :
      st.giveItems(BONE_FRAGMENT5,1)
      st.playSound("ItemSound.quest_itemget")
   elif npcId == 20102 :
    if st.getInt("cond") == 16 and st.getQuestItemsCount(BONE_FRAGMENT6) == 0 :
      st.giveItems(BONE_FRAGMENT6,1)
      st.playSound("ItemSound.quest_itemget")
   elif npcId == 20022 :
    if st.getInt("cond") == 16 and st.getQuestItemsCount(BONE_FRAGMENT7) == 0 :
      st.giveItems(BONE_FRAGMENT7,1)
      st.playSound("ItemSound.quest_itemget")
   elif npcId == 20100 :
    if st.getInt("cond") == 16 and st.getQuestItemsCount(BONE_FRAGMENT8) == 0 :
      st.giveItems(BONE_FRAGMENT8,1)
      st.playSound("ItemSound.quest_itemget")
   return

QUEST       = Quest(227,qn,"Test Of Reformer")
CREATED     = State('Start', QUEST)
STARTING    = State('Starting', QUEST)
STARTED     = State('Started', QUEST)
COMPLETED   = State('Completed', QUEST)


QUEST.setInitialState(CREATED)
QUEST.addStartNpc(30118)

QUEST.addTalkId(30118)

for npcId in [30666,30667,30669,30670,30732,30668]:
 QUEST.addTalkId(npcId)
for mobId in [20100,20102,20104,20404,20022,27099,27128,27130,27129,27132,27131]:
 QUEST.addKillId(mobId)