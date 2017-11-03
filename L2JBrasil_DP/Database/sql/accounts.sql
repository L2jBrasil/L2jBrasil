-- ---------------------------
-- Table structure for accounts
-- ---------------------------
CREATE TABLE IF NOT EXISTS `accounts` (
  `login` VARCHAR(45) NOT NULL default '',
  `password` VARCHAR(45) ,
  `lastactive` DECIMAL(20),
  `access_level` INT,
  `lastIP` VARCHAR(20),
  `lastServer` int(4) default 1,
  PRIMARY KEY (`login`)
);
