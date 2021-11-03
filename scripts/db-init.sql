-- Execute (copy and paste) as the "postgres" user (or a superuser), ie `docker-compose exec -u postgres postgres psql`
-- Remember to change to match what you have in the `db.properties` config!

DO
$do$
    BEGIN
        IF NOT exists(SELECT
                      FROM pg_catalog.pg_roles
                      WHERE rolname = 'ballbot')
        THEN CREATE USER ballbot WITH ENCRYPTED PASSWORD 'toor';
        END IF;
    END
$do$;

CREATE DATABASE ballbot_db OWNER ballbot;

\c ballbot;
CREATE SCHEMA IF NOT EXISTS ballbot_schema AUTHORIZATION ballbot;

REVOKE ALL ON SCHEMA public FROM PUBLIC;
