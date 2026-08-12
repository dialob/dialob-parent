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
package io.dialob.api.questionnaire;

import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import tools.jackson.databind.ObjectMapper;

import static org.junit.jupiter.api.Assertions.*;

class QuestionnaireMetadataTest {

  @Test
  void shouldDeserializeUnknownAttributesToAdditionalProperties() throws Exception {
    ObjectMapper objectMapper = new ObjectMapper();
    var metadata = objectMapper.readValue("""
      {
        "formId":"123",
        "status":"NEW",
        "extraProp":"extraValue"
      }
      """, Questionnaire.Metadata.class);
    assertTrue(!metadata.getAdditionalProperties().isEmpty());
    assertEquals("extraValue", metadata.getAdditionalProperties().get("extraProp"));
  }

  @Test
  void shouldSerializeAdditionalPropertiesToJsonAttributes() throws Exception {
    ObjectMapper objectMapper = new ObjectMapper();
    Questionnaire.Metadata metadata = new Questionnaire.Metadata.Builder().formId("123").putAdditionalProperties("extraProp","extraValue").build();
    assertEquals("{\"formId\":\"123\",\"status\":\"NEW\",\"extraProp\":\"extraValue\"}", objectMapper.writeValueAsString(metadata));
  }

  @Test
  public void deserializeQuestionnaireWithAdditionalPropertiesElement() throws Exception {
    var mapper = new ObjectMapper();
    var questionnaire = mapper.readValue("""
      {
        "metadata": {
          "formId": "sample-form",
          "title": "Sample Form",
          "additionalProperties": {
            "customField1": "customValue1"
          },
          "customField3": "customValue3"
        }
      }
      """, Questionnaire.class);
//    assertEquals("customValue1",questionnaire.getMetadata().getAdditionalProperties().get("customField1"));
    assertEquals("customValue3",questionnaire.getMetadata().getAdditionalProperties().get("customField3"));
    assertEquals("Sample Form",questionnaire.getMetadata().getAdditionalProperties().get("title"));
    assertNull(questionnaire.getMetadata().getAdditionalProperties().get("additionalProperties"));
    JSONAssert.assertEquals("""
      {
        "metadata":{
          "formId":"sample-form",
          "status":"NEW",
          "title":"Sample Form",
          "customField3":"customValue3"
        }
      }
      """, mapper.writeValueAsString(questionnaire), true);
  }

}
