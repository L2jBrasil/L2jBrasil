-- ----------------------------
-- Table structure for items
-- ----------------------------
CREATE TABLE IF NOT EXISTS items (
  `owner_id` INT, -- object id of the player or clan,owner of this item
  `object_id` INT NOT NULL DEFAULT 0,-- object id of the item
  `item_id` INT,   -- item id
  `count` INT,
  `enchant_level` INT,
  `loc` VARCHAR(10),      -- inventory,paperdoll,npc,clan warehouse,pet,and so on
  `loc_data` INT, -- depending on location: equiped slot,npc id,pet id,etc
  `price_sell` INT,
  `price_buy` INT,
  `time_of_use` INT, -- time of item use, for calculate of breackages
  `custom_type1` INT DEFAULT 0,
  `custom_type2` INT DEFAULT 0,
  `mana_left` decimal(3,0) NOT NULL default -1,
  PRIMARY KEY (`object_id`),
  KEY `key_owner_id` (`owner_id`),
  KEY `key_loc` (`loc`),
  KEY `key_item_id` (`item_id`),
  KEY `key_time_of_use` (`time_of_use`)
);
