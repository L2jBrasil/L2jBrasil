-- ---------------------------
-- Table structure for auction_watch
-- ---------------------------
CREATE TABLE IF NOT EXISTS auction_watch (
  charObjId INT NOT NULL default 0,
  auctionId INT NOT NULL default 0,
  PRIMARY KEY  (charObjId, auctionId)
)ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
