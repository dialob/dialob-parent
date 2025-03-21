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
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FormDeletedEventTest {
  private ObjectMapper mapper = new ObjectMapper().registerModules(new Jdk8Module());

  @Test
  void testConstructorJsonMapping() throws Exception {
    FormDeletedEvent event = ImmutableFormDeletedEvent.builder().source("node").tenant(Tenant.of("tenante")).formId("formi").build();
    assertEquals("{\"type\":\"FormDeleted\",\"tenant\":{\"id\":\"tenante\"},\"formId\":\"formi\",\"source\":\"node\"}", mapper.writeValueAsString(event));
    event = (FormDeletedEvent) mapper.readValue("{\"type\":\"FormDeleted\",\"source\":\"node1\",\"tenant\":{\"id\":\"tenante2\"},\"formId\":\"formi3\"}", DistributedEvent.class);
    Assertions.assertEquals("node1", event.getSource());
    Assertions.assertEquals("tenante2", event.getTenant().id());
    Assertions.assertEquals("formi3", event.getFormId());
  }

}
