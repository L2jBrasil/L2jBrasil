-- ---------------------------
-- Table structure for `castle_manor_production`
-- ---------------------------
CREATE TABLE IF NOT EXISTS `castle_manor_production` (
  `castle_id` INT NOT NULL DEFAULT '0',
  `seed_id` int(11) NOT NULL DEFAULT '0',
  `can_produce` int(11) NOT NULL DEFAULT '0',
  `start_produce` int(11) NOT NULL DEFAULT '0',
  `seed_price` int(11) NOT NULL DEFAULT '0',
  `period` INT NOT NULL DEFAULT '1',
  PRIMARY KEY  (`castle_id`,`seed_id`,`period`)
) DEFAULT CHARSET=utf8;
