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

import tools.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AnswerTest {

  @Test
  void hasBuilder() {
    Answer.Builder builder = new Answer.Builder();
    builder.id("q1");
    Answer answer = builder.build();
    assertEquals("q1", answer.id());
  }

  @Test
  void copyOfReturnsSameInstance() {
    var element = Answer.of("key2", 123);
    assertSame(element, Answer.copyOf(element));
  }

  @Test
  void shouldParseAnswer() throws Exception {
    ObjectMapper objectMapper = new ObjectMapper();
    Answer answer = objectMapper.readValue("{\"id\":\"q1\"}", Answer.class);
    assertNull(answer.value());
    assertEquals("q1", answer.id());

    answer = objectMapper.readValue("{\"id\":\"q1\",\"value\":\"123\"}", Answer.class);
    assertEquals("123",answer.value());
    assertEquals("q1", answer.id());

    answer = objectMapper.readValue("{\"id\":\"q1\",\"value\":[\"123\"]}", Answer.class);
    assertEquals(List.of("123"),answer.value());
    assertEquals("q1", answer.id());

    answer = objectMapper.readValue("{\"id\":\"q1\",\"value\":123}", Answer.class);
    assertEquals(123,answer.getValue());
    assertEquals("q1", answer.id());
  }

  @Test
  void shouldSerializeAnswer() throws Exception {
    ObjectMapper objectMapper = new ObjectMapper();
    Answer answer = Answer.of("q1", 123);
    assertEquals("{\"id\":\"q1\",\"value\":123}", objectMapper.writeValueAsString(answer));
    answer = new Answer.Builder().id("q1").value(123).readOnly(true).build();
    assertEquals("{\"id\":\"q1\",\"value\":123,\"readOnly\":true}", objectMapper.writeValueAsString(answer));
    answer = new Answer.Builder().id("q1").value(123).required(true).build();
    assertEquals("{\"id\":\"q1\",\"value\":123,\"required\":true}", objectMapper.writeValueAsString(answer));
  }
}
