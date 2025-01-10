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
package io.dialob.questionnaire.service.type;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.dialob.api.questionnaire.ImmutableQuestionnaire;
import io.dialob.api.questionnaire.ImmutableQuestionnaireMetadata;
import io.dialob.api.questionnaire.Questionnaire;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Date;

public class QuestionnaireDocumentTest {

    ObjectMapper objectMapper;

    @BeforeEach
    public void setup() {
      objectMapper = new ObjectMapper();
      objectMapper.registerModule(new JavaTimeModule());
//      objectMapper.configure(SerializationFeature.WRITE_DATES_WITH_ZONE_ID, true);
      objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
      objectMapper.configure(SerializationFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS, false);
      objectMapper.configure(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS, false);
    }


    @Test
    public void test() throws Exception {
        String data = "{\"_id\":\"609a38a672cf509eeca2b110c1022d35\",\"_rev\":\"1-a2ef6c9c6ab5d9c19603be0698b836b1\",\"answers\":[], \"metadata\": {\"formId\":\"609a38a672cf509eeca2b110c1021f3b\",\"formRev\":\"11-9ea0feacfd5911b129348e4d584679c9\",\"created\":1444838304680}}\n";
        Questionnaire questionnaire = objectMapper.readValue(data, Questionnaire.class);
        Assertions.assertNotNull(questionnaire);
        Assertions.assertEquals(new Date(Instant.parse("2015-10-14T15:58:24.680Z").toEpochMilli()), questionnaire.getMetadata().getCreated());
    }

    @Test
  public void timestamps() throws Exception {
      Questionnaire questionnaire = ImmutableQuestionnaire.builder().metadata(ImmutableQuestionnaireMetadata.builder().formId("123").status(Questionnaire.Metadata.Status.OPEN).created(new Date(Instant.parse("2015-10-14T15:58:24.680Z").toEpochMilli())).build()).build();

      String data = objectMapper.writeValueAsString(questionnaire);
      Assertions.assertEquals("{\"metadata\":{\"formId\":\"123\",\"status\":\"OPEN\",\"created\":\"2015-10-14T15:58:24.680+00:00\"}}", data);

    }


}
