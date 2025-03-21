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
package io.dialob.questionnaire.service.api;

import edu.umd.cs.findbugs.annotations.NonNull;
import io.dialob.api.proto.Action;
import io.dialob.api.proto.ActionItem;
import io.dialob.api.proto.ImmutableAction;
import io.dialob.api.questionnaire.Error;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class FormActions implements Serializable {

  private static final ImmutableAction RESET_ACTION = ImmutableAction.builder()
    .type(Action.Type.RESET)
    .build();

  private final List<Action> preActions = new ArrayList<>();

  private final List<Action> added = new ArrayList<>();

  private final List<Action> updated = new ArrayList<>();

  private final List<String> removedQuestions = new ArrayList<>();

  private final List<String> removedValueSets = new ArrayList<>();

  private final List<Action> addedErrors = new ArrayList<>();

  private final List<Action> removedErrors = new ArrayList<>();

  private final List<Action> postActions = new ArrayList<>();

  public void clear() {
    preActions.clear();
    added.clear();
    updated.clear();
    removedQuestions.clear();
    removedValueSets.clear();
    addedErrors.clear();
    removedErrors.clear();
    postActions.clear();
  }

  public void locale(Locale locale) {
    if (locale != null) {
      preActions.add(ImmutableAction.builder()
        .type(Action.Type.LOCALE)
        .value(locale.toString()).build());
    }
  }

  public void newQuestion(@NonNull ActionItem question) {
    added.add(ImmutableAction.builder()
      .type(Action.Type.ITEM)
      .item(question).build());
  }

  public void updateQuestion(@NonNull ActionItem question) {
    updated.add(ImmutableAction.builder()
      .type(Action.Type.ITEM)
      .item(question).build());
  }

  public void removeQuestion(@NonNull String questionId) {
    removedQuestions.add(questionId);
  }

  public void newValueSet(@NonNull io.dialob.api.proto.ValueSet valueSet) {
    added.add(ImmutableAction.builder()
      .type(Action.Type.VALUE_SET)
      .valueSet(valueSet).build());
  }

  public void updateValueSet(@NonNull io.dialob.api.proto.ValueSet valueSet) {
    updated.add(ImmutableAction.builder()
      .type(Action.Type.VALUE_SET)
      .valueSet(valueSet).build());
  }

  public void removeValueSet(@NonNull String valueSetId) {
    removedValueSets.add(valueSetId);
  }

  public void addError(@NonNull Error error) {
    addedErrors.add(ImmutableAction.builder()
      .type(Action.Type.ERROR)
      .error(error).build());
  }

  public void removeError(@NonNull Error error) {
    removedErrors.add(ImmutableAction.builder()
      .type(Action.Type.REMOVE_ERROR)
      .error(error).build());
  }

  public void removeAll() {
    preActions.add(RESET_ACTION);

  }

  public void complete() {
    postActions.add(
      ImmutableAction.builder()
        .type(Action.Type.COMPLETE)
        .build()
    );
  }

  @NonNull
  public List<Action> getActions() {

    List<Action> actions = new ArrayList<>();
    actions.addAll(preActions);
    actions.addAll(removedErrors);

    if (!removedQuestions.isEmpty()) {
      actions.add(ImmutableAction.builder()
        .type(Action.Type.REMOVE_ITEMS)
        .ids(removedQuestions).build());
    }
    if (!removedValueSets.isEmpty()) {
      actions.add(ImmutableAction.builder()
        .type(Action.Type.REMOVE_VALUE_SETS)
        .ids(removedValueSets).build());
    }
    actions.addAll(added);
    actions.addAll(updated);
    actions.addAll(addedErrors);
    actions.addAll(postActions);
    return actions;
  }
}
