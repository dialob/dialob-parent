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

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.task.TaskExecutor;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.ConcurrentWebSocketSessionDecorator;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.springframework.web.socket.sockjs.SockJsTransportFailureException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.net.HttpHeaders;
import com.google.common.net.InetAddresses;

import io.dialob.api.proto.Action;
import io.dialob.api.proto.Actions;
import io.dialob.api.proto.ImmutableAction;
import io.dialob.api.proto.ImmutableActions;
import io.dialob.db.spi.exceptions.DocumentNotFoundException;
import io.dialob.questionnaire.service.api.ActionProcessingService;
import io.dialob.questionnaire.service.api.FormActions;
import io.dialob.questionnaire.service.api.FormActionsUpdatesCallback;
import io.dialob.questionnaire.service.api.QuestionnaireActionsService;
import io.dialob.questionnaire.service.api.event.QuestionnaireActionsEvent;
import io.dialob.questionnaire.service.api.event.QuestionnaireCompletedEvent;
import io.dialob.questionnaire.service.api.event.QuestionnaireEvent;
import io.dialob.questionnaire.service.api.event.QuestionnaireEventPublisher;
import io.dialob.questionnaire.service.api.session.QuestionnaireSession;
import io.dialob.questionnaire.service.api.session.QuestionnaireSessionService;
import io.dialob.security.tenant.ImmutableTenant;
import io.dialob.security.tenant.ResysSecurityConstants;
import io.dialob.security.tenant.Tenant;
import io.dialob.security.tenant.TenantContextHolderCurrentTenant;
import io.dialob.settings.DialobSettings;
import io.dialob.settings.SessionSettings;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class QuestionnaireWebSocketHandler extends TextWebSocketHandler implements QuestionnaireActionsService {

  private final SessionSettings.SockJSSettings settings;

  private final QuestionnaireEventPublisher eventPublisher;

  private final ActionProcessingService actionProcessingService;

  private final ObjectMapper mapper;

  private final QuestionnaireSessionService questionnaireSessionService;

  private final TaskExecutor taskExecutor;

  private String questionnaireId;

  @NonNull
  private Tenant tenant = ResysSecurityConstants.DEFAULT_TENANT;

  private WebSocketSession session;

  private boolean reportStackTrace = true;

  public QuestionnaireWebSocketHandler(
    final DialobSettings settings,
    final QuestionnaireEventPublisher eventPublisher,
    final ActionProcessingService actionProcessingService,
    final ObjectMapper mapper,
    final QuestionnaireSessionService questionnaireSessionService,
    @Qualifier("applicationTaskExecutor") final TaskExecutor taskExecutor)
  {
    this.settings = settings.getSession().getSockjs();
    this.eventPublisher = eventPublisher;
    this.actionProcessingService = actionProcessingService;
    this.mapper = mapper;
    this.questionnaireSessionService = questionnaireSessionService;
    this.taskExecutor = taskExecutor;
  }

  // Note! If execution of method takes too long, client will fallback to slower connection methods.
  @Override
  public void afterConnectionEstablished(WebSocketSession session) throws Exception {
    this.session = new ConcurrentWebSocketSessionDecorator(session, settings.getSendTimeLimit(), settings.getMaxBinaryMessageBufferSize());
    final Map<String, Object> sessionAttributes = session.getAttributes();
    this.questionnaireId = (String) sessionAttributes.get(settings.getUrlAttributes().getSessionId());
    String tenantId = (String) sessionAttributes.get(settings.getUrlAttributes().getTenantId());
    if (tenantId != null && !isDefaultTenantPathPlaceholder(tenantId)) {
      this.tenant = ImmutableTenant.of(tenantId, Optional.empty());
    }
    TenantContextHolderCurrentTenant.runInTenantContext(this.tenant, () -> {
      publishConnectionEvent();
      sendFullForm();
    });
  }

  protected boolean isDefaultTenantPathPlaceholder(String tenantId) {
    return "-".equals(tenantId);
  }

  protected void sendFullForm() {
    taskExecutor.execute(() -> {
      final ImmutableActions.Builder actions = ImmutableActions.builder();
      try {
        QuestionnaireSession questionnaireSession = questionnaireSessionService.findOne(this.questionnaireId);
        FormActions formActions = new FormActions();
        questionnaireSession.buildFullForm(new FormActionsUpdatesCallback(formActions));
        actions.actions(formActions.getActions());
        String revision = questionnaireSession.getRevision();
        actions.rev(revision);
      } catch(DocumentNotFoundException e) {
        LOGGER.debug("Action QUESTIONNAIRE_NOT_FOUND: backend response '{}'", e.getMessage());
        actions.addActions(ImmutableAction.builder()
          .type(Action.Type.SERVER_ERROR)
          .serverEvent(true)
          .message("not found")
          .id(this.questionnaireId).build());
      } catch(Exception e) {
        LOGGER.error("Websocket handler failed: {}", e.getMessage());
        LOGGER.debug("Error in websocket handler", e);
        actions.actions(Collections.singletonList(createNotifyServerErrorAction(e)));
      }
      sendMessage(actions.build());
    });
  }

  protected void publishConnectionEvent() {
    InetAddress remoteAddress = resolveRealIp();
    LOGGER.info("WebSocket session '{}' from {} trying connect to '{}'", this.session.getId(), remoteAddress, this.questionnaireId);
    eventPublisher.clientConnected(questionnaireId, remoteAddress);
  }

  protected void publishDisconnectionEvent(CloseStatus closeStatus) {
    InetAddress remoteAddress = resolveRealIp();
    eventPublisher.clientDisconnected(StringUtils.defaultString(questionnaireId), remoteAddress, closeStatus.getCode());
    LOGGER.info("WebSocket session '{}' from {} disconnected from '{}'", this.session.getId(), remoteAddress, this.questionnaireId);
  }

  @Nullable
  private InetAddress resolveRealIp() {
    List<String> realIp = this.session.getHandshakeHeaders().getValuesAsList("X-Real-IP");
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("X-Real-IP {}", realIp);
      LOGGER.debug("X-Forwarded-For {}", this.session.getHandshakeHeaders().getValuesAsList(HttpHeaders.X_FORWARDED_FOR));
    }
    if (!realIp.isEmpty()) {

      return InetAddresses.forString(realIp.get(0));
    } else {
      List<String> forwardFor = this.session.getHandshakeHeaders().getValuesAsList(HttpHeaders.X_FORWARDED_FOR);
      if (!forwardFor.isEmpty()) {
        return InetAddresses.forString(forwardFor.get(0));
      }
    }
    InetSocketAddress remoteAddress = this.session.getRemoteAddress();
    if (remoteAddress != null) {
      return remoteAddress.getAddress();
    }
    return null;
  }

  @Override
  public void handleTextMessage(WebSocketSession session, TextMessage message) {
    TenantContextHolderCurrentTenant.runInTenantContext(this.tenant, () -> {
      final List<Action> actionList = new ArrayList<>();
      String prevRev = null;
      final String id = this.session.getId();
      try {
        MDC.put("socketSession", id);
        final Actions actions = mapper.readValue(message.getPayload(), Actions.class);
        prevRev = actions.getRev();
        List<Action> actions1 = actions.getActions();
        if (actions1 == null || actions1.isEmpty()) {
          LOGGER.info("Resource '{}' sent empty message.", id);
          return;
        }
        LOGGER.info("Resource '{}' sent {} action(s)", id, actions1.size());
        for (final Action action : actions1) {
          // TODO Is there better solution to prevent broadcast loop?
          if (action.getServerEvent() == null || !action.getServerEvent()) {
            handleAction(questionnaireId, ImmutableAction.builder().from(action).resourceId(id).build(), actions.getRev());

          }
        }
      } catch (IOException e) {
        LOGGER.info("unparseable message from client {} due error {}", id, e.getMessage());
        LOGGER.debug("message payload: {}", message.getPayload());
        return;
      } catch (Exception e) {
        LOGGER.debug("Server side error,", e);
        actionList.add(createNotifyServerErrorAction(e));
      } finally {
        MDC.remove("socketSession");
      }

      if (!actionList.isEmpty()) {
        Actions returnActions = ImmutableActions.builder()
          .rev(prevRev)
          .actions(actionList.stream().map(action -> ImmutableAction.builder().from(action).serverEvent(true).build()).collect(Collectors.toList()))
          .build();
        sendMessage(returnActions);
      }
    });
  }

  @Override
  public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
    TenantContextHolderCurrentTenant.runInTenantContext(this.tenant, () ->
      LOGGER.error("WebSocket transport error. " + this.session.getId(), exception));
  }

  @Override
  public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
    TenantContextHolderCurrentTenant.runInTenantContext(this.tenant, () -> {
      LOGGER.debug("WebSocket connection closed {} status {}", this.session.getId(), closeStatus);
      publishDisconnectionEvent(closeStatus);
      this.session = null;
    });
  }

  @Override
  public boolean supportsPartialMessages() {
    return false;
  }

  @NonNull
  @Override
  public Actions answerQuestion(@NonNull String questionnaireId, String revision, @NonNull List<Action> actions) {
    return actionProcessingService.answerQuestion(questionnaireId, revision, actions);
  }

  public void onQuestionnaireActionsEvent(QuestionnaireActionsEvent event) {
    if (isForThisHandler(event)) {
      try {
        Actions actions = event.getActions();
        List<Action> filteredActions = actions.getActions().stream()
          .filter(action -> !session.getId().equals(action.getResourceId()))
          .collect(Collectors.toList());
        sendMessage(ImmutableActions.builder().from(actions).actions(filteredActions).build());
      } catch (SockJsTransportFailureException transportFailureException) {
        // Occurs normally when client disconnects unexpectedly. Spring have already
        // closed connection here. We can ignore this exception.
        if (LOGGER.isTraceEnabled()) {
          LOGGER.trace("Unexpected client disconnect detected", transportFailureException);
        } else {
          LOGGER.info("Unexpected client disconnect detected");
        }
      }
    }
  }

  private Action createNotifyServerErrorAction(Exception e) {
    final ImmutableAction.Builder action = ImmutableAction.builder()
      .type(Action.Type.SERVER_ERROR)
      .serverEvent(true);
    if (reportStackTrace) {
      StringWriter sw = new StringWriter();
      e.printStackTrace(new PrintWriter(sw));
      action.message(e.getMessage());
      action.trace(sw.toString());
    }
    return action.build();
  }


  public void onQuestionnaireCompletedEvent(QuestionnaireCompletedEvent event) {
    if (isForThisHandler(event)) {
      sendMessage(ImmutableActions.builder()
        .addActions(ImmutableAction.builder()
          .type(Action.Type.COMPLETE)
          .id(event.getQuestionnaireId())
          .serverEvent(true)
          .build())
        .build());
    }
  }

  private void sendMessage(Actions actions)  {
    TextMessage message = null;
    try {
      message = new TextMessage(this.mapper.writeValueAsString(actions));
      if (sessionIsClosed()) {
        return;
      }
      this.session.sendMessage(message);
    } catch (IOException e) {
      LOGGER.info("unparseable message from client {} due error {}", this.session.getId(), e.getMessage());
      LOGGER.debug("message payload: {}", message != null ? message.getPayload() : actions);
      return;
    }
  }

  protected boolean isForThisHandler(QuestionnaireEvent event) {
    return questionnaireId != null && questionnaireId.equals(event.getQuestionnaireId());
  }

  protected boolean sessionIsClosed() {
    if (this.session == null || !this.session.isOpen()) {
      LOGGER.debug("Dangling socket handler... trying to unsubscribe");
      questionnaireId = null;
      return true;
    }
    return false;
  }
}
