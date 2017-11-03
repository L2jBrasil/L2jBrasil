-- ---------------------------
-- Table structure for `castle_manor_procure`
-- ---------------------------
CREATE TABLE IF NOT EXISTS `castle_manor_procure` (
  `castle_id` INT NOT NULL DEFAULT '0',
  `crop_id` int(11) NOT NULL DEFAULT '0',
  `can_buy` int(11) NOT NULL DEFAULT '0',
  `start_buy` int(11) NOT NULL DEFAULT '0',
  `price` int(11) NOT NULL DEFAULT '0',
  `reward_type` int(11) NOT NULL DEFAULT '0',
  `period` INT NOT NULL DEFAULT '1',
  PRIMARY KEY  (`castle_id`,`crop_id`,`period`)
) DEFAULT CHARSET=utf8;
