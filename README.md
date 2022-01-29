# BallBot

A Kotlin Discord bot with features I/friends asked for like a reaction image/sticker command and a scheduled Twitter account feed fetcher for news feeds.

```
~~~~~~~~~~~~~~~~ EVERYTHING IS HEAVILY WIP, I TAKE NO RESPOSIBILITIES ~~~~~~~~~~~~~~~~
```

Read the ~~undocumented~~ self-documenting code and good luck!!!!!!!!

## Precursor
To run the bot or develop, you will need a `Discord` developer account with a usable bot `token` as well as a `Twitter` developer account with `consumerKey`, `consumerSecret`, `accessToken` and `accessTokenSecret` to make use of the API. Acquiring these secrets is beyond the scope of this doc.

This project attempts to stick to [Semantic Versioning](https://semver.org/) as close as possible for its release versions and commits are done according to [Conventional Commits](https://www.conventionalcommits.org/en/v1.0.0/).

## Deployment
Both `docker` and `docker-compose` are required for deployment and testing (unless you are ok with setting everything up manually). Its also recommended that the user is added to the `docker` group (able to execute docker commands without `sudo`). Setting these up is beyond the scope of this doc.

Clone the repository and work on the root where `docker-compose.yml` is located unless stated otherwise.

### Secrets and config
Secrets are simply done using a `docker` `.env` and passed to the services in the `docker-compose` as either commands or environmental variables (yes I know this isn't great). This file will need to be created where the `docker-compose.yml` is and populated as shown in the example.

Example `.env`:
```
POSTGRES_DB=postgres
POSTGRES_USER=postgres
POSTGRES_PASSWORD=postgres

DB_DRIVER=org.postgresql.Driver
DB_URL=jdbc:postgresql://postgres:5432/db
DB_USER=user
DB_PASSWORD=password
DB_SCHEMA=schema

DISCORD_TOKEN_TEST=token

TWITTER_CONSUMER_KEY=key
TWITTER_CONSUMER_SECRET=secret
TWITTER_ACCESS_TOKEN=token
TWITTER_ACCESS_TOKEN_SECRET=secret
```

You must also create an `SQL` script that will be executed by `PostgreSQL` once the database is initialised. The config file must be named `db-init.sql` and placed at `/db` from the root repository. This file is passed to the `postgres` `docker` image within the `docker-compose`.

Example `db-init.sql`:
```roomsql
DO
$do$
    BEGIN
        IF NOT exists(SELECT
                      FROM pg_catalog.pg_roles
                      WHERE rolname = 'ballbot')
        THEN CREATE USER user WITH ENCRYPTED PASSWORD 'password';
        END IF;
    END
$do$;

CREATE DATABASE db OWNER user;

\c db;
CREATE SCHEMA IF NOT EXISTS schema AUTHORIZATION user;

REVOKE ALL ON SCHEMA public FROM PUBLIC;
```
The basic idea of the setup process is the following:

* A `PostgreSQL` superuser creates the database if it doesn't already exist.
* The user is created with an encrypted password
* The schema is created with the user given authorization to use it.

Make sure that the connection and schema details match what you set in the `.env` file.

### Running

Generate the new bot application `jar` with the included `gradle` instance and included `shadowJar` plugin using `./gradlew shadowJar` from the terminal (Linux). The default path and name for the `jar` will be something like `build/libs/BallBot-1.0.0-all.jar` and that should match what's in the `docker-compose` and set as the `JAR_FILE` arg.

To run everything in the docker-compose, use `docker-compose up --force-recreate` from the terminal as it will also rebuild the bot image without wasting space on older images. This will run the `postgres` image, setup the persistent volume for the database, run `flyway` migrations (should to be at `src/main/resources/db/migration` as shown in the `docker-compose`) and finally (re)build and run the bot application service itself at the end.

## Development
To use the gradle plugin versions of `flyway` (NOT RECOMMENDED UNLESS YOU SETUP A LOCAL POSTGRES INSTANCE AS WELL) or `JOOQ` for local development, create a `db.properties` config file within the `/db/` directory with the database connection details.

Example `db.properties`:
```
DB_DRIVER=org.postgresql.Driver
DB_URL=jdbc:postgresql://localhost:5432/db
DB_USER=user
DB_PASSWORD=password
DB_SCHEMA=schema
```

The details should match what is setup in the `db-init.sql` script and its recommended that the script is used for initialising the database by copying and pasting its content to a fresh instance.

**Changes to the database should be done within new SQL migration files following the present versioning scheme.**
It is important to then:

* Import the changes into the database by using the `flyway` imagine in the `docker-compose` or running `flywayMigrate` through the project's `gradle` wrapper.
* Update the `JOOQ` generated domain objects by running the `generateJooq` through the project's `gradle` wrapper.

This was developed with Linux/WSL2.0 and using Intellij community edition.
