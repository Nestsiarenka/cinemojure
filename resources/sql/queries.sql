-- :name create-user! :<! :1
-- :doc creates a new user record
INSERT INTO users
(first_name, last_name, email, login, user_group_id, pass)
VALUES (:first_name, :second_name, :email, :login, :user_group_id, :pass) RETURNING id

-- :name update-user! :! :n
-- :doc update an existing user record
UPDATE users
SET first_name = :first_name, last_name = :last_name,
    email = :email, login = :login, user_group_id = :user_group_id,
    last_login = :last_login, is_active = :is_active, pass = :pass
WHERE id = :id

-- :name get-user :? :1
-- :doc retrieve a user given the id.
SELECT * FROM users
WHERE id = :id

-- :name get-user-by-login :? :1
-- :doc retrieve a user given the login.
SELECT users.id, users.first_name, users.last_name
as second_name, users.login, users.email, users.pass,
users.is_active, user_groups.alias as user_group,
user_groups.id as
user_groups_id
FROM users, user_groups
WHERE login = :login

-- :name get-user-info :? :1
-- :doc retrieve info about user
SELECT users.first_name, users.last_name, users.login,
users.email, user_groups.alias as user_group FROM users, user_groups
WHERE users.user_group_id = user_groups.id AND users.id = :id

-- :name get-users :? :*
-- :doc retrieve all users.
SELECT * FROM users

-- :name get-user-role :? :1
-- :doc get group that an user belong.
SELECT user_groups.id, user_groups.alias FROM user_groups, users
WHERE user_groups.id = users.user_group_id AND users.id = :id

-- :name delete-user! :! :n
-- :doc delete a user given the id
DELETE FROM users
WHERE id = :id
-- :name add-genre! :<! :1
-- :doc creates a new genre record
INSERT INTO genres (name)
VALUES (:name) RETURNING id

-- :name get-genres :? :*
-- :doc retrieve all genres.
SELECT * FROM genres

-- :name get-genre :? :1
SELECT * FROM genres WHERE id = :id

-- :name delete-genre! :! :n
DELETE FROM genres WHERE id = :id 

-- :name add-film! :<! :1
-- :doc creates a new film record
INSERT INTO films
(title, duration_minutes, age_limit, logo_url)
VALUES (:title, :duration_minutes, :age_limit, :logo_url)
RETURNING id

-- :name get-films :? :*
-- :doc retrieve all films.
SELECT * FROM films

-- :name delete-film! :! :n
DELETE FROM films WHERE id = :id

-- :name get-film-by-id :? :1
SELECT * FROM films WHERE id = :film_id

-- :name add-films-genres! :! :n
-- :doc creates a new films genres records
INSERT INTO films_genres
(film_id, genre_id)
VALUES :t*:films_genres

-- :name get-films-genres :? :*
-- :doc retrieve films genres.
SELECT genres.id, genres.name FROM films_genres, genres WHERE
  films_genres.genre_id = genres.id AND film_id = :film_id

-- :name create-auditorium-parameter! :<! :1
-- :doc creates a new genre record
INSERT INTO auditorium_parameters
(name)
VALUES (:name) RETURNING id

-- :name get-all-auditorium-parameters :? :*
-- :doc retrieve films genres.
SELECT * FROM auditorium_parameters

-- :name create-auditorium! :<! :1
-- :doc creates a new auditorium
INSERT INTO auditoriums
(name)
VALUES (:name) RETURNING id

-- :name get-auditorium-by-id :? :1
SELECT * FROM auditoriums WHERE id = :auditorium_id

-- :name create-auditorium-parameters! :! :n
-- :doc creates a new auditorium parameters records
INSERT INTO auditoriums_auditorium_parameters
(auditorium_id, auditorium_parameter_is)
VALUES :tuple*:auditorium_parameters

-- :name get-auditorium-parameters :? :*
SELECT * FROM auditoriums_auditorium_parameters WHERE auditorium_id = :auditorium_id

-- :name add-session! :<! :1
-- :doc creates a new session
INSERT INTO sessions
(auditorium_id, film_id, begin_time)
VALUES (:auditorium_id, :film_id, :begin_time) RETURNING id

-- :name delete-session! :! :n
DELETE FROM sessions WHERE id = :id

-- :name get-sessions :? :*
-- :doc retrieve sessions.
SELECT * FROM sessions

-- :name get-sessions-by-film :? :*
-- :doc retrieve sessions by film.
SELECT * FROM sessions WHERE film_id = :film_id

-- :name get-session-by-id :? :1
SELECT * FROM sessions WHERE id = :id

-- :name create-seat-type! :<! :1
-- :doc creates a new seat
INSERT INTO seat_type
(name, cost)
VALUES (:name, :cost) RETURNING id

-- :name get-seat-types :? :*
-- :doc retrieve seat_type.
SELECT * FROM seat_type

-- :name create-seat! :<! :1
-- :doc creates a new seat
INSERT INTO seats
(seat_type_id, seat_raw, seat_number, auditorium_id)
VALUES (:seat_type_id, :seat_raw, :seat_number, :auditorium_id) RETURNING id

-- :name get_seats_by_auditorium :? :*
-- :doc retrieve sessions by film.
SELECT * FROM seats WHERE auditorium_id = :auditorium_id

-- :name update-seats! :! :n
UPDATE sessions_seats
SET user_id = :user_id,
seat_status = :seat_status
WHERE session_id = :session_id AND seat_id = :seat_id

-- :name get-seats-by-ids :? :*
SELECT * FROM seats WHERE seat_id IN (:v*:seats_ids)

-- :name get-sessions-seats :? :*
SELECT sessions_seats.session_id,
sessions_seats.seat_id, sessions_seats.seat_status,
seats.seat_raw, seats.seat_number,
seat_type.name AS seat_type, seat_type.cost AS seat_cost
FROM sessions_seats, seats, seat_type
WHERE sessions_seats.seat_id = seats.id
AND seats.seat_type_id = seat_type.id
AND sessions_seats.session_id = :session_id

-- :name get-sessions-seat :? :1
SELECT sessions_seats.session_id, sessions_seats.user_id,
sessions_seats.seat_id, sessions_seats.seat_status,
seats.seat_raw, seats.seat_number,
seat_type.name AS seat_type, seat_type.cost AS seat_cost
FROM sessions_seats, seats, seat_type
WHERE sessions_seats.seat_id = seats.id
AND seats.seat_type_id = seat_type.id
AND sessions_seats.session_id = :session_id
AND sessions_seats.seat_id = :seat_id
