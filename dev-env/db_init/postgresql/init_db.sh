#!/bin/bash
set -e

psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname "$POSTGRES_DB" <<-EOSQL
    CREATE USER dialob WITH PASSWORD 'dialob123';
    CREATE DATABASE dialob;
    GRANT ALL PRIVILEGES ON DATABASE dialob TO dialob;   
    
    \c dialob dbuser
    GRANT ALL ON SCHEMA public TO dialob;
EOSQL
