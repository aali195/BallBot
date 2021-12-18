#!/bin/bash
set -e

# Best to run this via tmux to keep the session running - will probably dockerise the whole thing later so this was a waste of time

APPLICATION_VERSION=${1:-"0.2.1"}
APPLICATION_PATH="build/libs/BallBot-$APPLICATION_VERSION-all.jar"

# Validation
# If compiled
if [ ! -f "$APPLICATION_PATH" ]; then
    echo "Application java file not found, compile using './gradlew shadowJar'"
    exit 1
fi
# If docker-compose is installed
if  (! type docker-compose > /dev/null); then
    echo "docker-compose command could not be found"
    exit 1
fi

# Run
# DB setup, don't forget to `docker-compose -f db/docker-compose.yml down` to take down
echo "Setting up database via docker-compose and sending to background"
docker-compose -f db/docker-compose.yml up -d
# Migrations - May run out of memory...
echo "============================================"
echo "Running migrations using flyway via gradlew (with --no-rebuild option)"
./gradlew flywayMigrate --no-rebuild
# Start up
echo "============================================"
echo "Running application"
java -jar "$APPLICATION_PATH"
