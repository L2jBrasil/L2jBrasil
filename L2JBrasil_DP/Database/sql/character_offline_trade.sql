drop table IF EXISTS `character_offline_trade`;
CREATE TABLE `character_offline_trade` (
  `charId` int(11) NOT NULL,
  `time` bigint(20) unsigned NOT NULL DEFAULT '0',
  `type` tinyint(4) NOT NULL DEFAULT '0',
  `title` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`charId`)
) ;

drop table IF EXISTS `character_offline_trade_items`;
CREATE TABLE `character_offline_trade_items` (
  `charId` int(10) NOT NULL DEFAULT '0',
  `item` int(10) NOT NULL DEFAULT '0',
  `count` int(20) NOT NULL DEFAULT '0',
  `price` int(20) NOT NULL DEFAULT '0',
  `enchant` int(20) NOT NULL DEFAULT '0'
) ;