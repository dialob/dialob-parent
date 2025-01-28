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
package io.dialob.integration.api.event;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import io.dialob.security.tenant.Tenant;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

class FormUpdatedEventTest {

  @Test
  void shouldSerializaIntoJsonAndBack() throws IOException {
    final FormUpdatedEvent event = ImmutableFormUpdatedEvent.builder()
      .source("node1")
      .tenant(Tenant.of("tent1"))
      .formId("formi")
      .revision("new")
      .build();
    ObjectMapper objectMapper = new ObjectMapper().registerModule(new Jdk8Module());
    String json = objectMapper.writeValueAsString(event);
    assertEquals("{\"type\":\"FormUpdated\",\"tenant\":{\"id\":\"tent1\"},\"formId\":\"formi\",\"source\":\"node1\",\"revision\":\"new\"}", json);
    DistributedEvent event2 = objectMapper.readValue(json, DistributedEvent.class);
    assertInstanceOf(FormUpdatedEvent.class, event2);
    assertEquals(event, event2);
  }
}
