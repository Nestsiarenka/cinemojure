CREATE TABLE user_groups
(id SMALLSERIAL PRIMARY KEY,
 alias VARCHAR(40));

 ;--
 INSERT INTO user_groups (alias) VALUES
	('guest'),
	('user'),
	('admin');