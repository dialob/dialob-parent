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
package io.dialob.db.jdbi;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.dialob.api.form.Form;
import io.dialob.api.form.FormTag;
import io.dialob.db.jdbc.DatabaseHelper;
import io.dialob.db.jdbc.PostgreSQLDatabaseHelper;
import io.dialob.db.jdbi.model.FormDocument;
import io.dialob.db.jdbi.model.FormRev;
import io.dialob.db.jdbi.model.Questionnaire;
import org.assertj.core.api.Assertions;
import org.jdbi.v3.core.spi.JdbiPlugin;
import org.jdbi.v3.core.statement.UnableToExecuteStatementException;
import org.jdbi.v3.postgres.PostgresPlugin;
import org.jdbi.v3.spring.EnableJdbiRepositories;
import org.jdbi.v3.spring.JdbiFactoryBean;
import org.jdbi.v3.sqlobject.SqlObjectPlugin;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.postgresql.util.PSQLException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.time.Instant;
import java.util.Optional;

@Tag("postgresql")
@Tag("container")
@SpringBootTest(
  properties = {
    "spring.datasource.driver-class-name=org.testcontainers.jdbc.ContainerDatabaseDriver",
    "spring.datasource.url=jdbc:tc:postgresql:17:///testdatabase",
    "spring.flyway.locations=classpath:db/migration,classpath:db/migration_{vendor}"
  },
  classes = {
    DataSourceAutoConfiguration.class,
    FlywayAutoConfiguration.class,
    FormDocumentDocumentJdbiRepositoryTest.TestConfiguration.class,
  })
class FormDocumentDocumentJdbiRepositoryTest {

  @Configuration(proxyBeanMethods = false)
  @EnableJdbiRepositories(repositories = {
    FormDocumentJdbiRepository.class,
    FormJdbiRepository.class,
    FormRevJdbiRepository.class,
    QuestionnaireJdbiRepository.class
  })
  static class TestConfiguration {

    @Bean
    JdbiPlugin postgresPlugin() {
      return new PostgresPlugin();
    }

    @Bean
    DatabaseHelper databaseHelper() {
      return new PostgreSQLDatabaseHelper(null);
    }

    @Bean
    JdbiPlugin sqlObjectPlugin() {
      return new SqlObjectPlugin();
    }

    @Bean
    JdbiPlugin dialobPlugin(DatabaseHelper databaseHelper, ObjectMapper objectMapper) {
      return new DialobJdbiPlugin(databaseHelper, objectMapper);
    }

    @Bean
    JdbiFactoryBean jdbiFactoryBean(DataSource dataSource) {
      return new JdbiFactoryBean(dataSource);
    }

    @Bean
    ObjectMapper objectMapper() {
      return new ObjectMapper().registerModule(new JavaTimeModule());
    }

  }


  @Autowired
  FormDocumentJdbiRepository repository;

  @Autowired
  FormJdbiRepository formRepository;

  @Autowired
  FormRevJdbiRepository formRevRepository;

  @Autowired
  QuestionnaireJdbiRepository questionnaireJdbiRepository;

  @Test
  void testInsert() {
    Instant now = Instant.now();
    // Insert as a new document
    FormDocument updatedDocument = repository.insert(FormDocument.of(
      new Form.Builder()
        .metadata(new Form.Metadata.Builder()
          .label("test")
          .tenantId("t1")
          .build())
        .build()
    ));
    Assertions.assertThat(updatedDocument).isNotNull();

    var formDocumentId = updatedDocument.id();
    formRepository.insert(new io.dialob.db.jdbi.model.Form(
      io.dialob.db.jdbi.model.Form.id("t1", "formi"),
      "labeli",
      formDocumentId,
      now,
      now
    ));

    // Update existing document
    var updatedDocument2 = repository.update(updatedDocument.nextRev("tester"));
    Optional<FormDocument> formDocument = repository.findOne(formDocumentId);
    Assertions.assertThat(formDocument)
      .isPresent().isEqualTo(updatedDocument2);


    // Update existing document fails if rev does not match
    var updatedDocument3 = repository.update(updatedDocument.nextRev("tester"));
    Assertions.assertThat(updatedDocument3)
      .isNotPresent();

    Optional<io.dialob.db.jdbi.model.Form> latestForm = formRepository.findOne(io.dialob.db.jdbi.model.Form.id("t1", "formi"));
    Assertions.assertThat(latestForm)
      .isPresent();

    var newFormRev = formRevRepository.insert(new FormRev(
      new FormRev.Id("t1", "formi", "v1"),
//      new FormRev.Id("t1", "formi", "v1"),
      null,
      now,
      now,
      formDocumentId,
      "Initial version",
      FormTag.Type.MUTABLE,
      "tester"
    ));
    Assertions.assertThat(newFormRev)
      .isPresent();

    var newFormRev2 = formRevRepository.insert(new FormRev(
      new FormRev.Id("t1", "formi", "v2"),
      new FormRev.Id("t1", "formi", "v1"),
      now,
      now,
      formDocumentId,
      "Initial version",
      FormTag.Type.MUTABLE,
      "tester"
    ));
    Assertions.assertThat(newFormRev2)
      .isPresent();

    Assertions.assertThatThrownBy(() -> formRevRepository.insert(newFormRev2.get()))
      .isInstanceOf(UnableToExecuteStatementException.class)
      .cause()
      .isInstanceOf(PSQLException.class)
      .hasMessageContaining("duplicate key value violates unique constraint")
      .extracting("sQLState")
      .isEqualTo("23505");

    questionnaireJdbiRepository.insert(Questionnaire.of("dude", "t1",
      new io.dialob.api.questionnaire.Questionnaire.Builder()
        .metadata(new io.dialob.api.questionnaire.Questionnaire.Metadata.Builder()
          .tenantId("t1")
          .formId(formDocumentId.id().toString())
          .build())
        .build()));

  }

  @Test
  void test() {
    Assertions.assertThat(repository.findOne(FormDocument.id("123", null)))
      .isNotPresent();
  }

}
