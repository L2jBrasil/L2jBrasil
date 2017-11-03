import sys
from com.it.br.gameserver.model.quest import State
from com.it.br.gameserver.model.quest import QuestState
from com.it.br.gameserver.model.quest.jython import QuestJython as JQuest
 
qn = "255_Tutorial"
 
# table for Quest Timer ( Ex == -2 ) [voice, html]
QTEXMTWO = {
    0  : ["tutorial_voice_001a","tutorial_human_fighter001.htm"],
    10 : ["tutorial_voice_001b","tutorial_human_mage001.htm"],
    18 : ["tutorial_voice_001c","tutorial_elven_fighter001.htm"],
    25 : ["tutorial_voice_001d","tutorial_elven_mage001.htm"],
    31 : ["tutorial_voice_001e","tutorial_delf_fighter001.htm"],
    38 : ["tutorial_voice_001f","tutorial_delf_mage001.htm"],
    44 : ["tutorial_voice_001g","tutorial_orc_fighter001.htm"],
    49 : ["tutorial_voice_001h","tutorial_orc_mage001.htm"],
    53 : ["tutorial_voice_001i","tutorial_dwarven_fighter001.htm"]
    }
# table for Client Event Enable (8) [html, x, y, z]
CEEa = {
    0  : ["tutorial_human_fighter007.htm",-71424,258336,-3109],
    10 : ["tutorial_human_mage007.htm",-91036,248044,-3568],
    18 : ["tutorial_elf007.htm",46112,41200,-3504],
    25 : ["tutorial_elf007.htm",46112,41200,-3504],
    31 : ["tutorial_delf007.htm",28384,11056,-4233],
    38 : ["tutorial_delf007.htm",28384,11056,-4233],
    44 : ["tutorial_orc007.htm",-56736,-113680,-672],
    49 : ["tutorial_orc007.htm",-56736,-113680,-672],
    53 : ["tutorial_dwarven_fighter007.htm",108567,-173994,-406]
    }
# table for Question Mark Clicked (9 & 11) learning skills [html, x, y, z]
QMCa = {
    0  : ["tutorial_fighter017.htm",-83165,242711,-3720],
    10 : ["tutorial_mage017.htm",-85247,244718,-3720],
    18 : ["tutorial_fighter017.htm",45610,52206,-2792],
    25 : ["tutorial_mage017.htm",45610,52206,-2792],
    31 : ["tutorial_fighter017.htm",10344,14445,-4242],
    38 : ["tutorial_mage017.htm",10344,14445,-4242],
    44 : ["tutorial_fighter017.htm",-46324,-114384,-200],
    49 : ["tutorial_fighter017.htm",-46305,-112763,-200],
    53 : ["tutorial_fighter017.htm",115447,-182672,-1440]
    }
# table for Question Mark Clicked (24) newbie lvl [html]
QMCb = {
    0  : "tutorial_human009.htm",
    10 : "tutorial_human009.htm",
    18 : "tutorial_elf009.htm",
    25 : "tutorial_elf009.htm",
    31 : "tutorial_delf009.htm",
    38 : "tutorial_delf009.htm",
    44 : "tutorial_orc009.htm",
    49 : "tutorial_orc009.htm",
    53 : "tutorial_dwarven009.htm"
    }
# table for Question Mark Clicked (35) 1st class transfer [html]
QMCc = {
    0  : "tutorial_21.htm",
    10 : "tutorial_21a.htm",
    18 : "tutorial_21b.htm",
    25 : "tutorial_21c.htm",
    31 : "tutorial_21g.htm",
    38 : "tutorial_21h.htm",
    44 : "tutorial_21d.htm",
    49 : "tutorial_21e.htm",
    53 : "tutorial_21f.htm"
    }
# table for Tutorial Close Link (26) 2nd class transfer [html]
TCLa = {
    1  : "tutorial_22w.htm",
    4  : "tutorial_22.htm",
    7  : "tutorial_22b.htm",
    11 : "tutorial_22c.htm",
    15 : "tutorial_22d.htm",
    19 : "tutorial_22e.htm",
    22 : "tutorial_22f.htm",
    26 : "tutorial_22g.htm",
    29 : "tutorial_22h.htm",
    32 : "tutorial_22n.htm",
    35 : "tutorial_22o.htm",
    39 : "tutorial_22p.htm",
    42 : "tutorial_22q.htm",
    45 : "tutorial_22i.htm",
    47 : "tutorial_22j.htm",
    50 : "tutorial_22k.htm",
    54 : "tutorial_22l.htm",
    56 : "tutorial_22m.htm"
    }
# table for Tutorial Close Link (23) 2nd class transfer [html]
TCLb = {
    4  : "tutorial_22aa.htm",
    7  : "tutorial_22ba.htm",
    11 : "tutorial_22ca.htm",
    15 : "tutorial_22da.htm",
    19 : "tutorial_22ea.htm",
    22 : "tutorial_22fa.htm",
    26 : "tutorial_22ga.htm",
    32 : "tutorial_22na.htm",
    35 : "tutorial_22oa.htm",
    39 : "tutorial_22pa.htm",
    50 : "tutorial_22ka.htm"
    }
# table for Tutorial Close Link (24) 2nd class transfer [html]
TCLc = {
    4  : "tutorial_22ab.htm",
    7  : "tutorial_22bb.htm",
    11 : "tutorial_22cb.htm",
    15 : "tutorial_22db.htm",
    19 : "tutorial_22eb.htm",
    22 : "tutorial_22fb.htm",
    26 : "tutorial_22gb.htm",
    32 : "tutorial_22nb.htm",
    35 : "tutorial_22ob.htm",
    39 : "tutorial_22pb.htm",
    50 : "tutorial_22kb.htm"
    }
class Quest (JQuest) :
 
    def __init__(self,id,name,descr): JQuest.__init__(self,id,name,descr)
 
    def onAdvEvent(self,event,npc,player):
        st = player.getQuestState(qn)
        classId = int(st.getPlayer().getClassId().getId())
        string = event[0:2]
        htmltext = ""
        # USER CONNECTED #
 
        if string == "UC" :
            playerLevel = player.getLevel()
            if playerLevel < 6 and st.getInt("onlyone") == 0 :
                uc = st.getInt("ucMemo")
                if uc == 0 :
                    st.set("ucMemo","0")
                    st.startQuestTimer("QT",10000)
                    st.set("ucMemo","0")
                    st.set("Ex","-2")
                elif uc == 1 :
                    st.showQuestionMark(1)
                    st.playTutorialVoice("tutorial_voice_006")
                    st.playSound("ItemSound.quest_tutorial")
                elif uc == 2 :
                    if st.getInt("Ex") == 2 :
                        st.showQuestionMark(3)
                        st.playSound("ItemSound.quest_tutorial")
                    if st.getQuestItemsCount(6353) == 1 :
                        st.showQuestionMark(5)
                        st.playSound("ItemSound.quest_tutorial")
                elif uc == 3 :
                    st.showQuestionMark(12)
                    st.playSound("ItemSound.quest_tutorial")
                    st.onTutorialClientEvent(0)
                else :
                    return
 
 
        # QUEST TIMER #
 
        elif string == "QT" :
            Ex = st.getInt("Ex")
            if Ex == -2 :
               if classId in QTEXMTWO.keys():
                  voice, htmltext = QTEXMTWO[classId]
                  st.playTutorialVoice(voice)
                  if st.getQuestItemsCount(5588) == 0 :
                     st.giveItems(5588,1)
                  st.startQuestTimer("QT",30000)
                  st.set("Ex","-3")
            elif Ex == -3 :
                st.playTutorialVoice("tutorial_voice_002")
                st.set("Ex","0")
            elif Ex == -4 :
                st.playTutorialVoice("tutorial_voice_008")
                st.set("Ex","-5")
 
        # TUTORIAL CLOSE [N] #
 
        elif string == "TE" :
          if event[2:].isdigit() :
            event_id = int(event[2:])
            if event_id == 0 :
                st.closeTutorialHtml()
            elif event_id == 1 :
                st.closeTutorialHtml()
                st.playTutorialVoice("tutorial_voice_006")
                st.showQuestionMark(1)
                st.playSound("ItemSound.quest_tutorial")
                st.startQuestTimer("QT",30000)
                st.set("Ex","-4")
            elif event_id == 2 :
                st.playTutorialVoice("tutorial_voice_003")
                htmltext = "tutorial_02.htm"
                st.onTutorialClientEvent(1)
                st.set("Ex","-5")
            elif event_id == 3 :
                htmltext = "tutorial_03.htm"
                st.onTutorialClientEvent(2)
            elif event_id == 5 :
                htmltext = "tutorial_05.htm"
                st.onTutorialClientEvent(8)
            elif event_id == 7 :
                htmltext = "tutorial_100.htm"
                st.onTutorialClientEvent(0)
            elif event_id == 8 :
                htmltext = "tutorial_101.htm"
                st.onTutorialClientEvent(0)
            elif event_id == 10 :
                htmltext = "tutorial_103.htm"
                st.onTutorialClientEvent(0)
            elif event_id == 12 :
                st.closeTutorialHtml()
            elif event_id == 23 :
                if classId in TCLb.keys():
                   htmltext = TCLb[classId]
            elif event_id == 24 :
                if classId in TCLc.keys():
                   htmltext = TCLc[classId]
            elif event_id == 25 :
                htmltext = "tutorial_22cc.htm"
            elif event_id == 26 :
                if classId in TCLa.keys():
                   htmltext = TCLa[classId]
            elif event_id == 27 :
                htmltext = "tutorial_29.htm"
            elif event_id == 28 :
                htmltext = "tutorial_28.htm"
        # CLIENT EVENT ENABLE [N] #
 
        elif string == "CE" :
          if event[2:].isdigit() :
            event_id = int(event[2:])
            playerLevel = player.getLevel()
            if event_id == 1 :
                if playerLevel < 6 :
                    st.playTutorialVoice("tutorial_voice_004")
                    htmltext = "tutorial_03.htm"
                    st.playSound("ItemSound.quest_tutorial")
                    st.onTutorialClientEvent(2)
            elif event_id == 2 :
                if playerLevel < 6 :
                    st.playTutorialVoice("tutorial_voice_005")
                    htmltext = "tutorial_05.htm"
                    st.playSound("ItemSound.quest_tutorial")
                    st.onTutorialClientEvent(8)
            elif event_id == 8 :
                if playerLevel < 6 :
                    if classId in CEEa.keys():
                       htmltext, x, y, z = CEEa[classId]
                       st.playSound("ItemSound.quest_tutorial")
                       st.addRadar(x,y,z)
                       st.playTutorialVoice("tutorial_voice_007")
                       st.set("ucMemo","1")
                       st.set("Ex","-5")
            elif event_id == 30 :
                if playerLevel < 6 and st.getInt("Die") == 0:
                    st.playTutorialVoice("tutorial_voice_016")
                    st.playSound("ItemSound.quest_tutorial")
                    st.set("Die","1")
                    st.showQuestionMark(8)
                    st.onTutorialClientEvent(0)
            elif event_id == 800000 :
                if playerLevel < 6 and st.getInt("sit") == 0:
                    st.playTutorialVoice("tutorial_voice_018")
                    st.playSound("ItemSound.quest_tutorial")
                    st.set("sit","1")
                    st.onTutorialClientEvent(0)
                    htmltext = "tutorial_21z.htm"
            elif event_id == 40 :
                if playerLevel == 5 and player.getClassId().level() == 0:
                   if st.getInt("lvl") < 5 :
                    if not player.getClassId().isMage() or classId == 49:
                     st.playTutorialVoice("tutorial_voice_014")
                     st.showQuestionMark(9)
                     st.playSound("ItemSound.quest_tutorial")
                     st.set("lvl","5")
                elif playerLevel == 6 and st.getInt("lvl") < 6 and player.getClassId().level() == 0:
                   st.playTutorialVoice("tutorial_voice_020")
                   st.playSound("ItemSound.quest_tutorial")
                   st.showQuestionMark(24)
                   st.set("lvl","6")
                elif playerLevel == 7 and player.getClassId().isMage() and classId != 49:
                   if st.getInt("lvl") < 7 and player.getClassId().level() == 0:
                      st.playTutorialVoice("tutorial_voice_019")
                      st.playSound("ItemSound.quest_tutorial")
                      st.set("lvl","7")
                      st.showQuestionMark(11)
                elif playerLevel == 10 :
                   if st.getInt("lvl") < 10:
                      st.playTutorialVoice("tutorial_voice_030")
                      st.playSound("ItemSound.quest_tutorial")
                      st.set("lvl","10")
                      st.showQuestionMark(27)
                elif playerLevel == 15 :
                   if st.getInt("lvl") < 15:
                      st.playSound("ItemSound.quest_tutorial")
                      st.set("lvl","15")
                      st.showQuestionMark(17)
                elif playerLevel == 19 :
                   if st.getInt("lvl") < 19:
                      race = st.getPlayer().getRace().ordinal()
                      if race != 5 and player.getClassId().level() == 0 :
                         if classId in [0,10,18,25,31,38,44,49,52]:
                           #st.playTutorialVoice("tutorial_voice_???")
                           st.playSound("ItemSound.quest_tutorial")
                           st.set("lvl","19")
                           st.showQuestionMark(35)
                elif playerLevel == 35 :
                   if st.getInt("lvl") < 35:
                      race = st.getPlayer().getRace().ordinal()
                      if race != 5 and player.getClassId().level() == 1 :
                         if classId in [1,4,7,11,15,19,22,26,29,32,35,39,42,45,47,50,54,56]:
                           #st.playTutorialVoice("tutorial_voice_???")
                           st.playSound("ItemSound.quest_tutorial")
                           st.set("lvl","35")
                           st.showQuestionMark(34)
            elif event_id == 45 :
                if playerLevel < 6 :
                   if st.getInt("HP") == 0:
                    st.playTutorialVoice("tutorial_voice_017")
                    st.playSound("ItemSound.quest_tutorial")
                    st.set("HP","1")
                    st.showQuestionMark(10)
                    st.onTutorialClientEvent(800000)
            elif event_id == 57 :
                if playerLevel < 6 and st.getInt("Adena") == 0:
                    st.playTutorialVoice("tutorial_voice_012")
                    st.playSound("ItemSound.quest_tutorial")
                    st.set("Adena","1")
                    st.showQuestionMark(23)
            elif event_id == 6353 :
                if playerLevel < 6 and st.getInt("Gemstone") == 0:
                    st.playTutorialVoice("tutorial_voice_013")
                    st.playSound("ItemSound.quest_tutorial")
                    st.set("Gemstone","1")
                    st.showQuestionMark(5)

 
        # QUESTION MARK CLICKED [N] #
 
        elif string == "QM" :
          if event[2:].isdigit() :
            MarkId = int(event[2:])
            if MarkId == 1 :
                st.playTutorialVoice("tutorial_voice_007")
                st.set("Ex","-5")
                if classId in CEEa.keys():
                   htmltext, x, y, z = CEEa[classId]
                   st.addRadar(x,y,z)
            elif MarkId == 3 :
                htmltext = "tutorial_09.htm"
            elif MarkId == 5 :
                if classId in CEEa.keys():
                   htmltext, x, y, z = CEEa[classId]
                   st.addRadar(x,y,z)
                htmltext = "tutorial_11.htm"
            elif MarkId == 7 :
                htmltext = "tutorial_15.htm"
                st.set("ucMemo","3")
            elif MarkId == 8 :
                htmltext = "tutorial_18.htm"
            elif MarkId == 9 :
                if classId in QMCa.keys():
                   htmltext, x, y, z = QMCa[classId]
                   st.addRadar(x,y,z)
            elif MarkId == 10 :
                htmltext = "tutorial_19.htm"
            elif MarkId == 11 :
                if classId in QMCa.keys():
                   htmltext, x, y, z = QMCa[classId]
                   st.addRadar(x,y,z)
            elif MarkId == 12 :
                htmltext = "tutorial_15.htm"
                st.set("ucMemo","4")
            elif MarkId == 13 :
                htmltext = "tutorial_30.htm"
            elif MarkId == 17 :
                htmltext = "tutorial_27.htm"
            elif MarkId == 23 :
                htmltext = "tutorial_24.htm"
            elif MarkId == 24 :
                if classId in QMCb.keys():
                   htmltext = QMCb[classId]
            elif MarkId == 26 :
                if st.getPlayer().getClassId().isMage() and classId != 49 :
                    htmltext = "tutorial_newbie004b.htm"
                else :
                    htmltext = "tutorial_newbie004a.htm"
            elif MarkId == 27 :
                htmltext = "tutorial_20.htm"
            elif MarkId == 34 :
                htmltext = "tutorial_28.htm"
            elif MarkId == 35 :
                if classId in QMCc.keys():
                   htmltext = QMCc[classId]
        if htmltext == "": return
        st.showTutorialHTML(str(htmltext))
        return
 
QUEST = Quest(255,qn,"Tutorial")
CREATED     = State('Start', QUEST)
STARTED     = State('Started', QUEST)
COMPLETED   = State('Completed', QUEST)

QUEST.setInitialState(CREATED)