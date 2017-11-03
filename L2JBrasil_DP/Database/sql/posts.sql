-- ---------------------------
-- Table structure for posts
-- ---------------------------
CREATE TABLE IF NOT EXISTS `posts` (
  `post_id` int(8) NOT NULL default '0',
  `post_owner_name` varchar(255) NOT NULL default '',
  `post_ownerid` int(8) NOT NULL default '0',
  `post_date` decimal(20,0) NOT NULL default '0',
  `post_topic_id` int(8) NOT NULL default '0',
  `post_forum_id` int(8) NOT NULL default '0',
  `post_txt` text NOT NULL
);
