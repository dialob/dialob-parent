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
package io.dialob.session.rest;

import com.google.common.collect.ImmutableList;
import io.dialob.api.proto.Actions;
import io.dialob.api.proto.ImmutableActions;
import io.dialob.questionnaire.service.api.ActionProcessingService;
import io.dialob.questionnaire.service.api.session.QuestionnaireSessionService;
import io.dialob.security.user.CurrentUserProvider;
import io.dialob.settings.DialobSettingsAutoConfiguration;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static io.dialob.api.proto.Action.Type.SERVER_ERROR;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNotNull;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {DialobSessionRestAutoConfiguration.class, DialobSettingsAutoConfiguration.class}, properties = {
  "dialob.databaseType=NONE",
  "dialob.session.rest.enabled=true",
  "dialob.session.returnStackTrace=true"
})
class AnswerControllerWithStackTest {

  @MockitoBean
  private QuestionnaireSessionService questionnaireSessionService;

  @MockitoBean
  private ActionProcessingService actionProcessingService;

  @MockitoBean
  private CurrentUserProvider currentUserProvider;

  @Autowired
  public AnswerController answerController;

  @Test
  void answerControllerShouldBeConfigured() {
    assertNotNull(answerController);
  }

  @Test
  void shouldReturn500WithStackIfEnabled() {
    when(questionnaireSessionService.findOne("123")).thenThrow(RuntimeException.class);
    ResponseEntity<Actions> responseEntity = answerController.getState("123");
    assertEquals(500, responseEntity.getStatusCode().value());
    assertEquals(SERVER_ERROR, responseEntity.getBody().getActions().get(0).getType());
    assertNotNull(responseEntity.getBody().getActions().get(0).getTrace());
  }

  @Test
  void shouldReturn500WithStackIfEnabledOnAnswers() {
    when(actionProcessingService.answerQuestion(eq("123"), eq("rev-10"), isNotNull())).thenThrow(RuntimeException.class);
    final ResponseEntity<Actions> responseEntity = answerController.answers("123", ImmutableActions.builder()
      .rev("rev-10")
      .actions(ImmutableList.of())
      .build());
    assertEquals(500, responseEntity.getStatusCode().value());
    assertEquals(SERVER_ERROR, responseEntity.getBody().getActions().get(0).getType());
    assertNotNull(responseEntity.getBody().getActions().get(0).getTrace());
  }


}
