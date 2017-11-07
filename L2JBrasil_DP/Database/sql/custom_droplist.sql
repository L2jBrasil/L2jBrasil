-- Information about category:
-- Only one drop will be given per category, except in the cases when category is -1.
-- Category -1 is used for SPOIL/SWEEP drops only!  Do NOT change this.
-- In general, category 0 is for adena and 1 seal stone color (each seal stone color should be in a different category).
-- In general, category 1 is for full drops and parts of equipable items.  However, you can change this.
-- In general, category 2 is for all other items.  However, you can change this.
-- In general, more categories are only used for RBs who have 1 item per category (i.e. do not really drop categorized),
-- with the sole exception of category 200 that is used for Life Stones. However, you can change this too.
-- You can create more categories as you see fit.  Just make sure the "category" number is non-negative!!
-- Also, it is NOT a problem if category numbers are skipped (so you can have -1, 1, 5, 10 as your categories).
--
-- If you wish to allow more than one item to be given from the same category, you can
-- split them up over several categories.
-- In addition, RBs and Grandbosses (mainly) may have the exact same item repeated in multiple categories.
-- This allows mobs to give 1 copy of the drop to each of several people (if they are lucky enough to get the drops).
-- Calculation for each drop, when in categories, is equivallent in chance as when outside of categories.
-- First, the sum of chances for each category is calculated as category chance.  If the category is selected
-- for drops (i.e. its chance is successful), then exactly 1 item from that category will be selected, with 
-- such a chance that the overall probability is maintained unchanged. 


--
-- Table structure for table `custom_droplist`
--
DROP TABLE IF EXISTS `custom_droplist`;
CREATE TABLE `custom_droplist` (
  `mobId` INT NOT NULL DEFAULT '0',
  `itemId` INT NOT NULL DEFAULT '0',
  `min` INT NOT NULL DEFAULT '0',
  `max` INT NOT NULL DEFAULT '0',
  `category` INT NOT NULL DEFAULT '0',
  `chance` INT NOT NULL DEFAULT '0',
  PRIMARY KEY  (`mobId`,`itemId`,`category`),
  KEY `key_mobId` (`mobId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

-- 
-- Dumping data for table `droplist`
-- 
