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
package io.dialob.form.service.api.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.dialob.api.form.Form;
import io.dialob.api.form.ImmutableForm;
import io.dialob.api.form.ImmutableFormMetadata;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class FormMetadataTest {

  private ObjectMapper objectMapper = new ObjectMapper();

  @Test
  void shouldParseLabelsSet() throws Exception {
    String document = "{\"metadata\":{\"labels\":[],\"label\":\"test\"}}";
    Form formDocument = objectMapper.readValue(document, Form.class);
    assertTrue(formDocument.getMetadata().getLabels().isEmpty());

    document = "{\"metadata\":{\"label\":\"test\"}}";
    formDocument = objectMapper.readValue(document, Form.class);
    assertTrue(formDocument.getMetadata().getLabels().isEmpty());

    document = "{\"metadata\":{\"labels\":[\"abc\"],\"label\":\"test\"}}";
    formDocument = objectMapper.readValue(document, Form.class);
    assertEquals(1, formDocument.getMetadata().getLabels().size());
    assertTrue(formDocument.getMetadata().getLabels().contains("abc"));
    document = "{\"metadata\":{\"labels\":[\"abc\",\"abc\"],\"label\":\"test\"}}";
    formDocument = objectMapper.readValue(document, Form.class);
    assertEquals(1, formDocument.getMetadata().getLabels().size());
    assertTrue(formDocument.getMetadata().getLabels().contains("abc"));

    document = "{\"metadata\":{\"labels\":[\"abc\",\"123\"],\"label\":\"test\"}}";
    formDocument = objectMapper.readValue(document, Form.class);
    assertEquals(2, formDocument.getMetadata().getLabels().size());
    assertTrue(formDocument.getMetadata().getLabels().contains("abc"));
    assertTrue(formDocument.getMetadata().getLabels().contains("123"));
    formDocument = ImmutableForm.builder().from(formDocument).metadata(ImmutableFormMetadata.builder().from(formDocument.getMetadata()).addLabels("ggg").build()).build();
    assertEquals("{\"metadata\":{\"label\":\"test\",\"labels\":[\"123\",\"abc\",\"ggg\"]}}", objectMapper.writeValueAsString(formDocument));
  }

  @Test
  void metadataPropertiesTest() throws Exception {
    String document = "{\"metadata\":{\"someRandomThing\": {\"someKey\": \"someValue\"},\"label\":\"test\"}}";
    Form formDocument = objectMapper.readValue(document, Form.class);
    assertNotNull(formDocument.getMetadata().getAdditionalProperties());
    Object obj = formDocument.getMetadata().getAdditionalProperties().get("someRandomThing");
    assertNotNull(obj);
    assertTrue(obj instanceof Map);
    assertEquals("someValue", ((Map) obj).get("someKey"));
  }

}
