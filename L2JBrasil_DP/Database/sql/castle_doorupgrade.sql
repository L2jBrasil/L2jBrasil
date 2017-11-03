-- ---------------------------
-- Table structure for castle_doorupgrade
-- ---------------------------
CREATE TABLE IF NOT EXISTS castle_doorupgrade (
  doorId INT NOT NULL default 0,
  hp INT NOT NULL default 0,
  pDef INT NOT NULL default 0,
  mDef INT NOT NULL default 0,
  PRIMARY KEY  (doorId )
);