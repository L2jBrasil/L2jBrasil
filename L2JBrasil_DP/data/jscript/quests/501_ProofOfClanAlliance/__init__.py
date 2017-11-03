# Made by QuestDevs Team: DraX, DrLecter, Rolarga
# With invaluable support from: [TI]Blue, warrax
# v0.1.r0 2005.12.05

import sys
from com.it.br.gameserver.datatables.sql         import SkillTable
from com.it.br.gameserver.network.serverpackets      import CreatureSay 
from com.it.br.gameserver.network.serverpackets      import MagicSkillUser
from com.it.br.gameserver.model.quest        import State
from com.it.br.gameserver.model.quest        import QuestState
from com.it.br.gameserver.model.quest.jython import QuestJython as JQuest

qn="501_ProofOfClanAlliance"
qd="Proof of Clan Alliance"

# debug facility, turn this to 0 to disable
DEBUG=1

# Quest Npcs
SIR_KRISTOF_RODEMAI  = 30756
STATUE_OF_OFFERING   = 30757
WITCH_ATHREA         = 30758
WITCH_KALIS          = 30759

# Quest Items
HERB_OF_HARIT     = 3832
HERB_OF_VANOR     = 3833
HERB_OF_OEL_MAHUM = 3834
BLOOD_OF_EVA      = 3835
SYMBOL_OF_LOYALTY = 3837
PROOF_OF_ALLIANCE = 3874
VOUCHER_OF_FAITH  = 3873
ANTIDOTE_RECIPE   = 3872
POTION_OF_RECOVERY= 3889

#Quest mobs, drop, rates and prices
CHESTS=range(18257,18265)
MOBS=[[20685,HERB_OF_VANOR],[20644,HERB_OF_HARIT],[20576,HERB_OF_OEL_MAHUM]]
RATE=35
#stackable items paid to retry chest game: (default 10k adena)
RETRY_ITEMS=57
RETRY_PRICE=10000

def leader(player) :
    leaderst = None
    clan = player.getClan()
    if clan :
        leader=clan.getLeader().getPlayerInstance()  
        if leader :  
           leaderst = leader.getQuestState(qn)  
    return leaderst

def members_finnish(leaderst) :
    dead_ppl = 0
    try : dead_ppl =  leaderst.get("dead_list").split()
    finally :
       if dead_ppl :
          clan = leaderst.getPlayer().getClan()
          if clan :
              for i in dead_ppl :
                 try : clan.getClanMember(i).getPlayerInstance().getQuestState(qn).exitQuest(1)
                 except : pass

def randomize_chests(leaderst) :
    chests = [ 1,0,0,1,1,0,0,1]
    for i in range(len(chests)-1, 0, -1) :
        j = leaderst.getRandom(7)
        chests[i], chests[j] = chests[j], chests[i]
    for i in range(len(chests)): chests[i]=str(chests[i])
    leaderst.set("chests"," ".join(chests))
    return

class Quest (JQuest) :

 def __init__(self,id,name,descr):
     JQuest.__init__(self,id,name,descr)
     # a hashtable tracking this quest's (chest) spawns, indexed by leaderST
     self.spawn_tracker = {}    

 def chest_game(self,leaderst,command) :
    #northern point
    x,y,z=102000,103350,-3500
    #row dist,slope,col dist
    u,v,w=200,100,200
    if command == "start" :
       leaderst.set("chest_game","1")
       leaderst.set("chest_count","0")
       attempts = leaderst.getInt("chest_try")
       leaderst.set("chest_try",str(attempts+1))
       randomize_chests(leaderst)
       tempList = []
       for row in range(2) :
           for col in range(4) :
               tempList.append(leaderst.addSpawn(18257+(4*row+col),x+(row*u)+(col*v),y-(w*col),z,61000))
       self.spawn_tracker[leaderst]=tempList
       leaderst.startQuestTimer("chest_timer",60000)
    elif command == "stop" :
       try:
           leaderst.set("chest_game","0")
           if self.spawn_tracker.has_key(leaderst) :
               trackedSpawns = self.spawn_tracker.pop(leaderst)
               for chest in trackedSpawns :
                   chest.decayMe()
       except: pass

 def onAdvEvent (self,event,npc,player):
   leaderst = 0
   st = 0
   if player.isClanLeader() : leaderst = player.getQuestState(qn)
   else :
       # non-leaders doing this quest need both their own quest state and the leader's
       st = player.getQuestState(qn)
       if not st: return
       leaderst = leader(player)
       if not leaderst: return
       
   htmltext = event
#####  Leaders area  ######
   if event == "30756-03.htm" :
     leaderst.setState(PART2)
     leaderst.set("cond","1")
     leaderst.playSound("ItemSound.quest_accept")
   elif event == "30759-03.htm" :
     leaderst.setState(PART3)
     leaderst.set("cond","2")
     leaderst.set("dead_list"," ")
   elif event == "30759-07.htm" :
     for i in range(3) :
        leaderst.takeItems(SYMBOL_OF_LOYALTY,1)
     leaderst.giveItems(ANTIDOTE_RECIPE,1)
     leaderst.setState(PART4)
     leaderst.set("cond","3")
     leaderst.set("ingredients","0 0 0")
     leaderst.set("chest_count","0")
     leaderst.set("chest_game","0")
     leaderst.set("chest_try","0")
     st.startQuestTimer("poison_timer",3600000)
     st.addNotifyOfDeath(player)
     SkillTable.getInstance().getInfo(4082,1).getEffects(npc,player);
   elif event == "poison_timer" :
     members_finnish(leaderst)
     leaderst.exitQuest(1)
     htmltext = "30759-09.htm"
   elif event == "chest_timer" :
     htmltext = ""
     self.chest_game(leaderst,"stop")
     if DEBUG: htmltext = "DEBUG MESSAGE: chest timer event sent."
#####  Members area  ######
   elif event == "30757-04.htm" :
     deadlist = leaderst.get("dead_list").split()
     deadlist.append(player.getName())
     leaderst.set("dead_list"," ".join(deadlist))
     player.reduceCurrentHp(player.getStatus().getCurrentHp(),player)
     st.giveItems(SYMBOL_OF_LOYALTY,1)
     st.playSound("ItemSound.quest_accept")
   elif event == "30757-05.htm" :
     st.exitQuest(1)
   elif event == "30758-03.htm" :
     self.chest_game(leaderst,"start")
   elif event == "30758-07.htm" :
     if st.getQuestItemsCount(RETRY_ITEMS) < RETRY_PRICE :
        htmltext = "30758-06.htm"
     else :
        st.takeItems(RETRY_ITEMS,RETRY_PRICE) 
   return htmltext

 def onTalk (self,npc,player):
   htmltext = "no_quest.htm"
   st = player.getQuestState(qn)
   if not st : return htmltext
   leaderst = leader(player)
   if not leaderst : return "30756-10.htm"

   npcId = npc.getNpcId()
   id = st.getState()
   if not npcId in [SIR_KRISTOF_RODEMAI,STATUE_OF_OFFERING,WITCH_KALIS,WITCH_ATHREA] and id == CREATED : return htmltext

   if npcId == SIR_KRISTOF_RODEMAI:
     if player.getClan() == None or player.isClanLeader() == 0:
       st.exitQuest(1) 
       htmltext = "30756-10.htm"
     else :
       if player.getClan().getLevel() <= 2 :
         st.exitQuest(1)
         htmltext =  "30756-08.htm"
       elif player.getClan().getLevel() >= 4 :
         st.exitQuest(1)
         htmltext =  "30756-09.htm"
       elif st.getState() == PART4 and st.getQuestItemsCount(VOUCHER_OF_FAITH):
          st.playSound("ItemSound.quest_fanfare_2")
          st.takeItems(VOUCHER_OF_FAITH,1)
          st.giveItems(PROOF_OF_ALLIANCE,1)
          st.addExpAndSp(0,120000)
          htmltext="30756-07.htm"
          st.exitQuest(1)
       elif st.getState() in [PART2,PART3] :
         htmltext =  "30756-06.htm"
       elif st.getQuestItemsCount(PROOF_OF_ALLIANCE) == 0 :
         st.set("cond","0")
         htmltext =  "30756-01.htm"
       else :
         st.exitQuest(1)
         if DEBUG: htmltext = "DEBUG: rodemai can't decide"
   elif npcId == WITCH_KALIS:
     if player.getClan() == None :
       st.exitQuest(1)
       if DEBUG: htmltext= "DEBUG: Kalis said NO CLAN."
     else:
       if player.isClanLeader() == 1 :
         if st.getState() == PART2 :
           htmltext =  "30759-01.htm"
         elif st.getState() == PART3 :
           htmltext = "30759-05.htm"
           if st.getQuestItemsCount(SYMBOL_OF_LOYALTY) == 3 :
              try : deads=len(st.get("dead_list").split())
              finally :
                 if deads == 3 :
                    htmltext = "30759-06.htm"
                 elif DEBUG:
                    htmltext="DEBUG: 3 clan members MUST die. Quest items aren't enough."
         elif st.getState() == PART4:
           if st.getQuestItemsCount(HERB_OF_HARIT) and \
              st.getQuestItemsCount(HERB_OF_VANOR) and \
              st.getQuestItemsCount(HERB_OF_OEL_MAHUM) and \
              st.getQuestItemsCount(BLOOD_OF_EVA) and \
              st.getQuestItemsCount(ANTIDOTE_RECIPE) and \
              st.getInt("chest_game")== 3 :
             st.takeItems(ANTIDOTE_RECIPE,1)
             st.takeItems(HERB_OF_HARIT,1)
             st.takeItems(HERB_OF_VANOR,1)
             st.takeItems(HERB_OF_OEL_MAHUM,1)
             st.takeItems(BLOOD_OF_EVA,1)
             st.giveItems(POTION_OF_RECOVERY,1)
             st.giveItems(VOUCHER_OF_FAITH,1)
             timer=st.getQuestTimer("poison_timer")
             if timer != None : timer.cancel()
             members_finnish(st)
             htmltext =  "30759-08.htm"
             st.playSound("ItemSound.quest_finish")
           elif st.getQuestItemsCount(VOUCHER_OF_FAITH)==0:
             htmltext =  "30759-10.htm"
         else :
           st.exitQuest(1)
       else :
         try :
           if leaderst.getState() == PART4 :
              htmltext =  "30759-11.htm"
         except :
           st.exitQuest(1)
           if DEBUG: htmltext= "DEBUG: Kalis cancels your application, leader conditions aren't right."
   elif npcId == STATUE_OF_OFFERING:
     if player.getClan() == None :
       st.exitQuest(1)
       if DEBUG: htmltext= "DEBUG: Statue said NO CLAN."
     else :
       if player.isClanLeader() == 1 :
         if st.getState() in [PART2,PART3,PART4] :
           htmltext =  "30757-03.htm"
         else :
           st.exitQuest(1)
           if DEBUG: htmltext= "DEBUG: Statue finished your quest. A leader shouldn't talk to it at this stage."
       else :
         if player.getLevel() <= 39 :
           st.exitQuest(1)
           htmltext =  "30757-02.htm"
         else :
           dlist=[]
           deads=3
           try :
              dlist=leaderst.get("dead_list").split()
              deads = len(dlist)
           except :
              st.exitQuest(1)
              if DEBUG: htmltext= "DEBUG: Statue can't gather leader info."
           if  deads < 3 :
             if player.getName() not in dlist :
                if not st.getQuestItemsCount(SYMBOL_OF_LOYALTY) :
                   htmltext =  "30757-01.htm"
                else :
                   htmltext =  "30757-06.htm"
                   st.exitQuest(1)
                   if DEBUG: htmltext= "DEBUG: Statue hates cheaters, if you dont die for the clan, where you got the Proof from?"
             else :
                 htmltext = "you cannot die again!"
   elif npcId == WITCH_ATHREA :
     if player.getClan() == None :
       st.exitQuest(1)
       if DEBUG: htmltext = "DEBUG: Athrea said NO CLAN."
     else :
       if player.isClanLeader() == 1 :
          st.exitQuest(1)
          if DEBUG: htmltext = "DEBUG: clan leader isn't supposed to be here, you broke the quest."
       else :
          if leaderst :
             game_state=leaderst.getInt("chest_game")  
             if game_state == 0 :  
                if leaderst.getInt("chest_try") == 0 :  
                   htmltext="30758-01.htm"  
                else :  
                   htmltext="30758-05.htm"
             elif game_state == 1 : 
                htmltext="30758-09.htm"  
             elif game_state == 2 :
                timer=leaderst.getQuestTimer("chest_timer")
                if timer != None : timer.cancel()
                self.chest_game(st,"stop")
                leaderst.set("chest_game","3")
                st.giveItems(BLOOD_OF_EVA,1)
                st.playSound("ItemSound.quest_middle")
                htmltext="30758-08.htm"
                members_finnish(leaderst)
          else :
             st.exitQuest(1)
             if DEBUG: htmltext = "DEBUG: Athrea can't find clan leader info. Leader d/c?"
   return htmltext

 def onKill(self,npc,player,isPet):
     st = player.getQuestState(qn)
     if not st : return 
     if st.getState() != CREATED : return 
     leaderst = leader(player)
   
     ### first part, general checking
     npcId=npc.getNpcId()
     if not leaderst :
        st.exitQuest(1)
        if DEBUG: return "DEBUG: onKill can't find leader info. Leader d/c?"
        return "Quest Failed"
     else :
        ingredients = []
        timer=leaderst.getQuestTimer("poison_timer")
        if timer == None :
           self.chest_game(st,"stop")
           if DEBUG: return "DEBUG: onKill can't find poison timer. Too much time have passed"
           return "Quest Failed"
        try : 
           ingredients = leaderst.get("ingredients").split()
        finally :
     ### second part, herbs gathering
           if len(ingredients) :
              for m in range(len(MOBS)) :
                 if not int(ingredients[m]) :
                    if npcId == MOBS[m][0] :
                       if st.getQuestItemsCount(MOBS[m][1]) == 0 :
                          if st.getRandom(100) < RATE :
                             st.giveItems(MOBS[m][1],1)
                             ingredients[m]='1'
                             leaderst.set("ingredients"," ".join(ingredients))
                             st.playSound("ItemSound.quest_middle")
                             return
     ### third part, chest game
        if npcId in CHESTS :
           timer=leaderst.getQuestTimer("chest_timer")
           #if timer == None : self.chest_game(st,"stop");return "Time is up!"
           chests = leaderst.get("chests").split()
           for i in range(len(chests)) :
               if npcId == 18257+i and chests[i] == '1' :
                  npc.broadcastPacket(CreatureSay(npc.getObjectId(),0,npc.getName(),"###### BINGO! ######"))
                  count=int(leaderst.get("chest_count"))
                  if count < 4 :
                     count+=1
                     leaderst.set("chest_count",str(count))
                     if count == 4 :
                        leaderst.getQuestTimer("chest_timer").cancel()
                        self.chest_game(leaderst,"stop")
                        leaderst.set("chest_game","2")
                        st.playSound("ItemSound.quest_middle")
                     else :
                        st.playSound("ItemSound.quest_itemget")
     return

 # only leaders are registered for onDeath.  Therefore, st should always match that of the leader
 def onDeath(self, npc, pc, st) :
     if st.getPlayer() == pc :
       timer1=st.getQuestTimer("poison_timer")
       timer2=st.getQuestTimer("chest_timer")
       if timer1 != None : timer1.cancel()
       if timer2 != None : timer2.cancel()
       st.exitQuest(1)
     if DEBUG: return "DEBUG: Leader died. Quest failed."


QUEST       = Quest(501,qn,qd)
CREATED     = State('Start',     QUEST)
PART2       = State('Part2',     QUEST)
PART3       = State('Part3',     QUEST)
PART4       = State('Part4',     QUEST)
COMPLETED   = State('Completed', QUEST)

QUEST.setInitialState(CREATED)

for i in [SIR_KRISTOF_RODEMAI,STATUE_OF_OFFERING] :
    QUEST.addStartNpc(i)
    QUEST.addTalkId(i)

for i in [WITCH_KALIS,WITCH_ATHREA] :
    QUEST.addTalkId(i)
    

for i in range(len(MOBS)) :
    QUEST.addKillId(MOBS[i][0])

for i in CHESTS :
    QUEST.addKillId(i)