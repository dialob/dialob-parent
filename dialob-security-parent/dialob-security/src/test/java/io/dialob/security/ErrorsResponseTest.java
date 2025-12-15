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
package io.dialob.security;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import net.javacrumbs.jsonunit.assertj.JsonAssertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class ErrorsResponseTest {

  @Test
  void shouldSetTimestampIfNull() {
    ErrorsResponse response = new ErrorsResponse(null, 400, "error", "message");
    assertNotNull(response.timestamp());
  }

  @Test
  void shouldSerializeAsJson() throws JsonProcessingException {
    var om = new ObjectMapper().registerModule(new JavaTimeModule()).configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    var json = om.writeValueAsString(new ErrorsResponse.Builder().build());
    JsonAssertions.assertThatJson(json)
      .isObject()
      .doesNotContainKeys("status", "error", "message")
      .node("timestamp").isString().matches("\\d{4}-[0-1]\\d-[0-3]\\dT[0-2]\\d:[0-5]\\d:[0-5]\\d(\\.\\d{1,6})?(Z|[+-]\\d{2}(:\\d{2})?)?");
  }

}
