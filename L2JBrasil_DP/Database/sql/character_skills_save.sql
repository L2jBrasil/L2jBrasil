-- ---------------------------
-- Table structure for character_skills
-- ---------------------------
CREATE TABLE IF NOT EXISTS character_skills_save (
  char_obj_id INT NOT NULL default 0,
  skill_id INT NOT NULL default 0,
  skill_level INT NOT NULL default 0,
  effect_count INT NOT NULL default 0,
  effect_cur_time INT NOT NULL default 0,
  reuse_delay INT(8) NOT NULL DEFAULT 0,
  restore_type INT(1) NOT NULL DEFAULT 0,
  `class_index` INT(1) NOT NULL DEFAULT 0,
  buff_index INT(2) NOT NULL default 0,
  PRIMARY KEY  (char_obj_id,skill_id,`class_index`)
) ;