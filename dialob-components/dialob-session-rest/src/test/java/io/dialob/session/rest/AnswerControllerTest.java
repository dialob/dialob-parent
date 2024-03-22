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

import io.dialob.api.proto.*;
import io.dialob.db.spi.exceptions.DocumentNotFoundException;
import io.dialob.questionnaire.service.api.ActionProcessingService;
import io.dialob.questionnaire.service.api.session.QuestionnaireSession;
import io.dialob.questionnaire.service.api.session.QuestionnaireSessionService;
import io.dialob.security.user.CurrentUserProvider;
import io.dialob.settings.DialobSettingsAutoConfiguration;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static io.dialob.api.proto.Action.Type.RESET;
import static io.dialob.api.proto.Action.Type.SERVER_ERROR;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {DialobSessionRestAutoConfiguration.class, DialobSettingsAutoConfiguration.class}, properties = {
  "dialob.session.rest.enabled=true"
})
public class AnswerControllerTest {

  @MockBean
  private QuestionnaireSessionService questionnaireSessionService;

  @MockBean
  private ActionProcessingService actionProcessingService;

  @MockBean
  private CurrentUserProvider currentUserProvider;

  @Autowired
  public AnswerController answerController;

  @Test
  public void answerControllerShouldBeConfigured() {
    assertNotNull(answerController);
  }

  @Test
  public void shouldReturn404IfQuestionnaireDoNotExists() {
    when(questionnaireSessionService.findOne("123")).thenThrow(DocumentNotFoundException.class);
    ResponseEntity<Actions> responseEntity = answerController.getState("123");
    assertEquals(404, responseEntity.getStatusCode().value());
    assertEquals(SERVER_ERROR, responseEntity.getBody().getActions().get(0).getType());
  }

  @Test
  public void shouldReturn404IfQuestionnaireDoNotExists2() {
    when(questionnaireSessionService.findOne("123")).thenReturn(null);
    ResponseEntity<Actions> responseEntity = answerController.getState("123");
    assertEquals(404, responseEntity.getStatusCode().value());
    assertEquals(SERVER_ERROR, responseEntity.getBody().getActions().get(0).getType());
  }

  @Test
  public void shouldReturn200AndUpdateActionsForFullForm() {
    QuestionnaireSession session = mock(QuestionnaireSession.class);
    when(questionnaireSessionService.findOne("123")).thenReturn(session);
    doAnswer(invocation -> {
      QuestionnaireSession.UpdatesCallback cb = invocation.getArgument(0);
      cb.removeAll();
      return null;
    }).when(session).buildFullForm(any(QuestionnaireSession.UpdatesCallback.class));
    doReturn("444333").when(session).getRevision();
    ResponseEntity<Actions> responseEntity = answerController.getState("123");

    assertEquals(200, responseEntity.getStatusCode().value());
    assertEquals("444333", responseEntity.getBody().getRev());
    assertEquals(1, responseEntity.getBody().getActions().size());
    assertEquals(RESET, responseEntity.getBody().getActions().get(0).getType());

    verify(session).getRevision();
    verify(session).buildFullForm(any(QuestionnaireSession.UpdatesCallback.class));
    verifyNoMoreInteractions(session);
  }

  @Test
  public void answersHandlerShouldReturn404IfQuestionnaireDoNotExists() {
    when(actionProcessingService.answerQuestion("123", null, Collections.emptyList())).thenThrow(DocumentNotFoundException.class);
    Actions actions = mock(Actions.class);
    ResponseEntity<Actions> responseEntity = answerController.answers("123", actions);
    assertEquals(404, responseEntity.getStatusCode().value());
    assertEquals(SERVER_ERROR, responseEntity.getBody().getActions().get(0).getType());
    verify(actions).getRev();
    verify(actions).getActions();
    verifyNoMoreInteractions(actions);
  }

  @Test
  public void answersHandlerShouldReturn404IfQuestionnaireDoNotExists2() {
    when(actionProcessingService.answerQuestion("123", null, Collections.emptyList())).thenThrow(DocumentNotFoundException.class);
    Actions actions = mock(Actions.class);
    ResponseEntity<Actions> responseEntity = answerController.answers("123", actions);
    assertEquals(404, responseEntity.getStatusCode().value());
    assertEquals(SERVER_ERROR, responseEntity.getBody().getActions().get(0).getType());
    verify(actions).getRev();
    verify(actions).getActions();
    verifyNoMoreInteractions(actions);
  }

  @Test
  public void shouldReturn200AndUpdateActions() {
    QuestionnaireSession session = mock(QuestionnaireSession.class);
    Actions requestActions = ImmutableActions.builder()
      .rev("aabb")
      .actions(new ArrayList<>()).build();
    when(questionnaireSessionService.findOne("123")).thenReturn(session);

    doAnswer(invocation -> ImmutableActions.builder()
        .addActions(ImmutableAction.builder()
          .type(Action.Type.ITEM)
          .item(ImmutableActionItem.builder().id("q1").type("note").build())
          .build())
        .rev("444334")
        .build()
    ).when(actionProcessingService).answerQuestion(eq("123"), eq("aabb"), any(List.class));

    doReturn( "444334").when(session).getRevision();
    ResponseEntity<Actions> responseEntity = answerController.answers("123", requestActions);

    assertEquals(200, responseEntity.getStatusCode().value());
    assertEquals("444334", responseEntity.getBody().getRev());
    assertEquals(1, responseEntity.getBody().getActions().size());
    assertEquals(Action.Type.ITEM, responseEntity.getBody().getActions().get(0).getType());

    verifyNoMoreInteractions(session);
  }


  @Test
  public void shouldReturn500WithoutStackByDefault() {
    when(questionnaireSessionService.findOne("123")).thenThrow(RuntimeException.class);
    ResponseEntity<Actions> responseEntity = answerController.getState("123");
    assertEquals(500, responseEntity.getStatusCode().value());
    assertEquals(SERVER_ERROR, responseEntity.getBody().getActions().get(0).getType());
    assertNull(responseEntity.getBody().getActions().get(0).getTrace());

    when(actionProcessingService.answerQuestion("123", null, null)).thenThrow(RuntimeException.class);
    responseEntity = answerController.answers("123", ImmutableActions.builder().build());
    assertEquals(500, responseEntity.getStatusCode().value());
    assertEquals(SERVER_ERROR, responseEntity.getBody().getActions().get(0).getType());
    assertNull(responseEntity.getBody().getActions().get(0).getTrace());
  }


}
