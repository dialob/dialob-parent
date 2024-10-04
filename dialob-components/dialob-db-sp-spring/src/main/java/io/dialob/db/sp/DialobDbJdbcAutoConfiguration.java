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

import io.dialob.db.jdbc.DatabaseHelper;
import io.dialob.db.jdbc.DropQuestionnaireToFormDocumentConstraint;
import io.dialob.settings.DialobSettings;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(DialobSettings.class)
public class DialobDbJdbcAutoConfiguration {
  /**
   * If forms do not share same JDBC database with questionnaires, we drop constraint to form document.
   */
  @Bean
  @ConditionalOnExpression("#{'${dialob.questionnaire-database.database-type:}' == 'JDBC' && '${dialob.form-database.database-type:}' != '${dialob.questionnaire-database.database-type:}'}")
  public DropQuestionnaireToFormDocumentConstraint dropQuestionnaireToFormDocumentConstraint(DialobSettings settings, DataSource dataSource) {
    DatabaseHelper databaseHelper = DialobDbSpAutoConfiguration.databaseHandler(dataSource, settings.getDb().getJdbc());
    return new DropQuestionnaireToFormDocumentConstraint(databaseHelper, settings.getDb().getJdbc().getSchema(), null);
  }

}
