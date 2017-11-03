-- ---------------------------
-- Table structure for `itemsonground`
-- ---------------------------
CREATE TABLE IF NOT EXISTS `itemsonground` (
  `object_id` int(11) NOT NULL default '0',
  `item_id` int(11) default NULL,
  `count` int(11) default NULL,
  `enchant_level` int(11) default NULL,
  `x` int(11) default NULL,
  `y` int(11) default NULL,
  `z` int(11) default NULL,
  `drop_time` decimal(20,0) default NULL,
  `equipable` int(1) default '0',
  PRIMARY KEY  (`object_id`)
);