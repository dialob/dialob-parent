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
package io.dialob.api.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ResponseStatusTest {

  @Test
  public void testJsonSerialization() throws Exception {
    Assertions.assertEquals("{\"ok\":true}", new ObjectMapper().writeValueAsString(ImmutableResponse.builder().ok(true).build()));
    Assertions.assertEquals("{\"ok\":false}", new ObjectMapper().writeValueAsString(ImmutableResponse.builder().ok(false).build()));
    Assertions.assertEquals(ImmutableResponse.builder().ok(true).build(), new ObjectMapper().readValue("{\"ok\":true}", Response.class));
    Assertions.assertEquals(ImmutableResponse.builder().build(), new ObjectMapper().readValue("{}", Response.class));
  }

}
