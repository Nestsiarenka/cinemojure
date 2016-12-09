CREATE TYPE seat_status AS ENUM ('free', 'incart', 'booked', 'blocked');

;--

CREATE TABLE sessions_seats
(session_id INTEGER NOT NULL REFERENCES sessions ON DELETE CASCADE,
 seat_id INTEGER NOT NULL REFERENCES seats,
 user_id INTEGER REFERENCES users,
 seat_status seat_status default 'free',
 PRIMARY KEY (session_id, seat_id));

;--
CREATE FUNCTION make_sessions_seats()
       RETURNS trigger AS $make_sessions_seats$
BEGIN
        INSERT INTO sessions_seats (session_id, seat_id)
        SELECT sessions.id, seats.id FROM sessions, auditoriums,
                seats WHERE sessions.auditorium_id = auditoriums.id
                        AND seats.auditorium_id = auditoriums.id
                        AND sessions.id = NEW.id;
        RETURN NEW;
END;
$make_sessions_seats$ LANGUAGE plpgsql;
;--
CREATE TRIGGER add_seates_for_session
AFTER INSERT ON sessions
      FOR EACH ROW EXECUTE PROCEDURE make_sessions_seats();
;--

INSERT INTO sessions (auditorium_id, film_id, begin_time)
VALUES (1, 1, TIMESTAMP '1999-01-08 04:05:05');
