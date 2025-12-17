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

import io.dialob.api.proto.Action;
import io.dialob.api.proto.ActionItem;
import io.dialob.api.questionnaire.Error;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;

class FormActionsTest {

  private FormActions formActions;

  @BeforeEach
  void setUp() {
    formActions = new FormActions();
  }

  @Test
  void shouldReturnEmptyListWhenNoActionsAdded() {
    List<Action> actions = formActions.getActions();

    assertNotNull(actions);
    assertTrue(actions.isEmpty());
  }

  @Test
  void shouldAddLocaleActionToPreActions() {
    formActions.locale(Locale.ENGLISH);

    List<Action> actions = formActions.getActions();

    assertEquals(1, actions.size());
    assertEquals(Action.Type.LOCALE, actions.get(0).getType());
    assertEquals("en", actions.get(0).getValue());
  }

  @Test
  void shouldNotAddLocaleActionWhenLocaleIsNull() {
    formActions.locale(null);

    List<Action> actions = formActions.getActions();

    assertTrue(actions.isEmpty());
  }

  @Test
  void shouldAddNewQuestionAction() {
    ActionItem item = new ActionItem.Builder().id("q1").type("text").build();
    formActions.newQuestion(item);

    List<Action> actions = formActions.getActions();

    assertEquals(1, actions.size());
    assertEquals(Action.Type.ITEM, actions.get(0).getType());
    assertEquals(item, actions.get(0).getItem());
  }

  @Test
  void shouldAddUpdateQuestionAction() {
    ActionItem item = new ActionItem.Builder().id("q1").type("text").build();
    formActions.updateQuestion(item);

    List<Action> actions = formActions.getActions();

    assertEquals(1, actions.size());
    assertEquals(Action.Type.ITEM, actions.get(0).getType());
    assertEquals(item, actions.get(0).getItem());
  }

  @Test
  void shouldAddRemoveQuestionAction() {
    formActions.removeQuestion("q1");

    List<Action> actions = formActions.getActions();

    assertEquals(1, actions.size());
    assertEquals(Action.Type.REMOVE_ITEMS, actions.get(0).getType());
    assertTrue(actions.get(0).getIds().contains("q1"));
  }

  @Test
  void shouldAddMultipleRemovedQuestionsToSingleAction() {
    formActions.removeQuestion("q1");
    formActions.removeQuestion("q2");
    formActions.removeQuestion("q3");

    List<Action> actions = formActions.getActions();

    assertEquals(1, actions.size());
    assertEquals(Action.Type.REMOVE_ITEMS, actions.get(0).getType());
    assertEquals(3, actions.get(0).getIds().size());
    assertTrue(actions.get(0).getIds().contains("q1"));
    assertTrue(actions.get(0).getIds().contains("q2"));
    assertTrue(actions.get(0).getIds().contains("q3"));
  }

  @Test
  void shouldAddNewValueSetAction() {
    io.dialob.api.proto.ValueSet valueSet = new io.dialob.api.proto.ValueSet.Builder()
      .id("vs1")
      .build();
    formActions.newValueSet(valueSet);

    List<Action> actions = formActions.getActions();

    assertEquals(1, actions.size());
    assertEquals(Action.Type.VALUE_SET, actions.get(0).getType());
    assertEquals(valueSet, actions.get(0).getValueSet());
  }

  @Test
  void shouldAddUpdateValueSetAction() {
    io.dialob.api.proto.ValueSet valueSet = new io.dialob.api.proto.ValueSet.Builder()
      .id("vs1")
      .build();
    formActions.updateValueSet(valueSet);

    List<Action> actions = formActions.getActions();

    assertEquals(1, actions.size());
    assertEquals(Action.Type.VALUE_SET, actions.get(0).getType());
    assertEquals(valueSet, actions.get(0).getValueSet());
  }

  @Test
  void shouldAddRemoveValueSetAction() {
    formActions.removeValueSet("vs1");

    List<Action> actions = formActions.getActions();

    assertEquals(1, actions.size());
    assertEquals(Action.Type.REMOVE_VALUE_SETS, actions.get(0).getType());
    assertTrue(actions.get(0).getIds().contains("vs1"));
  }

  @Test
  void shouldAddMultipleRemovedValueSetsToSingleAction() {
    formActions.removeValueSet("vs1");
    formActions.removeValueSet("vs2");

    List<Action> actions = formActions.getActions();

    assertEquals(1, actions.size());
    assertEquals(Action.Type.REMOVE_VALUE_SETS, actions.get(0).getType());
    assertEquals(2, actions.get(0).getIds().size());
    assertTrue(actions.get(0).getIds().contains("vs1"));
    assertTrue(actions.get(0).getIds().contains("vs2"));
  }

  @Test
  void shouldAddErrorAction() {
    Error error = new Error.Builder().id("e1").build();
    formActions.addError(error);

    List<Action> actions = formActions.getActions();

    assertEquals(1, actions.size());
    assertEquals(Action.Type.ERROR, actions.get(0).getType());
    assertEquals(error, actions.get(0).getError());
  }

  @Test
  void shouldAddRemoveErrorAction() {
    Error error = new Error.Builder().id("e1").build();
    formActions.removeError(error);

    List<Action> actions = formActions.getActions();

    assertEquals(1, actions.size());
    assertEquals(Action.Type.REMOVE_ERROR, actions.get(0).getType());
    assertEquals(error, actions.get(0).getError());
  }

  @Test
  void shouldAddResetAction() {
    formActions.removeAll();

    List<Action> actions = formActions.getActions();

    assertEquals(1, actions.size());
    assertEquals(Action.Type.RESET, actions.get(0).getType());
  }

  @Test
  void shouldAddCompleteAction() {
    formActions.complete();

    List<Action> actions = formActions.getActions();

    assertEquals(1, actions.size());
    assertEquals(Action.Type.COMPLETE, actions.get(0).getType());
  }

  @Test
  void shouldClearAllActions() {
    formActions.locale(Locale.ENGLISH);
    formActions.newQuestion(new ActionItem.Builder().id("q1").type("text").build());
    formActions.removeQuestion("q2");
    formActions.complete();

    formActions.clear();

    List<Action> actions = formActions.getActions();
    assertTrue(actions.isEmpty());
  }

  @Test
  void shouldReturnActionsInCorrectOrder() {
    // Pre actions (locale, reset)
    formActions.locale(Locale.ENGLISH);
    formActions.removeAll();

    // Removed errors
    Error error1 = new Error.Builder().id("e1").build();
    formActions.removeError(error1);

    // Removed items
    formActions.removeQuestion("q1");

    // Removed value sets
    formActions.removeValueSet("vs1");

    // Added items
    ActionItem newItem = new ActionItem.Builder().id("q2").type("text").build();
    formActions.newQuestion(newItem);

    // Updated items
    ActionItem updatedItem = new ActionItem.Builder().id("q3").type("text").build();
    formActions.updateQuestion(updatedItem);

    // Added errors
    Error error2 = new Error.Builder().id("e2").build();
    formActions.addError(error2);

    // Post actions (complete)
    formActions.complete();

    List<Action> actions = formActions.getActions();

    // Expected order: locale, reset, removeError, removeItems, removeValueSets, added, updated, addedError, complete
    assertEquals(9, actions.size());
    assertEquals(Action.Type.LOCALE, actions.get(0).getType());
    assertEquals(Action.Type.RESET, actions.get(1).getType());
    assertEquals(Action.Type.REMOVE_ERROR, actions.get(2).getType());
    assertEquals(Action.Type.REMOVE_ITEMS, actions.get(3).getType());
    assertEquals(Action.Type.REMOVE_VALUE_SETS, actions.get(4).getType());
    assertEquals(Action.Type.ITEM, actions.get(5).getType());
    assertEquals(Action.Type.ITEM, actions.get(6).getType());
    assertEquals(Action.Type.ERROR, actions.get(7).getType());
    assertEquals(Action.Type.COMPLETE, actions.get(8).getType());
  }

  @Test
  void shouldSkipRemoveItemsActionWhenNoQuestionsRemoved() {
    formActions.locale(Locale.ENGLISH);
    formActions.newQuestion(new ActionItem.Builder().id("q1").type("text").build());

    List<Action> actions = formActions.getActions();

    assertEquals(2, actions.size());
    assertFalse(actions.stream().anyMatch(a -> a.getType() == Action.Type.REMOVE_ITEMS));
  }

  @Test
  void shouldSkipRemoveValueSetsActionWhenNoValueSetsRemoved() {
    formActions.locale(Locale.ENGLISH);
    formActions.newQuestion(new ActionItem.Builder().id("q1").type("text").build());

    List<Action> actions = formActions.getActions();

    assertEquals(2, actions.size());
    assertFalse(actions.stream().anyMatch(a -> a.getType() == Action.Type.REMOVE_VALUE_SETS));
  }

  @Test
  void shouldAddMultipleNewQuestions() {
    ActionItem item1 = new ActionItem.Builder().id("q1").type("text").build();
    ActionItem item2 = new ActionItem.Builder().id("q2").type("text").build();

    formActions.newQuestion(item1);
    formActions.newQuestion(item2);

    List<Action> actions = formActions.getActions();

    assertEquals(2, actions.size());
    assertEquals(Action.Type.ITEM, actions.get(0).getType());
    assertEquals(Action.Type.ITEM, actions.get(1).getType());
  }

  @Test
  void shouldAddMultipleUpdatedQuestions() {
    ActionItem item1 = new ActionItem.Builder().id("q1").type("text").build();
    ActionItem item2 = new ActionItem.Builder().id("q2").type("text").build();

    formActions.updateQuestion(item1);
    formActions.updateQuestion(item2);

    List<Action> actions = formActions.getActions();

    assertEquals(2, actions.size());
    assertEquals(Action.Type.ITEM, actions.get(0).getType());
    assertEquals(Action.Type.ITEM, actions.get(1).getType());
  }

  @Test
  void shouldAddMultipleErrors() {
    Error error1 = new Error.Builder().id("e1").build();
    Error error2 = new Error.Builder().id("e2").build();

    formActions.addError(error1);
    formActions.addError(error2);

    List<Action> actions = formActions.getActions();

    assertEquals(2, actions.size());
    assertEquals(Action.Type.ERROR, actions.get(0).getType());
    assertEquals(Action.Type.ERROR, actions.get(1).getType());
  }

  @Test
  void shouldAddMultipleRemovedErrors() {
    Error error1 = new Error.Builder().id("e1").build();
    Error error2 = new Error.Builder().id("e2").build();

    formActions.removeError(error1);
    formActions.removeError(error2);

    List<Action> actions = formActions.getActions();

    assertEquals(2, actions.size());
    assertEquals(Action.Type.REMOVE_ERROR, actions.get(0).getType());
    assertEquals(Action.Type.REMOVE_ERROR, actions.get(1).getType());
  }

  @Test
  void shouldMixNewAndUpdatedQuestions() {
    ActionItem newItem = new ActionItem.Builder().id("q1").type("text").build();
    ActionItem updatedItem = new ActionItem.Builder().id("q2").type("text").build();

    formActions.newQuestion(newItem);
    formActions.updateQuestion(updatedItem);

    List<Action> actions = formActions.getActions();

    // New questions come before updated questions
    assertEquals(2, actions.size());
    assertEquals(newItem, actions.get(0).getItem());
    assertEquals(updatedItem, actions.get(1).getItem());
  }

  @Test
  void shouldHandleComplexScenario() {
    // Simulate a complex form update scenario
    formActions.locale(new Locale("fi"));
    formActions.removeError(new Error.Builder().id("old_error").build());
    formActions.removeQuestion("removed_q1");
    formActions.removeQuestion("removed_q2");
    formActions.newQuestion(new ActionItem.Builder().id("new_q1").type("text").build());
    formActions.updateQuestion(new ActionItem.Builder().id("updated_q1").type("text").build());
    formActions.addError(new Error.Builder().id("new_error").build());

    List<Action> actions = formActions.getActions();

    // Verify all actions are present
    assertTrue(actions.stream().anyMatch(a -> a.getType() == Action.Type.LOCALE));
    assertTrue(actions.stream().anyMatch(a -> a.getType() == Action.Type.REMOVE_ERROR));
    assertTrue(actions.stream().anyMatch(a -> a.getType() == Action.Type.REMOVE_ITEMS));
    assertTrue(actions.stream().anyMatch(a -> a.getType() == Action.Type.ITEM));
    assertTrue(actions.stream().anyMatch(a -> a.getType() == Action.Type.ERROR));
  }

  @Test
  void shouldAllowRepeatedCallsToGetActions() {
    formActions.newQuestion(new ActionItem.Builder().id("q1").type("text").build());

    List<Action> actions1 = formActions.getActions();
    List<Action> actions2 = formActions.getActions();

    assertEquals(actions1.size(), actions2.size());
    assertEquals(actions1.get(0).getType(), actions2.get(0).getType());
  }

  @Test
  void shouldClearAndAllowNewActionsToBeAdded() {
    formActions.newQuestion(new ActionItem.Builder().id("q1").type("text").build());
    formActions.clear();
    formActions.newQuestion(new ActionItem.Builder().id("q2").type("text").build());

    List<Action> actions = formActions.getActions();

    assertEquals(1, actions.size());
    assertEquals("q2", actions.get(0).getItem().getId());
  }

  @Test
  void shouldHandleNewAndUpdateValueSets() {
    io.dialob.api.proto.ValueSet newVs = new io.dialob.api.proto.ValueSet.Builder()
      .id("vs1")
      .build();
    io.dialob.api.proto.ValueSet updatedVs = new io.dialob.api.proto.ValueSet.Builder()
      .id("vs2")
      .build();

    formActions.newValueSet(newVs);
    formActions.updateValueSet(updatedVs);

    List<Action> actions = formActions.getActions();

    // New value sets come before updated value sets
    assertEquals(2, actions.size());
    assertEquals(newVs, actions.get(0).getValueSet());
    assertEquals(updatedVs, actions.get(1).getValueSet());
  }
}
