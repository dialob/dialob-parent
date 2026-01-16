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

import io.dialob.questionnaire.service.api.event.QuestionnaireActionsEvent;
import io.dialob.questionnaire.service.api.event.QuestionnaireCompletedEvent;
import io.micrometer.core.instrument.MeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;

import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PerQuestionnaireConnectionWebSocketHandlerTest {

  @Mock
  private AutowireCapableBeanFactory beanFactory;

  @Mock
  private MeterRegistry meterRegistry;

  @Mock
  private QuestionnaireWebSocketHandler questionnaireWebSocketHandler;

  @Mock
  private WebSocketSession session;

  private PerQuestionnaireConnectionWebSocketHandler handler;

  @BeforeEach
  void setUp() {
    when(beanFactory.createBean(QuestionnaireWebSocketHandler.class)).thenReturn(questionnaireWebSocketHandler);

    handler = new PerQuestionnaireConnectionWebSocketHandler(QuestionnaireWebSocketHandler.class, false, Optional.of(meterRegistry));
    handler.setBeanFactory(beanFactory);
  }

  @Test
  void shouldDelegateAfterConnectionEstablished() throws Exception {
    handler.afterConnectionEstablished(session);
    verify(questionnaireWebSocketHandler).afterConnectionEstablished(session);
  }

  @Test
  void shouldDelegateHandleMessage() throws Exception {
    WebSocketMessage<?> message = mock(WebSocketMessage.class);
    handler.afterConnectionEstablished(session);
    handler.handleMessage(session, message);
    verify(questionnaireWebSocketHandler).handleMessage(session, message);
  }

  @Test
  void shouldDelegateHandleTransportError() throws Exception {
    Throwable exception = new RuntimeException();
    handler.afterConnectionEstablished(session);
    handler.handleTransportError(session, exception);
    verify(questionnaireWebSocketHandler).handleTransportError(session, exception);
  }

  @Test
  void shouldDelegateAfterConnectionClosed() throws Exception {
    CloseStatus closeStatus = CloseStatus.NORMAL;
    handler.afterConnectionEstablished(session);
    handler.afterConnectionClosed(session, closeStatus);
    verify(questionnaireWebSocketHandler).afterConnectionClosed(session, closeStatus);
  }

  @Test
  void shouldDelegateOnQuestionnaireActionsEvent() throws Exception {
    QuestionnaireActionsEvent event = mock(QuestionnaireActionsEvent.class);
    handler.afterConnectionEstablished(session);
    handler.onQuestionnaireActionsEvent(event);
    verify(questionnaireWebSocketHandler).onQuestionnaireActionsEvent(event);
  }

  @Test
  void shouldDelegateOnQuestionnaireCompletedEvent() throws Exception {
    QuestionnaireCompletedEvent event = mock(QuestionnaireCompletedEvent.class);
    handler.afterConnectionEstablished(session);
    handler.onQuestionnaireCompletedEvent(event);
    verify(questionnaireWebSocketHandler).onQuestionnaireCompletedEvent(event);
  }
}
