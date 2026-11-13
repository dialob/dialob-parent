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
package io.dialob.questionnaire.service.sockjs;

import io.dialob.api.proto.Action;
import io.dialob.api.proto.ActionItem;
import io.dialob.api.proto.Actions;
import io.dialob.db.spi.exceptions.DocumentNotFoundException;
import io.dialob.questionnaire.service.api.ActionProcessingService;
import io.dialob.questionnaire.service.api.event.QuestionnaireActionsEvent;
import io.dialob.questionnaire.service.api.event.QuestionnaireCompletedEvent;
import io.dialob.questionnaire.service.api.event.QuestionnaireEventPublisher;
import io.dialob.questionnaire.service.api.session.QuestionnaireSession;
import io.dialob.questionnaire.service.api.session.QuestionnaireSessionService;
import io.dialob.security.tenant.Tenant;
import io.dialob.settings.DialobSettings;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.mockito.internal.verification.api.VerificationData;
import org.mockito.verification.VerificationMode;
import org.springframework.core.task.TaskExecutor;
import org.springframework.http.HttpHeaders;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import tools.jackson.databind.ObjectMapper;

import java.net.InetSocketAddress;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class QuestionnaireWebSocketHandlerTest {

  private AutoCloseable mocks;

  @BeforeEach
  public void reset() {
    mocks = MockitoAnnotations.openMocks(this);
  }

  @Mock
  public ActionProcessingService actionProcessingService;

  @Spy
  public ObjectMapper mapper = new ObjectMapper();

  @Spy
  public DialobSettings settings = new DialobSettings();

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
  void shouldDelegateSessionInitToAsyncTask() throws Exception {
    final WebSocketSession webSocketSession = mockWebSocketSessionFrom("localhost", 9999);
    final QuestionnaireSession questionnaireSession = Mockito.mock(QuestionnaireSession.class);
    final Map<String, Object> attributes = new HashMap<>();
    attributes.put("sessionId", "123-321");
    when(webSocketSession.getAttributes()).thenReturn(attributes);
    final HttpHeaders httpHeaders = Mockito.mock(HttpHeaders.class);
    when(webSocketSession.getHandshakeHeaders()).thenReturn(httpHeaders);
    doAnswer(invocation -> {
      ((Runnable) invocation.getArgument(0)).run();
      return null;
    }).when(taskExecutor).execute(any());
    when(questionnaireSessionService.findOne("123-321")).thenReturn(questionnaireSession);
    when(questionnaireSession.getRevision()).thenReturn("123");
    doAnswer(invocation -> {
      QuestionnaireSession.UpdatesCallback callback = invocation.getArgument(0);
      ActionItem textQuestion = new ActionItem.Builder()
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
  void shouldSendQuestionnaireNotFoundMessageIfQuestionnaireIsNotFound() throws Exception {
    final WebSocketSession webSocketSession = mockWebSocketSessionFrom("localhost", 9999);
    final Map<String, Object> attributes = new HashMap<>();
    attributes.put("sessionId", "123-321");
    when(webSocketSession.getAttributes()).thenReturn(attributes);
    final HttpHeaders httpHeaders = Mockito.mock(HttpHeaders.class);
    when(webSocketSession.getHandshakeHeaders()).thenReturn(httpHeaders);
    doAnswer(invocation -> {
      ((Runnable) invocation.getArgument(0)).run();
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

  // ========== Tests for onQuestionnaireCompletedEvent ==========

  @Test
  void shouldSendCompleteActionWhenQuestionnaireCompletedEventIsForThisHandler() throws Exception {
    // Setup the handler with a questionnaire ID
    final WebSocketSession webSocketSession = mockWebSocketSessionFrom("localhost", 9999);
    final Map<String, Object> attributes = new HashMap<>();
    attributes.put("sessionId", "questionnaire-123");
    when(webSocketSession.getAttributes()).thenReturn(attributes);
    when(webSocketSession.getId()).thenReturn("session-abc");
    final HttpHeaders httpHeaders = Mockito.mock(HttpHeaders.class);
    when(webSocketSession.getHandshakeHeaders()).thenReturn(httpHeaders);

    // Initialize the handler to set questionnaireId
    doAnswer(invocation -> {
      // Don't execute the task for initialization
      return null;
    }).when(taskExecutor).execute(any());

    questionnaireWebSocketHandler.afterConnectionEstablished(webSocketSession);

    // Create a completed event for the same questionnaire
    QuestionnaireCompletedEvent event = new QuestionnaireCompletedEvent(
      Tenant.of("test-tenant"),
      "questionnaire-123"
    );

    // Call the method under test
    questionnaireWebSocketHandler.onQuestionnaireCompletedEvent(event);

    // Verify that a COMPLETE action was sent
    verify(webSocketSession, new VerificationMode() {
      @Override
      public void verify(VerificationData data) {
        // Find the sendMessage call (should be the last one after initialization)
        boolean foundCompleteMessage = false;
        for (int i = data.getAllInvocations().size() - 1; i >= 0; i--) {
          if ("sendMessage".equals(data.getAllInvocations().get(i).getMethod().getName())) {
            TextMessage textMessage = data.getAllInvocations().get(i).getArgument(0);
            String message = new String(textMessage.asBytes());
            if (message.contains("\"type\":\"COMPLETE\"") &&
                message.contains("\"id\":\"questionnaire-123\"")) {
              foundCompleteMessage = true;
              break;
            }
          }
        }
        assertEquals(true, foundCompleteMessage, "Expected COMPLETE action to be sent");
      }

      @Override
      public VerificationMode description(String description) {
        return this;
      }
    }).sendMessage(any(TextMessage.class));
  }

  @Test
  void shouldNotSendMessageWhenQuestionnaireCompletedEventIsNotForThisHandler() throws Exception {
    // Setup the handler with a questionnaire ID
    final WebSocketSession webSocketSession = mockWebSocketSessionFrom("localhost", 9999);
    final Map<String, Object> attributes = new HashMap<>();
    attributes.put("sessionId", "questionnaire-123");
    when(webSocketSession.getAttributes()).thenReturn(attributes);
    when(webSocketSession.getId()).thenReturn("session-abc");
    final HttpHeaders httpHeaders = Mockito.mock(HttpHeaders.class);
    when(webSocketSession.getHandshakeHeaders()).thenReturn(httpHeaders);

    // Initialize the handler to set questionnaireId
    doAnswer(invocation -> {
      // Don't execute the task for initialization
      return null;
    }).when(taskExecutor).execute(any());

    questionnaireWebSocketHandler.afterConnectionEstablished(webSocketSession);

    // Clear the mock to track only new interactions
    Mockito.clearInvocations(webSocketSession);

    // Create a completed event for a DIFFERENT questionnaire
    QuestionnaireCompletedEvent event = new QuestionnaireCompletedEvent(
      Tenant.of("test-tenant"),
      "different-questionnaire-456"
    );

    // Call the method under test
    questionnaireWebSocketHandler.onQuestionnaireCompletedEvent(event);

    // Verify that NO message was sent
    verify(webSocketSession, never()).sendMessage(any(TextMessage.class));
  }

  @Test
  void shouldSendCompleteActionWithCorrectQuestionnaireId() throws Exception {
    // Setup the handler with a questionnaire ID
    final WebSocketSession webSocketSession = mockWebSocketSessionFrom("localhost", 9999);
    final Map<String, Object> attributes = new HashMap<>();
    attributes.put("sessionId", "questionnaire-789");
    when(webSocketSession.getAttributes()).thenReturn(attributes);
    when(webSocketSession.getId()).thenReturn("session-xyz");
    final HttpHeaders httpHeaders = Mockito.mock(HttpHeaders.class);
    when(webSocketSession.getHandshakeHeaders()).thenReturn(httpHeaders);

    // Initialize the handler
    doAnswer(invocation -> {
      return null;
    }).when(taskExecutor).execute(any());

    questionnaireWebSocketHandler.afterConnectionEstablished(webSocketSession);

    // Create a completed event
    QuestionnaireCompletedEvent event = new QuestionnaireCompletedEvent(
      Tenant.of("test-tenant"),
      "questionnaire-789"
    );

    // Call the method under test
    questionnaireWebSocketHandler.onQuestionnaireCompletedEvent(event);

    // Verify the message contains COMPLETE action with correct questionnaire ID
    ArgumentCaptor<TextMessage> messageCaptor = ArgumentCaptor.forClass(TextMessage.class);
    verify(webSocketSession, atLeastOnce()).sendMessage(messageCaptor.capture());

    // Find the COMPLETE message in the captured messages
    boolean foundCompleteMessage = false;
    for (TextMessage msg : messageCaptor.getAllValues()) {
      String message = new String(msg.asBytes());
      if (message.contains("\"type\":\"COMPLETE\"") && message.contains("\"id\":\"questionnaire-789\"")) {
        foundCompleteMessage = true;
        break;
      }
    }
    assertEquals(true, foundCompleteMessage, "Expected to find a COMPLETE action message with correct questionnaire ID");
  }

  @Test
  void shouldNotSendMessageWhenSessionIsClosed() throws Exception {
    // Setup the handler with a questionnaire ID
    final WebSocketSession webSocketSession = mockWebSocketSessionFrom("localhost", 9999);
    final Map<String, Object> attributes = new HashMap<>();
    attributes.put("sessionId", "questionnaire-123");
    when(webSocketSession.getAttributes()).thenReturn(attributes);
    when(webSocketSession.getId()).thenReturn("session-abc");
    final HttpHeaders httpHeaders = Mockito.mock(HttpHeaders.class);
    when(webSocketSession.getHandshakeHeaders()).thenReturn(httpHeaders);

    // Initialize the handler
    doAnswer(invocation -> {
      return null;
    }).when(taskExecutor).execute(any());

    questionnaireWebSocketHandler.afterConnectionEstablished(webSocketSession);

    // Close the session
    when(webSocketSession.isOpen()).thenReturn(false);

    // Clear the mock to track only new interactions
    Mockito.clearInvocations(webSocketSession);

    // Create a completed event
    QuestionnaireCompletedEvent event = new QuestionnaireCompletedEvent(
      Tenant.of("test-tenant"),
      "questionnaire-123"
    );
    // Call the method under test
    questionnaireWebSocketHandler.onQuestionnaireCompletedEvent(event);

    // Verify that NO message was sent because session is closed
    verify(webSocketSession, never()).sendMessage(any(TextMessage.class));
  }

  // ========== Tests for onQuestionnaireActionsEvent ==========

  @Test
  void shouldSendActionsWhenQuestionnaireActionsEventIsForThisHandler() throws Exception {
    // Setup the handler with a questionnaire ID
    final WebSocketSession webSocketSession = mockWebSocketSessionFrom("localhost", 9999);
    final Map<String, Object> attributes = new HashMap<>();
    attributes.put("sessionId", "questionnaire-123");
    when(webSocketSession.getAttributes()).thenReturn(attributes);
    when(webSocketSession.getId()).thenReturn("session-abc");
    final HttpHeaders httpHeaders = Mockito.mock(HttpHeaders.class);
    when(webSocketSession.getHandshakeHeaders()).thenReturn(httpHeaders);

    // Initialize the handler
    doAnswer(invocation -> {
      return null;
    }).when(taskExecutor).execute(any());

    questionnaireWebSocketHandler.afterConnectionEstablished(webSocketSession);

    // Create an actions event
    Actions actions = new Actions.Builder()
      .addActions(new Action.Builder().type(Action.Type.ANSWER).id("q1").answer("a1").build())
      .build();
    QuestionnaireActionsEvent event = new QuestionnaireActionsEvent(
      Tenant.of("test-tenant"),
      "questionnaire-123",
      actions
    );

    // Call the method under test
    questionnaireWebSocketHandler.onQuestionnaireActionsEvent(event);

    // Verify that the actions were sent
    ArgumentCaptor<TextMessage> messageCaptor = ArgumentCaptor.forClass(TextMessage.class);
    verify(webSocketSession, atLeastOnce()).sendMessage(messageCaptor.capture());

    boolean foundActionsMessage = false;
    for (TextMessage msg : messageCaptor.getAllValues()) {
      String message = new String(msg.asBytes());
      if (message.contains("\"type\":\"ANSWER\"") && message.contains("\"id\":\"q1\"") && message.contains("\"answer\":\"a1\"")) {
        foundActionsMessage = true;
        break;
      }
    }
    assertEquals(true, foundActionsMessage, "Expected actions to be sent");
  }

  @Test
  void shouldFilterActionsFromSameSession() throws Exception {
    // Setup the handler with a questionnaire ID
    final WebSocketSession webSocketSession = mockWebSocketSessionFrom("localhost", 9999);
    final Map<String, Object> attributes = new HashMap<>();
    attributes.put("sessionId", "questionnaire-123");
    when(webSocketSession.getAttributes()).thenReturn(attributes);
    when(webSocketSession.getId()).thenReturn("session-abc");
    final HttpHeaders httpHeaders = Mockito.mock(HttpHeaders.class);
    when(webSocketSession.getHandshakeHeaders()).thenReturn(httpHeaders);

    // Initialize the handler
    doAnswer(invocation -> {
      return null;
    }).when(taskExecutor).execute(any());

    questionnaireWebSocketHandler.afterConnectionEstablished(webSocketSession);

    // Create an actions event with one action from this session and one from another
    Actions actions = new Actions.Builder()
      .addActions(new Action.Builder().type(Action.Type.ANSWER).id("q1").answer("a1").resourceId("session-abc").build()) // From this session
      .addActions(new Action.Builder().type(Action.Type.ANSWER).id("q2").answer("a2").resourceId("other-session").build()) // From other session
      .build();
    QuestionnaireActionsEvent event = new QuestionnaireActionsEvent(
      Tenant.of("test-tenant"),
      "questionnaire-123",
      actions
    );

    // Call the method under test
    questionnaireWebSocketHandler.onQuestionnaireActionsEvent(event);

    // Verify that only the action from the other session was sent
    ArgumentCaptor<TextMessage> messageCaptor = ArgumentCaptor.forClass(TextMessage.class);
    verify(webSocketSession, atLeastOnce()).sendMessage(messageCaptor.capture());

    boolean foundFilteredMessage = false;
    for (TextMessage msg : messageCaptor.getAllValues()) {
      String message = new String(msg.asBytes());
      if (message.contains("\"id\":\"q2\"") && !message.contains("\"id\":\"q1\"")) {
        foundFilteredMessage = true;
        break;
      }
    }
    assertEquals(true, foundFilteredMessage, "Expected only actions from other sessions to be sent");
  }

  @Test
  void shouldNotSendActionsWhenQuestionnaireActionsEventIsNotForThisHandler() throws Exception {
    // Setup the handler with a questionnaire ID
    final WebSocketSession webSocketSession = mockWebSocketSessionFrom("localhost", 9999);
    final Map<String, Object> attributes = new HashMap<>();
    attributes.put("sessionId", "questionnaire-123");
    when(webSocketSession.getAttributes()).thenReturn(attributes);
    when(webSocketSession.getId()).thenReturn("session-abc");
    final HttpHeaders httpHeaders = Mockito.mock(HttpHeaders.class);
    when(webSocketSession.getHandshakeHeaders()).thenReturn(httpHeaders);

    // Initialize the handler
    doAnswer(invocation -> {
      return null;
    }).when(taskExecutor).execute(any());

    questionnaireWebSocketHandler.afterConnectionEstablished(webSocketSession);

    // Clear invocations
    Mockito.clearInvocations(webSocketSession);

    // Create an actions event for a DIFFERENT questionnaire
    Actions actions = new Actions.Builder()
      .addActions(new Action.Builder().type(Action.Type.ANSWER).id("q1").answer("a1").build())
      .build();
    QuestionnaireActionsEvent event = new QuestionnaireActionsEvent(
      Tenant.of("test-tenant"),
      "other-questionnaire",
      actions
    );

    // Call the method under test
    questionnaireWebSocketHandler.onQuestionnaireActionsEvent(event);

    // Verify that NO message was sent
    verify(webSocketSession, never()).sendMessage(any(TextMessage.class));
  }

  @Test
  void shouldHandleTextMessage() throws Exception {
    // Setup the handler with a questionnaire ID
    final WebSocketSession webSocketSession = mockWebSocketSessionFrom("localhost", 9999);
    final Map<String, Object> attributes = new HashMap<>();
    attributes.put("sessionId", "questionnaire-123");
    when(webSocketSession.getAttributes()).thenReturn(attributes);
    when(webSocketSession.getId()).thenReturn("session-abc");
    final HttpHeaders httpHeaders = Mockito.mock(HttpHeaders.class);
    when(webSocketSession.getHandshakeHeaders()).thenReturn(httpHeaders);

    // Initialize the handler
    doAnswer(invocation -> {
      return null;
    }).when(taskExecutor).execute(any());

    questionnaireWebSocketHandler.afterConnectionEstablished(webSocketSession);

    // Prepare incoming message
    String payload = "{\"actions\":[{\"type\":\"ANSWER\",\"id\":\"q1\",\"answer\":\"a1\"}],\"rev\":\"rev1\"}";
    TextMessage message = new TextMessage(payload);

    // Mock action processing
    Actions resultActions = new Actions.Builder()
      .rev("rev2")
      .addActions(new Action.Builder().type(Action.Type.ITEM).id("q2").build())
      .build();
    when(actionProcessingService.answerQuestion(eq("questionnaire-123"), eq("rev1"), anyList())).thenReturn(resultActions);

    // Call the method under test
    questionnaireWebSocketHandler.handleTextMessage(webSocketSession, message);

    // Verify that actionProcessingService was called
    verify(actionProcessingService).answerQuestion(eq("questionnaire-123"), eq("rev1"), anyList());
  }

  @Test
  void shouldHandleTextMessageEmptyActions() throws Exception {
    // Setup the handler with a questionnaire ID
    final WebSocketSession webSocketSession = mockWebSocketSessionFrom("localhost", 9999);
    final Map<String, Object> attributes = new HashMap<>();
    attributes.put("sessionId", "questionnaire-123");
    when(webSocketSession.getAttributes()).thenReturn(attributes);
    when(webSocketSession.getId()).thenReturn("session-abc");
    final HttpHeaders httpHeaders = Mockito.mock(HttpHeaders.class);
    when(webSocketSession.getHandshakeHeaders()).thenReturn(httpHeaders);

    // Initialize the handler
    doAnswer(invocation -> {
      return null;
    }).when(taskExecutor).execute(any());

    questionnaireWebSocketHandler.afterConnectionEstablished(webSocketSession);

    // Prepare incoming message
    String payload = "{\"actions\":[],\"rev\":\"rev1\"}";
    TextMessage message = new TextMessage(payload);


    // Call the method under test
    questionnaireWebSocketHandler.handleTextMessage(webSocketSession, message);

    // Verify that actionProcessingService was called
    verifyNoMoreInteractions(actionProcessingService);
  }

  @Test
  void shouldHandleTextMessageSkipServerActionsFromClient() throws Exception {
    // Setup the handler with a questionnaire ID
    final WebSocketSession webSocketSession = mockWebSocketSessionFrom("localhost", 9999);
    final Map<String, Object> attributes = new HashMap<>();
    attributes.put("sessionId", "questionnaire-123");
    when(webSocketSession.getAttributes()).thenReturn(attributes);
    when(webSocketSession.getId()).thenReturn("session-abc");
    final HttpHeaders httpHeaders = Mockito.mock(HttpHeaders.class);
    when(webSocketSession.getHandshakeHeaders()).thenReturn(httpHeaders);

    // Initialize the handler
    doAnswer(invocation -> {
      return null;
    }).when(taskExecutor).execute(any());

    questionnaireWebSocketHandler.afterConnectionEstablished(webSocketSession);

    // Prepare incoming message
    String payload = "{\"actions\":[{\"type\":\"RESET\"}],\"rev\":\"rev1\"}";
    TextMessage message = new TextMessage(payload);


    // Call the method under test
    questionnaireWebSocketHandler.handleTextMessage(webSocketSession, message);

    // Verify that actionProcessingService was called
    verifyNoMoreInteractions(actionProcessingService);
  }

  @AfterEach
  void tearDown() throws Exception {
    mocks.close();
  }

}
