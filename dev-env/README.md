# Local Development Environment

## Where's what

* **db_init** - Database initialization scripts (called from Docker Compose)
* **dialob-boot-config** - Spring boot configuration files for Dialob API service
* **dialob-session-config** - Spring boot configuration files for Dialob Session service
* **docker-compose.yaml** - Docker Compose configuration for the development environment
* **installForm.sh** - Script for updating/installing forms to dev environment database

## Setup

### Preparation
Ensure that docker has enough memory, at least 3GB is needed to run profiling and dialob applications.

#### Build java projects

In project's root directory

```shell
./mvnw clean install
```

## Build local container images

```bash
docker compose build
```

## Start environment

Start the environment:
```bash
docker compose up
```

This will start dialob database, reverse proxy, backend and session services. Note that we use `docker compose`, not `docker-compose`!

## Database

On first startup, following databases are created using init scripts in `db_init/postgresql`:

| Database   | Username | Password  | Purpose           |
|------------|----------|-----------|-------------------|
| **dialob** | dialob   | dialob123 | Dialob database   |

Postgres service is exposed on `localhost:5433`

```shell
PGPASSWORD=dialob123 psql -h localhost -p 5433 -U dialob -d dialob
```

### Dialob 

Dialob Admin UI: http://localhost:8080/dialob/
Dialob API: http://localhost:8080/dialob/api/
Dialob Session API: http://localhost:8080/session/

Due to UI customization configuration peculiarities, project-specific Composer and Filling UI needs to be started separately and links from Admin UI should not be used. 
(Opening composer from Admin UI will open built-in "vanilla" composer, **not** the live development environment for the customized version).

## Development tasks

### Logging configuration

Changes to logging configuration are automatically updated every 10 seconds. 

 - `/dialob-session-config/logback.xml`
 - `/dialob-boot-config/logback.xml`

### Debugging

`dialob-api` opens jwdp on port 5085 and `dialob-session` port 5085. You can connect java remote debugger on these ports. 

### Open form in composer

Accessing forms, use URLs like: **http://localhost:3000/?id=<formName>**

### Updating / Installing forms

**Warning!** Following procedure will overwrite forms in database!

- Start docker-compose bundle
- run `./intallForm.sh formfile.json` to update or install given form into database
