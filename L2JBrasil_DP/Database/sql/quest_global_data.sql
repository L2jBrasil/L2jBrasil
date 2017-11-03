-- ----------------------------
-- Table structure for global quest data (i.e. quest data not particular to a player)
-- Note: We had considered using character_quests with char_id = 0 for this, but decided 
-- against it, primarily for aesthetic purposes, cleaningness of code, expectability, and
-- to keep char-related data purely as char-related data, global purely as global.
-- ----------------------------
CREATE TABLE IF NOT EXISTS `quest_global_data` (
  `quest_name` VARCHAR(40) NOT NULL DEFAULT '',
  `var`  VARCHAR(20) NOT NULL DEFAULT '',
  `value` VARCHAR(255) ,
  PRIMARY KEY  (`quest_name`,`var`)
);
