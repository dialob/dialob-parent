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
import io.dialob.db.jdbc.JdbcDatabase;
import io.dialob.settings.DialobSettings;
import org.jdbi.v3.core.spi.JdbiPlugin;
import org.jdbi.v3.postgres.PostgresPlugin;
import org.jdbi.v3.spring.EnableJdbiRepositories;
import org.jdbi.v3.spring.JdbiFactoryBean;
import org.jdbi.v3.sqlobject.SqlObjectPlugin;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Configuration(proxyBeanMethods = false)
@EnableJdbiRepositories(repositories = {
  FormDocumentJdbiRepository.class,
  FormJdbiRepository.class,
  FormRevJdbiRepository.class,
  QuestionnaireJdbiRepository.class
})
@EnableConfigurationProperties(DialobSettings.class)
public class JdbiConfiguration {

  @Bean
  JdbiPlugin postgresPlugin() {
    return new PostgresPlugin();
  }

  @Bean
  JdbiPlugin sqlObjectPlugin() {
    return new SqlObjectPlugin();
  }

  @Bean
  public JdbiFactoryBean jdbiFactoryBean(DataSource dataSource, DatabaseHelper databaseHelper, DialobSettings dialobSettings) {
    var settings = dialobSettings.getDb().getJdbc();
    var factoryBean = new JdbiFactoryBean(dataSource);
    var remaps = settings.getRemap();
    var schema = settings.getSchema();
    factoryBean.setGlobalDefines(
      Stream.concat(
        Stream.concat(
          JdbcDatabase.TABLES.stream().map(table ->
            Map.entry(table, databaseHelper.tableName(schema, table))),
          remaps.entrySet().stream().map(
            entry -> Map.entry(
              entry.getKey(),
              databaseHelper.tableName(schema, entry.getValue())
            ))),
        schema != null ? Stream.of(Map.entry("SCHEMA", schema)) : Stream.empty()
      ).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue))
    );
    return factoryBean;
  }

}

