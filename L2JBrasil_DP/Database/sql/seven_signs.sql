CREATE TABLE IF NOT EXISTS `seven_signs` (
  `char_obj_id` INT NOT NULL default '0',
  `cabal` VARCHAR(4) NOT NULL default '',
  `seal` INT(1) NOT NULL default '0',
  `red_stones` INT NOT NULL default '0',
  `green_stones` INT NOT NULL default '0',
  `blue_stones` INT NOT NULL default '0',
  `ancient_adena_amount` DECIMAL(20,0) NOT NULL default '0',
  `contribution_score` DECIMAL(20,0) NOT NULL default '0',
  PRIMARY KEY  (`char_obj_id`)
);
