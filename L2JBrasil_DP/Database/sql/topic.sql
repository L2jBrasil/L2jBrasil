-- ---------------------------
-- Table structure for topic
-- ---------------------------
CREATE TABLE IF NOT EXISTS `topic` (
  `topic_id` int(8) NOT NULL default '0',
  `topic_forum_id` int(8) NOT NULL default '0',
  `topic_name` varchar(255) NOT NULL default '',
  `topic_date` decimal(20,0) NOT NULL default '0',
  `topic_ownername` varchar(255) NOT NULL default '0',
  `topic_ownerid` int(8) NOT NULL default '0',
  `topic_type` int(8) NOT NULL default '0',
  `topic_reply` int(8) NOT NULL default '0'
) ENGINE=MyISAM;
