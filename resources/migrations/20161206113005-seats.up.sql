CREATE TABLE seats
(id SERIAL PRIMARY KEY,
 seat_type_id SMALLINT NOT NULL REFERENCES seat_type,
 seat_raw SMALLINT NOT NULL,
 seat_number SMALLINT NOT NULL,
 auditorium_id SMALLINT NOT NULL REFERENCES auditoriums,
 UNIQUE(auditorium_id, seat_raw, seat_number));

;--

INSERT INTO seats (seat_type_id, seat_raw, seat_number, auditorium_id) VALUES (1, 1, 1, 1), (2, 2, 2, 1);
