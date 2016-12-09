CREATE TABLE films
(id SERIAL PRIMARY KEY,
 title VARCHAR(100) NOT NULL,
 duration_minutes INTEGER NOT NULL,
 age_limit SMALLINT NOT NULL,
 logo_url VARCHAR(100) NOT NULL);

;--
INSERT INTO films (title, duration_minutes, age_limit, logo_url)
VALUES ('Fantastic beasts and where to find them', 100, 12, 'hello')
