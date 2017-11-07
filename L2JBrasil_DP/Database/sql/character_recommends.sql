-- ---------------------------- 
-- Table structure for character_recommends 
-- ---------------------------- 
CREATE TABLE IF NOT EXISTS character_recommends ( 
 char_id INT NOT NULL default 0, 
 target_id INT(11) NOT NULL DEFAULT 0, 
 PRIMARY KEY (char_id,target_id) 
)ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
