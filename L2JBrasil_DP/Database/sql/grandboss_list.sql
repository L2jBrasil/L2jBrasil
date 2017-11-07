-- ---------------------------
-- Table structure for grandboss_list
-- ---------------------------

CREATE TABLE IF NOT EXISTS grandboss_list (
  `player_id` decimal(11,0) NOT NULL,
  `zone` varchar(40) NOT NULL,
  PRIMARY KEY  (`player_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;