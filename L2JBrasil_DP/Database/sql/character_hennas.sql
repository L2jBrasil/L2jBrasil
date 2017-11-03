CREATE TABLE IF NOT EXISTS `character_hennas` (
  `char_obj_id` INT NOT NULL DEFAULT 0,
  `symbol_id` INT,
  `slot` INT NOT NULL DEFAULT 0,
  `class_index` INT(1) NOT NULL DEFAULT 0,
  PRIMARY KEY (`char_obj_id`,`slot`,`class_index`)
);

