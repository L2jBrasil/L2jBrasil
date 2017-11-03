--
-- Table structure for table `boxes`
--

CREATE TABLE IF NOT EXISTS boxes (
  id int(11) NOT NULL auto_increment,
  spawn decimal(11,0) default NULL,
  npcid decimal(11,0) default NULL,
  drawer varchar(32) default NULL,
  itemid decimal(11,0) default NULL,
  name varchar(32) default '',
  count decimal(11,0) default NULL,
  enchant decimal(2,0) default NULL,
  PRIMARY KEY  (id)
);