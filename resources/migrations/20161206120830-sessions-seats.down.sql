DROP TABLE sessions_seats;

;--

DROP TYPE seat_status;

;--

DROP TRIGGER add_seates_for_session ON sessions;

;--
DROP FUNCTION make_sessions_seats();
