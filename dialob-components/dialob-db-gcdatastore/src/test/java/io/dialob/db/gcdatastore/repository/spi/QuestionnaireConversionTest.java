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
package io.dialob.db.gcdatastore.repository.spi;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.Entity.Builder;
import com.google.cloud.datastore.Key;
import com.google.cloud.datastore.KeyFactory;
import io.dialob.api.questionnaire.ImmutableAnswer;
import io.dialob.api.questionnaire.ImmutableQuestionnaire;
import io.dialob.api.questionnaire.ImmutableQuestionnaireMetadata;
import io.dialob.api.questionnaire.Questionnaire;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Date;

public class QuestionnaireConversionTest {

  @Test
  public void test() throws JsonProcessingException {
    ObjectMapper mapper = new ObjectMapper().registerModules(new JavaTimeModule());
    DatastoreQuestionnaireRepository repo = new DatastoreQuestionnaireRepository(null, mapper, null, "forms");
    Questionnaire document = ImmutableQuestionnaire.builder()
      .metadata(ImmutableQuestionnaireMetadata.builder()
      .formId("123")
      .created(Date.from(Instant.now()))
      .creator("Tester")
      .label("Label")
      .tenantId("tenant_id")
      .build())
      .addAnswers(ImmutableAnswer.builder().id("aid").value("super").build())
      .id("formid")
      .rev("1").build();

    Key key = null;
    KeyFactory keyFactory = new KeyFactory("test").setKind("test");
    key = keyFactory.newKey(document.getId());

    Builder builder = Entity.newBuilder(key);
    Entity convert = repo.convert(document, builder, document.getRev());

    Questionnaire convertedDocument = repo.convert(convert, Questionnaire.class);
    Assertions.assertNotNull(convertedDocument);
    Assertions.assertEquals(document, convertedDocument);
  }

}
