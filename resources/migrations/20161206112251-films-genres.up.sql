CREATE TABLE films_genres
(id SERIAL PRIMARY KEY,
 film_id INTEGER NOT NULL REFERENCES films ON DELETE CASCADE,
 genre_id SMALLINT NOT NULL REFERENCES genres);

;--
INSERT INTO films_genres (film_id, genre_id) VALUES (1, 1), (1, 2)
