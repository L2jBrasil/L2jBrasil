--
-- Table structure for table `boxaccess`
--

CREATE TABLE IF NOT EXISTS boxaccess (
  spawn decimal(11,0) default NULL,
  charname varchar(32) default NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;