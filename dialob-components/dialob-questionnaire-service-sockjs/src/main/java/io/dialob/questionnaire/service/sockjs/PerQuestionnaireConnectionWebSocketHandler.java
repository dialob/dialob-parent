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

import io.dialob.questionnaire.service.api.event.QuestionnaireActionsEvent;
import io.dialob.questionnaire.service.api.event.QuestionnaireCompletedEvent;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.noop.NoopCounter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.lang.NonNull;
import org.springframework.util.Assert;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.BeanCreatingHandlerProvider;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class PerQuestionnaireConnectionWebSocketHandler implements WebSocketHandler, BeanFactoryAware, HealthIndicator, QuestionnaireEventsHandler {

  public static final String CONNECTIONS_METRIC_NAME = "questionnaire.websocket.connections";

  private final BeanCreatingHandlerProvider<QuestionnaireWebSocketHandler> provider;

  private final Map<WebSocketSession, QuestionnaireWebSocketHandler> handlers =
    new ConcurrentHashMap<>();

  private final boolean supportsPartialMessages;

  private final Counter connectionsCounter;

  public PerQuestionnaireConnectionWebSocketHandler(Optional<MeterRegistry> meterRegistry) {
    this(QuestionnaireWebSocketHandler.class, false, meterRegistry);
  }

  public PerQuestionnaireConnectionWebSocketHandler(Class<QuestionnaireWebSocketHandler> handlerType, boolean supportsPartialMessages, Optional<MeterRegistry> meterRegistry) {
    this.connectionsCounter = meterRegistry
        .map(mr -> Counter.builder(CONNECTIONS_METRIC_NAME).register(mr))
        .orElseGet(() -> new NoopCounter(null));
    this.provider = new BeanCreatingHandlerProvider<>(handlerType);
    this.supportsPartialMessages = supportsPartialMessages;
  }

  @Override
  public void setBeanFactory(@NonNull BeanFactory beanFactory) {
    this.provider.setBeanFactory(beanFactory);
  }

  @Override
  public void afterConnectionEstablished(@NonNull WebSocketSession session) throws Exception {
    QuestionnaireWebSocketHandler handler = this.provider.getHandler();
    this.handlers.put(session, handler);
    connectionsCounter.increment();
    handler.afterConnectionEstablished(session);
  }

  @Override
  public void handleMessage(@NonNull WebSocketSession session, @NonNull WebSocketMessage<?> message) throws Exception {
    getHandler(session).handleMessage(session, message);
  }

  private QuestionnaireWebSocketHandler getHandler(WebSocketSession session) {
    QuestionnaireWebSocketHandler handler = this.handlers.get(session);
    Assert.isTrue(handler != null, "WebSocketHandler not found for " + session);
    return handler;
  }

  @Override
  public void handleTransportError(@NonNull WebSocketSession session, @NonNull Throwable exception) throws Exception {
    getHandler(session).handleTransportError(session, exception);
  }

  @Override
  public void afterConnectionClosed(@NonNull WebSocketSession session, @NonNull CloseStatus closeStatus) throws Exception {
    LOGGER.debug("afterConnectionClosed(\"{}\",{})", session.getId(), closeStatus);
    try {
      getHandler(session).afterConnectionClosed(session, closeStatus);
    } finally {
      destroy(session);
    }
  }

  @Override
  public boolean supportsPartialMessages() {
    return supportsPartialMessages;
  }

  private void destroy(WebSocketSession session) {
    QuestionnaireWebSocketHandler handler = this.handlers.remove(session);
    connectionsCounter.increment(-1);
    try {
      if (handler != null) {
        this.provider.destroy(handler);
      }
    } catch (Exception t) {
      LOGGER.warn("Error while destroying " + handler, t);
    }
  }


  @Override
  public String toString() {
    return "PerConnectionWebSocketHandlerProxy[handlerType=" + this.provider.getHandlerType() + "]";
  }

  @Override
  public void onQuestionnaireActionsEvent(QuestionnaireActionsEvent event) {
    // how to map event.getQuestionnaireId() -> Set<session> ??
    this.handlers.values().forEach(handler -> handler.onQuestionnaireActionsEvent(event));
  }

  @Override
  public void onQuestionnaireCompletedEvent(QuestionnaireCompletedEvent event) {
    this.handlers.values().forEach(handler -> handler.onQuestionnaireCompletedEvent(event));
  }

  @Override
  public Health health() {
    return Health.up().build();
  }
}
