-- ---------------------------
-- Table structure for castle
-- ---------------------------
CREATE TABLE IF NOT EXISTS castle 
(
  id INT NOT NULL default 0,
  name varchar(25) NOT NULL,
  taxPercent INT NOT NULL default 15,
  treasury INT NOT NULL default 0,
  siegeDate DECIMAL(20,0) NOT NULL default 0,
  siegeDayOfWeek INT NOT NULL default 7,
  siegeHourOfDay INT NOT NULL default 20,
  showNpcCrest varchar(5) default NULL,
  PRIMARY KEY  (name),
  KEY id (id)
)ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

INSERT IGNORE INTO `castle` VALUES 
(1,'Gludio',0,0,0,7,20,'false'),
(2,'Dion',0,0,0,7,20,'false'),
(3,'Giran',0,0,0,1,16,'false'),
(4,'Oren',0,0,0,1,16,'false'),
(5,'Aden',0,0,0,7,20,'false'),
(6,'Innadril',0,0,0,1,16,'false'),
(7,'Goddard',0,0,0,1,16,'false'),
(8,'Rune',0,0,0,7,20,'false'),
(9,'Schuttgart',0,0,0,7,20,'false');
