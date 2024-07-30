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
package io.dialob.questionnaire.service.api.session;

import edu.umd.cs.findbugs.annotations.NonNull;
import io.dialob.api.proto.Action;
import io.dialob.api.proto.ActionItem;
import io.dialob.api.proto.Actions;
import io.dialob.api.proto.ValueSet;
import io.dialob.api.questionnaire.Answer;
import io.dialob.api.questionnaire.Error;
import io.dialob.api.questionnaire.Questionnaire;
import io.dialob.api.questionnaire.VariableValue;
import org.immutables.value.Value;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Value.Enclosing
public interface QuestionnaireSession {

  enum QuestionClientVisibility {
    ALL,
    SHOW_DISABLED, //
    ONLY_ENABLED // Default behaviour
  }

  @Value.Immutable
  interface DispatchActionsResult {

    Actions getActions();

    boolean isDidComplete();

  }

  /**
   * @return update actions
   * @deprecated session should always generate updates. Client connectors should check state of session and
   * choose to send updates or full form
   */
  @Deprecated
  @NonNull
  DispatchActionsResult dispatchActions(String revision, @NonNull Collection<Action> actions);

  @NonNull
  DispatchActionsResult dispatchActions(@NonNull Collection<Action> actions);

  @NonNull
  Questionnaire getQuestionnaire();

  @NonNull
  String getRevision();

  String getRev();

  String getOwner();

  @NonNull
  Instant getLastUpdate();

  Optional<String> getActiveItem();

  @NonNull
  List<ValueSet> getValueSets();

  @NonNull
  List<Error> getErrors();

  @NonNull
  List<ActionItem> getItems();

  Optional<ActionItem> getItemById(@NonNull String itemId);

  @NonNull
  List<ActionItem> getVisibleItems();

  @NonNull
  List<Answer> getAnswers();

  @NonNull
  List<VariableValue> getVariableValues();

  void buildFullForm(@NonNull UpdatesCallback updatesCallback);

  Optional<String> getSessionId();

  String getTenantId();

  void activate();

  void passivate();

  boolean isActive();

  boolean isCompleted();

  boolean usesLastestFormRevision();

  @NonNull
  String getFormId();

  Optional<Locale> getLocale();

  QuestionClientVisibility getQuestionClientVisibility();

  QuestionnaireSession withIdAndRev(String id, String rev);

  @NonNull
  Questionnaire.Metadata.Status getStatus();

  enum State {
    NEW,
    INITIALIZED,
    ACTIVE,
    PASSIVE,
    COMPLETED;

    @NonNull
    public State to(@NonNull State next) {
      return next;
    }
  }

  interface UpdatesCallback {
    @NonNull
    UpdatesCallback questionAdded(@NonNull ActionItem item);

    @NonNull
    UpdatesCallback questionUpdated(@NonNull ActionItem item);

    @NonNull
    UpdatesCallback questionRemoved(@NonNull String itemId);

    @NonNull
    UpdatesCallback errorAdded(@NonNull Error error);

    @NonNull
    UpdatesCallback errorRemoved(@NonNull Error error);

    @NonNull
    UpdatesCallback removeAll();

    @NonNull
    UpdatesCallback locale(Locale locale);

    @NonNull
    UpdatesCallback completed();

    @NonNull
    UpdatesCallback valueSetAdded(@NonNull ValueSet valueSet);

    @NonNull
    UpdatesCallback valueSetUpdated(@NonNull ValueSet valueSet);

    @NonNull
    UpdatesCallback valueSetRemoved(@NonNull String valueSetId);

  }

  void close();

}
