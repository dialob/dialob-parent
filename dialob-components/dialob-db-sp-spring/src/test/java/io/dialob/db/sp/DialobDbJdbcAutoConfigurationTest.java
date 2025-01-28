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
package io.dialob.db.sp;

import com.azure.storage.blob.BlobServiceClient;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.dialob.db.azure.blob.storage.FormAzureBlobStorageDatabase;
import io.dialob.db.file.FormFileDatabase;
import io.dialob.db.file.QuestionnaireFileDatabase;
import io.dialob.db.jdbc.DropQuestionnaireToFormDocumentConstraint;
import io.dialob.db.jdbc.JdbcQuestionnaireDatabase;
import io.dialob.db.s3.FormS3Database;
import io.dialob.form.service.api.FormDatabase;
import io.dialob.form.service.api.FormVersionControlDatabase;
import io.dialob.questionnaire.service.api.QuestionnaireDatabase;
import io.dialob.security.tenant.CurrentTenant;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.assertj.ApplicationContextAssert;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;
import software.amazon.awssdk.services.s3.S3Client;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class DialobDbJdbcAutoConfigurationTest {

  @Configuration(proxyBeanMethods = false)
  public static class MockConfigurations {

    @Bean
    public DataSource dataSource() throws Exception {
      final DataSource dataSource = mock(DataSource.class);
      Connection connection = mock(Connection.class);
      when(dataSource.getConnection()).thenReturn(connection);
      DatabaseMetaData databaseMetaData = mock(DatabaseMetaData.class);
      when(databaseMetaData.getDatabaseProductName()).thenReturn("MySQL");
      when(connection.getMetaData()).thenReturn(databaseMetaData);
      return dataSource;
    }

    @Bean
    public S3Client s3Client() throws Exception {
      return mock(S3Client.class);
    }

    @Bean
    public JdbcTemplate jdbcTemplate(DataSource dataSource) throws Exception {
      final JdbcTemplate jdbcTemplate = mock(JdbcTemplate.class);
      when(jdbcTemplate.getDataSource()).thenReturn(dataSource);
      return jdbcTemplate;
    }

    @Bean
    public ObjectMapper objectMapper() {
      return new ObjectMapper();
    }

    @Bean
    public CurrentTenant currentTenant() {
      final CurrentTenant currentTenant = mock(CurrentTenant.class);
      when(currentTenant.getId()).thenReturn("test");
      return currentTenant;
    }

    @Bean
    BlobServiceClient blobServiceClient() {
      return mock(BlobServiceClient.class);
    }
  }

  @Test
  void testDialobDbJdbcAutoConfigurationTypeJDBC() {
    new ApplicationContextRunner()
      .withPropertyValues(
        "dialob.db.database-type=JDBC",
        "dialob.db.jdbc.schema=dialob"
      )
      .withUserConfiguration(MockConfigurations.class)
      .withConfiguration(AutoConfigurations.of(DialobDbSpAutoConfiguration.class, DialobDbJdbcAutoConfiguration.class))
      .run(context -> {
        Assertions.assertThat(context)
          .hasSingleBean(FormDatabase.class)
          .hasSingleBean(FormVersionControlDatabase.class)
          .hasSingleBean(QuestionnaireDatabase.class)
          .hasSingleBean(PlatformTransactionManager.class)
          .hasSingleBean(TransactionTemplate.class)
          .doesNotHaveBean(DropQuestionnaireToFormDocumentConstraint.class);

        Assertions.assertThat(context)
          .getBean(DialobDbSpAutoConfiguration.DialobDbJdbcAutoConfiguration.class).hasFieldOrPropertyWithValue("schema", "dialob");
      });
  }

  @Test
  void testDialobDbJdbcAutoConfigurationTypeFILEDB() {
    new ApplicationContextRunner()
      .withPropertyValues("dialob.db.database-type=FILEDB", "dialob.db.file.directory=src")
      .withUserConfiguration(MockConfigurations.class)
      .withConfiguration(AutoConfigurations.of(DialobDbSpAutoConfiguration.class, DialobDbJdbcAutoConfiguration.class))
      .run(context -> {
        Assertions.assertThat(context)
          .hasSingleBean(FormDatabase.class)
          .hasSingleBean(QuestionnaireDatabase.class)
          .doesNotHaveBean(DropQuestionnaireToFormDocumentConstraint.class);
        Assertions.assertThat(context).getBean(FormDatabase.class).isInstanceOf(FormFileDatabase.class);
        Assertions.assertThat(context).getBean(QuestionnaireDatabase.class).isInstanceOf(QuestionnaireFileDatabase.class);
      });
  }


  @Test
  void testDialobDbJdbcAutoConfigurationFormsFILEDBAndQuestoinnairesOnJDBC() {
    new ApplicationContextRunner()
      .withPropertyValues(
        "dialob.db.database-type=NONE",
        "dialob.formDatabase.database-type=FILEDB",
        "dialob.questionnaireDatabase.database-type=JDBC",
        "dialob.db.file.directory=src",
        "dialob.db.jdbc.schema=filetest"
      )
      .withUserConfiguration(MockConfigurations.class)
      .withConfiguration(AutoConfigurations.of(DialobDbSpAutoConfiguration.class, DialobDbJdbcAutoConfiguration.class))
      .run(context -> {
        ApplicationContextAssert contextAssert = Assertions.assertThat(context)
          .hasSingleBean(FormDatabase.class)
          .hasSingleBean(QuestionnaireDatabase.class)
          .hasSingleBean(PlatformTransactionManager.class)
          .hasSingleBean(TransactionTemplate.class)
          .hasSingleBean(DropQuestionnaireToFormDocumentConstraint.class);
        ;

        Assertions.assertThat(context).getBean(FormDatabase.class).isInstanceOf(FormFileDatabase.class);
        Assertions.assertThat(context).getBean(QuestionnaireDatabase.class).isInstanceOf(JdbcQuestionnaireDatabase.class);

        Assertions.assertThat(context)
          .getBean(DialobDbSpAutoConfiguration.DialobDbJdbcAutoConfiguration.class).hasFieldOrPropertyWithValue("schema", "filetest");
      });
  }


  @Test
  void testDialobDbAutoConfigurationFormsS3AndQuestoinnairesOnJDBC() {
    new ApplicationContextRunner()
      .withPropertyValues(
        "dialob.db.database-type=NONE",
        "dialob.formDatabase.database-type=S3",
        "dialob.formDatabase.s3.bucket=c-bucket",
        "dialob.formDatabase.s3.prefix=forms/",
        "dialob.questionnaireDatabase.s3.bucket=questionnaire-database-bucket",
        "dialob.questionnaireDatabase.s3.prefix=questionnaires/",
        "dialob.questionnaireDatabase.database-type=JDBC"
      )
      .withUserConfiguration(MockConfigurations.class)
      .withConfiguration(AutoConfigurations.of(DialobDbSpAutoConfiguration.class, DialobDbJdbcAutoConfiguration.class))
      .run(context -> {
        ApplicationContextAssert contextAssert = Assertions.assertThat(context)
          .hasSingleBean(FormDatabase.class)
          .hasSingleBean(QuestionnaireDatabase.class)
          .hasSingleBean(PlatformTransactionManager.class)
          .hasSingleBean(TransactionTemplate.class)
          .hasSingleBean(DropQuestionnaireToFormDocumentConstraint.class);

        contextAssert.getBean(FormDatabase.class).isInstanceOf(FormS3Database.class);
        contextAssert.getBean(QuestionnaireDatabase.class).isInstanceOf(JdbcQuestionnaireDatabase.class);

      });
  }

  @Test
  void testDialobDbAutoConfigurationFormsAzureBSAndQuestoinnairesOnJDBC() {
    new ApplicationContextRunner()
      .withPropertyValues(
        "dialob.db.database-type=NONE",
        "dialob.formDatabase.database-type=AZURE_BLOB_STORAGE",
        "dialob.formDatabase.azure-blob-storage.container-name=c-bucket",
        "dialob.formDatabase.azure-blob-storage.prefix=forms",
        "dialob.questionnaireDatabase.database-type=JDBC"
      )
      .withUserConfiguration(MockConfigurations.class)
      .withConfiguration(AutoConfigurations.of(DialobDbSpAutoConfiguration.class, DialobDbJdbcAutoConfiguration.class))
      .run(context -> {
        ApplicationContextAssert contextAssert = Assertions.assertThat(context)
          .hasSingleBean(FormDatabase.class)
          .hasSingleBean(QuestionnaireDatabase.class)
          .hasSingleBean(PlatformTransactionManager.class)
          .hasSingleBean(TransactionTemplate.class)
          .hasSingleBean(DropQuestionnaireToFormDocumentConstraint.class);

        contextAssert.getBean(FormDatabase.class).isInstanceOf(FormAzureBlobStorageDatabase.class);
        contextAssert.getBean(QuestionnaireDatabase.class).isInstanceOf(JdbcQuestionnaireDatabase.class);

      });
  }

  @Test
  void testTestCombination1() {
    new ApplicationContextRunner()
      .withPropertyValues(
        "dialob.db.file.directory=.",
        "dialob.db.database-type=NONE",
        "dialob.formDatabase.database-type=FILEDB",
        "dialob.questionnaireDatabase.database-type=JDBC"
      )
      .withUserConfiguration(MockConfigurations.class)
      .withConfiguration(AutoConfigurations.of(DialobDbSpAutoConfiguration.class, DialobDbJdbcAutoConfiguration.class))
      .run(context -> {
        Assertions.assertThat(context)
          .hasSingleBean(DropQuestionnaireToFormDocumentConstraint.class);
      });

    new ApplicationContextRunner()
      .withPropertyValues(
        "dialob.db.file.directory=.",
        "dialob.db.database-type=NONE",
//        "dialob.formDatabase.database-type=FILEDB",
        "dialob.questionnaireDatabase.database-type=JDBC"
      )
      .withUserConfiguration(MockConfigurations.class)
      .withConfiguration(AutoConfigurations.of(DialobDbSpAutoConfiguration.class, DialobDbJdbcAutoConfiguration.class))
      .run(context -> {
        Assertions.assertThat(context)
          .hasSingleBean(DropQuestionnaireToFormDocumentConstraint.class);
      });

    new ApplicationContextRunner()
      .withPropertyValues(
        "dialob.db.file.directory=.",
        "dialob.db.database-type=NONE",
        "dialob.formDatabase.database-type=FILEDB",
        "dialob.questionnaireDatabase.database-type=FILEDB"
      )
      .withUserConfiguration(MockConfigurations.class)
      .withConfiguration(AutoConfigurations.of(DialobDbSpAutoConfiguration.class, DialobDbJdbcAutoConfiguration.class))
      .run(context -> {
        Assertions.assertThat(context)
          .doesNotHaveBean(DropQuestionnaireToFormDocumentConstraint.class);
      });


    // TODO BUG If JDBC is enabled questionnaires will always use it.
//    new ApplicationContextRunner()
//      .withPropertyValues(
//        "dialob.db.file.directory=.",
//        "dialob.db.database-type=NONE",
//        "dialob.formDatabase.database-type=JDBC",
//        "dialob.questionnaireDatabase.database-type=FILEDB"
//      )
//      .withUserConfiguration(MockConfigurations.class)
//      .withConfiguration(AutoConfigurations.of(DialobDbSpAutoConfiguration.class, DialobDbJdbcAutoConfiguration.class))
//      .run(context -> {
//        Assertions.assertThat(context)
//          .doesNotHaveBean(DropQuestionnaireToFormDocumentConstraint.class);
//      });

  }



}
