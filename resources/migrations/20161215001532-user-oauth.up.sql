ALTER TABLE users
ADD COLUMN oauth_id VARCHAR(30);

;--
ALTER TABLE users
ADD COLUMN oauth_provider VARCHAR(15);
