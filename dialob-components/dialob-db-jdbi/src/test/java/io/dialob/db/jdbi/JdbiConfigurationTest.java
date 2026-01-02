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

import io.dialob.db.jdbc.DatabaseHelper;
import io.dialob.settings.DialobSettings;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.statement.SqlStatements;
import org.jdbi.v3.spring.JdbiFactoryBean;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class JdbiConfigurationTest {

  @Configuration(proxyBeanMethods = false)
  public static class MockConfiguration {

    @Bean
    public DataSource dataSource() throws Exception {
      final DataSource dataSource = mock(DataSource.class);
      Connection connection = mock(Connection.class);
      when(dataSource.getConnection()).thenReturn(connection);
      DatabaseMetaData databaseMetaData = mock(DatabaseMetaData.class);
      when(databaseMetaData.getDatabaseProductName()).thenReturn("PostgreSQL");
      when(connection.getMetaData()).thenReturn(databaseMetaData);
      return dataSource;
    }

    @Bean
    public DatabaseHelper databaseHelper() {
      DatabaseHelper helper = mock(DatabaseHelper.class);
      when(helper.tableName(org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.anyString()))
        .thenAnswer(invocation -> {
          String schema = invocation.getArgument(0);
          String tableName = invocation.getArgument(1);
          return schema != null ? schema + "." + tableName : tableName;
        });
      return helper;
    }
  }

  @Test
  void testJdbiFactoryBeanCreation() {
    new ApplicationContextRunner()
      .withPropertyValues(
        "dialob.db.jdbc.schema=testschema"
      )
      .withUserConfiguration(MockConfiguration.class)
      .withConfiguration(AutoConfigurations.of(JdbiConfiguration.class))
      .run(context -> {
        assertThat(context)
          .hasSingleBean(JdbiFactoryBean.class)
          .hasSingleBean(DialobSettings.class);

        JdbiFactoryBean factoryBean = context.getBean(JdbiFactoryBean.class);
        assertThat(factoryBean).isNotNull();
      });
  }

  @Test
  void testJdbiFactoryBeanWithTableRemapping() {
    new ApplicationContextRunner()
      .withPropertyValues(
        "dialob.db.jdbc.schema=testschema",
        "dialob.db.jdbc.remap.forms=custom_forms",
        "dialob.db.jdbc.remap.questionnaires=custom_questionnaires"
      )
      .withUserConfiguration(MockConfiguration.class)
      .withConfiguration(AutoConfigurations.of(JdbiConfiguration.class))
      .run(context -> {
        assertThat(context)
          .hasSingleBean(JdbiFactoryBean.class);

        DialobSettings.DatabaseSettings.JdbcSettings settings =
          context.getBean(DialobSettings.class).getDb().getJdbc();

        assertThat(settings.getSchema()).isEqualTo("testschema");
        assertThat(settings.getRemap()).containsEntry("forms", "custom_forms");
        assertThat(settings.getRemap()).containsEntry("questionnaires", "custom_questionnaires");
      });
  }

  @Test
  void testJdbiFactoryBeanWithoutSchema() {
    new ApplicationContextRunner()
      .withUserConfiguration(MockConfiguration.class)
      .withConfiguration(AutoConfigurations.of(JdbiConfiguration.class))
      .run(context -> {
        assertThat(context)
          .hasSingleBean(JdbiFactoryBean.class);

        DialobSettings.DatabaseSettings.JdbcSettings settings =
          context.getBean(DialobSettings.class).getDb().getJdbc();

        assertThat(settings.getSchema()).isNull();
      });
  }

  @Test
  void testJdbiFactoryBeanWithEmptyRemap() {
    new ApplicationContextRunner()
      .withPropertyValues(
        "dialob.db.jdbc.schema=myschema"
      )
      .withUserConfiguration(MockConfiguration.class)
      .withConfiguration(AutoConfigurations.of(JdbiConfiguration.class))
      .run(context -> {
        assertThat(context)
          .hasSingleBean(JdbiFactoryBean.class);

        DialobSettings.DatabaseSettings.JdbcSettings settings =
          context.getBean(DialobSettings.class).getDb().getJdbc();

        assertThat(settings.getSchema()).isEqualTo("myschema");
        assertThat(settings.getRemap()).isNullOrEmpty();
      });
  }

  @Test
  void testGlobalDefinesConfiguration() {
    new ApplicationContextRunner()
      .withPropertyValues(
        "dialob.db.jdbc.schema=prod_schema",
        "dialob.db.jdbc.remap.forms=tform",
        "dialob.db.jdbc.remap.questionnaires=tquest"
      )
      .withBean(DataSource.class, () -> {
        try {
          DataSource dataSource = mock(DataSource.class);
          Connection connection = mock(Connection.class);
          when(dataSource.getConnection()).thenReturn(connection);
          DatabaseMetaData metadata = mock(DatabaseMetaData.class);
          when(metadata.getDatabaseProductName()).thenReturn("PostgreSQL");
          when(connection.getMetaData()).thenReturn(metadata);
          return dataSource;
        } catch (Exception e) {
          throw new RuntimeException(e);
        }
      })
      .withBean(DatabaseHelper.class, () -> {
        DatabaseHelper helper = mock(DatabaseHelper.class);
        when(helper.tableName(eq("prod_schema"), anyString())).thenAnswer(
          invocation ->
            invocation.getArguments()[0] + "." + invocation.getArguments()[1]
        );
        return helper;
      })
      .withConfiguration(AutoConfigurations.of(JdbiConfiguration.class))
      .run(context -> {
        assertThat(context).hasSingleBean(JdbiFactoryBean.class);
        assertThat(context).hasSingleBean(Jdbi.class);

        Jdbi bean = context.getBean(Jdbi.class);
        var attrs = bean
          .getConfig(SqlStatements.class)
          .getAttributes();

        assertThat(attrs).containsOnly(
          Map.entry("forms", "prod_schema.tform"),
          Map.entry("questionnaires", "prod_schema.tquest"),

          Map.entry("form_archive", "prod_schema.form_archive"),
          Map.entry("form_document", "prod_schema.form_document"),
          Map.entry("form_rev_archive", "prod_schema.form_rev_archive"),
          Map.entry("form_rev", "prod_schema.form_rev"),
          Map.entry("questionnaire", "prod_schema.questionnaire"),
          Map.entry("form", "prod_schema.form"),

          Map.entry("SCHEMA", "prod_schema")
        );
      });
  }
}
