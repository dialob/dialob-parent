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
package io.dialob.session.engine;

import io.dialob.api.proto.ActionItem;
import io.dialob.api.proto.ValueSet;
import io.dialob.api.questionnaire.Error;
import io.dialob.questionnaire.service.api.FormActions;
import io.dialob.session.engine.session.model.*;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;
import java.util.Locale;
import java.util.function.Function;
import java.util.function.Predicate;

import static org.mockito.Mockito.*;

class FormActionsUpdatesItemsVisitorTest {

  @Test
  void shouldHandleLanguageChange() {
    FormActions formActions = Mockito.mock(FormActions.class);
    Predicate<SessionObject> isVisiblePredicate = Mockito.mock(Predicate.class);
    Function<ItemState, ActionItem> toActionItemFunction = Mockito.mock(Function.class);

    FormActionsUpdatesItemsVisitor visitor = new FormActionsUpdatesItemsVisitor(formActions, isVisiblePredicate, toActionItemFunction);

    visitor.visitSession().ifPresent(v -> v.visitLanguageChange("en", "fi"));

    verify(formActions).locale(Locale.of("fi"));
    verifyNoMoreInteractions(formActions);
  }

  @Test
  void shouldHandleCompleted() {
    FormActions formActions = Mockito.mock(FormActions.class);
    Predicate<SessionObject> isVisiblePredicate = Mockito.mock(Predicate.class);
    Function<ItemState, ActionItem> toActionItemFunction = Mockito.mock(Function.class);

    FormActionsUpdatesItemsVisitor visitor = new FormActionsUpdatesItemsVisitor(formActions, isVisiblePredicate, toActionItemFunction);

    visitor.visitCompleted();

    verify(formActions).complete();
    verifyNoMoreInteractions(formActions);
  }

  @Test
  void shouldHandleUpdatedValueSet() {
    FormActions formActions = Mockito.mock(FormActions.class);
    Predicate<SessionObject> isVisiblePredicate = Mockito.mock(Predicate.class);
    Function<ItemState, ActionItem> toActionItemFunction = Mockito.mock(Function.class);

    FormActionsUpdatesItemsVisitor visitor = new FormActionsUpdatesItemsVisitor(formActions, isVisiblePredicate, toActionItemFunction);

    ValueSetState valueSetState = Mockito.mock(ValueSetState.class);
    when(valueSetState.id()).thenReturn(new ValueSetId("vs1"));
    when(valueSetState.entries()).thenReturn(List.of());

    visitor.visitUpdatedValueSets().ifPresent(v -> v.visitUpdatedValueSet(null, valueSetState));

    verify(formActions).newValueSet(any(ValueSet.class));
    verifyNoMoreInteractions(formActions);
  }

  @Test
  void shouldHandleActivatedError() {
    FormActions formActions = Mockito.mock(FormActions.class);
    Predicate<SessionObject> isVisiblePredicate = Mockito.mock(Predicate.class);
    Function<ItemState, ActionItem> toActionItemFunction = Mockito.mock(Function.class);

    FormActionsUpdatesItemsVisitor visitor = new FormActionsUpdatesItemsVisitor(formActions, isVisiblePredicate, toActionItemFunction);

    ErrorState errorState = Mockito.mock(ErrorState.class);
    when(errorState.itemId()).thenReturn(IdUtils.toId("item1"));
    when(errorState.code()).thenReturn("err1");
    when(errorState.label()).thenReturn("Error message");

    when(isVisiblePredicate.test(errorState)).thenReturn(true);

    visitor.visitUpdatedErrorStates().ifPresent(v -> v.visitUpdatedErrorState(null, errorState));

    verify(formActions).addError(any(Error.class));
    verifyNoMoreInteractions(formActions);
  }

  @Test
  void shouldHandleInactivatedError() {
    FormActions formActions = Mockito.mock(FormActions.class);
    Predicate<SessionObject> isVisiblePredicate = Mockito.mock(Predicate.class);
    Function<ItemState, ActionItem> toActionItemFunction = Mockito.mock(Function.class);

    FormActionsUpdatesItemsVisitor visitor = new FormActionsUpdatesItemsVisitor(formActions, isVisiblePredicate, toActionItemFunction);

    ErrorState errorState = Mockito.mock(ErrorState.class);
    when(errorState.itemId()).thenReturn(IdUtils.toId("item1"));
    when(errorState.code()).thenReturn("err1");

    when(isVisiblePredicate.test(errorState)).thenReturn(true);

    visitor.visitUpdatedErrorStates().ifPresent(v -> v.visitUpdatedErrorState(errorState, null));

    verify(formActions).removeError(any(Error.class));
    verifyNoMoreInteractions(formActions);
  }

  @Test
  void shouldHandleUpdatedError() {
    FormActions formActions = Mockito.mock(FormActions.class);
    Predicate<SessionObject> isVisiblePredicate = Mockito.mock(Predicate.class);
    Function<ItemState, ActionItem> toActionItemFunction = Mockito.mock(Function.class);

    FormActionsUpdatesItemsVisitor visitor = new FormActionsUpdatesItemsVisitor(formActions, isVisiblePredicate, toActionItemFunction);

    ErrorState originalState = Mockito.mock(ErrorState.class);
    ErrorState updatedState = Mockito.mock(ErrorState.class);
    when(updatedState.itemId()).thenReturn(IdUtils.toId("item1"));
    when(updatedState.code()).thenReturn("err1");
    when(updatedState.label()).thenReturn("Updated error message");

    when(isVisiblePredicate.test(originalState)).thenReturn(true);
    when(isVisiblePredicate.test(updatedState)).thenReturn(true);

    visitor.visitUpdatedErrorStates().ifPresent(v -> v.visitUpdatedErrorState(originalState, updatedState));

    verify(formActions).addError(any(Error.class));
    verifyNoMoreInteractions(formActions);
  }

  @Test
  void shouldHandleActivatedItem() {
    FormActions formActions = Mockito.mock(FormActions.class);
    Predicate<SessionObject> isVisiblePredicate = Mockito.mock(Predicate.class);
    Function<ItemState, ActionItem> toActionItemFunction = Mockito.mock(Function.class);

    FormActionsUpdatesItemsVisitor visitor = new FormActionsUpdatesItemsVisitor(formActions, isVisiblePredicate, toActionItemFunction);

    ItemState itemState = Mockito.mock(ItemState.class);
    ActionItem actionItem = Mockito.mock(ActionItem.class);

    when(isVisiblePredicate.test(itemState)).thenReturn(true);
    when(toActionItemFunction.apply(itemState)).thenReturn(actionItem);

    visitor.visitUpdatedItems().ifPresent(v -> v.visitUpdatedItemState(null, itemState));

    verify(formActions).newQuestion(actionItem);
    verifyNoMoreInteractions(formActions);
  }

  @Test
  void shouldHandleInactivatedItem() {
    FormActions formActions = Mockito.mock(FormActions.class);
    Predicate<SessionObject> isVisiblePredicate = Mockito.mock(Predicate.class);
    Function<ItemState, ActionItem> toActionItemFunction = Mockito.mock(Function.class);

    FormActionsUpdatesItemsVisitor visitor = new FormActionsUpdatesItemsVisitor(formActions, isVisiblePredicate, toActionItemFunction);

    ItemState itemState = Mockito.mock(ItemState.class);
    when(itemState.id()).thenReturn(IdUtils.toId("item1"));

    when(isVisiblePredicate.test(itemState)).thenReturn(true);

    visitor.visitUpdatedItems().ifPresent(v -> v.visitUpdatedItemState(itemState, null));

    verify(formActions).removeQuestion("item1");
    verifyNoMoreInteractions(formActions);
  }

  @Test
  void shouldHandleUpdatedItem() {
    FormActions formActions = Mockito.mock(FormActions.class);
    Predicate<SessionObject> isVisiblePredicate = Mockito.mock(Predicate.class);
    Function<ItemState, ActionItem> toActionItemFunction = Mockito.mock(Function.class);

    FormActionsUpdatesItemsVisitor visitor = new FormActionsUpdatesItemsVisitor(formActions, isVisiblePredicate, toActionItemFunction);

    ItemState originalState = Mockito.mock(ItemState.class);
    ItemState updatedState = Mockito.mock(ItemState.class);
    ActionItem actionItem = Mockito.mock(ActionItem.class);

    when(isVisiblePredicate.test(originalState)).thenReturn(true);
    when(isVisiblePredicate.test(updatedState)).thenReturn(true);
    when(toActionItemFunction.apply(updatedState)).thenReturn(actionItem);

    visitor.visitUpdatedItems().ifPresent(v -> v.visitUpdatedItemState(originalState, updatedState));

    verify(formActions).updateQuestion(actionItem);
    verifyNoMoreInteractions(formActions);
  }

  @Test
  void shouldHandleEnabledItem() {
    FormActions formActions = Mockito.mock(FormActions.class);
    Predicate<SessionObject> isVisiblePredicate = Mockito.mock(Predicate.class);
    Function<ItemState, ActionItem> toActionItemFunction = Mockito.mock(Function.class);

    FormActionsUpdatesItemsVisitor visitor = new FormActionsUpdatesItemsVisitor(formActions, isVisiblePredicate, toActionItemFunction);

    ItemState originalState = Mockito.mock(ItemState.class);
    ItemState updatedState = Mockito.mock(ItemState.class);
    ActionItem actionItem = Mockito.mock(ActionItem.class);

    when(originalState.isDisabled()).thenReturn(true);
    when(updatedState.isDisabled()).thenReturn(false);
    when(isVisiblePredicate.test(originalState)).thenReturn(true);
    when(isVisiblePredicate.test(updatedState)).thenReturn(true);
    when(toActionItemFunction.apply(updatedState)).thenReturn(actionItem);

    visitor.visitUpdatedItems().ifPresent(v -> v.visitUpdatedItemState(originalState, updatedState));

    verify(formActions).updateQuestion(actionItem);
    verifyNoMoreInteractions(formActions);
  }

  @Test
  void shouldHandleDisabledItem() {
    FormActions formActions = Mockito.mock(FormActions.class);
    Predicate<SessionObject> isVisiblePredicate = Mockito.mock(Predicate.class);
    Function<ItemState, ActionItem> toActionItemFunction = Mockito.mock(Function.class);

    FormActionsUpdatesItemsVisitor visitor = new FormActionsUpdatesItemsVisitor(formActions, isVisiblePredicate, toActionItemFunction);

    ItemState originalState = Mockito.mock(ItemState.class);
    ItemState updatedState = Mockito.mock(ItemState.class);
    ActionItem actionItem = Mockito.mock(ActionItem.class);

    when(originalState.isDisabled()).thenReturn(false);
    when(updatedState.isDisabled()).thenReturn(true);
    when(isVisiblePredicate.test(originalState)).thenReturn(true);
    when(isVisiblePredicate.test(updatedState)).thenReturn(true);
    when(toActionItemFunction.apply(updatedState)).thenReturn(actionItem);

    visitor.visitUpdatedItems().ifPresent(v -> v.visitUpdatedItemState(originalState, updatedState));

    verify(formActions).updateQuestion(actionItem);
    verifyNoMoreInteractions(formActions);
  }
}
