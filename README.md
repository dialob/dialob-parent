Dialob backend services
=======================

## Where's what

* **dev-env** - Local development docker-compose configuration for front end development 
* **compose** - Local development docker-compose configuration (obsolete)
* **dialob-bom** - Internal Maven dependency definitions
* **dialob-boot** - Spring boot application for Dialob Backend API
* **dialob-build-parent** - Build scripts
* **dialob-components** - Component modules
  * **dialob-cache** - Configurations for different caches (Session, Program, Questionnaire, Form)
  * **dialob-cloud-aws** - AWS support: PostSubmitHandler to S3, publish questionnaire and form events to AWS SNS
  * **dialob-cloud-gcp** - GCP support: Publish questionnaire and form events to GCP PubSub
  * **dialob-core** - (no function, obsolete)
  * **dialob-db-assets** - Form persistence implementation: Wrench asset service (obsolete)
  * **dialob-db-dialob-api** - Form and questionnaire persistence implementation: Use external Dialob API as persistence (obsolete?)
  * **dialob-db-file** - Form and questionnaire persistence implementation: Filesystem
  * **dialob-db-jdbc** - Form and questionnaire persistence implementation: JDBC, database schema definitions and Flyway migrations for MySql and Postgres
  * **dialob-db-mongo** - Form and questionnaire persistence implementation: MongoDb
  * **dialob-db-s3** - Form and questionnaire persistence implementation: AWS S3 storage
  * **dialob-db-sp-spring** - Spring Boot autoconfiguration for persitence implemenations
  * **dialob-db-spi** - SPI types for persistence implementations
  * **dialob-demo-functions** - (obsolete)
  * **dialob-form-service** - Form service implementations for Composer (Item renamer, Item copier)
  * **dialob-form-service-api** - Form service API types
  * **dialob-form-service-rest** - Form REST service implementation
  * **dialob-function** - Form expression language function registry implementation
  * **dialob-groovy-functions** - Form expression language function registry for Groovy
  * **dialob-integration-api** - Dialob component integration API types
  * **dialob-integration-queue** - Dialob component integration implementation for Redis
  * **dialob-integration-redis** - Dialob component integration implementation for Redis
  * **dialob-questionnaire-service** - Questionnaire service implementation, PostSubmitHandler
  * **dialob-questionnaire-service-api** - Questionnaire service API types
  * **dialob-questionnaire-service-rest** - Questionnaire REST service implementation
  * **dialob-questionnaire-service-sockjs** - Questionnaire SockJS (websocket) implementation (obsolete)
  * **dialob-rest** - REST service autoconfiguration, Exception types
  * **dialob-rule-parser** - Dialob Expression Language parser (Antlr)
  * **dialob-service-common** - Some common configuration and exception types for services
  * **dialob-session-engine** - Dialob session engine 
  * **dialob-session-rest** - Dialob session REST service implementation
  * **dialob-springdoc** - Springdoc configuration
  * **dialob-tenant** - Tenant service autoconfiguration
  * **dialob-tenant-service-rest** - Tenant REST service implementation
* **dialob-config-parent** - Internal configuration
  * **dialob-common** - Constant definitions
  * **dialob-settings** - Default application settings (spring profile configurations)
* **dialob-security-parent** - Security implementations
  * **dialob-security** - Common security types 
  * **dialob-security-aws** - AWS ELB Authentication implementation
  * **dialob-security-spring** - Spring security implementation (APIKey, OAuth2)
  * **dialob-security-uaa** - UAA security implementation
* **dialob-session-boot** - Spring boot application for Dialob Session API (Form filling backend)
* **dialob-spring-boot** - Spring boot autoconfigurations
* **docs** - Additional documentation
* **utils** - utility scripts
  * **apikeygen.js** -- API key generation script
  * **upgradeForm.js** -- Form document upgrade script from Dialob 0.x to 2.x
* **frontend** - Frontend (filling, composer) libraries and applications
  * **dialob-fill-api** - Dialob filling API (typings and state management for filling) **Published to npmjs.com as @dialob/fill-api**
  * **dialob-fill-react** - React hooks and context for Dialob filling **Published to npmjs.com as @dialob/fill-react**
  * **dialob-fill-demo-material** - Demo Dialob filling application on MUI 5 (used in demo.dialob.io)
  * **dialob-fill-material** - Dialob filling component library implementation on top of MUI 5  **Published to npmjs.com as @dialob/fill-material**
  * **dialob-review-material** - Dialob filled form review renderer on MUI 5 **Published internally as @resys/dialob-review-material**
  * **dialob-composer-semantic** - Dialob Composer component library on Semantic UI **Published internally as @resys/dialob-composer**
  * **dialob-composer-semantic-app** - Dialob Composer reference application on Semantic UI (used in demo.dialob.io)
  * **dialob-composer-material** - Dialob Composer component library on Material UI

## Supported spring profiles

For default configurations, see YML-s in **dialob-config-parent/dialob-settings**

* **aws** - AWS environment: Enable AWS ELB authentication
* **dialobapidb** - Form and questionnaire persistence - Dialob API
* **elasticsearch** - (deprecated, not in use)
* **filedb** - Form and questionnaire persistence - Filesystem
* **jdbc** - Form and questionnaire persistence - JDBC
* **mongodb** - Form and questionnaire persistence - MongoDb
* **uaa** - Security implementation UAA
* **ui** - Enable UI components (enabled by default)

## Building tasks

### Updating license headers

```shell
mvn license:format -P update-license-headers
```

### Build scripts

Build scripts are executed by GitHub actions

* **dialob-build-parent/release.sh** - Executed on `main` branch for building new maven release version. Triggered by push to `main`. Deploys to maven central.

* **dialob-build-parent/docker.sh** - Builds docker images for **dialob-boot** and **dialob-session-boot** for latest release version and deploys them to Docker Hub.

## Docker images

### Dialob API service
Implements tenant, form and questionnaire REST services (**dialob-boot**)

```shell
docker pull resys/dialob-boot:latest
```

See [docker image readme](docs/dialob-boot-docker.md)

See [Dockerfile](dialob-boot/Dockerfile)

### Dialob Session service
Implements REST service for form filling sessions (**dialob-session-boot**)

```shell
docker pull resys/doalob-boot:latest
```

See [docker image readme](docs/dialob-session-boot-docker.md)

See [Dockerfile](dialob-session-boot/Dockerfile)

## Additional documentation

- [Using S3 data storage](docs/S3.md)
- [CSV data query api](docs/csv-api.md)


### Migrated repositories for monorepo

* https://gitlab.com/dialob/dialob-fill-api (master) migrated to frontend/dialob-fill-api
* https://gitlab.com/dialob/dialob-fill-react (master) migrated to frontend/dialob-fill-react
* https://gitlab.com/dialob/dialob-fill-demo-material (main) migrated to frontend/dialob-fill-demo-material
* https://git.resys.io/dialob/dialob-fill-material (mui-next5) migrated to frontend/dialob-fill-material
* https://git.resys.io/dialob/dialob-review-material (master) migrated to frontend/dialob-review-material
* https://git.resys.io/dialob/dialob-composer-v2 (master) migrated to frontend/dialob-composer-semantic
* https://git.resys.io/dialob/dialob-composer-generic-app (master) migrated to frontend/dialob-composer-semantic-app

---

https://dialob.io

### Running DB2 tests

```shell
mvn clean test -D"groups=db2" -D"excludedGroups=postgresql,mysql"
```

### Running locally

1. Run local database server

```shell
docker run --name dialob-postgres -p 5432:5432 -e POSTGRES_PASSWORD=password123 postgres
```
