--
-- PostgreSQL database dump
--


-- implementation of expiry date for each session
CREATE TABLE osiam_session (
    user_id character varying(255) NOT NULL,
    session_expiry timestamp without time zone,
    client_internal_id bigint NOT NULL,
    CONSTRAINT uc_osiam_session_user_client PRIMARY KEY (user_id,client_internal_id),
    CONSTRAINT fk_osiam_session_to_osiam_client_internal_id (client_internal_id) REFERENCES osiam_client(internal_id);
);
