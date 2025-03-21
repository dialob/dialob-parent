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
package io.dialob.questionnaire.service.api.event;

import io.dialob.api.proto.Actions;
import io.dialob.api.proto.ImmutableActions;
import io.dialob.integration.api.event.EventPublisher;
import io.dialob.security.tenant.CurrentTenant;
import io.dialob.security.tenant.ResysSecurityConstants;
import io.dialob.security.tenant.Tenant;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.net.InetAddress;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class QuestionnaireEventPublisherTest {

  @Mock
  private EventPublisher eventPublisher;

  @Mock
  private CurrentTenant currentTenant;

  @InjectMocks
  private QuestionnaireEventPublisher questionnaireEventPublisher;

  @Test
  void publishesQuestionnaireOpenedEvent() {
    String questionnaireId = "q1";
    Tenant tenant = Tenant.of("tenant1");
    when(currentTenant.get()).thenReturn(tenant);

    questionnaireEventPublisher.opened(questionnaireId);

    verify(eventPublisher).publish(ImmutableQuestionnaireOpenedEvent.builder()
      .tenant(tenant)
      .questionnaireId(questionnaireId)
      .build());
  }

  @Test
  void publishesQuestionnaireCreatedEvent() {
    String questionnaireId = "q2";
    Tenant tenant = Tenant.of("tenant2");
    when(currentTenant.get()).thenReturn(tenant);

    questionnaireEventPublisher.created(questionnaireId);

    verify(eventPublisher).publish(ImmutableQuestionnaireCreatedEvent.builder()
      .tenant(tenant)
      .questionnaireId(questionnaireId)
      .build());
  }

  @Test
  void publishesQuestionnaireCompletedEventWithTenantId() {
    String tenantId = "tenant3";
    String questionnaireId = "q3";
    Tenant tenant = Tenant.of(tenantId);

    questionnaireEventPublisher.completed(tenantId, questionnaireId);

    verify(eventPublisher).publish(ImmutableQuestionnaireCompletedEvent.builder()
      .tenant(tenant)
      .questionnaireId(questionnaireId)
      .build());
  }

  @Test
  void publishesQuestionnaireCompletedEventWithDefaultTenant() {
    String questionnaireId = "q4";

    questionnaireEventPublisher.completed(null, questionnaireId);

    verify(eventPublisher).publish(ImmutableQuestionnaireCompletedEvent.builder()
      .tenant(ResysSecurityConstants.DEFAULT_TENANT)
      .questionnaireId(questionnaireId)
      .build());
  }

  @Test
  void publishesQuestionnaireActionsEvent() {
    String questionnaireId = "q5";
    Actions actions = ImmutableActions.builder().build();
    Tenant tenant = Tenant.of("tenant4");
    when(currentTenant.get()).thenReturn(tenant);

    questionnaireEventPublisher.actions(questionnaireId, actions);

    verify(eventPublisher).publish(ImmutableQuestionnaireActionsEvent.builder()
      .tenant(tenant)
      .questionnaireId(questionnaireId)
      .actions(actions)
      .build());
  }

  @Test
  void publishesClientConnectedEvent() {
    String questionnaireId = "q6";
    InetAddress client = mock();
    Tenant tenant = Tenant.of("tenant5");
    when(currentTenant.get()).thenReturn(tenant);

    questionnaireEventPublisher.clientConnected(questionnaireId, client);

    verify(eventPublisher).publish(ImmutableQuestionnaireClientConnectedEvent.builder()
      .tenant(tenant)
      .questionnaireId(questionnaireId)
      .client(client)
      .build());
  }

  @Test
  void publishesClientDisconnectedEvent() {
    String questionnaireId = "q7";
    InetAddress client = mock();
    int closeStatus = 1000;
    Tenant tenant = Tenant.of("tenant6");
    when(currentTenant.get()).thenReturn(tenant);

    questionnaireEventPublisher.clientDisconnected(questionnaireId, client, closeStatus);

    verify(eventPublisher).publish(ImmutableQuestionnaireClientDisconnectedEvent.builder()
      .tenant(tenant)
      .questionnaireId(questionnaireId)
      .client(client)
      .closeStatus(closeStatus)
      .build());
  }
}
