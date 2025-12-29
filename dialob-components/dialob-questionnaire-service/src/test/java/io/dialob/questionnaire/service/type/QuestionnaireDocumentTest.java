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

import io.dialob.api.questionnaire.Questionnaire;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.cfg.DateTimeFeature;

import java.time.Instant;

class QuestionnaireDocumentTest {

    ObjectMapper objectMapper;

    @BeforeEach
    public void setup() {
      objectMapper = new ObjectMapper().rebuild()
        .configure(DateTimeFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS, false)
        .build();
    }


    @Test
    void test() {
        String data = "{\"_id\":\"609a38a672cf509eeca2b110c1022d35\",\"_rev\":\"1-a2ef6c9c6ab5d9c19603be0698b836b1\",\"answers\":[], \"metadata\": {\"formId\":\"609a38a672cf509eeca2b110c1021f3b\",\"formRev\":\"11-9ea0feacfd5911b129348e4d584679c9\",\"created\":1444838304680}}\n";
        Questionnaire questionnaire = objectMapper
          .readValue(data, Questionnaire.class);
        Assertions.assertNotNull(questionnaire);
        Assertions.assertEquals(Instant.parse("2015-10-14T15:58:24.680Z"), questionnaire.getMetadata().getCreated());
    }

    @Test
  void timestamps() throws Exception {
      Questionnaire questionnaire = new Questionnaire.Builder().metadata(new Questionnaire.Metadata.Builder().formId("123").status(Questionnaire.Metadata.Status.OPEN).created(Instant.parse("2015-10-14T15:58:24.680Z")).build()).build();

      String data = objectMapper.writeValueAsString(questionnaire);
      Assertions.assertEquals("{\"metadata\":{\"formId\":\"123\",\"status\":\"OPEN\",\"created\":\"2015-10-14T15:58:24.680Z\"}}", data);

    }


}
