CREATE TABLE IF NOT EXISTS siege_clans (
   castle_id int(1) NOT NULL default 0,
   clan_id int(11) NOT NULL default 0,
   type int(1) default NULL,
   castle_owner int(1) default NULL,
   PRIMARY KEY  (clan_id,castle_id)
);