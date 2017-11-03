-- ---------------------------- 
-- Table structure for character_friends
-- ---------------------------- 
CREATE TABLE IF NOT EXISTS `character_friends` ( 
  `char_id` INT NOT NULL default 0,
  `friend_id` INT(11) NOT NULL DEFAULT 0,
  `friend_name` VARCHAR(35) NOT NULL DEFAULT '',
  PRIMARY KEY  (`char_id`,`friend_name`) 
);
