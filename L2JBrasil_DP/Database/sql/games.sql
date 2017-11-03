-- ---------------------------
-- Table structure for games
-- ---------------------------
CREATE TABLE IF NOT EXISTS games (
  id INT NOT NULL default 0,
  idnr INT NOT NULL default 0,
  number1 INT NOT NULL default 0,
  number2 INT NOT NULL default 0,
  prize  INT NOT NULL default 0,
  newprize  INT NOT NULL default 0,
  prize1  INT NOT NULL default 0,
  prize2  INT NOT NULL default 0,
  prize3  INT NOT NULL default 0,
  enddate decimal(20,0) NOT NULL default 0,
  finished INT NOT NULL default 0,
  PRIMARY KEY (`id`,`idnr`)
);

