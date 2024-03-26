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
package io.dialob.questionnaire.service.api.event;

import edu.umd.cs.findbugs.annotations.NonNull;
import io.dialob.api.proto.Actions;
import io.dialob.integration.api.event.EventPublisher;
import io.dialob.security.tenant.CurrentTenant;
import io.dialob.security.tenant.ImmutableTenant;
import io.dialob.security.tenant.ResysSecurityConstants;
import io.dialob.security.tenant.Tenant;

import java.net.InetAddress;
import java.util.Optional;

public class QuestionnaireEventPublisher {

  private final EventPublisher applicationEventPublisher;

  private final CurrentTenant currentTenant;

  private Tenant getTenant() {
    Tenant tenant = currentTenant.get();
    if (tenant == null) {
      return ResysSecurityConstants.DEFAULT_TENANT;
    }
    return tenant;
  }

  private Tenant getTenant(String tenantId) {
    if (tenantId != null) {
      return ImmutableTenant.of(tenantId, Optional.empty());
    }
    return ResysSecurityConstants.DEFAULT_TENANT;
  }

  public QuestionnaireEventPublisher(@NonNull EventPublisher applicationEventPublisher, CurrentTenant currentTenant) {
    this.applicationEventPublisher = applicationEventPublisher;
    this.currentTenant = currentTenant;
  }

  public void opened(@NonNull String questionnaireId) {
    applicationEventPublisher.publish(ImmutableQuestionnaireOpenedEvent.builder()
      .tenant(getTenant())
      .questionnaireId(questionnaireId)
      .build());
  }

  public void created(@NonNull String questionnaireId) {
    applicationEventPublisher.publish(ImmutableQuestionnaireCreatedEvent.builder()
      .tenant(getTenant())
      .questionnaireId(questionnaireId)
      .build());
  }

  public void completed(String tenantId, @NonNull String questionnaireId) {
    applicationEventPublisher.publish(ImmutableQuestionnaireCompletedEvent.builder()
      .tenant(getTenant(tenantId))
      .questionnaireId(questionnaireId)
      .build());
  }

  public void actions(@NonNull String questionnaireId, @NonNull Actions actions) {
    applicationEventPublisher.publish(ImmutableQuestionnaireActionsEvent.builder()
      .tenant(getTenant())
      .questionnaireId(questionnaireId)
      .actions(actions)
      .build());
  }

  public void clientConnected(@NonNull String questionnaireId, InetAddress client) {
    applicationEventPublisher.publish(ImmutableQuestionnaireClientConnectedEvent.builder()
      .tenant(getTenant())
      .questionnaireId(questionnaireId)
      .client(client)
      .build());
  }

  public void clientDisconnected(@NonNull String questionnaireId, InetAddress client, int closeStatus) {
    applicationEventPublisher.publish(ImmutableQuestionnaireClientDisconnectedEvent.builder()
      .tenant(getTenant())
      .questionnaireId(questionnaireId)
      .client(client)
      .closeStatus(closeStatus)
      .build());
  }
}
