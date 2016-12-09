CREATE TABLE seat_type
(id SMALLSERIAL PRIMARY KEY,
 name VARCHAR(30),  
 cost INTEGER);

;--

INSERT INTO seat_type (name, cost) VALUES ('Common', 20),
('Recliner', 30);
