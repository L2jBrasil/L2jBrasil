CREATE TABLE IF NOT EXISTS `heroes` (
  `char_id` decimal(11,0) NOT NULL default '0',
  `char_name` varchar(45) NOT NULL default '',
  `class_id` decimal(3,0) NOT NULL default '0',
  `count` decimal(3,0) NOT NULL default '0',
  `played` decimal(1,0) NOT NULL default '0',
  PRIMARY KEY  (`char_id`)
);