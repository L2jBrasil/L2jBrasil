-- ---------------------------
-- Table structure for clanhall
-- ---------------------------
CREATE TABLE IF NOT EXISTS `clanhall` (
  `id` int(11) NOT NULL default '0',
  `name` varchar(40) NOT NULL default '',
  `ownerId` int(11) NOT NULL default '0',
  `lease` int(10) NOT NULL default '0',
  `desc` text NOT NULL,
  `location` varchar(15) NOT NULL default '',
  `paidUntil` decimal(20,0) NOT NULL default '0',
  `Grade` decimal(1,0) NOT NULL default '0',
  `paid` int( 1 ) NOT NULL default '0',
  PRIMARY KEY `id` (`id`)
)ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

-- ----------------------------
-- Records 
-- ----------------------------
INSERT IGNORE INTO `clanhall` VALUES
 ('21','Fortress of Resistance','0','100000','Ol Mahum Fortress of Resistance','Dion','0','0','0'),
 ('22','Moonstone Hall','0','100000','Clan hall located in the Town of Gludio','Gludio','0','2','0'),
 ('23','Onyx Hall','0','100000','Clan hall located in the Town of Gludio','Gludio','0','2','0'),
 ('24','Topaz Hall','0','100000','Clan hall located in the Town of Gludio','Gludio','0','2','0'),
 ('25','Ruby Hall','0','100000','Clan hall located in the Town of Gludio','Gludio','0','2','0'),
 ('26','Crystal Hall','0','100000','Clan hall located in Gludin Village','Gludin','0','2','0'),
 ('27','Onyx Hall','0','100000','Clan hall located in Gludin Village','Gludin','0','2','0'),
 ('28','Sapphire Hall','0','100000','Clan hall located in Gludin Village','Gludin','0','2','0'),
 ('29','Moonstone Hall','0','100000','Clan hall located in Gludin Village','Gludin','0','2','0'),
 ('30','Emerald Hall','0','100000','Clan hall located in Gludin Village','Gludin','0','2','0'),
 ('31','The Atramental Barracks','0','100000','Clan hall located in the Town of Dion','Dion','0','1','0'),
 ('32','The Scarlet Barracks','0','100000','Clan hall located in the Town of Dion','Dion','0','1','0'),
 ('33','The Viridian Barracks','0','100000','Clan hall located in the Town of Dion','Dion','0','1','0'),
 ('34','Devastated Castle','0','100000','Contestable Clan Hall','Aden','0','0','0'),
 ('35','Bandit Stronghold','0','100000','Contestable Clan Hall','Oren','0','0','0'),
 ('36','The Golden Chamber','0','100000','Clan hall located in the Town of Aden','Aden','0','3','0'),
 ('37','The Silver Chamber','0','100000','Clan hall located in the Town of Aden','Aden','0','3','0'),
 ('38','The Mithril Chamber','0','100000','Clan hall located in the Town of Aden','Aden','0','3','0'),
 ('39','Silver Manor','0','100000','Clan hall located in the Town of Aden','Aden','0','3','0'),
 ('40','Gold Manor','0','100000','Clan hall located in the Town of Aden','Aden','0','3','0'),
 ('41','The Bronze Chamber','0','100000','Clan hall located in the Town of Aden','Aden','0','3','0'),
 ('42','The Golden Chamber','0','100000','Clan hall located in the Town of Giran','Giran','0','3','0'),
 ('43','The Silver Chamber','0','100000','Clan hall located in the Town of Giran','Giran','0','3','0'),
 ('44','The Mithril Chamber','0','100000','Clan hall located in the Town of Giran','Giran','0','3','0'),
 ('45','The Bronze Chamber','0','100000','Clan hall located in the Town of Giran','Giran','0','3','0'),
 ('46','Silver Manor','0','100000','Clan hall located in the Town of Giran','Giran','0','3','0'),
 ('47','Moonstone Hall','0','100000','Clan hall located in the Town of Goddard','Goddard','0','3','0'),
 ('48','Onyx Hall','0','100000','Clan hall located in the Town of Goddard','Goddard','0','3','0'),
 ('49','Emerald Hall','0','100000','Clan hall located in the Town of Goddard','Goddard','0','3','0'),
 ('50','Sapphire Hall','0','100000','Clan hall located in the Town of Goddard','Goddard','0','3','0'),
 ('51','Mont Chamber','0','100000','An upscale Clan hall located in the Rune Township','Rune','0','3','0'),
 ('52','Astaire Chamber','0','100000','An upscale Clan hall located in the Rune Township','Rune','0','3','0'),
 ('53','Aria Chamber','0','100000','An upscale Clan hall located in the Rune Township','Rune','0','3','0'),
 ('54','Yiana Chamber','0','100000','An upscale Clan hall located in the Rune Township','Rune','0','3','0'),
 ('55','Roien Chamber','0','100000','An upscale Clan hall located in the Rune Township','Rune','0','3','0'),
 ('56','Luna Chamber','0','100000','An upscale Clan hall located in the Rune Township','Rune','0','3','0'),
 ('57','Traban Chamber','0','100000','An upscale Clan hall located in the Rune Township','Rune','0','3','0'),
 ('58','Eisen Hall','0','100000','Clan hall located in the Town of Schuttgart','Schuttgart','0','2','0'),
 ('59','Heavy Metal Hall','0','100000','Clan hall located in the Town of Schuttgart','Schuttgart','0','2','0'),
 ('60','Molten Ore Hall','0','100000','Clan hall located in the Town of Schuttgart','Schuttgart','0','2','0'),
 ('61','Titan Hall','0','100000','Clan hall located in the Town of Schuttgart','Schuttgart','0','2','0'),
 ('62','Rainbow Springs','0','100000','','Goddard','0','0','0'),
 ('63','Beast Farm','0','100000','','Rune','0','0','0'),
 ('64','Fortress of the Dead','0','100000','','Rune','0','0','0');