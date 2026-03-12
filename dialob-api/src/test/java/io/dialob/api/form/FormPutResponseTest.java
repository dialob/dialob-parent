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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class FormPutResponseTest {

  @Test
  void hasBuilder() {
    FormPutResponse formPutResponse = new FormPutResponse.Builder()
      .id("form1")
      .rev("rev1")
      .ok(true)
      .build();
    assertNotNull(formPutResponse);
    assertEquals("form1", formPutResponse.getId());
    assertEquals("rev1", formPutResponse.getRev());
  }

  @Test
  void jsonDeserialization() throws JsonProcessingException {
    ObjectMapper objectMapper = new ObjectMapper();
    var data = objectMapper.readValue("""
      {
        "id":"f1",
        "rev":"1",
        "reason":"test"
      }
      """, FormPutResponse.class);
    assertEquals("f1", data.id());
    assertEquals("1", data.rev());
    assertEquals("test", data.reason());

  }
}
