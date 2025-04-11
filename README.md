Dialob Backend Services
=======================

[![Maven Central](https://img.shields.io/maven-central/v/io.dialob/dialob-db-s3.svg?label=Maven%20Central&style=for-the-badge)](https://search.maven.org/artifact/io.dialob/dialob-bom)
![Apache License, Version 2.0, January 2004](https://img.shields.io/github/license/apache/maven.svg?label=License&style=for-the-badge)
[![Sonar Quality Gate](https://img.shields.io/sonar/quality_gate/dialob_dialob-parent?server=https%3A%2F%2Fsonarcloud.io&style=for-the-badge)](https://sonarcloud.io/summary/overall?id=dialob_dialob-parent)
[![Sonar Coverage](https://img.shields.io/sonar/coverage/dialob_dialob-parent?server=https%3A%2F%2Fsonarcloud.io&style=for-the-badge)](https://sonarcloud.io/component_measures?id=dialob_dialob-parent&metric=coverage&view=list)

## Where's What

* **dev-env** - Local development Docker Compose configuration for front-end development.
* **compose** - Local development Docker Compose configuration (obsolete).
* **dialob-bom** - Internal Maven dependency definitions.
* **dialob-boot** - Spring Boot application for Dialob Backend API.
* **dialob-build-parent** - Build scripts.
* **dialob-components** - Component modules:
  * **dialob-cache** - Configurations for different caches (Session, Program, Questionnaire, Form).
  * **dialob-cloud-aws** - AWS support: PostSubmitHandler to S3, publish questionnaire and form events to AWS SNS.
  * **dialob-cloud-gcp** - GCP support: Publish questionnaire and form events to GCP Pub/Sub.
  * **dialob-core** - (No function, obsolete).
  * **dialob-db-azure-blob-storage** - Form and questionnaire persistence implementation: Azure Blob Storage.
  * **dialob-db-file** - Form and questionnaire persistence implementation: Filesystem.
  * **dialob-db-jdbc** - Form and questionnaire persistence implementation: JDBC, database schema definitions, and Flyway migrations for MySQL and PostgreSQL.
  * **dialob-db-s3** - Form and questionnaire persistence implementation: AWS S3 storage.
  * **dialob-db-sp-spring** - Spring Boot auto-configuration for persistence implementations.
  * **dialob-db-spi** - SPI types for persistence implementations.
  * **dialob-demo-functions** - (Obsolete).
  * **dialob-form-service** - Form service implementations for Composer (Item renamer, Item copier).
  * **dialob-form-service-api** - Form service API types.
  * **dialob-form-service-rest** - Form REST service implementation.
  * **dialob-function** - Form expression language function registry implementation.
  * **dialob-integration-api** - Dialob component integration API types.
  * **dialob-integration-queue** - Dialob component integration implementation for Redis.
  * **dialob-integration-redis** - Dialob component integration implementation for Redis.
  * **dialob-questionnaire-service** - Questionnaire service implementation, PostSubmitHandler.
  * **dialob-questionnaire-service-api** - Questionnaire service API types.
  * **dialob-questionnaire-service-rest** - Questionnaire REST service implementation.
  * **dialob-questionnaire-service-sockjs** - Questionnaire SockJS (WebSocket) implementation (obsolete).
  * **dialob-rest** - REST service auto-configuration, exception types.
  * **dialob-rule-parser** - Dialob Expression Language parser (ANTLR).
  * **dialob-service-common** - Common configuration and exception types for services.
  * **dialob-session-engine** - Dialob session engine.
  * **dialob-session-rest** - Dialob session REST service implementation.
  * **dialob-springdoc** - Springdoc configuration.
  * **dialob-tenant** - Tenant service auto-configuration.
  * **dialob-tenant-service-rest** - Tenant REST service implementation.
* **dialob-config-parent** - Internal configuration:
  * **dialob-common** - Constant definitions.
  * **dialob-settings** - Default application settings (Spring profile configurations).
* **dialob-security-parent** - Security implementations:
  * **dialob-security** - Common security types.
  * **dialob-security-aws** - AWS ELB authentication implementation.
  * **dialob-security-spring** - Spring Security implementation (API Key, OAuth2).
* **dialob-session-boot** - Spring Boot application for Dialob Session API (Form filling backend).
* **dialob-spring-boot** - Spring Boot auto-configurations.
* **docs** - Additional documentation.
* **utils** - Utility scripts:
  * **apikeygen.js** - API key generation script.
  * **upgradeForm.js** - Form document upgrade script from Dialob 0.x to 2.x.
* **frontend** - Frontend (filling, composer) libraries and applications:
  * **demo-dialob-io-app**.
  * **dialob-fill-api** - Dialob filling API (typings and state management for filling). **Published to npmjs.com as [@dialob/fill-api](https://www.npmjs.com/package/@dialob/fill-api)** ![version](https://img.shields.io/npm/v/%40dialob%2Ffill-api).
  * **dialob-fill-react** - React hooks and context for Dialob filling. **Published to npmjs.com as [@dialob/fill-react](https://www.npmjs.com/package/@dialob/fill-react)** ![version](https://img.shields.io/npm/v/%40dialob%2Ffill-react).
  * **dialob-fill-demo-material** - Demo Dialob filling application on MUI 5 (used in demo.dialob.io).
  * **dialob-fill-material** - Dialob filling component library implementation on top of MUI 5. **Published to npmjs.com as [@dialob/fill-material](https://www.npmjs.com/package/@dialob/fill-material)** ![version](https://img.shields.io/npm/v/%40dialob%2Ffill-material).
  * **dialob-review-material** - Dialob filled form review renderer on MUI 5. **Published internally as @dialob/dialob-review-material**.
  * **dialob-composer-semantic** - Dialob Composer component library on Semantic UI. **Published internally as @dialob/dialob-composer**.
  * **dialob-composer-semantic-app** - Dialob Composer reference application on Semantic UI (used in demo.dialob.io).
  * **dialob-composer-material** - Dialob Composer component library on Material UI.
  * **dialob-composer-material-custom-app**.
  * **dialob-dashboard-material** - Dialob dashboard component library on Material UI. **Published to npmjs.com as [@dialob/dashboard-material](https://www.npmjs.com/package/@dialob/dashboard-material)** ![version](https://img.shields.io/npm/v/%40dialob%2Fdashboard-material).

## Supported Spring Profiles

For default configurations, see YML files in **dialob-config-parent/dialob-settings**.

* **jdbc** - Form and questionnaire persistence - JDBC (enabled by default).
* **ui** - Enable UI components (enabled by default).
* **aws** - AWS environment: Enable AWS ELB authentication.
* **filedb** - Form and questionnaire persistence - Filesystem.
* **oauth2** - Enable OAuth2 authentication.

## Building Tasks

### Updating License Headers

```shell
mvn license:format -P update-license-headers
```

### Build Scripts

Build scripts are executed by GitHub Actions:

* **dialob-build-parent/release.sh** - Executed on the `main` branch for building new Maven release versions. Triggered by a push to `main`. Deploys to Maven Central.
* **dialob-build-parent/docker.sh** - Builds Docker images for **dialob-boot** and **dialob-session-boot** for the latest release version and deploys them to Docker Hub.

## Docker Images

### Dialob API Service

Implements tenant, form, and questionnaire REST services (**dialob-boot**).

```shell
docker pull resys/dialob-boot:latest
```

See [Docker image README](docs/dialob-boot-docker.md).

See [Dockerfile](dialob-boot/Dockerfile).

### Dialob Session Service

Implements REST service for form filling sessions (**dialob-session-boot**).

```shell
docker pull resys/dialob-session-boot:latest
```

See [Docker image README](docs/dialob-session-boot-docker.md).

See [Dockerfile](dialob-session-boot/Dockerfile).

## Additional Documentation

- [Using S3 Data Storage](docs/S3.md)
- [CSV Data Query API](docs/csv-api.md)

## Local Development

1. Build everything:

```shell
./mvnw clean install
```

2. Start a local database server:

```shell
docker run --name dialob-postgres -p 5432:5432 -e POSTGRES_PASSWORD=password123 postgres
```

3. Start local services:

```shell
cd dialob-boot
mvn spring-boot:run
```

```shell
cd dialob-session-boot
mvn spring-boot:run
```

Open the URL: [http://localhost:8081/](http://localhost:8081/)

## Testing

### Running DB2 Tests

DB2 container startup is very slow, and if you are using an ARM-based computer, it will be even slower. IBM does not provide an ARM build of DB2.

```shell
mvn clean test -D"groups=db2" -D"excludedGroups=postgresql,mysql"
```

## Release

### Backend

Merge release version to `main` branch. Github pipeline will execute release and publish artifacts to maven central.

### Frontend

Frontend projects are published using [Changesets](https://github.com/changesets/changesets). 

#### Making a release

This is for maintainer, who has commit right to `dev` branch:

1. Use command `pnpm changeset` to select component(s) for a release. 
2. Commit and push *changeset* to git.
3. Github action will create pull request for a release. After merging changeset into dev branch github action does actual release.

> [!IMPORTANT]
> If there is a peer dependency to project to be released, changeset will trigger major update on dependand project. See more details [here](https://github.com/changesets/changesets/issues/1011). We should avoid making unnecessary major releases.

> [!NOTE]
> `patch` release does not trigger internal releases. Only patched packages will be released. 

#### Release steps dissected 

- `pnpm changeset` 
  Creates changeset request file under folder `.changeset`
- `pnpm changeset version` 
  updates `package.json` and changelogs based on request in. `.changeset`. Removes request file created in previous step.
- `pnpm install --lockfile-only` 
  Updates pnpm lockfile to reflect changes from version bump.

#### Frontend library release process step-by-step

This is for developer who do not have rights to directly commit to `dev` branch.

In case a release of a frontend library is needed to be published to npmjs.com follow these steps.

- Create a local branch of the repository for the release
- `pnpm changeset && pnpm changeset version && pnpm install --lockfile-only`
- On prompt **Which packages would you like to include?** Select the libraries to be released (use space to select, enter to confirm)
- On prompt **Which packages should have a major bump?** Select the libraries that need major version (**major**.minor.patch) to be increased or press enter to select none
- On prompt **Which packages should have a minor bump?** Select the libraries that need minor version (major.**minor**.patch) to be increased or press enter to select none
- Libraries that were not selected in previous last steps will get their patch version (major.minor.**patch**) increased, confirm the proposed version numbers
- On prompt **Please enter a summary for this change (this will be in the changelogs).** Provide a summary text describing the relase for changelog file
- Review and commit the resulting changes
- Create a pull request for the branch and assign it to a maintainer for a review
- You can modify the version numbers / changelog text in the files if adjustment is needed. Run `pnpm install --lockfile-only` to update the lockfile if you modified any version numbers by hand.
- After the maintainer has merged the branch, CI/CD pipeline will build the library release and deploy it to npmjs.com

> [!IMPORTANT]
> If you release dialob/fill-api and @dialob/fill-material has peerdep to it. changeset will bump major version on @dialob/fill-material. To avoid this you need to manually revert change back and rub `pnpm install --lockfile-only` before committing the changeset PR.

#### Frontend application deployment to demo.dialob.io

Frontend applications under dialob-parent repository that depend on any library will be built automatically by the CI/CD pipeline on changes in the application or library in the dev branch and artifacts will be uploaded into the CDN as follows:

- `https://cdn.resys.io/dialob/demo-dialob-io-app/dev/`
- `https://cdn.resys.io/dialob/dialob-admin-ui/dev/`
- `https://cdn.resys.io/dialob/dialob-composer-custom/dev/`
- `https://cdn.resys.io/dialob/dialob-composer-generic-app/dev/`
- `https://cdn.resys.io/dialob/dialob-composer-material/dev/`
- `https://cdn.resys.io/dialob/dialob-fill-demo-material/dev/`
- `https://cdn.resys.io/dialob/dialob-review-ui-app/dev/`

demo.dialob.io dialob-api container needs restarting to make the changes visible in the site.

  
