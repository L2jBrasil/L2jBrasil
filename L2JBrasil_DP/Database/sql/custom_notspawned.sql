#--------------------------------------
# Table structure for custom_notspawned
#--------------------------------------
CREATE TABLE IF NOT EXISTS `custom_notspawned` (
  `id` int(11) NOT NULL,
  `isCustom` int(1) NOT NULL,
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
