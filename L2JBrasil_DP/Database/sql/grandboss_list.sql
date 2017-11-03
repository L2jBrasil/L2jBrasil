-- ---------------------------
-- Table structure for grandboss_list
-- ---------------------------

CREATE TABLE IF NOT EXISTS grandboss_list (
  `player_id` decimal(11,0) NOT NULL,
  `zone` varchar(40) NOT NULL,
  PRIMARY KEY  (`player_id`)
);