#By Bian 

import sys
from com.it.br.gameserver.model.quest import State
from com.it.br.gameserver.model.quest import QuestState
from com.it.br.gameserver.model.quest.jython import QuestJython as JQuest

qn = "384_WarehouseKeepersPastime"
#Variables
DROP_CHANCE=15
REQUIRED_ONE=10
REQUIRED_TWO=100

#Quest items
WH_MEDAL = 5964

#Rewards
REWARD_ONE=[1890,1893,1888,1887,1894,951,952]
REWARD_ONE_LOSE=[917,951,1892,4041]

REWARD_TWO=[883,952,401,852]
REWARD_TWO_CLIFF=[952,500,603,74]
REWARD_TWO_BAXT=[952,500,135]
#[2437,2463]

#Messages
default   = "<html><body>You are either not carrying out your quest or don't meet the criteria.</body></html>"
start     = "Start.htm"
starting  = "Starting.htm"
binfo1    = "Bingo_howto.htm"
bingo     = "Bingo_start.htm"
bingo0    = "Bingo_starting.htm"
bingcliff = "bingocliff.htm"
bingbaxt  = "bingobaxt.htm"
ext_msg   = "Quest aborted"
gametype  = "gameonetwo.htm"

#NPCs
#WK_CLIFF = 30182
#WK_BAXT = 30685
NPC=[30182,30685]
#Mobs
MOBS = [20556, 20559, 20677, 20241, 20286, 20758, 20759, 20760, 20242, 20281, 20243, 20282, 20635, 20605, 20668, 20945, 20946, 20947, 20948, 20949, 20950, 20942, 20943, 20944]
#templates
number  = ["second","third","fourth","fifth","sixth"]
header  = "<html><body>Warehouse:<br><br>"
headercliff  = "<html><body>Warehouse Freightman Cliff:<br><br>"
headerbaxt  = "<html><body>Warehouse Chief Baxt:<br><br>"
link    = "<td align=center><a action=\"bypass -h Quest 384_WarehouseKeepersPastime "
middle  = "</tr></table><br><br>Your selection thus far: <br><br><table border=1 width=120 hieght=64>"
footer  = "</table></body></html>"
loser   = "Wow! How unlucky can you get? Your choices are highlighted in red below. As you can see, your choices didn't make a single line! Losing this badly is actually quite rare!<br><br>You look so sad, I feel bad for you... Wait here...<br><br>.<br><br>.<br><br>.<br><br>Take this... I hope it will bring you better luck in the future.<br><br>"
winner  = "Excellent! As you can see, you've formed three lines! Congratulations! As promised, I'll give you some unclaimed merchandise from the warehouse. Wait here...<br><br>.<br><br>.<br><br>.<br><br>Whew, it's dusty! OK, here you go. Do you like it?<br><br>"
average = "Hum. Well, your choices are highlighted in red below. As you can see your choices didn't formed three lines... but you were near, so don't be sad. You can always get another few infernium ores and try again. Better luck in the future!<br><br>"

def partial(st) :
    html = " number:<br><br><table border=0><tr>"
    for z in range(1,10) :
        html += link+str(z)+"\">"+str(z)+"</a></td>"
    html += middle
    chosen = st.get("chosen").split()   
    for y in range(0,7,3) :
        html +="<tr>"
        for x in range(3) :
            html+="<td align=center>"+chosen[x+y]+"</td>"
        html +="</tr>"
    html += footer
    return html

def result(st) :
    chosen = st.get("chosen").split()
    grid = st.get("grid").split()
    html = "<table border=1 width=120 height=64>"
    for y in range(0,7,3) :
        html +="<tr>"
        for x in range(3) :
            html+="<td align=center>"
            if grid[x+y] == chosen[x+y] :
                html+="<font color=\"FF0000\"> "+grid[x+y]+" </font>"
            else :
                html+=grid[x+y]
            html+="</td>"
        html +="</tr>"
    html += footer
    return html
 
class Quest (JQuest) :

 def __init__(self,id,name,descr): JQuest.__init__(self,id,name,descr)

 def onEvent (self,event,st) :
    htmltext = event
    if event == "yes" :
       htmltext = "gotobaxt.htm"
       st.setState(STARTED)
       st.set("cond","1")
       st.playSound("ItemSound.quest_accept")
    elif event == "privetstviedva" :
        htmltext = "privetstviedva.htm"
    elif event == "privetstvietri" : 
        htmltext = "privetstvietri.htm"
    elif event == "privetstviebaxt.htm" : 
        st.set("cond","2")  
        st.playSound("ItemSound.quest_accept")  
    elif event == "binfo" :
        htmltext = binfo1
    elif event == "0" :
       htmltext = ext_msg
       st.exitQuest(1)
    elif event == "bingo" :
       if st.getQuestItemsCount(WH_MEDAL) >= REQUIRED_ONE :
         st.takeItems(WH_MEDAL,REQUIRED_ONE)
         htmltext = bingo0
         grid = range(1,10) #random.sample(xrange(1,10),9) ... damn jython that makes me think that inefficient stuff
         for i in range(len(grid)-1, 0, -1) :
           j = st.getRandom(8)
           grid[i], grid[j] = grid[j], grid[i]
         for i in range(len(grid)): grid[i]=str(grid[i])
         st.set("chosen","? ? ? ? ? ? ? ? ?")
         st.set("grid"," ".join(grid))
         st.set("playing","1")       
       else :
         htmltext = "You don't have required items"
    elif event == "bingocliff" :
       if st.getQuestItemsCount(WH_MEDAL) >= REQUIRED_TWO :
         st.takeItems(WH_MEDAL,REQUIRED_TWO)
         htmltext = bingcliff   
         grid = range(1,10) #random.sample(xrange(1,10),9) ... damn jython that makes me think that inefficient stuff
         for i in range(len(grid)-1, 0, -1) :
           j = st.getRandom(8)
           grid[i], grid[j] = grid[j], grid[i]
         for i in range(len(grid)): grid[i]=str(grid[i])
         st.set("chosen","? ? ? ? ? ? ? ? ?")
         st.set("grid"," ".join(grid))
         st.set("playingcliff","1")
       else :
         htmltext = "You don't have required items"
    elif event == "bingobaxt" :
       if st.getQuestItemsCount(WH_MEDAL) >= REQUIRED_TWO :
         st.takeItems(WH_MEDAL,REQUIRED_TWO)
         htmltext = bingbaxt   
         grid = range(1,10) #random.sample(xrange(1,10),9) ... damn jython that makes me think that inefficient stuff
         for i in range(len(grid)-1, 0, -1) :
           j = st.getRandom(8)
           grid[i], grid[j] = grid[j], grid[i]
         for i in range(len(grid)): grid[i]=str(grid[i])
         st.set("chosen","? ? ? ? ? ? ? ? ?")
         st.set("grid"," ".join(grid))
         st.set("playingbaxt","1")
       else :
         htmltext = "You don't have required items"         
    else :
       for i in range(1,10) :
          if event == str(i) :
            if st.getInt("playing"):
              chosen = st.get("chosen").split()
              grid = st.get("grid").split()
              if chosen.count("?") >= 3 :
                  chosen[grid.index(str(i))]=str(i)
                  st.set("chosen"," ".join(chosen))
                  if chosen.count("?")==3 :
                      htmltext = header
                      row = col = diag = 0
                      for i in range(3) :
                          if ''.join(chosen[3*i:3*i+3]).isdigit() : row += 1
                          if ''.join(chosen[i:9:3]).isdigit() : col += 1
                      if ''.join(chosen[0:9:4]).isdigit() : diag += 1
                      if ''.join(chosen[2:7:2]).isdigit() : diag += 1
                      if (col + row + diag) == 3 :
                          htmltext += winner
                          st.giveItems(REWARD_ONE[st.getRandom(len(REWARD_ONE))],1)
                          st.playSound("ItemSound.quest_finish")
                      elif (diag + row + col) == 0 :
                          htmltext += loser
                          st.giveItems(REWARD_ONE_LOSE[st.getRandom(len(REWARD_ONE_LOSE))],1)
                          st.playSound("ItemSound.quest_jackpot")
                      else :
                          htmltext += average                     
                          st.playSound("ItemSound.quest_giveup")
                      htmltext += result(st)
                      for var in ["chosen","grid","playing"]:
                          st.unset(var)
                  else :
                      htmltext = header+"Select your "+number[8-chosen.count("?")]+partial(st)   
            elif st.getInt("playingcliff"):
              chosen = st.get("chosen").split()
              grid = st.get("grid").split()
              if chosen.count("?") >= 3 :
                  chosen[grid.index(str(i))]=str(i)
                  st.set("chosen"," ".join(chosen))
                  if chosen.count("?")==3 :
                      htmltext = headercliff
                      row = col = diag = 0
                      for i in range(3) :
                          if ''.join(chosen[3*i:3*i+3]).isdigit() : row += 1
                          if ''.join(chosen[i:9:3]).isdigit() : col += 1
                      if ''.join(chosen[0:9:4]).isdigit() : diag += 1
                      if ''.join(chosen[2:7:2]).isdigit() : diag += 1
                      if (col + row + diag) == 3 :
                          htmltext += winner
                          st.giveItems(REWARD_TWO[st.getRandom(len(REWARD_TWO))],1)
                          st.playSound("ItemSound.quest_finish")
                      elif (diag + row + col) == 0 :
                          htmltext += loser
                          st.giveItems(REWARD_TWO_CLIFF[st.getRandom(len(REWARD_TWO_CLIFF))],1)
                          st.playSound("ItemSound.quest_jackpot")
                      else :
                          htmltext += average                     
                          st.playSound("ItemSound.quest_giveup")
                      htmltext += result(st)
                      for var in ["chosen","grid","playingcliff"]:
                          st.unset(var)
                  else :
                      htmltext = header+"Select your "+number[8-chosen.count("?")]+partial(st)
            elif st.getInt("playingbaxt"):
              chosen = st.get("chosen").split()
              grid = st.get("grid").split()
              if chosen.count("?") >= 3 :
                  chosen[grid.index(str(i))]=str(i)
                  st.set("chosen"," ".join(chosen))
                  if chosen.count("?")==3 :
                      htmltext = headerbaxt
                      row = col = diag = 0
                      for i in range(3) :
                          if ''.join(chosen[3*i:3*i+3]).isdigit() : row += 1
                          if ''.join(chosen[i:9:3]).isdigit() : col += 1
                      if ''.join(chosen[0:9:4]).isdigit() : diag += 1
                      if ''.join(chosen[2:7:2]).isdigit() : diag += 1
                      if (col + row + diag) == 3 :
                          htmltext += winner
                          st.giveItems(REWARD_TWO[st.getRandom(len(REWARD_TWO))],1)
                          st.playSound("ItemSound.quest_finish")
                      elif (diag + row + col) == 0 :
                          htmltext += loser
                          if st.getRandom(100) < 50 : 
                              st.giveItems(REWARD_TWO_BAXT[st.getRandom(len(REWARD_TWO_BAXT))],1)
                              st.playSound("ItemSound.quest_jackpot")
                          else :
                              st.giveItems(2437,1)
                              st.giveItems(2463,1)
                              st.playSound("ItemSound.quest_jackpot")
                      else :
                          htmltext += average                     
                          st.playSound("ItemSound.quest_giveup")
                      htmltext += result(st)
                      for var in ["chosen","grid","playingcliff"]:
                          st.unset(var)
                  else :
                      htmltext = header+"Select your "+number[8-chosen.count("?")]+partial(st)
            else:
              htmltext = "<html><body>Oops!</body></html>"
    return htmltext

 def onTalk (self,npc,player):
   htmltext = default
   st = player.getQuestState(qn)
   if not st : return htmltext

   npcId = npc.getNpcId()
   id = st.getState()
   if id == CREATED :
      st.set("cond","0")
      if npcId == 30182 :
         if player.getLevel() < 40 :
            st.exitQuest(1)
            htmltext = "Low_level.htm"
         else :
            htmltext = "privetstvie.htm"
   elif id == STARTED :
      if st.getQuestItemsCount(WH_MEDAL) >= 1 :
         #htmltext = gametype
         if npcId == 30182 :
            htmltext = "gamecliff.htm"
         elif npcId == 30685 :
            htmltext = "gamebaxt.htm"
      elif npcId == 30685 :
            htmltext = "privetstviebaxt.htm"    
      else :
         htmltext = "Starting2.htm"
   return htmltext

 def onKill (self, npc, player,isPet):
    st = player.getQuestState(qn)
    if not st : return
    if st :
        if st.getState() == STARTED :
            npcId = npc.getNpcId()
            cond = st.getInt("cond")
            count = st.getQuestItemsCount(WH_MEDAL)
            if cond == 1 :
                chance = DROP_CHANCE*Config.RATE_DROP_QUEST
                numItems, chance = divmod(chance,100)
                if st.getRandom(100) < chance : 
                    numItems += 1
                if numItems :
                    st.playSound("ItemSound.quest_itemget")
                    st.giveItems(WH_MEDAL,int(numItems))
        return

# Quest class and state definition
QUEST       = Quest(384,qn,"Warehouse Keeper's Pastime")
CREATED     = State('Start',     QUEST)
STARTED     = State('Started',   QUEST)


QUEST.setInitialState(CREATED)
QUEST.addStartNpc(30182)

for npcId in NPC:
  QUEST.addTalkId(npcId)

for i in MOBS :
  QUEST.addKillId(i)
