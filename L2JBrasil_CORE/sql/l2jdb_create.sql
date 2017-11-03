------------------------------
-- Table structure for accounts
------------------------------
CREATE TABLE accounts (
  login varchar(45) ,
  password varchar(45) ,
  lastactive decimal(20) ,
  access_level decimal(11) ,
  lastIP varchar(16),
  PRIMARY KEY  (login)
);

------------------------------
-- Table structure for armor
------------------------------
CREATE TABLE armor (
  item_id int(11) ,
  name varchar(70) ,
  bodypart varchar(15) ,
  crystallizable varchar(5) ,
  armor_type varchar(5) ,
  weight int(5) ,
  material varchar(15) ,
  crystal_type varchar(4) ,
  avoid_modify int(1) ,
  durability int(3) ,
  p_def int(3) ,
  m_def int(2) ,
  mp_bonus int(3) ,
  price int(11) ,
  crystal_count int(4) ,
  sellable varchar(5) ,
  PRIMARY KEY  (item_id)
);

------------------------------
-- Table structure for character_hennas
------------------------------
CREATE TABLE character_hennas (
  char_obj_id decimal(11) ,
  symbol_id decimal(11) ,
  slot decimal(1) ,
  PRIMARY KEY  (char_obj_id,slot)
);

------------------------------
-- Table structure for char_templates
------------------------------
CREATE TABLE char_templates (
  ClassId int(11) ,
  ClassName varchar(20) ,
  RaceId int(1) ,
  STR int(2) ,
  CON int(2) ,
  DEX int(2) ,
  _INT int(2) ,
  WIT int(2) ,
  MEN int(2) ,
  P_ATK int(3) ,
  P_DEF int(3) ,
  M_ATK int(3) ,
  M_DEF int(2) ,
  P_SPD int(3) ,
  M_SPD int(3) ,
  ACC int(3) ,
  CRITICAL int(3) ,
  EVASION int(3) ,
  MOVE_SPD int(3) ,
  _LOAD int(11) ,
  x int(9) ,
  y int(9) ,
  z int(9) ,
  canCraft int(1) ,
  M_UNK1 decimal(4,2) ,
  M_UNK2 decimal(8,6) ,
  M_COL_R decimal(3,1) ,
  M_COL_H decimal(4,1) ,
  F_UNK1 decimal(4,2) ,
  F_UNK2 decimal(8,6) ,
  F_COL_R decimal(3,1) ,
  F_COL_H decimal(4,1) ,
  items1 int(4) ,
  items2 int(4) ,
  items3 int(4) ,
  items4 int(4) ,
  items5 int(10) ,
  PRIMARY KEY  (ClassId)
);

------------------------------
-- Table structure for character_macroses
------------------------------
CREATE TABLE character_macroses (
  char_obj_id decimal(11) ,
  id decimal(11) ,
  icon decimal(3) ,
  name varchar(20) ,
  descr varchar(80) ,
  acronym varchar(4) ,
  commands varchar(255) ,
  PRIMARY KEY  (char_obj_id,id)
);

------------------------------
-- Table structure for character_quests
------------------------------
CREATE TABLE character_quests (
  char_id int(11) ,
  name varchar(20) ,
  var varchar(20) ,
  value varchar(255) ,
  PRIMARY KEY  (char_id,name,var)
);

------------------------------
-- Table structure for character_recipebook
------------------------------
CREATE TABLE character_recipebook (
  char_id decimal(11) ,
  id decimal(11) ,
  PRIMARY KEY  (id,char_id)
);

------------------------------
-- Table structure for character_shortcuts
------------------------------
CREATE TABLE character_shortcuts (
  char_obj_id decimal(11) ,
  slot decimal(3) ,
  page decimal(3) ,
  type decimal(3) ,
  shortcut_id decimal(16) ,
  level varchar(4) ,
  unknown decimal(3) ,
  PRIMARY KEY  (char_obj_id,slot,page)
);

------------------------------
-- Table structure for character_skills
------------------------------
CREATE TABLE character_skills (
  char_obj_id decimal(11) ,
  skill_id decimal(3) ,
  skill_level varchar(5) ,
  skill_name varchar(24) ,
  PRIMARY KEY  (char_obj_id,skill_id)
);

------------------------------
-- Table structure for characters
------------------------------
CREATE TABLE characters (
  account_name varchar(13) ,
  obj_Id decimal(11) ,
  char_name varchar(35) ,
  level decimal(11) ,
  maxHp decimal(11) ,
  curHp decimal(18) ,
  maxCp decimal(11,0) ,
  curCp decimal(18,0) ,
  maxMp decimal(11) ,
  curMp decimal(18) ,
  acc decimal(11) ,
  crit decimal(10) ,
  evasion decimal(11) ,
  mAtk decimal(11) ,
  mDef decimal(11) ,
  mSpd decimal(11) ,
  pAtk decimal(11) ,
  pDef decimal(11) ,
  pSpd decimal(11) ,
  runSpd decimal(11) ,
  walkSpd decimal(11) ,
  str decimal(11) ,
  con decimal(11) ,
  dex decimal(11) ,
  _int decimal(11) ,
  men decimal(11) ,
  wit decimal(11) ,
  face decimal(11) ,
  hairStyle decimal(11) ,
  hairColor decimal(11) ,
  sex decimal(11) ,
  heading decimal(11) ,
  x decimal(11) ,
  y decimal(11) ,
  z decimal(11) ,
  movement_multiplier decimal(9,8) ,
  attack_speed_multiplier decimal(10,9) ,
  colRad decimal(10,9) ,
  colHeight decimal(10,9) ,
  exp decimal(11) ,
  sp decimal(11) ,
  karma decimal(11) ,
  pvpkills decimal(11) ,
  pkkills decimal(11) ,
  clanid decimal(11) ,
  maxload decimal(11) ,
  race decimal(11) ,
  classid decimal(11) ,
  deletetime decimal(11) ,
  cancraft decimal(11) ,
  title varchar(15) ,
  allyId decimal(11) ,
  rec_have int(3) ,
  rec_left int(3) ,
  accesslevel decimal(4) ,
  online decimal(1) ,
  char_slot decimal(1) ,
  lastAccess decimal(20,0) ,
  PRIMARY KEY  (obj_Id)
);

------------------------------
-- Table structure for clan_data
------------------------------
CREATE TABLE clan_data (
  clan_id decimal(11) ,
  clan_name varchar(45) ,
  clan_level decimal(1) ,
  hasCastle decimal(1) ,
  hasHideout decimal(1) ,
  ally_id decimal(9) ,
  ally_name varchar(45) ,
  leader_id decimal(11) ,
  crest_id decimal(11) ,
  ally_crest_id decimal(11) ,
  PRIMARY KEY  (clan_id)
);

------------------------------
-- Table structure for class_list
------------------------------
CREATE TABLE class_list (
  class_name varchar(19) ,
  id int(10) ,
  parent_id int(11) ,
  PRIMARY KEY  (id)
);

------------------------------
-- Table structure for droplist
------------------------------
CREATE TABLE droplist (
  mobId int(5) ,
  itemId int(4) ,
  min int(5) ,
  max int(5) ,
  sweep int(1) ,
  chance int(7) ,
  PRIMARY KEY  (mobId,itemId)
);

------------------------------
-- Table structure for etcitem
------------------------------
CREATE TABLE etcitem (
  item_id decimal(11) ,
  name varchar(38) ,
  crystallizable varchar(5) ,
  item_type varchar(12) ,
  weight decimal(4) ,
  consume_type varchar(9) ,
  material varchar(11) ,
  crystal_type varchar(4) ,
  durability decimal(3) ,
  price decimal(11) ,
  crystal_count int(4) ,
  sellable varchar(5) ,
  PRIMARY KEY  (item_id)
);

------------------------------
-- Table structure for henna
------------------------------
CREATE TABLE henna (
  symbol_id int(11) ,
  symbol_name varchar(45) ,
  dye_id int(11) ,
  dye_amount int(11) ,
  price int(11) ,
  stat_INT decimal(11) ,
  stat_STR decimal(11) ,
  stat_CON decimal(11) ,
  stat_MEM decimal(11) ,
  stat_DEX decimal(11) ,
  stat_WIT decimal(11) ,
  PRIMARY KEY  (symbol_id)
);

------------------------------
-- Table structure for henna_trees
------------------------------
CREATE TABLE henna_trees (
  class_id decimal(10) ,
  symbol_id decimal(10) ,
  PRIMARY KEY  (class_id)
);

------------------------------
-- Table structure for items
------------------------------
CREATE TABLE items (
  owner_id decimal(10) ,  -- object id of the player or clan, owner of this item
  object_id decimal(11) , -- object id of the item
  item_id decimal(6) ,    -- item id
  count decimal(10) ,
  enchant_level decimal(2) ,
  loc varchar(10) ,        -- inventory, paperdoll, npc, clan warehouse, pet, and so on
  loc_data decimal(10) , -- depending on location: equiped slot, npc id, pet id, etc
  price_sell decimal(10) ,
  price_buy decimal(10) ,
  PRIMARY KEY  (object_id)
);

------------------------------
-- Table structure for lvlupgain
------------------------------
CREATE TABLE lvlupgain (
  classid int(3) ,
  defaulthpbase decimal(4,1) ,
  defaulthpadd decimal(3,2) ,
  defaulthpmod decimal(3,2) ,
  defaultcpbase decimal(4,1) ,
  defaultcpadd decimal(3,2) ,
  defaultcpmod decimal(3,2) ,
  defaultmpbase decimal(4,1) ,
  defaultmpadd decimal(3,2) ,
  defaultmpmod decimal(3,2) ,
  class_lvl int(3) ,
  PRIMARY KEY  (classid)
);

------------------------------
-- Table structure for mapregion
------------------------------
CREATE TABLE mapregion (
  region int(11) ,
  sec0 int(2) ,
  sec1 int(2) ,
  sec2 int(2) ,
  sec3 int(2) ,
  sec4 int(2) ,
  sec5 int(2) ,
  sec6 int(2) ,
  sec7 int(2) ,
  sec8 int(2) ,
  sec9 int(2) ,
  PRIMARY KEY  (region)
);

------------------------------
-- Table structure for merchant_areas_list
------------------------------
CREATE TABLE merchant_areas_list (
  merchant_area_id int(10) ,
  merchant_area_name varchar(25) ,
  tax decimal(3,2) ,
  Chaotic int(11) ,
  PRIMARY KEY  (merchant_area_id)
);

------------------------------
-- Table structure for merchant_buylists
------------------------------
CREATE TABLE merchant_buylists (
  item_id decimal(9) ,
  price decimal(11) ,
  shop_id decimal(9) ,
  `order` decimal(4) ,
  PRIMARY KEY  (item_id,shop_id)
);

------------------------------
-- Table structure for merchant_lease
------------------------------
CREATE TABLE merchant_lease (
  merchant_id int(11) ,
  player_id int(11) ,
  bid int(11) ,
  type int(11) ,
  player_name varchar(35) ,
  PRIMARY KEY  (merchant_id,player_id,type)
);

------------------------------
-- Table structure for merchant_shopids
------------------------------
CREATE TABLE merchant_shopids (
  shop_id decimal(9) ,
  npc_id varchar(9) ,
  PRIMARY KEY  (shop_id)
);

------------------------------
-- Table structure for merchants
------------------------------
CREATE TABLE merchants (
  npc_id int(11) ,
  merchant_area_id int(4) ,
  PRIMARY KEY  (npc_id)
);

------------------------------
-- Table structure for minions
------------------------------

CREATE TABLE `minions` (
  `boss_id` decimal(11,0) NOT NULL default '0',
  `minion_id` decimal(11,0) NOT NULL default '0',
  `amount` int(4) NOT NULL default '0',
  PRIMARY KEY  (`boss_id`,`minion_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

------------------------------
-- Table structure for npc
------------------------------
CREATE TABLE npc (
  id decimal(11) ,
  name varchar(40) ,
  class varchar(40) ,
  collision_radius decimal(5,2) ,
  collision_height decimal(5,2) ,
  level decimal(2) ,
  sex varchar(6) ,
  type varchar(20) ,
  attackrange decimal(3) ,
  hp decimal(7) ,
  mp decimal(4) ,
  exp decimal(6) ,
  sp decimal(6) ,
  patk decimal(5) ,
  pdef decimal(3) ,
  matk decimal(5) ,
  mdef decimal(3) ,
  atkspd decimal(3) ,
  aggro decimal(6) ,
  matkspd decimal(3) ,
  rhand decimal(4) ,
  lhand decimal(3) ,
  armor decimal(1) ,
  walkspd decimal(3) ,
  runspd decimal(3) ,
  isUndead decimal(1) ,
  PRIMARY KEY  (id)
);

------------------------------
-- Table structure for npcskills
------------------------------
CREATE TABLE npcskills (
  npcid int(11) ,
  skillid int(11) ,
  level int(11) ,
  PRIMARY KEY  (npcid,skillid,level)
);

------------------------------
-- Table structure for pets
------------------------------
CREATE TABLE pets (
  item_obj_id decimal(11) ,
  objId decimal(11) ,
  name varchar(12) ,
  level decimal(11) ,
  maxHp decimal(11) ,
  curHp decimal(18) ,
  maxMp decimal(11) ,
  curMp decimal(18) ,
  acc decimal(11) ,
  crit decimal(11) ,
  evasion decimal(11) ,
  mAtk decimal(11) ,
  mDef decimal(11) ,
  mSpd decimal(11) ,
  pAtk decimal(11) ,
  pDef decimal(11) ,
  pSpd decimal(11) ,
  str decimal(11) ,
  con decimal(11) ,
  dex decimal(11) ,
  _int decimal(11) ,
  men decimal(11) ,
  wit decimal(11) ,
  exp decimal(11) ,
  sp decimal(11) ,
  karma decimal(11) ,
  pkkills decimal(11) ,
  maxload decimal(11) ,
  fed decimal(11) ,
  max_fed decimal(11) ,
  PRIMARY KEY  (item_obj_id)
);

------------------------------
-- Table structure for skill_learn
------------------------------
CREATE TABLE skill_learn (
  npc_id int(11) ,
  class_id int(11) ,
  PRIMARY KEY  (npc_id,class_id)
);

------------------------------
-- Table structure for skill_spellbooks
------------------------------
CREATE TABLE skill_spellbooks (
  skill_id int(11),
  item_id int(11),
  KEY skill_id (skill_id,item_id)
);

------------------------------
-- Table structure for skill_trees
------------------------------
CREATE TABLE skill_trees (
  class_id int(10) ,
  skill_id int(10) ,
  level int(10) ,
  name varchar(25) ,
  sp int(10) ,
  min_level int(10) ,
  PRIMARY KEY  (class_id,skill_id,level)
);

------------------------------
-- Table structure for spawnlist
------------------------------
CREATE TABLE `spawnlist` (
  `id` int(11) NOT NULL auto_increment,
  `location` varchar(30) NOT NULL default '',
  `count` int(9) NOT NULL default '0',
  `npc_templateid` int(9) NOT NULL default '0',
  `locx` int(9) NOT NULL default '0',
  `locy` int(9) NOT NULL default '0',
  `locz` int(9) NOT NULL default '0',
  `randomx` int(9) NOT NULL default '0',
  `randomy` int(9) NOT NULL default '0',
  `heading` int(9) NOT NULL default '0',
  `respawn_delay` int(9) NOT NULL default '0',
  `loc_id` int(9) NOT NULL default '0',
  PRIMARY KEY  (`id`),
  KEY `loc_id` (`loc_id`)
);

--
-- Table structure for table `locations`
--

CREATE TABLE `locations` (
  `loc_id` int(9) NOT NULL default '0',
  `loc_x` int(9) NOT NULL default '0',
  `loc_y` int(9) NOT NULL default '0',
  `loc_zmin` int(9) NOT NULL default '0',
  `loc_zmax` int(9) NOT NULL default '0',
  `proc` int(3) NOT NULL default '0',
  PRIMARY KEY  (`loc_id`,`loc_x`,`loc_y`),
  KEY `proc` (`proc`)
);

------------------------------
-- Table structure for teleport
------------------------------
CREATE TABLE teleport (
  Description varchar(75) ,
  id decimal(11) ,
  loc_x decimal(9) ,
  loc_y decimal(9) ,
  loc_z decimal(9) ,
  price decimal(5) ,
  PRIMARY KEY  (id)
);

------------------------------
-- Table structure for weapon
------------------------------
CREATE TABLE weapon (
  item_id decimal(11) ,
  name varchar(39) ,
  bodypart varchar(6) ,
  crystallizable varchar(5) ,
  weight decimal(4) ,
  soulshots decimal(2) ,
  spiritshots decimal(1) ,
  material varchar(11) ,
  crystal_type varchar(4) ,
  p_dam decimal(5) ,
  rnd_dam decimal(2) ,
  weaponType varchar(8) ,
  critical decimal(2) ,
  hit_modify decimal(6,5) ,
  avoid_modify decimal(2) ,
  shield_def decimal(3) ,
  shield_def_rate decimal(2) ,
  atk_speed decimal(3) ,
  mp_consume decimal(2) ,
  m_dam decimal(3) ,
  durability decimal(3) ,
  price decimal(11) ,
  crystal_count int(4) ,
  sellable varchar(5) ,
  PRIMARY KEY  (item_id)
);

--
-- Table structure for table `boxes`
--

CREATE TABLE boxes (
  id int(11) NOT NULL auto_increment,
  spawn decimal(11,0) default NULL,
  npcid decimal(11,0) default NULL,
  drawer varchar(32) default NULL,
  itemid decimal(11,0) default NULL,
  name varchar(32) default '',
  count decimal(11,0) default NULL,
  enchant decimal(2,0) default NULL,
  PRIMARY KEY  (id)
);

--
-- Table structure for table `boxaccess`
--

CREATE TABLE boxaccess (
  spawn decimal(11,0) default NULL,
  charname varchar(32) default NULL
);

--
-- Table structure for table `siege_clans`
--

CREATE TABLE siege_clans ( 
  castle_id int(1) default 0,
  clan_id int(11) default 0,
  type int(1) default NULL,
  castle_owner int(1) default NULL,
  PRIMARY KEY  (clan_id, castle_id)
);

--
-- Table structure for table `seven_signs`
--

CREATE TABLE seven_signs (
  char_obj_id int(11) NOT NULL default 0,
  cabal varchar(4) NOT NULL default,
  seal int(1) NOT NULL default -1,
  red_stones int(10) NOT NULL default 0,
  green_stones int(10) NOT NULL default 0,
  blue_stones int(10) NOT NULL default 0,
  ancient_adena_amount int(10) NOT NULL default 0,
  contribution_score int(10) NOT NULL default 0,
  PRIMARY KEY  (`char_obj_id`)
)

--
-- Table structure for table `global_tasks`
--

CREATE TABLE global_tasks (
  id int(11) NOT NULL auto_increment,
  task varchar(50) NOT NULL default '',
  type varchar(50) NOT NULL default '',
  last_activation int(20) NOT NULL default 0,
  param1 varchar(100) NOT NULL default '',
  param2 varchar(100) NOT NULL default '',
  param3 varchar(255) NOT NULL default '',
  PRIMARY KEY  (`id`)
);
