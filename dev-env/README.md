# Local Development Environment

## Where's what

* **db_data** - Postgres database files (local only), created on first run
* **db_init** - Database initialization scripts (called from Docker Compose)
* **dialob-boot-config** - Spring boot configuration files for Dialob API service
* **dialob-session-config** - Spring boot configuration files for Dialob Session service
* **docker-compose.yaml** - Docker Compose configuration for the development environment
* **installForm.sh** - Script for updating/installing forms to dev environment database

## Setup

### Preparation
Ensure that docker has enough memory, at least 3GB is needed to run profiling and dialob applications.

### Create docker networks
Check available docker networks:
```bash
docker network list
```

If this does not show **application** and **backend** networks, then create them:

```bash
docker network create application
docker network create backend
```

## Start environment

Start the environment:
```bash
docker compose up
```

This will start dialob database, backend and session services. Note that we use `docker compose`, not `docker-compose`!

## Database

On first startup, following databases are created using init scripts in `db_init/postgresql`:

| Database   | Username | Password  | Purpose           |
|------------|----------|-----------|-------------------|
| **dialob** | dialob   | dialob123 | Dialob database   |

`db_data` directory will be created to contain database files. To reset database, remote this directory (owned by root).

Postgres service is exposed on `localhost:5433`

### Dialob 

Dialob Admin UI: http://localhost:8085/dialob/
Dialob API: http://localhost:8085/dialob/api/
Dialob Session API: http://localhost:8080/dialob/api/

Due to UI customization configuration peculiarities, project-specific Composer and Filling UI needs to be started separately and links from Admin UI should not be used. 
(Opening composer from Admin UI will open built-in "vanilla" composer, **not** the live development environment for the customized version).

## Development tasks

### Open form in composer

Accessing forms, use URLs like: **http://localhost:3000/?id=<formName>**

### Updating / Installing forms

**Warning!** Following procedure will overwrite forms in database!

- Start docker-compose bundle
- run `./intallForm.sh formfile.json` to update or install given form into database
