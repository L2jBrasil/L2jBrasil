# Supplier of Reagents version 0.2
# by DrLecter for the Official L2J Datapack Project.
# Visit http://forum.l2jdp.com for more details.

import sys
from com.it.br import Config
from com.it.br.gameserver.model.quest import State
from com.it.br.gameserver.model.quest import QuestState
from com.it.br.gameserver.model.quest.jython import QuestJython as JQuest
#Quest info
QUEST_NUMBER,QUEST_NAME,QUEST_DESCRIPTION = 373,"SupplierOfReagents","Supplier of Reagents"
qn = "373_SupplierOfReagents"

#Variables
#itemId:[icon,name,description]
ITEMS={
6011:["etc_reagent_red_i00","Wyrm's Blood",""],
6012:["etc_inf_ore_high_i00","Lava Stone",""],
6013:["etc_broken_crystal_silver_i00","Moonstone Shard",""],
6014:["etc_piece_bone_black_i00","Rotten Bone Piece",""],
6015:["etc_reagent_green_i00","Demon's Blood",""],
6016:["etc_inf_ore_least_i00","Infernium Ore","Low Level Reagent"],
6017:["etc_ginseng_red_i00","Blood Root",""],
6018:["etc_powder_gray_i00","Volcanic Ash",""],
6019:["etc_reagent_silver_i00","Quicksilver",""],
6020:["etc_powder_orange_i00","Sulfur",""],
6021:["etc_dragons_blood_i05","Dracoplasm","Low Level Reagent"],
6022:["etc_powder_red_i00","Magma Dust",""],
6023:["etc_powder_white_i00","Moon Dust","Low Level Reagent"],
6024:["etc_potion_purpel_i00","Necroplasm","Low Level Reagent"],
6025:["etc_potion_green_i00","Demonplasm","Low Level Reagent"],
6026:["etc_powder_black_i00","Inferno Dust",""], 
6027:["etc_dragon_blood_i00","Draconic Essence","High Level Reagent"],
6028:["etc_dragons_blood_i00","Fire Essence","High Level Reagent"],
6029:["etc_mithril_ore_i00","Lunargent","High Level Reagent"],
6030:["etc_dragons_blood_i02","Midnight Oil","High Level Reagent"],
6031:["etc_dragons_blood_i05","Demonic Essence","High Level Reagent"],
6032:["etc_dragons_blood_i04","Abyss Oil","High Level Reagent"],
6033:["etc_luxury_wine_b_i00","Hellfire Oil","Highest Level Reagent"],
6034:["etc_luxury_wine_c_i00","Nightmare Oil","Highest Level Reagent"],
6320:["etc_broken_crystal_silver_i00","Pure Silver",""],
6321:["etc_broken_crystal_gold_i00","True Gold",""],
}
#Quest items
REAGENT_POUCH1,   REAGENT_POUCH2,REAGENT_POUCH3, REAGENT_BOX, \
WYRMS_BLOOD,      LAVA_STONE,    MOONSTONE_SHARD,ROTTEN_BONE, \
DEMONS_BLOOD,     INFERNIUM_ORE, BLOOD_ROOT,     VOLCANIC_ASH,\
QUICKSILVER,      SULFUR,        DRACOPLASM,     MAGMA_DUST,  \
MOON_DUST,        NECROPLASM,    DEMONPLASM,     INFERNO_DUST,\
DRACONIC_ESSENCE, FIRE_ESSENCE,  LUNARGENT,      MIDNIGHT_OIL,\
DEMONIC_ESSENCE,  ABYSS_OIL,     HELLFIRE_OIL,   NIGHTMARE_OIL=range(6007,6035)
MIXING_STONE1 = 5904
#Mimir's Elixir items
BLOOD_FIRE, MIMIRS_ELIXIR, PURE_SILVER, TRUE_GOLD = range(6318,6322)

MATS=range(6011,6032)+range(6320,6322)
#Messages
default   = "<html><body>You are either not carrying out your quest or don't meet the criteria.</body></html>"
#NPCs
WESLEY,URN=30166,31149
#Mobs & Drop
#index = mobId, array = [ TotalChance, (item0, chance0),(item1, chance1),...]
DROPLIST = {
20813: [100, (QUICKSILVER,60),(ROTTEN_BONE,40)],
20822: [100, (VOLCANIC_ASH,40),(REAGENT_POUCH1,60)],
21061: [90, (DEMONS_BLOOD,70),(MOONSTONE_SHARD,20)],
20828: [100, (REAGENT_POUCH2,70),(QUICKSILVER,30)],
21066: [40, (REAGENT_BOX,40)],
21111: [50, (WYRMS_BLOOD,50)],
21115: [50, (REAGENT_POUCH3,50)]
}
#temperature:[success_%,reagent_qty_obtained]
TEMPERATURE={1:[100,1],2:[45,2],3:[15,3]}
#reagent:[ingredient,ingredient_qty,catalyst,catalyst_qty]
FORMULAS = {
DRACOPLASM:      [WYRMS_BLOOD,10,BLOOD_ROOT,1],     MAGMA_DUST:     [LAVA_STONE,10,VOLCANIC_ASH,1],MOON_DUST:[MOONSTONE_SHARD,10,VOLCANIC_ASH,1],
NECROPLASM:      [ROTTEN_BONE,10,BLOOD_ROOT,1],     DEMONPLASM:     [DEMONS_BLOOD,10,BLOOD_ROOT,1],INFERNO_DUST:[INFERNIUM_ORE,10,VOLCANIC_ASH,1],
DRACONIC_ESSENCE:[DRACOPLASM,10,QUICKSILVER,1],     FIRE_ESSENCE:   [MAGMA_DUST,10,SULFUR,1],      LUNARGENT:[MOON_DUST,10,QUICKSILVER,1],
MIDNIGHT_OIL:    [NECROPLASM,10,QUICKSILVER,1],     DEMONIC_ESSENCE:[DEMONPLASM,10,SULFUR,1],      ABYSS_OIL:[INFERNO_DUST,10,SULFUR,1],
HELLFIRE_OIL:    [FIRE_ESSENCE,1,DEMONIC_ESSENCE,1],NIGHTMARE_OIL:  [LUNARGENT,1,MIDNIGHT_OIL,1],  PURE_SILVER:[LUNARGENT,1,QUICKSILVER,1],
MIMIRS_ELIXIR:   [PURE_SILVER,1,TRUE_GOLD,1],
}

def render_urn(st, page) :
    stone,ingredient,catalyst = st.getInt("mixing"),st.getInt("ingredient"),st.getInt("catalyst")
    if page == "Start" :
       html = "<html><body>Alchemists Mixing Urn:<br><table border=0 width=300><tr><tr><td width=50%><a action=\"bypass -h Quest 373_SupplierOfReagents U_M_MACT\">MACT Mixing Stone</a></td><td></td></tr><tr><td><a action=\"bypass -h Quest 373_SupplierOfReagents U_I_IACT\">IACT Ingredients</a></td><td>(current: INGR)</td></tr><tr><td><a action=\"bypass -h Quest 373_SupplierOfReagents U_C_CACT\">CACT Catalyst</a></td><td>(current: CATA)</td></tr><tr><td><a action=\"bypass -h Quest 373_SupplierOfReagents 31149-5.htm\">Select Temperature</a></td><td>(current: TEMP)</td></tr><tr><td><a action=\"bypass -h Quest 373_SupplierOfReagents 31149-6.htm\">Mix Ingredients</a></td><td></td></tr></table></body></html>"
       ingr,cata,temp=st.getInt("ingredient"),st.getInt("catalyst"),st.get("temp")
       if ingr : ingr = ITEMS[ingr][1]+"x"+st.get("i_qty")
       else : ingr = "None"
       if cata : cata = ITEMS[cata][1]+"x"+st.get("c_qty")
       else : cata = "None"
       html = html.replace("INGR",ingr).replace("CATA",cata).replace("TEMP",temp)
       if stone : html = html.replace("MACT","Retrieve")
       else : html = html.replace("MACT","Insert")
       if ingredient : html = html.replace("IACT","Retrieve")
       else : html = html.replace("IACT","Insert")
       if catalyst : html = html.replace("CACT","Retrieve")
       else : html = html.replace("CACT","Insert")
    elif isinstance(page,list) :
       html = "<html><body>Insert:<table border=0>"
       amt = 0
       for i in MATS :
         if st.getQuestItemsCount(i):
           amt += 1
           html += "<tr><td height=45><img src=icon."+ITEMS[i][0]+" height=32 width=32></td><td width=180>"+ITEMS[i][1]+"</td><td><button value=X1 action=\"bypass -h Quest 373_SupplierOfReagents x_1_"+page[1]+"_"+str(i)+"\" width=40 height=15 fore=sek.cbui92><button value=X10 action=\"bypass -h Quest 373_SupplierOfReagents x_2_"+page[1]+"_"+str(i)+"\" width=40 height=15 fore=sek.cbui92></td></tr>"
       if not amt : html += "<tr><td>You don't have any material that could be used with this Urn. Read the Mixing Manual.</td></tr>"
       html += "</table><center><a action=\"bypass -h Quest 373_SupplierOfReagents urn\">Back</a></center></body></html>"
    return html

class Quest (JQuest) :

 def __init__(self,id,name,descr): JQuest.__init__(self,id,name,descr)

 def onEvent (self,event,st) :
    id = st.getState() 
    htmltext = event
    if event == "30166-4.htm" :
       st.setState(STARTED)
       st.set("cond","1")
       st.set("ingredient","0")
       st.set("catalyst","0")
       st.set("i_qty","0")
       st.set("c_qty","0")
       st.set("temp","0")
       st.set("mixing","0")
       st.giveItems(6317,1)
       st.giveItems(5904,1)
       st.playSound("ItemSound.quest_accept")
    elif event == "30166-5.htm" :
       for i in range(6007,6035)+[6317,5904] : 
          st.takeItems(i,-1)
       st.exitQuest(1)
       st.playSound("ItemSound.quest_finish")
    elif event == "urn" :
        htmltext = render_urn(st,"Start")
    elif event.startswith("U_") :
       event = event.split("_")
       if event[1]=="M" :
          if event[2] == "Insert" :
              if st.getQuestItemsCount(MIXING_STONE1) :
                 st.takeItems(MIXING_STONE1,-1)
                 st.set("mixing","1")
                 htmltext = "31149-2.htm"
              else :
                 htmltext = "You don't have a mixing stone."
          elif event[2] == "Retrieve" :
              if st.getInt("mixing") :
                 st.set("mixing","0")
                 st.set("temp","0")
                 st.giveItems(MIXING_STONE1,1)
                 if st.getInt("ingredient") or st.getInt("catalyst") :
                     htmltext = "31149-2c.htm"
                 else :
                     htmltext = "31149-2a.htm"
              else :
                 htmltext = "31149-2b.htm"
       elif event[2] == "Insert" :
          htmltext = render_urn(st,event)
       elif event[2] == "Retrieve" :
          if event[1] == "I" :
             item=st.getInt("ingredient")
             qty =st.getInt("i_qty")
             st.set("ingredient","0")
             st.set("i_qty","0")
          elif event[1] == "C" :
             item=st.getInt("catalyst")
             qty =st.getInt("c_qty")
             st.set("catalyst","0")
             st.set("c_qty","0")
          if item and qty :
             st.giveItems(item,qty)
             htmltext="31149-3a.htm"
          else :
             htmltext = "31149-3b.htm" 
    elif event.startswith("x_") :
       x,qty,dst,item=event.split("_")
       if qty=="2": qty="10"
       if st.getQuestItemsCount(int(item)) >= int(qty) :
          if dst == "I" :
             dest = "ingredient"
             count= "i_qty"
          else :
             dest = "catalyst"
             count= "c_qty"
          st.takeItems(int(item),int(qty))
          st.set(dest,item)
          st.set(count,qty)
          st.playSound("SkillSound5.liquid_mix_01")
          htmltext = "31149-4a.htm"
       else :
          htmltext = "31149-4b.htm"
    elif event.startswith("tmp_") :
       st.set("temp",event.split("_")[1])
       htmltext = "31149-5a.htm"
    elif event == "31149-6.htm" :
       if st.getInt("mixing") :
          temp=st.getInt("temp")
          if temp :
             ingredient,catalyst,iq,cq = st.getInt("ingredient"),st.getInt("catalyst"),st.getInt("i_qty"),st.getInt("c_qty")
             st.set("ingredient","0")
             st.set("i_qty","0")
             st.set("catalyst","0")
             st.set("c_qty","0")
             st.set("temp","0")
             item=0
             for i in FORMULAS :
                 if [ingredient,iq,catalyst,cq] == FORMULAS[i] :
                    item=i
                    break
             if item == PURE_SILVER and temp != 1:
                st.playSound("SkillSound5.liquid_fail_01")
                return "31149-7c.htm"
             if item == MIMIRS_ELIXIR :
                if temp == 3 :
                  if st.getQuestItemsCount(BLOOD_FIRE) :
                     st.takeItems(BLOOD_FIRE,1)
                  else :
                     st.playSound("SkillSound5.liquid_fail_01")
                     return "31149-7a.htm"
                else :
                  st.playSound("SkillSound5.liquid_fail_01")
                  return "31149-7b.htm"
             if item :
                chance,qty=TEMPERATURE[temp]
                if item == MIMIRS_ELIXIR :
                   mimirs=st.getPlayer().getQuestState("235_MimirsElixir")
                   if mimirs :
                      chance = 100
                      qty = 1
                      mimirs.set("cond","8")
                   else :
                      st.playSound("SkillSound5.liquid_fail_01")
                      return "31149-7d.htm"
                if st.getRandom(100) < chance :
                   st.giveItems(item,qty)
                   st.playSound("SkillSound5.liquid_success_01")
                else :
                   st.playSound("SkillSound5.liquid_fail_01")
                   htmltext = "31149-6c.htm"
             else :
                st.playSound("SkillSound5.liquid_fail_01")
                htmltext = "31149-6d.htm"
          else :
             st.playSound("SkillSound5.liquid_fail_01")
             htmltext = "31149-6b.htm"
       else :
          st.playSound("SkillSound5.liquid_fail_01")
          htmltext="31149-6a.htm"
    return htmltext

 def onTalk (self,npc,player):
   htmltext = default
   st = player.getQuestState(qn)
   if not st : return htmltext

   npcId = npc.getNpcId()
   id = st.getState()
   if npcId == WESLEY :
      if id == CREATED :
         st.set("cond","0")
         htmltext = "30166-1.htm"
         if player.getLevel() < 57 :
            st.exitQuest(1)
            htmltext = "30166-2.htm"
      else :
         htmltext = "30166-3.htm"
   elif id == STARTED :
      htmltext = render_urn(st,"Start")
   return htmltext

 def onKill(self,npc,player,isPet) :
     partyMember = self.getRandomPartyMemberState(player, STARTED)
     if not partyMember : return
     st = partyMember.getQuestState(qn)
     npcId = npc.getNpcId()
     # The quest rates increase the rates of dropping "something", but only one
     # entry will be chosen to drop per kill.  In order to not overshadow entries
     # that appear later in the list, first check with the sum of all entries to
     # see if any one of them will drop, then select which one...
     totalDropChance = DROPLIST[npcId][0]
     if totalDropChance*Config.RATE_DROP_QUEST > st.getRandom(100) :
         # At this point, we decided that one entry from this list will definitely be dropped
         # to select which one, get a random value in the range of the total chance and find
         # the first item that passes this range.
         itemToDrop =st.getRandom(totalDropChance)
         indexChance = 0
         for i in range(1,len(DROPLIST[npcId])) :
             item, chance = DROPLIST[npcId][i]
             indexChance += chance
             if indexChance > itemToDrop :
                 # Now, we have selected which item to drop.  However, the quest rates are also
                 # capable of giving this item a bonus amount, if its individual chance surpases
                 # 100% after rates.  Apply rates to see for bonus amounts...
                 # definitely give at least 1 item.  If the chance exceeds 100%, then give some
                 # additional bonus...
                 numItems,chance = divmod(chance*Config.RATE_DROP_QUEST,100)
                 if numItems == 0 or chance > st.getRandom(100) :
                     numItems += 1
                 st.giveItems(item,int(numItems))
                 st.playSound("ItemSound.quest_itemget")
     return

# Quest class and state definition
QUEST       = Quest(QUEST_NUMBER, str(QUEST_NUMBER)+"_"+QUEST_NAME, QUEST_DESCRIPTION)

CREATED     = State('Start',     QUEST)
STARTED     = State('Started',   QUEST)
COMPLETED   = State('Completed', QUEST)

QUEST.setInitialState(CREATED)
# Quest NPC starter initialization
QUEST.addStartNpc(WESLEY)
# Quest initialization
QUEST.addTalkId(WESLEY)

QUEST.addTalkId(URN)

for i in DROPLIST.keys():
  QUEST.addKillId(i)