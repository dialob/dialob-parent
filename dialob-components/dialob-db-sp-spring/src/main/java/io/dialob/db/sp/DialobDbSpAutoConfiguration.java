/*
 * Copyright © 2015 - 2021 ReSys (info@dialob.io)
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
import edu.umd.cs.findbugs.annotations.NonNull;
import io.dialob.db.assets.AssetFormDatabase;
import io.dialob.db.assets.repository.AssetRepository;
import io.dialob.db.assets.repository.GenericAssetRepository;
import io.dialob.db.assets.serialization.AssetFormDeserializer;
import io.dialob.db.assets.serialization.AssetFormMetadataRowDeserializer;
import io.dialob.db.assets.serialization.AssetFormSerializer;
import io.dialob.db.azure.blob.storage.FormBlobStorageDatabase;
import io.dialob.db.azure.blob.storage.QuestionnaireBlobStorageDatabase;
import io.dialob.db.dialob.api.DialobApiDbSettings;
import io.dialob.db.dialob.api.DialobApiFormDatabase;
import io.dialob.db.dialob.api.DialobApiQuestionnaireDatabase;
import io.dialob.db.dialob.api.DialobApiTemplate;
import io.dialob.db.file.FormFileDatabase;
import io.dialob.db.file.QuestionnaireFileDatabase;
import io.dialob.db.jdbc.*;
import io.dialob.db.mongo.MongoQuestionnaireIdObfuscator;
import io.dialob.db.mongo.database.MongoDbFormDatabase;
import io.dialob.db.mongo.database.MongoDbQuestionnaireDatabase;
import io.dialob.db.mongo.repository.FormRepository;
import io.dialob.db.mongo.repository.QuestionnaireRepository;
import io.dialob.db.s3.FormS3Database;
import io.dialob.db.s3.QuestionnaireS3Database;
import io.dialob.db.spi.spring.DatabaseExceptionMapper;
import io.dialob.form.service.api.FormDatabase;
import io.dialob.form.service.api.FormVersionControlDatabase;
import io.dialob.questionnaire.service.api.QuestionnaireDatabase;
import io.dialob.settings.DialobSettings;
import io.dialob.settings.DialobSettings.DatabaseType;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.Assert;
import org.springframework.web.client.RestTemplate;
import software.amazon.awssdk.services.s3.S3Client;

import javax.sql.DataSource;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * NB! Conditional property prefixes and names should follow canonical naming,
 * see https://github.com/spring-projects/spring-boot/wiki/Canonical-properties
 * to enable these properties configuration through environment variables.
 */
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(DialobSettings.class)
@Import(DatabaseExceptionMapper.class)
public class DialobDbSpAutoConfiguration {

  /**
   * If forms do not share same JDBC database with questionnaires, we drop constraint to form document.
   */
  @Bean
  @ConditionalOnExpression("#{'${dialob.questionnaire-database.database-type:}' == 'JDBC' && '${dialob.form-database.database-type:}' != '${dialob.questionnaire-database.database-type:}'}")
  public DropQuestionnaireToFormDocumentConstraint dropQuestionnaireToFormDocumentConstraint(DialobSettings settings, DataSource dataSource) {
    DatabaseHelper databaseHelper = databaseHandler(dataSource, settings.getDb().getJdbc());
    return new DropQuestionnaireToFormDocumentConstraint(databaseHelper, settings.getDb().getJdbc().getSchema(), null);
  }


  @ConditionalOnDatabaseType(DialobSettings.DatabaseType.MONGODB)
  @Import({
    org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration.class,
    org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration.class
  })
  @EnableMongoRepositories
  public static class DialobDbMongoAutoConfiguration {

    @Bean
    @ConditionalOnProperty(prefix = "dialob.form-database", name = "database-type", havingValue = "MONGODB", matchIfMissing = true)
    public FormDatabase formDatabase(FormRepository repository) {
      return new MongoDbFormDatabase(repository);
    }

    @Bean
    @ConditionalOnProperty(prefix = "dialob.questionnaire-database", name = "database-type", havingValue = "MONGODB", matchIfMissing = true)
    public QuestionnaireDatabase questionnaireDatabase(QuestionnaireRepository repository, MongoQuestionnaireIdObfuscator mongoQuestionnaireIdObfuscator) {
      return new MongoDbQuestionnaireDatabase(repository, mongoQuestionnaireIdObfuscator);
    }

    @Bean
    @ConditionalOnMissingBean(MongoQuestionnaireIdObfuscator.class)
    public MongoQuestionnaireIdObfuscator mongoQuestionnaireIdObfuscator() {
      return new MongoQuestionnaireIdObfuscator();
    }
  }


  @Configuration(proxyBeanMethods = false)
  @ConditionalOnDatabaseType(DialobSettings.DatabaseType.JDBC)
  public static class DialobDbJdbcAutoConfiguration {

    private final JdbcVersionControlledFormDatabase versionControlledFormDatabase;
    private final JdbcQuestionnaireDatabase jdbcQuestionnaireDatabase;

    private final String schema;

    public DialobDbJdbcAutoConfiguration(TransactionTemplate transactionTemplate,
                                         JdbcTemplate jdbcTemplate,
                                         ObjectMapper objectMapper,
                                         DialobSettings settings) {

      this.schema = settings.getDb().getJdbc().getSchema();

      DatabaseHelper databaseHelper = databaseHandler(jdbcTemplate.getDataSource(), settings.getDb().getJdbc());

      Predicate<String> isAnyTenantPredicate = tenantId -> false;
      if (settings.getTenant().getMode() == DialobSettings.TenantSettings.Mode.FIXED) {
        isAnyTenantPredicate = tenantId -> settings.getTenant().getFixedId().equals(tenantId);
      }
      this.versionControlledFormDatabase = new JdbcVersionControlledFormDatabase(jdbcTemplate, this.schema, databaseHelper, transactionTemplate, new JdbcFormDatabase(jdbcTemplate, databaseHelper, transactionTemplate, objectMapper, schema, isAnyTenantPredicate), isAnyTenantPredicate, objectMapper);

      Optional<FormVersionControlDatabase> formVersionControl = Optional.empty();
      /* Version controlled database currently supported only for JDBC type,
       * verify that form database belongs to that.
       */
      if (settings.getDb().getDatabaseType() == DatabaseType.JDBC ||
          settings.getFormDatabase().getDatabaseType() == DatabaseType.JDBC) {
        formVersionControl = Optional.of(versionControlledFormDatabase);
      }

      this.jdbcQuestionnaireDatabase = new JdbcQuestionnaireDatabase(jdbcTemplate, databaseHelper, transactionTemplate, objectMapper, this.schema, formVersionControl, isAnyTenantPredicate);
    }



    @Bean
    @ConditionalOnMissingBean(PlatformTransactionManager.class)
    public static PlatformTransactionManager platformTransactionManager(DataSource dataSource) {
      return new DataSourceTransactionManager(dataSource);
    }

    @Bean
    @ConditionalOnMissingBean(TransactionTemplate.class)
    public static TransactionTemplate transactionTemplate(PlatformTransactionManager platformTransactionManager) {
      return new TransactionTemplate(platformTransactionManager);
    }

    // Spring bug here: Spring should not think this as FormDatabase, because return type do not implement type. So, we wrap implementation with proxy
    @Bean
    @ConditionalOnProperty(prefix = "dialob.form-database", name = "database-type", havingValue = "JDBC", matchIfMissing = true)
    public FormVersionControlDatabase formVersionControlDatabase() {
      return (FormVersionControlDatabase) Proxy.newProxyInstance(
        this.getClass().getClassLoader(),
        new Class<?>[]{FormVersionControlDatabase.class},
        (o, method, objects) -> {
          try {
            return method.invoke(versionControlledFormDatabase, objects);
          } catch (InvocationTargetException ite) {
            if (ite.getTargetException() instanceof RuntimeException) {
              throw ite.getTargetException();
            }
            throw ite;
          }
        });
    }

    @Bean
    @ConditionalOnProperty(prefix = "dialob.form-database", name = "database-type", havingValue = "JDBC", matchIfMissing = true)
    public FormDatabase formDatabase() {
      return (FormDatabase) Proxy.newProxyInstance(
        this.getClass().getClassLoader(),
        new Class<?>[]{FormDatabase.class},
        (o, method, objects) -> {
          try {
            return method.invoke(versionControlledFormDatabase, objects);
          } catch (InvocationTargetException ite) {
            if (ite.getTargetException() instanceof RuntimeException) {
              throw ite.getTargetException();
            }
            throw ite;
          }
        });
    }

    @Bean
    public QuestionnaireDatabase questionnaireDatabase() {
      return this.jdbcQuestionnaireDatabase;
    }



  }

  @Configuration(proxyBeanMethods = false)
  @ConditionalOnDatabaseType(DialobSettings.DatabaseType.FILEDB)
  public static class DialobDbFileAutoConfiguration {

    private String directory(@NonNull String baseDirectory, @NonNull String type) {
      final File directory = new File(Objects.requireNonNull(baseDirectory, "property dialob.db.file.directory not set"));
      Assert.isTrue(directory.exists(), "File db directory " + baseDirectory + " does not exists");
      Assert.isTrue(directory.isDirectory(), "File db directory " + baseDirectory + " is not directory");
      return directory.toPath().resolve(type).toString();
    }

    @Bean
    @ConditionalOnProperty(prefix = "dialob.form-database", name = "database-type", havingValue = "FILEDB", matchIfMissing = true)
    public FormDatabase formDatabase(ObjectMapper objectMapper, DialobSettings dialobSettings) {
      return new FormFileDatabase(directory(dialobSettings.getDb().getFile().getDirectory(), "forms"), objectMapper);
    }

    @Bean
    @ConditionalOnProperty(prefix = "dialob.questionnaire-database", name = "database-type", havingValue = "FILEDB", matchIfMissing = true)
    public QuestionnaireDatabase questionnaireDatabase(ObjectMapper objectMapper, DialobSettings dialobSettings) {
      return new QuestionnaireFileDatabase(directory(dialobSettings.getDb().getFile().getDirectory(), "questionnaires"), objectMapper);
    }
  }

  @Configuration(proxyBeanMethods = false)
  @ConditionalOnDatabaseType(DialobSettings.DatabaseType.S3)
  public static class DialobDbS3AutoConfiguration {

    @Bean
    @ConditionalOnProperty(prefix = "dialob.form-database", name = "database-type", havingValue = "S3", matchIfMissing = true)
    public FormDatabase formDatabase(S3Client s3Client, ObjectMapper objectMapper, DialobSettings settings) {
      return new FormS3Database(s3Client, objectMapper,
        Objects.requireNonNull(settings.getFormDatabase().getS3().getBucket(), "Define S3 bucket for forms"),
        Objects.toString(settings.getFormDatabase().getS3().getPrefix(), "forms/")
      );
    }

    @Bean
    @ConditionalOnProperty(prefix = "dialob.questionnaire-database", name = "database-type", havingValue = "S3", matchIfMissing = true)
    public QuestionnaireDatabase questionnaireDatabase(S3Client s3Client, ObjectMapper objectMapper, DialobSettings settings) {
      return new QuestionnaireS3Database(s3Client, objectMapper,
        Objects.requireNonNull(settings.getFormDatabase().getS3().getBucket(), "Define S3 bucket for questionnaires"),
        Objects.toString(settings.getQuestionnaireDatabase().getS3().getPrefix(), "questionnaires/")
      );
    }
  }

  @Configuration(proxyBeanMethods = false)
  @ConditionalOnDatabaseType(DialobSettings.DatabaseType.AZURE_BLOB_STORAGE)
  public static class DialobDbAzureBlobStorageAutoConfiguration {

    @Bean
    @ConditionalOnProperty(prefix = "dialob.form-database", name = "database-type", havingValue = "AZURE_BLOB_STORAGE", matchIfMissing = true)
    public FormDatabase formDatabase(BlobServiceClient blobServiceClient, ObjectMapper objectMapper, DialobSettings settings) {
      var containerName = Objects.requireNonNull(settings.getFormDatabase().getAzureBlobStorage().getContainerName(), "Define Blob Storage container name for forms");
      return new FormBlobStorageDatabase(blobServiceClient.getBlobContainerClient(containerName), objectMapper,
        Objects.toString(settings.getQuestionnaireDatabase().getAzureBlobStorage().getPrefix(), "forms/")
      );
    }

    @Bean
    @ConditionalOnProperty(prefix = "dialob.questionnaire-database", name = "database-type", havingValue = "AZURE_BLOB_STORAGE", matchIfMissing = true)
    public QuestionnaireDatabase questionnaireDatabase(BlobServiceClient blobServiceClient, ObjectMapper objectMapper, DialobSettings settings) {
      var containerName = Objects.requireNonNull(settings.getQuestionnaireDatabase().getAzureBlobStorage().getContainerName(), "Define Blob Storage container name for questionnaires");
      return new QuestionnaireBlobStorageDatabase(blobServiceClient.getBlobContainerClient(containerName), objectMapper,
        Objects.toString(settings.getQuestionnaireDatabase().getAzureBlobStorage().getPrefix(), "questionnaires/")
      );
    }
  }





  @Configuration(proxyBeanMethods = false)
  @ConditionalOnDatabaseType(DialobSettings.DatabaseType.DIALOBAPIDB)
  @EnableConfigurationProperties(DialobApiDbSettings.class)
  public class DialobDbDialobApiAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(DialobApiTemplate.class)
    public DialobApiTemplate dialobApiTemplate(DialobApiDbSettings settings) {
      RestTemplate restTemplate = new RestTemplate();
      return new DialobApiTemplate(restTemplate, settings);
    }

    @Bean
    @ConditionalOnProperty(prefix = "dialob.form-database", name = "database-type", havingValue = "DIALOBAPIDB", matchIfMissing = true)
    public FormDatabase formDatabase(DialobApiTemplate dialobApiTemplate) {
      return new DialobApiFormDatabase(dialobApiTemplate);
    }

    @Bean
    @ConditionalOnProperty(prefix = "dialob.questionnaire-database", name = "database-type", havingValue = "DIALOBAPIDB", matchIfMissing = true)
    public QuestionnaireDatabase questionnaireDatabase(DialobApiTemplate dialobApiTemplate) {
      return new DialobApiQuestionnaireDatabase(dialobApiTemplate);
    }

  }

  @Configuration(proxyBeanMethods = false)
  @ConditionalOnDatabaseType(DialobSettings.DatabaseType.ASSETS)
  public class DialobDbAssetsAutoConfiguration {

    @Bean
    public FormDatabase assetFormDatabase(ObjectMapper objectMapper, DialobSettings settings) {

      RestTemplate restTemplate = new RestTemplate();
      AssetRepository assetRepository = new GenericAssetRepository(restTemplate, settings, objectMapper);

      AssetFormSerializer assetFormSerializer = new AssetFormSerializer(objectMapper);
      AssetFormDeserializer assetFormDeserializer = new AssetFormDeserializer(objectMapper);
      AssetFormMetadataRowDeserializer assetFormMetadataRowDeserializer = new AssetFormMetadataRowDeserializer(objectMapper);

      return new AssetFormDatabase(assetRepository, assetFormSerializer, assetFormDeserializer, assetFormMetadataRowDeserializer);
    }
  }

  static DatabaseHelper databaseHandler(DataSource dataSource, DialobSettings.DatabaseSettings.JdbcSettings settings) {
    try (Connection connection = dataSource.getConnection()) {
      String databaseProductName = connection.getMetaData().getDatabaseProductName();
      if (databaseProductName.startsWith("DB2/")) {
        databaseProductName = "DB2";
      }
      return switch (databaseProductName) {
        case "PostgreSQL" -> new PostgreSQLDatabaseHelper(settings.getSchema());
        case "MySQL" -> new MySQLDatabaseHelper(settings.getSchema());
        case "DB2" -> new DB2DatabaseHelper(settings.getSchema(), settings.getRemap());
        default ->
          throw new IllegalStateException("Unsupported database product " + connection.getMetaData().getDatabaseProductName());
      };
    } catch (SQLException e) {
      throw new IllegalStateException(e);
    }
  }

}
