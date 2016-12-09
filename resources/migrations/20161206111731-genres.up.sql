CREATE TABLE genres
(id SMALLSERIAL PRIMARY KEY,
 name VARCHAR(30) NOT NULL UNIQUE);

;--
INSERT INTO genres (name) VALUES ('Adventures'), ('Fantastic'),
('Horror');
