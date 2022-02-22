/*
 * Copyright © 2015 - 2021 ReSys (info@dialob.io)
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
package io.dialob.session.rest;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import io.dialob.questionnaire.service.api.ActionProcessingService;
import io.dialob.questionnaire.service.api.session.QuestionnaireSessionService;
import io.dialob.security.user.CurrentUserProvider;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {DefaultAnswerControllerTest.TestConfiguration.class})
@SpringBootTest
@WebAppConfiguration
class DefaultAnswerControllerTest {

  @Configurable
  public static class TestConfiguration {

    @Bean
    public CurrentUserProvider currentUserProvider() {
      return () -> null;
    }

    @Bean
    public AnswerController answerController(QuestionnaireSessionService questionnaireSessionService,
                                             ActionProcessingService actionProcessingService,
                                             SessionPermissionEvaluator sessionPermissionEvaluator,
                                             Optional<CurrentUserProvider> currentUserProvider) {
      return new DefaultAnswerController(questionnaireSessionService, actionProcessingService, sessionPermissionEvaluator, true, currentUserProvider);
    }

  }

  @MockBean
  QuestionnaireSessionService questionnaireSessionService;

  @MockBean
  ActionProcessingService actionProcessingService;

  @MockBean
  SessionPermissionEvaluator sessionPermissionEvaluator;



  @Autowired
  private AnswerController answerController;

  private MockMvc mockMvc;

  @BeforeEach
  public void setUp() {
    mockMvc = standaloneSetup(answerController).build();
  }

  @Test
  public void shouldReturnJust400ForInvalidJsonPayload() throws Exception {
    mockMvc.perform(MockMvcRequestBuilders
      .post("/session-id")
      .content("{\"rev\":\"heh\",\"actions\":[{}]")
      .contentType(MediaType.APPLICATION_JSON)
    )
      .andExpect(status().isBadRequest())
      .andExpect(content().string(""))
    ;
  }

}
