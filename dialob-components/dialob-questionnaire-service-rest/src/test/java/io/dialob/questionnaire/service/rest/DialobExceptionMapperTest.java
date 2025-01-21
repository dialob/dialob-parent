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
package io.dialob.questionnaire.service.rest;

import io.dialob.questionnaire.service.api.FormDataMissingException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = {DialobExceptionMapper.class, DialobExceptionMapperTest.TestService.class})
@AutoConfigureMockMvc
class DialobExceptionMapperTest {

  @Autowired
  private MockMvc mockMvc;

  @RestController
  static class TestService {
    @GetMapping("/someEndpoint")
    public ResponseEntity someEndpoint() {
      throw new FormDataMissingException("testForm", "1");
    }
  }

  @Test
  void testFormDataMissingException() throws Exception {
    mockMvc.perform(get("/someEndpoint")
        .accept(MediaType.APPLICATION_JSON))
      .andExpect(status().isNotFound())
      .andExpect(content().json("{\"error\":\"form_not_found\",\"reason\":\"Form 'testForm' rev '1' cannot be loaded.\"}"));
  }
}
