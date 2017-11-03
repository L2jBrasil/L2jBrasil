-- ---------------------------
-- Table structure for grandboss_data
-- ---------------------------
CREATE TABLE IF NOT EXISTS grandboss_data (
  `boss_id` INTEGER NOT NULL DEFAULT 0,
  `loc_x` INTEGER NOT NULL DEFAULT 0,
  `loc_y` INTEGER NOT NULL DEFAULT 0,
  `loc_z` INTEGER NOT NULL DEFAULT 0,
  `heading` INTEGER NOT NULL DEFAULT 0,
  `respawn_time` BIGINT NOT NULL DEFAULT 0,
  `currentHP` DECIMAL(8,0) DEFAULT NULL,
  `currentMP` DECIMAL(8,0) DEFAULT NULL,
  `status` TINYINT NOT NULL DEFAULT 0,
  PRIMARY KEY(`boss_id`)
);

INSERT INTO `grandboss_data` VALUES 

(29001, -21610, 181594, -5734, 0, 60000, 2179536, 667, 0), -- Queen Ant (40)
(29006, 17726, 108915, -6480, 0, 60000, 162561, 575, 0), -- Core (50)
(29014, 55024, 17368, -5412, 10126, 60000, 325124, 1660, 0), -- Orfen (50)
(29019, 185708,114298,-8221,32768, 60000, 13090000, 22197, 0), -- Antharas (79)
(29020, 115213,16623,10080,41740, 60000, 790857, 3347, 0), -- Baium (75)
(29022, 55312, 219168, -3223, 0, 60000, 858518, 1975, 0), -- Zaken (60)
(29028, -105200,-253104,-15264,0, 60000, 16660000, 22197, 0), -- Valakas (85)
(29045, 0,0,0,0, 0, 790857, 1859, 0),						-- Frintezza
(29046, 174231, -88006, -5115, 0, 0, 1824900, 23310, 0), -- Scarlet Van Halisha (85)
(29047, 174231, -88006, -5115, 0, 0, 898044, 4519, 0), -- Scarlet Van Halisha (85)
(29054, 0, 0, 0, 0, 0, 300000, 2000, 0),			-- Benom
(29062, 0, 0, 0, 0, 0, 0, 0, 0),				-- High Priestess van Halter
(29065, -123348, -248881, -15537, 44732, 0, 1532678, 4255, 0);	-- Sailren
