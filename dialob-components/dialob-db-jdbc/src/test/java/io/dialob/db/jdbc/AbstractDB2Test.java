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
import org.junit.jupiter.api.BeforeAll;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;
import org.testcontainers.containers.Db2Container;
import org.testcontainers.junit.jupiter.Container;

import java.io.IOException;
import java.time.Duration;
import java.util.Optional;


public interface AbstractDB2Test extends JdbcBackendTest {

  String SCHEMA = "db2inst1";
  String DATABASE = "dialob";
  String PASSWORD = "db2inst1";
  String USERNAME = "db2inst1";

  @Container
  Db2Container container = new Db2Container("icr.io/db2_community/db2:11.5.0.0a")
    .acceptLicense()
    .withDatabaseName(DATABASE)
    .withUsername(USERNAME)
    .withPassword(PASSWORD)
    .withStartupTimeout(Duration.ofMinutes(15));

  class Attrs {

    BasicDataSource dataSource;

    TransactionTemplate transactionTemplate;

    JdbcTemplate jdbcTemplate;

    ObjectMapper objectMapper;

    JdbcFormDatabase jdbcFormDatabase;

    CurrentTenant currentTenant;

    Tenant activeTenant;

  }

  Attrs ATTRS = new Attrs();

  static BasicDataSource createEmbeddedDatabase() throws IOException {

    String jdbcUrl = container.getJdbcUrl();
    System.out.println("DB2 jdbc url: " + jdbcUrl);

    // Point it to the database
    ATTRS.dataSource = new BasicDataSource();
    ATTRS.dataSource.setUsername(USERNAME);
    ATTRS.dataSource.setPassword(PASSWORD);
    ATTRS.dataSource.setUrl(jdbcUrl);

    return ATTRS.dataSource;
  }


  @BeforeAll
  static void startDb() throws Exception {
    ATTRS.dataSource = createEmbeddedDatabase();


    Flyway flyway = Flyway.configure()
      .locations("db/migration_db2","db/migration")
      .dataSource(ATTRS.dataSource)
      .load();
    // Start the migration_db2
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
    ATTRS.jdbcFormDatabase = new JdbcFormDatabase(ATTRS.jdbcTemplate, new DB2DatabaseHelper(SCHEMA, null), ATTRS.transactionTemplate, ATTRS.objectMapper, SCHEMA, IS_ANY_TENANT_PREDICATE);
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


  default FormVersionControlDatabase getJdbcFormVersionControlDatabase() {
    return new JdbcVersionControlledFormDatabase(getJdbcTemplate(), SCHEMA, new DB2DatabaseHelper(SCHEMA, null), getTransactionTemplate(), getJdbcFormDatabase(), IS_ANY_TENANT_PREDICATE, objectMapper);
  }

  default QuestionnaireDatabase getQuestionnaireDatabase() {
    return new JdbcQuestionnaireDatabase(getJdbcTemplate(), new DB2DatabaseHelper(SCHEMA, null), getTransactionTemplate(), objectMapper, SCHEMA, Optional.of(getJdbcFormVersionControlDatabase()), IS_ANY_TENANT_PREDICATE);
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
