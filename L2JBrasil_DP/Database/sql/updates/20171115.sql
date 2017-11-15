ALTER TABLE `characters` 
CHANGE COLUMN `obj_Id` `obj_Id` DECIMAL(11,0) NOT NULL DEFAULT '0' FIRST,
CHANGE COLUMN `pc_point` `pc_point` INT(11) NOT NULL DEFAULT '0',
ADD INDEX `hero` (`hero` ASC),
ADD INDEX `account` (`account_name` ASC),
ADD INDEX `base_class` (`base_class` ASC);