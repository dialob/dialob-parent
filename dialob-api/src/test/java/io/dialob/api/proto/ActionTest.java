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
package io.dialob.api.proto;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ActionTest {

  @Test
  void shouldParseAction() throws Exception {
    ObjectMapper objectMapper = new ObjectMapper();
    Action action = objectMapper.readValue("{\"type\":\"ANSWER\"}", Action.class);
    assertEquals(ImmutableAction.builder()
        .type(Action.Type.ANSWER).build(),
      action);
  }
  @Test
  void shouldJsonSerializeAction() throws Exception {
    ObjectMapper objectMapper = new ObjectMapper();

    assertEquals("{\"type\":\"ANSWER\"}", objectMapper.writeValueAsString(ImmutableAction.builder()
        .type(Action.Type.ANSWER).build()));
  }
}

