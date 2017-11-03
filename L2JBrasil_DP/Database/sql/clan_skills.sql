-- ----------------------------
-- Table structure for clan_skills
-- ----------------------------
CREATE TABLE IF NOT EXISTS clan_skills (
  clan_id int(11) NOT NULL default 0,
  skill_id int(11) NOT NULL default 0,
  skill_level int(5) NOT NULL default 0,
  skill_name varchar(26) default NULL,
  PRIMARY KEY  (`clan_id`,`skill_id`)
);
