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

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import edu.umd.cs.findbugs.annotations.NonNull;
import io.dialob.integration.api.event.TenantScopedEvent;

/**
 * Common type for all questionnaire related events
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXTERNAL_PROPERTY, property = "type")
@JsonSubTypes({
  @JsonSubTypes.Type(value = ImmutableQuestionnaireCompletedEvent.class, name = "QuestionnaireCompleted"),
  @JsonSubTypes.Type(value = ImmutableQuestionnaireActionsEvent.class, name = "QuestionnaireActions"),
  @JsonSubTypes.Type(value = ImmutableQuestionnaireClientConnectedEvent.class, name = "QuestionnaireClientConnected"),
  @JsonSubTypes.Type(value = ImmutableQuestionnaireClientDisconnectedEvent.class, name = "QuestionnaireClientDisconnected"),
  @JsonSubTypes.Type(value = ImmutableQuestionnaireCreatedEvent.class, name = "QuestionnaireCreated"),
  @JsonSubTypes.Type(value = ImmutableQuestionnaireOpenedEvent.class, name = "QuestionnaireOpened")
})
public interface QuestionnaireEvent extends TenantScopedEvent {

  /**
   * @return questionnaire id of event
   */
  @NonNull
  String getQuestionnaireId();

}
