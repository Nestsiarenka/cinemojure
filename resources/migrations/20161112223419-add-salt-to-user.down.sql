ALTER TABLE users
DROP COLUMN salt,
ALTER COLUMN pass TYPE VARCHAR(300);
