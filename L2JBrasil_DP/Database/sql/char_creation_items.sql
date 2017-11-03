DROP TABLE IF EXISTS `char_creation_items`;
CREATE TABLE `char_creation_items` (
  `classId` smallint(6) NOT NULL,
  `itemId` smallint(6) unsigned NOT NULL,
  `amount` int(10) unsigned NOT NULL DEFAULT '1',
  `equipped` enum('true','false') NOT NULL DEFAULT 'false',
  PRIMARY KEY (`classId`,`itemId`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;


INSERT INTO `char_creation_items` VALUES ('-1', '5588', '1', 'false');
INSERT INTO `char_creation_items` VALUES ('0', '10', '1', 'false');
INSERT INTO `char_creation_items` VALUES ('0', '1146', '1', 'true');
INSERT INTO `char_creation_items` VALUES ('0', '1147', '1', 'true');
INSERT INTO `char_creation_items` VALUES ('0', '2369', '1', 'true');
INSERT INTO `char_creation_items` VALUES ('10', '6', '1', 'true');
INSERT INTO `char_creation_items` VALUES ('10', '425', '1', 'true');
INSERT INTO `char_creation_items` VALUES ('10', '461', '1', 'true');
INSERT INTO `char_creation_items` VALUES ('18', '10', '1', 'false');
INSERT INTO `char_creation_items` VALUES ('18', '1146', '1', 'true');
INSERT INTO `char_creation_items` VALUES ('18', '1147', '1', 'true');
INSERT INTO `char_creation_items` VALUES ('18', '2369', '1', 'true');
INSERT INTO `char_creation_items` VALUES ('25', '6', '1', 'true');
INSERT INTO `char_creation_items` VALUES ('25', '425', '1', 'true');
INSERT INTO `char_creation_items` VALUES ('25', '461', '1', 'true');
INSERT INTO `char_creation_items` VALUES ('31', '10', '1', 'false');
INSERT INTO `char_creation_items` VALUES ('31', '1146', '1', 'true');
INSERT INTO `char_creation_items` VALUES ('31', '1147', '1', 'true');
INSERT INTO `char_creation_items` VALUES ('31', '2369', '1', 'true');
INSERT INTO `char_creation_items` VALUES ('38', '6', '1', 'true');
INSERT INTO `char_creation_items` VALUES ('38', '425', '1', 'true');
INSERT INTO `char_creation_items` VALUES ('38', '461', '1', 'true');
INSERT INTO `char_creation_items` VALUES ('44', '1146', '1', 'true');
INSERT INTO `char_creation_items` VALUES ('44', '1147', '1', 'true');
INSERT INTO `char_creation_items` VALUES ('44', '2368', '1', 'true');
INSERT INTO `char_creation_items` VALUES ('44', '2369', '1', 'false');
INSERT INTO `char_creation_items` VALUES ('49', '425', '1', 'true');
INSERT INTO `char_creation_items` VALUES ('49', '461', '1', 'true');
INSERT INTO `char_creation_items` VALUES ('49', '2368', '1', 'true');
INSERT INTO `char_creation_items` VALUES ('53', '10', '1', 'false');
INSERT INTO `char_creation_items` VALUES ('53', '1146', '1', 'true');
INSERT INTO `char_creation_items` VALUES ('53', '1147', '1', 'true');
INSERT INTO `char_creation_items` VALUES ('53', '2370', '1', 'true');