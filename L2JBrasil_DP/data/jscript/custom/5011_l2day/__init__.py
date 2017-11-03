### Settings
qn = "5011_l2day"
QuestId     = 5011
QuestName   = "l2day"
QuestDesc   = "custom"
InitialHtml = "1.htm"

### Items - Format [name, giveItemId, giveItemQty, giveItem1Id, giveItem1Qty, giveItem2Id, giveItem2Qty, giveItem3Id, giveItem3Qty, giveItem4Id, giveItem4Qty, takeItem1Id, takeItem1Qty, takeItem2Id, takeItem2Qty, takeItem3Id, takeItem3Qty, takeItem4Id, takeItem4Qty, takeItem5Id, takeItem5Qty, takeItem6Id, takeItem6Qty, takeItem7Id, takeItem7Qty, takeItem8Id, takeItem8Qty, takeItem9Id, takeItem9Qty]
Items       = [
["Lineage II", 3959, 3, 3958, 3, 3929, 3, 3932, 3, 3882, 1, 3881, 1, 3883, 1, 3877, 2, 3875, 1, 3879, 1, 3888, 1, 3876, 0, 3878, 0],
["NCSOFT", 3958, 1, 3959, 1, 3926, 1, 3927, 1, 3883, 1, 3876, 1, 3886, 1, 3884, 1, 3878, 1, 3887, 1, 3875, 0, 3877, 0, 3879, 0],
["CHRONICLE", 3934, 2, 3959, 2, 3958, 2, 3928, 2, 3876, 2, 3880, 1, 3885, 1, 3884, 1, 3883, 1, 3881, 1, 3882, 1, 3877, 1, 3887, 0]
]

### ---------------------------------------------------------------------------
### DO NOT MODIFY BELOW THIS LINE
### ---------------------------------------------------------------------------

print "importing " + QuestDesc + ": " + str(QuestId) + ": " + QuestName + ": " + str(len(Items)) + " item(s)",
import sys
from com.it.br.gameserver.model.quest import State
from com.it.br.gameserver.model.quest import QuestState
from com.it.br.gameserver.model.quest.jython import QuestJython as JQuest

### doRequestedEvent
def do_RequestedEvent(event, st, giveItem1Id, giveItem1Qty, giveItem2Id, giveItem2Qty, giveItem3Id, giveItem3Qty, giveItem4Id, giveItem4Qty, takeItem1Id, takeItem1Qty, takeItem2Id, takeItem2Qty, takeItem3Id, takeItem3Qty, takeItem4Id, takeItem4Qty, takeItem5Id, takeItem5Qty, takeItem6Id, takeItem6Qty, takeItem7Id, takeItem7Qty, takeItem8Id, takeItem8Qty, takeItem9Id, takeItem9Qty) :
    if st.getQuestItemsCount(takeItem1Id) >= takeItem1Qty and st.getQuestItemsCount(takeItem2Id) >= takeItem2Qty and st.getQuestItemsCount(takeItem3Id) >= takeItem3Qty and st.getQuestItemsCount(takeItem4Id) >= takeItem4Qty and st.getQuestItemsCount(takeItem5Id) >= takeItem5Qty and st.getQuestItemsCount(takeItem6Id) >= takeItem6Qty and st.getQuestItemsCount(takeItem7Id) >= takeItem7Qty and st.getQuestItemsCount(takeItem8Id) >= takeItem8Qty and st.getQuestItemsCount(takeItem9Id) >= takeItem9Qty :
        st.takeItems(takeItem1Id, takeItem1Qty)
        st.takeItems(takeItem2Id, takeItem2Qty)
        st.takeItems(takeItem3Id, takeItem3Qty)
        st.takeItems(takeItem4Id, takeItem4Qty)
        st.takeItems(takeItem5Id, takeItem5Qty)
        st.takeItems(takeItem6Id, takeItem6Qty)
        st.takeItems(takeItem7Id, takeItem7Qty)
        st.takeItems(takeItem8Id, takeItem8Qty)
        st.takeItems(takeItem9Id, takeItem9Qty)
        st.giveItems(giveItem1Id, giveItem1Qty)
        st.giveItems(giveItem2Id, giveItem2Qty)
        st.giveItems(giveItem3Id, giveItem3Qty)
        st.giveItems(giveItem4Id, giveItem4Qty)
        return "2.htm" 
    else :
        return "You do not have enough materials."

### main code
class Quest (JQuest) :

 def __init__(self,id,name,descr): JQuest.__init__(self,id,name,descr)

 def onEvent (self,event,st) :
    htmltext = event

    if event == "0":
        return InitialHtml

    for item in Items:
        if event == str(item[1]):
            htmltext = do_RequestedEvent(event, st, item[1], item[2], item[3], item[4], item[5], item[6], item[7], item[8], item[9], item[10], item[11], item[12], item[13], item[14], item[15], item[16], item[17], item[18], item[19], item[20], item[21], item[22], item[23], item[24], item[25], item[26])
    
    if htmltext != event:
      st.setState(COMPLETED)
      st.exitQuest(1)

    return htmltext

 def onTalk (Self,npcId,player):
   st = player.getQuestState(qn)
   htmltext = "<html><head><body>I have nothing to say to you.</body></html>"
   st.set("cond","0")
   st.setState(STARTED)
   return InitialHtml

### Quest class and state definition
QUEST       = Quest(QuestId,str(QuestId) + "_" + QuestName,QuestDesc)
CREATED     = State('Start',     QUEST)
STARTED     = State('Started',   QUEST)
COMPLETED   = State('Completed', QUEST)

QUEST.setInitialState(CREATED)

QUEST.addStartNpc(31774)
# pako the cat
QUEST.addTalkId(31774)

print "done ..."