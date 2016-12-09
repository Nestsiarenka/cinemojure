CREATE TABLE auditoriums_auditorium_parameters
(id SERIAL PRIMARY KEY,
 auditorium_id SMALLINT REFERENCES auditoriums,
 auditorium_parameter_id SMALLINT REFERENCES auditorium_parameters);

;--

INSERT INTO auditoriums_auditorium_parameters (auditorium_id,
auditorium_parameter_id) VALUES (1, 1), (1, 2)
