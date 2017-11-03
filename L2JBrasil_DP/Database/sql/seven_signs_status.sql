CREATE TABLE IF NOT EXISTS `seven_signs_status` (
  `id` int(3) NOT NULL default '0',
  `current_cycle` int(10) NOT NULL default '1',
  `festival_cycle` int(10) NOT NULL default '1',
  `active_period` int(10) NOT NULL default '1',
  `date` int(10) NOT NULL default '1',
  `previous_winner` int(10) NOT NULL default '0',
  `dawn_stone_score` DECIMAL(20,0) NOT NULL default '0',
  `dawn_festival_score` int(10) NOT NULL default '0',
  `dusk_stone_score` DECIMAL(20,0) NOT NULL default '0',
  `dusk_festival_score` int(10) NOT NULL default '0',
  `avarice_owner` int(10) NOT NULL default '0',
  `gnosis_owner` int(10) NOT NULL default '0',
  `strife_owner` int(10) NOT NULL default '0',
  `avarice_dawn_score` int(10) NOT NULL default '0',
  `gnosis_dawn_score` int(10) NOT NULL default '0',
  `strife_dawn_score` int(10) NOT NULL default '0',
  `avarice_dusk_score` int(10) NOT NULL default '0',
  `gnosis_dusk_score` int(10) NOT NULL default '0',
  `strife_dusk_score` int(10) NOT NULL default '0',
  `accumulated_bonus0` int(10) NOT NULL default '0',
  `accumulated_bonus1` int(10) NOT NULL default '0',
  `accumulated_bonus2` int(10) NOT NULL default '0',
  `accumulated_bonus3` int(10) NOT NULL default '0',
  `accumulated_bonus4` int(10) NOT NULL default '0',
  PRIMARY KEY  (`id`)
);

INSERT IGNORE INTO `seven_signs_status` VALUES
(0,1,1,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0);