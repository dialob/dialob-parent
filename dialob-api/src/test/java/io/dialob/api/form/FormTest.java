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
package io.dialob.api.form;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FormTest {

  ObjectMapper objectMapper = new ObjectMapper();

  @Test
  void hasBuilder() {
    Form form = new Form.Builder()
      .metadata(new Form.Metadata.Builder().label("label").build())
      .build();
    Assertions.assertNotNull(form);
    Assertions.assertNotNull(form.getMetadata());
    Assertions.assertEquals("label", form.getMetadata().getLabel());
  }

  @Test
  void testWithRev() {
    Form form1 = new Form.Builder()
      .metadata(new Form.Metadata.Builder().label("label").build())
      .rev("1")
      .build();
    Form form2 = form1.withRev("2");
    Assertions.assertEquals("1", form1.getRev());
    Assertions.assertEquals("2", form2.getRev());
  }

  @Test
  void metadataIsRequired() {
    var exception = assertThrows(IllegalStateException.class, () -> new Form.Builder().build());
    assertEquals("Cannot build Form, some of required attributes are not set [metadata]", exception.getMessage());
  }


  @Test
  void metadataLabelIsRequired() {
    var exception = assertThrows(IllegalStateException.class, () ->
      new Form.Metadata.Builder().build());
    assertEquals("Cannot build Metadata, some of required attributes are not set [label]", exception.getMessage());
  }

  @Test
  void testFormItemAdditionalProperties() throws Exception {
    Form form = new Form.Builder().metadata(new Form.Metadata.Builder().label("laabeli").putAdditionalProperties("extra","value").build())
      .addValueSets(new FormValueSet.Builder()
        .id("vs1")
        .addEntries(new FormValueSetEntry.Builder().id("id1").putLabel("fi","ota1").putAdditionalProperties("selite","extravalue").build())
        .putAdditionalProperties("extraItem","valuee")
        .build()).build();
    String expected = "{\"metadata\":{\"label\":\"laabeli\",\"extra\":\"value\"},\"valueSets\":[{\"id\":\"vs1\",\"entries\":[{\"id\":\"id1\",\"label\":{\"fi\":\"ota1\"},\"selite\":\"extravalue\"}],\"extraItem\":\"valuee\"}]}";
    assertEquals(expected, objectMapper.writeValueAsString(form));

    Form form2 = objectMapper.readValue(expected, Form.class);
    assertNotSame(form, form2);
    assertEquals(form, form2);

  }

}
