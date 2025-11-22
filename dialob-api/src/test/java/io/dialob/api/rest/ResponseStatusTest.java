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

import tools.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ResponseStatusTest {

  @Test
  void testJsonSerialization() throws Exception {
    var mapper = new ObjectMapper();

    Assertions.assertEquals("{\"ok\":true}", mapper.writeValueAsString(Response.OK_RESPONSE));
    Assertions.assertEquals("{\"ok\":false}", mapper.writeValueAsString(Response.NOT_OK_RESPONSE));
    Assertions.assertEquals(new Response.ResposeRecord(true, null, null), mapper.readValue("{\"ok\":true}", Response.class));
    Assertions.assertEquals(new Response.ResposeRecord(false, null, null), mapper.readValue("{}", Response.class));
  }

}
