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
package io.dialob.db.jdbc;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.dialob.api.questionnaire.Questionnaire;
import io.dialob.form.service.api.FormVersionControlDatabase;
import io.dialob.security.tenant.CurrentTenant;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.jdbc.core.JdbcTemplate;
import edu.umd.cs.findbugs.annotations.NonNull;
import org.springframework.transaction.support.TransactionTemplate;

import java.io.StringReader;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Optional;

public class JdbcQuestionnaireDatabaseTest extends JdbcBackendDatabaseTest {

  @Override
  JdbcBackendDatabase jdbcBackendDatabase(TransactionTemplate transactionTemplate, @NonNull CurrentTenant currentTenant, JdbcTemplate jdbcTemplate, DatabaseHelper databaseHelper, ObjectMapper objectMapper, String schema) {
    return new JdbcQuestionnaireDatabase(jdbcTemplate, databaseHelper, transactionTemplate, objectMapper, schema, Optional.empty(), tenantId -> false);
  }

  @Test
  public void testToObject() {
    String json = "{\"_id\": \"e207ff3feda43167e8ccd5848482cde8\", \"_rev\": \"1\", \"answers\": [{\"id\": \"text2\", \"type\": \"STRING\", \"value\": null}, {\"id\": \"text1\", \"type\": \"STRING\", \"value\": null}], \"metadata\": {\"label\": \"New Form\", " +
      "\"owner\": \"825e9451-8d02-46d3-b5d3-051a9ceb9b74\", \"formId\": \"90bf19f8d87373d2cdc765624578e709\", \"status\": \"NEW\", \"created\": \"2019-02-25T15:39:37.899+0000\", \"creator\": \"825e9451-8d02-46d3-b5d3-051a9ceb9b74\", " +
      "\"language\": \"en\", \"tenantId\": \"itest\", \"lastAnswer\": \"2019-02-25T15:39:37.865+0000\"}, \"activeItem\": \"group1\"}";

    JdbcTemplate jdbcTemplate = Mockito.mock(JdbcTemplate.class);
    CurrentTenant currentTenant = Mockito.mock(CurrentTenant.class);
    DatabaseHelper databaseHelper = Mockito.mock(DatabaseHelper.class);
    TransactionTemplate transactionTemplate = Mockito.mock(TransactionTemplate.class);
    String schema = "public";
    FormVersionControlDatabase versionControlDatabase;

    JdbcQuestionnaireDatabase database = new JdbcQuestionnaireDatabase(jdbcTemplate, databaseHelper, transactionTemplate, objectMapper, schema, Optional.empty(), tenantId -> false);
    Questionnaire questionnaire = database.toObject(
      new byte[] {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
      1,
      "itest",
      new byte[] {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
      "NEW       ",
      Timestamp.from(Instant.now()),
      Timestamp.from(Instant.now()),
      new StringReader(json)
      );
    Assertions.assertEquals(Questionnaire.Metadata.Status.NEW, questionnaire.getMetadata().getStatus());
  }

}
