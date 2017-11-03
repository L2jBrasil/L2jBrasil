#written by Rolarga
##################################FEEL FREE TO CHANGE IDs, REWARDS, PRICES, NPCs AND DROPDATAS THEY ARE JUST CUSTOM BY ME##################################

qn = "333_BlackLionHunt"

#Technical relatet Items
BLACK_LION_MARK = 1369
ADENA_ID = 57

#Drops & Rewards
CARGO_BOX1,CARGO_BOX2,CARGO_BOX3,CARGO_BOX4 = range(3440,3444)
UNDEAD_ASH,BLOODY_AXE_INSIGNIAS,DELU_FANG,STAKATO_TALONS = range(3848,3852)
SOPHIAS_LETTER1,SOPHIAS_LETTER2,SOPHIAS_LETTER3,SOPHIAS_LETTER4,LIONS_CLAW,LIONS_EYE,GUILD_COIN = range(3671,3678)
ALACRITY_POTION = 735
SCROLL_ESCAPE = 736
SOULSHOT_D = 1463
SPIRITSHOT_D = 2510
HEALING_POTION=1061
#Box rewards
GLUDIO_APPLE,CORN_MEAL,WOLF_PELTS,MONNSTONE,GLUDIO_WEETS_FLOWER,SPIDERSILK_ROPE,ALEXANDRIT,              \
SILVER_TEA,GOLEM_PART,FIRE_EMERALD,SILK_FROCK,PORCELAN_URN,IMPERIAL_DIAMOND,STATUE_SHILIEN_HEAD,         \
STATUE_SHILIEN_TORSO,STATUE_SHILIEN_ARM,STATUE_SHILIEN_LEG,COMPLETE_STATUE,FRAGMENT_ANCIENT_TABLE1,      \
FRAGMENT_ANCIENT_TABLE2,FRAGMENT_ANCIENT_TABLE3,FRAGMENT_ANCIENT_TABLE4,COMPLETE_TABLET = range(3444,3467)

#Price to Open a Box
OPEN_BOX_PRICE=650


#Lists
#List of all NPCs this Quest: Sophya,Redfoot,Rupio,Undinas(Shilien Temple),Lockirin(Dwarfen Village)
NPC=[30735,30736,30471,30130,30531,30737]
#List for some Item Groups
statue_list=[STATUE_SHILIEN_HEAD,STATUE_SHILIEN_TORSO,STATUE_SHILIEN_ARM,STATUE_SHILIEN_LEG]
tablet_list=[FRAGMENT_ANCIENT_TABLE1,FRAGMENT_ANCIENT_TABLE2,FRAGMENT_ANCIENT_TABLE3,FRAGMENT_ANCIENT_TABLE4]

#This Handels the Drop Datas npcId:[part,allowToDrop,ChanceForPartItem,ChanceForBox,PartItem]
#--Part, the Quest has 4 Parts 1=Execution Ground, 2=Partisan Hideaway 3=Near Giran Town, Delu Lizzards 4=Cruma Tower Area.
#--AllowToDrop --> if you will that the mob can drop, set allowToDrop==1. This is because not all mobs are really like official.
#--ChanceForPartItem --> set the dropchance for Ash in % for the mob with the npcId in same Line.
#--ChanceForBox --> set the dropchance for Boxes in % to the mob with the npcId in same Line. 
#--PartItem --> this defines wich Item should this Mob drop, because 4 Parts.. 4 Different Items.
DROPLIST={
#Execturion Ground - Part 1
20160:[1,1,67,29,UNDEAD_ASH],      #Neer Crawler
20171:[1,1,76,31,UNDEAD_ASH],      #Specter
20197:[1,1,89,25,UNDEAD_ASH],      #Sorrow Maiden
20200:[1,1,60,28,UNDEAD_ASH],      #Strain  
20201:[1,1,70,29,UNDEAD_ASH],      #Ghoul
20202:[1,0,60,24,UNDEAD_ASH],      #Dead Seeker (not official Monster for this Quest)
20198:[1,1,60,35,UNDEAD_ASH],      #Neer Ghoul Berserker
#Partisan Hideaway - Part 2
20207:[2,1,69,29,BLOODY_AXE_INSIGNIAS],  #Ol Mahum Guerilla
20208:[2,1,67,32,BLOODY_AXE_INSIGNIAS],  #Ol Mahum Raider
20209:[2,1,62,33,BLOODY_AXE_INSIGNIAS],  #Ol Mahum Marksman
20210:[2,1,78,23,BLOODY_AXE_INSIGNIAS],  #Ol Mahum Sergeant
20211:[2,1,71,22,BLOODY_AXE_INSIGNIAS],  #Ol Mahum Captain
#Delu Lizzardmans near Giran - Part 3
20251:[3,1,70,30,DELU_FANG],        #Delu Lizardman
20252:[3,1,67,28,DELU_FANG],        #Delu Lizardman Scout
20253:[3,1,65,26,DELU_FANG],        #Delu Lizardman Warrior
20781:[3,0,69,31,DELU_FANG],        #Delu Lizardman Shaman (not official Monster for this Quest)
#Cruma Area - Part 4
20157:[4,1,66,32,STAKATO_TALONS],    #Marsh Stakato
20230:[4,1,68,26,STAKATO_TALONS],    #Marsh Stakato Worker
20232:[4,1,67,28,STAKATO_TALONS],    #Marsh Stakato Soldier
20234:[4,1,69,32,STAKATO_TALONS]    #Marsh Stakato Drone
}

######################################## DO NOT MODIFY BELOW THIS LINE ####################################################################################

#Messages
#technical relatet messages
html        = "<html><body>"
htmlend        = "</body></html>"
back        = "<a action=\"bypass -h Quest 333_BlackLionHunt f_more_help\">Return</a>"
#Sophya
sophia        = "Mercenary Sophia:<br>"
#-Item information
p_redfoot      = html+sophia+"Red foot  you may not like him much personally.  But he is the type of person who is missed when he is not around.  Although he is in charge of delivery of military supplies, he also works on the side as a broker of stolen war trophies and loot.  He is also a great source of information for our mercenary troops.  Since he brings with him quite a lot of useful information, stop by often to ask him questions.<br><a action=\"bypass -h Quest 333_BlackLionHunt p_trader_info\">Ask about the traders union.</a><br><a action=\"bypass -h Quest 333_BlackLionHunt continue\">Continue with your mission.</a><br><a action=\"bypass -h Quest 333_BlackLionHunt leave\">Stop the mission you have been engaged in.</a>"+htmlend
p_no_items      = html+sophia+"Dear brother of the Black Lion.  Dont you think that the place where you should be is not in this village but in the battlefield where the evil spirits are running wild!<br><a action=\"bypass -h Quest 333_BlackLionHunt continue\">Continue with your mission.</a><br><a action=\"bypass -h Quest 333_BlackLionHunt leave\">Stop the mission you have been engaged in.</a>"+htmlend
p_trader_info    = html+sophia+"This cargo box definitely belongs to the Aden Traders Union. I can tell from the seal that is stamped on the box. If you want to return the cargo to them, go see Trader Union Member Morgan at the magic grocery store.  He works for the Aden Traders Union. <br><a action=\"bypass -h Quest 333_BlackLionHunt p_redfoot\">Ask about Red Foot. </a><br><a action=\"bypass -h Quest 333_BlackLionHunt continue\">Continue with your mission. </a><br><a action=\"bypass -h Quest 333_BlackLionHunt leave\">Stop the mission you have been engaged in.</a>"+htmlend
p_both_info      = html+sophia+"Dear brother.  I appreciate your hard work in carrying out the mission.  Ill give you your reward according to the number of proofs you have brought back.<br>What is that box?  It seems like you have brought back a cargo box that belongs to a traders union.  Since there is no clause regarding the returning of a cargo box written in our contract,  I guess we dont have the duty to return the cargo to the traders. But, if we return it to the traders union, perhaps they will give us some sort of reward?   If you dont feel like helping traders, you can go see Red Foot.   He is an expert in dealing with spoils that are hard to get rid of. <br><a action=\"bypass -h Quest 333_BlackLionHunt p_redfoot\">Ask about Red Foot. </a><br><a action=\"bypass -h Quest 333_BlackLionHunt p_trader_info\">Ask about the traders union.</a><br> <a action=\"bypass -h Quest 333_BlackLionHunt continue\">Continue with your mission.</a><br><a action=\"bypass -h Quest 333_BlackLionHunt leave\">Stop the mission you have been engaged in.</a>"+htmlend
p_only_item_info  = html+sophia+"Dear brother.  I appreciate your hard work in carrying out the mission. Ill give you your reward according to the number of proofs you have brought back.<br><a action=\"bypass -h Quest 333_BlackLionHunt continue\">Continue to carry out the mission.</a><br><a action=\"bypass -h Quest 333_BlackLionHunt leave\">Stop the mission you have been engaged in.</a>"+htmlend
p_leave_mission    = html+sophia+"I appreciate your hard work so far.  Even a lion needs a break.   Retire to the village and replenish your energy while resting there.  Maintaining ones own physical strength is one of the basic skills of being a mercenary.<br><a action=\"bypass -h Quest 333_BlackLionHunt start_chose_parts\">Tell him that you would like to take on a new mission.</a><br><a action=\"bypass -h Quest 333_BlackLionHunt r_exit\">Leave the mercenary troop.</a>"+htmlend
p_only_box_info    = html+sophia+"Dear brother.  I appreciate your hard work in carrying out the mission.  Ill give you your reward according to the number of proofs you have brought back.<br><a action=\"bypass -h Quest 333_BlackLionHunt continue\">Continue to carry out the mission.</a><br><a action=\"bypass -h Quest 333_BlackLionHunt leave\">Stop the mission you have been engaged in.</a>"+htmlend
p_first_eye      = html+sophia+"Wait a moment.  I present to you the <font color=\"LEVEL\">mark of the lions eye</font>. This is an award to recognize your distinguished services that you have demonstrated on the battlefield.  And new supplies have been issued for you.   Since they are expendable goods that will be useful in combat, store and guard them well!  Now, I expect you to continue to make great achievements."+htmlend
p_eye        = html+sophia+"Wait a moment.  I present to you the <font color=\"LEVEL\">mark of the lions eye</font>. This is an award to recognize your distinguished services that you have demonstrated on the battlefield.  And new supplies have been issued for you.   Since they are expendable goods that will be useful in combat, store and guard them well!  Now, I expect you to continue to make great achievements."+htmlend
#-exit messages
r_exit        = html+sophia+"What?!  You want to leave the Black Lion troop?  Well, I guess you must have your own reasons.  I wont ask about that  However, Ill tell you one thing  The only place for someone like yourself who was born with the blood of a fighter is in a mercenary troop.  No matter where you go, you are destined to end up on the battlefield.<br>Anyhow, if you really want to withdraw from the mercenary troop, bear this in mind.  Upon your departure, you will have to return the Black Lion Mark.  Also, according to your contributions of service you have achieved so far, you will receive the proper discharge pay.<br><a action=\"bypass -h Quest 333_BlackLionHunt continue\">Continue to work for the mercenary troop.</a><br><a action=\"bypass -h Quest 333_BlackLionHunt exit\">Leave the mercenary troop.</a>"+htmlend
exit        = html+sophia+"If you really have already made up your mind, I will not hold you back any more.  Please return the Black Lion Mark.  I regret that our relationship as comrades will end from here on.  Oh, take this before you go.  This is the reward for your services you have earned until now for the mercenary troop.  Wherever you go, you can use it as seed money to establish yourself in a new place.  Well, until the day when we draw our swords together again.  Good luck brother!"+htmlend
#-Start
start_error1    = html+sophia+"As I said before, our current mission is to drive out the evil spirits in this area.  However, since the main force of the mercenary troop has been dispatched to Gludio, our military force is suffering a shortage.  The only thing we are managing to do right now is to contain the evil spirits from attacking the village.  Even now, if I can find some tough skinned fighters, I am willing to hire them right away to supplement our force!<br>(This is a quest that can be carried out by a character of level 25 or above and in possession of the Black Lion Mark.)"+htmlend
start_error2    = html+sophia+"As I said before, our current mission is to drive out the evil spirits in this area.  However, since the main force of the mercenary troop has been dispatched to Gludio, our military force is suffering a shortage.  The only thing we are managing to do right now is to contain the evil spirits from attacking the village.  Even now, if I can find some tough skinned fighters, I am willing to hire them right away to supplement our force!<br>You seem like you have a lot of experience fighting evil spirits.  Someone of your caliber and experience would be easily qualified to join our mercenary troop However, according to our policy, in order to be a member of our mercenary troop, you will still have to pass our test.  If you are interested, go see <font color=\"LEVEL\">Captain Leopold in Gludin</font>. If you are approved by him and can bring back the Black Lion Mark, I will take you in as one of our brothers and give you the opportunity to join us in the fight.<br>(This is a quest that can be carried out by a character of level 25 or above and in possession of the Black Lion Mark.)"+htmlend
start_start      = html+sophia+"Dear brother of the Black Lion.  Our current situation is as follows.  As I said before, our current mission is to drive out the evil spirits in this area.  However, since the main force of the mercenary troop has been dispatched to Gludio, our military force is suffering a shortage.  The only thing we are managing to do right now is to contain the evil spirits from attacking the village. <br>Fortunately, Captain Leopold of Gludin has sent many newly selected mercenary brothers.  So we can launch our attack on the stronghold of the evil spirits in earnest.  Brother, I would like you to join us in this fight.<br><a action=\"bypass -h Quest 333_BlackLionHunt start\">Tell him that you will join them in the fight.</a>"+htmlend
start_explain    = html+sophia+"All right!  The time has come for the black lions that have been crouching until now, to bare their claws and start hunting!<br>Now, I will explain our combat situation.  We currently have four targets of attack.  They are the Execution Ground, the Partisan Hideaway, southern shoreline area, and the Cruma Marshlands.  Since we do not have enough combat power to hold a drawn-out war, we will dispatch a small group of soldiers as a strike force to carry out the strategy of hit and run to drive out the evil spirits.<br><a action=\"bypass -h Quest 333_BlackLionHunt start_parts\">Listen to the mission of each area.</a>"+htmlend
start_parts      = html+sophia+"About which mission would you like to hear about?<br><br><a action=\"bypass -h Quest 333_BlackLionHunt p1_explanation\">Clean out the undead in the Execution Ground.</a><br><a action=\"bypass -h Quest 333_BlackLionHunt p2_explanation\">Drive out the ol mahum in the Partisan Hideaway.</a><br><a action=\"bypass -h Quest 333_BlackLionHunt p3_explanation\">Drive out the delu lizardman in the southern shore area.</a><br><a action=\"bypass -h Quest 333_BlackLionHunt p4_explanation\">Smash the marsh stakato in the Cruma Marshlands.</a>"+htmlend
start_ask_again    = html+sophia+"Dear Black Lion brother, the war with the evil spirits has already started! Dont you think you should be active in this fight with us?<br><a action=\"bypass -h Quest 333_BlackLionHunt start_parts\">Listen to the explanation about the mission.</a>"+htmlend
start_continue    = html+sophia+"Hurry!  Rush to the battlefield, destroy your enemies and taste the sweetness of victory!"+htmlend
#-Part 1
p1_explanation    = html+sophia+"The Execution Ground is located in the eastern part of the village. It is an eerie place where people can hear the never ending cries of dead souls. Your mission is to clean out the undead that are infesting the place. According to a rumor, in order to pay for the spilled blood of innocent people who were killed during the farmers uprising, the undead came back to this world Well, all I know is that we need to carry out the given assignment. Still, I dont feel good about this for some reason. <br><a action=\"bypass -h Quest 333_BlackLionHunt p1_t\">Take on the mission.</a><br><a action=\"bypass -h Quest 333_BlackLionHunt start_chose_parts\">Listen to the explanation about another mission.</a>"+htmlend
p1_take        = html+sophia+"The undead that we need to drive out are <font color=\"LEVEL\">specters, sorrow maiden, neer crawlers, neer ghoul berserker, strains and ghouls</font>. I dont know whether you know about this or not.  But when an undead is destroyed, it leaves behind a handful of ashes.  Bring me back that as proof of victory.  The more <font color=\"LEVEL\">ashes of undead </font> you bring, the more rewards you will receive.  Since you once worked under Captain Leopold, you already know the rule about proofs and rewards, dont you?  <br> Now then, hurry up.  Prepare yourself for combat and leave for the Execution Ground immediately. Prove to everybody the fact that even the undead who have been resurrected from the dungeon of purgatory will not be a match for us the Black Lion troop!"+htmlend
#-Part 2
p2_explanation    = html+sophia+"This mission is to drive out the remnants of the Grecian military force that is currently encamped in the northwestern part of  the Horseshoe Valley.  Dont take them too lightly by thinking that they are mere rabble of defeated soldiers.    Your opponents are ol mahums of the Bloody Axe army led by the Blood Lord Nurka.  They are a formidable enemy that you shouldnt take too lightly.<br><a action=\"bypass -h Quest 333_BlackLionHunt p2_t\">Take on the mission.<\a> <br> <a action=\"bypass -h Quest 333_BlackLionHunt start_chose_parts\">Listen to the explanation about another mission.</a>"+htmlend
p2_take        = html+sophia+"The enemies that you will have to defeat are <font color=\"LEVEL\">ol mahum guerillas, ol mahum raiders, ol mahum marksman, ol mahum sergeants and ol mahum captains</font>.<br>As a proof that you have defeated the enemy, bring me back the <font color=\"LEVEL\">bloodyaxe insignia</font> the symbol of the partisan army.  The more proofs you bring, the greater your reward will be.  Well, since you once worked under Captain Leopold, you already know the rule about proofs and rewards, dont you?<br>Now then, hurry up and leave for the Partisan Hideaway. Show them the wrath of the Black Lion troop to those rampaging ol mahums!"+htmlend
#-Part 3
p3_explanation    = html+sophia+"The headquarters of delu lizardmen was originally located at the shore area in the southern part of Giran.  However, lately, many of them are infiltrating into Dion territory.  We dont know yet whether they are entering Dion just to find food or to prepare some large scale invasion.  But what is clear is that they are making their movements with some clear goal.  Our mission is to smash their units one by one and instill fear in them thereby discouraging them from settling down in this area.<br><a action=\"bypass -h Quest 333_BlackLionHunt p3_t\">Take on the mission.</a> <br> <a action=\"bypass -h Quest 333_BlackLionHunt start_chose_parts\">Listen to the explanation about another mission.</a>"+htmlend
p3_take        = html+sophia+"The enemies that you will deal with are <font color=\"LEVEL\">Delu Lizardmen, Delu Lizardman Scouts and Delu Lizardman Warriors</font>. As a proof that you slayed them, bring back the <font color=\"LEVEL\">Delu Lizardmens' Teeth</font>. But be careful.  Comparing with the felim tribe in Gludio or langk lizardman, they are much more violent and hostile.<br>The more proofs of victory you bring, the greater your reward will be.  Well, since you once worked under Captain Leopold, you should already know the rule about proofs and rewards, dont you? <br>Well then, leave for the battleground.  Trample down and destroy those lizards that are running amok without knowing their proper place!"+htmlend
#-Part 4
p4_explanation    = html+sophia+"This mission has to deal with weird evil creatures called stacato that are infesting the Cruma Marshlands.  Have you ever seen a stacato?  It looks like an insect.  Its a sinister race that gives you a creepy feeling.  Their body is covered by a tight stakato chitin and they have sharp claws instead of hands.  They also have surprisingly fast movements.  You should never underestimate them. <br>To make matters worse, the Marshlands are inhabited by giant leeches, spiders and strange evil spirits hovering around the Tower of Giants.  This makes the place very treacherous when one has to carry out military operations.<br><a action=\"bypass -h Quest 333_BlackLionHunt p4_t\">Take on the mission.</a><br><a action=\"bypass -h Quest 333_BlackLionHunt start_chose_parts\">Listen to the explanation about another mission.</a>"+htmlend
p4_take        = html+sophia+"The enemies you will have to destroy are <font color=\"LEVEL\">marsh stakatos, marsh stakato workers, marsh stakato soldiers and marsh stakato drones</font>. As a proof of your victory, bring me the claws of the stakatos you kill.  As always, The more proofs of victory you bring, the greater your reward will be. <br>Now, get ready for combat and leave for the Cruma Marshlands immediately.  This time, make sure to teach those stakatos the fearful lesson of the Black Lion troop!"+htmlend
#Redfoot
redfoot        = "Mercenary Red Foot:<br>"
f_no_box      = html+redfoot+"Hey brother!  Did you tell me that youre currently on active duty?  I know that must be hard work!  Is there anything I can help you with?<br><a action=\"bypass -h Quest 333_BlackLionHunt f_info\">Ask whether he has any useful information.</a>"+htmlend
f_give_box      = html+redfoot+"Hey brother!  Did you tell me that youre currently on active duty?  I know that must be hard work!  Is there anything I can help you with?<br>What kind of box is that?  Oh, I know.  Thats a cargo box used by traders.  Since you brought it to me  I can assume that you dont have any intention of returning it to the merchant, am I right?  All right.  Ill open the box for you.  Of course, you can have the contents of the box.  Instead, Ill charge you a small fee.  I think <font color=\"LEVEL\">650 adena</font> will be appropriate.  Think of it as the price for opening the box and for keeping my mouth shut.<br><a action=\"bypass -h Quest 333_BlackLionHunt f_give\">Ask him to open the box for you.</a><br>"+back+htmlend
f_rnd_list      = ["Some time ago, I overheard some dwarves talking among themselves in a blacksmiths shop  The head of the dwarves perhaps he is called the first elder?  Anyway, he was desperately looking for a scroll that had hieroglyphic characters of giants on it.  Although I didnt get to hear the details, it seemed like some very critical secret regarding the giants scientific technology was written on it.  I always thought that dwarves were just busying themselves with their work in blacksmith shops and being warehouse keepers.  But what do you know  I think they have been working on secret things while avoiding the scrutiny of humans.<br>",
             "Some time ago, while I was talking with the members of the Dark Elf Guild, I found out that Dark Elves worship the Goddess Shilen!!   Are you saying that everybody already knows about that?   Well, I heard about it for the first time  <br>Why would you want to worship a goddess of death  It doesnt make any sense to me.  But according to those who have been to the Dark Forest, the Temple of Shilen built by the Dark Elves is truly magnificent.  They told me that the Abyssal Celebrants at the temple are gathering fragments of the statue of Goddess Shilen from everywhere!<br>",
             "Have you ever heard of a hatchling?  It is a cute baby dragon that has been hatched from the egg of a wyrm or a drake.  Among pet handlers, I heard that there is someone who knows how to raise a hatchling as ones own pet  I think his name is Cooper or something like that.  If you are interested in keeping a hatchling as a pet, why dont you go see him!<br>",
             "I will give you the information that a clan leader, who wants to grow the power of his clan would be interested in obtaining.  In each area, there are aristocrats who give support to small clans.  These aristocrats would include Sir Kristof Rodemai in Giran Castle Town and Sir Gustaf Athebaldt of Oren Castle Town.  Of course they are not just philanthropists.  I would say they would want something in return for their support of a clan, wouldnt you agree?<br>",
             "Some time ago, I heard a rumor that there is a society of ancient coin collectors.  At first, I thought that they must be collectors of some anniversary coins that are not very valuable.  But later, I found out that the value of the coins they collect is extremely high.  And members of this society are very enthusiastic about their collection activities that whoever brings some rare coins to them, they will trade them for some high priced items!  I heard that in order to join the coin collection society, you have to go see a dwarf called Sorint at the Hunters Village.<br>",
             "I heard that if you go near the Ivory Tower in Oren, you will find a fake alchemist.  It is said that he goes around telling people that he can make some magic potion that will make peoples wishes come true  Many people have fallen victim to his fake potion.  But what is surprising is that once in a blue moon, after using the potion peoples wishes actually did come true.  The problem is that this only happens very very rarely<br>",
             "Hush!  Come closer to me.  Ill give you some information of great worth.  I overheard traders talking among themselves.  Some time ago, the cargo wagon of the Aden Business Association that was traveling from Giran to Dion had been robbed and an item of great value was stolen.  It is a gem called the Imperial Diamond and it is a priceless gem.  Well, since it was a gemstone that used to decorate a kings crown, it wouldnt be an ordinary item now would it?  I wish I could have the opportunity to look at it even once in my life time<br>",
             "Have you heard about the rumor?  Antharas, the earthdragon who had been sleeping in Dragon Valley has awoken.  This is terrible news  If it comes out of the Lair of Antharas and runs amok, all of Giran region will fall into a state of pandemonium in no time <br>However, there is someone who is recruiting people to form a militia force to catch Antharas.  It is a woman named Gabrielle in Giran Castle Town.  However, does she really think that they will have any chance against the dragon  Unfortunately, I think it is way beyond their power!<br>",
             "In this village, there is a young man who is dreaming of becoming the best chef in the kingdom.  His name is Jonas.  Lately he has been working hard to prepare himself to compete in a culinary competition.   He is looking for an adventurer who can find ingredients to make exotic dishes<br>",
             "In Giran Castle Town, there is a young man whose only aim in life is to take revenge.  Everybody has been telling him that its useless but he is determined to kill the earthdragon Antharas with his own hands.  Every day, he makes special arrows.  Furthermore, if anybody brings him the raw materials he needs to make the arrows, he will pay the person with an ample reward.  If you are interested, why dont you go see him?  His name is  Belton and he works as a guard in Giran Castle Town.<br>",
             "I will give you some information that will be useful to someone who travels a lot like yourself.  According to the law of the land, the traders of this kingdom are not allowed to deal with criminals.  But there are some traders who ignore this rule and sell their items to outlaws.  These people include, Grocer Pano of Floran Village and Twyla who has set up her trade in the western section of the Dark Forest.  Although they are business people, I think its shameless of them to deal with criminals just to make money.<br>",
             "Would you like me to introduce you to a job opportunity?  If you go to the northwestern area of Gludio, there is a farmer whose name is Peter.  He is currently hiring mercenaries to chase out turek orcs that have settled down near his farm  With your ability, you could deal with turek orcs with no problem, right?  <br>By the way, did you know that relics of ancient kingdoms are often found in that area?  While dealing with turek orcs, I heard that some people have discovered precious ancient relics by accident.<br>",
             "Have you heard of the Aden Business Guild?  It is an association of human traders.  Since they saw that dwarven traders and warehouse keepers were generating a lot of profit while engaging in their organizational activities, humans decided to imitate them by forming a guild of their own.  However, it seems to me that the business savvy of a dwarf is inborn  No matter how hard humans try, I dont think they can keep up with dwarves.<br>On top of that, adding insult to injury, evil spirits frequently attack the guilds cargo wagons and steal their valuable goods making the humans suffer great losses.<br>",
             "Some time ago, while I was talking with the members of the Dark Elf Guild, I found out that Dark Elves worship the Goddess Shilen!!   Are you saying that everybody already knows about that?   Well, I heard about it for the first time <br>Why would you want to worship a goddess of death  It doesnt make any sense to me.  But according to those who have been to the Dark Forest, the Temple of Shilen built by the Dark Elves is truly magnificent.  They told me that the Abyssal Celebrants at the temple are gathering fragments of the statue of Goddess Shilen from everywhere!<br>",
             "Have you ever heard of a hatchling?  It is a cute baby dragon that has been hatched from the egg of a wyrm or a drake.  Among pet handlers, I heard that there is someone who knows how to raise a hatchling as ones own pet  I think his name is Cooper or something like that.  If you are interested in keeping a hatchling as a pet, why dont you go see him!<br>",
             "I will give you the information that a clan leader, who wants to grow the power of his clan would be interested in obtaining.  In each area, there are aristocrats who give support to small clans.  These aristocrats would include Sir Kristof Rodemai in Giran Castle Town and Sir Gustaf Athebaldt of Oren Castle Town.  Of course they are not just philanthropists.  I would say they would want something in return for their support of a clan, wouldnt you agree?<br>",
             "Some time ago, I heard a rumor that there is a society of ancient coin collectors.  At first, I thought that they must be collectors of some anniversary coins that are not very valuable.  But later, I found out that the value of the coins they collect is extremely high.  And members of this society are very enthusiastic about their collection activities that whoever brings some rare coins to them, they will trade them for some high priced items!  I heard that in order to join the coin collection society, you have to go see a dwarf called Sorint at the Hunters Village.<br>",
             "I heard that if you go near the Ivory Tower in Oren, you will find a fake alchemist.  It is said that he goes around telling people that he can make some magic potion that will make peoples wishes come true  Many people have fallen victim to his fake potion.  But what is surprising is that once in a blue moon, after using the potion peoples wishes actually did come true.  The problem is that this only happens very very rarely<br>",
             "Mercenary Red Foot<br>",
             "Have you heard about the rumor?  Antharas, the earthdragon who had been sleeping in Dragon Valley has awoken.  This is terrible news  If it comes out of the Lair of Antharas and runs amok, all of Giran region will fall into a state of pandemonium in no time <br>However, there is someone who is recruiting people to form a militia force to catch Antharas.  It is a woman named Gabrielle in Giran Castle Town.  However, does she really think that they will have any chance against the dragon  Unfortunately, I think it is way beyond their power!<br>",
             ]
f_no_news      = html+redfoot+"Sorry.  I dont have any new information for you.  Why dont you stop by again later.<br>"+back+htmlend
f_more_help      = html+redfoot+"Is there anything else I can help you with?<br><a action=\"bypass -h Quest 333_BlackLionHunt f_give\">Ask him to open the box for you.</a>"+htmlend
f_no_more_box    = html+redfoot+"I dont know what to say to you!  You dont even have a Cargo Box with you.  But are you still asking me to open one?<br><br><a action=\"bypass -h Quest 333_BlackLionHunt f_info\">Ask whether he has any useful information.</a>"+htmlend
f_more_help2    = html+redfoot+"Is there anything else you would like me to help you with?<br><a action=\"bypass -h Quest 333_BlackLionHunt f_give\">Ask him to open the cargo box for you.</a><br><a action=\"bypass -h Quest 333_BlackLionHunt f_info\">Ask him whether he has any useful information for you.</a>"+htmlend
f_not_adena      = html+redfoot+"Listen here brother!  This is not enough money!!!  You didnt think by any chance that I would give out the information to you for free, did you?  This is business for me too, understand!<br>"+back+htmlend
#Rupio
rupio        = "Blacksmith Rupio:<br>"
r_no_items      = html+rupio+"Are you a Black Lion mercenary? What's the reason for visiting our blacksmith shop...? Have you come to have a weapon made?"+htmlend
r_items        = html+rupio+"What can I help with?<br><a action=\"bypass -h Quest 333_BlackLionHunt r_give_statue\">Ask to put together pieces of statue.</a><br><a action=\"bypass -h Quest 333_BlackLionHunt r_give_tablet\">Ask to put together stone tablet."+htmlend
r_statue_pieces    = html+rupio+"How did you know that my hobby was relic restoration? But to restore a stone item to its original condition, you can't be missing even one piece. If you want me to put the statue of the goddess back together, you'll need all the pieces, which are <font color=\"LEVEL\">head, torso, arms and legs</font>), right?"+htmlend
r_statue_brockes  = html+rupio+"OK, shall I demonstrate my talents now? First... Attach the legs to the base... Then the torso above that... And if we match the joints of the arms and head properly... Huh? The statue just crumbled... I knew that it was really old and the material weak but.. When I just applied some pressure to connect... Oh, I'm really sorry."+htmlend
r_statue_complete  = html+rupio+"OK, shall I demonstrate my talents now? First... Attach the legs to the base... Then the torso above that... And if we match the joints of the arms and head properly... OK! It's finished! The joints of the connecting parts are still visible but overall, it looks perfect, don't you think? Hmm... Is it the image of the goddess of Shilen? Looking at it carefully, it's really a beautiful statue."+htmlend
r_tablet_pieces    = html+rupio+"How did you know that my hobby was relic restoration? But with relics like tablets on which words are written, we can't read them if there is even one piece missing and so there's really no point in just putting the other pieces together. In my experience, square-shaped relics like stone tablets often break into <font color=\"LEVEL\">four pieces</font>."+htmlend
r_tablet_brockes  = html+rupio+"OK, shall I demonstrate my talents now? Well, this fragment looks like it goes to the very bottom section of the stone tablet... And this piece is above that... Oh! The tablet just crumbled... I should have expected that the material would be really weak from having been exposed to the rain and wind for such a long time... Darn...! I'm really sorry for making such a big mistake."+htmlend
r_tablet_complete  = html+rupio+"OK, shall I demonstrate my talents now? Well, this fragment looks like it goes to the very bottom section of the stone tablet... And this piece is above that... It's like putting together a puzzle... OK... It's finished! It's an ancient stone tablet... I'm really curious whether some secrets of history are recorded on it! Hmm... But these letters look like writing of the titans... I've seen this somewhere before...! Where in the world could...?!"+htmlend
#Lockirin
lockirin      = "First Elder Lockirin:<br>"
l_no_tablet      = html+lockirin+"I'm extremely interested in the civilization of the titans. In particular, I'd pay any amount to hold in my hands a clay tablet on which the titan writing is written.  If I was some high-class person such as yourself, I might have seen such a thing as that. They say that ancient clay tablets are often found in the Dion region..."+htmlend
l_just_pieces    = html+lockirin+"This clay tablet... Where in the world could...?! It's just one part, but... Maphr...!<br> Look at this, young fellow! Where in the world did you find this? If you can gather the other pieces too and assemble them into a single item, I'll give you something great in gratitude! I promise in the name of the first elder of the guild federation!"+htmlend
l_tablet      = html+lockirin+"This clay tablet... Where in the world could...?! It's just one part, but... Maphr...!  Where did such a precious thing...? Look at this, young fellow! I'll present to you a big gift of gratitude so please give this tablet to me!<br><a action=\"bypass -h Quest 333_BlackLionHunt l_give\">Hand over clay tablet.</a><br><a action=\"bypass -h Quest 333_BlackLionHunt l_info\">Don't hand over."+htmlend
l_give        = html+lockirin+"I'm really thankful! Finally the deep-rooted work of our guild federation...! Here, take this gift of gratitude! And if you find more of these clay tablets in the future, please bring those to me also! I'll express my thanks adequately!"+htmlend
l_info        = html+lockirin+"Huh...?! I said I'd express my thanks abundantly but you still refuse... Look here, young fellow. Do you really think you can sell that tablet somewhere else at a higher price? I guarantee that no matter how hard you look, you won't find anyone that will give you as much as I will. If your opinion changes, please come to me again. Turn over that tablet to me anytime and I'll give you a big gift of gratitude as I promised!"+htmlend
#Undiras
undiras        = "Abyssal Celebrant Undrias:<br>"
u_no_statue      = html+undiras+"Throughout the continent this temple is the only place where the goddess Shilen is enshrined.  Due to the humans distorted religious reformation, our goddess that we worship has been misunderstood to be a sinister being that brings death and destruction.  But we dark elves still worship Shilen as our creator and as a goddess who is in charge of life and death.<br>Unfortunately, when this place was invaded by the allied forces of humans and elves, many sacred objects which were decorating the temple were lost.  Especially, many statues of the goddess Shilen which were exquisitely crafted were stolen.  We the Abyssal Celebrants are willing to give a great reward to those who can recover the statues for us."+htmlend
u_just_pieces    = html+undiras+"Oh this piece must be?  Although its only part of it still this piece came from one of the statues of Shilen that were lost!  Where in the world did you find it?  Can you find the rest of the parts and bring us a completely restored statue?  If you do, we will pay you a large sum of reward money!"+htmlend
u_statue      = html+undiras+"Oh this piece must be?  Although its only part of it still this piece came from one of the statues of Shilen that were lost!  Where in the world did you find it?  Where did you find this sacred object?  This statue is a sacred object for us dark elves.  I will pay you a great sum of money if you will hand it over to me.  After all, it is not of much use to you any way,  right?<br><a action=\"bypass -h Quest 333_BlackLionHunt u_give\">Give him the statue of Shilen.</a><br><a action=\"bypass -h Quest 333_BlackLionHunt u_info\">Refuse to give him the statue of Shilen.</a>"+htmlend
u_give        = html+undiras+"Due to the humans distorted religious reformation, our goddess that we worship has been misunderstood to be a sinister being that brings death and destruction.  But we dark elves still worship Shilen as our creator and as a goddess who is in charge of life and death.  Unfortunately, when this place was invaded by the allied forces of humans and elves, many sacred objects which were decorating the temple were lost.  Especially, many statues of the goddess Shilen which were exquisitely crafted were stolen.  The statue you brought here is one of the statues that were lost at that time.  Thank you so much.  Here is the reward money I promised you.  If you find any more statues like this, please bring them to me.  Well,  then may the divine protection of abyss be with you!"+htmlend
u_info        = html+undiras+"That statue is a sacred object for us dark elves.  Anyway, if you keep it for yourself, you wont have much use for it.  Furthermore, if you carry a statue of Shilen with you and walk around among humans, people will accuse you of being a pagan.  You would be lucky if you are not burnt to death at the stake.  Anyway, if you change your mind, please come back and see me.  If you hand over the statue to me, I am willing to pay you a generous sum of reward money.  Well,  then may the divine protection of abyss be with you."+htmlend
#Morgan
morgan        = "Guilde Member Morgan:<br>"
m_no_box      = html+morgan+"You're a member of the Black Lion Mercenaries, aren't you? I heard that you are working hard to get rid of the evil creatures in this area recently. Please keep up the good work in the future too!"+htmlend
m_box        = html+morgan+"You're a member of the Black Lion Mercenaries, aren't you? I heard that you are working hard to get rid of the evil creatures in this area recently. Please keep up the good work in the future too! But do you have some business with me...?<br><a action=\"bypass -h Quest 333_BlackLionHunt m_give\">Give freight box.</a>"+htmlend
m_rnd_1        = html+morgan+"It's a freight box of our commercial guild!? It is freight that was stolen from our carts having been attacked by evil creatures recently. But there is a lot of freight that was looted and so our losses are really big. Still, it is really fortunate that you could recover this part.<br>As a representative of the commercial guild, I thank you for your efforts. Here, take this gift of gratitude, even though it's not much. And I present you with these (<font color=\"LEVEL\">coins from our guild</font>). It's like a plaque of appreciation that we give to people that have contributed to the commercial guild.<br><a action=\"bypass -h Quest 333_BlackLionHunt m_reward\">Go back.</a>"+htmlend
m_rnd_2        = html+morgan+"You've gotten another freight box. I'm thankful again. The freight that is being looted by the evil creatures is increasing by the day but without the help of mercenaries like yourself, the losses of our commercial guild would be so much more. Here, take this gift of gratitude!. And, as always, I present you with these <font color=\"LEVEL\">coins from our guild</font>). Please keep up the good work in the future too!<br><a action=\"bypass -h Quest 333_BlackLionHunt m_reward\">Go back.</a>"+htmlend
m_rnd_3        = html+morgan+"I really thank you for recovering so many freight boxes like this for us. If the financial situation of our commercial guild were a bit better, we would hire competent mercenaries such as yourself as bodyguards... In that case, the evil creatures would never be able to loot our stuff, no?<br>OK! Here, take the gift of gratitude! The amount of the gratitude money increased greatly after I spoke to my superiors about the hard work you have been doing for our guild. As this is appropriate acknowledgment for your hard work, please take it without refusing. And, as always, I present you with these <font color=\"LEVEL\">coins from our guild.</font><br><a action=\"bypass -h Quest 333_BlackLionHunt m_reward\">Go back.</a>"+htmlend
m_no_more_box       = html+morgan+"Freight box...? What box are you talking about? It doesn't look to me as if you have one of those..."+htmlend
m_reward      = html+morgan+"Is there anything I can do for you...?<br><a action=\"bypass -h Quest 333_BlackLionHunt m_give\">Give freight box.</a>"+htmlend

import sys 
from com.it.br.gameserver.model.quest import State
from com.it.br.gameserver.model.quest import QuestState
from com.it.br.gameserver.model.quest.jython import QuestJython as JQuest
#This Put all Mob Ids from dictionari in a list. So its possible to add new mobs, to one of this 4 Areas, without modification on the addKill Part.
MOBS=DROPLIST.keys()

def giveRewards(st,item,count):
  st.giveItems(ADENA_ID,35*count)
  st.takeItems(item,count)
  if count < 20:
    return
  if count<50:
    st.giveItems(LIONS_CLAW,1)
  elif count<100:
    st.giveItems(LIONS_CLAW,2)
  else:
    st.giveItems(LIONS_CLAW,3)
  return


class Quest (JQuest) :

  def __init__(self,id,name,descr): JQuest.__init__(self,id,name,descr)

  def onEvent (self,event,st) :
    part = st.getInt("part")
    if event == "start" :
      st.set("cond","1")
      st.setState(STARTED)
      st.playSound("ItemSound.quest_accept")
      #just to go with the official, until we have the option to make the take part invisible, like on officials.
      st.takeItems(BLACK_LION_MARK,1)
      st.giveItems(BLACK_LION_MARK,1)
      return start_explain
    elif event == "p1_t":
      st.set("part","1")
      st.giveItems(SOPHIAS_LETTER1,1)
      return p1_take
    elif event == "p2_t":
      st.set("part","2")
      st.giveItems(SOPHIAS_LETTER2,1)
      return p2_take
    elif event == "p3_t":
      st.set("part","3")
      st.giveItems(SOPHIAS_LETTER3,1)
      return p3_take
    elif event == "p4_t":
      st.set("part","4")
      st.giveItems(SOPHIAS_LETTER4,1)
      return p4_take
    elif event == "exit":
      st.takeItems(BLACK_LION_MARK,1)
      st.exitQuest(1)
      return exit
    elif event == "continue":
      claw=int(st.getQuestItemsCount(LIONS_CLAW)/10)
      check_eye=st.getQuestItemsCount(LIONS_EYE)
      if claw :
        st.giveItems(LIONS_EYE,claw)
        eye=st.getQuestItemsCount(LIONS_EYE)
        st.takeItems(LIONS_CLAW,claw*10)
        ala_count=3
        soul_count=100
        soe_count=20
        heal_count=20
        spir_count=50
        if eye > 9:
          ala_count=4
          soul_count=400
          soe_count=30
          heal_count=50
          spir_count=200
        elif eye > 4:
          spir_count=100
          soul_count=200
          heal_count=25
        while claw > 0:
          n = st.getRandom(5)
          if n < 1 :
            st.giveItems(ALACRITY_POTION, int(ala_count*Config.RATE_QUESTS_REWARD))
          elif n < 2 :
            st.giveItems(SOULSHOT_D, int(soul_count*Config.RATE_QUESTS_REWARD))
          elif n < 3:
            st.giveItems(SCROLL_ESCAPE, int(soe_count*Config.RATE_QUESTS_REWARD))
          elif n < 4:
            st.giveItems(SPIRITSHOT_D,int(spir_count*Config.RATE_QUESTS_REWARD))
          elif n == 4:
            st.giveItems(HEALING_POTION,int(heal_count*Config.RATE_QUESTS_REWARD))
          claw-=1
        if check_eye:
          return p_eye
        else:
          return p_first_eye
      else:
        return start_continue
    elif event == "leave":
      if part == 1:
        order = SOPHIAS_LETTER1
      elif part == 2:
        order = SOPHIAS_LETTER2
      elif part == 3:
        order = SOPHIAS_LETTER3
      elif part == 4:
        order = SOPHIAS_LETTER4
      else:
        order = 0
      st.set("part","0")
      if order:
        st.takeItems(order,1)
      return p_leave_mission
    elif event == "f_info":
      text = st.getInt("text")
      if text<4:
        rnd=int(st.getRandom(20))
        st.set("text",str(text+1))
        text_rnd = html+redfoot+f_rnd_list[rnd]+back+htmlend
        return text_rnd
      else:
        return f_no_news
    elif event == "f_give":
      if st.getQuestItemsCount(CARGO_BOX1) :
        if st.getQuestItemsCount(ADENA_ID)>=OPEN_BOX_PRICE:
          st.takeItems(CARGO_BOX1,1)
          st.takeItems(ADENA_ID,650)
          random = st.getRandom(162)
          standart = "All right, lets go ahead and open this box  Opening a padlock like this one is a piece of cake  Here we go!  There that was too easy.  Now, lets see whats in the box.<br>"
          statue = "Whats this?  A fragment of a stone statue?  Hmm it looks like a Statue of Shilen, the goddess  Isnt she the goddess of death?  For some reason, I dont have a good feeling about this.  But, if this was not a fragment but the complete statue, it could have fetched a large amount of money  There is someone who can put together a broken relic like this Hes Blacksmith Rupio.  If you collect all the fragments of the statue and take them to him, he will put them together to restore it and make it complete.<br>" 
          tablet = "A broken tablet fragment?  Hmm  It has some incomprehensible symbols on it.  Is this a relic from ancient times?  If it was not a fragment but a complete tablet, this might have been a very valuable historical object.  If you can find all the fragments of the tablet, you can get them to be restored to its complete form  If you are interested, go see <font color=\"LEVEL\">Rupio</font> the blacksmith. He is an expert of relic restoration.<br>"
          if random < 21 :
            st.giveItems(GLUDIO_APPLE,int(Config.RATE_QUESTS_REWARD))
            return html+redfoot+standart+"<br>Fruit?  Oh, they must be apples grown in Gludio!  Mmm they look delicious.  If you take them to the market before they go bad, I guess you will be able to make some money.<br>"+back+htmlend
          elif random < 41:
            st.giveItems(CORN_MEAL,int(Config.RATE_QUESTS_REWARD))
            return html+redfoot+standart+"Isnt this corn meal?  Isnt this used to feed pigs?  Well, anyhow, since its not anything you can use for yourself, you should take it to the market to sell it.<br>"+back+htmlend
          elif random < 61:
            st.giveItems(WOLF_PELTS,int(Config.RATE_QUESTS_REWARD))
            return html+redfoot+standart+"<br>Whats this pelts?  Dire Wolf Pelts?  They seem to have been tanned by a leather craftman. But they are not of high quality.  Maybe they can be used to make leather hats?  Well, anyhow, if you take them to the market, you will get some money for them.<br>"+back+htmlend
          elif random < 74:
            st.giveItems(MONNSTONE,int(Config.RATE_QUESTS_REWARD))
            return html+redfoot+standart+"<br>A gem?  Oh wow, a Moonstone!  You should be able to sell it at a fairly good price.<br>"+back+htmlend
          elif random < 86:
            st.giveItems(GLUDIO_WEETS_FLOWER,int(Config.RATE_QUESTS_REWARD))
            return html+redfoot+standart+"<br>Hmm?  Whats this powder?  Should I taste it?   Yes, this must be Gludio Wheat Flour!  It can be used for baking bread I guess.   Well, anyhow, you should be able to sell it for a decent price at the market.<br>"+back+htmlend
          elif random < 98:
            st.giveItems(SPIDERSILK_ROPE,int(Config.RATE_QUESTS_REWARD))
            return html+redfoot+standart+"Whats this?  Its Spidersilk Rope!  Its a very strong and light rope that is made from the spidersilk that is collected from the Tarantula Spiders webs in the Spine Mountain Range.  If you take it to a store, Im sure you should be able to get a really good price for it.<br>"+back+htmlend
          elif random < 99:
            st.giveItems(ALEXANDRIT,int(Config.RATE_QUESTS_REWARD))
            return html+redfoot+standart+back+htmlend
          elif random < 109:
            st.giveItems(SILVER_TEA,int(Config.RATE_QUESTS_REWARD))
            return html+redfoot+standart+"Hmm?  A silver bowl?  And a teacup?  They seem to be of pretty high quality!  It seems like they were made by elven artisans.  I am not interested in such exquisite items but, anyway, if you take them to a store, you should be able to sell them for a fairly good price.<br>"+back+htmlend
          elif random < 119:
            st.giveItems(GOLEM_PART,int(Config.RATE_QUESTS_REWARD))
            return html+redfoot+standart+"Hmm?  Machine parts?  This Guild Mark seems to be that of the Black Anvil Guild what do you think?  Although I dont know for sure, these seem to be parts that are used by dwarves to do repair work on golems.  If you take them to a store, I think you will be able to sell them at a pretty reasonable price.<br>"+back+htmlend
          elif random < 123:
            st.giveItems(FIRE_EMERALD,int(Config.RATE_QUESTS_REWARD))
            return html+redfoot+standart+"Whats this gem?  Ah!  Its a Fire Emerald!  Dont you know about it?  its a rare and precious gemstone that gives out a strong red light when its exposed to sun light.  You are so lucky!  You can take it to a store and sell it at a very high price.<br>"+back+htmlend
          elif random < 127:
            st.giveItems(SILK_FROCK,int(Config.RATE_QUESTS_REWARD))
            return html+redfoot+standart+"Isnt this a dress?!  This is a silk dress for a woman.  It looks pretty expensive, dont you think?  Take a look at this design.  This is an item that has been imported from Avella of the East.  At a time like this, who would use such a luxurious item?  This must be ordered by a noblewoman who has a liking for foreign products dont you think?  You should take this to a store and sell it off!  Im sure you will get a very high price for it.<br>"+back+htmlend
          elif random < 131:
            st.giveItems(PORCELAN_URN,int(Config.RATE_QUESTS_REWARD))
            return html+redfoot+standart+back+htmlend
          elif random < 132:
            st.giveItems(IMPERIAL_DIAMOND,int(Config.RATE_QUESTS_REWARD))
            return html+redfoot+standart+"Hmm?  Dont tell me!  I dont believe it!!!  Wow, an Imperial Diamond?  Isnt this the one that was used to decorate the crown of the king of Elmo-Aden?  Its truly beautiful!   You are extremely lucky!  You got yourself a priceless item.  If you take it to the market, Im sure you will be able to get a huge amount of money for it.<br>"+back+htmlend
          elif random < 147:
            random_stat=st.getRandom(4)
            if random_stat == 3 :
              st.giveItems(STATUE_SHILIEN_HEAD,1)
              return html+redfoot+standart+statue+back+htmlend
            elif random_stat == 0 :
              st.giveItems(STATUE_SHILIEN_TORSO,1)
              return html+redfoot+standart+statue+back+htmlend
            elif random_stat == 1 :
              st.giveItems(STATUE_SHILIEN_ARM,1)
              return html+redfoot+standart+statue+back+htmlend
            elif random_stat == 2 :
              st.giveItems(STATUE_SHILIEN_LEG,1)
              return html+redfoot+standart+statue+back+htmlend
          elif random < 162:
            random_tab=st.getRandom(4)
            if random_tab == 0 :
              st.giveItems(FRAGMENT_ANCIENT_TABLE1,1)
              return html+redfoot+standart+tablet+back+htmlend
            elif random_tab == 1:
              st.giveItems(FRAGMENT_ANCIENT_TABLE2,1)
              return html+redfoot+standart+tablet+back+htmlend
            elif random_tab == 2 :
              st.giveItems(FRAGMENT_ANCIENT_TABLE3,1)
              return html+redfoot+standart+tablet+back+htmlend
            elif random_tab == 3 :
              st.giveItems(FRAGMENT_ANCIENT_TABLE4,1)
              return html+redfoot+standart+tablet+back+htmlend
        else:
          return f_not_adena
      else:
        return f_no_more_box
    elif event in  ["r_give_statue","r_give_tablet"]:
      if event == "r_give_statue":
        items = statue_list
        item = COMPLETE_STATUE
        pieces = r_statue_pieces
        brockes = r_statue_brockes
        complete = r_statue_complete
      elif event == "r_give_tablet":
        items = tablet_list
        item = COMPLETE_TABLET
        pieces = r_tablet_pieces
        brockes = r_tablet_brockes
        complete = r_tablet_complete
      count=0
      for id in items:
        if st.getQuestItemsCount(id):
          count+=1
      if count>3:
        for id in items:
          st.takeItems(id,1)
        if st.getRandom(2)==1 :
          st.giveItems(item,1)
          return complete
        else:
          return brockes 
      elif count<4 and count!=0:
        return pieces
      else:
        return r_no_items
    elif event == "l_give" :
      if st.getQuestItemsCount(COMPLETE_TABLET):
        st.takeItems(COMPLETE_TABLET,1)
        st.giveItems(ADENA_ID,30000)
        return l_give
      else:
        return no_tablet
    elif event == "u_give" :
      if st.getQuestItemsCount(COMPLETE_STATUE) :
        st.takeItems(COMPLETE_STATUE,1)
        st.giveItems(ADENA_ID,30000)
        return u_give
      else:
        return no_statue
    elif event == "m_give":
      if st.getQuestItemsCount(CARGO_BOX1):
        coins = st.getQuestItemsCount(GUILD_COIN)
        count = int(coins/40)
        if count > 2 : count = 2
        st.giveItems(GUILD_COIN,1)
        st.giveItems(ADENA_ID,(1+count)*100)
        st.takeItems(CARGO_BOX1,1)
        random = st.getRandom(3)
        if random == 0:
          return m_rnd_1
        elif random == 1:
          return m_rnd_2
        else:
          return m_rnd_3
      else:
        return m_no_box
    elif event == "start_parts":
      return start_parts
    elif event == "m_reward":
      return m_reward
    elif event == "u_info":
      return u_info
    elif event == "l_info":
      return l_info
    elif event == "p_redfoot":
      return p_redfoot
    elif event == "p_trader_info":
      return p_trader_info
    elif event == "start_chose_parts":
      return start_parts
    elif event == "p1_explanation":
      return p1_explanation
    elif event == "p2_explanation":
      return p2_explanation
    elif event == "p3_explanation":
      return p3_explanation
    elif event == "p4_explanation":
      return p4_explanation
    elif event == "f_more_help":
      return f_more_help
    elif event == "r_exit":
      return r_exit
    
  def onTalk (self,npc,player):
    htmltext = "<html><body>You are either not carrying out your quest or don't meet the criteria.</body></html>"
    st = player.getQuestState(qn)
    if not st : return htmltext

    npcId = npc.getNpcId()
    id = st.getState()
    if npcId != NPC[0] and id != STARTED : return htmltext

    if id == CREATED :
      st.setState(STARTING)
      st.set("cond","0")
      st.set("part","0")
      st.set("text","0")
      if npcId == NPC[0]:
        if st.getQuestItemsCount(BLACK_LION_MARK) :
          if player.getLevel() >24 :
            return  start_start
          else:
            st.exitQuest(1)
            return start_error1
        else:
          st.exitQuest(1)
          return start_error2
    else: 
      part=st.getInt("part")
      if npcId==NPC[0]:
          if part == 1:
            item = UNDEAD_ASH
          elif part == 2:
            item = BLOODY_AXE_INSIGNIAS
          elif part == 3:
            item = DELU_FANG
          elif part == 4:
            item = STAKATO_TALONS
          else:
            return start_ask_again
          count=st.getQuestItemsCount(item)
          box=st.getQuestItemsCount(CARGO_BOX1)
          if box and count:
            giveRewards(st,item,count)
            return p_both_info
          elif box:
            return p_only_box_info
          elif count:
            giveRewards(st,item,count)
            return p_only_item_info
          else:
            return p_no_items
      elif npcId==NPC[1]:
          if st.getQuestItemsCount(CARGO_BOX1):
            return f_give_box
          else:
            return f_no_box
      elif npcId==NPC[2]:
          count=0
          for items in statue_list:
            if st.getQuestItemsCount(items):
              count+=1
          for items in tablet_list:
            if st.getQuestItemsCount(items):
              count+=1
          if count:
            return r_items
          else:
            return r_no_items
      elif npcId==NPC[3]:
        if st.getQuestItemsCount(COMPLETE_STATUE):
          return u_statue
        else:
          count=0
          for items in statue_list:
            if st.getQuestItemsCount(items):
              count+=1
          if count:
            return u_just_pieces
          else:
            return u_no_statue
      elif npcId==NPC[4]:
        if st.getQuestItemsCount(COMPLETE_TABLET):
          return l_tablet
        else:
          count=0
          for items in tablet_list:
            if st.getQuestItemsCount(items):
              count+=1
          if count:
            return l_just_pieces
          else:
            return l_no_tablet
      elif npcId==NPC[5]:
        if st.getQuestItemsCount(CARGO_BOX1):
          return m_box
        else:
          return m_no_box
          
  def onKill(self,npc,player,isPet):
    st = player.getQuestState(qn)
    if not st : return 
    if st.getState() != STARTED : return 

    npcId = npc.getNpcId()
    part,allowDrop,chancePartItem,chanceBox,partItem=DROPLIST[npcId]
    random1 = st.getRandom(101)
    random2 = st.getRandom(101)
    mobLevel = npc.getLevel()
    playerLevel = player.getLevel()
    if playerLevel - mobLevel > 8:
      chancePartItem/=3
      chanceBox/=3
    if allowDrop and st.getInt("part")==part :
      if random1<chancePartItem :
        st.giveItems(partItem,1)
        st.playSound("ItemSound.quest_itemget")
      if random2<chanceBox :
        st.giveItems(CARGO_BOX1,1)
        if not random1<chancePartItem:
          st.playSound("ItemSound.quest_itemget") 
    return


QUEST       = Quest(333,qn,"BlackLionHunt")
CREATED     = State('Start', QUEST)
STARTING    = State('Starting', QUEST)
STARTED     = State('Started', QUEST)
COMPLETED   = State('Completed', QUEST)

QUEST.setInitialState(CREATED)
QUEST.addStartNpc(NPC[0])

for npcId in NPC:
  QUEST.addTalkId(npcId)

for mobId in MOBS:
  QUEST.addKillId(mobId)