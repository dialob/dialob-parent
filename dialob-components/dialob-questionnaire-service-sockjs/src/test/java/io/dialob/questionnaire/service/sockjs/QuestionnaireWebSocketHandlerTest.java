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
package io.dialob.questionnaire.service.sockjs;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.dialob.api.proto.ActionItem;
import io.dialob.api.proto.ImmutableActionItem;
import io.dialob.db.spi.exceptions.DocumentNotFoundException;
import io.dialob.questionnaire.service.api.ActionProcessingService;
import io.dialob.questionnaire.service.api.event.QuestionnaireEventPublisher;
import io.dialob.questionnaire.service.api.session.QuestionnaireSession;
import io.dialob.questionnaire.service.api.session.QuestionnaireSessionService;
import io.dialob.settings.DialobSettings;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.mockito.internal.verification.api.VerificationData;
import org.mockito.verification.VerificationMode;
import org.springframework.core.task.TaskExecutor;
import org.springframework.http.HttpHeaders;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.net.InetSocketAddress;
import java.time.Clock;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class QuestionnaireWebSocketHandlerTest {

  @BeforeEach
  public void reset() {
    MockitoAnnotations.initMocks(this);
  }

  @Mock
  public ActionProcessingService actionProcessingService;

  @Spy
  public ObjectMapper mapper = new ObjectMapper();

  @Spy
  public DialobSettings settings = new DialobSettings();

  @Mock
  public Clock clock;

  @Mock
  public QuestionnaireSessionService questionnaireSessionService;

  @Mock
  public TaskExecutor taskExecutor;

  @Mock
  QuestionnaireEventPublisher eventPublisher;

  @InjectMocks
  public QuestionnaireWebSocketHandler questionnaireWebSocketHandler;

  protected WebSocketSession mockWebSocketSessionFrom(String hostname, int port) {
    WebSocketSession webSocketSession = Mockito.mock(WebSocketSession.class);
    when(webSocketSession.getRemoteAddress()).thenReturn(new InetSocketAddress(hostname, port));
    when(webSocketSession.isOpen()).thenReturn(true);
    return webSocketSession;
  }


  @Test
  public void shouldDelegateSessionInitToAsyncTask() throws Exception {
    final WebSocketSession webSocketSession = mockWebSocketSessionFrom("localhost",9999);
    final QuestionnaireSession questionnaireSession = Mockito.mock(QuestionnaireSession.class);
    final Map<String,Object> attributes = new HashMap<>();
    attributes.put("sessionId","123-321");
    when(webSocketSession.getAttributes()).thenReturn(attributes);
    final HttpHeaders httpHeaders = Mockito.mock(HttpHeaders.class);
    when(webSocketSession.getHandshakeHeaders()).thenReturn(httpHeaders);
    doAnswer(invocation -> {
      ((Runnable)invocation.getArgument(0)).run();
      return null;
    }).when(taskExecutor).execute(any());
    when(questionnaireSessionService.findOne("123-321")).thenReturn(questionnaireSession);
    when(questionnaireSession.getRevision()).thenReturn("123");
    doAnswer(invocation -> {
      QuestionnaireSession.UpdatesCallback callback = (QuestionnaireSession.UpdatesCallback) invocation.getArgument(0);
      ActionItem textQuestion = ImmutableActionItem.builder()
        .type("text")
        .id("q1")
        .className(Collections.emptyList())
        .label("Question?").build();
      callback.removeAll().questionAdded(textQuestion);
      return null;
    }).when(questionnaireSession).buildFullForm(any(QuestionnaireSession.UpdatesCallback.class));

    questionnaireWebSocketHandler.afterConnectionEstablished(webSocketSession);

    verify(webSocketSession, new VerificationMode() {
      @Override
      public void verify(VerificationData data) {
        assertEquals(7, data.getAllInvocations().size());
        assertEquals("sendMessage", data.getAllInvocations().get(6).getMethod().getName());
        TextMessage textMessage = data.getAllInvocations().get(6).getArgument(0);
        String message = new String(textMessage.asBytes());
        assertEquals("{\"rev\":\"123\",\"actions\":[{\"type\":\"RESET\"},{\"type\":\"ITEM\",\"item\":{\"id\":\"q1\",\"type\":\"text\",\"label\":\"Question?\"}}]}", message);
      }

      @Override
      public VerificationMode description(String description) {
        return this;
      }
    }).sendMessage(any(TextMessage.class));
    verify(taskExecutor).execute(any(Runnable.class));
    verify(questionnaireSessionService).findOne("123-321");
    verify(questionnaireSession).getRevision();
    verify(questionnaireSession).buildFullForm(any(QuestionnaireSession.UpdatesCallback.class));
    verifyNoMoreInteractions(taskExecutor, questionnaireSessionService, questionnaireSession);
  }

  @Test
  public void shouldSendQuestionnaireNotFoundMessageIfQuestionnaireIsNotFound() throws Exception {
    final WebSocketSession webSocketSession = mockWebSocketSessionFrom("localhost",9999);
    final QuestionnaireSession questionnaireSession = Mockito.mock(QuestionnaireSession.class);
    final Map<String,Object> attributes = new HashMap<>();
    attributes.put("sessionId","123-321");
    when(webSocketSession.getAttributes()).thenReturn(attributes);
    final HttpHeaders httpHeaders = Mockito.mock(HttpHeaders.class);
    when(webSocketSession.getHandshakeHeaders()).thenReturn(httpHeaders);
    doAnswer(invocation -> {
      ((Runnable)invocation.getArgument(0)).run();
      return null;
    }).when(taskExecutor).execute(any());
    when(questionnaireSessionService.findOne("123-321")).thenThrow(DocumentNotFoundException.class);

    questionnaireWebSocketHandler.afterConnectionEstablished(webSocketSession);

    verify(webSocketSession, new VerificationMode() {
      @Override
      public void verify(VerificationData data) {
        assertEquals(7, data.getAllInvocations().size());
        assertEquals("sendMessage", data.getAllInvocations().get(6).getMethod().getName());
        TextMessage textMessage = data.getAllInvocations().get(6).getArgument(0);
        String message = new String(textMessage.asBytes());
        assertEquals("{\"actions\":[{\"type\":\"SERVER_ERROR\",\"id\":\"123-321\",\"message\":\"not found\"}]}", message);
      }

      @Override
      public VerificationMode description(String description) {
        return this;
      }
    }).sendMessage(any(TextMessage.class));
    verify(taskExecutor).execute(any(Runnable.class));
    verify(questionnaireSessionService).findOne("123-321");
    verifyNoMoreInteractions(taskExecutor, questionnaireSessionService);
  }

}
