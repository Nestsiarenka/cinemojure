CREATE TABLE users
(id SERIAL PRIMARY KEY,
 first_name VARCHAR(30) NOT NULL,
 last_name VARCHAR(30) NOT NULL,
 login VARCHAR(30) UNIQUE NOT NULL,
 email VARCHAR (30) NOT NULL,
 user_group_id SMALLINT NOT NULL REFERENCES user_groups,
 last_login TIME,
 is_active BOOLEAN DEFAULT TRUE,
 pass VARCHAR(300));

;--

CREATE UNIQUE INDEX user_email_index ON
        users (lower(email))
