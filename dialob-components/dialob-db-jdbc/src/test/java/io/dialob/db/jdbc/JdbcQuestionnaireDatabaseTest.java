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
import edu.umd.cs.findbugs.annotations.NonNull;
import io.dialob.api.questionnaire.Questionnaire;
import io.dialob.security.tenant.CurrentTenant;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.support.TransactionTemplate;

import java.io.StringReader;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Optional;

class JdbcQuestionnaireDatabaseTest extends JdbcBackendDatabaseTest {

  @Override
  JdbcBackendDatabase jdbcBackendDatabase(TransactionTemplate transactionTemplate, @NonNull CurrentTenant currentTenant, JdbcTemplate jdbcTemplate, DatabaseHelper databaseHelper, ObjectMapper objectMapper, String schema) {
    return new JdbcQuestionnaireDatabase(jdbcTemplate, databaseHelper, transactionTemplate, objectMapper, schema, Optional.empty(), tenantId -> false);
  }

  @Test
  void testToObject() {
    String json = """
      {
          "_id": "60812bd3dcb6e605b406915b",
          "answers": [
          ],
          "metadata": {
              "formId": "c6b164aea3bd9d44c6e423b7dd91064c",
              "status": "COMPLETED",
              "formRev": "1",
              "created": "2021-04-22T07:54:59.874Z",
              "lastAnswer": "2021-04-22T07:57:12.543Z",
              "label": "Sijoittajaprofiilikysely (HA) uusi",
              "language": "fi",
              "owner": "180667-5139",
              "creator": "180667-5139",
              "fdsafdsaf": "fdsafdsa",
              "additionalProperties": { }
          }
      }
      """;
      JdbcTemplate jdbcTemplate = Mockito.mock(JdbcTemplate.class);
    DatabaseHelper databaseHelper = Mockito.mock(DatabaseHelper.class);
    TransactionTemplate transactionTemplate = Mockito.mock(TransactionTemplate.class);
    String schema = "public";

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
