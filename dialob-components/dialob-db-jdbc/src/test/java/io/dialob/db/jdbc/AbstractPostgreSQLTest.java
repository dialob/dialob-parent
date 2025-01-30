/*
 * Copyright © 2015 - 2025 ReSys (info@dialob.io)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.dialob.db.jdbc;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.dialob.form.service.api.FormVersionControlDatabase;
import io.dialob.questionnaire.service.api.QuestionnaireDatabase;
import io.dialob.security.tenant.CurrentTenant;
import io.dialob.security.tenant.ResysSecurityConstants;
import io.dialob.security.tenant.Tenant;
import org.apache.commons.dbcp2.BasicDataSource;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

import java.sql.SQLException;
import java.time.Duration;
import java.util.Optional;

public interface AbstractPostgreSQLTest extends JdbcBackendTest {

  int PORT = 5432;

  String SCHEMA = null;

  class Attrs {
    PostgreSQLContainer container = new PostgreSQLContainer<>(DockerImageName.parse("postgres:13"))
      .withExposedPorts(PORT)
      .withStartupTimeout(Duration.ofMinutes(15))
      .withUsername("postgres")
      .withPassword("postgres");

    BasicDataSource dataSource;

    TransactionTemplate transactionTemplate;

    JdbcTemplate jdbcTemplate;

    ObjectMapper objectMapper;

    JdbcFormDatabase jdbcFormDatabase;

    CurrentTenant currentTenant;

    Tenant activeTenant;

  }

  Attrs ATTRS = new Attrs();

  static BasicDataSource createEmbeddedDatabase() throws Exception {
    ATTRS.container.start();
    String jdbcUrl = "jdbc:postgresql://" + ATTRS.container.getHost() + ":" + ATTRS.container.getFirstMappedPort() + "/postgres";

    ATTRS.dataSource = new BasicDataSource();
    ATTRS.dataSource.setUsername("postgres");
    ATTRS.dataSource.setPassword("postgres");
    ATTRS.dataSource.setUrl(jdbcUrl);

    Logger log = LoggerFactory.getLogger(AbstractPostgreSQLTest.class);

    for (int t = 0; t < 100; ++t) {
      try (var c = ATTRS.dataSource.getConnection()) {
        log.info("Connect succeeded after {} tries", t);
        break;
      } catch (SQLException e) {
        Thread.sleep(100L);
      }
    }

    return ATTRS.dataSource;
  }

  @BeforeAll
  static void startPostgreSQL() throws Exception {
    ATTRS.dataSource = createEmbeddedDatabase();

    // Point it to the database
    Flyway flyway = Flyway.configure()
      .locations("db/migration_postgresql","db/migration")
      .dataSource(ATTRS.dataSource)
      .load();

    // Start the migration_postgresql
    flyway.migrate();

    ATTRS.activeTenant = ResysSecurityConstants.DEFAULT_TENANT;

    ATTRS.transactionTemplate = new TransactionTemplate(new DataSourceTransactionManager(ATTRS.dataSource));
    ATTRS.jdbcTemplate = new JdbcTemplate(ATTRS.dataSource);
    ATTRS.objectMapper = objectMapper;
    ATTRS.currentTenant = new CurrentTenant() {
      @Override
      public Tenant get() {
        return ATTRS.activeTenant;
      }

      @Override
      public boolean isInTenantScope() {
        return true;
      }
    };
    ATTRS.jdbcFormDatabase = new JdbcFormDatabase(ATTRS.jdbcTemplate, new PostgreSQLDatabaseHelper(SCHEMA), ATTRS.transactionTemplate, ATTRS.objectMapper, SCHEMA, IS_ANY_TENANT_PREDICATE);
  }


  default JdbcFormDatabase getJdbcFormDatabase() {
    return ATTRS.jdbcFormDatabase;
  }

  default JdbcTemplate getJdbcTemplate() {
    return ATTRS.jdbcTemplate;
  }

  default TransactionTemplate getTransactionTemplate() {
    return ATTRS.transactionTemplate;
  }

  @AfterAll
  static void stopMySQL() {
    ATTRS.container.stop();
  }

  default FormVersionControlDatabase getJdbcFormVersionControlDatabase() {
    return new JdbcVersionControlledFormDatabase(getJdbcTemplate(), null, new PostgreSQLDatabaseHelper(SCHEMA), getTransactionTemplate(), getJdbcFormDatabase(), IS_ANY_TENANT_PREDICATE, objectMapper);
  }

  default QuestionnaireDatabase getQuestionnaireDatabase() {
    return new JdbcQuestionnaireDatabase(getJdbcTemplate(), new PostgreSQLDatabaseHelper(SCHEMA), getTransactionTemplate(), objectMapper, SCHEMA, Optional.of(getJdbcFormVersionControlDatabase()), IS_ANY_TENANT_PREDICATE);
  }

  default CurrentTenant getCurrentTenant() {
    return ATTRS.currentTenant;
  }

  default void setActiveTenant(String tenantId) {
    ATTRS.activeTenant = Tenant.of(tenantId);
  }

  default void resetTenant() {
    ATTRS.activeTenant = ResysSecurityConstants.DEFAULT_TENANT;
  }

}
