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

import edu.umd.cs.findbugs.annotations.NonNull;
import io.dialob.api.proto.Actions;
import io.dialob.integration.api.event.EventPublisher;
import io.dialob.security.tenant.CurrentTenant;
import io.dialob.security.tenant.ResysSecurityConstants;
import io.dialob.security.tenant.Tenant;

import java.net.InetAddress;

/**
 * Publishes various questionnaire-related events.
 */
public class QuestionnaireEventPublisher {

  private final EventPublisher applicationEventPublisher;
  private final CurrentTenant currentTenant;

  /**
   * Retrieves the tenant based on the provided tenant ID.
   * If the tenant ID is null, returns the default tenant.
   *
   * @param tenantId the ID of the tenant
   * @return the Tenant object
   */
  private Tenant getTenant(String tenantId) {
    if (tenantId != null) {
      return Tenant.of(tenantId);
    }
    return ResysSecurityConstants.DEFAULT_TENANT;
  }

  /**
   * Constructs a new QuestionnaireEventPublisher.
   *
   * @param applicationEventPublisher the event publisher to use
   * @param currentTenant the current tenant context
   */
  public QuestionnaireEventPublisher(@NonNull EventPublisher applicationEventPublisher, CurrentTenant currentTenant) {
    this.applicationEventPublisher = applicationEventPublisher;
    this.currentTenant = currentTenant;
  }

  /**
   * Publishes an event indicating that a questionnaire has been opened.
   *
   * @param questionnaireId the ID of the opened questionnaire
   */
  public void opened(@NonNull String questionnaireId) {
    applicationEventPublisher.publish(ImmutableQuestionnaireOpenedEvent.builder()
      .tenant(currentTenant.get())
      .questionnaireId(questionnaireId)
      .build());
  }

  /**
   * Publishes an event indicating that a questionnaire has been created.
   *
   * @param questionnaireId the ID of the created questionnaire
   */
  public void created(@NonNull String questionnaireId) {
    applicationEventPublisher.publish(ImmutableQuestionnaireCreatedEvent.builder()
      .tenant(currentTenant.get())
      .questionnaireId(questionnaireId)
      .build());
  }

  /**
   * Publishes an event indicating that a questionnaire has been completed.
   *
   * @param tenantId the ID of the tenant
   * @param questionnaireId the ID of the completed questionnaire
   */
  public void completed(String tenantId, @NonNull String questionnaireId) {
    applicationEventPublisher.publish(ImmutableQuestionnaireCompletedEvent.builder()
      .tenant(getTenant(tenantId))
      .questionnaireId(questionnaireId)
      .build());
  }

  /**
   * Publishes an event containing actions performed on a questionnaire.
   *
   * @param questionnaireId the ID of the questionnaire
   * @param actions the actions performed
   */
  public void actions(@NonNull String questionnaireId, @NonNull Actions actions) {
    applicationEventPublisher.publish(ImmutableQuestionnaireActionsEvent.builder()
      .tenant(currentTenant.get())
      .questionnaireId(questionnaireId)
      .actions(actions)
      .build());
  }

  /**
   * Publishes an event indicating that a client has connected to a questionnaire.
   *
   * @param questionnaireId the ID of the questionnaire
   * @param client the client's IP address
   */
  public void clientConnected(@NonNull String questionnaireId, InetAddress client) {
    applicationEventPublisher.publish(ImmutableQuestionnaireClientConnectedEvent.builder()
      .tenant(currentTenant.get())
      .questionnaireId(questionnaireId)
      .client(client)
      .build());
  }

  /**
   * Publishes an event indicating that a client has disconnected from a questionnaire.
   *
   * @param questionnaireId the ID of the questionnaire
   * @param client the client's IP address
   * @param closeStatus the status code indicating the reason for disconnection
   */
  public void clientDisconnected(@NonNull String questionnaireId, InetAddress client, int closeStatus) {
    applicationEventPublisher.publish(ImmutableQuestionnaireClientDisconnectedEvent.builder()
      .tenant(currentTenant.get())
      .questionnaireId(questionnaireId)
      .client(client)
      .closeStatus(closeStatus)
      .build());
  }
}
