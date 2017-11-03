#Made by Emperorc
import sys
from com.it.br.gameserver.model.quest          import State
from com.it.br.gameserver.model.quest          import QuestState
from com.it.br.gameserver.model.quest.jython   import QuestJython as JQuest
from com.it.br.util                            import Rnd
from com.it.br.gameserver.network.serverpackets        import NpcSay

qn = "335_SongOfTheHunter"

#NPCS
Grey = 30744
Tor = 30745
Cybellin = 30746

#Items
Cyb_Dagger = 3471
License_1 = 3692
License_2 = 3693
Leaf_Pin = 3694
Test_Instructions_1 = 3695
Test_Instructions_2 = 3696
Cyb_Req = 3697

#Mobs
Breka_Orc_Warrior = 20271
Windsus = 20553
Tarlk_Bugbear_Warrior = 20571
Gremlin_Filcher = 27149
Mobs = [Breka_Orc_Warrior, Windsus, Tarlk_Bugbear_Warrior, Gremlin_Filcher]
Lizardmen = [20578,20579,20581,20582,20641,20642,20643]

#Droplist Format- npcId:[itemId,itemAmount,chance]
Level_1 = {
    20550 : [3709,40,75], #Gaurdian Basilisk
    20581 : [3710,20,50], #Leto Lizardman Shaman
    27140 : [3711,1,100], #Breka Overlord Haka
    27141 : [3712,1,100], #Breka Overlord Jaka
    27142 : [3713,1,100], #Breka Overlord Marka
    27143 : [3714,1,100], #Windsus Aleph
    20563 : [3715,20,50], #Manashen Gargoyle
    20565 : [3715,20,50], #Enchanted Stone Golemn
    20555 : [3716,30,70], #Giant Fungus
    }
Level_2 = {
    20586 : [3717,20,50],   #Timak Orc Warrior
    20560 : [3718,20,50],   #Trisalim Spider
    20561 : [3718,20,50],   #Trisalim Tarantula
    20591 : [3719,30,100],  #Valley Treant
    20597 : [3719,30,100],  #Valley Treant Elder
    20675 : [3720,20,50],   #Tairim
    20660 : [3721,20,50],   #Archer of Greed
    27144 : [3722,1,100],   #Tarlk Raider Athu
    27145 : [3723,1,100],   #Tarlk Raider Lanka
    27146 : [3724,1,100],   #Tarlk Raider Triska
    27147 : [3725,1,100],   #Tarlk Raider Motura
    27148 : [3726,1,100],   #Tarlk Raider Kalath
    }

Grey_Advance = [
    #level 1
    [[3709],40],
    [[3710],20],
    [[3711,3712,3713],1],
    [[3714],1],
    [[3715],20],
    [[3716],30],
    #level 2
    [[3717],20],
    [[3718],20],
    [[3719],30],
    [[3720],20],
    [[3721],20],
    [[3722,3723,3724,3725,3726],1]
    ]

#Droplist Format- npcId : [itemRequired,itemGive,itemToGiveAmount,itemAmount,chance]
Tor_requests_1 = {
    20578 : [3727,3769,'1',40,80],  #Leto Lizardman Archer
    20579 : [3727,3769,'1',40,83],  #Leto Lizardman Soldier
    20586 : [3728,3770,'1',50,89],  #Timak Orc Warrior
    20588 : [3728,3770,'1',50,100], #Timak Orc Overlord
    20565 : [3729,3771,'1',50,100], #Enchanted Stone Golem
    20556 : [3730,3772,'1',30,50],  #Giant Monster Eye
    20557 : [3731,3773,'1',40,80],  #Dire Wyrm
    20550 : [3732,3774,'Rnd.get(2) + 1',100,100], #Guardian Basilisk
    20552 : [3733,3775,'1',50,100], #Fettered Soul
    20553 : [3734,3776,'1',30,50],  #Windsus
    20554 : [3735,3777,'2',100,100],#Grandis
    20631 : [3736,3778,'1',50,100], #Taik Orc Archer
    20632 : [3736,3778,'1',50,93],  #Taik Orc Warrior
    20600 : [3737,3779,'1',30,50],  #Karul Bugbear
    20601 : [3738,3780,'1',40,62],  #Tamlin Orc
    20602 : [3738,3780,'1',40,80],  #Tamlin Orc Archer
    27157 : [3739,3781,'1',1,100],  #Leto Chief Narak
    20567 : [3740,3782,'1',50,50],  #Enchanted Gargoyle
    20269 : [3741,3783,'1',50,93],  #Breka Orc Shaman
    20271 : [3741,3783,'1',50,100], #Breka Orc Warrior
    27156 : [3742,3784,'1',1,100],  #Leto Shaman Ketz
    27158 : [3743,3785,'1',1,100],  #Timak Raider Kaikee
    20603 : [3744,3786,'1',30,50],  #Kronbe Spider
    27160 : [3746,3788,'1',1,100],  #Gok Magok
    27164 : [3747,3789,'1',1,100]   #Karul Chief Orooto
    }

#Droplist Format- npcId : [itemRequired,itemGive,itemAmount,chance]
Tor_requests_2 = {
    20560 : [3749,3791,40,66],  #Trisalim Spider
    20561 : [3749,3791,40,75],  #Trisalim Tarantula
    20633 : [3750,3792,50,53],  #Taik Orc Shaman
    20634 : [3750,3792,50,99],  #Taik Orc Captain
    20641 : [3751,3793,40,88],  #Harit Lizardman Grunt
    20642 : [3751,3793,40,88],  #Harit Lizardman Archer
    20643 : [3751,3793,40,91],  #Harit Lizardman Warrior
    20661 : [3752,3794,20,50],  #Hatar Ratman Thief
    20662 : [3752,3794,20,52],  #Hatar Ratman Boss
    20667 : [3753,3795,30,90],  #Farcran
    20589 : [3754,3796,40,49],  #Fline
    20590 : [3755,3797,40,51],  #Liele
    20592 : [3756,3798,40,80],  #Satyr
    20598 : [3756,3798,40,100], #Satyr Elder
    20682 : [3758,3800,30,70],  #Vanor Silenos Grunt
    20683 : [3758,3800,30,85],  #Vanor Silenos Scout
    20684 : [3758,3800,30,90],  #Vanor Silenos Warrior
    20571 : [3759,3801,30,63],  #Tarlk Bugbear Warrior
    27159 : [3760,3802,1,100],  #Timak Overlord Okun
    27161 : [3761,3803,1,100],  #Taik Overlord Kakran
    20639 : [3762,3804,40,86],  #Mirror
    20664 : [3763,3805,20,77],  #Deprive
    20593 : [3764,3806,20,68],  #Unicorn
    20599 : [3764,3806,20,86],  #Unicorn Elder
    27163 : [3765,3807,1,100],  #Vanor Elder Kerunos
    20659 : [3766,3808,20,73],  #Grave Wanderer
    27162 : [3767,3809,1,100],  #Hatar Chieftain Kubel
    20676 : [3768,3810,10,64]   #Judge of Marsh
    }
#FilcherDropList Format- reqId : [item,amount,bonus]
Filcher = {
    3752 : [3794,20,3],
    3754 : [3796,40,5],
    3755 : [3797,40,5],
    3762 : [3804,40,5]
    }

#SpawnList Format- npcId : [item1,item2,npcToSpawn]
Tor_requests_tospawn = {
    20582 : [3739,3781,27157],  #Leto Lizardman Overlord
    20581 : [3742,3784,27156],  #Leto Lizardman Shaman
    20586 : [3743,3785,27158],  #Timak Orc Warrior
    20554 : [3746,3788,27160],  #Grandis
#level 2
    20588 : [3760,3802,27159],   #Timak Orc Overlord
    20634 : [3761,3803,27161],   #Tiak Orc Captain
    20686 : [3765,3807,27163],   #Vanor Silenos Chieftan
    20662 : [3767,3809,27162]    #Hatar Ratman Boss
    }

#RewardsList Format- requestId : [item,quantity,rewardAmount]
Tor_Rewards_1 = {
    3727 : [3769,40,2090],
    3728 : [3770,50,6340],
    3729 : [3771,50,9480],
    3730 : [3772,30,9110],
    3731 : [3773,40,8690],
    3732 : [3774,100,9480],
    3733 : [3775,50,11280],
    3734 : [3776,30,9640],
    3735 : [3777,100,9180],
    3736 : [3778,50,5160],
    3737 : [3779,30,3140],
    3738 : [3780,40,3160],
    3739 : [3781,1,6370],
    3740 : [3782,50,19080],
    3741 : [3783,50,17730],
    3742 : [3784,1,5790],
    3743 : [3785,1,8560],
    3744 : [3786,30,8320],
    3746 : [3788,1,27540],
    3747 : [3789,1,20560],
    }

Tor_Rewards_2 = {
    3749 : [3791,40,7250],
    3750 : [3792,50,7160],
    3751 : [3793,40,6580],
    3752 : [3794,20,10100],
    3753 : [3795,30,13000],
    3754 : [3796,40,7660],
    3755 : [3797,40,7660],
    3756 : [3798,40,11260],
    3758 : [3800,30,8810],
    3759 : [3801,30,7350],
    3760 : [3802,1,8760],
    3761 : [3803,1,9380],
    3762 : [3804,40,17820],
    3763 : [3805,20,17540],
    3764 : [3806,20,14160],
    3765 : [3807,1,15960],
    3766 : [3808,20,39100],
    3767 : [3809,1,39550],
    3768 : [3810,10,41200]
    }

#Format item : adenaAmount
Cyb_Rewards = {
    3699 : 3400,
    3700 : 6800,
    3701 : 13600,
    3702 : 27200,
    3703 : 54400,
    3704 : 108800,
    3705 : 217600,
    3706 : 435200,
    3707 : 870400
    }

Tor_menu = [
    "<a action=\"bypass -h Quest 335_SongOfTheHunter 3727\">C: Obtain 40 charms of Kadesh</a><br>",
    "<a action=\"bypass -h Quest 335_SongOfTheHunter 3728\">C: Collect 50 Timak Jade Necklaces</a><br>",
    "<a action=\"bypass -h Quest 335_SongOfTheHunter 3729\">C: Gather 50 Enchanted Golem Shards</a><br>",
    "<a action=\"bypass -h Quest 335_SongOfTheHunter 3730\">C: Collect and bring back 30 pieces of Giant Monster Eye Meat</a><br>",
    "<a action=\"bypass -h Quest 335_SongOfTheHunter 3731\">C: Collect and bring back 40 Dire Wyrm Eggs</a><br>",
    "<a action=\"bypass -h Quest 335_SongOfTheHunter 3732\">C: Collect and bring back 100 guardian basilisk talons</a><br>",
    "<a action=\"bypass -h Quest 335_SongOfTheHunter 3733\">C: Collect and bring back 50 revenants chains</a><br>",
    "<a action=\"bypass -h Quest 335_SongOfTheHunter 3734\">C: Collect and bring back 30 Windsus Tusks</a><br>",
    "<a action=\"bypass -h Quest 335_SongOfTheHunter 3735\">C: Collect and bring back 100 Grandis Skulls</a><br>",
    "<a action=\"bypass -h Quest 335_SongOfTheHunter 3736\">C: Collect and bring back 50 Taik Obsidian Amulets</a><br>",
    "<a action=\"bypass -h Quest 335_SongOfTheHunter 3737\">C: Bring me 30 heads of karul bugbears</a><br>",
    "<a action=\"bypass -h Quest 335_SongOfTheHunter 3738\">C: Collect 40 Tamlin Ivory Charms</a><br>",
    "<a action=\"bypass -h Quest 335_SongOfTheHunter 3739\">B: Bring me the head of Elder Narak of the leto lizardmen</a><br>",
    "<a action=\"bypass -h Quest 335_SongOfTheHunter 3740\">B: Collect and bring back 50 Enchanted Gargoyle Horns</a><br>",
    "<a action=\"bypass -h Quest 335_SongOfTheHunter 3741\">B: Collect and bring back 50 Coiled Serpent Totems</a><br>",
    "<a action=\"bypass -h Quest 335_SongOfTheHunter 3742\">B: Bring me the totem of the Serpent Demon Kadesh</a><br>",
    "<a action=\"bypass -h Quest 335_SongOfTheHunter 3743\">B: Bring me the head of Kaikis</a><br>",
    "<a action=\"bypass -h Quest 335_SongOfTheHunter 3744\">B: Collect and bring back 30 Kronbe Venom Sacs</a><br>",
    "<a action=\"bypass -h Quest 335_SongOfTheHunter 3746\">A: Recover the precious stone tablet that was stolen from a Dwarven cargo wagon by grandis</a><br>",
    "<a action=\"bypass -h Quest 335_SongOfTheHunter 3747\">A: Recover the precious Book of Shunaiman</a><br>",
    "<a action=\"bypass -h Quest 335_SongOfTheHunter 3749\">C: Collect and bring back 40 Trisalim Venom Sacs</a><br>",
    "<a action=\"bypass -h Quest 335_SongOfTheHunter 3750\">C: Collect and bring back 50 Taik Orc Totems</a><br>",
    "<a action=\"bypass -h Quest 335_SongOfTheHunter 3751\">C: Collect and bring back 40 Harit Lizardman barbed necklaces</a><br>",
    "<a action=\"bypass -h Quest 335_SongOfTheHunter 3752\">C: Collect and bring back 20 coins of the old empire</a><br>",
    "<a action=\"bypass -h Quest 335_SongOfTheHunter 3753\">C: Kill 30 farcrans and bring back their skins</a><br>",
    "<a action=\"bypass -h Quest 335_SongOfTheHunter 3754\">C: Collect and bring back 40 Tempest Shards</a><br>",
    "<a action=\"bypass -h Quest 335_SongOfTheHunter 3755\">C: Collect and bring back 40 Tsunami Shards</a><br>",
    "<a action=\"bypass -h Quest 335_SongOfTheHunter 3756\">C: Collect and bring back 40 Satyr Manes</a><br>",
    "<a action=\"bypass -h Quest 335_SongOfTheHunter 3758\">C: Collect and bring back 30 Shillien Manes</a><br>",
    "<a action=\"bypass -h Quest 335_SongOfTheHunter 3759\">C: Collect and bring back 30 tarlk bugbear totems</a><br>",
    "<a action=\"bypass -h Quest 335_SongOfTheHunter 3760\">B: Bring me the head of Okun</a><br>",
    "<a action=\"bypass -h Quest 335_SongOfTheHunter 3761\">B: Bring me the head of Kakran</a><br>",
    "<a action=\"bypass -h Quest 335_SongOfTheHunter 3762\">B: Collect and bring back 40 narcissus soulstones</a><br>",
    "<a action=\"bypass -h Quest 335_SongOfTheHunter 3763\">B: Collect and bring back 20 Deprive Eyes</a><br>",
    "<a action=\"bypass -h Quest 335_SongOfTheHunter 3764\">B: Collect and bring back 20 horns of summon unicorn</a><br>",
    "<a action=\"bypass -h Quest 335_SongOfTheHunter 3765\">B: Bring me the golden mane of Kerunos</a><br>",
    "<a action=\"bypass -h Quest 335_SongOfTheHunter 3766\">A: Bring back 20 skulls of undead executed criminals</a><br>",
    "<a action=\"bypass -h Quest 335_SongOfTheHunter 3767\">A: Recover the stolen bust of the late King Travis</a><br>",
    "<a action=\"bypass -h Quest 335_SongOfTheHunter 3768\">A: Recover 10 swords of Cadmus</a><br>"
    ]


def HasItems(st,check) :
    count = 0
    for list in Grey_Advance :
        count2 = 0
        for item in list[0] :
            if not st.getQuestItemsCount(item) >= list[1] :
                break
            count2 += 1
        if count2 == len(list[0]) :
            count += 1
    if count >= check :
        return 1
    return 0

def AutoChat(npc,text) :
    chars = npc.getKnownList().getKnownPlayers().values().toArray()
    if chars != None:
       for pc in chars :
          sm = NpcSay(npc.getObjectId(), 0, npc.getNpcId(), text)
          pc.sendPacket(sm)

def HasRequestCompleted(st,level) :
    rewards = Tor_Rewards_1
    if level == 2 :
        rewards = Tor_Rewards_2
    for req in rewards.keys() :
        if st.getQuestItemsCount(req) :
            if st.getQuestItemsCount(rewards[req][0]) >= rewards[req][1] :
                return req
    return 0    

class Quest (JQuest) :

    def __init__(self,id,name,descr):
        JQuest.__init__(self,id,name,descr)
        self.questItemIds = range(3692,3811) + [3471]

    def onAdvEvent (self,event,npc,player):
        st = player.getQuestState(qn)
        if not st: return
        htmltext = event
        if event == "30744-03.htm" :
            st.setState(STARTED)
            st.playSound("ItemSound.quest_accept")
            st.giveItems(Test_Instructions_1,1)
            st.set("cond","1")
            #set Memo = 0
        elif event == "30744-32.htm" :
            st.playSound("ItemSound.quest_finish")
            if st.getQuestItemsCount(Leaf_Pin) >= 20 :
                htmltext = "30744-33.htm"
                st.giveItems(57,20000)
            st.exitQuest(1)
        elif event == "30744-19.htm" :
            if not HasItems(st,1) :
                st.giveItems(Test_Instructions_2,1)
                htmltext = "30744-18.htm"
        elif event == "30745-03.htm" :
            if st.getQuestItemsCount(Test_Instructions_2) :
                htmltext = "30745-04.htm"
        elif event == "Tor_list_1" :
            if not st.getInt("hasTask") :
                htmltext = "<html><body>Guild Member Tor:<br>"
                pins = st.getQuestItemsCount(Leaf_Pin)
                reply_0 = Rnd.get(12)
                reply_1 = Rnd.get(12)
                reply_2 = Rnd.get(12)
                reply_3 = Rnd.get(12)
                reply_4 = Rnd.get(12)
                if Rnd.get(100) < 20 :
                    if pins < 4 and pins :
                        reply_0 = Rnd.get(6) + 12
                        reply_2 = Rnd.get(6)
                        reply_3 = Rnd.get(6) + 6
                    elif pins >= 4 :
                        reply_0 = Rnd.get(6) + 6
                        if not Rnd.get(20) :
                            reply_1 = Rnd.get(2) + 18
                        reply_2 = Rnd.get(6)
                        reply_3 = Rnd.get(6) + 6
                elif pins >= 4 :
                    if not Rnd.get(20) :
                        reply_1 = Rnd.get(2) + 18
                    reply_2 = Rnd.get(6)
                    reply_3 = Rnd.get(6) + 6
                htmltext += Tor_menu[reply_0] + Tor_menu[reply_1] + Tor_menu[reply_2] + Tor_menu[reply_3] + Tor_menu[reply_4]
                htmltext += "</body></html>"
        elif event == "Tor_list_2" :
            if not st.getInt("hasTask") :
                htmltext = "<html><body>Guild Member Tor:<br>"
                pins = st.getQuestItemsCount(Leaf_Pin)
                reply_0 = Rnd.get(10)
                reply_1 = Rnd.get(10)
                reply_2 = Rnd.get(5)
                reply_3 = Rnd.get(5) + 5
                reply_4 = Rnd.get(10)
                if Rnd.get(100) < 20 :
                    if pins < 4 and pins:
                        reply_0 = Rnd.get(6) + 10
                    elif pins >= 4 :
                        reply_0 = Rnd.get(6) + 10
                        if not Rnd.get(20):
                            reply_1 = Rnd.get(3) + 16
                elif pins >= 4 :
                    if not Rnd.get(20) :
                        reply_1 = Rnd.get(3) + 16
                htmltext += Tor_menu[reply_0 + 20] + Tor_menu[reply_1 + 20] + Tor_menu[reply_2 + 20] + Tor_menu[reply_3 + 20] + Tor_menu[reply_4 + 20]
                htmltext += "</body></html>"
        elif event == "30745-10.htm" :
            st.takeItems(Leaf_Pin,1)
            for item in range(3727,3811) :
                st.takeItems(item,-1)
            st.set("hasTask","0")
        elif event == "30746-03.htm" :
            if not st.getQuestItemsCount(Cyb_Req) :
                st.giveItems(Cyb_Req,1)
            if not st.getQuestItemsCount(3471) :
                st.giveItems(3471,1)
            if not st.getQuestItemsCount(3698) :
                st.giveItems(3698,1)
            st.takeItems(6708,-1)
        elif event == "30746-08.htm" :
            for item in Cyb_Rewards.keys() :
                if st.getQuestItemsCount(item) :
                    st.takeItems(item,-1)
                    st.giveItems(57,Cyb_Rewards[item])
                    break
        elif event == "30746-12.htm" :
            st.takeItems(3698,-1)
            st.takeItems(3697,-1)
            st.takeItems(3471,-1)
        elif event.isdigit() :
            event = int(event)
            st.giveItems(event,1)
            st.set("hasTask","1")
            event = event - 3712
            htmltext = "30745-" + str(event) + ".htm"
        return htmltext

    def onTalk (self,npc,player):
        htmltext = "<html><body>You are either not on a quest that involves this NPC, or you don't meet this NPC's minimum quest requirements.</body></html>"
        st = player.getQuestState(qn)
        if not st : return htmltext
        npcId = npc.getNpcId()
        cond = st.getInt("cond")
        id = st.getState()
        level = player.getLevel()
        bracelet_1 = st.getQuestItemsCount(License_1)
        bracelet_2 = st.getQuestItemsCount(License_2)
        if npcId == Grey :
            if id == CREATED :
                if level >= 35 :
                    htmltext = "02"
                else :
                    htmltext = "01"
            elif cond == 1 :
                if HasItems(st,3) :
                    htmltext = "12"
                    st.set("cond","2")
                    for item in range(3709,3717) :
                        st.takeItems(item,-1)
                    st.takeItems(Test_Instructions_1,-1)
                    st.giveItems(License_1,1)
                else :
                    htmltext = "11"
            elif cond == 2 :
                instructions = st.getQuestItemsCount(Test_Instructions_2)
                if level < 45 and bracelet_1 :
                    htmltext = "13"
                elif level >= 45 and bracelet_1 and not instructions :
                    htmltext = "16"
                elif instructions :
                    if HasItems(st,3) :
                        htmltext = "28"
                        st.set("cond","3")
                        for item in range(3718,3727) :
                            st.takeItems(item,-1)
                        st.takeItems(Test_Instructions_2,-1)
                        st.takeItems(License_1,-1)
                        st.giveItems(License_2,1)
                    else :
                        htmltext = "27"
            elif cond == 3 :
                htmltext = "29"
        elif npcId == Tor :
            if not bracelet_1 and not bracelet_2 :
                htmltext = "01"
            elif bracelet_1 :
                req = HasRequestCompleted(st,1)
                if not st.getInt("hasTask") :
                    if level >= 45 :
                        if st.getQuestItemsCount(Test_Instructions_2) :
                            htmltext = "04"
                        else :
                            htmltext = "05"
                    else :
                        htmltext = "02"
                elif req :
                    htmltext = "12"
                    item,quantity,reward = Tor_Rewards_1[req]
                    st.giveItems(Leaf_Pin,1)
                    st.giveItems(57,reward)
                    st.playSound("ItemSound.quest_middle")
                    st.set("hasTask","0")
                    st.takeItems(req,-1)
                    st.takeItems(item,-1)
                else :
                    htmltext = "08"
            elif bracelet_2 :
                req = HasRequestCompleted(st,2)
                if not st.getInt("hasTask") :
                    htmltext = "06"
                elif req :
                    htmltext = "13"
                    item,quantity,reward = Tor_Rewards_2[req]
                    st.giveItems(Leaf_Pin,1)
                    st.giveItems(57,reward)
                    st.playSound("ItemSound.quest_middle")
                    st.set("hasTask","0")
                    st.takeItems(req,-1)
                    st.takeItems(item,-1)
                else :
                    htmltext = "08"
        elif npcId == Cybellin :
            if not bracelet_1 and not bracelet_2 :
                htmltext = "01"
            elif bracelet_1 or bracelet_2 :
                if not st.getQuestItemsCount(Cyb_Req) :
                    htmltext = "02"
                elif st.getQuestItemsCount(3698) :
                    htmltext = "05"
                elif st.getQuestItemsCount(3707) :
                    htmltext = "07"
                    st.takeItems(3707,-1)
                    st.giveItems(57,Cyb_Rewards[3707])
                elif st.getQuestItemsCount(3708) :
                    htmltext = "11"
                    st.takeItems(3708,-1)
                elif st.getQuestItemsCount(3699) or st.getQuestItemsCount(3700) or st.getQuestItemsCount(3701) or st.getQuestItemsCount(3702) or \
                     st.getQuestItemsCount(3703) or st.getQuestItemsCount(3704) or st.getQuestItemsCount(3705) or st.getQuestItemsCount(3706) :
                    htmltext = "06"
                else :
                    htmltext = "10"
        if htmltext.isdigit():
			htmltext = str(npcId) + "-" + htmltext + ".htm"
        return htmltext

    def onKill(self,npc,player,isPet):
        st = player.getQuestState(qn)
        if not st : return
        npcId = npc.getNpcId()
        cond = st.getInt("cond")
        rand = Rnd.get(100)
        instructions_1 = st.getQuestItemsCount(Test_Instructions_1)
        instructions_2 = st.getQuestItemsCount(Test_Instructions_2)
        if cond == 1 and instructions_1 :
            if npcId in Level_1.keys() :
                item,amount,chance = Level_1[npcId]
                if rand < chance and st.getQuestItemsCount(item) < amount :
                    st.giveItems(item,1)
                    if st.getQuestItemsCount(item) >= amount :
                        st.playSound("ItemSound.quest_middle")
                    else :
                        st.playSound("ItemSound.quest_itemget")
            elif npcId == Breka_Orc_Warrior and rand < 10 :
                if st.getQuestItemsCount(3711) == 0 :
                    st.addSpawn(27140,300000)
                elif st.getQuestItemsCount(3712) == 0 :
                    st.addSpawn(27141,300000)
                elif st.getQuestItemsCount(3713) == 0 :
                    st.addSpawn(27142,300000)
            elif npcId == Windsus and not st.getQuestItemsCount(3714) and rand < 10 :
                st.addSpawn(27143,300000)
        elif cond == 2 :
            if instructions_2 :
                if npcId in Level_2.keys() :
                    item,amount,chance = Level_2[npcId]
                    if rand < chance and st.getQuestItemsCount(item) < amount :
                        st.giveItems(item,1)
                        if st.getQuestItemsCount(item) >= amount :
                            st.playSound("ItemSound.quest_middle")
                        else :
                            st.playSound("ItemSound.quest_itemget")
                elif npcId == Tarlk_Bugbear_Warrior and rand < 10 :
                    if st.getQuestItemsCount(3722) == 0 :
                        st.addSpawn(27144,300000)
                    elif st.getQuestItemsCount(3723) == 0 :
                        st.addSpawn(27145,300000)
                    elif st.getQuestItemsCount(3724) == 0 :
                        st.addSpawn(27146,300000)
                    elif st.getQuestItemsCount(3725) == 0 :
                        st.addSpawn(27147,300000)
                    elif st.getQuestItemsCount(3726) == 0 :
                        st.addSpawn(27148,300000)
            elif npcId in Tor_requests_1.keys() :
                req,give,giveAmount,amount,chance = Tor_requests_1[npcId]
                if rand < chance and st.getQuestItemsCount(req) and st.getQuestItemsCount(give) < amount :
                    st.giveItems(give,eval(giveAmount))
                    if st.getQuestItemsCount(give) >= amount :
                        st.playSound("ItemSound.quest_middle")
                    else :
                        st.playSound("ItemSound.quest_itemget")
                    if npcId in [27160,27164] and Rnd.get(2) :
                        st.addSpawn(27150,300000)
                        st.addSpawn(27150,300000)
                        AutoChat(npc,"We will destroy the legacy of the ancient empire!")
        elif cond == 3 :
            if npcId in Tor_requests_2.keys() :
                req,give,amount,chance = Tor_requests_2[npcId]
                if st.getQuestItemsCount(req) and st.getQuestItemsCount(give) < amount :
                    if rand < chance :
                        st.giveItems(give,1)
                        if st.getQuestItemsCount(give) >= amount :
                            st.playSound("ItemSound.quest_middle")
                        else :
                            st.playSound("ItemSound.quest_itemget")
                        if npcId == 27162 and Rnd.get(2) :
                            st.addSpawn(27150,300000)
                            st.addSpawn(27150,300000)
                            AutoChat(npc,"We will destroy the legacy of the ancient empire!")
                    if npcId in [20661,20662,20589,20590,20639] and not Rnd.get(20) :
                        st.addSpawn(Gremlin_Filcher,300000)
                        AutoChat(npc,"Get out! The jewels are mine!")
            elif npcId == Gremlin_Filcher :
                req = 0
                for item in Filcher.keys() :
                    if st.getQuestItemsCount(item) :
                        req = item
                        break
                if req :
                    item,amount,bonus = Filcher[req]
                    if st.getQuestItemsCount(item) < amount :
                        st.giveItems(item,bonus)
                        if st.getQuestItemsCount(item) >= amount :
                            st.playSound("ItemSound.quest_middle")
                        else :
                            st.playSound("ItemSound.quest_itemget")
                        AutoChat(npc,"What!")
        if npcId in Tor_requests_tospawn.keys() and rand < 10:
            it1,it2,id = Tor_requests_tospawn[npcId]
            if st.getQuestItemsCount(it1) and not st.getQuestItemsCount(it2) :
                st.addSpawn(id,300000)
        if npcId in Lizardmen and player.getActiveWeaponItem() and player.getActiveWeaponItem().getItemId() == Cyb_Dagger and st.getQuestItemsCount(Cyb_Req) and not st.getQuestItemsCount(3708):
            if Rnd.get(2) :
                if cond == 2 or cond == 3 :
                    for item in range(3698,3707) :
                        if st.getQuestItemsCount(item) :
                            st.giveItems(item+1,1)
                            st.takeItems(item,-1)
                            if item >= 3703 :
                                st.playSound("ItemSound.quest_jackpot")
                            break
            else :
                for item in range(3698,3707) :
                    st.takeItems(item,-1)
                st.giveItems(3708,1)
        return

QUEST = Quest(335,qn,"The Song of the Hunter")
CREATED   = State('Start',     QUEST)
STARTED   = State('started',   QUEST)
QUEST.setInitialState(CREATED)

QUEST.addStartNpc(Grey)
QUEST.addTalkId(Grey)
QUEST.addTalkId(Tor)
QUEST.addTalkId(Cybellin)

npcs = []
for npc in Level_1.keys() + Level_2.keys() + Tor_requests_1.keys() + Tor_requests_2.keys() + Tor_requests_tospawn.keys() + Mobs :
    if npc not in npcs :
        QUEST.addKillId(npc)
        npcs.append(npc)
del npcs